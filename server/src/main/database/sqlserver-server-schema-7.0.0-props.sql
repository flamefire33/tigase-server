--
--  Tigase Jabber/XMPP Server
--  Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
--
--  This program is free software: you can redistribute it and/or modify
--  it under the terms of the GNU Affero General Public License as published by
--  the Free Software Foundation, either version 3 of the License.
--
--  This program is distributed in the hope that it will be useful,
--  but WITHOUT ANY WARRANTY; without even the implied warranty of
--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--  GNU Affero General Public License for more details.
--
--  You should have received a copy of the GNU Affero General Public License
--  along with this program. Look for COPYING file in the top folder.
--  If not, see http://www.gnu.org/licenses/.
--
--  $Rev: $
--  Last modified by $Author: $
--  $Date: $
--

-- QUERY START:
SET QUOTED_IDENTIFIER ON
-- QUERY END:
GO

-- QUERY START:
-- This is a dummy user who keeps all the database-properties
if not exists (select 1 from tig_users where user_id = 'db-properties')
exec dbo.TigAddUserPlainPw 'db-properties', NULL;
-- QUERY END:
GO

select GETDATE(), ' - Setting schema version to 5.1';

-- QUERY START:
exec dbo.TigPutDBProperty 'schema-version', '5.1';
-- QUERY END:
GO

-- QUERY START:
exec TigSetComponentVersion('server', '5.1');
-- QUERY END:
GO
