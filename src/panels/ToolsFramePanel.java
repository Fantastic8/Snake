package panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import neuralnetwork.CellRecord;
import snake.Head;
import snake.Inputer;
import tools.Global;

public class ToolsFramePanel extends JPanel {
	
	private Head head;
	private double[][] recallinput;
	private double[] recalloutput;
	private double[] analyzeoutput;
	private boolean shownetwork;
	
	/**************************************************************************************************************
	 *                                             Constructor                                                    *
	 **************************************************************************************************************/
	public ToolsFramePanel()
	{
		head=null;
		recallinput=null;
		recalloutput=null;
		analyzeoutput=null;
	}
	
	/**************************************************************************************************************
	 *                                                Paint(input row and column reverse)                         *
	 **************************************************************************************************************/
	@Override
	public void paint(Graphics g)
	{
		//draw inputer
		if(head!=null)
		{
			if(head.getInputer().getInput()!=null)
			{
				drawMatrix(g,head.getInputer().getInput(),115,100,370);
				//draw output
				drawOutput(g,head.getNetwork().getOutputs(),200,400,50);
			}
			
			
			if(shownetwork)
			{
				drawNetwork(g,new Point(225,450),new Point(275,450),head.getNetwork().getNumNodes(),head.getNetwork().getCellArray());
			}
			else//if network show, no show below
			{
				//draw recall
				if(recallinput!=null)
				{
					drawMatrix(g,recallinput,332,597,190);
					//draw output
					drawOutput(g,recalloutput,334,739,30);
					drawOutput(g,analyzeoutput,414,739,30);
				}
				
				//draw train file name
				if(Global.SnakeTrainFile!=null)
				{
					g.setFont(new Font("Leelawadee",Font.PLAIN,20));
					g.setColor(Color.WHITE);
					g.drawString(Global.SnakeTrainFile, 345, 858);
				}
			}
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
	 *                                              draw network                                                  *
	 **************************************************************************************************************/
	public void drawNetwork(Graphics g,Point output1, Point output2, int[] NumNodes, CellRecord[][] CellArray)
	{
		if(CellArray==null)
		{
			return;
		}
		//height = 460px - (900px-20px)
		//weight = 16px - 484px
		
		int NumOfRows=NumNodes[0];
		int sidelength=0,basey,basex,colorp;
		
		//find max rows
		for(int i=0;i<NumNodes.length;i++)
		{
			if(NumOfRows<NumNodes[i])
			{
				NumOfRows=NumNodes[i];
			}
		}
		sidelength=(int)(468/(NumOfRows*2+1));
		
		for(int i=0;i<NumNodes.length-1;i++)
		{
			//calculate y
			basey=(int)(460+420*(NumNodes.length-i-1)/(NumNodes.length-1)-sidelength/2);
			for(int j=0;j<NumNodes[i];j++)
			{
				//calculate x
				colorp=(int)(255/(1+Math.exp(-CellArray[j][i].Output)));//scale
				g.setColor(new Color(colorp,colorp,colorp));
				//draw lines
				if(i==NumNodes.length-2)
				{
					g.drawLine(252-sidelength*(NumNodes[i]-1/2-2*j)+sidelength/2, basey+sidelength/2,(int)output1.getX(),(int)output1.getY());
					g.drawLine(252-sidelength*(NumNodes[i]-1/2-2*j)+sidelength/2, basey+sidelength/2,(int)output2.getX(),(int)output2.getY());					
				}
				else
				{
					for(int p=0;p<NumNodes[i+1];p++)
					{
						g.drawLine(252-sidelength*(NumNodes[i]-1/2-2*j)+sidelength/2, basey+sidelength/2, 252-sidelength*(NumNodes[i+1]-1/2-2*p)+sidelength/2, 460+420*(NumNodes.length-i-2)/(NumNodes.length-1)-sidelength/2);
					}
				}
				//draw neuron
				g.fillRect(252-sidelength*(NumNodes[i]-1/2-2*j), basey, sidelength, sidelength);
				
				//CellArray[j][i]
			}
		}
	}
	
	
	/**************************************************************************************************************
	 *                                              draw Output                                                   *
	 **************************************************************************************************************/
	public void drawOutput(Graphics g,double[] output,int startx,int starty,int sidelength)
	{
		if(output==null)
		{
			return;
		}
		g.setFont(new Font("Leelawadee",Font.BOLD,(int)(sidelength*0.5)));
		//first output[0]
		if(output[0]>0)
		{
			g.setColor(Color.WHITE);
			g.fillRect(startx, starty, sidelength, sidelength);
			g.setColor(Color.BLACK);
		}
		else
		{
			g.setColor(Color.WHITE);
		}
		g.drawString(String.valueOf(output[0]).substring(0, 3), (int)(startx+sidelength*0.1), (int)(starty+sidelength*0.7));
		//second output[1]
		if(output[1]>0)
		{
			g.setColor(Color.WHITE);
			g.fillRect(startx+sidelength, starty, sidelength, sidelength);
			g.setColor(Color.BLACK);
		}
		else
		{
			g.setColor(Color.WHITE);
		}
		g.drawString(String.valueOf(output[1]).substring(0, 3), (int)((startx+sidelength)+sidelength*0.1),(int)(starty+sidelength*0.7));
	}
	
	/**************************************************************************************************************
	 *                                              copy Matrix                                                   *
	 **************************************************************************************************************/
	public double[][] copyMatrix(double[][] matrix)
	{
		double[][] copy=new double[Global.InputerBridgeNumber][Global.InputerBridgeNumber];
		for(int i=0;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber;j++)
			{
				copy[i][j]=matrix[i][j];
			}
		}
		return copy;
	}
	
	/**************************************************************************************************************
	 *                                              draw Matrix                                                   *
	 **************************************************************************************************************/
	public void drawMatrix(Graphics g,double[][] matrix,int startx,int starty,int sidelength)
	{
		int x,y,size;
		g.setFont(new Font("Leelawadee",Font.BOLD,(int)(sidelength/Global.InputerBridgeNumber*0.75)));
		g.setColor(Color.BLACK);
		for(int row=0;row<Global.InputerBridgeNumber;row++)
		{
			for(int column=0;column<Global.InputerBridgeNumber;column++)
			{
				x=startx+column*(g.getFont().getSize()+2);
				y=starty+row*(g.getFont().getSize()+2);
				size=g.getFont().getSize()+2;
				if(matrix[row][column]>0)
				{
					g.setColor(Color.PINK);
					g.fillRect(x-size/8, y-(size*7/8), size, size);
					g.setColor(Color.WHITE);
					g.drawString(Integer.toString((int)matrix[row][column]), x ,y);
				}
				else if(matrix[row][column]==0)
				{
					g.setColor(Color.WHITE);
					g.fillRect(x-size/8, y-(size*7/8), size, size);
					g.setColor(Color.BLACK);
					g.drawString(Integer.toString((int)matrix[row][column]), x ,y);
				}
				else
				{
					g.setColor(Color.DARK_GRAY);
					g.fillRect(x-size/8, y-(size*7/8), size, size);
					g.setColor(Color.WHITE);
					g.drawString(Integer.toString((int)matrix[row][column]), x ,y);
				}
				
			}
		}
	}

	/**************************************************************************************************************
	 *                                           Getters and Setters                                              *
	 **************************************************************************************************************/

	public void setHead(Head head) {
		this.head = head;
	}
	
	public void drawrecall(boolean recall)
	{
		if(recall==true)
		{
			recallinput=copyMatrix(head.getMemory().Recall(Global.SnakeRecallIndex));
			recalloutput=head.getNetwork().TestForward(recallinput);
			analyzeoutput=head.getNetwork().analyzeInput(recallinput);
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					recallinput=null;
				}
			}).start();
		}
		else
		{
			recallinput=null;
			recalloutput=null;
		}
	}

	public boolean isShownetwork() {
		return shownetwork;
	}

	public void setShownetwork(boolean shownetwork) {
		this.shownetwork = shownetwork;
	}
}
