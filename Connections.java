
import java.util.Random;

//this class defines a Connection object to link any two neurons; one "from"
//which the connection starts, and one "to" which it goes

public class Connections{
	
	Neuron_Object from;
	
	Neuron_Object to;
	
	static double weight = 0;
	
	
	//parameterized constructor which defines the from and to neurons, and randomly
	//initializes weight of the connection b/w them
	Connections(Neuron_Object source, Neuron_Object dest){
		
		from = source;
		
		to = dest;
		
		weight = (new Random().nextDouble() - 0.45);

	}
	
	//our error-correcting mechanism is gradient-descent i.e. in training phase, the
	//in case of error, the weights of each connection get updated according
	//to the parameter (which is the product of error and current input)
	static double updateWeight(double update) {
		
		return weight-=update;
		
	}
	
	double getWeight() {
		
		
		return weight;
	}
	
	
}
