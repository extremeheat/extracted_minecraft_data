package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.HashSet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class DebugConfigCommand {
   public DebugConfigCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debugconfig").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))).then(Commands.literal("config").then(Commands.argument("target", EntityArgument.player()).executes((var0x) -> config((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayer(var0x, "target")))))).then(Commands.literal("unconfig").then(Commands.argument("target", UuidArgument.uuid()).suggests((var0x, var1x) -> SharedSuggestionProvider.suggest(getUuidsInConfig(((CommandSourceStack)var0x.getSource()).getServer()), var1x)).executes((var0x) -> unconfig((CommandSourceStack)var0x.getSource(), UuidArgument.getUuid(var0x, "target")))))).then(Commands.literal("dialog").then(Commands.argument("target", UuidArgument.uuid()).suggests((var0x, var1x) -> SharedSuggestionProvider.suggest(getUuidsInConfig(((CommandSourceStack)var0x.getSource()).getServer()), var1x)).then(Commands.argument("dialog", ResourceOrIdArgument.dialog(var1)).executes((var0x) -> showDialog((CommandSourceStack)var0x.getSource(), UuidArgument.getUuid(var0x, "target"), ResourceOrIdArgument.getDialog(var0x, "dialog")))))));
   }

   private static Iterable<String> getUuidsInConfig(MinecraftServer var0) {
      HashSet var1 = new HashSet();

      for(Connection var3 : var0.getConnection().getConnections()) {
         PacketListener var5 = var3.getPacketListener();
         if (var5 instanceof ServerConfigurationPacketListenerImpl var4) {
            var1.add(var4.getOwner().id().toString());
         }
      }

      return var1;
   }

   private static int config(CommandSourceStack var0, ServerPlayer var1) {
      GameProfile var2 = var1.getGameProfile();
      var1.connection.switchToConfig();
      var0.sendSuccess(() -> {
         String var10000 = var2.name();
         return Component.literal("Switched player " + var10000 + "(" + String.valueOf(var2.id()) + ") to config mode");
      }, false);
      return 1;
   }

   @Nullable
   private static ServerConfigurationPacketListenerImpl findConfigPlayer(MinecraftServer var0, UUID var1) {
      for(Connection var3 : var0.getConnection().getConnections()) {
         PacketListener var5 = var3.getPacketListener();
         if (var5 instanceof ServerConfigurationPacketListenerImpl var4) {
            if (var4.getOwner().id().equals(var1)) {
               return var4;
            }
         }
      }

      return null;
   }

   private static int unconfig(CommandSourceStack var0, UUID var1) {
      ServerConfigurationPacketListenerImpl var2 = findConfigPlayer(var0.getServer(), var1);
      if (var2 != null) {
         var2.returnToWorld();
         return 1;
      } else {
         var0.sendFailure(Component.literal("Can't find player to unconfig"));
         return 0;
      }
   }

   private static int showDialog(CommandSourceStack var0, UUID var1, Holder<Dialog> var2) {
      ServerConfigurationPacketListenerImpl var3 = findConfigPlayer(var0.getServer(), var1);
      if (var3 != null) {
         var3.send(new ClientboundShowDialogPacket(var2));
         return 1;
      } else {
         var0.sendFailure(Component.literal("Can't find player to talk to"));
         return 0;
      }
   }
}
