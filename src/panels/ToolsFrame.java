package panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import snake.Head;
import tools.AddButton;
import tools.Global;

public class ToolsFrame extends JFrame implements Runnable{
	
	private ToolsFramePanel PaintJPanel;//panel
	private JPanel MainJPanel;//panel
	private ImageIcon MainBG;//background
	private JLabel mainbackground;//label of background
	private JTextArea messagebox;//show message
	
	
	//button
	private ImageIcon ImageIconBeansStop;
	private ImageIcon ImageIconBeansContinue;
	private JLabel LabelBeansStop;
	private JLabel LabelBeansContinue;
	private JLabel LabelNetworkShow;
	private JLabel LabelNetworkClose;
	private JButton ButtonBeansStop;
	private JButton ButtonBeansContinue;
	private JButton ButtonNetworkShow;
	private JButton ButtonNetworkClose;
	/**************************************************************************************************************
	 *                                              Contructor                                                    *
	 **************************************************************************************************************/
	public ToolsFrame()
	{
		setUndecorated(true);//undecorated
		setResizable(false);//not resize
		setBounds(1195, 20, 450, 300);
		this.setSize(500, 1000);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//set panel
		MainJPanel=(JPanel)getContentPane();
		PaintJPanel=new ToolsFramePanel();
		PaintJPanel.setBounds(0, 0, 500, 1000);
		ToolsFrame.this.add(PaintJPanel);
		//load background image
		MainBG=new ImageIcon(ToolsFrame.class.getResource("../UI/ToolsFrame.jpg"));
		//load background label
		mainbackground=new JLabel(MainBG);
		mainbackground.setBounds(0, 0, MainBG.getIconWidth(),MainBG.getIconHeight());
		
		MainJPanel.setOpaque(false);
		PaintJPanel.setOpaque(false);
		MainJPanel.setLayout(null);
		this.getLayeredPane().setLayout(null);
		this.getLayeredPane().add(mainbackground, new Integer(Integer.MIN_VALUE));
		
		//add close button
		new AddButton(MainJPanel,ToolsFrame.this.getWidth()-33,3,"../UI/Close.png"){

			@Override
			public void event() {
				// TODO Auto-generated method stub
				System.exit(0);//close
			}};
			
		//add minimize button
		new AddButton(MainJPanel,ToolsFrame.this.getWidth()-63,3,"../UI/Minimize.png"){

			@Override
			public void event() {
				// TODO Auto-generated method stub
				ToolsFrame.this.setExtendedState(ToolsFrame.this.ICONIFIED);//set minimize
				Global.canvas.setExtendedState(Global.canvas.ICONIFIED);
			}};
		
		//add network close button
		AddButton networkclose=new AddButton(MainJPanel,0,390,"../UI/NetworkCloseButton.png"){

			@Override
			public void event() {
				// TODO Auto-generated method stub
				LabelNetworkClose.setVisible(false);
				ButtonNetworkClose.setVisible(false);
				messagebox.setVisible(true);
				PaintJPanel.setShownetwork(false);
			}};
		LabelNetworkClose=networkclose.getLabel();
		ButtonNetworkClose=networkclose.getButton();
		LabelNetworkClose.setVisible(false);
		ButtonNetworkClose.setVisible(false);
		
		//add network show button
		AddButton networkshow=new AddButton(MainJPanel,176,381,"../UI/NetworkshowButton.png"){

			@Override
			public void event() {
				// TODO Auto-generated method stub
				LabelNetworkClose.setVisible(true);
				ButtonNetworkClose.setVisible(true);
				messagebox.setVisible(false);
				PaintJPanel.setShownetwork(true);
			}};
		LabelNetworkShow=networkshow.getLabel();
		ButtonNetworkShow=networkshow.getButton();
		
		//add message box
		messagebox=new JTextArea();
		messagebox.setBounds(45, 575, 260, 160);
		//messagebox.setOpaque(true);
		messagebox.setEditable(false);
		messagebox.setBackground(Color.WHITE);
		messagebox.setFont(new Font("Leelawadee",Font.PLAIN,20));
		messagebox.setAlignmentX(CENTER_ALIGNMENT);//center
		MainJPanel.add(messagebox);
		
		//add beans stop button
		AddButton beansstop=new AddButton(MainJPanel,110,753,"../UI/BeansStopButton.png"){

			@Override
			public void event() {
				// TODO Auto-generated method stub
				Global.BeansCreate=false;
				//switch
				LabelBeansStop.setVisible(false);
				ButtonBeansStop.setVisible(false);
				LabelBeansContinue.setVisible(true);
				ButtonBeansContinue.setVisible(true);
			}};
		LabelBeansStop=beansstop.getLabel();
		ButtonBeansStop=beansstop.getButton();
		
		//add beans continue button
		AddButton beanscontinue=new AddButton(MainJPanel,110,753,"../UI/BeansContinueButton.png"){

			@Override
			public void event() {
				// TODO Auto-generated method stub
				 Global.BeansCreate=true;
				  //switch
				  LabelBeansContinue.setVisible(false);
				  ButtonBeansContinue.setVisible(false);
				  LabelBeansStop.setVisible(true);
				  ButtonBeansStop.setVisible(true);
			}};
		LabelBeansContinue=beanscontinue.getLabel();
		ButtonBeansContinue=beanscontinue.getButton();
		LabelBeansContinue.setVisible(false);
		ButtonBeansContinue.setVisible(false);
		
			
		//add beans clear button
		new AddButton(MainJPanel,210,753,"../UI/BeansClearButton.png"){
			@Override
			public void event() {
				// TODO Auto-generated method stub
				Global.Beans.removeAll(Global.Beans);
			}};
			
		//add snake control button new
		new AddButton(MainJPanel,55,865,"../UI/SnakeButtonNew.png"){
			@Override
			public void event() {
				// TODO Auto-generated method stub
				
			}};
			
		//add snake control button import
		new AddButton(MainJPanel,180,865,"../UI/SnakeButtonImport.png"){
			@Override
			public void event() {
				// TODO Auto-generated method stub
				
			}};
			
		//add train file button new
		new AddButton(MainJPanel,345,865,"../UI/TrainFileButtonNew.png"){
			@Override
			public void event() {
				// TODO Auto-generated method stub
				JFileChooser TrainFile=new JFileChooser();
				TrainFile.setDialogType(JFileChooser.SAVE_DIALOG);//saver
				TrainFile.setFileFilter(new FileFilter(){//Only Accept *.dat
		            @Override
		            public boolean accept(File pathname) {
		                // TODO Auto-generated method stub
		                String s = pathname.getName().toLowerCase();
		                if(s.endsWith(".dat")||!s.contains(".")){
		                    return true;
		                }
		                return false;
		            }
					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return ".dat";
					}
		        });
				TrainFile.setCurrentDirectory(new File(System.getProperty("user.dir")+"/src/SnakesData/TrainningData/"));
				TrainFile.showSaveDialog(ToolsFrame.this);
				
				if(TrainFile.getSelectedFile()!=null)
				{
					Global.SnakeTrainFile=TrainFile.getSelectedFile().getName()+".dat";
					//die immediately
					if(Global.Snakes.size()>0)
					{
						Global.Snakes.get(0).stop=true;
					}
				}
			}};
			
		//add train file button load
		new AddButton(MainJPanel,345,915,"../UI/TrainFileButtonLoad.png"){
			@Override
			public void event() {
				// TODO Auto-generated method stub
				JFileChooser TrainFileChooser=new JFileChooser();
				TrainFileChooser.setFileFilter(new FileFilter(){//Only Accept *.dat
		            @Override
		            public boolean accept(File pathname) {
		                // TODO Auto-generated method stub
		                String s = pathname.getName().toLowerCase();
		                if(s.endsWith(".dat")||!s.contains(".")){
		                    return true;
		                }
		                return false;
		            }
					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return ".dat";
					}
		        });
				TrainFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")+"/src/SnakesData/TrainningData/"));
				TrainFileChooser.showOpenDialog(ToolsFrame.this);
				
				//set training file
				if(TrainFileChooser.getSelectedFile()!=null)
				{
					Global.SnakeTrainFile=TrainFileChooser.getSelectedFile().getName();
					//die immediately
					if(Global.Snakes.size()>0)
					{
						Global.Snakes.get(0).stop=true;
					}
				}
			}};
	}
	
	/**************************************************************************************************************
	 *                                              Thread Start                                                  *
	 **************************************************************************************************************/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ToolsFrame.this.setVisible(true);
		
		try {
			Thread.sleep(1000);//wait for canvas to setup
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		displaySnake(0);
	}
	
	/**************************************************************************************************************
	 *                                                     Tools                                                  *
	 **************************************************************************************************************/
	
	public void displaySnake(int indexofsnake)
	{
		if(indexofsnake<0)
		{
			PaintJPanel.setHead(null);
		}
		else
		{
			PaintJPanel.setHead(Global.Snakes.get(indexofsnake));
		}
	}
	
	public void displaySnake(Head head)
	{
		if(Global.Snakes.contains(head))
		{
			PaintJPanel.setHead(head);
		}
		else
		{
			PaintJPanel.setHead(null);
		}
	}
	
	public void addMessage(String str)
	{
		String text=messagebox.getText();
		String[] lines=text.split("\n");
		if(lines.length>5)//5 lines
		{
			text="";
			for(int i=1;i<lines.length;i++)
			{
				text+=lines[i]+"\n";
			}
		}
		text+=str+"\n";
		messagebox.setText(text);
	}
	
	public void newMessage(String str)
	{
		messagebox.setText(str+"\n");
	}
	
	public void drawrecall(boolean recall)
	{
		PaintJPanel.drawrecall(recall);
	}

}
