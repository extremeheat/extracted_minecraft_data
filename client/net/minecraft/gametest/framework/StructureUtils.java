package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class StructureUtils {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
   public static String testStructuresDir = "gameteststructures";

   public StructureUtils() {
      super();
   }

   public static Rotation getRotationForRotationSteps(int var0) {
      switch (var0) {
         case 0 -> {
            return Rotation.NONE;
         }
         case 1 -> {
            return Rotation.CLOCKWISE_90;
         }
         case 2 -> {
            return Rotation.CLOCKWISE_180;
         }
         case 3 -> {
            return Rotation.COUNTERCLOCKWISE_90;
         }
         default -> throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + var0);
      }
   }

   public static int getRotationStepsForRotation(Rotation var0) {
      switch (var0) {
         case NONE -> {
            return 0;
         }
         case CLOCKWISE_90 -> {
            return 1;
         }
         case CLOCKWISE_180 -> {
            return 2;
         }
         case COUNTERCLOCKWISE_90 -> {
            return 3;
         }
         default -> throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + String.valueOf(var0));
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
      var5.getCommandBlock().setCommand("test runclosest");
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
      Vec3i var4 = ((StructureTemplate)var3.getStructureManager().get(new ResourceLocation(var0.getStructureName())).orElseThrow(() -> {
         return new IllegalStateException("Missing test structure: " + var0.getStructureName());
      })).getSize();
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
            throw new IllegalArgumentException("Invalid rotation: " + String.valueOf(var2));
         }

         var6 = var1.offset(0, 0, var4.getX() - 1);
      }

      forceLoadChunks(var5, var3);
      clearSpaceForStructure(var5, var3);
      return createStructureBlock(var0, var6.below(), var2, var3);
   }

   public static void encaseStructure(AABB var0, ServerLevel var1, boolean var2) {
      BlockPos var3 = BlockPos.containing(var0.minX, var0.minY, var0.minZ).offset(-1, 1, -1);
      BlockPos var4 = BlockPos.containing(var0.maxX, var0.maxY, var0.maxZ);
      BlockPos.betweenClosedStream(var3, var4).forEach((var4x) -> {
         boolean var5 = var4x.getX() == var3.getX() || var4x.getX() == var4.getX() || var4x.getZ() == var3.getZ() || var4x.getZ() == var4.getZ();
         boolean var6 = var4x.getY() == var4.getY();
         if (var5 || var6 && var2) {
            var1.setBlockAndUpdate(var4x, Blocks.BARRIER.defaultBlockState());
         }

      });
   }

   public static void removeBarriers(AABB var0, ServerLevel var1) {
      BlockPos var2 = BlockPos.containing(var0.minX, var0.minY, var0.minZ).offset(-1, 1, -1);
      BlockPos var3 = BlockPos.containing(var0.maxX, var0.maxY, var0.maxZ);
      BlockPos.betweenClosedStream(var2, var3).forEach((var3x) -> {
         boolean var4 = var3x.getX() == var2.getX() || var3x.getX() == var3.getX() || var3x.getZ() == var2.getZ() || var3x.getZ() == var3.getZ();
         boolean var5 = var3x.getY() == var3.getY();
         if (var1.getBlockState(var3x).is(Blocks.BARRIER) && (var4 || var5)) {
            var1.setBlockAndUpdate(var3x, Blocks.AIR.defaultBlockState());
         }

      });
   }

   private static void forceLoadChunks(BoundingBox var0, ServerLevel var1) {
      var0.intersectingChunks().forEach((var1x) -> {
         var1.setChunkForced(var1x.x, var1x.z, true);
      });
   }

   public static void clearSpaceForStructure(BoundingBox var0, ServerLevel var1) {
      int var2 = var0.minY() - 1;
      BoundingBox var3 = new BoundingBox(var0.minX() - 2, var0.minY() - 3, var0.minZ() - 3, var0.maxX() + 3, var0.maxY() + 20, var0.maxZ() + 3);
      BlockPos.betweenClosedStream(var3).forEach((var2x) -> {
         clearBlock(var2, var2x, var1);
      });
      var1.getBlockTicks().clearArea(var3);
      var1.clearBlockEvents(var3);
      AABB var4 = new AABB((double)var3.minX(), (double)var3.minY(), (double)var3.minZ(), (double)var3.maxX(), (double)var3.maxY(), (double)var3.maxZ());
      List var5 = var1.getEntitiesOfClass(Entity.class, var4, (var0x) -> {
         return !(var0x instanceof Player);
      });
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
      return findStructureBlocks(var0, var1, var2).filter((var2x) -> {
         return doesStructureContain(var2x, var0, var2);
      }).findFirst();
   }

   public static Optional<BlockPos> findNearestStructureBlock(BlockPos var0, int var1, ServerLevel var2) {
      Comparator var3 = Comparator.comparingInt((var1x) -> {
         return var1x.distManhattan(var0);
      });
      return findStructureBlocks(var0, var1, var2).min(var3);
   }

   public static Stream<BlockPos> findStructureBlocks(BlockPos var0, int var1, ServerLevel var2) {
      BoundingBox var3 = (new BoundingBox(var0)).inflatedBy(var1);
      return BlockPos.betweenClosedStream(var3).filter((var1x) -> {
         return var2.getBlockState(var1x).is(Blocks.STRUCTURE_BLOCK);
      }).map(BlockPos::immutable);
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
         String var10002 = var0.getTestName();
         throw new RuntimeException("Failed to load structure info for test: " + var10002 + ". Structure name: " + var0.getStructureName());
      } else {
         return var4;
      }
   }

   public static Stream<BlockPos> radiusStructureBlockPos(int var0, Vec3 var1, ServerLevel var2) {
      BlockPos var3 = BlockPos.containing(var1.x, (double)var2.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, BlockPos.containing(var1)).getY(), var1.z);
      BlockPos var4 = var3.offset(-var0, 0, -var0);
      BlockPos var5 = var3.offset(var0, 0, var0);
      return BlockPos.betweenClosedStream(var4, var5).filter((var1x) -> {
         return var2.getBlockState(var1x).is(Blocks.STRUCTURE_BLOCK);
      });
   }

   public static Stream<BlockPos> lookedAtStructureBlockPos(BlockPos var0, Entity var1, ServerLevel var2) {
      boolean var3 = true;
      Vec3 var4 = var1.getEyePosition();
      Vec3 var5 = var4.add(var1.getLookAngle().scale(200.0));
      Stream var10000 = findStructureBlocks(var0, 200, var2).map((var1x) -> {
         return var2.getBlockEntity(var1x, BlockEntityType.STRUCTURE_BLOCK);
      }).flatMap(Optional::stream).filter((var2x) -> {
         return getStructureBounds(var2x).clip(var4, var5).isPresent();
      }).map(BlockEntity::getBlockPos);
      Objects.requireNonNull(var0);
      return var10000.sorted(Comparator.comparing(var0::distSqr)).limit(1L);
   }

   private static void clearBlock(int var0, BlockPos var1, ServerLevel var2) {
      BlockState var3;
      if (var1.getY() < var0) {
         var3 = Blocks.STONE.defaultBlockState();
      } else {
         var3 = Blocks.AIR.defaultBlockState();
      }

      BlockInput var4 = new BlockInput(var3, Collections.emptySet(), (CompoundTag)null);
      var4.place(var2, var1, 2);
      var2.blockUpdated(var1, var3.getBlock());
   }

   private static boolean doesStructureContain(BlockPos var0, BlockPos var1, ServerLevel var2) {
      StructureBlockEntity var3 = (StructureBlockEntity)var2.getBlockEntity(var0);
      return getStructureBoundingBox(var3).isInside(var1);
   }
}
