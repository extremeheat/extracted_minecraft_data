package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class StopSoundCommand {
   public StopSoundCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      RequiredArgumentBuilder var1 = (RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players())
            .executes(var0x -> stopSound((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), null, null)))
         .then(
            Commands.literal("*")
               .then(
                  Commands.argument("sound", ResourceLocationArgument.id())
                     .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                     .executes(
                        var0x -> stopSound(
                              (CommandSourceStack)var0x.getSource(),
                              EntityArgument.getPlayers(var0x, "targets"),
                              null,
                              ResourceLocationArgument.getId(var0x, "sound")
                           )
                     )
               )
         );

      for(SoundSource var5 : SoundSource.values()) {
         var1.then(
            ((LiteralArgumentBuilder)Commands.literal(var5.getName())
                  .executes(var1x -> stopSound((CommandSourceStack)var1x.getSource(), EntityArgument.getPlayers(var1x, "targets"), var5, null)))
               .then(
                  Commands.argument("sound", ResourceLocationArgument.id())
                     .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                     .executes(
                        var1x -> stopSound(
                              (CommandSourceStack)var1x.getSource(),
                              EntityArgument.getPlayers(var1x, "targets"),
                              var5,
                              ResourceLocationArgument.getId(var1x, "sound")
                           )
                     )
               )
         );
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stopsound").requires(var0x -> var0x.hasPermission(2))).then(var1));
   }

   private static int stopSound(CommandSourceStack var0, Collection<ServerPlayer> var1, @Nullable SoundSource var2, @Nullable ResourceLocation var3) {
      ClientboundStopSoundPacket var4 = new ClientboundStopSoundPacket(var3, var2);

      for(ServerPlayer var6 : var1) {
         var6.connection.send(var4);
      }

      if (var2 != null) {
         if (var3 != null) {
            var0.sendSuccess(() -> Component.translatable("commands.stopsound.success.source.sound", var3, var2.getName()), true);
         } else {
            var0.sendSuccess(() -> Component.translatable("commands.stopsound.success.source.any", var2.getName()), true);
         }
      } else if (var3 != null) {
         var0.sendSuccess(() -> Component.translatable("commands.stopsound.success.sourceless.sound", var3), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.stopsound.success.sourceless.any"), true);
      }

      return var1.size();
   }
}
