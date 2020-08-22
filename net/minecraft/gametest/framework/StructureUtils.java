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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;

public class StructureUtils {
   public static String testStructuresDir = "gameteststructures";

   public static AABB getStructureBounds(StructureBlockEntity var0) {
      BlockPos var1 = var0.getBlockPos().offset(var0.getStructurePos());
      return new AABB(var1, var1.offset(var0.getStructureSize()));
   }

   public static void addCommandBlockAndButtonToStartTest(BlockPos var0, ServerLevel var1) {
      var1.setBlockAndUpdate(var0, Blocks.COMMAND_BLOCK.defaultBlockState());
      CommandBlockEntity var2 = (CommandBlockEntity)var1.getBlockEntity(var0);
      var2.getCommandBlock().setCommand("test runthis");
      var1.setBlockAndUpdate(var0.offset(0, 0, -1), Blocks.STONE_BUTTON.defaultBlockState());
   }

   public static void createNewEmptyStructureBlock(String var0, BlockPos var1, BlockPos var2, int var3, ServerLevel var4) {
      BoundingBox var5 = createStructureBoundingBox(var1, var2, var3);
      clearSpaceForStructure(var5, var1.getY(), var4);
      var4.setBlockAndUpdate(var1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity var6 = (StructureBlockEntity)var4.getBlockEntity(var1);
      var6.setIgnoreEntities(false);
      var6.setStructureName(new ResourceLocation(var0));
      var6.setStructureSize(var2);
      var6.setMode(StructureMode.SAVE);
      var6.setShowBoundingBox(true);
   }

   public static StructureBlockEntity spawnStructure(String var0, BlockPos var1, int var2, ServerLevel var3, boolean var4) {
      BoundingBox var5 = createStructureBoundingBox(var1, getStructureTemplate(var0, var3).getSize(), var2);
      forceLoadChunks(var1, var3);
      clearSpaceForStructure(var5, var1.getY(), var3);
      StructureBlockEntity var6 = createStructureBlock(var0, var1, var3, var4);
      var3.getBlockTicks().fetchTicksInArea(var5, true, false);
      var3.clearBlockEvents(var5);
      return var6;
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
      BlockPos.betweenClosedStream(var0).forEach((var2x) -> {
         clearBlock(var1, var2x, var2);
      });
      var2.getBlockTicks().fetchTicksInArea(var0, true, false);
      var2.clearBlockEvents(var0);
      AABB var3 = new AABB((double)var0.x0, (double)var0.y0, (double)var0.z0, (double)var0.x1, (double)var0.y1, (double)var0.z1);
      List var4 = var2.getEntitiesOfClass(Entity.class, var3, (var0x) -> {
         return !(var0x instanceof Player);
      });
      var4.forEach(Entity::remove);
   }

   public static BoundingBox createStructureBoundingBox(BlockPos var0, BlockPos var1, int var2) {
      BlockPos var3 = var0.offset(-var2, -3, -var2);
      BlockPos var4 = var0.offset(var1).offset(var2 - 1, 30, var2 - 1);
      return BoundingBox.createProper(var3.getX(), var3.getY(), var3.getZ(), var4.getX(), var4.getY(), var4.getZ());
   }

   public static Optional findStructureBlockContainingPos(BlockPos var0, int var1, ServerLevel var2) {
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

   public static Collection findStructureBlocks(BlockPos var0, int var1, ServerLevel var2) {
      ArrayList var3 = Lists.newArrayList();
      AABB var4 = new AABB(var0);
      var4 = var4.inflate((double)var1);

      for(int var5 = (int)var4.minX; var5 <= (int)var4.maxX; ++var5) {
         for(int var6 = (int)var4.minY; var6 <= (int)var4.maxY; ++var6) {
            for(int var7 = (int)var4.minZ; var7 <= (int)var4.maxZ; ++var7) {
               BlockPos var8 = new BlockPos(var5, var6, var7);
               BlockState var9 = var2.getBlockState(var8);
               if (var9.getBlock() == Blocks.STRUCTURE_BLOCK) {
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

   private static StructureBlockEntity createStructureBlock(String var0, BlockPos var1, ServerLevel var2, boolean var3) {
      var2.setBlockAndUpdate(var1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity var4 = (StructureBlockEntity)var2.getBlockEntity(var1);
      var4.setMode(StructureMode.LOAD);
      var4.setIgnoreEntities(false);
      var4.setStructureName(new ResourceLocation(var0));
      var4.loadStructure(var3);
      if (var4.getStructureSize() != BlockPos.ZERO) {
         return var4;
      } else {
         StructureTemplate var5 = getStructureTemplate(var0, var2);
         var4.loadStructure(var3, var5);
         if (var4.getStructureSize() == BlockPos.ZERO) {
            throw new RuntimeException("Failed to load structure " + var0);
         } else {
            return var4;
         }
      }
   }

   @Nullable
   private static CompoundTag tryLoadStructure(Path var0) {
      try {
         BufferedReader var1 = Files.newBufferedReader(var0);
         String var2 = IOUtils.toString(var1);
         return TagParser.parseTag(var2);
      } catch (IOException var3) {
         return null;
      } catch (CommandSyntaxException var4) {
         throw new RuntimeException("Error while trying to load structure " + var0, var4);
      }
   }

   private static void clearBlock(int var0, BlockPos var1, ServerLevel var2) {
      ChunkGeneratorSettings var4 = var2.getChunkSource().getGenerator().getSettings();
      BlockState var3;
      if (var4 instanceof FlatLevelGeneratorSettings) {
         BlockState[] var5 = ((FlatLevelGeneratorSettings)var4).getLayers();
         if (var1.getY() < var0) {
            var3 = var5[var1.getY() - 1];
         } else {
            var3 = Blocks.AIR.defaultBlockState();
         }
      } else if (var1.getY() == var0 - 1) {
         var3 = var2.getBiome(var1).getSurfaceBuilderConfig().getTopMaterial();
      } else if (var1.getY() < var0 - 1) {
         var3 = var2.getBiome(var1).getSurfaceBuilderConfig().getUnderMaterial();
      } else {
         var3 = Blocks.AIR.defaultBlockState();
      }

      BlockInput var6 = new BlockInput(var3, Collections.emptySet(), (CompoundTag)null);
      var6.place(var2, var1, 2);
      var2.blockUpdated(var1, var3.getBlock());
   }

   private static boolean doesStructureContain(BlockPos var0, BlockPos var1, ServerLevel var2) {
      StructureBlockEntity var3 = (StructureBlockEntity)var2.getBlockEntity(var0);
      AABB var4 = getStructureBounds(var3);
      return var4.contains(new Vec3(var1));
   }
}
