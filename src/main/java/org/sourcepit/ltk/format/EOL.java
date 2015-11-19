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
public enum EOL {
   LF('\n'), CRLF('\r', '\n'), CR('\r');

   private static EOL system;

   private final char[] chars;

   private final String string;

   private EOL(char... eol) {
      this.chars = eol;
      this.string = String.valueOf(chars, 0, chars.length);
   }

   public static EOL system() {
      if (system == null) {
         system = parse(System.getProperty("line.separator"));
      }
      return system;
   }

   public static EOL parse(final String string) {
      for (EOL eol : values()) {
         if (eol.string.equals(string)) {
            return eol;
         }
      }
      throw new UnsupportedOperationException("Unsupported EOL style: " + string);
   }

   public String asString() {
      return string;
   }

   public char[] asChars() {
      return chars;
   }
}
