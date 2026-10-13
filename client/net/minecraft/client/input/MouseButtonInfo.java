package net.minecraft.client.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public record MouseButtonInfo(@MouseButtonInfo.MouseButton int button, @InputWithModifiers.Modifiers int modifiers) implements InputWithModifiers {
   public MouseButtonInfo {
      super();
   }

   public @MouseButtonInfo.MouseButton int input() {
      return this.button;
   }

   public boolean isLeftMouse() {
      return this.button == 1;
   }

   public boolean isMiddleMouse() {
      return this.button == 2;
   }

   public boolean isRightMouse() {
      return this.button == 3;
   }

   public boolean isMouse4() {
      return this.button == 4;
   }

   public boolean isMouse5() {
      return this.button == 5;
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface Action {
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface MouseButton {
   }
}
