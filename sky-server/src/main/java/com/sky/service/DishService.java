package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {


    /**
     *
     * 菜品分页查询
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 新增菜品与对应的口味
     *
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     *
     * 菜品批量删除
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);
}
