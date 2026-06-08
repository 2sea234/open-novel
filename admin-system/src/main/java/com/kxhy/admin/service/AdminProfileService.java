package com.kxhy.admin.service;

import com.kxhy.admin.domain.dto.AdminProfilePasswordDTO;
import com.kxhy.admin.domain.dto.AdminProfileUpdateDTO;
import com.kxhy.admin.domain.vo.AdminProfileVO;

public interface AdminProfileService {

    AdminProfileVO selectProfileById(Long adminId);

    void updateProfile(Long adminId, AdminProfileUpdateDTO dto, String username);

    void updateProfilePassword(Long adminId, AdminProfilePasswordDTO dto, String username);

}
