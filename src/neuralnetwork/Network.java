package neuralnetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;

import snake.Head;
import tools.Global;

public class Network implements Runnable{
	
	private Head head;				//snake's head
	private int NumOfCols;       /* number of layers + 1  i.e, include input layer */
	private int NumOfRows;       /* max number of rows net + 1, last is bias node  */
	private int NumINs;       /* number of inputs, not including bias node     */
	private int NumOUTs;       /* number of outputs, not including bias node    */
	private double LearningRate=0.5;     /* most books suggest 0.3                        */
	private double Criteria=1;     /* all outputs must be within this to terminate  */
	private double TestCriteria=1;     /* all outputs must be within this to generalize */
	private long MaxIterate=900000;  /* maximum number of iterations                */
	private int ReportIntv=101;     /* print report every time this many cases done*/
	private double Momentum=0.9;     /* momentum constant                             */
	private int TrainCases;      /* number of training cases        */
	private int CalculateInputsAndOutputsS=0;/*CalculateInputsAndOutputs count */
	// network topology by column ------------------------------------
	private int[] NumNodes;//topology col1=NumIN
	private String ParametersFile;  /* file containing snake's parameters*/
	private String TrainFile;  /* file containing training data */
	
	private int[] NumRowsPer; /* number of rows used in each column incl. bias */
	/* note - bias is not included on output layer   */
	/* note - leftmost value must equal NumINs+1     */
	/* note - rightmost value must equal NumOUTs     */

	private double[][] TrainArray;
	private int CritrIt = 3 * TrainCases;
	
	private CellRecord[][]  CellArray;
	private double[] Inputs;
	private double[] Outputs;
	private double[] DesiredOutputs;
	private double[][] extrema;  // [0] is low, [1] is high
	private int Iteration;
	private double[] ScaledCriteria;
	private double[] ScaledTestCriteria;
	
	DecimalFormat format;
	
	/**************************************************************************************************************
	 *                                                Constructor                                                 * 
	 **************************************************************************************************************/
	public Network(Head head)
	{
		this.head=head;
		ParametersFile=head.getName();
		format=new DecimalFormat();
		format.applyPattern("#.0000");
	}
	
	/**************************************************************************************************************
	 *                                                 Load Snake                                                 *
	 **************************************************************************************************************
	 * All Parameters
	 * double learning rate
	 * double criteria
	 * long MaxIterate
	 * double Momentum
	 * int NumNodesLayers(NumCols)
	 * int[NumNodesLayers] NumNodes
	 **************************************************************************************************************/
	private boolean loadParameters(String filename)
	{
		BufferedReader read;
		try{
			File parametersfile=new File(Network.class.getResource("../SnakesData/SnakesParameters/"+filename+".snk").getFile());
			if(!parametersfile.exists())
			{
				return false;
			}
			read=new BufferedReader(new FileReader(parametersfile));
			String textLine="";
			String str="";
		    while(( textLine=read.readLine())!=null)
		    {
		    	str+=textLine+" ";
		    }
		    String[] numbersArray=str.split(" ");
		    //set parameters
	    	//Generation=Long.parseLong(numbersArray[0]);
	    	LearningRate=Double.parseDouble(numbersArray[0]);
	    	Criteria=Double.parseDouble(numbersArray[1]);
	    	MaxIterate=Long.parseLong(numbersArray[2]);
	    	Momentum=Double.parseDouble(numbersArray[3]);
	    	int NumNodesLayers=Integer.parseInt(numbersArray[4]);
	    	NumNodes=new int[NumNodesLayers];
	    	//load topology
	    	for(int i=0;i<NumNodesLayers;i++)
	    	{
	    		NumNodes[i]=Integer.parseInt(numbersArray[5+i]);
	    	}
	    	//load cellrecord
			//CellArray=new CellRecord[NumOfRows][NumOfCols];
		    read.close();
		    return true;
		}
		catch(Exception e)
		{
System.out.println("Load Parameter Failed");
	    	return false;
		}
	}
	
	/**************************************************************************************************************
	 *                                                 Load Snake                                                 *
	 **************************************************************************************************************
	 * All Parameters
	 * double learning rate
	 * double criteria
	 * long MaxIterate
	 * double Momentum
	 * int NumNodesLayers(NumCols)
	 * int[NumNodesLayers] NumNodes
	 **************************************************************************************************************/
	private boolean saveParameters(String filename)
	{
		BufferedReader read;
		try{
			File parametersfile=new File(Network.class.getResource("../SnakesData/SnakesParameters/"+filename+".snk").getFile());
			if(!parametersfile.exists())
			{
				return false;
			}
			read=new BufferedReader(new FileReader(parametersfile));
			String textLine="";
			String str="";
		    while(( textLine=read.readLine())!=null)
		    {
		    	str+=textLine+" ";
		    }
		    String[] numbersArray=str.split(" ");
		    //set parameters
	    	//Generation=Long.parseLong(numbersArray[0]);
	    	LearningRate=Double.parseDouble(numbersArray[0]);
	    	Criteria=Double.parseDouble(numbersArray[1]);
	    	MaxIterate=Long.parseLong(numbersArray[2]);
	    	Momentum=Double.parseDouble(numbersArray[3]);
	    	int NumNodesLayers=Integer.parseInt(numbersArray[4]);
	    	NumNodes=new int[NumNodesLayers];
	    	//load topology
	    	for(int i=0;i<NumNodesLayers;i++)
	    	{
	    		NumNodes[i]=Integer.parseInt(numbersArray[5+i]);
	    	}
	    	//load cellrecord
			//CellArray=new CellRecord[NumOfRows][NumOfCols];
		    read.close();
		    return true;
		}
		catch(Exception e)
		{
System.out.println("Load Parameter Failed");
	    	return false;
		}
	}
	
	/**************************************************************************************************************
	 *                            Set up all parameters to get ready to control                                   *
	 **************************************************************************************************************/
	//set up all parameters
	private boolean setup()
	{
		if(NumNodes==null)
		{
			return false;
		}
		int I,J,K;
		//set up col and rows
		NumOfCols=NumNodes.length;
		NumOfRows=NumNodes[0];
		for(int i=0;i<NumNodes.length;i++)
		{
			if(NumOfRows<NumNodes[i])
			{
				NumOfRows=NumNodes[i];
			}
		}
		NumRowsPer=new int[NumOfRows];
		
		//set up INs and OUTs
		NumINs=NumNodes[0];
		NumOUTs=NumNodes[NumNodes.length-1];
		
		Inputs=new double[NumINs];
		DesiredOutputs=new double[NumOUTs];
		extrema=new double[NumINs + NumOUTs][2];
		ScaledCriteria=new double[NumOUTs];
		ScaledTestCriteria=new double[NumOUTs];
		TrainArray=new double[TrainCases][NumINs + NumOUTs];
		//TestArray=new double[TestCases][NumINs + NumOUTs];
		
		//set up topology
		NumRowsPer[0]=NumNodes[0]+1;
		for(int i=1;i<NumNodes.length;i++)
		{
			NumRowsPer[i]=NumNodes[i];
		}
		
		//setup cell array
		CellArray=new CellRecord[NumOfRows][NumOfCols];
		for(int i=0;i<NumOfRows;i++)
		{
			for(int j=0;j<NumOfCols;j++)
			{
				CellArray[i][j]=new CellRecord(NumOfRows);
			}
		}

		/* initialize the weights to small random values. */
		/* initialize previous changes to 0 (momentum).   */
		for (I = 1; I < NumOfCols; I++)
		{
			for (J = 0; J < NumRowsPer[I]; J++)
			{
				for (K = 0; K < NumRowsPer[I - 1]; K++)
				{
					CellArray[J][I].Weights[K] = 2.0 * Math.random() - 1.0;
					CellArray[J][I].PrevDelta[K] = 0;
				}
			}
		}
		return true;
	}
	/**************************************************************************************************************
	 *                                    Thread that control snake's movement                                    *
	 **************************************************************************************************************/
	@Override
	public void run() {
		/*if(!loadParameters(head.getName()))
		{
			Global.toolsframe.addMessage("Parameter File Not Found.");
			return;
		}*/
		train();
System.out.println("Train Cases="+TrainCases);
		while(!head.isStop())
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Outputs=TestForward(head.getInputer().getInput());
			if(Outputs[0]>0)//left button
			{
	//System.out.println("Left down");
				head.left=true;
			}
			else
			{
	//System.out.println("Left release");
				head.left=false;
			}
			if(Outputs[1]>0)//right button
			{
	//System.out.println("Right down");
				head.right=true;
			}
			else
			{
	//System.out.println("Right release");
				head.right=false;
			}
		}
	}
	
	/**************************************************************************************************************
	 *                                          Check the input in the String same                                *
	 **************************************************************************************************************/
	public boolean checkinput(int numberofinput,String input1,double[][] input2,int blurrysize)
	{
		String[] input=input1.split(" ");
		int blurry=0;
		for(int i=0;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber;j++)
			{
				if(Math.abs(Double.parseDouble(input[i*Global.InputerBridgeNumber+j])-input2[i][j])>Global.BlurryVal)
				{
					//return false;
					blurry++;
					if(blurry>=blurrysize)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**************************************************************************************************************
	 *                                          get output from String input                                      *
	 **************************************************************************************************************/
	public double[] getoutput(String input)
	{
		String[] ins=input.split(" ");
		double[] output=new double[NumOUTs];
		for(int i=Global.InputerNumber;i<ins.length;i++)
		{
			output[i-Global.InputerNumber]=Double.parseDouble(ins[i]);
		}
		return output;
	}
	
	/**************************************************************************************************************
	 *                                          get input from String input                                      *
	 **************************************************************************************************************/
	public double[][] getinput(String input)
	{
		String[] ins=input.split(" ");
		double[][] inputer=new double[Global.InputerBridgeNumber][Global.InputerBridgeNumber];
		for(int i=0;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber;j++)
			{
				inputer[i][j]=Double.parseDouble(ins[i*Global.InputerBridgeNumber+j]);
			}
		}
		return inputer;
	}

	
	/**************************************************************************************************************
	 *                                          Add a train case to Training file                                 
	 * @throws IOException *
	 **************************************************************************************************************/
	public void addTrainingCase(int Memoryindex) throws IOException
	{
System.out.println("Add Trainning Case");
		double[][] input=head.getMemory().Recall(Memoryindex);
		if(input==null)
		{
			return;
		}
		File trainfile=new File(System.getProperty("user.dir")+"/src/SnakesData/TrainningData/"+TrainFile);
		//read old data
		BufferedReader read=new BufferedReader(new FileReader(trainfile));
		int traincase=Integer.parseInt(read.readLine());//read train cases
		double[] output;
		LinkedList<String> cases=new LinkedList<String>();//store all cases
		//int modify list;
		LinkedList<String> modify=new LinkedList<String>();//multiple modify case
		String textLine="";
		//check modify data
		for(int i=0;i<traincase&&(textLine=read.readLine())!=null;i++)
		{
			if(checkinput(Global.InputerNumber,textLine,input,Global.BlurrySize))//modify old data
			{
				if(!head.stop)//not die, eat beans
				{
					read.close();
					return;//no need to modify
				}
System.out.println("Modify");
				//modify=i;
				//modify.add(i);
				modify.add(textLine);
				continue;//no need to add this case to cases
			}
			cases.add(textLine);
	    }
System.out.println("Modify number="+modify.size());
		traincase+=1-modify.size();//modify train cases
		//modify old data
		if(modify.size()<=0)//no old data match
		{
			//add input
			//modify=traincase-1;
			//modify.add(traincase-1);
			if(!head.stop)//eat beans
			{
				output=TestForward(head.getMemory().Recall(Global.SnakeRecallIndex));
				/*//scale up
				output[0]=(output[0]*1.1)%10;
				output[1]=(output[1]*1.1)%10;*/
			}
			else//die
			{
				output=analyzeInput(input);
			}
		}
		else//old data match && die
		{
			output=analyzeInput(input);
			//modify input and output
			//modify input
			for(int i=0;i<Global.InputerBridgeNumber;i++)
			{
				for(int j=0;j<Global.InputerBridgeNumber;j++)
				{
					for(int t=0;t<modify.size();t++)
					{
						input[i][j]+=getinput(modify.get(t))[i][j];
					}
					input[i][j]/=(modify.size()+1);
				}
			}
			//modify outputs
System.out.println("Modify");
			for(int i=0;i<output.length;i++)
			{
				for(int t=0;t<modify.size();t++)
				{
					//output[i]=(output[i]*0.7+modifyoutput[i]*0.3);
					output[i]+=getoutput(modify.get(t))[i];
				}
				output[i]/=(modify.size()+1);
			}
			
		}
		
		//cases[modify]="";//reset modify string
		String tempcase="";
		for(int i=0;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber;j++)
			{
				tempcase+=format.format(input[i][j])+" ";
			}
		}
		//add output
		for(int i=0;i<NumOUTs;i++)
		{
			if(i==NumOUTs-1)
			{
				tempcase+=format.format(output[i]);
				break;
			}
			tempcase+=format.format(output[i])+" ";
		}
		cases.add(tempcase);
		read.close();
		//write new data
		BufferedWriter write=new BufferedWriter(new FileWriter(trainfile));
		write.write(String.valueOf(traincase)+"\n");
		for(int i=0;i<traincase;i++)
		{
			write.write(cases.get(i)+"\n");
		}
		write.close();
		
	}
	
	/**************************************************************************************************************
	 *                                   Analyze this input to get a better output                                *
	 **************************************************************************************************************/
	public double[] analyzeInput(double[][] input)
	{
		double[] output=new double[NumOUTs];
		for(int i=0;i<NumOUTs;i++)
		{
			output[i]=0;
		}
		//front left
		for(int i=0;i<Global.InputerBridgeNumber/2;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber/2;j++)
			{
				if(input[i][j]<0)
				{
					output[0]-=i+j;
				}
				else if(input[i][j]>0)
				{
					output[0]+=1.5*(i+j);
				}
			}
		}
		//front right
		for(int i=0;i<Global.InputerBridgeNumber/2;i++)
		{
			for(int j=Global.InputerBridgeNumber/2;j<Global.InputerBridgeNumber;j++)
			{
				if(input[i][j]<0)
				{
					output[1]-=Global.InputerBridgeNumber+i-j;
				}
				else if(input[i][j]>0)
				{
					output[1]+=1.5*(Global.InputerBridgeNumber+i-j);
				}
			}
		}
		
		//back left
		for(int i=Global.InputerBridgeNumber/2;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber/2;j++)
			{
				if(i==Global.InputerBridgeNumber/2 && j==Global.InputerBridgeNumber/2-1) // remove its body
				{
					continue;
				}
				if(input[i][j]<0)
				{
					output[1]+=(Global.InputerBridgeNumber-i+j)*0.5;
				}
				else if(input[i][j]>0)
				{
					output[1]-=0.75*(Global.InputerBridgeNumber-i+j);
				}
			}
		}
				
		//back right
		for(int i=Global.InputerBridgeNumber/2;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=Global.InputerBridgeNumber/2;j<Global.InputerBridgeNumber;j++)
			{
				if(i==Global.InputerBridgeNumber/2 && j==Global.InputerBridgeNumber/2)//remove its body
				{
					continue;
				}
				
				if(input[i][j]<0)
				{
					output[0]+=(2*Global.InputerBridgeNumber-i-j)*0.5;
				}
				else if(input[i][j]>0)
				{
					output[0]-=0.75*(2*Global.InputerBridgeNumber-i-j);
				}
			}
		}
		//back
		//check front
		for(int i=Global.InputerBridgeNumber/2-1;i>=0;i--)
		{
			if(input[i][Global.InputerBridgeNumber/2-1]<0&&input[i][Global.InputerBridgeNumber/2]<0)
			{
				int index=((int)(Math.random()*100))%2;//choose a way to reverse
System.out.print("both index="+index+"Original output["+index+"]="+output[index]);
				output[index]=-output[index]*1.5;
System.out.println(" new output["+index+"]="+output[index]);
				break;
			}
			else if(input[i][Global.InputerBridgeNumber/2-1]<0)
			{
				output[1]=Math.abs(output[1])*1.5;
				break;
			}
			else if(input[i][Global.InputerBridgeNumber/2]<0)
			{
				output[0]=Math.abs(output[0])*1.5;
				break;
			}
		}
		
		//double positive check
		if(output[0]>0 && output[1]>0)
		{
			double temp=output[0];
			output[0]=output[0]/(output[0]+output[1])*(output[0]-output[1]);
			output[1]=output[1]/(temp+output[1])*(output[1]-temp);
		}
		
		//scale down
		output[0]=10/(1+Math.exp(-output[0]/10))-5;
		output[1]=10/(1+Math.exp(-output[1]/10))-5;
		
System.out.println("output[0]="+output[0]+" output[1]="+output[1]);
		return output;
	}
	
	/**************************************************************************************************************
	 *               train neural network based on training cases from train file                                 *
	 **************************************************************************************************************/
	public void train() {
		CalculateInputsAndOutputsS=0;
		setup();
		try {
			GetData(TrainFile);// read training and test data into arrays
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		int    I, J, K, existsError, ConvergedIterations = 0;
		double Sum, newDelta;

		Iteration = 0;
		ScaleCriteria();

//System.out.println();
//System.out.println("Iteration     Inputs          Desired Outputs          Actual Outputs");

		// -------------------------------
		// beginning of main training loop
		do
		{ /* retrieve a training pair */
			CalculateInputsAndOutputs();
			for (J = 0; J < NumRowsPer[0] - 1; J++)
			{
				CellArray[J][0].Output = Inputs[J];
			}

			/* set up bias nodes */
			for (I = 0; I < NumOfCols - 1; I++)
			{
				CellArray[NumRowsPer[I] - 1][I].Output = 1.0;
				CellArray[NumRowsPer[I] - 1][I].Error = 0.0;
			}

			/**************************
			*    FORWARD PASS        *
			**************************/
			/* hidden layers */
			for (I = 1; I < NumOfCols - 1; I++)
			{
				for (J = 0; J < NumRowsPer[I] - 1; J++)
				{
					Sum = 0.0;
					for (K = 0; K < NumRowsPer[I - 1]; K++)
					{
						Sum += CellArray[J][I].Weights[K] * CellArray[K][I - 1].Output;
					}
					CellArray[J][I].Output = squashing(Sum);
					CellArray[J][I].Error = 0.0;
				}
			}

			/* output layer  */
			for (J = 0; J < NumOUTs; J++)
			{
				Sum = 0.0;
				for (K = 0; K < NumRowsPer[NumOfCols - 2]; K++)
				{
					Sum += CellArray[J][NumOfCols - 1].Weights[K]* CellArray[K][NumOfCols - 2].Output;
				}
				CellArray[J][NumOfCols - 1].Output = squashing(Sum);
				CellArray[J][NumOfCols - 1].Error = 0.0;
			}

			/**************************
			*    BACKWARD PASS       *
			**************************/
			/* calculate error at each output node */
			for (J = 0; J < NumOUTs; J++)
			{
				CellArray[J][NumOfCols - 1].Error =	DesiredOutputs[J] - CellArray[J][NumOfCols - 1].Output;
			}

			/* check to see how many consecutive oks seen so far */
			existsError = 0;
			for (J = 0; J < NumOUTs; J++)
			{
				if (Math.abs(CellArray[J][NumOfCols - 1].Error) > ScaledCriteria[J])
				{
					existsError = 1;
				}
			}
			if (existsError == 0)
			{
				ConvergedIterations++;
			}
			else
			{
				ConvergedIterations = 0;
			}

			/* apply derivative of squashing function to output errors */
			for (J = 0; J < NumOUTs; J++)
			{
				CellArray[J][NumOfCols - 1].Error =	CellArray[J][NumOfCols - 1].Error * Dsquashing(CellArray[J][NumOfCols - 1].Output);
			}

			/* backpropagate error */
			/* output layer */
			for (J = 0; J < NumRowsPer[NumOfCols - 2]; J++)
			{
				for (K = 0; K < NumRowsPer[NumOfCols - 1]; K++)
				{
					CellArray[J][NumOfCols - 2].Error = CellArray[J][NumOfCols - 2].Error+ CellArray[K][NumOfCols - 1].Weights[J]* CellArray[K][NumOfCols - 1].Error* Dsquashing(CellArray[J][NumOfCols - 2].Output);
				}
			}
				

			/* hidden layers */
			for (I = NumOfCols - 3; I >= 0; I--)
			{
				for (J = 0; J < NumRowsPer[I]; J++)
				{
					for (K = 0; K < NumRowsPer[I + 1] - 1; K++)
					{
						CellArray[J][I].Error =	CellArray[J][I].Error+ CellArray[K][I + 1].Weights[J] * CellArray[K][I + 1].Error* Dsquashing(CellArray[J][I].Output);
					}
				}
			}
				

			/* adjust weights */
			for (I = 1; I < NumOfCols; I++)
			{
				for (J = 0; J < NumRowsPer[I]; J++)
				{
					for (K = 0; K < NumRowsPer[I - 1]; K++)
					{
						newDelta = (Momentum * CellArray[J][I].PrevDelta[K])+ (LearningRate * CellArray[K][I - 1].Output * CellArray[J][I].Error);
						CellArray[J][I].Weights[K] = CellArray[J][I].Weights[K] + newDelta;
						CellArray[J][I].PrevDelta[K] = newDelta;
					}
				}
			}
			//System.out.println(Iteration);
			//GenReport(Iteration);
			Iteration++;
		} while (!((ConvergedIterations >= CritrIt) || (Iteration >= MaxIterate)));
		// end of main training loop
		// -------------------------------
		FinReport(ConvergedIterations);
		TrainForward();
		
		//train over
		head.train=true;//ready to go
	}
	
	/**************************************************************************************************************
	 *                     Get data from Training and Testing Files, put into arrays                              *
	 *                     @throws IOException                                                                    *
	 **************************************************************************************************************/
	void GetData(String filename) throws IOException
	{
		for (int i = 0; i < (NumINs + NumOUTs); i++)
		{
			extrema[i][0] = 99999.0; extrema[i][1] = -99999.0;
		}

		// read in training data
		File trainfile=new File(System.getProperty("user.dir")+"/src/SnakesData/TrainningData/"+filename);
		if(!trainfile.exists())
		{
			System.out.println("Create Train File.");
			trainfile.createNewFile();
			BufferedWriter writer=new BufferedWriter(new FileWriter(trainfile));
			writer.write(1+"\n");//train case
			for(int i=0;i<Global.InputerBridgeNumber;i++)
			{
				for(int j=0;j<Global.InputerBridgeNumber;j++)
				{
					writer.write(head.getInputer().getInput()[i][j]+" ");
				}
			}
			//default output
			writer.write("-3 -3\n");
			writer.close();
		}
		BufferedReader read=new BufferedReader(new FileReader(trainfile));
		//set up train cases
	    TrainCases=Integer.parseInt(read.readLine());
	    
	    //set train cases informatin
	    Global.toolsframe.addMessage(Global.SnakeTrainFile+" Training Cases ( "+TrainCases+" )");
	    TrainArray=new double[TrainCases][NumINs + NumOUTs];
	    CritrIt=3*TrainCases;
	    String[] cases=new String[TrainCases];
	    String[] numbers;
		for (int i = 0; i < TrainCases; i++)
		{
			cases[i]=read.readLine();
			numbers=cases[i].split(" ");
			for (int j = 0; j < (NumINs + NumOUTs); j++)
			{
				try{
					TrainArray[i][j]=Double.parseDouble(numbers[j]);
				}
				catch(Exception e)
				{
System.out.println(cases[i]+" i="+i+" j="+j);
				}
//System.out.println("TrainArray["+i+"]["+j+"/"+(NumINs + NumOUTs)+"]="+TrainArray[i][j]);
				if (TrainArray[i][j] < extrema[j][0])
				{
					extrema[j][0] = TrainArray[i][j];
				}
				if (TrainArray[i][j] > extrema[j][1])
				{
					extrema[j][1] = TrainArray[i][j];
				}
			}
		}
		read.close();

		// scale training and test data to range 0..1
		for (int i = 0; i < TrainCases; i++)
		{
			for (int j = 0; j < NumINs + NumOUTs; j++)
			{
				TrainArray[i][j] = ScaleDown(TrainArray[i][j], j);
			}
		}
	}

	/**************************************************************************************************************
	 *                                           Assign the next training pair                                    *
	 **************************************************************************************************************/
	void CalculateInputsAndOutputs()
	{
		for (int i = 0; i < NumINs; i++)
		{
			Inputs[i] = TrainArray[CalculateInputsAndOutputsS][i];
		}
		for (int i = 0; i < NumOUTs; i++)
		{
			DesiredOutputs[i] = TrainArray[CalculateInputsAndOutputsS][i + NumINs];
		}
		CalculateInputsAndOutputsS++;
		if (CalculateInputsAndOutputsS == TrainCases)
		{
			CalculateInputsAndOutputsS = 0;
		}
	}

	/**************************************************************************************************************
	 *                                           Assign the next testing pair                                     *
	 **************************************************************************************************************/
	void TestInputs(double[][] input)
	{
		for(int i=0;i<Global.InputerBridgeNumber;i++)
		{
			for(int j=0;j<Global.InputerBridgeNumber;j++)
			{
				if(Inputs==null)
				{
System.out.println("Inputs Error");
				}
				Inputs[i*Global.InputerBridgeNumber+j]=input[i][j];
			}
		}
	}
	
	/**************************************************************************************************************
	 *                                        Run Test Data forward pass only                                     *
	 **************************************************************************************************************/
	public double[] TestForward(double[][] input)
	{
		int GoodCount = 0;
		double Sum, TotalError = 0;
		TestInputs(input);
		for (int J = 0; J < NumRowsPer[0] - 1; J++)
		{
			CellArray[J][0].Output = Inputs[J];
		}

		/* hidden layers */
		for (int I = 1; I < NumOfCols - 1; I++)
		{
			for (int J = 0; J < NumRowsPer[I] - 1; J++)
			{
				Sum = 0.0;
				for (int K = 0; K < NumRowsPer[I - 1]; K++)
				{
					Sum += CellArray[J][I].Weights[K] * CellArray[K][I - 1].Output;
				}
				CellArray[J][I].Output = squashing(Sum);
				CellArray[J][I].Error = 0.0;
			}
		}
			

		/* output layer  */
		for (int J = 0; J < NumOUTs; J++)
		{
			Sum = 0.0;
			for (int K = 0; K < NumRowsPer[NumOfCols - 2]; K++)
			{
				Sum += CellArray[J][NumOfCols - 1].Weights[K]* CellArray[K][NumOfCols - 2].Output;
			}
			CellArray[J][NumOfCols - 1].Output = squashing(Sum);
			CellArray[J][NumOfCols - 1].Error =	DesiredOutputs[J] - CellArray[J][NumOfCols - 1].Output;
			if (Math.abs(CellArray[J][NumOfCols - 1].Error) <= ScaledTestCriteria[J])
			{
				GoodCount++;
			}
			TotalError += CellArray[J][NumOfCols - 1].Error * CellArray[J][NumOfCols - 1].Error;
		}
		/******************************************************************
		 * 
		 *                         out put here
		 *                                                 
		 ******************************************************************/
//for (int J = 0; J < NumOUTs; J++)
//{
//	System.out.println(" "+format.format(ScaleOutput(CellArray[J][NumOfCols - 1].Output, NumINs + J)));
//}
		double[] output=new double[NumOUTs];
		output[0]=ScaleOutput(CellArray[0][NumOfCols - 1].Output, NumINs);
		output[1]=ScaleOutput(CellArray[1][NumOfCols - 1].Output, NumINs+1);
		return output;
	}

	/**************************************************************************************************************
	 *                             Run Training Data forward pass only, after training                            *
	 **************************************************************************************************************/
	void TrainForward()
	{
		int GoodCount = 0;
		double Sum, TotalError = 0;
//System.out.println();
//System.out.println("Confirm Training Cases");
		for (int H = 0; H < TrainCases; H++)
		{
			CalculateInputsAndOutputs();
			for (int J = 0; J < NumRowsPer[0] - 1; J++)
			{
				CellArray[J][0].Output = Inputs[J];
			}

			/* hidden layers */
			for (int I = 1; I < NumOfCols - 1; I++)
			{
				for (int J = 0; J < NumRowsPer[I] - 1; J++)
				{
					Sum = 0.0;
					for (int K = 0; K < NumRowsPer[I - 1]; K++)
					{
						Sum += CellArray[J][I].Weights[K] * CellArray[K][I - 1].Output;
					}
					CellArray[J][I].Output = squashing(Sum);
					CellArray[J][I].Error = 0.0;
				}
			}

			/* output layer  */
			for (int J = 0; J < NumOUTs; J++)
			{
				Sum = 0.0;
				for (int K = 0; K < NumRowsPer[NumOfCols - 2]; K++)
				{
					Sum += CellArray[J][NumOfCols - 1].Weights[K]* CellArray[K][NumOfCols - 2].Output;
				}
				CellArray[J][NumOfCols - 1].Output = squashing(Sum);
				CellArray[J][NumOfCols - 1].Error =	DesiredOutputs[J] - CellArray[J][NumOfCols - 1].Output;
				if (Math.abs(CellArray[J][NumOfCols - 1].Error) <= ScaledCriteria[J])
				{
					GoodCount++;
				}
				TotalError += CellArray[J][NumOfCols - 1].Error*CellArray[J][NumOfCols - 1].Error;
			}
//GenReport(-1);
		}
System.out.println();
System.out.println("Sum Squared Error for Training cases   = "+format.format(TotalError));
System.out.println("% of Training Cases that meet criteria = "+format.format(((double)GoodCount / (double)TrainCases)));
System.out.println();
	}

	/**************************************************************************************************************
	 *                                                      Final Report                                          *
	 **************************************************************************************************************/
	void FinReport(int CIterations)
	{
		if (CIterations<CritrIt)
		{
System.out.println("Network did not converge");
		}
		else
		{
System.out.println("Converged to within criteria");
		}
		Global.toolsframe.addMessage("Total iterations = "+Iteration);
System.out.println("Total number of iterations = "+Iteration);
	}

	/**************************************************************************************************************
	 *                                        Generation Report                                                   *
	 *                                     pass in a -1 if running test cases                                     *
	 **************************************************************************************************************/
	void GenReport(int Iteration)
	{
		int J;		
		if (Iteration == -1)
		{
			for (J = 0; J < NumRowsPer[0] - 1; J++)
			{
System.out.print(" "+format.format(ScaleOutput(Inputs[J], J)));
			}
System.out.print("  ");
			for (J = 0; J < NumOUTs; J++)
			{
System.out.print(" "+format.format(ScaleOutput(DesiredOutputs[J], NumINs + J)));
			}
System.out.print("  ");
			for (J = 0; J < NumOUTs; J++)
			{
System.out.print(" "+format.format(ScaleOutput(CellArray[J][NumOfCols - 1].Output, NumINs + J)));
			}
System.out.println();
		}
		else if ((Iteration % ReportIntv) == 0)
		{
System.out.print("  "+Iteration+"  ");
			for (J = 0; J < NumRowsPer[0] - 1; J++)
			{
System.out.print(" "+format.format(ScaleOutput(Inputs[J], J)));
			}
System.out.print("  ");
			for (J = 0; J < NumOUTs; J++)
			{
System.out.print(" "+format.format(ScaleOutput(DesiredOutputs[J], NumINs + J)));
			}
System.out.print("  ");
			for (J = 0; J < NumOUTs; J++)
			{
System.out.print(" "+format.format(ScaleOutput(CellArray[J][NumOfCols - 1].Output, NumINs + J)));
			}
System.out.println();
		}
	}

	/**************************************************************************************************************
	 *                                               Squashing Function                                           *
	 **************************************************************************************************************/
	double squashing(double Sum)
	{
		return 1.0 / (1.0 + Math.exp(-Sum));
	}

	/**************************************************************************************************************
	 *                                       Derivative of Squashing Function                                     *
	 **************************************************************************************************************/
	double Dsquashing(double out)
	{
		return out * (1.0 - out);
	}

	/**************************************************************************************************************
	 *                                           Scale Desired Output                                             *
	 **************************************************************************************************************/
	double ScaleDown(double X, int output)
	{
		return 0.9*(X - extrema[output][0]) / (extrema[output][1] - extrema[output][0]+0.01) + 0.05;
	}

	/**************************************************************************************************************
	 *                                  Scale actual output to original range                                     *
	 **************************************************************************************************************/
	double ScaleOutput(double X, int output)
	{
		double range = extrema[output][1] - extrema[output][0];
		double scaleUp = ((X - 0.05) / 0.9) * range;
		return (extrema[output][0] + scaleUp);
	}
	
	/**************************************************************************************************************
	 *                                            Scale criteria                                                  *
	 **************************************************************************************************************/
	void ScaleCriteria()
	{
		int J;
		for (J = 0; J < NumOUTs; J++)
		{
			ScaledCriteria[J] = 0.9*Criteria / (extrema[NumINs + J][1] - extrema[NumINs + J][0]);
		}
		for (J = 0; J < NumOUTs; J++)
		{
			ScaledTestCriteria[J] = 0.9*TestCriteria / (extrema[NumINs + J][1] - extrema[NumINs + J][0]);
		}
	}

	/**************************************************************************************************************
	 *                                        Getters and Setters                                                 *
	 **************************************************************************************************************/
	public double getLearningRate() {
		return LearningRate;
	}

	public void setLearningRate(double learningRate) {
		LearningRate = learningRate;
	}

	public double getCriteria() {
		return Criteria;
	}

	public void setCriteria(double criteria) {
		Criteria = criteria;
	}

	public double getTestCriteria() {
		return TestCriteria;
	}

	public void setTestCriteria(double testCriteria) {
		TestCriteria = testCriteria;
	}

	public long getMaxIterate() {
		return MaxIterate;
	}

	public void setMaxIterate(long maxIterate) {
		MaxIterate = maxIterate;
	}

	public double getMomentum() {
		return Momentum;
	}

	public void setMomentum(double momentum) {
		Momentum = momentum;
	}

	public int[] getNumNodes() {
		return NumNodes;
	}

	public void setNumNodes(int[] numNodes) {
		NumNodes = numNodes;
	}
	
	public CellRecord[][] getCellArray() {
		return CellArray;
	}
	public void setCellArray(CellRecord[][] cellArray) {
		CellArray = cellArray;
	}
	public double[] getOutput()
	{
		double[] output=new double[NumOUTs];
		for(int i=0;i<NumOUTs;i++)
		{
			output[i]=CellArray[NumNodes.length-1][i].Output;
		}
		return output;
	}
	public String getTrainFile() {
		return TrainFile;
	}
	public void setTrainFile(String trainFile) {
		TrainFile = trainFile;
	}
	public double[] getOutputs() {
		return Outputs;
	}
	
	public void reportParameters()
	{
		System.out.println("NumOfCols="+NumOfCols);
		System.out.println("NumOfRows="+NumOfRows);
		System.out.println("NumINs="+NumINs);
		System.out.println("NumOUTs="+NumOUTs);
		System.out.println("LearningRate="+LearningRate);
		System.out.println("Criteria="+Criteria);
		//System.out.println("TestCriteria="+TestCriteria);
		System.out.println("MaxIterate="+MaxIterate);
		System.out.println("ReportIntv="+ReportIntv);
		System.out.println("Momentum="+Momentum);
		System.out.println("TrainCases="+TrainCases);
		//System.out.println("TestCases="+TestCases);
		for(int i=0;i<NumNodes.length;i++)
		{
			System.out.println("NumNodes"+(i+1)+"="+NumNodes[i]);
		}
	}
}
