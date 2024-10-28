package net.minecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.BooleanSupplier;

public class ToggleKeyMapping extends KeyMapping {
   private final BooleanSupplier needsToggle;

   public ToggleKeyMapping(String var1, int var2, String var3, BooleanSupplier var4) {
      super(var1, InputConstants.Type.KEYSYM, var2, var3);
      this.needsToggle = var4;
   }

   public void setDown(boolean var1) {
      if (this.needsToggle.getAsBoolean()) {
         if (var1) {
            super.setDown(!this.isDown());
         }
      } else {
         super.setDown(var1);
      }

   }

   protected void reset() {
      super.setDown(false);
   }
}
