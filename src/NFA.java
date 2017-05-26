import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

public class NFA {
    protected Set<FSMState> states;
    private FSMState startState;
    private Set<FSMState> finalStates;
    public Set<FSMTransition> transitions;
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
		FSMState newStart = new FSMState();
		HashSet<FSMTransition> epsilonTransitions = new HashSet<FSMTransition>();
		epsilonTransitions.add(new FSMTransition('e', newStart, a.startState));
		epsilonTransitions.add(new FSMTransition('e', newStart, b.startState));
		HashSet<FSMState> newStates = new HashSet<FSMState>();
		newStates.addAll(a.states);
		newStates.addAll(b.states);
		newStates.add(newStart);
		HashSet<FSMState> newFinal = new HashSet<FSMState>();
		newFinal.addAll(a.finalStates);
		newFinal.addAll(b.finalStates);
		HashSet<FSMTransition> newT = new HashSet<FSMTransition>();
		newT.addAll(a.transitions);
		newT.addAll(b.transitions);
        NFA newN = new NFA(newStates, newStart, newFinal, newT);
        return epsilonClosure(newN, epsilonTransitions);
    }

    /* @returns an NFA which accepts the concat of a and b */
    public static NFA concat(NFA a, NFA b) {
		HashSet<FSMTransition> epsilonTransitions = new HashSet<FSMTransition>();
		for(FSMState s : a.finalStates) {
			epsilonTransitions.add(new FSMTransition('e', s, b.startState));
		}
        HashSet<FSMState> newStates = new HashSet<FSMState>();
		newStates.addAll(a.states);
		newStates.addAll(b.states);
		HashSet<FSMTransition> newT = new HashSet<FSMTransition>();
		newT.addAll(a.transitions);
		newT.addAll(b.transitions);
		NFA newN = new NFA(newStates, a.startState, b.finalStates, newT);
        return epsilonClosure(newN, epsilonTransitions);
    }

    /* @returns an NFA which accepts the Kleene star of a */
    public static NFA star(NFA n) {
		HashSet<FSMTransition> epsilonTransitions = new HashSet<FSMTransition>();
		
		FSMState newStart = new FSMState();
		HashSet<FSMState> newStates = new HashSet<FSMState>();
		for(FSMState s : n.finalStates) {
			epsilonTransitions.add(new FSMTransition('e', s, n.startState));
		}
		epsilonTransitions.add(new FSMTransition('e', newStart, n.startState));

		newStates.addAll(n.states);
		newStates.add(newStart);
		HashSet<FSMState> newFinal = new HashSet<FSMState>();
		newFinal.addAll(n.finalStates);
		newFinal.add(newStart);;
		NFA newN = new NFA(newStates, newStart, newFinal, n.transitions);
        return epsilonClosure(newN, epsilonTransitions);
    }

    /* @returns an NFA which is equivalent to n (including all transitions in epsilonTransition) that
     *          does not contain any epsilon transitions */
    public static NFA epsilonClosure(NFA n, Set<FSMTransition> epsilonTransitions) {
        HashMap<String, ArrayList<FSMTransition>> exit = new HashMap<String, ArrayList<FSMTransition>>();
		for (FSMTransition t : epsilonTransitions) {
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
		//first step
		for(FSMState state : n.states) {
			if(exit.containsKey(state.toString())) {
				boolean going = true;
				String curr = state.toString();
				ArrayList<String> todos = new ArrayList<String>();
				HashSet<String> visited = new HashSet<String>();
				visited.add(state.toString());
				while(going) {
					going = false;
					for(FSMTransition t : exit.get(curr)) {
						if(exit.containsKey(t.getDestination())) {
							todos.add(t.getDestination().toString());
							if(!exit.containsKey(t.getDestination().toString())) {
								FSMTransition tmp = new FSMTransition('e', state, t.getDestination());
								epsilonTransitions.add(tmp);
								ArrayList<FSMTransition> tmpar = new ArrayList<FSMTransition>();
								tmpar.add(tmp);
								exit.put(state.toString(),tmpar);
								todos.add(t.getDestination().toString());
							}
						}
					}
					for(String s : todos){
						if(!visited.contains(s)) {
							going = true;
							visited.add(s);
						}
						else todos.remove(s);
					}
				}
			}
		}
		//System.out.println(epsilonTransitions);
		//System.out.println("--");
		//second step
		HashSet<FSMTransition> newT = new HashSet<FSMTransition>();
		HashMap<String, ArrayList<FSMTransition>> morexit = new HashMap<String, ArrayList<FSMTransition>>();
		for (FSMTransition t : n.transitions) {
			//if(!epsilonTransitions.contains(t)) {
				newT.add(t);
				String k = t.getSource().toString();
				if(morexit.containsKey(k)) {
					ArrayList<FSMTransition> tmp = morexit.get(k);
					tmp.add(t);
					morexit.put(k, tmp);
				}
				else {
					ArrayList<FSMTransition> tmp = new ArrayList<FSMTransition>();
					tmp.add(t);
					morexit.put(k, tmp);
				}
			//}
		}
		
		
		HashSet<FSMState> newFinal = new HashSet<FSMState>(n.finalStates);
		//System.out.println(newFinal);
		for (FSMTransition t : epsilonTransitions) {
			if(morexit.containsKey(t.getDestination().toString())) {
				for(FSMTransition good : morexit.get(t.getDestination().toString())) {
					//System.out.println(good.getCharacter());
					newT.add(new FSMTransition(good.getCharacter(), t.getSource(), good.getDestination()));
					//System.out.println(newT);
					if(newFinal.contains(t.getDestination())) {
						newFinal.add(t.getSource());
						//System.out.println(t);
					}
				}
			}
		}
		//System.out.println(newFinal+" _----");
		
		
		//remove unreachable
		boolean changed = true;
		HashSet<FSMState> newS = new HashSet<FSMState>(n.states);
		//System.out.println(newS);
		while(changed) {
			HashSet<FSMState> tmp = new HashSet<FSMState>();
			for(FSMState s : newS) {
				for(FSMTransition t : newT) {
					if(t.getDestination().toString().equals(s.toString()) || t.equals(n.startState))
						tmp.add(s);
				}
			}
			if(newS.equals(tmp)) changed = false;
			newS = new HashSet<FSMState>(tmp);
		}
		newS.add(n.startState);
		HashSet<FSMTransition> realNewT = new HashSet<FSMTransition>();
		for(FSMTransition t : newT) {
			if(newS.contains(t.getSource()) && newS.contains(t.getDestination()))
				realNewT.add(t);
				
		}
		
		//System.out.println(newFinal);
		HashSet<FSMState> realNewFinal = new HashSet<FSMState>();
		for(FSMState s : newFinal) {
			for(FSMTransition t : realNewT) {
				if(t.getSource().toString().equals(s.toString()) || t.getDestination().toString().equals(s.toString()))
					realNewFinal.add(s);
			}
		}
        return new NFA(newS, n.startState, realNewFinal, realNewT);
    }
}
