package org.gs.game.gostop.play;

import org.gs.game.gostop.TableCardPoint;
//import org.gs.game.gostop.action.GameAction;

public interface IPlayHandler
{
    void pickCard();
    
    void onPostActive();
    
    //GameAction getSelectTableCardAction(TableCardPoint flipTcp);
    
    void decideGo();
    
    void decideNine();
    
    void decideGoOnFourCards();
}
