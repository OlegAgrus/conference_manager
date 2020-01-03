package com.agrus.conference_manager.mapper;

public interface DtoModelMapper<Dto, Model> {

    public Dto convertToDto(Model model);

    public Model convertToModel(Dto dto);

}
