package dev.blueon.goranime.client.ime;

import com.google.common.base.Splitter;
import java.util.List;

public final class HangulProcessor {

    private static final String JUNGSUNG_TABLE = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
    private static final String JONGSUNG_TABLE = "\u0000ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
    private static final List<String> JUNGSUNG_COMBI = Splitter.on(",").splitToList(",,,,,,,,,ㅗㅏ,ㅗㅐ,ㅗㅣ,,,ㅜㅓ,ㅜㅔ,ㅜㅣ,,,ㅡㅣ,ㅣ");
    private static final List<String> JONGSUNG_COMBI = Splitter.on(",").splitToList(",,,ㄱㅅ,,ㄴㅈ,ㄴㅎ,,,ㄹㄱ,ㄹㅁ,ㄹㅂ,ㄹㅅ,ㄹㅌ,ㄹㅍ,ㄹㅎ,,,ㅂㅅ,,,,,,,,,");

    private HangulProcessor() {}

    public static boolean isJaeum(char c) {
        return c >= 0x3131 && c <= 0x314E;
    }

    public static boolean isMoeum(char c) {
        return c >= 0x314F && c <= 0x3163;
    }

    public static boolean isJungsung(char c) {
        return JUNGSUNG_TABLE.indexOf(c) != -1;
    }

    public static boolean isJungsung(char prev, char curr) {
        int jung = ((prev - 0xAC00) % (21 * 28)) / 28;
        for (int i = 0; i < JUNGSUNG_COMBI.size(); i++) {
            char[] tbl = JUNGSUNG_COMBI.get(i).toCharArray();
            if (tbl.length == 2 && tbl[0] == JUNGSUNG_TABLE.charAt(jung) && tbl[1] == curr) {
                return true;
            }
        }
        return false;
    }

    public static int getJungsung(char prev, char curr) {
        int jung = ((prev - 0xAC00) % (21 * 28)) / 28;
        for (int i = 0; i < JUNGSUNG_COMBI.size(); i++) {
            char[] tbl = JUNGSUNG_COMBI.get(i).toCharArray();
            if (tbl.length == 2 && tbl[0] == JUNGSUNG_TABLE.charAt(jung) && tbl[1] == curr) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isJongsung(char c) {
        return JONGSUNG_TABLE.indexOf(c) != -1;
    }

    public static boolean isJongsung(char prev, char curr) {
        int jong = ((prev - 0xAC00) % (21 * 28)) % 28;
        for (int i = 0; i < JONGSUNG_COMBI.size(); i++) {
            char[] tbl = JONGSUNG_COMBI.get(i).toCharArray();
            if (tbl.length == 2 && tbl[0] == JONGSUNG_TABLE.charAt(jong) && tbl[1] == curr) {
                return true;
            }
        }
        return false;
    }

    public static int getJongsung(char prev, char curr) {
        int jong = ((prev - 0xAC00) % (21 * 28)) % 28;
        for (int i = 0; i < JONGSUNG_COMBI.size(); i++) {
            char[] tbl = JONGSUNG_COMBI.get(i).toCharArray();
            if (tbl.length == 2 && tbl[0] == JONGSUNG_TABLE.charAt(jong) && tbl[1] == curr) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isHangulSyllable(char c) {
        return c >= 0xAC00 && c <= 0xD7AF;
    }

    public static boolean isHangulChar(char c) {
        return isJaeum(c) || isMoeum(c) || isHangulSyllable(c);
    }

    public static char synthesize(int cho, int jung, int jong) {
        return (char) ('가' + cho * 28 * 21 + jung * 28 + jong);
    }
}
