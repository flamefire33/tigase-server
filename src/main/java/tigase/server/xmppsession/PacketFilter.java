/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2007 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package tigase.server.xmppsession;

import java.util.Queue;
import java.util.logging.Logger;
import tigase.db.NonAuthUserRepository;
import tigase.server.Packet;
import tigase.xml.Element;
import tigase.xmpp.Authorization;
import tigase.xmpp.NotAuthorizedException;
import tigase.xmpp.XMPPResourceConnection;
import tigase.xmpp.StanzaType;
import tigase.xmpp.PacketErrorTypeException;
import tigase.util.JIDUtils;

/**
 * Describe class PacketFilter here.
 *
 *
 * Created: Fri Feb  2 15:08:58 2007
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class PacketFilter {

  /**
   * Variable <code>log</code> is a class logger.
   */
  private static final Logger log =
		Logger.getLogger("tigase.server.xmppsession.PacketFilter");

	/**
	 * Creates a new <code>PacketFilter</code> instance.
	 *
	 */
	public PacketFilter() {	}

	public boolean preprocess(final Packet packet, final XMPPResourceConnection session,
		final NonAuthUserRepository repo, final Queue<Packet> results) {
		if (session == null || !session.isAuthorized()) {
			return false;
		} // end of if (session == null)

		try {
			// For all messages coming from the owner of this account set
			// proper 'from' attribute. This is actually needed for the case
			// when the user sends a message to himself.
			if (packet.getFrom() != null
				&& packet.getFrom().equals(session.getConnectionId())) {
				String from_jid = session.getJID();
				if (from_jid != null && !from_jid.isEmpty()) {
					log.finest("Setting correct from attribute: " + from_jid);
					packet.getElement().setAttribute("from", from_jid);
				} else {
					log.warning("Session is authenticated but session.getJid() is empty: "
						+ packet.toString());
				}
			}

		} catch (NotAuthorizedException e) {
			return false;
		} // end of try-catch

		return false;
	}

	public boolean forward(final Packet packet, final XMPPResourceConnection session,
		final NonAuthUserRepository repo, final Queue<Packet> results) {
		return false;
	}

	public boolean process(final Packet packet, final XMPPResourceConnection session,
		final NonAuthUserRepository repo, final Queue<Packet> results) {

		if (session == null) {
			return false;
		} // end of if (session == null)

		log.finest("Processing packet: " + packet.toString());

		try {
			// Can not forward packet if there is no destination address
			if (packet.getElemTo() == null) {
				// If this is simple <iq type="result"/> then ignore it
				// and consider it OK
				if (packet.getElemName().equals("iq")
					&& packet.getType() != null
					&& packet.getType() == StanzaType.result) {
					// Nothing to do....
					return true;
				}
				log.warning("No 'to' address, can't deliver packet: "
					+ packet.getStringData());
				return false;
			}

			// Already done in forward method....
			// No need to repeat this (performance - everything counts...)
// 			// For all messages coming from the owner of this account set
// 			// proper 'from' attribute. This is actually needed for the case
// 			// when the user sends a message to himself.
// 			if (packet.getFrom() != null
// 				&& packet.getFrom().equals(session.getConnectionId())) {
// 				packet.getElement().setAttribute("from", session.getJID());
// 				log.finest("Setting correct from attribute: " + session.getJID());
// 			} // end of if (packet.getFrom().equals(session.getConnectionId()))

			String id = null;

			id = JIDUtils.getNodeID(packet.getElemTo());
			if (id.equals(session.getUserId())) {
				// Yes this is message to 'this' client
				log.finest("Yes, this is packet to 'this' client: " + id);
				Element elem = packet.getElement().clone();
				Packet result = new Packet(elem);
				result.setTo(session.getParentSession().
					getResourceConnection(packet.getElemTo()).getConnectionId());
				log.finest("Setting to: " + result.getTo());
				result.setFrom(packet.getTo());
				results.offer(result);
				return true;
			} // end of else

			id = JIDUtils.getNodeID(packet.getElemFrom());
			if (id.equals(session.getUserId())) {
				Element result = packet.getElement().clone();
				results.offer(new Packet(result));
				return true;
			}
		} catch (NotAuthorizedException e) {
			try {
				results.offer(Authorization.NOT_AUTHORIZED.getResponseMessage(packet,
						"You must authorize session first.", true));
				log.warning("NotAuthorizedException for packet: "	+ packet.getStringData());
			} catch (PacketErrorTypeException e2) {
				log.warning("Packet processing exception: " + e2);
			}
		} // end of try-catch

		return false;
	}

}
