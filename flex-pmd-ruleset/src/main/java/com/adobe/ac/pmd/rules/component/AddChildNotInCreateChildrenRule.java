package com.adobe.ac.pmd.rules.component;

import com.adobe.ac.pmd.nodes.IFunction;
import com.adobe.ac.pmd.parser.IParserNode;
import com.adobe.ac.pmd.rules.core.AbstractAstFlexRule;
import com.adobe.ac.pmd.rules.core.ViolationPriority;

public class AddChildNotInCreateChildrenRule extends AbstractAstFlexRule // NO_UCD
{
   private static final String   CREATE_CHILDREN = "createChildren";
   private static final String[] METHOD_NAMES    =
                                                 { "addChild",
               "addChildAt"                     };

   @Override
   protected final void findViolations( final IFunction function )
   {
      if ( !function.getName().equals( CREATE_CHILDREN ) )
      {
         for ( final String methodName : METHOD_NAMES )
         {
            for ( final IParserNode statement : function.findPrimaryStatementsInBody( methodName ) )
            {
               addViolation( statement );
            }
         }
      }
   }

   @Override
   protected final ViolationPriority getDefaultPriority()
   {
      return ViolationPriority.HIGH;
   }
}