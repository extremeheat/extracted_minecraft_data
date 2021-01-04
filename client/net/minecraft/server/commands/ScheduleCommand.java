package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType(new TranslatableComponent("commands.schedule.same_tick", new Object[0]));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(Commands.argument("time", TimeArgument.time()).executes((var0x) -> {
         return schedule((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctionOrTag(var0x, "function"), IntegerArgumentType.getInteger(var0x, "time"));
      })))));
   }

   private static int schedule(CommandSourceStack var0, Either<CommandFunction, Tag<CommandFunction>> var1, int var2) throws CommandSyntaxException {
      if (var2 == 0) {
         throw ERROR_SAME_TICK.create();
      } else {
         long var3 = var0.getLevel().getGameTime() + (long)var2;
         var1.ifLeft((var4) -> {
            ResourceLocation var5 = var4.getId();
            var0.getLevel().getLevelData().getScheduledEvents().reschedule(var5.toString(), var3, new FunctionCallback(var5));
            var0.sendSuccess(new TranslatableComponent("commands.schedule.created.function", new Object[]{var5, var2, var3}), true);
         }).ifRight((var4) -> {
            ResourceLocation var5 = var4.getId();
            var0.getLevel().getLevelData().getScheduledEvents().reschedule("#" + var5.toString(), var3, new FunctionTagCallback(var5));
            var0.sendSuccess(new TranslatableComponent("commands.schedule.created.tag", new Object[]{var5, var2, var3}), true);
         });
         return (int)Math.floorMod(var3, 2147483647L);
      }
   }
}
