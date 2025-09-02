package net.minecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.BooleanSupplier;

public class ToggleKeyMapping extends KeyMapping {
   private final BooleanSupplier needsToggle;
   private boolean releasedByScreenWhenDown;

   public ToggleKeyMapping(String var1, int var2, KeyMapping.Category var3, BooleanSupplier var4) {
      this(var1, InputConstants.Type.KEYSYM, var2, var3, var4);
   }

   public ToggleKeyMapping(String var1, InputConstants.Type var2, int var3, KeyMapping.Category var4, BooleanSupplier var5) {
      super(var1, var2, var3, var4);
      this.needsToggle = var5;
   }

   protected boolean shouldSetOnIngameFocus() {
      return super.shouldSetOnIngameFocus() && !this.needsToggle.getAsBoolean();
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

   protected void release() {
      if (this.needsToggle.getAsBoolean() && this.isDown() || this.releasedByScreenWhenDown) {
         this.releasedByScreenWhenDown = true;
      }

      this.reset();
   }

   public boolean shouldRestoreStateOnScreenClosed() {
      boolean var1 = this.needsToggle.getAsBoolean() && this.key.getType() == InputConstants.Type.KEYSYM && this.releasedByScreenWhenDown;
      this.releasedByScreenWhenDown = false;
      return var1;
   }

   protected void reset() {
      super.setDown(false);
   }
}
