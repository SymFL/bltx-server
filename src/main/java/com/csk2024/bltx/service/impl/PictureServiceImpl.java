package com.csk2024.bltx.service.impl;

import com.csk2024.bltx.constant.Constants;
import com.csk2024.bltx.mapper.TPictureMapper;
import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.service.PictureService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {
    @Resource
    private TPictureMapper tPictureMapper;
    @Override
    public PageInfo<TPicture> getPicturesByPage(Integer current) {
        PageHelper.startPage(current, Constants.PAGE_SIZE);
        List<TPicture> list = tPictureMapper.selectPicsByPage();
        return new PageInfo<>(list);
    }

    @Override
    public int delPic(Integer id) {
        return tPictureMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int batchDel(List<String> id) {
        return tPictureMapper.batchDelete(id);
    }
}
