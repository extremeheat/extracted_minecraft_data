package net.minecraft.client.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EffectRenderer {
   private static final ResourceLocation field_110737_b = new ResourceLocation("textures/particle/particles.png");
   protected World field_78878_a;
   private List[] field_78876_b = new List[4];
   private TextureManager field_78877_c;
   private Random field_78875_d = new Random();

   public EffectRenderer(World var1, TextureManager var2) {
      super();
      if (var1 != null) {
         this.field_78878_a = var1;
      }

      this.field_78877_c = var2;

      for(int var3 = 0; var3 < 4; ++var3) {
         this.field_78876_b[var3] = new ArrayList();
      }
   }

   public void func_78873_a(EntityFX var1) {
      int var2 = var1.func_70537_b();
      if (this.field_78876_b[var2].size() >= 4000) {
         this.field_78876_b[var2].remove(0);
      }

      this.field_78876_b[var2].add(var1);
   }

   public void func_78868_a() {
      for(int var1 = 0; var1 < 4; ++var1) {
         for(int var2 = 0; var2 < this.field_78876_b[var1].size(); ++var2) {
            EntityFX var3 = (EntityFX)this.field_78876_b[var1].get(var2);

            try {
               var3.func_70071_h_();
            } catch (Throwable var8) {
               CrashReport var5 = CrashReport.func_85055_a(var8, "Ticking Particle");
               CrashReportCategory var6 = var5.func_85058_a("Particle being ticked");
               var6.func_71500_a("Particle", new EffectRenderer$1(this, var3));
               var6.func_71500_a("Particle Type", new EffectRenderer$2(this, var1));
               throw new ReportedException(var5);
            }

            if (var3.field_70128_L) {
               this.field_78876_b[var1].remove(var2--);
            }
         }
      }
   }

   public void func_78874_a(Entity var1, float var2) {
      float var3 = ActiveRenderInfo.field_74588_d;
      float var4 = ActiveRenderInfo.field_74586_f;
      float var5 = ActiveRenderInfo.field_74587_g;
      float var6 = ActiveRenderInfo.field_74596_h;
      float var7 = ActiveRenderInfo.field_74589_e;
      EntityFX.field_70556_an = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      EntityFX.field_70554_ao = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      EntityFX.field_70555_ap = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;

      for(int var8 = 0; var8 < 3; ++var8) {
         if (!this.field_78876_b[var8].isEmpty()) {
            switch(var8) {
               case 0:
               default:
                  this.field_78877_c.func_110577_a(field_110737_b);
                  break;
               case 1:
                  this.field_78877_c.func_110577_a(TextureMap.field_110575_b);
                  break;
               case 2:
                  this.field_78877_c.func_110577_a(TextureMap.field_110576_c);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(false);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glAlphaFunc(516, 0.003921569F);
            Tessellator var9 = Tessellator.field_78398_a;
            var9.func_78382_b();

            for(int var10 = 0; var10 < this.field_78876_b[var8].size(); ++var10) {
               EntityFX var11 = (EntityFX)this.field_78876_b[var8].get(var10);
               var9.func_78380_c(var11.func_70070_b(var2));

               try {
                  var11.func_70539_a(var9, var2, var3, var7, var4, var5, var6);
               } catch (Throwable var16) {
                  CrashReport var13 = CrashReport.func_85055_a(var16, "Rendering Particle");
                  CrashReportCategory var14 = var13.func_85058_a("Particle being rendered");
                  var14.func_71500_a("Particle", new EffectRenderer$3(this, var11));
                  var14.func_71500_a("Particle Type", new EffectRenderer$4(this, var8));
                  throw new ReportedException(var13);
               }
            }

            var9.func_78381_a();
            GL11.glDisable(3042);
            GL11.glDepthMask(true);
            GL11.glAlphaFunc(516, 0.1F);
         }
      }
   }

   public void func_78872_b(Entity var1, float var2) {
      float var3 = 0.017453292F;
      float var4 = MathHelper.func_76134_b(var1.field_70177_z * 0.017453292F);
      float var5 = MathHelper.func_76126_a(var1.field_70177_z * 0.017453292F);
      float var6 = -var5 * MathHelper.func_76126_a(var1.field_70125_A * 0.017453292F);
      float var7 = var4 * MathHelper.func_76126_a(var1.field_70125_A * 0.017453292F);
      float var8 = MathHelper.func_76134_b(var1.field_70125_A * 0.017453292F);
      byte var9 = 3;
      List var10 = this.field_78876_b[var9];
      if (!var10.isEmpty()) {
         Tessellator var11 = Tessellator.field_78398_a;

         for(int var12 = 0; var12 < var10.size(); ++var12) {
            EntityFX var13 = (EntityFX)var10.get(var12);
            var11.func_78380_c(var13.func_70070_b(var2));
            var13.func_70539_a(var11, var2, var4, var8, var5, var6, var7);
         }
      }
   }

   public void func_78870_a(World var1) {
      this.field_78878_a = var1;

      for(int var2 = 0; var2 < 4; ++var2) {
         this.field_78876_b[var2].clear();
      }
   }

   public void func_147215_a(int var1, int var2, int var3, Block var4, int var5) {
      if (var4.func_149688_o() != Material.field_151579_a) {
         byte var6 = 4;

         for(int var7 = 0; var7 < var6; ++var7) {
            for(int var8 = 0; var8 < var6; ++var8) {
               for(int var9 = 0; var9 < var6; ++var9) {
                  double var10 = (double)var1 + ((double)var7 + 0.5) / (double)var6;
                  double var12 = (double)var2 + ((double)var8 + 0.5) / (double)var6;
                  double var14 = (double)var3 + ((double)var9 + 0.5) / (double)var6;
                  this.func_78873_a(
                     new EntityDiggingFX(
                           this.field_78878_a,
                           var10,
                           var12,
                           var14,
                           var10 - (double)var1 - 0.5,
                           var12 - (double)var2 - 0.5,
                           var14 - (double)var3 - 0.5,
                           var4,
                           var5
                        )
                        .func_70596_a(var1, var2, var3)
                  );
               }
            }
         }
      }
   }

   public void func_78867_a(int var1, int var2, int var3, int var4) {
      Block var5 = this.field_78878_a.func_147439_a(var1, var2, var3);
      if (var5.func_149688_o() != Material.field_151579_a) {
         float var6 = 0.1F;
         double var7 = (double)var1
            + this.field_78875_d.nextDouble() * (var5.func_149753_y() - var5.func_149704_x() - (double)(var6 * 2.0F))
            + (double)var6
            + var5.func_149704_x();
         double var9 = (double)var2
            + this.field_78875_d.nextDouble() * (var5.func_149669_A() - var5.func_149665_z() - (double)(var6 * 2.0F))
            + (double)var6
            + var5.func_149665_z();
         double var11 = (double)var3
            + this.field_78875_d.nextDouble() * (var5.func_149693_C() - var5.func_149706_B() - (double)(var6 * 2.0F))
            + (double)var6
            + var5.func_149706_B();
         if (var4 == 0) {
            var9 = (double)var2 + var5.func_149665_z() - (double)var6;
         }

         if (var4 == 1) {
            var9 = (double)var2 + var5.func_149669_A() + (double)var6;
         }

         if (var4 == 2) {
            var11 = (double)var3 + var5.func_149706_B() - (double)var6;
         }

         if (var4 == 3) {
            var11 = (double)var3 + var5.func_149693_C() + (double)var6;
         }

         if (var4 == 4) {
            var7 = (double)var1 + var5.func_149704_x() - (double)var6;
         }

         if (var4 == 5) {
            var7 = (double)var1 + var5.func_149753_y() + (double)var6;
         }

         this.func_78873_a(
            new EntityDiggingFX(this.field_78878_a, var7, var9, var11, 0.0, 0.0, 0.0, var5, this.field_78878_a.func_72805_g(var1, var2, var3))
               .func_70596_a(var1, var2, var3)
               .func_70543_e(0.2F)
               .func_70541_f(0.6F)
         );
      }
   }

   public String func_78869_b() {
      return "" + (this.field_78876_b[0].size() + this.field_78876_b[1].size() + this.field_78876_b[2].size());
   }
}
