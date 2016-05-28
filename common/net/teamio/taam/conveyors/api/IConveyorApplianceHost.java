package net.teamio.taam.conveyors.api;

import java.util.List;

public interface IConveyorApplianceHost extends IConveyorSlots {

	public boolean canAcceptAppliance(String type);

	public List<IConveyorAppliance> getAppliances();

}