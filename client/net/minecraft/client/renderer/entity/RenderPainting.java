package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render<EntityPainting> {
   private static final ResourceLocation field_110807_a = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

   public RenderPainting(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityPainting var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(var2, var4, var6);
      GlStateManager.func_179114_b(180.0F - var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179091_B();
      this.func_180548_c(var1);
      EntityPainting.EnumArt var10 = var1.field_70522_e;
      float var11 = 0.0625F;
      GlStateManager.func_179152_a(var11, var11, var11);
      this.func_77010_a(var1, var10.field_75703_B, var10.field_75704_C, var10.field_75699_D, var10.field_75700_E);
      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityPainting var1) {
      return field_110807_a;
   }

   private void func_77010_a(EntityPainting var1, int var2, int var3, int var4, int var5) {
      float var6 = (float)(-var2) / 2.0F;
      float var7 = (float)(-var3) / 2.0F;
      float var8 = 0.5F;
      float var9 = 0.75F;
      float var10 = 0.8125F;
      float var11 = 0.0F;
      float var12 = 0.0625F;
      float var13 = 0.75F;
      float var14 = 0.8125F;
      float var15 = 0.001953125F;
      float var16 = 0.001953125F;
      float var17 = 0.7519531F;
      float var18 = 0.7519531F;
      float var19 = 0.0F;
      float var20 = 0.0625F;

      for(int var21 = 0; var21 < var2 / 16; ++var21) {
         for(int var22 = 0; var22 < var3 / 16; ++var22) {
            float var23 = var6 + (float)((var21 + 1) * 16);
            float var24 = var6 + (float)(var21 * 16);
            float var25 = var7 + (float)((var22 + 1) * 16);
            float var26 = var7 + (float)(var22 * 16);
            this.func_77008_a(var1, (var23 + var24) / 2.0F, (var25 + var26) / 2.0F);
            float var27 = (float)(var4 + var2 - var21 * 16) / 256.0F;
            float var28 = (float)(var4 + var2 - (var21 + 1) * 16) / 256.0F;
            float var29 = (float)(var5 + var3 - var22 * 16) / 256.0F;
            float var30 = (float)(var5 + var3 - (var22 + 1) * 16) / 256.0F;
            Tessellator var31 = Tessellator.func_178181_a();
            WorldRenderer var32 = var31.func_178180_c();
            var32.func_181668_a(7, DefaultVertexFormats.field_181710_j);
            var32.func_181662_b((double)var23, (double)var26, (double)(-var8)).func_181673_a((double)var28, (double)var29).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, (double)(-var8)).func_181673_a((double)var27, (double)var29).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, (double)(-var8)).func_181673_a((double)var27, (double)var30).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, (double)(-var8)).func_181673_a((double)var28, (double)var30).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, (double)var8).func_181673_a((double)var9, (double)var11).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, (double)var8).func_181673_a((double)var10, (double)var11).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, (double)var8).func_181673_a((double)var10, (double)var12).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, (double)var8).func_181673_a((double)var9, (double)var12).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, (double)(-var8)).func_181673_a((double)var13, (double)var15).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, (double)(-var8)).func_181673_a((double)var14, (double)var15).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, (double)var8).func_181673_a((double)var14, (double)var16).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, (double)var8).func_181673_a((double)var13, (double)var16).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, (double)var8).func_181673_a((double)var13, (double)var15).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, (double)var8).func_181673_a((double)var14, (double)var15).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, (double)(-var8)).func_181673_a((double)var14, (double)var16).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, (double)(-var8)).func_181673_a((double)var13, (double)var16).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, (double)var8).func_181673_a((double)var18, (double)var19).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, (double)var8).func_181673_a((double)var18, (double)var20).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, (double)(-var8)).func_181673_a((double)var17, (double)var20).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, (double)(-var8)).func_181673_a((double)var17, (double)var19).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, (double)(-var8)).func_181673_a((double)var18, (double)var19).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, (double)(-var8)).func_181673_a((double)var18, (double)var20).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, (double)var8).func_181673_a((double)var17, (double)var20).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, (double)var8).func_181673_a((double)var17, (double)var19).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
            var31.func_78381_a();
         }
      }

   }

   private void func_77008_a(EntityPainting var1, float var2, float var3) {
      int var4 = MathHelper.func_76128_c(var1.field_70165_t);
      int var5 = MathHelper.func_76128_c(var1.field_70163_u + (double)(var3 / 16.0F));
      int var6 = MathHelper.func_76128_c(var1.field_70161_v);
      EnumFacing var7 = var1.field_174860_b;
      if (var7 == EnumFacing.NORTH) {
         var4 = MathHelper.func_76128_c(var1.field_70165_t + (double)(var2 / 16.0F));
      }

      if (var7 == EnumFacing.WEST) {
         var6 = MathHelper.func_76128_c(var1.field_70161_v - (double)(var2 / 16.0F));
      }

      if (var7 == EnumFacing.SOUTH) {
         var4 = MathHelper.func_76128_c(var1.field_70165_t - (double)(var2 / 16.0F));
      }

      if (var7 == EnumFacing.EAST) {
         var6 = MathHelper.func_76128_c(var1.field_70161_v + (double)(var2 / 16.0F));
      }

      int var8 = this.field_76990_c.field_78722_g.func_175626_b(new BlockPos(var4, var5, var6), 0);
      int var9 = var8 % 65536;
      int var10 = var8 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var9, (float)var10);
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
   }
}
