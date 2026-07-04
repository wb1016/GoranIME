package dev.blueon.goranime.client.ime;

public final class LangTypeManager {

    private static final LangTypeManager INSTANCE = new LangTypeManager();

    private LanguageType current = LanguageType.EN;

    private LangTypeManager() {}

    public static LangTypeManager get() {
        return INSTANCE;
    }

    public LanguageType current() {
        return current;
    }

    public boolean isKorean() {
        return current == LanguageType.KO;
    }

    public void toggle() {
        current = current == LanguageType.KO ? LanguageType.EN : LanguageType.KO;
    }

    public void setKorean(boolean korean) {
        current = korean ? LanguageType.KO : LanguageType.EN;
    }
}
