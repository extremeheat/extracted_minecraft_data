package net.minecraft.client.server;

import java.net.SocketAddress;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;

public class IntegratedPlayerList extends PlayerList {
   public IntegratedPlayerList(final IntegratedServer server, final LayeredRegistryAccess<RegistryLayer> registryHolder, final PlayerDataStorage playerDataStorage) {
      super(server, registryHolder, playerDataStorage, server.notificationManager());
      this.setViewDistance(10);
   }

   public Component canPlayerLogin(final SocketAddress address, final NameAndId nameAndId) {
      return (Component)(this.getServer().isSingleplayerOwner(nameAndId) && this.getPlayerByName(nameAndId.name()) != null ? Component.translatable("multiplayer.disconnect.name_taken") : super.canPlayerLogin(address, nameAndId));
   }

   public void placeNewPlayer(final Connection connection, final ServerPlayer player, final CommonListenerCookie cookie) {
      super.placeNewPlayer(connection, player, cookie);
      this.getServer().onPlayerListChanged();
   }

   public void remove(final ServerPlayer player) {
      super.remove(player);
      this.getServer().onPlayerListChanged();
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }
}
