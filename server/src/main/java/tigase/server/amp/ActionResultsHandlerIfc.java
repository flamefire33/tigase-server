/*
 * ActionResultsHandlerIfc.java
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
package tigase.server.amp;

//~--- non-JDK imports --------------------------------------------------------

import tigase.server.Packet;

import java.util.Queue;

//~--- JDK imports ------------------------------------------------------------

//~--- interfaces -------------------------------------------------------------

/**
 * Created: May 1, 2010 3:26:50 PM
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public interface ActionResultsHandlerIfc {

	boolean addOutPacket(Packet packet);

	boolean addOutPackets(Queue<Packet> packets);
}

//~ Formatted in Sun Code Convention

//~ Formatted by Jindent --- http://www.jindent.com
