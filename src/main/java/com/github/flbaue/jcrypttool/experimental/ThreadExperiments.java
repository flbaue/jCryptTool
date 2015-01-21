/*
 * Copyright 2015 Florian Bauer, florian.bauer@posteo.de
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.flbaue.jcrypttool.experimental;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Florian Bauer on 15.01.15.
 */
public class ThreadExperiments {

    public static void main(String[] args) {
        new ThreadExperiments().run();
    }

    private void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

    }
}
