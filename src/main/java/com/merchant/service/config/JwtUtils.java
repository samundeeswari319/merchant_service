package com.merchant.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.service.common.APIResponse;
import com.merchant.service.common.ErrorResponses;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.model.User;
import io.jsonwebtoken.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    private final String secret_key = "nippy_qpay_data";
    private long accessTokenValidity = 60*60*1000;
    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private ObjectMapper mapper = new ObjectMapper();

    public JwtUtils(){
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createToken(User user, String token) {
        Claims claims = Jwts.claims();

        //claims.put("mobile", merchant.getMobile_number().toString());
        claims.put("user_id", ""+user.getUser_id());
        claims.put("mobile", ""+user.user_details.get("mobile_number"));
        claims.put("app_id", ""+user.app_id);
        claims.put("mid", ""+user.mid);
        claims.put("token",""+token);

        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(user.getUser_id()+"")
//                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        Map<String, Object> errorDetails = new HashMap<>();
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            APIResponse responses = new APIResponse();
            responses.setStatus(false);
            responses.setCode(HttpStatus.UNAUTHORIZED.value());
            responses.setData("Invalid Token");
            ErrorResponses errorResponse = new ErrorResponses(ErrorCode.ACCESS_DENIED);
            errorResponse.additionalInfo.excepText = "Access Denied";
            responses.setError(errorResponse);
            //errorDetails.put("response",responses);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), responses);
            return null;
//            req.setAttribute("expired", ex.getMessage());
            //throw new AccessDeniedException("Access Denied");
//            throw ex;
        } catch (Exception ex) {
//            req.setAttribute("invalid", ex.getMessage());
            APIResponse responses = new APIResponse();
            responses.setStatus(false);
            responses.setCode(HttpStatus.UNAUTHORIZED.value());
            responses.setData("Invalid Token");
            ErrorResponses errorResponse = new ErrorResponses(ErrorCode.ACCESS_DENIED);
            errorResponse.additionalInfo.excepText = "Access Denied";
            responses.setError(errorResponse);
           // errorDetails.put("response",responses);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), responses);
            return null;
          //  throw new ServletException(new AccessDeniedException("Access Denied"));
            //throw new AccessDeniedException("Access Denied");
//            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getUserId(Claims claims) {
        return claims.get("user_id").toString();
    }

    public String getMobile(Claims claims) {
        return claims.get("mobile").toString();
    }

    public String getAppId(Claims claims) {
        return claims.get("app_id").toString();
    }

    public String getMid(Claims claims) {
        return claims.get("mid").toString();
    }

    public String getToken(Claims claims) {
        return claims.get("token").toString();
    }
}
