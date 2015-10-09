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
