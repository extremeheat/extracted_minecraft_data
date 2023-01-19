package net.minecraft.world.level.levelgen.feature;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.slf4j.Logger;

public class MonsterRoomFeature extends Feature<NoneFeatureConfiguration> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityType<?>[] MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

   public MonsterRoomFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      Predicate var2 = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
      BlockPos var3 = var1.origin();
      RandomSource var4 = var1.random();
      WorldGenLevel var5 = var1.level();
      boolean var6 = true;
      int var7 = var4.nextInt(2) + 2;
      int var8 = -var7 - 1;
      int var9 = var7 + 1;
      boolean var10 = true;
      boolean var11 = true;
      int var12 = var4.nextInt(2) + 2;
      int var13 = -var12 - 1;
      int var14 = var12 + 1;
      int var15 = 0;

      for(int var16 = var8; var16 <= var9; ++var16) {
         for(int var17 = -1; var17 <= 4; ++var17) {
            for(int var18 = var13; var18 <= var14; ++var18) {
               BlockPos var19 = var3.offset(var16, var17, var18);
               Material var20 = var5.getBlockState(var19).getMaterial();
               boolean var21 = var20.isSolid();
               if (var17 == -1 && !var21) {
                  return false;
               }

               if (var17 == 4 && !var21) {
                  return false;
               }

               if ((var16 == var8 || var16 == var9 || var18 == var13 || var18 == var14)
                  && var17 == 0
                  && var5.isEmptyBlock(var19)
                  && var5.isEmptyBlock(var19.above())) {
                  ++var15;
               }
            }
         }
      }

      if (var15 >= 1 && var15 <= 5) {
         for(int var25 = var8; var25 <= var9; ++var25) {
            for(int var28 = 3; var28 >= -1; --var28) {
               for(int var30 = var13; var30 <= var14; ++var30) {
                  BlockPos var32 = var3.offset(var25, var28, var30);
                  BlockState var34 = var5.getBlockState(var32);
                  if (var25 == var8 || var28 == -1 || var30 == var13 || var25 == var9 || var28 == 4 || var30 == var14) {
                     if (var32.getY() >= var5.getMinBuildHeight() && !var5.getBlockState(var32.below()).getMaterial().isSolid()) {
                        var5.setBlock(var32, AIR, 2);
                     } else if (var34.getMaterial().isSolid() && !var34.is(Blocks.CHEST)) {
                        if (var28 == -1 && var4.nextInt(4) != 0) {
                           this.safeSetBlock(var5, var32, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), var2);
                        } else {
                           this.safeSetBlock(var5, var32, Blocks.COBBLESTONE.defaultBlockState(), var2);
                        }
                     }
                  } else if (!var34.is(Blocks.CHEST) && !var34.is(Blocks.SPAWNER)) {
                     this.safeSetBlock(var5, var32, AIR, var2);
                  }
               }
            }
         }

         for(int var26 = 0; var26 < 2; ++var26) {
            for(int var29 = 0; var29 < 3; ++var29) {
               int var31 = var3.getX() + var4.nextInt(var7 * 2 + 1) - var7;
               int var33 = var3.getY();
               int var35 = var3.getZ() + var4.nextInt(var12 * 2 + 1) - var12;
               BlockPos var36 = new BlockPos(var31, var33, var35);
               if (var5.isEmptyBlock(var36)) {
                  int var22 = 0;

                  for(Direction var24 : Direction.Plane.HORIZONTAL) {
                     if (var5.getBlockState(var36.relative(var24)).getMaterial().isSolid()) {
                        ++var22;
                     }
                  }

                  if (var22 == 1) {
                     this.safeSetBlock(var5, var36, StructurePiece.reorient(var5, var36, Blocks.CHEST.defaultBlockState()), var2);
                     RandomizableContainerBlockEntity.setLootTable(var5, var4, var36, BuiltInLootTables.SIMPLE_DUNGEON);
                     break;
                  }
               }
            }
         }

         this.safeSetBlock(var5, var3, Blocks.SPAWNER.defaultBlockState(), var2);
         BlockEntity var27 = var5.getBlockEntity(var3);
         if (var27 instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)var27).getSpawner().setEntityId(this.randomEntityId(var4));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", new Object[]{var3.getX(), var3.getY(), var3.getZ()});
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType<?> randomEntityId(RandomSource var1) {
      return Util.getRandom(MOBS, var1);
   }
}