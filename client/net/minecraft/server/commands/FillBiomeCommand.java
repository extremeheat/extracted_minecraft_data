package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class FillBiomeCommand {
   public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(Component.translatable("argument.pos.unloaded"));
   private static final Dynamic2CommandExceptionType ERROR_VOLUME_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("commands.fillbiome.toobig", var0, var1);
   });

   public FillBiomeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fillbiome").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("biome", ResourceArgument.resource(var1, Registries.BIOME)).executes((var0x) -> {
         return fill((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to"), ResourceArgument.getResource(var0x, "biome", Registries.BIOME), (var0) -> {
            return true;
         });
      })).then(Commands.literal("replace").then(Commands.argument("filter", ResourceOrTagArgument.resourceOrTag(var1, Registries.BIOME)).executes((var0x) -> {
         CommandSourceStack var10000 = (CommandSourceStack)var0x.getSource();
         BlockPos var10001 = BlockPosArgument.getLoadedBlockPos(var0x, "from");
         BlockPos var10002 = BlockPosArgument.getLoadedBlockPos(var0x, "to");
         Holder.Reference var10003 = ResourceArgument.getResource(var0x, "biome", Registries.BIOME);
         ResourceOrTagArgument.Result var10004 = ResourceOrTagArgument.getResourceOrTag(var0x, "filter", Registries.BIOME);
         Objects.requireNonNull(var10004);
         return fill(var10000, var10001, var10002, var10003, var10004::test);
      })))))));
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

   public static Either<Integer, CommandSyntaxException> fill(ServerLevel var0, BlockPos var1, BlockPos var2, Holder<Biome> var3) {
      return fill(var0, var1, var2, var3, (var0x) -> {
         return true;
      }, (var0x) -> {
      });
   }

   public static Either<Integer, CommandSyntaxException> fill(ServerLevel var0, BlockPos var1, BlockPos var2, Holder<Biome> var3, Predicate<Holder<Biome>> var4, Consumer<Supplier<Component>> var5) {
      BlockPos var6 = quantize(var1);
      BlockPos var7 = quantize(var2);
      BoundingBox var8 = BoundingBox.fromCorners(var6, var7);
      int var9 = var8.getXSpan() * var8.getYSpan() * var8.getZSpan();
      int var10 = var0.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
      if (var9 > var10) {
         return Either.right(ERROR_VOLUME_TOO_LARGE.create(var10, var9));
      } else {
         ArrayList var11 = new ArrayList();

         ChunkAccess var14;
         for(int var12 = SectionPos.blockToSectionCoord(var8.minZ()); var12 <= SectionPos.blockToSectionCoord(var8.maxZ()); ++var12) {
            for(int var13 = SectionPos.blockToSectionCoord(var8.minX()); var13 <= SectionPos.blockToSectionCoord(var8.maxX()); ++var13) {
               var14 = var0.getChunk(var13, var12, ChunkStatus.FULL, false);
               if (var14 == null) {
                  return Either.right(ERROR_NOT_LOADED.create());
               }

               var11.add(var14);
            }
         }

         MutableInt var15 = new MutableInt(0);
         Iterator var16 = var11.iterator();

         while(var16.hasNext()) {
            var14 = (ChunkAccess)var16.next();
            var14.fillBiomesFromNoise(makeResolver(var15, var14, var8, var3, var4), var0.getChunkSource().randomState().sampler());
            var14.setUnsaved(true);
         }

         var0.getChunkSource().chunkMap.resendBiomesForChunks(var11);
         var5.accept(() -> {
            return Component.translatable("commands.fillbiome.success.count", var15.getValue(), var8.minX(), var8.minY(), var8.minZ(), var8.maxX(), var8.maxY(), var8.maxZ());
         });
         return Either.left(var15.getValue());
      }
   }

   private static int fill(CommandSourceStack var0, BlockPos var1, BlockPos var2, Holder.Reference<Biome> var3, Predicate<Holder<Biome>> var4) throws CommandSyntaxException {
      Either var5 = fill(var0.getLevel(), var1, var2, var3, var4, (var1x) -> {
         var0.sendSuccess(var1x, true);
      });
      Optional var6 = var5.right();
      if (var6.isPresent()) {
         throw (CommandSyntaxException)var6.get();
      } else {
         return (Integer)var5.left().get();
      }
   }
}
