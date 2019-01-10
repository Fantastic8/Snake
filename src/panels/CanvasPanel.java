package panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import snake.Head;
import tools.Global;

public class CanvasPanel extends JPanel {
	private LinkedList<Head> Snakes;// all snake's heads
	private LinkedList<Point> Beans;// all beans' position
	
	private BufferedImage SnakeHead;
	private BufferedImage SnakeBody;
	
	/**************************************************************************************************************
	 *                                             Constructor                                                    *
	 **************************************************************************************************************/
	public CanvasPanel(LinkedList<Head> Snakes,LinkedList<Point> Beans)
	{
		this.Snakes=Snakes;
		this.Beans=Beans;
		try {
			SnakeHead=ImageIO.read(CanvasPanel.class.getResource("../UI/SnakeHead.png"));
			SnakeBody=ImageIO.read(CanvasPanel.class.getResource("../UI/SnakeBody.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**************************************************************************************************************
	 *                                                Paint                                                       *
	 **************************************************************************************************************/
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		//draw snakes
		try
		{
			for(int i=0;i<Global.Snakes.size();i++)
			{
				//draw body
				for(int j=0;j<Snakes.get(i).getBody().size();j++)
				{
					if(Snakes.get(i).getBody().get(j).isVisible())
					{
						g.drawImage(SnakeBody,(int)Snakes.get(i).getBody().get(j).getX()-Global.SnakeRadius, (int)Snakes.get(i).getBody().get(j).getY()-Global.SnakeRadius, Global.SnakeRadius*2, Global.SnakeRadius*2,this);
					}
				}
				//draw head
				g.drawImage(rotateImage(SnakeHead,(int)(90-Snakes.get(i).getAngle())), (int)Snakes.get(i).getX()-Global.SnakeRadius, (int)Snakes.get(i).getY()-Global.SnakeRadius, Global.SnakeRadius*2, Global.SnakeRadius*2, this);
				//draw name
				g.setFont(new Font("Leelawadee",Font.PLAIN,20));
				g.drawString(Snakes.get(i).getName(), (int)Snakes.get(i).getX()-Global.SnakeRadius-5, (int)Snakes.get(i).getY()-Global.SnakeRadius-5);
				
				//draw inputer
				//drawInputer(g,i);
				//g.fillOval(x-2, y-2, 4, 4);
				
			}
		}
		catch(Exception e)
		{
			//do nothing
		}
		//draw beans
		g.setColor(Color.ORANGE);
		for(int i=0;i<Beans.size();i++)
		{
			g.fillOval(Beans.get(i).x-Global.BeanRadius, Beans.get(i).y-Global.BeanRadius, Global.BeanRadius*2, Global.BeanRadius*2);
		}
		
		
		repaint();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**************************************************************************************************************
	 *                                             Draw Inputer                                                   *
	 **************************************************************************************************************/	
	public void drawInputer(Graphics g,int indexofsnake)
	{
		//draw detector
		//g.drawOval((int)(Snakes.get(indexofsnake).getX()-Global.InputerUnit*Global.InputerBridgeNumber/2), (int)(Snakes.get(indexofsnake).getY()-Global.InputerUnit*Global.InputerBridgeNumber/2), (int)(Global.InputerUnit*Global.InputerBridgeNumber), (int)(Global.InputerUnit*Global.InputerBridgeNumber));
		
		int x=(int)(Snakes.get(indexofsnake).getX()+Global.InputerUnit*(Global.InputerBridgeNumber-1)/2*Math.sqrt(2)*Math.cos(Math.toRadians(45+Snakes.get(indexofsnake).getAngle())));
		int y=(int)(Snakes.get(indexofsnake).getY()-Global.InputerUnit*(Global.InputerBridgeNumber-1)/2*Math.sqrt(2)*Math.sin(Math.toRadians(45+Snakes.get(indexofsnake).getAngle())));
		int tempx,tempy;
		for(int l=0;l<Global.InputerBridgeNumber;l++)
		{
			for(int j=0;j<Global.InputerBridgeNumber;j++)
			{
				tempx=(int)(x+Global.InputerUnit*(j*Math.cos(Math.toRadians(90-Snakes.get(indexofsnake).getAngle()))+l*Math.sin(Math.toRadians(-90+Snakes.get(indexofsnake).getAngle()))));
				tempy=(int)(y+Global.InputerUnit*(j*Math.sin(Math.toRadians(90-Snakes.get(indexofsnake).getAngle()))+l*Math.cos(Math.toRadians(-90+Snakes.get(indexofsnake).getAngle()))));
				if(Snakes.get(indexofsnake).getInputer().getInput()[l][j]>0)
				{
					g.setColor(Color.PINK);
					g.fillOval(tempx-Global.InputerUnit/2, tempy-Global.InputerUnit/2, Global.InputerUnit, Global.InputerUnit);
				}
				else if(Snakes.get(indexofsnake).getInputer().getInput()[l][j]==0)
				{
					g.setColor(Color.PINK);
					g.drawOval(tempx-Global.InputerUnit/2, tempy-Global.InputerUnit/2, Global.InputerUnit, Global.InputerUnit);
				}
				else
				{
					g.setColor(Color.BLACK);
					g.fillOval(tempx-Global.InputerUnit/2, tempy-Global.InputerUnit/2, Global.InputerUnit, Global.InputerUnit);
				}
			}
		}
	}
	/**************************************************************************************************************
	 *                                             Rotate Image                                                   *
	 **************************************************************************************************************/
	public static BufferedImage rotateImage(final BufferedImage bufferedimage,
            final int degree) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }
}
