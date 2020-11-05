package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
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
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PlayerDataStorage;
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
   private final List<ServerPlayer> players = Lists.newArrayList();
   private final Map<UUID, ServerPlayer> playersByUUID = Maps.newHashMap();
   private final UserBanList bans;
   private final IpBanList ipBans;
   private final ServerOpList ops;
   private final UserWhiteList whitelist;
   private final Map<UUID, ServerStatsCounter> stats;
   private final Map<UUID, PlayerAdvancements> advancements;
   private final PlayerDataStorage playerIo;
   private boolean doWhiteList;
   private final RegistryAccess.RegistryHolder registryHolder;
   protected final int maxPlayers;
   private int viewDistance;
   private GameType overrideGameMode;
   private boolean allowCheatsForAllPlayers;
   private int sendAllPlayerInfoIn;

   public PlayerList(MinecraftServer var1, RegistryAccess.RegistryHolder var2, PlayerDataStorage var3, int var4) {
      super();
      this.bans = new UserBanList(USERBANLIST_FILE);
      this.ipBans = new IpBanList(IPBANLIST_FILE);
      this.ops = new ServerOpList(OPLIST_FILE);
      this.whitelist = new UserWhiteList(WHITELIST_FILE);
      this.stats = Maps.newHashMap();
      this.advancements = Maps.newHashMap();
      this.server = var1;
      this.registryHolder = var2;
      this.maxPlayers = var4;
      this.playerIo = var3;
   }

   public void placeNewPlayer(Connection var1, ServerPlayer var2) {
      GameProfile var3 = var2.getGameProfile();
      GameProfileCache var4 = this.server.getProfileCache();
      GameProfile var5 = var4.get(var3.getId());
      String var6 = var5 == null ? var3.getName() : var5.getName();
      var4.add(var3);
      CompoundTag var7 = this.load(var2);
      ResourceKey var23;
      if (var7 != null) {
         DataResult var10000 = DimensionType.parseLegacy(new Dynamic(NbtOps.INSTANCE, var7.get("Dimension")));
         Logger var10001 = LOGGER;
         var10001.getClass();
         var23 = (ResourceKey)var10000.resultOrPartial(var10001::error).orElse(Level.OVERWORLD);
      } else {
         var23 = Level.OVERWORLD;
      }

      ResourceKey var8 = var23;
      ServerLevel var9 = this.server.getLevel(var8);
      ServerLevel var10;
      if (var9 == null) {
         LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", var8);
         var10 = this.server.overworld();
      } else {
         var10 = var9;
      }

      var2.setLevel(var10);
      var2.gameMode.setLevel((ServerLevel)var2.level);
      String var11 = "local";
      if (var1.getRemoteAddress() != null) {
         var11 = var1.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", var2.getName().getString(), var11, var2.getId(), var2.getX(), var2.getY(), var2.getZ());
      LevelData var12 = var10.getLevelData();
      this.updatePlayerGameMode(var2, (ServerPlayer)null, var10);
      ServerGamePacketListenerImpl var13 = new ServerGamePacketListenerImpl(this.server, var1, var2);
      GameRules var14 = var10.getGameRules();
      boolean var15 = var14.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
      boolean var16 = var14.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
      var13.send(new ClientboundLoginPacket(var2.getId(), var2.gameMode.getGameModeForPlayer(), var2.gameMode.getPreviousGameModeForPlayer(), BiomeManager.obfuscateSeed(var10.getSeed()), var12.isHardcore(), this.server.levelKeys(), this.registryHolder, var10.dimensionType(), var10.dimension(), this.getMaxPlayers(), this.viewDistance, var16, !var15, var10.isDebug(), var10.isFlat()));
      var13.send(new ClientboundCustomPayloadPacket(ClientboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(this.getServer().getServerModName())));
      var13.send(new ClientboundChangeDifficultyPacket(var12.getDifficulty(), var12.isDifficultyLocked()));
      var13.send(new ClientboundPlayerAbilitiesPacket(var2.abilities));
      var13.send(new ClientboundSetCarriedItemPacket(var2.inventory.selected));
      var13.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      var13.send(new ClientboundUpdateTagsPacket(this.server.getTags()));
      this.sendPlayerPermissionLevel(var2);
      var2.getStats().markAllDirty();
      var2.getRecipeBook().sendInitialRecipeBook(var2);
      this.updateEntireScoreboard(var10.getScoreboard(), var2);
      this.server.invalidateStatus();
      TranslatableComponent var17;
      if (var2.getGameProfile().getName().equalsIgnoreCase(var6)) {
         var17 = new TranslatableComponent("multiplayer.player.joined", new Object[]{var2.getDisplayName()});
      } else {
         var17 = new TranslatableComponent("multiplayer.player.joined.renamed", new Object[]{var2.getDisplayName(), var6});
      }

      this.broadcastMessage(var17.withStyle(ChatFormatting.YELLOW), ChatType.SYSTEM, Util.NIL_UUID);
      var13.teleport(var2.getX(), var2.getY(), var2.getZ(), var2.yRot, var2.xRot);
      this.players.add(var2);
      this.playersByUUID.put(var2.getUUID(), var2);
      this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[]{var2}));

      for(int var18 = 0; var18 < this.players.size(); ++var18) {
         var2.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[]{(ServerPlayer)this.players.get(var18)}));
      }

      var10.addNewPlayer(var2);
      this.server.getCustomBossEvents().onPlayerConnect(var2);
      this.sendLevelInfo(var2, var10);
      if (!this.server.getResourcePack().isEmpty()) {
         var2.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash());
      }

      Iterator var24 = var2.getActiveEffects().iterator();

      while(var24.hasNext()) {
         MobEffectInstance var19 = (MobEffectInstance)var24.next();
         var13.send(new ClientboundUpdateMobEffectPacket(var2.getId(), var19));
      }

      if (var7 != null && var7.contains("RootVehicle", 10)) {
         CompoundTag var25 = var7.getCompound("RootVehicle");
         Entity var26 = EntityType.loadEntityRecursive(var25.getCompound("Entity"), var10, (var1x) -> {
            return !var10.addWithUUID(var1x) ? null : var1x;
         });
         if (var26 != null) {
            UUID var20;
            if (var25.hasUUID("Attach")) {
               var20 = var25.getUUID("Attach");
            } else {
               var20 = null;
            }

            Iterator var21;
            Entity var22;
            if (var26.getUUID().equals(var20)) {
               var2.startRiding(var26, true);
            } else {
               var21 = var26.getIndirectPassengers().iterator();

               while(var21.hasNext()) {
                  var22 = (Entity)var21.next();
                  if (var22.getUUID().equals(var20)) {
                     var2.startRiding(var22, true);
                     break;
                  }
               }
            }

            if (!var2.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               var10.despawn(var26);
               var21 = var26.getIndirectPassengers().iterator();

               while(var21.hasNext()) {
                  var22 = (Entity)var21.next();
                  var10.despawn(var22);
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
      CompoundTag var2 = this.server.getWorldData().getLoadedPlayerTag();
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
            var3.removed = true;

            Entity var5;
            for(Iterator var4 = var3.getIndirectPassengers().iterator(); var4.hasNext(); var5.removed = true) {
               var5 = (Entity)var4.next();
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
         return new TranslatableComponent("multiplayer.disconnect.not_whitelisted");
      } else if (this.ipBans.isBanned(var1)) {
         IpBanListEntry var3 = this.ipBans.get(var1);
         var4 = new TranslatableComponent("multiplayer.disconnect.banned_ip.reason", new Object[]{var3.getReason()});
         if (var3.getExpires() != null) {
            var4.append((Component)(new TranslatableComponent("multiplayer.disconnect.banned_ip.expiration", new Object[]{BAN_DATE_FORMAT.format(var3.getExpires())})));
         }

         return var4;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(var2) ? new TranslatableComponent("multiplayer.disconnect.server_full") : null;
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
         var6.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.duplicate_login"));
      }

      ServerLevel var10 = this.server.overworld();
      Object var9;
      if (this.server.isDemo()) {
         var9 = new DemoMode(var10);
      } else {
         var9 = new ServerPlayerGameMode(var10);
      }

      return new ServerPlayer(this.server, var10, var1, (ServerPlayerGameMode)var9);
   }

   public ServerPlayer respawn(ServerPlayer var1, boolean var2) {
      this.players.remove(var1);
      var1.getLevel().removePlayerImmediately(var1);
      BlockPos var3 = var1.getRespawnPosition();
      float var4 = var1.getRespawnAngle();
      boolean var5 = var1.isRespawnForced();
      ServerLevel var6 = this.server.getLevel(var1.getRespawnDimension());
      Optional var7;
      if (var6 != null && var3 != null) {
         var7 = Player.findRespawnPositionAndUseSpawnBlock(var6, var3, var4, var5, var2);
      } else {
         var7 = Optional.empty();
      }

      ServerLevel var9 = var6 != null && var7.isPresent() ? var6 : this.server.overworld();
      Object var8;
      if (this.server.isDemo()) {
         var8 = new DemoMode(var9);
      } else {
         var8 = new ServerPlayerGameMode(var9);
      }

      ServerPlayer var10 = new ServerPlayer(this.server, var9, var1.getGameProfile(), (ServerPlayerGameMode)var8);
      var10.connection = var1.connection;
      var10.restoreFrom(var1, var2);
      var10.setId(var1.getId());
      var10.setMainArm(var1.getMainArm());
      Iterator var11 = var1.getTags().iterator();

      while(var11.hasNext()) {
         String var12 = (String)var11.next();
         var10.addTag(var12);
      }

      this.updatePlayerGameMode(var10, var1, var9);
      boolean var17 = false;
      if (var7.isPresent()) {
         BlockState var18 = var9.getBlockState(var3);
         boolean var13 = var18.is(Blocks.RESPAWN_ANCHOR);
         Vec3 var14 = (Vec3)var7.get();
         float var15;
         if (!var18.is(BlockTags.BEDS) && !var13) {
            var15 = var4;
         } else {
            Vec3 var16 = Vec3.atBottomCenterOf(var3).subtract(var14).normalize();
            var15 = (float)Mth.wrapDegrees(Mth.atan2(var16.z, var16.x) * 57.2957763671875D - 90.0D);
         }

         var10.moveTo(var14.x, var14.y, var14.z, var15, 0.0F);
         var10.setRespawnPosition(var9.dimension(), var3, var4, var5, false);
         var17 = !var2 && var13;
      } else if (var3 != null) {
         var10.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
      }

      while(!var9.noCollision(var10) && var10.getY() < 256.0D) {
         var10.setPos(var10.getX(), var10.getY() + 1.0D, var10.getZ());
      }

      LevelData var19 = var10.level.getLevelData();
      var10.connection.send(new ClientboundRespawnPacket(var10.level.dimensionType(), var10.level.dimension(), BiomeManager.obfuscateSeed(var10.getLevel().getSeed()), var10.gameMode.getGameModeForPlayer(), var10.gameMode.getPreviousGameModeForPlayer(), var10.getLevel().isDebug(), var10.getLevel().isFlat(), var2));
      var10.connection.teleport(var10.getX(), var10.getY(), var10.getZ(), var10.yRot, var10.xRot);
      var10.connection.send(new ClientboundSetDefaultSpawnPositionPacket(var9.getSharedSpawnPos(), var9.getSharedSpawnAngle()));
      var10.connection.send(new ClientboundChangeDifficultyPacket(var19.getDifficulty(), var19.isDifficultyLocked()));
      var10.connection.send(new ClientboundSetExperiencePacket(var10.experienceProgress, var10.totalExperience, var10.experienceLevel));
      this.sendLevelInfo(var10, var9);
      this.sendPlayerPermissionLevel(var10);
      var9.addRespawnedPlayer(var10);
      this.players.add(var10);
      this.playersByUUID.put(var10.getUUID(), var10);
      var10.initMenu();
      var10.setHealth(var10.getHealth());
      if (var17) {
         var10.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 1.0F, 1.0F));
      }

      return var10;
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

   public void broadcastAll(Packet<?> var1) {
      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         ((ServerPlayer)this.players.get(var2)).connection.send(var1);
      }

   }

   public void broadcastAll(Packet<?> var1, ResourceKey<Level> var2) {
      for(int var3 = 0; var3 < this.players.size(); ++var3) {
         ServerPlayer var4 = (ServerPlayer)this.players.get(var3);
         if (var4.level.dimension() == var2) {
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
               var7.sendMessage(var2, var1.getUUID());
            }
         }

      }
   }

   public void broadcastToAllExceptTeam(Player var1, Component var2) {
      Team var3 = var1.getTeam();
      if (var3 == null) {
         this.broadcastMessage(var2, ChatType.SYSTEM, var1.getUUID());
      } else {
         for(int var4 = 0; var4 < this.players.size(); ++var4) {
            ServerPlayer var5 = (ServerPlayer)this.players.get(var4);
            if (var5.getTeam() != var3) {
               var5.sendMessage(var2, var1.getUUID());
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
      return this.ops.contains(var1) || this.server.isSingleplayerOwner(var1) && this.server.getWorldData().getAllowCommands() || this.allowCheatsForAllPlayers;
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

   public void broadcast(@Nullable Player var1, double var2, double var4, double var6, double var8, ResourceKey<Level> var10, Packet<?> var11) {
      for(int var12 = 0; var12 < this.players.size(); ++var12) {
         ServerPlayer var13 = (ServerPlayer)this.players.get(var12);
         if (var13 != var1 && var13.level.dimension() == var10) {
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
      WorldBorder var3 = this.server.overworld().getWorldBorder();
      var1.connection.send(new ClientboundSetBorderPacket(var3, ClientboundSetBorderPacket.Type.INITIALIZE));
      var1.connection.send(new ClientboundSetTimePacket(var2.getGameTime(), var2.getDayTime(), var2.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      var1.connection.send(new ClientboundSetDefaultSpawnPositionPacket(var2.getSharedSpawnPos(), var2.getSharedSpawnAngle()));
      if (var2.isRaining()) {
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, var2.getRainLevel(1.0F)));
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, var2.getThunderLevel(1.0F)));
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

   public List<ServerPlayer> getPlayersWithAddress(String var1) {
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

   private void updatePlayerGameMode(ServerPlayer var1, @Nullable ServerPlayer var2, ServerLevel var3) {
      if (var2 != null) {
         var1.gameMode.setGameModeForPlayer(var2.gameMode.getGameModeForPlayer(), var2.gameMode.getPreviousGameModeForPlayer());
      } else if (this.overrideGameMode != null) {
         var1.gameMode.setGameModeForPlayer(this.overrideGameMode, GameType.NOT_SET);
      }

      var1.gameMode.updateGameMode(var3.getServer().getWorldData().getGameType());
   }

   public void setAllowCheatsForAllPlayers(boolean var1) {
      this.allowCheatsForAllPlayers = var1;
   }

   public void removeAll() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         ((ServerPlayer)this.players.get(var1)).connection.disconnect(new TranslatableComponent("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void broadcastMessage(Component var1, ChatType var2, UUID var3) {
      this.server.sendMessage(var1, var3);
      this.broadcastAll(new ClientboundChatPacket(var1, var2, var3));
   }

   public ServerStatsCounter getPlayerStats(Player var1) {
      UUID var2 = var1.getUUID();
      ServerStatsCounter var3 = var2 == null ? null : (ServerStatsCounter)this.stats.get(var2);
      if (var3 == null) {
         File var4 = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
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
         File var4 = this.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile();
         File var5 = new File(var4, var2 + ".json");
         var3 = new PlayerAdvancements(this.server.getFixerUpper(), this, this.server.getAdvancements(), var5, var1);
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

   public List<ServerPlayer> getPlayers() {
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
         var2.reload(this.server.getAdvancements());
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
