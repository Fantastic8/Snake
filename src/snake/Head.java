package snake;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import neuralnetwork.Network;
import panels.Canvas;
import panels.ToolsFrame;
import tools.Global;

public class Head implements Runnable {
	//private data
	private double x;
	private double y;
	private double angle;
	private int length;
	private ArrayList<Body> body;
	private Memory memory;
	private Inputer inputer;
	private Network network;
	private Starve starve;
	private long bornTime;
	
	//public flag
	public boolean stop;
	public boolean left;
	public boolean right;
	public boolean train;
	
	//display
	private String name;
	private Canvas canvas;
	
	
	/**************************************************************************************************************
	 *                                                Constructor                                                 *
	 **************************************************************************************************************/
	public Head(Canvas canvas,int x, int y,int length,String name)
	{
		this.x=x;
		this.y=y;
		this.length=length;
		this.canvas=canvas;
		this.name=name;
		angle=90;//vertical
		body=new ArrayList<Body>();
		left=false;
		right=false;
		train=false;
		starve=new Starve(Head.this);
		inputer=new Inputer(Head.this);//snake's neural network input getter
		memory=new Memory(Head.this,inputer);//snake's memory
		network=new Network(Head.this);//snake's neural network action controller
		network.setNumNodes(new int[]{Global.InputerNumber,(int)(Global.InputerNumber*1.3),Global.InputerNumber/2,Global.InputerNumber/4,2});
		network.setTrainFile(Global.SnakeTrainFile);
	}
	
	/**************************************************************************************************************
	 *                                             Thread Begin                                                   *
	 **************************************************************************************************************/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		createEvent();//create event
		
		train=false;
		stop=false;
		
		new Thread(new Detector(Head.this)).start();//detect the state of snake
		new Thread(inputer).start();//detect the input of snake
		try {
			Thread.sleep(10);//wait for detector setup
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		new Thread(network).start();//snake movement control
		while(!train)//wait for training
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		new Thread(starve).start();//starve control
		
		bornEvent();
		
		while(!stop)
		{
			try {
				Thread.sleep(50-Global.SnakeSpeed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forward();
			
			if(left)
			{
				turnleft();
			}
			if(right)
			{
				turnright();
			}
		}
		dieEvent();
	}	
	
	/**************************************************************************************************************
	 *                                              length control                                                *
	 **************************************************************************************************************/
	public void grow()//add 9 invisible bodies and 1 visible body
	{
		for(int i=0;i<9;i++)
		{
			body.add(new Body(Head.this,false));
		}
		body.add(new Body(Head.this,true));
		length++;
	}
	
	/**************************************************************************************************************
	 *                                             action control                                                 *
	 **************************************************************************************************************/
	synchronized public void forward()
	{
		//move tail first
		for(int i=body.size()-1;i>=0;i--)
		{
			body.get(i).move();
		}
		//move head
		x+=Global.SnakeStep*Math.cos(Math.toRadians(angle));
		y-=Global.SnakeStep*Math.sin(Math.toRadians(angle));
	}
	
	public void turnleft()
	{
		angle=(angle+4)%360;
	}
	
	public void turnright()
	{
		angle=(angle-4)%360;
	}
	
	/**************************************************************************************************************
	 *                                             Create Event                                                   *
	 **************************************************************************************************************/
	public void createEvent()
	{
		Global.toolsframe.newMessage("Snake "+name+" Training...");
		//tool frame display
		
	}
	
	/**************************************************************************************************************
	 *                                             Born Event                                                     *
	 **************************************************************************************************************/
	public void bornEvent()
	{
		bornTime=System.currentTimeMillis();
		Global.toolsframe.addMessage("Snake "+name+" Training Done");
		Global.toolsframe.addMessage("Snake "+name+" born");
		Global.toolsframe.displaySnake(Head.this);
	}
	
	/**************************************************************************************************************
	 *                                             Eat Event                                                      *
	 **************************************************************************************************************/
	public void eatEvent(Point bean)
	{
		//snake grow
		grow();
		//beans delete
		Global.Beans.remove(bean);
		//starve refresh
		starve.refresh();
		//add learning case
		try {
			network.addTrainingCase(Global.SnakeRecallIndex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//show message
		Global.toolsframe.addMessage(name+" eat beans, length="+length);
		Global.toolsframe.addMessage("Add Training Cases->");
		Global.toolsframe.drawrecall(true);
	}
	
	/**************************************************************************************************************
	 *                                             Starve Event                                                     *
	 **************************************************************************************************************/
	public void starveEvent()
	{
		//search on list
		if(!Global.Snakes.contains(Head.this))
		{
			return;
		}
		Global.toolsframe.addMessage("Snake "+name+" starve to death");
	}
	
	/**************************************************************************************************************
	 *                                             Die  Event                                                     *
	 **************************************************************************************************************/
	public void dieEvent()
	{
		//after death
System.out.println("Die");
		//set message
		Global.toolsframe.addMessage("Snake "+name+" Die");
		//add training case to 
		try {
			Thread.sleep(200);
			network.addTrainingCase(Global.SnakeRecallIndex);
			Global.Snakes.remove(Head.this);
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Global.toolsframe.drawrecall(true);
		
		//report
		long duration=(System.currentTimeMillis()-bornTime)/1000;
		Global.toolsframe.addMessage("Snake "+name+" Report:");
		Global.toolsframe.addMessage("Length : "+length);
		Global.toolsframe.addMessage("Duration(s) : "+duration);
		Global.toolsframe.addMessage("Score : "+ScoreEvaluation(duration,length));
		
		
		try {
			
			Global.toolsframe.addMessage("Snake "+name+" will reborn in 5s");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//reborn
		canvas.addSnake(getName());
	}
	
	/**************************************************************************************************************
	 *                                             Score Evaluation                                               *
	 **************************************************************************************************************/
	public int ScoreEvaluation(long duration,int length)
	{
		return (int)((length-5)*10+duration);
	}
	
	
	/**************************************************************************************************************
	 *                                            getters and setters                                             *
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

	public int getLength() {
		return length;
	}

	public ArrayList<Body> getBody() {
		return body;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public Inputer getInputer() {
		return inputer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Memory getMemory() {
		return memory;
	}

	public Starve getStarve() {
		return starve;
	}

	public Network getNetwork() {
		return network;
	}
	
}
