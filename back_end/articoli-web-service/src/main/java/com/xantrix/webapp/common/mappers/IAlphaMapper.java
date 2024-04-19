package com.xantrix.webapp.common.mappers;

public interface IAlphaMapper<E, M> {
	/**
	 * Creates a new Model starting from the entity
	 * 
	 * @param entity entity to convert
	 * @return the model
	 */
	M toModel(E entity);

	/**
	 * Updates the passed model
	 * 
	 * @param entity entity to convert
	 * @param model model to update
	 * @return updated model
	 */
	M toModel(E entity, M model);
	
	/**
	 * Creates new entity starting from the model
	 * 
	 * @param model model to convert
	 * @return the entity
	 */
	E toEntity(M model);
	
	/**
	 * Updates the passed entity
	 * 
	 * @param model model to convert
	 * @param entity entity to update
	 * @return the updated entity
	 */
	E toEntity(M model, E entity);
	
}
