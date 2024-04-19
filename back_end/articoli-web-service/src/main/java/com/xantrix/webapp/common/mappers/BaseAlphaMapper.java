package com.xantrix.webapp.common.mappers;

public abstract class BaseAlphaMapper<E, M> implements IAlphaMapper<E, M> {

	@Override
	public M toModel(E entity) {
		return toModel(entity, null);
	}

	@Override
	public E toEntity(M model) {
		return toEntity(model, null);
	}
}
