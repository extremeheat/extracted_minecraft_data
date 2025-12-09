package net.minecraft.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public record KeyEvent(@InputConstants.Value int key, int scancode, @InputWithModifiers.Modifiers int modifiers) implements InputWithModifiers {
   public KeyEvent(@InputConstants.Value int var1, int var2, @InputWithModifiers.Modifiers int var3) {
      super();
      this.key = var1;
      this.scancode = var2;
      this.modifiers = var3;
   }

   public int input() {
      return this.key;
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface Action {
   }
}
