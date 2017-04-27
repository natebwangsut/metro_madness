package com.unimelb.swen30006.metromadness.passengers;

import java.util.Random;

import com.unimelb.swen30006.metromadness.stations.CargoStation;
import com.unimelb.swen30006.metromadness.stations.Station;

public class Passenger {

    private final int id;
    private Station beginning;
    private Station destination;
    private float travelTime;
    private boolean reachedDestination;
    private Cargo cargo;

    public Passenger(int id, Random random, Station start, Station end){
        this.id = id;
        this.beginning = start;
        this.destination = end;
        this.reachedDestination = false;
        this.travelTime = 0;

        // Do not generate cargo if not from CargoStation
        if (start instanceof CargoStation) {
            this.cargo = generateCargo(random);
        } else {
            this.cargo = new Cargo(0);
        }
    }

    
    public int getID(){
        return this.id;
    }
    
    public Station getDestination(){
        return this.destination;
    }
    
    public float getTravelTime(){
        return this.travelTime;
    }
    
    public void update(float time){
        if(!this.reachedDestination){
            this.travelTime += time;
        }
    }
    
    
    // Encapsulated Cargo class and its methods
    public class Cargo{
        private int weight;

        public Cargo(int weight){
            this.setWeight(weight);
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
    
    public Cargo generateCargo(Random random){
        return new Cargo(random.nextInt(50) +1);
    }
    
    public Cargo getCargo(){
        return cargo;
    }

    
}
