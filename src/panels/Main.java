package panels;

import tools.Global;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Canvas c=new Canvas();
		ToolsFrame t=new ToolsFrame();
		new Thread(c).start();
		new Thread(t).start();
		Global.canvas=c;
		Global.toolsframe=t;
	}
}
