package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLockIconButton extends GuiButton {
   private boolean field_175231_o = false;

   public GuiLockIconButton(int var1, int var2, int var3) {
      super(var1, var2, var3, 20, 20, "");
   }

   public boolean func_175230_c() {
      return this.field_175231_o;
   }

   public void func_175229_b(boolean var1) {
      this.field_175231_o = var1;
   }

   public void func_146112_a(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         var1.func_110434_K().func_110577_a(GuiButton.field_146122_a);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = var2 >= this.field_146128_h && var3 >= this.field_146129_i && var2 < this.field_146128_h + this.field_146120_f && var3 < this.field_146129_i + this.field_146121_g;
         GuiLockIconButton.Icon var5;
         if (this.field_175231_o) {
            if (!this.field_146124_l) {
               var5 = GuiLockIconButton.Icon.LOCKED_DISABLED;
            } else if (var4) {
               var5 = GuiLockIconButton.Icon.LOCKED_HOVER;
            } else {
               var5 = GuiLockIconButton.Icon.LOCKED;
            }
         } else if (!this.field_146124_l) {
            var5 = GuiLockIconButton.Icon.UNLOCKED_DISABLED;
         } else if (var4) {
            var5 = GuiLockIconButton.Icon.UNLOCKED_HOVER;
         } else {
            var5 = GuiLockIconButton.Icon.UNLOCKED;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, var5.func_178910_a(), var5.func_178912_b(), this.field_146120_f, this.field_146121_g);
      }
   }

   static enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int field_178914_g;
      private final int field_178920_h;

      private Icon(int var3, int var4) {
         this.field_178914_g = var3;
         this.field_178920_h = var4;
      }

      public int func_178910_a() {
         return this.field_178914_g;
      }

      public int func_178912_b() {
         return this.field_178920_h;
      }
   }
}
