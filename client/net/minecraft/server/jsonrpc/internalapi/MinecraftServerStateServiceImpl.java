package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class MinecraftServerStateServiceImpl implements MinecraftServerStateService {
   private final DedicatedServer server;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftServerStateServiceImpl(final DedicatedServer server, final JsonRpcLogger jsonrpcLogger) {
      super();
      this.server = server;
      this.jsonrpcLogger = jsonrpcLogger;
   }

   public boolean isReady() {
      return this.server.isReady();
   }

   public boolean saveEverything(final boolean suppressLogs, final boolean flush, final boolean force, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Save everything. SuppressLogs: {}, flush: {}, force: {}", suppressLogs, flush, force);
      return this.server.saveEverything(suppressLogs, flush, force);
   }

   public void halt(final boolean waitForShutdown, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Halt server. WaitForShutdown: {}", waitForShutdown);
      this.server.halt(waitForShutdown);
   }

   public void sendSystemMessage(final Component message, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Send system message: '{}'", message.getString());
      this.server.sendSystemMessage(message);
   }

   public void sendSystemMessage(final Component message, final boolean overlay, final Collection<ServerPlayer> players, final ClientInfo clientInfo) {
      List<String> playerNames = players.stream().map(Player::getPlainTextName).toList();
      this.jsonrpcLogger.log(clientInfo, "Send system message to '{}' players (overlay: {}): '{}'", playerNames.size(), overlay, message.getString());

      for(ServerPlayer player : players) {
         if (overlay) {
            player.sendOverlayMessage(message);
         } else {
            player.sendSystemMessage(message);
         }
      }

   }

   public void broadcastSystemMessage(final Component message, final boolean overlay, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Broadcast system message (overlay: {}): '{}'", overlay, message.getString());

      for(ServerPlayer player : this.server.getPlayerList().getPlayers()) {
         if (overlay) {
            player.sendOverlayMessage(message);
         } else {
            player.sendSystemMessage(message);
         }
      }

   }
}
