package org.gs.game.gostop.event;

import java.util.ArrayList;
import java.util.List;

public class GameEventManager extends Thread
{
    private static GameEventManager _instance = null;
    
    private List<IGameEventListener> eventListeners;
    private List<GameEvent> eventQueue;

    public static void addGameEventListener(IGameEventListener listener)
    {
        getInstance().addListener(listener);
    }

    /**
     * Fires an event from a dedicated thread
     * 
     * @param e the event to fire
     * @param synchronous true to handle the event synchronously
     */
    public static void fireGameEvent(GameEvent e, boolean synchronous)
    {
        if (synchronous)
            getInstance().handleGameEvent(e);
        else
            getInstance().addItemEvent(e);
    }
    
    private static synchronized GameEventManager getInstance()
    {
        if (_instance == null)
            _instance = new GameEventManager();
        
        return _instance;
    }
    
    private GameEventManager()
    {
        eventQueue = new ArrayList<GameEvent>(8);
        eventListeners = new ArrayList<IGameEventListener>(4);
        
        setName("ItemEventMgr");
    }
    
    private synchronized void addListener(IGameEventListener listener)
    {
        if (eventListeners.contains(listener) == false)
            eventListeners.add(listener);
    }
    
    public void run()
    {
        do
        {
            handleGameEvent(getNextGameEvent());
        } while (true);
    }
    
    private synchronized GameEvent getNextGameEvent()
        throws RuntimeException
    {
        GameEvent event = null;
        
        do
        {
            if (eventQueue.size() > 0)
                event = eventQueue.remove(0);
            else
            {
                try
                {
                    wait();
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        } while (event == null);
        
        return event;
    }
    
    private synchronized void addItemEvent(GameEvent e)
    {
        eventQueue.add(e);
        
        if (isAlive() == false)
            start();
        
        notify();
    }
    
    private void handleGameEvent(GameEvent e)
    {
        for (IGameEventListener gel: eventListeners)
            gel.onGameEvent(e);
    }
}
