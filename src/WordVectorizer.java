import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//following interface defines method signatures which every text-to-vector class/app needs to implement, albeit
//they might wanna do it in different ways. 

//Text cleaning, forming a bag of words ( essentially a vocab of words with corresponding frequency) and converting
//text to a vector based on the vocab are all methods that every text classification needs to implement in different
//depending on requirements, so we keep them in an interface

interface text2Matrix {
	
	String TextCleaner(String line);
	
	void bagOfWordsCreator(String line);
	
	ArrayList<Integer> wordVectorizer(String line);
	
}

public class WordVectorizer implements text2Matrix {
	
	public ArrayList<Integer> label_array = new ArrayList<Integer>();//1-d array storing pos/neg labels for testing
	
	public ArrayList<Integer> test_label_array = new ArrayList<Integer>();
	public ArrayList<ArrayList<Integer>> test_matrix = new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<Integer>> input_matrix = new ArrayList<ArrayList<Integer>>(); //forming a matrix of word vecs
	
     ArrayList<String> wordsList = new ArrayList<String>(); //storing all the words
	
	 ArrayList<String> stopWords = new ArrayList<String>(); //storing stop words
	
	public Set<String> bagOfWords = new HashSet<String>(); //storing unqiue instances of
															//words for vocab, we used set
	
	//lines and test_lines store positive and negative tweets respectively
	static ArrayList<String> lines = new ArrayList<String>(); 
	static ArrayList<String> test_lines = new ArrayList<String>();

	
	//simply reads in a file of data line by line, cleans and tokenizes each line, creates a bag of words model
	//and uses the former to make a vector for each line so that it can be fed to a neural net
	
	 void trainDataReader(String train_path) throws IOException {
		
		stopWordsReader(stopWords, "stopwords.txt");
		 
		
		File csv = new File(train_path); //creating File object to hold .csv training data
		
		FileReader fr = new FileReader(csv); //FileNotoundException thrown to deal with exception here
		
		BufferedReader bfr = new BufferedReader(fr);
		
		try {
			
		System.out.println("TWEETS\n");
		for(String line; (line=bfr.readLine())!= null;) {
			
			lines.add(TextCleaner(line.split(",")[0].toLowerCase()));

			label_array.add(Integer.parseInt(line.split(",")[1]));
			
			bagOfWordsCreator(TextCleaner(line.split(",")[0].toLowerCase()));
			
			System.out.println(TextCleaner(line.split(",")[0].toLowerCase()));
			
			
		}
		
		wordRemover(); //calling this method here to remove unnecessary words from vocab
		
		
		for(String l: lines) {
			
			input_matrix.add(wordVectorizer(l));
		}
		
		}

		
		catch(IOException ioe){
			
			System.err.println("Unexpected issue while reading:\n\n" + ioe);
			
		}
		
		catch(Exception e){
			
			System.err.println("Unexpected general issue:\n\n" + e);
			
		}
		
		//closing the filereader and bufferedreader objects in finally block
		
		finally {
			
			try{
				fr.close(); 
				bfr.close();	}
			
			catch(Exception e) {
				
				System.err.println("Exception while closing: " + e);
			}
			
			}
		
		
		}  //trainingDataReader method ends
	 
	 
	 
	 
	 void testDataReader(String test_path)throws IOException {
		 
		 
		 File csv = new File(test_path); 
			
			FileReader fr = new FileReader(csv); 
			
			BufferedReader bfr = new BufferedReader(fr);
			
				
			System.out.println("MOVIE REVIEWS\n");
			for(String line; (line=bfr.readLine())!= null;) {
				
				test_lines.add(TextCleaner(line.split(",")[0].toLowerCase()));
				
				test_label_array.add(Integer.parseInt(line.split(",")[1]));
				
				System.out.println(TextCleaner(line.split(",")[0].toLowerCase()));
				
				
			}

		 int count =0;
			for(String l: test_lines) {

				test_matrix.add(wordVectorizer(l));
				System.out.println("--->" + count);

				count++;
			}
			
	 } //testDataReader method ends
	
	
	//method below to remove RT(retweet symbol), https links and other unnecessary characters
	//including html encodings(quot; or amp;)from the tweet to clean it
	
	public String TextCleaner(String text) {

	    return text.replaceAll("[!#$%^&*?,.;?\"0-9/;():-]", "").replace("RT", "")
				.replaceAll("http.*?\\s", "").replaceAll("@.*?\\s", "").replaceAll("www.*?\\s", "")
				.replace("quot", "").replace("amp", "");
	}
	
	
	//splits a line of text into tokens, adds them to the Set type field bagOfWords to obtain a vocabulary of unique
	//words, and also adds them to ArrayList type field wordsList to obtain frequency of each word in the vocabulary 
	
	public void bagOfWordsCreator(String line){
		
		String[] tokens = line.trim().split("\\s+");
		
		for(int i=0; i < tokens.length; i++) {
			
			bagOfWords.add(tokens[i]);
			wordsList.add(tokens[i]);
			
		}
		
		
	}
	
	
	//this removes all the stopwords and highly infrequent words
	public void wordRemover() {
		
		for(int i=0; i < wordsList.size(); i++) {
			
			if((Collections.frequency(wordsList, wordsList.get(i)) <= 5) || wordsList.get(i).equals(" ")==true) {
				
				bagOfWords.remove(wordsList.get(i));
				
			}
			
		}
		
		bagOfWords.removeAll(stopWords);
		
		
	}
	
	
	//following method reads all the "stopwords" i.e. words that add no meaning to a sentence like "a" or "the",
	//and stores them in a the List type field variable stopWords.

	static void stopWordsReader(ArrayList<String> ar, String path) throws IOException {
		
		File file = new File(path);
		FileReader fr = new FileReader(file);
		BufferedReader bfr = new BufferedReader(fr);
		
		String stopWords = "";
		
		while((stopWords=bfr.readLine()) != null) {
			
			ar.add(stopWords);
			
		}
		bfr.close();
	}
	
	//following method takes in a line of text and converts it into a vector for feeding into a neural net. We have chosen
	//to form a binary vector i.e. it consists of either 1 (indicating presence of a word in vocab) or 0 (indicating absence)
	
	public ArrayList<Integer> wordVectorizer(String line) {
		
		String[] tweet = line.split("\\s+");
		
		//initializes a vector with length of vocabulary and populates it with zeroes
		ArrayList<Integer> vector = new ArrayList<Integer>(Collections.nCopies(bagOfWords.size(),0));
		
		for(int i=0; i < tweet.length; i++) {
			
			if(bagOfWords.contains(tweet[i])) {
			
				vector.set(new ArrayList<String>(bagOfWords).indexOf(tweet[i]), 1 );
			}
			
		}
		
		return vector;
	}
	
	

}