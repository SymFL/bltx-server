package com.csk2024.bltx.service;

import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.model.TUser;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PictureService {
    PageInfo<TPicture> getPicturesByPage(Integer current);

    int delPic(Integer id);

    int batchDel(List<String> id);
}
