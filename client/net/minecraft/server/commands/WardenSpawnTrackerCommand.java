package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.Player;

public class WardenSpawnTrackerCommand {
   public WardenSpawnTrackerCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("warden_spawn_tracker").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("clear").executes((var0x) -> {
         return resetTracker((CommandSourceStack)var0x.getSource(), ImmutableList.of(((CommandSourceStack)var0x.getSource()).getPlayerOrException()));
      }))).then(Commands.literal("set").then(Commands.argument("warning_level", IntegerArgumentType.integer(0, 4)).executes((var0x) -> {
         return setWarningLevel((CommandSourceStack)var0x.getSource(), ImmutableList.of(((CommandSourceStack)var0x.getSource()).getPlayerOrException()), IntegerArgumentType.getInteger(var0x, "warning_level"));
      }))));
   }

   private static int setWarningLevel(CommandSourceStack var0, Collection<? extends Player> var1, int var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Player var4 = (Player)var3.next();
         var4.getWardenSpawnTracker().ifPresent((var1x) -> {
            var1x.setWarningLevel(var2);
         });
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.set.success.single", ((Player)var1.iterator().next()).getDisplayName());
         }, true);
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.set.success.multiple", var1.size());
         }, true);
      }

      return var1.size();
   }

   private static int resetTracker(CommandSourceStack var0, Collection<? extends Player> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Player var3 = (Player)var2.next();
         var3.getWardenSpawnTracker().ifPresent(WardenSpawnTracker::reset);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.clear.success.single", ((Player)var1.iterator().next()).getDisplayName());
         }, true);
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.clear.success.multiple", var1.size());
         }, true);
      }

      return var1.size();
   }
}
