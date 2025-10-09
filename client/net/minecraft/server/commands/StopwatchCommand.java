package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Stopwatch;
import net.minecraft.world.Stopwatches;

public class StopwatchCommand {
   private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.stopwatch.already_exists", var0));
   public static final DynamicCommandExceptionType ERROR_DOES_NOT_EXIST = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.stopwatch.does_not_exist", var0));
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_STOPWATCHES = (var0, var1) -> SharedSuggestionProvider.suggestResource(((CommandSourceStack)var0.getSource()).getServer().getStopwatches().ids(), var1);

   public StopwatchCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stopwatch").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_STOPWATCHES).then(Commands.literal("create").executes((var0x) -> createStopwatch((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"))))).then(Commands.literal("query").executes((var0x) -> queryStopwatch((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"))))).then(Commands.literal("restart").executes((var0x) -> restartStopwatch((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"))))).then(Commands.literal("remove").executes((var0x) -> removeStopwatch((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"))))));
   }

   private static int createStopwatch(CommandSourceStack var0, ResourceLocation var1) throws CommandSyntaxException {
      MinecraftServer var2 = var0.getServer();
      Stopwatches var3 = var2.getStopwatches();
      Stopwatch var4 = new Stopwatch(Stopwatches.currentTime());
      if (!var3.add(var1, var4)) {
         throw ERROR_ALREADY_EXISTS.create(var1);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.stopwatch.create.success", Component.translationArg(var1)), true);
         return 1;
      }
   }

   private static int queryStopwatch(CommandSourceStack var0, ResourceLocation var1) throws CommandSyntaxException {
      MinecraftServer var2 = var0.getServer();
      Stopwatches var3 = var2.getStopwatches();
      Stopwatch var4 = var3.get(var1);
      if (var4 == null) {
         throw ERROR_DOES_NOT_EXIST.create(var1);
      } else {
         long var5 = Stopwatches.currentTime();
         double var7 = var4.elapsedSeconds(var5);
         var0.sendSuccess(() -> Component.translatable("commands.stopwatch.query", Component.translationArg(var1), var7), true);
         return 1;
      }
   }

   private static int restartStopwatch(CommandSourceStack var0, ResourceLocation var1) throws CommandSyntaxException {
      MinecraftServer var2 = var0.getServer();
      Stopwatches var3 = var2.getStopwatches();
      if (!var3.update(var1, (var0x) -> new Stopwatch(Stopwatches.currentTime()))) {
         throw ERROR_DOES_NOT_EXIST.create(var1);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.stopwatch.restart.success", Component.translationArg(var1)), true);
         return 1;
      }
   }

   private static int removeStopwatch(CommandSourceStack var0, ResourceLocation var1) throws CommandSyntaxException {
      MinecraftServer var2 = var0.getServer();
      Stopwatches var3 = var2.getStopwatches();
      if (!var3.remove(var1)) {
         throw ERROR_DOES_NOT_EXIST.create(var1);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.stopwatch.remove.success", Component.translationArg(var1)), true);
         return 1;
      }
   }
}
