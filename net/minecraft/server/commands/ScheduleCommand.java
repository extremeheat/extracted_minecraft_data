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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType(new TranslatableComponent("commands.schedule.same_tick", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_CANT_REMOVE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.schedule.cleared.failure", new Object[]{var0});
   });
   private static final SuggestionProvider SUGGEST_SCHEDULE = (var0, var1) -> {
      return SharedSuggestionProvider.suggest((Iterable)((CommandSourceStack)var0.getSource()).getLevel().getLevelData().getScheduledEvents().getEventsIds(), var1);
   };

   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("time", TimeArgument.time()).executes((var0x) -> {
         return schedule((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctionOrTag(var0x, "function"), IntegerArgumentType.getInteger(var0x, "time"), true);
      })).then(Commands.literal("append").executes((var0x) -> {
         return schedule((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctionOrTag(var0x, "function"), IntegerArgumentType.getInteger(var0x, "time"), false);
      }))).then(Commands.literal("replace").executes((var0x) -> {
         return schedule((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctionOrTag(var0x, "function"), IntegerArgumentType.getInteger(var0x, "time"), true);
      })))))).then(Commands.literal("clear").then(Commands.argument("function", StringArgumentType.greedyString()).suggests(SUGGEST_SCHEDULE).executes((var0x) -> {
         return remove((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "function"));
      }))));
   }

   private static int schedule(CommandSourceStack var0, Either var1, int var2, boolean var3) throws CommandSyntaxException {
      if (var2 == 0) {
         throw ERROR_SAME_TICK.create();
      } else {
         long var4 = var0.getLevel().getGameTime() + (long)var2;
         TimerQueue var6 = var0.getLevel().getLevelData().getScheduledEvents();
         var1.ifLeft((var6x) -> {
            ResourceLocation var7 = var6x.getId();
            String var8 = var7.toString();
            if (var3) {
               var6.remove(var8);
            }

            var6.schedule(var8, var4, new FunctionCallback(var7));
            var0.sendSuccess(new TranslatableComponent("commands.schedule.created.function", new Object[]{var7, var2, var4}), true);
         }).ifRight((var6x) -> {
            ResourceLocation var7 = var6x.getId();
            String var8 = "#" + var7.toString();
            if (var3) {
               var6.remove(var8);
            }

            var6.schedule(var8, var4, new FunctionTagCallback(var7));
            var0.sendSuccess(new TranslatableComponent("commands.schedule.created.tag", new Object[]{var7, var2, var4}), true);
         });
         return (int)Math.floorMod(var4, 2147483647L);
      }
   }

   private static int remove(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      int var2 = var0.getLevel().getLevelData().getScheduledEvents().remove(var1);
      if (var2 == 0) {
         throw ERROR_CANT_REMOVE.create(var1);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.schedule.cleared.success", new Object[]{var2, var1}), true);
         return var2;
      }
   }
}
