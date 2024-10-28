package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class KickCommand {
   private static final SimpleCommandExceptionType ERROR_KICKING_OWNER = new SimpleCommandExceptionType(Component.translatable("commands.kick.owner.failed"));
   private static final SimpleCommandExceptionType ERROR_SINGLEPLAYER = new SimpleCommandExceptionType(Component.translatable("commands.kick.singleplayer.failed"));

   public KickCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kick").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var0x) -> {
         return kickPlayers((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), Component.translatable("multiplayer.disconnect.kicked"));
      })).then(Commands.argument("reason", MessageArgument.message()).executes((var0x) -> {
         return kickPlayers((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), MessageArgument.getMessage(var0x, "reason"));
      }))));
   }

   private static int kickPlayers(CommandSourceStack var0, Collection<ServerPlayer> var1, Component var2) throws CommandSyntaxException {
      if (!var0.getServer().isPublished()) {
         throw ERROR_SINGLEPLAYER.create();
      } else {
         int var3 = 0;
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            ServerPlayer var5 = (ServerPlayer)var4.next();
            if (!var0.getServer().isSingleplayerOwner(var5.getGameProfile())) {
               var5.connection.disconnect(var2);
               var0.sendSuccess(() -> {
                  return Component.translatable("commands.kick.success", var5.getDisplayName(), var2);
               }, true);
               ++var3;
            }
         }

         if (var3 == 0) {
            throw ERROR_KICKING_OWNER.create();
         } else {
            return var3;
         }
      }
   }
}
