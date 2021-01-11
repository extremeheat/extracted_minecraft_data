package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest> {
   private static final ResourceLocation field_147507_b = new ResourceLocation("textures/entity/chest/trapped_double.png");
   private static final ResourceLocation field_147508_c = new ResourceLocation("textures/entity/chest/christmas_double.png");
   private static final ResourceLocation field_147505_d = new ResourceLocation("textures/entity/chest/normal_double.png");
   private static final ResourceLocation field_147506_e = new ResourceLocation("textures/entity/chest/trapped.png");
   private static final ResourceLocation field_147503_f = new ResourceLocation("textures/entity/chest/christmas.png");
   private static final ResourceLocation field_147504_g = new ResourceLocation("textures/entity/chest/normal.png");
   private ModelChest field_147510_h = new ModelChest();
   private ModelChest field_147511_i = new ModelLargeChest();
   private boolean field_147509_j;

   public TileEntityChestRenderer() {
      super();
      Calendar var1 = Calendar.getInstance();
      if (var1.get(2) + 1 == 12 && var1.get(5) >= 24 && var1.get(5) <= 26) {
         this.field_147509_j = true;
      }

   }

   public void func_180535_a(TileEntityChest var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.func_179126_j();
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179132_a(true);
      int var10;
      if (!var1.func_145830_o()) {
         var10 = 0;
      } else {
         Block var11 = var1.func_145838_q();
         var10 = var1.func_145832_p();
         if (var11 instanceof BlockChest && var10 == 0) {
            ((BlockChest)var11).func_176455_e(var1.func_145831_w(), var1.func_174877_v(), var1.func_145831_w().func_180495_p(var1.func_174877_v()));
            var10 = var1.func_145832_p();
         }

         var1.func_145979_i();
      }

      if (var1.field_145992_i == null && var1.field_145991_k == null) {
         ModelChest var15;
         if (var1.field_145990_j == null && var1.field_145988_l == null) {
            var15 = this.field_147510_h;
            if (var9 >= 0) {
               this.func_147499_a(field_178460_a[var9]);
               GlStateManager.func_179128_n(5890);
               GlStateManager.func_179094_E();
               GlStateManager.func_179152_a(4.0F, 4.0F, 1.0F);
               GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.func_179128_n(5888);
            } else if (this.field_147509_j) {
               this.func_147499_a(field_147503_f);
            } else if (var1.func_145980_j() == 1) {
               this.func_147499_a(field_147506_e);
            } else {
               this.func_147499_a(field_147504_g);
            }
         } else {
            var15 = this.field_147511_i;
            if (var9 >= 0) {
               this.func_147499_a(field_178460_a[var9]);
               GlStateManager.func_179128_n(5890);
               GlStateManager.func_179094_E();
               GlStateManager.func_179152_a(8.0F, 4.0F, 1.0F);
               GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.func_179128_n(5888);
            } else if (this.field_147509_j) {
               this.func_147499_a(field_147508_c);
            } else if (var1.func_145980_j() == 1) {
               this.func_147499_a(field_147507_b);
            } else {
               this.func_147499_a(field_147505_d);
            }
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179091_B();
         if (var9 < 0) {
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.func_179109_b((float)var2, (float)var4 + 1.0F, (float)var6 + 1.0F);
         GlStateManager.func_179152_a(1.0F, -1.0F, -1.0F);
         GlStateManager.func_179109_b(0.5F, 0.5F, 0.5F);
         short var12 = 0;
         if (var10 == 2) {
            var12 = 180;
         }

         if (var10 == 3) {
            var12 = 0;
         }

         if (var10 == 4) {
            var12 = 90;
         }

         if (var10 == 5) {
            var12 = -90;
         }

         if (var10 == 2 && var1.field_145990_j != null) {
            GlStateManager.func_179109_b(1.0F, 0.0F, 0.0F);
         }

         if (var10 == 5 && var1.field_145988_l != null) {
            GlStateManager.func_179109_b(0.0F, 0.0F, -1.0F);
         }

         GlStateManager.func_179114_b((float)var12, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179109_b(-0.5F, -0.5F, -0.5F);
         float var13 = var1.field_145986_n + (var1.field_145989_m - var1.field_145986_n) * var8;
         float var14;
         if (var1.field_145992_i != null) {
            var14 = var1.field_145992_i.field_145986_n + (var1.field_145992_i.field_145989_m - var1.field_145992_i.field_145986_n) * var8;
            if (var14 > var13) {
               var13 = var14;
            }
         }

         if (var1.field_145991_k != null) {
            var14 = var1.field_145991_k.field_145986_n + (var1.field_145991_k.field_145989_m - var1.field_145991_k.field_145986_n) * var8;
            if (var14 > var13) {
               var13 = var14;
            }
         }

         var13 = 1.0F - var13;
         var13 = 1.0F - var13 * var13 * var13;
         var15.field_78234_a.field_78795_f = -(var13 * 3.1415927F / 2.0F);
         var15.func_78231_a();
         GlStateManager.func_179101_C();
         GlStateManager.func_179121_F();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         if (var9 >= 0) {
            GlStateManager.func_179128_n(5890);
            GlStateManager.func_179121_F();
            GlStateManager.func_179128_n(5888);
         }

      }
   }
}
