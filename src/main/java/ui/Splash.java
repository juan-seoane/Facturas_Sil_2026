package ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JWindow;

import modelo.base._Ruta;

public class Splash extends JWindow implements Runnable {

    private BufferedImage splash = null;
    private BufferedImage image = null;

    public Splash() {
        //System.out.println("Entrando en el constructor del splash");
        InputStream in;
        int i = (int) (Math.floor(Math.random() * 4 ) + 1);

        try {
            in = getClass().getResourceAsStream(_Ruta.IMG.getRuta() + "/splash" + i + ".jpg");

            if (in == null) {
                System.out.println("No se encontró la imagen splash" + i + ".jpg");
            } else {
                image = ImageIO.read(in);
            }
        } catch (IOException ex) {
            System.out.println("Error " + ex + " al cargar la imagen de inicio");
        }

        createShadowPicture(image);
    }

    @Override
    public void paint(Graphics g) {
        //System.out.println("Entrando en el paint del splash");
        if (splash != null) {
            g.drawImage(splash, 0, 0, null);
        }
    }

    private void createShadowPicture(BufferedImage image) {
        //System.out.println("Creando Shadow Picture del splash");
        int width = image.getWidth();
        int height = image.getHeight();

        int extra = 0;

        setSize(new Dimension(width + extra, height + extra));
        setLocationRelativeTo(null);
        Rectangle windowRect = getBounds();

        splash = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) splash.getGraphics();

        try {
            Robot robot = new Robot(getGraphicsConfiguration().getDevice());
            BufferedImage capture = robot.createScreenCapture(
                new Rectangle(windowRect.x, windowRect.y, windowRect.width + extra, windowRect.height + extra)
            );
            g2.drawImage(capture, null, 0, 0);
        } catch (AWTException e) {}

        BufferedImage shadow = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics g = shadow.getGraphics();
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.8f));
        g.fillRoundRect(6, 6, width, height, 12, 12);

        g2.drawImage(image, 0, 0, this);
    }

    public static void main(String[] args) {
        /*    Timer timer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {

                }
            });
            timer.start(); */
    }

    @Override
    public void run() {
        setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Splash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
