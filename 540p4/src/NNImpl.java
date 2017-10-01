/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the input layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public ArrayList<Node> outputNodes=null;// list of the output layer nodes

	public ArrayList<Instance> trainingSet=null;//the training set

	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs

	/**
	 * This constructor creates the nodes necessary for the neural network
	 * Also connects the nodes of different layers
	 * After calling the constructor the last node of both inputNodes and  
	 * hiddenNodes will be bias nodes. 
	 */

	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[][] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;

		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=trainingSet.get(0).classValues.size();
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}

		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);

		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}

		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);

		//Output node layer
		outputNodes=new ArrayList<Node> ();
		for(int i=0;i<outputNodeCount;i++)
		{
			Node node=new Node(4);
			//Connecting output layer nodes with hidden layer nodes
			for(int j=0;j<hiddenNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
				node.parents.add(nwp);
			}	
			outputNodes.add(node);
		}	
	}

	/**
	 * For example if the outputs
	 * of the outputNodes are [0.1, 0.5, 0.2, 0.1, 0.1], it should return 1. If outputs
	 * of the outputNodes are [0.1, 0.5, 0.1, 0.5, 0.2], it should return 3. 
	 * The parameter is a single instance. 
	 */
	public int calculateOutputForInstance(Instance inst)
	{
		ArrayList<Double> outputToResult=new ArrayList<Double>();
		for(int i=0;i<inputNodes.size()-1;i++){
			inputNodes.get(i).setInput(inst.attributes.get(i));
		}
		for(Node hiddenNode:hiddenNodes){
			hiddenNode.calculateOutput();	
		}
		for(Node outputNode:outputNodes){
			outputNode.calculateOutput();
			outputToResult.add(outputNode.getOutput());
			//add the output into the list of inputs to hidden layer
		}
		double temp=outputToResult.get(0);
		int index=0;
		int i=0;
		for(Double e:outputToResult){
			if(e>=temp){ 
				temp=e;
				index=i;	
			}
			i++;
		}
		return index;
	}

	private void updateWeights(double[][]wij,double[][]wjk){
		
		for(int i=0;i<outputNodes.size();i++){
			if(outputNodes.get(i).parents!=null){
				ArrayList<NodeWeightPair> parents=outputNodes.get(i).parents;
				for(int j=0;j<wjk[i].length;j++){
					parents.get(j).weight+=wjk[i][j];
				}
			}

		}//update the weights of links from hidden layer to output layer 
		for(int i=0;i<hiddenNodes.size();i++){
			if(hiddenNodes.get(i).parents!=null){
				ArrayList<NodeWeightPair> parents=hiddenNodes.get(i).parents;
				for(int j=0;j<wij[i].length;j++){
					parents.get(j).weight+=wij[i][j];
				}
			}//update weights of links from input layer to hidden layer
		}
	}
	
	private void hiddenToOut(Instance instance, double[][]wjk,double[]deltaO){
		double a;

		for(int j=0;j<wjk.length;j++){
			
			for(int k=0;k<wjk[j].length;k++){
				a=hiddenNodes.get(k).getOutput();
				wjk[j][k]=learningRate*deltaO[j]*a;
			}//use deltaK to get wjk
		}
	}
	private void inToHidden(Instance instance, double[][]wij,double[]deltaH){

		double a;
		for(int i=0;i<wij.length;i++){
			
			for(int j=0;j<wij[i].length;j++){
				a=inputNodes.get(j).getOutput();
				wij[i][j]=learningRate*a*deltaH[i];
			}//use delta J to get wij
		}
	}
	private void delta(Instance instance, double[]deltaO,double[]deltaH){

		double a;
		double o;
		double t;
	
		for(int k=0;k<outputNodes.size();k++){
			o=outputNodes.get(k).getOutput();
			t=instance.classValues.get(k);
			deltaO[k]=(t-o)*o*(1.0-o);
			
		}//get the deltaK
		
		for(int j=0;j<deltaH.length;j++){
			for(int k=0;k<outputNodes.size();k++){
				o=outputNodes.get(k).getOutput();
				t=instance.classValues.get(k);
				deltaH[j]+=deltaO[k]*outputNodes.get(k).parents.get(j).weight;
			}
			a=hiddenNodes.get(j).getOutput();
			deltaH[j]*=a*(1.0-a);
			
		}	//get the deltaJ with the help of delta K
	}
	
	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */

	public void train()
	{
		// TODO: add code here
		double[][] wjk=new double[outputNodes.size()][hiddenNodes.size()];
		double[][] wij=new double[hiddenNodes.size()][inputNodes.size()];
		double[]deltaO=new double[outputNodes.size()];
		double[]deltaH=new double[hiddenNodes.size()];
		Instance instance=null;
		for(int e=0;e<maxEpoch;e++){
			for(int i=0;i< trainingSet.size();i++){
				instance=trainingSet.get(i);//get the instance
				calculateOutputForInstance(instance);//calcualte input
				delta(instance,deltaO,deltaH);//calculate deltaK and deltaJ
				hiddenToOut(instance,wjk,deltaO);//update wjk
				inToHidden(instance,wij,deltaH);//update wij
				updateWeights(wij,wjk);//update the weights
			}
		}
	}
}
