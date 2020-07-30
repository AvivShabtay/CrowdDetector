package acs.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {
    private String elementId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private DoneBy createdBy;
    private Location location;
    private String elementAttributes; // Map->String
    private Set<ElementEntity> parents;
    private Set<ElementEntity> children;

    public ElementEntity() {
        this.children = new HashSet<>();
        this.parents = new HashSet<>();
    }

    public ElementEntity(
        String elementId,
        String type,
        String name,
        Boolean active,
        Date createdTimestamp,
        DoneBy createdBy,
        Location location,
        String elementAttributes,
        Set<ElementEntity> parents,
        Set<ElementEntity> children
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
        this.parents = parents;
        this.children = children;
    }

    @Id
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Embedded
    public DoneBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DoneBy createdBy) {
        this.createdBy = createdBy;
    }

    @Embedded
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Lob
    public String getElementAttributes() {
        return elementAttributes;
    }

    public void setElementAttributes(String elementAttributes) {
        this.elementAttributes = elementAttributes;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    public Set<ElementEntity> getParents() {
        return parents;
    }

    public void setParents(Set<ElementEntity> parents) {
        this.parents = parents;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    public Set<ElementEntity> getChildren() {
        return children;
    }

    public void setChildren(Set<ElementEntity> children) {
        this.children = children;
    }

    public void addParent(ElementEntity parent) {
        this.parents.add(parent);
    }

    public void addChildren(ElementEntity child) {
        this.children.add(child);
        child.addParent(this);
    }
}
