package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.level.ServerPlayer;

public class ServerStateService {
   public ServerStateService() {
      super();
   }

   public static ServerState status(MinecraftApi var0) {
      return !var0.serverStateService().isReady() ? ServerStateService.ServerState.NOT_STARTED : new ServerState(true, PlayerService.get(var0), ServerStatus.Version.current());
   }

   public static boolean save(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverStateService().saveEverything(true, var1, true, var2);
   }

   public static boolean stop(MinecraftApi var0, ClientInfo var1) {
      var0.submit((Runnable)(() -> var0.serverStateService().halt(false, var1)));
      return true;
   }

   public static boolean systemMessage(MinecraftApi var0, SystemMessage var1, ClientInfo var2) {
      Message var3 = var1.message();
      MutableComponent var4;
      if (var3.translatable().isPresent()) {
         if (var3.translatableParams().isPresent() && !((List)var3.translatableParams().get()).isEmpty()) {
            var4 = Component.translatable((String)var3.translatable().get(), ((List)var3.translatableParams().get()).toArray());
         } else {
            var4 = Component.translatable((String)var3.translatable().get());
         }
      } else {
         if (!var3.literal().isPresent()) {
            return false;
         }

         var4 = Component.literal((String)var3.literal().get());
      }

      if (var1.receivingPlayers().isPresent()) {
         if (((List)var1.receivingPlayers().get()).isEmpty()) {
            return false;
         }

         for(PlayerDto var6 : (List)var1.receivingPlayers().get()) {
            ServerPlayer var7;
            if (var6.id().isPresent()) {
               var7 = var0.playerListService().getPlayer((UUID)var6.id().get());
            } else {
               if (!var6.name().isPresent()) {
                  continue;
               }

               var7 = var0.playerListService().getPlayerByName((String)var6.name().get());
            }

            if (var7 != null) {
               var7.sendSystemMessage(var4, var1.overlay());
            }
         }
      } else {
         var0.serverStateService().broadcastSystemMessage(var4, var1.overlay(), var2);
      }

      return true;
   }

   public static record ServerState(boolean started, List<PlayerDto> players, ServerStatus.Version version) {
      public static final Codec<ServerState> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.fieldOf("started").forGetter(ServerState::started), PlayerDto.CODEC.codec().listOf().lenientOptionalFieldOf("players", List.of()).forGetter(ServerState::players), ServerStatus.Version.CODEC.fieldOf("version").forGetter(ServerState::version)).apply(var0, ServerState::new));
      public static final ServerState NOT_STARTED = new ServerState(false, List.of(), ServerStatus.Version.current());

      public ServerState(boolean var1, List<PlayerDto> var2, ServerStatus.Version var3) {
         super();
         this.started = var1;
         this.players = var2;
         this.version = var3;
      }
   }

   public static record SystemMessage(Message message, boolean overlay, Optional<List<PlayerDto>> receivingPlayers) {
      public static final Codec<SystemMessage> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Message.CODEC.fieldOf("message").forGetter(SystemMessage::message), Codec.BOOL.fieldOf("overlay").forGetter(SystemMessage::overlay), PlayerDto.CODEC.codec().listOf().lenientOptionalFieldOf("receivingPlayers").forGetter(SystemMessage::receivingPlayers)).apply(var0, SystemMessage::new));

      public SystemMessage(Message var1, boolean var2, Optional<List<PlayerDto>> var3) {
         super();
         this.message = var1;
         this.overlay = var2;
         this.receivingPlayers = var3;
      }
   }
}
