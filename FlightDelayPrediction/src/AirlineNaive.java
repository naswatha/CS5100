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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * AirlineNaive class is used to predict airline delays using 
 * my own implementation of Naive Bayes algorithm
 * Created by Karthik 
 */
public class AirlineNaive {

    public static void runNaive(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Provide correct number of command line arguments");
            System.exit(1);
        }
        Configuration conf = new Configuration();
        // Set the text output separator from the reducer to comma
        conf.set("mapreduce.output.textoutputformat.separator", ",");

        // First MapReduce job generates the model
        Job job = Job.getInstance(conf, "Airline Naive");
        job.setJarByClass(AirlineNaive.class);
        job.setMapperClass(AirlineNaiveMapper.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(AirlineCompositeKey.class);
        job.setReducerClass(AirlineNaiveReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        if (!job.waitForCompletion(true)) {
            System.out.println("First MR job failed");
            System.exit(-1);
        }

        // Second MapReduce job tests the model
        Job job2 = Job.getInstance(conf, "Airline Naive MR 2");
        job2.setJarByClass(AirlineNaive.class);
        job2.setMapperClass(AirlineNaiveMapper2.class);
        job2.setMapOutputKeyClass(IntWritable.class);
        job2.setMapOutputValueClass(AirlineCompositeKey.class);
        job2.setReducerClass(AirlineNaiveReducer2.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job2, new Path(args[2]));
        FileOutputFormat.setOutputPath(job2, new Path(args[3]));
       
        if (!job2.waitForCompletion(true)) {
            System.out.println("Second MR job failed");
            System.exit(-1);
        }
        
        System.out.println("===================================================================");
        System.out.println("Validation Report with Confusion Matrix for Naive Bayes");
        System.out.println("===================================================================");
        Validator.runValidator(new String[] {"outputFinal/part-r-00000","Validate/98validate.csv"});
        System.out.println("===================================================================");
        System.exit(0);
    }

    /**
    * Mapper class of the first MapReduce job
    * Reads the train dataset, creates an AirlineDetails object after removing fields that are not needed
    * Writes the month(key) and the AirlineCompositeKey object (value) to the context.
    */
    static class AirlineNaiveMapper extends Mapper<LongWritable, Text, IntWritable, AirlineCompositeKey> {

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
    * Reducer class of the first MapReduce job
    * Generates the model for the given training dataset
    */
    static class AirlineNaiveReducer extends Reducer<IntWritable, AirlineCompositeKey, IntWritable, IntWritable> {

        @Override
        public void reduce(IntWritable key, Iterable<AirlineCompositeKey> values, Context context) throws IOException, InterruptedException {
        	NaiveBayesClassifier nbc = new NaiveBayesClassifier();        	
            // Use the iterable on AirlineCompositeKey and create the dataset
            for (AirlineCompositeKey value : values) {
            	nbc.addInstance(value);
            }
            nbc.buildClassifier(); 
            try {
                // Get the filesystem object from the configuration of the context
                Configuration conf = context.getConfiguration();
                FileSystem fs = FileSystem.get(conf);
                // Write the reducer output to a file on the HDFS
                FSDataOutputStream out = fs.create(new Path(key.toString()+".txt"));
                ObjectOutputStream oStream = new ObjectOutputStream(out);
                oStream.writeUTF(nbc.getModel().toString());
                oStream.flush();
                oStream.close();
            } catch (Exception e) {
                System.err.println("Exception during save model: "+e.getMessage());
            }
            context.write(key, key);
        }
    }

    /**
    * Mapper class for the second mapreduce job
    * Reads the test dataset and removes fields that are not needed
    */
    static class AirlineNaiveMapper2 extends Mapper<LongWritable, Text, IntWritable, AirlineCompositeKey> {

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
    * Reducer for the second MR job
	* Loads the Naive Bayes classifier model and predicts flight delays for the test data
    */
    static class AirlineNaiveReducer2 extends Reducer<IntWritable, AirlineCompositeKey, Text, Text> {

        @Override
        public void reduce(IntWritable key, Iterable<AirlineCompositeKey> values, Context context) throws IOException, InterruptedException {

            // Get the filesystem object from the configuration
            Configuration conf = context.getConfiguration();
            FileSystem fs = FileSystem.get(conf);

            // Read the model file from the filesystem
            FSDataInputStream in = fs.open(new Path(key.toString()+".txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            String model = objectInputStream.readUTF();
            NaiveBayesClassifier nbc = new NaiveBayesClassifier(model);
            objectInputStream.close();
                
            for (AirlineCompositeKey value : values) {
                String pred = nbc.predict(value);
                String contextKey = value.flno
                       + "_" + getFormattedDate(value.date)
                       + "_" + getFormattedTime(value.crsDepTime);
                context.write(new Text(contextKey),new Text(pred));
            }
        }

		/**
         * Get the formatted time from the integer
         * @param value integer time to be formatted
         * @return formatted string representing the time
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
         * Get the formatted date from the integer
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
