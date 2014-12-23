package founderio.taam.multinet.logistics;

import codechicken.lib.vec.BlockCoord;

public interface IStation {

	String getName();

	int getStationID();

	boolean isConnectedToManager();

	void linkToManager(BlockCoord coords);
}