package acs.init;

import acs.boundaries.ElementBoundary;
import acs.data.Location;
import acs.logic.ElementServiceExtended;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("elementChildrenManualTests")
public class ElementChildrenInitializer implements CommandLineRunner {
    private ElementServiceExtended elementService;

    public ElementChildrenInitializer() {}

    @Autowired
    public void setElementService(ElementServiceExtended elementService) {
        this.elementService = elementService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<ElementBoundary> allElements = IntStream
            .range(0, 10)
            .mapToObj(
                i ->
                    new ElementBoundary(
                        "" + i,
                        "personEntity",
                        "Alon",
                        true,
                        null,
                        null,
                        new Location(32.115129, 34.871529),
                        null
                    )
            )
            .map(element -> this.elementService.create("TestUserManager@tst.com", element))
            .collect(Collectors.toList());

        for (int j = 1; j < allElements.toArray().length; j++) {
            this.elementService.addChildToElement(
                    "TestUserManager@tst.com",
                    allElements.get(j).getElementId(),
                    allElements.get(0).getElementId()
                );
        }
        for (int j = 1; j < allElements.toArray().length; j++) {
            this.elementService.addChildToElement(
                    "TestUserManager@tst.com",
                    allElements.get(0).getElementId(),
                    allElements.get(j).getElementId()
                );
        }
    }
}
