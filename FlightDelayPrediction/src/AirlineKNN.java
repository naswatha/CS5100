import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 *@description: Two Mapreduce job implementing the  KNN algorithm to predict the 
 *flight delay.
 *@author Karthik, Naveen
 *
 */
public class AirlineKNN {

	/**
	 *@description: Main function running two mapreduce job for train and test.
	 *@param: String[] args link to files 
	 *@return void
	 */
    public static void runKNN(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Provide correct number of command line arguments");
            System.exit(1);
        }
        Configuration conf = new Configuration();
        // Set the text output separator from the reducer to comma
        conf.set("mapreduce.output.textoutputformat.separator", ",");

        // First MapReduce job generates the model
        Job job = Job.getInstance(conf, "Airline Weka");
        job.setJarByClass(AirlineKNN.class);
        job.setMapperClass(AirlineKNNMapper.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(AirlineCompositeKey.class);
        job.setReducerClass(AirlineKNNReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        if (!job.waitForCompletion(true)) {
            System.out.println("First MR job failed");
            System.exit(-1);
        }

        // Second MapReduce job tests the model
        Job job2 = Job.getInstance(conf, "Airline Weka MR 2");
        job2.setJarByClass(AirlineKNN.class);
        job2.setMapperClass(AirlineKNNMapper2.class);
        job2.setMapOutputKeyClass(IntWritable.class);
        job2.setMapOutputValueClass(AirlineCompositeKey.class);
        job2.setReducerClass(AirlineKNNReducer2.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job2, new Path(args[2]));
        FileOutputFormat.setOutputPath(job2, new Path(args[3]));
        if (!job2.waitForCompletion(true)) {
            System.out.println("Second MR job failed");
            System.exit(-1);
        }
        
        System.out.println("===================================================================");
        System.out.println("Validation Report with Confusion Matrix for k Nearest Neighbors:");
        System.out.println("===================================================================");
        Validator.runValidator(new String[] {"outputFinal/part-r-00000","Validate/98validate.csv"});
        System.out.println("===================================================================");
        System.exit(0);
    }

    /**
	 *@description: Mapper class of the first MapReduce job. Reads the train dataset, creates an AirlineDetails object 
	 *after removing fields that are not needed. Writes the month(key) and the AirlineCompositeKey object (value) to the context.
	 */
    static class AirlineKNNMapper extends Mapper<LongWritable, Text, IntWritable, AirlineCompositeKey> {

        @Override
        public void map(LongWritable key, Text value, org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {
            String line = value.toString();
            AirlineDetails flightDetails = AirlineDetails.createObject(line);
            // If there is a parse exception or if the flight is insane, return
            if (flightDetails == null || !flightDetails.isSane()) return;
            context.write(new IntWritable(flightDetails.month),new AirlineCompositeKey(flightDetails.getFactors()));
        }
    }

    /**
	 *@description: Reducer class of the first MapReduce job
	 *Generates the model for the given training dataset
	 */
    static class AirlineKNNReducer extends Reducer<IntWritable, AirlineCompositeKey, IntWritable, IntWritable> {

        @Override
        public void reduce(IntWritable key, Iterable<AirlineCompositeKey> values, Context context) throws IOException, InterruptedException {

        	KNNClassifier knn = new KNNClassifier();        	
            // Use the iterable on AirlineCompositeKey and create the dataset
            for (AirlineCompositeKey value : values) {
            	knn.addInstances(value);
            }

            knn.buildClassifier();
            
            List<KNNRecord> normalizedData = knn.getNormalizedMatrix();
            StringBuilder writeData = new StringBuilder();
            for(KNNRecord knnRec : normalizedData){            	
            	
            	int commaCounter = 0;
            	for(Double val : knnRec.getFeatures()){
            		commaCounter++;
            		writeData.append(val);
            		if(commaCounter < knnRec.getFeatures().size()) writeData.append(",");
            	}
            	writeData.append("@").append(knnRec.getLabel());            	
            	writeData.append("#");
                
            }
           
                                    
            File file = new File("knnModel.txt");
            if (!file.exists()) {
				file.createNewFile();
			}
            
            String writeNewData = writeData.toString();
            writeNewData = writeNewData.substring(0, writeNewData.length() - 1);
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(writeNewData);
			bw.close();
            
            Configuration conf = context.getConfiguration();
            FileSystem fs = FileSystem.get(conf);
            
            FSDataOutputStream outMax = fs.create(new Path("max.txt"));
            ObjectOutputStream oStreamMax = new ObjectOutputStream(outMax);
            oStreamMax.writeUTF(knn.maxListToString());
            oStreamMax.flush();
            oStreamMax.close();
            
            FSDataOutputStream outMin = fs.create(new Path("min.txt"));
            ObjectOutputStream oStreamMin = new ObjectOutputStream(outMin);
            oStreamMin.writeUTF(knn.minListToString());
            oStreamMin.flush();
            oStreamMin.close();            
            
            context.write(key, key);
        }

    }
    
    

    /**
	 *@description: Mapper class for the second mapreduce job
	 *Reads the test dataset and removes fields that are not needed
	 */
    static class AirlineKNNMapper2 extends Mapper<LongWritable, Text, IntWritable, AirlineCompositeKey> {

        @Override
        public void map(LongWritable key, Text testValue, org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {

            String line = testValue.toString();

            // Create an AirlineFlightDetails object
            AirlineDetails testFlightDetails = AirlineDetails.createTestObject(line);

            if (testFlightDetails == null) return;

            // Writes the month(key) and the stringified AirlineDetails object (value) to the context
            context.write(new IntWritable(testFlightDetails.month),new AirlineCompositeKey(testFlightDetails.getFactors()));

        }

    }

    /**
	 *@description: Reducer for the second MR job
	 */
    static class AirlineKNNReducer2 extends Reducer<IntWritable, AirlineCompositeKey, Text, Text> {

    	
        @Override
        public void reduce(IntWritable key, Iterable<AirlineCompositeKey> values, Context context) throws IOException, InterruptedException {
        	int k = 5;

                // Get the filesystem object from the configuration
                Configuration conf = context.getConfiguration();
                FileSystem fs = FileSystem.get(conf);                
                
                FSDataInputStream inMin = fs.open(new Path("min.txt"));
                ObjectInputStream objectInputStreamMin = new ObjectInputStream(inMin);
                String min = objectInputStreamMin.readUTF();
                List<Double> minList = KNNClassifier.strToList(min);
                objectInputStreamMin.close();
                
                FSDataInputStream inMax = fs.open(new Path("max.txt"));
                ObjectInputStream objectInputStreamMax = new ObjectInputStream(inMax);
                String max = objectInputStreamMax.readUTF();
                List<Double> maxList = KNNClassifier.strToList(max);
                objectInputStreamMax.close();                
                String model = readFile("knnModel.txt");
                KNNClassifier knn = new KNNClassifier(model,minList,maxList);
                
                for (AirlineCompositeKey value : values) {
                	String pred = knn.predict(value,k);
                	String contextKey = value.flno
                            + "_" + getFormattedDate(value.date)
                            + "_" + getFormattedTime(value.crsDepTime);
                    context.write(new Text(contextKey),new Text(pred));
                }
        }

        /**
    	 *@description: read contents of file
    	 *@param: String filename name of file
    	 *@return: String contents of file.
    	 */
        public static String readFile(String fileName) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                return sb.toString();
            } finally {
                br.close();
            }
        }

        /**
    	 *@description: get the formatted time from the data.
    	 *@param: int value of the time.
    	 *@return: String equivalent of the time
    	 */
		private String getFormattedTime(int value) {
            int hours = value/60;
            int minutes = value%60;
            int hLen = String.valueOf(hours).length();
            int mLen = String.valueOf(minutes).length();
            String hStr = String.valueOf(hours);
            String mStr = String.valueOf(minutes);

            if (hLen < 2) {
                hStr = "0"+hStr;
            }
            if (mLen < 2) {
                mStr = "0"+mStr;
            }

            String finalTime = hStr+mStr;
            if (finalTime.equals("2400")) finalTime = "0000";

            return finalTime;

        }


        /***
         * @description Get the formatted date from the integer
         * @param value integer date to be formatted
         * @return formatted string representing the date
         */
        private static String getFormattedDate(int value) {
            String date = String.valueOf(value);
            String year = date.substring(0,4);
            String month = date.substring(4,6);
            String day = date.substring(6);
            return (year+"-"+month+"-"+day);
        }
    }

}
