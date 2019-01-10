package tools;

import java.awt.Point;
import java.util.LinkedList;

import panels.Canvas;
import panels.ToolsFrame;
import snake.Head;

public class Global {
	public static Canvas canvas;
	public static ToolsFrame toolsframe;
	//snake parameters
	public static LinkedList<Head> Snakes;// all snake's heads
	public static int SnakeSpace=2;
	public static int SnakeSpeed=40;
	public static int SnakeStep=2;
	public static int SnakeRadius=12;
	public static int StartLength=5;
	public static int SnakeRecallIndex=3;
	public static int SnakeMemoryLength=5;
	public static int StarvingTime=15000;//15s not eatting anything then die
	public static String SnakeTrainFile="Alpha.dat";
	public static String SnakeName="Alpha";
	
	//beans parameters
	public static LinkedList<Point> Beans;// all beans' position
	public static int BeansMaxNumber=50;
	public static int BeanRadius=10;
	public static int BeansInterval=3000;
	public static int BeansRandomness=1000;
	public static boolean BeansCreate=true;
	
	//input variables
	public static int InputerUnit=SnakeRadius*3;
	public static int InputerBridgeNumber=6;
	public static int InputerNumber=InputerBridgeNumber*InputerBridgeNumber;
	public static int BlurrySize=5;//check number similar input from previous training cases
	public static int BlurryVal=2;//check similar value of input
	//values for input
	public static int BeansValue=5;
	public static int BodyValue=-5;
	public static int WallValue=-8;
}
