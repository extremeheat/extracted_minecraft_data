package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateScreenPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerPlayerUnlocks;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.TheGame;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class PlayerList {
   public static final File USERBANLIST_FILE = new File("banned-players.json");
   public static final File IPBANLIST_FILE = new File("banned-ips.json");
   public static final File OPLIST_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   public static final Component CHAT_FILTERED_FULL = Component.translatable("chat.filtered_full");
   public static final Component DUPLICATE_LOGIN_DISCONNECT_MESSAGE = Component.translatable("multiplayer.disconnect.duplicate_login");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SEND_PLAYER_INFO_INTERVAL = 600;
   private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final TheGame theGame;
   private final List<ServerPlayer> players = Lists.newArrayList();
   private final Map<UUID, ServerPlayer> playersByUUID = Maps.newHashMap();
   private final UserBanList bans;
   private final IpBanList ipBans;
   private final ServerOpList ops;
   private final UserWhiteList whitelist;
   private final Map<UUID, ServerStatsCounter> stats;
   private final Map<UUID, PlayerAdvancements> advancements;
   private final Map<UUID, ServerPlayerUnlocks> unlocks;
   private final PlayerDataStorage playerIo;
   private boolean doWhiteList;
   protected final int maxPlayers;
   private int viewDistance;
   private int simulationDistance;
   private boolean allowCommandsForAllPlayers;
   private static final boolean ALLOW_LOGOUTIVATOR = false;
   private int sendAllPlayerInfoIn;

   public PlayerList(TheGame var1, PlayerDataStorage var2, int var3) {
      super();
      this.bans = new UserBanList(USERBANLIST_FILE);
      this.ipBans = new IpBanList(IPBANLIST_FILE);
      this.ops = new ServerOpList(OPLIST_FILE);
      this.whitelist = new UserWhiteList(WHITELIST_FILE);
      this.stats = Maps.newHashMap();
      this.advancements = Maps.newHashMap();
      this.unlocks = Maps.newHashMap();
      this.theGame = var1;
      this.maxPlayers = var3;
      this.playerIo = var2;
   }

   public void placeNewPlayer(Connection var1, ServerPlayer var2, CommonListenerCookie var3) {
      GameProfile var4 = var2.getGameProfile();
      TheGame var5 = var2.theGame();
      MinecraftServer var6 = var5.server();
      GameProfileCache var7 = var6.getProfileCache();
      String var8;
      if (var7 != null) {
         Optional var9 = var7.get(var4.getId());
         var8 = (String)var9.map(GameProfile::getName).orElse(var4.getName());
         var7.add(var4);
      } else {
         var8 = var4.getName();
      }

      Optional var23 = this.load(var2);
      ResourceKey var10 = (ResourceKey)var23.flatMap((var0) -> {
         DataResult var10000 = DimensionType.parseLegacy(new Dynamic(NbtOps.INSTANCE, var0.get("Dimension")));
         Logger var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         return var10000.resultOrPartial(var10001::error);
      }).orElse(Level.OVERWORLD);
      ServerLevel var11 = var5.getLevel(var10);
      ServerLevel var12;
      if (var11 == null) {
         LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", var10);
         var12 = var5.overworld();
      } else {
         var12 = var11;
      }

      var2.setServerLevel(var12);
      String var13 = var1.getLoggableAddress(var6.logIPs());
      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", new Object[]{var2.getName().getString(), var13, var2.getId(), var2.getX(), var2.getY(), var2.getZ()});
      LevelData var14 = var12.getLevelData();
      var2.loadGameTypes((CompoundTag)var23.orElse((Object)null));
      ServerGamePacketListenerImpl var15 = new ServerGamePacketListenerImpl(var5, var1, var2, var3);
      var1.setupInboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(var5.registryAccess()), var15), var15);
      GameRules var16 = var12.getGameRules();
      boolean var17 = var16.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
      boolean var18 = var16.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
      boolean var19 = var16.getBoolean(GameRules.RULE_LIMITED_CRAFTING);
      var15.send(new ClientboundLoginPacket(var2.getId(), var14.isHardcore(), var5.levelKeys(), this.getMaxPlayers(), this.viewDistance, this.simulationDistance, var18, !var17, var19, var2.createCommonSpawnInfo(var12), var6.enforceSecureProfile()));
      var15.send(new ClientboundChangeDifficultyPacket(var14.getDifficulty(), var14.isDifficultyLocked()));
      var15.send(new ClientboundPlayerAbilitiesPacket(var2.getAbilities()));
      var15.send(new ClientboundSetHeldSlotPacket(var2.getInventory().getSelectedSlot()));
      RecipeManager var20 = var5.getRecipeManager();
      var15.send(new ClientboundUpdateRecipesPacket(var20.getSynchronizedItemProperties(), var20.getSynchronizedStonecutterRecipes()));
      this.sendPlayerPermissionLevel(var2);
      var2.getStats().markAllDirty();
      var2.getRecipeBook().sendInitialRecipeBook(var2);
      this.updateEntireScoreboard(var12.getScoreboard(), var2);
      var6.invalidateStatus();
      MutableComponent var21;
      if (var2.getGameProfile().getName().equalsIgnoreCase(var8)) {
         var21 = Component.translatable("multiplayer.player.joined", var2.getDisplayName());
      } else {
         var21 = Component.translatable("multiplayer.player.joined.renamed", var2.getDisplayName(), var8);
      }

      this.broadcastSystemMessage(var21.withStyle(ChatFormatting.YELLOW), false);
      var15.teleport(var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot());
      ServerStatus var22 = var6.getStatus();
      if (var22 != null && !var3.transferred()) {
         var2.sendServerStatus(var22);
      }

      var2.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(this.players));
      this.players.add(var2);
      this.playersByUUID.put(var2.getUUID(), var2);
      this.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(var2)));
      this.sendLevelInfo(var2, var12);
      var12.addNewPlayer(var2);
      var5.getCustomBossEvents().onPlayerConnect(var2);
      this.sendActivePlayerEffects(var2);
      var23.ifPresent((var1x) -> {
         var2.loadAndSpawnEnderPearls(var1x);
         var2.loadAndSpawnParentVehicle(var1x);
      });
      var2.initInventoryMenu();
   }

   protected void updateEntireScoreboard(ServerScoreboard var1, ServerPlayer var2) {
      HashSet var3 = Sets.newHashSet();

      for(PlayerTeam var5 : var1.getPlayerTeams()) {
         var2.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(var5, true));
      }

      for(DisplaySlot var7 : DisplaySlot.values()) {
         Objective var8 = var1.getDisplayObjective(var7);
         if (var8 != null && !var3.contains(var8)) {
            List var10000 = var1.getStartTrackingPackets(var8);
            ServerGamePacketListenerImpl var10001 = var2.connection;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::send);
            var3.add(var8);
         }
      }

   }

   public void addWorldborderListener(ServerLevel var1) {
      var1.getWorldBorder().addListener(new BorderChangeListener() {
         public void onBorderSizeSet(WorldBorder var1, double var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderSizePacket(var1));
         }

         public void onBorderSizeLerping(WorldBorder var1, double var2, double var4, long var6) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderLerpSizePacket(var1));
         }

         public void onBorderCenterSet(WorldBorder var1, double var2, double var4) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderCenterPacket(var1));
         }

         public void onBorderSetWarningTime(WorldBorder var1, int var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDelayPacket(var1));
         }

         public void onBorderSetWarningBlocks(WorldBorder var1, int var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDistancePacket(var1));
         }

         public void onBorderSetDamagePerBlock(WorldBorder var1, double var2) {
         }

         public void onBorderSetDamageSafeZOne(WorldBorder var1, double var2) {
         }
      });
   }

   public Optional<CompoundTag> load(ServerPlayer var1) {
      TheGame var2 = var1.theGame();
      CompoundTag var3 = var2.getWorldData().getLoadedPlayerTag();
      Optional var4;
      if (var2.server().isSingleplayerOwner(var1.getGameProfile()) && var3 != null) {
         var4 = Optional.of(var3);
         var1.load(var3);
         LOGGER.debug("loading single player");
      } else {
         var4 = this.playerIo.load(var1);
      }

      return var4;
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

      ServerPlayerUnlocks var4 = (ServerPlayerUnlocks)this.unlocks.get(var1.getUUID());
      if (var4 != null) {
         var4.save();
      }

   }

   public void remove(ServerPlayer var1) {
      ServerLevel var2 = var1.serverLevel();
      var1.awardStat(Stats.LEAVE_GAME);
      this.save(var1);
      if (var1.isPassenger()) {
         Entity var3 = var1.getRootVehicle();
         if (var3.hasExactlyOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            var1.stopRiding();
            var3.getPassengersAndSelf().forEach((var0) -> var0.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
         }
      }

      var1.unRide();

      for(ThrownEnderpearl var4 : var1.getEnderPearls()) {
         var4.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
      }

      var2.removePlayerImmediately(var1, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
      var1.getAdvancements().stopListening();
      this.players.remove(var1);
      var1.theGame().getCustomBossEvents().onPlayerDisconnect(var1);
      UUID var6 = var1.getUUID();
      ServerPlayer var7 = (ServerPlayer)this.playersByUUID.get(var6);
      if (var7 == var1) {
         this.playersByUUID.remove(var6);
         this.stats.remove(var6);
         this.advancements.remove(var6);
         this.unlocks.remove(var6);
      }

      this.broadcastAll(new ClientboundPlayerInfoRemovePacket(List.of(var1.getUUID())));
   }

   @Nullable
   public Component canPlayerLogin(SocketAddress var1, GameProfile var2) {
      if (this.bans.isBanned(var2)) {
         UserBanListEntry var5 = (UserBanListEntry)this.bans.get(var2);
         MutableComponent var6 = Component.translatable("multiplayer.disconnect.banned.reason", var5.getReason());
         if (var5.getExpires() != null) {
            var6.append((Component)Component.translatable("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format(var5.getExpires())));
         }

         return var6;
      } else if (!this.isWhiteListed(var2)) {
         return Component.translatable("multiplayer.disconnect.not_whitelisted");
      } else if (this.ipBans.isBanned(var1)) {
         IpBanListEntry var3 = this.ipBans.get(var1);
         MutableComponent var4 = Component.translatable("multiplayer.disconnect.banned_ip.reason", var3.getReason());
         if (var3.getExpires() != null) {
            var4.append((Component)Component.translatable("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format(var3.getExpires())));
         }

         return var4;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(var2) ? Component.translatable("multiplayer.disconnect.server_full") : null;
      }
   }

   public boolean disconnectAllPlayersWithProfile(GameProfile var1) {
      UUID var2 = var1.getId();
      Set var3 = Sets.newIdentityHashSet();

      for(ServerPlayer var5 : this.players) {
         if (var5.getUUID().equals(var2)) {
            var3.add(var5);
         }
      }

      ServerPlayer var7 = (ServerPlayer)this.playersByUUID.get(var1.getId());
      if (var7 != null) {
         var3.add(var7);
      }

      for(ServerPlayer var6 : var3) {
         var6.connection.disconnect(DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
      }

      return !var3.isEmpty();
   }

   public ServerPlayer respawn(ServerPlayer var1, boolean var2, Entity.RemovalReason var3, Optional<TeleportTransition> var4) {
      this.players.remove(var1);
      var1.serverLevel().removePlayerImmediately(var1, var3);
      TeleportTransition var5 = (TeleportTransition)var4.orElseGet(() -> var1.findRespawnPositionAndUseSpawnBlock(!var2, TeleportTransition.DO_NOTHING));
      ServerLevel var6 = var5.newLevel();
      ServerPlayer var7 = new ServerPlayer(var1.theGame(), var6, var1.getGameProfile(), var1.clientInformation());
      var7.connection = var1.connection;
      var7.restoreFrom(var1, var2);
      var7.setId(var1.getId());
      var7.setMainArm(var1.getMainArm());
      if (!var5.missingRespawnBlock()) {
         var7.copyRespawnPosition(var1);
      }

      for(String var9 : var1.getTags()) {
         var7.addTag(var9);
      }

      Vec3 var16 = var5.position();
      var7.snapTo(var16.x, var16.y, var16.z, var5.yRot(), var5.xRot());
      if (var5.missingRespawnBlock()) {
         var7.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
      }

      int var17 = var2 ? 1 : 0;
      ServerLevel var10 = var7.serverLevel();
      LevelData var11 = var10.getLevelData();
      var7.connection.send(new ClientboundRespawnPacket(var7.createCommonSpawnInfo(var10), (byte)var17));
      var7.connection.teleport(var7.getX(), var7.getY(), var7.getZ(), var7.getYRot(), var7.getXRot());
      var7.connection.send(new ClientboundSetDefaultSpawnPositionPacket(var6.getSharedSpawnPos(), var6.getSharedSpawnAngle()));
      var7.connection.send(new ClientboundChangeDifficultyPacket(var11.getDifficulty(), var11.isDifficultyLocked()));
      var7.connection.send(new ClientboundSetExperiencePacket(var7.experienceProgress, var7.totalExperience, var7.experienceLevel));
      this.sendActivePlayerEffects(var7);
      this.sendLevelInfo(var7, var6);
      this.sendPlayerPermissionLevel(var7);
      var6.addRespawnedPlayer(var7);
      this.players.add(var7);
      this.playersByUUID.put(var7.getUUID(), var7);
      var7.initInventoryMenu();
      var7.setHealth(var7.getHealth());
      ServerPlayer.RespawnConfig var12 = var7.getRespawnConfig();
      if (!var2 && var12 != null) {
         ServerLevel var13 = var6.theGame().getLevel(var12.dimension());
         if (var13 != null) {
            BlockPos var14 = var12.pos();
            BlockState var15 = var13.getBlockState(var14);
            if (var15.is(Blocks.RESPAWN_ANCHOR)) {
               var7.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double)var14.getX(), (double)var14.getY(), (double)var14.getZ(), 1.0F, 1.0F, var6.getRandom().nextLong()));
            }
         }
      }

      return var7;
   }

   public void sendActivePlayerEffects(ServerPlayer var1) {
      this.sendActiveEffects(var1, var1.connection);
   }

   public void sendActiveEffects(LivingEntity var1, ServerGamePacketListenerImpl var2) {
      for(MobEffectInstance var4 : var1.getActiveEffects()) {
         var2.send(new ClientboundUpdateMobEffectPacket(var1.getId(), var4, false));
      }

   }

   public void sendPlayerPermissionLevel(ServerPlayer var1) {
      GameProfile var2 = var1.getGameProfile();
      int var3 = var1.theGame().server().getProfilePermissions(var2);
      this.sendPlayerPermissionLevel(var1, var3);
   }

   public void tick() {
      if (++this.sendAllPlayerInfoIn > 600) {
         this.broadcastAll(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY), this.players));
         this.sendAllPlayerInfoIn = 0;
      }

   }

   public void broadcastAll(Packet<?> var1) {
      for(ServerPlayer var3 : this.players) {
         var3.connection.send(var1);
      }

   }

   public void broadcastAll(Packet<?> var1, ResourceKey<Level> var2) {
      for(ServerPlayer var4 : this.players) {
         if (var4.level().dimension() == var2) {
            var4.connection.send(var1);
         }
      }

   }

   public void broadcastSystemToTeam(Player var1, Component var2) {
      PlayerTeam var3 = var1.getTeam();
      if (var3 != null) {
         for(String var6 : ((Team)var3).getPlayers()) {
            ServerPlayer var7 = this.getPlayerByName(var6);
            if (var7 != null && var7 != var1) {
               var7.sendSystemMessage(var2);
            }
         }

      }
   }

   public void broadcastSystemToAllExceptTeam(Player var1, Component var2) {
      PlayerTeam var3 = var1.getTeam();
      if (var3 == null) {
         this.broadcastSystemMessage(var2, false);
      } else {
         for(int var4 = 0; var4 < this.players.size(); ++var4) {
            ServerPlayer var5 = (ServerPlayer)this.players.get(var4);
            if (var5.getTeam() != var3) {
               var5.sendSystemMessage(var2);
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
      this.ops.add(new ServerOpListEntry(var1, this.theGame().server().getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(var1)));
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

      var1.theGame().getCommands().sendCommands(var1);
   }

   public boolean isWhiteListed(GameProfile var1) {
      return !this.doWhiteList || this.ops.contains(var1) || this.whitelist.contains(var1);
   }

   public boolean isOp(GameProfile var1) {
      return this.ops.contains(var1) || this.theGame().server().isSingleplayerOwner(var1) && this.theGame().getWorldData().isAllowCommands() || this.allowCommandsForAllPlayers;
   }

   @Nullable
   public ServerPlayer getPlayerByName(String var1) {
      int var2 = this.players.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         ServerPlayer var4 = (ServerPlayer)this.players.get(var3);
         if (var4.getGameProfile().getName().equalsIgnoreCase(var1)) {
            return var4;
         }
      }

      return null;
   }

   public void broadcast(@Nullable Player var1, double var2, double var4, double var6, double var8, ResourceKey<Level> var10, Packet<?> var11) {
      for(int var12 = 0; var12 < this.players.size(); ++var12) {
         ServerPlayer var13 = (ServerPlayer)this.players.get(var12);
         if (var13 != var1 && var13.level().dimension() == var10) {
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
      TheGame var3 = var2.theGame();
      WorldBorder var4 = var3.overworld().getWorldBorder();
      var1.connection.send(new ClientboundInitializeBorderPacket(var4));
      var1.connection.send(new ClientboundSetTimePacket(var2.getGameTime(), var2.getDayTime(), var2.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      var1.connection.send(new ClientboundSetDefaultSpawnPositionPacket(var2.getSharedSpawnPos(), var2.getSharedSpawnAngle()));
      if (var2.isRaining()) {
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, var2.getRainLevel(1.0F)));
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, var2.getThunderLevel(1.0F)));
      }

      var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START, 0.0F));
      var2.tickRateManager().updateJoiningPlayer(var1);
   }

   public void sendAllPlayerInfo(ServerPlayer var1) {
      var1.inventoryMenu.sendAllDataToRemote();
      var1.resetSentInfo();
      var1.connection.send(new ClientboundSetHeldSlotPacket(var1.getInventory().getSelectedSlot()));
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

      for(ServerPlayer var4 : this.players) {
         if (var4.getIpAddress().equals(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   @Nullable
   public CompoundTag getSingleplayerData() {
      return null;
   }

   public void setAllowCommandsForAllPlayers(boolean var1) {
      this.allowCommandsForAllPlayers = var1;
   }

   public void removeAll() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         ((ServerPlayer)this.players.get(var1)).connection.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void broadcastSystemMessage(Component var1, boolean var2) {
      this.broadcastSystemMessage(var1, (var1x) -> var1, var2);
   }

   public void broadcastSystemMessage(Component var1, Function<ServerPlayer, Component> var2, boolean var3) {
      this.theGame().server().sendSystemMessage(var1);

      for(ServerPlayer var5 : this.players) {
         Component var6 = (Component)var2.apply(var5);
         if (var6 != null) {
            var5.sendSystemMessage(var6, var3);
         }
      }

   }

   public void broadcastChatMessage(PlayerChatMessage var1, CommandSourceStack var2, ChatType.Bound var3) {
      Objects.requireNonNull(var2);
      this.broadcastChatMessage(var1, var2::shouldFilterMessageTo, var2.getPlayer(), var3);
   }

   public void broadcastChatMessage(PlayerChatMessage var1, ServerPlayer var2, ChatType.Bound var3) {
      Objects.requireNonNull(var2);
      this.broadcastChatMessage(var1, var2::shouldFilterMessageTo, var2, var3);
   }

   private void broadcastChatMessage(PlayerChatMessage var1, Predicate<ServerPlayer> var2, @Nullable ServerPlayer var3, ChatType.Bound var4) {
      boolean var5 = this.verifyChatTrusted(var1);
      this.theGame().server().logChatMessage(var1.decoratedContent(), var4, var5 ? null : "Not Secure");
      OutgoingChatMessage var6 = OutgoingChatMessage.create(var1);
      boolean var7 = false;

      for(ServerPlayer var9 : this.players) {
         boolean var10 = var2.test(var9);
         var9.sendChatMessage(var6, var10, var4);
         var7 |= var10 && var1.isFullyFiltered();
      }

      if (var7 && var3 != null) {
         var3.sendSystemMessage(CHAT_FILTERED_FULL);
      }

   }

   private boolean verifyChatTrusted(PlayerChatMessage var1) {
      return var1.hasSignature() && !var1.hasExpiredServer(Instant.now());
   }

   public ServerStatsCounter getPlayerStats(ServerPlayer var1) {
      UUID var2 = var1.getUUID();
      ServerStatsCounter var3 = (ServerStatsCounter)this.stats.get(var2);
      MinecraftServer var4 = var1.theGame().server();
      if (var3 == null) {
         File var5 = var4.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
         File var6 = new File(var5, String.valueOf(var2) + ".json");
         if (!var6.exists()) {
            File var7 = new File(var5, var1.getName().getString() + ".json");
            Path var8 = var7.toPath();
            if (FileUtil.isPathNormalized(var8) && FileUtil.isPathPortable(var8) && var8.startsWith(var5.getPath()) && var7.isFile()) {
               var7.renameTo(var6);
            }
         }

         var3 = new ServerStatsCounter(var4, var6);
         this.stats.put(var2, var3);
      }

      return var3;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayer var1) {
      UUID var2 = var1.getUUID();
      MinecraftServer var3 = var1.theGame().server();
      PlayerAdvancements var4 = (PlayerAdvancements)this.advancements.get(var2);
      if (var4 == null) {
         Path var5 = var3.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(String.valueOf(var2) + ".json");
         var4 = new PlayerAdvancements(var3.getFixerUpper(), this, var1.theGame().getAdvancements(), var5, var1);
         this.advancements.put(var2, var4);
      }

      var4.setPlayer(var1);
      return var4;
   }

   public ServerPlayerUnlocks getPlayerUnlocks(ServerPlayer var1) {
      UUID var2 = var1.getUUID();
      MinecraftServer var3 = var1.theGame().server();
      ServerPlayerUnlocks var4 = (ServerPlayerUnlocks)this.unlocks.get(var2);
      if (var4 == null) {
         Path var5 = var3.getWorldPath(LevelResource.PLAYER_UNLOCKS_DIR).resolve(String.valueOf(var2) + ".json");
         var4 = new ServerPlayerUnlocks(var3.getFixerUpper(), this, var5, var1);
         this.unlocks.put(var2, var4);
      }

      var4.setPlayer(var1);
      return var4;
   }

   public void setViewDistance(int var1) {
      this.viewDistance = var1;
      this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket(var1));

      for(ServerLevel var3 : this.theGame().getAllLevels()) {
         if (var3 != null) {
            var3.getChunkSource().setViewDistance(var1);
         }
      }

   }

   protected TheGame theGame() {
      return this.theGame;
   }

   public void setSimulationDistance(int var1) {
      this.simulationDistance = var1;
      this.broadcastAll(new ClientboundSetSimulationDistancePacket(var1));

      for(ServerLevel var3 : this.theGame().getAllLevels()) {
         if (var3 != null) {
            var3.getChunkSource().setSimulationDistance(var1);
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

   public void reloadResources(TheGame var1) {
      for(PlayerAdvancements var3 : this.advancements.values()) {
         var3.reload(var1.getAdvancements());
      }

      for(ServerPlayerUnlocks var8 : this.unlocks.values()) {
         var8.reload();
      }

      this.broadcastAll(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(var1.registries())));
      RecipeManager var7 = var1.getRecipeManager();
      ClientboundUpdateRecipesPacket var9 = new ClientboundUpdateRecipesPacket(var7.getSynchronizedItemProperties(), var7.getSynchronizedStonecutterRecipes());

      for(ServerPlayer var5 : this.players) {
         var5.connection.send(var9);
         var5.getRecipeBook().sendInitialRecipeBook(var5);
      }

   }

   public boolean isAllowCommandsForAllPlayers() {
      return this.allowCommandsForAllPlayers;
   }

   public void syncContainerMenuData(MenuType<? extends AbstractContainerMenu> var1, List<Integer> var2) {
      for(ServerPlayer var4 : this.players) {
         if (var4.containerMenu.getTypeRaw() == var1) {
            var4.containerMenu.updateData(var2);
            var4.connection.send(new ClientboundUpdateScreenPacket(var1, var2));
         }
      }

   }
}
