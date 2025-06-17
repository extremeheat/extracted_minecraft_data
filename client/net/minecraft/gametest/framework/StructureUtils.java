package net.minecraft.gametest.framework;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class StructureUtils {
   public static final int DEFAULT_Y_SEARCH_RADIUS = 10;
   public static final String DEFAULT_TEST_STRUCTURES_DIR = "Minecraft.Server/src/test/convertables/data";
   public static Path testStructuresDir = Paths.get("Minecraft.Server/src/test/convertables/data");

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

   public static TestInstanceBlockEntity createNewEmptyTest(ResourceLocation var0, BlockPos var1, Vec3i var2, Rotation var3, ServerLevel var4) {
      BoundingBox var5 = getStructureBoundingBox(TestInstanceBlockEntity.getStructurePos(var1), var2, var3);
      clearSpaceForStructure(var5, var4);
      var4.setBlockAndUpdate(var1, Blocks.TEST_INSTANCE_BLOCK.defaultBlockState());
      TestInstanceBlockEntity var6 = (TestInstanceBlockEntity)var4.getBlockEntity(var1);
      ResourceKey var7 = ResourceKey.create(Registries.TEST_INSTANCE, var0);
      var6.set(new TestInstanceBlockEntity.Data(Optional.of(var7), var2, var3, false, TestInstanceBlockEntity.Status.CLEARED, Optional.empty()));
      return var6;
   }

   public static void clearSpaceForStructure(BoundingBox var0, ServerLevel var1) {
      int var2 = var0.minY() - 1;
      BoundingBox var3 = new BoundingBox(var0.minX() - 2, var0.minY() - 3, var0.minZ() - 3, var0.maxX() + 3, var0.maxY() + 20, var0.maxZ() + 3);
      BlockPos.betweenClosedStream(var3).forEach((var2x) -> clearBlock(var2, var2x, var1));
      var1.getBlockTicks().clearArea(var3);
      var1.clearBlockEvents(var3);
      AABB var4 = AABB.of(var3);
      List var5 = var1.getEntitiesOfClass(Entity.class, var4, (var0x) -> !(var0x instanceof Player));
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

   public static Optional<BlockPos> findTestContainingPos(BlockPos var0, int var1, ServerLevel var2) {
      return findTestBlocks(var0, var1, var2).filter((var2x) -> doesStructureContain(var2x, var0, var2)).findFirst();
   }

   public static Optional<BlockPos> findNearestTest(BlockPos var0, int var1, ServerLevel var2) {
      Comparator var3 = Comparator.comparingInt((var1x) -> var1x.distManhattan(var0));
      return findTestBlocks(var0, var1, var2).min(var3);
   }

   public static Stream<BlockPos> findTestBlocks(BlockPos var0, int var1, ServerLevel var2) {
      return var2.getPoiManager().findAll((var0x) -> var0x.is(PoiTypes.TEST_INSTANCE), (var0x) -> true, var0, var1, PoiManager.Occupancy.ANY).map(BlockPos::immutable);
   }

   public static Stream<BlockPos> lookedAtTestPos(BlockPos var0, Entity var1, ServerLevel var2) {
      boolean var3 = true;
      Vec3 var4 = var1.getEyePosition();
      Vec3 var5 = var4.add(var1.getLookAngle().scale(200.0));
      Stream var10000 = findTestBlocks(var0, 200, var2).map((var1x) -> var2.getBlockEntity(var1x, BlockEntityType.TEST_INSTANCE_BLOCK)).flatMap(Optional::stream).filter((var2x) -> var2x.getStructureBounds().clip(var4, var5).isPresent()).map(BlockEntity::getBlockPos);
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
      var4.place(var2, var1, 818);
      var2.updateNeighborsAt(var1, var3.getBlock());
   }

   private static boolean doesStructureContain(BlockPos var0, BlockPos var1, ServerLevel var2) {
      BlockEntity var4 = var2.getBlockEntity(var0);
      if (var4 instanceof TestInstanceBlockEntity var3) {
         return var3.getStructureBoundingBox().isInside(var1);
      } else {
         return false;
      }
   }
}
