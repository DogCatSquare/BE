package DC_square.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 이 어노테이션을 메서드에만 붙일 수 있음
@Retention(RetentionPolicy.RUNTIME) // AOP는 앱이 실행되는 중에 어노테이션이 붙어있는지 감지해서 동작하기 때문에 RUNTIME이 필요
public @interface CheckAccountStop {

}
