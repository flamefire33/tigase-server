/*
 * ExtProcessor.java
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

package tigase.server.ext;

//~--- non-JDK imports --------------------------------------------------------

import tigase.server.Packet;
import tigase.xml.Element;

import java.util.List;
import java.util.Queue;

//~--- JDK imports ------------------------------------------------------------

//~--- interfaces -------------------------------------------------------------

/**
 * Created: Oct 1, 2009 8:40:36 PM
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public interface ExtProcessor {

	String getId();

	List<Element> getStreamFeatures(ComponentIOService serv, ComponentProtocolHandler handler);

	//~--- methods --------------------------------------------------------------

	boolean process(Packet p, ComponentIOService serv, ComponentProtocolHandler handler, Queue<Packet> results);

	void startProcessing(Packet p, ComponentIOService serv, ComponentProtocolHandler handler, Queue<Packet> results);
}

//~ Formatted in Sun Code Convention

//~ Formatted by Jindent --- http://www.jindent.com
