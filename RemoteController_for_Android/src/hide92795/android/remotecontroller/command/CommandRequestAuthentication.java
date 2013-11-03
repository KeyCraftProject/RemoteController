package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class CommandRequestAuthentication implements Command {

	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		connection.requests.sendAuthorizeData();
		return null;
	}

}
