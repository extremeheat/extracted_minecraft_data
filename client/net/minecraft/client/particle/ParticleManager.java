package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;

public class ParticleManager {
   private static final ResourceLocation field_110737_b = new ResourceLocation("textures/particle/particles.png");
   protected World field_78878_a;
   private final ArrayDeque<Particle>[][] field_78876_b = (ArrayDeque[][])(new ArrayDeque[4][]);
   private final Queue<ParticleEmitter> field_178933_d = Queues.newArrayDeque();
   private final TextureManager field_78877_c;
   private final Random field_78875_d = new Random();
   private final Int2ObjectMap<IParticleFactory<?>> field_178932_g = new Int2ObjectOpenHashMap();
   private final Queue<Particle> field_187241_h = Queues.newArrayDeque();

   public ParticleManager(World var1, TextureManager var2) {
      super();
      this.field_78878_a = var1;
      this.field_78877_c = var2;

      for(int var3 = 0; var3 < 4; ++var3) {
         this.field_78876_b[var3] = (ArrayDeque[])(new ArrayDeque[2]);

         for(int var4 = 0; var4 < 2; ++var4) {
            this.field_78876_b[var3][var4] = Queues.newArrayDeque();
         }
      }

      this.func_178930_c();
   }

   private void func_178930_c() {
      this.func_199283_a(Particles.field_197608_a, new ParticleSpell.AmbientMobFactory());
      this.func_199283_a(Particles.field_197609_b, new ParticleHeart.AngryVillagerFactory());
      this.func_199283_a(Particles.field_197610_c, new Barrier.Factory());
      this.func_199283_a(Particles.field_197611_d, new ParticleDigging.Factory());
      this.func_199283_a(Particles.field_197612_e, new ParticleBubble.Factory());
      this.func_199283_a(Particles.field_203220_f, new ParticleBubbleColumnUp.Factory());
      this.func_199283_a(Particles.field_203217_T, new ParticleBubblePop.Factory());
      this.func_199283_a(Particles.field_197613_f, new ParticleCloud.Factory());
      this.func_199283_a(Particles.field_197614_g, new ParticleCrit.Factory());
      this.func_199283_a(Particles.field_203218_U, new ParticleCurrentDown.Factory());
      this.func_199283_a(Particles.field_197615_h, new ParticleCrit.DamageIndicatorFactory());
      this.func_199283_a(Particles.field_197616_i, new ParticleDragonBreath.Factory());
      this.func_199283_a(Particles.field_206864_X, new ParticleSuspendedTown.DolphinSpeedFactory());
      this.func_199283_a(Particles.field_197617_j, new ParticleDrip.LavaFactory());
      this.func_199283_a(Particles.field_197618_k, new ParticleDrip.WaterFactory());
      this.func_199283_a(Particles.field_197619_l, new ParticleRedstone.Factory());
      this.func_199283_a(Particles.field_197620_m, new ParticleSpell.Factory());
      this.func_199283_a(Particles.field_197621_n, new ParticleMobAppearance.Factory());
      this.func_199283_a(Particles.field_197622_o, new ParticleCrit.MagicFactory());
      this.func_199283_a(Particles.field_197623_p, new ParticleEnchantmentTable.EnchantmentTable());
      this.func_199283_a(Particles.field_197624_q, new ParticleEndRod.Factory());
      this.func_199283_a(Particles.field_197625_r, new ParticleSpell.MobFactory());
      this.func_199283_a(Particles.field_197626_s, new ParticleExplosionHuge.Factory());
      this.func_199283_a(Particles.field_197627_t, new ParticleExplosionLarge.Factory());
      this.func_199283_a(Particles.field_197628_u, new ParticleFallingDust.Factory());
      this.func_199283_a(Particles.field_197629_v, new ParticleFirework.Factory());
      this.func_199283_a(Particles.field_197630_w, new ParticleWaterWake.Factory());
      this.func_199283_a(Particles.field_197631_x, new ParticleFlame.Factory());
      this.func_199283_a(Particles.field_197632_y, new ParticleSuspendedTown.HappyVillagerFactory());
      this.func_199283_a(Particles.field_197633_z, new ParticleHeart.Factory());
      this.func_199283_a(Particles.field_197590_A, new ParticleSpell.InstantFactory());
      this.func_199283_a(Particles.field_197591_B, new ParticleBreaking.Factory());
      this.func_199283_a(Particles.field_197592_C, new ParticleBreaking.SlimeFactory());
      this.func_199283_a(Particles.field_197593_D, new ParticleBreaking.SnowballFactory());
      this.func_199283_a(Particles.field_197594_E, new ParticleSmokeLarge.Factory());
      this.func_199283_a(Particles.field_197595_F, new ParticleLava.Factory());
      this.func_199283_a(Particles.field_197596_G, new ParticleSuspendedTown.Factory());
      this.func_199283_a(Particles.field_205167_W, new ParticleEnchantmentTable.NautilusFactory());
      this.func_199283_a(Particles.field_197597_H, new ParticleNote.Factory());
      this.func_199283_a(Particles.field_197598_I, new ParticleExplosion.Factory());
      this.func_199283_a(Particles.field_197599_J, new ParticlePortal.Factory());
      this.func_199283_a(Particles.field_197600_K, new ParticleRain.Factory());
      this.func_199283_a(Particles.field_197601_L, new ParticleSmokeNormal.Factory());
      this.func_199283_a(Particles.field_197602_M, new ParticleSpit.Factory());
      this.func_199283_a(Particles.field_197603_N, new ParticleSweepAttack.Factory());
      this.func_199283_a(Particles.field_197604_O, new ParticleTotem.Factory());
      this.func_199283_a(Particles.field_203219_V, new ParticleSquidInk.Factory());
      this.func_199283_a(Particles.field_197605_P, new ParticleSuspend.Factory());
      this.func_199283_a(Particles.field_197606_Q, new ParticleSplash.Factory());
      this.func_199283_a(Particles.field_197607_R, new ParticleSpell.WitchFactory());
   }

   public <T extends IParticleData> void func_199283_a(ParticleType<T> var1, IParticleFactory<T> var2) {
      this.field_178932_g.put(IRegistry.field_212632_u.func_148757_b(var1), var2);
   }

   public void func_199282_a(Entity var1, IParticleData var2) {
      this.field_178933_d.add(new ParticleEmitter(this.field_78878_a, var1, var2));
   }

   public void func_199281_a(Entity var1, IParticleData var2, int var3) {
      this.field_178933_d.add(new ParticleEmitter(this.field_78878_a, var1, var2, var3));
   }

   @Nullable
   public Particle func_199280_a(IParticleData var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      Particle var14 = this.func_199927_b(var1, var2, var4, var6, var8, var10, var12);
      if (var14 != null) {
         this.func_78873_a(var14);
         return var14;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends IParticleData> Particle func_199927_b(T var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      IParticleFactory var14 = (IParticleFactory)this.field_178932_g.get(IRegistry.field_212632_u.func_148757_b(var1.func_197554_b()));
      return var14 == null ? null : var14.func_199234_a(var1, this.field_78878_a, var2, var4, var6, var8, var10, var12);
   }

   public void func_78873_a(Particle var1) {
      this.field_187241_h.add(var1);
   }

   public void func_78868_a() {
      for(int var1 = 0; var1 < 4; ++var1) {
         this.func_178922_a(var1);
      }

      if (!this.field_178933_d.isEmpty()) {
         ArrayList var4 = Lists.newArrayList();
         Iterator var2 = this.field_178933_d.iterator();

         while(var2.hasNext()) {
            ParticleEmitter var3 = (ParticleEmitter)var2.next();
            var3.func_189213_a();
            if (!var3.func_187113_k()) {
               var4.add(var3);
            }
         }

         this.field_178933_d.removeAll(var4);
      }

      if (!this.field_187241_h.isEmpty()) {
         for(Particle var5 = (Particle)this.field_187241_h.poll(); var5 != null; var5 = (Particle)this.field_187241_h.poll()) {
            int var6 = var5.func_70537_b();
            int var7 = var5.func_187111_c() ? 0 : 1;
            if (this.field_78876_b[var6][var7].size() >= 16384) {
               this.field_78876_b[var6][var7].removeFirst();
            }

            this.field_78876_b[var6][var7].add(var5);
         }
      }

   }

   private void func_178922_a(int var1) {
      this.field_78878_a.field_72984_F.func_76320_a(String.valueOf(var1));

      for(int var2 = 0; var2 < 2; ++var2) {
         this.field_78878_a.field_72984_F.func_76320_a(String.valueOf(var2));
         this.func_187240_a(this.field_78876_b[var1][var2]);
         this.field_78878_a.field_72984_F.func_76319_b();
      }

      this.field_78878_a.field_72984_F.func_76319_b();
   }

   private void func_187240_a(Queue<Particle> var1) {
      if (!var1.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Particle var3 = (Particle)var2.next();
            this.func_178923_d(var3);
            if (!var3.func_187113_k()) {
               var2.remove();
            }
         }
      }

   }

   private void func_178923_d(Particle var1) {
      try {
         var1.func_189213_a();
      } catch (Throwable var6) {
         CrashReport var3 = CrashReport.func_85055_a(var6, "Ticking Particle");
         CrashReportCategory var4 = var3.func_85058_a("Particle being ticked");
         int var5 = var1.func_70537_b();
         var4.func_189529_a("Particle", var1::toString);
         var4.func_189529_a("Particle Type", () -> {
            if (var5 == 0) {
               return "MISC_TEXTURE";
            } else if (var5 == 1) {
               return "TERRAIN_TEXTURE";
            } else {
               return var5 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var5;
            }
         });
         throw new ReportedException(var3);
      }
   }

   public void func_78874_a(Entity var1, float var2) {
      float var3 = ActiveRenderInfo.func_178808_b();
      float var4 = ActiveRenderInfo.func_178803_d();
      float var5 = ActiveRenderInfo.func_178805_e();
      float var6 = ActiveRenderInfo.func_178807_f();
      float var7 = ActiveRenderInfo.func_178809_c();
      Particle.field_70556_an = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      Particle.field_70554_ao = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      Particle.field_70555_ap = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      Particle.field_190016_K = var1.func_70676_i(var2);
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.func_179092_a(516, 0.003921569F);

      for(int var8 = 0; var8 < 3; ++var8) {
         for(int var9 = 0; var9 < 2; ++var9) {
            if (!this.field_78876_b[var8][var9].isEmpty()) {
               switch(var9) {
               case 0:
                  GlStateManager.func_179132_a(false);
                  break;
               case 1:
                  GlStateManager.func_179132_a(true);
               }

               switch(var8) {
               case 0:
               default:
                  this.field_78877_c.func_110577_a(field_110737_b);
                  break;
               case 1:
                  this.field_78877_c.func_110577_a(TextureMap.field_110575_b);
               }

               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator var10 = Tessellator.func_178181_a();
               BufferBuilder var11 = var10.func_178180_c();
               var11.func_181668_a(7, DefaultVertexFormats.field_181704_d);
               Iterator var12 = this.field_78876_b[var8][var9].iterator();

               while(var12.hasNext()) {
                  Particle var13 = (Particle)var12.next();

                  try {
                     var13.func_180434_a(var11, var1, var2, var3, var7, var4, var5, var6);
                  } catch (Throwable var18) {
                     CrashReport var15 = CrashReport.func_85055_a(var18, "Rendering Particle");
                     CrashReportCategory var16 = var15.func_85058_a("Particle being rendered");
                     var16.func_189529_a("Particle", var13::toString);
                     var16.func_189529_a("Particle Type", () -> {
                        if (var8 == 0) {
                           return "MISC_TEXTURE";
                        } else if (var8 == 1) {
                           return "TERRAIN_TEXTURE";
                        } else {
                           return var8 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var8;
                        }
                     });
                     throw new ReportedException(var15);
                  }
               }

               var10.func_78381_a();
            }
         }
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179084_k();
      GlStateManager.func_179092_a(516, 0.1F);
   }

   public void func_78872_b(Entity var1, float var2) {
      float var3 = ActiveRenderInfo.func_178808_b();
      float var4 = ActiveRenderInfo.func_178803_d();
      float var5 = ActiveRenderInfo.func_178805_e();
      float var6 = ActiveRenderInfo.func_178807_f();
      float var7 = ActiveRenderInfo.func_178809_c();
      Particle.field_70556_an = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      Particle.field_70554_ao = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      Particle.field_70555_ap = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      Particle.field_190016_K = var1.func_70676_i(var2);

      for(int var8 = 0; var8 < 2; ++var8) {
         ArrayDeque var9 = this.field_78876_b[3][var8];
         if (!var9.isEmpty()) {
            Tessellator var10 = Tessellator.func_178181_a();
            BufferBuilder var11 = var10.func_178180_c();
            Iterator var12 = var9.iterator();

            while(var12.hasNext()) {
               Particle var13 = (Particle)var12.next();
               var13.func_180434_a(var11, var1, var2, var3, var7, var4, var5, var6);
            }
         }
      }

   }

   public void func_78870_a(@Nullable World var1) {
      this.field_78878_a = var1;

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 2; ++var3) {
            this.field_78876_b[var2][var3].clear();
         }
      }

      this.field_178933_d.clear();
   }

   public void func_180533_a(BlockPos var1, IBlockState var2) {
      if (!var2.func_196958_f()) {
         VoxelShape var3 = var2.func_196954_c(this.field_78878_a, var1);
         double var4 = 0.25D;
         var3.func_197755_b((var3x, var5, var7, var9, var11, var13) -> {
            double var15 = Math.min(1.0D, var9 - var3x);
            double var17 = Math.min(1.0D, var11 - var5);
            double var19 = Math.min(1.0D, var13 - var7);
            int var21 = Math.max(2, MathHelper.func_76143_f(var15 / 0.25D));
            int var22 = Math.max(2, MathHelper.func_76143_f(var17 / 0.25D));
            int var23 = Math.max(2, MathHelper.func_76143_f(var19 / 0.25D));

            for(int var24 = 0; var24 < var21; ++var24) {
               for(int var25 = 0; var25 < var22; ++var25) {
                  for(int var26 = 0; var26 < var23; ++var26) {
                     double var27 = ((double)var24 + 0.5D) / (double)var21;
                     double var29 = ((double)var25 + 0.5D) / (double)var22;
                     double var31 = ((double)var26 + 0.5D) / (double)var23;
                     double var33 = var27 * var15 + var3x;
                     double var35 = var29 * var17 + var5;
                     double var37 = var31 * var19 + var7;
                     this.func_78873_a((new ParticleDigging(this.field_78878_a, (double)var1.func_177958_n() + var33, (double)var1.func_177956_o() + var35, (double)var1.func_177952_p() + var37, var27 - 0.5D, var29 - 0.5D, var31 - 0.5D, var2)).func_174846_a(var1));
                  }
               }
            }

         });
      }
   }

   public void func_180532_a(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.field_78878_a.func_180495_p(var1);
      if (var3.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
         int var4 = var1.func_177958_n();
         int var5 = var1.func_177956_o();
         int var6 = var1.func_177952_p();
         float var7 = 0.1F;
         AxisAlignedBB var8 = var3.func_196954_c(this.field_78878_a, var1).func_197752_a();
         double var9 = (double)var4 + this.field_78875_d.nextDouble() * (var8.field_72336_d - var8.field_72340_a - 0.20000000298023224D) + 0.10000000149011612D + var8.field_72340_a;
         double var11 = (double)var5 + this.field_78875_d.nextDouble() * (var8.field_72337_e - var8.field_72338_b - 0.20000000298023224D) + 0.10000000149011612D + var8.field_72338_b;
         double var13 = (double)var6 + this.field_78875_d.nextDouble() * (var8.field_72334_f - var8.field_72339_c - 0.20000000298023224D) + 0.10000000149011612D + var8.field_72339_c;
         if (var2 == EnumFacing.DOWN) {
            var11 = (double)var5 + var8.field_72338_b - 0.10000000149011612D;
         }

         if (var2 == EnumFacing.UP) {
            var11 = (double)var5 + var8.field_72337_e + 0.10000000149011612D;
         }

         if (var2 == EnumFacing.NORTH) {
            var13 = (double)var6 + var8.field_72339_c - 0.10000000149011612D;
         }

         if (var2 == EnumFacing.SOUTH) {
            var13 = (double)var6 + var8.field_72334_f + 0.10000000149011612D;
         }

         if (var2 == EnumFacing.WEST) {
            var9 = (double)var4 + var8.field_72340_a - 0.10000000149011612D;
         }

         if (var2 == EnumFacing.EAST) {
            var9 = (double)var4 + var8.field_72336_d + 0.10000000149011612D;
         }

         this.func_78873_a((new ParticleDigging(this.field_78878_a, var9, var11, var13, 0.0D, 0.0D, 0.0D, var3)).func_174846_a(var1).func_70543_e(0.2F).func_70541_f(0.6F));
      }
   }

   public String func_78869_b() {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 2; ++var3) {
            var1 += this.field_78876_b[var2][var3].size();
         }
      }

      return String.valueOf(var1);
   }
}
