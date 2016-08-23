import java.util.Arrays;

public class Selector {

	public static void main(String[] args) throws Exception {
		
		String select = args[0];
		
		String[] arguments = Arrays.copyOfRange(args, 1, args.length);
		switch(select){
		
		case "Naive" : AirlineNaive.runNaive(arguments);
		break;
		case "KNN" : AirlineKNN.runKNN(arguments);
		break;
		case "WekaNaive" : AirlineWekaNaive.runWekaNaive(arguments);
		break;
		case "WekaKNN" : AirlineWekaKNN.runWekaKNN(arguments);
		break;
		default: System.out.println("Please provide valid arguments");
		System.exit(-1);
		}
	}

}
