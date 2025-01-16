package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dao.ClothRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepository clothRepository;
}
