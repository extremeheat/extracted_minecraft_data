package net.minecraft.server.commands;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;

public class LocateCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final DynamicCommandExceptionType ERROR_STRUCTURE_NOT_FOUND = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.locate.structure.not_found", var0);
   });
   private static final DynamicCommandExceptionType ERROR_STRUCTURE_INVALID = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.locate.structure.invalid", var0);
   });
   private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.locate.biome.not_found", var0);
   });
   private static final DynamicCommandExceptionType ERROR_POI_NOT_FOUND = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.locate.poi.not_found", var0);
   });
   private static final int MAX_STRUCTURE_SEARCH_RADIUS = 100;
   private static final int MAX_BIOME_SEARCH_RADIUS = 6400;
   private static final int BIOME_SAMPLE_RESOLUTION_HORIZONTAL = 32;
   private static final int BIOME_SAMPLE_RESOLUTION_VERTICAL = 64;
   private static final int POI_SEARCH_RADIUS = 256;

   public LocateCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locate").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("structure").then(Commands.argument("structure", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.STRUCTURE)).executes((var0x) -> {
         return locateStructure((CommandSourceStack)var0x.getSource(), ResourceOrTagKeyArgument.getResourceOrTagKey(var0x, "structure", Registries.STRUCTURE, ERROR_STRUCTURE_INVALID));
      })))).then(Commands.literal("biome").then(Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(var1, Registries.BIOME)).executes((var0x) -> {
         return locateBiome((CommandSourceStack)var0x.getSource(), ResourceOrTagArgument.getResourceOrTag(var0x, "biome", Registries.BIOME));
      })))).then(Commands.literal("poi").then(Commands.argument("poi", ResourceOrTagArgument.resourceOrTag(var1, Registries.POINT_OF_INTEREST_TYPE)).executes((var0x) -> {
         return locatePoi((CommandSourceStack)var0x.getSource(), ResourceOrTagArgument.getResourceOrTag(var0x, "poi", Registries.POINT_OF_INTEREST_TYPE));
      }))));
   }

   private static Optional<? extends HolderSet.ListBacked<Structure>> getHolders(ResourceOrTagKeyArgument.Result<Structure> var0, Registry<Structure> var1) {
      Either var10000 = var0.unwrap();
      Function var10001 = (var1x) -> {
         return var1.getHolder(var1x).map((var0) -> {
            return HolderSet.direct(var0);
         });
      };
      Objects.requireNonNull(var1);
      return (Optional)var10000.map(var10001, var1::getTag);
   }

   private static int locateStructure(CommandSourceStack var0, ResourceOrTagKeyArgument.Result<Structure> var1) throws CommandSyntaxException {
      Registry var2 = var0.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);
      HolderSet var3 = (HolderSet)getHolders(var1, var2).orElseThrow(() -> {
         return ERROR_STRUCTURE_INVALID.create(var1.asPrintable());
      });
      BlockPos var4 = BlockPos.containing(var0.getPosition());
      ServerLevel var5 = var0.getLevel();
      Stopwatch var6 = Stopwatch.createStarted(Util.TICKER);
      Pair var7 = var5.getChunkSource().getGenerator().findNearestMapStructure(var5, var3, var4, 100, false);
      var6.stop();
      if (var7 == null) {
         throw ERROR_STRUCTURE_NOT_FOUND.create(var1.asPrintable());
      } else {
         return showLocateResult(var0, var1, var4, var7, "commands.locate.structure.success", false, var6.elapsed());
      }
   }

   private static int locateBiome(CommandSourceStack var0, ResourceOrTagArgument.Result<Biome> var1) throws CommandSyntaxException {
      BlockPos var2 = BlockPos.containing(var0.getPosition());
      Stopwatch var3 = Stopwatch.createStarted(Util.TICKER);
      Pair var4 = var0.getLevel().findClosestBiome3d(var1, var2, 6400, 32, 64);
      var3.stop();
      if (var4 == null) {
         throw ERROR_BIOME_NOT_FOUND.create(var1.asPrintable());
      } else {
         return showLocateResult(var0, var1, var2, var4, "commands.locate.biome.success", true, var3.elapsed());
      }
   }

   private static int locatePoi(CommandSourceStack var0, ResourceOrTagArgument.Result<PoiType> var1) throws CommandSyntaxException {
      BlockPos var2 = BlockPos.containing(var0.getPosition());
      ServerLevel var3 = var0.getLevel();
      Stopwatch var4 = Stopwatch.createStarted(Util.TICKER);
      Optional var5 = var3.getPoiManager().findClosestWithType(var1, var2, 256, PoiManager.Occupancy.ANY);
      var4.stop();
      if (var5.isEmpty()) {
         throw ERROR_POI_NOT_FOUND.create(var1.asPrintable());
      } else {
         return showLocateResult(var0, var1, var2, ((Pair)var5.get()).swap(), "commands.locate.poi.success", false, var4.elapsed());
      }
   }

   public static int showLocateResult(CommandSourceStack var0, ResourceOrTagArgument.Result<?> var1, BlockPos var2, Pair<BlockPos, ? extends Holder<?>> var3, String var4, boolean var5, Duration var6) {
      String var7 = (String)var1.unwrap().map((var1x) -> {
         return var1.asPrintable();
      }, (var2x) -> {
         String var10000 = var1.asPrintable();
         return var10000 + " (" + ((Holder)var3.getSecond()).getRegisteredName() + ")";
      });
      return showLocateResult(var0, var2, var3, var4, var5, var7, var6);
   }

   public static int showLocateResult(CommandSourceStack var0, ResourceOrTagKeyArgument.Result<?> var1, BlockPos var2, Pair<BlockPos, ? extends Holder<?>> var3, String var4, boolean var5, Duration var6) {
      String var7 = (String)var1.unwrap().map((var0x) -> {
         return var0x.location().toString();
      }, (var1x) -> {
         String var10000 = String.valueOf(var1x.location());
         return "#" + var10000 + " (" + ((Holder)var3.getSecond()).getRegisteredName() + ")";
      });
      return showLocateResult(var0, var2, var3, var4, var5, var7, var6);
   }

   private static int showLocateResult(CommandSourceStack var0, BlockPos var1, Pair<BlockPos, ? extends Holder<?>> var2, String var3, boolean var4, String var5, Duration var6) {
      BlockPos var7 = (BlockPos)var2.getFirst();
      int var8 = var4 ? Mth.floor(Mth.sqrt((float)var1.distSqr(var7))) : Mth.floor(dist(var1.getX(), var1.getZ(), var7.getX(), var7.getZ()));
      String var9 = var4 ? String.valueOf(var7.getY()) : "~";
      MutableComponent var10 = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", var7.getX(), var9, var7.getZ())).withStyle((var2x) -> {
         return var2x.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + var7.getX() + " " + var9 + " " + var7.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")));
      });
      var0.sendSuccess(() -> {
         return Component.translatable(var3, var5, var10, var8);
      }, false);
      LOGGER.info("Locating element " + var5 + " took " + var6.toMillis() + " ms");
      return var8;
   }

   private static float dist(int var0, int var1, int var2, int var3) {
      int var4 = var2 - var0;
      int var5 = var3 - var1;
      return Mth.sqrt((float)(var4 * var4 + var5 * var5));
   }
}
