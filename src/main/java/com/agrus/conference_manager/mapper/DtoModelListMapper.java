package com.agrus.conference_manager.mapper;

import java.util.List;

public interface DtoModelListMapper<Dto, Model> extends DtoModelMapper<Dto, Model> {

    public List<Dto> convertToDtoList(List<Model> modelList);

    public List<Model> convertToModelList(List<Dto> dtoList);

}
