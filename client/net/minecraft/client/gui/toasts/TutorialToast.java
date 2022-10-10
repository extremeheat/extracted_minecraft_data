package net.minecraft.client.gui.toasts;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class TutorialToast implements IToast {
   private final TutorialToast.Icons field_193671_c;
   private final String field_193672_d;
   private final String field_193673_e;
   private IToast.Visibility field_193674_f;
   private long field_193675_g;
   private float field_193676_h;
   private float field_193677_i;
   private final boolean field_193678_j;

   public TutorialToast(TutorialToast.Icons var1, ITextComponent var2, @Nullable ITextComponent var3, boolean var4) {
      super();
      this.field_193674_f = IToast.Visibility.SHOW;
      this.field_193671_c = var1;
      this.field_193672_d = var2.func_150254_d();
      this.field_193673_e = var3 == null ? null : var3.func_150254_d();
      this.field_193678_j = var4;
   }

   public IToast.Visibility func_193653_a(GuiToast var1, long var2) {
      var1.func_192989_b().func_110434_K().func_110577_a(field_193654_a);
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      var1.func_73729_b(0, 0, 0, 96, 160, 32);
      this.field_193671_c.func_193697_a(var1, 6, 6);
      if (this.field_193673_e == null) {
         var1.func_192989_b().field_71466_p.func_211126_b(this.field_193672_d, 30.0F, 12.0F, -11534256);
      } else {
         var1.func_192989_b().field_71466_p.func_211126_b(this.field_193672_d, 30.0F, 7.0F, -11534256);
         var1.func_192989_b().field_71466_p.func_211126_b(this.field_193673_e, 30.0F, 18.0F, -16777216);
      }

      if (this.field_193678_j) {
         Gui.func_73734_a(3, 28, 157, 29, -1);
         float var4 = (float)MathHelper.func_151238_b((double)this.field_193676_h, (double)this.field_193677_i, (double)((float)(var2 - this.field_193675_g) / 100.0F));
         int var5;
         if (this.field_193677_i >= this.field_193676_h) {
            var5 = -16755456;
         } else {
            var5 = -11206656;
         }

         Gui.func_73734_a(3, 28, (int)(3.0F + 154.0F * var4), 29, var5);
         this.field_193676_h = var4;
         this.field_193675_g = var2;
      }

      return this.field_193674_f;
   }

   public void func_193670_a() {
      this.field_193674_f = IToast.Visibility.HIDE;
   }

   public void func_193669_a(float var1) {
      this.field_193677_i = var1;
   }

   public static enum Icons {
      MOVEMENT_KEYS(0, 0),
      MOUSE(1, 0),
      TREE(2, 0),
      RECIPE_BOOK(0, 1),
      WOODEN_PLANKS(1, 1);

      private final int field_193703_f;
      private final int field_193704_g;

      private Icons(int var3, int var4) {
         this.field_193703_f = var3;
         this.field_193704_g = var4;
      }

      public void func_193697_a(Gui var1, int var2, int var3) {
         GlStateManager.func_179147_l();
         var1.func_73729_b(var2, var3, 176 + this.field_193703_f * 20, this.field_193704_g * 20, 20, 20);
         GlStateManager.func_179147_l();
      }
   }
}
