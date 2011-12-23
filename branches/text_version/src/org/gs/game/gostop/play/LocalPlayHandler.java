package org.gs.game.gostop.play;

import org.gs.game.gostop.TextGamePanel;
import org.gs.game.gostop.GamePlayer;
import org.gs.game.gostop.GameTable;
import org.gs.game.gostop.TableCardPoint;
//import org.gs.game.gostop.action.GameAction;
//import org.gs.game.gostop.action.ShowDialogAction;
//import org.gs.game.gostop.action.ShowGoAction;
//import org.gs.game.gostop.action.post.QueryGoPostAction;
import org.gs.game.gostop.dlg.GameQueryDlg;
import org.gs.game.gostop.event.GameEventType;
import org.gs.game.gostop.item.CardItem;

public class LocalPlayHandler// implements IPlayHandler
{
    private GamePlayer gamePlayer;
    private GameTable gameTable;
    
    protected LocalPlayHandler(GamePlayer gamePlayer, GameTable gameTable)
    {
        this.gamePlayer = gamePlayer;
        this.gameTable = gameTable;
    }
    
    public void pickCard()
    {
        TableCardPoint tcp;
        
        for (CardItem cardItem: gamePlayer.getHoldCards())
        {
            // enable holding cards to be clicked
            cardItem.setCanClick(true);
            
            // show card active status: first/safe, bomb
            tcp = gameTable.getTableCardPoint(cardItem.getMajorCode(), false);
            if (tcp == null)
            {
                if (cardItem.isBonusCard() == false
                    && gameTable.isCardTaken(cardItem.getMajorCode(), true)
                    && gamePlayer.getHoldCardCount(cardItem.getMajorCode()) == 2)
                    cardItem.setActiveStatus(CardItem.Status.SAFE);
            }
            else
            {
                CardItem.Status cas;
                
                if (gameTable.isCardTaken(cardItem.getMajorCode(), true)
                    || tcp.getCardCount(true) == 3)
                    cas = CardItem.Status.SAFE;
                else if (gamePlayer.getHoldCardCount(cardItem.getMajorCode()) >= 3)
                    cas = CardItem.Status.BOMB;
                else
                    cas = CardItem.Status.FIRST;
                
                cardItem.setActiveStatus(cas);
            }
        }
        
        if (gamePlayer.getFlipCount() > 0)
            gameTable.setCanClickTopCard(true);
    }

    public void onPostActive()
    {
        gamePlayer.sortHoldCards();
        gameTable.setCanClickTopCard(false);
    }

/*
    public GameAction getSelectTableCardAction(TableCardPoint flipTcp)
    {
        GameQueryDlg queryDlg = new GameQueryDlg(null,
                                                 GameQueryDlg.SELECT_CARD,
                                                 null,
                                                 flipTcp.getCardItems());
        return new ShowDialogAction(queryDlg, TextGamePanel.TIME_UNITS_PER_SECOND*10,
                                    false, false);
    }
*/
    
    public void decideGo()
    {
        TextGamePanel gamePanel = gamePlayer.getGamePanel();
/*
        GameQueryDlg queryDlg = new GameQueryDlg(gamePanel, GameQueryDlg.QUERY_GO,
                                                 null,
                                                 gameTable.getCurrentWinMoney(gamePlayer));
        ShowDialogAction sda = new ShowDialogAction(queryDlg,
                                                    TextGamePanel.TIME_UNITS_PER_SECOND*10,
                                                    false, true);
        
        sda.setCompleteEventType(GameEventType.GO_DECIDED);
        sda.addGamePostAction(new QueryGoPostAction(gamePlayer));
        
        gamePanel.getActionManager().addItem(sda);
*/
    }

    public void decideNine()
    {
        TextGamePanel gamePanel = gamePlayer.getGamePanel();
/*
        GameQueryDlg queryDlg = new GameQueryDlg(gamePanel,
                                                 GameQueryDlg.QUERY_NINE,
                                                 GameEventType.NINE_DECIDED,
                                                 false);
        ShowDialogAction sda = new ShowDialogAction(queryDlg,
                                                    TextGamePanel.TIME_UNITS_PER_SECOND*10,
                                                    false);
        
        gamePanel.getActionManager().addItem(sda);*/
    }

    public void decideGoOnFourCards()
    {
        TextGamePanel gamePanel = gamePlayer.getGamePanel();
/*
        GameQueryDlg queryDlg = new GameQueryDlg(gamePanel,
                                                 GameQueryDlg.QUERY_FOUR_CARD,
                                                 GameEventType.FOUR_CARDS_DECIDED,
                                                 false);
        ShowDialogAction sda = new ShowDialogAction(queryDlg,
                                                    TextGamePanel.TIME_UNITS_PER_SECOND*10,
                                                    false, true);
        ShowGoAction sga = new ShowGoAction(gamePlayer, 20, null, true);
        
        sda.setNextAction(sga);
        gamePanel.getActionManager().addItem(sda);*/
    }
}
