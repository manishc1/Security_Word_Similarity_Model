package demo.pos;
import com.aliasi.corpus.StringParser;
import com.aliasi.corpus.TagHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PennTreebankMrgTagParser extends StringParser<TagHandler> {

    public void parseString(char[] cs, int start, int end) {
        // System.out.println("parseString(" + new String(cs,start,end-start) + ")");
        for (int i = start; i < end; ++i) {
            if (cs[i] != '(') continue;
            List<String> tokList = new ArrayList<String>();
            List<String> tagList = new ArrayList<String>();
            i = parseTree(cs,i,tokList,tagList);
            String[] toks = tokList.<String>toArray(new String[tokList.size()]);
            String[] tags = tagList.<String>toArray(new String[tagList.size()]);
            getHandler().handle(toks,null,tags);
        }
    }

    // returns one past end of tree
    int parseTree(char[] cs, int i, List<String> tokList, List<String> tagList) {
        // System.out.println("parseTree(" + i + ")");
        while (cs[i] != '(') ++i;
        StringBuilder sbCat = new StringBuilder();
        i = parseToken(cs,i+1,sbCat);
        while (cs[i] == ' ' || cs[i] == '\n') ++i;
        if (cs[i] == '(') {
            i = parseTrees(cs,i,tokList,tagList);
        } else {
            StringBuilder sbTok = new StringBuilder();
            i = parseToken(cs,i,sbTok);
            if (!"-NONE-".equals(sbCat.toString())) {
                tokList.add(sbTok.toString());
                tagList.add(sbCat.toString());
            }
            ++i;
        }        
        return i;
    }

    // returns index one past where it found end of token
    int parseToken(char[] cs, int i, StringBuilder sb) {
        // System.out.println("parseToken(" + i + ")");
        while (cs[i] != ' ' && cs[i] != '\n' && cs[i] != '(' && cs[i] != ')') {
            sb.append(cs[i]);
            ++i;
        }
        // System.out.println("token=" + sb);
        return i;
    }

    // returns index of final right paren after seq of trees
    int parseTrees(char[] cs, int i, List<String> tokList, List<String> tagList) {
        // System.out.println("parseTrees(" + i + ")");
        while (i < cs.length && cs[i] != ')') {
            if (cs[i] == '(') 
                i = parseTree(cs,i,tokList,tagList);
            else
                ++i;
        }
        return i + 1;
    }

}