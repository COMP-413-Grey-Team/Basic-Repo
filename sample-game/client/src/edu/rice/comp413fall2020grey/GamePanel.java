package edu.rice.comp413fall2020grey;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements Runnable {

  public static final int BILLION = 1000000000;

  public static final double GAME_HERTZ = 60.0;
  public static final double TIME_BEFORE_UPDATE = BILLION / GAME_HERTZ;
  public static final int MUST_UPDATE_BEFORE_RENDER = 5;
  public static final double TARGET_FPS = 60;
  public static final double TOTAL_TIME_BEFORE_RENDER = BILLION / TARGET_FPS;

  public int width;
  public int height;

  private Thread thread;
  private boolean running = false;

  private BufferedImage img;
  private Graphics2D g;

  public GamePanel(final int width, final int height) {
    this.width = width;
    this.height = height;

    setPreferredSize(new Dimension(width, height));
    setFocusable(true);
    requestFocus();
  }

  @Override
  public void addNotify() {
    super.addNotify();
    if (thread == null) {
      thread = new Thread(this, "GameThread");
      thread.start();
    }
  }

  public void init() {
    running = true;

    img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g = (Graphics2D) img.getGraphics();
  }

  @Override
  public void run() {
    init();

    double lastUpdateTime = System.nanoTime();
    double lastRenderTime;

    int frameCount = 0;
    int lastSecondTime = (int) (lastUpdateTime / BILLION);
    int oldFrameCount = 0;

    while (running) {
      double now = System.nanoTime();
      int updateCount = 0;
      while(((now - lastUpdateTime) > TIME_BEFORE_UPDATE) && (updateCount < MUST_UPDATE_BEFORE_RENDER)) {
        update();
        input();
        lastUpdateTime += TIME_BEFORE_UPDATE;
        updateCount++;
      }
      if (now - lastUpdateTime > TIME_BEFORE_UPDATE) {
        lastUpdateTime = now - TIME_BEFORE_UPDATE;
      }
      input();
      render();
      draw();
      lastRenderTime = now;
      frameCount++;

      int thisSecond = (int) (lastUpdateTime / BILLION);
      if (thisSecond > lastSecondTime) {
        if (frameCount != oldFrameCount) {
//          System.out.println(frameCount);
          oldFrameCount = frameCount;
        }
        frameCount = 0;
        lastSecondTime = thisSecond;
      }
      while (now - lastRenderTime < TOTAL_TIME_BEFORE_RENDER && now - lastUpdateTime < TIME_BEFORE_UPDATE) {
        Thread.yield();
        try {
          Thread.sleep(1);
        } catch(Exception e) {
          System.out.println("ERROR: yielding thread");
        }
        now = System.nanoTime();
      }
    }
  }

  private void update() {

  }

  public void input() {

  }

  private void render() {
    if (g != null) {
      g.setColor(new Color(66, 134, 244));
      g.fillRect(0, 0, width, height);
    }
  }

  private void draw() {
    Graphics g2 = this.getGraphics();
    g2.drawImage(img, 0, 0, width, height, null);
    g2.dispose();
  }

}
