package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiBrewingStand extends GuiContainer {
   private static final ResourceLocation field_147014_u = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private static final int[] field_184857_v = new int[]{29, 24, 20, 16, 11, 6, 0};
   private final InventoryPlayer field_175384_v;
   private final IInventory field_147013_v;

   public GuiBrewingStand(InventoryPlayer var1, IInventory var2) {
      super(new ContainerBrewingStand(var1, var2));
      this.field_175384_v = var1;
      this.field_147013_v = var2;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      super.func_73863_a(var1, var2, var3);
      this.func_191948_b(var1, var2);
   }

   protected void func_146979_b(int var1, int var2) {
      String var3 = this.field_147013_v.func_145748_c_().func_150254_d();
      this.field_146289_q.func_211126_b(var3, (float)(this.field_146999_f / 2 - this.field_146289_q.func_78256_a(var3) / 2), 6.0F, 4210752);
      this.field_146289_q.func_211126_b(this.field_175384_v.func_145748_c_().func_150254_d(), 8.0F, (float)(this.field_147000_g - 96 + 2), 4210752);
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147014_u);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      int var6 = this.field_147013_v.func_174887_a_(1);
      int var7 = MathHelper.func_76125_a((18 * var6 + 20 - 1) / 20, 0, 18);
      if (var7 > 0) {
         this.func_73729_b(var4 + 60, var5 + 44, 176, 29, var7, 4);
      }

      int var8 = this.field_147013_v.func_174887_a_(0);
      if (var8 > 0) {
         int var9 = (int)(28.0F * (1.0F - (float)var8 / 400.0F));
         if (var9 > 0) {
            this.func_73729_b(var4 + 97, var5 + 16, 176, 0, 9, var9);
         }

         var9 = field_184857_v[var8 / 2 % 7];
         if (var9 > 0) {
            this.func_73729_b(var4 + 63, var5 + 14 + 29 - var9, 185, 29 - var9, 12, var9);
         }
      }

   }
}
