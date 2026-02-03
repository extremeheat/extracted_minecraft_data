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

   public MinecraftApi(final NotificationManager notificationManager, final MinecraftAllowListService allowListService, final MinecraftBanListService banListService, final MinecraftPlayerListService minecraftPlayerListService, final MinecraftGameRuleService gameRuleService, final MinecraftOperatorListService minecraftOperatorListService, final MinecraftServerSettingsService minecraftServerSettingsService, final MinecraftServerStateService minecraftServerStateService, final MinecraftExecutorService executorService) {
      super();
      this.notificationManager = notificationManager;
      this.allowListService = allowListService;
      this.banListService = banListService;
      this.minecraftPlayerListService = minecraftPlayerListService;
      this.gameRuleService = gameRuleService;
      this.minecraftOperatorListService = minecraftOperatorListService;
      this.minecraftServerSettingsService = minecraftServerSettingsService;
      this.minecraftServerStateService = minecraftServerStateService;
      this.executorService = executorService;
   }

   public <V> CompletableFuture<V> submit(final Supplier<V> supplier) {
      return this.executorService.submit(supplier);
   }

   public CompletableFuture<Void> submit(final Runnable runnable) {
      return this.executorService.submit(runnable);
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

   public static MinecraftApi of(final DedicatedServer server) {
      JsonRpcLogger jsonrpcLogger = new JsonRpcLogger();
      MinecraftAllowListServiceImpl allowListService = new MinecraftAllowListServiceImpl(server, jsonrpcLogger);
      MinecraftBanListServiceImpl banListService = new MinecraftBanListServiceImpl(server, jsonrpcLogger);
      MinecraftPlayerListServiceImpl playerListService = new MinecraftPlayerListServiceImpl(server, jsonrpcLogger);
      MinecraftGameRuleServiceImpl gameRuleService = new MinecraftGameRuleServiceImpl(server, jsonrpcLogger);
      MinecraftOperatorListServiceImpl operatorListService = new MinecraftOperatorListServiceImpl(server, jsonrpcLogger);
      MinecraftServerSettingsServiceImpl serverSettingsService = new MinecraftServerSettingsServiceImpl(server, jsonrpcLogger);
      MinecraftServerStateServiceImpl serverStateService = new MinecraftServerStateServiceImpl(server, jsonrpcLogger);
      MinecraftExecutorService executorService = new MinecraftExecutorServiceImpl(server);
      return new MinecraftApi(server.notificationManager(), allowListService, banListService, playerListService, gameRuleService, operatorListService, serverSettingsService, serverStateService, executorService);
   }
}
