/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2011 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
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
 * $Rev: 2411 $
 * Last modified by $Author: kobit $
 * $Date: 2010-10-27 20:27:58 -0600 (Wed, 27 Oct 2010) $
 * 
 */
/*
 Activate on the server user tracking mechanisms to aid in problem resolution.
 AS:Description: Activate log tracker for a user
 AS:CommandId: http://jabber.org/protocol/admin#add-user-tracker
 AS:Component: sess-man
 */
package tigase.admin

import tigase.server.*
import tigase.xmpp.*
import tigase.util.*
import java.util.logging.*

def JID = "accountjid"
def FILE_NAME = "file-name"
def FILE_LIMIT = 25000000
def FILE_COUNT = 5

def p = (Packet)packet

def userJid = Command.getFieldValue(packet, JID)
def fileName = Command.getFieldValue(packet, FILE_NAME)

if (userJid == null) {
	def result = p.commandResult(Command.DataType.form);

	Command.addTitle(result, "Adding a User Log Tracker")
	Command.addInstructions(result, "Fill out this form to add a user log tracker.")

	Command.addFieldValue(result, "FORM_TYPE", "http://jabber.org/protocol/admin",
			"hidden")
	Command.addFieldValue(result, JID, userJid ?: "", "jid-single",
			"The Jabber ID for the account to be tracked")
	Command.addFieldValue(result, FILE_NAME, fileName ?: "", "text-single",
			"File name to write user's log entries")

	return result
}

def result = p.commandResult(Command.DataType.result)

def users_sessions = (Map)userSessions
bareJID = BareJID.bareJIDInstance(userJid)

XMPPSession session = users_sessions.get(bareJID)

if (session != null) {
	if (fileName == null || fileName == "") {
		fileName = "logs/" + userJid
	}
	def trackers = [userJid]
	session.getConnectionIds().each { trackers += it.toString() }

	LogFilter filter = new LogFilter(trackers.toArray(new String[trackers.size()]))
	FileHandler handler = new FileHandler(fileName, FILE_LIMIT, FILE_COUNT)
	handler.setLevel(Level.ALL)
	handler.setFilter(filter)
	Logger.getLogger("").addHandler(handler)

	Command.addTextField(result, "Note", "Operation successful");
	Command.addFieldMultiValue(result, "Tracking elements: "+trackers.size(), trackers)
} else {
	Command.addTextField(result, "Note", "User: " + userJid + " is not logged in.")
}

return result