package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiControls extends GuiScreen {
   private static final GameSettings.Options[] field_146492_g;
   private GuiScreen field_146496_h;
   protected String field_146495_a = "Controls";
   private GameSettings field_146497_i;
   public KeyBinding field_146491_f = null;
   public long field_152177_g;
   private GuiKeyBindingList field_146494_r;
   private GuiButton field_146493_s;

   public GuiControls(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146496_h = var1;
      this.field_146497_i = var2;
   }

   public void func_73866_w_() {
      this.field_146494_r = new GuiKeyBindingList(this, this.field_146297_k);
      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 155, this.field_146295_m - 29, 150, 20, I18n.func_135052_a("gui.done")));
      this.field_146292_n.add(this.field_146493_s = new GuiButton(201, this.field_146294_l / 2 - 155 + 160, this.field_146295_m - 29, 150, 20, I18n.func_135052_a("controls.resetAll")));
      this.field_146495_a = I18n.func_135052_a("controls.title");
      int var1 = 0;
      GameSettings.Options[] var2 = field_146492_g;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameSettings.Options var5 = var2[var4];
         if (var5.func_74380_a()) {
            this.field_146292_n.add(new GuiOptionSlider(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, 18 + 24 * (var1 >> 1), var5));
         } else {
            this.field_146292_n.add(new GuiOptionButton(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, 18 + 24 * (var1 >> 1), var5, this.field_146497_i.func_74297_c(var5)));
         }

         ++var1;
      }

   }

   public void func_146274_d() {
      super.func_146274_d();
      this.field_146494_r.func_178039_p();
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 200) {
         this.field_146297_k.func_147108_a(this.field_146496_h);
      } else if (var1.field_146127_k == 201) {
         KeyBinding[] var2 = this.field_146297_k.field_71474_y.field_74324_K;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            KeyBinding var5 = var2[var4];
            var5.func_151462_b(var5.func_151469_h());
         }

         KeyBinding.func_74508_b();
      } else if (var1.field_146127_k < 100 && var1 instanceof GuiOptionButton) {
         this.field_146497_i.func_74306_a(((GuiOptionButton)var1).func_146136_c(), 1);
         var1.field_146126_j = this.field_146497_i.func_74297_c(GameSettings.Options.func_74379_a(var1.field_146127_k));
      }

   }

   protected void func_73864_a(int var1, int var2, int var3) {
      if (this.field_146491_f != null) {
         this.field_146497_i.func_151440_a(this.field_146491_f, -100 + var3);
         this.field_146491_f = null;
         KeyBinding.func_74508_b();
      } else if (var3 != 0 || !this.field_146494_r.func_148179_a(var1, var2, var3)) {
         super.func_73864_a(var1, var2, var3);
      }

   }

   protected void func_146286_b(int var1, int var2, int var3) {
      if (var3 != 0 || !this.field_146494_r.func_148181_b(var1, var2, var3)) {
         super.func_146286_b(var1, var2, var3);
      }

   }

   protected void func_73869_a(char var1, int var2) {
      if (this.field_146491_f != null) {
         if (var2 == 1) {
            this.field_146497_i.func_151440_a(this.field_146491_f, 0);
         } else if (var2 != 0) {
            this.field_146497_i.func_151440_a(this.field_146491_f, var2);
         } else if (var1 > 0) {
            this.field_146497_i.func_151440_a(this.field_146491_f, var1 + 256);
         }

         this.field_146491_f = null;
         this.field_152177_g = Minecraft.func_71386_F();
         KeyBinding.func_74508_b();
      } else {
         super.func_73869_a(var1, var2);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146494_r.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146495_a, this.field_146294_l / 2, 8, 16777215);
      boolean var4 = true;
      KeyBinding[] var5 = this.field_146497_i.field_74324_K;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         KeyBinding var8 = var5[var7];
         if (var8.func_151463_i() != var8.func_151469_h()) {
            var4 = false;
            break;
         }
      }

      this.field_146493_s.field_146124_l = !var4;
      super.func_73863_a(var1, var2, var3);
   }

   static {
      field_146492_g = new GameSettings.Options[]{GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN};
   }
}
