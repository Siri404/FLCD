package model;

import java.util.List;
import java.util.Map;

public class Production {
    private String lhs;
    private Map<List<String>, Integer> rhs;

    Production(String lhs, Map<List<String>, Integer> rhs){
        this.lhs = lhs;
        this.rhs = rhs;
    }


    public String getLhs() {
        return lhs;
    }

    public void setLhs(String lhs) {
        this.lhs = lhs;
    }

    public Map<List<String>, Integer> getRhs() {
        return rhs;
    }

    public void setRhs(Map<List<String>, Integer> rhs) {
        this.rhs = rhs;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Production)) {
            return false;
        }
        Production p = (Production) obj;
        return p.getRhs().equals(rhs) && p.getLhs().equals(lhs);
    }

}
