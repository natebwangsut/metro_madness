package com.unimelb.swen30006.metromadness.trains;

import com.badlogic.gdx.graphics.Color;
import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.rmi.runtime.Log;

import java.awt.geom.Point2D;

/**
 * Nate Bhurinat W. (@natebwangsut | nate.bwangsut@gmail.com)
 * https://github.com/natebwangsut
 */

public class CargoTrain extends Train {

    private static Logger logger = LogManager.getLogger();

//    Train Rendering Configs
//    public static final int     MAX_TRIPS       = 4;
//    public static final Color   FORWARD_COLOUR  = Color.ORANGE;
//    public static final Color   BACKWARD_COLOUR = Color.VIOLET;
//    public static final float   TRAIN_WIDTH     = 4;
//    public static final float   TRAIN_LENGTH    = 6;
//    public static final float   TRAIN_SPEED     = 50f;

    public CargoTrain(Line trainLine, Station start, boolean forward, String name) {
        super(trainLine, start, forward, name);
    }

    @Override
    public void update(float delta) {
        // Update all passengers
        for (Passenger p : this.passengers) {
            p.update(delta);
        }
        boolean hasChanged = false;
        if (previousState == null || previousState != this.state) {
            previousState = this.state;
            hasChanged = true;
        }

        // Update the state
        switch (this.state) {
            case FROM_DEPOT:
                if (hasChanged) {
                    logger.info(this.name + " is travelling from the depot: " + this.station.name + " Station...");
                }

                // We have our station initialized we just need to retrieve the next track, enter the
                // current station officially and mark as in station
                try {
                    if (this.station.canEnter(this.trainLine)) {

                        this.station.enter(this);
                        this.pos = (Point2D.Float) this.station.position.clone();
                        this.state = State.IN_STATION;
                        this.disembarked = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case IN_STATION:
                if (hasChanged) {
                    logger.info(this.name + " is in " + this.station.name + " Station.");
                }

                // When in station we want to disembark passengers
                // and wait 10 seconds for incoming passengers
                if (!this.disembarked) {
                    this.disembark();
                    this.departureTimer = this.station.getDepartureTime();
                    this.disembarked = true;
                } else {
                    // Count down if departure timer.
                    if (this.departureTimer > 0) {
                        this.departureTimer -= delta;
                    } else {
                        this.state = State.SEARCH_TRACK;
                    }
                }
                break;
            case SEARCH_TRACK:
                // We are ready to depart, find the next track and wait until we can enter
                try {
                    boolean endOfLine = this.trainLine.endOfLine(this.station);
                    if (endOfLine) {
                        this.forward = !this.forward;
                    }
                    this.track = this.trainLine.nextTrack(this.station, this.forward);
                    this.state = State.READY_DEPART;
                    break;
                } catch (Exception e) {
                    // Massive error.
                    return;
                }
            case READY_DEPART:
                if (hasChanged) {
                    logger.info(this.name + " is ready to depart from " + this.station.name + " Station!");
                }

                // When ready to depart, check that the track is clear and if
                // so, then occupy it if possible.
                if (this.track.canEnter(this.forward)) {
                    try {
                        // Find the next
                        Station next = this.trainLine.nextStation(this.station, this.forward);
                        // Depart our current station
                        this.station.depart(this);
                        this.station = next;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    this.track.enter(this);
                    this.state = State.ON_ROUTE;
                }
                break;
            case ON_ROUTE:
                if (hasChanged) {
                    logger.info(this.name + " enroute to " + this.station.name + " Station!");
                }

                // Checkout if we have reached the new station
                // Nate: Simulation smoothness
                if (this.pos.distance(this.station.position) < 5) {
                    this.state = State.WAITING_ENTRY;
                } else {
                    move(delta);
                }
                break;
            case WAITING_ENTRY:
                if (hasChanged) {
                    logger.info(this.name + " is awaiting entry " + this.station.name + " Station..!");
                }

                // Waiting to enter, we need to check the station has room and if so
                // then we need to enter, otherwise we just wait
                try {
                    if (this.station.canEnter(this.trainLine) && this.station.compatible(this)) {
                        this.track.leave(this);
                        this.pos = (Point2D.Float) this.station.position.clone();
                        this.station.enter(this);
                        this.state = State.IN_STATION;
                        this.disembarked = false;
                    } else if (!this.station.compatible(this)) {
                        this.state = State.SKIPPING_STATION;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SKIPPING_STATION:
                if (hasChanged) {
                    logger.info(this.name + " is skipping " + this.station.name + " Station..!");
                }

                this.track.leave(this);
                this.pos = (Point2D.Float) this.station.position.clone();

                try {
                    boolean endOfLine = this.trainLine.endOfLine(this.station);
                    if (endOfLine) {
                        this.forward = !this.forward;
                    }
                    this.track = this.trainLine.nextTrack(this.station, this.forward);
                } catch (Exception e) {
                    // Massive error.
                    return;
                }

                if (this.track.canEnter(this.forward)) {
                    try {
                        // Find the next
                        this.station = this.trainLine.nextStation(this.station, this.forward);;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    this.track.enter(this);
                    this.state = State.ON_ROUTE;
                }
                break;
        }
    }
}
