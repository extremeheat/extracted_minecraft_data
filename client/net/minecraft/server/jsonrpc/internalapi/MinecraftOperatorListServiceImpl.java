package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;

public class MinecraftOperatorListServiceImpl implements MinecraftOperatorListService {
   private final MinecraftServer minecraftServer;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftOperatorListServiceImpl(final MinecraftServer minecraftServer, final JsonRpcLogger jsonrpcLogger) {
      super();
      this.minecraftServer = minecraftServer;
      this.jsonrpcLogger = jsonrpcLogger;
   }

   public Collection<ServerOpListEntry> getEntries() {
      return this.minecraftServer.getPlayerList().getOps().getEntries();
   }

   public void op(final NameAndId nameAndId, final Optional<PermissionLevel> permissionLevel, final Optional<Boolean> canBypassPlayerLimit, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Op '{}'", nameAndId);
      this.minecraftServer.getPlayerList().op(nameAndId, permissionLevel.map(LevelBasedPermissionSet::forLevel), canBypassPlayerLimit);
   }

   public void op(final NameAndId nameAndId, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Op '{}'", nameAndId);
      this.minecraftServer.getPlayerList().op(nameAndId);
   }

   public void deop(final NameAndId nameAndId, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Deop '{}'", nameAndId);
      this.minecraftServer.getPlayerList().deop(nameAndId);
   }

   public void clear(final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Clear operator list");
      this.minecraftServer.getPlayerList().getOps().clear();
   }
}
