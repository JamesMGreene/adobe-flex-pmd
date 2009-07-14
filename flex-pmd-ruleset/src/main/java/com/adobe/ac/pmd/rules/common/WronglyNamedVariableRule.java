/**
 *    Copyright (c) 2008. Adobe Systems Incorporated.
 *    All rights reserved.
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions
 *    are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in
 *        the documentation and/or other materials provided with the
 *        distribution.
 *      * Neither the name of Adobe Systems Incorporated nor the names of
 *        its contributors may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 *    OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.adobe.ac.pmd.rules.common;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.adobe.ac.pmd.nodes.IAttribute;
import com.adobe.ac.pmd.nodes.IConstant;
import com.adobe.ac.pmd.nodes.IFunction;
import com.adobe.ac.pmd.nodes.IVariable;
import com.adobe.ac.pmd.parser.IParserNode;
import com.adobe.ac.pmd.rules.core.AbstractAstFlexRule;
import com.adobe.ac.pmd.rules.core.ViolationPriority;

public class WronglyNamedVariableRule extends AbstractAstFlexRule
{
   private static final String[] FORBIDDEN_NAMES =
                                                 { "tmp",
               "temp",
               "foo",
               "bar"                            };

   @Override
   protected void findViolations( final List< IFunction > functions )
   {
      for ( final IFunction function : functions )
      {
         findViolationsInVariables( function.getParameters() );

         for ( final Entry< String, IParserNode > variableNameEntrySet : function.getLocalVariables()
                                                                                 .entrySet() )
         {
            checkWronglyNamedVariable( variableNameEntrySet.getKey(),
                                       variableNameEntrySet.getValue() );
         }
      }
   }

   @Override
   protected void findViolationsFromAttributes( final List< IAttribute > attributes )
   {
      findViolationsInVariables( attributes );
   }

   @Override
   protected void findViolationsFromConstants( final List< IConstant > constants )
   {
      findViolationsInVariables( constants );
   }

   @Override
   protected ViolationPriority getDefaultPriority()
   {
      return ViolationPriority.NORMAL;
   }

   private void checkWronglyNamedVariable( final String variableName,
                                           final IParserNode variableNode )
   {
      for ( final String forbiddenName : FORBIDDEN_NAMES )
      {
         if ( forbiddenName.equals( variableName ) )
         {
            addViolation( variableNode );
         }
      }
   }

   private void findViolationsInVariables( final Collection< ? extends IVariable > variables )
   {
      for ( final IVariable variable : variables )
      {
         checkWronglyNamedVariable( variable.getName(),
                                    variable.getInternalNode() );
      }
   }
}
