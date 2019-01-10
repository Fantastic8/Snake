package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class GenerateData {
	static String SourceFile="TrainingData.txt";//"TrainingData.txt";
	static String DestinationFile="BeamA.dat";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//input
		BufferedReader readTxt=new BufferedReader(new FileReader(new File(SourceFile)));
		//out put
	    BufferedWriter writeTxt=new BufferedWriter(new FileWriter(new File(DestinationFile)));
	    //format number
	    DecimalFormat format=new DecimalFormat();
	    format.applyPattern("#.00");
		String textLine="";
	     String str="";
	     while(( textLine=readTxt.readLine())!=null){
	               str+=textLine+" ";
	      }
	     String[] numbersArray=str.split(" ");
	     double input1,input2;
	     String output1,output2,output3;
	     for(int i=0;i<numbersArray.length;i+=2)
	     {
	    	 input1=Double.parseDouble(numbersArray[i]);
	    	 input2=Double.parseDouble(numbersArray[i+1]);
	    	 output1=format.format(input1-input2);
	    	 output2=format.format((input1+input2)/2);
	    	 output3=format.format((int)Math.round(input1));
	    	 //writeTxt.write(input1+" "+input2+" "+output1+"\n");
	    	 //writeTxt.write(input1+" "+input2+" "+output2+"\n");
	    	 //writeTxt.write(input1+" "+input2+" "+output3+"\n");
	    	 writeTxt.write(input1+" "+input2+" "+output1+" "+output2+" "+output3+"\n");
	     }
	     readTxt.close();
	     writeTxt.close();
	}

}
