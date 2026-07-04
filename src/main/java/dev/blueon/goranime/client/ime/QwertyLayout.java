package dev.blueon.goranime.client.ime;

public final class QwertyLayout {

    private static final QwertyLayout INSTANCE = new QwertyLayout();

    private final String layout =
        "`1234567890-=~!@#$%^&*()_+" +
        "qwertyuiop[]\\QWERTYUIOP{}|" +
        "asdfghjkl;'ASDFGHJKL:\"" +
        "zxcvbnm,./ZXCVBNM<>?";

    private QwertyLayout() {}

    public static QwertyLayout get() {
        return INSTANCE;
    }

    public String layout() {
        return layout;
    }

    public int indexOf(int codePoint) {
        return layout.indexOf(codePoint);
    }
}
