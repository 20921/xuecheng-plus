package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
//        //将list转map,以备使用,排除根节点
//        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId()))
//                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
//        //最终返回的list
//        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
//        //依次遍历每个元素,排除根节点
//        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).forEach(item -> {
//            if (item.getParentid().equals(id)) {
//                categoryTreeDtos.add(item);
//            }
//            //找到当前节点的父节点
//            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
//            if (courseCategoryTreeDto != null) {
//                if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
//                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
//                }
//                //下边开始往ChildrenTreeNodes属性中放子节点
//                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
//            }
//        });
        //定义最终返回的list
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        //定义临时存储的map
        Map<String, CourseCategoryTreeDto> mapTemp = new HashMap<>();
        for (CourseCategoryTreeDto courseCategoryTreeDto : courseCategoryTreeDtos) {
            //存储临时节点
            mapTemp.put(courseCategoryTreeDto.getId(),courseCategoryTreeDto);
            //分离父节点
            if (courseCategoryTreeDto.getParentid().equals(id)){
                categoryTreeDtos.add(courseCategoryTreeDto);
            }
            CourseCategoryTreeDto categoryTreeDto = mapTemp.get(courseCategoryTreeDto.getParentid());
            //找出父节点下的子节点
            if (categoryTreeDto!= null){
                if (categoryTreeDto.getChildrenTreeNodes() == null){
                    categoryTreeDto.setChildrenTreeNodes(new ArrayList<>());
                }
                categoryTreeDto.getChildrenTreeNodes().add(courseCategoryTreeDto);
            }
        }
        return categoryTreeDtos;
    }
}

