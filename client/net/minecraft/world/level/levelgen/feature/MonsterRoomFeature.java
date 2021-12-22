package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonsterRoomFeature extends Feature<NoneFeatureConfiguration> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityType<?>[] MOBS;
   private static final BlockState AIR;

   public MonsterRoomFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      Predicate var2 = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE.getName());
      BlockPos var3 = var1.origin();
      Random var4 = var1.random();
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

      int var16;
      int var17;
      int var18;
      BlockPos var19;
      for(var16 = var8; var16 <= var9; ++var16) {
         for(var17 = -1; var17 <= 4; ++var17) {
            for(var18 = var13; var18 <= var14; ++var18) {
               var19 = var3.offset(var16, var17, var18);
               Material var20 = var5.getBlockState(var19).getMaterial();
               boolean var21 = var20.isSolid();
               if (var17 == -1 && !var21) {
                  return false;
               }

               if (var17 == 4 && !var21) {
                  return false;
               }

               if ((var16 == var8 || var16 == var9 || var18 == var13 || var18 == var14) && var17 == 0 && var5.isEmptyBlock(var19) && var5.isEmptyBlock(var19.above())) {
                  ++var15;
               }
            }
         }
      }

      if (var15 >= 1 && var15 <= 5) {
         for(var16 = var8; var16 <= var9; ++var16) {
            for(var17 = 3; var17 >= -1; --var17) {
               for(var18 = var13; var18 <= var14; ++var18) {
                  var19 = var3.offset(var16, var17, var18);
                  BlockState var27 = var5.getBlockState(var19);
                  if (var16 != var8 && var17 != -1 && var18 != var13 && var16 != var9 && var17 != 4 && var18 != var14) {
                     if (!var27.is(Blocks.CHEST) && !var27.is(Blocks.SPAWNER)) {
                        this.safeSetBlock(var5, var19, AIR, var2);
                     }
                  } else if (var19.getY() >= var5.getMinBuildHeight() && !var5.getBlockState(var19.below()).getMaterial().isSolid()) {
                     var5.setBlock(var19, AIR, 2);
                  } else if (var27.getMaterial().isSolid() && !var27.is(Blocks.CHEST)) {
                     if (var17 == -1 && var4.nextInt(4) != 0) {
                        this.safeSetBlock(var5, var19, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), var2);
                     } else {
                        this.safeSetBlock(var5, var19, Blocks.COBBLESTONE.defaultBlockState(), var2);
                     }
                  }
               }
            }
         }

         for(var16 = 0; var16 < 2; ++var16) {
            for(var17 = 0; var17 < 3; ++var17) {
               var18 = var3.getX() + var4.nextInt(var7 * 2 + 1) - var7;
               int var26 = var3.getY();
               int var28 = var3.getZ() + var4.nextInt(var12 * 2 + 1) - var12;
               BlockPos var29 = new BlockPos(var18, var26, var28);
               if (var5.isEmptyBlock(var29)) {
                  int var22 = 0;
                  Iterator var23 = Direction.Plane.HORIZONTAL.iterator();

                  while(var23.hasNext()) {
                     Direction var24 = (Direction)var23.next();
                     if (var5.getBlockState(var29.relative(var24)).getMaterial().isSolid()) {
                        ++var22;
                     }
                  }

                  if (var22 == 1) {
                     this.safeSetBlock(var5, var29, StructurePiece.reorient(var5, var29, Blocks.CHEST.defaultBlockState()), var2);
                     RandomizableContainerBlockEntity.setLootTable(var5, var4, var29, BuiltInLootTables.SIMPLE_DUNGEON);
                     break;
                  }
               }
            }
         }

         this.safeSetBlock(var5, var3, Blocks.SPAWNER.defaultBlockState(), var2);
         BlockEntity var25 = var5.getBlockEntity(var3);
         if (var25 instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)var25).getSpawner().setEntityId(this.randomEntityId(var4));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", var3.getX(), var3.getY(), var3.getZ());
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType<?> randomEntityId(Random var1) {
      return (EntityType)Util.getRandom((Object[])MOBS, var1);
   }

   static {
      MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
      AIR = Blocks.CAVE_AIR.defaultBlockState();
   }
}
