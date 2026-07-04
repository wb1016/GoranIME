package dev.blueon.goranime.client.ime;

import com.google.common.base.Splitter;
import java.util.List;

public final class KeyboardLayout {

    public static final KeyboardLayout INSTANCE = new KeyboardLayout();

    /** Korean dubeolsik layout mapped to US qwerty positions. */
    public final String layout =
        "`1234567890-=~!@#$%^&*()_+" +
        "ㅂㅈㄷㄱㅅㅛㅕㅑㅐㅔ[]\\ㅃㅉㄸㄲㅆㅛㅕㅑㅒㅖ{}|" +
        "ㅁㄴㅇㄹㅎㅗㅓㅏㅣ;'ㅁㄴㅇㄹㅎㅗㅓㅏㅣ:\"" +
        "ㅋㅌㅊㅍㅠㅜㅡ,./ㅋㅌㅊㅍㅠㅜㅡ<>?";

    public final String chosungTable = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
    public final String jungsungTable = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
    public final String jongsungTable = "\u0000ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";

    public final List<String> jungsungRefTable = Splitter.on(",").splitToList(
        ",,,,,,,,,,ㅗㅏ,ㅗㅐ,ㅗㅣ,,,ㅜㅓ,ㅜㅔ,ㅜㅣ,,,ㅡㅣ,ㅣ");
    public final List<String> jongsungRefTable = Splitter.on(",").splitToList(
        ",,,ㄱㅅ,,ㄴㅈ,ㄴㅎ,,,ㄹㄱ,ㄹㅁ,ㄹㅂ,ㄹㅅ,ㄹㅌ,ㄹㅍ,ㄹㅎ,,,ㅂㅅ,,,,,,,,,");

    /** Current jamo assembly position in text. -1 means no composition in progress. */
    public int assemblePosition = -1;

    private KeyboardLayout() {}

    public int qwertyIndex(char ch) {
        return QwertyLayout.get().indexOf(ch);
    }
}
