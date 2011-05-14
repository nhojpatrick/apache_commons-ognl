/*
 * $Id$
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.ognl;

import java.util.Map;

/**
 * This interface defines methods for calling methods in a target object. Methods are broken up into static and instance
 * methods for convenience. indexes into the target object, which must be an array.
 * 
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public interface MethodAccessor
{
    /**
     * Calls the static method named with the arguments given on the class given.
     * 
     * @param context expression context in which the method should be called
     * @param targetClass the object in which the method exists
     * @param methodName the name of the method
     * @param args the arguments to the method
     * @return result of calling the method
     * @exception MethodFailedException if there is an error calling the method
     */
    Object callStaticMethod( Map context, Class targetClass, String methodName, Object[] args )
        throws MethodFailedException;

    /**
     * Calls the method named with the arguments given.
     * 
     * @param context expression context in which the method should be called
     * @param target the object in which the method exists
     * @param methodName the name of the method
     * @param args the arguments to the method
     * @return result of calling the method
     * @exception MethodFailedException if there is an error calling the method
     */
    Object callMethod( Map context, Object target, String methodName, Object[] args )
        throws MethodFailedException;
}
