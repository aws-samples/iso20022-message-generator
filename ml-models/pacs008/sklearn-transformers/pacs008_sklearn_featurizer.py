# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0
#
import argparse
import csv
import json
import os
import shutil
import sys
import time
import string
from io import StringIO

import joblib
import numpy as np
import pandas as pd
from sagemaker_containers.beta.framework import (
    content_types,
    encoders,
    env,
    modules,
    transformer,
    worker,
)
from pandas.api.types import is_string_dtype, is_numeric_dtype, is_categorical_dtype
from sklearn.preprocessing import LabelEncoder, OneHotEncoder, StandardScaler, Binarizer
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.compose import ColumnTransformer, make_column_selector
from sklearn.decomposition import TruncatedSVD
from sklearn import ensemble, metrics, model_selection, naive_bayes
#from sklearn.feature_extraction import stop_words
from sklearn.feature_extraction import _stop_words
from sklearn.pipeline import Pipeline
from sklearn.pipeline import make_pipeline
from sklearn.impute import SimpleImputer
from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.compose import ColumnTransformer
from sklearn.model_selection import train_test_split
from pacs008_sklearn_transformer import CategoricalFeatureTransformer, TextFeatureTransformer


# Since we get a headerless CSV file we specify the column names here.
feature_columns_names = [
    "Dbtr_PstlAdr_Ctry",  # Debtor Country Code
    "Cdtr_PstlAdr_Ctry",  # Creditor Country Code
    "RgltryRptg_DbtCdtRptgInd",  # Regulatory Reporting Indicator Code
    "RgltryRptg_Authrty_Ctry",  # Regulatory Reporting Country Code
    "RgltryRptg_Dtls_Cd",  # Regulatory Reporting Details Code
    "InstrForNxtAgt",  # instructions for next agent
]  

# lable column is the first column in the dataset for training
label_column = "y_target"

# Added by custom transformer for 'InstrForNxtAgt' text feature - result of feature engineering
new_feature_columns_names = [
    "InstrForNxtAgt"+"_num_chars",
    "InstrForNxtAgt"+"_num_words",
    "InstrForNxtAgt"+"_mean_word_len",
    "InstrForNxtAgt"+"_num_unique_words",
    "InstrForNxtAgt"+"_num_stopwords",
    "InstrForNxtAgt"+"_num_punctuations",
    "InstrForNxtAgt"+"_num_words_upper",
    "InstrForNxtAgt"+"_nb_tfidf_word_failure",
    "InstrForNxtAgt"+"_nb_tfidf_word_success",
    "InstrForNxtAgt"+"_nb_tfidf_char_failure",
    "InstrForNxtAgt"+"_nb_tfidf_char_success",
    "InstrForNxtAgt"+"_nb_word_count_failure",
    "InstrForNxtAgt"+"_nb_word_count_success",
    "InstrForNxtAgt"+"_nb_char_count_failure",
    "InstrForNxtAgt"+"_nb_char_count_success",
]  

feature_columns_dtype = {
    "Dbtr_PstlAdr_Ctry": "category",  
    "Cdtr_PstlAdr_Ctry": "category",  
    "RgltryRptg_DbtCdtRptgInd": "category",  
    "RgltryRptg_Authrty_Ctry": "category",  
    "RgltryRptg_Dtls_Cd": "category",
    "InstrForNxtAgt": "object",
}

label_column_dtype = {"y_target": "int64"}  # 0=Failure, 1=Success
#label_column_dtype = {"y_target": "string"} 

new_columns_dtype = {
    "InstrForNxtAgt"+"_num_chars": "int64",
    "InstrForNxtAgt"+"_num_words": "int64",
    "InstrForNxtAgt"+"_mean_word_len": "float64",
    "InstrForNxtAgt"+"_num_unique_words": "int64",
    "InstrForNxtAgt"+"_num_stopwords": "int64",
    "InstrForNxtAgt"+"_num_punctuations": "int64",
    "InstrForNxtAgt"+"_num_words_upper": "int64",
    "InstrForNxtAgt"+"_nb_tfidf_word_failure": "float64",
    "InstrForNxtAgt"+"_nb_tfidf_word_success": "float64",
    "InstrForNxtAgt"+"_nb_tfidf_char_failure": "float64",
    "InstrForNxtAgt"+"_nb_tfidf_char_success": "float64",
    "InstrForNxtAgt"+"_nb_word_count_failure": "float64",
    "InstrForNxtAgt"+"_nb_word_count_success": "float64",
    "InstrForNxtAgt"+"_nb_char_count_failure": "float64",
    "InstrForNxtAgt"+"_nb_char_count_success": "float64",
}

def merge_two_dicts(x, y):
    z = x.copy()  # start with x's keys and values
    z.update(y)  # modifies z with y's keys and values & returns None
    return z


if __name__ == "__main__":

    parser = argparse.ArgumentParser()

    # Sagemaker specific arguments. Defaults are set in the environment variables.
    parser.add_argument("--output-data-dir", type=str, default=os.environ["SM_OUTPUT_DATA_DIR"])
    parser.add_argument("--model-dir", type=str, default=os.environ["SM_MODEL_DIR"])
    parser.add_argument("--train", type=str, default=os.environ["SM_CHANNEL_TRAIN"])

    args = parser.parse_args()

    # Take the set of files and read them all into a single pandas dataframe
    input_files = [os.path.join(args.train, file) for file in os.listdir(args.train)]
    if len(input_files) == 0:
        raise ValueError(
            (
                "There are no files in {}.\n"
                + "This usually indicates that the channel ({}) was incorrectly specified,\n"
                + "the data specification in S3 was incorrectly specified or the role specified\n"
                + "does not have permission to access the data."
            ).format(args.train, "train")
        )

    raw_data = [
        pd.read_csv(
            file,
            header=None,
            names= [label_column] + feature_columns_names,
            dtype=merge_two_dicts(label_column_dtype, feature_columns_dtype),
        )
        for file in input_files
    ]
    concat_data = pd.concat(raw_data)

    # Target is needed in fit
    y_target = concat_data['y_target']
    
    # Labels should not be preprocessed. predict_fn will reinsert the labels after featurizing.
    concat_data.drop(label_column, axis=1, inplace=True)

    # This section is adapted from the scikit-learn example of using preprocessing pipelines:
    #
    # https://scikit-learn.org/stable/auto_examples/compose/plot_column_transformer_mixed_types.html
    #
    # We will train our classifier with the following features:
    # Categorical Features:
    # - Dbtr_PstlAdr_Ctry
    # - Cdtr_PstlAdr_Ctry
    # - RgltryRptg_DbtCdtRptgInd
    # - RgltryRptg_Authrty_Ctry
    # - RgltryRptg_Dtls_Cd
    # Text Feature(s):
    # - InstrForNxtAgt

    cat_features_names = [
        "Dbtr_PstlAdr_Ctry",  
        "Cdtr_PstlAdr_Ctry",  
        "RgltryRptg_DbtCdtRptgInd",  
        "RgltryRptg_Authrty_Ctry",  
        "RgltryRptg_Dtls_Cd"
    ]

    # Default OneHotEncoder for categorical features
    # categorical_transformer = make_pipeline(
    #     SimpleImputer(strategy="constant", fill_value="missing"),
    #     OneHotEncoder(handle_unknown="ignore"),
    # )
    
    # Use custom CategoricalFeatureTransformer for categgorial features
    categorical_transformer = CategoricalFeatureTransformer(cat_features_names)
    
    # Use custom TextFeatureTransformer for 'InstrForNxtAgt' for feature engineering text feature
    text_feature_transformer = TextFeatureTransformer('InstrForNxtAgt')
    
    preprocessor = ColumnTransformer(
        transformers=[
            ("cat", categorical_transformer, make_column_selector(dtype_include="category")),
            ("text_custom", text_feature_transformer, ['InstrForNxtAgt']),
        ]
    )

    preprocessor.fit(concat_data, y_target)
    
    joblib.dump(preprocessor, os.path.join(args.model_dir, "model.joblib"))

    print("saved model!")


def input_fn(input_data, content_type):
    """Parse input data payload

    We currently only take csv input. Since we need to process both labelled
    and unlabelled data we first determine whether the label column is present
    by looking at how many columns were provided.
    """
    
    if content_type == "text/csv":
        # Read the raw input data as CSV.
        df = pd.read_csv(StringIO(input_data), header=None)

        if len(df.columns) == len(feature_columns_names) + 1:
            # This is a labelled example, includes the y_target label
            df.columns = [label_column] + feature_columns_names
        elif len(df.columns) == len(feature_columns_names):
            # This is an unlabelled example.
            df.columns = feature_columns_names
        
        return df
    else:
        raise ValueError("{} not supported by script!".format(content_type))


def output_fn(prediction, accept):
    """Format prediction output

    The default accept/content-type between containers for serial inference is JSON.
    We also want to set the ContentType or mimetype as the same value as accept so the next
    container can read the response payload correctly.
    """
    
    if accept == "application/json":
        instances = []
        for row in prediction.tolist():
            instances.append({"features": row})

        json_output = {"instances": instances}

        return worker.Response(json.dumps(json_output), mimetype=accept)
    elif accept == "text/csv":
        return worker.Response(encoders.encode(prediction, accept), mimetype=accept)
    else:
        raise RuntimeException("{} accept type is not supported by this script.".format(accept))


def predict_fn(input_data, model):
    """Preprocess input data

    We implement this because the default predict_fn uses .predict(), but our model is a preprocessor
    so we want to use .transform().

    The output is returned in the following order:
        - categorical features are encoded
        - text feature is transformed using custom TextFeatureTransformer adding new features in order returned by the transform method
        - text feature is dropped by TextFeatureTransformer
    """
    
    features = model.transform(input_data)

    if label_column in input_data:
        # Return the label (as the first column) and the set of features.
        return np.insert(features, 0, input_data[label_column], axis=1)
    else:
        # Return only the set of features
        return features


def model_fn(model_dir):
    """Deserialize fitted model"""
    preprocessor = joblib.load(os.path.join(model_dir, "model.joblib"))
    
    return preprocessor
