package cc.yamrc.konechat.hypertext;

public sealed interface MarkupToken permits MarkupToken.Text, MarkupToken.Open,
        MarkupToken.Close, MarkupToken.Legacy {
    record Text(String value, boolean matchable) implements MarkupToken {
        public Text {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("text token must not be empty");
            }
        }
    }

    record Open(String value) implements MarkupToken {
        public Open {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("open token must not be empty");
            }
        }
    }

    record Close(String value) implements MarkupToken {
        public Close {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("close token must not be empty");
            }
        }
    }

    record Legacy(char code) implements MarkupToken {
    }
}
