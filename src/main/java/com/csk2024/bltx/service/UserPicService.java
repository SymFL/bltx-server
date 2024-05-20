package com.csk2024.bltx.service;

import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.query.PictureQuery;
import com.csk2024.bltx.result.R;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserPicService {
    PageInfo<TPicture> getPicturesByPage(PictureQuery pictureQuery);

    int delPic(Integer id);

    int batchDel(List<String> id);

    R upload(PictureQuery pictureQuery);

    PictureQuery info(Integer id);

    int predict(Integer id);
}
