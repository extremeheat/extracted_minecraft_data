package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureUtils {
   private static final Logger LOGGER = LogManager.getLogger();
   public static String testStructuresDir = "gameteststructures";

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

   public static AABB getStructureBounds(StructureBlockEntity var0) {
      BlockPos var1 = var0.getBlockPos();
      BlockPos var2 = var1.offset(var0.getStructureSize().offset(-1, -1, -1));
      BlockPos var3 = StructureTemplate.transform(var2, Mirror.NONE, var0.getRotation(), var1);
      return new AABB(var1, var3);
   }

   public static BoundingBox getStructureBoundingBox(StructureBlockEntity var0) {
      BlockPos var1 = var0.getBlockPos();
      BlockPos var2 = var1.offset(var0.getStructureSize().offset(-1, -1, -1));
      BlockPos var3 = StructureTemplate.transform(var2, Mirror.NONE, var0.getRotation(), var1);
      return new BoundingBox(var1, var3);
   }

   public static void addCommandBlockAndButtonToStartTest(BlockPos var0, BlockPos var1, Rotation var2, ServerLevel var3) {
      BlockPos var4 = StructureTemplate.transform(var0.offset(var1), Mirror.NONE, var2, var0);
      var3.setBlockAndUpdate(var4, Blocks.COMMAND_BLOCK.defaultBlockState());
      CommandBlockEntity var5 = (CommandBlockEntity)var3.getBlockEntity(var4);
      var5.getCommandBlock().setCommand("test runthis");
      BlockPos var6 = StructureTemplate.transform(var4.offset(0, 0, -1), Mirror.NONE, var2, var4);
      var3.setBlockAndUpdate(var6, Blocks.STONE_BUTTON.defaultBlockState().rotate(var2));
   }

   public static void createNewEmptyStructureBlock(String var0, BlockPos var1, BlockPos var2, Rotation var3, ServerLevel var4) {
      BoundingBox var5 = getStructureBoundingBox(var1, var2, var3);
      clearSpaceForStructure(var5, var1.getY(), var4);
      var4.setBlockAndUpdate(var1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity var6 = (StructureBlockEntity)var4.getBlockEntity(var1);
      var6.setIgnoreEntities(false);
      var6.setStructureName(new ResourceLocation(var0));
      var6.setStructureSize(var2);
      var6.setMode(StructureMode.SAVE);
      var6.setShowBoundingBox(true);
   }

   public static StructureBlockEntity spawnStructure(String var0, BlockPos var1, Rotation var2, int var3, ServerLevel var4, boolean var5) {
      BlockPos var6 = getStructureTemplate(var0, var4).getSize();
      BoundingBox var7 = getStructureBoundingBox(var1, var6, var2);
      BlockPos var8;
      if (var2 == Rotation.NONE) {
         var8 = var1;
      } else if (var2 == Rotation.CLOCKWISE_90) {
         var8 = var1.offset(var6.getZ() - 1, 0, 0);
      } else if (var2 == Rotation.CLOCKWISE_180) {
         var8 = var1.offset(var6.getX() - 1, 0, var6.getZ() - 1);
      } else {
         if (var2 != Rotation.COUNTERCLOCKWISE_90) {
            throw new IllegalArgumentException("Invalid rotation: " + var2);
         }

         var8 = var1.offset(0, 0, var6.getX() - 1);
      }

      forceLoadChunks(var1, var4);
      clearSpaceForStructure(var7, var1.getY(), var4);
      StructureBlockEntity var9 = createStructureBlock(var0, var8, var2, var4, var5);
      var4.getBlockTicks().fetchTicksInArea(var7, true, false);
      var4.clearBlockEvents(var7);
      return var9;
   }

   private static void forceLoadChunks(BlockPos var0, ServerLevel var1) {
      ChunkPos var2 = new ChunkPos(var0);

      for(int var3 = -1; var3 < 4; ++var3) {
         for(int var4 = -1; var4 < 4; ++var4) {
            int var5 = var2.x + var3;
            int var6 = var2.z + var4;
            var1.setChunkForced(var5, var6, true);
         }
      }

   }

   public static void clearSpaceForStructure(BoundingBox var0, int var1, ServerLevel var2) {
      BoundingBox var3 = new BoundingBox(var0.x0 - 2, var0.y0 - 3, var0.z0 - 3, var0.x1 + 3, var0.y1 + 20, var0.z1 + 3);
      BlockPos.betweenClosedStream(var3).forEach((var2x) -> {
         clearBlock(var1, var2x, var2);
      });
      var2.getBlockTicks().fetchTicksInArea(var3, true, false);
      var2.clearBlockEvents(var3);
      AABB var4 = new AABB((double)var3.x0, (double)var3.y0, (double)var3.z0, (double)var3.x1, (double)var3.y1, (double)var3.z1);
      List var5 = var2.getEntitiesOfClass(Entity.class, var4, (var0x) -> {
         return !(var0x instanceof Player);
      });
      var5.forEach(Entity::discard);
   }

   public static BoundingBox getStructureBoundingBox(BlockPos var0, BlockPos var1, Rotation var2) {
      BlockPos var3 = var0.offset(var1).offset(-1, -1, -1);
      BlockPos var4 = StructureTemplate.transform(var3, Mirror.NONE, var2, var0);
      BoundingBox var5 = BoundingBox.createProper(var0.getX(), var0.getY(), var0.getZ(), var4.getX(), var4.getY(), var4.getZ());
      int var6 = Math.min(var5.x0, var5.x1);
      int var7 = Math.min(var5.z0, var5.z1);
      BlockPos var8 = new BlockPos(var0.getX() - var6, 0, var0.getZ() - var7);
      var5.move(var8);
      return var5;
   }

   public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos var0, int var1, ServerLevel var2) {
      return findStructureBlocks(var0, var1, var2).stream().filter((var2x) -> {
         return doesStructureContain(var2x, var0, var2);
      }).findFirst();
   }

   @Nullable
   public static BlockPos findNearestStructureBlock(BlockPos var0, int var1, ServerLevel var2) {
      Comparator var3 = Comparator.comparingInt((var1x) -> {
         return var1x.distManhattan(var0);
      });
      Collection var4 = findStructureBlocks(var0, var1, var2);
      Optional var5 = var4.stream().min(var3);
      return (BlockPos)var5.orElse((Object)null);
   }

   public static Collection<BlockPos> findStructureBlocks(BlockPos var0, int var1, ServerLevel var2) {
      ArrayList var3 = Lists.newArrayList();
      AABB var4 = new AABB(var0);
      var4 = var4.inflate((double)var1);

      for(int var5 = (int)var4.minX; var5 <= (int)var4.maxX; ++var5) {
         for(int var6 = (int)var4.minY; var6 <= (int)var4.maxY; ++var6) {
            for(int var7 = (int)var4.minZ; var7 <= (int)var4.maxZ; ++var7) {
               BlockPos var8 = new BlockPos(var5, var6, var7);
               BlockState var9 = var2.getBlockState(var8);
               if (var9.is(Blocks.STRUCTURE_BLOCK)) {
                  var3.add(var8);
               }
            }
         }
      }

      return var3;
   }

   private static StructureTemplate getStructureTemplate(String var0, ServerLevel var1) {
      StructureManager var2 = var1.getStructureManager();
      StructureTemplate var3 = var2.get(new ResourceLocation(var0));
      if (var3 != null) {
         return var3;
      } else {
         String var4 = var0 + ".snbt";
         Path var5 = Paths.get(testStructuresDir, var4);
         CompoundTag var6 = tryLoadStructure(var5);
         if (var6 == null) {
            throw new RuntimeException("Could not find structure file " + var5 + ", and the structure is not available in the world structures either.");
         } else {
            return var2.readStructure(var6);
         }
      }
   }

   private static StructureBlockEntity createStructureBlock(String var0, BlockPos var1, Rotation var2, ServerLevel var3, boolean var4) {
      var3.setBlockAndUpdate(var1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity var5 = (StructureBlockEntity)var3.getBlockEntity(var1);
      var5.setMode(StructureMode.LOAD);
      var5.setRotation(var2);
      var5.setIgnoreEntities(false);
      var5.setStructureName(new ResourceLocation(var0));
      var5.loadStructure(var3, var4);
      if (var5.getStructureSize() != BlockPos.ZERO) {
         return var5;
      } else {
         StructureTemplate var6 = getStructureTemplate(var0, var3);
         var5.loadStructure(var3, var4, var6);
         if (var5.getStructureSize() == BlockPos.ZERO) {
            throw new RuntimeException("Failed to load structure " + var0);
         } else {
            return var5;
         }
      }
   }

   @Nullable
   private static CompoundTag tryLoadStructure(Path var0) {
      try {
         BufferedReader var1 = Files.newBufferedReader(var0);
         String var2 = IOUtils.toString(var1);
         return NbtUtils.snbtToStructure(var2);
      } catch (IOException var3) {
         return null;
      } catch (CommandSyntaxException var4) {
         throw new RuntimeException("Error while trying to load structure " + var0, var4);
      }
   }

   private static void clearBlock(int var0, BlockPos var1, ServerLevel var2) {
      BlockState var3 = null;
      FlatLevelGeneratorSettings var4 = FlatLevelGeneratorSettings.getDefault(var2.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
      if (var4 instanceof FlatLevelGeneratorSettings) {
         BlockState[] var5 = var4.getLayers();
         int var6 = var4.getLayerIndex(var1.getY());
         if (var1.getY() < var0 && var6 > 0 && var6 <= var5.length) {
            var3 = var5[var6 - 1];
         }
      } else if (var1.getY() == var0 - 1) {
         var3 = var2.getBiome(var1).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
      } else if (var1.getY() < var0 - 1) {
         var3 = var2.getBiome(var1).getGenerationSettings().getSurfaceBuilderConfig().getUnderMaterial();
      }

      if (var3 == null) {
         var3 = Blocks.AIR.defaultBlockState();
      }

      BlockInput var7 = new BlockInput(var3, Collections.emptySet(), (CompoundTag)null);
      var7.place(var2, var1, 2);
      var2.blockUpdated(var1, var3.getBlock());
   }

   private static boolean doesStructureContain(BlockPos var0, BlockPos var1, ServerLevel var2) {
      StructureBlockEntity var3 = (StructureBlockEntity)var2.getBlockEntity(var0);
      AABB var4 = getStructureBounds(var3).inflate(1.0D);
      return var4.contains(Vec3.atCenterOf(var1));
   }
}
