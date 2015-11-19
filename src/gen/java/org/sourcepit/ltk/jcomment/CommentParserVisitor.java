// Generated from CommentParser.g4 by ANTLR 4.5.1

/**
 * Copyright 2015 Bernd Vogt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sourcepit.ltk.jcomment;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CommentParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *           operations with no return type.
 */
public interface CommentParserVisitor<T> extends ParseTreeVisitor<T> {
   /**
    * Visit a parse tree produced by {@link CommentParser#comment}.
    * 
    * @param ctx the parse tree
    * @return the visitor result
    */
   T visitComment(CommentParser.CommentContext ctx);

   /**
    * Visit a parse tree produced by {@link CommentParser#docComment}.
    * 
    * @param ctx the parse tree
    * @return the visitor result
    */
   T visitDocComment(CommentParser.DocCommentContext ctx);

   /**
    * Visit a parse tree produced by {@link CommentParser#blockComment}.
    * 
    * @param ctx the parse tree
    * @return the visitor result
    */
   T visitBlockComment(CommentParser.BlockCommentContext ctx);

   /**
    * Visit a parse tree produced by {@link CommentParser#lineComment}.
    * 
    * @param ctx the parse tree
    * @return the visitor result
    */
   T visitLineComment(CommentParser.LineCommentContext ctx);

   /**
    * Visit a parse tree produced by {@link CommentParser#commentText}.
    * 
    * @param ctx the parse tree
    * @return the visitor result
    */
   T visitCommentText(CommentParser.CommentTextContext ctx);
}