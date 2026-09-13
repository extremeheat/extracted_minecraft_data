package net.minecraft.world;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase$SpawnListEntry;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals {
   private HashMap field_77193_b = new HashMap();

   public SpawnerAnimals() {
      super();
   }

   protected static ChunkPosition func_151350_a(World var0, int var1, int var2) {
      Chunk var3 = var0.func_72964_e(var1, var2);
      int var4 = var1 * 16 + var0.field_73012_v.nextInt(16);
      int var5 = var2 * 16 + var0.field_73012_v.nextInt(16);
      int var6 = var0.field_73012_v.nextInt(var3 == null ? var0.func_72940_L() : var3.func_76625_h() + 16 - 1);
      return new ChunkPosition(var4, var6, var5);
   }

   public int func_77192_a(WorldServer var1, boolean var2, boolean var3, boolean var4) {
      if (!var2 && !var3) {
         return 0;
      } else {
         this.field_77193_b.clear();

         for(int var5 = 0; var5 < var1.field_73010_i.size(); ++var5) {
            EntityPlayer var6 = (EntityPlayer)var1.field_73010_i.get(var5);
            int var7 = MathHelper.func_76128_c(var6.field_70165_t / 16.0);
            int var8 = MathHelper.func_76128_c(var6.field_70161_v / 16.0);
            byte var9 = 8;

            for(int var10 = -var9; var10 <= var9; ++var10) {
               for(int var11 = -var9; var11 <= var9; ++var11) {
                  boolean var12 = var10 == -var9 || var10 == var9 || var11 == -var9 || var11 == var9;
                  ChunkCoordIntPair var13 = new ChunkCoordIntPair(var10 + var7, var11 + var8);
                  if (!var12) {
                     this.field_77193_b.put(var13, false);
                  } else if (!this.field_77193_b.containsKey(var13)) {
                     this.field_77193_b.put(var13, true);
                  }
               }
            }
         }

         int var34 = 0;
         ChunkCoordinates var35 = var1.func_72861_E();

         for(EnumCreatureType var39 : EnumCreatureType.values()) {
            if ((!var39.func_75599_d() || var3)
               && (var39.func_75599_d() || var2)
               && (!var39.func_82705_e() || var4)
               && var1.func_72907_a(var39.func_75598_a()) <= var39.func_75601_b() * this.field_77193_b.size() / 256) {
               label128:
               for(ChunkCoordIntPair var41 : this.field_77193_b.keySet()) {
                  if (!this.field_77193_b.get(var41)) {
                     ChunkPosition var42 = func_151350_a(var1, var41.field_77276_a, var41.field_77275_b);
                     int var14 = var42.field_151329_a;
                     int var15 = var42.field_151327_b;
                     int var16 = var42.field_151328_c;
                     if (!var1.func_147439_a(var14, var15, var16).func_149721_r()
                        && var1.func_147439_a(var14, var15, var16).func_149688_o() == var39.func_75600_c()) {
                        int var17 = 0;

                        for(int var18 = 0; var18 < 3; ++var18) {
                           int var19 = var14;
                           int var20 = var15;
                           int var21 = var16;
                           byte var22 = 6;
                           BiomeGenBase$SpawnListEntry var23 = null;
                           IEntityLivingData var24 = null;

                           for(int var25 = 0; var25 < 4; ++var25) {
                              var19 += var1.field_73012_v.nextInt(var22) - var1.field_73012_v.nextInt(var22);
                              var20 += var1.field_73012_v.nextInt(1) - var1.field_73012_v.nextInt(1);
                              var21 += var1.field_73012_v.nextInt(var22) - var1.field_73012_v.nextInt(var22);
                              if (func_77190_a(var39, var1, var19, var20, var21)) {
                                 float var26 = (float)var19 + 0.5F;
                                 float var27 = (float)var20;
                                 float var28 = (float)var21 + 0.5F;
                                 if (var1.func_72977_a((double)var26, (double)var27, (double)var28, 24.0) == null) {
                                    float var29 = var26 - (float)var35.field_71574_a;
                                    float var30 = var27 - (float)var35.field_71572_b;
                                    float var31 = var28 - (float)var35.field_71573_c;
                                    float var32 = var29 * var29 + var30 * var30 + var31 * var31;
                                    if (!(var32 < 576.0F)) {
                                       if (var23 == null) {
                                          var23 = var1.func_73057_a(var39, var19, var20, var21);
                                          if (var23 == null) {
                                             break;
                                          }
                                       }

                                       try {
                                          var43 = (EntityLiving)var23.field_76300_b.getConstructor(World.class).newInstance(var1);
                                       } catch (Exception var33) {
                                          var33.printStackTrace();
                                          return var34;
                                       }

                                       var43.func_70012_b((double)var26, (double)var27, (double)var28, var1.field_73012_v.nextFloat() * 360.0F, 0.0F);
                                       if (var43.func_70601_bi()) {
                                          ++var17;
                                          var1.func_72838_d(var43);
                                          var24 = var43.func_110161_a(var24);
                                          if (var17 >= var43.func_70641_bl()) {
                                             continue label128;
                                          }
                                       }

                                       var34 += var17;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return var34;
      }
   }

   public static boolean func_77190_a(EnumCreatureType var0, World var1, int var2, int var3, int var4) {
      if (var0.func_75600_c() == Material.field_151586_h) {
         return var1.func_147439_a(var2, var3, var4).func_149688_o().func_76224_d()
            && var1.func_147439_a(var2, var3 - 1, var4).func_149688_o().func_76224_d()
            && !var1.func_147439_a(var2, var3 + 1, var4).func_149721_r();
      } else if (!World.func_147466_a(var1, var2, var3 - 1, var4)) {
         return false;
      } else {
         Block var5 = var1.func_147439_a(var2, var3 - 1, var4);
         return var5 != Blocks.field_150357_h
            && !var1.func_147439_a(var2, var3, var4).func_149721_r()
            && !var1.func_147439_a(var2, var3, var4).func_149688_o().func_76224_d()
            && !var1.func_147439_a(var2, var3 + 1, var4).func_149721_r();
      }
   }

   public static void func_77191_a(World var0, BiomeGenBase var1, int var2, int var3, int var4, int var5, Random var6) {
      List var7 = var1.func_76747_a(EnumCreatureType.creature);
      if (!var7.isEmpty()) {
         while(var6.nextFloat() < var1.func_76741_f()) {
            BiomeGenBase$SpawnListEntry var8 = (BiomeGenBase$SpawnListEntry)WeightedRandom.func_76271_a(var0.field_73012_v, var7);
            IEntityLivingData var9 = null;
            int var10 = var8.field_76301_c + var6.nextInt(1 + var8.field_76299_d - var8.field_76301_c);
            int var11 = var2 + var6.nextInt(var4);
            int var12 = var3 + var6.nextInt(var5);
            int var13 = var11;
            int var14 = var12;

            for(int var15 = 0; var15 < var10; ++var15) {
               boolean var16 = false;

               for(int var17 = 0; !var16 && var17 < 4; ++var17) {
                  int var18 = var0.func_72825_h(var11, var12);
                  if (func_77190_a(EnumCreatureType.creature, var0, var11, var18, var12)) {
                     float var19 = (float)var11 + 0.5F;
                     float var20 = (float)var18;
                     float var21 = (float)var12 + 0.5F;

                     EntityLiving var22;
                     try {
                        var22 = (EntityLiving)var8.field_76300_b.getConstructor(World.class).newInstance(var0);
                     } catch (Exception var24) {
                        var24.printStackTrace();
                        continue;
                     }

                     var22.func_70012_b((double)var19, (double)var20, (double)var21, var6.nextFloat() * 360.0F, 0.0F);
                     var0.func_72838_d(var22);
                     var9 = var22.func_110161_a(var9);
                     var16 = true;
                  }

                  var11 += var6.nextInt(5) - var6.nextInt(5);

                  for(var12 += var6.nextInt(5) - var6.nextInt(5);
                     var11 < var2 || var11 >= var2 + var4 || var12 < var3 || var12 >= var3 + var4;
                     var12 = var14 + var6.nextInt(5) - var6.nextInt(5)
                  ) {
                     var11 = var13 + var6.nextInt(5) - var6.nextInt(5);
                  }
               }
            }
         }
      }
   }
}
