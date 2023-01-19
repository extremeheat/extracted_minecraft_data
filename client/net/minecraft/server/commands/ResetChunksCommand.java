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
               for(BlockPos var16 : BlockPos.betweenClosed(
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

      ProcessorMailbox var30 = ProcessorMailbox.create(Util.backgroundExecutor(), "worldgen-resetchunks");
      long var31 = System.currentTimeMillis();
      int var32 = (var1 * 2 + 1) * (var1 * 2 + 1);
      UnmodifiableIterator var33 = ImmutableList.of(
            ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.LIQUID_CARVERS, ChunkStatus.FEATURES
         )
         .iterator();

      while(var33.hasNext()) {
         ChunkStatus var35 = (ChunkStatus)var33.next();
         long var17 = System.currentTimeMillis();
         CompletableFuture var19 = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, var30::tell);

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

                  var19 = var19.thenComposeAsync(
                     var5x -> var35.generate(var30::tell, var3, var4.getGenerator(), var3.getStructureManager(), var4.getLightEngine(), var0xx -> {
                           throw new UnsupportedOperationException("Not creating full chunks here");
                        }, var24, true).thenApply(var1xx -> {
                           if (var35 == ChunkStatus.NOISE) {
                              var1xx.left().ifPresent(var0xxx -> Heightmap.primeHeightmaps(var0xxx, ChunkStatus.POST_FEATURES));
                           }
   
                           return Unit.INSTANCE;
                        }), var30::tell
                  );
               }
            }
         }

         var0.getServer().managedBlock(var19::isDone);
         LOGGER.debug(var35.getName() + " took " + (System.currentTimeMillis() - var17) + " ms");
      }

      long var34 = System.currentTimeMillis();

      for(int var36 = var6.z - var1; var36 <= var6.z + var1; ++var36) {
         for(int var18 = var6.x - var1; var18 <= var6.x + var1; ++var18) {
            ChunkPos var38 = new ChunkPos(var18, var36);
            LevelChunk var39 = var4.getChunk(var18, var36, false);
            if (var39 != null && (!var2 || !var39.isOldNoiseGeneration())) {
               for(BlockPos var41 : BlockPos.betweenClosed(
                  var38.getMinBlockX(),
                  var3.getMinBuildHeight(),
                  var38.getMinBlockZ(),
                  var38.getMaxBlockX(),
                  var3.getMaxBuildHeight() - 1,
                  var38.getMaxBlockZ()
               )) {
                  var4.blockChanged(var41);
               }
            }
         }
      }

      LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - var34) + " ms");
      long var37 = System.currentTimeMillis() - var31;
      var0.sendSuccess(
         Component.literal(
            String.format("%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", var32, var37, var32, (float)var37 / (float)var32)
         ),
         true
      );
      return 1;
   }
}
