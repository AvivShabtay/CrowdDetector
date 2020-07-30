package acs.logic;

import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.data.DoneBy;
import acs.data.ElementEntity;
import acs.data.ElementEntityConverter;
import acs.data.Location;
import acs.data.UserRole;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ElementServiceWithDb implements ElementServiceExtended {
    private ElementEntityConverter elementEntityConverter;
    private ElementDao elementDao;
    private UserServiceExtended userService;

    public static final int PAGE = 0;
    public static final int SIZE = Integer.MAX_VALUE;

    @Autowired
    public ElementServiceWithDb(ElementDao elementDao, UserDao userDao, UserServiceExtended userService) {
        this.elementDao = elementDao;
        this.userService = userService;
    }

    @Autowired
    public void setElementEntityConverter(ElementEntityConverter elementEntityConverter) {
        this.elementEntityConverter = elementEntityConverter;
    }

    @Override
    @Transactional(readOnly = false)
    public ElementBoundary create(ElementBoundary element, DoneBy createdBy) {
        if (element.getName() == null) {
            throw new MissingAttributeException("Name is required");
        }

        String newId;
        if (element.getElementId() == null) {
            newId = UUID.randomUUID().toString();
        } else {
            newId = element.getElementId();
        }
        ElementEntity elementEntity = this.elementEntityConverter.convertBoundaryToEntity(element);
        elementEntity.setElementId(newId);
        elementEntity.setCreatedBy(createdBy);
        elementEntity.setCreatedTimestamp(new Date());
        ElementEntity newElement = this.elementDao.save(elementEntity);

        return this.elementEntityConverter.convertEntityToBoundary(newElement);
    }

    @Override
    @Transactional(readOnly = false)
    public ElementBoundary create(String managerEmail, ElementBoundary element) {
        this.userService.identifyUserRoleByEmail(managerEmail, Arrays.asList(UserRole.MANAGER));

        return this.create(element, new DoneBy(managerEmail));
    }

    @Override
    @Transactional(readOnly = false)
    public void update(String elementId, ElementBoundary update) {
        Optional<ElementEntity> foundElement = this.elementDao.findById(elementId);

        if (!foundElement.isPresent()) {
            throw new ElementNotFoundException("Couldn't find Element with ID: " + elementId);
        }

        ElementBoundary element = this.elementEntityConverter.convertEntityToBoundary(foundElement.get());

        boolean isDirty = false;

        if (update.getType() != null) {
            element.setType(update.getType());
            isDirty = true;
        }

        if (update.getName() != null) {
            element.setName(update.getName());
            isDirty = true;
        }

        if (update.getActive() != null) {
            element.setActive(update.getActive());
            isDirty = true;
        }

        if (update.getLocation() != null) {
            element.setLocation(update.getLocation());
            isDirty = true;
        }

        if (update.getElementAttributes() != null) {
            element.setElementAttributes(update.getElementAttributes());
            isDirty = true;
        }

        if (isDirty) {
            this.elementDao.save(this.elementEntityConverter.convertBoundaryToEntity(element));
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void update(String managerEmail, String elementId, ElementBoundary update) {
        this.userService.identifyUserRoleByEmail(managerEmail, Arrays.asList(UserRole.MANAGER));
        this.update(elementId, update);
    }

    @Override
    @Transactional(readOnly = false)
    public void inactiveByTypeAndEmail(String userEmail, String type, Object page, Object size) {
        int pages = ((page == null) ? PAGE : Integer.parseInt(page.toString()));
        int sizes = ((size == null) ? SIZE : Integer.parseInt(size.toString()));

        this.getAllByTypeAndEmail(userEmail, type, pages, sizes)
            .stream()
            .map(
                element -> {
                    element.setActive(false);
                    this.update(element.getElementId(), element);
                    return element;
                }
            )
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementBoundary> getAll(String userEmail) {
        UserBoundary contexUser =
            this.userService.identifyUserRoleByEmail(userEmail, Arrays.asList(UserRole.MANAGER, UserRole.PLAYER));

        if (contexUser.getRole().equals(UserRole.MANAGER.name())) {
            return StreamSupport
                .stream(this.elementDao.findAll().spliterator(), false)
                .map(elementEntity -> this.elementEntityConverter.convertEntityToBoundary(elementEntity))
                .collect(Collectors.toList());
        } else {
            return this.elementDao.findAllByActiveAndCreatedBy_Email(
                    true,
                    contexUser.getEmail(),
                    PageRequest.of(PAGE, SIZE, Direction.DESC, "createdTimestamp", "elementId")
                )
                .getContent()
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ElementBoundary> getAll(String userEmail, int page, int size) {
        UserBoundary contexUser =
            this.userService.identifyUserRoleByEmail(userEmail, Arrays.asList(UserRole.PLAYER, UserRole.MANAGER));

        if (contexUser.getRole().equals(UserRole.MANAGER.name())) {
            return this.elementDao.findAll(PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId"))
                .getContent()
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        } else {
            return this.elementDao.findAllByActiveAndCreatedBy_Email(
                    true,
                    contexUser.getEmail(),
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
                )
                .getContent()
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ElementBoundary getSpecificElement(String userEmail, String elementId) {
        UserBoundary contexUser =
            this.userService.identifyUserRoleByEmail(userEmail, Arrays.asList(UserRole.MANAGER, UserRole.PLAYER));

        List<ElementBoundary> element;
        if (contexUser.getRole().equals(UserRole.MANAGER.name())) {
            element =
                StreamSupport
                    .stream(
                        this.elementDao.findAllByElementId(
                                elementId,
                                PageRequest.of(PAGE, SIZE, Direction.DESC, "createdTimestamp", "elementId")
                            )
                            .spliterator(),
                        false
                    )
                    .map(elementEntity -> this.elementEntityConverter.convertEntityToBoundary(elementEntity))
                    .collect(Collectors.toList());
        } else {
            element =
                StreamSupport
                    .stream(
                        this.elementDao.findAllByElementIdAndActiveAndCreatedBy_Email(
                                elementId,
                                true,
                                contexUser.getEmail(),
                                PageRequest.of(PAGE, SIZE, Direction.DESC, "createdTimestamp", "elementId")
                            )
                            .spliterator(),
                        false
                    )
                    .map(elementEntity -> this.elementEntityConverter.convertEntityToBoundary(elementEntity))
                    .collect(Collectors.toList());
        }

        if (element.isEmpty()) {
            throw new ElementNotFoundException("Couldn't find Element with ID: " + elementId);
        }
        return element.get(0);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllElements(String adminEmail) {
        this.userService.identifyUserRoleByEmail(adminEmail, Arrays.asList(UserRole.ADMIN));

        this.elementDao.deleteAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void addChildToElement(String managerEmail, String parentElementId, String childElementId) {
        this.userService.identifyUserRoleByEmail(managerEmail, Arrays.asList(UserRole.MANAGER));

        if (parentElementId != null && parentElementId.equals(childElementId)) {
            throw new ElemenGeneralException("Element cannot be a child of himself!");
        }
        ElementEntity parent = getElementById(parentElementId);

        ElementEntity child = getElementById(childElementId);

        parent.addChildren(child);

        this.elementDao.save(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ElementBoundary> getAllElementChildren(String userEmail, String parentElementId, int page, int size) {
        UserBoundary contexUser =
            this.userService.identifyUserRoleByEmail(userEmail, Arrays.asList(UserRole.MANAGER, UserRole.PLAYER));

        if (contexUser.getRole().equals(UserRole.MANAGER.name())) {
            return this.elementDao.findAllByParents_elementId(
                    parentElementId,
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
                )
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        } else {
            return this.elementDao.findAllByParents_elementIdAndActiveAndCreatedBy_Email(
                    parentElementId,
                    true,
                    contexUser.getEmail(),
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
                )
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ElementBoundary> getAllElementParents(String userEmail, String childElementId, int page, int size) {
        UserBoundary contexUser =
            this.userService.identifyUserRoleByEmail(userEmail, Arrays.asList(UserRole.MANAGER, UserRole.PLAYER));

        if (contexUser.getRole().equals(UserRole.MANAGER.name())) {
            return this.elementDao.findAllByChildren_elementId(
                    childElementId,
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
                )
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        } else {
            return this.elementDao.findAllByChildren_elementIdAndActiveAndCreatedBy_Email(
                    childElementId,
                    true,
                    contexUser.getEmail(),
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
                )
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ElementBoundary> getAllByName(String userEmail, String name, int page, int size) {
        UserBoundary contexUser =
            this.userService.identifyUserRoleByEmail(userEmail, Arrays.asList(UserRole.MANAGER, UserRole.PLAYER));
        if (contexUser.getRole().equals(UserRole.MANAGER.name())) {
            return this.elementDao.findAllByName(
                    name,
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
                )
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        } else {
            return this.elementDao.findAllByNameAndActiveAndCreatedBy_Email(
                    name,
                    true,
                    contexUser.getEmail(),
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
                )
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ElementBoundary> getAllByType(String userEmail, String type, int page, int size) {
        UserBoundary contexUser =
            this.userService.identifyUserRoleByEmail(userEmail, Arrays.asList(UserRole.MANAGER, UserRole.PLAYER));
        if (contexUser.getRole().equals(UserRole.MANAGER.name())) {
            return this.elementDao.findAllByType(type, PageRequest.of(page, size, Direction.DESC, "createdTimestamp"))
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        } else {
            return this.elementDao.findAllByTypeAndActiveAndCreatedBy_Email(
                    type,
                    true,
                    contexUser.getEmail(),
                    PageRequest.of(page, size, Direction.DESC, "createdTimestamp")
                )
                .stream()
                .map(this.elementEntityConverter::convertEntityToBoundary)
                .collect(Collectors.toSet());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ElementBoundary> getAllByTypeAndEmail(String userEmail, String type, int page, int size) {
        return this.elementDao.findAllByTypeAndActiveAndCreatedBy_Email(
                type,
                true,
                userEmail,
                PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId")
            )
            .stream()
            .map(this.elementEntityConverter::convertEntityToBoundary)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ElementBoundary> getAllByTypeAndRadius(
        DoneBy createdBy,
        String type,
        Location location,
        Double radius,
        Object page,
        Object size
    ) {
        int pages = ((page == null) ? PAGE : Integer.parseInt(page.toString()));
        int sizes = ((size == null) ? SIZE : Integer.parseInt(size.toString()));

        return this.elementDao.findAllByTypeAndActiveAndCreatedBy_EmailNot(
                type,
                true,
                createdBy.getEmail(),
                PageRequest.of(pages, sizes, Direction.DESC, "createdTimestamp", "elementId")
            )
            .stream()
            .filter(element -> UserEnvironmentAction.calcDistance(location, element.getLocation()) <= radius)
            .map(this.elementEntityConverter::convertEntityToBoundary)
            .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    private ElementEntity getElementById(String elementId) {
        return this.elementDao.findById(elementId)
            .orElseThrow(() -> new ElementNotFoundException("Could not find element for id: " + elementId));
    }
}
