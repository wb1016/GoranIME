package dev.blueon.goranime.client.ime;

public final class HangulUtil {

    private HangulUtil() {}

    /**
     * Normalize case for qwerty→jamo mapping, accounting for CapsLock and Shift modifiers.
     */
    public static int fixedQwertyIndex(int codePoint, int modifiers) {
        boolean shift = (modifiers & 0x01) == 1;
        int cp = codePoint;

        if (cp >= 65 && cp <= 90) {
            cp += 32;
        }
        if (cp >= 97 && cp <= 122) {
            if (shift) {
                cp -= 32;
            }
        }
        return QwertyLayout.get().indexOf(cp);
    }

    /**
     * Fix CapsLock/shift artifacts that would produce wrong jamo.
     */
    public static char fix(int modifiers, char original, char hangul) {
        if (HangulProcessor.isHangulChar(hangul)) {
            int idx = fixedQwertyIndex(original, modifiers);
            if (idx != -1) {
                return KeyboardLayout.INSTANCE.layout.charAt(idx);
            }
        }
        return hangul;
    }
}
