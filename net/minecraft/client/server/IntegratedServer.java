package net.minecraft.client.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.Crypt;
import net.minecraft.util.profiling.GameProfiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Snooper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final LevelSettings settings;
   private boolean paused;
   private int publishedPort = -1;
   private LanServerPinger lanPinger;
   private UUID uuid;

   public IntegratedServer(Minecraft var1, String var2, String var3, LevelSettings var4, YggdrasilAuthenticationService var5, MinecraftSessionService var6, GameProfileRepository var7, GameProfileCache var8, ChunkProgressListenerFactory var9) {
      super(new File(var1.gameDirectory, "saves"), var1.getProxy(), var1.getFixerUpper(), new Commands(false), var5, var6, var7, var8, var9, var2);
      this.setSingleplayerName(var1.getUser().getName());
      this.setLevelName(var3);
      this.setDemo(var1.isDemo());
      this.setBonusChest(var4.hasStartingBonusItems());
      this.setMaxBuildHeight(256);
      this.setPlayerList(new IntegratedPlayerList(this));
      this.minecraft = var1;
      this.settings = this.isDemo() ? MinecraftServer.DEMO_SETTINGS : var4;
   }

   public void loadLevel(String var1, String var2, long var3, LevelType var5, JsonElement var6) {
      this.ensureLevelConversion(var1);
      LevelStorage var7 = this.getStorageSource().selectLevel(var1, this);
      this.detectBundledResources(this.getLevelIdName(), var7);
      LevelData var8 = var7.prepareLevel();
      if (var8 == null) {
         var8 = new LevelData(this.settings, var2);
      } else {
         var8.setLevelName(var2);
      }

      this.loadDataPacks(var7.getFolder(), var8);
      ChunkProgressListener var9 = this.progressListenerFactory.create(11);
      this.createLevels(var7, var8, this.settings, var9);
      if (this.getLevel(DimensionType.OVERWORLD).getLevelData().getDifficulty() == null) {
         this.setDifficulty(this.minecraft.options.difficulty, true);
      }

      this.prepareLevels(var9);
   }

   public boolean initServer() throws IOException {
      LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setAnimals(true);
      this.setNpcsEnabled(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      LOGGER.info("Generating keypair");
      this.setKeyPair(Crypt.generateKeyPair());
      this.loadLevel(this.getLevelIdName(), this.getLevelName(), this.settings.getSeed(), this.settings.getLevelType(), this.settings.getLevelTypeOptions());
      this.setMotd(this.getSingleplayerName() + " - " + this.getLevel(DimensionType.OVERWORLD).getLevelData().getLevelName());
      return true;
   }

   public void tickServer(BooleanSupplier var1) {
      boolean var2 = this.paused;
      this.paused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused();
      GameProfiler var3 = this.getProfiler();
      if (!var2 && this.paused) {
         var3.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAll();
         this.saveAllChunks(false, false, false);
         var3.pop();
      }

      if (!this.paused) {
         super.tickServer(var1);
         int var4 = Math.max(2, this.minecraft.options.renderDistance + -1);
         if (var4 != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", var4, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(var4);
         }

      }
   }

   public boolean canGenerateStructures() {
      return false;
   }

   public GameType getDefaultGameType() {
      return this.settings.getGameType();
   }

   public Difficulty getDefaultDifficulty() {
      return this.minecraft.level.getLevelData().getDifficulty();
   }

   public boolean isHardcore() {
      return this.settings.isHardcore();
   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public boolean isEpollEnabled() {
      return false;
   }

   public void onServerCrash(CrashReport var1) {
      this.minecraft.delayCrash(var1);
   }

   public CrashReport fillReport(CrashReport var1) {
      var1 = super.fillReport(var1);
      var1.getSystemDetails().setDetail("Type", (Object)"Integrated Server (map_client.txt)");
      var1.getSystemDetails().setDetail("Is Modded", () -> {
         String var1 = ClientBrandRetriever.getClientModName();
         if (!var1.equals("vanilla")) {
            return "Definitely; Client brand changed to '" + var1 + "'";
         } else {
            var1 = this.getServerModName();
            if (!"vanilla".equals(var1)) {
               return "Definitely; Server brand changed to '" + var1 + "'";
            } else {
               return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.";
            }
         }
      });
      return var1;
   }

   public void populateSnooper(Snooper var1) {
      super.populateSnooper(var1);
      var1.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
   }

   public boolean publishServer(GameType var1, boolean var2, int var3) {
      try {
         this.getConnection().startTcpServerListener((InetAddress)null, var3);
         LOGGER.info("Started serving on {}", var3);
         this.publishedPort = var3;
         this.lanPinger = new LanServerPinger(this.getMotd(), var3 + "");
         this.lanPinger.start();
         this.getPlayerList().setOverrideGameMode(var1);
         this.getPlayerList().setAllowCheatsForAllPlayers(var2);
         int var4 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(var4);
         Iterator var5 = this.getPlayerList().getPlayers().iterator();

         while(var5.hasNext()) {
            ServerPlayer var6 = (ServerPlayer)var5.next();
            this.getCommands().sendCommands(var6);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public void halt(boolean var1) {
      this.executeBlocking(() -> {
         ArrayList var1 = Lists.newArrayList(this.getPlayerList().getPlayers());
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ServerPlayer var3 = (ServerPlayer)var2.next();
            if (!var3.getUUID().equals(this.uuid)) {
               this.getPlayerList().remove(var3);
            }
         }

      });
      super.halt(var1);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   public int getPort() {
      return this.publishedPort;
   }

   public void setDefaultGameMode(GameType var1) {
      super.setDefaultGameMode(var1);
      this.getPlayerList().setOverrideGameMode(var1);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID var1) {
      this.uuid = var1;
   }

   public boolean isSingleplayerOwner(GameProfile var1) {
      return var1.getName().equalsIgnoreCase(this.getSingleplayerName());
   }
}
