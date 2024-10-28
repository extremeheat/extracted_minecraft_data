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
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
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
import net.minecraft.core.LayeredRegistryAccess;
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
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ClientInformation;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
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
   private final LayeredRegistryAccess<RegistryLayer> registries;
   protected final int maxPlayers;
   private int viewDistance;
   private int simulationDistance;
   private boolean allowCommandsForAllPlayers;
   private static final boolean ALLOW_LOGOUTIVATOR = false;
   private int sendAllPlayerInfoIn;

   public PlayerList(MinecraftServer var1, LayeredRegistryAccess<RegistryLayer> var2, PlayerDataStorage var3, int var4) {
      super();
      this.bans = new UserBanList(USERBANLIST_FILE);
      this.ipBans = new IpBanList(IPBANLIST_FILE);
      this.ops = new ServerOpList(OPLIST_FILE);
      this.whitelist = new UserWhiteList(WHITELIST_FILE);
      this.stats = Maps.newHashMap();
      this.advancements = Maps.newHashMap();
      this.server = var1;
      this.registries = var2;
      this.maxPlayers = var4;
      this.playerIo = var3;
   }

   public void placeNewPlayer(Connection var1, ServerPlayer var2, CommonListenerCookie var3) {
      GameProfile var4 = var2.getGameProfile();
      GameProfileCache var5 = this.server.getProfileCache();
      String var6;
      Optional var7;
      if (var5 != null) {
         var7 = var5.get(var4.getId());
         var6 = (String)var7.map(GameProfile::getName).orElse(var4.getName());
         var5.add(var4);
      } else {
         var6 = var4.getName();
      }

      var7 = this.load(var2);
      ResourceKey var8 = (ResourceKey)var7.flatMap((var0) -> {
         DataResult var10000 = DimensionType.parseLegacy(new Dynamic(NbtOps.INSTANCE, var0.get("Dimension")));
         Logger var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         return var10000.resultOrPartial(var10001::error);
      }).orElse(Level.OVERWORLD);
      ServerLevel var9 = this.server.getLevel(var8);
      ServerLevel var10;
      if (var9 == null) {
         LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", var8);
         var10 = this.server.overworld();
      } else {
         var10 = var9;
      }

      var2.setServerLevel(var10);
      String var11 = var1.getLoggableAddress(this.server.logIPs());
      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", new Object[]{var2.getName().getString(), var11, var2.getId(), var2.getX(), var2.getY(), var2.getZ()});
      LevelData var12 = var10.getLevelData();
      var2.loadGameTypes((CompoundTag)var7.orElse((Object)null));
      ServerGamePacketListenerImpl var13 = new ServerGamePacketListenerImpl(this.server, var1, var2, var3);
      var1.setupInboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(this.server.registryAccess())), var13);
      GameRules var14 = var10.getGameRules();
      boolean var15 = var14.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
      boolean var16 = var14.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
      boolean var17 = var14.getBoolean(GameRules.RULE_LIMITED_CRAFTING);
      var13.send(new ClientboundLoginPacket(var2.getId(), var12.isHardcore(), this.server.levelKeys(), this.getMaxPlayers(), this.viewDistance, this.simulationDistance, var16, !var15, var17, var2.createCommonSpawnInfo(var10), this.server.enforceSecureProfile()));
      var13.send(new ClientboundChangeDifficultyPacket(var12.getDifficulty(), var12.isDifficultyLocked()));
      var13.send(new ClientboundPlayerAbilitiesPacket(var2.getAbilities()));
      var13.send(new ClientboundSetCarriedItemPacket(var2.getInventory().selected));
      var13.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getOrderedRecipes()));
      this.sendPlayerPermissionLevel(var2);
      var2.getStats().markAllDirty();
      var2.getRecipeBook().sendInitialRecipeBook(var2);
      this.updateEntireScoreboard(var10.getScoreboard(), var2);
      this.server.invalidateStatus();
      MutableComponent var18;
      if (var2.getGameProfile().getName().equalsIgnoreCase(var6)) {
         var18 = Component.translatable("multiplayer.player.joined", var2.getDisplayName());
      } else {
         var18 = Component.translatable("multiplayer.player.joined.renamed", var2.getDisplayName(), var6);
      }

      this.broadcastSystemMessage(var18.withStyle(ChatFormatting.YELLOW), false);
      var13.teleport(var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot());
      ServerStatus var19 = this.server.getStatus();
      if (var19 != null && !var3.transferred()) {
         var2.sendServerStatus(var19);
      }

      var2.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(this.players));
      this.players.add(var2);
      this.playersByUUID.put(var2.getUUID(), var2);
      this.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(var2)));
      this.sendLevelInfo(var2, var10);
      var10.addNewPlayer(var2);
      this.server.getCustomBossEvents().onPlayerConnect(var2);
      this.sendActivePlayerEffects(var2);
      if (var7.isPresent() && ((CompoundTag)var7.get()).contains("RootVehicle", 10)) {
         CompoundTag var20 = ((CompoundTag)var7.get()).getCompound("RootVehicle");
         Entity var21 = EntityType.loadEntityRecursive(var20.getCompound("Entity"), var10, (var1x) -> {
            return !var10.addWithUUID(var1x) ? null : var1x;
         });
         if (var21 != null) {
            UUID var22;
            if (var20.hasUUID("Attach")) {
               var22 = var20.getUUID("Attach");
            } else {
               var22 = null;
            }

            Iterator var23;
            Entity var24;
            if (var21.getUUID().equals(var22)) {
               var2.startRiding(var21, true);
            } else {
               var23 = var21.getIndirectPassengers().iterator();

               while(var23.hasNext()) {
                  var24 = (Entity)var23.next();
                  if (var24.getUUID().equals(var22)) {
                     var2.startRiding(var24, true);
                     break;
                  }
               }
            }

            if (!var2.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               var21.discard();
               var23 = var21.getIndirectPassengers().iterator();

               while(var23.hasNext()) {
                  var24 = (Entity)var23.next();
                  var24.discard();
               }
            }
         }
      }

      var2.initInventoryMenu();
   }

   protected void updateEntireScoreboard(ServerScoreboard var1, ServerPlayer var2) {
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = var1.getPlayerTeams().iterator();

      while(var4.hasNext()) {
         PlayerTeam var5 = (PlayerTeam)var4.next();
         var2.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(var5, true));
      }

      DisplaySlot[] var12 = DisplaySlot.values();
      int var13 = var12.length;

      for(int var6 = 0; var6 < var13; ++var6) {
         DisplaySlot var7 = var12[var6];
         Objective var8 = var1.getDisplayObjective(var7);
         if (var8 != null && !var3.contains(var8)) {
            List var9 = var1.getStartTrackingPackets(var8);
            Iterator var10 = var9.iterator();

            while(var10.hasNext()) {
               Packet var11 = (Packet)var10.next();
               var2.connection.send(var11);
            }

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
      CompoundTag var2 = this.server.getWorldData().getLoadedPlayerTag();
      Optional var3;
      if (this.server.isSingleplayerOwner(var1.getGameProfile()) && var2 != null) {
         var3 = Optional.of(var2);
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
      ServerLevel var2 = var1.serverLevel();
      var1.awardStat(Stats.LEAVE_GAME);
      this.save(var1);
      if (var1.isPassenger()) {
         Entity var3 = var1.getRootVehicle();
         if (var3.hasExactlyOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            var1.stopRiding();
            var3.getPassengersAndSelf().forEach((var0) -> {
               var0.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
            });
         }
      }

      var1.unRide();
      var2.removePlayerImmediately(var1, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
      var1.getAdvancements().stopListening();
      this.players.remove(var1);
      this.server.getCustomBossEvents().onPlayerDisconnect(var1);
      UUID var5 = var1.getUUID();
      ServerPlayer var4 = (ServerPlayer)this.playersByUUID.get(var5);
      if (var4 == var1) {
         this.playersByUUID.remove(var5);
         this.stats.remove(var5);
         this.advancements.remove(var5);
      }

      this.broadcastAll(new ClientboundPlayerInfoRemovePacket(List.of(var1.getUUID())));
   }

   @Nullable
   public Component canPlayerLogin(SocketAddress var1, GameProfile var2) {
      MutableComponent var4;
      if (this.bans.isBanned(var2)) {
         UserBanListEntry var5 = (UserBanListEntry)this.bans.get(var2);
         var4 = Component.translatable("multiplayer.disconnect.banned.reason", var5.getReason());
         if (var5.getExpires() != null) {
            var4.append((Component)Component.translatable("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format(var5.getExpires())));
         }

         return var4;
      } else if (!this.isWhiteListed(var2)) {
         return Component.translatable("multiplayer.disconnect.not_whitelisted");
      } else if (this.ipBans.isBanned(var1)) {
         IpBanListEntry var3 = this.ipBans.get(var1);
         var4 = Component.translatable("multiplayer.disconnect.banned_ip.reason", var3.getReason());
         if (var3.getExpires() != null) {
            var4.append((Component)Component.translatable("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format(var3.getExpires())));
         }

         return var4;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(var2) ? Component.translatable("multiplayer.disconnect.server_full") : null;
      }
   }

   public ServerPlayer getPlayerForLogin(GameProfile var1, ClientInformation var2) {
      return new ServerPlayer(this.server, this.server.overworld(), var1, var2);
   }

   public boolean disconnectAllPlayersWithProfile(GameProfile var1) {
      UUID var2 = var1.getId();
      Set var3 = Sets.newIdentityHashSet();
      Iterator var4 = this.players.iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         if (var5.getUUID().equals(var2)) {
            var3.add(var5);
         }
      }

      ServerPlayer var7 = (ServerPlayer)this.playersByUUID.get(var1.getId());
      if (var7 != null) {
         var3.add(var7);
      }

      Iterator var8 = var3.iterator();

      while(var8.hasNext()) {
         ServerPlayer var6 = (ServerPlayer)var8.next();
         var6.connection.disconnect(DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
      }

      return !var3.isEmpty();
   }

   public ServerPlayer respawn(ServerPlayer var1, boolean var2, Entity.RemovalReason var3) {
      this.players.remove(var1);
      var1.serverLevel().removePlayerImmediately(var1, var3);
      DimensionTransition var4 = var1.findRespawnPositionAndUseSpawnBlock(var2, DimensionTransition.DO_NOTHING);
      ServerLevel var5 = var4.newLevel();
      ServerPlayer var6 = new ServerPlayer(this.server, var5, var1.getGameProfile(), var1.clientInformation());
      var6.connection = var1.connection;
      var6.restoreFrom(var1, var2);
      var6.setId(var1.getId());
      var6.setMainArm(var1.getMainArm());
      if (!var4.missingRespawnBlock()) {
         var6.copyRespawnPosition(var1);
      }

      Iterator var7 = var1.getTags().iterator();

      while(var7.hasNext()) {
         String var8 = (String)var7.next();
         var6.addTag(var8);
      }

      Vec3 var13 = var4.pos();
      var6.moveTo(var13.x, var13.y, var13.z, var4.yRot(), var4.xRot());
      if (var4.missingRespawnBlock()) {
         var6.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
      }

      int var14 = var2 ? 1 : 0;
      ServerLevel var9 = var6.serverLevel();
      LevelData var10 = var9.getLevelData();
      var6.connection.send(new ClientboundRespawnPacket(var6.createCommonSpawnInfo(var9), (byte)var14));
      var6.connection.teleport(var6.getX(), var6.getY(), var6.getZ(), var6.getYRot(), var6.getXRot());
      var6.connection.send(new ClientboundSetDefaultSpawnPositionPacket(var5.getSharedSpawnPos(), var5.getSharedSpawnAngle()));
      var6.connection.send(new ClientboundChangeDifficultyPacket(var10.getDifficulty(), var10.isDifficultyLocked()));
      var6.connection.send(new ClientboundSetExperiencePacket(var6.experienceProgress, var6.totalExperience, var6.experienceLevel));
      this.sendActivePlayerEffects(var6);
      this.sendLevelInfo(var6, var5);
      this.sendPlayerPermissionLevel(var6);
      var5.addRespawnedPlayer(var6);
      this.players.add(var6);
      this.playersByUUID.put(var6.getUUID(), var6);
      var6.initInventoryMenu();
      var6.setHealth(var6.getHealth());
      if (!var2) {
         BlockPos var11 = BlockPos.containing(var4.pos());
         BlockState var12 = var5.getBlockState(var11);
         if (var12.is(Blocks.RESPAWN_ANCHOR)) {
            var6.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double)var11.getX(), (double)var11.getY(), (double)var11.getZ(), 1.0F, 1.0F, var5.getRandom().nextLong()));
         }
      }

      return var6;
   }

   public void sendActivePlayerEffects(ServerPlayer var1) {
      this.sendActiveEffects(var1, var1.connection);
   }

   public void sendActiveEffects(LivingEntity var1, ServerGamePacketListenerImpl var2) {
      Iterator var3 = var1.getActiveEffects().iterator();

      while(var3.hasNext()) {
         MobEffectInstance var4 = (MobEffectInstance)var3.next();
         var2.send(new ClientboundUpdateMobEffectPacket(var1.getId(), var4, false));
      }

   }

   public void sendPlayerPermissionLevel(ServerPlayer var1) {
      GameProfile var2 = var1.getGameProfile();
      int var3 = this.server.getProfilePermissions(var2);
      this.sendPlayerPermissionLevel(var1, var3);
   }

   public void tick() {
      if (++this.sendAllPlayerInfoIn > 600) {
         this.broadcastAll(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY), this.players));
         this.sendAllPlayerInfoIn = 0;
      }

   }

   public void broadcastAll(Packet<?> var1) {
      Iterator var2 = this.players.iterator();

      while(var2.hasNext()) {
         ServerPlayer var3 = (ServerPlayer)var2.next();
         var3.connection.send(var1);
      }

   }

   public void broadcastAll(Packet<?> var1, ResourceKey<Level> var2) {
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         if (var4.level().dimension() == var2) {
            var4.connection.send(var1);
         }
      }

   }

   public void broadcastSystemToTeam(Player var1, Component var2) {
      PlayerTeam var3 = var1.getTeam();
      if (var3 != null) {
         Collection var4 = ((Team)var3).getPlayers();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
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
      return this.ops.contains(var1) || this.server.isSingleplayerOwner(var1) && this.server.getWorldData().isAllowCommands() || this.allowCommandsForAllPlayers;
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
      WorldBorder var3 = this.server.overworld().getWorldBorder();
      var1.connection.send(new ClientboundInitializeBorderPacket(var3));
      var1.connection.send(new ClientboundSetTimePacket(var2.getGameTime(), var2.getDayTime(), var2.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      var1.connection.send(new ClientboundSetDefaultSpawnPositionPacket(var2.getSharedSpawnPos(), var2.getSharedSpawnAngle()));
      if (var2.isRaining()) {
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, var2.getRainLevel(1.0F)));
         var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, var2.getThunderLevel(1.0F)));
      }

      var1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START, 0.0F));
      this.server.tickRateManager().updateJoiningPlayer(var1);
   }

   public void sendAllPlayerInfo(ServerPlayer var1) {
      var1.inventoryMenu.sendAllDataToRemote();
      var1.resetSentInfo();
      var1.connection.send(new ClientboundSetCarriedItemPacket(var1.getInventory().selected));
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

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
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
      this.broadcastSystemMessage(var1, (var1x) -> {
         return var1;
      }, var2);
   }

   public void broadcastSystemMessage(Component var1, Function<ServerPlayer, Component> var2, boolean var3) {
      this.server.sendSystemMessage(var1);
      Iterator var4 = this.players.iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
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
      this.server.logChatMessage(var1.decoratedContent(), var4, var5 ? null : "Not Secure");
      OutgoingChatMessage var6 = OutgoingChatMessage.create(var1);
      boolean var7 = false;

      boolean var10;
      for(Iterator var8 = this.players.iterator(); var8.hasNext(); var7 |= var10 && var1.isFullyFiltered()) {
         ServerPlayer var9 = (ServerPlayer)var8.next();
         var10 = var2.test(var9);
         var9.sendChatMessage(var6, var10, var4);
      }

      if (var7 && var3 != null) {
         var3.sendSystemMessage(CHAT_FILTERED_FULL);
      }

   }

   private boolean verifyChatTrusted(PlayerChatMessage var1) {
      return var1.hasSignature() && !var1.hasExpiredServer(Instant.now());
   }

   public ServerStatsCounter getPlayerStats(Player var1) {
      UUID var2 = var1.getUUID();
      ServerStatsCounter var3 = (ServerStatsCounter)this.stats.get(var2);
      if (var3 == null) {
         File var4 = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
         File var5 = new File(var4, String.valueOf(var2) + ".json");
         if (!var5.exists()) {
            File var6 = new File(var4, var1.getName().getString() + ".json");
            Path var7 = var6.toPath();
            if (FileUtil.isPathNormalized(var7) && FileUtil.isPathPortable(var7) && var7.startsWith(var4.getPath()) && var6.isFile()) {
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
         Path var4 = this.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(String.valueOf(var2) + ".json");
         var3 = new PlayerAdvancements(this.server.getFixerUpper(), this, this.server.getAdvancements(), var4, var1);
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

   public void setSimulationDistance(int var1) {
      this.simulationDistance = var1;
      this.broadcastAll(new ClientboundSetSimulationDistancePacket(var1));
      Iterator var2 = this.server.getAllLevels().iterator();

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
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

   public void reloadResources() {
      Iterator var1 = this.advancements.values().iterator();

      while(var1.hasNext()) {
         PlayerAdvancements var2 = (PlayerAdvancements)var1.next();
         var2.reload(this.server.getAdvancements());
      }

      this.broadcastAll(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
      ClientboundUpdateRecipesPacket var4 = new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getOrderedRecipes());
      Iterator var5 = this.players.iterator();

      while(var5.hasNext()) {
         ServerPlayer var3 = (ServerPlayer)var5.next();
         var3.connection.send(var4);
         var3.getRecipeBook().sendInitialRecipeBook(var3);
      }

   }

   public boolean isAllowCommandsForAllPlayers() {
      return this.allowCommandsForAllPlayers;
   }
}
