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

   public MinecraftOperatorListServiceImpl(MinecraftServer var1, JsonRpcLogger var2) {
      super();
      this.minecraftServer = var1;
      this.jsonrpcLogger = var2;
   }

   public Collection<ServerOpListEntry> getEntries() {
      return this.minecraftServer.getPlayerList().getOps().getEntries();
   }

   public void op(NameAndId var1, Optional<PermissionLevel> var2, Optional<Boolean> var3, ClientInfo var4) {
      this.jsonrpcLogger.log(var4, "Op '{}'", var1);
      this.minecraftServer.getPlayerList().op(var1, var2.map(LevelBasedPermissionSet::forLevel), var3);
   }

   public void op(NameAndId var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Op '{}'", var1);
      this.minecraftServer.getPlayerList().op(var1);
   }

   public void deop(NameAndId var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Deop '{}'", var1);
      this.minecraftServer.getPlayerList().deop(var1);
   }

   public void clear(ClientInfo var1) {
      this.jsonrpcLogger.log(var1, "Clear operator list");
      this.minecraftServer.getPlayerList().getOps().clear();
   }
}
