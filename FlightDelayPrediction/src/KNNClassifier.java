import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *@description: Implementation of k Nearest Neighbors Classifier for airline delay prediction
 *@author Naveen
 */
public class KNNClassifier {

	private List<Double> maxValueList;
	private List<Double> minValueList;
	private List<KNNRecord> dataMatrix;
	private List<KNNRecord> normalizedMatrix;


	/**
	 *@description: Default constructor 
	 *@return: KNNClassifier object
	 */
	public KNNClassifier(){
		this.maxValueList = new ArrayList<Double>();
		for(int i=0;i<AirlineCompositeKey.getFeatureNames().size();i++) this.maxValueList.add(Double.MIN_VALUE);
		this.minValueList = new ArrayList<Double>();
		for(int i=0;i<AirlineCompositeKey.getFeatureNames().size();i++) this.minValueList.add(Double.MAX_VALUE);
		this.dataMatrix = new ArrayList<KNNRecord>();
		this.normalizedMatrix = new ArrayList<KNNRecord>();
		//this.model = new StringBuilder();
	}

	/**
	 *@description: parameterized constructor builds the normalized data matrix
	 *@param: String predictModel containing the data matrix of the model.
	 *@param: List<Double> minList containing the minimum values of features
	 *@param: List<Double> maxList containing the maxium values of features.
	 *@return: KNNCLassifier object
	 */
	public KNNClassifier(String predictModel, List<Double> minList, List<Double> maxList) {
		this();
		maxValueList = maxList;
		minValueList = minList;

		String[] records = predictModel.split("#");		
		for(String rec : records){

			List<Double> features = new ArrayList<>();			
			String[] parts = rec.split("@");
			String[] featuresStr = parts[0].split(",");			

			for(String feature : featuresStr) {
				features.add(Double.parseDouble(feature));
			}
			KNNRecord insideData = new KNNRecord(features,parts[1]);
			normalizedMatrix.add(insideData);
		}
	}

	public List<Double> getMaxValueList() {
		return maxValueList;
	}

	public List<Double> getMinValueList() {
		return minValueList;
	}

	public List<KNNRecord> getNormalizedMatrix() {
		return normalizedMatrix;
	}

	/**
	 *@description: convert the string to arraylist
	 *@param: String list 
	 *@return: List<Double> ls contains values in double.
	 */
	public static List<Double> strToList(String list){
		String[] features = list.split(",");
		List<Double> ls = new ArrayList<Double>();
		for(String feature : features) ls.add(Double.parseDouble(feature));
		return ls;
	}

	/**
	 *@description: convert the list to string.
	 *@param: 
	 *@return: String converted from value list. 
	 */
	public String maxListToString(){
		StringBuilder str = new StringBuilder();
		int commaCounter = 0;
		for(int i = 0 ; i < maxValueList.size(); i++){
			commaCounter++;
			str.append(maxValueList.get(i));
			if(commaCounter < maxValueList.size()) str.append(",");
		}
		return str.toString();
	}

	/**
	 *@description: convert the list to string.
	 *@param: 
	 *@return: String converted from value list. 
	 */
	public String minListToString(){
		StringBuilder str = new StringBuilder();
		int commaCounter = 0;
		for(int i = 0 ; i < minValueList.size(); i++){
			commaCounter++;
			str.append(minValueList.get(i));
			if(commaCounter < minValueList.size()) str.append(",");
		}
		return str.toString();
	}

	/**
	 *@description: convert the data from the train to object with class either true or false
	 *depending on the isDelayed flag.
	 *@param: AirlineCompositeKey instance data
	 *@return: void
	 */
	public void addInstances(AirlineCompositeKey instance){
		List<String> featureNames = AirlineCompositeKey.getFeatureNames();
		List<Double> features = new ArrayList<>();
		for(int i = 0; i < featureNames.size(); i++){			
			int featureVal = instance.getFeatureValue(featureNames.get(i));
			double curMax = maxValueList.get(i);
			maxValueList.set(i, Math.max(curMax, featureVal));

			double curMin = minValueList.get(i);
			minValueList.set(i, Math.min(curMin, featureVal));			
			features.add(featureVal*1.0);
		}
		KNNRecord knnRec = new KNNRecord(features, instance.isDelayed);	
		dataMatrix.add(knnRec);

	}

	/**
	 *@description: normalize the values for each feature based on its max and min values
	 *@param:
	 *@return: 
	 */
	public void buildClassifier() {		
		for(KNNRecord knnRec : dataMatrix){
			List<Double> features = new ArrayList<>();				
			for(int i = 0; i < knnRec.getFeatures().size(); i++){
				Double featureVal = (knnRec.getFeatures().get(i) - minValueList.get(i)) / (maxValueList.get(i) - minValueList.get(i));
				features.add(featureVal);
			}
			KNNRecord newKnnRec = new KNNRecord(features, knnRec.getLabel());	
			normalizedMatrix.add(newKnnRec);
		}		
	}


	/**
	 *@description: For each record from the test data compare the euclidian distance of 
	 *all other values from the train data and find the k nearest neighbors and poll for 
	 *maximum true or false.
	 *@param: AirlineCompositeKey instance of the test data
	 *@param: int k value of k in KNN
	 *@return: String with value either true or false depending on the nearest neighbors count
	 */
	public String predict(AirlineCompositeKey instance, int k) {	
		ArrayList<Double> testData = new ArrayList<Double>();
		List<String> featureNames = AirlineCompositeKey.getFeatureNames();
		for(int i = 0; i < featureNames.size(); i++){
			int instVal = instance.getFeatureValue(featureNames.get(i));
			Double newVal = (instVal - minValueList.get(i)) / (maxValueList.get(i) - minValueList.get(i));
			testData.add(newVal);
		}		
		TreeMap<Double,String> closestValMap = new TreeMap<>();
		for(int i = 0; i < k; i++)	closestValMap.put(100000.0 - i,"NAN");
		for(KNNRecord knnRec : normalizedMatrix){
			double eucDist = 0.0;
			for(int i = 0; i < testData.size(); i++){
				double testFeatureVal = testData.get(i);
				double trainFeatureVal = knnRec.getFeatures().get(i);
				eucDist += Math.pow((testFeatureVal - trainFeatureVal), 2);
			}
			eucDist = Math.sqrt(eucDist);		
			if(eucDist < closestValMap.lastKey()){
				closestValMap.pollLastEntry();
				closestValMap.put(eucDist,knnRec.getLabel());				
			}
		}	
		int trues=0;
		int falses=0;
		for(String value:closestValMap.values()) {

			if(value.equals("FALSE")) 
				falses++;
			else trues++; 
		}
		if(trues < falses) 
			return "FALSE";
		return "TRUE";		
	}
}
