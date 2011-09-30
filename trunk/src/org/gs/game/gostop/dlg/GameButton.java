package org.gs.game.gostop.dlg;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Translucent game button
 */
public class GameButton extends JButton implements FocusListener
{
    private static final long serialVersionUID = -7237764222472370253L;

    private float alpha;
    
    public GameButton(String text)
    {
        this(text, null);
    }
    
    public GameButton(Icon icon)
    {
        this(null, icon);
    }
    
    public GameButton(String text, Icon icon)
    {
        super(text, icon);
        
        setOpaque(false);
        this.alpha = GameDialog.DEFAULT_ALPHA;
        addFocusListener(this);
    }

    public void paint(Graphics g)
    {
        BufferedImage image = (BufferedImage)createImage(getWidth(), getHeight());

        Graphics2D ig2 = image.createGraphics();
        ig2.setClip(g.getClip());
        super.paint(ig2);
        ig2.dispose();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g2.drawImage(image, 0, 0, null);
    }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
        repaint();
    }
    
    public void focusGained(FocusEvent e)
    {
        if (e.getComponent() == this || e.getOppositeComponent() == this)
            repaint();
    }
    
    public void focusLost(FocusEvent e)
    {
        if (e.getComponent() == this || e.getOppositeComponent() == this)
            repaint();
    }
}
