// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package com.herve.rest;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

// tag::applicationPath[]
@ApplicationPath("v1/order")
// end::applicationPath[]
// tag::systemApplication[]
public class OrderApplication extends Application {

}
// end::systemApplication[]