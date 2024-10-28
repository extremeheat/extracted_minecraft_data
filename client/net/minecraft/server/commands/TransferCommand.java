package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.server.level.ServerPlayer;

public class TransferCommand {
   private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(Component.translatable("commands.transfer.error.no_players"));

   public TransferCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("transfer").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("hostname", StringArgumentType.string()).executes((var0x) -> {
         return transfer((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "hostname"), 25565, List.of(((CommandSourceStack)var0x.getSource()).getPlayerOrException()));
      })).then(((RequiredArgumentBuilder)Commands.argument("port", IntegerArgumentType.integer(1, 65535)).executes((var0x) -> {
         return transfer((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "hostname"), IntegerArgumentType.getInteger(var0x, "port"), List.of(((CommandSourceStack)var0x.getSource()).getPlayerOrException()));
      })).then(Commands.argument("players", EntityArgument.players()).executes((var0x) -> {
         return transfer((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "hostname"), IntegerArgumentType.getInteger(var0x, "port"), EntityArgument.getPlayers(var0x, "players"));
      })))));
   }

   private static int transfer(CommandSourceStack var0, String var1, int var2, Collection<ServerPlayer> var3) throws CommandSyntaxException {
      if (var3.isEmpty()) {
         throw ERROR_NO_PLAYERS.create();
      } else {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            ServerPlayer var5 = (ServerPlayer)var4.next();
            var5.connection.send(new ClientboundTransferPacket(var1, var2));
         }

         if (var3.size() == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.transfer.success.single", ((ServerPlayer)var3.iterator().next()).getDisplayName(), var1, var2);
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.transfer.success.multiple", var3.size(), var1, var2);
            }, true);
         }

         return var3.size();
      }
   }
}
