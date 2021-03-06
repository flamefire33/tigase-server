/*
 * AllHistoryCache.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.util.historyCache;

import tigase.stats.StatisticsList;
import tigase.sys.TigaseRuntime;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Artur Hefczyc Created May 28, 2011
 */
public class AllHistoryCache {

	private static final Logger log = Logger.getLogger(AllHistoryCache.class.getName());
	private StatisticsList[] buffer = null;
	private int count = 0;
	private int highMemoryLevel = 95;
	private int highMemoryUsageCount = 0;
	private int start = 0;

	public AllHistoryCache(int limit, int highMemoryLevel) {
		buffer = new StatisticsList[limit];
		this.highMemoryLevel = highMemoryLevel;
	}

	public synchronized void addItem(StatisticsList item) {
		int ix = (start + count) % buffer.length;
		buffer[ix] = item;
		if (count < buffer.length) {
			count++;
		} else {
			start++;
			start %= buffer.length;
		}
		if (isHighMemoryUsage()) {
			highMemoryUsageCount++;
			int minimalSize = count / 2;
			if (minimalSize < 5) {
				minimalSize = 5;
			}
			if (count > minimalSize) {
				for (int i = 0; i < count - minimalSize; i++) {
					buffer[(start + i) % buffer.length] = null;
				}
				start = (start + count) - minimalSize;
				count = minimalSize;
			}
			log.log(Level.CONFIG, "Shrinking statistics to {0} items for {1} time",
					new Object[]{minimalSize, highMemoryUsageCount});
		} else {
			highMemoryUsageCount = 0;
		}
	}

	public synchronized StatisticsList[] getCurrentHistory() {
		StatisticsList[] result = new StatisticsList[count];
		for (int i = 0; i < count; i++) {
			int ix = (start + i) % buffer.length;
			result[i] = buffer[ix];
		}
		return result;
	}

	protected boolean isHighMemoryUsage() {
		return TigaseRuntime.getTigaseRuntime().getHeapMemUsage() > highMemoryLevel;
	}

}
