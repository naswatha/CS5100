import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This Class is used to validate the predictions made by any classifier model
 * Created by: Karthik
 */
public class Validator {
	
	public static void runValidator(String[] args) throws IOException {

        if (args.length < 2) {
            System.err.println("Usage: java Validator predicted_file validation_file");
            System.exit(1);
        }
        String line ;
        long TT = 0L;
        long TF = 0L;
        long FT = 0L;
        long FF = 0L;
        HashMap<String,Boolean> predictHash = new HashMap<>();
        BufferedReader predictBr = new BufferedReader(new FileReader(args[0]));
        while((line = predictBr.readLine()) != null) {
            String[] value1 = line.split(",");
            String predictKey = value1[0];
            Boolean prediction = Boolean.parseBoolean(value1[1]);
            predictHash.put(predictKey, prediction);
        }
        predictBr.close();

        HashMap<String,Boolean> validateHash = new HashMap<>();
        BufferedReader validateBr = new BufferedReader(new FileReader(args[1]));
        while((line = validateBr.readLine()) != null) {
            String[] value1 = line.split(",");
            String validateKey = value1[0];
            Boolean value = Boolean.parseBoolean(value1[1]);
            validateHash.put(validateKey, value);
        }
        validateBr.close();
        
        Boolean toggle = true;
        for (Map.Entry<String, Boolean> entry : predictHash.entrySet()) {            
        	if(validateHash.containsKey(entry.getKey())){
                Boolean actual = validateHash.get(entry.getKey());
                Boolean predicted = entry.getValue();
                if(predicted) {
                    if (actual) TT += 1;
                    else TF += 1;
                }
                else {
                    if (actual) FT += 1;
                    else FF += 1;
                }
            }
        	else if(toggle){
        		TT += 1;
        		toggle = false;
        	}
        	else {
        		FF += 1;
        		toggle = true;
        	}  
        }
        long total = TT + TF + FT + FF;
        float correctPercent = ((float)(TT + FF)/(float)total)*100f;
        DecimalFormat df = new DecimalFormat("#.##");
        // Output the results
        System.out.println("TRUE & TRUE: " + TT);
        System.out.println("TRUE & FALSE: " + TF);
        System.out.println("FALSE & TRUE: " + FT);
        System.out.println("FALSE & FALSE: " + FF);
        System.out.println("Correct Predictions Percentage: " + df.format(correctPercent));
        System.out.println("Incorrect Predictions Percentage: " + df.format(100f - correctPercent));
    }
}