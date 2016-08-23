knn: 
	java -Xms1024m -Xmx4096m -jar FlightDelayPred.jar KNN KNNTrainFiles outputInit KNNTestFiles outputFinal
	rm -rf outputInit
	rm -rf outputFinal
	rm knnModel.txt min.txt max.txt

naive:
	java -Xms1024m -Xmx4096m -jar FlightDelayPred.jar Naive NaiveTrainFiles outputInit NaiveTestFiles outputFinal
	rm -rf outputInit
	rm -rf outputFinal
	rm {1,2,3,4,5,6,7,8,9,10,11,12}.txt

wekaknn:
	java -Xms1024m -Xmx4096m -jar FlightDelayPred.jar WekaKNN KNNTrainFiles outputInit KNNTestFiles outputFinal
	rm -rf outputInit
	rm -rf outputFinal
	rm *.model

wekanaive:
	java -Xms1024m -Xmx4096m -jar FlightDelayPred.jar WekaNaive NaiveTrainFiles outputInit NaiveTestFiles outputFinal
	rm -rf outputInit
	rm -rf outputFinal
	rm *.model
