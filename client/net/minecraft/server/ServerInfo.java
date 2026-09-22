package net.minecraft.server;

import net.minecraft.network.Connection;
import net.minecraft.network.ServerConnectionDetails;

public interface ServerInfo {
   String getMotd();

   String getServerVersion();

   int getPlayerCount();

   int getMaxPlayers();

   boolean acceptsConnection(Connection connection, ServerConnectionDetails details);
}
