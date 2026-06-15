package highlighting.antlr;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;


/// MiniJava Pretty Printer (minimal, stateful)
///
/// Requirements:
/// - Reproduce the whole program (comments and whitespaces are gone).
/// - Ignore whitespace from the input; instead, generate:
///     - indentation for class bodies and blocks,
///     - exactly one line per statement (lines ending in ';').
///
/// Simplification:
/// Everything that is not indentation or line breaks is printed as raw tokens (with a very simple
/// space heuristic). Expression and signature formatting is therefore not "nice", which is
/// acceptable for this exercise.
public final class PrettyPrinterVisitor extends MiniJavaBaseVisitor<Void> {

  private final StringBuilder out = new StringBuilder();
  private final int indentWidth;
  private int currentIndent = 0;
  private boolean atLineStart = true;

  // For simple spacing between tokens:
  private Token lastToken = null;

  public PrettyPrinterVisitor(int indentWidth) {
    this.indentWidth = Math.max(0, indentWidth);
  }

  public String result() {
    return out.toString();
  }

  // ----------------------------------------------------
  // Structural methods – these enforce indentation and "one statement per line"
  //
  // TODO: implement the four structural visitXyz-methods below: visitCompilationUnit,
  // visitClassBody, visitBlock, and visitStatement
  // ----------------------------------------------------

    @Override
    public Void visitCompilationUnit(MiniJavaParser.CompilationUnitContext ctx) {
        // Gehe durch alle Kindknoten der Datei (z.B. package, imports, class)
        for (int i = 0; i < ctx.getChildCount(); i++) {
            var child = ctx.getChild(i);
            visit(child);

            // Nach dem Package-Statement oder einem Import werfen wir eine Leerzeile ein,
            // um die Teile laut TODO sinnvoll zu trennen.
            String childClass = child.getClass().getSimpleName();

            if (childClass.contains("Package") || childClass.contains("Import")) {
                nl();
            }
        }

        return null;
    }

    @Override
    public Void visitClassBody(MiniJavaParser.ClassBodyContext ctx) {
        // Öffnende Klammer '{' ausgeben und in neue Zeile springen
        writeln("{");
        currentIndent++; // Einrückung für die Member erhöhen

        // Alle Member (Felder, Methoden) zwischen { und } besuchen.
        // Index 0 ist '{', der letzte Index ist '}'
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            visit(ctx.getChild(i));
            nl(); // Jedes Member bekommt eine eigene Zeile
        }

        currentIndent--; // Einrückung wieder zurücksetzen
        write("}");      // Schließende Klammer auf neuer Zeile ausgeben
        return null;
    }

    @Override
    public Void visitBlock(MiniJavaParser.BlockContext ctx) {
        writeln("{");
        currentIndent++; // Weiter reinwandern

        // Alle Statements im Block ablaufen (ohne die äußeren Klammern)
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            visit(ctx.getChild(i));
        }

        currentIndent--; // Wieder rauswandern
        write("}");
        return null;
    }

    @Override
    public Void visitStatement(MiniJavaParser.StatementContext ctx) {
        // Wir lassen ANTLR das Statement (und seine Unterknoten) ausgeben
        visitChildren(ctx);

        // Nach JEDEM normalen Statement machen wir einen Zeilenumbruch.
        // Wenn das Statement ein Block ({...}), ein If oder ein While war,
        // haben diese meistens schon ihr eigenes 'nl()' am Ende getriggert.
        // Für einfache Statements (Zuweisungen, Returns) erzwingen wir hier die neue Zeile:
        if (ctx.block() == null && ctx.IF() == null && ctx.getChildCount() > 0) {
            // Da wir im Screenshot sehen, dass ctx.IF() existiert:
            // Wenn es kein Block und kein If-Statement ist, machen wir nach dem einfachen Statement einen Umbruch.
            nl();
        }
        return null;
    }

  // ---------------- helper methods ----------------

  private void indent() {
    if (atLineStart) {
      out.repeat(" ", Math.max(0, indentWidth * currentIndent));
      atLineStart = false;
    }
  }

  private void write(String s) {
    if (s == null || s.isEmpty()) return;
    indent();
    out.append(s);
  }

  private void nl() {
    out.append('\n');
    atLineStart = true;
    lastToken = null; // Reset spacing context at the beginning of a line
  }

  private void writeln(String s) {
    write(s);
    nl();
  }

  // --------------- token output + basic spacing ---------------

  @Override
  public Void visitTerminal(TerminalNode node) {
    Token t = node.getSymbol();
    String text = t.getText();

    if (lastToken != null) {
      int prevType = lastToken.getType();
      int curType = t.getType();

      // Simple heuristic: insert a space between "word-like" tokens
      if (needsSpaceBetween(prevType, curType)) write(" ");
    }

    write(text);
    lastToken = t;
    return null;
  }

  private boolean needsSpaceBetween(int prevType, int curType) {
    return isWordLike(prevType) && isWordLike(curType);
  }

  private boolean isWordLike(int type) {
    return type == MiniJavaLexer.IDENTIFIER
        || type == MiniJavaLexer.STRING_LITERAL
        || type == MiniJavaLexer.CHAR_LITERAL
        || type == MiniJavaLexer.NULL
        || type == MiniJavaLexer.PACKAGE
        || type == MiniJavaLexer.IMPORT
        || type == MiniJavaLexer.CLASS
        || type == MiniJavaLexer.PUBLIC
        || type == MiniJavaLexer.PRIVATE
        || type == MiniJavaLexer.FINAL
        || type == MiniJavaLexer.RETURN
        || type == MiniJavaLexer.NEW
        || type == MiniJavaLexer.IF
        || type == MiniJavaLexer.ELSE
        || type == MiniJavaLexer.WHILE
        || type == MiniJavaLexer.EXTENDS
        || type == MiniJavaLexer.IMPLEMENTS;
  }
}
