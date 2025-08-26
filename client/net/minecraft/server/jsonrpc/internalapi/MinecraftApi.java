package net.minecraft.server.jsonrpc.internalapi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.server.dedicated.DedicatedServer;

public class MinecraftApi {
   private final MinecraftAllowListService allowListService;
   private final MinecraftBanListService banListService;
   private final MinecraftPlayerListService playerListService;
   private final MinecraftGameRuleService gameRuleService;
   private final MinecraftOperatorListService operatorListService;
   private final MinecraftServerSettingsService serverSettingsService;
   private final MinecraftServerStateService serverStateService;
   private final DedicatedServer server;

   public MinecraftApi(MinecraftAllowListService var1, MinecraftBanListService var2, MinecraftPlayerListService var3, MinecraftGameRuleService var4, MinecraftOperatorListService var5, MinecraftServerSettingsService var6, MinecraftServerStateService var7, DedicatedServer var8) {
      super();
      this.allowListService = var1;
      this.banListService = var2;
      this.playerListService = var3;
      this.gameRuleService = var4;
      this.operatorListService = var5;
      this.serverSettingsService = var6;
      this.serverStateService = var7;
      this.server = var8;
   }

   public <V> CompletableFuture<V> submit(Supplier<V> var1) {
      return this.server.submit(var1);
   }

   public CompletableFuture<Void> submit(Runnable var1) {
      return this.server.submit(var1);
   }

   public MinecraftAllowListService allowListService() {
      return this.allowListService;
   }

   public MinecraftBanListService banListService() {
      return this.banListService;
   }

   public MinecraftPlayerListService playerListService() {
      return this.playerListService;
   }

   public MinecraftGameRuleService gameRuleService() {
      return this.gameRuleService;
   }

   public MinecraftOperatorListService operatorListService() {
      return this.operatorListService;
   }

   public MinecraftServerSettingsService serverSettingsService() {
      return this.serverSettingsService;
   }

   public MinecraftServerStateService serverStateService() {
      return this.serverStateService;
   }
}
