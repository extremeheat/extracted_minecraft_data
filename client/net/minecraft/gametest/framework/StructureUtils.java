package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class StructureUtils {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
   public static String testStructuresDir = "gameteststructures";

   public StructureUtils() {
      super();
   }

   public static Rotation getRotationForRotationSteps(int var0) {
      switch(var0) {
         case 0:
            return Rotation.NONE;
         case 1:
            return Rotation.CLOCKWISE_90;
         case 2:
            return Rotation.CLOCKWISE_180;
         case 3:
            return Rotation.COUNTERCLOCKWISE_90;
         default:
            throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + var0);
      }
   }

   public static int getRotationStepsForRotation(Rotation var0) {
      switch(var0) {
         case NONE:
            return 0;
         case CLOCKWISE_90:
            return 1;
         case CLOCKWISE_180:
            return 2;
         case COUNTERCLOCKWISE_90:
            return 3;
         default:
            throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + var0);
      }
   }

   public static AABB getStructureBounds(StructureBlockEntity var0) {
      return AABB.of(getStructureBoundingBox(var0));
   }

   public static BoundingBox getStructureBoundingBox(StructureBlockEntity var0) {
      BlockPos var1 = getStructureOrigin(var0);
      BlockPos var2 = getTransformedFarCorner(var1, var0.getStructureSize(), var0.getRotation());
      return BoundingBox.fromCorners(var1, var2);
   }

   public static BlockPos getStructureOrigin(StructureBlockEntity var0) {
      return var0.getBlockPos().offset(var0.getStructurePos());
   }

   public static void addCommandBlockAndButtonToStartTest(BlockPos var0, BlockPos var1, Rotation var2, ServerLevel var3) {
      BlockPos var4 = StructureTemplate.transform(var0.offset(var1), Mirror.NONE, var2, var0);
      var3.setBlockAndUpdate(var4, Blocks.COMMAND_BLOCK.defaultBlockState());
      CommandBlockEntity var5 = (CommandBlockEntity)var3.getBlockEntity(var4);
      var5.getCommandBlock().setCommand("test runthis");
      BlockPos var6 = StructureTemplate.transform(var4.offset(0, 0, -1), Mirror.NONE, var2, var4);
      var3.setBlockAndUpdate(var6, Blocks.STONE_BUTTON.defaultBlockState().rotate(var2));
   }

   public static void createNewEmptyStructureBlock(String var0, BlockPos var1, Vec3i var2, Rotation var3, ServerLevel var4) {
      BoundingBox var5 = getStructureBoundingBox(var1.above(), var2, var3);
      clearSpaceForStructure(var5, var4);
      var4.setBlockAndUpdate(var1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity var6 = (StructureBlockEntity)var4.getBlockEntity(var1);
      var6.setIgnoreEntities(false);
      var6.setStructureName(new ResourceLocation(var0));
      var6.setStructureSize(var2);
      var6.setMode(StructureMode.SAVE);
      var6.setShowBoundingBox(true);
   }

   public static StructureBlockEntity prepareTestStructure(GameTestInfo var0, BlockPos var1, Rotation var2, ServerLevel var3) {
      Vec3i var4 = var3.getStructureManager()
         .get(new ResourceLocation(var0.getStructureName()))
         .orElseThrow(() -> new IllegalStateException("Missing test structure: " + var0.getStructureName()))
         .getSize();
      BoundingBox var5 = getStructureBoundingBox(var1, var4, var2);
      BlockPos var6;
      if (var2 == Rotation.NONE) {
         var6 = var1;
      } else if (var2 == Rotation.CLOCKWISE_90) {
         var6 = var1.offset(var4.getZ() - 1, 0, 0);
      } else if (var2 == Rotation.CLOCKWISE_180) {
         var6 = var1.offset(var4.getX() - 1, 0, var4.getZ() - 1);
      } else {
         if (var2 != Rotation.COUNTERCLOCKWISE_90) {
            throw new IllegalArgumentException("Invalid rotation: " + var2);
         }

         var6 = var1.offset(0, 0, var4.getX() - 1);
      }

      forceLoadChunks(var5, var3);
      clearSpaceForStructure(var5, var3);
      return createStructureBlock(var0, var6.below(), var2, var3);
   }

   private static void forceLoadChunks(BoundingBox var0, ServerLevel var1) {
      var0.intersectingChunks().forEach(var1x -> var1.setChunkForced(var1x.x, var1x.z, true));
   }

   public static void clearSpaceForStructure(BoundingBox var0, ServerLevel var1) {
      int var2 = var0.minY() - 1;
      BoundingBox var3 = new BoundingBox(var0.minX() - 2, var0.minY() - 3, var0.minZ() - 3, var0.maxX() + 3, var0.maxY() + 20, var0.maxZ() + 3);
      BlockPos.betweenClosedStream(var3).forEach(var2x -> clearBlock(var2, var2x, var1));
      var1.getBlockTicks().clearArea(var3);
      var1.clearBlockEvents(var3);
      AABB var4 = new AABB((double)var3.minX(), (double)var3.minY(), (double)var3.minZ(), (double)var3.maxX(), (double)var3.maxY(), (double)var3.maxZ());
      List var5 = var1.getEntitiesOfClass(Entity.class, var4, var0x -> !(var0x instanceof Player));
      var5.forEach(Entity::discard);
   }

   public static BlockPos getTransformedFarCorner(BlockPos var0, Vec3i var1, Rotation var2) {
      BlockPos var3 = var0.offset(var1).offset(-1, -1, -1);
      return StructureTemplate.transform(var3, Mirror.NONE, var2, var0);
   }

   public static BoundingBox getStructureBoundingBox(BlockPos var0, Vec3i var1, Rotation var2) {
      BlockPos var3 = getTransformedFarCorner(var0, var1, var2);
      BoundingBox var4 = BoundingBox.fromCorners(var0, var3);
      int var5 = Math.min(var4.minX(), var4.maxX());
      int var6 = Math.min(var4.minZ(), var4.maxZ());
      return var4.move(var0.getX() - var5, 0, var0.getZ() - var6);
   }

   public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos var0, int var1, ServerLevel var2) {
      return findStructureBlocks(var0, var1, var2).stream().filter(var2x -> doesStructureContain(var2x, var0, var2)).findFirst();
   }

   @Nullable
   public static BlockPos findNearestStructureBlock(BlockPos var0, int var1, ServerLevel var2) {
      Comparator var3 = Comparator.comparingInt(var1x -> var1x.distManhattan(var0));
      Collection var4 = findStructureBlocks(var0, var1, var2);
      Optional var5 = var4.stream().min(var3);
      return (BlockPos)var5.orElse(null);
   }

   public static Collection<BlockPos> findStructureBlocks(BlockPos var0, int var1, ServerLevel var2) {
      ArrayList var3 = Lists.newArrayList();
      BoundingBox var4 = new BoundingBox(var0).inflatedBy(var1);
      BlockPos.betweenClosedStream(var4).forEach(var2x -> {
         if (var2.getBlockState(var2x).is(Blocks.STRUCTURE_BLOCK)) {
            var3.add(var2x.immutable());
         }
      });
      return var3;
   }

   private static StructureBlockEntity createStructureBlock(GameTestInfo var0, BlockPos var1, Rotation var2, ServerLevel var3) {
      var3.setBlockAndUpdate(var1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity var4 = (StructureBlockEntity)var3.getBlockEntity(var1);
      var4.setMode(StructureMode.LOAD);
      var4.setRotation(var2);
      var4.setIgnoreEntities(false);
      var4.setStructureName(new ResourceLocation(var0.getStructureName()));
      var4.setMetaData(var0.getTestName());
      if (!var4.loadStructureInfo(var3)) {
         throw new RuntimeException("Failed to load structure info for test: " + var0.getTestName() + ". Structure name: " + var0.getStructureName());
      } else {
         return var4;
      }
   }

   private static void clearBlock(int var0, BlockPos var1, ServerLevel var2) {
      BlockState var3;
      if (var1.getY() < var0) {
         var3 = Blocks.STONE.defaultBlockState();
      } else {
         var3 = Blocks.AIR.defaultBlockState();
      }

      BlockInput var4 = new BlockInput(var3, Collections.emptySet(), null);
      var4.place(var2, var1, 2);
      var2.blockUpdated(var1, var3.getBlock());
   }

   private static boolean doesStructureContain(BlockPos var0, BlockPos var1, ServerLevel var2) {
      StructureBlockEntity var3 = (StructureBlockEntity)var2.getBlockEntity(var0);
      return getStructureBoundingBox(var3).isInside(var1);
   }
}
