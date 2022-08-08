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
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
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
      ChunkPos var6 = new ChunkPos(new BlockPos(var5));
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

      ProcessorMailbox var30 = ProcessorMailbox.create(Util.backgroundExecutor(), "worldgen-resetchunks");
      long var31 = System.currentTimeMillis();
      int var32 = (var1 * 2 + 1) * (var1 * 2 + 1);
      UnmodifiableIterator var33 = ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.LIQUID_CARVERS, ChunkStatus.FEATURES).iterator();

      long var17;
      while(var33.hasNext()) {
         ChunkStatus var35 = (ChunkStatus)var33.next();
         var17 = System.currentTimeMillis();
         Supplier var10000 = () -> {
            return Unit.INSTANCE;
         };
         Objects.requireNonNull(var30);
         CompletableFuture var19 = CompletableFuture.supplyAsync(var10000, var30::tell);

         for(int var20 = var6.z - var1; var20 <= var6.z + var1; ++var20) {
            for(int var21 = var6.x - var1; var21 <= var6.x + var1; ++var21) {
               ChunkPos var22 = new ChunkPos(var21, var20);
               LevelChunk var23 = var4.getChunk(var21, var20, false);
               if (var23 != null && (!var2 || !var23.isOldNoiseGeneration())) {
                  ArrayList var24 = Lists.newArrayList();
                  int var25 = Math.max(1, var35.getRange());

                  for(int var26 = var22.z - var25; var26 <= var22.z + var25; ++var26) {
                     for(int var27 = var22.x - var25; var27 <= var22.x + var25; ++var27) {
                        ChunkAccess var28 = var4.getChunk(var27, var26, var35.getParent(), true);
                        Object var29;
                        if (var28 instanceof ImposterProtoChunk) {
                           var29 = new ImposterProtoChunk(((ImposterProtoChunk)var28).getWrapped(), true);
                        } else if (var28 instanceof LevelChunk) {
                           var29 = new ImposterProtoChunk((LevelChunk)var28, true);
                        } else {
                           var29 = var28;
                        }

                        var24.add(var29);
                     }
                  }

                  Function var10001 = (var5x) -> {
                     Objects.requireNonNull(var30);
                     return var35.generate(var30::tell, var3, var4.getGenerator(), var3.getStructureManager(), var4.getLightEngine(), (var0) -> {
                        throw new UnsupportedOperationException("Not creating full chunks here");
                     }, var24, true).thenApply((var1) -> {
                        if (var35 == ChunkStatus.NOISE) {
                           var1.left().ifPresent((var0) -> {
                              Heightmap.primeHeightmaps(var0, ChunkStatus.POST_FEATURES);
                           });
                        }

                        return Unit.INSTANCE;
                     });
                  };
                  Objects.requireNonNull(var30);
                  var19 = var19.thenComposeAsync(var10001, var30::tell);
               }
            }
         }

         MinecraftServer var36 = var0.getServer();
         Objects.requireNonNull(var19);
         var36.managedBlock(var19::isDone);
         Logger var37 = LOGGER;
         String var39 = var35.getName();
         var37.debug(var39 + " took " + (System.currentTimeMillis() - var17) + " ms");
      }

      long var34 = System.currentTimeMillis();

      for(int var38 = var6.z - var1; var38 <= var6.z + var1; ++var38) {
         for(int var18 = var6.x - var1; var18 <= var6.x + var1; ++var18) {
            ChunkPos var40 = new ChunkPos(var18, var38);
            LevelChunk var41 = var4.getChunk(var18, var38, false);
            if (var41 != null && (!var2 || !var41.isOldNoiseGeneration())) {
               Iterator var42 = BlockPos.betweenClosed(var40.getMinBlockX(), var3.getMinBuildHeight(), var40.getMinBlockZ(), var40.getMaxBlockX(), var3.getMaxBuildHeight() - 1, var40.getMaxBlockZ()).iterator();

               while(var42.hasNext()) {
                  BlockPos var43 = (BlockPos)var42.next();
                  var4.blockChanged(var43);
               }
            }
         }
      }

      LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - var34) + " ms");
      var17 = System.currentTimeMillis() - var31;
      var0.sendSuccess(Component.literal(String.format(Locale.ROOT, "%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", var32, var17, var32, (float)var17 / (float)var32)), true);
      return 1;
   }
}
