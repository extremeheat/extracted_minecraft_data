package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
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

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
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
               var19 = var4.offset(var16, var17, var18);
               Material var20 = var1.getBlockState(var19).getMaterial();
               boolean var21 = var20.isSolid();
               if (var17 == -1 && !var21) {
                  return false;
               }

               if (var17 == 4 && !var21) {
                  return false;
               }

               if ((var16 == var8 || var16 == var9 || var18 == var13 || var18 == var14) && var17 == 0 && var1.isEmptyBlock(var19) && var1.isEmptyBlock(var19.above())) {
                  ++var15;
               }
            }
         }
      }

      if (var15 >= 1 && var15 <= 5) {
         for(var16 = var8; var16 <= var9; ++var16) {
            for(var17 = 3; var17 >= -1; --var17) {
               for(var18 = var13; var18 <= var14; ++var18) {
                  var19 = var4.offset(var16, var17, var18);
                  BlockState var27 = var1.getBlockState(var19);
                  if (var16 != var8 && var17 != -1 && var18 != var13 && var16 != var9 && var17 != 4 && var18 != var14) {
                     if (!var27.is(Blocks.CHEST) && !var27.is(Blocks.SPAWNER)) {
                        var1.setBlock(var19, AIR, 2);
                     }
                  } else if (var19.getY() >= var1.getMinBuildHeight() && !var1.getBlockState(var19.below()).getMaterial().isSolid()) {
                     var1.setBlock(var19, AIR, 2);
                  } else if (var27.getMaterial().isSolid() && !var27.is(Blocks.CHEST)) {
                     if (var17 == -1 && var3.nextInt(4) != 0) {
                        var1.setBlock(var19, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                     } else {
                        var1.setBlock(var19, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

         for(var16 = 0; var16 < 2; ++var16) {
            for(var17 = 0; var17 < 3; ++var17) {
               var18 = var4.getX() + var3.nextInt(var7 * 2 + 1) - var7;
               int var26 = var4.getY();
               int var28 = var4.getZ() + var3.nextInt(var12 * 2 + 1) - var12;
               BlockPos var29 = new BlockPos(var18, var26, var28);
               if (var1.isEmptyBlock(var29)) {
                  int var22 = 0;
                  Iterator var23 = Direction.Plane.HORIZONTAL.iterator();

                  while(var23.hasNext()) {
                     Direction var24 = (Direction)var23.next();
                     if (var1.getBlockState(var29.relative(var24)).getMaterial().isSolid()) {
                        ++var22;
                     }
                  }

                  if (var22 == 1) {
                     var1.setBlock(var29, StructurePiece.reorient(var1, var29, Blocks.CHEST.defaultBlockState()), 2);
                     RandomizableContainerBlockEntity.setLootTable(var1, var3, var29, BuiltInLootTables.SIMPLE_DUNGEON);
                     break;
                  }
               }
            }
         }

         var1.setBlock(var4, Blocks.SPAWNER.defaultBlockState(), 2);
         BlockEntity var25 = var1.getBlockEntity(var4);
         if (var25 instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)var25).getSpawner().setEntityId(this.randomEntityId(var3));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", var4.getX(), var4.getY(), var4.getZ());
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
