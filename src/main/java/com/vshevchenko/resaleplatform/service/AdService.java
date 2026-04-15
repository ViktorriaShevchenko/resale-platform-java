package com.vshevchenko.resaleplatform.service;

import org.springframework.web.multipart.MultipartFile;
import com.vshevchenko.resaleplatform.dto.Ad;
import com.vshevchenko.resaleplatform.dto.Ads;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateAd;
import com.vshevchenko.resaleplatform.dto.ExtendedAd;

public interface AdService {

    Ads getAllAds();
    Ads getAdsByUser(String email);
    ExtendedAd getAdById(Integer id);
    Ad createAd(String email, CreateOrUpdateAd createOrUpdateAd, MultipartFile image);
    Ad updateAd(Integer id, String email, CreateOrUpdateAd createOrUpdateAd);
    void deleteAd(Integer id, String email);
    void updateAdImage(Integer id, String email, MultipartFile image);
}
