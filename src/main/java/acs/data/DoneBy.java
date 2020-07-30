package acs.data;

import javax.persistence.Embeddable;

@Embeddable
public class DoneBy {
    private String email;

    public DoneBy() {}

    public DoneBy(String email) {
        super();
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null) || !(other instanceof DoneBy)) return false;

        DoneBy temp = (DoneBy) other;
        return this.email.equals(temp.getEmail());
    }
}
