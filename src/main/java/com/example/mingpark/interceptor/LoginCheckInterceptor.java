//package com.example.mingpark.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class LoginCheckInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 기존에 만들어진 세션을 가져옴
//        HttpSession session = request.getSession(false);
//
//        // 세션이 없거나, 세션에 로그인한 회원 정보(loginMember)가 없다면?
//        if (session == null || session.getAttribute("loginMember") == null) {
//
//            // [참고] 비동기 AJAX (Fetch API) 요청인지 확인하는 헤더 정보
//            String requestedWith = request.getHeader("X-Requested-With");
//
//            // 만약 포도알 클릭이나 새로고침 같은 API(/api/...) 요청이거나 Fetch 요청이라면?
//            if ("XMLHttpRequest".equals(requestedWith) || request.getRequestURI().startsWith("/api/")) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized 에러 반환
//                response.setContentType("application/json;charset=UTF-8");
//                response.getWriter().write("{\"status\": \"unauthorized\", \"message\": \"로그인이 필요합니다.\"}");
//                return false; // 더 이상 컨트롤러로 가지 못하게 딱 막아버림!
//            }
//
//            // 만약 일반 화면(웹 페이지 주소창 입력 등) 요청이라면 로그인 페이지로 리다이렉트
//            // 원래 가려던 주소(request.getRequestURI())를 뒤에 붙여두면 로그인 후 다시 일로 보내줄 수 있어!
//            response.sendRedirect("/members/login?redirectURL=" + request.getRequestURI());
//            return false; // 통과 실패!
//        }
//
//        // 3. 세션도 있고 로그인도 잘 되어 있다면 통과
//        return true;
//    }
//}