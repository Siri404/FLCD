package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private List<Production> productions;
    private String temporaryStartingSymbol;
    private String startingSymbol;

    public Grammar() {
        nonTerminals = new HashSet<>();
        terminals = new HashSet<>();
        productions = new ArrayList<>();
        readGrammarFromFile();

        //enrich the grammar
        temporaryStartingSymbol = startingSymbol + "'";
        nonTerminals.add(temporaryStartingSymbol);
        Map<List<String>, Integer> map = new HashMap<>();
        map.put(Collections.singletonList(temporaryStartingSymbol.substring(0, temporaryStartingSymbol.length() - 1)), 0);
        productions.add(new Production(temporaryStartingSymbol, map));
    }

    public void readGrammarFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("resources/grammar.txt"));
            nonTerminals.addAll(Arrays.asList(lines.get(0).split(" ")));
            startingSymbol = lines.get(1);
            terminals.addAll(Arrays.asList(lines.get(2).split(" ")));

            //remove startingSymbol, nonTerminals and terminals lines
            lines.remove(2);
            lines.remove(1);
            lines.remove(0);

            //parse productions
            lines.forEach(line -> {
                String[] sides = line.split("->");
                Map<List<String>, Integer> map = new HashMap<>();
                map.put(Arrays.asList(sides[1].split(" ")), 0);
                Production production = new Production(sides[0].strip(), map);
                productions.add(production);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(Set<String> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(Set<String> terminals) {
        this.terminals = terminals;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public void setProductions(List<Production> productions) {
        this.productions = productions;
    }

    public String getTemporaryStartingSymbol() {
        return temporaryStartingSymbol;
    }

    public void setTemporaryStartingSymbol(String temporaryStartingSymbol) {
        this.temporaryStartingSymbol = temporaryStartingSymbol;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public void setStartingSymbol(String startingSymbol) {
        this.startingSymbol = startingSymbol;
    }
}
