package real;

import threading.Message;
import io.Session;
import threading.Server;
import server.ServerController;
import server.util;

public class RealController extends ServerController {
    Server server;

    public RealController() {
        this.server = Server.getInstance();
    }

    @Override
    public void processGameMessage(final Session conn, final Message message) {
        try {
            final byte b = message.reader().readByte();
            util.Debug("msg -29-> " + b);
            switch (b) {
                case -127: {
                    if (conn.user == null) {
                        conn.loginGame(message);
                        break;
                    }
                    break;
                }
                case -125: {
                    conn.setConnect(message);
                    break;
                }
                case -85: {
                    ItemData.divedeItem(conn.user, message);
                    break;
                }
                default:
                    util.Debug("Not match process game " + b);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void userLogin(final Session conn, final Message m) {
    }

    @Override
    public boolean userLogout(final Session conn) {
        return false;
    }
}
