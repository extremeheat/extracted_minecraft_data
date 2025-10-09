package net.minecraft.client.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public record MouseButtonInfo(int button, @InputWithModifiers.Modifiers int modifiers) implements InputWithModifiers {
   public MouseButtonInfo(int var1, @InputWithModifiers.Modifiers int var2) {
      super();
      this.button = var1;
      this.modifiers = var2;
   }

   public int input() {
      return this.button;
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface Action {
   }

   @Retention(RetentionPolicy.SOURCE)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface MouseButton {
   }
}
