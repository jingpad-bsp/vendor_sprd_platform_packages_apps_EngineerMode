package com.unisoc.engineermode.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Implementation {
    Class<?> interfaceClass();
    /*
        use string array to store all properties data
        each entry in array is a key-value pair, format is "KEY:VALUE"
        such as {"androidVersion:10", "product:sp9832"}
    * */
//    String[] properties() default {};
    Property[] properties() default {};
//    String androidVersion() default "";
//    String chip() default "";
//    String wcn() default "";
//    String board() default "";
}
