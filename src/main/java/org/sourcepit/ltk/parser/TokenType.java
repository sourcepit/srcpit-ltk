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

package org.sourcepit.ltk.parser;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

import org.antlr.v4.runtime.Lexer;

public final class TokenType {
   private final Class<? extends Lexer> sourceType;
   private final int tokenId;

   public TokenType(Class<? extends Lexer> sourceType, int tokenId) {
      notNull(sourceType);
      isTrue(tokenId > -2);
      this.sourceType = sourceType;
      this.tokenId = tokenId;
   }

   public Class<? extends Lexer> getSourceType() {
      return sourceType;
   }

   public int getTokenId() {
      return tokenId;
   }

   public boolean is(Class<? extends Lexer> sourceType, int tokenId) {
      return this.tokenId == tokenId && this.sourceType.equals(sourceType);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
      result = prime * result + tokenId;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final TokenType other = (TokenType) obj;
      if (sourceType == null) {
         if (other.sourceType != null) {
            return false;
         }
      }
      else if (!sourceType.equals(other.sourceType)) {
         return false;
      }
      if (tokenId != other.tokenId) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append("TerminalType [sourceType=");
      builder.append(sourceType.getSimpleName());
      builder.append(", tokenId=");
      builder.append(tokenId);
      builder.append("]");
      return builder.toString();
   }


}