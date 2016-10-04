package com.retro.engine.util.string;

import com.retro.engine.debug.Debug;
import com.retro.engine.util.vector.Vector2;

import java.util.HashMap;

/**
 * Created by Michael on 7/15/2016.
 */
public class UtilString {

    /**
     * Format a string with the current formatters in the retro engine. Will change in time.
     * @param msg Message to be formatted.
     * @return Message that has been formatted.
     */
    public static String formatAll(String msg){
        return (new FormatterTime()).format(msg);
    }

    public static int countChars(String t, char c){
        int count = 0;
        char[] text = t.toCharArray();
        for(char cc : text){
            if(cc == c){
                count++;
            }
        }
        return count;
    }

    public static String getTextWithWrap(String text, int wrap){
        if(wrap <= 0)
            return text;

        String newText = "";
        int currentLineSize = 0;
        for(int i=0;i<text.length();i++)
        {
            currentLineSize ++;
            newText += text.charAt(i);

            if(text.charAt(i) == '\n')
            {
                currentLineSize = 0;
                continue;
            }
            if(currentLineSize >= wrap && (i != text.length()-1 && text.charAt(i+1) != '\n')){
                newText+="\n";
                currentLineSize = 0;
            }else if(i != text.length()-1 && text.charAt(i+1) == '\n')
                currentLineSize = 0;
        }

        return newText;
    }

    // A HashMap of the values inside a bitmap font and the respective letter.
    private static HashMap<Character, Vector2> c_alphabetMap;
    public static float getAlphabetWidth(){
        float ret = 7f / 672f;
        return ret;
    }
    public static void loadAlphabetHashMap(){// 7 wide, 96 total
        c_alphabetMap = new HashMap<>();
        int start = 32;
        int end = 126;
        for(int i = start;i<=end;i++)
        {
            //Debug.out("\tAlphabet: "+ (char)i + "  - "+(new Vector2( (float)(i-start)*getAlphabetWidth(), 0)).toString());
            c_alphabetMap.put((char) i, new Vector2( (float)(i-start)*getAlphabetWidth(), 0));
        }
    }
    public static Vector2 getCharacterLocation(char c){
        if(c_alphabetMap == null)
            loadAlphabetHashMap();
        Vector2 a = c_alphabetMap.get(c);

        return a;
    }
}
