package com.spaghettic0der;

import java.util.ArrayList;

public class Round
{
    private ArrayList<Roll> rollArrayList;

    public Round()
    {
        rollArrayList = new ArrayList<>();
    }

    public ArrayList<Roll> getRollArrayList()
    {
        return rollArrayList;
    }

    public void addToRound(Roll roll)
    {
        rollArrayList.add(roll);
    }

    public void clearRollArrayList()
    {
        rollArrayList.clear();
    }

    public Roll getLastRoll()
    {
        return rollArrayList.get(rollArrayList.size() - 1);
    }
}
