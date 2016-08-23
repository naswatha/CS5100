import java.util.*;

/**
 * This Class uses the Naive Bayes algorithm to build a classifier model 
 * and predict class labels based on the model built.
 * Created by: Karthik
 */
public class NaiveBayesClassifier {

	private long trueCounter;
	private long falseCounter;
	private long totalCounter;
	private HashMap<String,ArrayList<Integer>> featureMap;
	private HashMap<String,String> predictMap;
	private StringBuilder model;
	private double trueProb;
	private double falseProb;	
	
	/**
	* Default Constructor used to initialize all fields 
	*/
	public NaiveBayesClassifier(){
		this.trueCounter = 0;
		this.falseCounter = 0;
		this.totalCounter = 0;
		this.trueProb = 0.0;
		this.falseProb = 0.0;
		this.model = new StringBuilder();
		this.featureMap = new HashMap<String,ArrayList<Integer>>();
	}
	
	/**
	* Parametrized Constructor used to load a NaiveBayesClassifier from a string
	* @param: predictModel The string using which a NaiveBayesClassifier is created.
	*/
	public NaiveBayesClassifier(String predictModel) {
		this();		
		this.predictMap = new HashMap<String,String>();
		String splits[] = predictModel.split(":");
		String probs[] = splits[1].split(",");
		trueProb = Double.parseDouble(probs[0]);
		falseProb = Double.parseDouble(probs[1]);

		String records[] = splits[0].split("#");
		for(String rec : records){
			String fields[] = rec.split(",");
			String key = fields[0]+","+fields[1];		
			String value = fields[2]+","+fields[3];
			predictMap.put(key,value);
		}
		
	}

	public long getTrueCounter() {
		return trueCounter;
	}

	public void setTrueCounter(long trueCounter) {
		this.trueCounter = trueCounter;
	}

	public long getFalseCounter() {
		return falseCounter;
	}

	public void setFalseCounter(long falseCounter) {
		this.falseCounter = falseCounter;
	}

	public long getTotalCounter() {
		return totalCounter;
	}

	public void setTotalCounter(long totalCounter) {
		this.totalCounter = totalCounter;
	}

	public HashMap<String, ArrayList<Integer>> getFeatureMap() {
		return featureMap;
	}

	public void setFeatureMap(HashMap<String, ArrayList<Integer>> featureMap) {
		this.featureMap = featureMap;
	}
	
	public StringBuilder getModel() {
		return model;
	}

	public void setModel(StringBuilder model) {
		this.model = model;
	}

	/**
	* This method is used to extract the features in the given object and store it in a map.
	* @param: instance The AirlineCompositeKey object from which the features are extracted.
	*/
	public void addInstance(AirlineCompositeKey instance) {
		this.totalCounter++;
		if(instance.isDelayed.equals("TRUE"))	this.trueCounter++;
		else	this.falseCounter++;
		
		List<String> featureNames = AirlineCompositeKey.getFeatureNames();
		for(String feature : featureNames){
			String key = feature+","+instance.isDelayed;
			if(featureMap.containsKey(key)){
				ArrayList<Integer> values = featureMap.get(key);
				values.add(instance.getFeatureValue(feature));
				featureMap.put(key,values);
			}
			else{
				ArrayList<Integer> values = new ArrayList<Integer>();
				values.add(instance.getFeatureValue(feature));
				featureMap.put(key, values);
			}
		}
	}

	/**
	* This method computes the Mean and Standard Deviation for each (feature,label) combination
	*/
	public void buildClassifier() {
		int counter = 0;
		for (Map.Entry<String, ArrayList<Integer>> entry : featureMap.entrySet()){
			counter++;			
			model.append(entry.getKey());
			ArrayList<Integer> values = entry.getValue();
			long count = 0;
			double sum = 0.0;
			for(Integer value : values){
				count++;
				sum += value;
			}
			double mean = sum/count;
			
			//Standard Deviation
			double sdSum = 0.0;
			for(Integer value : values){
				double ent = value - mean;
				sdSum += Math.pow(ent, 2);
			}
			sdSum = sdSum/(count - 1);
			double sdFinal = Math.sqrt(sdSum);  
			if(sdFinal <= 0.0){
				sdFinal = 1.0;
			}
			model.append(",").append(mean).append(",").append(sdFinal);			
			if(counter < AirlineCompositeKey.getFeatureNames().size() * 2)
				model.append("#");
		}		
		model.append(":").append((double)trueCounter/totalCounter).append(",").append((double)falseCounter/totalCounter);		
	}

	/**
	* This method predicts is the given flight will be delayed or not.
	* @params: instance The AirlineCompositeKey object whose class label has to be predicted
	* @returns: TRUE or FALSE based on whether the flight would be delayed or not.
	*/
	public String predict(AirlineCompositeKey instance) {
		List<String> featureNames = AirlineCompositeKey.getFeatureNames();
		double truePrediction = trueProb;
		double falsePrediction = falseProb;
		for(String name : featureNames){
			String key1 = name+",TRUE";
			String value1[] = predictMap.get(key1).split(",");
			double mean = Double.parseDouble(value1[0]);
			double sd = Double.parseDouble(value1[1]);
			int x = instance.getFeatureValue(name);
			double probXByC = getNormalDist(mean,sd,x);
			truePrediction *= probXByC;
			
			String key2 = name+",FALSE";
			String value2[] = predictMap.get(key2).split(",");
			double mean2 = Double.parseDouble(value2[0]);
			double sd2 = Double.parseDouble(value2[1]);
			int x2 = instance.getFeatureValue(name);
			double probXByC2 = getNormalDist(mean2,sd2,x2);
			falsePrediction *= probXByC2;
		}		
		if(truePrediction > falsePrediction) return "TRUE";
		return "FALSE";		
	}
	
	/**
	* This method computes the normal distribution of a given value.
	* @params: mean The mean value
	* @params: sd The Standard deviation value
	* @params: vaue The value whose normal distribution has to be computed
	* @returns: The normal distribution of a given value
	*/
	public static double getNormalDist(double mean, double sd, int value){
		double a = (value - mean)/sd;
		double b = Math.pow(a, 2) * (-0.5);
		double c = Math.exp(b);
		double d = 1/ (Math.sqrt(2*Math.PI) * sd);
		return c * d;
	}
}
