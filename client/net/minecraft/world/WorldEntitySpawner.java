package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathType;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldEntitySpawner {
   private static final Logger field_209383_a = LogManager.getLogger();
   private static final int field_180268_a = (int)Math.pow(17.0D, 2.0D);
   private final Set<ChunkPos> field_77193_b = Sets.newHashSet();

   public WorldEntitySpawner() {
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
                  int var42 = 0;
                  BlockPos var43 = var1.func_175694_M();
                  EnumCreatureType[] var44 = EnumCreatureType.values();
                  var9 = var44.length;

                  label161:
                  for(int var45 = 0; var45 < var9; ++var45) {
                     EnumCreatureType var46 = var44[var45];
                     if ((!var46.func_75599_d() || var3) && (var46.func_75599_d() || var2) && (!var46.func_82705_e() || var4)) {
                        var12 = var46.func_75601_b() * var5 / field_180268_a;
                        int var47 = var1.func_72907_a(var46.func_75598_a(), var12);
                        if (var47 <= var12) {
                           BlockPos.MutableBlockPos var48 = new BlockPos.MutableBlockPos();
                           Iterator var49 = this.field_77193_b.iterator();

                           label158:
                           while(true) {
                              int var18;
                              int var19;
                              int var20;
                              IBlockState var21;
                              do {
                                 if (!var49.hasNext()) {
                                    continue label161;
                                 }

                                 ChunkPos var16 = (ChunkPos)var49.next();
                                 BlockPos var17 = func_180621_a(var1, var16.field_77276_a, var16.field_77275_b);
                                 var18 = var17.func_177958_n();
                                 var19 = var17.func_177956_o();
                                 var20 = var17.func_177952_p();
                                 var21 = var1.func_180495_p(var17);
                              } while(var21.func_185915_l());

                              int var22 = 0;

                              for(int var23 = 0; var23 < 3; ++var23) {
                                 int var24 = var18;
                                 int var25 = var19;
                                 int var26 = var20;
                                 boolean var27 = true;
                                 Biome.SpawnListEntry var28 = null;
                                 IEntityLivingData var29 = null;
                                 int var30 = MathHelper.func_76143_f(Math.random() * 4.0D);
                                 int var31 = 0;

                                 for(int var32 = 0; var32 < var30; ++var32) {
                                    var24 += var1.field_73012_v.nextInt(6) - var1.field_73012_v.nextInt(6);
                                    var25 += var1.field_73012_v.nextInt(1) - var1.field_73012_v.nextInt(1);
                                    var26 += var1.field_73012_v.nextInt(6) - var1.field_73012_v.nextInt(6);
                                    var48.func_181079_c(var24, var25, var26);
                                    float var33 = (float)var24 + 0.5F;
                                    float var34 = (float)var26 + 0.5F;
                                    EntityPlayer var35 = var1.func_212817_a((double)var33, (double)var34, -1.0D);
                                    if (var35 != null) {
                                       double var36 = var35.func_70092_e((double)var33, (double)var25, (double)var34);
                                       if (var36 > 576.0D && var43.func_177954_c((double)var33, (double)var25, (double)var34) >= 576.0D) {
                                          if (var28 == null) {
                                             var28 = var1.func_175734_a(var46, var48);
                                             if (var28 == null) {
                                                break;
                                             }

                                             var30 = var28.field_76301_c + var1.field_73012_v.nextInt(1 + var28.field_76299_d - var28.field_76301_c);
                                          }

                                          if (var1.func_175732_a(var46, var28, var48)) {
                                             EntitySpawnPlacementRegistry.SpawnPlacementType var38 = EntitySpawnPlacementRegistry.func_209344_a(var28.field_200702_b);
                                             if (var38 != null && func_209382_a(var38, var1, var48, var28.field_200702_b)) {
                                                EntityLiving var39;
                                                try {
                                                   var39 = (EntityLiving)var28.field_200702_b.func_200721_a(var1);
                                                } catch (Exception var41) {
                                                   field_209383_a.warn("Failed to create mob", var41);
                                                   return var42;
                                                }

                                                var39.func_70012_b((double)var33, (double)var25, (double)var34, var1.field_73012_v.nextFloat() * 360.0F, 0.0F);
                                                if ((var36 <= 16384.0D || !var39.func_70692_ba()) && var39.func_205020_a(var1, false) && var39.func_205019_a(var1)) {
                                                   var29 = var39.func_204210_a(var1.func_175649_E(new BlockPos(var39)), var29, (NBTTagCompound)null);
                                                   if (var39.func_205019_a(var1)) {
                                                      ++var22;
                                                      ++var31;
                                                      var1.func_72838_d(var39);
                                                   } else {
                                                      var39.func_70106_y();
                                                   }

                                                   if (var22 >= var39.func_70641_bl()) {
                                                      continue label158;
                                                   }

                                                   if (var39.func_204209_c(var31)) {
                                                      break;
                                                   }
                                                }

                                                var42 += var22;
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

                  return var42;
               }

               var7 = (EntityPlayer)var6.next();
            } while(var7.func_175149_v());

            int var8 = MathHelper.func_76128_c(var7.field_70165_t / 16.0D);
            var9 = MathHelper.func_76128_c(var7.field_70161_v / 16.0D);
            boolean var10 = true;

            for(int var11 = -8; var11 <= 8; ++var11) {
               for(var12 = -8; var12 <= 8; ++var12) {
                  boolean var13 = var11 == -8 || var11 == 8 || var12 == -8 || var12 == 8;
                  ChunkPos var14 = new ChunkPos(var11 + var8, var12 + var9);
                  if (!this.field_77193_b.contains(var14)) {
                     ++var5;
                     if (!var13 && var1.func_175723_af().func_177730_a(var14)) {
                        PlayerChunkMapEntry var15 = var1.func_184164_w().func_187301_b(var14.field_77276_a, var14.field_77275_b);
                        if (var15 != null && var15.func_187274_e()) {
                           this.field_77193_b.add(var14);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static BlockPos func_180621_a(World var0, int var1, int var2) {
      Chunk var3 = var0.func_72964_e(var1, var2);
      int var4 = var1 * 16 + var0.field_73012_v.nextInt(16);
      int var5 = var2 * 16 + var0.field_73012_v.nextInt(16);
      int var6 = var3.func_201576_a(Heightmap.Type.LIGHT_BLOCKING, var4, var5) + 1;
      int var7 = var0.field_73012_v.nextInt(var6 + 1);
      return new BlockPos(var4, var7, var5);
   }

   public static boolean func_206851_a(IBlockState var0, IFluidState var1) {
      if (var0.func_185898_k()) {
         return false;
      } else if (var0.func_185897_m()) {
         return false;
      } else if (!var1.func_206888_e()) {
         return false;
      } else {
         return !var0.func_203425_a(BlockTags.field_203437_y);
      }
   }

   public static boolean func_209382_a(EntitySpawnPlacementRegistry.SpawnPlacementType var0, IWorldReaderBase var1, BlockPos var2, @Nullable EntityType<? extends EntityLiving> var3) {
      if (var3 != null && var1.func_175723_af().func_177746_a(var2)) {
         IBlockState var4 = var1.func_180495_p(var2);
         IFluidState var5 = var1.func_204610_c(var2);
         switch(var0) {
         case IN_WATER:
            return var5.func_206884_a(FluidTags.field_206959_a) && var1.func_204610_c(var2.func_177977_b()).func_206884_a(FluidTags.field_206959_a) && !var1.func_180495_p(var2.func_177984_a()).func_185915_l();
         case ON_GROUND:
         default:
            IBlockState var6 = var1.func_180495_p(var2.func_177977_b());
            if (var6.func_185896_q() || var3 != null && EntitySpawnPlacementRegistry.func_209345_a(var3, var6)) {
               Block var7 = var6.func_177230_c();
               boolean var8 = var7 != Blocks.field_150357_h && var7 != Blocks.field_180401_cv;
               return var8 && func_206851_a(var4, var5) && func_206851_a(var1.func_180495_p(var2.func_177984_a()), var1.func_204610_c(var2.func_177984_a()));
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static void func_77191_a(IWorld var0, Biome var1, int var2, int var3, Random var4) {
      List var5 = var1.func_76747_a(EnumCreatureType.CREATURE);
      if (!var5.isEmpty()) {
         int var6 = var2 << 4;
         int var7 = var3 << 4;

         while(var4.nextFloat() < var1.func_76741_f()) {
            Biome.SpawnListEntry var8 = (Biome.SpawnListEntry)WeightedRandom.func_76271_a(var4, var5);
            int var9 = var8.field_76301_c + var4.nextInt(1 + var8.field_76299_d - var8.field_76301_c);
            IEntityLivingData var10 = null;
            int var11 = var6 + var4.nextInt(16);
            int var12 = var7 + var4.nextInt(16);
            int var13 = var11;
            int var14 = var12;

            for(int var15 = 0; var15 < var9; ++var15) {
               boolean var16 = false;

               for(int var17 = 0; !var16 && var17 < 4; ++var17) {
                  BlockPos var18 = func_208498_a(var0, var8.field_200702_b, var11, var12);
                  if (func_209382_a(EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, var0, var18, var8.field_200702_b)) {
                     EntityLiving var19;
                     try {
                        var19 = (EntityLiving)var8.field_200702_b.func_200721_a(var0.func_201672_e());
                     } catch (Exception var24) {
                        field_209383_a.warn("Failed to create mob", var24);
                        continue;
                     }

                     double var20 = MathHelper.func_151237_a((double)var11, (double)var6 + (double)var19.field_70130_N, (double)var6 + 16.0D - (double)var19.field_70130_N);
                     double var22 = MathHelper.func_151237_a((double)var12, (double)var7 + (double)var19.field_70130_N, (double)var7 + 16.0D - (double)var19.field_70130_N);
                     var19.func_70012_b(var20, (double)var18.func_177956_o(), var22, var4.nextFloat() * 360.0F, 0.0F);
                     if (var19.func_205020_a(var0, false) && var19.func_205019_a(var0)) {
                        var10 = var19.func_204210_a(var0.func_175649_E(new BlockPos(var19)), var10, (NBTTagCompound)null);
                        var0.func_72838_d(var19);
                        var16 = true;
                     }
                  }

                  var11 += var4.nextInt(5) - var4.nextInt(5);

                  for(var12 += var4.nextInt(5) - var4.nextInt(5); var11 < var6 || var11 >= var6 + 16 || var12 < var7 || var12 >= var7 + 16; var12 = var14 + var4.nextInt(5) - var4.nextInt(5)) {
                     var11 = var13 + var4.nextInt(5) - var4.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos func_208498_a(IWorld var0, @Nullable EntityType<? extends EntityLiving> var1, int var2, int var3) {
      BlockPos var4 = new BlockPos(var2, var0.func_201676_a(EntitySpawnPlacementRegistry.func_209342_b(var1), var2, var3), var3);
      BlockPos var5 = var4.func_177977_b();
      return var0.func_180495_p(var5).func_196957_g(var0, var5, PathType.LAND) ? var5 : var4;
   }
}
