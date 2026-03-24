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

   public static List<PlayerDto> get(final MinecraftApi minecraftApi) {
      return minecraftApi.playerListService().getPlayers().stream().map(PlayerDto::from).toList();
   }

   public static List<PlayerDto> kick(final MinecraftApi minecraftApi, final List<KickDto> kick, final ClientInfo clientInfo) {
      List<PlayerDto> kicked = new ArrayList();

      for(KickDto kickDto : kick) {
         ServerPlayer serverPlayer = getServerPlayer(minecraftApi, kickDto.player());
         if (serverPlayer != null) {
            minecraftApi.playerListService().remove(serverPlayer, clientInfo);
            serverPlayer.connection.disconnect((Component)kickDto.message.flatMap(Message::asComponent).orElse(DEFAULT_KICK_MESSAGE));
            kicked.add(kickDto.player());
         }
      }

      return kicked;
   }

   private static @Nullable ServerPlayer getServerPlayer(final MinecraftApi minecraftApi, final PlayerDto playerDto) {
      if (playerDto.id().isPresent()) {
         return minecraftApi.playerListService().getPlayer((UUID)playerDto.id().get());
      } else {
         return playerDto.name().isPresent() ? minecraftApi.playerListService().getPlayerByName((String)playerDto.name().get()) : null;
      }
   }

   public static record KickDto(PlayerDto player, Optional<Message> message) {
      public static final MapCodec<KickDto> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(PlayerDto.CODEC.codec().fieldOf("player").forGetter(KickDto::player), Message.CODEC.optionalFieldOf("message").forGetter(KickDto::message)).apply(i, KickDto::new));

      public KickDto {
         super();
      }
   }
}
