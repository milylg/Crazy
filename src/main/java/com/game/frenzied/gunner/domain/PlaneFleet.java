package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.SoundEffect;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/13 20:39
 */
public class PlaneFleet {
    private static Logger log = LoggerFactory.getLogger(PlaneFleet.class);


    private static BattleDisposition disposition;
    private static SelfTransportPlaneFleet selfTransportPlaneFleet;

    private static Random gen = new Random();
    private static int hitPlanes;
    private static int trustDegreeValue = 15;
    private static ReportCallback reportCallback;
    private static ExecutorService executorService;
    private static boolean pause = false;

    static {
        reportCallback = new CrashReport();
        executorService = new ThreadPoolExecutor(
                7,
                10,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(10)
        );
    }

    @FunctionalInterface
    public interface ReportCallback {
        void report();
    }


    private static class CrashReport implements ReportCallback {

        // have thread safe problem
        @Override
        public void report() {
            hitPlanes ++;
        }
    }

    private static class TrustDegreeReport implements ReportCallback {

        // have thread safe problem
        @Override
        public void report() {
            trustDegreeValue ++;
        }
    }

    private static abstract class RecoverSuspendMechanism {
        private static final Logger log = LoggerFactory.getLogger(RecoverSuspendMechanism.class);
        private boolean isSuspend = false;
        protected boolean isInterrupt = false;
        private Object lock = new Object();

        protected void checkSuspendState() {

            synchronized (lock) {
                log.info("acquire lock....");
                if (isSuspend) {
                    log.info("suspend:true ... wait suspend is false");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        // log
                    }
                }
            }
            log.info("skip synchronized block... suspend = {}", isSuspend);
        }

        public void setSuspend(boolean pause) {

            log.info("setSuspend method");
            synchronized (lock) {
                log.info("acquire lock... isSuspend:{}", isSuspend);
                this.isSuspend = pause;
                lock.notifyAll();
            }
            log.info("set isSuspend = {}, then = {}", pause, isSuspend);
        }


        protected abstract void execute();

        // Bug:this way is not normal running, so......
//        private void waitRandomSecond(int bound) {
//            try {
//                TimeUnit.SECONDS.sleep(gen.nextInt(bound));
//            } catch (InterruptedException e) {
//                // log
//            }
//        }
//
//        private void waitRandomSecond(int base, int bound) {
//            try {
//                TimeUnit.SECONDS.sleep(base + gen.nextInt(bound));
//            } catch (InterruptedException e) {
//                // log
//            }
//        }

    }

    private static class BattleDisposition extends RecoverSuspendMechanism implements Runnable {

        @Override
        public void run() {
            while (true) {
                checkSuspendState();
                execute();
            }
        }

        @Override
        protected void execute() {
            log.info("execute:build battle plane.");
            BattlePlane plane = BattlePlane.buildActorOf(
                    gen.nextBoolean()
                            ? BattlePlane.OrientationType.TO_LEFT
                            : BattlePlane.OrientationType.TO_RIGHT
            );


            plane.setReportCallback(reportCallback);

            try {
                TimeUnit.SECONDS.sleep(gen.nextInt(1));
                plane.shootBall();
                TimeUnit.SECONDS.sleep(gen.nextInt(1));
            } catch (InterruptedException e) {
                // log
            }

            // here is a bug: why method is unuseful.
//            waitRandomSecond(2);
//            plane.shootBall();
//            waitRandomSecond(3, gen.nextInt(2));
        }
    }

    private static class SelfTransportPlaneFleet extends RecoverSuspendMechanism implements Runnable {

        private static final int DEFAULT_DEGREE_BELIEF = 10;
        private int degreeBelief;

        public SelfTransportPlaneFleet() {
            this(DEFAULT_DEGREE_BELIEF);
        }

        public SelfTransportPlaneFleet(int degreeBelief) {
            this.degreeBelief = degreeBelief;
        }

        @Override
        public void run() {
            while (true) {
                checkSuspendState();
                execute();
            }
        }

        @Override
        protected void execute() {
            log.info("execute:build transport plane.");
            TransportPlane plane = new TransportPlane();
            SoundEffect.forTransportPlaneFly().play();
            try {
                TimeUnit.SECONDS.sleep(gen.nextInt(2));
                plane.release();
                TimeUnit.SECONDS.sleep(degreeBelief + gen.nextInt(degreeBelief/2));
            } catch (InterruptedException e) {
                // log
            }
            // Unknown Bug: it's unuseful. method only run once.
//            waitRandomSecond(2);
//            plane.release();
//            waitRandomSecond(degreeBelief, gen.nextInt(degreeBelief/2));
        }

    }


    public static void deploy() {
        if (disposition == null) {
            disposition = new BattleDisposition();
            executorService.submit(disposition);
        }
        if (selfTransportPlaneFleet == null) {
            selfTransportPlaneFleet = new SelfTransportPlaneFleet(trustDegreeValue);
            executorService.submit(selfTransportPlaneFleet);
        }
    }


    public static void pause() {
        pause = !pause;
        disposition.setSuspend(pause);
        selfTransportPlaneFleet.setSuspend(pause);
    }

    @Deprecated
    public static void pause(boolean isSuspend) {
        disposition.setSuspend(isSuspend);
        selfTransportPlaneFleet.setSuspend(isSuspend);
    }

    public static int hitPlanes() {
        return hitPlanes;
    }

    public static void reset() {
        hitPlanes = 0;
        trustDegreeValue = 15;
        pause = false;
    }

}
