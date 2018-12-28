import java.io.IOException;
import java.util.*;


public class RUNCLASS {

    public static ArrayList<String> reviewDict = new ArrayList<String>();
    public static Set<String> reviewDictSet = new HashSet<String>();
    public static ArrayList<String> stopWords_movie = new ArrayList<String>();

    public static void main(String[] args) {

        NeuralNet nn;
        try {
            NeuralNet.wv.trainDataReader("reviewDSCSV.csv");

        //initializing neural net with vocab size input layer neurons, and 10 hidden layer neurons
        nn = new NeuralNet(NeuralNet.wv.bagOfWords.size() , 5);

        System.out.println("\nInitializing training....");


        //for 10 epochs i.e. views the entire training data 10 times
        for(int j=0; j < 10; j++) {

            for(int i = 0; i < NeuralNet.wv.input_matrix.size(); i++) {

                nn.backPropagation(NeuralNet.wv.input_matrix.get(i),  NeuralNet.wv.label_array.get(i));


            }

            System.out.println("Completed an epoch!!!");



        }
        //training via back propagation ends after 10 epochs, or iterations

        System.out.println("\nFinished training!!");
        Thread.sleep(4000);
        //now testing

        System.out.println("INITIALIZING TESTING");



        NeuralNet.wv.testDataReader("reviewDSCSVtest.csv");



        for(int i = 0; i < NeuralNet.wv.test_matrix.size(); i++) {


            double result = NeuralNet.binaryOutput(nn.forwardProp(NeuralNet.wv.test_matrix.get(i)));


            double error = 0.5 * Math.pow(result - NeuralNet.wv.test_label_array.get(i), 2);

            System.out.println(result);

            System.out.printf("--->ERROR: %.2f   GUESS: %.2f    ACTUAL: %d\n", error, result, NeuralNet.wv.test_label_array.get(i));

            //doing this because the accuracy we would like to see for each text is above 85%

            if(error ==0.0) {

                NeuralNet.count++;

               /* BufferedWriter bfw = new BufferedWriter(new FileWriter(new File("weights.txt")));
                bfw.flush(new Connections(NeuralNet.inputLayer[0], NeuralNet.hiddenLayer[0]).getWeight());
                bfw.newLine();*/
            }

        }



        //accuracy displayed based on how many times it classifies a sentence correctly
        String result = "ACCURACY (% of reviews correctly classified): " + (NeuralNet.count)+ "%";



        System.out.println(result);

        double result1;
        while(true) {

            Scanner sc1 = new Scanner(System.in);
            System.out.println("Enter the movie Name: ");
            String movieName = sc1.nextLine();

            if(movieName.equals("Finish"))
                break;

            String enteredTEXT = SCRAPE.getDATA(movieName);
            System.out.println(enteredTEXT);

            result1 = nn.forwardProp(nn.wv.wordVectorizer(enteredTEXT));

            System.out.println((result1 > 0.60 ? "Positive" : "Negative"));

            if(result1 > 0.60){


                reviewDictSet.add(movieName);

            }



        }

        Iterator ist = reviewDictSet.iterator();


        while(ist.hasNext()){

            reviewDict.add(ist.next().toString());

        }

        WordVectorizer.stopWordsReader(stopWords_movie, "stopwords.txt");
        reviewDict.removeAll(stopWords_movie);

        RadixSort.sort(reviewDict);

        System.out.println("///// MOVIES OF THE YEAR //////");
        System.out.println();

        for(int j=0; j < reviewDict.size(); j++){

            System.out.println(reviewDict.get(j));


        }

        //RadixSort.sort(reviewDict);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
