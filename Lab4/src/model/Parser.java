package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private Grammar grammar;
    private List<Pair<String, Map<String, Integer>>> parseTable;
    private String derivationsString;

    public Parser() {
        grammar = new Grammar();
        parseTable = new ArrayList<>();
        try {
            createParsingTable();
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    private List<Production> closure(Production production) {
        List<Production> closure = new ArrayList<>();
        closure.add(production);
        boolean notDone = true;
        while (notDone) {
            notDone = false;
            for (int i = 0; i < closure.size(); i++) {
                Production p = closure.get(i);
                Map.Entry<List<String>, Integer> entry = p.getRhs().entrySet().iterator().next();
                int pointIndex = entry.getValue();
                if (pointIndex != entry.getKey().size()) {
                    String elem = entry.getKey().get(pointIndex);
                    for (Production prod : grammar.getProductions()) {
                        if (elem.equals(prod.getLhs())) {
                            //add the point at the beginning

                            Map<List<String>, Integer> map = new HashMap<>();
                            map.put(entry.getKey(), 0);
                            Production newProd = new Production(prod.getLhs(), map);
                            entry.setValue(0);
                            if (!closure.contains(newProd)) {
                                closure.add(newProd);
                                notDone = true;
                            }
                        }
                    }
                }
            }
        }
        return closure;
    }

    private List<Production> goTo(List<Production> s, String element) throws ParserException {
        for (Production production : s) {
            Map.Entry<List<String>, Integer> entry = production.getRhs().entrySet().iterator().next();
            Integer pointIndex = entry.getValue();
            if (entry.getKey().indexOf(element) == pointIndex) {
                //move the point
                entry.setValue(entry.getValue() + 1);
                return closure(production);
            }
        }
        throw new ParserException("Invalid element passed to toGo function!");
    }

    private void createParsingTable() throws ParserException {
        //colcan
        List<List<Production>> states = new ArrayList<>();
        //initialize s0
        Production initialProd = grammar.getProductions().get(grammar.getProductions().size() - 1);
        List<Production> s0 = closure(initialProd);
        states.add(s0);
        boolean notDone = true;
        while (notDone) {
            notDone = false;
            for (int i = 0; i < states.size(); i++) {
                List<Production> state = states.get(i);
                //add table row for new state
                if (parseTable.size() <= i) {
                    Pair<String, Map<String, Integer>> newPair = new Pair<>();
                    Map<String, Integer> goToRow = new HashMap<>();
                    newPair.setKey("");
                    newPair.setValue(goToRow);
                    parseTable.add(newPair);
                }
                //goto with nonterminals
                for (String nonTerminal : grammar.getNonTerminals()) {
                    try {
                        List<Production> newState = goTo(state, nonTerminal);
                        if (!states.contains(newState)) {
                            states.add(newState);
                            //set goToRow value
                            parseTable.get(i).getValue().put(nonTerminal, states.size() - 1);
                            notDone = true;
                        } else {
                            //set goToRow value
                            int index = states.indexOf(newState);
                            parseTable.get(i).getValue().put(nonTerminal, index);
                        }
                    } catch (ParserException ignored) {
                    }
                }
                //goto with terminals
                for (String terminal : grammar.getTerminals()) {
                    try {
                        List<Production> newState = goTo(state, terminal);
                        if (!states.contains(newState)) {
                            states.add(newState);
                            //set goToRow value
                            parseTable.get(i).getValue().put(terminal, states.size() - 1);
                            notDone = true;
                        } else {
                            //set goToRow value
                            int index = states.indexOf(newState);
                            parseTable.get(i).getValue().put(terminal, index);
                        }
                    } catch (ParserException ignored) {
                    }
                }
            }
        }
        setActions(states);
    }

    private void setActions(List<List<Production>> states) throws ParserException {
        for (int i = 0; i < states.size(); i++) {
            List<Production> state = states.get(i);
            String action = "";
            for (Production production : state) {
                Map.Entry<List<String>, Integer> entry = production.getRhs().entrySet().iterator().next();
                int pointIndex = entry.getValue();
                //point before terminal/nonterminal
                if (pointIndex != entry.getKey().size() - 1) {
                    //check for conflict
                    if (!action.equals("") && !action.equals("shift")) {
                        throw new ParserException("shift conflict!");
                    }
                    action = "shift";
                }
                //point at the end
                else {
                    //check accept
                    if (production.getLhs().equals(grammar.getTemporaryStartingSymbol())) {
                        //check for conflict
                        if (!action.equals("")) {
                            throw new ParserException("accept conflict!");
                        }
                        action = "accept";
                    }
                    //reduce
                    else {
                        //find production number for reduce
                        for (int j = 0; j < grammar.getProductions().size() - 1; j++) {
                            if (entry.getKey().equals(grammar.getProductions().get(j).getRhs())
                                    && production.getLhs().equals(grammar.getProductions().get(j).getLhs())) {
                                //check for conflict
                                if (!action.equals("") && !action.equals("reduce" + (j + 1))) {
                                    throw new ParserException("reduce conflict!");
                                }
                                action = "reduce" + (j + 1);
                            }
                        }
                    }
                }
                //check for error
                if (action.equals("")) {
                    throw new ParserException("Parse table error!");
                }

            }
            parseTable.get(i).setKey(action);
        }
    }

    public String parse(String sequence) throws ParserException {
        String inputStack = sequence + "$";
        String workingStack = "$0";
        StringBuilder output = new StringBuilder();
        output.append(workingStack);
        int index = 0;
        while (true) {
            String currentState = workingStack.substring(workingStack.length() - 1);
            String action = parseTable.get(Integer.parseInt(currentState)).getKey();
            if (action.equals("shift")) {
                Integer nextState = parseTable.get(Integer.parseInt(currentState)).getValue().get(String.valueOf(inputStack.charAt(index)));
                if (nextState == null) {
                    throw new ParserException("Sequence rejected");
                }
                workingStack += String.valueOf(inputStack.charAt(index)) + nextState;
                inputStack = inputStack.substring(1);
                output.append(" => ").append(workingStack);
            } else if (action.contains("reduce")) {
                Production production = grammar.getProductions().get(Integer.parseInt(String.valueOf(action.charAt(action.length() - 1))) - 1);
                int tokensToRemove = production.getRhs().keySet().iterator().next().size() * 2;
                workingStack = workingStack.substring(0, workingStack.length() - tokensToRemove);
                if (workingStack.charAt(workingStack.length() - 1) == '$') {
                    throw new ParserException("Sequence rejected");
                }
                currentState = workingStack.substring(workingStack.length() - 1);
                Integer nextState = parseTable.get(Integer.parseInt(currentState)).getValue().get(production.getLhs());
                workingStack += production.getLhs() + nextState;
                output.append(" => ").append(workingStack);
            } else {
                return output.toString();
            }
        }


    }

    public List<Pair<String, Map<String, Integer>>> getParseTable() {
        return parseTable;
    }

    public void setParseTable(List<Pair<String, Map<String, Integer>>> parseTable) {
        this.parseTable = parseTable;
    }
}
