package com.madao.web.interceptor;

import com.madao.api.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class AuthorityGetInterceptor implements HandlerInterceptor {
    String adminGetAuthority = "admin-read";

    @Value("${AUTHORITY_RESULT}")
    private String authorityResult = "authorityResult";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("preHandler........................................");
        System.out.println(authorityResult);
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute("user");
        System.out.println("userDTO---" + userDTO);
        if(userDTO!=null){
            List<String> authorityList = userDTO.getAuthorityList();
            System.out.println("authorityList......................");
            authorityList.stream().forEach(System.out::println);
            if(!authorityList.contains(adminGetAuthority)){
                System.out.println("false");
                request.setAttribute(authorityResult, false);
            }else{
                System.out.println("true");
                request.setAttribute(authorityResult, true);
            }
        }
        return true;
    }
}
