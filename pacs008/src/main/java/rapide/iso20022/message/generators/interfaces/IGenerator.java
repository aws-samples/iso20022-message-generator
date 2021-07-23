/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.interfaces;

import com.prowidesoftware.swift.model.mx.AbstractMX;

import java.util.List;

public interface IGenerator {
    List<AbstractMX> generate();
}
