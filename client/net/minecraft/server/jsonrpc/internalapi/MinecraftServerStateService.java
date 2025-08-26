package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;

public class MinecraftServerStateService {
   private final DedicatedServer server;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftServerStateService(DedicatedServer var1, JsonRpcLogger var2) {
      super();
      this.server = var1;
      this.jsonrpcLogger = var2;
   }

   public boolean isReady() {
      return this.server.isReady();
   }

   public boolean saveEverything(boolean var1, boolean var2, boolean var3, ClientInfo var4) {
      this.jsonrpcLogger.log(var4, "Save everything. SuppressLogs: {}, flush: {}, force: {}", var1, var2, var3);
      return this.server.saveEverything(var1, var2, var3);
   }

   public void halt(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Halt server. WaitForShutdown: {}", var1);
      this.server.halt(var1);
   }

   public void sendSystemMessage(Component var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Send system message: '{}'", var1.getString());
      this.server.sendSystemMessage(var1);
   }

   public void sendSystemMessage(Component var1, boolean var2, Collection<ServerPlayer> var3, ClientInfo var4) {
      List var5 = var3.stream().map((var0) -> var0.getName().getString()).toList();
      this.jsonrpcLogger.log(var4, "Send system message to '{}' players (overlay: {}): '{}'", var5.size(), var2, var1.getString());

      for(ServerPlayer var7 : var3) {
         if (var2) {
            var7.sendSystemMessage(var1, true);
         } else {
            var7.sendSystemMessage(var1);
         }
      }

   }

   public void broadcastSystemMessage(Component var1, boolean var2, ClientInfo var3) {
      this.jsonrpcLogger.log(var3, "Broadcast system message (overlay: {}): '{}'", var2, var1.getString());

      for(ServerPlayer var5 : this.server.getPlayerList().getPlayers()) {
         if (var2) {
            var5.sendSystemMessage(var1, true);
         } else {
            var5.sendSystemMessage(var1);
         }
      }

   }
}
