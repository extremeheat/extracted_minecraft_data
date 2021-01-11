package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals {
   private static final int field_180268_a = (int)Math.pow(17.0D, 2.0D);
   private final Set<ChunkCoordIntPair> field_77193_b = Sets.newHashSet();

   public SpawnerAnimals() {
      super();
   }

   public int func_77192_a(WorldServer var1, boolean var2, boolean var3, boolean var4) {
      if (!var2 && !var3) {
         return 0;
      } else {
         this.field_77193_b.clear();
         int var5 = 0;
         Iterator var6 = var1.field_73010_i.iterator();

         while(true) {
            EntityPlayer var7;
            int var9;
            int var12;
            do {
               if (!var6.hasNext()) {
                  int var36 = 0;
                  BlockPos var37 = var1.func_175694_M();
                  EnumCreatureType[] var38 = EnumCreatureType.values();
                  var9 = var38.length;

                  label132:
                  for(int var39 = 0; var39 < var9; ++var39) {
                     EnumCreatureType var40 = var38[var39];
                     if ((!var40.func_75599_d() || var3) && (var40.func_75599_d() || var2) && (!var40.func_82705_e() || var4)) {
                        var12 = var1.func_72907_a(var40.func_75598_a());
                        int var41 = var40.func_75601_b() * var5 / field_180268_a;
                        if (var12 <= var41) {
                           Iterator var42 = this.field_77193_b.iterator();

                           label129:
                           while(true) {
                              int var17;
                              int var18;
                              int var19;
                              Block var20;
                              do {
                                 if (!var42.hasNext()) {
                                    continue label132;
                                 }

                                 ChunkCoordIntPair var15 = (ChunkCoordIntPair)var42.next();
                                 BlockPos var16 = func_180621_a(var1, var15.field_77276_a, var15.field_77275_b);
                                 var17 = var16.func_177958_n();
                                 var18 = var16.func_177956_o();
                                 var19 = var16.func_177952_p();
                                 var20 = var1.func_180495_p(var16).func_177230_c();
                              } while(var20.func_149721_r());

                              int var21 = 0;

                              for(int var22 = 0; var22 < 3; ++var22) {
                                 int var23 = var17;
                                 int var24 = var18;
                                 int var25 = var19;
                                 byte var26 = 6;
                                 BiomeGenBase.SpawnListEntry var27 = null;
                                 IEntityLivingData var28 = null;

                                 for(int var29 = 0; var29 < 4; ++var29) {
                                    var23 += var1.field_73012_v.nextInt(var26) - var1.field_73012_v.nextInt(var26);
                                    var24 += var1.field_73012_v.nextInt(1) - var1.field_73012_v.nextInt(1);
                                    var25 += var1.field_73012_v.nextInt(var26) - var1.field_73012_v.nextInt(var26);
                                    BlockPos var30 = new BlockPos(var23, var24, var25);
                                    float var31 = (float)var23 + 0.5F;
                                    float var32 = (float)var25 + 0.5F;
                                    if (!var1.func_175636_b((double)var31, (double)var24, (double)var32, 24.0D) && var37.func_177954_c((double)var31, (double)var24, (double)var32) >= 576.0D) {
                                       if (var27 == null) {
                                          var27 = var1.func_175734_a(var40, var30);
                                          if (var27 == null) {
                                             break;
                                          }
                                       }

                                       if (var1.func_175732_a(var40, var27, var30) && func_180267_a(EntitySpawnPlacementRegistry.func_180109_a(var27.field_76300_b), var1, var30)) {
                                          EntityLiving var33;
                                          try {
                                             var33 = (EntityLiving)var27.field_76300_b.getConstructor(World.class).newInstance(var1);
                                          } catch (Exception var35) {
                                             var35.printStackTrace();
                                             return var36;
                                          }

                                          var33.func_70012_b((double)var31, (double)var24, (double)var32, var1.field_73012_v.nextFloat() * 360.0F, 0.0F);
                                          if (var33.func_70601_bi() && var33.func_70058_J()) {
                                             var28 = var33.func_180482_a(var1.func_175649_E(new BlockPos(var33)), var28);
                                             if (var33.func_70058_J()) {
                                                ++var21;
                                                var1.func_72838_d(var33);
                                             }

                                             if (var21 >= var33.func_70641_bl()) {
                                                continue label129;
                                             }
                                          }

                                          var36 += var21;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  return var36;
               }

               var7 = (EntityPlayer)var6.next();
            } while(var7.func_175149_v());

            int var8 = MathHelper.func_76128_c(var7.field_70165_t / 16.0D);
            var9 = MathHelper.func_76128_c(var7.field_70161_v / 16.0D);
            byte var10 = 8;

            for(int var11 = -var10; var11 <= var10; ++var11) {
               for(var12 = -var10; var12 <= var10; ++var12) {
                  boolean var13 = var11 == -var10 || var11 == var10 || var12 == -var10 || var12 == var10;
                  ChunkCoordIntPair var14 = new ChunkCoordIntPair(var11 + var8, var12 + var9);
                  if (!this.field_77193_b.contains(var14)) {
                     ++var5;
                     if (!var13 && var1.func_175723_af().func_177730_a(var14)) {
                        this.field_77193_b.add(var14);
                     }
                  }
               }
            }
         }
      }
   }

   protected static BlockPos func_180621_a(World var0, int var1, int var2) {
      Chunk var3 = var0.func_72964_e(var1, var2);
      int var4 = var1 * 16 + var0.field_73012_v.nextInt(16);
      int var5 = var2 * 16 + var0.field_73012_v.nextInt(16);
      int var6 = MathHelper.func_154354_b(var3.func_177433_f(new BlockPos(var4, 0, var5)) + 1, 16);
      int var7 = var0.field_73012_v.nextInt(var6 > 0 ? var6 : var3.func_76625_h() + 16 - 1);
      return new BlockPos(var4, var7, var5);
   }

   public static boolean func_180267_a(EntityLiving.SpawnPlacementType var0, World var1, BlockPos var2) {
      if (!var1.func_175723_af().func_177746_a(var2)) {
         return false;
      } else {
         Block var3 = var1.func_180495_p(var2).func_177230_c();
         if (var0 == EntityLiving.SpawnPlacementType.IN_WATER) {
            return var3.func_149688_o().func_76224_d() && var1.func_180495_p(var2.func_177977_b()).func_177230_c().func_149688_o().func_76224_d() && !var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149721_r();
         } else {
            BlockPos var4 = var2.func_177977_b();
            if (!World.func_175683_a(var1, var4)) {
               return false;
            } else {
               Block var5 = var1.func_180495_p(var4).func_177230_c();
               boolean var6 = var5 != Blocks.field_150357_h && var5 != Blocks.field_180401_cv;
               return var6 && !var3.func_149721_r() && !var3.func_149688_o().func_76224_d() && !var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149721_r();
            }
         }
      }
   }

   public static void func_77191_a(World var0, BiomeGenBase var1, int var2, int var3, int var4, int var5, Random var6) {
      List var7 = var1.func_76747_a(EnumCreatureType.CREATURE);
      if (!var7.isEmpty()) {
         while(var6.nextFloat() < var1.func_76741_f()) {
            BiomeGenBase.SpawnListEntry var8 = (BiomeGenBase.SpawnListEntry)WeightedRandom.func_76271_a(var0.field_73012_v, var7);
            int var9 = var8.field_76301_c + var6.nextInt(1 + var8.field_76299_d - var8.field_76301_c);
            IEntityLivingData var10 = null;
            int var11 = var2 + var6.nextInt(var4);
            int var12 = var3 + var6.nextInt(var5);
            int var13 = var11;
            int var14 = var12;

            for(int var15 = 0; var15 < var9; ++var15) {
               boolean var16 = false;

               for(int var17 = 0; !var16 && var17 < 4; ++var17) {
                  BlockPos var18 = var0.func_175672_r(new BlockPos(var11, 0, var12));
                  if (func_180267_a(EntityLiving.SpawnPlacementType.ON_GROUND, var0, var18)) {
                     EntityLiving var19;
                     try {
                        var19 = (EntityLiving)var8.field_76300_b.getConstructor(World.class).newInstance(var0);
                     } catch (Exception var21) {
                        var21.printStackTrace();
                        continue;
                     }

                     var19.func_70012_b((double)((float)var11 + 0.5F), (double)var18.func_177956_o(), (double)((float)var12 + 0.5F), var6.nextFloat() * 360.0F, 0.0F);
                     var0.func_72838_d(var19);
                     var10 = var19.func_180482_a(var0.func_175649_E(new BlockPos(var19)), var10);
                     var16 = true;
                  }

                  var11 += var6.nextInt(5) - var6.nextInt(5);

                  for(var12 += var6.nextInt(5) - var6.nextInt(5); var11 < var2 || var11 >= var2 + var4 || var12 < var3 || var12 >= var3 + var4; var12 = var14 + var6.nextInt(5) - var6.nextInt(5)) {
                     var11 = var13 + var6.nextInt(5) - var6.nextInt(5);
                  }
               }
            }
         }

      }
   }
}
