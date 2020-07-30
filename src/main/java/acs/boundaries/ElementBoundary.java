package acs.boundaries;

import acs.data.DoneBy;
import acs.data.Location;
import java.util.Date;
import java.util.Map;

public class ElementBoundary {
    private String elementId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private DoneBy createdBy;
    private Location location;
    private Map<String, Object> elementAttributes;

    public ElementBoundary() {}

    public ElementBoundary(
        String elementId,
        String type,
        String name,
        Boolean active,
        Date createdTimestamp,
        DoneBy createdBy,
        Location location,
        Map<String, Object> elementAttributes
    ) {
        super();
        this.elementId = elementId;
        this.type = type;
        this.name = name;
        this.active = active;
        this.createdTimestamp = createdTimestamp;
        this.createdBy = createdBy;
        this.location = location;
        this.elementAttributes = elementAttributes;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String id) {
        this.elementId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public DoneBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DoneBy createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Object> getElementAttributes() {
        return elementAttributes;
    }

    public void setElementAttributes(Map<String, Object> elementAttributes) {
        this.elementAttributes = elementAttributes;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new ElementBoundary(
            this.elementId,
            this.type,
            this.name,
            this.active,
            this.createdTimestamp,
            this.createdBy,
            this.location,
            this.elementAttributes
        );
    }

    @Override
    public String toString() {
        return (
            "ElementBoundary [elementId=" +
            elementId +
            ", type=" +
            type +
            ", name=" +
            name +
            ", active=" +
            active +
            ", createdTimestamp=" +
            createdTimestamp +
            ", createdBy=" +
            createdBy +
            ", location=" +
            location +
            ", elementAttributes=" +
            elementAttributes +
            "]"
        );
    }
}
