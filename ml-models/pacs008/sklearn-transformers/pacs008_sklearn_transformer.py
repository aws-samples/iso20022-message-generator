# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0
#
import os
import string
import numpy as np
import pandas as pd

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

eng_stopwords = _stop_words.ENGLISH_STOP_WORDS
#print(eng_stopwords)

def create_meta_text_features(text_df, text_feature_name):
    # Number of characters in the text
    text_df[text_feature_name + "_num_chars"] = text_df[text_feature_name].apply(lambda x: len(str(x)))

    # Number of words in the text
    text_df[text_feature_name + "_num_words"] = text_df[text_feature_name].apply(lambda x: len(str(x).split()))

    ## Average length of the words in the text
    text_df[text_feature_name + "_mean_word_len"] = text_df[text_feature_name].apply(lambda x: np.mean([len(w) for w in str(x).split()]))

    # Number of unique words in the text
    text_df[text_feature_name + "_num_unique_words"] = text_df[text_feature_name].apply(lambda x: len(set(str(x).split())))

    # Number of stopwords in the text
    text_df[text_feature_name + "_num_stopwords"] = text_df[text_feature_name].apply(lambda x: len([w for w in str(x).lower().split() if w in eng_stopwords]))
    
    # Number of punctuations in the text
    text_df[text_feature_name + "_num_punctuations"] = text_df[text_feature_name].apply(lambda x: len([c for c in str(x) if c in string.punctuation]))
    
    # Number of upper case words in the text
    text_df[text_feature_name + "_num_words_upper"] = text_df[text_feature_name].apply(lambda x: len([w for w in str(x).split() if w.isupper()]))

    return text_df

# Fit naive bayes model on input data
def fit_naive_bayes_model(train_X, train_y):
    model = naive_bayes.MultinomialNB()
    model.fit(train_X, train_y)
    
    return model

# Fit the tfidf vectorizer on words in corpus and returns the model
# - corpus - list of text documents
# returns fitted TfidfVectorizer
# set stop_words=None which is default, other option of stop_words='english' may not work in this example
#  - or provide a list of stop words, requires more analysis
def fit_word_tfidf_vectorizer(corpus):
    tfidf_vectorizer = TfidfVectorizer(ngram_range=(1,3))
    tfidf_vectorizer.fit(corpus)

    return tfidf_vectorizer

# Fit the tfidf vectorizer on characters in corpus and returns the model
# - corpus - list of text documents
# returns fitted TfidfVectorizer
def fit_char_tfidf_vectorizer(corpus):
    tfidf_vectorizer = TfidfVectorizer(ngram_range=(1,5), analyzer='char')
    tfidf_vectorizer.fit(corpus)

    return tfidf_vectorizer

# Fit a count vectorizer on words in corpus and returns the model
# - corpus - list of text documents
# returns fitted CountVectorizer
# set stop_words=None which is default, other option of stop_words='english' may not work in this example
#  - or provide a list of stop words, requires more analysis
def fit_word_count_vectorizer(corpus):
    count_vectorizer = CountVectorizer(ngram_range=(1,3))
    count_vectorizer.fit(corpus)
   
    return count_vectorizer

# Fit a count vectorizer on characters in corpus and returns the model
# - corpus - list of text documents
# returns fitted CountVectorizer
def fit_char_count_vectorizer(corpus):
    count_vectorizer = CountVectorizer(ngram_range=(1,7), analyzer='char')
    count_vectorizer.fit(corpus)
   
    return count_vectorizer

# Scikit-learn custom transformer
class TextFeatureTransformer(BaseEstimator, TransformerMixin):
    def __init__(self, feature_name):
        print(f'=>* TextFeatureTransformer init method, feature_name: {feature_name}')
        self.feature_name = feature_name
        
    def fit(self, X, y):
        print(f'=>* TextFeatureTransformer.fit() method, feature_name: {self.feature_name}')
        print(f'=>* X Shape before fit() actions: {X.shape}')

        # Fill missing text i.e. NaN with 'none'
        X[self.feature_name].fillna('none', inplace=True)
        
        # Get features as a list
        text_feature_list = X[self.feature_name].values.tolist()
        #print(f"text_feature_list: {text_feature_list}")

        # create/fit TfidfVectorizer for words in the text
        self.word_tfidf_vectorizer = fit_word_tfidf_vectorizer(text_feature_list)
        
        # Train naive bayes classifier on word tfidf vector
        X_word_tfidf_vec = self.word_tfidf_vectorizer.transform(text_feature_list)
        self.word_tfidf_nv_classifier = fit_naive_bayes_model(X_word_tfidf_vec, y)
        
        # create/fit TfidfVectorizer for characters in the text
        self.char_tfidf_vectorizer = fit_char_tfidf_vectorizer(text_feature_list)
        
        # Train naive bayes classifier on character tfidf vector
        X_char_tfidf_vec = self.char_tfidf_vectorizer.transform(text_feature_list)
        self.char_tfidf_nv_classifier = fit_naive_bayes_model(X_char_tfidf_vec, y)

        # create/fit CountVectorizer for words in the text
        self.word_count_vectorizer = fit_word_count_vectorizer(text_feature_list)
        
        # Train naive bayes classifier on word count vector
        X_word_count_vec = self.word_count_vectorizer.transform(text_feature_list)
        self.word_count_nv_classifier = fit_naive_bayes_model(X_word_count_vec, y)

        # create/fit CountVectorizer for characters in the text
        self.char_count_vectorizer = fit_char_count_vectorizer(text_feature_list)
        
        # Train naive bayes classifier on character count vector
        X_char_count_vec = self.char_count_vectorizer.transform(text_feature_list)
        self.char_count_nv_classifier = fit_naive_bayes_model(X_char_count_vec, y)

        # Useful for validating & debugging the preprocessing models were trained and are available in transform
        # Check values from here in transform()
#         print(f"===>** self.word_tfidf_vectorizer vocab:{self.word_tfidf_vectorizer.vocabulary_}")
#         print(f"===>** self.word_tfidf_vectorizer feature names:{self.word_tfidf_vectorizer.get_feature_names()}")
#         print(f"===>** self.word_tfidf_vectorizer get_stop_words:{self.word_tfidf_vectorizer.get_stop_words()}")
#         print(f"===>** self.word_tfidf_nv_classifier class_count_:{self.word_tfidf_nv_classifier.class_count_}")
#         print(f"===>** self.word_tfidf_nv_classifier classes_:{self.word_tfidf_nv_classifier.classes_}")
#         print(f"===>** self.word_tfidf_nv_classifier feature_count_:{self.word_tfidf_nv_classifier.feature_count_}")
#         print(f"===>** self.word_tfidf_nv_classifier n_features_in_:{self.word_tfidf_nv_classifier.n_features_in_}")
#         print(f"===>** self.char_tfidf_vectorizer vocab:{self.word_tfidf_vectorizer.vocabulary_}")
#         print(f"===>** self.char_tfidf_vectorizer feature names:{self.word_tfidf_vectorizer.get_feature_names()}")
#         print(f"===>** self.char_tfidf_vectorizer get_stop_words:{self.word_tfidf_vectorizer.get_stop_words()}")
#         print(f"===>** self.char_tfidf_nv_classifier class_count_:{self.char_tfidf_nv_classifier.class_count_}")
#         print(f"===>** self.char_tfidf_nv_classifier classes_:{self.char_tfidf_nv_classifier.classes_}")
#         print(f"===>** self.char_tfidf_nv_classifier feature_count_:{self.char_tfidf_nv_classifier.feature_count_}")
#         print(f"===>** self.char_tfidf_nv_classifier n_features_in_:{self.char_tfidf_nv_classifier.n_features_in_}")
#         print(f"===>** self.word_count_vectorizer vocab:{self.word_count_vectorizer.vocabulary_}")
#         print(f"===>** self.word_count_vectorizer feature names:{self.word_count_vectorizer.get_feature_names()}")
#         print(f"===>** self.word_count_vectorizer get_stop_words:{self.word_count_vectorizer.get_stop_words()}")
#         print(f"===>** self.word_count_nv_classifier class_count_:{self.word_count_nv_classifier.class_count_}")
#         print(f"===>** self.word_count_nv_classifier classes_:{self.word_count_nv_classifier.classes_}")
#         print(f"===>** self.word_count_nv_classifier feature_count_:{self.word_count_nv_classifier.feature_count_}")
#         print(f"===>** self.word_count_nv_classifier n_features_in_:{self.word_count_nv_classifier.n_features_in_}")
#         print(f"===>** self.char_count_vectorizer vocab:{self.char_count_vectorizer.vocabulary_}")
#         print(f"===>** self.char_count_vectorizer feature names:{self.char_count_vectorizer.get_feature_names()}")
#         print(f"===>** self.char_count_vectorizer get_stop_words:{self.char_count_vectorizer.get_stop_words()}")
#         print(f"===>** self.char_count_nv_classifier class_count_:{self.char_count_nv_classifier.class_count_}")
#         print(f"===>** self.char_count_nv_classifier classes_:{self.char_count_nv_classifier.classes_}")
#         print(f"===>** self.char_count_nv_classifier feature_count_:{self.char_count_nv_classifier.feature_count_}")
#         print(f"===>** self.char_count_nv_classifier n_features_in_:{self.char_count_nv_classifier.n_features_in_}")
        
#         print(f"=>* returning from fit()")
        
        return self

    def transform(self, X, y=None):
        print(f'===>** TextFeatureTransformer.transform() method, feature_name: {self.feature_name}')
        
        # Useful for validating & debugging the preprocessing models were trained and are available in transform
        # Check values from fit()
#         print(f"===>** self.word_tfidf_vectorizer vocab:{self.word_tfidf_vectorizer.vocabulary_}")
#         print(f"===>** self.word_tfidf_vectorizer feature names:{self.word_tfidf_vectorizer.get_feature_names()}")
#         print(f"===>** self.word_tfidf_vectorizer get_stop_words:{self.word_tfidf_vectorizer.get_stop_words()}")
#         print(f"===>** self.word_tfidf_nv_classifier class_count_:{self.word_tfidf_nv_classifier.class_count_}")
#         print(f"===>** self.word_tfidf_nv_classifier classes_:{self.word_tfidf_nv_classifier.classes_}")
#         print(f"===>** self.word_tfidf_nv_classifier feature_count_:{self.word_tfidf_nv_classifier.feature_count_}")
#         print(f"===>** self.word_tfidf_nv_classifier n_features_in_:{self.word_tfidf_nv_classifier.n_features_in_}")
#         print(f"===>** self.char_tfidf_vectorizer vocab:{self.word_tfidf_vectorizer.vocabulary_}")
#         print(f"===>** self.char_tfidf_vectorizer feature names:{self.word_tfidf_vectorizer.get_feature_names()}")
#         print(f"===>** self.char_tfidf_vectorizer get_stop_words:{self.word_tfidf_vectorizer.get_stop_words()}")
#         print(f"===>** self.char_tfidf_nv_classifier class_count_:{self.char_tfidf_nv_classifier.class_count_}")
#         print(f"===>** self.char_tfidf_nv_classifier classes_:{self.char_tfidf_nv_classifier.classes_}")
#         print(f"===>** self.char_tfidf_nv_classifier feature_count_:{self.char_tfidf_nv_classifier.feature_count_}")
#         print(f"===>** self.char_tfidf_nv_classifier n_features_in_:{self.char_tfidf_nv_classifier.n_features_in_}")
#         print(f"===>** self.word_count_vectorizer vocab:{self.word_count_vectorizer.vocabulary_}")
#         print(f"===>** self.word_count_vectorizer feature names:{self.word_count_vectorizer.get_feature_names()}")
#         print(f"===>** self.word_count_vectorizer get_stop_words:{self.word_count_vectorizer.get_stop_words()}")
#         print(f"===>** self.word_count_nv_classifier class_count_:{self.word_count_nv_classifier.class_count_}")
#         print(f"===>** self.word_count_nv_classifier classes_:{self.word_count_nv_classifier.classes_}")
#         print(f"===>** self.word_count_nv_classifier feature_count_:{self.word_count_nv_classifier.feature_count_}")
#         print(f"===>** self.word_count_nv_classifier n_features_in_:{self.word_count_nv_classifier.n_features_in_}")
#         print(f"===>** self.char_count_vectorizer vocab:{self.char_count_vectorizer.vocabulary_}")
#         print(f"===>** self.char_count_vectorizer feature names:{self.char_count_vectorizer.get_feature_names()}")
#         print(f"===>** self.char_count_vectorizer get_stop_words:{self.char_count_vectorizer.get_stop_words()}")
#         print(f"===>** self.char_count_nv_classifier class_count_:{self.char_count_nv_classifier.class_count_}")
#         print(f"===>** self.char_count_nv_classifier classes_:{self.char_count_nv_classifier.classes_}")
#         print(f"===>** self.char_count_nv_classifier feature_count_:{self.char_count_nv_classifier.feature_count_}")
#         print(f"===>** self.char_count_nv_classifier n_features_in_:{self.char_count_nv_classifier.n_features_in_}")
        
        print(f"===>** X shape before transform actions:{X.shape}")
        
        # Feature that is being transformed
        text_feature_name = self.feature_name

        # Fill missing text i.e. NaN with 'none'
        X[self.feature_name].fillna('none', inplace=True)
        
        # add meta text features
        create_meta_text_features(X, self.feature_name)

        # Get features as a list
        text_feature_list = X[self.feature_name].values.tolist()
        #print(f'=>* text_feature_list: {text_feature_list}')

        # Add the word tfidf based prediction probabilities for Failure or Success from text as new features
        word_tfidf_vec = self.word_tfidf_vectorizer.transform(text_feature_list)
        word_tfidf_y_pred_proba = self.word_tfidf_nv_classifier.predict_proba(word_tfidf_vec)
        X[[text_feature_name + "_nb_tfidf_word_failure", text_feature_name + "_nb_tfidf_word_success"]] = word_tfidf_y_pred_proba
        
        # Add the character tfidf based prediction probabilities for Failure or Success from text as new features
        char_tfidf_vec = self.char_tfidf_vectorizer.transform(text_feature_list)
        char_tfidf_y_pred_proba = self.char_tfidf_nv_classifier.predict_proba(char_tfidf_vec)
        X[[text_feature_name + "_nb_tfidf_char_failure", text_feature_name + "_nb_tfidf_char_success"]] = char_tfidf_y_pred_proba
        
        # Add the word count based prediction probabilities for Failure or Success from text as new features
        word_count_vec = self.word_count_vectorizer.transform(text_feature_list)
        word_count_y_pred_proba = self.word_count_nv_classifier.predict_proba(word_count_vec)
        X[[text_feature_name + "_nb_word_count_failure", text_feature_name + "_nb_word_count_success"]] = word_count_y_pred_proba 
        
        # Add the character count based prediction probabilities for Failure or Success from text as new features
        char_count_vec = self.char_count_vectorizer.transform(text_feature_list)
        char_count_y_pred_proba = self.char_count_nv_classifier.predict_proba(char_count_vec)
        X[[text_feature_name + "_nb_char_count_failure", text_feature_name + "_nb_char_count_success"]] = char_count_y_pred_proba 

        #print(f"=>* X Shape after adding char_count_y_pred_proba:{X.shape}")
        #print(f"X after adding char_count_y_pred_proba:{X}")

        # Drop text feature before training or prediction
        X.drop([text_feature_name], axis=1, inplace=True)
        print(f"=>* Final X shape after dropping text feature:{X.shape}")
        #print(f"X after dropping text feature:{X}")
        
        return X

class CategoricalFeatureTransformer(BaseEstimator, TransformerMixin):
    def __init__(self, categorical_features):
        print(f'=>* CategoricalFeatureTransformer init method, categorical_features: {categorical_features}')
        self.categorical_features = categorical_features
        
    def fit(self, X, y=None):
        print(f'=>* CategoricalFeatureTransformer.fit() method, categorical_features: {self.categorical_features}')

        categorical_features = self.categorical_features

        # Convert categorical features to categorical data and unique Index (integer value).
        self.feature_categories = {}
        for col in categorical_features:
            # Fill missing text i.e. NaN with 'none'
            X[col] = X[col].cat.add_categories('missing')
            X[col].fillna('missing', inplace=True)
            X[col] = pd.Categorical(X[col])
            # remember categories, this will be needed in transform
            self.feature_categories[col] = X[col].cat.categories
        
        print(f"=>* CategoricalFeatureTransformer.fit() method, self.feature_categories: {self.feature_categories}")
        
        return self

    def transform(self, X, y=None):
        print(f'=>* CategoricalFeatureTransformer.transform() method, categorical_features: {self.categorical_features}')
        
        print(f'=>* CategoricalFeatureTransformer.transform() method, self.feature_categories: {self.feature_categories}')

        print(f"=>* CategoricalFeatureTransformer X shape before transform actions:{X.shape}")

        # Convert categorical features to categorical type and unique Index (integer value) from fit 
        # Important when used during inference, important to keep index same as in fit (in training step)
        for col in self.categorical_features:
            # Fill missing text i.e. NaN with 'none'
            #X[col] = X[col].cat.add_categories('missing')
            X[col].fillna('missing', inplace=True)
            X[col] = pd.Categorical(X[col], self.feature_categories[col])

        # Convert from original string feature values to categorical integer values
        for col in self.categorical_features:
            X[col] = X[col].cat.codes

        return X