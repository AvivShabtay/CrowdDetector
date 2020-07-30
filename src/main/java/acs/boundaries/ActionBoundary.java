package acs.boundaries;

import acs.data.DoneBy;
import acs.data.Element;
import java.util.Date;
import java.util.Map;

public class ActionBoundary {
    private String actionId;
    private String type;
    private Element element;
    private Date createdTimestamp;
    private DoneBy invokedBy;
    private Map<String, Object> actionAttributes;

    public ActionBoundary() {}

    public ActionBoundary(
        String actionId,
        String type,
        Element element,
        Date createdTimestamp,
        DoneBy invokedBy,
        Map<String, Object> actionAttributes
    ) {
        super();
        this.actionId = actionId;
        this.type = type;
        this.element = element;
        this.createdTimestamp = createdTimestamp;
        this.invokedBy = invokedBy;
        this.actionAttributes = actionAttributes;
    }

    public ActionBoundary(String type, DoneBy invokedBy, Map<String, Object> actionAttributes) {
        super();
        this.type = type;
        this.invokedBy = invokedBy;
        this.actionAttributes = actionAttributes;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DoneBy getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(DoneBy invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Map<String, Object> getActionAttributes() {
        return actionAttributes;
    }

    public void setActionAttributes(Map<String, Object> actionAttributes) {
        this.actionAttributes = actionAttributes;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new ActionBoundary(
            this.actionId,
            this.type,
            this.element,
            this.createdTimestamp,
            this.invokedBy,
            this.actionAttributes
        );
    }

    @Override
    public String toString() {
        return (
            "ActionBoundary [actionId=" +
            actionId +
            ", type=" +
            type +
            ", element=" +
            element +
            ", createdTimestamp=" +
            createdTimestamp +
            ", invokedBy=" +
            invokedBy +
            ", actionAttributes=" +
            actionAttributes +
            "]"
        );
    }
}
