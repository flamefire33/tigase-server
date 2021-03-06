/*
 * SaslEXTERNAL.java
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
package tigase.auth.mechanisms;

import tigase.auth.XmppSaslException;
import tigase.auth.XmppSaslException.SaslError;
import tigase.auth.callbacks.ValidateCertificateData;
import tigase.util.stringprep.TigaseStringprepException;
import tigase.xmpp.jid.BareJID;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import java.util.Map;

public class SaslEXTERNAL
		extends AbstractSasl {

	public static final String PEER_CERTIFICATE_KEY = "PEER_CERTIFICATE_ENTRY_KEY";
	private static final String MECHANISM = "EXTERNAL";

	SaslEXTERNAL(Map<? super String, ?> props, CallbackHandler callbackHandler) {
		super(props, callbackHandler);
	}

	@Override
	public byte[] evaluateResponse(byte[] response) throws SaslException {
		BareJID jid;
		try {
			if (response != null && response.length > 0) {
				jid = BareJID.bareJIDInstance(new String(response));
			} else {
				jid = null;
			}
		} catch (TigaseStringprepException e) {
			throw new XmppSaslException(SaslError.malformed_request);
		}

		final ValidateCertificateData ac = new ValidateCertificateData(jid);
		handleCallbacks(ac);

		if (ac.isAuthorized() == true) {
			authorizedId = ac.getAuthorizedID();
		} else {
			throw new XmppSaslException(SaslError.invalid_authzid);
		}

		complete = true;

		return null;
	}

	@Override
	public String getAuthorizationID() {
		return authorizedId;
	}

	@Override
	public String getMechanismName() {
		return MECHANISM;
	}

	@Override
	public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
		return null;
	}

	@Override
	public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
		return null;
	}

}
