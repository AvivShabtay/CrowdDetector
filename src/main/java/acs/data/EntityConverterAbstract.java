package acs.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;

public abstract class EntityConverterAbstract<Entity, Boundary> {
    private ObjectMapper jackson;

    @PostConstruct
    public void setup() {
        this.jackson = new ObjectMapper();
    }

    public ObjectMapper getJackson() {
        return jackson;
    }

    public void setJackson(ObjectMapper jackson) {
        this.jackson = jackson;
    }

    public abstract Boundary convertEntityToBoundary(Entity entity);

    public abstract Entity convertBoundaryToEntity(Boundary boundary);
}
