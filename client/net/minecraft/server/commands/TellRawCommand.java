package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

public class TellRawCommand {
   public TellRawCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", ComponentArgument.textComponent(var1)).executes((var0x) -> {
         int var1 = 0;

         for(Iterator var2 = EntityArgument.getPlayers(var0x, "targets").iterator(); var2.hasNext(); ++var1) {
            ServerPlayer var3 = (ServerPlayer)var2.next();
            var3.sendSystemMessage(ComponentUtils.updateForEntity((CommandSourceStack)var0x.getSource(), (Component)ComponentArgument.getComponent(var0x, "message"), var3, 0), false);
         }

         return var1;
      }))));
   }
}
