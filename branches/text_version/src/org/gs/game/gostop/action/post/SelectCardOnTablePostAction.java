package org.gs.game.gostop.action.post;

import org.gs.game.gostop.action.GameAction;
import org.gs.game.gostop.action.TakeActionGroup;

public class SelectCardOnTablePostAction implements IGamePostAction
{
    private TakeActionGroup nextAction;
    
    public SelectCardOnTablePostAction()
    {
        nextAction = null;
    }
    
    public void setNextAction(TakeActionGroup nextAction)
    {
        this.nextAction = nextAction;
    }
    
    public void onActionComplete(GameAction ga)
    {
        if (nextAction != null)
            nextAction.setSelectedFlip(Boolean.TRUE.equals(ga.getResult()) ? 0 : 1);
    }
}
