package com.madao.web.interceptor;

import com.madao.api.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class AuthorityBanInterceptor implements HandlerInterceptor {
    private String adminGetAuthority = "admin-ban";

    @Value("${AUTHORITY_RESULT}")
    private String authorityResult = "authorityResult";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("preHandler........................................");
        System.out.println(authorityResult);
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute("user");
        if(userDTO!=null){
            List<String> authorityList = userDTO.getAuthorityList();
            if(!authorityList.contains(adminGetAuthority)){
                System.out.println("false");
                request.setAttribute(authorityResult, false);
            }else{
                request.setAttribute(authorityResult, true);
                System.out.println("true");
            }
        }
        return true;
    }
}
