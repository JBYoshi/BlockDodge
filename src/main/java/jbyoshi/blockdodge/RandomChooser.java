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
package jbyoshi.blockdodge;

import java.util.*;

public final class RandomChooser<E> {
	private final List<E> list;
	private int index;

	@SafeVarargs
	public RandomChooser(E... options) {
		this(Arrays.asList(options), null);
	}

	public RandomChooser(Iterable<E> options) {
		this(toList(options), null);
	}

	private static <E> List<E> toList(Iterable<E> it) {
		if (it instanceof Collection) {
			return new ArrayList<E>((Collection<E>) it);
		}
		ArrayList<E> list = new ArrayList<>();
		for (E e : it) {
			list.add(e);
		}
		return list;
	}

	private RandomChooser(List<E> options, Void flag) {
		this.list = options;
		if (list.isEmpty()) {
			throw new IllegalArgumentException(
					"Must provide at least one option!");
		}
		this.index = options.size();
	}

	public synchronized E next() {
		if (index >= list.size()) {
			Collections.shuffle(list);
			index = 0;
		}
		return list.get(index++);
	}
}
