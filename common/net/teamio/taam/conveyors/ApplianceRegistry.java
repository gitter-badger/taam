package net.teamio.taam.conveyors;

import java.util.HashMap;
import java.util.Map;

public class ApplianceRegistry {
	private ApplianceRegistry() {
		// Util Class
	}

	private static Map<String, IConveyorApplianceFactory> applianceFactories;

	static {
		applianceFactories = new HashMap<String, IConveyorApplianceFactory>();
	}

	public static void registerFactory(String name, IConveyorApplianceFactory factory) {
		if (applianceFactories.containsKey(name)) {
			throw new RuntimeException("Duplicate registration of appliance factory: " + name + " Previously registered: " + applianceFactories.get(name));
		}
		applianceFactories.put(name, factory);
	}

	public static IConveyorApplianceFactory getFactory(String name) {
		return applianceFactories.get(name);
	}
}