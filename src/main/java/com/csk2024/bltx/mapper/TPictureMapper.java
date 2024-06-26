package com.csk2024.bltx.mapper;

import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.query.PictureQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TPictureMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TPicture record);

    int insertSelective(TPicture record);

    TPicture selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TPicture record);

    int updateByPrimaryKey(TPicture record);

    List<TPicture> selectPicsByPage(@Param("pictureQuery") PictureQuery pictureQuery);

    int batchDelete(@Param("ids") List<String> id);

    List<TPicture> selectUserPicsByPage(@Param("pictureQuery") PictureQuery pictureQuery);
}