package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.model.repository.ClothFolderRepository;
import com.clokey.server.domain.model.repository.ClothImageRepository;
import com.clokey.server.domain.model.repository.ClothRepository;
import com.clokey.server.domain.model.entity.Cloth;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.domain.model.repository.HistoryClothRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepository clothRepository;
    private final ClothImageRepository clothImageRepository;
    private final ClothFolderRepository clothFolderRepository;
    private final HistoryClothRepository historyClothRepository;
    //private final S3Uploader s3Uploader; // S3 업로드를 위한 의존성

    @Override
    public boolean clothExist(Long clothId) {
        System.out.println(clothId);
        return clothRepository.existsById(clothId);
    }

    @Override
    public boolean isPublic(Long clothId) {
        Visibility visibility = clothRepository.findById(clothId)
                .get()
                .getVisibility();
        return visibility.equals(Visibility.PUBLIC);
    }

//    public ClothResponseDto.ClothCreateResult createCloth(ClothRequestDto.ClothCreateRequest request, MultipartFile imageFile) {
//
//        // Cloth 엔티티 생성
//        Cloth cloth = clothRepository.save(ClothConverter.toClothCreateRequest(request));
//
//        // 이미지 업로드 후 URL 저장
//        String imageUrl = s3Uploader.upload(imageFile, "cloth-images");
//        ClothImage clothImage = ClothImage.builder()
//                .imageUrl(imageUrl)
//                .cloth(cloth)
//                .build();
//        clothImageRepository.save(clothImage);
//
//        return ClothConverter.toResponse(cloth);
//    }

    public Optional<Cloth> readClothById(Long clothId) {
        return clothRepository.findById(clothId);
    }

    @Transactional
    public void deleteClothById(Long clothId){
        historyClothRepository.deleteAllByClothId(clothId);
        clothFolderRepository.deleteAllByClothId(clothId);
        clothImageRepository.deleteAllByClothId(clothId);
        clothRepository.deleteById(clothId);
    }
}
