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

package org.sourcepit.ltk.jcomment;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

/**
 * @author Bernd Vogt <bernd.vogt@sourcepit.org>
 */
public abstract class AbstractCommentLexer extends Lexer {

   public static final int JAVA_DOC_LINE_PREFIX = 2;

   protected boolean allowBlockTag;

   public AbstractCommentLexer(CharStream input) {
      super(input);
   }

   private Token cachedToken;

   @Override
   public Token nextToken() {
      if (cachedToken != null) {
         Token t = cachedToken;
         cachedToken = null;
         return t;
      }
      Token t = _nextToken();
      CommonToken aggregate = null;
      while (t.getType() == CommentLexer.CommentText) {
         if (aggregate == null) {
            aggregate = (CommonToken) t;
         }
         else {
            aggregate.setStopIndex(t.getStopIndex());
         }
         t = _nextToken();
      }
      if (aggregate == null) {
         return t;
      }
      cachedToken = t;
      return aggregate;
   }

   private Token _nextToken() {
      Token token = super.nextToken();
      switch (token.getType()) {
         case CommentLexer.DocCommentStart :
         case CommentLexer.BlockCommentStart :
         case CommentLexer.BlockCommentLinePrefix :
         case CommentLexer.Nl :
            allowBlockTag = true;
            break;
         case CommentLexer.Ws :
            break;
         default :
            allowBlockTag = false;
            break;
      }
      return token;
   }

   static boolean isTagStart(int c) {
      if (c >= 'A' && c <= 'Z') {
         return true;
      }
      if (c >= 'a' && c <= 'z') {
         return true;
      }
      return false;
   }

   static boolean isChar(int c, int... chars) {
      for (int _c : chars) {
         if (c == _c) {
            return true;
         }
      }
      return false;
   }

   static boolean isNotChar(int c, int... chars) {
      for (int _c : chars) {
         if (c == _c) {
            return false;
         }
      }
      return true;
   }

   protected int LA(int i) {
      return _input.LA(i);
   }
}
