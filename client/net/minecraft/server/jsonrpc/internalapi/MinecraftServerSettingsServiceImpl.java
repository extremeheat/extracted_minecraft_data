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

   public MinecraftServerSettingsServiceImpl(final DedicatedServer server, final JsonRpcLogger jsonrpcLogger) {
      super();
      this.server = server;
      this.jsonrpcLogger = jsonrpcLogger;
   }

   public boolean isAutoSave() {
      return this.server.isAutoSave();
   }

   public boolean setAutoSave(final boolean enabled, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update autosave from {} to {}", this.isAutoSave(), enabled);
      this.server.setAutoSave(enabled);
      return this.isAutoSave();
   }

   public Difficulty getDifficulty() {
      return this.server.getWorldData().getDifficulty();
   }

   public Difficulty setDifficulty(final Difficulty difficulty, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update difficulty from '{}' to '{}'", this.getDifficulty(), difficulty);
      this.server.setDifficulty(difficulty);
      return this.getDifficulty();
   }

   public boolean isEnforceWhitelist() {
      return this.server.isEnforceWhitelist();
   }

   public boolean setEnforceWhitelist(final boolean enforce, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update enforce allowlist from {} to {}", this.isEnforceWhitelist(), enforce);
      this.server.setEnforceWhitelist(enforce);
      this.server.kickUnlistedPlayers();
      return this.isEnforceWhitelist();
   }

   public boolean isUsingWhitelist() {
      return this.server.isUsingWhitelist();
   }

   public boolean setUsingWhitelist(final boolean use, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update using allowlist from {} to {}", this.isUsingWhitelist(), use);
      this.server.setUsingWhitelist(use);
      this.server.kickUnlistedPlayers();
      return this.isUsingWhitelist();
   }

   public int getMaxPlayers() {
      return this.server.getMaxPlayers();
   }

   public int setMaxPlayers(final int maxPlayers, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update max players from {} to {}", this.getMaxPlayers(), maxPlayers);
      this.server.setMaxPlayers(maxPlayers);
      return this.getMaxPlayers();
   }

   public int getPauseWhenEmptySeconds() {
      return this.server.pauseWhenEmptySeconds();
   }

   public int setPauseWhenEmptySeconds(final int emptySeconds, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update pause when empty from {} seconds to {} seconds", this.getPauseWhenEmptySeconds(), emptySeconds);
      this.server.setPauseWhenEmptySeconds(emptySeconds);
      return this.getPauseWhenEmptySeconds();
   }

   public int getPlayerIdleTimeout() {
      return this.server.playerIdleTimeout();
   }

   public int setPlayerIdleTimeout(final int idleTime, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update player idle timeout from {} minutes to {} minutes", this.getPlayerIdleTimeout(), idleTime);
      this.server.setPlayerIdleTimeout(idleTime);
      return this.getPlayerIdleTimeout();
   }

   public boolean allowFlight() {
      return this.server.allowFlight();
   }

   public boolean setAllowFlight(final boolean allow, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update allow flight from {} to {}", this.allowFlight(), allow);
      this.server.setAllowFlight(allow);
      return this.allowFlight();
   }

   public int getSpawnProtectionRadius() {
      return this.server.spawnProtectionRadius();
   }

   public int setSpawnProtectionRadius(final int spawnProtection, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update spawn protection radius from {} to {}", this.getSpawnProtectionRadius(), spawnProtection);
      this.server.setSpawnProtectionRadius(spawnProtection);
      return this.getSpawnProtectionRadius();
   }

   public String getMotd() {
      return this.server.getMotd();
   }

   public String setMotd(final String motd, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update MOTD from '{}' to '{}'", this.getMotd(), motd);
      this.server.setMotd(motd);
      return this.getMotd();
   }

   public boolean forceGameMode() {
      return this.server.forceGameMode();
   }

   public boolean setForceGameMode(final boolean force, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update force game mode from {} to {}", this.forceGameMode(), force);
      this.server.setForceGameMode(force);
      return this.forceGameMode();
   }

   public GameType getGameMode() {
      return this.server.gameMode();
   }

   public GameType setGameMode(final GameType gameMode, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update game mode from '{}' to '{}'", this.getGameMode(), gameMode);
      this.server.setGameMode(gameMode);
      return this.getGameMode();
   }

   public int getViewDistance() {
      return this.server.viewDistance();
   }

   public int setViewDistance(final int viewDistance, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update view distance from {} to {}", this.getViewDistance(), viewDistance);
      this.server.setViewDistance(viewDistance);
      return this.getViewDistance();
   }

   public int getSimulationDistance() {
      return this.server.simulationDistance();
   }

   public int setSimulationDistance(final int simulationDistance, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update simulation distance from {} to {}", this.getSimulationDistance(), simulationDistance);
      this.server.setSimulationDistance(simulationDistance);
      return this.getSimulationDistance();
   }

   public boolean acceptsTransfers() {
      return this.server.acceptsTransfers();
   }

   public boolean setAcceptsTransfers(final boolean accept, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update accepts transfers from {} to {}", this.acceptsTransfers(), accept);
      this.server.setAcceptsTransfers(accept);
      return this.acceptsTransfers();
   }

   public int getStatusHeartbeatInterval() {
      return this.server.statusHeartbeatInterval();
   }

   public int setStatusHeartbeatInterval(final int statusHeartbeatInterval, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update status heartbeat interval from {} to {}", this.getStatusHeartbeatInterval(), statusHeartbeatInterval);
      this.server.setStatusHeartbeatInterval(statusHeartbeatInterval);
      return this.getStatusHeartbeatInterval();
   }

   public LevelBasedPermissionSet getOperatorUserPermissions() {
      return this.server.operatorUserPermissions();
   }

   public LevelBasedPermissionSet setOperatorUserPermissions(final LevelBasedPermissionSet permissions, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update operator user permission level from {} to {}", this.getOperatorUserPermissions(), permissions.level());
      this.server.setOperatorUserPermissions(permissions);
      return this.getOperatorUserPermissions();
   }

   public boolean hidesOnlinePlayers() {
      return this.server.hidesOnlinePlayers();
   }

   public boolean setHidesOnlinePlayers(final boolean hide, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update hides online players from {} to {}", this.hidesOnlinePlayers(), hide);
      this.server.setHidesOnlinePlayers(hide);
      return this.hidesOnlinePlayers();
   }

   public boolean repliesToStatus() {
      return this.server.repliesToStatus();
   }

   public boolean setRepliesToStatus(final boolean enable, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update replies to status from {} to {}", this.repliesToStatus(), enable);
      this.server.setRepliesToStatus(enable);
      return this.repliesToStatus();
   }

   public int getEntityBroadcastRangePercentage() {
      return this.server.entityBroadcastRangePercentage();
   }

   public int setEntityBroadcastRangePercentage(final int percentage, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Update entity broadcast range percentage from {}% to {}%", this.getEntityBroadcastRangePercentage(), percentage);
      this.server.setEntityBroadcastRangePercentage(percentage);
      return this.getEntityBroadcastRangePercentage();
   }
}
