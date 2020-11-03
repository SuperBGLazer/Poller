/*
    This library is called Poller. It will automatic run static methods that have an annotation.
    Copyright (C) 2020 Breyon Gunn

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package xyz.lazertechbuilds.poller;

import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollerManager {
    private static final List<Poller> pollerList = new ArrayList<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static int time = 60;

    /**
     * Setup the poller
     * @param time The time in between polls
     */
    public static void setup(int time) {
        PollerManager.time = time;
        setup();
    }

    /**
     * Setup the poller
     */
    public static void setup() {
        getPollers();
        scheduleTask();
    }

    /**
     * Stop the poller
     */
    public static void stop() {
        scheduler.shutdown();
    }

    private static void getPollers() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> pollerClasses = reflections.getTypesAnnotatedWith(Poll.class);

        for (Class<?> clazz :
                pollerClasses) {
            for (Method method:
                    clazz.getMethods()) {
                if (method.getParameterCount() == 0 && Modifier.isStatic(method.getModifiers()) &&
                        method.isAnnotationPresent(PollMethod.class)) {
                    PollMethod pollMethod = method.getAnnotation(PollMethod.class);
                    pollerList.add(new Poller(method, pollMethod.time(), pollMethod.timeUnit()));
                }
            }
        }
    }

    private static void scheduleTask() {
        Runnable runnable = PollerManager::poll;
        scheduler.scheduleAtFixedRate(runnable, 0, time,
                TimeUnit.SECONDS);

    }

    /**
     * Poll all the Pollers
     */
    public static void poll() {
        for (Poller poller :
                pollerList) {
            poller.poll();
        }
    }
}
