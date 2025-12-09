package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public class PlayerService {
   private static final Component DEFAULT_KICK_MESSAGE = Component.translatable("multiplayer.disconnect.kicked");

   public PlayerService() {
      super();
   }

   public static List<PlayerDto> get(MinecraftApi var0) {
      return var0.playerListService().getPlayers().stream().map(PlayerDto::from).toList();
   }

   public static List<PlayerDto> kick(MinecraftApi var0, List<KickDto> var1, ClientInfo var2) {
      ArrayList var3 = new ArrayList();

      for(KickDto var5 : var1) {
         ServerPlayer var6 = getServerPlayer(var0, var5.player());
         if (var6 != null) {
            var0.playerListService().remove(var6, var2);
            var6.connection.disconnect((Component)var5.message.flatMap(Message::asComponent).orElse(DEFAULT_KICK_MESSAGE));
            var3.add(var5.player());
         }
      }

      return var3;
   }

   private static @Nullable ServerPlayer getServerPlayer(MinecraftApi var0, PlayerDto var1) {
      if (var1.id().isPresent()) {
         return var0.playerListService().getPlayer((UUID)var1.id().get());
      } else {
         return var1.name().isPresent() ? var0.playerListService().getPlayerByName((String)var1.name().get()) : null;
      }
   }

   public static record KickDto(PlayerDto player, Optional<Message> message) {
      final Optional<Message> message;
      public static final MapCodec<KickDto> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PlayerDto.CODEC.codec().fieldOf("player").forGetter(KickDto::player), Message.CODEC.optionalFieldOf("message").forGetter(KickDto::message)).apply(var0, KickDto::new));

      public KickDto(PlayerDto var1, Optional<Message> var2) {
         super();
         this.player = var1;
         this.message = var2;
      }
   }
}
