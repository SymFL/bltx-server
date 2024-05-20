package com.csk2024.bltx.service;

import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.query.PictureQuery;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PictureService {
    PageInfo<TPicture> getPicturesByPage(PictureQuery pictureQuery);

    int delPic(Integer id);

    int batchDel(List<String> id);

    PictureQuery info(Integer id);
}
