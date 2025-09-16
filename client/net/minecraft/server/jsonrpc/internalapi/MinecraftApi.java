package net.minecraft.server.jsonrpc.internalapi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.notifications.NotificationManager;

public class MinecraftApi {
   private final NotificationManager notificationManager;
   private final MinecraftAllowListService allowListService;
   private final MinecraftBanListService banListService;
   private final MinecraftPlayerListService minecraftPlayerListService;
   private final MinecraftGameRuleService gameRuleService;
   private final MinecraftOperatorListService minecraftOperatorListService;
   private final MinecraftServerSettingsService minecraftServerSettingsService;
   private final MinecraftServerStateService minecraftServerStateService;
   private final MinecraftExecutorService executorService;

   public MinecraftApi(NotificationManager var1, MinecraftAllowListService var2, MinecraftBanListService var3, MinecraftPlayerListService var4, MinecraftGameRuleService var5, MinecraftOperatorListService var6, MinecraftServerSettingsService var7, MinecraftServerStateService var8, MinecraftExecutorService var9) {
      super();
      this.notificationManager = var1;
      this.allowListService = var2;
      this.banListService = var3;
      this.minecraftPlayerListService = var4;
      this.gameRuleService = var5;
      this.minecraftOperatorListService = var6;
      this.minecraftServerSettingsService = var7;
      this.minecraftServerStateService = var8;
      this.executorService = var9;
   }

   public <V> CompletableFuture<V> submit(Supplier<V> var1) {
      return this.executorService.submit(var1);
   }

   public CompletableFuture<Void> submit(Runnable var1) {
      return this.executorService.submit(var1);
   }

   public MinecraftAllowListService allowListService() {
      return this.allowListService;
   }

   public MinecraftBanListService banListService() {
      return this.banListService;
   }

   public MinecraftPlayerListService playerListService() {
      return this.minecraftPlayerListService;
   }

   public MinecraftGameRuleService gameRuleService() {
      return this.gameRuleService;
   }

   public MinecraftOperatorListService operatorListService() {
      return this.minecraftOperatorListService;
   }

   public MinecraftServerSettingsService serverSettingsService() {
      return this.minecraftServerSettingsService;
   }

   public MinecraftServerStateService serverStateService() {
      return this.minecraftServerStateService;
   }

   public NotificationManager notificationManager() {
      return this.notificationManager;
   }

   public static MinecraftApi of(DedicatedServer var0) {
      JsonRpcLogger var1 = new JsonRpcLogger();
      MinecraftAllowListServiceImpl var2 = new MinecraftAllowListServiceImpl(var0, var1);
      MinecraftBanListServiceImpl var3 = new MinecraftBanListServiceImpl(var0, var1);
      MinecraftPlayerListServiceImpl var4 = new MinecraftPlayerListServiceImpl(var0, var1);
      MinecraftGameRuleServiceImpl var5 = new MinecraftGameRuleServiceImpl(var0, var1);
      MinecraftOperatorListServiceImpl var6 = new MinecraftOperatorListServiceImpl(var0, var1);
      MinecraftServerSettingsServiceImpl var7 = new MinecraftServerSettingsServiceImpl(var0, var1);
      MinecraftServerStateServiceImpl var8 = new MinecraftServerStateServiceImpl(var0, var1);
      MinecraftExecutorServiceImpl var9 = new MinecraftExecutorServiceImpl(var0);
      return new MinecraftApi(var0.notificationManager(), var2, var3, var4, var5, var6, var7, var8, var9);
   }
}
