package org.gs.game.gostop.config;

import java.util.ArrayList;
import java.util.List;

public class BonusCards
{
    private static ArrayList<BonusCards> _bonusCards = null;
    
    private int trippleCount;
    private int doubleCount;
    private int kingCount;
    
    public static void addBonusCards(int trippleCount, int doubleCount, int kingCount)
    {
        if (_bonusCards == null)
            _bonusCards = new ArrayList<BonusCards>();
        
        _bonusCards.add(new BonusCards(trippleCount, doubleCount, kingCount));
    }
    
    public static List<BonusCards> getBonusCards()
    {
        return _bonusCards;
    }
    
    public BonusCards(int trippleCount, int doubleCount, int kingCount)
    {
        this.trippleCount = trippleCount;
        this.doubleCount = doubleCount;
        this.kingCount = kingCount;
    }
    
    public int getTripleCount()
    {
        return trippleCount;
    }
    
    public int getDoubleCount()
    {
        return doubleCount;
    }
    
    public int getKingCount()
    {
        return kingCount;
    }
}
