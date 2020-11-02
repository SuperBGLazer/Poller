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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class Poller {
    private final Method method;
    private final long time;
    private long lastPolled = 0;

    /**
     *
     * @param method The static methods that needs executed
     * @param time The time in minutes
     */
    public Poller(Method method, long time, TimeUnit timeUnit) {
        this.method = method;
        switch (timeUnit) {
            case DAYS:
                time = (long) 8.64e+7 * time;
                break;
            case HOURS:
                time = (long) 3.6e+6 * time;
                break;
            case MINUTES:
                time = 60000 * time;
                break;
            case SECONDS:
                time = 1000 * time;
                break;
            case MILLISECONDS:
                break;
            case NANOSECONDS:
                time = (long) (time / 1e+6);
            case MICROSECONDS:
                time = time / 1000;
        }
        this.time = time;
    }

    /**
     * This will execute the method if the time has passed.
     */
    public void poll() {
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime > lastPolled + time) {
                method.setAccessible(true);
                method.invoke(null);
                lastPolled = currentTime;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
