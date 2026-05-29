package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RegexHighlighterTest {

  @Test
  public void testResolveConflicts() {
    RegexHighlighter highlighter = new RegexHighlighter();

    // Fall 1: Zwei direkt aneinandergrenzende Regionen (müssen BEIDE bleiben)
    HighlightRegion r1 = new HighlightRegion(0, 5, null);
    HighlightRegion r2 = new HighlightRegion(5, 10, null);

    // Fall 2: Eine Region, die r1 und r2 schneidet (muss GEFILTERT werden)
    HighlightRegion r3 = new HighlightRegion(3, 8, null);

    List<HighlightRegion> normalized = List.of(r1, r2, r3);
    List<HighlightRegion> result = highlighter.resolveConflicts(normalized);

    // Es dürfen nur r1 und r2 übrig bleiben
    assertEquals(2, result.size());
    assertTrue(result.contains(r1));
    assertTrue(result.contains(r2));
    assertFalse(result.contains(r3));
  }
}
