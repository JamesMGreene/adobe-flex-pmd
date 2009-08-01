package com.adobe.ac.pmd.rules.core.thresholded;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;

import com.adobe.ac.pmd.rules.core.AbstractRegexpBasedRule;

public abstract class AbstractMaximizedRegexpBasedRule extends AbstractRegexpBasedRule implements
                                                                                      IThresholdedRule
{
   public final int getThreshold()
   {
      return getIntProperty( propertyDescriptorFor( getThresholdName() ) );
   }

   public final String getThresholdName()
   {
      return MAXIMUM;
   }

   @Override
   protected final Map< String, PropertyDescriptor > propertiesByName()
   {
      return getRuleProperties( this );
   }
}