package net.teamio.taam.conveyors;

import java.util.List;

public interface IConveyorApplianceHost {

	public boolean canAcceptAppliance(String type);

	public List<IConveyorAppliance> getAppliances();

	public IConveyorSlots getSlots();
}