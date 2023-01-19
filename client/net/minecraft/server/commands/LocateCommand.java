package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
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

public class LocateCommand {
   private static final DynamicCommandExceptionType ERROR_STRUCTURE_NOT_FOUND = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.locate.structure.not_found", var0)
   );
   private static final DynamicCommandExceptionType ERROR_STRUCTURE_INVALID = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.locate.structure.invalid", var0)
   );
   private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.locate.biome.not_found", var0)
   );
   private static final DynamicCommandExceptionType ERROR_BIOME_INVALID = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.locate.biome.invalid", var0)
   );
   private static final DynamicCommandExceptionType ERROR_POI_NOT_FOUND = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.locate.poi.not_found", var0)
   );
   private static final DynamicCommandExceptionType ERROR_POI_INVALID = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.locate.poi.invalid", var0)
   );
   private static final int MAX_STRUCTURE_SEARCH_RADIUS = 100;
   private static final int MAX_BIOME_SEARCH_RADIUS = 6400;
   private static final int BIOME_SAMPLE_RESOLUTION_HORIZONTAL = 32;
   private static final int BIOME_SAMPLE_RESOLUTION_VERTICAL = 64;
   private static final int POI_SEARCH_RADIUS = 256;

   public LocateCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locate")
                     .requires(var0x -> var0x.hasPermission(2)))
                  .then(
                     Commands.literal("structure")
                        .then(
                           Commands.argument("structure", ResourceOrTagLocationArgument.resourceOrTag(Registry.STRUCTURE_REGISTRY))
                              .executes(
                                 var0x -> locateStructure(
                                       (CommandSourceStack)var0x.getSource(),
                                       ResourceOrTagLocationArgument.getRegistryType(var0x, "structure", Registry.STRUCTURE_REGISTRY, ERROR_STRUCTURE_INVALID)
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.literal("biome")
                     .then(
                        Commands.argument("biome", ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY))
                           .executes(
                              var0x -> locateBiome(
                                    (CommandSourceStack)var0x.getSource(),
                                    ResourceOrTagLocationArgument.getRegistryType(var0x, "biome", Registry.BIOME_REGISTRY, ERROR_BIOME_INVALID)
                                 )
                           )
                     )
               ))
            .then(
               Commands.literal("poi")
                  .then(
                     Commands.argument("poi", ResourceOrTagLocationArgument.resourceOrTag(Registry.POINT_OF_INTEREST_TYPE_REGISTRY))
                        .executes(
                           var0x -> locatePoi(
                                 (CommandSourceStack)var0x.getSource(),
                                 ResourceOrTagLocationArgument.getRegistryType(var0x, "poi", Registry.POINT_OF_INTEREST_TYPE_REGISTRY, ERROR_POI_INVALID)
                              )
                        )
                  )
            )
      );
   }

   private static Optional<? extends HolderSet.ListBacked<Structure>> getHolders(
      ResourceOrTagLocationArgument.Result<Structure> var0, Registry<Structure> var1
   ) {
      return (Optional<? extends HolderSet.ListBacked<Structure>>)var0.unwrap()
         .map(var1x -> var1.getHolder(var1x).map(var0xx -> HolderSet.direct(var0xx)), var1::getTag);
   }

   private static int locateStructure(CommandSourceStack var0, ResourceOrTagLocationArgument.Result<Structure> var1) throws CommandSyntaxException {
      Registry var2 = var0.getLevel().registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
      HolderSet var3 = getHolders(var1, var2).orElseThrow(() -> ERROR_STRUCTURE_INVALID.create(var1.asPrintable()));
      BlockPos var4 = new BlockPos(var0.getPosition());
      ServerLevel var5 = var0.getLevel();
      Pair var6 = var5.getChunkSource().getGenerator().findNearestMapStructure(var5, var3, var4, 100, false);
      if (var6 == null) {
         throw ERROR_STRUCTURE_NOT_FOUND.create(var1.asPrintable());
      } else {
         return showLocateResult(var0, var1, var4, var6, "commands.locate.structure.success", false);
      }
   }

   private static int locateBiome(CommandSourceStack var0, ResourceOrTagLocationArgument.Result<Biome> var1) throws CommandSyntaxException {
      BlockPos var2 = new BlockPos(var0.getPosition());
      Pair var3 = var0.getLevel().findClosestBiome3d(var1, var2, 6400, 32, 64);
      if (var3 == null) {
         throw ERROR_BIOME_NOT_FOUND.create(var1.asPrintable());
      } else {
         return showLocateResult(var0, var1, var2, var3, "commands.locate.biome.success", true);
      }
   }

   private static int locatePoi(CommandSourceStack var0, ResourceOrTagLocationArgument.Result<PoiType> var1) throws CommandSyntaxException {
      BlockPos var2 = new BlockPos(var0.getPosition());
      ServerLevel var3 = var0.getLevel();
      Optional var4 = var3.getPoiManager().findClosestWithType(var1, var2, 256, PoiManager.Occupancy.ANY);
      if (var4.isEmpty()) {
         throw ERROR_POI_NOT_FOUND.create(var1.asPrintable());
      } else {
         return showLocateResult(var0, var1, var2, ((Pair)var4.get()).swap(), "commands.locate.poi.success", false);
      }
   }

   public static int showLocateResult(
      CommandSourceStack var0,
      ResourceOrTagLocationArgument.Result<?> var1,
      BlockPos var2,
      Pair<BlockPos, ? extends Holder<?>> var3,
      String var4,
      boolean var5
   ) {
      BlockPos var6 = (BlockPos)var3.getFirst();
      String var7 = (String)var1.unwrap()
         .map(
            var0x -> var0x.location().toString(),
            var1x -> "#"
                  + var1x.location()
                  + " ("
                  + (String)((Holder)var3.getSecond()).unwrapKey().map(var0xx -> var0xx.location().toString()).orElse("[unregistered]")
                  + ")"
         );
      int var8 = var5 ? Mth.floor(Mth.sqrt((float)var2.distSqr(var6))) : Mth.floor(dist(var2.getX(), var2.getZ(), var6.getX(), var6.getZ()));
      String var9 = var5 ? String.valueOf(var6.getY()) : "~";
      MutableComponent var10 = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", var6.getX(), var9, var6.getZ()))
         .withStyle(
            var2x -> var2x.withColor(ChatFormatting.GREEN)
                  .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + var6.getX() + " " + var9 + " " + var6.getZ()))
                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
         );
      var0.sendSuccess(Component.translatable(var4, var7, var10, var8), false);
      return var8;
   }

   private static float dist(int var0, int var1, int var2, int var3) {
      int var4 = var2 - var0;
      int var5 = var3 - var1;
      return Mth.sqrt((float)(var4 * var4 + var5 * var5));
   }
}
