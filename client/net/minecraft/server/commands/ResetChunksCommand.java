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
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("resetchunks").requires(var0x -> var0x.hasPermission(2)))
               .executes(var0x -> resetChunks((CommandSourceStack)var0x.getSource(), 0, true)))
            .then(
               ((RequiredArgumentBuilder)Commands.argument("range", IntegerArgumentType.integer(0, 5))
                     .executes(var0x -> resetChunks((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "range"), true)))
                  .then(
                     Commands.argument("skipOldChunks", BoolArgumentType.bool())
                        .executes(
                           var0x -> resetChunks(
                                 (CommandSourceStack)var0x.getSource(),
                                 IntegerArgumentType.getInteger(var0x, "range"),
                                 BoolArgumentType.getBool(var0x, "skipOldChunks")
                              )
                        )
                  )
            )
      );
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

      for (int var11 = var7; var11 <= var8; var11++) {
         for (int var12 = var9; var12 <= var10; var12++) {
            ChunkPos var13 = new ChunkPos(var12, var11);
            LevelChunk var14 = var4.getChunk(var12, var11, false);
            if (var14 != null && (!var2 || !var14.isOldNoiseGeneration())) {
               for (BlockPos var16 : BlockPos.betweenClosed(
                  var13.getMinBlockX(),
                  var3.getMinBuildHeight(),
                  var13.getMinBlockZ(),
                  var13.getMaxBlockX(),
                  var3.getMaxBuildHeight() - 1,
                  var13.getMaxBlockZ()
               )) {
                  var3.setBlock(var16, Blocks.AIR.defaultBlockState(), 16);
               }
            }
         }
      }

      ProcessorMailbox var31 = ProcessorMailbox.create(Util.backgroundExecutor(), "worldgen-resetchunks");
      long var32 = System.currentTimeMillis();
      int var33 = (var1 * 2 + 1) * (var1 * 2 + 1);
      UnmodifiableIterator var34 = ImmutableList.of(
            ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.FEATURES, ChunkStatus.INITIALIZE_LIGHT
         )
         .iterator();

      while (var34.hasNext()) {
         ChunkStatus var36 = (ChunkStatus)var34.next();
         long var17 = System.currentTimeMillis();
         CompletableFuture var19 = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, var31::tell);
         WorldGenContext var20 = new WorldGenContext(var3, var4.getGenerator(), var3.getStructureManager(), var4.getLightEngine());

         for (int var21 = var6.z - var1; var21 <= var6.z + var1; var21++) {
            for (int var22 = var6.x - var1; var22 <= var6.x + var1; var22++) {
               ChunkPos var23 = new ChunkPos(var22, var21);
               LevelChunk var24 = var4.getChunk(var22, var21, false);
               if (var24 != null && (!var2 || !var24.isOldNoiseGeneration())) {
                  ArrayList var25 = Lists.newArrayList();
                  int var26 = Math.max(1, var36.getRange());

                  for (int var27 = var23.z - var26; var27 <= var23.z + var26; var27++) {
                     for (int var28 = var23.x - var26; var28 <= var23.x + var26; var28++) {
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

                  var19 = var19.thenComposeAsync(var4x -> var36.generate(var20, var31::tell, var0xx -> {
                        throw new UnsupportedOperationException("Not creating full chunks here");
                     }, var25).thenApply(var1xx -> {
                        if (var36 == ChunkStatus.NOISE) {
                           Heightmap.primeHeightmaps(var1xx, ChunkStatus.POST_FEATURES);
                        }

                        return Unit.INSTANCE;
                     }), var31::tell);
               }
            }
         }

         var0.getServer().managedBlock(var19::isDone);
         LOGGER.debug(var36 + " took " + (System.currentTimeMillis() - var17) + " ms");
      }

      long var35 = System.currentTimeMillis();

      for (int var37 = var6.z - var1; var37 <= var6.z + var1; var37++) {
         for (int var18 = var6.x - var1; var18 <= var6.x + var1; var18++) {
            ChunkPos var39 = new ChunkPos(var18, var37);
            LevelChunk var40 = var4.getChunk(var18, var37, false);
            if (var40 != null && (!var2 || !var40.isOldNoiseGeneration())) {
               for (BlockPos var42 : BlockPos.betweenClosed(
                  var39.getMinBlockX(),
                  var3.getMinBuildHeight(),
                  var39.getMinBlockZ(),
                  var39.getMaxBlockX(),
                  var3.getMaxBuildHeight() - 1,
                  var39.getMaxBlockZ()
               )) {
                  var4.blockChanged(var42);
               }
            }
         }
      }

      LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - var35) + " ms");
      long var38 = System.currentTimeMillis() - var32;
      var0.sendSuccess(
         () -> Component.literal(
               String.format(
                  Locale.ROOT,
                  "%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk",
                  var33,
                  var38,
                  var33,
                  (float)var38 / (float)var33
               )
            ),
         true
      );
      return 1;
   }
}
