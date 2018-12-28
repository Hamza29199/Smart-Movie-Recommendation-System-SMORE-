import java.io.IOException;
import java.util.ArrayList;


public class NeuralNet{
	
	//these are variables for storing bias for input and hidden layer. They can be changed
	//but we have kept them zero for now i.e. no bias

	double bias = 0.0;
	double bias1 = 0.0;


	//creating the WordVectorizer objects for training, and then testing the system
	static WordVectorizer wv = new WordVectorizer();
	
	static int count=0; //for counting the number of tweets it classifies correctly
	
	static double learning_rate = 0.08;  //setting the rate at which parameters to be updated
	
	static Neuron_Object[] inputLayer,hiddenLayer; //an array of Neuron_Objects for the input
											//and hidden layer
	
	static Neuron_Object outputNeuron; //a single neuron required for computing output
	
	//Constructor sets up the network and populates
	//and populates each layer with specified number of neurons accordingly
	
	NeuralNet(int inputs, int hidden) throws IOException {
		
		inputLayer = new Neuron_Object[inputs + 1];
		
		hiddenLayer = new Neuron_Object[hidden + 1];
		
		//iterating over the input and hidden layers to add neuron
		for(int i=0; i < inputs; i++) {
			
			inputLayer[i] = new Neuron_Object();
			
			
		}
		
		inputLayer[inputs] = new Neuron_Object(bias); 
		
		for(int i=0; i < hidden; i++) {
			
			hiddenLayer[i] = new Neuron_Object();
			
			
		}
		hiddenLayer[hidden] = new Neuron_Object(bias1); 
		
		outputNeuron = new Neuron_Object();
			
		
		//establishing the connection between each neuron in input layer
		//and corresponding neurons in hidden layer via nested for loop
	
	for (int i = 0; i < inputLayer.length; i++) {
		
        for (int j = 0; j < hiddenLayer.length; j++) {

      //makes connection b/w any two neurons in input and hidden layer
     Connections c = new Connections(inputLayer[i],hiddenLayer[j]); 
     
      inputLayer[i].addConnections(c);
      hiddenLayer[j].addConnections(c);
      
        }
    }
	
    
	//establishing the connection between each neuron in hidden layer
			//and the output
    
    for (int i = 0; i < hiddenLayer.length; i++) {
    	
        Connections c = new Connections(hiddenLayer[i] ,outputNeuron);
        
        hiddenLayer[i].addConnections(c);
        
        outputNeuron.addConnections(c);
    }

}  //constructor ends
	
	
	
//so in a feed-forward neural net, there are two main steps; 
//forward propagation(where inputs are multiplied with the weights, summed up and passed forward)
//and backward propagation (the phase where the weights are tweaked as per the error,
//which is the difference between the network's guess and the actual output)
	
	
	double forwardProp(ArrayList<Integer> inputArray) {
		
		for (int i = 0; i < inputArray.size(); i++) {
			
            inputLayer[i].output = inputArray.get(i); 
            
            
        }
        
        //Each neuron in the hidden layer sums up the product
		//of each neuron's input value and weight of the corresponding connection to 
		//produce its output
		
        for (int i = 0; i < hiddenLayer.length-1; i++) {
        	
            hiddenLayer[i].hidden_neuron_guess();
        }

        // Calculate the output of the output neuron
        outputNeuron.output_neuron_guess();
        
        
        // Return the final output
        return (outputNeuron.output);
        
    }
	
	
	static int binaryOutput(double y) {
		
		
		return y > 0.60 ? 1 : 0 ;
		
	}

	//backPropagation phase starts
	
	double backPropagation(ArrayList<Integer>inputs, int correct_ans) {
		
		double forwardPropResult = forwardProp(inputs); //first considers the net's output
		
		double error = (forwardPropResult - correct_ans);
		
		//first we tweak the connections from hidden layer to output neuron
		for(int i=0; i < hiddenLayer.length; i++) {
			
			 Neuron_Object neuronToTweak = hiddenLayer[i]; //we consider the connection of a neuron in hidden layer
            
            double output = neuronToTweak.output;  // we get its result i.e. the input for the output layer
            
          //multiplying by tanh derivative
          //double tweakedWeight =error*(output)*(1-Math.pow(forwardPropResult,2));
           
          double tweakedWeight =error*(output)*(forwardPropResult)*(1-forwardPropResult);
          //double tweakedBias = error* (forwardPropResult)*(1-forwardPropResult);
          
            neuronToTweak.synapses.get(i).updateWeight(learning_rate*tweakedWeight);
                
            
        }
		
		
        // Now we adjust the connections between input layer and hidden layer
		
        for (int i = 0; i < inputLayer.length; i++) {
             
            for(int j=0; j < hiddenLayer.length; j++) {
                    
                    Neuron_Object neuron = inputLayer[i];
                    
                    //deltaWeight= amount by which to update weight
                    
                    double deltaWeight = error* Neuron_Object.relu_deriv_func(neuron.output)*neuron.output;
                  
                    //double deltaBias = error*Neuron_Object.relu_deriv_func(neuron.output); 
                    
                    	neuron.synapses.get(j).updateWeight(learning_rate*deltaWeight);
                         
                    	
                }
            } 

        
        return forwardPropResult;
} //backPropagation class ends here

		
	
	
} //class ends here
	