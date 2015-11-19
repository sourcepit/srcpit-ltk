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

import static org.sourcepit.ltk.jcomment.CommentLexer.BlockCommentEnd;
import static org.sourcepit.ltk.parser.ParseNodeUtils.isRuleOfType;
import static org.sourcepit.ltk.parser.ParseNodeUtils.isTerminalOfType;
import static org.sourcepit.ltk.parser.TerminalUtils.isWs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.RuleContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Lexer;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.ActionContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.GrammarDeclContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.IdContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.LabeledAltContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.LexerAltContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.LexerAltListContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.LexerCommandContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.LexerRuleBlockContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.LexerRuleContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.ModeSpecContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.OptionContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.OptionValueContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.OptionsSpecBodyContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.PrequelConstructContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.RuleAltListContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.RuleBlockContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.TokenContext;
import org.sourcepit.ltk.antlr4.ANTLRv4Parser.TokensSpecBodyContext;
import org.sourcepit.ltk.jcomment.CommentLexer;
import org.sourcepit.ltk.jcomment.CommentParser.CommentContext;
import org.sourcepit.ltk.parser.ParseNode;
import org.sourcepit.ltk.parser.Rule;
import org.sourcepit.ltk.parser.Terminal;
import org.sourcepit.ltk.parser.TerminalUtils;

public class AntlrRendererFactory extends CommentRendererFactory implements RendererFactory {

   @Override
   public boolean isPartiallyRendered(ParseNode node) {
      return super.isPartiallyRendered(node);
   }

   private final class NoopRenderer implements Renderer {
      @Override
      public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
      }
   }

   private final class BlankRenderer implements Renderer {
      @Override
      public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
         if (!lines.isPrevCharWs()) {
            final Terminal terminal;
            if (node.isRule()) {
               terminal = getFirstTerminal(node);
            }
            else {
               terminal = node.asTerminal();
            }
            if (terminal != null && !isPrevBlockCommentEnd(terminal)) {
               out.append(' ');
            }
         }
      }

      private boolean isPrevBlockCommentEnd(Terminal terminal) {
         final Terminal previous = terminal.getPrevious();
         return previous != null && previous.getToken().getType().is(CommentLexer.class, BlockCommentEnd);
      }
   }

   private final class NewLineRenderer implements Renderer {

      private final int numberOfNewLines;

      public NewLineRenderer() {
         this(1);
      }

      public NewLineRenderer(int numberOfNewLines) {
         this.numberOfNewLines = numberOfNewLines;
      }


      @Override
      public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {

         if (lines.isNewLine() && lines.getCurrentLineNumber() == 1) {
            return;
         }

         while (lines.getPrevNewLines() < numberOfNewLines) {
            out.append('\n');
         }
      }
   }

   interface ParseNodeFilter {
      boolean select(ParseNode child);
   }

   public static List<ParseNode> getChildren(ParseNode node, ParseNodeFilter filter) {
      List<ParseNode> result = new ArrayList<>();
      for (ParseNode child : node.getChildren()) {
         if (filter.select(child)) {
            result.add(child);
         }
      }
      return result;
   }

   @Override
   public Renderer createPreRenderer(ParseNode node) {
      Renderer renderer = super.createPreRenderer(node);
      if (renderer != null) {
         return renderer;
      }

      if (isRuleOfType(node, GrammarDeclContext.class)) {
         return new NewLineRenderer();
      }

      if (isRuleOfType(node, CommentContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               if (!lines.isPrevCharWs()) {
                  final Terminal previous = node.asRule().getOrigin().getPrevious();

                  if (previous != null && isWs(previous)) {
                     if (previous.getToken().getText().contains("\n")) {
                        out.append('\n');
                     }
                     else {
                        out.append(' ');
                     }
                  }

               }
            }
         };
      }

      if (isRuleOfType(node, ParserRuleSpecContext.class)) {
         return new NewLineRenderer(2);
      }

      if (isRuleOfType(node, LexerRuleContext.class)) {
         return new NewLineRenderer(2);
      }

      if (isRuleOfType(node, PrequelConstructContext.class)) {
         return new NewLineRenderer(2);
      }

      if (isRuleOfType(node, ModeSpecContext.class)) {
         return new NewLineRenderer(2);
      }

      if (isRuleOfType(node, OptionContext.class)) {
         return new NewLineRenderer();
      }

      if (isRuleOfType(node, TokenContext.class)) {
         return new NewLineRenderer();
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.GRAMMAR)) {
         return new BlankRenderer();
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.ASSIGN)) {
         return new BlankRenderer();
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.ACTION)) {
         final Rule parent = node.getParent();
         if (parent != null) {
            if (isRuleOfType(parent, ActionContext.class)) {
               return new BlankRenderer();
            }
         }
      }

      // parser rule
      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.RULE_REF)) {
         if (isRuleOfType(node.getParent(), ParserRuleSpecContext.class)) {
            return new BlankRenderer();
         }
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.COLON)) {
         if (isRuleOfType(node.getParent(), RuleBlockContext.class)) {
            return new NewLineRenderer();
         }
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.OR)) {
         if (isRuleOfType(node.getParent(), RuleAltListContext.class)) {
            return new NewLineRenderer();
         }
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.SEMI)) {
         if (isRuleOfType(node.getParent(), RuleBlockContext.class)) {
            return new NewLineRenderer();
         }
      }

      if (isRuleOfType(node, LabeledAltContext.class)) {
         return new BlankRenderer();
      }

      // lexer rule
      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.TOKEN_REF)) {
         if (isRuleOfType(node.getParent(), LexerRuleContext.class)) {
            return new BlankRenderer();
         }
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.COLON)) {
         if (isRuleOfType(node.getParent(), LexerRuleBlockContext.class)) {
            return new NewLineRenderer();
         }
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.OR)) {
         if (isRuleOfType(node.getParent(), LexerAltListContext.class)) {
            if (isRuleOfType(node.getParent().getParent(), LexerRuleBlockContext.class)) {
               return new NewLineRenderer();
            }
         }
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.SEMI)) {
         if (isRuleOfType(node.getParent(), LexerRuleBlockContext.class)) {
            return new NewLineRenderer();
         }
      }

      if (isRuleOfType(node, LexerAltContext.class)) {
         return new BlankRenderer();
      }

      if (isRuleOfType(node, IdContext.class)) {
         final Rule parent = node.getParent();
         if (parent != null) {
            if (isRuleOfType(parent, OptionContext.class) || isRuleOfType(parent, OptionValueContext.class)
               || isRuleOfType(parent, ActionContext.class)) {
               return null;
            }
         }
         return new BlankRenderer();
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.DOT)) {
         return null;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.SEMI)) {
         return null;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.COMMA)) {
         return null;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.QUESTION)) {
         return null;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.PLUS)) {
         return null;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.STAR)) {
         return null;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.COLONCOLON)) {
         return null;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.LPAREN)) {
         final Rule parent = node.getParent();
         if (parent != null && isRuleOfType(parent, LexerCommandContext.class)) {
            return null;
         }
      }

      if (hasParentOfType(node, OptionValueContext.class)) {
         return null;
      }

      if (hasParentOfType(node, ActionContext.class)) {
         return null;
      }

      if (node.isTerminal() && !TerminalUtils.isWs(node.asTerminal())) {
         return new BlankRenderer();
      }

      return null;
   }

   private static boolean hasParentOfType(ParseNode node, Class<? extends RuleContext> type) {
      final Rule parent = node.getParent();
      if (parent == null) {
         return false;
      }
      if (type.isAssignableFrom(parent.getType())) {
         return true;
      }
      return hasParentOfType(parent, type);
   }

   @Override
   public Renderer createPostRenderer(ParseNode node) {
      Renderer renderer = super.createPostRenderer(node);
      if (renderer != null) {
         return renderer;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.DOC_COMMENT)) {
         return new NewLineRenderer();
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.ASSIGN)) {
         return new BlankRenderer();
      }

      if (isRuleOfType(node, OptionsSpecBodyContext.class)) {
         return new NewLineRenderer();
      }

      if (isRuleOfType(node, TokensSpecBodyContext.class)) {
         return new NewLineRenderer();
      }

      if (isRuleOfType(node, CommentContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               final Terminal terminal = getNextTerminal(node.asRule());
               if (terminal != null && isWs(terminal)) {
                  final List<ParseNode> siblings = terminal.getParent().getChildren();
                  int indexOf = siblings.indexOf(terminal);
                  if (indexOf < siblings.size() - 1) {
                     if (terminal.getToken().getText().contains("\n")) {
                        out.append('\n');
                     }
                     else {
                        out.append(' ');
                     }
                  }
               }
            }
         };
      }

      return null;
   }

   private Terminal getNextTerminal(Rule rule) {

      Rule parent = rule.getParent();
      if (parent == null) {
         return null;
      }

      final List<ParseNode> children = parent.getChildren();

      int idx = children.indexOf(rule) + 1;
      if (idx > 0) {
         for (; idx < children.size(); idx++) {
            final ParseNode child = children.get(idx);
            if (child.isTerminal()) {
               return child.asTerminal();
            }
            else {
               final Terminal terminal = getFirstTerminal(child.asRule());
               if (terminal != null) {
                  return terminal;
               }
            }
         }
      }

      return getNextTerminal(parent);
   }

   private static Terminal getFirstTerminal(ParseNode node) {
      final ParseTreeVisitorWithResult<Terminal> visitor = new ParseTreeVisitorWithResult<Terminal>(null) {
         @Override
         public boolean visit(Terminal terminal) {
            setResult(terminal);
            return false;
         }
      };
      node.accept(visitor);
      return visitor.getResult();
   }

   @Override
   public Renderer createMainRenderer(ParseNode node) {
      Renderer renderer = super.createMainRenderer(node);
      if (renderer != null) {
         return renderer;
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.OPTIONS)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("options {");
            }
         };
      }

      if (isTerminalOfType(node, ANTLRv4Lexer.class, ANTLRv4Lexer.TOKENS)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("tokens {");
            }
         };
      }

      if (node.isTerminal() && node.asTerminal().getToken().getType().getTokenId() > 0) {
         return new TerminalRenderer();
      }
      return null;
   }

   @Override
   public Renderer createIndentationRenderer(ParseNode node) {
      Renderer renderer = super.createIndentationRenderer(node);
      if (renderer != null) {
         return renderer;
      }

      if (isRuleOfType(node, OptionsSpecBodyContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("    ");
            }
         };
      }

      if (isRuleOfType(node, TokensSpecBodyContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("    ");
            }
         };
      }

      // parser rule
      if (isRuleOfType(node, RuleBlockContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("    ");
            }
         };
      }

      if (isRuleOfType(node, LabeledAltContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("  ");
            }
         };
      }

      // lexer rule
      if (isRuleOfType(node, LexerRuleBlockContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("    ");
            }
         };
      }

      if (isRuleOfType(node, LexerAltContext.class)) {
         return new Renderer() {
            @Override
            public void render(LineCounter lines, ParseNode node, Appendable out) throws IOException {
               out.append("  ");
            }
         };
      }

      return null;
   }
}