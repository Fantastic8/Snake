package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import neuralnetwork.Network;

public class CreateData {
	static String DestinationFileA="BeamA.dat";
	static String DestinationFileB="BeamB.dat";
	static int lengthA=35;
	static int lengthB=30;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//out put
		BufferedWriter writeTxtA=new BufferedWriter(new FileWriter(new File(DestinationFileA)));
	    BufferedWriter writeTxtB=new BufferedWriter(new FileWriter(new File(DestinationFileB)));
	    //format number
	    DecimalFormat format=new DecimalFormat();
	    format.applyPattern("#.00");
	     double input1,input2;
	     String output1,output2,output3;
	     //output A
	     for(int i=0;i<lengthA;i++)
	     {
	    	 input1=Double.parseDouble(format.format(Math.random()*10));
	    	 input2=Double.parseDouble(format.format(Math.random()*10));
	    	 output1=format.format(input1-input2);
	    	 output2=format.format((input1+input2)/2);
	    	 output3=format.format((int)Math.round(input1));
	    	 //writeTxtA.write(input1+" "+input2+" "+output1+"\n");
	    	 //writeTxtA.write(input1+" "+input2+" "+output2+"\n");
	    	 //writeTxtA.write(input1+" "+input2+" "+output3+"\n");
	    	 writeTxtA.write(input1+" "+input2+" "+output1+" "+output2+" "+output3+"\n");
	     }
	     writeTxtA.close();
	     
	   //output B
	     for(int i=0;i<lengthB;i++)
	     {
	    	 input1=Double.parseDouble(format.format(Math.random()*10));
	    	 input2=Double.parseDouble(format.format(Math.random()*10));
	    	 output1=format.format(input1-input2);
	    	 output2=format.format((input1+input2)/2);
	    	 output3=format.format((int)Math.round(input1));
	    	 //writeTxtB.write(input1+" "+input2+" "+output1+"\n");
	    	 //writeTxtB.write(input1+" "+input2+" "+output2+"\n");
	    	 //writeTxtB.write(input1+" "+input2+" "+output3+"\n");
	    	 writeTxtB.write(input1+" "+input2+" "+output1+" "+output2+" "+output3+"\n");
	     }
	     writeTxtB.close();
	}

}
