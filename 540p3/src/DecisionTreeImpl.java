import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */




public class DecisionTreeImpl {
	private DecTreeNode root;
	// ordered list of attributes
	private ArrayList<String> mTrainAttributes;
	//
	private double[][] bestSplitPointList;
	//private double[][] printInfoList;
	private ArrayList<ArrayList<Double>> mTrainDataSet;
	// Min number of instances per leaf.
	private int minLeafNumber;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl(){		// no code necessary this is void purposefully
}
	
	/**
	 * overloaded constructor just for printing root gain info
	 * @param trainDataSet
	 * @param trainAttributeNames
	 */
	DecisionTreeImpl(ArrayList<ArrayList<Double>> trainDataSet,ArrayList<String> trainAttributeNames) {
		this.mTrainAttributes = trainAttributeNames;
		ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();
		// create a copy of trainDataSet
		for (int i = 0; i < trainDataSet.size(); i++)
			dataSet.add(new ArrayList<Double>());
		for (int i = 0; i < trainDataSet.size(); i++) {
			for (int j = 0; j < trainDataSet.get(0).size(); j++) {
				dataSet.get(i).add(trainDataSet.get(i).get(j).doubleValue());
			}
		}
		this.mTrainDataSet = dataSet;
		// initialize the 2D array
		bestSplitPointList = new double[trainAttributeNames.size()][2];
		// build decision tree
		rootInfoGain(dataSet, mTrainAttributes, minLeafNumber);

	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning
	 * set.
	 * 
	 * @param train:
	 *            the training set
	 * @param tune:
	 *            the tuning set
	 */
	DecisionTreeImpl(ArrayList<ArrayList<Double>> trainDataSet, ArrayList<String> trainAttributeNames,
			int minLeafNumber) {
		this.mTrainAttributes = trainAttributeNames;
		ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();
		// create a copy of trainDataSet
		for (int i = 0; i < trainDataSet.size(); i++)
			dataSet.add(new ArrayList<Double>());
		for (int i = 0; i < trainDataSet.size(); i++) {
			for (int j = 0; j < trainDataSet.get(0).size(); j++) {
				dataSet.get(i).add(trainDataSet.get(i).get(j).doubleValue());
			}
		}
		this.mTrainDataSet = dataSet;
		this.minLeafNumber = minLeafNumber;
		// initialize the list of best split points
		bestSplitPointList = new double[trainAttributeNames.size()][2];
		// build decision tree
		this.root = buildTree(mTrainDataSet);
		
	}

	private DecTreeNode buildTree(ArrayList<ArrayList<Double>> dataSet) {
		// check if all data has the same class value
		if (isall(dataSet)) {
			int classValue = dataSet.get(0).get(mTrainAttributes.size()).intValue();
			// leaf node
			DecTreeNode node = new DecTreeNode(classValue, null, 0.0);
			return node;
		}
		// check if the size of dataSet is lower than or equal to minimum limit
		if (dataSet.size() <= minLeafNumber) {
			DecTreeNode node = new DecTreeNode(pluralityClassLabel(dataSet), null, 0.0);
			return node;
		}
		// calculate the best information gain of each attribute from a data set
		rootInfoGain(dataSet, mTrainAttributes, minLeafNumber);
		// select the best attribute
		int feature = bestAttribute(bestSplitPointList);
		// create feature node
		DecTreeNode root = new DecTreeNode(-1, "A" + (feature + 1), bestSplitPointList[feature][1]);
		ArrayList<ArrayList<Double>> subDataSet1 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> subDataSet2 = new ArrayList<ArrayList<Double>>();
		// split data into two sub data set according to specified threshold
		splitData(dataSet, subDataSet1, subDataSet2, bestSplitPointList[feature][1], feature);
		root.left = buildTree(subDataSet1);
		root.right = buildTree(subDataSet2);
		return root;
	}
	
	/**
	 * Split the data set according to the threshold of best attribute
	 * @param dataSet the training set
	 * @param sub1 left subtree
	 * @param sub2 right subtree
	 * @param threshold split point
	 * @param attribute the specific attribute
	 */
	private void splitData(ArrayList<ArrayList<Double>> dataSet, ArrayList<ArrayList<Double>> sub1, ArrayList<ArrayList<Double>> sub2, double threshold, int attribute) {
		for(int i = 0; i < dataSet.size(); i++) {
			if(dataSet.get(i).get(attribute).doubleValue() <= threshold) {
				ArrayList<Double> data = new ArrayList<Double>();
				for (int j = 0; j < dataSet.get(i).size(); j++) {
					data.add(dataSet.get(i).get(j).doubleValue());
				}//if less than threshold add the instancethe left tree
				sub1.add(data);
			} else {
				ArrayList<Double> data = new ArrayList<Double>();
				for (int j = 0; j < dataSet.get(i).size(); j++) {
					data.add(dataSet.get(i).get(j).doubleValue());
				}
				sub2.add(data);
			}//else add the instance to right tree
		}
	}
	
	/**
	 * Choose the best attribute
	 * @param list the list of all thresholds of all attribute
	 * @return
	 */
	private int bestAttribute(double[][] list) {
		double best = 0;
		int index = 0;
		for (int i = 0; i < list.length; i++) {
			// if multiple attributes have the same information gain, split on 
			// the attribute that appears later in the list of attribute labels
			if (list[i][0] >= best) {
				best = list[i][0];
				index = i;
			}
		}
		return index;
	}

	/**
	 * Check if the all data have the same class value
	 * @param dataSet
	 * @return
	 */
	private boolean isall(ArrayList<ArrayList<Double>> dataSet) {
		for (int i = 0; i < dataSet.size() - 1; i++) {
			if (!dataSet.get(i).get(mTrainAttributes.size()).equals(dataSet.get(i + 1).get(mTrainAttributes.size()).doubleValue()))
				return false;
		}
		return true;
	}

	/**
	 * Give class label to a data set that has less than or equal to 10 data
	 * @param dataSet
	 * @return
	 */
	private int pluralityClassLabel(ArrayList<ArrayList<Double>> dataSet) {
		int count = 0;
		for (int i = 0; i < dataSet.size(); i++) {
			if (dataSet.get(i).get(mTrainAttributes.size()).doubleValue() == 0)
				count++;
		}
		if (count > (dataSet.size() - count))
			return 0;
		else
			return 1;
	}
	
	/**
	 * Classify the instance
	 * @param instance
	 * @return
	 */
	public int classify(List<Double> instance) {
		return classify(instance, root);
	}
	
	/**
	 * Recursive method to classify an instance
	 * @param instance
	 * @param node
	 * @return
	 */
	private int classify(List<Double> instance, DecTreeNode node) {
		if(node.isLeaf())
			return node.classLabel;
		if(instance.get(Integer.parseInt(node.attribute.substring(1, node.attribute.length())) - 1) <= node.threshold)
			return classify(instance, node.left);
		return classify(instance, node.right);
	}

	/**
	 * Calculate all the information gain of all the attributes
	 * @param dataSet
	 * @param trainAttributeNames
	 * @param minLeafNumber
	 */
	public void rootInfoGain(ArrayList<ArrayList<Double>> dataSet, ArrayList<String> trainAttributeNames,
			int minLeafNumber) {
		this.mTrainAttributes = trainAttributeNames;
		this.mTrainDataSet = dataSet;
		this.minLeafNumber = minLeafNumber;
		// calculate the root entropy
		double entropy = calculateEntropy(dataSet);
		for (int i = 0; i < trainAttributeNames.size(); i++) {
			// sort the list according to attribute value and class value
			sortList(dataSet, i);
			ArrayList<Double> thresholds = new ArrayList<Double>();
			// calculate all possible thresholds of an attribute
			threshold(dataSet, thresholds, i);
			double largestInfoGain = 0;
			double threshold = 0;
			// select best attribute and information gain
			for (int j = 0; j < thresholds.size(); j++) {
				double infoGain = infoGain(dataSet, entropy, thresholds.get(j).doubleValue(), i);
				if (infoGain >= largestInfoGain) {
					largestInfoGain = infoGain;
					threshold = thresholds.get(j).doubleValue();
				}
			}
			// save the best attribute and information gain
			bestSplitPointList[i][0] = largestInfoGain;
			bestSplitPointList[i][1] = threshold;
		}
	}

	public void printInfo(){
		for (int i = 0; i < bestSplitPointList.length; i++) {
			System.out.println(this.mTrainAttributes.get(i) + " " + String.format("%.6f", bestSplitPointList[i][0]));
		}
		
	}
	/**
	 * Sort the ArrayList of ArrayList according to attribute value
	 * 
	 * @param dataSet
	 *            the input data
	 * @param index
	 *            the attribute number
	 */
	private void sortList(ArrayList<ArrayList<Double>> dataSet, int index) {
		Collections.sort(dataSet, new Comparator<List<Double>>() {
			@Override
			public int compare(List<Double> o1, List<Double> o2) {
				int compare = o1.get(index).compareTo(o2.get(index));
				if(compare == 0) {
					if(o1.get(mTrainAttributes.size()).doubleValue() > o2.get(mTrainAttributes.size()).doubleValue())
						return 1;
					else if(o1.get(mTrainAttributes.size()).doubleValue() < o2.get(mTrainAttributes.size()).doubleValue())
						return -1;
					else
						return 0;
				} else
					return compare;
			}
		});
	}

	/**
	 * Compute candidate thresholds for an attribute
	 * 
	 * @param dataSet
	 * @param candidates
	 * @param index
	 */
	private void threshold(ArrayList<ArrayList<Double>> dataSet, ArrayList<Double> candidates, int index) {
		double split = dataSet.get(0).get(mTrainAttributes.size()).doubleValue();
		for (int i = 1; i < dataSet.size(); i++) {
			if (dataSet.get(i).get(mTrainAttributes.size()).doubleValue() != split) {
				double threshold = (dataSet.get(i).get(index).doubleValue() + dataSet.get(i - 1).get(index).doubleValue()) / 2;
				candidates.add(threshold);
				split = dataSet.get(i).get(mTrainAttributes.size()).doubleValue();
			}
		}
	}

	/**
	 * Calculate entropy
	 * 
	 * @param dataSet
	 * @return
	 */
	private double calculateEntropy(ArrayList<ArrayList<Double>> dataSet) {
		int count = 0;
		for (int i = 0; i < dataSet.size(); i++)
			if (dataSet.get(i).get(mTrainAttributes.size()).intValue() == 0)
				count++;
		double p = (double)count / dataSet.size();
		double q = (double)(dataSet.size() - count) / dataSet.size();
		double entropy = -(p * (Math.log(p) / Math.log(2)))
				- ((q) * (Math.log(q) / Math.log(2)));
		return entropy;
	}

	/**
	 * Calculate information gain
	 * 
	 * @param dataSet
	 * @param rootInfoGain
	 * @param threshold
	 * @param index
	 * @return
	 */
	private double infoGain(ArrayList<ArrayList<Double>> dataSet, double entropy, double threshold, int index) {
		int difference = 0;
		for (int i = 0; i < dataSet.size(); i++) {
			if (Double.compare(dataSet.get(i).get(index).doubleValue(), threshold) <= 0) {
				difference++;
			} else
				break;
		}

		int count1 = 0;
		int count2 = 0;
		for (int i = 0; i < difference; i++)
			if (dataSet.get(i).get(mTrainAttributes.size()).doubleValue() == 0)
				count1++;
		for (int i = difference; i < dataSet.size(); i++)
			if (dataSet.get(i).get(mTrainAttributes.size()).doubleValue() == 0)
				count2++;

		double proportion = 1.0 * difference / dataSet.size();
		double subProportion1 = 1.0 * count1 / difference;
		double subq=(double)(difference - count1) /difference;
		double subProportion2 = 1.0 * count2 / (dataSet.size() - difference);
		double subq1=(double)(dataSet.size()-difference - count2) / (dataSet.size()-difference);
		double entropy1 = proportion * (-(subProportion1 * (Math.log(subProportion1) / Math.log(2)))
				- (subq) * (Math.log(subq) / Math.log(2)));
		double entropy2 = (1.0 - proportion) * (-(subProportion2 * (Math.log(subProportion2) / Math.log(2)))
				- ( subq1) * (Math.log(subq1) / Math.log(2)));
		if (Double.isNaN(entropy1))
			entropy1 = 0;
		if (Double.isNaN(entropy2))
			entropy2 = 0;
		return (entropy - entropy1 - entropy2);
	}

	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {
		printTreeNode("", this.root);
	}

	/**
	 * Recursively prints the tree structure, left subtree first, then right
	 * subtree.
	 */
	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + node.attribute;

		System.out.print(printStr + " <= " + String.format("%.6f", node.threshold));
		if (node.left.isLeaf()) {
			System.out.println(": " + String.valueOf(node.left.classLabel));
		} else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%.6f", node.threshold));
		if (node.right.isLeaf()) {
			System.out.println(": " + String.valueOf(node.right.classLabel));
		} else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
	}

	public double printAccuracy(int numEqual, int numTotal) {
		double accuracy = 1.0*numEqual / (double) numTotal;
		System.out.println(accuracy);
		return accuracy;
	}

	/**
	 * Private class to facilitate instance sorting by argument position since
	 * java doesn't like passing variables to comparators through nested
	 * variable scopes.
	 */
	private class DataBinder {

		public ArrayList<Double> mData;
		public int i;

		public DataBinder(int i, ArrayList<Double> mData) {
			this.mData = mData;
			this.i = i;
		}

		public double getArgItem() {
			return mData.get(i);
		}

		public ArrayList<Double> getData() {
			return mData;
		}

	}

}