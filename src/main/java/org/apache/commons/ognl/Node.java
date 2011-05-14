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

import org.apache.commons.ognl.enhance.ExpressionAccessor;

/**
 * JJTree interface for AST nodes, as modified to handle the OGNL operations getValue and setValue. JJTree's original
 * comment: All AST nodes must implement this interface. It provides basic machinery for constructing the parent and
 * child relationships between nodes.
 * 
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public interface Node
    extends JavaSource
{

    /**
     * This method is called after the node has been made the current node. It indicates that child nodes can now be
     * added to it.
     */
    public void jjtOpen();

    /**
     * This method is called after all the child nodes have been added.
     */
    public void jjtClose();

    /**
     * This pair of methods are used to inform the node of its parent.
     */
    public void jjtSetParent( Node n );

    public Node jjtGetParent();

    /**
     * This method tells the node to add its argument to the node's list of children.
     */
    public void jjtAddChild( Node n, int i );

    /**
     * This method returns a child node. The children are numbered from zero, left to right.
     */
    public Node jjtGetChild( int i );

    /** Return the number of children the node has. */
    public int jjtGetNumChildren();

    // OGNL additions to Node:

    /**
     * Extracts the value from the given source object that is appropriate for this node within the given context.
     */
    public Object getValue( OgnlContext context, Object source )
        throws OgnlException;

    /**
     * Sets the given value in the given target as appropriate for this node within the given context.
     */
    public void setValue( OgnlContext context, Object target, Object value )
        throws OgnlException;

    /**
     * Gets the compiled bytecode enhanced expression accessor for getting/setting values.
     * 
     * @return The accessor for this node, or null if none has been compiled for it.
     */
    ExpressionAccessor getAccessor();

    /**
     * Sets a new compiled accessor for this node expression.
     * 
     * @param accessor The compiled representation of this node.
     */
    void setAccessor( ExpressionAccessor accessor );
}
