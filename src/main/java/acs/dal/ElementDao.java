package acs.dal;

import acs.data.ElementEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String> {
    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByParents_elementId(@Param("elementId") String elementId, Pageable pageable);

    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByParents_elementIdAndActive(
        @Param("elementId") String elementId,
        @Param("active") boolean active,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByParents_elementIdAndActiveAndCreatedBy_Email(
        @Param("elementId") String elementId,
        @Param("active") boolean active,
        @Param("email") String email,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByChildren_elementId(@Param("elementId") String elementId, Pageable pageable);

    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByChildren_elementIdAndActive(
        @Param("childElementId") String childElementId,
        @Param("active") boolean active,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByChildren_elementIdAndActiveAndCreatedBy_Email(
        @Param("childElementId") String childElementId,
        @Param("active") boolean active,
        @Param("email") String email,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByName(@Param("name") String name, Pageable pageable);

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByNameAndActive(
        @Param("name") String name,
        @Param("active") boolean active,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByNameAndActiveAndCreatedBy_Email(
        @Param("name") String name,
        @Param("active") boolean active,
        @Param("email") String email,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByType(@Param("type") String type, Pageable pageable);

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByTypeAndActive(
        @Param("type") String type,
        @Param("active") boolean active,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByTypeAndActiveAndLocation_LatBetweenAndLocation_LngBetweenAndCreatedBy_EmailNot(
        @Param("type") String type,
        @Param("active") boolean active,
        @Param("minLat") Double minLat,
        @Param("maxLat") Double maxLat,
        @Param("minLng") Double minLng,
        @Param("maxLng") Double maxLng,
        @Param("createdBy") String createdBy,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByTypeAndActiveAndCreatedBy_EmailNot(
        @Param("type") String type,
        @Param("active") boolean active,
        @Param("createdBy") String createdBy,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByActive(@Param("active") boolean active, Pageable pageable);

    @Transactional(readOnly = true)
    public Page<ElementEntity> findAllByActiveAndCreatedBy_Email(
        @Param("active") boolean active,
        @Param("email") String email,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByElementIdAndActive(
        @Param("elementId") String elementId,
        @Param("active") boolean active,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByElementIdAndActiveAndCreatedBy_Email(
        @Param("elementId") String elementId,
        @Param("active") boolean active,
        @Param("email") String email,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByTypeAndActiveAndCreatedBy_Email(
        @Param("type") String type,
        @Param("active") boolean active,
        @Param("email") String email,
        Pageable pageable
    );

    @Transactional(readOnly = true)
    public List<ElementEntity> findAllByElementId(@Param("elementId") String elementId, Pageable pageable);
}
