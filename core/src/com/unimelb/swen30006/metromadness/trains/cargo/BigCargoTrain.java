package com.unimelb.swen30006.metromadness.trains.cargo;

import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;
import com.unimelb.swen30006.metromadness.trains.CargoTrain;

/**
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

public class BigCargoTrain extends CargoTrain {

    private static int MAX_WEIGHT = 1000;
    private static int MAX_PASSENGER = 80;

    public BigCargoTrain(Line trainLine, Station start, boolean forward, String name) {
        super(trainLine, start, forward, name, MAX_WEIGHT, MAX_PASSENGER);
    }
}
