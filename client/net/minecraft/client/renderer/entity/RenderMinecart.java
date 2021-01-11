package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderMinecart<T extends EntityMinecart> extends Render<T> {
   private static final ResourceLocation field_110804_g = new ResourceLocation("textures/entity/minecart.png");
   protected ModelBase field_77013_a = new ModelMinecart();

   public RenderMinecart(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      this.func_180548_c(var1);
      long var10 = (long)var1.func_145782_y() * 493286711L;
      var10 = var10 * var10 * 4392167121L + var10 * 98761L;
      float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.func_179109_b(var12, var13, var14);
      double var15 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var9;
      double var17 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var9;
      double var19 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var9;
      double var21 = 0.30000001192092896D;
      Vec3 var23 = var1.func_70489_a(var15, var17, var19);
      float var24 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9;
      if (var23 != null) {
         Vec3 var25 = var1.func_70495_a(var15, var17, var19, var21);
         Vec3 var26 = var1.func_70495_a(var15, var17, var19, -var21);
         if (var25 == null) {
            var25 = var23;
         }

         if (var26 == null) {
            var26 = var23;
         }

         var2 += var23.field_72450_a - var15;
         var4 += (var25.field_72448_b + var26.field_72448_b) / 2.0D - var17;
         var6 += var23.field_72449_c - var19;
         Vec3 var27 = var26.func_72441_c(-var25.field_72450_a, -var25.field_72448_b, -var25.field_72449_c);
         if (var27.func_72433_c() != 0.0D) {
            var27 = var27.func_72432_b();
            var8 = (float)(Math.atan2(var27.field_72449_c, var27.field_72450_a) * 180.0D / 3.141592653589793D);
            var24 = (float)(Math.atan(var27.field_72448_b) * 73.0D);
         }
      }

      GlStateManager.func_179109_b((float)var2, (float)var4 + 0.375F, (float)var6);
      GlStateManager.func_179114_b(180.0F - var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(-var24, 0.0F, 0.0F, 1.0F);
      float var30 = (float)var1.func_70496_j() - var9;
      float var31 = var1.func_70491_i() - var9;
      if (var31 < 0.0F) {
         var31 = 0.0F;
      }

      if (var30 > 0.0F) {
         GlStateManager.func_179114_b(MathHelper.func_76126_a(var30) * var30 * var31 / 10.0F * (float)var1.func_70493_k(), 1.0F, 0.0F, 0.0F);
      }

      int var32 = var1.func_94099_q();
      IBlockState var28 = var1.func_174897_t();
      if (var28.func_177230_c().func_149645_b() != -1) {
         GlStateManager.func_179094_E();
         this.func_110776_a(TextureMap.field_110575_b);
         float var29 = 0.75F;
         GlStateManager.func_179152_a(var29, var29, var29);
         GlStateManager.func_179109_b(-0.5F, (float)(var32 - 8) / 16.0F, 0.5F);
         this.func_180560_a(var1, var9, var28);
         GlStateManager.func_179121_F();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_180548_c(var1);
      }

      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
      this.field_77013_a.func_78088_a(var1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(T var1) {
      return field_110804_g;
   }

   protected void func_180560_a(T var1, float var2, IBlockState var3) {
      GlStateManager.func_179094_E();
      Minecraft.func_71410_x().func_175602_ab().func_175016_a(var3, var1.func_70013_c(var2));
      GlStateManager.func_179121_F();
   }
}
