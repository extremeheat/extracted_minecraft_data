package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ResetChunksCommand {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ResetChunksCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("resetchunks").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         return resetChunks((CommandSourceStack)var0x.getSource(), 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("range", IntegerArgumentType.integer(0, 5)).executes((var0x) -> {
         return resetChunks((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "range"), true);
      })).then(Commands.argument("skipOldChunks", BoolArgumentType.bool()).executes((var0x) -> {
         return resetChunks((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "range"), BoolArgumentType.getBool(var0x, "skipOldChunks"));
      }))));
   }

   private static int resetChunks(CommandSourceStack var0, int var1, boolean var2) {
      ServerLevel var3 = var0.getLevel();
      ServerChunkCache var4 = var3.getChunkSource();
      var4.chunkMap.debugReloadGenerator();
      Vec3 var5 = var0.getPosition();
      ChunkPos var6 = new ChunkPos(BlockPos.containing(var5));
      int var7 = var6.z - var1;
      int var8 = var6.z + var1;
      int var9 = var6.x - var1;
      int var10 = var6.x + var1;

      for(int var11 = var7; var11 <= var8; ++var11) {
         for(int var12 = var9; var12 <= var10; ++var12) {
            ChunkPos var13 = new ChunkPos(var12, var11);
            LevelChunk var14 = var4.getChunk(var12, var11, false);
            if (var14 != null && (!var2 || !var14.isOldNoiseGeneration())) {
               Iterator var15 = BlockPos.betweenClosed(var13.getMinBlockX(), var3.getMinBuildHeight(), var13.getMinBlockZ(), var13.getMaxBlockX(), var3.getMaxBuildHeight() - 1, var13.getMaxBlockZ()).iterator();

               while(var15.hasNext()) {
                  BlockPos var16 = (BlockPos)var15.next();
                  var3.setBlock(var16, Blocks.AIR.defaultBlockState(), 16);
               }
            }
         }
      }

      ProcessorMailbox var31 = ProcessorMailbox.create(Util.backgroundExecutor(), "worldgen-resetchunks");
      long var32 = System.currentTimeMillis();
      int var33 = (var1 * 2 + 1) * (var1 * 2 + 1);
      UnmodifiableIterator var34 = ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.FEATURES, ChunkStatus.INITIALIZE_LIGHT).iterator();

      long var17;
      while(var34.hasNext()) {
         ChunkStatus var36 = (ChunkStatus)var34.next();
         var17 = System.currentTimeMillis();
         Supplier var10000 = () -> {
            return Unit.INSTANCE;
         };
         Objects.requireNonNull(var31);
         CompletableFuture var19 = CompletableFuture.supplyAsync(var10000, var31::tell);
         WorldGenContext var20 = new WorldGenContext(var3, var4.getGenerator(), var3.getStructureManager(), var4.getLightEngine());

         for(int var21 = var6.z - var1; var21 <= var6.z + var1; ++var21) {
            for(int var22 = var6.x - var1; var22 <= var6.x + var1; ++var22) {
               ChunkPos var23 = new ChunkPos(var22, var21);
               LevelChunk var24 = var4.getChunk(var22, var21, false);
               if (var24 != null && (!var2 || !var24.isOldNoiseGeneration())) {
                  ArrayList var25 = Lists.newArrayList();
                  int var26 = Math.max(1, var36.getRange());

                  for(int var27 = var23.z - var26; var27 <= var23.z + var26; ++var27) {
                     for(int var28 = var23.x - var26; var28 <= var23.x + var26; ++var28) {
                        ChunkAccess var29 = var4.getChunk(var28, var27, var36.getParent(), true);
                        Object var30;
                        if (var29 instanceof ImposterProtoChunk) {
                           var30 = new ImposterProtoChunk(((ImposterProtoChunk)var29).getWrapped(), true);
                        } else if (var29 instanceof LevelChunk) {
                           var30 = new ImposterProtoChunk((LevelChunk)var29, true);
                        } else {
                           var30 = var29;
                        }

                        var25.add(var30);
                     }
                  }

                  Function var10001 = (var4x) -> {
                     Objects.requireNonNull(var31);
                     return var36.generate(var20, var31::tell, (var0) -> {
                        throw new UnsupportedOperationException("Not creating full chunks here");
                     }, var25).thenApply((var1) -> {
                        if (var36 == ChunkStatus.NOISE) {
                           Heightmap.primeHeightmaps(var1, ChunkStatus.POST_FEATURES);
                        }

                        return Unit.INSTANCE;
                     });
                  };
                  Objects.requireNonNull(var31);
                  var19 = var19.thenComposeAsync(var10001, var31::tell);
               }
            }
         }

         MinecraftServer var37 = var0.getServer();
         Objects.requireNonNull(var19);
         var37.managedBlock(var19::isDone);
         Logger var38 = LOGGER;
         String var40 = String.valueOf(var36);
         var38.debug(var40 + " took " + (System.currentTimeMillis() - var17) + " ms");
      }

      long var35 = System.currentTimeMillis();

      for(int var39 = var6.z - var1; var39 <= var6.z + var1; ++var39) {
         for(int var18 = var6.x - var1; var18 <= var6.x + var1; ++var18) {
            ChunkPos var41 = new ChunkPos(var18, var39);
            LevelChunk var42 = var4.getChunk(var18, var39, false);
            if (var42 != null && (!var2 || !var42.isOldNoiseGeneration())) {
               Iterator var43 = BlockPos.betweenClosed(var41.getMinBlockX(), var3.getMinBuildHeight(), var41.getMinBlockZ(), var41.getMaxBlockX(), var3.getMaxBuildHeight() - 1, var41.getMaxBlockZ()).iterator();

               while(var43.hasNext()) {
                  BlockPos var44 = (BlockPos)var43.next();
                  var4.blockChanged(var44);
               }
            }
         }
      }

      LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - var35) + " ms");
      var17 = System.currentTimeMillis() - var32;
      var0.sendSuccess(() -> {
         return Component.literal(String.format(Locale.ROOT, "%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", var33, var17, var33, (float)var17 / (float)var33));
      }, true);
      return 1;
   }
}
