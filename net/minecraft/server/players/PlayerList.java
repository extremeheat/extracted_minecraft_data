package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.PlayerIO;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
   public static final File USERBANLIST_FILE = new File("banned-players.json");
   public static final File IPBANLIST_FILE = new File("banned-ips.json");
   public static final File OPLIST_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List players = Lists.newArrayList();
   private final Map playersByUUID = Maps.newHashMap();
   private final UserBanList bans;
   private final IpBanList ipBans;
   private final ServerOpList ops;
   private final UserWhiteList whitelist;
   private final Map stats;
   private final Map advancements;
   private PlayerIO playerIo;
   private boolean doWhiteList;
   protected final int maxPlayers;
   private int viewDistance;
   private GameType overrideGameMode;
   private boolean allowCheatsForAllPlayers;
   private int sendAllPlayerInfoIn;

   public PlayerList(MinecraftServer var1, int var2) {
      this.bans = new UserBanList(USERBANLIST_FILE);
      this.ipBans = new IpBanList(IPBANLIST_FILE);
      this.ops = new ServerOpList(OPLIST_FILE);
      this.whitelist = new UserWhiteList(WHITELIST_FILE);
      this.stats = Maps.newHashMap();
      this.advancements = Maps.newHashMap();
      this.server = var1;
      this.maxPlayers = var2;
      this.getBans().setEnabled(true);
      this.getIpBans().setEnabled(true);
   }

   public void placeNewPlayer(Connection var1, ServerPlayer var2) {
      GameProfile var3 = var2.getGameProfile();
      GameProfileCache var4 = this.server.getProfileCache();
      GameProfile var5 = var4.get(var3.getId());
      String var6 = var5 == null ? var3.getName() : var5.getName();
      var4.add(var3);
      CompoundTag var7 = this.load(var2);
      ServerLevel var8 = this.server.getLevel(var2.dimension);
      var2.setLevel(var8);
      var2.gameMode.setLevel((ServerLevel)var2.level);
      String var9 = "local";
      if (var1.getRemoteAddress() != null) {
         var9 = var1.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", var2.getName().getString(), var9, var2.getId(), var2.getX(), var2.getY(), var2.getZ());
      LevelData var10 = var8.getLevelData();
      this.updatePlayerGameMode(var2, (ServerPlayer)null, var8);
      ServerGamePacketListenerImpl var11 = new ServerGamePacketListenerImpl(this.server, var1, var2);
      GameRules var12 = var8.getGameRules();
      boolean var13 = var12.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
      boolean var14 = var12.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
      var11.send(new ClientboundLoginPacket(var2.getId(), var2.gameMode.getGameModeForPlayer(), LevelData.obfuscateSeed(var10.getSeed()), var10.isHardcore(), var8.dimension.getType(), this.getMaxPlayers(), var10.getGeneratorType(), this.viewDistance, var14, !var13));
      var11.send(new ClientboundCustomPayloadPacket(ClientboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(this.getServer().getServerModName())));
      var11.send(new ClientboundChangeDifficultyPacket(var10.getDifficulty(), var10.isDifficultyLocked()));
      var11.send(new ClientboundPlayerAbilitiesPacket(var2.abilities));
      var11.send(new ClientboundSetCarriedItemPacket(var2.inventory.selected));
      var11.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      var11.send(new ClientboundUpdateTagsPacket(this.server.getTags()));
      this.sendPlayerPermissionLevel(var2);
      var2.getStats().markAllDirty();
      var2.getRecipeBook().sendInitialRecipeBook(var2);
      this.updateEntireScoreboard(var8.getScoreboard(), var2);
      this.server.invalidateStatus();
      TranslatableComponent var15;
      if (var2.getGameProfile().getName().equalsIgnoreCase(var6)) {
         var15 = new TranslatableComponent("multiplayer.player.joined", new Object[]{var2.getDisplayName()});
      } else {
         var15 = new TranslatableComponent("multiplayer.player.joined.renamed", new Object[]{var2.getDisplayName(), var6});
      }

      this.broadcastMessage(var15.withStyle(ChatFormatting.YELLOW));
      var11.teleport(var2.getX(), var2.getY(), var2.getZ(), var2.yRot, var2.xRot);
      this.players.add(var2);
      this.playersByUUID.put(var2.getUUID(), var2);
      this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[]{var2}));

      for(int var16 = 0; var16 < this.players.size(); ++var16) {
         var2.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[]{(ServerPlayer)this.players.get(var16)}));
      }

      var8.addNewPlayer(var2);
      this.server.getCustomBossEvents().onPlayerConnect(var2);
      this.sendLevelInfo(var2, var8);
      if (!this.server.getResourcePack().isEmpty()) {
         var2.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash());
      }

      Iterator var21 = var2.getActiveEffects().iterator();

      while(var21.hasNext()) {
         MobEffectInstance var17 = (MobEffectInstance)var21.next();
         var11.send(new ClientboundUpdateMobEffectPacket(var2.getId(), var17));
      }

      if (var7 != null && var7.contains("RootVehicle", 10)) {
         CompoundTag var22 = var7.getCompound("RootVehicle");
         Entity var23 = EntityType.loadEntityRecursive(var22.getCompound("Entity"), var8, (var1x) -> {
            return !var8.addWithUUID(var1x) ? null : var1x;
         });
         if (var23 != null) {
            UUID var18 = var22.getUUID("Attach");
            Iterator var19;
            Entity var20;
            if (var23.getUUID().equals(var18)) {
               var2.startRiding(var23, true);
            } else {
               var19 = var23.getIndirectPassengers().iterator();

               while(var19.hasNext()) {
                  var20 = (Entity)var19.next();
                  if (var20.getUUID().equals(var18)) {
                     var2.startRiding(var20, true);
                     break;
                  }
               }
            }

            if (!var2.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               var8.despawn(var23);
               var19 = var23.getIndirectPassengers().iterator();

               while(var19.hasNext()) {
                  var20 = (Entity)var19.next();
                  var8.despawn(var20);
               }
            }
         }
      }

      var2.initMenu();
   }

   protected void updateEntireScoreboard(ServerScoreboard var1, ServerPlayer var2) {
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = var1.getPlayerTeams().iterator();

      while(var4.hasNext()) {
         PlayerTeam var5 = (PlayerTeam)var4.next();
         var2.connection.send(new ClientboundSetPlayerTeamPacket(var5, 0));
      }

      for(int var9 = 0; var9 < 19; ++var9) {
         Objective var10 = var1.getDisplayObjective(var9);
         if (var10 != null && !var3.contains(var10)) {
            List var6 = var1.getStartTrackingPackets(var10);
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               Packet var8 = (Packet)var7.next();
               var2.connection.send(var8);
            }

            var3.add(var10);
         }
      }

   }

   public void setLevel(ServerLevel var1) {
      this.playerIo = var1.getLevelStorage();
      var1.getWorldBorder().addListener(new BorderChangeListener() {
         public void onBorderSizeSet(WorldBorder var1, double var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(var1, ClientboundSetBorderPacket.Type.SET_SIZE));
         }

         public void onBorderSizeLerping(WorldBorder var1, double var2, double var4, long var6) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(var1, ClientboundSetBorderPacket.Type.LERP_SIZE));
         }

         public void onBorderCenterSet(WorldBorder var1, double var2, double var4) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(var1, ClientboundSetBorderPacket.Type.SET_CENTER));
         }

         public void onBorderSetWarningTime(WorldBorder var1, int var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(var1, ClientboundSetBorderPacket.Type.SET_WARNING_TIME));
         }

         public void onBorderSetWarningBlocks(WorldBorder var1, int var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(var1, ClientboundSetBorderPacket.Type.SET_WARNING_BLOCKS));
         }

         public void onBorderSetDamagePerBlock(WorldBorder var1, double var2) {
         }

         public void onBorderSetDamageSafeZOne(WorldBorder var1, double var2) {
         }
      });
   }

   @Nullable
   public CompoundTag load(ServerPlayer var1) {
      CompoundTag var2 = this.server.getLevel(DimensionType.OVERWORLD).getLevelData().getLoadedPlayerTag();
      CompoundTag var3;
      if (var1.getName().getString().equals(this.server.getSingleplayerName()) && var2 != null) {
         var3 = var2;
         var1.load(var2);
         LOGGER.debug("loading single player");
      } else {
         var3 = this.playerIo.load(var1);
      }

      return var3;
   }

   protected void save(ServerPlayer var1) {
      this.playerIo.save(var1);
      ServerStatsCounter var2 = (ServerStatsCounter)this.stats.get(var1.getUUID());
      if (var2 != null) {
         var2.save();
      }

      PlayerAdvancements var3 = (PlayerAdvancements)this.advancements.get(var1.getUUID());
      if (var3 != null) {
         var3.save();
      }

   }

   public void remove(ServerPlayer var1) {
      ServerLevel var2 = var1.getLevel();
      var1.awardStat(Stats.LEAVE_GAME);
      this.save(var1);
      if (var1.isPassenger()) {
         Entity var3 = var1.getRootVehicle();
         if (var3.hasOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            var1.stopRiding();
            var2.despawn(var3);
            Iterator var4 = var3.getIndirectPassengers().iterator();

            while(var4.hasNext()) {
               Entity var5 = (Entity)var4.next();
               var2.despawn(var5);
            }

            var2.getChunk(var1.xChunk, var1.zChunk).markUnsaved();
         }
      }

      var1.unRide();
      var2.removePlayerImmediately(var1);
      var1.getAdvancements().stopListening();
      this.players.remove(var1);
      this.server.getCustomBossEvents().onPlayerDisconnect(var1);
      UUID var6 = var1.getUUID();
      ServerPlayer var7 = (ServerPlayer)this.playersByUUID.get(var6);
      if (var7 == var1) {
         this.playersByUUID.remove(var6);
         this.stats.remove(var6);
         this.advancements.remove(var6);
      }

      this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, new ServerPlayer[]{var1}));
   }

   @Nullable
   public Component canPlayerLogin(SocketAddress var1, GameProfile var2) {
      TranslatableComponent var4;
      if (this.bans.isBanned(var2)) {
         UserBanListEntry var5 = (UserBanListEntry)this.bans.get(var2);
         var4 = new TranslatableComponent("multiplayer.disconnect.banned.reason", new Object[]{var5.getReason()});
         if (var5.getExpires() != null) {
            var4.append((Component)(new TranslatableComponent("multiplayer.disconnect.banned.expiration", new Object[]{BAN_DATE_FORMAT.format(var5.getExpires())})));
         }

         return var4;
      } else if (!this.isWhiteListed(var2)) {
         return new TranslatableComponent("multiplayer.disconnect.not_whitelisted", new Object[0]);
      } else if (this.ipBans.isBanned(var1)) {
         IpBanListEntry var3 = this.ipBans.get(var1);
         var4 = new TranslatableComponent("multiplayer.disconnect.banned_ip.reason", new Object[]{var3.getReason()});
         if (var3.getExpires() != null) {
            var4.append((Component)(new TranslatableComponent("multiplayer.disconnect.banned_ip.expiration", new Object[]{BAN_DATE_FORMAT.format(var3.getExpires())})));
         }

         return var4;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(var2) ? new TranslatableComponent("multiplayer.disconnect.server_full", new Object[0]) : null;
      }
   }

   public ServerPlayer getPlayerForLogin(GameProfile var1) {
      UUID var2 = Player.createPlayerUUID(var1);
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < this.players.size(); ++var4) {
         ServerPlayer var5 = (ServerPlayer)this.players.get(var4);
         if (var5.getUUID().equals(var2)) {
            var3.add(var5);
         }
      }

      ServerPlayer var7 = (ServerPlayer)this.playersByUUID.get(var1.getId());
      if (var7 != null && !var3.contains(var7)) {
         var3.add(var7);
      }

      Iterator var8 = var3.iterator();

      while(var8.hasNext()) {
         ServerPlayer var6 = (ServerPlayer)var8.next();
         var6.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.duplicate_login", new Object[0]));
      }

      Object var9;
      if (this.server.isDemo()) {
         var9 = new DemoMode(this.server.getLevel(DimensionType.OVERWORLD));
      } else {
         var9 = new ServerPlayerGameMode(this.server.getLevel(DimensionType.OVERWORLD));
      }

      return new ServerPlayer(this.server, this.server.getLevel(DimensionType.OVERWORLD), var1, (ServerPlayerGameMode)var9);
   }

   public ServerPlayer respawn(ServerPlayer var1, DimensionType var2, boolean var3) {
      this.players.remove(var1);
      var1.getLevel().removePlayerImmediately(var1);
      BlockPos var4 = var1.getRespawnPosition();
      boolean var5 = var1.isRespawnForced();
      var1.dimension = var2;
      Object var6;
      if (this.server.isDemo()) {
         var6 = new DemoMode(this.server.getLevel(var1.dimension));
      } else {
         var6 = new ServerPlayerGameMode(this.server.getLevel(var1.dimension));
      }

      ServerPlayer var7 = new ServerPlayer(this.server, this.server.getLevel(var1.dimension), var1.getGameProfile(), (ServerPlayerGameMode)var6);
      var7.connection = var1.connection;
      var7.restoreFrom(var1, var3);
      var7.setId(var1.getId());
      var7.setMainArm(var1.getMainArm());
      Iterator var8 = var1.getTags().iterator();

      while(var8.hasNext()) {
         String var9 = (String)var8.next();
         var7.addTag(var9);
      }

      ServerLevel var12 = this.server.getLevel(var1.dimension);
      this.updatePlayerGameMode(var7, var1, var12);
      if (var4 != null) {
         Optional var13 = Player.checkBedValidRespawnPosition(this.server.getLevel(var1.dimension), var4, var5);
         if (var13.isPresent()) {
            Vec3 var10 = (Vec3)var13.get();
            var7.moveTo(var10.x, var10.y, var10.z, 0.0F, 0.0F);
            var7.setRespawnPosition(var4, var5, false);
         } else {
            var7.connection.send(new ClientboundGameEventPacket(0, 0.0F));
         }
      }

      while(!var12.noCollision(var7) && var7.getY() < 256.0D) {
         var7.setPos(var7.getX(), var7.getY() + 1.0D, var7.getZ());
      }

      LevelData var14 = var7.level.getLevelData();
      var7.connection.send(new ClientboundRespawnPacket(var7.dimension, LevelData.obfuscateSeed(var14.getSeed()), var14.getGeneratorType(), var7.gameMode.getGameModeForPlayer()));
      BlockPos var11 = var12.getSharedSpawnPos();
      var7.connection.teleport(var7.getX(), var7.getY(), var7.getZ(), var7.yRot, var7.xRot);
      var7.connection.send(new ClientboundSetSpawnPositionPacket(var11));
      var7.connection.send(new ClientboundChangeDifficultyPacket(var14.getDifficulty(), var14.isDifficultyLocked()));
      var7.connection.send(new ClientboundSetExperiencePacket(var7.experienceProgress, var7.totalExperience, var7.experienceLevel));
      this.sendLevelInfo(var7, var12);
      this.sendPlayerPermissionLevel(var7);
      var12.addRespawnedPlayer(var7);
      this.players.add(var7);
      this.playersByUUID.put(var7.getUUID(), var7);
      var7.initMenu();
      var7.setHealth(var7.getHealth());
      return var7;
   }

   public void sendPlayerPermissionLevel(ServerPlayer var1) {
      GameProfile var2 = var1.getGameProfile();
      int var3 = this.server.getProfilePermissions(var2);
      this.sendPlayerPermissionLevel(var1, var3);
   }

   public void tick() {
      if (++this.sendAllPlayerInfoIn > 600) {
         this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY, this.players));
         this.sendAllPlayerInfoIn = 0;
      }

   }

   public void broadcastAll(Packet var1) {
      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         ((ServerPlayer)this.players.get(var2)).connection.send(var1);
      }

   }

   public void broadcastAll(Packet var1, DimensionType var2) {
      for(int var3 = 0; var3 < this.players.size(); ++var3) {
         ServerPlayer var4 = (ServerPlayer)this.players.get(var3);
         if (var4.dimension == var2) {
            var4.connection.send(var1);
         }
      }

   }

   public void broadcastToTeam(Player var1, Component var2) {
      Team var3 = var1.getTeam();
      if (var3 != null) {
         Collection var4 = var3.getPlayers();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            ServerPlayer var7 = this.getPlayerByName(var6);
            if (var7 != null && var7 != var1) {
               var7.sendMessage(var2);
            }
         }

      }
   }

   public void broadcastToAllExceptTeam(Player var1, Component var2) {
      Team var3 = var1.getTeam();
      if (var3 == null) {
         this.broadcastMessage(var2);
      } else {
         for(int var4 = 0; var4 < this.players.size(); ++var4) {
            ServerPlayer var5 = (ServerPlayer)this.players.get(var4);
            if (var5.getTeam() != var3) {
               var5.sendMessage(var2);
            }
         }

      }
   }

   public String[] getPlayerNamesArray() {
      String[] var1 = new String[this.players.size()];

      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         var1[var2] = ((ServerPlayer)this.players.get(var2)).getGameProfile().getName();
      }

      return var1;
   }

   public UserBanList getBans() {
      return this.bans;
   }

   public IpBanList getIpBans() {
      return this.ipBans;
   }

   public void op(GameProfile var1) {
      this.ops.add(new ServerOpListEntry(var1, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(var1)));
      ServerPlayer var2 = this.getPlayer(var1.getId());
      if (var2 != null) {
         this.sendPlayerPermissionLevel(var2);
      }

   }

   public void deop(GameProfile var1) {
      this.ops.remove(var1);
      ServerPlayer var2 = this.getPlayer(var1.getId());
      if (var2 != null) {
         this.sendPlayerPermissionLevel(var2);
      }

   }

   private void sendPlayerPermissionLevel(ServerPlayer var1, int var2) {
      if (var1.connection != null) {
         byte var3;
         if (var2 <= 0) {
            var3 = 24;
         } else if (var2 >= 4) {
            var3 = 28;
         } else {
            var3 = (byte)(24 + var2);
         }

         var1.connection.send(new ClientboundEntityEventPacket(var1, var3));
      }

      this.server.getCommands().sendCommands(var1);
   }

   public boolean isWhiteListed(GameProfile var1) {
      return !this.doWhiteList || this.ops.contains(var1) || this.whitelist.contains(var1);
   }

   public boolean isOp(GameProfile var1) {
      return this.ops.contains(var1) || this.server.isSingleplayerOwner(var1) && this.server.getLevel(DimensionType.OVERWORLD).getLevelData().getAllowCommands() || this.allowCheatsForAllPlayers;
   }

   @Nullable
   public ServerPlayer getPlayerByName(String var1) {
      Iterator var2 = this.players.iterator();

      ServerPlayer var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (ServerPlayer)var2.next();
      } while(!var3.getGameProfile().getName().equalsIgnoreCase(var1));

      return var3;
   }

   public void broadcast(@Nullable Player var1, double var2, double var4, double var6, double var8, DimensionType var10, Packet var11) {
      for(int var12 = 0; var12 < this.players.size(); ++var12) {
         ServerPlayer var13 = (ServerPlayer)this.players.get(var12);
         if (var13 != var1 && var13.dimension == var10) {
            double var14 = var2 - var13.getX();
            double var16 = var4 - var13.getY();
            double var18 = var6 - var13.getZ();
            if (var14 * var14 + var16 * var16 + var18 * var18 < var8 * var8) {
               var13.connection.send(var11);
            }
         }
      }

   }

   public void saveAll() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         this.save((ServerPlayer)this.players.get(var1));
      }

   }

   public UserWhiteList getWhiteList() {
      return this.whitelist;
   }

   public String[] getWhiteListNames() {
      return this.whitelist.getUserList();
   }

   public ServerOpList getOps() {
      return this.ops;
   }

   public String[] getOpNames() {
      return this.ops.getUserList();
   }

   public void reloadWhiteList() {
   }

   public void sendLevelInfo(ServerPlayer var1, ServerLevel var2) {
      WorldBorder var3 = this.server.getLevel(DimensionType.OVERWORLD).getWorldBorder();
      var1.connection.send(new ClientboundSetBorderPacket(var3, ClientboundSetBorderPacket.Type.INITIALIZE));
      var1.connection.send(new ClientboundSetTimePacket(var2.getGameTime(), var2.getDayTime(), var2.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      BlockPos var4 = var2.getSharedSpawnPos();
      var1.connection.send(new ClientboundSetSpawnPositionPacket(var4));
      if (var2.isRaining()) {
         var1.connection.send(new ClientboundGameEventPacket(1, 0.0F));
         var1.connection.send(new ClientboundGameEventPacket(7, var2.getRainLevel(1.0F)));
         var1.connection.send(new ClientboundGameEventPacket(8, var2.getThunderLevel(1.0F)));
      }

   }

   public void sendAllPlayerInfo(ServerPlayer var1) {
      var1.refreshContainer(var1.inventoryMenu);
      var1.resetSentInfo();
      var1.connection.send(new ClientboundSetCarriedItemPacket(var1.inventory.selected));
   }

   public int getPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isUsingWhitelist() {
      return this.doWhiteList;
   }

   public void setUsingWhiteList(boolean var1) {
      this.doWhiteList = var1;
   }

   public List getPlayersWithAddress(String var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         if (var4.getIpAddress().equals(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public CompoundTag getSingleplayerData() {
      return null;
   }

   public void setOverrideGameMode(GameType var1) {
      this.overrideGameMode = var1;
   }

   private void updatePlayerGameMode(ServerPlayer var1, ServerPlayer var2, LevelAccessor var3) {
      if (var2 != null) {
         var1.gameMode.setGameModeForPlayer(var2.gameMode.getGameModeForPlayer());
      } else if (this.overrideGameMode != null) {
         var1.gameMode.setGameModeForPlayer(this.overrideGameMode);
      }

      var1.gameMode.updateGameMode(var3.getLevelData().getGameType());
   }

   public void setAllowCheatsForAllPlayers(boolean var1) {
      this.allowCheatsForAllPlayers = var1;
   }

   public void removeAll() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         ((ServerPlayer)this.players.get(var1)).connection.disconnect(new TranslatableComponent("multiplayer.disconnect.server_shutdown", new Object[0]));
      }

   }

   public void broadcastMessage(Component var1, boolean var2) {
      this.server.sendMessage(var1);
      ChatType var3 = var2 ? ChatType.SYSTEM : ChatType.CHAT;
      this.broadcastAll(new ClientboundChatPacket(var1, var3));
   }

   public void broadcastMessage(Component var1) {
      this.broadcastMessage(var1, true);
   }

   public ServerStatsCounter getPlayerStats(Player var1) {
      UUID var2 = var1.getUUID();
      ServerStatsCounter var3 = var2 == null ? null : (ServerStatsCounter)this.stats.get(var2);
      if (var3 == null) {
         File var4 = new File(this.server.getLevel(DimensionType.OVERWORLD).getLevelStorage().getFolder(), "stats");
         File var5 = new File(var4, var2 + ".json");
         if (!var5.exists()) {
            File var6 = new File(var4, var1.getName().getString() + ".json");
            if (var6.exists() && var6.isFile()) {
               var6.renameTo(var5);
            }
         }

         var3 = new ServerStatsCounter(this.server, var5);
         this.stats.put(var2, var3);
      }

      return var3;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayer var1) {
      UUID var2 = var1.getUUID();
      PlayerAdvancements var3 = (PlayerAdvancements)this.advancements.get(var2);
      if (var3 == null) {
         File var4 = new File(this.server.getLevel(DimensionType.OVERWORLD).getLevelStorage().getFolder(), "advancements");
         File var5 = new File(var4, var2 + ".json");
         var3 = new PlayerAdvancements(this.server, var5, var1);
         this.advancements.put(var2, var3);
      }

      var3.setPlayer(var1);
      return var3;
   }

   public void setViewDistance(int var1) {
      this.viewDistance = var1;
      this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket(var1));
      Iterator var2 = this.server.getAllLevels().iterator();

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
         if (var3 != null) {
            var3.getChunkSource().setViewDistance(var1);
         }
      }

   }

   public List getPlayers() {
      return this.players;
   }

   @Nullable
   public ServerPlayer getPlayer(UUID var1) {
      return (ServerPlayer)this.playersByUUID.get(var1);
   }

   public boolean canBypassPlayerLimit(GameProfile var1) {
      return false;
   }

   public void reloadResources() {
      Iterator var1 = this.advancements.values().iterator();

      while(var1.hasNext()) {
         PlayerAdvancements var2 = (PlayerAdvancements)var1.next();
         var2.reload();
      }

      this.broadcastAll(new ClientboundUpdateTagsPacket(this.server.getTags()));
      ClientboundUpdateRecipesPacket var4 = new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());
      Iterator var5 = this.players.iterator();

      while(var5.hasNext()) {
         ServerPlayer var3 = (ServerPlayer)var5.next();
         var3.connection.send(var4);
         var3.getRecipeBook().sendInitialRecipeBook(var3);
      }

   }

   public boolean isAllowCheatsForAllPlayers() {
      return this.allowCheatsForAllPlayers;
   }
}
