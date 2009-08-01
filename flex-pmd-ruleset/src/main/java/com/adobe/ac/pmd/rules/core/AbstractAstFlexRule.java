package com.adobe.ac.pmd.rules.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.adobe.ac.pmd.IFlexViolation;
import com.adobe.ac.pmd.StackTraceUtils;
import com.adobe.ac.pmd.files.IFlexFile;
import com.adobe.ac.pmd.nodes.IAttribute;
import com.adobe.ac.pmd.nodes.IClass;
import com.adobe.ac.pmd.nodes.IConstant;
import com.adobe.ac.pmd.nodes.IFunction;
import com.adobe.ac.pmd.nodes.INode;
import com.adobe.ac.pmd.nodes.IPackage;
import com.adobe.ac.pmd.parser.IParserNode;
import com.adobe.ac.pmd.parser.KeyWords;
import com.adobe.ac.pmd.parser.NodeKind;

/**
 * Abstract class for AST-based rule Extends this class if your rule is only
 * detectable in an AS script block, which can be converted into an Abstract
 * Synthax Tree. Then you will be able to either use the visitor pattern, or to
 * iterate from the package node, in order to find your violation(s).
 * 
 * @author xagnetti
 */
public abstract class AbstractAstFlexRule extends AbstractFlexRule implements IFlexAstRule
{
   private interface ExpressionVisitor
   {
      void visitExpression( final IParserNode ast );
   }

   private static final Logger LOGGER = Logger.getLogger( AbstractAstFlexRule.class.getName() );

   protected static IParserNode getNameFromFunctionDeclaration( final IParserNode functionNode )
   {
      IParserNode nameChild = null;
      if ( functionNode.numChildren() != 0 )
      {
         for ( final IParserNode child : functionNode.getChildren() )
         {
            if ( child.is( NodeKind.NAME ) )
            {
               nameChild = child;
               break;
            }
         }
      }
      return nameChild;
   }

   protected static IParserNode getTypeFromFieldDeclaration( final IParserNode fieldNode )
   {
      IParserNode typeNode = null;

      for ( final IParserNode node : fieldNode.getChildren() )
      {
         if ( node.is( NodeKind.NAME_TYPE_INIT )
               && node.numChildren() > 1 )
         {
            typeNode = node.getChild( 1 );
            break;
         }
      }
      return typeNode;
   }

   private IFlexFile                    currentFile;
   private Map< String, IFlexFile >     filesInSourcePath;

   private final List< IFlexViolation > violations;

   public AbstractAstFlexRule()
   {
      super();

      violations = new ArrayList< IFlexViolation >();
   }

   @Override
   public boolean isConcernedByTheGivenFile( final IFlexFile file )
   {
      return true;
   }

   /**
    * @param function
    * @return the added violation positioned on the given function node
    */
   protected final IFlexViolation addViolation( final IFunction function )
   {
      final IParserNode name = getNameFromFunctionDeclaration( function.getInternalNode() );

      return addViolation( name,
                           name,
                           name.getStringValue() );
   }

   protected final IFlexViolation addViolation( final IFunction function,
                                                final String messageToReplace )
   {
      final IParserNode name = getNameFromFunctionDeclaration( function.getInternalNode() );

      return addViolation( name,
                           name,
                           messageToReplace );
   }

   /**
    * @param violatingNode
    * @return the added violation replacing the threshold value in the message
    *         if any.
    */
   protected final IFlexViolation addViolation( final INode violatingNode )
   {
      return addViolation( violatingNode.getInternalNode(),
                           violatingNode.getInternalNode() );
   }

   /**
    * @param violatingNode
    * @return the added violation replacing the threshold value in the message
    *         if any.
    */
   protected final IFlexViolation addViolation( final INode violatingNode,
                                                final String... messageToReplace )
   {
      return addViolation( violatingNode.getInternalNode(),
                           violatingNode.getInternalNode(),
                           messageToReplace );
   }

   /**
    * @param violatingNode
    * @param endNode
    * @return the added violation replacing the threshold value in the message
    *         if any.
    */
   protected final IFlexViolation addViolation( final IParserNode violatingNode )
   {
      return addViolation( violatingNode,
                           violatingNode );
   }

   /**
    * @param beginningNode
    * @param endNode
    * @param messageToReplace
    * @return the add violation replacing the {0} token by the specified message
    */
   protected final IFlexViolation addViolation( final IParserNode beginningNode,
                                                final IParserNode endNode,
                                                final String... messageToReplace )
   {
      final IFlexViolation violation = addViolation( ViolationPosition.create( beginningNode.getLine(),
                                                                               endNode.getLine(),
                                                                               beginningNode.getColumn(),
                                                                               endNode.getColumn() ) );

      for ( int i = 0; i < messageToReplace.length; i++ )
      {
         violation.replacePlaceholderInMessage( messageToReplace[ i ],
                                                i );
      }

      return violation;
   }

   /**
    * @param violatingNode
    * @param endNode
    * @param messageToReplace
    * @return the add violation replacing the {0} token by the specified message
    */
   protected final IFlexViolation addViolation( final IParserNode violatingNode,
                                                final String... messageToReplace )
   {
      return addViolation( violatingNode,
                           violatingNode,
                           messageToReplace );
   }

   /**
    * @param violationPosition
    * @return the added violation positioned at the given position
    */
   protected final IFlexViolation addViolation( final ViolationPosition violationPosition )
   {
      return addViolation( violations,
                           currentFile,
                           violationPosition );
   }

   /**
    * find the violations list from the given class node
    * 
    * @param classNode
    */
   protected void findViolations( final IClass classNode )
   {
      if ( classNode.getAttributes() != null )
      {
         findViolationsFromAttributes( classNode.getAttributes() );
      }
      if ( classNode.getConstants() != null )
      {
         findViolationsFromConstants( classNode.getConstants() );
      }
      if ( classNode.getFunctions() != null )
      {
         findViolations( classNode.getFunctions() );
      }
      if ( classNode.getConstructor() != null )
      {
         findViolationsFromConstructor( classNode.getConstructor() );
      }
   }

   protected void findViolations( final IFunction function )
   {
   }

   /**
    * Override this method if you need to find violations from the package ( or
    * any subsequent node like class or function)
    * 
    * @param packageNode
    */
   protected void findViolations( final IPackage packageNode )
   {
      final IClass classNode = packageNode.getClassNode();

      if ( classNode != null )
      {
         findViolations( classNode );
      }
   }

   /**
    * find the violations list from the given functions list
    * 
    * @param functions
    */
   protected void findViolations( final List< IFunction > functions )
   {
      for ( final IFunction function : functions )
      {
         findViolations( function );
      }
   }

   /**
    * find the violations list from the given class variables list
    * 
    * @param variables
    */
   protected void findViolationsFromAttributes( final List< IAttribute > variables )
   {
   }

   /**
    * find the violations list from the given class constants list
    * 
    * @param constants
    */
   protected void findViolationsFromConstants( final List< IConstant > constants )
   {
   }

   /**
    * find the violations list from the given class constructor node
    * 
    * @param constructor
    */
   protected void findViolationsFromConstructor( final IFunction constructor )
   {
   }

   /**
    * @return the current file under investigation
    */
   protected final IFlexFile getCurrentFile()
   {
      return currentFile;
   }

   /**
    * @return the list of Flex files in the source path
    */
   protected final Map< String, IFlexFile > getFilesInSourcePath()
   {
      return filesInSourcePath;
   }

   @Override
   protected final List< IFlexViolation > processFileBody( final IPackage packageNode,
                                                           final IFlexFile file,
                                                           final Map< String, IFlexFile > files )
   {
      currentFile = file;
      filesInSourcePath = files;
      try
      {
         if ( packageNode != null )
         {
            visitCompilationUnit( packageNode.getInternalNode() );
            findViolations( packageNode );
         }
      }
      catch ( final Exception e )
      {
         LOGGER.finer( StackTraceUtils.print( e ) );
      }
      final List< IFlexViolation > copy = new ArrayList< IFlexViolation >( violations );

      violations.clear();

      return copy;
   }

   protected void visitCatch( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         visitNameTypeInit( ast.getChild( 0 ) );
         visitBlock( ast.getChild( 1 ) );
      }
   }

   final protected void visitClass( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         IParserNode content = null;
         for ( final IParserNode node : ast.getChildren() )
         {
            if ( node.is( NodeKind.CONTENT ) )
            {
               content = node;
            }
         }
         visitClassContent( content );
      }
   }

   /**
    * Visit the condition of a if, while, ...
    * 
    * @param condition
    */
   protected void visitCondition( final IParserNode condition )
   {
      visitExpression( condition );
   }

   protected void visitDo( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         visitBlock( ast.getChild( 0 ) );
         visitCondition( ast.getChild( 1 ) );
      }
   }

   protected void visitElse( final IParserNode ast )
   {
      visitBlock( ast.getChild( 2 ) );
   }

   protected void visitFinally( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         visitBlock( ast.getChild( 0 ) );
      }
   }

   protected void visitFor( final IParserNode ast )
   {
      if ( ast.numChildren() > 3 )
      {
         visitBlock( ast.getChild( 3 ) );
      }
   }

   protected void visitForEach( final IParserNode ast )
   {
      if ( ast.numChildren() > 2 )
      {
         visitBlock( ast.getChild( 2 ) );
      }
   }

   protected void visitForIn( final IParserNode ast )
   {
   }

   protected void visitFunction( final IParserNode ast,
                                 final String type )
   {
      if ( isNodeNavigable( ast ) )
      {
         final Iterator< IParserNode > iterator = ast.getChildren().iterator();
         IParserNode node = iterator.next();

         if ( node.is( NodeKind.META_LIST )
               || node.is( NodeKind.MOD_LIST ) )
         {
            node = iterator.next();
         }
         node = iterator.next();
         visitParameters( node );
         node = iterator.next();
         visitFunctionReturnType( node );
         if ( iterator.hasNext() )
         {
            node = iterator.next();
            visitFunctionBody( node );
         }
      }
   }

   protected void visitFunctionReturnType( final IParserNode node )
   {
      visitBlock( node );
   }

   protected void visitIf( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         visitCondition( ast.getChild( 0 ) );
         visitThen( ast );
         if ( ast.numChildren() == 3 )
         {
            visitElse( ast );
         }
      }
   }

   protected void visitInterface( final IParserNode ast )
   {
   }

   protected void visitMethodCall( final IParserNode ast )
   {
      final Iterator< IParserNode > iterator = ast.getChildren().iterator();
      visitExpression( iterator.next() );
      do
      {
         visitExpressionList( iterator.next() );
      }
      while ( iterator.hasNext() );
   }

   protected void visitParameters( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         for ( final IParserNode node2 : ast.getChildren() )
         {
            final IParserNode node = node2.getChild( 0 );

            if ( node.is( NodeKind.NAME_TYPE_INIT ) )
            {
               visitNameTypeInit( node );
            }
         }
      }
   }

   protected void visitStatement( final IParserNode ast )
   {
      if ( ast == null )
      {
         return;
      }
      if ( ast.is( NodeKind.FOR ) )
      {
         visitFor( ast );
      }
      else if ( ast.is( NodeKind.FORIN ) )
      {
         visitForIn( ast );
      }
      else if ( ast.is( NodeKind.FOREACH ) )
      {
         visitForEach( ast );
      }
      else if ( ast.is( NodeKind.DO ) )
      {
         visitDo( ast );
      }
      else if ( ast.is( NodeKind.WHILE ) )
      {
         visitWhile( ast );
      }
      else if ( ast.is( NodeKind.IF ) )
      {
         visitIf( ast );
      }
      else if ( ast.is( NodeKind.SWITCH ) )
      {
         visitSwitch( ast );
      }
      else if ( ast.is( NodeKind.TRY ) )
      {
         visitTry( ast );
      }
      else if ( ast.is( NodeKind.CATCH ) )
      {
         visitCatch( ast );
      }
      else if ( ast.is( NodeKind.FINALLY ) )
      {
         visitFinally( ast );
      }
      else if ( ast.is( NodeKind.LEFT_CURLY_BRACKET ) )
      {
         visitBlock( ast );
      }
      else if ( ast.is( NodeKind.VAR ) )
      {
         visitVarOrConstList( ast,
                              KeyWords.VAR );
      }
      else if ( ast.is( NodeKind.CONST ) )
      {
         visitVarOrConstList( ast,
                              KeyWords.CONST );
      }
      else if ( ast.is( NodeKind.RETURN ) )
      {
         visitReturn( ast );
      }
      else if ( !ast.is( NodeKind.STMT_EMPTY ) )
      {
         visitExpressionList( ast );
      }
   }

   protected void visitSwitch( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         final Iterator< IParserNode > iterator = ast.getChildren().iterator();

         visitExpression( iterator.next() );

         if ( iterator.hasNext() )
         {
            final IParserNode cases = iterator.next();

            if ( cases.getChildren() != null )
            {
               for ( final IParserNode caseNode : cases.getChildren() )
               {
                  final IParserNode child = caseNode.getChild( 0 );

                  if ( child.is( NodeKind.DEFAULT ) )
                  {
                     visitSwitchDefaultCase( caseNode.getChild( 1 ) );
                  }
                  else
                  {
                     visitSwitchCase( caseNode.getChild( 1 ) );
                     visitExpression( child );
                  }
               }
            }
         }
      }
   }

   protected void visitSwitchCase( final IParserNode child )
   {
      visitBlock( child );
   }

   protected void visitSwitchDefaultCase( final IParserNode child )
   {
      visitBlock( child );
   }

   protected void visitThen( final IParserNode ast )
   {
      visitBlock( ast.getChild( 1 ) );
   }

   protected void visitTry( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         visitBlock( ast.getChild( 0 ) );
      }
   }

   protected void visitVarOrConstList( final IParserNode ast,
                                       final KeyWords varOrConst )
   {
      if ( isNodeNavigable( ast ) )
      {
         final Iterator< IParserNode > iterator = ast.getChildren().iterator();

         IParserNode node = iterator.next();
         if ( node.is( NodeKind.META_LIST )
               || node.is( NodeKind.MOD_LIST ) )
         {
            node = iterator.next();
         }
         while ( node != null )
         {
            visitNameTypeInit( node );
            node = iterator.hasNext() ? iterator.next()
                                     : null;
         }
      }
   }

   protected void visitWhile( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         visitCondition( ast.getChild( 0 ) );
         visitBlock( ast.getChild( 1 ) );
      }
   }

   private boolean isNodeNavigable( final IParserNode node )
   {
      return node != null
            && node.numChildren() != 0;
   }

   private void visitAdditiveExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.ADD,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitMultiplicativeExpression( ast );
                          }
                       } );
   }

   private void visitAndExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.AND,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitBitwiseOrExpression( ast );
                          }
                       } );
   }

   private void visitArrayAccessor( final IParserNode ast )
   {
      final Iterator< IParserNode > iterator = ast.getChildren().iterator();
      visitExpression( iterator.next() );
      do
      {
         visitExpression( iterator.next() );
      }
      while ( iterator.hasNext() );
   }

   private void visitBitwiseAndExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.B_AND,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitEqualityExpression( ast );
                          }
                       } );
   }

   private void visitBitwiseOrExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.B_OR,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitBitwiseXorExpression( ast );
                          }
                       } );
   }

   private void visitBitwiseXorExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.B_XOR,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitBitwiseAndExpression( ast );
                          }
                       } );
   }

   private void visitBlock( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         for ( final IParserNode node : ast.getChildren() )
         {
            visitStatement( node );
         }
      }
   }

   private void visitClassContent( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         for ( final IParserNode node : ast.getChildren() )
         {
            if ( node.is( NodeKind.VAR_LIST ) )
            {
               visitVarOrConstList( node,
                                    KeyWords.VAR );
            }
            else if ( node.is( NodeKind.CONST_LIST ) )
            {
               visitVarOrConstList( node,
                                    KeyWords.CONST );
            }
            else if ( node.is( NodeKind.FUNCTION ) )
            {
               visitFunction( node,
                              "" );
            }
            else if ( node.is( NodeKind.SET ) )
            {
               visitFunction( node,
                              "set " );
            }
            else if ( node.is( NodeKind.GET ) )
            {
               visitFunction( node,
                              "get " );
            }
         }
      }
   }

   private void visitCompilationUnit( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         for ( final IParserNode node : ast.getChildren() )
         {
            if ( node.is( NodeKind.PACKAGE )
                  && node.numChildren() >= 2 )
            {
               visitPackageContent( node.getChild( 1 ) );
            }
            if ( !node.is( NodeKind.PACKAGE )
                  && node.numChildren() > 0 )
            {
               visitPackageContent( node );
            }
         }
      }
   }

   private void visitConditionalExpression( final IParserNode ast )
   {
      if ( ast != null )
      {
         if ( ast.is( NodeKind.CONDITIONAL ) )
         {
            final Iterator< IParserNode > iterator = ast.getChildren().iterator();
            final IParserNode node = iterator.next();

            visitOrExpression( node );

            while ( iterator.hasNext() )
            {
               visitExpression( iterator.next() );
               visitExpression( iterator.next() );
            }
         }
         else
         {
            visitOrExpression( ast );
         }
      }
   }

   private void visitEqualityExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.EQUALITY,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitRelationalExpression( ast );
                          }
                       } );
   }

   private void visitExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.ASSIGN,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitConditionalExpression( ast );
                          }
                       } );
   }

   private void visitExpression( final IParserNode ast,
                                 final NodeKind kind,
                                 final ExpressionVisitor visitor )
   {
      if ( ast != null )
      {
         if ( ast.is( kind ) )
         {
            final Iterator< IParserNode > iterator = ast.getChildren().iterator();
            final IParserNode node = iterator.next();

            visitor.visitExpression( node );

            while ( iterator.hasNext() )
            {
               iterator.next();
               visitor.visitExpression( iterator.next() );
            }
         }
         else
         {
            visitor.visitExpression( ast );
         }
      }
   }

   private void visitExpressionList( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         for ( final IParserNode node : ast.getChildren() )
         {
            visitExpression( node );
         }
      }
   }

   private void visitFunctionBody( final IParserNode node )
   {
      visitBlock( node );
   }

   private void visitMultiplicativeExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.MULTIPLICATION,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitUnaryExpression( ast );
                          }
                       } );
   }

   private void visitNameTypeInit( final IParserNode ast )
   {
      if ( ast != null
            && ast.numChildren() != 0 )
      {
         final Iterator< IParserNode > iterator = ast.getChildren().iterator();
         IParserNode node;

         iterator.next();

         if ( iterator.hasNext() )
         {
            node = iterator.next();
         }

         if ( iterator.hasNext() )
         {
            node = iterator.next();

            if ( node.is( NodeKind.INIT ) )
            {
               visitExpression( node );
            }
         }
      }
   }

   private void visitOrExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.OR,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitAndExpression( ast );
                          }
                       } );
   }

   private void visitPackageContent( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         for ( final IParserNode node : ast.getChildren() )
         {
            if ( node.is( NodeKind.CLASS ) )
            {
               visitClass( node );
            }
            else if ( node.is( NodeKind.INTERFACE ) )
            {
               visitInterface( node );
            }
         }
      }
   }

   private void visitPrimaryExpression( final IParserNode ast )
   {
      if ( ast != null )
      {
         if ( ast.numChildren() != 0
               && ast.is( NodeKind.ARRAY ) )
         {
            visitExpressionList( ast );
         }
         else if ( ast.is( NodeKind.OBJECT ) )
         {
            if ( isNodeNavigable( ast ) )
            {
               for ( final IParserNode node : ast.getChildren() )
               {
                  visitExpression( node.getChild( 1 ) );
               }
            }
         }
         else if ( ast.is( NodeKind.NEW ) )
         {
            visitExpression( ast.getChild( 0 ) );
            visitExpressionList( ast.getChild( 1 ) );
         }
         else if ( ast.is( NodeKind.ENCAPSULATED ) )
         {
            visitExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.E4X_ATTR ) )
         {
            final IParserNode node = ast.getChild( 0 );

            if ( !node.is( NodeKind.NAME )
                  && !node.is( NodeKind.STAR ) )
            {
               visitExpression( node );
            }
         }
      }
   }

   private void visitRelationalExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.RELATION,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitShiftExpression( ast );
                          }
                       } );
   }

   private void visitReturn( final IParserNode ast )
   {
      if ( isNodeNavigable( ast ) )
      {
         visitExpression( ast.getChild( 0 ) );
      }
   }

   private void visitShiftExpression( final IParserNode ast )
   {
      visitExpression( ast,
                       NodeKind.SHIFT,
                       new ExpressionVisitor()
                       {
                          public void visitExpression( final IParserNode ast )
                          {
                             visitAdditiveExpression( ast );
                          }
                       } );
   }

   private void visitUnaryExpression( final IParserNode ast )
   {
      if ( ast != null )
      {
         if ( ast.is( NodeKind.PRE_INC ) )
         {
            visitUnaryExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.PRE_DEC ) )
         {
            visitUnaryExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.MINUS ) )
         {
            visitUnaryExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.PLUS ) )
         {
            visitUnaryExpression( ast.getChild( 0 ) );
         }
         else
         {
            visitUnaryExpressionNotPlusMinus( ast );
         }
      }
   }

   private void visitUnaryExpressionNotPlusMinus( final IParserNode ast )
   {
      if ( ast != null )
      {
         if ( ast.is( NodeKind.DELETE ) )
         {
            visitExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.VOID ) )
         {
            visitExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.TYPEOF ) )
         {
            visitExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.NOT ) )
         {
            visitExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.B_NOT ) )
         {
            visitExpression( ast.getChild( 0 ) );
         }
         else
         {
            visitUnaryPostfixExpression( ast );
         }
      }
   }

   private void visitUnaryPostfixExpression( final IParserNode ast )
   {
      if ( ast != null )
      {
         if ( ast.is( NodeKind.ARRAY_ACCESSOR ) )
         {
            visitArrayAccessor( ast );
         }
         else if ( ast.is( NodeKind.DOT ) )
         {
            visitExpression( ast.getChild( 0 ) );
            visitExpression( ast.getChild( 1 ) );
         }
         else if ( ast.is( NodeKind.CALL ) )
         {
            visitMethodCall( ast );
         }
         else if ( ast.is( NodeKind.POST_INC ) )
         {
            visitPrimaryExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.POST_DEC ) )
         {
            visitPrimaryExpression( ast.getChild( 0 ) );
         }
         else if ( ast.is( NodeKind.E4X_FILTER ) )
         {
            visitExpression( ast.getChild( 0 ) );
            visitExpression( ast.getChild( 1 ) );
         }
         else if ( ast.is( NodeKind.E4X_STAR ) )
         {
            visitExpression( ast.getChild( 0 ) );
         }
         else
         {
            visitPrimaryExpression( ast );
         }
      }
   }
}