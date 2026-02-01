package com.example.unicode.service.impl;

import com.example.unicode.configuration.MomoConfig;
import com.example.unicode.dto.request.MomoRequest;
import com.example.unicode.dto.request.SearchSubcriptionRequest;
import com.example.unicode.dto.request.SubcriptionReportRequest;
import com.example.unicode.dto.response.SubcriptionReportResponse;
import com.example.unicode.dto.response.SubcriptionResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Subcription;
import com.example.unicode.entity.Summaries;
import com.example.unicode.entity.Users;
import com.example.unicode.enums.StatusPayment;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.SubcriptionMapper;
import com.example.unicode.mapper.SumariesMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.SubcriptionRepository;
import com.example.unicode.repository.SumariesRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.SubcriptionService;
import com.example.unicode.specification.SubcripSpecification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubcriptionServiceImpl implements SubcriptionService {
    private final SubcriptionRepository subcriptionRepository;
    private final SubcriptionMapper subcriptionMapper;
    private final MomoConfig config;
    private final RestTemplate restTemplate = new RestTemplate();
    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;
    private final SumariesRepository sumariesRepository;
    private final SumariesMapper  sumariesMapper;

    @Override
    public String buyCourses(UUID courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByEmail(email);
        if(users == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Course course = courseRepository.findById(courseId).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        Subcription subcription = Subcription.builder()
                .course(course)
                .learner(users)
                .subcriptionPrice(Math.round(course.getPrice()))
                .content("Payment for course "+ course.getTitle())
                .build();
        subcriptionRepository.save(subcription);
        String orderId = subcription.getSubcriptionId().toString();
        String requestId = UUID.randomUUID().toString();
        String extraData = "";

        String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                config.getAccessKey(), subcription.getSubcriptionPrice(), extraData, "ipUrl",
                orderId, subcription.getContent(), config.getPartnerCode(),
                config.getRedirectUrl(), requestId, config.getRequestType()
        );
        log.info("Raw {}", rawSignature);
        String signature;
        try {
            signature = hmacSHA256(rawSignature, config.getSecretKey());

        } catch (Exception e) {
            throw new AppException(ErrorCode.PAYMENT_HASH_DATA_FAIL);
        }
        log.info("Signature {}", signature);
        MomoRequest request = new MomoRequest();
        request.setPartnerCode(config.getPartnerCode());
        request.setAccessKey(config.getAccessKey());
        request.setRequestId(requestId);
        request.setOrderId(orderId);
        request.setAmount(subcription.getSubcriptionPrice());
        request.setOrderInfo(subcription.getContent());
        request.setRedirectUrl(config.getRedirectUrl());
        request.setIpnUrl("ipUrl");
        request.setRequestType(config.getRequestType());
        request.setExtraData(extraData);
        request.setSignature(signature);
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        log.info(" Request {}",request.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MomoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                config.getEndPoint() + "/create",
                entity,
                Map.class
        );

        return response.getBody().get("payUrl").toString();
    }

    @Override
    public SubcriptionResponse updateStatus(HttpServletRequest request) {
        if(!veryfySignature(request)) {
            throw new AppException(ErrorCode.VERIFY_SIGN_FAIL);
        }
        int code = Integer.parseInt(request.getParameter("resultCode"));
        Subcription subcription = subcriptionRepository.findById(UUID.fromString(request.getParameter("orderId"))).orElseThrow(
                () -> new AppException(ErrorCode.SUBCRIPTION_NOT_FOUND)

        );
        subcription.setPayType(request.getParameter("payType"));
        subcription.setMessage(request.getParameter("message"));
        if(code == 0) {
            subcription.setStatusPayment(StatusPayment.SUCCESS);
        }else{
            subcription.setStatusPayment(StatusPayment.ERROR);
        }
        return subcriptionMapper.entityToResponse(subcriptionRepository.save(subcription));
    }

    @Override
    public Page<SubcriptionResponse> getAll(SearchSubcriptionRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        if(request == null)
        {
            return subcriptionRepository.findAll(pageable).map(subcriptionMapper::entityToResponse);
        }
        Specification<Subcription> spe = Specification.allOf(
                SubcripSpecification.searchByCoursId(request.getCourseId()),
                SubcripSpecification.searchByLernerId(request.getLearnerId()),
                SubcripSpecification.searchByStatusPayment(request.getStatusPayment()),
             SubcripSpecification.searchByDate(request.getFrom().atStartOfDay(),request.getTo().plusDays(1).atStartOfDay())
                );
        return subcriptionRepository.findAll(spe,pageable).map(subcriptionMapper::entityToResponse);
    }

    @Override
    public SubcriptionReportResponse report(SubcriptionReportRequest request) {
        SubcriptionReportResponse response  = SubcriptionReportResponse.builder().build();
        List<Summaries> summaries = sumariesRepository.findByLocalDateBetween(request.getFrom(),request.getTo());
        if(request.getTo().equals(LocalDate.now()))
        {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDateTime.now();
            Summaries s = Summaries.builder()
                    .localDate(LocalDate.now())
                    .totalPayment(subcriptionRepository.countByCreatedAtBetween(start,end))
                    .error(subcriptionRepository.countByCreatedAtBetweenAndStatusPayment(start,end, StatusPayment.ERROR))
                    .success(subcriptionRepository.countByCreatedAtBetweenAndStatusPayment(start,end, StatusPayment.SUCCESS))
                    .totalAmount(subcriptionRepository.sumBySubcriptionDate(start,end))
                    .build();
            summaries.add(s);
        }
        if(summaries != null)
        {
            summaries.forEach((s)->{
                response.setTotalError(response.getTotalError() + s.getError());
                response.setTotalSuccess(response.getTotalSuccess() + s.getSuccess());
                response.setTotalPayment(response.getTotalPayment()+ s.getTotalPayment());
                response.setTotalAmount(response.getTotalAmount()+ s.getTotalAmount());

            });
            response.setTotalPending(response.getTotalPayment() -(response.getTotalPending()+ response.getTotalError()));
        }
        response.setData(summaries.stream().map(sumariesMapper::entityToResponse).toList());
        return response;
    }

    @Override
    public SubcriptionResponse getById(UUID id) {
        Subcription subcription = subcriptionRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.SUBCRIPTION_NOT_FOUND)
        );
        return subcriptionMapper.entityToResponse(subcription);
    }

    public boolean veryfySignature(HttpServletRequest request) {
        Subcription subcription = subcriptionRepository.findById(UUID.fromString(request.getParameter("orderId"))).orElseThrow(
                () -> new AppException(ErrorCode.SUBCRIPTION_NOT_FOUND)

        );
        String receivedSignature = request.getParameter("signature");
        if (receivedSignature == null) return false;

        try {
            String rawData = "accessKey=" + config.getAccessKey() +
                    "&amount=" + request.getParameter("amount") +
                    "&extraData=" + request.getParameter("extraData") +
                    "&message=" + request.getParameter("message") +
                    "&orderId=" + request.getParameter("orderId") +
                    "&orderInfo=" + request.getParameter("orderInfo") +
                    "&orderType=" + request.getParameter("orderType") +
                    "&partnerCode=" + request.getParameter("partnerCode") +
                    "&payType=" + request.getParameter("payType") +
                    "&requestId=" + request.getParameter("requestId") +
                    "&responseTime=" + request.getParameter("responseTime") +
                    "&resultCode=" + request.getParameter("resultCode") +
                    "&transId=" + request.getParameter("transId");

            String hash = hmacSHA256(rawData, config.getSecretKey());

            log.info("RAW = {}", rawData);
            log.info("HASH = {}", hash);
            log.info("RECEIVED = {}", receivedSignature);

            return receivedSignature.equalsIgnoreCase(hash);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder(2 * rawHmac.length);
        for (byte b : rawHmac) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) hex.append('0');
            hex.append(s);
        }
        return hex.toString();
    }

}
