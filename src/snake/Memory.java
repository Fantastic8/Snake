package snake;

import java.util.LinkedList;

import tools.Global;

public class Memory {
	private Head head;
	private int memorylength;
	private Inputer inputer;
	private LinkedList<double[][]> mem;
	
	public Memory(Head head,Inputer inputer)
	{
		this.head=head;
		this.inputer=inputer;//updater
		memorylength=Global.SnakeMemoryLength;
		mem=new LinkedList<double[][]>();
		mem.addFirst(CreateMem(inputer.getInput()));
	}
	
	public void updateMemory()
	{
		//add new memory
		mem.addFirst(CreateMem(inputer.getInput()));
		//forget
		if(mem.size()>memorylength)
		{
			mem.removeLast();
		}
	}
	
	public double[][] CreateMem(double[][] input)
	{
		double[][] output=new double[Global.InputerBridgeNumber][Global.InputerBridgeNumber];
		for(int i=0;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber;j++)
			{
				output[i][j]=input[i][j];
			}
		}
		return output;
	}
	
	public double[][] Recall(int index)
	{
		if(mem.size()<=index)
		{
			return null;
		}
		return mem.get(index);
	}
}
