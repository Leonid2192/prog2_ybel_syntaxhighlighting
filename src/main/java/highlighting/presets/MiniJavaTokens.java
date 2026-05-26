package highlighting.presets;

import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {

    public static List<Token> defaultTokens() {
        return List.of(
            // 1. Javadoc-Kommentare
            Token.of(Pattern.compile("/\\*\\*(?s).*?\\*/"), MiniJavaColours.JAVADOC_COMMENT_COLOUR),

            // 2. Mehrzeilige Blockkommentare
            Token.of(Pattern.compile("/\\*(?s).*?\\*/"), MiniJavaColours.BLOCK_COMMENT_COLOUR),

            // 3. Einzeilige Kommentare
            Token.of(Pattern.compile("//.*"), MiniJavaColours.LINE_COMMENT_COLOUR),

            // 4. Strings (Exakt das Original-Pattern aus der GitHub-Vorlage)
            Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR),

            // 5. Characters
            Token.of(Pattern.compile("'.'"), MiniJavaColours.CHAR_LITERAL_COLOUR),

            // 6. Annotationen
            Token.of(Pattern.compile("@[a-zA-Z-]+"), MiniJavaColours.ANNOTATION_COLOUR),

            // 7. Keywords
            Token.of(Pattern.compile("\\b(package|import|class|public|private|final|return|null|new)\\b"), MiniJavaColours.KEYWORD_COLOUR)
        );
    }
}
