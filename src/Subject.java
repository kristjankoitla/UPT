import java.util.*;


public class Subject implements java.io.Serializable{
    //todo also share stats and neurons from subject to copy.

    public float fitness;
    //ws - weight set
    //np - neuron population
    //nc - neuron count, not same as population. used fod input and output neurons

    private int np[];
    private double[][][] weights;
    private double[][] neurons;
    //following two variables are not weightIndex'
    private int[] wsSize;
    private int[] wsSizeCumulative;

    private int[][] weightIndex;
    private int[][] neuronIndex;

    private int weightQuantity;
    private int neuronQuantity;

    //only used to show user some stats
    private int inputNC;
    private int outputNC;
    private int hiddenNC;

    private Random random = new Random();

    private void construct(boolean clone, int inputNeuronCount, int outputNeuronCount, int[] hiddenNeuronCount){
        inputNC = inputNeuronCount;
        outputNC = outputNeuronCount;
        hiddenNC = Arrays.stream(hiddenNeuronCount).sum(); //prints 10
        neuronQuantity = hiddenNC + outputNC + inputNC;

        weightQuantity = inputNeuronCount*hiddenNeuronCount[0];
        for (int i = 0; i < hiddenNeuronCount.length-1; i++)
            weightQuantity += hiddenNeuronCount[i]*hiddenNeuronCount[i+1];
        weightQuantity += hiddenNeuronCount[hiddenNeuronCount.length-1]*outputNeuronCount;

        np = new int[hiddenNeuronCount.length+2];

        np[0] = inputNeuronCount;
        System.arraycopy(hiddenNeuronCount, 0, np, 1, hiddenNeuronCount.length + 1 - 1);
        np[hiddenNeuronCount.length+1] = outputNeuronCount;


        //neuronSet = new int[] {inputNeuronCount, 2, 3, 2, outputNeuronCount}; //first and last being input and output

        if(!clone){
            createNeurons();
            createWeights();
        }

        createIndex();
        storePopSize();
    }

    public void constructFrom(double[][][] exampleWeights, double[][] exampleNeurons){
        int inputNeuronCount = exampleNeurons[0].length;
        int outputNeuronCount = exampleNeurons[exampleNeurons.length-1].length;
        int[] hiddenNeuronCount = new int[exampleNeurons.length-2];

        for (int i = 1; i < exampleNeurons.length-1; i++) {
            hiddenNeuronCount[i-1] = exampleNeurons[i].length;
        }

        weights = exampleWeights;
        neurons = exampleNeurons;
        construct(true, inputNeuronCount, outputNeuronCount, hiddenNeuronCount);
    }

    public void constructNew(int inputNeuronCount, int outputNeuronCount, int... hiddenNeuronCount){
        construct(false, inputNeuronCount, outputNeuronCount, hiddenNeuronCount);
    }

    private void createNeurons(){
        List<double[]> neuronsList = new ArrayList<>();

        for (int i : np) {
            double[] temp = new double[i];
            Arrays.fill(temp, 0d);
            neuronsList.add(temp);
        }

        neurons = new double[np.length][];
        neuronsList.toArray(neurons);
    }

    private void createWeights() {
        double[] low;
        double[][] high;

        weights = new double[np.length-1][][];
        for (int ns = 1; ns < np.length; ns++) {
            high = new double[np[ns]][];
            for (int n = 0; n < np[ns]; n++) {
                low = new double[np[ns-1]];
                for (int w = 0; w < np[ns-1]; w++) {
                    low[w] = 0;
                }
                high[n] = low;
            }
            weights[ns-1] = high;
        }
    }

    public double[][][] shareWeights(){
        double[] low;
        double[][] high;

        double[][][] sWeights = new double[np.length-1][][];
        for (int ns = 1; ns < np.length; ns++) {
            high = new double[np[ns]][];
            for (int n = 0; n < np[ns]; n++) {
                low = new double[np[ns-1]];
                System.arraycopy(weights[ns - 1][n], 0, low, 0, np[ns - 1]);
                high[n] = low;
            }
            sWeights[ns-1] = high;
        }
        return sWeights;
    }

    public double[][] shareNeurons(){
        double[][] sNeurons = new double[neurons.length][];
        for (int i = 0; i < neurons.length; i++) {
            double[] sNs = new double[neurons[i].length];
            System.arraycopy(neurons[i], 0, sNs, 0, neurons[i].length);
            sNeurons[i] = sNs;
        }
        return sNeurons;
    }

    private void createIndex() {
        int counter = 0;
        weightIndex = new int[weightQuantity+1][3];

        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    counter++;
                    weightIndex[counter] = new int[] {i, j, k};
                }
            }
        }

        counter = 0;
        neuronIndex = new int[neuronQuantity+1][2];

        for (int i = 0; i < neurons.length; i++) {
            for (int j = 0; j < neurons[i].length; j++) {
                counter++;
                neuronIndex[counter] = new int[] {i, j};
            }
        }
    }

    //weights population per weight set
    private void storePopSize(){

        wsSize = new int[weights.length];
        wsSizeCumulative = new int[weights.length];
        int temp;

        for (int i = 0; i < weights.length; i++) {
            temp = 0;

            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    temp ++;

                }

            }
            wsSize[i] = temp;
            if(i != 0)
                wsSizeCumulative[i] = wsSizeCumulative[i-1] + temp;
            else
                wsSizeCumulative[0] = temp;

        }

    }

    private double weightValue(int i) {
        return(weights[weightIndex[i][0]][weightIndex[i][1]][weightIndex[i][2]]);
    }


    public void showStats(){
        System.out.println("//stats//");
        System.out.println("weights in subject: " + weightQuantity);
        System.out.println("neurons in subject: " + neuronQuantity);
        System.out.println("input neurons: " + inputNC);
        System.out.println("hidden neurons: " + hiddenNC);
        System.out.println("output neurons: " + outputNC);

        System.out.println("\n");
    }

    //0 to weight quantity - 1
    public void showWeight (int i) {
        i++;
        System.out.println("//weight by weightIndex " + i +"//");
        System.out.println("weight position: " + java.util.Arrays.toString(weightIndex[i]));
        System.out.println("weight value: " + weightValue(i));

        System.out.println("\n");
    }

    public void showNeurons() {
        System.out.println("//neurons(" + neuronQuantity + ")//");
        for (double[] neuron : neurons) {
            System.out.println("/");
            for (double i : neuron) {
                System.out.println(i);
            }

        }

        System.out.println("\n");
    }

    public void showWeights() {
        System.out.println("//weights("+weightQuantity+")//");
        for (double[][] weight : weights) {
            System.out.println("//");

            for (double[] doubles : weight) {
                System.out.println("/");

                for (double aDouble : doubles) {
                    System.out.println(aDouble);
                }
            }

        }

        System.out.println("\n");
    }

    public double[] tick(double... in) throws Exception{
        if(in.length != np[0]) throw  new Exception("Too many/not enough inputs for subject");

        //making a temporary duplicate of the actual neuron sets
        double[][] tNeurons = new double[neurons.length][];
        for (int i = 0; i < neurons.length; i++)
            tNeurons[i] = neurons[i].clone();

        //iterating through every neuron and its weights for the calculation
        for(int ns = 1; ns < np.length; ns++) {
            for(int nr = 0; nr < neurons[ns].length; nr++) {
                for(int w = 0; w < weights[ns-1][nr].length; w++) {

                    if(ns == 1)//exception for the first neuron set as those are inputs
                        tNeurons[ns][nr] += in[nr] * weights[ns-1][nr][w];
                    else
                        tNeurons[ns][nr] += tNeurons[ns-1][w] * weights[ns-1][nr][w];

                }
                tNeurons[ns][nr] = Utilities.logistics(tNeurons[ns][nr]);
            }
        }
        return(tNeurons[np.length-1]);
    }

    public void mutate(int weightRecurrence, int neuronRecurrence){
        //mutating weights
        for (int i = 0; i < weightRecurrence; i++) {
            int targetIndex = random.nextInt(weightQuantity);
            double mutateValue = random.nextDouble()/100 * (random.nextBoolean() ? 1: -1);

            int targetPosition[] = weightIndex[targetIndex];

            weights[targetPosition[0]][targetPosition[1]][targetPosition[2]] += mutateValue;
        }

        //mutating neurons
        for (int i = 0; i < neuronRecurrence; i++) {
            int targetIndex = random.nextInt(neuronQuantity);
            double mutateValue = random.nextDouble()/100 * (random.nextBoolean() ? 1: -1);

            int targetPosition[] = neuronIndex[targetIndex];

            neurons[targetPosition[0]][targetPosition[1]] += mutateValue;

        }
    }
}
