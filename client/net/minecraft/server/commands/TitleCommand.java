package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.server.level.ServerPlayer;

public class TitleCommand {
   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("title").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("clear").executes((var0x) -> {
         return clearTitle((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"));
      }))).then(Commands.literal("reset").executes((var0x) -> {
         return resetTitle((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"));
      }))).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.textComponent()).executes((var0x) -> {
         return showTitle((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), ComponentArgument.getComponent(var0x, "title"), ClientboundSetTitlesPacket.Type.TITLE);
      })))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.textComponent()).executes((var0x) -> {
         return showTitle((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), ComponentArgument.getComponent(var0x, "title"), ClientboundSetTitlesPacket.Type.SUBTITLE);
      })))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.textComponent()).executes((var0x) -> {
         return showTitle((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), ComponentArgument.getComponent(var0x, "title"), ClientboundSetTitlesPacket.Type.ACTIONBAR);
      })))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then(Commands.argument("stay", IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setTimes((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "fadeIn"), IntegerArgumentType.getInteger(var0x, "stay"), IntegerArgumentType.getInteger(var0x, "fadeOut"));
      })))))));
   }

   private static int clearTitle(CommandSourceStack var0, Collection<ServerPlayer> var1) {
      ClientboundSetTitlesPacket var2 = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.CLEAR, (Component)null);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         var4.connection.send(var2);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.title.cleared.single", new Object[]{((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.title.cleared.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int resetTitle(CommandSourceStack var0, Collection<ServerPlayer> var1) {
      ClientboundSetTitlesPacket var2 = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.RESET, (Component)null);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         var4.connection.send(var2);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.title.reset.single", new Object[]{((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.title.reset.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int showTitle(CommandSourceStack var0, Collection<ServerPlayer> var1, Component var2, ClientboundSetTitlesPacket.Type var3) throws CommandSyntaxException {
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         var5.connection.send(new ClientboundSetTitlesPacket(var3, ComponentUtils.updateForEntity(var0, var2, var5, 0)));
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.title.show." + var3.name().toLowerCase(Locale.ROOT) + ".single", new Object[]{((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.title.show." + var3.name().toLowerCase(Locale.ROOT) + ".multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int setTimes(CommandSourceStack var0, Collection<ServerPlayer> var1, int var2, int var3, int var4) {
      ClientboundSetTitlesPacket var5 = new ClientboundSetTitlesPacket(var2, var3, var4);
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         ServerPlayer var7 = (ServerPlayer)var6.next();
         var7.connection.send(var5);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.title.times.single", new Object[]{((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.title.times.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }
}
