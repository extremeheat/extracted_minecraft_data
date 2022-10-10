package net.minecraft.client.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class TileEntityEndPortalRenderer extends TileEntityRenderer<TileEntityEndPortal> {
   private static final ResourceLocation field_147529_c = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation field_147526_d = new ResourceLocation("textures/entity/end_portal.png");
   private static final Random field_147527_e = new Random(31100L);
   private static final FloatBuffer field_188201_h = GLAllocation.func_74529_h(16);
   private static final FloatBuffer field_188202_i = GLAllocation.func_74529_h(16);
   private final FloatBuffer field_147528_b = GLAllocation.func_74529_h(16);

   public TileEntityEndPortalRenderer() {
      super();
   }

   public void func_199341_a(TileEntityEndPortal var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.func_179140_f();
      field_147527_e.setSeed(31100L);
      GlStateManager.func_179111_a(2982, field_188201_h);
      GlStateManager.func_179111_a(2983, field_188202_i);
      double var10 = var2 * var2 + var4 * var4 + var6 * var6;
      int var12 = this.func_191286_a(var10);
      float var13 = this.func_191287_c();
      boolean var14 = false;

      for(int var15 = 0; var15 < var12; ++var15) {
         GlStateManager.func_179094_E();
         float var16 = 2.0F / (float)(18 - var15);
         if (var15 == 0) {
            this.func_147499_a(field_147529_c);
            var16 = 0.15F;
            GlStateManager.func_179147_l();
            GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         }

         if (var15 >= 1) {
            this.func_147499_a(field_147526_d);
            var14 = true;
            Minecraft.func_71410_x().field_71460_t.func_191514_d(true);
         }

         if (var15 == 1) {
            GlStateManager.func_179147_l();
            GlStateManager.func_187401_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         }

         GlStateManager.func_179149_a(GlStateManager.TexGen.S, 9216);
         GlStateManager.func_179149_a(GlStateManager.TexGen.T, 9216);
         GlStateManager.func_179149_a(GlStateManager.TexGen.R, 9216);
         GlStateManager.func_179105_a(GlStateManager.TexGen.S, 9474, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
         GlStateManager.func_179105_a(GlStateManager.TexGen.T, 9474, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
         GlStateManager.func_179105_a(GlStateManager.TexGen.R, 9474, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
         GlStateManager.func_179087_a(GlStateManager.TexGen.S);
         GlStateManager.func_179087_a(GlStateManager.TexGen.T);
         GlStateManager.func_179087_a(GlStateManager.TexGen.R);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179096_D();
         GlStateManager.func_179109_b(0.5F, 0.5F, 0.0F);
         GlStateManager.func_179152_a(0.5F, 0.5F, 1.0F);
         float var17 = (float)(var15 + 1);
         GlStateManager.func_179109_b(17.0F / var17, (2.0F + var17 / 1.5F) * ((float)Util.func_211177_b() % 800000.0F / 800000.0F), 0.0F);
         GlStateManager.func_179114_b((var17 * var17 * 4321.0F + var17 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179152_a(4.5F - var17 / 4.0F, 4.5F - var17 / 4.0F, 1.0F);
         GlStateManager.func_179110_a(field_188202_i);
         GlStateManager.func_179110_a(field_188201_h);
         Tessellator var18 = Tessellator.func_178181_a();
         BufferBuilder var19 = var18.func_178180_c();
         var19.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         float var20 = (field_147527_e.nextFloat() * 0.5F + 0.1F) * var16;
         float var21 = (field_147527_e.nextFloat() * 0.5F + 0.4F) * var16;
         float var22 = (field_147527_e.nextFloat() * 0.5F + 0.5F) * var16;
         if (var1.func_184313_a(EnumFacing.SOUTH)) {
            var19.func_181662_b(var2, var4, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4 + 1.0D, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2, var4 + 1.0D, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
         }

         if (var1.func_184313_a(EnumFacing.NORTH)) {
            var19.func_181662_b(var2, var4 + 1.0D, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4 + 1.0D, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2, var4, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
         }

         if (var1.func_184313_a(EnumFacing.EAST)) {
            var19.func_181662_b(var2 + 1.0D, var4 + 1.0D, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4 + 1.0D, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
         }

         if (var1.func_184313_a(EnumFacing.WEST)) {
            var19.func_181662_b(var2, var4, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2, var4, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2, var4 + 1.0D, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2, var4 + 1.0D, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
         }

         if (var1.func_184313_a(EnumFacing.DOWN)) {
            var19.func_181662_b(var2, var4, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2, var4, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
         }

         if (var1.func_184313_a(EnumFacing.UP)) {
            var19.func_181662_b(var2, var4 + (double)var13, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4 + (double)var13, var6 + 1.0D).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2 + 1.0D, var4 + (double)var13, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
            var19.func_181662_b(var2, var4 + (double)var13, var6).func_181666_a(var20, var21, var22, 1.0F).func_181675_d();
         }

         var18.func_78381_a();
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
         this.func_147499_a(field_147529_c);
      }

      GlStateManager.func_179084_k();
      GlStateManager.func_179100_b(GlStateManager.TexGen.S);
      GlStateManager.func_179100_b(GlStateManager.TexGen.T);
      GlStateManager.func_179100_b(GlStateManager.TexGen.R);
      GlStateManager.func_179145_e();
      if (var14) {
         Minecraft.func_71410_x().field_71460_t.func_191514_d(false);
      }

   }

   protected int func_191286_a(double var1) {
      byte var3;
      if (var1 > 36864.0D) {
         var3 = 1;
      } else if (var1 > 25600.0D) {
         var3 = 3;
      } else if (var1 > 16384.0D) {
         var3 = 5;
      } else if (var1 > 9216.0D) {
         var3 = 7;
      } else if (var1 > 4096.0D) {
         var3 = 9;
      } else if (var1 > 1024.0D) {
         var3 = 11;
      } else if (var1 > 576.0D) {
         var3 = 13;
      } else if (var1 > 256.0D) {
         var3 = 14;
      } else {
         var3 = 15;
      }

      return var3;
   }

   protected float func_191287_c() {
      return 0.75F;
   }

   private FloatBuffer func_147525_a(float var1, float var2, float var3, float var4) {
      this.field_147528_b.clear();
      this.field_147528_b.put(var1).put(var2).put(var3).put(var4);
      this.field_147528_b.flip();
      return this.field_147528_b;
   }
}
