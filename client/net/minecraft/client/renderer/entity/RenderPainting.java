package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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
      PaintingType var10 = var1.field_70522_e;
      float var11 = 0.0625F;
      GlStateManager.func_179152_a(0.0625F, 0.0625F, 0.0625F);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      this.func_77010_a(var1, var10.func_200834_b(), var10.func_200832_c(), var10.func_200833_d(), var10.func_200835_e());
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

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
            BufferBuilder var32 = var31.func_178180_c();
            var32.func_181668_a(7, DefaultVertexFormats.field_181710_j);
            var32.func_181662_b((double)var23, (double)var26, -0.5D).func_187315_a((double)var28, (double)var29).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, -0.5D).func_187315_a((double)var27, (double)var29).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, -0.5D).func_187315_a((double)var27, (double)var30).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, -0.5D).func_187315_a((double)var28, (double)var30).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, 0.5D).func_187315_a(0.75D, 0.0D).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, 0.5D).func_187315_a(0.8125D, 0.0D).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, 0.5D).func_187315_a(0.8125D, 0.0625D).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, 0.5D).func_187315_a(0.75D, 0.0625D).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, -0.5D).func_187315_a(0.75D, 0.001953125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, -0.5D).func_187315_a(0.8125D, 0.001953125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, 0.5D).func_187315_a(0.8125D, 0.001953125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, 0.5D).func_187315_a(0.75D, 0.001953125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, 0.5D).func_187315_a(0.75D, 0.001953125D).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, 0.5D).func_187315_a(0.8125D, 0.001953125D).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, -0.5D).func_187315_a(0.8125D, 0.001953125D).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, -0.5D).func_187315_a(0.75D, 0.001953125D).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, 0.5D).func_187315_a(0.751953125D, 0.0D).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, 0.5D).func_187315_a(0.751953125D, 0.0625D).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var26, -0.5D).func_187315_a(0.751953125D, 0.0625D).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var23, (double)var25, -0.5D).func_187315_a(0.751953125D, 0.0D).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, -0.5D).func_187315_a(0.751953125D, 0.0D).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, -0.5D).func_187315_a(0.751953125D, 0.0625D).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var26, 0.5D).func_187315_a(0.751953125D, 0.0625D).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
            var32.func_181662_b((double)var24, (double)var25, 0.5D).func_187315_a(0.751953125D, 0.0D).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
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
