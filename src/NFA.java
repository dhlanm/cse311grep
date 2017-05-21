import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

public class NFA {
    protected Set<FSMState> states;
    private FSMState startState;
    private Set<FSMState> finalStates;
    private Set<FSMTransition> transitions;
    private static final char EPSILON = '-';
    private static final char TAB = 9;

    /* Main Constructor */
    public NFA(Set<FSMState> states, FSMState startState, Set<FSMState> finalStates, Set<FSMTransition> transitions) {
        if (!states.contains(startState)) {
            throw new IllegalArgumentException("The start state must be in the NFA.");
        }

        if (!states.containsAll(finalStates)) {
        	throw new IllegalArgumentException("The final states must be in the NFA.");
        }
        
        for (FSMTransition t : transitions) {
        	if (!states.contains(t.getSource()) || !states.contains(t.getDestination())) {
        		throw new IllegalArgumentException("The transitions may only use states in the NFA.");
        	}
        }

        this.states = states;
        this.startState = startState;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    /* Copy Constructor */
    public NFA(NFA n) {
        this(new HashSet<FSMState>(n.states), n.startState, new HashSet<FSMState>(n.finalStates), new HashSet<FSMTransition>(n.transitions));
    }

    /* @returns a new NFA that accepts any single character. */
    public static NFA dot() {
        HashSet<FSMState> states = new HashSet<FSMState>();
		HashSet<FSMState> finalStates = new HashSet<FSMState>();
        FSMState startState = new FSMState();
        FSMState acceptState = new FSMState();

        states.add(startState);
        states.add(acceptState);

        finalStates.add(acceptState);

		HashSet<FSMTransition> transitions = new HashSet<FSMTransition>();

        for (char c=' '; c <= '~'; c++) {
            transitions.add(new FSMTransition(c, startState, acceptState));
        }
        transitions.add(new FSMTransition(TAB, startState, acceptState));

        return new NFA(states, startState, finalStates, transitions);
    }


    /* @returns true if and only if the NFA accepts s. */
    public boolean read(String s) {
        /* TODO: You need to implement this! */
		HashSet<FSMState> onStates = new HashSet<FSMState>();
		onStates.add(this.startState);
		HashMap<String, ArrayList<FSMTransition>> exit = new HashMap<String, ArrayList<FSMTransition>>();
		for (FSMTransition t : this.transitions) {
			String k = t.getSource().toString();
			if(exit.containsKey(k)) {
				ArrayList<FSMTransition> tmp = exit.get(k);
				tmp.add(t);
				exit.put(k, tmp);
			}
			else {
				ArrayList<FSMTransition> tmp = new ArrayList<FSMTransition>();
				tmp.add(t);
				exit.put(k, tmp);
			}
        }
		
		for (char ch: s.toCharArray()) {
			HashSet<FSMState> newOnStates = new HashSet<FSMState>();
			for (FSMState state : onStates) {
				if(exit.containsKey(state.toString())) {
					for(FSMTransition t : exit.get(state.toString())) {
						if(t.getCharacter() == ch) {
							newOnStates.add(t.getDestination());
						}
					}
				}
			}
			onStates = new HashSet<FSMState>(newOnStates);
		}
		for (FSMState state : onStates) {
			if(finalStates.contains(state))
				return true;
		}
        return false;
    }

    /* @returns an NFA which accepts the union of a and b */
    public static NFA union(NFA a, NFA b) {
        /* TODO: You need to implement this! */
        return null;
    }

    /* @returns an NFA which accepts the concat of a and b */
    public static NFA concat(NFA a, NFA b) {
        /* TODO: You need to implement this! */
        return null;
    }

    /* @returns an NFA which accepts the Kleene star of a */
    public static NFA star(NFA n) {
        /* TODO: You need to implement this! */
        return null;
    }

    /* @returns an NFA which is equivalent to n (including all transitions in epsilonTransition) that
     *          does not contain any epsilon transitions */
    public static NFA epsilonClosure(NFA n, Set<FSMTransition> epsilonTransitions) {
        /* TODO: You need to implement this! */
        return null;
    }
}
