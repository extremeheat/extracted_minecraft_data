package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class DataPackCommand {
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.unknown", var0));
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.enable.failed", var0));
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.disable.failed", var0));
   private static final DynamicCommandExceptionType ERROR_CANNOT_DISABLE_FEATURE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.datapack.disable.failed.feature", var0));
   private static final Dynamic2CommandExceptionType ERROR_PACK_FEATURES_NOT_ENABLED = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("commands.datapack.enable.failed.no_flags", var0, var1));
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

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires((var0x) -> var0x.hasPermission(2))).then(Commands.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", StringArgumentType.string()).suggests(UNSELECTED_PACKS).executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var0, var1) -> var1.getDefaultPosition().insert(var0, var1, Pack::selectionConfig, false)))).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var1, var2) -> var1.add(var1.indexOf(getPack(var0x, "existing", false)) + 1, var2)))))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var1, var2) -> var1.add(var1.indexOf(getPack(var0x, "existing", false)), var2)))))).then(Commands.literal("last").executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), List::add)))).then(Commands.literal("first").executes((var0x) -> enablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", true), (var0, var1) -> var0.add(0, var1))))))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((var0x) -> disablePack((CommandSourceStack)var0x.getSource(), getPack(var0x, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes((var0x) -> listPacks((CommandSourceStack)var0x.getSource()))).then(Commands.literal("available").executes((var0x) -> listAvailablePacks((CommandSourceStack)var0x.getSource())))).then(Commands.literal("enabled").executes((var0x) -> listEnabledPacks((CommandSourceStack)var0x.getSource())))));
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
