package dev.blockhit.config;

import java.lang.annotation.*;
import org.polyfrost.oneconfig.api.config.v1.Visualizer;
import org.polyfrost.oneconfig.api.config.v1.annotations.Option;

@Option(display = Visualizer.ButtonVisualizer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SoundChoice {
  String title();

  String description() default "";

  String category() default "General";

  String subcategory() default "Sound";

  String text() default "Choose";
}
