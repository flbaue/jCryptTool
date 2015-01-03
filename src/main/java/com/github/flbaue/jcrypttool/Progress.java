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

package com.github.flbaue.jcrypttool;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class Progress {
    private int progress;
    private List<ProgressListener> progressListenerList = new LinkedList<>();

    public void updateProgress(int value) {
        progress = value;
        ProgressEvent progressEvent = new ProgressEvent(value);

        for (ProgressListener listener : progressListenerList) {
            listener.progressUpdate(progressEvent);
        }
    }

    public void addProgressListener(ProgressListener listener) {
        progressListenerList.add(listener);
    }
}
