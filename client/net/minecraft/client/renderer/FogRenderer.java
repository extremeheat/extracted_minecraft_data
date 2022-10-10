package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.lwjgl.opengl.GL;

public class FogRenderer {
   private final FloatBuffer field_205091_a = GLAllocation.func_74529_h(16);
   private final FloatBuffer field_205092_b = GLAllocation.func_74529_h(16);
   private float field_205093_c;
   private float field_205094_d;
   private float field_205095_e;
   private float field_205098_h = -1.0F;
   private float field_205099_i = -1.0F;
   private float field_205100_j = -1.0F;
   private int field_205101_k = -1;
   private int field_205102_l = -1;
   private long field_205103_m = -1L;
   private final GameRenderer field_205104_n;
   private final Minecraft field_205105_o;

   public FogRenderer(GameRenderer var1) {
      super();
      this.field_205104_n = var1;
      this.field_205105_o = var1.func_205000_l();
      this.field_205091_a.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
   }

   public void func_78466_h(float var1) {
      WorldClient var2 = this.field_205105_o.field_71441_e;
      Entity var3 = this.field_205105_o.func_175606_aa();
      ActiveRenderInfo.func_186703_a(this.field_205105_o.field_71441_e, var3, var1);
      IFluidState var5 = ActiveRenderInfo.func_206243_b(this.field_205105_o.field_71441_e, var3, var1);
      if (var5.func_206884_a(FluidTags.field_206959_a)) {
         this.func_205086_b(var3, var2, var1);
      } else if (var5.func_206884_a(FluidTags.field_206960_b)) {
         this.field_205093_c = 0.6F;
         this.field_205094_d = 0.1F;
         this.field_205095_e = 0.0F;
         this.field_205103_m = -1L;
      } else {
         this.func_205089_a(var3, var2, var1);
         this.field_205103_m = -1L;
      }

      double var6 = (var3.field_70137_T + (var3.field_70163_u - var3.field_70137_T) * (double)var1) * var2.field_73011_w.func_76565_k();
      if (var3 instanceof EntityLivingBase && ((EntityLivingBase)var3).func_70644_a(MobEffects.field_76440_q)) {
         int var8 = ((EntityLivingBase)var3).func_70660_b(MobEffects.field_76440_q).func_76459_b();
         if (var8 < 20) {
            var6 *= (double)(1.0F - (float)var8 / 20.0F);
         } else {
            var6 = 0.0D;
         }
      }

      if (var6 < 1.0D) {
         if (var6 < 0.0D) {
            var6 = 0.0D;
         }

         var6 *= var6;
         this.field_205093_c = (float)((double)this.field_205093_c * var6);
         this.field_205094_d = (float)((double)this.field_205094_d * var6);
         this.field_205095_e = (float)((double)this.field_205095_e * var6);
      }

      float var10;
      if (this.field_205104_n.func_205002_d(var1) > 0.0F) {
         var10 = this.field_205104_n.func_205002_d(var1);
         this.field_205093_c = this.field_205093_c * (1.0F - var10) + this.field_205093_c * 0.7F * var10;
         this.field_205094_d = this.field_205094_d * (1.0F - var10) + this.field_205094_d * 0.6F * var10;
         this.field_205095_e = this.field_205095_e * (1.0F - var10) + this.field_205095_e * 0.6F * var10;
      }

      float var11;
      if (var5.func_206884_a(FluidTags.field_206959_a)) {
         var10 = 0.0F;
         if (var3 instanceof EntityPlayerSP) {
            EntityPlayerSP var9 = (EntityPlayerSP)var3;
            var10 = var9.func_203719_J();
         }

         var11 = 1.0F / this.field_205093_c;
         if (var11 > 1.0F / this.field_205094_d) {
            var11 = 1.0F / this.field_205094_d;
         }

         if (var11 > 1.0F / this.field_205095_e) {
            var11 = 1.0F / this.field_205095_e;
         }

         this.field_205093_c = this.field_205093_c * (1.0F - var10) + this.field_205093_c * var11 * var10;
         this.field_205094_d = this.field_205094_d * (1.0F - var10) + this.field_205094_d * var11 * var10;
         this.field_205095_e = this.field_205095_e * (1.0F - var10) + this.field_205095_e * var11 * var10;
      } else if (var3 instanceof EntityLivingBase && ((EntityLivingBase)var3).func_70644_a(MobEffects.field_76439_r)) {
         var10 = this.field_205104_n.func_180438_a((EntityLivingBase)var3, var1);
         var11 = 1.0F / this.field_205093_c;
         if (var11 > 1.0F / this.field_205094_d) {
            var11 = 1.0F / this.field_205094_d;
         }

         if (var11 > 1.0F / this.field_205095_e) {
            var11 = 1.0F / this.field_205095_e;
         }

         this.field_205093_c = this.field_205093_c * (1.0F - var10) + this.field_205093_c * var11 * var10;
         this.field_205094_d = this.field_205094_d * (1.0F - var10) + this.field_205094_d * var11 * var10;
         this.field_205095_e = this.field_205095_e * (1.0F - var10) + this.field_205095_e * var11 * var10;
      }

      GlStateManager.func_179082_a(this.field_205093_c, this.field_205094_d, this.field_205095_e, 0.0F);
   }

   private void func_205089_a(Entity var1, World var2, float var3) {
      float var4 = 0.25F + 0.75F * (float)this.field_205105_o.field_71474_y.field_151451_c / 32.0F;
      var4 = 1.0F - (float)Math.pow((double)var4, 0.25D);
      Vec3d var5 = var2.func_72833_a(this.field_205105_o.func_175606_aa(), var3);
      float var6 = (float)var5.field_72450_a;
      float var7 = (float)var5.field_72448_b;
      float var8 = (float)var5.field_72449_c;
      Vec3d var9 = var2.func_72948_g(var3);
      this.field_205093_c = (float)var9.field_72450_a;
      this.field_205094_d = (float)var9.field_72448_b;
      this.field_205095_e = (float)var9.field_72449_c;
      if (this.field_205105_o.field_71474_y.field_151451_c >= 4) {
         double var10 = MathHelper.func_76126_a(var2.func_72929_e(var3)) > 0.0F ? -1.0D : 1.0D;
         Vec3d var12 = new Vec3d(var10, 0.0D, 0.0D);
         float var13 = (float)var1.func_70676_i(var3).func_72430_b(var12);
         if (var13 < 0.0F) {
            var13 = 0.0F;
         }

         if (var13 > 0.0F) {
            float[] var14 = var2.field_73011_w.func_76560_a(var2.func_72826_c(var3), var3);
            if (var14 != null) {
               var13 *= var14[3];
               this.field_205093_c = this.field_205093_c * (1.0F - var13) + var14[0] * var13;
               this.field_205094_d = this.field_205094_d * (1.0F - var13) + var14[1] * var13;
               this.field_205095_e = this.field_205095_e * (1.0F - var13) + var14[2] * var13;
            }
         }
      }

      this.field_205093_c += (var6 - this.field_205093_c) * var4;
      this.field_205094_d += (var7 - this.field_205094_d) * var4;
      this.field_205095_e += (var8 - this.field_205095_e) * var4;
      float var15 = var2.func_72867_j(var3);
      float var11;
      float var16;
      if (var15 > 0.0F) {
         var11 = 1.0F - var15 * 0.5F;
         var16 = 1.0F - var15 * 0.4F;
         this.field_205093_c *= var11;
         this.field_205094_d *= var11;
         this.field_205095_e *= var16;
      }

      var11 = var2.func_72819_i(var3);
      if (var11 > 0.0F) {
         var16 = 1.0F - var11 * 0.5F;
         this.field_205093_c *= var16;
         this.field_205094_d *= var16;
         this.field_205095_e *= var16;
      }

   }

   private void func_205086_b(Entity var1, IWorldReaderBase var2, float var3) {
      long var4 = Util.func_211177_b();
      int var6 = var2.func_180494_b(new BlockPos(ActiveRenderInfo.func_178806_a(var1, (double)var3))).func_204274_p();
      if (this.field_205103_m < 0L) {
         this.field_205101_k = var6;
         this.field_205102_l = var6;
         this.field_205103_m = var4;
      }

      int var7 = this.field_205101_k >> 16 & 255;
      int var8 = this.field_205101_k >> 8 & 255;
      int var9 = this.field_205101_k & 255;
      int var10 = this.field_205102_l >> 16 & 255;
      int var11 = this.field_205102_l >> 8 & 255;
      int var12 = this.field_205102_l & 255;
      float var13 = MathHelper.func_76131_a((float)(var4 - this.field_205103_m) / 5000.0F, 0.0F, 1.0F);
      float var14 = (float)var10 + (float)(var7 - var10) * var13;
      float var15 = (float)var11 + (float)(var8 - var11) * var13;
      float var16 = (float)var12 + (float)(var9 - var12) * var13;
      this.field_205093_c = var14 / 255.0F;
      this.field_205094_d = var15 / 255.0F;
      this.field_205095_e = var16 / 255.0F;
      if (this.field_205101_k != var6) {
         this.field_205101_k = var6;
         this.field_205102_l = MathHelper.func_76141_d(var14) << 16 | MathHelper.func_76141_d(var15) << 8 | MathHelper.func_76141_d(var16);
         this.field_205103_m = var4;
      }

   }

   public void func_78468_a(int var1, float var2) {
      Entity var3 = this.field_205105_o.func_175606_aa();
      this.func_205090_a(false);
      GlStateManager.func_187432_a(0.0F, -1.0F, 0.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      IFluidState var4 = ActiveRenderInfo.func_206243_b(this.field_205105_o.field_71441_e, var3, var2);
      float var8;
      if (var3 instanceof EntityLivingBase && ((EntityLivingBase)var3).func_70644_a(MobEffects.field_76440_q)) {
         var8 = 5.0F;
         int var9 = ((EntityLivingBase)var3).func_70660_b(MobEffects.field_76440_q).func_76459_b();
         if (var9 < 20) {
            var8 = 5.0F + (this.field_205104_n.func_205001_m() - 5.0F) * (1.0F - (float)var9 / 20.0F);
         }

         GlStateManager.func_187430_a(GlStateManager.FogMode.LINEAR);
         if (var1 == -1) {
            GlStateManager.func_179102_b(0.0F);
            GlStateManager.func_179153_c(var8 * 0.8F);
         } else {
            GlStateManager.func_179102_b(var8 * 0.25F);
            GlStateManager.func_179153_c(var8);
         }

         if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager.func_187412_c(34138, 34139);
         }
      } else if (var4.func_206884_a(FluidTags.field_206959_a)) {
         GlStateManager.func_187430_a(GlStateManager.FogMode.EXP2);
         if (var3 instanceof EntityLivingBase) {
            if (var3 instanceof EntityPlayerSP) {
               EntityPlayerSP var5 = (EntityPlayerSP)var3;
               float var6 = 0.05F - var5.func_203719_J() * var5.func_203719_J() * 0.03F;
               Biome var7 = var5.field_70170_p.func_180494_b(new BlockPos(var5));
               if (var7 == Biomes.field_76780_h || var7 == Biomes.field_150599_m) {
                  var6 += 0.005F;
               }

               GlStateManager.func_179095_a(var6);
            } else {
               GlStateManager.func_179095_a(0.05F);
            }
         } else {
            GlStateManager.func_179095_a(0.1F);
         }
      } else if (var4.func_206884_a(FluidTags.field_206960_b)) {
         GlStateManager.func_187430_a(GlStateManager.FogMode.EXP);
         GlStateManager.func_179095_a(2.0F);
      } else {
         var8 = this.field_205104_n.func_205001_m();
         GlStateManager.func_187430_a(GlStateManager.FogMode.LINEAR);
         if (var1 == -1) {
            GlStateManager.func_179102_b(0.0F);
            GlStateManager.func_179153_c(var8);
         } else {
            GlStateManager.func_179102_b(var8 * 0.75F);
            GlStateManager.func_179153_c(var8);
         }

         if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager.func_187412_c(34138, 34139);
         }

         if (this.field_205105_o.field_71441_e.field_73011_w.func_76568_b((int)var3.field_70165_t, (int)var3.field_70161_v) || this.field_205105_o.field_71456_v.func_184046_j().func_184056_f()) {
            GlStateManager.func_179102_b(var8 * 0.05F);
            GlStateManager.func_179153_c(Math.min(var8, 192.0F) * 0.5F);
         }
      }

      GlStateManager.func_179142_g();
      GlStateManager.func_179127_m();
      GlStateManager.func_179104_a(1028, 4608);
   }

   public void func_205090_a(boolean var1) {
      if (var1) {
         GlStateManager.func_187402_b(2918, this.field_205091_a);
      } else {
         GlStateManager.func_187402_b(2918, this.func_205087_b());
      }

   }

   private FloatBuffer func_205087_b() {
      if (this.field_205098_h != this.field_205093_c || this.field_205099_i != this.field_205094_d || this.field_205100_j != this.field_205095_e) {
         this.field_205092_b.clear();
         this.field_205092_b.put(this.field_205093_c).put(this.field_205094_d).put(this.field_205095_e).put(1.0F);
         this.field_205092_b.flip();
         this.field_205098_h = this.field_205093_c;
         this.field_205099_i = this.field_205094_d;
         this.field_205100_j = this.field_205095_e;
      }

      return this.field_205092_b;
   }
}
