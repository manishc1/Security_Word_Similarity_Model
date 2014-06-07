package demo.pos;
import com.aliasi.corpus.TagHandler;

import com.aliasi.io.FileExtensionFilter;

import java.io.File;

public class TrainTreebankPos {

    public static void main(String[] args) throws Exception {
        TagHandler handler = new TagHandler() {
                public void handle(String[] toks, String[] whites, String[] tags) {
                    for (int i = 0; i < toks.length; ++i)
                        System.out.println(toks[i] + "|" + tags[i] + " ");
                    System.out.println("--------------------------------------");
                }
            };
        PennTreebankMrgTagParser parser = new PennTreebankMrgTagParser();
        parser.setHandler(handler);

        File tb3PosDir = new File(args[0]);
        System.out.println("tb dir=" + tb3PosDir);
        File wsjDir = new File(tb3PosDir,"wsj");
        System.out.println("wsjDir=" + wsjDir);

        for (File dir : wsjDir.listFiles()) {
            for (File file : dir.listFiles(new FileExtensionFilter(".mrg"))) {
                System.out.println("\nFILE=" + file);
                parser.parse(file);
            }
        }
    }

}