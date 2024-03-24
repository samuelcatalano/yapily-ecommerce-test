package co.uk.yapily.service.base;

import co.uk.yapily.dto.base.BaseDto;
import co.uk.yapily.exception.ServiceException;

import java.util.List;

/**
 * BaseService interface for managing BaseEntity and BaseDto objects
 *
 * @param <T> BaseDto type
 * @author Samuel Catalano
 * @since 1.0.0
 */
public interface BaseService<T extends BaseDto> {

  /**
   * Save an entity object
   *
   * @param dto BaseDto object to be saved
   * @return the saved BaseEntity object
   * @throws ServiceException if there is an error saving the object
   */
  T save(T dto) throws ServiceException;

  /**
   * Delete an entity object by id
   *
   * @param id id of the BaseEntity object to be deleted
   * @throws ServiceException if there is an error deleting the object
   */
  void delete(Long id) throws ServiceException;

  /**
   * Find all entities objects
   *
   * @return a list of DTOs objects
   * @throws ServiceException if there is an error finding the objects
   */
  List<T> findAll() throws ServiceException;

  /**
   * Find an entity object by id
   *
   * @param id id of the BaseEntity object to be found
   * @return the BaseEntity object with the given id
   * @throws ServiceException if there is an error finding the object
   */
  T findById(Long id) throws ServiceException;
}
