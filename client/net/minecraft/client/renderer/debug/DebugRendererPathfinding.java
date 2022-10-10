package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class DebugRendererPathfinding implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_188290_a;
   private final Map<Integer, Path> field_188291_b = Maps.newHashMap();
   private final Map<Integer, Float> field_188292_c = Maps.newHashMap();
   private final Map<Integer, Long> field_188293_d = Maps.newHashMap();
   private EntityPlayer field_190068_e;
   private double field_190069_f;
   private double field_190070_g;
   private double field_190071_h;

   public DebugRendererPathfinding(Minecraft var1) {
      super();
      this.field_188290_a = var1;
   }

   public void func_188289_a(int var1, Path var2, float var3) {
      this.field_188291_b.put(var1, var2);
      this.field_188293_d.put(var1, Util.func_211177_b());
      this.field_188292_c.put(var1, var3);
   }

   public void func_190060_a(float var1, long var2) {
      if (!this.field_188291_b.isEmpty()) {
         long var4 = Util.func_211177_b();
         this.field_190068_e = this.field_188290_a.field_71439_g;
         this.field_190069_f = this.field_190068_e.field_70142_S + (this.field_190068_e.field_70165_t - this.field_190068_e.field_70142_S) * (double)var1;
         this.field_190070_g = this.field_190068_e.field_70137_T + (this.field_190068_e.field_70163_u - this.field_190068_e.field_70137_T) * (double)var1;
         this.field_190071_h = this.field_190068_e.field_70136_U + (this.field_190068_e.field_70161_v - this.field_190068_e.field_70136_U) * (double)var1;
         GlStateManager.func_179094_E();
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.func_179131_c(0.0F, 1.0F, 0.0F, 0.75F);
         GlStateManager.func_179090_x();
         GlStateManager.func_187441_d(6.0F);
         Iterator var6 = this.field_188291_b.keySet().iterator();

         while(true) {
            Path var8;
            float var9;
            PathPoint var10;
            int var11;
            PathPoint var12;
            do {
               Integer var7;
               if (!var6.hasNext()) {
                  var6 = this.field_188291_b.keySet().iterator();

                  while(var6.hasNext()) {
                     var7 = (Integer)var6.next();
                     var8 = (Path)this.field_188291_b.get(var7);
                     PathPoint[] var18 = var8.func_189965_h();
                     int var20 = var18.length;

                     for(var11 = 0; var11 < var20; ++var11) {
                        var12 = var18[var11];
                        if (this.func_190066_a(var12) <= 40.0F) {
                           DebugRenderer.func_190076_a(String.format("%s", var12.field_186287_m), (double)var12.field_75839_a + 0.5D, (double)var12.field_75837_b + 0.75D, (double)var12.field_75838_c + 0.5D, var1, -65536);
                           DebugRenderer.func_190076_a(String.format(Locale.ROOT, "%.2f", var12.field_186286_l), (double)var12.field_75839_a + 0.5D, (double)var12.field_75837_b + 0.25D, (double)var12.field_75838_c + 0.5D, var1, -65536);
                        }
                     }

                     var18 = var8.func_189966_g();
                     var20 = var18.length;

                     for(var11 = 0; var11 < var20; ++var11) {
                        var12 = var18[var11];
                        if (this.func_190066_a(var12) <= 40.0F) {
                           DebugRenderer.func_190076_a(String.format("%s", var12.field_186287_m), (double)var12.field_75839_a + 0.5D, (double)var12.field_75837_b + 0.75D, (double)var12.field_75838_c + 0.5D, var1, -16776961);
                           DebugRenderer.func_190076_a(String.format(Locale.ROOT, "%.2f", var12.field_186286_l), (double)var12.field_75839_a + 0.5D, (double)var12.field_75837_b + 0.25D, (double)var12.field_75838_c + 0.5D, var1, -16776961);
                        }
                     }

                     for(int var19 = 0; var19 < var8.func_75874_d(); ++var19) {
                        var10 = var8.func_75877_a(var19);
                        if (this.func_190066_a(var10) <= 40.0F) {
                           DebugRenderer.func_190076_a(String.format("%s", var10.field_186287_m), (double)var10.field_75839_a + 0.5D, (double)var10.field_75837_b + 0.75D, (double)var10.field_75838_c + 0.5D, var1, -1);
                           DebugRenderer.func_190076_a(String.format(Locale.ROOT, "%.2f", var10.field_186286_l), (double)var10.field_75839_a + 0.5D, (double)var10.field_75837_b + 0.25D, (double)var10.field_75838_c + 0.5D, var1, -1);
                        }
                     }
                  }

                  Integer[] var15 = (Integer[])this.field_188293_d.keySet().toArray(new Integer[0]);
                  int var16 = var15.length;

                  for(int var17 = 0; var17 < var16; ++var17) {
                     Integer var21 = var15[var17];
                     if (var4 - (Long)this.field_188293_d.get(var21) > 20000L) {
                        this.field_188291_b.remove(var21);
                        this.field_188293_d.remove(var21);
                     }
                  }

                  GlStateManager.func_179098_w();
                  GlStateManager.func_179084_k();
                  GlStateManager.func_179121_F();
                  return;
               }

               var7 = (Integer)var6.next();
               var8 = (Path)this.field_188291_b.get(var7);
               var9 = (Float)this.field_188292_c.get(var7);
               this.func_190067_a(var1, var8);
               var10 = var8.func_189964_i();
            } while(this.func_190066_a(var10) > 40.0F);

            WorldRenderer.func_189696_b((new AxisAlignedBB((double)((float)var10.field_75839_a + 0.25F), (double)((float)var10.field_75837_b + 0.25F), (double)var10.field_75838_c + 0.25D, (double)((float)var10.field_75839_a + 0.75F), (double)((float)var10.field_75837_b + 0.75F), (double)((float)var10.field_75838_c + 0.75F))).func_72317_d(-this.field_190069_f, -this.field_190070_g, -this.field_190071_h), 0.0F, 1.0F, 0.0F, 0.5F);

            for(var11 = 0; var11 < var8.func_75874_d(); ++var11) {
               var12 = var8.func_75877_a(var11);
               if (this.func_190066_a(var12) <= 40.0F) {
                  float var13 = var11 == var8.func_75873_e() ? 1.0F : 0.0F;
                  float var14 = var11 == var8.func_75873_e() ? 0.0F : 1.0F;
                  WorldRenderer.func_189696_b((new AxisAlignedBB((double)((float)var12.field_75839_a + 0.5F - var9), (double)((float)var12.field_75837_b + 0.01F * (float)var11), (double)((float)var12.field_75838_c + 0.5F - var9), (double)((float)var12.field_75839_a + 0.5F + var9), (double)((float)var12.field_75837_b + 0.25F + 0.01F * (float)var11), (double)((float)var12.field_75838_c + 0.5F + var9))).func_72317_d(-this.field_190069_f, -this.field_190070_g, -this.field_190071_h), var13, 0.0F, var14, 0.5F);
               }
            }
         }
      }
   }

   public void func_190067_a(float var1, Path var2) {
      Tessellator var3 = Tessellator.func_178181_a();
      BufferBuilder var4 = var3.func_178180_c();
      var4.func_181668_a(3, DefaultVertexFormats.field_181706_f);

      for(int var5 = 0; var5 < var2.func_75874_d(); ++var5) {
         PathPoint var6 = var2.func_75877_a(var5);
         if (this.func_190066_a(var6) <= 40.0F) {
            float var7 = (float)var5 / (float)var2.func_75874_d() * 0.33F;
            int var8 = var5 == 0 ? 0 : MathHelper.func_181758_c(var7, 0.9F, 0.9F);
            int var9 = var8 >> 16 & 255;
            int var10 = var8 >> 8 & 255;
            int var11 = var8 & 255;
            var4.func_181662_b((double)var6.field_75839_a - this.field_190069_f + 0.5D, (double)var6.field_75837_b - this.field_190070_g + 0.5D, (double)var6.field_75838_c - this.field_190071_h + 0.5D).func_181669_b(var9, var10, var11, 255).func_181675_d();
         }
      }

      var3.func_78381_a();
   }

   private float func_190066_a(PathPoint var1) {
      return (float)(Math.abs((double)var1.field_75839_a - this.field_190068_e.field_70165_t) + Math.abs((double)var1.field_75837_b - this.field_190068_e.field_70163_u) + Math.abs((double)var1.field_75838_c - this.field_190068_e.field_70161_v));
   }
}
