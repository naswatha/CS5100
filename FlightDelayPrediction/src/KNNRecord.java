import java.util.ArrayList;
import java.util.List;

/**
 *@description: KNN record object used to store the model with train data for 
 *finding out the euclidian distance and retain the class.
 *@author Naveen
 */
public class KNNRecord {
	@Override
	public String toString() {
		return "KNNRecord [features=" + features.toString() + ", label=" + label + "]";
	}
	private List<Double> features;
	private String label;

	public KNNRecord(){
		this.features = new ArrayList<Double>();
		this.label = "NAN";
	}

	public KNNRecord(List<Double> features, String label) {
		this.features = features;
		this.label = label;
	}
	public List<Double> getFeatures() {
		return features;
	}
	public void setFeatures(List<Double> features) {
		this.features = features;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
