package org.gs.game.gostop.action;

import org.gs.game.gostop.GamePlayer;
import org.gs.game.gostop.GameTable;
import org.gs.game.gostop.TableCardPoint;
import org.gs.game.gostop.action.post.SelectCardOnTablePostAction;
import org.gs.game.gostop.action.pre.PlaySoundPreAction;
import org.gs.game.gostop.item.CardItem;
import org.gs.game.gostop.sound.GameSoundManager;

public class FlipDeckActionGroup extends GameActionGroup
{
    private GameTable gameTable;
    private TableCardPoint layTcp;
    private int selected;
    
    public FlipDeckActionGroup(GamePlayer player, int actionUnit,
                               GameTable gameTable, TableCardPoint layTcp, int selected)
    {
        super(player, actionUnit);

        this.gameTable = gameTable;
        this.layTcp = layTcp;
        this.selected = selected;
    }

    public void execute()
    {
        GamePlayer gamePlayer = getGamePlayer();
        CardItem nextCard = gameTable.getTopDeckCard(true);
        GameAction cur = new ShowCardAction(nextCard, actionUnit);      // show
        
        firstAction = lastAction = cur;
        
        while (nextCard.isBonusCard())
        {
            if (layTcp != null && layTcp.getCardCount(true) > 1)
                cur = new MoveAction(nextCard, actionUnit, layTcp);     // move-to-table
            else
                cur = new MoveAction(nextCard, actionUnit, gamePlayer); // take
            cur.setPreExecuteAction(new PlaySoundPreAction(GameSoundManager.SOUND_MOVE));
            lastAction.setNextAction(cur);
            lastAction = cur;
            
            nextCard = gameTable.getTopDeckCard(true);
            cur = new ShowCardAction(nextCard, actionUnit);             // show
            lastAction.setNextAction(cur);
            lastAction = cur;
        }
        
        TableCardPoint flipTcp = gameTable.getTableCardPoint(nextCard.getMajorCode(), true);
        
        cur = new MoveAction(nextCard, actionUnit, flipTcp);        // move-to-table
        String soundId = flipTcp.getCardCount(false) == 0 ? GameSoundManager.SOUND_PUT_ON_EMPTY
                                                            : GameSoundManager.SOUND_PUT_ON_CARD;
        cur.setPreExecuteAction(new PlaySoundPreAction(soundId));
        lastAction.setNextAction(cur);
        lastAction = cur;
        
        // choose a table card to take if necessary
        SelectCardOnTablePostAction scotpa = null;
        
        if (flipTcp != layTcp && flipTcp.needToQueryForTaking())
        {
            cur = gamePlayer.getSelectTableCardAction(flipTcp);
            scotpa = new SelectCardOnTablePostAction();
            cur.addGamePostAction(scotpa);
            lastAction.setNextAction(cur);
            lastAction = cur;
        }
        
        cur = new TakeActionGroup(gamePlayer, actionUnit, gameTable, layTcp, selected,
                                  flipTcp, -1);
        if (scotpa != null)
            scotpa.setNextAction((TakeActionGroup)cur);
        lastAction.setNextAction(cur);
        lastAction = cur;
    }
}
