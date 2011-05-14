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

import java.security.*;

/**
 * BasicPermission subclass that defines a permission token for invoking methods within OGNL. This does not override any
 * methods (except constructors) and does not implement actions. It is similar in spirit to the
 * {@link java.lang.reflect.ReflectPermission} class in that it guards access to methods.
 * 
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public class OgnlInvokePermission
    extends BasicPermission
{
    public OgnlInvokePermission( String name )
    {
        super( name );
    }

    public OgnlInvokePermission( String name, String actions )
    {
        super( name, actions );
    }
}
