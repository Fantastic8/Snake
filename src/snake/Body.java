package snake;

import tools.Global;

public class Body {
	//data
	private int id;
	private double x;
	private double y;
	private double angle;
	private Head head;
	private boolean visible;
	
	/**************************************************************************************************************
	 *                                             Constructor                                                    *
	 **************************************************************************************************************/
	public Body(Head head, boolean visible)
	{
		this.head=head;
		this.visible=visible;
		angle=90;//vertical
		//calculate its data(id, angle, x, y)
		id=head.getBody().size();
		if(id==0)//first body
		{
			angle=head.getAngle();
			x=head.getX()-Global.SnakeSpace*Math.cos(Math.toRadians(angle));
			y=head.getY()+Global.SnakeSpace*Math.sin(Math.toRadians(angle));
		}
		else
		{
			Body lastbody=head.getBody().get(id-1);//last body
			angle=lastbody.getAngle();
			x=lastbody.getX()-Global.SnakeSpace*Math.cos(Math.toRadians(angle));
			y=lastbody.getY()+Global.SnakeSpace*Math.sin(Math.toRadians(angle));
		}
	}
	
	/**************************************************************************************************************
	 *                                             Body Move forward                                              *
	 **************************************************************************************************************/
	synchronized void move()
	{
		if(id==0)//follow head
		{
			x=head.getX();
			y=head.getY();
			angle=head.getAngle();
		}
		else
		{
			Body previousbody=head.getBody().get(id-1);
			x=previousbody.getX();
			y=previousbody.getY();
			angle=previousbody.getAngle();
		}
	}
	
	/**************************************************************************************************************
	 *                                          getters and setters                                               *
	 **************************************************************************************************************/
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public boolean isVisible() {
		return visible;
	}

	public Head getHead() {
		return head;
	}
}
