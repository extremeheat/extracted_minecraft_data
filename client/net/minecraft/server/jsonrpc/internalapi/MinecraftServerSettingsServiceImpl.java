package net.minecraft.server.jsonrpc.internalapi;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class MinecraftServerSettingsServiceImpl implements MinecraftServerSettingsService {
   private final DedicatedServer server;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftServerSettingsServiceImpl(DedicatedServer var1, JsonRpcLogger var2) {
      super();
      this.server = var1;
      this.jsonrpcLogger = var2;
   }

   public boolean isAutoSave() {
      return this.server.isAutoSave();
   }

   public boolean setAutoSave(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update autosave from {} to {}", this.isAutoSave(), var1);
      this.server.setAutoSave(var1);
      return this.isAutoSave();
   }

   public Difficulty getDifficulty() {
      return this.server.getWorldData().getDifficulty();
   }

   public Difficulty setDifficulty(Difficulty var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update difficulty from '{}' to '{}'", this.getDifficulty(), var1);
      this.server.setDifficulty(var1);
      return this.getDifficulty();
   }

   public boolean isEnforceWhitelist() {
      return this.server.isEnforceWhitelist();
   }

   public boolean setEnforceWhitelist(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update enforce allowlist from {} to {}", this.isEnforceWhitelist(), var1);
      this.server.setEnforceWhitelist(var1);
      this.server.kickUnlistedPlayers();
      return this.isEnforceWhitelist();
   }

   public boolean isUsingWhitelist() {
      return this.server.isUsingWhitelist();
   }

   public boolean setUsingWhitelist(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update using allowlist from {} to {}", this.isUsingWhitelist(), var1);
      this.server.setUsingWhitelist(var1);
      this.server.kickUnlistedPlayers();
      return this.isUsingWhitelist();
   }

   public int getMaxPlayers() {
      return this.server.getMaxPlayers();
   }

   public int setMaxPlayers(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update max players from {} to {}", this.getMaxPlayers(), var1);
      this.server.setMaxPlayers(var1);
      return this.getMaxPlayers();
   }

   public int getPauseWhenEmptySeconds() {
      return this.server.pauseWhenEmptySeconds();
   }

   public int setPauseWhenEmptySeconds(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update pause when empty from {} seconds to {} seconds", this.getPauseWhenEmptySeconds(), var1);
      this.server.setPauseWhenEmptySeconds(var1);
      return this.getPauseWhenEmptySeconds();
   }

   public int getPlayerIdleTimeout() {
      return this.server.playerIdleTimeout();
   }

   public int setPlayerIdleTimeout(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update player idle timeout from {} minutes to {} minutes", this.getPlayerIdleTimeout(), var1);
      this.server.setPlayerIdleTimeout(var1);
      return this.getPlayerIdleTimeout();
   }

   public boolean allowFlight() {
      return this.server.allowFlight();
   }

   public boolean setAllowFlight(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update allow flight from {} to {}", this.allowFlight(), var1);
      this.server.setAllowFlight(var1);
      return this.allowFlight();
   }

   public int getSpawnProtectionRadius() {
      return this.server.spawnProtectionRadius();
   }

   public int setSpawnProtectionRadius(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update spawn protection radius from {} to {}", this.getSpawnProtectionRadius(), var1);
      this.server.setSpawnProtectionRadius(var1);
      return this.getSpawnProtectionRadius();
   }

   public String getMotd() {
      return this.server.getMotd();
   }

   public String setMotd(String var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update MOTD from '{}' to '{}'", this.getMotd(), var1);
      this.server.setMotd(var1);
      return this.getMotd();
   }

   public boolean forceGameMode() {
      return this.server.forceGameMode();
   }

   public boolean setForceGameMode(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update force game mode from {} to {}", this.forceGameMode(), var1);
      this.server.setForceGameMode(var1);
      return this.forceGameMode();
   }

   public GameType getGameMode() {
      return this.server.gameMode();
   }

   public GameType setGameMode(GameType var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update game mode from '{}' to '{}'", this.getGameMode(), var1);
      this.server.setGameMode(var1);
      return this.getGameMode();
   }

   public int getViewDistance() {
      return this.server.viewDistance();
   }

   public int setViewDistance(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update view distance from {} to {}", this.getViewDistance(), var1);
      this.server.setViewDistance(var1);
      return this.getViewDistance();
   }

   public int getSimulationDistance() {
      return this.server.simulationDistance();
   }

   public int setSimulationDistance(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update simulation distance from {} to {}", this.getSimulationDistance(), var1);
      this.server.setSimulationDistance(var1);
      return this.getSimulationDistance();
   }

   public boolean acceptsTransfers() {
      return this.server.acceptsTransfers();
   }

   public boolean setAcceptsTransfers(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update accepts transfers from {} to {}", this.acceptsTransfers(), var1);
      this.server.setAcceptsTransfers(var1);
      return this.acceptsTransfers();
   }

   public int getStatusHeartbeatInterval() {
      return this.server.statusHeartbeatInterval();
   }

   public int setStatusHeartbeatInterval(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update status heartbeat interval from {} to {}", this.getStatusHeartbeatInterval(), var1);
      this.server.setStatusHeartbeatInterval(var1);
      return this.getStatusHeartbeatInterval();
   }

   public LevelBasedPermissionSet getOperatorUserPermissions() {
      return this.server.operatorUserPermissions();
   }

   public LevelBasedPermissionSet setOperatorUserPermissions(LevelBasedPermissionSet var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update operator user permission level from {} to {}", this.getOperatorUserPermissions(), var1.level());
      this.server.setOperatorUserPermissions(var1);
      return this.getOperatorUserPermissions();
   }

   public boolean hidesOnlinePlayers() {
      return this.server.hidesOnlinePlayers();
   }

   public boolean setHidesOnlinePlayers(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update hides online players from {} to {}", this.hidesOnlinePlayers(), var1);
      this.server.setHidesOnlinePlayers(var1);
      return this.hidesOnlinePlayers();
   }

   public boolean repliesToStatus() {
      return this.server.repliesToStatus();
   }

   public boolean setRepliesToStatus(boolean var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update replies to status from {} to {}", this.repliesToStatus(), var1);
      this.server.setRepliesToStatus(var1);
      return this.repliesToStatus();
   }

   public int getEntityBroadcastRangePercentage() {
      return this.server.entityBroadcastRangePercentage();
   }

   public int setEntityBroadcastRangePercentage(int var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Update entity broadcast range percentage from {}% to {}%", this.getEntityBroadcastRangePercentage(), var1);
      this.server.setEntityBroadcastRangePercentage(var1);
      return this.getEntityBroadcastRangePercentage();
   }
}
