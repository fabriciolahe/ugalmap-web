package eu.crystalsystem.ugalmap.rest.response;

import java.util.List;

import eu.crystalsystem.ugalmap.models.BuildingCoordinates;
import eu.crystalsystem.ugalmap.models.GenericHeader;
import eu.crystalsystem.ugalmap.models.GenericResponse;

public class BuildingCoordinatesResponse extends GenericResponse {
	
	private List<BuildingCoordinates> buildingCoordinates;
	
	public BuildingCoordinatesResponse() {
	}

	public BuildingCoordinatesResponse(GenericHeader genericHeader) {
		super(genericHeader);
	}
	
	public BuildingCoordinatesResponse(GenericHeader genericHeader, List<BuildingCoordinates> buildingCoordinates) {
		super(genericHeader);
		this.buildingCoordinates = buildingCoordinates;
	}

	public List<BuildingCoordinates> getBuildingCoordinates() {
		return buildingCoordinates;
	}

	public void setBuildingCoordinates(List<BuildingCoordinates> buildingCoordinates) {
		this.buildingCoordinates = buildingCoordinates;
	}
	
}
