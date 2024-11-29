package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.request.ClientInfoAndStatusRequest;
import com.distributionsys.backend.dtos.request.ClientInfoRequest;
import com.distributionsys.backend.dtos.response.ClientInfoAndStatusResponse;
import com.distributionsys.backend.dtos.response.ClientInfoResponse;
import com.distributionsys.backend.entities.sql.ClientInfo;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.repositories.ClientInfoRepository;
import com.distributionsys.backend.services.auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientInfoService {
    ClientInfoRepository clientInfoRepository;
    JwtService jwtService;

    // Lấy thông tin client dựa trên JWT
    public ClientInfoResponse getClientInfo(String accessToken) {
        String email = jwtService.readPayload(accessToken).get("sub");

        // Tìm client theo email và trả về ClientInfoResponse nếu có
        return clientInfoRepository.findByUserEmail(email)
                .map(client -> new ClientInfoResponse(
                        client.getFirstName(),
                        client.getLastName(),
                        client.getGender(),
                        client.getDob(),
                        client.getPhone()
                ))
                // Nếu không tìm thấy client, ném ngoại lệ với mã lỗi INVALID_TOKEN
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
    }

    // Cập nhật thông tin client
    @Transactional
    public void updateClientInfo(String accessToken, ClientInfoRequest updatedInfo) {
        String email = jwtService.readPayload(accessToken).get("sub");
        var clientInfo = clientInfoRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));

        clientInfo.setFirstName(updatedInfo.getFirstName());
        clientInfo.setLastName(updatedInfo.getLastName());
        clientInfo.setDob(updatedInfo.getDob());
        clientInfo.setGender(updatedInfo.getGender());
        clientInfo.setPhone(updatedInfo.getPhone());
        clientInfoRepository.updateClientInfoByClientInfoId(clientInfo);
    }
}
