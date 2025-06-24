package src;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.*;

public class JPanelImage extends JPanel {
    private final Image image;
    private float opacity = 1f; // 1 = totalmente visible, 0 = totalmente transparente

    public JPanelImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        this.image = icon.getImage();
        setLayout(new OverlayLayout(this));
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0f, Math.min(opacity, 1f));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}

