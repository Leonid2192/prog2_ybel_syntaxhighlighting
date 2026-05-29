package highlighting.presets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.core.HighlightRegion;
import highlighting.regex.Token;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenTest {

  private List<Token> tokens;

  @BeforeEach
  public void setUp() {
    this.tokens = MiniJavaTokens.defaultTokens();
  }

  @Test
  public void testJavadocComment() {
    Token javadocToken = tokens.get(0);
    List<HighlightRegion> regions = javadocToken.test("/** Javadoc hier \n * zweite Zeile */");
    assertEquals(1, regions.size());

    List<HighlightRegion> noRegions = javadocToken.test("/* Normaler Blockkommentar */");
    assertTrue(noRegions.isEmpty());
  }

  @Test
  public void testBlockComment() {
    Token blockToken = tokens.get(1);
    List<HighlightRegion> regions = blockToken.test("/* Start Mitte Ende */");
    assertEquals(1, regions.size());
  }

  @Test
  public void testLineComment() {
    Token lineToken = tokens.get(2);
    List<HighlightRegion> regions = lineToken.test("int i = 0; // Kommentar mit public class");
    assertEquals(1, regions.size());
    assertEquals(11, regions.get(0).start());
  }

  @Test
  public void testStrings() {
    Token stringToken = tokens.get(3);
    List<HighlightRegion> regions = stringToken.test("String text = \"Hallo Welt\";");
    assertEquals(1, regions.size());

    List<HighlightRegion> multiple = stringToken.test("\"String eins\" und \"String zwei\"");
    assertEquals(2, multiple.size());
  }

  @Test
  public void testCharacters() {
    Token charToken = tokens.get(4);
    List<HighlightRegion> regions = charToken.test("char c = 'a';");
    assertEquals(1, regions.size());

    assertTrue(charToken.test("''").isEmpty());
    assertTrue(charToken.test("'abc'").isEmpty());
  }

  @Test
  public void testAnnotations() {
    Token annotationToken = tokens.get(5);
    List<HighlightRegion> regions = annotationToken.test("@Override @My-Custom-Annotation");
    assertEquals(2, regions.size());
  }

  @Test
  public void testKeywords() {
    Token keywordToken = tokens.get(6);
    List<HighlightRegion> regions = keywordToken.test("public class MyClass { return null; }");
    assertEquals(4, regions.size());

    List<HighlightRegion> noRegions = keywordToken.test("publication classifier returnable");
    assertTrue(noRegions.isEmpty());
  }
}
