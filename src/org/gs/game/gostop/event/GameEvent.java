package org.gs.game.gostop.event;

import org.gs.game.gostop.GamePlayer;
import org.gs.game.gostop.item.CardItem;

public class GameEvent
{
    private Object sourceObject;
    private GameEventType eventType;
    private GameEventResult result;
    
    public GameEvent(Object sourceObject, GameEventType eventType)
    {
        this(sourceObject, eventType, (GameEventResult)null);
    }
    
    public GameEvent(Object sourceObject, GameEventType eventType, boolean result)
    {
        this(sourceObject, eventType, new GameEventResult(result));
    }
    
    public GameEvent(Object sourceObject, GameEventType eventType, Object resultObject)
    {
        this(sourceObject, eventType, new GameEventResult(resultObject));
    }
    
    public GameEvent(Object sourceObject, GameEventType eventType, GameEventResult result)
    {
        this.sourceObject = sourceObject;
        this.eventType = eventType;
        this.result = result;
    }
    
    public Object getSourceObject()
    {
        return sourceObject;
    }
    
    public GameEventType getEventType()
    {
        return eventType;
    }
    
    public boolean getBoolResult()
    {
        return result == null ? false : result.getBoolResult(); 
    }
    
    public String getStringResult()
    {
        return getEventResult(String.class);
    }
    
    public CardItem getCardItemResult()
    {
        return getEventResult(CardItem.class);
    }
    
    public GamePlayer getGamePlayerResult()
    {
        return getEventResult(GamePlayer.class);
    }
    
    public <T> T getEventResult(Class<T> tClass)
    {
        return result == null ? null : result.getResultObject(tClass);
    }
    
    public int getIntResult()
    {
        return result == null ? -1 : result.getIntResult();
    }
}
