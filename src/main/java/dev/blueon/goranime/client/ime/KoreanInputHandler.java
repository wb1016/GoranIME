package dev.blueon.goranime.client.ime;

/**
 * Manual Korean Hangul composition from raw keystrokes. Handles jamo
 * assembly/disassembly, double consonants/vowels, and final consonant splitting.
 */
public final class KoreanInputHandler {

    private KoreanInputHandler() {}

    /**
     * Decompose the Hangul syllable at cursor on backspace.
     */
    public static boolean onBackspace(int cursor, String text, TextInserter inserter) {
        if (cursor == 0 || cursor != KeyboardLayout.INSTANCE.assemblePosition || text.isEmpty()) {
            return false;
        }

        char ch = text.charAt(cursor - 1);

        if (HangulProcessor.isHangulSyllable(ch)) {
            int code = ch - 0xAC00;
            int cho = code / (21 * 28);
            int jung = (code % (21 * 28)) / 28;
            int jong = (code % (21 * 28)) % 28;

            if (jong != 0) {
                char[] ref = KeyboardLayout.INSTANCE.jongsungRefTable.get(jong).toCharArray();
                if (ref.length == 2) {
                    jong = KeyboardLayout.INSTANCE.jongsungTable.indexOf(ref[0]);
                } else {
                    jong = 0;
                }
                inserter.replace(String.valueOf(HangulProcessor.synthesize(cho, jung, jong)));
            } else {
                char[] ref = KeyboardLayout.INSTANCE.jungsungRefTable.get(jung).toCharArray();
                if (ref.length == 2) {
                    jung = KeyboardLayout.INSTANCE.jungsungTable.indexOf(ref[0]);
                    inserter.replace(String.valueOf(HangulProcessor.synthesize(cho, jung, 0)));
                } else {
                    inserter.replace(String.valueOf(KeyboardLayout.INSTANCE.chosungTable.charAt(cho)));
                }
            }
            return true;
        } else if (HangulProcessor.isHangulChar(ch)) {
            KeyboardLayout.INSTANCE.assemblePosition = -1;
            return false;
        }
        return false;
    }

    /**
     * Process a character typed while in Korean mode. Assembles jamo into Hangul syllables.
     */
    public static boolean onCharTyped(
        int codePoint, int modifiers, String text,
        int cursor, boolean noSelection, TextInserter inserter
    ) {
        int idx = HangulUtil.fixedQwertyIndex(codePoint, modifiers);
        if (idx == -1) {
            KeyboardLayout.INSTANCE.assemblePosition = -1;
            return false;
        }

        if (cursor == 0) {
            char curr = KeyboardLayout.INSTANCE.layout.charAt(idx);
            inserter.write(String.valueOf(curr));
            KeyboardLayout.INSTANCE.assemblePosition = HangulProcessor.isHangulChar(curr) ? cursor + 1 : -1;
            return true;
        }

        char prev = text.charAt(cursor - 1);
        char curr = KeyboardLayout.INSTANCE.layout.charAt(idx);

        if (cursor == KeyboardLayout.INSTANCE.assemblePosition && noSelection) {
            // Consonant + vowel → syllable
            if (HangulProcessor.isJaeum(prev) && HangulProcessor.isMoeum(curr)) {
                int cho = KeyboardLayout.INSTANCE.chosungTable.indexOf(prev);
                int jung = KeyboardLayout.INSTANCE.jungsungTable.indexOf(curr);
                inserter.replace(String.valueOf(HangulProcessor.synthesize(cho, jung, 0)));
                KeyboardLayout.INSTANCE.assemblePosition = cursor;
                return true;
            }

            if (HangulProcessor.isHangulSyllable(prev)) {
                int code = prev - 0xAC00;
                int cho = code / (21 * 28);
                int jung = (code % (21 * 28)) / 28;
                int jong = (code % (21 * 28)) % 28;

                // Double vowel
                if (jong == 0 && HangulProcessor.isJungsung(prev, curr)) {
                    jung = HangulProcessor.getJungsung(prev, curr);
                    inserter.replace(String.valueOf(HangulProcessor.synthesize(cho, jung, 0)));
                    KeyboardLayout.INSTANCE.assemblePosition = cursor;
                    return true;
                }

                // Add final consonant
                if (jong == 0 && HangulProcessor.isJongsung(curr)) {
                    int newJong = KeyboardLayout.INSTANCE.jongsungTable.indexOf(curr);
                    inserter.replace(String.valueOf(HangulProcessor.synthesize(cho, jung, newJong)));
                    KeyboardLayout.INSTANCE.assemblePosition = cursor;
                    return true;
                }

                // Double final consonant
                if (jong != 0 && HangulProcessor.isJongsung(prev, curr)) {
                    int newJong = HangulProcessor.getJongsung(prev, curr);
                    inserter.replace(String.valueOf(HangulProcessor.synthesize(cho, jung, newJong)));
                    KeyboardLayout.INSTANCE.assemblePosition = cursor;
                    return true;
                }

                // Split final consonant for next syllable
                if (jong != 0 && HangulProcessor.isJungsung(curr)) {
                    char[] ref = KeyboardLayout.INSTANCE.jongsungRefTable.get(jong).toCharArray();
                    int newCho;
                    if (ref.length == 2) {
                        newCho = KeyboardLayout.INSTANCE.chosungTable.indexOf(ref[1]);
                        jong = KeyboardLayout.INSTANCE.jongsungTable.indexOf(ref[0]);
                    } else {
                        newCho = KeyboardLayout.INSTANCE.chosungTable.indexOf(
                            KeyboardLayout.INSTANCE.jongsungTable.charAt(jong));
                        jong = 0;
                    }
                    char prevSyl = HangulProcessor.synthesize(cho, jung, jong);
                    int newJung = KeyboardLayout.INSTANCE.jungsungTable.indexOf(curr);
                    char nextSyl = HangulProcessor.synthesize(newCho, newJung, 0);
                    inserter.replace(String.valueOf(prevSyl) + nextSyl);
                    KeyboardLayout.INSTANCE.assemblePosition = cursor + 1;
                    return true;
                }
            }
        }

        char hangul = HangulUtil.fix(modifiers, (char) codePoint, curr);
        inserter.write(String.valueOf(hangul));
        KeyboardLayout.INSTANCE.assemblePosition = HangulProcessor.isHangulChar(hangul) ? cursor + 1 : -1;
        return true;
    }

    @FunctionalInterface
    public interface TextInserter {
        void write(String text);

        default void replace(String text) {
            write(text);
        }
    }
}
