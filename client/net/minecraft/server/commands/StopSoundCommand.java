package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
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
      RequiredArgumentBuilder var1 = (RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var0x) -> {
         return stopSound((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), (SoundSource)null, (ResourceLocation)null);
      })).then(Commands.literal("*").then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((var0x) -> {
         return stopSound((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), (SoundSource)null, ResourceLocationArgument.getId(var0x, "sound"));
      })));
      SoundSource[] var2 = SoundSource.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundSource var5 = var2[var4];
         var1.then(((LiteralArgumentBuilder)Commands.literal(var5.getName()).executes((var1x) -> {
            return stopSound((CommandSourceStack)var1x.getSource(), EntityArgument.getPlayers(var1x, "targets"), var5, (ResourceLocation)null);
         })).then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((var1x) -> {
            return stopSound((CommandSourceStack)var1x.getSource(), EntityArgument.getPlayers(var1x, "targets"), var5, ResourceLocationArgument.getId(var1x, "sound"));
         })));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stopsound").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(var1));
   }

   private static int stopSound(CommandSourceStack var0, Collection<ServerPlayer> var1, @Nullable SoundSource var2, @Nullable ResourceLocation var3) {
      ClientboundStopSoundPacket var4 = new ClientboundStopSoundPacket(var3, var2);
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         ServerPlayer var6 = (ServerPlayer)var5.next();
         var6.connection.send(var4);
      }

      if (var2 != null) {
         if (var3 != null) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.stopsound.success.source.sound", Component.translationArg(var3), var2.getName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.stopsound.success.source.any", var2.getName());
            }, true);
         }
      } else if (var3 != null) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.stopsound.success.sourceless.sound", Component.translationArg(var3));
         }, true);
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.stopsound.success.sourceless.any");
         }, true);
      }

      return var1.size();
   }
}
