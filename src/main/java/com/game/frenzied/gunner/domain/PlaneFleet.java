package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.SoundEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import java.util.concurrent.TimeUnit;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/13 20:39
 */
public class PlaneFleet {
    private static Logger log = LoggerFactory.getLogger(PlaneFleet.class);


    private static BattleDisposition disposition;
    private static TransportPlaneScheduler transportPlaneScheduler;
    private static Random gen = new Random();
    private static int hitPlanes;
    private static ReportCallback reportCallback;

    static {
        reportCallback = new CrashReport();
    }

    public interface ReportCallback {
        void report();
    }

    private static class CrashReport implements ReportCallback {

        @Override
        public void report() {
            hitPlanes ++;
        }
    }

    private static class BattleDisposition extends Thread {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    BattlePlane plane = new BattlePlane(
                            gen.nextBoolean()
                                    ? BattlePlane.OrientationType.TO_LEFT
                                    : BattlePlane.OrientationType.TO_RIGHT
                    );
                    plane.setReportCallback(reportCallback);
                    AbstractActor.abstractActors.add(plane);
                    int waitTime = gen.nextInt(2);
                    TimeUnit.SECONDS.sleep(waitTime);
                    plane.shootBall();
                    waitTime = gen.nextInt(7);
                    TimeUnit.SECONDS.sleep(waitTime);
                }
            } catch (InterruptedException e) {

            }
        }
    }

    private static class TransportPlaneScheduler extends Thread {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    TransportPlane plane = new TransportPlane();
                    SoundEffect.forTransportPlaneFly().play();
                    AbstractActor.abstractActors.add(plane);
                    int waitTime = gen.nextInt(2);
                    TimeUnit.SECONDS.sleep(waitTime);
                    plane.release();
                    waitTime = 30 + gen.nextInt(10);
                    TimeUnit.SECONDS.sleep(waitTime);
                }
            } catch (InterruptedException e) {

            }
        }
    }

    public static void deploy() {
        disposition = new BattleDisposition();
        disposition.start();

        transportPlaneScheduler = new TransportPlaneScheduler();
        transportPlaneScheduler.start();
    }


    public static void pause() {
        disposition.interrupt();
        transportPlaneScheduler.interrupt();
    }

    public static int hitPlanes() {
        return hitPlanes;
    }

    public static void reset() {
        hitPlanes = 0;
    }

}
