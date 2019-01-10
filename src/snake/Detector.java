package snake;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import panels.Canvas;
import tools.Global;

public class Detector implements Runnable {
	private Head head;
	/**************************************************************************************************************
	 *                                              Constructor                                                   *
	 **************************************************************************************************************/
	public Detector(Head head)
	{
		this.head=head;
	}

	/**************************************************************************************************************
	 *                                             Thread Begin                                                   *
	 **************************************************************************************************************/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Canvas canvas=head.getCanvas();
		ArrayList<Body> body=head.getBody();
		while(!head.isStop())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//check death
			//touch wall
			if(head.getX()-Global.SnakeRadius<=0||head.getY()-Global.SnakeRadius<=0||head.getX()+Global.SnakeRadius>=canvas.getWidth()||head.getY()+Global.SnakeRadius>=canvas.getHeight())
			{
				head.setStop(true);
			}
			//touch tail
			for(int j=0;j<Global.Snakes.size();j++)
			{
				for(int i=2;i<Global.Snakes.get(j).getBody().size()/10;i++)
				{
					if(distance(head.getX(),head.getY(),Global.Snakes.get(j).getBody().get(i*10-1).getX(),Global.Snakes.get(j).getBody().get(i*10-1).getY())<=Global.SnakeRadius*2)
					{
						head.setStop(true);
						break;
					}
				}
			}
			//eat beans
			for(int i=0;i<Global.Beans.size();i++)
			{
				if(distance(head.getX(),head.getY(),Global.Beans.get(i).x,Global.Beans.get(i).y)<=Global.SnakeRadius+Global.BeanRadius)
				{
					//eat event
					head.eatEvent(Global.Beans.get(i));
				}
			}
		}
	}
	
	/**************************************************************************************************************
	 *                                             Distance Calculate                                             *
	 **************************************************************************************************************/
	public double distance(double x1, double y1, double x2, double y2)
	{
		double x=x1-x2;
		double y=y1-y2;
		return Math.sqrt(x*x+y*y);
	}
}
