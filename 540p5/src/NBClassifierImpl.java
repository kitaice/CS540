/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 5: Naive Bayes

  NBClassifierImpl.java
  This is the main class that implements functions for Naive Bayes Algorithm!
  ---------
 *Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as NBClassifierImpl for testing
  		- Not to import any external libraries
  		- Not to include any packages 
 *Notice: To use this file, you should implement 2 methods below.

	@author: TA 
	@date: April 2017
 *****************************************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.math.*;


public class NBClassifierImpl implements NBClassifier {

	private int nFeatures; 		// The number of features including the class 
	private int[] featureSize;	// Size of each features
	private List<List<Double[]>> logPosProbs;	// parameters of Naive Bayes
	private double[]marginal=new double[2];
	/**
	 * Constructs a new classifier without any trained knowledge.
	 */
	public NBClassifierImpl() {

	}

	/**
	 * Construct a new classifier 
	 * 
	 * @param int[] sizes of all attributes
	 */
	public NBClassifierImpl(int[] features) {
		this.nFeatures = features.length;

		// initialize feature size
		this.featureSize = features.clone();
		//initialize the number of values in each feature
		this.logPosProbs = new ArrayList<List<Double[]>>(this.nFeatures);
	}


	/**
	 * Read training data and learn parameters
	 * 
	 * @param int[][] training data
	 */
	@Override
	public void fit(int[][] data) {
		//	TODO
		int numPos=0;//count number of positive instances
		int numNeg=0;//count number of negative instances
		for(int i=0;i<data.length;i++){
			if(data[i][nFeatures-1]==1) numPos++;
			else  numNeg++;
		}
		marginal[0]=Math.log((double)(numPos+1.0)/(double)(data.length+2.0));
		marginal[1]=Math.log((double)(numNeg+1.0)/(double)(data.length+2.0));
		//calculate log marginal probability
		int sizeofFeature;
		for(int i=0;i<nFeatures-1;i++){
			sizeofFeature=featureSize[i];//numbers of values of each feature
			List<Double[]>conditional=new ArrayList<Double[]>(sizeofFeature);
			//store the log conditional probilities for each feature
			for(int j=0;j<sizeofFeature;j++){
				Double[]temp=new Double[2];
				int label=nFeatures-1;
				int countPos=0;
				int countNeg=0;
				for(int k=0;k<data.length;k++){
					if(data[k][i]==j){
						if(data[k][label]==1)countPos++;//p(x=j|y=1)
						else countNeg++;//p(x=j|y=0)
					}
				}
				//				int countPos=conditional(1,i,j,data);
				//				int countNeg=conditional(0,i,j,data);
				double conPos=((double)countPos+1.0)/(numPos+(double)sizeofFeature);
				double conNeg=((double)countNeg+1.0)/(numNeg+(double)sizeofFeature);
				//calculate conditional probability
				temp[0]=Math.log(conPos);
				temp[1]=Math.log(conNeg);
				conditional.add(temp);
			}
			logPosProbs.add(conditional);
		}

	}

	//	private int conditional(int classval,int index, int attribute, int[][]data){
	//		int count=0;
	//		int label=nFeatures-1;
	//		for(int i=0;i<data.length;i++){
	//			if(data[i][index]==attribute){
	//				if(data[i][label]==classval)count++;//p(x=j|y=1)
	//
	//			}
	//		}
	//		return count;
	//	}
	/**
	 * Classify new dataset
	 * 
	 * @param int[][] test data
	 * @return Label[] classified labels
	 */
	@Override
	public Label[] classify(int[][] instances) {

		int nrows = instances.length;
		Label[] yPred = new Label[nrows]; // predicted data
		double logPos;
		double logNeg;
		for(int i=0;i<instances.length;i++){//every instance
			logPos=0;
			logNeg=0;
			for(int j=0;j<nFeatures-1;j++){//every attribute, get its conditional probability accordingly
				logPos+=logPosProbs.get(j).get(instances[i][j])[0];//get the conditional probability
				logNeg+=logPosProbs.get(j).get(instances[i][j])[1];
			}
			logPos+=marginal[0];
			logNeg+=marginal[1];
			if(logPos>=logNeg) {//if the log probability of positive is larger than or equals 
				//that of negative, classify the instance as positive
				yPred[i]=Label.Positive;
			}
			else{
				yPred[i]=Label.Negative;
			} 
		}
		//	TODO

		return yPred;
	}
}