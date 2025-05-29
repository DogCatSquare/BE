package DC_square.spring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//읽기 전용 Swagger 세팅
@Component
@Order(1)
public class SwaggerMethodFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String referer = request.getHeader("Referer");
    String method = request.getMethod();
    String uri = request.getRequestURI();

    // Referer가 Swagger UI에서 온 것이고, GET이 아닌 요청은 차단
    if (referer != null && referer.contains("/swagger-ui") && !method.equalsIgnoreCase("GET")) {
      System.out.println("[SwaggerMethodFilter] 차단됨 - URI: " + uri + ", 메서드: " + method + ", Referer: " + referer);
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Swagger UI에서는 GET만 허용됩니다.");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
