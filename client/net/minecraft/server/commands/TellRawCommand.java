package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

public class TellRawCommand {
   public TellRawCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("targets", EntityArgument.players())
                  .then(
                     Commands.argument("message", ComponentArgument.textComponent())
                        .executes(
                           var0x -> {
                              int var1 = 0;
                     
                              for(ServerPlayer var3 : EntityArgument.getPlayers(var0x, "targets")) {
                                 var3.sendSystemMessage(
                                    ComponentUtils.updateForEntity(
                                       (CommandSourceStack)var0x.getSource(), ComponentArgument.getComponent(var0x, "message"), var3, 0
                                    ),
                                    false
                                 );
                                 ++var1;
                              }
                     
                              return var1;
                           }
                        )
                  )
            )
      );
   }
}
