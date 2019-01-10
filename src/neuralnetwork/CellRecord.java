package neuralnetwork;

public class CellRecord {
	public double Output;
	public double Error;
	public double[] Weights;
	public double[] PrevDelta;
	
	public CellRecord(int NumOfRows)
	{
		Weights=new double[NumOfRows];
		PrevDelta=new double[NumOfRows];
	}
}
