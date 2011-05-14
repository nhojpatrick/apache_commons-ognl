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

import java.io.PrintWriter;
import java.io.Serializable;

/**
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public abstract class SimpleNode
    implements Node, Serializable
{

    protected Node _parent;

    protected Node[] _children;

    protected int _id;

    protected OgnlParser _parser;

    private boolean _constantValueCalculated;

    private volatile boolean _hasConstantValue;

    private Object _constantValue;

    private ExpressionAccessor _accessor;

    public SimpleNode( int i )
    {
        _id = i;
    }

    public SimpleNode( OgnlParser p, int i )
    {
        this( i );
        _parser = p;
    }

    public void jjtOpen()
    {
    }

    public void jjtClose()
    {
    }

    public void jjtSetParent( Node n )
    {
        _parent = n;
    }

    public Node jjtGetParent()
    {
        return _parent;
    }

    public void jjtAddChild( Node n, int i )
    {
        if ( _children == null )
        {
            _children = new Node[i + 1];
        }
        else if ( i >= _children.length )
        {
            Node c[] = new Node[i + 1];
            System.arraycopy( _children, 0, c, 0, _children.length );
            _children = c;
        }
        _children[i] = n;
    }

    public Node jjtGetChild( int i )
    {
        return _children[i];
    }

    public int jjtGetNumChildren()
    {
        return ( _children == null ) ? 0 : _children.length;
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to customize the way the node appears when the
     * tree is dumped. If your output uses more than one line you should override toString(String), otherwise overriding
     * toString() is probably all you need to do.
     */

    public String toString()
    {
        return OgnlParserTreeConstants.jjtNodeName[_id];
    }

    // OGNL additions

    public String toString( String prefix )
    {
        return prefix + OgnlParserTreeConstants.jjtNodeName[_id] + " " + toString();
    }

    public String toGetSourceString( OgnlContext context, Object target )
    {
        return toString();
    }

    public String toSetSourceString( OgnlContext context, Object target )
    {
        return toString();
    }

    /*
     * Override this method if you want to customize how the node dumps out its children.
     */

    public void dump( PrintWriter writer, String prefix )
    {
        writer.println( toString( prefix ) );

        if ( _children != null )
        {
            for ( int i = 0; i < _children.length; ++i )
            {
                SimpleNode n = (SimpleNode) _children[i];
                if ( n != null )
                {
                    n.dump( writer, prefix + "  " );
                }
            }
        }
    }

    public int getIndexInParent()
    {
        int result = -1;

        if ( _parent != null )
        {
            int icount = _parent.jjtGetNumChildren();

            for ( int i = 0; i < icount; i++ )
            {
                if ( _parent.jjtGetChild( i ) == this )
                {
                    result = i;
                    break;
                }
            }
        }

        return result;
    }

    public Node getNextSibling()
    {
        Node result = null;
        int i = getIndexInParent();

        if ( i >= 0 )
        {
            int icount = _parent.jjtGetNumChildren();

            if ( i < icount )
            {
                result = _parent.jjtGetChild( i + 1 );
            }
        }
        return result;
    }

    protected Object evaluateGetValueBody( OgnlContext context, Object source )
        throws OgnlException
    {
        context.setCurrentObject( source );
        context.setCurrentNode( this );

        if ( !_constantValueCalculated )
        {
            _constantValueCalculated = true;
            boolean constant = isConstant( context );

            if ( constant )
            {
                _constantValue = getValueBody( context, source );
            }

            _hasConstantValue = constant;
        }

        return _hasConstantValue ? _constantValue : getValueBody( context, source );
    }

    protected void evaluateSetValueBody( OgnlContext context, Object target, Object value )
        throws OgnlException
    {
        context.setCurrentObject( target );
        context.setCurrentNode( this );
        setValueBody( context, target, value );
    }

    public final Object getValue( OgnlContext context, Object source )
        throws OgnlException
    {
        Object result = null;

        if ( context.getTraceEvaluations() )
        {

            EvaluationPool pool = OgnlRuntime.getEvaluationPool();
            Throwable evalException = null;
            Evaluation evaluation = pool.create( this, source );

            context.pushEvaluation( evaluation );
            try
            {
                result = evaluateGetValueBody( context, source );
            }
            catch ( OgnlException ex )
            {
                evalException = ex;
                throw ex;
            }
            catch ( RuntimeException ex )
            {
                evalException = ex;
                throw ex;
            }
            finally
            {
                Evaluation eval = context.popEvaluation();

                eval.setResult( result );
                if ( evalException != null )
                {
                    eval.setException( evalException );
                }
                if ( ( evalException == null ) && ( context.getRootEvaluation() == null )
                    && !context.getKeepLastEvaluation() )
                {
                    pool.recycleAll( eval );
                }
            }
        }
        else
        {
            result = evaluateGetValueBody( context, source );
        }

        return result;
    }

    /**
     * Subclasses implement this method to do the actual work of extracting the appropriate value from the source
     * object.
     */
    protected abstract Object getValueBody( OgnlContext context, Object source )
        throws OgnlException;

    public final void setValue( OgnlContext context, Object target, Object value )
        throws OgnlException
    {
        if ( context.getTraceEvaluations() )
        {
            EvaluationPool pool = OgnlRuntime.getEvaluationPool();
            Throwable evalException = null;
            Evaluation evaluation = pool.create( this, target, true );

            context.pushEvaluation( evaluation );
            try
            {
                evaluateSetValueBody( context, target, value );
            }
            catch ( OgnlException ex )
            {
                evalException = ex;
                ex.setEvaluation( evaluation );
                throw ex;
            }
            catch ( RuntimeException ex )
            {
                evalException = ex;
                throw ex;
            }
            finally
            {
                Evaluation eval = context.popEvaluation();

                if ( evalException != null )
                {
                    eval.setException( evalException );
                }
                if ( ( evalException == null ) && ( context.getRootEvaluation() == null )
                    && !context.getKeepLastEvaluation() )
                {
                    pool.recycleAll( eval );
                }
            }
        }
        else
        {
            evaluateSetValueBody( context, target, value );
        }
    }

    /**
     * Subclasses implement this method to do the actual work of setting the appropriate value in the target object. The
     * default implementation throws an <code>InappropriateExpressionException</code>, meaning that it cannot be a set
     * expression.
     */
    protected void setValueBody( OgnlContext context, Object target, Object value )
        throws OgnlException
    {
        throw new InappropriateExpressionException( this );
    }

    /** Returns true iff this node is constant without respect to the children. */
    public boolean isNodeConstant( OgnlContext context )
        throws OgnlException
    {
        return false;
    }

    public boolean isConstant( OgnlContext context )
        throws OgnlException
    {
        return isNodeConstant( context );
    }

    public boolean isNodeSimpleProperty( OgnlContext context )
        throws OgnlException
    {
        return false;
    }

    public boolean isSimpleProperty( OgnlContext context )
        throws OgnlException
    {
        return isNodeSimpleProperty( context );
    }

    public boolean isSimpleNavigationChain( OgnlContext context )
        throws OgnlException
    {
        return isSimpleProperty( context );
    }

    protected boolean lastChild( OgnlContext context )
    {
        return _parent == null || context.get( "_lastChild" ) != null;
    }

    /**
     * This method may be called from subclasses' jjtClose methods. It flattens the tree under this node by eliminating
     * any children that are of the same class as this node and copying their children to this node.
     */
    protected void flattenTree()
    {
        boolean shouldFlatten = false;
        int newSize = 0;

        for ( int i = 0; i < _children.length; ++i )
            if ( _children[i].getClass() == getClass() )
            {
                shouldFlatten = true;
                newSize += _children[i].jjtGetNumChildren();
            }
            else
                ++newSize;

        if ( shouldFlatten )
        {
            Node[] newChildren = new Node[newSize];
            int j = 0;

            for ( int i = 0; i < _children.length; ++i )
            {
                Node c = _children[i];
                if ( c.getClass() == getClass() )
                {
                    for ( int k = 0; k < c.jjtGetNumChildren(); ++k )
                        newChildren[j++] = c.jjtGetChild( k );

                }
                else
                    newChildren[j++] = c;
            }

            if ( j != newSize )
                throw new Error( "Assertion error: " + j + " != " + newSize );

            _children = newChildren;
        }
    }

    public ExpressionAccessor getAccessor()
    {
        return _accessor;
    }

    public void setAccessor( ExpressionAccessor accessor )
    {
        _accessor = accessor;
    }
}
