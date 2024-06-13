package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class DebugConfigCommand {
   public DebugConfigCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debugconfig").requires(var0x -> var0x.hasPermission(3)))
               .then(
                  Commands.literal("config")
                     .then(
                        Commands.argument("target", EntityArgument.player())
                           .executes(var0x -> config((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayer(var0x, "target")))
                     )
               ))
            .then(
               Commands.literal("unconfig")
                  .then(
                     Commands.argument("target", UuidArgument.uuid())
                        .suggests(
                           (var0x, var1) -> SharedSuggestionProvider.suggest(getUuidsInConfig(((CommandSourceStack)var0x.getSource()).getServer()), var1)
                        )
                        .executes(var0x -> unconfig((CommandSourceStack)var0x.getSource(), UuidArgument.getUuid(var0x, "target")))
                  )
            )
      );
   }

   private static Iterable<String> getUuidsInConfig(MinecraftServer var0) {
      HashSet var1 = new HashSet();

      for (Connection var3 : var0.getConnection().getConnections()) {
         if (var3.getPacketListener() instanceof ServerConfigurationPacketListenerImpl var4) {
            var1.add(var4.getOwner().getId().toString());
         }
      }

      return var1;
   }

   private static int config(CommandSourceStack var0, ServerPlayer var1) {
      GameProfile var2 = var1.getGameProfile();
      var1.connection.switchToConfig();
      var0.sendSuccess(() -> Component.literal("Switched player " + var2.getName() + "(" + var2.getId() + ") to config mode"), false);
      return 1;
   }

   private static int unconfig(CommandSourceStack var0, UUID var1) {
      for (Connection var3 : var0.getServer().getConnection().getConnections()) {
         PacketListener var5 = var3.getPacketListener();
         if (var5 instanceof ServerConfigurationPacketListenerImpl) {
            ServerConfigurationPacketListenerImpl var4 = (ServerConfigurationPacketListenerImpl)var5;
            if (var4.getOwner().getId().equals(var1)) {
               var4.returnToWorld();
            }
         }
      }

      var0.sendFailure(Component.literal("Can't find player to unconfig"));
      return 0;
   }
}
