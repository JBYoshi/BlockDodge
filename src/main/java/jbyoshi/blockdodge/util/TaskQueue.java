/*
 * Copyright (c) 2015 JBYoshi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jbyoshi.blockdodge.util;

import java.util.*;

public final class TaskQueue {
	private final Queue<Runnable> tasks = new LinkedList<>();

	public synchronized void add(Runnable task) {
		tasks.add(task);
	}

	public void runAll() {
		List<Runnable> toRun;
		synchronized (this) {
			toRun = new ArrayList<Runnable>(tasks);
			tasks.clear();
		}
		toRun.forEach(Runnable::run);
	}
}
