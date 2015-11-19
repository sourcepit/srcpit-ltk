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

import java.util.List;

public interface ParseNode extends Adaptable {

   boolean isRoot();

   boolean isRule();

   Rule asRule();

   boolean isTerminal();

   Terminal asTerminal();

   Rule getParent();

   List<ParseNode> getChildren();

   List<ParseNode> getVisibleChildren();

   boolean isNestedLanguage();

   Terminal getOrigin();

   boolean accept(ParseTreeVisitor visitor);

}
