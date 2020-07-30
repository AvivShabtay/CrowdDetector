package acs.boundaries;

public class ElementIdBoundary {
    private String elementId;

    public ElementIdBoundary() {}

    public ElementIdBoundary(String elementId) {
        super();
        this.elementId = elementId;
    }

    public String getId() {
        return elementId;
    }

    public void setId(String elementId) {
        this.elementId = elementId;
    }
}
