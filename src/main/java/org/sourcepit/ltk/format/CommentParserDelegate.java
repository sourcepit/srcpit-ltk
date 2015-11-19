/*
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

package org.sourcepit.ltk.format;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.sourcepit.ltk.antlr4.ParserUtils;
import org.sourcepit.ltk.jcomment.CommentLexer;
import org.sourcepit.ltk.jcomment.CommentParser;
import org.sourcepit.ltk.parser.ParseResult;
import org.sourcepit.ltk.parser.ParserDelegate;
import org.sourcepit.ltk.parser.TokenUtils;

public class CommentParserDelegate implements ParserDelegate {
   @Override
   public ParseResult parse(String input, Class<? extends ParserRuleContext> ruleType) {
      final CharStream charStream = new ANTLRInputStream(input);
      final Lexer lexer = new CommentLexer(charStream);
      final BufferedTokenStream tokenStream = new CommonTokenStream(lexer);
      final CommentParser parser = new CommentParser(tokenStream);
      try {
         final ParseTree parseTree = ParserUtils.parse(parser, ruleType);
         return new ParseResult(lexer, tokenStream, parser, parseTree);
      }
      catch (RecognitionException e) {
         return new ParseResult(lexer, tokenStream, parser, e);
      }
   }

   @Override
   public ParseResult parseNestedLanguage(Class<? extends Lexer> sourceType, Token token) {
      return null;
   }

   @Override
   public int getLen(RuleNode parent, Token token, List<Token> hiddenTokensToRight, TokenStream tokenStream) {
      int len = 0;
      for (org.antlr.v4.runtime.Token hiddenToken : hiddenTokensToRight) {
         final String text = hiddenToken.getText();
         if (TokenUtils.isWs(text)) {
            len++;
         }
         else {
            if (hiddenToken.getCharPositionInLine() > token.getCharPositionInLine()) {
               len++;
            }
            else {
               break;
            }
         }
      }
      return len;
   }
}