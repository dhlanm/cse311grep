import java.util.*;
import java.io.*;

public class Grep {
    public static void main(String[] args)
	throws FileNotFoundException, 
	       MalformedGrammarException,
	       ExceptionInInitializerError,
           Exception {
        if (args.length != 1) {
            System.out.println("Usage: java Grep <regex>");
            System.exit(1);
        }

        String input = args[0];

        /* Build the CFG */ 
        CFG cfg = new CFG("regex-grammar.cfg");
        EarleyParse.setGrammar(cfg);

        /* Parse The Input String */
        ASTNode parsed = null;
        try {
            parsed = EarleyParse.parse(input);
        } catch (NullPointerException e) {
            System.out.println("Your CFG failed to parse the input " + input + "!!");
            System.exit(1);
        }
            
        System.out.println("Yay!  We parsed the input correctly!");

        NFA N = makeNFAFromRegex(parsed);
    }

    public static NFA makeNFAFromRegex(ASTNode n) {
        return null;
    }
}
