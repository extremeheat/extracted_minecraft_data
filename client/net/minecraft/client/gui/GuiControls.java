package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;

public class GuiControls extends GuiScreen {
   private static final GameSettings.Options[] field_146492_g;
   private final GuiScreen field_146496_h;
   protected String field_146495_a = "Controls";
   private final GameSettings field_146497_i;
   public KeyBinding field_146491_f;
   public long field_152177_g;
   private GuiKeyBindingList field_146494_r;
   private GuiButton field_146493_s;

   public GuiControls(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146496_h = var1;
      this.field_146497_i = var2;
   }

   protected void func_73866_w_() {
      this.field_146494_r = new GuiKeyBindingList(this, this.field_146297_k);
      this.field_195124_j.add(this.field_146494_r);
      this.func_195073_a(this.field_146494_r);
      this.func_189646_b(new GuiButton(200, this.field_146294_l / 2 - 155 + 160, this.field_146295_m - 29, 150, 20, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiControls.this.field_146297_k.func_147108_a(GuiControls.this.field_146496_h);
         }
      });
      this.field_146493_s = this.func_189646_b(new GuiButton(201, this.field_146294_l / 2 - 155, this.field_146295_m - 29, 150, 20, I18n.func_135052_a("controls.resetAll")) {
         public void func_194829_a(double var1, double var3) {
            KeyBinding[] var5 = GuiControls.this.field_146297_k.field_71474_y.field_74324_K;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               KeyBinding var8 = var5[var7];
               var8.func_197979_b(var8.func_197977_i());
            }

            KeyBinding.func_74508_b();
         }
      });
      this.field_146495_a = I18n.func_135052_a("controls.title");
      int var1 = 0;
      GameSettings.Options[] var2 = field_146492_g;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameSettings.Options var5 = var2[var4];
         if (var5.func_74380_a()) {
            this.func_189646_b(new GuiOptionSlider(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, 18 + 24 * (var1 >> 1), var5));
         } else {
            this.func_189646_b(new GuiOptionButton(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, 18 + 24 * (var1 >> 1), var5, this.field_146497_i.func_74297_c(var5)) {
               public void func_194829_a(double var1, double var3) {
                  GuiControls.this.field_146497_i.func_74306_a(this.func_146136_c(), 1);
                  this.field_146126_j = GuiControls.this.field_146497_i.func_74297_c(GameSettings.Options.func_74379_a(this.field_146127_k));
               }
            });
         }

         ++var1;
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.field_146491_f != null) {
         this.field_146497_i.func_198014_a(this.field_146491_f, InputMappings.Type.MOUSE.func_197944_a(var5));
         this.field_146491_f = null;
         KeyBinding.func_74508_b();
         return true;
      } else if (var5 == 0 && this.field_146494_r.mouseClicked(var1, var3, var5)) {
         this.func_195072_d(true);
         this.func_195073_a(this.field_146494_r);
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0 && this.field_146494_r.mouseReleased(var1, var3, var5)) {
         this.func_195072_d(false);
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.field_146491_f != null) {
         if (var1 == 256) {
            this.field_146497_i.func_198014_a(this.field_146491_f, InputMappings.field_197958_a);
         } else {
            this.field_146497_i.func_198014_a(this.field_146491_f, InputMappings.func_197954_a(var1, var2));
         }

         this.field_146491_f = null;
         this.field_152177_g = Util.func_211177_b();
         KeyBinding.func_74508_b();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146494_r.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146495_a, this.field_146294_l / 2, 8, 16777215);
      boolean var4 = false;
      KeyBinding[] var5 = this.field_146497_i.field_74324_K;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         KeyBinding var8 = var5[var7];
         if (!var8.func_197985_l()) {
            var4 = true;
            break;
         }
      }

      this.field_146493_s.field_146124_l = var4;
      super.func_73863_a(var1, var2, var3);
   }

   static {
      field_146492_g = new GameSettings.Options[]{GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP};
   }
}
