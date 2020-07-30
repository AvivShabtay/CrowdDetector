package acs.data;

import javax.persistence.Embeddable;

@Embeddable //Embedded in ActionEntity
public class Element {
    private String elementId;

    public Element() {}

    public Element(String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null) || !(other instanceof Element)) return false;

        Element temp = (Element) other;
        return this.elementId.equals(temp.getElementId());
    }
}
