package org.gs.game.gostop;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.gs.game.gostop.action.*;
import org.gs.game.gostop.action.post.AddMissionBonusPostAction;
import org.gs.game.gostop.action.post.IGamePostAction;
import org.gs.game.gostop.config.GameMission;
import org.gs.game.gostop.config.GameType;
import org.gs.game.gostop.config.GameUser;
import org.gs.game.gostop.config.WinType;
import org.gs.game.gostop.config.GameMission.MissionType;
import org.gs.game.gostop.dlg.GameQueryDlg;
import org.gs.game.gostop.dlg.GameResultDlg;
import org.gs.game.gostop.event.GameEventType;
import org.gs.game.gostop.item.AlertItem;
import org.gs.game.gostop.item.CardItem;
import org.gs.game.gostop.sound.GameSoundManager;

public class GameManager implements ICardDeck
{
    public static enum PlayStep { PICK_LEAD, INIT_GAME, PLAYING }
    private static final int FOUR_CARD_WIN_POINTS = 7;
    
    private GamePanel gamePanel;
    private PlayStep playStep;
    private List<GamePlayer> gamePlayers;
    private GamePlayer userPlayer;
    private List<Integer> cardDeck;
    private int curPlayer;
    
    public GameManager(GamePanel gamePanel)
    {
        this.gamePanel = gamePanel;
        playStep = null;
        resetGamePlayers();
    }
    
    protected void resetGamePlayers()
    {
        gamePlayers = new ArrayList<GamePlayer>();
    }
    
    protected void addGamePlayer(GamePlayer gamePlayer)
    {
        gamePlayers.add(gamePlayer);
        
        if (gamePlayer.isOpposite() == false)
            userPlayer = gamePlayer;
        
        getGameTable().addPlayer(gamePlayer);
    }

    protected void resetFreeCardPoints(Point[] points, int maxOverlapWidth)
    {
        getGameTable().resetTableCardPoints(points, maxOverlapWidth);
    }
    
    protected void addTableCardForPickLead(CardItem cardItem)
    {
        cardItem.setFlipped(false);
        cardItem.setCanClick(true);
        
        getGameTable().getTableCardPoint(cardItem.getMajorCode()).addCardItem(cardItem);
    }
    
    public void setPlayStep(PlayStep playStep)
    {
        this.playStep = playStep;
    }
    
    protected PlayStep getPlayStep()
    {
        return playStep;
    }
    
    public void onCardClicked(CardItem cardItem)
    {
        if (PlayStep.PICK_LEAD.equals(getPlayStep()))
            onPickedForLead(cardItem);
        else if (PlayStep.PLAYING.equals(getPlayStep()))
            onPickedForPlay(cardItem);
    }
    
    /**
     * Called when all the players picked cards to choose the lead player
     */
    public void onPickLeadComplete()
    {
        int leadMajor = 13;
        
        gamePanel.getMissionItem().setMessage(null, null);
        
    	curPlayer = 0;
    	
        for (int i = 0; i < gamePlayers.size(); i++)
        {
            GamePlayer player = gamePlayers.get(i);
            if (player.getSelectedCard().getMajorCode() < leadMajor)
            {
            	curPlayer = i;
                leadMajor = player.getSelectedCard().getMajorCode();
            }
        }
        
        GamePlayer leadPlayer = gamePlayers.get(curPlayer);
        ShowAlertAction saa = new ShowAlertAction(leadPlayer, 20, AlertItem.ALERT_LEAD,
                                                  GameEventType.ALERT_COMPLETED);
        
        setLeadPlayer(leadPlayer);
        
        gamePanel.getActionManager().addItem(saa);
    }
    
    /**
     * Called when the alert box disappears
     */
    public void onAlertComplete()
    {
        if (playStep == PlayStep.PICK_LEAD)
            onNewGame(null);
    }
    
    public void onNewGame(GamePlayer leadPlayer)
    {
        if (leadPlayer != null)
            setLeadPlayer(leadPlayer);

        GameSoundManager.playSound(GameSoundManager.SOUND_PLAY, null);
        
        setPlayStep(PlayStep.INIT_GAME);
        gamePanel.getMissionItem().setMission(null);
        getGameTable().clearTableCards();
        stackCards();
    }
    
    /**
     * Called when all the cards are stacked on the table
     */
    public void onStackComplete()
    {
        GameType gameType = getGameTable().getGameType();
        int[] tableCards = gameType.getTableCards();
        int[] playerCards = gameType.getPlayerCards();
        DealAction firstAction = null;
        DealAction lastAction = null;
        
        for (int i = 0; i < tableCards.length; i++)
        {
            // deal cards on the table
            for (int k = 0; k < tableCards[i]; k++)
            {
            	CardItem cardItem = getTopDeckCard(true);
            	DealAction action = new DealAction(cardItem, 3, getGameTable(), k == 0);
            	
            	if (firstAction == null)
            	    firstAction = action;
            	if (lastAction != null)
            	    lastAction.setNextAction(action, k == 0 ? 3 : 1);
            	lastAction = action;
            }
            
            // deal cards for the players
            for (int p = 0; p < gamePlayers.size(); p++)
            {
                GamePlayer gamePlayer = getNextPlayer();
                
                for (int k = 0; k < playerCards[i]; k++)
                {
                    CardItem cardItem = getTopDeckCard(true);
                    DealAction action = new DealAction(cardItem, 3, gamePlayer, k==0);

                    lastAction.setNextAction(action, k == 0 ? 3: 1);
                    lastAction = action;
                }
            }
        }
        
        lastAction.setCompleteEventType(GameEventType.DEAL_COMPLETED);
        
        gamePanel.getActionManager().addItem(firstAction);
    }

    public void onDealComplete()
    {
        sleep(100);
        
        userPlayer.sortHoldCards();
        
        Point ptFourCards = getGameTable().getTableFourCardPoint();
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        
        // If four cards of a same type are dealt on the table,
        // the lead player wins the game
        if (ptFourCards != null)
        {
            int winPoints = FOUR_CARD_WIN_POINTS;
            ShowAlertAction saa = new ShowAlertAction(gamePlayer, 20,
                                                      AlertItem.ALERT_4CARDS,
                                                      ptFourCards,
                                                      GameEventType.ALERT_COMPLETED);
            GameResultDlg resultDlg = new GameResultDlg(gamePanel, gamePlayer, gamePlayers, winPoints);
            ShowDialogAction sda = new ShowDialogAction(resultDlg, 0, false);
            
            saa.setNextAction(sda);
            gamePanel.getActionManager().addItem(saa);
        }
        else
        {
            int i = 0;
            
            while (i < gamePlayers.size() && gamePlayer.checkFourCards() == false)
            {
                gamePlayer = getNextPlayer();
                i++;
            }
            
            if (i >= gamePlayers.size())
                startPlay();
        }
    }
    
    public void onFourCardsDecided(boolean go)
    {
        if (go)
        {
            GamePlayer gamePlayer = getNextPlayer();
            
            while (gamePlayer.isLeadPlayer() == false && gamePlayer.checkFourCards() == false)
                gamePlayer = getNextPlayer();
            
            if (gamePlayer.isLeadPlayer())
                startPlay();
        }
        else
        {
            int winPoints = FOUR_CARD_WIN_POINTS;
            GamePlayer gamePlayer = gamePlayers.get(curPlayer);
            GameResultDlg resultDlg = new GameResultDlg(gamePanel, gamePlayer, gamePlayers, winPoints);
            ShowDialogAction sda = new ShowDialogAction(resultDlg, 0, false);

            gamePanel.getActionManager().addItem(sda);
        }
    }
    
    public void onFlipStackCompleted()
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
     
        setPlayStep(PlayStep.PLAYING);
        gamePanel.getMissionItem().setMission(GameMission.UnknownMission);

        getGameTable().addCurTurn();
        gamePlayer.play();
    }
    
    public void onNineDecided(boolean useAsLeaf)
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        gamePlayer.getPlayerStatus().setNineDecided(useAsLeaf);
        
        int missionBonus;
        if ((missionBonus = getGameTable().checkNewMission(gamePlayer)) > 0)
        {
            Point pt = gamePlayer.getTakenRect(CardClass.TEN).getLocation();
            
            if (gamePlayer.isOpposite())
                pt.x = gamePlayer.getAlertLocation().x;
            else
                pt.y = gamePlayer.getAlertLocation().y;
            
            GameAction ga = new ShowAlertAction(gamePlayer, 15,
                                                AlertItem.ALERT_MISSION, pt,
                                                GameEventType.PLAY_COMPLETED);  // show-alert
            IGamePostAction ambpa = new AddMissionBonusPostAction(gamePlayer,
                                                                  getGameTable(),
                                                                  missionBonus);
            ga.addGamePostAction(ambpa);
            gamePanel.getActionManager().addItem(ga);
        }
        else
            onPlayCompleted();
    }
    
    public void onPlayCompleted()
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        boolean continuePlay = true;
        
        if (gamePlayer.getKeepTurn())
            gamePlayer.calculatePoints(false);
        else if (gamePlayer.checkNineDecide())
            continuePlay = false;
        else
        {
            continuePlay = gamePlayer.calculatePoints(true);
            
            if (continuePlay)
            {
                gamePlayer.onPostActive();
                
                if (getGameTable().isFirstTurn()
                    && gamePlayer.getGameUser().getMoney() < GameUser.getRefillMoney())
                    refillMoney(gamePlayer);
                
                gamePlayer = getNextPlayer();
                
                if (gamePlayer.isLeadPlayer())
                {
                    if (getGameTable().getCurTurn() == 2)
                        setMission();
                    
                    if (getGameTable().isGameOver())
                    {
                        continuePlay = false;
                        onDrawGame();
                    }
                }
            }
        }
        
        if (continuePlay)
            gamePlayer.play();
    }
    
    public void onGoDecided(boolean go, int winPoints)
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        
        if (go)
        {
            gamePlayer.onPostActive();
            getGameTable().addGoPlayer(gamePlayer);
            
            gamePlayer = getNextPlayer();
            if (gamePlayer.isLeadPlayer() && getGameTable().getCurTurn() == 2)
                setMission();
            gamePlayer.play();
        }
        else    // stop
        {
            GameResultDlg resultDlg = new GameResultDlg(gamePanel, gamePlayer,
                                                        gamePlayers, winPoints);
            ShowDialogAction sda = new ShowDialogAction(resultDlg, 0, false);
            
            gamePanel.getActionManager().addItem(sda);
        }
    }
    
    public CardItem getTopDeckCard(boolean remove)
    {
        int index = -1;
        
        if (cardDeck.size() > 0)
        {
            if (remove)
                index = cardDeck.remove(cardDeck.size()-1);
            else
                index = cardDeck.get(cardDeck.size()-1);
        }
        
        return index < 0 ? null : gamePanel.getCardItems().get(index);
    }
    
    public void setCanClickTopCard(boolean canClick)
    {
        int index = cardDeck.size() > 0 ? cardDeck.get(cardDeck.size()-1) : -1;
        
        if (index >= 0)
            gamePanel.getCardItems().get(index).setCanClick(canClick);
    }
    
    /**
     * Called when the user picked a card to choose the lead player
     * 
     * @param cardItem the card item picked by the user
     */
    private void onPickedForLead(CardItem cardItem)
    {
        GameSoundManager.stopAllSound();
        getGameTable().disableTableCardsClick();
        cardItem.setFlipped(true);
        
        PickTableCardAction pickAction = null;
        PickTableCardAction lastAction = null;
        
        for (GamePlayer player: gamePlayers)
        {
            if (player.isOpposite())
            {
                // Create actions to pick cards for opponent players   
                PickTableCardAction ptca = new PickTableCardAction(player, 5, getGameTable());
                if (pickAction == null)
                    pickAction = ptca;
                if (lastAction != null)
                    lastAction.setNextAction(ptca);
                lastAction = ptca;
            }
            else
                player.setSelectedCard(cardItem);
        }
        
        gamePanel.getActionManager().addItem(pickAction);
    }
    
    private GameTable getGameTable()
    {
        return gamePanel.getGameTable();
    }
    
    private void startPlay()
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        
        // Take the bonus cards from the table for the lead (current) player
        List<CardItem> bonusCards = getGameTable().getTableBonusCards();
        if (bonusCards != null)
        {
            TakeBonusCardsActionGroup fsa =
                new TakeBonusCardsActionGroup(gamePlayer, 3, getGameTable(), bonusCards);
            
            fsa.setCompleteEventType(GameEventType.FLIP_STACK_COMPLETED);
            gamePanel.getActionManager().addItem(fsa);
        }
        else
            onFlipStackCompleted();
    }
    
    private void onPickedForPlay(CardItem cardItem)
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        GameAction ga = null;
        
        if (gamePlayer.isOpposite())
        {
            // TODO: need to consider the remote players
            // If the player is a computer player, other cases are already handled
        }
        // check for triple/quadruple
        else if (cardItem.isBonusCard() == false
                 && CardItem.Status.BOMB.equals(cardItem.getActiveStatus()) == false
                 && gamePlayer.getHoldCardCount(cardItem.getMajorCode()) >= 3)
        {
            GameQueryDlg queryDlg = new GameQueryDlg(gamePanel,
                                                     GameQueryDlg.SWING_CARDS,
                                                     GameEventType.SWING_DECIDED,
                                                     cardItem);
            ga = new ShowDialogAction(queryDlg, GamePanel.TIME_UNITS_PER_SECOND*10,
                                      false, true);
        }
        else
        {
            TableCardPoint tcp = getGameTable().getTableCardPoint(cardItem.getMajorCode(), false);
            
            // Check if table has 2 different type cards with the same major code...
            if (tcp != null && tcp.needToQueryForTaking()
                && gamePlayer.isHoldingCard(cardItem.getCardCode()))
            {
                GameQueryDlg queryDlg = new GameQueryDlg(gamePanel,
                                                         GameQueryDlg.SELECT_CARD,
                                                         GameEventType.SELECTED_CARD_ON_TABLE,
                                                         false, cardItem,
                                                         tcp.getCardItems());
                ga = new ShowDialogAction(queryDlg, GamePanel.TIME_UNITS_PER_SECOND*10,
                                          false, true);
            }
        }

        gamePlayer.disableCardClick();
        
        if (ga == null)
            ga = new PlayActionGroup(gamePlayer, 3, cardItem, getGameTable());
        
        gamePanel.getActionManager().addItem(ga);
    }
    
    public void onSwingDecided(boolean swing, CardItem cardItem)
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        GameAction ga = null;
        
        if (swing)
        {
            gamePlayer.addTripleCount();
            
            Point pointAlert = gamePlayer.getAlertLocation();
            AlertItem alertItem = gamePanel.getAlertItem();
            ShowAlertAction saa = new ShowAlertAction(gamePlayer, 20, null,
                                                      GameEventType.ALERT_COMPLETED);
            
            alertItem.setSwingCards(AlertItem.ALERT_SWING,
                                    gamePlayer.getHoldCards(cardItem.getMajorCode(), false),
                                    pointAlert);
            
            ga = saa;
        }

        PlayActionGroup pa = new PlayActionGroup(gamePlayer, 3, cardItem, getGameTable());
        
        if (ga == null)
            ga = pa;
        else
            ga.setNextAction(pa);
        
        gamePanel.getActionManager().addItem(ga);
    }
    
    public void onSelectedCardOnTable(CardItem cardItem, int selected)
    {
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        
        PlayActionGroup pa = new PlayActionGroup(gamePlayer, 3, cardItem, selected, getGameTable());
        
        gamePanel.getActionManager().addItem(pa);
    }
    
    private void stackCards()
    {
        gamePanel.setCardStack(generateCardStack(), GameEventType.STACK_COMPLETED);
    }
    
    private void setMission()
    {
        GameMission mission = GameMission.getRandomMission();
        gamePanel.getMissionItem().setMission(mission);
        
        if (mission.getMissionType() == MissionType.AND
            || mission.getMissionType() == MissionType.OR)
        {
            List<CardItem> cardItems = gamePanel.getCardItems();
            
            for (CardItem cardItem: cardItems)
            {
                if (mission.isMissionCard(cardItem.getCardCode()))
                    cardItem.setMission(true);
            }
        }
        
        if (mission != GameMission.NoMission)
            GameSoundManager.playSound(GameSoundManager.SOUND_MISSION, null);
    }
    
    private List<Integer> generateCardStack()
    {
        List<CardItem> cardItems = gamePanel.getCardItems();
        int cardCount = cardItems.size();
        
        for (CardItem cardItem: cardItems)
        {
            cardItem.setActiveStatus(CardItem.Status.NONE);
            cardItem.setMission(false);
            cardItem.setCanClick(false);
        }
        
        gamePanel.showShuffle(true, 20);
        
        cardDeck = new ArrayList<Integer>();
        for (int i = 0; i < cardCount; i++)
            cardDeck.add(i);
            
        do
        {
            for (int i = 0; i < cardCount; i++)
            {
                int temp = cardDeck.get(i);
                int second = Main.getRandom().nextInt(cardCount);
                
                cardDeck.set(i, cardDeck.get(second));
                cardDeck.set(second, temp);
            }
            
            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        } while(gamePanel.getActionManager().isComplete() == false);

        return cardDeck;
    }
    
    private void onDrawGame()
    {
        // show game result
        GameQueryDlg queryDlg = new GameQueryDlg(gamePanel,
                                                 GameQueryDlg.QUERY_MORE_GAME_ON_DRAW,
                                                 GameEventType.MORE_GAME_DECIDED,
                                                 gamePlayers.get(curPlayer));
        ShowDialogAction sda = new ShowDialogAction(queryDlg, 0, false);
        
        gamePanel.getActionManager().addItem(sda);
        
        // Save the draw information
        List<GameUser> gameUsers = new ArrayList<GameUser>();
        
        for (GamePlayer player: gamePlayers)
        {
            player.getGameUser().updateMoney(0, false, WinType.DRAW);
            gameUsers.add(player.getGameUser());
        }
        
        GameUser.updateGameUsers(gameUsers);
    }
    
    private void refillMoney(GamePlayer gamePlayer)
    {
        GameUser gameUser = gamePlayer.getGameUser();
        gameUser.updateMoney(GameUser.getRefillMoney(), true, WinType.NONE);
        ShowAlertAction saa = new ShowAlertAction(gamePlayer, 15, AlertItem.ALERT_REFILL);
        gamePanel.getActionManager().addItem(saa);
    }
    
    private GamePlayer getNextPlayer()
    {
        curPlayer = (curPlayer+1) % gamePlayers.size();
        GamePlayer gamePlayer = gamePlayers.get(curPlayer);
        
        if (playStep == PlayStep.PLAYING && gamePlayer.isLeadPlayer())
            getGameTable().addCurTurn();
        
        return gamePlayer;
    }
    
    private void setLeadPlayer(GamePlayer leadPlayer)
    {
        for (GamePlayer gp: gamePlayers)
            gp.initPlayer(gp == leadPlayer);
    }
    
    private void sleep(int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
