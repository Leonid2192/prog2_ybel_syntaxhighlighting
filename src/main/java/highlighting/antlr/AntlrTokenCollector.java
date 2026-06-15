package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;

// TODO Phase III — AntlrTokenCollector (token-based syntax highlighting).

// This highlighter uses the ANTLR-generated MiniJavaLexer to turn the input text into a token
// stream. {@code collectMatches(String)} is the only method you need to implement: extract tokens
// of interest and map them to {@code HighlightRegions} using the colours from {@code
// MiniJavaColours}. Sorting, filtering of invalid regions, and conflict handling are performed by
// the base class {@code SyntaxHighlighter} via the template method {@code computeRegions(...)}.
public class AntlrTokenCollector extends SyntaxHighlighter {

  // TODO (Phase III — implement this method): Use the token stream produced by the ANTLR-generated
  // {@code MiniJavaLexer} to collect highlight regions.
  //
  // Requirements / hints:
  // - Iterate over the lexer tokens (typically via {@code CommonTokenStream}); ignore the EOF
  // token.
  // - For each token type that should be coloured (e.g., keywords, string/char literals, comments),
  // create a {@code HighlightRegion} with the corresponding colour from {@code MiniJavaColours}.
  // - Use {@code Token#getStartIndex()} and {@code Token#getStopIndex()} (inclusive) to compute
  // {@code [start, end)} ranges: {@code start = startIndex, end = stopIndex + 1}.
  // - Do not sort, merge, or resolve overlaps here; return all candidates as you find them.
  // Normalisation and conflict resolution are handled later by the template method.
  // - Annotation highlighting: colour '@' and the immediately following IDENTIFIER token (if
  // present).
  @Override
  public List<HighlightRegion> collectMatches(String text) {
      List<HighlightRegion> regions = new ArrayList<>();

      // ANTLR-Infrastruktur aufbauen
      CharStream charStream = CharStreams.fromString(text);
      MiniJavaLexer lexer = new MiniJavaLexer(charStream);
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);

      // Token-Stream initialisieren und alle Token laden
      tokenStream.fill();
      List<Token> tokens = tokenStream.getTokens();

      for (Token token : tokens) {
          // EOF (End of File) ignorieren
          if (token.getType() == Token.EOF) {
              continue;
          }

          // Bestimme den Typ/Style basierend auf dem Token-Typ aus der Grammatik
          Color styleCategory = mapTokenToStyle(token.getType());

          if (styleCategory != null) {
              int start = token.getStartIndex();
              // ANTLR StopIndex ist inklusiv, HighlightRegion verlangt oft die Länge oder exklusives Ende
              int end = token.getStopIndex() + 1;

              regions.add(new HighlightRegion(start, end, styleCategory));
          }
      }

      return regions;
  }

    private Color mapTokenToStyle(int tokenType) {
        switch (tokenType) {
            case MiniJavaLexer.BLOCK_COMMENT:
            case MiniJavaLexer.LINE_COMMENT:
                return Color.GREEN; // Oder welche Farbe Kommentare haben sollen

            case MiniJavaLexer.PUBLIC:
            case MiniJavaLexer.CLASS:
            case MiniJavaLexer.RETURN:
            case MiniJavaLexer.IF:
            case MiniJavaLexer.ELSE:
            case MiniJavaLexer.WHILE:
                return Color.ORANGE; // Oder Color.BLUE für Keywords

            case MiniJavaLexer.STRING_LITERAL:
                return Color.BLUE;

            default:
                return null; // Keine Farbe -> Standardtext
        }
    }


}
