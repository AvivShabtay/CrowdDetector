package acs.init;

import acs.boundaries.ElementBoundary;
import acs.logic.ElementServiceExtended;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("elementManualTests")
public class ElementInitializer implements CommandLineRunner {
    private ElementServiceExtended elementService;

    public ElementInitializer() {}

    @Autowired
    public void setElementService(ElementServiceExtended elementService) {
        this.elementService = elementService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<ElementBoundary> allelements = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            ElementBoundary newElement = new ElementBoundary();
            newElement.setName("message #" + i);
            newElement.setElementId("" + i);
            newElement = this.elementService.create("##", newElement);
            allelements.add(newElement);
        }

        this.elementService.addChildToElement(
                "TestUserManager@tst.com",
                allelements.get(0).getElementId(),
                allelements.get(1).getElementId()
            );

        this.elementService.addChildToElement(
                "TestUserManager@tst.com",
                allelements.get(0).getElementId(),
                allelements.get(2).getElementId()
            );
    }
}
