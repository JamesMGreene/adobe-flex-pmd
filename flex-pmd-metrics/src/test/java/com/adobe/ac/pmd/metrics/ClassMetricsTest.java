/**
 *    Copyright (c) 2009, Adobe Systems, Incorporated
 *    All rights reserved.
 *
 *    Redistribution  and  use  in  source  and  binary  forms, with or without
 *    modification,  are  permitted  provided  that  the  following  conditions
 *    are met:
 *
 *      * Redistributions  of  source  code  must  retain  the  above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions  in  binary  form  must reproduce the above copyright
 *        notice,  this  list  of  conditions  and  the following disclaimer in
 *        the    documentation   and/or   other  materials  provided  with  the
 *        distribution.
 *      * Neither the name of the Adobe Systems, Incorporated. nor the names of
 *        its  contributors  may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *    THIS  SOFTWARE  IS  PROVIDED  BY THE  COPYRIGHT  HOLDERS AND CONTRIBUTORS
 *    "AS IS"  AND  ANY  EXPRESS  OR  IMPLIED  WARRANTIES,  INCLUDING,  BUT NOT
 *    LIMITED  TO,  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 *    OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,  INCIDENTAL,  SPECIAL,
 *    EXEMPLARY,  OR  CONSEQUENTIAL  DAMAGES  (INCLUDING,  BUT  NOT  LIMITED TO,
 *    PROCUREMENT  OF  SUBSTITUTE   GOODS  OR   SERVICES;  LOSS  OF  USE,  DATA,
 *    OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *    LIABILITY,  WHETHER  IN  CONTRACT,  STRICT  LIABILITY, OR TORT (INCLUDING
 *    NEGLIGENCE  OR  OTHERWISE)  ARISING  IN  ANY  WAY  OUT OF THE USE OF THIS
 *    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.adobe.ac.pmd.metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;

import net.sourceforge.pmd.PMDException;

import org.junit.Test;

import com.adobe.ac.pmd.FlexPmdTestBase;
import com.adobe.ac.pmd.files.FileSetUtils;
import com.adobe.ac.pmd.files.IFlexFile;
import com.adobe.ac.pmd.nodes.IClass;
import com.adobe.ac.pmd.nodes.impl.NodeFactory;
import com.adobe.ac.pmd.parser.IParserNode;

public class ClassMetricsTest extends FlexPmdTestBase
{
   @Test
   public void testBug157() throws PMDException
   {
      final IFlexFile file = getTestFiles().get( "bug.FlexPMD157.as" );
      final IParserNode ast = FileSetUtils.buildAst( file );
      final IClass classNode = NodeFactory.createPackage( ast ).getClassNode();
      final ClassMetrics classMetrics = ClassMetrics.create( "bug",
                                                             new File( file.getFilePath() ),
                                                             InternalFunctionMetrics.create( new ProjectMetrics(),
                                                                                             file.getFullyQualifiedName(),
                                                                                             classNode ),
                                                             classNode,
                                                             file,
                                                             0 );

      assertEquals( "<object><name>bug.FlexPMD157</name><ccn>0</ccn><ncss>3</ncss><javadocs>0</javadocs>"
                          + "<javadoc_lines>0</javadoc_lines><multi_comment_lines>0</multi_comment_lines>"
                          + "<single_comment_lines>0</single_comment_lines><functions>0</functions></object>",
                    classMetrics.toXmlString() );
   }

   @Test
   public void testBug181() throws PMDException
   {
      final IFlexFile file = getTestFiles().get( "bug.FlexPMD181.as" );
      final IParserNode ast = FileSetUtils.buildAst( file );
      final IClass classNode = NodeFactory.createPackage( ast ).getClassNode();
      final ClassMetrics classMetrics = ClassMetrics.create( "bug",
                                                             new File( file.getFilePath() ),
                                                             InternalFunctionMetrics.create( new ProjectMetrics(),
                                                                                             file.getFullyQualifiedName(),
                                                                                             classNode ),
                                                             classNode,
                                                             file,
                                                             0 );

      assertEquals( "<object><name>bug.FlexPMD181</name><ccn>3</ccn><ncss>379</ncss><javadocs>1403"
                          + "</javadocs><javadoc_lines>1403</javadoc_lines><multi_comment_lines>4"
                          + "</multi_comment_lines><single_comment_lines>0</single_comment_lines>"
                          + "<functions>81</functions></object>",
                    classMetrics.toXmlString() );
   }

   @Test
   public void testBug232() throws PMDException
   {
      final IFlexFile file = getTestFiles().get( "bug.FlexPMD232.as" );
      final IParserNode ast = FileSetUtils.buildAst( file );
      final IClass classNode = NodeFactory.createPackage( ast ).getClassNode();
      final ClassMetrics classMetrics = ClassMetrics.create( "bug",
                                                             new File( file.getFilePath() ),
                                                             InternalFunctionMetrics.create( new ProjectMetrics(),
                                                                                             file.getFullyQualifiedName(),
                                                                                             classNode ),
                                                             classNode,
                                                             file,
                                                             0 );

      assertEquals( "<object><name>bug.FlexPMD232</name><ccn>4</ccn><ncss>7</ncss><javadocs>0</javadocs>"
                          + "<javadoc_lines>0</javadoc_lines><multi_comment_lines>0</multi_comment_lines>"
                          + "<single_comment_lines>0</single_comment_lines><functions>1</functions></object>",
                    classMetrics.toXmlString() );
   }

   @Test
   public void testBug233() throws PMDException
   {
      final IFlexFile file = getTestFiles().get( "bug.Duane.mxml" );
      final IParserNode ast = FileSetUtils.buildAst( file );
      final IClass classNode = NodeFactory.createPackage( ast ).getClassNode();
      final ClassMetrics classMetrics = ClassMetrics.create( "bug",
                                                             new File( file.getFilePath() ),
                                                             InternalFunctionMetrics.create( new ProjectMetrics(),
                                                                                             file.getFullyQualifiedName(),
                                                                                             classNode ),
                                                             classNode,
                                                             file,
                                                             1 );

      assertEquals( "<object><name>bug.Duane</name><ccn>1</ccn><ncss>217</ncss><javadocs>0</javadocs>"
                          + "<javadoc_lines>0</javadoc_lines><multi_comment_lines>0</multi_comment_lines>"
                          + "<single_comment_lines>0</single_comment_lines><functions>8</functions></object>",
                    classMetrics.toXmlString() );
   }

   @Test
   public void testToXmlString() throws PMDException
   {
      final IFlexFile file = getTestFiles().get( "RadonDataGrid.as" );
      final IParserNode ast = FileSetUtils.buildAst( file );
      final IClass classNode = NodeFactory.createPackage( ast ).getClassNode();
      final ClassMetrics classMetrics = ClassMetrics.create( "com.adobe.ac",
                                                             new File( file.getFilePath() ),
                                                             InternalFunctionMetrics.create( new ProjectMetrics(),
                                                                                             file.getFullyQualifiedName(),
                                                                                             classNode ),
                                                             classNode,
                                                             file,
                                                             0 );

      assertEquals( "<object><name>com.adobe.ac.RadonDataGrid</name><ccn>3</ccn><ncss>87</ncss><javadocs>0</javadocs>"
                          + "<javadoc_lines>0</javadoc_lines><multi_comment_lines>0</multi_comment_lines>"
                          + "<single_comment_lines>0</single_comment_lines><functions>7</functions></object>",
                    classMetrics.toXmlString() );
   }

   @Test
   public void testToXmlStringWithMultiLineComments() throws PMDException
   {
      final IFlexFile file = getTestFiles().get( "bug.FlexPMD60.as" );
      final IParserNode ast = FileSetUtils.buildAst( file );
      final IClass classNode = NodeFactory.createPackage( ast ).getClassNode();
      final ClassMetrics classMetrics = ClassMetrics.create( "bug",
                                                             new File( file.getFilePath() ),
                                                             InternalFunctionMetrics.create( new ProjectMetrics(),
                                                                                             file.getFullyQualifiedName(),
                                                                                             classNode ),
                                                             classNode,
                                                             file,
                                                             0 );

      assertEquals( "<object><name>bug.FlexPMD60</name><ccn>1</ccn><ncss>4</ncss><javadocs>9</javadocs>"
                          + "<javadoc_lines>9</javadoc_lines><multi_comment_lines>7</multi_comment_lines>"
                          + "<single_comment_lines>0</single_comment_lines><functions>1</functions></object>",
                    classMetrics.toXmlString() );
   }
}
