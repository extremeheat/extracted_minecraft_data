package net.minecraft.client.gui;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiScreenOptionsSounds extends GuiScreen {
   private final GuiScreen field_146505_f;
   private final GameSettings field_146506_g;
   protected String field_146507_a = "Options";
   private String field_146508_h;

   public GuiScreenOptionsSounds(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146505_f = var1;
      this.field_146506_g = var2;
   }

   @Override
   public void func_73866_w_() {
      int var1 = 0;
      this.field_146507_a = I18n.func_135052_a("options.sounds.title");
      this.field_146508_h = I18n.func_135052_a("options.off");
      this.field_146292_n
         .add(
            new GuiScreenOptionsSounds$Button(
               this,
               SoundCategory.MASTER.func_147156_b(),
               this.field_146294_l / 2 - 155 + var1 % 2 * 160,
               this.field_146295_m / 6 - 12 + 24 * (var1 >> 1),
               SoundCategory.MASTER,
               true
            )
         );
      var1 += 2;

      for(SoundCategory var5 : SoundCategory.values()) {
         if (var5 != SoundCategory.MASTER) {
            this.field_146292_n
               .add(
                  new GuiScreenOptionsSounds$Button(
                     this, var5.func_147156_b(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (var1 >> 1), var5, false
                  )
               );
            ++var1;
         }
      }

      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.done")));
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 200) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_146505_f);
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146507_a, this.field_146294_l / 2, 15, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   protected String func_146504_a(SoundCategory var1) {
      float var2 = this.field_146506_g.func_151438_a(var1);
      return var2 == 0.0F ? this.field_146508_h : (int)(var2 * 100.0F) + "%";
   }
}
