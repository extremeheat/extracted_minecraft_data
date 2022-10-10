package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.storage.loot.LootTableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonsFeature extends Feature<NoFeatureConfig> {
   private static final Logger field_175918_a = LogManager.getLogger();
   private static final EntityType<?>[] field_175916_b;
   private static final IBlockState field_205189_c;

   public DungeonsFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      boolean var6 = true;
      int var7 = var3.nextInt(2) + 2;
      int var8 = -var7 - 1;
      int var9 = var7 + 1;
      boolean var10 = true;
      boolean var11 = true;
      int var12 = var3.nextInt(2) + 2;
      int var13 = -var12 - 1;
      int var14 = var12 + 1;
      int var15 = 0;

      int var16;
      int var17;
      int var18;
      BlockPos var19;
      for(var16 = var8; var16 <= var9; ++var16) {
         for(var17 = -1; var17 <= 4; ++var17) {
            for(var18 = var13; var18 <= var14; ++var18) {
               var19 = var4.func_177982_a(var16, var17, var18);
               Material var20 = var1.func_180495_p(var19).func_185904_a();
               boolean var21 = var20.func_76220_a();
               if (var17 == -1 && !var21) {
                  return false;
               }

               if (var17 == 4 && !var21) {
                  return false;
               }

               if ((var16 == var8 || var16 == var9 || var18 == var13 || var18 == var14) && var17 == 0 && var1.func_175623_d(var19) && var1.func_175623_d(var19.func_177984_a())) {
                  ++var15;
               }
            }
         }
      }

      if (var15 >= 1 && var15 <= 5) {
         for(var16 = var8; var16 <= var9; ++var16) {
            for(var17 = 3; var17 >= -1; --var17) {
               for(var18 = var13; var18 <= var14; ++var18) {
                  var19 = var4.func_177982_a(var16, var17, var18);
                  if (var16 != var8 && var17 != -1 && var18 != var13 && var16 != var9 && var17 != 4 && var18 != var14) {
                     if (var1.func_180495_p(var19).func_177230_c() != Blocks.field_150486_ae) {
                        var1.func_180501_a(var19, field_205189_c, 2);
                     }
                  } else if (var19.func_177956_o() >= 0 && !var1.func_180495_p(var19.func_177977_b()).func_185904_a().func_76220_a()) {
                     var1.func_180501_a(var19, field_205189_c, 2);
                  } else if (var1.func_180495_p(var19).func_185904_a().func_76220_a() && var1.func_180495_p(var19).func_177230_c() != Blocks.field_150486_ae) {
                     if (var17 == -1 && var3.nextInt(4) != 0) {
                        var1.func_180501_a(var19, Blocks.field_150341_Y.func_176223_P(), 2);
                     } else {
                        var1.func_180501_a(var19, Blocks.field_150347_e.func_176223_P(), 2);
                     }
                  }
               }
            }
         }

         for(var16 = 0; var16 < 2; ++var16) {
            for(var17 = 0; var17 < 3; ++var17) {
               var18 = var4.func_177958_n() + var3.nextInt(var7 * 2 + 1) - var7;
               int var26 = var4.func_177956_o();
               int var27 = var4.func_177952_p() + var3.nextInt(var12 * 2 + 1) - var12;
               BlockPos var28 = new BlockPos(var18, var26, var27);
               if (var1.func_175623_d(var28)) {
                  int var22 = 0;
                  Iterator var23 = EnumFacing.Plane.HORIZONTAL.iterator();

                  while(var23.hasNext()) {
                     EnumFacing var24 = (EnumFacing)var23.next();
                     if (var1.func_180495_p(var28.func_177972_a(var24)).func_185904_a().func_76220_a()) {
                        ++var22;
                     }
                  }

                  if (var22 == 1) {
                     var1.func_180501_a(var28, StructurePiece.func_197528_a(var1, var28, Blocks.field_150486_ae.func_176223_P()), 2);
                     TileEntityLockableLoot.func_195479_a(var1, var3, var28, LootTableList.field_186422_d);
                     break;
                  }
               }
            }
         }

         var1.func_180501_a(var4, Blocks.field_150474_ac.func_176223_P(), 2);
         TileEntity var25 = var1.func_175625_s(var4);
         if (var25 instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)var25).func_145881_a().func_200876_a(this.func_201043_a(var3));
         } else {
            field_175918_a.error("Failed to fetch mob spawner entity at ({}, {}, {})", var4.func_177958_n(), var4.func_177956_o(), var4.func_177952_p());
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType<?> func_201043_a(Random var1) {
      return field_175916_b[var1.nextInt(field_175916_b.length)];
   }

   static {
      field_175916_b = new EntityType[]{EntityType.field_200741_ag, EntityType.field_200725_aD, EntityType.field_200725_aD, EntityType.field_200748_an};
      field_205189_c = Blocks.field_201941_jj.func_176223_P();
   }
}
