package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

public class MsgCommand {
   public MsgCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register(
         (LiteralArgumentBuilder)Commands.literal("msg")
            .then(
               Commands.argument("targets", EntityArgument.players())
                  .then(
                     Commands.argument("message", MessageArgument.message())
                        .executes(
                           var0x -> sendMessage(
                                 (CommandSourceStack)var0x.getSource(),
                                 EntityArgument.getPlayers(var0x, "targets"),
                                 MessageArgument.getChatMessage(var0x, "message")
                              )
                        )
                  )
            )
      );
      var0.register((LiteralArgumentBuilder)Commands.literal("tell").redirect(var1));
      var0.register((LiteralArgumentBuilder)Commands.literal("w").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack var0, Collection<ServerPlayer> var1, MessageArgument.ChatMessage var2) {
      if (var1.isEmpty()) {
         return 0;
      } else {
         var2.resolve(var0)
            .thenAcceptAsync(
               var2x -> {
                  Component var3 = var2x.raw().serverContent();
      
                  for(ServerPlayer var5 : var1) {
                     var0.sendSuccess(
                        Component.translatable("commands.message.display.outgoing", var5.getDisplayName(), var3)
                           .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
                        false
                     );
                     PlayerChatMessage var6 = var2x.filter(var0, var5);
                     if (var6 != null) {
                        var5.sendChatMessage(var6, var0.asChatSender(), ChatType.MSG_COMMAND);
                     }
                  }
               },
               var0.getServer()
            );
         return var1.size();
      }
   }
}
