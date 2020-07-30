package acs.boundaries;

import acs.data.DoneBy;
import acs.data.Location;
import java.util.Date;
import java.util.Map;

public class AreaBoundary extends ElementBoundary {

    public AreaBoundary(
        String elementId,
        String type,
        String name,
        Boolean active,
        Date createdTimestamp,
        DoneBy createdBy,
        Location location,
        Map<String, Object> elementAttributes
    ) {
        super(elementId, type, name, active, createdTimestamp, createdBy, location, elementAttributes);
    }
}
