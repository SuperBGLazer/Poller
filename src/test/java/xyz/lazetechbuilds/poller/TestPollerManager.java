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
package xyz.lazetechbuilds.poller;

import org.junit.jupiter.api.*;
import xyz.lazertechbuilds.poller.Poll;
import xyz.lazertechbuilds.poller.PollMethod;
import xyz.lazertechbuilds.poller.PollerManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Poll
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestPollerManager {
    private static final AtomicInteger pollCount = new AtomicInteger(0);

    @AfterAll
    static void stop() {
        PollerManager.stop();
    }

    @Order(0)
    @Test
    void setup() {
        PollerManager.setup(1);
    }

    @Order(1)
    @Test
    void execute() throws InterruptedException {
        PollerManager.poll();
        assert pollCount.get() == 2;
        TimeUnit.SECONDS.sleep(2);
        assert pollCount.get() == 4;
    }

    @PollMethod(time = 1, timeUnit = TimeUnit.SECONDS)
    public static void poll() {
        System.out.println("Polling 1");
        pollCount.incrementAndGet();
    }

    @PollMethod(time = 1, timeUnit = TimeUnit.SECONDS)
    public static void poll2() {
        System.out.println("Polling 2");
        pollCount.incrementAndGet();
    }
}