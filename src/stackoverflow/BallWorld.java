package stackoverflow;




import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;
import javax.swing.JPanel;




/**
 * The control logic and main display panel for game.
 */
public class BallWorld extends JPanel {
   private static final int UPDATE_RATE = 30;  // Frames per second (fps)
   
   private Ball green;         // A single bouncing Ball's instance
   private Ball blue;         // A single bouncing Ball's instance
   private Ball puck;         // A single bouncing Ball's instance
   private ContainerBox box;  // The container rectangular box
  
   private DrawCanvas canvas; // Custom canvas for drawing the box/ball
   private int canvasWidth;
   private int canvasHeight;
  
   /**
    * Constructor to create the UI components and init the game objects.
    * Set the drawing canvas to fill the screen (given its width and height).
    * 
    * @param width : screen width
    * @param height : screen height
    */
   public BallWorld(int width, int height) {
  
      canvasWidth = width;
      canvasHeight = height;
      
      // Init the ball at a random location (inside the box) and moveAngle
      Random rand = new Random();
      int radius = 200;
      int x = rand.nextInt(canvasWidth - radius * 2 - 20) + radius + 10;
      int y = rand.nextInt(canvasHeight - radius * 2 - 20) + radius + 10;
      int speed = 5;
      int angleInDegree = rand.nextInt(360);
      green = new Ball(x, y, radius/3, speed, angleInDegree, Color.GREEN);
      blue = new Ball(x, y, radius/5, speed, angleInDegree*2, Color.BLUE);
      puck = new Ball(x, y, radius/7, speed+5, angleInDegree, Color.RED);
     

      
      // Init the Container Box to fill the screen
      box = new ContainerBox(0, 0, canvasWidth, canvasHeight, Color.BLACK, Color.WHITE);
     
      // Init the custom drawing panel for drawing the game
      canvas = new DrawCanvas();
      this.setLayout(new BorderLayout());
      this.add(canvas, BorderLayout.CENTER);
      
      // Handling window resize.
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            Component c = (Component)e.getSource();
            Dimension dim = c.getSize();
            canvasWidth = dim.width;
            canvasHeight = dim.height;
            // Adjust the bounds of the container to fill the window
            box.set(0, 0, canvasWidth, canvasHeight);
         }
      });
  
      // Start the ball bouncing
      gameStart();
   }
   
   /** Start the ball bouncing. */
   public void gameStart() {
      // Run the game logic in its own thread.
      Thread gameThread = new Thread() {
         public void run() {
            while (true) {
               // Execute one time-step for the game 
               gameUpdate();
               // Refresh the display
               repaint();
               // Delay and give other thread a chance
               try {
                  Thread.sleep(1000 / UPDATE_RATE);
               } catch (InterruptedException ex) {}
            }
         }
      };
      gameThread.start();  // Invoke GaemThread.run()
   }
   
   /** 
    * One game time-step. 
    * Update the game objects, with proper collision detection and response.
    */
   public void gameUpdate() {
      green.moveOneStepWithCollisionDetection(box);
      blue.moveOneStepWithCollisionDetection(box);
      puck.moveOneStepWithCollisionDetection(box);
   }
   
   /** The custom drawing panel for the bouncing ball (inner class). */
   class DrawCanvas extends JPanel {
      /** Custom drawing codes */
      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);    // Paint background
         // Draw the box and the ball
         box.draw(g);
         green.draw(g);
         blue.draw(g);
         puck.draw(g);
         // Display ball's information
         g.setColor(Color.WHITE);
         g.setFont(new Font("Courier New", Font.PLAIN, 12));
         g.drawString("green " + green.toString(), 20, 30);
         g.drawString("blue " + blue.toString(), 20, 30);
         g.drawString("puck " + puck.toString(), 20, 30);
      }
  
      /** Called back to get the preferred size of the component. */
      @Override
      public Dimension getPreferredSize() {
         return (new Dimension(canvasWidth, canvasHeight));
      }
   }
}
