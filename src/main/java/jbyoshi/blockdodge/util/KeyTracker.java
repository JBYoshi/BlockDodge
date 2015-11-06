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

import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

public final class KeyTracker implements KeyListener {
	private final Set<Integer> presses = new HashSet<>();
	private static final Map<Integer, String> names = new HashMap<>();

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		presses.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		presses.remove(e.getKeyCode());
	}

	public boolean isPressed(int keyCode) {
		return presses.contains(keyCode);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ");
		presses.stream().map(names::get).forEach(joiner::add);
		return "[" + joiner.toString() + "]";
	}

	static {
		for (Field f : KeyEvent.class.getDeclaredFields()) {
			if (Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers())
					&& Modifier.isFinal(f.getModifiers()) && f.getName().startsWith("VK_")) {
				try {
					names.put(f.getInt(null), f.getName());
				} catch (Exception e) {
					throw new AssertionError(e);
				}
			}
		}
	}

}
