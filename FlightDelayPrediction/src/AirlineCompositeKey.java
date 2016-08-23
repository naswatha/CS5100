import org.apache.commons.lang.text.StrTokenizer;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Naveen
 * @description: This class is a writable having details of the factors used for airline delay prediction
 */
public class AirlineCompositeKey implements WritableComparable<AirlineCompositeKey> {


	public  int day;
	public  int dayOfWeek;
	public  int date;
	public  int flno;
	public  int orgid;
	public  int destid;
	public  int crsArrTime;
	public  int crsDepTime;
	public  int crsElapTime;
	public  int distance;
	public  String isDelayed;

	/**
	 * @description: Default constructor
	 */
	public AirlineCompositeKey(){}

	/**
	 * @description: Parameterized constructor for data from dataset.
	 * @param: data from file.
	 * @return: AirlineCompositeKey object with assigned values.
	 */
	public AirlineCompositeKey(String data) {
		StrTokenizer t = new StrTokenizer(data,',','"');
		t.setIgnoreEmptyTokens(false);
		String[] line = t.getTokenArray();
		this.day = Integer.parseInt(line[Constants.DAYOFMONTH]);
		this.dayOfWeek = Integer.parseInt(line[Constants.DAYOFWEEK]);
		this.date = Integer.parseInt(line[Constants.DATE]);
		this.flno = Integer.parseInt(line[Constants.FLNO]);
		this.orgid = Integer.parseInt(line[Constants.ORGID]);
		this.destid = Integer.parseInt(line[Constants.DESTID]);
		this.crsArrTime = Integer.parseInt(line[Constants.CRSARRTIME]);
		this.crsDepTime = Integer.parseInt(line[Constants.CRSDEPTIME]);
		this.crsElapTime = Integer.parseInt(line[Constants.CRSELAPTIME]);
		this.distance = Integer.parseInt(line[Constants.DISTANCE]);
		this.isDelayed = line[Constants.ISDELAYED];

	}
	/**
	 * @description: Compare to method required to sort object based on date.
	 * @param: AirlineCompositeKey airlineCompositeKey
	 * @return: object with highest date.
	 */
	@Override
	public int compareTo(AirlineCompositeKey airlineCompositeKey) {
		if (date >= airlineCompositeKey.date) {
			return 1;
		}
		return 0;
	}
	/**
	 * @description: write data to hdfs.
	 * @param: DataOutput data to be written
	 * @return: void
	 */
	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(day);
		dataOutput.writeInt(dayOfWeek);
		dataOutput.writeInt(date);
		dataOutput.writeInt(flno);
		dataOutput.writeInt(orgid);
		dataOutput.writeInt(destid);
		dataOutput.writeInt(crsArrTime);
		dataOutput.writeInt(crsDepTime);
		dataOutput.writeInt(crsElapTime);
		dataOutput.writeInt(distance);
		dataOutput.writeUTF(isDelayed);


	}
	/**
	 * @description: read data from hdfs
	 * @param: DataInput input data from hdfs
	 * @return: void
	 */
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		day = dataInput.readInt();
		dayOfWeek = dataInput.readInt();
		date = dataInput.readInt();
		flno = dataInput.readInt();
		orgid = dataInput.readInt();
		destid = dataInput.readInt();
		crsArrTime = dataInput.readInt();
		crsDepTime = dataInput.readInt();
		crsElapTime = dataInput.readInt();
		distance = dataInput.readInt();
		isDelayed = dataInput.readUTF();
	}
	/**
	 * @description: to list all the features required for model
	 * @param: 
	 * @return: list of feature names
	 */
	public static List<String> getFeatureNames(){
		List<String> names = new ArrayList<String>();
		names.add("DAY");
		names.add("DAYOFWEEK");
		names.add("DATE");
		names.add("FLNO");
		names.add("ORGID");
		names.add("DESTID");
		names.add("CRSARRTIME");
		names.add("CRSDEPTIME");
		names.add("CRSELAPTIME");
		names.add("DISTANCE");
		return names;
	}
	/**
	 * @description: get value of the feature given name.
	 * @param: String name feature name
	 * @return: int data
	 */
	public int getFeatureValue(String name){
		int value = 1;
		switch(name){
		case "DAY" : value = day;
		break;
		case "DAYOFWEEK" : value = dayOfWeek;
		break;
		case "DATE" : value = date;
		break;
		case "FLNO" : value = flno;
		break;
		case "ORGID" : value = orgid;
		break;
		case "DESTID" : value = destid;
		break;
		case "CRSARRTIME" : value = crsArrTime;
		break;
		case "CRSDEPTIME" : value = crsDepTime;
		break;
		case "CRSELAPTIME" : value = crsElapTime;
		break;
		case "DISTANCE" : value = distance;
		break;
		default : value = 1;
		}

		return value;
	}
}
