package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;

public class TitleCommand {
   public TitleCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("title").requires(var0x -> var0x.hasPermission(2)))
            .then(
               ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                    "targets", EntityArgument.players()
                                 )
                                 .then(
                                    Commands.literal("clear")
                                       .executes(var0x -> clearTitle((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets")))
                                 ))
                              .then(
                                 Commands.literal("reset")
                                    .executes(var0x -> resetTitle((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets")))
                              ))
                           .then(
                              Commands.literal("title")
                                 .then(
                                    Commands.argument("title", ComponentArgument.textComponent())
                                       .executes(
                                          var0x -> showTitle(
                                                (CommandSourceStack)var0x.getSource(),
                                                EntityArgument.getPlayers(var0x, "targets"),
                                                ComponentArgument.getComponent(var0x, "title"),
                                                "title",
                                                ClientboundSetTitleTextPacket::new
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("subtitle")
                              .then(
                                 Commands.argument("title", ComponentArgument.textComponent())
                                    .executes(
                                       var0x -> showTitle(
                                             (CommandSourceStack)var0x.getSource(),
                                             EntityArgument.getPlayers(var0x, "targets"),
                                             ComponentArgument.getComponent(var0x, "title"),
                                             "subtitle",
                                             ClientboundSetSubtitleTextPacket::new
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("actionbar")
                           .then(
                              Commands.argument("title", ComponentArgument.textComponent())
                                 .executes(
                                    var0x -> showTitle(
                                          (CommandSourceStack)var0x.getSource(),
                                          EntityArgument.getPlayers(var0x, "targets"),
                                          ComponentArgument.getComponent(var0x, "title"),
                                          "actionbar",
                                          ClientboundSetActionBarTextPacket::new
                                       )
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("times")
                        .then(
                           Commands.argument("fadeIn", TimeArgument.time())
                              .then(
                                 Commands.argument("stay", TimeArgument.time())
                                    .then(
                                       Commands.argument("fadeOut", TimeArgument.time())
                                          .executes(
                                             var0x -> setTimes(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   EntityArgument.getPlayers(var0x, "targets"),
                                                   IntegerArgumentType.getInteger(var0x, "fadeIn"),
                                                   IntegerArgumentType.getInteger(var0x, "stay"),
                                                   IntegerArgumentType.getInteger(var0x, "fadeOut")
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int clearTitle(CommandSourceStack var0, Collection<ServerPlayer> var1) {
      ClientboundClearTitlesPacket var2 = new ClientboundClearTitlesPacket(false);

      for(ServerPlayer var4 : var1) {
         var4.connection.send(var2);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.title.cleared.single", ((ServerPlayer)var1.iterator().next()).getDisplayName()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.title.cleared.multiple", var1.size()), true);
      }

      return var1.size();
   }

   private static int resetTitle(CommandSourceStack var0, Collection<ServerPlayer> var1) {
      ClientboundClearTitlesPacket var2 = new ClientboundClearTitlesPacket(true);

      for(ServerPlayer var4 : var1) {
         var4.connection.send(var2);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.title.reset.single", ((ServerPlayer)var1.iterator().next()).getDisplayName()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.title.reset.multiple", var1.size()), true);
      }

      return var1.size();
   }

   private static int showTitle(CommandSourceStack var0, Collection<ServerPlayer> var1, Component var2, String var3, Function<Component, Packet<?>> var4) throws CommandSyntaxException {
      for(ServerPlayer var6 : var1) {
         var6.connection.send((Packet<?>)var4.apply(ComponentUtils.updateForEntity(var0, var2, var6, 0)));
      }

      if (var1.size() == 1) {
         var0.sendSuccess(
            () -> Component.translatable("commands.title.show." + var3 + ".single", ((ServerPlayer)var1.iterator().next()).getDisplayName()), true
         );
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.title.show." + var3 + ".multiple", var1.size()), true);
      }

      return var1.size();
   }

   private static int setTimes(CommandSourceStack var0, Collection<ServerPlayer> var1, int var2, int var3, int var4) {
      ClientboundSetTitlesAnimationPacket var5 = new ClientboundSetTitlesAnimationPacket(var2, var3, var4);

      for(ServerPlayer var7 : var1) {
         var7.connection.send(var5);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.title.times.single", ((ServerPlayer)var1.iterator().next()).getDisplayName()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.title.times.multiple", var1.size()), true);
      }

      return var1.size();
   }
}
