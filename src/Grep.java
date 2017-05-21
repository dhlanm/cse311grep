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
		return makeNFAFromRegex(n, "def");
	}
	
    public static NFA makeNFAFromRegex(ASTNode n, String from) {
		NFA r;
		//.out.println(n);
		//System.out.println(from);
		//try{System.out.println(n.hasOneChild());}catch(Exception e){System.out.println("noc");}
		//System.out.println("---");
		if(n.isTerminal()) {
			switch(n.getValue()){
				case "'": 
					FSMState ss = new FSMState();
					HashSet<FSMState> s = new HashSet<FSMState>();
					s.add(ss);
					r = new NFA(s, ss, new HashSet<FSMState>(), new HashSet<FSMTransition>());
					break;
				case "-":
					FSMState ss2 = new FSMState();
					HashSet<FSMState> s2 = new HashSet<FSMState>();
					s2.add(ss2);
					HashSet<FSMState> f2 = new HashSet<FSMState>();
					f2.add(ss2);
					r = new NFA(s2, ss2, f2, new HashSet<FSMTransition>());
					break;
				default:
					FSMState ss3 = new FSMState();
					FSMState fin = new FSMState();
					HashSet<FSMState> s3 = new HashSet<FSMState>();
					s3.add(ss3);
					s3.add(fin);
					HashSet<FSMState> f3 = new HashSet<FSMState>();
					f3.add(fin);
					HashSet<FSMTransition> t3 = new HashSet<FSMTransition>();
					t3.add(new FSMTransition(n.getValue().charAt(0),ss3,fin));
					r = new NFA(s3, ss3, f3, t3);
					break;
			}
			return r;
		}
		switch(n.getRuleName()) {
			case "U":
				if(n.hasOneChild()) return makeNFAFromRegex(n.getChild(), "???");
				r = NFA.union(makeNFAFromRegex(n.getLeftChild(), "U"),makeNFAFromRegex(n.getRightChild(), "U"));		
				break;
			case "C":
				if(n.hasOneChild()) return makeNFAFromRegex(n.getChild(), "!!!");
				r = NFA.concat(makeNFAFromRegex(n.getLeftChild(), "C"),makeNFAFromRegex(n.getRightChild(), "C"));
				break;
			case "S": 
				r = NFA.star(makeNFAFromRegex(n.getChild(),"S"));
				break;
			case "P":
				r = makeNFAFromRegex(n.getChildren()[1]);
				break;
			default: 
				r = makeNFAFromRegex(n.getChild());
				break;
		}

		return r;
    }
}
