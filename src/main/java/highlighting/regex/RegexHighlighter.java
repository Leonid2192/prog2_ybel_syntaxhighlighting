package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.ArrayList;
import java.util.List;

public class RegexHighlighter extends SyntaxHighlighter {

  private final List<Token> tokens;

  public RegexHighlighter() {
    this.tokens = MiniJavaTokens.defaultTokens();
  }

  // Aufgabe 2.1: Alle Matches unnabhängig voneinander in einer gemeinsamen Liste sammeln
  @Override
  public List<HighlightRegion> collectMatches(String text) {
    List<HighlightRegion> candidates = new ArrayList<>();
    if (text == null || text.isEmpty()) {
      return candidates;
    }

    // Jedes definierte Token wird auf den Text angewendet
    for (Token token : tokens) {
      candidates.addAll(token.test(text));
    }
    return candidates;
  }

  // Aufgabe 2.2: Konflikte für halboffene Intervalle [start, end) auflösen
  @Override
  public List<HighlightRegion> resolveConflicts(List<HighlightRegion> normalized) {
    List<HighlightRegion> resolved = new ArrayList<>();

    for (HighlightRegion current : normalized) {
      boolean hasConflict = false;

      for (HighlightRegion accepted : resolved) {
        // Überlappungsprüfung: Zwei Regionen schneiden sich NICHT, wenn:
        // current.end <= accepted.start ODER current.start >= accepted.end
        if (!(current.end() <= accepted.start() || current.start() >= accepted.end())) {
          hasConflict = true;
          break;
        }
      }

      // Wenn kein Konflikt mit einer bereits ausgewählten Region vorliegt, behalten
      if (!hasConflict) {
        resolved.add(current);
      }
    }

    return resolved;
  }
}
