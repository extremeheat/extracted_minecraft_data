package net.minecraft.server.jsonrpc.methods;

import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class ServerSettingsService {
   public ServerSettingsService() {
      super();
   }

   public static boolean autosave(MinecraftApi var0) {
      return var0.serverSettingsService().isAutoSave();
   }

   public static boolean setAutosave(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setAutoSave(var1, var2);
   }

   public static Difficulty difficulty(MinecraftApi var0) {
      return var0.serverSettingsService().getDifficulty();
   }

   public static Difficulty setDifficulty(MinecraftApi var0, Difficulty var1, ClientInfo var2) {
      return var0.serverSettingsService().setDifficulty(var1, var2);
   }

   public static boolean enforceAllowlist(MinecraftApi var0) {
      return var0.serverSettingsService().isEnforceWhitelist();
   }

   public static boolean setEnforceAllowlist(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setEnforceWhitelist(var1, var2);
   }

   public static boolean usingAllowlist(MinecraftApi var0) {
      return var0.serverSettingsService().isUsingWhitelist();
   }

   public static boolean setUsingAllowlist(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setUsingWhitelist(var1, var2);
   }

   public static int maxPlayers(MinecraftApi var0) {
      return var0.serverSettingsService().getMaxPlayers();
   }

   public static int setMaxPlayers(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setMaxPlayers(var1, var2);
   }

   public static int pauseWhenEmpty(MinecraftApi var0) {
      return var0.serverSettingsService().getPauseWhenEmptySeconds();
   }

   public static int setPauseWhenEmpty(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setPauseWhenEmptySeconds(var1, var2);
   }

   public static int playerIdleTimeout(MinecraftApi var0) {
      return var0.serverSettingsService().getPlayerIdleTimeout();
   }

   public static int setPlayerIdleTimeout(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setPlayerIdleTimeout(var1, var2);
   }

   public static boolean allowFlight(MinecraftApi var0) {
      return var0.serverSettingsService().allowFlight();
   }

   public static boolean setAllowFlight(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setAllowFlight(var1, var2);
   }

   public static int spawnProtection(MinecraftApi var0) {
      return var0.serverSettingsService().getSpawnProtectionRadius();
   }

   public static int setSpawnProtection(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setSpawnProtectionRadius(var1, var2);
   }

   public static String motd(MinecraftApi var0) {
      return var0.serverSettingsService().getMotd();
   }

   public static String setMotd(MinecraftApi var0, String var1, ClientInfo var2) {
      return var0.serverSettingsService().setMotd(var1, var2);
   }

   public static boolean forceGameMode(MinecraftApi var0) {
      return var0.serverSettingsService().forceGameMode();
   }

   public static boolean setForceGameMode(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setForceGameMode(var1, var2);
   }

   public static GameType gameMode(MinecraftApi var0) {
      return var0.serverSettingsService().getGameMode();
   }

   public static GameType setGameMode(MinecraftApi var0, GameType var1, ClientInfo var2) {
      return var0.serverSettingsService().setGameMode(var1, var2);
   }

   public static int viewDistance(MinecraftApi var0) {
      return var0.serverSettingsService().getViewDistance();
   }

   public static int setViewDistance(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setViewDistance(var1, var2);
   }

   public static int simulationDistance(MinecraftApi var0) {
      return var0.serverSettingsService().getSimulationDistance();
   }

   public static int setSimulationDistance(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setSimulationDistance(var1, var2);
   }

   public static boolean acceptTransfers(MinecraftApi var0) {
      return var0.serverSettingsService().acceptsTransfers();
   }

   public static boolean setAcceptTransfers(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setAcceptsTransfers(var1, var2);
   }

   public static int statusHeartbeatInterval(MinecraftApi var0) {
      return var0.serverSettingsService().getStatusHeartbeatInterval();
   }

   public static int setStatusHeartbeatInterval(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setStatusHeartbeatInterval(var1, var2);
   }

   public static PermissionLevel operatorUserPermissionLevel(MinecraftApi var0) {
      return var0.serverSettingsService().getOperatorUserPermissions().level();
   }

   public static PermissionLevel setOperatorUserPermissionLevel(MinecraftApi var0, PermissionLevel var1, ClientInfo var2) {
      return var0.serverSettingsService().setOperatorUserPermissions(LevelBasedPermissionSet.forLevel(var1), var2).level();
   }

   public static boolean hidesOnlinePlayers(MinecraftApi var0) {
      return var0.serverSettingsService().hidesOnlinePlayers();
   }

   public static boolean setHidesOnlinePlayers(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setHidesOnlinePlayers(var1, var2);
   }

   public static boolean repliesToStatus(MinecraftApi var0) {
      return var0.serverSettingsService().repliesToStatus();
   }

   public static boolean setRepliesToStatus(MinecraftApi var0, boolean var1, ClientInfo var2) {
      return var0.serverSettingsService().setRepliesToStatus(var1, var2);
   }

   public static int entityBroadcastRangePercentage(MinecraftApi var0) {
      return var0.serverSettingsService().getEntityBroadcastRangePercentage();
   }

   public static int setEntityBroadcastRangePercentage(MinecraftApi var0, int var1, ClientInfo var2) {
      return var0.serverSettingsService().setEntityBroadcastRangePercentage(var1, var2);
   }
}
