package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;

public class ServerPackCommand {
   public ServerPackCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("serverpack").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("push").then(((RequiredArgumentBuilder)Commands.argument("url", StringArgumentType.string()).then(((RequiredArgumentBuilder)Commands.argument("uuid", UuidArgument.uuid()).then(Commands.argument("hash", StringArgumentType.word()).executes((var0x) -> {
         return pushPack((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "url"), Optional.of(UuidArgument.getUuid(var0x, "uuid")), Optional.of(StringArgumentType.getString(var0x, "hash")));
      }))).executes((var0x) -> {
         return pushPack((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "url"), Optional.of(UuidArgument.getUuid(var0x, "uuid")), Optional.empty());
      }))).executes((var0x) -> {
         return pushPack((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "url"), Optional.empty(), Optional.empty());
      })))).then(Commands.literal("pop").then(Commands.argument("uuid", UuidArgument.uuid()).executes((var0x) -> {
         return popPack((CommandSourceStack)var0x.getSource(), UuidArgument.getUuid(var0x, "uuid"));
      }))));
   }

   private static void sendToAllConnections(CommandSourceStack var0, Packet<?> var1) {
      var0.getServer().getConnection().getConnections().forEach((var1x) -> {
         var1x.send(var1);
      });
   }

   private static int pushPack(CommandSourceStack var0, String var1, Optional<UUID> var2, Optional<String> var3) {
      UUID var4 = (UUID)var2.orElseGet(() -> {
         return UUID.nameUUIDFromBytes(var1.getBytes(StandardCharsets.UTF_8));
      });
      String var5 = (String)var3.orElse("");
      ClientboundResourcePackPushPacket var6 = new ClientboundResourcePackPushPacket(var4, var1, var5, false, (Optional)null);
      sendToAllConnections(var0, var6);
      return 0;
   }

   private static int popPack(CommandSourceStack var0, UUID var1) {
      ClientboundResourcePackPopPacket var2 = new ClientboundResourcePackPopPacket(Optional.of(var1));
      sendToAllConnections(var0, var2);
      return 0;
   }
}
