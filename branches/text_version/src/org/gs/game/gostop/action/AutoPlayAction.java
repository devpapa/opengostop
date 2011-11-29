package org.gs.game.gostop.action;

import org.gs.game.gostop.TableCardPoint;
import org.gs.game.gostop.play.AutoPlayHandler;

public class AutoPlayAction extends GameAction
{
    private TableCardPoint flipTcp;
    
    public AutoPlayAction(AutoPlayHandler playHandler, TableCardPoint flipTcp)
    {
        super(playHandler, 0);
        
        this.flipTcp = flipTcp;
    }

    public boolean execute(float progress)
    {
        selectTableCard();
        
        return true;
    }
    
    private void selectTableCard()
    {
        AutoPlayHandler playHandler = (AutoPlayHandler)target;
        
        setResult(playHandler.selectTableCard(flipTcp) == 0);
    }
}
