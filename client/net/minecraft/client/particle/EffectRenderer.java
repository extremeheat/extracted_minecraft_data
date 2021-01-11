package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EffectRenderer {
   private static final ResourceLocation field_110737_b = new ResourceLocation("textures/particle/particles.png");
   protected World field_78878_a;
   private List<EntityFX>[][] field_78876_b = new List[4][];
   private List<EntityParticleEmitter> field_178933_d = Lists.newArrayList();
   private TextureManager field_78877_c;
   private Random field_78875_d = new Random();
   private Map<Integer, IParticleFactory> field_178932_g = Maps.newHashMap();

   public EffectRenderer(World var1, TextureManager var2) {
      super();
      this.field_78878_a = var1;
      this.field_78877_c = var2;

      for(int var3 = 0; var3 < 4; ++var3) {
         this.field_78876_b[var3] = new List[2];

         for(int var4 = 0; var4 < 2; ++var4) {
            this.field_78876_b[var3][var4] = Lists.newArrayList();
         }
      }

      this.func_178930_c();
   }

   private void func_178930_c() {
      this.func_178929_a(EnumParticleTypes.EXPLOSION_NORMAL.func_179348_c(), new EntityExplodeFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_BUBBLE.func_179348_c(), new EntityBubbleFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_SPLASH.func_179348_c(), new EntitySplashFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_WAKE.func_179348_c(), new EntityFishWakeFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_DROP.func_179348_c(), new EntityRainFX.Factory());
      this.func_178929_a(EnumParticleTypes.SUSPENDED.func_179348_c(), new EntitySuspendFX.Factory());
      this.func_178929_a(EnumParticleTypes.SUSPENDED_DEPTH.func_179348_c(), new EntityAuraFX.Factory());
      this.func_178929_a(EnumParticleTypes.CRIT.func_179348_c(), new EntityCrit2FX.Factory());
      this.func_178929_a(EnumParticleTypes.CRIT_MAGIC.func_179348_c(), new EntityCrit2FX.MagicFactory());
      this.func_178929_a(EnumParticleTypes.SMOKE_NORMAL.func_179348_c(), new EntitySmokeFX.Factory());
      this.func_178929_a(EnumParticleTypes.SMOKE_LARGE.func_179348_c(), new EntityCritFX.Factory());
      this.func_178929_a(EnumParticleTypes.SPELL.func_179348_c(), new EntitySpellParticleFX.Factory());
      this.func_178929_a(EnumParticleTypes.SPELL_INSTANT.func_179348_c(), new EntitySpellParticleFX.InstantFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_MOB.func_179348_c(), new EntitySpellParticleFX.MobFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_MOB_AMBIENT.func_179348_c(), new EntitySpellParticleFX.AmbientMobFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_WITCH.func_179348_c(), new EntitySpellParticleFX.WitchFactory());
      this.func_178929_a(EnumParticleTypes.DRIP_WATER.func_179348_c(), new EntityDropParticleFX.WaterFactory());
      this.func_178929_a(EnumParticleTypes.DRIP_LAVA.func_179348_c(), new EntityDropParticleFX.LavaFactory());
      this.func_178929_a(EnumParticleTypes.VILLAGER_ANGRY.func_179348_c(), new EntityHeartFX.AngryVillagerFactory());
      this.func_178929_a(EnumParticleTypes.VILLAGER_HAPPY.func_179348_c(), new EntityAuraFX.HappyVillagerFactory());
      this.func_178929_a(EnumParticleTypes.TOWN_AURA.func_179348_c(), new EntityAuraFX.Factory());
      this.func_178929_a(EnumParticleTypes.NOTE.func_179348_c(), new EntityNoteFX.Factory());
      this.func_178929_a(EnumParticleTypes.PORTAL.func_179348_c(), new EntityPortalFX.Factory());
      this.func_178929_a(EnumParticleTypes.ENCHANTMENT_TABLE.func_179348_c(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
      this.func_178929_a(EnumParticleTypes.FLAME.func_179348_c(), new EntityFlameFX.Factory());
      this.func_178929_a(EnumParticleTypes.LAVA.func_179348_c(), new EntityLavaFX.Factory());
      this.func_178929_a(EnumParticleTypes.FOOTSTEP.func_179348_c(), new EntityFootStepFX.Factory());
      this.func_178929_a(EnumParticleTypes.CLOUD.func_179348_c(), new EntityCloudFX.Factory());
      this.func_178929_a(EnumParticleTypes.REDSTONE.func_179348_c(), new EntityReddustFX.Factory());
      this.func_178929_a(EnumParticleTypes.SNOWBALL.func_179348_c(), new EntityBreakingFX.SnowballFactory());
      this.func_178929_a(EnumParticleTypes.SNOW_SHOVEL.func_179348_c(), new EntitySnowShovelFX.Factory());
      this.func_178929_a(EnumParticleTypes.SLIME.func_179348_c(), new EntityBreakingFX.SlimeFactory());
      this.func_178929_a(EnumParticleTypes.HEART.func_179348_c(), new EntityHeartFX.Factory());
      this.func_178929_a(EnumParticleTypes.BARRIER.func_179348_c(), new Barrier.Factory());
      this.func_178929_a(EnumParticleTypes.ITEM_CRACK.func_179348_c(), new EntityBreakingFX.Factory());
      this.func_178929_a(EnumParticleTypes.BLOCK_CRACK.func_179348_c(), new EntityDiggingFX.Factory());
      this.func_178929_a(EnumParticleTypes.BLOCK_DUST.func_179348_c(), new EntityBlockDustFX.Factory());
      this.func_178929_a(EnumParticleTypes.EXPLOSION_HUGE.func_179348_c(), new EntityHugeExplodeFX.Factory());
      this.func_178929_a(EnumParticleTypes.EXPLOSION_LARGE.func_179348_c(), new EntityLargeExplodeFX.Factory());
      this.func_178929_a(EnumParticleTypes.FIREWORKS_SPARK.func_179348_c(), new EntityFirework.Factory());
      this.func_178929_a(EnumParticleTypes.MOB_APPEARANCE.func_179348_c(), new MobAppearance.Factory());
   }

   public void func_178929_a(int var1, IParticleFactory var2) {
      this.field_178932_g.put(var1, var2);
   }

   public void func_178926_a(Entity var1, EnumParticleTypes var2) {
      this.field_178933_d.add(new EntityParticleEmitter(this.field_78878_a, var1, var2));
   }

   public EntityFX func_178927_a(int var1, double var2, double var4, double var6, double var8, double var10, double var12, int... var14) {
      IParticleFactory var15 = (IParticleFactory)this.field_178932_g.get(var1);
      if (var15 != null) {
         EntityFX var16 = var15.func_178902_a(var1, this.field_78878_a, var2, var4, var6, var8, var10, var12, var14);
         if (var16 != null) {
            this.func_78873_a(var16);
            return var16;
         }
      }

      return null;
   }

   public void func_78873_a(EntityFX var1) {
      int var2 = var1.func_70537_b();
      int var3 = var1.func_174838_j() != 1.0F ? 0 : 1;
      if (this.field_78876_b[var2][var3].size() >= 4000) {
         this.field_78876_b[var2][var3].remove(0);
      }

      this.field_78876_b[var2][var3].add(var1);
   }

   public void func_78868_a() {
      for(int var1 = 0; var1 < 4; ++var1) {
         this.func_178922_a(var1);
      }

      ArrayList var4 = Lists.newArrayList();
      Iterator var2 = this.field_178933_d.iterator();

      while(var2.hasNext()) {
         EntityParticleEmitter var3 = (EntityParticleEmitter)var2.next();
         var3.func_70071_h_();
         if (var3.field_70128_L) {
            var4.add(var3);
         }
      }

      this.field_178933_d.removeAll(var4);
   }

   private void func_178922_a(int var1) {
      for(int var2 = 0; var2 < 2; ++var2) {
         this.func_178925_a(this.field_78876_b[var1][var2]);
      }

   }

   private void func_178925_a(List<EntityFX> var1) {
      ArrayList var2 = Lists.newArrayList();

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         EntityFX var4 = (EntityFX)var1.get(var3);
         this.func_178923_d(var4);
         if (var4.field_70128_L) {
            var2.add(var4);
         }
      }

      var1.removeAll(var2);
   }

   private void func_178923_d(final EntityFX var1) {
      try {
         var1.func_70071_h_();
      } catch (Throwable var6) {
         CrashReport var3 = CrashReport.func_85055_a(var6, "Ticking Particle");
         CrashReportCategory var4 = var3.func_85058_a("Particle being ticked");
         final int var5 = var1.func_70537_b();
         var4.func_71500_a("Particle", new Callable<String>() {
            public String call() throws Exception {
               return var1.toString();
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         var4.func_71500_a("Particle Type", new Callable<String>() {
            public String call() throws Exception {
               if (var5 == 0) {
                  return "MISC_TEXTURE";
               } else if (var5 == 1) {
                  return "TERRAIN_TEXTURE";
               } else {
                  return var5 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var5;
               }
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
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
      EntityFX.field_70556_an = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      EntityFX.field_70554_ao = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      EntityFX.field_70555_ap = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      GlStateManager.func_179147_l();
      GlStateManager.func_179112_b(770, 771);
      GlStateManager.func_179092_a(516, 0.003921569F);

      for(final int var8 = 0; var8 < 3; ++var8) {
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
               WorldRenderer var11 = var10.func_178180_c();
               var11.func_181668_a(7, DefaultVertexFormats.field_181704_d);

               for(int var12 = 0; var12 < this.field_78876_b[var8][var9].size(); ++var12) {
                  final EntityFX var13 = (EntityFX)this.field_78876_b[var8][var9].get(var12);

                  try {
                     var13.func_180434_a(var11, var1, var2, var3, var7, var4, var5, var6);
                  } catch (Throwable var18) {
                     CrashReport var15 = CrashReport.func_85055_a(var18, "Rendering Particle");
                     CrashReportCategory var16 = var15.func_85058_a("Particle being rendered");
                     var16.func_71500_a("Particle", new Callable<String>() {
                        public String call() throws Exception {
                           return var13.toString();
                        }

                        // $FF: synthetic method
                        public Object call() throws Exception {
                           return this.call();
                        }
                     });
                     var16.func_71500_a("Particle Type", new Callable<String>() {
                        public String call() throws Exception {
                           if (var8 == 0) {
                              return "MISC_TEXTURE";
                           } else if (var8 == 1) {
                              return "TERRAIN_TEXTURE";
                           } else {
                              return var8 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var8;
                           }
                        }

                        // $FF: synthetic method
                        public Object call() throws Exception {
                           return this.call();
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
      float var3 = 0.017453292F;
      float var4 = MathHelper.func_76134_b(var1.field_70177_z * 0.017453292F);
      float var5 = MathHelper.func_76126_a(var1.field_70177_z * 0.017453292F);
      float var6 = -var5 * MathHelper.func_76126_a(var1.field_70125_A * 0.017453292F);
      float var7 = var4 * MathHelper.func_76126_a(var1.field_70125_A * 0.017453292F);
      float var8 = MathHelper.func_76134_b(var1.field_70125_A * 0.017453292F);

      for(int var9 = 0; var9 < 2; ++var9) {
         List var10 = this.field_78876_b[3][var9];
         if (!var10.isEmpty()) {
            Tessellator var11 = Tessellator.func_178181_a();
            WorldRenderer var12 = var11.func_178180_c();

            for(int var13 = 0; var13 < var10.size(); ++var13) {
               EntityFX var14 = (EntityFX)var10.get(var13);
               var14.func_180434_a(var12, var1, var2, var4, var8, var5, var6, var7);
            }
         }
      }

   }

   public void func_78870_a(World var1) {
      this.field_78878_a = var1;

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 2; ++var3) {
            this.field_78876_b[var2][var3].clear();
         }
      }

      this.field_178933_d.clear();
   }

   public void func_180533_a(BlockPos var1, IBlockState var2) {
      if (var2.func_177230_c().func_149688_o() != Material.field_151579_a) {
         var2 = var2.func_177230_c().func_176221_a(var2, this.field_78878_a, var1);
         byte var3 = 4;

         for(int var4 = 0; var4 < var3; ++var4) {
            for(int var5 = 0; var5 < var3; ++var5) {
               for(int var6 = 0; var6 < var3; ++var6) {
                  double var7 = (double)var1.func_177958_n() + ((double)var4 + 0.5D) / (double)var3;
                  double var9 = (double)var1.func_177956_o() + ((double)var5 + 0.5D) / (double)var3;
                  double var11 = (double)var1.func_177952_p() + ((double)var6 + 0.5D) / (double)var3;
                  this.func_78873_a((new EntityDiggingFX(this.field_78878_a, var7, var9, var11, var7 - (double)var1.func_177958_n() - 0.5D, var9 - (double)var1.func_177956_o() - 0.5D, var11 - (double)var1.func_177952_p() - 0.5D, var2)).func_174846_a(var1));
               }
            }
         }

      }
   }

   public void func_180532_a(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.field_78878_a.func_180495_p(var1);
      Block var4 = var3.func_177230_c();
      if (var4.func_149645_b() != -1) {
         int var5 = var1.func_177958_n();
         int var6 = var1.func_177956_o();
         int var7 = var1.func_177952_p();
         float var8 = 0.1F;
         double var9 = (double)var5 + this.field_78875_d.nextDouble() * (var4.func_149753_y() - var4.func_149704_x() - (double)(var8 * 2.0F)) + (double)var8 + var4.func_149704_x();
         double var11 = (double)var6 + this.field_78875_d.nextDouble() * (var4.func_149669_A() - var4.func_149665_z() - (double)(var8 * 2.0F)) + (double)var8 + var4.func_149665_z();
         double var13 = (double)var7 + this.field_78875_d.nextDouble() * (var4.func_149693_C() - var4.func_149706_B() - (double)(var8 * 2.0F)) + (double)var8 + var4.func_149706_B();
         if (var2 == EnumFacing.DOWN) {
            var11 = (double)var6 + var4.func_149665_z() - (double)var8;
         }

         if (var2 == EnumFacing.UP) {
            var11 = (double)var6 + var4.func_149669_A() + (double)var8;
         }

         if (var2 == EnumFacing.NORTH) {
            var13 = (double)var7 + var4.func_149706_B() - (double)var8;
         }

         if (var2 == EnumFacing.SOUTH) {
            var13 = (double)var7 + var4.func_149693_C() + (double)var8;
         }

         if (var2 == EnumFacing.WEST) {
            var9 = (double)var5 + var4.func_149704_x() - (double)var8;
         }

         if (var2 == EnumFacing.EAST) {
            var9 = (double)var5 + var4.func_149753_y() + (double)var8;
         }

         this.func_78873_a((new EntityDiggingFX(this.field_78878_a, var9, var11, var13, 0.0D, 0.0D, 0.0D, var3)).func_174846_a(var1).func_70543_e(0.2F).func_70541_f(0.6F));
      }
   }

   public void func_178928_b(EntityFX var1) {
      this.func_178924_a(var1, 1, 0);
   }

   public void func_178931_c(EntityFX var1) {
      this.func_178924_a(var1, 0, 1);
   }

   private void func_178924_a(EntityFX var1, int var2, int var3) {
      for(int var4 = 0; var4 < 4; ++var4) {
         if (this.field_78876_b[var4][var2].contains(var1)) {
            this.field_78876_b[var4][var2].remove(var1);
            this.field_78876_b[var4][var3].add(var1);
         }
      }

   }

   public String func_78869_b() {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 2; ++var3) {
            var1 += this.field_78876_b[var2][var3].size();
         }
      }

      return "" + var1;
   }
}
