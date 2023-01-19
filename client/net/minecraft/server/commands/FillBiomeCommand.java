package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class FillBiomeCommand {
   private static final int MAX_FILL_AREA = 32768;
   public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(Component.translatable("argument.pos.unloaded"));
   private static final Dynamic2CommandExceptionType ERROR_VOLUME_TOO_LARGE = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatable("commands.fillbiome.toobig", var0, var1)
   );

   public FillBiomeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fillbiome").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("from", BlockPosArgument.blockPos())
                  .then(
                     Commands.argument("to", BlockPosArgument.blockPos())
                        .then(
                           ((RequiredArgumentBuilder)Commands.argument("biome", ResourceArgument.resource(var1, Registries.BIOME))
                                 .executes(
                                    var0x -> fill(
                                          (CommandSourceStack)var0x.getSource(),
                                          BlockPosArgument.getLoadedBlockPos(var0x, "from"),
                                          BlockPosArgument.getLoadedBlockPos(var0x, "to"),
                                          ResourceArgument.getResource(var0x, "biome", Registries.BIOME),
                                          var0xx -> true
                                       )
                                 ))
                              .then(
                                 Commands.literal("replace")
                                    .then(
                                       Commands.argument("filter", ResourceOrTagArgument.resourceOrTag(var1, Registries.BIOME))
                                          .executes(
                                             var0x -> fill(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   BlockPosArgument.getLoadedBlockPos(var0x, "from"),
                                                   BlockPosArgument.getLoadedBlockPos(var0x, "to"),
                                                   ResourceArgument.getResource(var0x, "biome", Registries.BIOME),
                                                   ResourceOrTagArgument.getResourceOrTag(var0x, "filter", Registries.BIOME)::test
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int quantize(int var0) {
      return QuartPos.toBlock(QuartPos.fromBlock(var0));
   }

   private static BlockPos quantize(BlockPos var0) {
      return new BlockPos(quantize(var0.getX()), quantize(var0.getY()), quantize(var0.getZ()));
   }

   private static BiomeResolver makeResolver(MutableInt var0, ChunkAccess var1, BoundingBox var2, Holder<Biome> var3, Predicate<Holder<Biome>> var4) {
      return (var5, var6, var7, var8) -> {
         int var9 = QuartPos.toBlock(var5);
         int var10 = QuartPos.toBlock(var6);
         int var11 = QuartPos.toBlock(var7);
         Holder var12 = var1.getNoiseBiome(var5, var6, var7);
         if (var2.isInside(var9, var10, var11) && var4.test(var12)) {
            var0.increment();
            return var3;
         } else {
            return var12;
         }
      };
   }

   private static int fill(CommandSourceStack var0, BlockPos var1, BlockPos var2, Holder.Reference<Biome> var3, Predicate<Holder<Biome>> var4) throws CommandSyntaxException {
      BlockPos var5 = quantize(var1);
      BlockPos var6 = quantize(var2);
      BoundingBox var7 = BoundingBox.fromCorners(var5, var6);
      int var8 = var7.getXSpan() * var7.getYSpan() * var7.getZSpan();
      if (var8 > 32768) {
         throw ERROR_VOLUME_TOO_LARGE.create(32768, var8);
      } else {
         ServerLevel var9 = var0.getLevel();
         ArrayList var10 = new ArrayList();

         for(int var11 = SectionPos.blockToSectionCoord(var7.minZ()); var11 <= SectionPos.blockToSectionCoord(var7.maxZ()); ++var11) {
            for(int var12 = SectionPos.blockToSectionCoord(var7.minX()); var12 <= SectionPos.blockToSectionCoord(var7.maxX()); ++var12) {
               ChunkAccess var13 = var9.getChunk(var12, var11, ChunkStatus.FULL, false);
               if (var13 == null) {
                  throw ERROR_NOT_LOADED.create();
               }

               var10.add(var13);
            }
         }

         MutableInt var14 = new MutableInt(0);

         for(ChunkAccess var16 : var10) {
            var16.fillBiomesFromNoise(makeResolver(var14, var16, var7, var3, var4), var9.getChunkSource().randomState().sampler());
            var16.setUnsaved(true);
            var9.getChunkSource().chunkMap.resendChunk(var16);
         }

         var0.sendSuccess(
            Component.translatable(
               "commands.fillbiome.success.count", var14.getValue(), var7.minX(), var7.minY(), var7.minZ(), var7.maxX(), var7.maxY(), var7.maxZ()
            ),
            true
         );
         return var14.getValue();
      }
   }
}
