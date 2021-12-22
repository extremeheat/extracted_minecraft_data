package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EmoteCommands {
   public EmoteCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((var0x) -> {
         String var1 = StringArgumentType.getString(var0x, "action");
         Entity var2 = ((CommandSourceStack)var0x.getSource()).getEntity();
         MinecraftServer var3 = ((CommandSourceStack)var0x.getSource()).getServer();
         if (var2 != null) {
            if (var2 instanceof ServerPlayer) {
               ServerPlayer var4 = (ServerPlayer)var2;
               var4.getTextFilter().processStreamMessage(var1).thenAcceptAsync((var4x) -> {
                  String var5 = var4x.getFiltered();
                  Component var6 = var5.isEmpty() ? null : createMessage(var0x, var5);
                  Component var7 = createMessage(var0x, var4x.getRaw());
                  var3.getPlayerList().broadcastMessage(var7, (var3x) -> {
                     return var4.shouldFilterMessageTo(var3x) ? var6 : var7;
                  }, ChatType.CHAT, var2.getUUID());
               }, var3);
               return 1;
            }

            var3.getPlayerList().broadcastMessage(createMessage(var0x, var1), ChatType.CHAT, var2.getUUID());
         } else {
            var3.getPlayerList().broadcastMessage(createMessage(var0x, var1), ChatType.SYSTEM, Util.NIL_UUID);
         }

         return 1;
      })));
   }

   private static Component createMessage(CommandContext<CommandSourceStack> var0, String var1) {
      return new TranslatableComponent("chat.type.emote", new Object[]{((CommandSourceStack)var0.getSource()).getDisplayName(), var1});
   }
}
