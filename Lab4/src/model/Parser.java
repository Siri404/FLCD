package model;
import java.util.*;

public class Parser {
    private Grammar grammar;
    private List<Pair<String, Map<String, Integer>>> parseTable;
    private String parseTree;

    public Parser(){
        grammar = new Grammar();
        parseTable = new ArrayList<>();
        try{
            createParsingTable();
        }catch (ParserException e){
            e.printStackTrace();
        }
    }

    private List<Production> Closure(Production production){
        List<Production> closure = new ArrayList<>();
        closure.add(production);
        boolean notDone = true;
        while(notDone){
            notDone = false;
            for(int i=0; i<closure.size(); i++){
                Production p = closure.get(i);
                int pointIndex = p.getRhs().indexOf('.');
                if (pointIndex != p.getRhs().length() - 1) {
                    String elem = "" +  p.getRhs().charAt(pointIndex+1);
                    for (Production prod : grammar.getProductions()) {
                        if (elem.equals(prod.getLhs())) {
                            //add the point at the beginning
                            Production newProd = new Production(prod.getLhs(), "." + prod.getRhs());
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
            int pointIndex = production.getRhs().indexOf('.');
            if (production.getRhs().indexOf(element) == pointIndex + 1) {
                String newRhs = production.getRhs();

                //move the point
                newRhs = newRhs.substring(0,pointIndex) + element + "." + newRhs.substring(pointIndex + element.length() + 1);

                return Closure(new Production(production.getLhs(), newRhs));
            }
        }
        throw new ParserException("Invalid element passed to toGo function!");
    }

    private void createParsingTable() throws ParserException {
        //colcan
        List<List<Production>> states = new ArrayList<>();
        //initialize s0
        Production initialProd = grammar.getProductions().get(grammar.getProductions().size()-1);
        initialProd.setRhs("." + initialProd.getRhs());
        List<Production> s0 = Closure(initialProd);
        states.add(s0);
        boolean notDone = true;
        while (notDone){
            notDone = false;
            for(int i=0; i<states.size(); i++) {
                List<Production> state = states.get(i);
                //add table row for new state
                if(parseTable.size() <= i){
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
                        }
                        else{
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
                        }
                        else{
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
        for(int i=0; i<states.size(); i++){
            List<Production> state = states.get(i);
            String action = "";
            for (Production production : state) {
                String rhs = production.getRhs();
                int pointIndex = rhs.indexOf(".");
                //point before terminal/nonterminal
                if(pointIndex != rhs.length() - 1){
                    //check for conflict
                    if(!action.equals("") && !action.equals("shift")){
                        throw new ParserException("shift conflict!");
                    }
                    action = "shift";
                }
                //point at the end
                else{
                    rhs = rhs.substring(0, rhs.length()-1);
                    //check accept
                    if(production.getLhs().equals(grammar.getStartingSymbol())){
                        //check for conflict
                        if(!action.equals("")){
                            throw new ParserException("accept conflict!");
                        }
                        action = "accept";
                    }
                    //reduce
                    else {
                        //find production number for reduce
                        for(int j=0; j<grammar.getProductions().size()-1; j++){
                            if(rhs.equals(grammar.getProductions().get(j).getRhs())
                                    && production.getLhs().equals(grammar.getProductions().get(j).getLhs())){
                                //check for conflict
                                if(!action.equals("") && !action.equals("reduce " + (j+1))){
                                    throw new ParserException("reduce conflict!");
                                }
                                action = "reduce " + (j+1);
                            }
                        }
                    }
                }
                //check for error
                if(action.equals("")){
                    throw new ParserException("Parse table error!");
                }

            }
            parseTable.get(i).setKey(action);
        }
    }

    public List<Pair<String, Map<String, Integer>>> getParseTable() {
        return parseTable;
    }

    public void setParseTable(List<Pair<String, Map<String, Integer>>> parseTable) {
        this.parseTable = parseTable;
    }
}
