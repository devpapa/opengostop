package org.gs.game.gostop.action;

import org.gs.game.gostop.GamePlayer;

public abstract class GameActionGroup extends GameAction
{
    protected int actionUnit;
    protected GameAction firstAction;
    protected GameAction lastAction;
    
    public GameActionGroup(GamePlayer player, int actionUnit)
    {
        super(player, 0);
        
        this.actionUnit = actionUnit;
        firstAction = lastAction = null;
    }

    public boolean execute(float progress)
    {
        execute();
        
        if (completeEventType != null && lastAction != null)      // handle post processing
        {
            lastAction.setCompleteEventType(completeEventType);
            setCompleteEventType(null);
        }
        
        if (lastAction != null && getNextActions() != null)
            lastAction.setNextActions(getNextActions(), 0);
        
        if (firstAction != null)
            setNextAction(firstAction);
        
        return true;
    }
    
    protected abstract void execute();
    
    protected GamePlayer getGamePlayer()
    {
        return (GamePlayer)target;
    }
}
