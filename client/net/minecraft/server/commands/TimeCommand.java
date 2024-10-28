package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class TimeCommand {
   public TimeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("time").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("set").then(Commands.literal("day").executes((var0x) -> {
         return setTime((CommandSourceStack)var0x.getSource(), 1000);
      }))).then(Commands.literal("noon").executes((var0x) -> {
         return setTime((CommandSourceStack)var0x.getSource(), 6000);
      }))).then(Commands.literal("night").executes((var0x) -> {
         return setTime((CommandSourceStack)var0x.getSource(), 13000);
      }))).then(Commands.literal("midnight").executes((var0x) -> {
         return setTime((CommandSourceStack)var0x.getSource(), 18000);
      }))).then(Commands.argument("time", TimeArgument.time()).executes((var0x) -> {
         return setTime((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time"));
      })))).then(Commands.literal("add").then(Commands.argument("time", TimeArgument.time()).executes((var0x) -> {
         return addTime((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("query").then(Commands.literal("daytime").executes((var0x) -> {
         return queryTime((CommandSourceStack)var0x.getSource(), getDayTime(((CommandSourceStack)var0x.getSource()).getLevel()));
      }))).then(Commands.literal("gametime").executes((var0x) -> {
         return queryTime((CommandSourceStack)var0x.getSource(), (int)(((CommandSourceStack)var0x.getSource()).getLevel().getGameTime() % 2147483647L));
      }))).then(Commands.literal("day").executes((var0x) -> {
         return queryTime((CommandSourceStack)var0x.getSource(), (int)(((CommandSourceStack)var0x.getSource()).getLevel().getDayTime() / 24000L % 2147483647L));
      }))));
   }

   private static int getDayTime(ServerLevel var0) {
      return (int)(var0.getDayTime() % 24000L);
   }

   private static int queryTime(CommandSourceStack var0, int var1) {
      var0.sendSuccess(() -> {
         return Component.translatable("commands.time.query", var1);
      }, false);
      return var1;
   }

   public static int setTime(CommandSourceStack var0, int var1) {
      Iterator var2 = var0.getServer().getAllLevels().iterator();

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
         var3.setDayTime((long)var1);
      }

      var0.sendSuccess(() -> {
         return Component.translatable("commands.time.set", var1);
      }, true);
      return getDayTime(var0.getLevel());
   }

   public static int addTime(CommandSourceStack var0, int var1) {
      Iterator var2 = var0.getServer().getAllLevels().iterator();

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
         var3.setDayTime(var3.getDayTime() + (long)var1);
      }

      int var4 = getDayTime(var0.getLevel());
      var0.sendSuccess(() -> {
         return Component.translatable("commands.time.set", var4);
      }, true);
      return var4;
   }
}
