package com.merchant.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.service.common.APIResponse;
import com.merchant.service.common.ErrorResponses;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.model.AuthTokenModel;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtil;
    private ObjectMapper mapper;
    private AuthTokenModel authTokenModel;

    public JwtAuthorizationFilter(JwtUtils jwtUtil, ObjectMapper mapper, AuthTokenModel authTokenModel) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.authTokenModel = authTokenModel;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> errorDetails = new HashMap<>();
        String path = request.getRequestURI();
        try {
            String accessToken = jwtUtil.resolveToken(request);
            if (path.contains("/login/auth/") || path.contains("/api/")) {
                filterChain.doFilter(request, response);
                return;
            }else if(accessToken == null){
                handleAccessDeniedResponse(response, "Token is missing");
                return;
            }
            //System.out.println("token : "+accessToken);
            Claims claims = jwtUtil.resolveClaims(request,response);

            authTokenModel.setMobile(jwtUtil.getMobile(claims));
            authTokenModel.setUser_id(jwtUtil.getUserId(claims));
            authTokenModel.setApp_id(jwtUtil.getAppId(claims));
            authTokenModel.setMid(jwtUtil.getMid(claims));

            if(claims != null/* & jwtUtil.validateClaims(claims)*/){
                String email = claims.getSubject();
                //System.out.println("email : "+email);
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(email,"",new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            APIResponse responses = new APIResponse();
            responses.setStatus(false);
            responses.setCode(HttpStatus.FORBIDDEN.value());
            responses.setData("Invalid Token");
            ErrorResponses errorResponse = new ErrorResponses(ErrorCode.ACCESS_DENIED);
            errorResponse.additionalInfo.excepText = "Access Denied";
            responses.setError(errorResponse);
           // errorDetails.put("response",responses);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), responses);

            /*errorDetails.put("status",HttpStatus.FORBIDDEN.value());
            errorDetails.put("error", "Access is denied");
            errorDetails.put("data",e.getMessage());*/

//            APIResponse responses = new APIResponse();
//            responses.setStatus(HttpStatus.FORBIDDEN.value());
//            responses.setData(e.getMessage());
//            responses.setError("Access is denied");
//            response = (HttpServletResponse) responses;
//            mapper.writeValue(response.getWriter(), errorDetails);
           // throw new AccessDeniedException("Access Denied");
        }
        filterChain.doFilter(request, response);
    }

    /*@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = jwtUtil.resolveToken(request);
            if (accessToken == null) {
                handleAccessDeniedResponse(response, "Token is missing");
                return;
            }

            Claims claims = jwtUtil.resolveClaims(request, response);

            authTokenModel.setMobile(jwtUtil.getMobile(claims));
            authTokenModel.setUser_id(jwtUtil.getUserId(claims));

            if (claims != null) {
                String email = claims.getSubject();
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, "", new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            handleAccessDeniedResponse(response, "Invalid Token");
            return;
        }

        filterChain.doFilter(request, response);
    }*/

    private void handleAccessDeniedResponse(HttpServletResponse response, String errorMessage) throws IOException {
        APIResponse responses = new APIResponse();
        responses.setStatus(false);
        responses.setCode(HttpStatus.FORBIDDEN.value());
        responses.setData(null);
        ErrorResponses errorResponse = new ErrorResponses(ErrorCode.ACCESS_DENIED);
        errorResponse.additionalInfo.excepText = "Access Denied";
        responses.setError(errorResponse);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), responses);
    }

}





