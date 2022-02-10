package com.whatswater.sql.alias;


import java.util.Set;

public class AliasFactory {
    private int symbolVal = 1;
    private Set<String> namedAlias;

    public String getNextAlias() {
        int h = symbolVal;
        StringBuilder sv = new StringBuilder(1);
        while(h != 0) {
            int t = h - 1;
            sv.append((char)((t % 26) + 97));
            h = t / 26;
        }
        this.symbolVal++;
        return sv.toString();
    }

    public void addNamedAlias(String alias) {
        namedAlias.add(alias);
    }
}
