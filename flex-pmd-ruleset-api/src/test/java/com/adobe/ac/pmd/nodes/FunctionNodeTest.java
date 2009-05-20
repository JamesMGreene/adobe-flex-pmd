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
package com.adobe.ac.pmd.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.adobe.ac.pmd.FlexPmdTestBase;
import com.adobe.ac.pmd.parser.exceptions.TokenException;

import de.bokelberg.flex.parser.AS3Parser;

public class FunctionNodeTest extends FlexPmdTestBase
{
   private IFunction constructor;
   private IFunction drawHighlightIndicator;
   private IFunction drawRowBackground;
   private IFunction drawSelectionIndicator;
   private IFunction isTrueGetter;
   private IFunction isTrueSetter;
   private IFunction placeSortArrow;

   @Before
   public void setup() throws IOException,
                      TokenException
   {
      final AS3Parser parser = new AS3Parser();
      final IClass radonDataGridClassNode = new PackageNode( parser.buildAst( testFiles.get( "RadonDataGrid.as" )
                                                                                       .getFilePath() ) ).getClassNode();

      constructor = radonDataGridClassNode.getFunctions().get( 0 );
      drawHighlightIndicator = radonDataGridClassNode.getFunctions().get( 1 );
      drawSelectionIndicator = radonDataGridClassNode.getFunctions().get( 2 );
      drawRowBackground = radonDataGridClassNode.getFunctions().get( 3 );
      placeSortArrow = radonDataGridClassNode.getFunctions().get( 4 );
      isTrueGetter = radonDataGridClassNode.getFunctions().get( 5 );
      isTrueSetter = radonDataGridClassNode.getFunctions().get( 6 );
   }

   @Test
   public void testGetCyclomaticComplexity()
   {
      assertEquals( 2,
                    constructor.getCyclomaticComplexity() );
      assertEquals( 1,
                    drawHighlightIndicator.getCyclomaticComplexity() );
      assertEquals( 1,
                    drawSelectionIndicator.getCyclomaticComplexity() );
      assertEquals( 4,
                    drawRowBackground.getCyclomaticComplexity() );
      assertEquals( 13,
                    placeSortArrow.getCyclomaticComplexity() );
   }

   @Test
   public void testGetName()
   {
      assertEquals( "RadonDataGrid",
                    constructor.getName() );
      assertEquals( "drawHighlightIndicator",
                    drawHighlightIndicator.getName() );
      assertEquals( "drawSelectionIndicator",
                    drawSelectionIndicator.getName() );
      assertEquals( "drawRowBackground",
                    drawRowBackground.getName() );
      assertEquals( "placeSortArrow",
                    placeSortArrow.getName() );
   }

   @Test
   public void testGetParameters()
   {
      assertEquals( 0,
                    constructor.getParameters().size() );
      assertEquals( 7,
                    drawHighlightIndicator.getParameters().size() );
      assertEquals( 7,
                    drawSelectionIndicator.getParameters().size() );
      assertEquals( 6,
                    drawRowBackground.getParameters().size() );
      assertEquals( 0,
                    placeSortArrow.getParameters().size() );
   }

   @Test
   public void testGetReturnType()
   {
      assertEquals( "",
                    constructor.getReturnType().getInternalNode().getStringValue() );
      assertEquals( "void",
                    drawHighlightIndicator.getReturnType().getInternalNode().getStringValue() );
      assertEquals( "void",
                    drawSelectionIndicator.getReturnType().getInternalNode().getStringValue() );
      assertEquals( "void",
                    drawRowBackground.getReturnType().getInternalNode().getStringValue() );
      assertEquals( "void",
                    placeSortArrow.getReturnType().getInternalNode().getStringValue() );
   }

   @Test
   public void testIsGetter()
   {
      assertFalse( constructor.isGetter() );
      assertFalse( drawHighlightIndicator.isGetter() );
      assertFalse( isTrueSetter.isGetter() );
      assertTrue( isTrueGetter.isGetter() );
   }

   @Test
   public void testIsSetter()
   {
      assertFalse( constructor.isSetter() );
      assertFalse( drawHighlightIndicator.isSetter() );
      assertFalse( isTrueGetter.isSetter() );
      assertTrue( isTrueSetter.isSetter() );
   }

   @Test
   public void testLocalVariables()
   {
      assertEquals( 0,
                    constructor.getLocalVariables().size() );
      assertEquals( 2,
                    drawHighlightIndicator.getLocalVariables().size() );
      assertEquals( 13,
                    drawSelectionIndicator.getLocalVariables().size() );
      assertEquals( 5,
                    drawRowBackground.getLocalVariables().size() );
   }
}
