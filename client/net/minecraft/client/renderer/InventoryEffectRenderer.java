package net.minecraft.client.renderer;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class InventoryEffectRenderer extends GuiContainer {
   private boolean field_147045_u;

   public InventoryEffectRenderer(Container var1) {
      super(var1);
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.func_175378_g();
   }

   protected void func_175378_g() {
      if (!this.field_146297_k.field_71439_g.func_70651_bq().isEmpty()) {
         this.field_147003_i = 160 + (this.field_146294_l - this.field_146999_f - 200) / 2;
         this.field_147045_u = true;
      } else {
         this.field_147003_i = (this.field_146294_l - this.field_146999_f) / 2;
         this.field_147045_u = false;
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      super.func_73863_a(var1, var2, var3);
      if (this.field_147045_u) {
         this.func_147044_g();
      }

   }

   private void func_147044_g() {
      int var1 = this.field_147003_i - 124;
      int var2 = this.field_147009_r;
      boolean var3 = true;
      Collection var4 = this.field_146297_k.field_71439_g.func_70651_bq();
      if (!var4.isEmpty()) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179140_f();
         int var5 = 33;
         if (var4.size() > 5) {
            var5 = 132 / (var4.size() - 1);
         }

         for(Iterator var6 = this.field_146297_k.field_71439_g.func_70651_bq().iterator(); var6.hasNext(); var2 += var5) {
            PotionEffect var7 = (PotionEffect)var6.next();
            Potion var8 = Potion.field_76425_a[var7.func_76456_a()];
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146297_k.func_110434_K().func_110577_a(field_147001_a);
            this.func_73729_b(var1, var2, 0, 166, 140, 32);
            if (var8.func_76400_d()) {
               int var9 = var8.func_76392_e();
               this.func_73729_b(var1 + 6, var2 + 7, 0 + var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
            }

            String var11 = I18n.func_135052_a(var8.func_76393_a());
            if (var7.func_76458_c() == 1) {
               var11 = var11 + " " + I18n.func_135052_a("enchantment.level.2");
            } else if (var7.func_76458_c() == 2) {
               var11 = var11 + " " + I18n.func_135052_a("enchantment.level.3");
            } else if (var7.func_76458_c() == 3) {
               var11 = var11 + " " + I18n.func_135052_a("enchantment.level.4");
            }

            this.field_146289_q.func_175063_a(var11, (float)(var1 + 10 + 18), (float)(var2 + 6), 16777215);
            String var10 = Potion.func_76389_a(var7);
            this.field_146289_q.func_175063_a(var10, (float)(var1 + 10 + 18), (float)(var2 + 6 + 10), 8355711);
         }

      }
   }
}
