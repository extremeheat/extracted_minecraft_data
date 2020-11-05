package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class MsgCommand {
   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((var0x) -> {
         return sendMessage((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), MessageArgument.getMessage(var0x, "message"));
      }))));
      var0.register((LiteralArgumentBuilder)Commands.literal("tell").redirect(var1));
      var0.register((LiteralArgumentBuilder)Commands.literal("w").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack var0, Collection<ServerPlayer> var1, Component var2) {
      UUID var3 = var0.getEntity() == null ? Util.NIL_UUID : var0.getEntity().getUUID();
      Entity var5 = var0.getEntity();
      Consumer var4;
      if (var5 instanceof ServerPlayer) {
         ServerPlayer var6 = (ServerPlayer)var5;
         var4 = (var2x) -> {
            var6.sendMessage((new TranslatableComponent("commands.message.display.outgoing", new Object[]{var2x, var2})).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), var6.getUUID());
         };
      } else {
         var4 = (var2x) -> {
            var0.sendSuccess((new TranslatableComponent("commands.message.display.outgoing", new Object[]{var2x, var2})).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), false);
         };
      }

      Iterator var8 = var1.iterator();

      while(var8.hasNext()) {
         ServerPlayer var7 = (ServerPlayer)var8.next();
         var4.accept(var7.getDisplayName());
         var7.sendMessage((new TranslatableComponent("commands.message.display.incoming", new Object[]{var0.getDisplayName(), var2})).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), var3);
      }

      return var1.size();
   }
}
