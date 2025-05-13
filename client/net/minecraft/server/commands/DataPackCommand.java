package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

public class DataPackCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.unknown", var0));
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.enable.failed", var0));
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.disable.failed", var0));
   private static final DynamicCommandExceptionType ERROR_CANNOT_DISABLE_FEATURE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.disable.failed.feature", var0));
   private static final Dynamic2CommandExceptionType ERROR_PACK_FEATURES_NOT_ENABLED = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("commands.datapack.enable.failed.no_flags", var0, var1));
   private static final DynamicCommandExceptionType ERROR_PACK_INVALID_NAME = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.create.invalid_name", var0));
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_EXISTS = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.create.already_exists", var0));
   private static final Dynamic2CommandExceptionType ERROR_PACK_METADATA_ENCODE_FAILURE = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("commands.datapack.create.metadata_encode_failure", var0, var1));
   private static final DynamicCommandExceptionType ERROR_PACK_IO_FAILURE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.create.io_failure", var0));
   private static final SuggestionProvider<CommandSourceStack> SELECTED_PACKS = (var0, var1) -> SharedSuggestionProvider.suggest(((CommandSourceStack)var0.getSource()).getServer().getPackRepository().getSelectedIds().stream().map(StringArgumentType::escapeIfRequired), var1);
   private static final SuggestionProvider<CommandSourceStack> UNSELECTED_PACKS = (var0, var1) -> {
      PackRepository var2 = ((CommandSourceStack)var0.getSource()).getServer().getPackRepository();
      Collection var3 = var2.getSelectedIds();
      FeatureFlagSet var4 = ((CommandSourceStack)var0.getSource()).enabledFeatures();
      return SharedSuggestionProvider.suggest(var2.getAvailablePacks().stream().filter((var1x) -> var1x.getRequestedFeatures().isSubsetOf(var4)).map(Pack::getId).filter((var1x) -> !var3.contains(var1x)).map(StringArgumentType::escapeIfRequired), var1);
   };

   public DataPackCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires(Commands.hasPermission(2))).then(Commands.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", StringArgumentType.string()).suggests(UNSELECTED_PACKS).executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var0, var1) -> var1.getDefaultPosition().insert(var0, var1, Pack::selectionConfig, false)))).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var1, var2) -> var1.add(var1.indexOf(getPack(var0x, "existing", false)) + 1, var2)))))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var1, var2) -> var1.add(var1.indexOf(getPack(var0x, "existing", false)), var2)))))).then(Commands.literal("last").executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), List::add)))).then(Commands.literal("first").executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var0, var1) -> var0.add(0, var1))))))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((var0x) -> disablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes((var0x) -> listPacks((CommandSourceStack)var0x.getSource()))).then(Commands.literal("available").executes((var0x) -> listAvailablePacks((CommandSourceStack)var0x.getSource())))).then(Commands.literal("enabled").executes((var0x) -> listEnabledPacks((CommandSourceStack)var0x.getSource()))))).then(((LiteralArgumentBuilder)Commands.literal("create").requires(Commands.hasPermission(4))).then(Commands.argument("id", StringArgumentType.string()).then(Commands.argument("description", ComponentArgument.textComponent(var1)).executes((var0x) -> createPack((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "id"), ComponentArgument.getResolvedComponent(var0x, "description")))))));
   }

   private static int createPack(CommandSourceStack var0, String var1, Component var2) throws CommandSyntaxException {
      Path var3 = var0.getServer().getWorldPath(LevelResource.DATAPACK_DIR);
      if (!FileUtil.isValidStrictPathSegment(var1)) {
         throw ERROR_PACK_INVALID_NAME.create(var1);
      } else {
         Path var4 = var3.resolve(var1);
         if (Files.exists(var4, new LinkOption[0])) {
            throw ERROR_PACK_ALREADY_EXISTS.create(var1);
         } else {
            PackMetadataSection var5 = new PackMetadataSection(var2, SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA), Optional.empty());
            DataResult var6 = PackMetadataSection.CODEC.encodeStart(JsonOps.INSTANCE, var5);
            Optional var7 = var6.error();
            if (var7.isPresent()) {
               throw ERROR_PACK_METADATA_ENCODE_FAILURE.create(var1, ((DataResult.Error)var7.get()).message());
            } else {
               JsonObject var8 = new JsonObject();
               var8.add(PackMetadataSection.TYPE.name(), (JsonElement)var6.getOrThrow());

               try {
                  Files.createDirectory(var4);
                  Files.createDirectory(var4.resolve(PackType.SERVER_DATA.getDirectory()));
                  BufferedWriter var9 = Files.newBufferedWriter(var4.resolve("pack.mcmeta"), StandardCharsets.UTF_8);

                  try {
                     JsonWriter var10 = new JsonWriter(var9);

                     try {
                        var10.setSerializeNulls(false);
                        var10.setIndent("  ");
                        GsonHelper.writeValue(var10, var8, (Comparator)null);
                     } catch (Throwable var15) {
                        try {
                           var10.close();
                        } catch (Throwable var14) {
                           var15.addSuppressed(var14);
                        }

                        throw var15;
                     }

                     var10.close();
                  } catch (Throwable var16) {
                     if (var9 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var13) {
                           var16.addSuppressed(var13);
                        }
                     }

                     throw var16;
                  }

                  if (var9 != null) {
                     var9.close();
                  }
               } catch (IOException var17) {
                  LOGGER.warn("Failed to create pack at {}", var3.toAbsolutePath(), var17);
                  throw ERROR_PACK_IO_FAILURE.create(var1);
               }

               var0.sendSuccess(() -> Component.translatable("commands.datapack.create.success", var1), true);
               return 1;
            }
         }
      }
   }

   private static int enablePack(CommandSourceStack var0, Pack var1, Inserter var2) throws CommandSyntaxException {
      PackRepository var3 = var0.getServer().getPackRepository();
      ArrayList var4 = Lists.newArrayList(var3.getSelectedPacks());
      var2.apply(var4, var1);
      var0.sendSuccess(() -> Component.translatable("commands.datapack.modify.enable", var1.getChatLink(true)), true);
      ReloadCommand.reloadPacks((Collection)var4.stream().map(Pack::getId).collect(Collectors.toList()), var0);
      return var4.size();
   }

   private static int disablePack(CommandSourceStack var0, Pack var1) {
      PackRepository var2 = var0.getServer().getPackRepository();
      ArrayList var3 = Lists.newArrayList(var2.getSelectedPacks());
      var3.remove(var1);
      var0.sendSuccess(() -> Component.translatable("commands.datapack.modify.disable", var1.getChatLink(true)), true);
      ReloadCommand.reloadPacks((Collection)var3.stream().map(Pack::getId).collect(Collectors.toList()), var0);
      return var3.size();
   }

   private static int listPacks(CommandSourceStack var0) {
      return listEnabledPacks(var0) + listAvailablePacks(var0);
   }

   private static int listAvailablePacks(CommandSourceStack var0) {
      PackRepository var1 = var0.getServer().getPackRepository();
      var1.reload();
      Collection var2 = var1.getSelectedPacks();
      Collection var3 = var1.getAvailablePacks();
      FeatureFlagSet var4 = var0.enabledFeatures();
      List var5 = var3.stream().filter((var2x) -> !var2.contains(var2x) && var2x.getRequestedFeatures().isSubsetOf(var4)).toList();
      if (var5.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.datapack.list.available.none"), false);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.datapack.list.available.success", var5.size(), ComponentUtils.formatList(var5, (Function)((var0) -> var0.getChatLink(false)))), false);
      }

      return var5.size();
   }

   private static int listEnabledPacks(CommandSourceStack var0) {
      PackRepository var1 = var0.getServer().getPackRepository();
      var1.reload();
      Collection var2 = var1.getSelectedPacks();
      if (var2.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.datapack.list.enabled.none"), false);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.datapack.list.enabled.success", var2.size(), ComponentUtils.formatList(var2, (Function)((var0) -> var0.getChatLink(true)))), false);
      }

      return var2.size();
   }

   private static Pack getPack(CommandContext<CommandSourceStack> var0, String var1, boolean var2) throws CommandSyntaxException {
      String var3 = StringArgumentType.getString(var0, var1);
      PackRepository var4 = ((CommandSourceStack)var0.getSource()).getServer().getPackRepository();
      Pack var5 = var4.getPack(var3);
      if (var5 == null) {
         throw ERROR_UNKNOWN_PACK.create(var3);
      } else {
         boolean var6 = var4.getSelectedPacks().contains(var5);
         if (var2 && var6) {
            throw ERROR_PACK_ALREADY_ENABLED.create(var3);
         } else if (!var2 && !var6) {
            throw ERROR_PACK_ALREADY_DISABLED.create(var3);
         } else {
            FeatureFlagSet var7 = ((CommandSourceStack)var0.getSource()).enabledFeatures();
            FeatureFlagSet var8 = var5.getRequestedFeatures();
            if (!var2 && !var8.isEmpty() && var5.getPackSource() == PackSource.FEATURE) {
               throw ERROR_CANNOT_DISABLE_FEATURE.create(var3);
            } else if (!var8.isSubsetOf(var7)) {
               throw ERROR_PACK_FEATURES_NOT_ENABLED.create(var3, FeatureFlags.printMissingFlags(var7, var8));
            } else {
               return var5;
            }
         }
      }
   }

   interface Inserter {
      void apply(List<Pack> var1, Pack var2) throws CommandSyntaxException;
   }
}
