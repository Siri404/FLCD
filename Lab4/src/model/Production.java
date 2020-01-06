package model;

import java.util.List;

public class Production {
    private String lhs;
    private String rhs;

    Production(String lhs, String rhs){
        this.lhs = lhs;
        this.rhs = rhs;
    }


    public String getLhs() {
        return lhs;
    }

    public void setLhs(String lhs) {
        this.lhs = lhs;
    }

    public String getRhs() {
        return rhs;
    }

    public void setRhs(String rhs) {
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
