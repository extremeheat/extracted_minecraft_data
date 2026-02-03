package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.level.ServerPlayer;

public class ServerStateService {
   public ServerStateService() {
      super();
   }

   public static ServerState status(final MinecraftApi minecraftApi) {
      return !minecraftApi.serverStateService().isReady() ? ServerStateService.ServerState.NOT_STARTED : new ServerState(true, PlayerService.get(minecraftApi), ServerStatus.Version.current());
   }

   public static boolean save(final MinecraftApi minecraftApi, final boolean flush, final ClientInfo clientInfo) {
      return minecraftApi.serverStateService().saveEverything(true, flush, true, clientInfo);
   }

   public static boolean stop(final MinecraftApi minecraftApi, final ClientInfo clientInfo) {
      minecraftApi.submit((Runnable)(() -> minecraftApi.serverStateService().halt(false, clientInfo)));
      return true;
   }

   public static boolean systemMessage(final MinecraftApi minecraftApi, final SystemMessage systemMessage, final ClientInfo clientInfo) {
      Component component = (Component)systemMessage.message().asComponent().orElse((Object)null);
      if (component == null) {
         return false;
      } else {
         if (systemMessage.receivingPlayers().isPresent()) {
            if (((List)systemMessage.receivingPlayers().get()).isEmpty()) {
               return false;
            }

            for(PlayerDto playerDto : (List)systemMessage.receivingPlayers().get()) {
               ServerPlayer player;
               if (playerDto.id().isPresent()) {
                  player = minecraftApi.playerListService().getPlayer((UUID)playerDto.id().get());
               } else {
                  if (!playerDto.name().isPresent()) {
                     continue;
                  }

                  player = minecraftApi.playerListService().getPlayerByName((String)playerDto.name().get());
               }

               if (player != null) {
                  player.sendSystemMessage(component, systemMessage.overlay());
               }
            }
         } else {
            minecraftApi.serverStateService().broadcastSystemMessage(component, systemMessage.overlay(), clientInfo);
         }

         return true;
      }
   }

   public static record ServerState(boolean started, List<PlayerDto> players, ServerStatus.Version version) {
      public static final Codec<ServerState> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.BOOL.fieldOf("started").forGetter(ServerState::started), PlayerDto.CODEC.codec().listOf().lenientOptionalFieldOf("players", List.of()).forGetter(ServerState::players), ServerStatus.Version.CODEC.fieldOf("version").forGetter(ServerState::version)).apply(i, ServerState::new));
      public static final ServerState NOT_STARTED = new ServerState(false, List.of(), ServerStatus.Version.current());

      public ServerState {
         super();
      }
   }

   public static record SystemMessage(Message message, boolean overlay, Optional<List<PlayerDto>> receivingPlayers) {
      public static final Codec<SystemMessage> CODEC = RecordCodecBuilder.create((i) -> i.group(Message.CODEC.fieldOf("message").forGetter(SystemMessage::message), Codec.BOOL.fieldOf("overlay").forGetter(SystemMessage::overlay), PlayerDto.CODEC.codec().listOf().lenientOptionalFieldOf("receivingPlayers").forGetter(SystemMessage::receivingPlayers)).apply(i, SystemMessage::new));

      public SystemMessage {
         super();
      }
   }
}
