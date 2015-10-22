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
package jbyoshi.blockdodge.updater;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.*;

public final class Updater {
	private static final JsonParser PARSER = new JsonParser();
	private static final String CURRENT_VERSION;
	private static final URL VERSION_URL;

	static {
		StringBuilder currentVersion = new StringBuilder();
		char[] cbuf = new char[32];
		try (InputStreamReader in = new InputStreamReader(Updater.class.getResourceAsStream("version.txt"))) {
			int read = in.read(cbuf);
			currentVersion.append(cbuf, 0, read);
		} catch (IOException e) {
			throw new Error(e);
		}
		CURRENT_VERSION = currentVersion.toString().trim();

		try {
			VERSION_URL = new URL("https://api.github.com/repos/JBYoshi/BlockDodge/releases/latest");
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}

	public static Optional<Version> findUpdate() throws IOException {
		try {
			JsonObject parsed = PARSER.parse(new InputStreamReader(VERSION_URL.openStream())).getAsJsonObject();
			if (parsed.has("message")) {
				throw new IOException("Git API error: " + parsed.get("message"));
			}
			String name = parsed.get("name").getAsString();
			if (name.equals(CURRENT_VERSION) || name.equals("v" + CURRENT_VERSION)) {
				// No update
				return Optional.empty();
			}
			String htmlUrl = parsed.get("html_url").getAsString();
			return Optional.of(new Version(name, htmlUrl));
		} catch (IllegalStateException e) {
			throw new IOException(e);
		}
	}

	public static boolean isEnabled() {
		return !CURRENT_VERSION.startsWith("$") && !CURRENT_VERSION.endsWith("-SNAPSHOT");
	}

	public static String getCurrentVersion() {
		if (CURRENT_VERSION.startsWith("$")) {
			return "Dev";
		}
		if (CURRENT_VERSION.endsWith("-SNAPSHOT")) {
			return CURRENT_VERSION.substring(0, CURRENT_VERSION.length() - "-SNAPSHOT".length()) + " Dev";
		}
		return CURRENT_VERSION;
	}
}
