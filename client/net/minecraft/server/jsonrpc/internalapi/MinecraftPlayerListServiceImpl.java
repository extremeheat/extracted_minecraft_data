package net.minecraft.server.jsonrpc.internalapi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.notifications.NotificationManager;
import net.minecraft.server.players.NameAndId;
import org.jspecify.annotations.Nullable;

public class MinecraftPlayerListServiceImpl implements MinecraftPlayerListService {
   private final NotificationManager notificationManager;
   private final JsonRpcLogger jsonRpcLogger;

   public MinecraftPlayerListServiceImpl(final NotificationManager notificationManager, final JsonRpcLogger jsonRpcLogger) {
      super();
      this.notificationManager = notificationManager;
      this.jsonRpcLogger = jsonRpcLogger;
   }

   private DedicatedServer server() {
      return (DedicatedServer)Objects.requireNonNull(this.notificationManager.server());
   }

   public List<ServerPlayer> getPlayers() {
      return this.server().getPlayerList().getPlayers();
   }

   public @Nullable ServerPlayer getPlayer(final UUID uuid) {
      return this.server().getPlayerList().getPlayer(uuid);
   }

   public Optional<NameAndId> fetchUserByName(final String name) {
      return this.server().services().nameToIdCache().get(name);
   }

   public Optional<NameAndId> fetchUserById(final UUID id) {
      return Optional.ofNullable(this.server().services().sessionService().fetchProfile(id, true)).map((profile) -> new NameAndId(profile.profile()));
   }

   public Optional<NameAndId> getCachedUserById(final UUID id) {
      return this.server().services().nameToIdCache().get(id);
   }

   public Optional<ServerPlayer> getPlayer(final Optional<UUID> id, final Optional<String> name) {
      if (id.isPresent()) {
         return Optional.ofNullable(this.server().getPlayerList().getPlayer((UUID)id.get()));
      } else {
         return name.isPresent() ? Optional.ofNullable(this.server().getPlayerList().getPlayerByName((String)name.get())) : Optional.empty();
      }
   }

   public List<ServerPlayer> getPlayersWithAddress(final String ip) {
      return this.server().getPlayerList().getPlayersWithAddress(ip);
   }

   public void remove(final ServerPlayer serverPlayer, final ClientInfo clientInfo) {
      this.server().getPlayerList().remove(serverPlayer);
      this.jsonRpcLogger.log(clientInfo, "Remove player '{}'", serverPlayer.getPlainTextName());
   }

   public @Nullable ServerPlayer getPlayerByName(final String name) {
      return this.server().getPlayerList().getPlayerByName(name);
   }
}
