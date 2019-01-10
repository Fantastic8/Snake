package panels;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import snake.Head;
import tools.Global;

public class Canvas extends JFrame implements Runnable{
	
	//canvas variables
	private CanvasPanel PaintJPanel;//panel
	private JPanel MainJPanel;//panel
	private ImageIcon MainBG;//background
	private JLabel mainbackground;//label of background
	private int xframeold;
	private int yframeold;
	
	//data variables
	private LinkedList<Head> Snakes;// all snake's heads
	private LinkedList<Point> Beans;// all beans' position
	
	/**************************************************************************************************************
	 *                                             Constructor                                                    *
	 **************************************************************************************************************/
	public Canvas()
	{
		//set snake data
		Snakes=new LinkedList<Head> ();
		Beans=new LinkedList<Point>();
		//set global
		Global.Snakes=Snakes;
		Global.Beans=Beans;
				
		setUndecorated(true);//undecorated
		setResizable(false);//not resize
		setBounds(200, 20, 450, 300);
		this.setSize(1000, 1000);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//set panel
		MainJPanel=(JPanel)getContentPane();
		PaintJPanel=new CanvasPanel(Snakes,Beans);
		PaintJPanel.setBounds(0, 0, 1000, 1000);
		Canvas.this.add(PaintJPanel);
		//load background image
		MainBG=new ImageIcon(Canvas.class.getResource("../UI/Net.jpg"));
		//load background label
		mainbackground=new JLabel(MainBG);
		mainbackground.setBounds(0, 0, MainBG.getIconWidth(),MainBG.getIconHeight());
		
		MainJPanel.setOpaque(false);
		PaintJPanel.setOpaque(false);
		MainJPanel.setLayout(null);
		((JPanel)getContentPane()).setLayout(null);
		this.getLayeredPane().setLayout(null);
		this.getLayeredPane().add(mainbackground, new Integer(Integer.MIN_VALUE));
		
		//===============================================================draggable
		this.addMouseListener(new MouseAdapter() 
		{
		  @Override
		  public void mousePressed(MouseEvent e) {
		  xframeold = e.getX();
		  yframeold = e.getY();
		  }
		 });
		this.addMouseMotionListener(new MouseMotionAdapter() {
			  public void mouseDragged(MouseEvent e) {
			  int xOnScreen = e.getXOnScreen();
			  int yOnScreen = e.getYOnScreen();
			  int xframenew = xOnScreen - xframeold;
			  int yframenew = yOnScreen - yframeold;
			  Canvas.this.setLocation(xframenew, yframenew);
			  Global.toolsframe.setLocation(xframenew+995,yframenew);
			  }
			 });
		
		//========Control=====================
		/*Canvas.this.addKeyListener(new KeyAdapter(){
			
            public void keyPressed(KeyEvent e){
            	if(Snakes.size()==0)
            	{
            		return;
            	}
                switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:Snakes.get(0).left=true; break;
                case KeyEvent.VK_RIGHT:Snakes.get(0).right=true; break;
               }
            }
            
            public void keyReleased(KeyEvent e){
            	if(Snakes.size()==0)
            	{
            		return;
            	}
                switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:Snakes.get(0).left=false; break;
                case KeyEvent.VK_RIGHT:Snakes.get(0).right=false; break;
               }
            }
		});*/
	}
	
	/**************************************************************************************************************
	 *                                              Thread Start                                                  *
	 **************************************************************************************************************/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Canvas.this.setVisible(true);
		//set the map
		CreateBeans();
		addSnake(Global.SnakeName);
	}
	
	/**************************************************************************************************************
	 *                                          Add A Snake to Snake LinkedList                                   *
	 **************************************************************************************************************/
	//void addSnake(int x,int y,int length)
	public void addSnake(String name)
	{		
		Head head=new Head(Canvas.this,(int)Canvas.this.getWidth()/2,(int)Canvas.this.getHeight()/2,0,name);
		//Head head=new Head(Canvas.this,x,y,length,"a"+length);
		Snakes.add(head);
		for(int i=0;i<Global.StartLength;i++)
		{
			head.grow();
		}
		new Thread(head).start();
	}
	
	/**************************************************************************************************************
	 *                                          Add Beans to LinkedList                                           *
	 **************************************************************************************************************/
	void CreateBeans()
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep((long) (1000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(true)
				{
					while(Beans.size()>=Global.BeansMaxNumber)//control beans number
					{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					while(!Global.BeansCreate)//stop
					{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep((long) (Global.BeansInterval+Math.random()*Global.BeansRandomness));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Beans.addLast(new Point(10+(int)(Math.random()*(MainJPanel.getWidth()-20)),10+(int)(Math.random()*(MainJPanel.getHeight()-20))));
					repaint();
				}
			}}).start();
	}
}
