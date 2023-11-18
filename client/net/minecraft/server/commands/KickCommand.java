package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class KickCommand {
   public KickCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kick").requires(var0x -> var0x.hasPermission(3)))
            .then(
               ((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players())
                     .executes(
                        var0x -> kickPlayers(
                              (CommandSourceStack)var0x.getSource(),
                              EntityArgument.getPlayers(var0x, "targets"),
                              Component.translatable("multiplayer.disconnect.kicked")
                           )
                     ))
                  .then(
                     Commands.argument("reason", MessageArgument.message())
                        .executes(
                           var0x -> kickPlayers(
                                 (CommandSourceStack)var0x.getSource(),
                                 EntityArgument.getPlayers(var0x, "targets"),
                                 MessageArgument.getMessage(var0x, "reason")
                              )
                        )
                  )
            )
      );
   }

   private static int kickPlayers(CommandSourceStack var0, Collection<ServerPlayer> var1, Component var2) {
      for(ServerPlayer var4 : var1) {
         var4.connection.disconnect(var2);
         var0.sendSuccess(() -> Component.translatable("commands.kick.success", var4.getDisplayName(), var2), true);
      }

      return var1.size();
   }
}
