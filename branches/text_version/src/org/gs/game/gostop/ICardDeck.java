package org.gs.game.gostop;

import org.gs.game.gostop.item.CardItem;

public interface ICardDeck
{
    CardItem getTopDeckCard(boolean remove);
    
    void setCanClickTopCard(boolean canClick);
}
