package org.gs.game.gostop.item;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

//import org.gs.game.gostop.TextGamePanel;

public class PlayerPaneItem extends GameItem
{
    private boolean active;
    
    public PlayerPaneItem(JComponent parent, Rectangle rect)
    {
        super(parent, rect);
        
        active = false;
        
        // to register it as a child item of the game panel
        fireZOrderChanged(false);
    }

    public void paintItem(Graphics g)
    {
        fillBackground(g);
        
        if (active)
            drawBorder(g, activeBorder);
    }
    
    public int getZOrder()
    {
//	        return TextGamePanel.BACKGROUND_ZORDER;
		return 0;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
        repaint();
    }
}
