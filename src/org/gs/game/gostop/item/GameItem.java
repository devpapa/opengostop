package org.gs.game.gostop.item;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.gs.game.gostop.event.GameEvent;
import org.gs.game.gostop.event.GameEventManager;
import org.gs.game.gostop.event.GameEventType;

public abstract class GameItem
{
    protected static final int X_MARGIN = 5;
    protected static final int Y_MARGIN = 3;
    protected static final Color borderColor = Color.LIGHT_GRAY;
    protected static final Color activeBorder = Color.ORANGE;
    
    protected JComponent parent;
    protected Rectangle rect;
    protected boolean canClick;
    protected boolean mouseOver;
    
    protected static enum TextAlign { LEFT, CENTER, RIGHT }
    
    public GameItem(JComponent parent, Rectangle rect)
    {
        this.parent = parent;
        this.rect = new Rectangle(rect);
        canClick = false;
        mouseOver = false;
    }
    
    public abstract void paintItem(Graphics g);
    public abstract int getZOrder();
    
    public void repaint()
    {
        if (rect != null && rect.isEmpty() == false)
            parent.repaint(rect);
    }
    
    public void setRect(Rectangle rect)
    {
        Rectangle prevrect = this.rect;
        
        this.rect = new Rectangle(rect);
        
        if (prevrect.isEmpty() == false)
            parent.repaint(prevrect);
        
        repaint();
    }
    
    public void moveItem(Point pos)
    {
        if (rect.getLocation().equals(pos) == false)
        {
            Rectangle toRect = new Rectangle(rect);
            
            toRect.setLocation(pos);
            setRect(toRect);
        }
    }
    
    public Rectangle getRect()
    {
        return rect;
    }
    
    public void setCanClick(boolean canClick)
    {
        this.canClick = canClick;
        mouseOver = canClick && mouseOver;
    }
    
    public boolean canClick()
    {
        return canClick;
    }
    
    protected void fillBackground(Graphics g)
    {
        fillRect(g, rect, borderColor, 0.2f);
    }
    
    protected void fillRect(Graphics g, Rectangle rFill, Color color, float alpha)
    {
        g.setColor(color);
        ((Graphics2D)g).setComposite(AlphaComposite.SrcOver.derive(alpha));
        ((Graphics2D)g).fill(rFill);
        g.setPaintMode();  // restore the composite mode
    }

    protected int drawString(Graphics g, String text, int top)
    {
        return drawString(g, text, X_MARGIN, top, TextAlign.LEFT);
    }
    
    protected int drawString(Graphics g, String text, int xmargin, int top, TextAlign align)
    {
        int drawnChars = text.length();
        FontMetrics fm = g.getFontMetrics();
        int yoff = fm.getLeading()/2 + fm.getMaxAscent();
        char[] textChars = text.toCharArray();
        int clientWidth = (int)rect.getWidth() - xmargin * 2;
        int charsWidth;
        
        do
        {
            charsWidth = fm.charsWidth(textChars, 0, drawnChars);
            if (charsWidth > clientWidth)
                drawnChars--;
        } while (charsWidth > clientWidth && drawnChars > 0);
    
        int xoff = rect.x + xmargin;
        if (drawnChars == text.length() && align != TextAlign.LEFT)
        {
            if (align == TextAlign.RIGHT)
                xoff = rect.x + rect.width - xmargin - charsWidth;
            else
                xoff += (rect.width - charsWidth) / 2;
        }
        
        g.drawChars(textChars, 0, drawnChars, xoff, rect.y + top + yoff);
        
        return drawnChars;
    }
    
    protected int getTextLineHeight(Graphics g)
    {
        FontMetrics fm = g.getFontMetrics();
        
        return fm.getHeight();
    }
    
    public void mouseClicked(MouseEvent e)
    {
        fireItemEvent(new GameEvent(this, GameEventType.ITEM_CLICKED));
    }
    
    public void mouseMoved(MouseEvent e)
    {
        boolean oldOver = mouseOver;
        
        mouseOver = canClick;
        
        if (canClick && oldOver != mouseOver)
            repaint();
    }

    public void mouseExited()
    {
        mouseOver = false;
        
        if (canClick)
            repaint();
    }

    protected void drawBorder(Graphics g, Color color)
    {
        g.setColor(color);
        g.drawRect(rect.x, rect.y, rect.width-1, rect.height-1);
    }
    
//    protected void drawBorder(Graphics g)
//    {
//        g.setColor(borderColor);
//        ((Graphics2D)g).setComposite(AlphaComposite.SrcOver.derive(0.4f));
//        g.drawRect(rect.x, rect.y, rect.width-1, rect.height-1);
//        g.setPaintMode();  // restore the composite mode
//    }
    
    protected void fireZOrderChanged(boolean synchronous)
    {
        fireItemEvent(new GameEvent(this, GameEventType.ZORDER_CHANGED), synchronous);
    }
    
    protected void fireItemEvent(GameEvent e)
    {
        fireItemEvent(e, false);
    }

    protected void fireItemEvent(GameEvent e, boolean synchronous)
    {
        GameEventManager.fireGameEvent(e, synchronous);
    }
}
