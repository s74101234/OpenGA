/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openga.MainProgram;
import openga.Fitness.FitnessI;
import openga.ObjectiveFunctions.ObjectiveFunctionI;
import openga.chromosomes.chromosome;
import openga.chromosomes.population;
import openga.chromosomes.populationI;
import openga.operator.crossover.EDAICrossover;
import openga.operator.miningGene.*;
import openga.operator.mutation.EDAIMutation;
import openga.operator.selection.SelectI;

/**
 *
 * @author Kuo Yu-Cheng
 */
public class singleThreadGAwithEDA4 extends singleThreadGAwithEDA2 implements PBILInteractiveWithEDA4I {

    public singleThreadGAwithEDA4() {
    }
    
    int D1;
    int D2;
    boolean OptMin;
    int epoch;
    populationI populationOldTemp,populationTemp;
    int stopGeneration;
    
//    PBILInteractiveWithEDA3 PBIL1;   //PBIL
//    PBILInteractiveWithEDA3_2 PBIL1;   //PBIL
    PBILInteractiveWithEDA4 PBIL1;
    
    public populationI mergePopulation(populationI population1,populationI population2){
      int newPopulationSize = population1.getPopulationSize() + population2.getPopulationSize();
      populationI result = new population();
      result.setGenotypeSizeAndLength(encodeType, newPopulationSize, newPopulationSize, numberOfObjs);
      result.initNewPop();
      int index = 0;
      for(int i = 0 ; i < population1.getPopulationSize() ; i++){
        result.setSingleChromosome(index, population1.getSingleChromosome(i));
        index++;
      }
      for(int i = 0 ; i < population2.getPopulationSize() ; i++){
        result.setSingleChromosome(index, population2.getSingleChromosome(i));
        index++;
      } 
      return result;
    }

    public void startGA() {
        Population = initialStage();
        
//        System.out.println(Population.getSingleChromosome(0).genes[0]);
        //evaluate the objective values and calculate fitness values
//        System.out.println(generations);
        //System.exit(0);
        ProcessObjectiveAndFitness();
        intialOffspringPopulation();
        archieve = findParetoFront(Population, 0);

        PBIL1 = new PBILInteractiveWithEDA4(Population, lamda, beta , D1 , D2 , OptMin , epoch);   // PBIL

        container = PBIL1.getContainer();
        inter = PBIL1.getInter();

        temporaryContainer = new double[length][length];
        
        int i = 0;

        //for (int i = 0; i < generations; i++) {
        do{
//            System.out.println("generations "+i);
            if(i%stopGeneration == 0){
              populationOldTemp = Population;
              populationTemp = populationOldTemp;
            }else if(i%stopGeneration != 0 ){
              populationTemp = mergePopulation(populationOldTemp,Population);
              populationOldTemp = populationTemp;
            }
//            System.out.println(i + ":" + populationTemp.getPopulationSize());
            
            currentGeneration = i;
            offsrping = selectionStage(Population);
            
            //collect gene information, it's for mutation matrix
            if (i % startingGenDividen != 0) {
              
                tempNumberOfCrossoverTournament = 1;
                tempNumberOfMutationTournament = 1;
            } else {
                    tempNumberOfCrossoverTournament = numberOfCrossoverTournament;
                    tempNumberOfMutationTournament = numberOfMutationTournament;
                    PBIL1 = new PBILInteractiveWithEDA4(populationTemp, lamda, beta , D1 , D2 , OptMin , epoch);   // PBIL
                    PBIL1.startStatistics();

                    container = PBIL1.getContainer();
                    inter = PBIL1.getInter();                        

                    temporaryContainer = container;
                    
            }
            //Crossover
            offsrping = crossoverStage(offsrping, temporaryContainer, inter);

            //Mutation
            //System.out.println("mutationStage");
            offsrping = mutationStage(offsrping, temporaryContainer, inter);
            //System.out.println("timeClock1 "+timeClock1.getExecutionTime());
            ProcessObjectiveAndFitness(offsrping);
            Population = replacementStage(Population, offsrping);  //Population
            evalulatePop(Population);

            //String generationResults = "";
            //generationResults = i + "\t" + getBest() + "\n";
            //writeFile("eda2_655" , generationResults);
            
            currentUsedSolution += fixPopSize;//Solutions used in genetic search

            //local search
            if (applyLocalSearch == true && i % 10 == 0 ) {
                localSearchStage(1);
            } 
            
            i ++;

        }while(i < generations && currentUsedSolution < this.fixPopSize*this.generations);
        //printResults();
    }
    
  
    public int getBestSolnIndex(populationI arch1) {
        int index = 0;
        double bestobj = Double.MAX_VALUE;
        for (int k = 0; k < getArchieve().getPopulationSize(); k++) {
            if (bestobj > getArchieve().getObjectiveValues(k)[0]) {
                bestobj = getArchieve().getObjectiveValues(k)[0];
                index = k;
            }
        }
        return index;
    }

  @Override
  public void setEDAinfo(double lamda, double beta, int numberOfCrossoverTournament, 
          int numberOfMutationTournament, int startingGenDividen , 
          int D1 , int D2 , boolean OptMin , int epoch , int stopGeneration) {
    
        this.lamda = lamda;
        this.beta = beta;
        this.numberOfCrossoverTournament = numberOfCrossoverTournament;
        this.numberOfMutationTournament = numberOfMutationTournament;
        this.startingGenDividen = startingGenDividen;
        this.D1 = D1;
        this.D2 = D2;
        this.OptMin = OptMin;
        this.epoch = epoch;
        this.stopGeneration = stopGeneration;
        
  }
  
}
