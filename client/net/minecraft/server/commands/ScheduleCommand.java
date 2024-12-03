package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.MacroFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType(Component.translatable("commands.schedule.same_tick"));
   private static final DynamicCommandExceptionType ERROR_CANT_REMOVE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.schedule.cleared.failure", var0));
   private static final SimpleCommandExceptionType ERROR_MACRO = new SimpleCommandExceptionType(Component.translatableEscape("commands.schedule.macro"));
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_SCHEDULE = (var0, var1) -> SharedSuggestionProvider.suggest(((CommandSourceStack)var0.getSource()).getServer().getWorldData().overworldData().getScheduledEvents().getEventsIds(), var1);

   public ScheduleCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires((var0x) -> var0x.hasPermission(2))).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("time", TimeArgument.time()).executes((var0x) -> schedule((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctionOrTag(var0x, "function"), IntegerArgumentType.getInteger(var0x, "time"), true))).then(Commands.literal("append").executes((var0x) -> schedule((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctionOrTag(var0x, "function"), IntegerArgumentType.getInteger(var0x, "time"), false)))).then(Commands.literal("replace").executes((var0x) -> schedule((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctionOrTag(var0x, "function"), IntegerArgumentType.getInteger(var0x, "time"), true))))))).then(Commands.literal("clear").then(Commands.argument("function", StringArgumentType.greedyString()).suggests(SUGGEST_SCHEDULE).executes((var0x) -> remove((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "function"))))));
   }

   private static int schedule(CommandSourceStack var0, Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> var1, int var2, boolean var3) throws CommandSyntaxException {
      if (var2 == 0) {
         throw ERROR_SAME_TICK.create();
      } else {
         long var4 = var0.getLevel().getGameTime() + (long)var2;
         ResourceLocation var6 = (ResourceLocation)var1.getFirst();
         TimerQueue var7 = var0.getServer().getWorldData().overworldData().getScheduledEvents();
         Optional var8 = ((Either)var1.getSecond()).left();
         if (var8.isPresent()) {
            if (var8.get() instanceof MacroFunction) {
               throw ERROR_MACRO.create();
            }

            String var9 = var6.toString();
            if (var3) {
               var7.remove(var9);
            }

            var7.schedule(var9, var4, new FunctionCallback(var6));
            var0.sendSuccess(() -> Component.translatable("commands.schedule.created.function", Component.translationArg(var6), var2, var4), true);
         } else {
            String var10 = "#" + String.valueOf(var6);
            if (var3) {
               var7.remove(var10);
            }

            var7.schedule(var10, var4, new FunctionTagCallback(var6));
            var0.sendSuccess(() -> Component.translatable("commands.schedule.created.tag", Component.translationArg(var6), var2, var4), true);
         }

         return Math.floorMod(var4, 2147483647);
      }
   }

   private static int remove(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      int var2 = var0.getServer().getWorldData().overworldData().getScheduledEvents().remove(var1);
      if (var2 == 0) {
         throw ERROR_CANT_REMOVE.create(var1);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.schedule.cleared.success", var2, var1), true);
         return var2;
      }
   }
}
