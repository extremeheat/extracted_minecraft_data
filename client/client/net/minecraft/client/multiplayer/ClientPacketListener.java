package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SnifferSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.LocalChatSession;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.GameEventDebugPayload;
import net.minecraft.network.protocol.common.custom.GameEventListenerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestClearMarkersDebugPayload;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import net.minecraft.network.protocol.common.custom.HiveDebugPayload;
import net.minecraft.network.protocol.common.custom.NeighborUpdatesDebugPayload;
import net.minecraft.network.protocol.common.custom.PathfindingDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiAddedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiRemovedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiTicketCountDebugPayload;
import net.minecraft.network.protocol.common.custom.RaidsDebugPayload;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.network.protocol.common.custom.VillageSectionsDebugPayload;
import net.minecraft.network.protocol.common.custom.WorldGenAttemptDebugPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStepPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Crypt;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ClientPacketListener extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component UNSECURE_SERVER_TOAST_TITLE = Component.translatable("multiplayer.unsecureserver.toast.title");
   private static final Component UNSERURE_SERVER_TOAST = Component.translatable("multiplayer.unsecureserver.toast");
   private static final Component INVALID_PACKET = Component.translatable("multiplayer.disconnect.invalid_packet");
   private static final Component CHAT_VALIDATION_FAILED_ERROR = Component.translatable("multiplayer.disconnect.chat_validation_failed");
   private static final Component RECONFIGURE_SCREEN_MESSAGE = Component.translatable("connect.reconfiguring");
   private static final int PENDING_OFFSET_THRESHOLD = 64;
   private final GameProfile localGameProfile;
   private ClientLevel level;
   private ClientLevel.ClientLevelData levelData;
   private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
   private final Set<PlayerInfo> listedPlayers = new ReferenceOpenHashSet();
   private final ClientAdvancements advancements;
   private final ClientSuggestionProvider suggestionsProvider;
   private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
   private int serverChunkRadius = 3;
   private int serverSimulationDistance = 3;
   private final RandomSource random = RandomSource.createThreadSafe();
   private CommandDispatcher<SharedSuggestionProvider> commands = new CommandDispatcher();
   private final RecipeManager recipeManager;
   private final UUID id = UUID.randomUUID();
   private Set<ResourceKey<Level>> levels;
   private final RegistryAccess.Frozen registryAccess;
   private final FeatureFlagSet enabledFeatures;
   private final PotionBrewing potionBrewing;
   @Nullable
   private LocalChatSession chatSession;
   private SignedMessageChain.Encoder signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
   private LastSeenMessagesTracker lastSeenMessages = new LastSeenMessagesTracker(20);
   private MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
   private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
   private final PingDebugMonitor pingDebugMonitor;
   private final DebugSampleSubscriber debugSampleSubscriber;
   @Nullable
   private LevelLoadStatusManager levelLoadStatusManager;
   private boolean serverEnforcesSecureChat;
   private boolean seenInsecureChatWarning = false;
   private volatile boolean closed;
   private final Scoreboard scoreboard = new Scoreboard();

   public ClientPacketListener(Minecraft var1, Connection var2, CommonListenerCookie var3) {
      super(var1, var2, var3);
      this.localGameProfile = var3.localGameProfile();
      this.registryAccess = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
      this.advancements = new ClientAdvancements(var1, this.telemetryManager);
      this.suggestionsProvider = new ClientSuggestionProvider(this, var1);
      this.pingDebugMonitor = new PingDebugMonitor(this, var1.getDebugOverlay().getPingLogger());
      this.recipeManager = new RecipeManager(this.registryAccess);
      this.debugSampleSubscriber = new DebugSampleSubscriber(this, var1.getDebugOverlay());
      if (var3.chatState() != null) {
         var1.gui.getChat().restoreState(var3.chatState());
      }

      this.potionBrewing = PotionBrewing.bootstrap(this.enabledFeatures);
   }

   public ClientSuggestionProvider getSuggestionsProvider() {
      return this.suggestionsProvider;
   }

   public void close() {
      this.closed = true;
      this.clearLevel();
      this.telemetryManager.onDisconnect();
   }

   public void clearLevel() {
      this.level = null;
      this.levelLoadStatusManager = null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   @Override
   public void handleLogin(ClientboundLoginPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
      CommonPlayerSpawnInfo var2 = var1.commonPlayerSpawnInfo();
      ArrayList var3 = Lists.newArrayList(var1.levels());
      Collections.shuffle(var3);
      this.levels = Sets.newLinkedHashSet(var3);
      ResourceKey var4 = var2.dimension();
      Holder var5 = var2.dimensionType();
      this.serverChunkRadius = var1.chunkRadius();
      this.serverSimulationDistance = var1.simulationDistance();
      boolean var6 = var2.isDebug();
      boolean var7 = var2.isFlat();
      ClientLevel.ClientLevelData var8 = new ClientLevel.ClientLevelData(Difficulty.NORMAL, var1.hardcore(), var7);
      this.levelData = var8;
      this.level = new ClientLevel(
         this,
         var8,
         var4,
         var5,
         this.serverChunkRadius,
         this.serverSimulationDistance,
         this.minecraft::getProfiler,
         this.minecraft.levelRenderer,
         var6,
         var2.seed()
      );
      this.minecraft.setLevel(this.level, ReceivingLevelScreen.Reason.OTHER);
      if (this.minecraft.player == null) {
         this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
         this.minecraft.player.setYRot(-180.0F);
         if (this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
         }
      }

      this.minecraft.debugRenderer.clear();
      this.minecraft.player.resetPos();
      this.minecraft.player.setId(var1.playerId());
      this.level.addEntity(this.minecraft.player);
      this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
      this.minecraft.cameraEntity = this.minecraft.player;
      this.startWaitingForNewLevel(this.minecraft.player, this.level, ReceivingLevelScreen.Reason.OTHER);
      this.minecraft.player.setReducedDebugInfo(var1.reducedDebugInfo());
      this.minecraft.player.setShowDeathScreen(var1.showDeathScreen());
      this.minecraft.player.setDoLimitedCrafting(var1.doLimitedCrafting());
      this.minecraft.player.setLastDeathLocation(var2.lastDeathLocation());
      this.minecraft.player.setPortalCooldown(var2.portalCooldown());
      this.minecraft.gameMode.setLocalMode(var2.gameType(), var2.previousGameType());
      this.minecraft.options.setServerRenderDistance(var1.chunkRadius());
      this.chatSession = null;
      this.lastSeenMessages = new LastSeenMessagesTracker(20);
      this.messageSignatureCache = MessageSignatureCache.createDefault();
      if (this.connection.isEncrypted()) {
         this.minecraft.getProfileKeyPairManager().prepareKeyPair().thenAcceptAsync(var1x -> var1x.ifPresent(this::setKeyPair), this.minecraft);
      }

      this.telemetryManager.onPlayerInfoReceived(var2.gameType(), var1.hardcore());
      this.minecraft.quickPlayLog().log(this.minecraft);
      this.serverEnforcesSecureChat = var1.enforcesSecureChat();
      if (this.serverData != null && !this.seenInsecureChatWarning && !this.enforcesSecureChat()) {
         SystemToast var9 = SystemToast.multiline(
            this.minecraft, SystemToast.SystemToastId.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSERURE_SERVER_TOAST
         );
         this.minecraft.getToasts().addToast(var9);
         this.seenInsecureChatWarning = true;
      }
   }

   @Override
   public void handleAddEntity(ClientboundAddEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.createEntityFromPacket(var1);
      if (var2 != null) {
         var2.recreateFromPacket(var1);
         this.level.addEntity(var2);
         this.postAddEntitySoundInstance(var2);
      } else {
         LOGGER.warn("Skipping Entity with id {}", var1.getType());
      }
   }

   @Nullable
   private Entity createEntityFromPacket(ClientboundAddEntityPacket var1) {
      EntityType var2 = var1.getType();
      if (var2 == EntityType.PLAYER) {
         PlayerInfo var3 = this.getPlayerInfo(var1.getUUID());
         if (var3 == null) {
            LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", var1.getUUID());
            return null;
         } else {
            return new RemotePlayer(this.level, var3.getProfile());
         }
      } else {
         return var2.create(this.level);
      }
   }

   private void postAddEntitySoundInstance(Entity var1) {
      if (var1 instanceof AbstractMinecart var2) {
         this.minecraft.getSoundManager().play(new MinecartSoundInstance(var2));
      } else if (var1 instanceof Bee var3) {
         boolean var4 = var3.isAngry();
         Object var5;
         if (var4) {
            var5 = new BeeAggressiveSoundInstance(var3);
         } else {
            var5 = new BeeFlyingSoundInstance(var3);
         }

         this.minecraft.getSoundManager().queueTickingSound((TickableSoundInstance)var5);
      }
   }

   @Override
   public void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      ExperienceOrb var8 = new ExperienceOrb(this.level, var2, var4, var6, var1.getValue());
      var8.syncPacketPositionCodec(var2, var4, var6);
      var8.setYRot(0.0F);
      var8.setXRot(0.0F);
      var8.setId(var1.getId());
      this.level.addEntity(var8);
   }

   @Override
   public void handleSetEntityMotion(ClientboundSetEntityMotionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         var2.lerpMotion((double)var1.getXa() / 8000.0, (double)var1.getYa() / 8000.0, (double)var1.getZa() / 8000.0);
      }
   }

   @Override
   public void handleSetEntityData(ClientboundSetEntityDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.id());
      if (var2 != null) {
         var2.getEntityData().assignValues(var1.packedItems());
      }
   }

   @Override
   public void handleTeleportEntity(ClientboundTeleportEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         double var3 = var1.getX();
         double var5 = var1.getY();
         double var7 = var1.getZ();
         var2.syncPacketPositionCodec(var3, var5, var7);
         if (!var2.isControlledByLocalInstance()) {
            float var9 = (float)(var1.getyRot() * 360) / 256.0F;
            float var10 = (float)(var1.getxRot() * 360) / 256.0F;
            var2.lerpTo(var3, var5, var7, var9, var10, 3);
            var2.setOnGround(var1.isOnGround());
         }
      }
   }

   @Override
   public void handleTickingState(ClientboundTickingStatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (this.minecraft.level != null) {
         TickRateManager var2 = this.minecraft.level.tickRateManager();
         var2.setTickRate(var1.tickRate());
         var2.setFrozen(var1.isFrozen());
      }
   }

   @Override
   public void handleTickingStep(ClientboundTickingStepPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (this.minecraft.level != null) {
         TickRateManager var2 = this.minecraft.level.tickRateManager();
         var2.setFrozenTicksToRun(var1.tickSteps());
      }
   }

   @Override
   public void handleSetCarriedItem(ClientboundSetCarriedItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (Inventory.isHotbarSlot(var1.getSlot())) {
         this.minecraft.player.getInventory().selected = var1.getSlot();
      }
   }

   @Override
   public void handleMoveEntity(ClientboundMoveEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         if (!var2.isControlledByLocalInstance()) {
            if (var1.hasPosition()) {
               VecDeltaCodec var3 = var2.getPositionCodec();
               Vec3 var4 = var3.decode((long)var1.getXa(), (long)var1.getYa(), (long)var1.getZa());
               var3.setBase(var4);
               float var5 = var1.hasRotation() ? (float)(var1.getyRot() * 360) / 256.0F : var2.lerpTargetYRot();
               float var6 = var1.hasRotation() ? (float)(var1.getxRot() * 360) / 256.0F : var2.lerpTargetXRot();
               var2.lerpTo(var4.x(), var4.y(), var4.z(), var5, var6, 3);
            } else if (var1.hasRotation()) {
               float var7 = (float)(var1.getyRot() * 360) / 256.0F;
               float var8 = (float)(var1.getxRot() * 360) / 256.0F;
               var2.lerpTo(var2.lerpTargetX(), var2.lerpTargetY(), var2.lerpTargetZ(), var7, var8, 3);
            }

            var2.setOnGround(var1.isOnGround());
         }
      }
   }

   @Override
   public void handleRotateMob(ClientboundRotateHeadPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         float var3 = (float)(var1.getYHeadRot() * 360) / 256.0F;
         var2.lerpHeadTo(var3, 3);
      }
   }

   @Override
   public void handleRemoveEntities(ClientboundRemoveEntitiesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      var1.getEntityIds().forEach(var1x -> this.level.removeEntity(var1x, Entity.RemovalReason.DISCARDED));
   }

   @Override
   public void handleMovePlayer(ClientboundPlayerPositionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      Vec3 var3 = var2.getDeltaMovement();
      boolean var4 = var1.getRelativeArguments().contains(RelativeMovement.X);
      boolean var5 = var1.getRelativeArguments().contains(RelativeMovement.Y);
      boolean var6 = var1.getRelativeArguments().contains(RelativeMovement.Z);
      double var7;
      double var9;
      if (var4) {
         var7 = var3.x();
         var9 = var2.getX() + var1.getX();
         var2.xOld = var2.xOld + var1.getX();
         var2.xo = var2.xo + var1.getX();
      } else {
         var7 = 0.0;
         var9 = var1.getX();
         var2.xOld = var9;
         var2.xo = var9;
      }

      double var11;
      double var13;
      if (var5) {
         var11 = var3.y();
         var13 = var2.getY() + var1.getY();
         var2.yOld = var2.yOld + var1.getY();
         var2.yo = var2.yo + var1.getY();
      } else {
         var11 = 0.0;
         var13 = var1.getY();
         var2.yOld = var13;
         var2.yo = var13;
      }

      double var15;
      double var17;
      if (var6) {
         var15 = var3.z();
         var17 = var2.getZ() + var1.getZ();
         var2.zOld = var2.zOld + var1.getZ();
         var2.zo = var2.zo + var1.getZ();
      } else {
         var15 = 0.0;
         var17 = var1.getZ();
         var2.zOld = var17;
         var2.zo = var17;
      }

      var2.setPos(var9, var13, var17);
      var2.setDeltaMovement(var7, var11, var15);
      float var19 = var1.getYRot();
      float var20 = var1.getXRot();
      if (var1.getRelativeArguments().contains(RelativeMovement.X_ROT)) {
         var2.setXRot(var2.getXRot() + var20);
         var2.xRotO += var20;
      } else {
         var2.setXRot(var20);
         var2.xRotO = var20;
      }

      if (var1.getRelativeArguments().contains(RelativeMovement.Y_ROT)) {
         var2.setYRot(var2.getYRot() + var19);
         var2.yRotO += var19;
      } else {
         var2.setYRot(var19);
         var2.yRotO = var19;
      }

      this.connection.send(new ServerboundAcceptTeleportationPacket(var1.getId()));
      this.connection.send(new ServerboundMovePlayerPacket.PosRot(var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot(), false));
   }

   @Override
   public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      var1.runUpdates((var1x, var2) -> this.level.setServerVerifiedBlockState(var1x, var2, 19));
   }

   @Override
   public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      int var2 = var1.getX();
      int var3 = var1.getZ();
      this.updateLevelChunk(var2, var3, var1.getChunkData());
      ClientboundLightUpdatePacketData var4 = var1.getLightData();
      this.level.queueLightUpdate(() -> {
         this.applyLightData(var2, var3, var4);
         LevelChunk var4x = this.level.getChunkSource().getChunk(var2, var3, false);
         if (var4x != null) {
            this.enableChunkLight(var4x, var2, var3);
         }
      });
   }

   @Override
   public void handleChunksBiomes(ClientboundChunksBiomesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);

      for (ClientboundChunksBiomesPacket.ChunkBiomeData var3 : var1.chunkBiomeData()) {
         this.level.getChunkSource().replaceBiomes(var3.pos().x, var3.pos().z, var3.getReadBuffer());
      }

      for (ClientboundChunksBiomesPacket.ChunkBiomeData var9 : var1.chunkBiomeData()) {
         this.level.onChunkLoaded(new ChunkPos(var9.pos().x, var9.pos().z));
      }

      for (ClientboundChunksBiomesPacket.ChunkBiomeData var10 : var1.chunkBiomeData()) {
         for (int var4 = -1; var4 <= 1; var4++) {
            for (int var5 = -1; var5 <= 1; var5++) {
               for (int var6 = this.level.getMinSection(); var6 < this.level.getMaxSection(); var6++) {
                  this.minecraft.levelRenderer.setSectionDirty(var10.pos().x + var4, var6, var10.pos().z + var5);
               }
            }
         }
      }
   }

   private void updateLevelChunk(int var1, int var2, ClientboundLevelChunkPacketData var3) {
      this.level.getChunkSource().replaceWithPacketData(var1, var2, var3.getReadBuffer(), var3.getHeightmaps(), var3.getBlockEntitiesTagsConsumer(var1, var2));
   }

   private void enableChunkLight(LevelChunk var1, int var2, int var3) {
      LevelLightEngine var4 = this.level.getChunkSource().getLightEngine();
      LevelChunkSection[] var5 = var1.getSections();
      ChunkPos var6 = var1.getPos();

      for (int var7 = 0; var7 < var5.length; var7++) {
         LevelChunkSection var8 = var5[var7];
         int var9 = this.level.getSectionYFromSectionIndex(var7);
         var4.updateSectionStatus(SectionPos.of(var6, var9), var8.hasOnlyAir());
         this.level.setSectionDirtyWithNeighbors(var2, var9, var3);
      }
   }

   @Override
   public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.getChunkSource().drop(var1.pos());
      this.queueLightRemoval(var1);
   }

   private void queueLightRemoval(ClientboundForgetLevelChunkPacket var1) {
      ChunkPos var2 = var1.pos();
      this.level.queueLightUpdate(() -> {
         LevelLightEngine var2x = this.level.getLightEngine();
         var2x.setLightEnabled(var2, false);

         for (int var3 = var2x.getMinLightSection(); var3 < var2x.getMaxLightSection(); var3++) {
            SectionPos var4 = SectionPos.of(var2, var3);
            var2x.queueSectionData(LightLayer.BLOCK, var4, null);
            var2x.queueSectionData(LightLayer.SKY, var4, null);
         }

         for (int var5 = this.level.getMinSection(); var5 < this.level.getMaxSection(); var5++) {
            var2x.updateSectionStatus(SectionPos.of(var2, var5), true);
         }
      });
   }

   @Override
   public void handleBlockUpdate(ClientboundBlockUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.setServerVerifiedBlockState(var1.getPos(), var1.getBlockState(), 19);
   }

   @Override
   public void handleConfigurationStart(ClientboundStartConfigurationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.getChatListener().clearQueue();
      this.sendChatAcknowledgement();
      ChatComponent.State var2 = this.minecraft.gui.getChat().storeState();
      this.minecraft.clearClientLevel(new ServerReconfigScreen(RECONFIGURE_SCREEN_MESSAGE, this.connection));
      this.connection
         .setupInboundProtocol(
            ConfigurationProtocols.CLIENTBOUND,
            new ClientConfigurationPacketListenerImpl(
               this.minecraft,
               this.connection,
               new CommonListenerCookie(
                  this.localGameProfile,
                  this.telemetryManager,
                  this.registryAccess,
                  this.enabledFeatures,
                  this.serverBrand,
                  this.serverData,
                  this.postDisconnectScreen,
                  this.serverCookies,
                  var2
               )
            )
         );
      this.send(ServerboundConfigurationAcknowledgedPacket.INSTANCE);
      this.connection.setupOutboundProtocol(ConfigurationProtocols.SERVERBOUND);
   }

   @Override
   public void handleTakeItemEntity(ClientboundTakeItemEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getItemId());
      Object var3 = (LivingEntity)this.level.getEntity(var1.getPlayerId());
      if (var3 == null) {
         var3 = this.minecraft.player;
      }

      if (var2 != null) {
         if (var2 instanceof ExperienceOrb) {
            this.level
               .playLocalSound(
                  var2.getX(),
                  var2.getY(),
                  var2.getZ(),
                  SoundEvents.EXPERIENCE_ORB_PICKUP,
                  SoundSource.PLAYERS,
                  0.1F,
                  (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F,
                  false
               );
         } else {
            this.level
               .playLocalSound(
                  var2.getX(),
                  var2.getY(),
                  var2.getZ(),
                  SoundEvents.ITEM_PICKUP,
                  SoundSource.PLAYERS,
                  0.2F,
                  (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F,
                  false
               );
         }

         this.minecraft
            .particleEngine
            .add(new ItemPickupParticle(this.minecraft.getEntityRenderDispatcher(), this.minecraft.renderBuffers(), this.level, var2, (Entity)var3));
         if (var2 instanceof ItemEntity var4) {
            ItemStack var5 = var4.getItem();
            if (!var5.isEmpty()) {
               var5.shrink(var1.getAmount());
            }

            if (var5.isEmpty()) {
               this.level.removeEntity(var1.getItemId(), Entity.RemovalReason.DISCARDED);
            }
         } else if (!(var2 instanceof ExperienceOrb)) {
            this.level.removeEntity(var1.getItemId(), Entity.RemovalReason.DISCARDED);
         }
      }
   }

   @Override
   public void handleSystemChat(ClientboundSystemChatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.getChatListener().handleSystemMessage(var1.content(), var1.overlay());
   }

   @Override
   public void handlePlayerChat(ClientboundPlayerChatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Optional var2 = var1.body().unpack(this.messageSignatureCache);
      if (var2.isEmpty()) {
         this.connection.disconnect(INVALID_PACKET);
      } else {
         this.messageSignatureCache.push((SignedMessageBody)var2.get(), var1.signature());
         UUID var3 = var1.sender();
         PlayerInfo var4 = this.getPlayerInfo(var3);
         if (var4 == null) {
            LOGGER.error("Received player chat packet for unknown player with ID: {}", var3);
            this.minecraft.getChatListener().handleChatMessageError(var3, var1.chatType());
         } else {
            RemoteChatSession var5 = var4.getChatSession();
            SignedMessageLink var6;
            if (var5 != null) {
               var6 = new SignedMessageLink(var1.index(), var3, var5.sessionId());
            } else {
               var6 = SignedMessageLink.unsigned(var3);
            }

            PlayerChatMessage var7 = new PlayerChatMessage(var6, var1.signature(), (SignedMessageBody)var2.get(), var1.unsignedContent(), var1.filterMask());
            var7 = var4.getMessageValidator().updateAndValidate(var7);
            if (var7 != null) {
               this.minecraft.getChatListener().handlePlayerChatMessage(var7, var4.getProfile(), var1.chatType());
            } else {
               this.minecraft.getChatListener().handleChatMessageError(var3, var1.chatType());
            }
         }
      }
   }

   @Override
   public void handleDisguisedChat(ClientboundDisguisedChatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.getChatListener().handleDisguisedChatMessage(var1.message(), var1.chatType());
   }

   @Override
   public void handleDeleteChat(ClientboundDeleteChatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Optional var2 = var1.messageSignature().unpack(this.messageSignatureCache);
      if (var2.isEmpty()) {
         this.connection.disconnect(INVALID_PACKET);
      } else {
         this.lastSeenMessages.ignorePending((MessageSignature)var2.get());
         if (!this.minecraft.getChatListener().removeFromDelayedMessageQueue((MessageSignature)var2.get())) {
            this.minecraft.gui.getChat().deleteMessage((MessageSignature)var2.get());
         }
      }
   }

   @Override
   public void handleAnimate(ClientboundAnimatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         if (var1.getAction() == 0) {
            LivingEntity var3 = (LivingEntity)var2;
            var3.swing(InteractionHand.MAIN_HAND);
         } else if (var1.getAction() == 3) {
            LivingEntity var4 = (LivingEntity)var2;
            var4.swing(InteractionHand.OFF_HAND);
         } else if (var1.getAction() == 2) {
            Player var5 = (Player)var2;
            var5.stopSleepInBed(false, false);
         } else if (var1.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.CRIT);
         } else if (var1.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.ENCHANTED_HIT);
         }
      }
   }

   @Override
   public void handleHurtAnimation(ClientboundHurtAnimationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.id());
      if (var2 != null) {
         var2.animateHurt(var1.yaw());
      }
   }

   @Override
   public void handleSetTime(ClientboundSetTimePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.level.setGameTime(var1.getGameTime());
      this.minecraft.level.setDayTime(var1.getDayTime());
      this.telemetryManager.setTime(var1.getGameTime());
   }

   @Override
   public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.level.setDefaultSpawnPos(var1.getPos(), var1.getAngle());
   }

   @Override
   public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getVehicle());
      if (var2 == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean var3 = var2.hasIndirectPassenger(this.minecraft.player);
         var2.ejectPassengers();

         for (int var7 : var1.getPassengers()) {
            Entity var8 = this.level.getEntity(var7);
            if (var8 != null) {
               var8.startRiding(var2, true);
               if (var8 == this.minecraft.player && !var3) {
                  if (var2 instanceof Boat) {
                     this.minecraft.player.yRotO = var2.getYRot();
                     this.minecraft.player.setYRot(var2.getYRot());
                     this.minecraft.player.setYHeadRot(var2.getYRot());
                  }

                  MutableComponent var9 = Component.translatable("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage());
                  this.minecraft.gui.setOverlayMessage(var9, false);
                  this.minecraft.getNarrator().sayNow(var9);
               }
            }
         }
      }
   }

   @Override
   public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getSourceId());
      if (var2 instanceof Mob) {
         ((Mob)var2).setDelayedLeashHolderId(var1.getDestId());
      }
   }

   private static ItemStack findTotem(Player var0) {
      for (InteractionHand var4 : InteractionHand.values()) {
         ItemStack var5 = var0.getItemInHand(var4);
         if (var5.is(Items.TOTEM_OF_UNDYING)) {
            return var5;
         }
      }

      return new ItemStack(Items.TOTEM_OF_UNDYING);
   }

   @Override
   public void handleEntityEvent(ClientboundEntityEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         switch (var1.getEventId()) {
            case 21:
               this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)var2));
               break;
            case 35:
               byte var3 = 40;
               this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.TOTEM_OF_UNDYING, 30);
               this.level.playLocalSound(var2.getX(), var2.getY(), var2.getZ(), SoundEvents.TOTEM_USE, var2.getSoundSource(), 1.0F, 1.0F, false);
               if (var2 == this.minecraft.player) {
                  this.minecraft.gameRenderer.displayItemActivation(findTotem(this.minecraft.player));
               }
               break;
            case 63:
               this.minecraft.getSoundManager().play(new SnifferSoundInstance((Sniffer)var2));
               break;
            default:
               var2.handleEntityEvent(var1.getEventId());
         }
      }
   }

   @Override
   public void handleDamageEvent(ClientboundDamageEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.entityId());
      if (var2 != null) {
         var2.handleDamageEvent(var1.getSource(this.level));
      }
   }

   @Override
   public void handleSetHealth(ClientboundSetHealthPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.player.hurtTo(var1.getHealth());
      this.minecraft.player.getFoodData().setFoodLevel(var1.getFood());
      this.minecraft.player.getFoodData().setSaturation(var1.getSaturation());
   }

   @Override
   public void handleSetExperience(ClientboundSetExperiencePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.player.setExperienceValues(var1.getExperienceProgress(), var1.getTotalExperience(), var1.getExperienceLevel());
   }

   @Override
   public void handleRespawn(ClientboundRespawnPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      CommonPlayerSpawnInfo var2 = var1.commonPlayerSpawnInfo();
      ResourceKey var3 = var2.dimension();
      Holder var4 = var2.dimensionType();
      LocalPlayer var5 = this.minecraft.player;
      ResourceKey var6 = var5.level().dimension();
      boolean var7 = var3 != var6;
      ReceivingLevelScreen.Reason var8 = this.determineLevelLoadingReason(var5.isDeadOrDying(), var3, var6);
      if (var7) {
         Map var9 = this.level.getAllMapData();
         boolean var10 = var2.isDebug();
         boolean var11 = var2.isFlat();
         ClientLevel.ClientLevelData var12 = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), var11);
         this.levelData = var12;
         this.level = new ClientLevel(
            this,
            var12,
            var3,
            var4,
            this.serverChunkRadius,
            this.serverSimulationDistance,
            this.minecraft::getProfiler,
            this.minecraft.levelRenderer,
            var10,
            var2.seed()
         );
         this.level.addMapData(var9);
         this.minecraft.setLevel(this.level, var8);
      }

      this.minecraft.cameraEntity = null;
      if (var5.hasContainerOpen()) {
         var5.closeContainer();
      }

      LocalPlayer var13;
      if (var1.shouldKeep((byte)2)) {
         var13 = this.minecraft.gameMode.createPlayer(this.level, var5.getStats(), var5.getRecipeBook(), var5.isShiftKeyDown(), var5.isSprinting());
      } else {
         var13 = this.minecraft.gameMode.createPlayer(this.level, var5.getStats(), var5.getRecipeBook());
      }

      this.startWaitingForNewLevel(var13, this.level, var8);
      var13.setId(var5.getId());
      this.minecraft.player = var13;
      if (var7) {
         this.minecraft.getMusicManager().stopPlaying();
      }

      this.minecraft.cameraEntity = var13;
      if (var1.shouldKeep((byte)2)) {
         List var14 = var5.getEntityData().getNonDefaultValues();
         if (var14 != null) {
            var13.getEntityData().assignValues(var14);
         }
      }

      if (var1.shouldKeep((byte)1)) {
         var13.getAttributes().assignValues(var5.getAttributes());
      }

      var13.resetPos();
      this.level.addEntity(var13);
      var13.setYRot(-180.0F);
      var13.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(var13);
      var13.setReducedDebugInfo(var5.isReducedDebugInfo());
      var13.setShowDeathScreen(var5.shouldShowDeathScreen());
      var13.setLastDeathLocation(var2.lastDeathLocation());
      var13.setPortalCooldown(var2.portalCooldown());
      var13.spinningEffectIntensity = var5.spinningEffectIntensity;
      var13.oSpinningEffectIntensity = var5.oSpinningEffectIntensity;
      if (this.minecraft.screen instanceof DeathScreen || this.minecraft.screen instanceof DeathScreen.TitleConfirmScreen) {
         this.minecraft.setScreen(null);
      }

      this.minecraft.gameMode.setLocalMode(var2.gameType(), var2.previousGameType());
   }

   private ReceivingLevelScreen.Reason determineLevelLoadingReason(boolean var1, ResourceKey<Level> var2, ResourceKey<Level> var3) {
      ReceivingLevelScreen.Reason var4 = ReceivingLevelScreen.Reason.OTHER;
      if (!var1) {
         if (var2 == Level.NETHER || var3 == Level.NETHER) {
            var4 = ReceivingLevelScreen.Reason.NETHER_PORTAL;
         } else if (var2 == Level.END || var3 == Level.END) {
            var4 = ReceivingLevelScreen.Reason.END_PORTAL;
         }
      }

      return var4;
   }

   @Override
   public void handleExplosion(ClientboundExplodePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Explosion var2 = new Explosion(
         this.minecraft.level,
         null,
         var1.getX(),
         var1.getY(),
         var1.getZ(),
         var1.getPower(),
         var1.getToBlow(),
         var1.getBlockInteraction(),
         var1.getSmallExplosionParticles(),
         var1.getLargeExplosionParticles(),
         var1.getExplosionSound()
      );
      var2.finalizeExplosion(true);
      this.minecraft
         .player
         .setDeltaMovement(
            this.minecraft.player.getDeltaMovement().add((double)var1.getKnockbackX(), (double)var1.getKnockbackY(), (double)var1.getKnockbackZ())
         );
   }

   @Override
   public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (this.level.getEntity(var1.getEntityId()) instanceof AbstractHorse var3) {
         LocalPlayer var4 = this.minecraft.player;
         SimpleContainer var5 = new SimpleContainer(var1.getSize());
         HorseInventoryMenu var6 = new HorseInventoryMenu(var1.getContainerId(), var4.getInventory(), var5, var3);
         var4.containerMenu = var6;
         this.minecraft.setScreen(new HorseInventoryScreen(var6, var4.getInventory(), var3));
      }
   }

   @Override
   public void handleOpenScreen(ClientboundOpenScreenPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      MenuScreens.create(var1.getType(), this.minecraft, var1.getContainerId(), var1.getTitle());
   }

   @Override
   public void handleContainerSetSlot(ClientboundContainerSetSlotPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      ItemStack var3 = var1.getItem();
      int var4 = var1.getSlot();
      this.minecraft.getTutorial().onGetItem(var3);
      if (var1.getContainerId() == -1) {
         if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
            var2.containerMenu.setCarried(var3);
         }
      } else if (var1.getContainerId() == -2) {
         var2.getInventory().setItem(var4, var3);
      } else {
         boolean var5 = false;
         if (this.minecraft.screen instanceof CreativeModeInventoryScreen var6) {
            var5 = !var6.isInventoryOpen();
         }

         if (var1.getContainerId() == 0 && InventoryMenu.isHotbarSlot(var4)) {
            if (!var3.isEmpty()) {
               ItemStack var8 = var2.inventoryMenu.getSlot(var4).getItem();
               if (var8.isEmpty() || var8.getCount() < var3.getCount()) {
                  var3.setPopTime(5);
               }
            }

            var2.inventoryMenu.setItem(var4, var1.getStateId(), var3);
         } else if (var1.getContainerId() == var2.containerMenu.containerId && (var1.getContainerId() != 0 || !var5)) {
            var2.containerMenu.setItem(var4, var1.getStateId(), var3);
         }
      }
   }

   @Override
   public void handleContainerContent(ClientboundContainerSetContentPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      if (var1.getContainerId() == 0) {
         var2.inventoryMenu.initializeContents(var1.getStateId(), var1.getItems(), var1.getCarriedItem());
      } else if (var1.getContainerId() == var2.containerMenu.containerId) {
         var2.containerMenu.initializeContents(var1.getStateId(), var1.getItems(), var1.getCarriedItem());
      }
   }

   @Override
   public void handleOpenSignEditor(ClientboundOpenSignEditorPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      BlockPos var2 = var1.getPos();
      if (this.level.getBlockEntity(var2) instanceof SignBlockEntity var3) {
         this.minecraft.player.openTextEdit(var3, var1.isFrontText());
      } else {
         BlockState var6 = this.level.getBlockState(var2);
         SignBlockEntity var5 = new SignBlockEntity(var2, var6);
         var5.setLevel(this.level);
         this.minecraft.player.openTextEdit(var5, var1.isFrontText());
      }
   }

   @Override
   public void handleBlockEntityData(ClientboundBlockEntityDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      BlockPos var2 = var1.getPos();
      this.minecraft.level.getBlockEntity(var2, var1.getType()).ifPresent(var2x -> {
         CompoundTag var3 = var1.getTag();
         if (!var3.isEmpty()) {
            var2x.loadWithComponents(var3, this.registryAccess);
         }

         if (var2x instanceof CommandBlockEntity && this.minecraft.screen instanceof CommandBlockEditScreen) {
            ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
         }
      });
   }

   @Override
   public void handleContainerSetData(ClientboundContainerSetDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      if (var2.containerMenu != null && var2.containerMenu.containerId == var1.getContainerId()) {
         var2.containerMenu.setData(var1.getId(), var1.getValue());
      }
   }

   @Override
   public void handleSetEquipment(ClientboundSetEquipmentPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (this.level.getEntity(var1.getEntity()) instanceof LivingEntity var3) {
         var1.getSlots().forEach(var1x -> var3.setItemSlot((EquipmentSlot)var1x.getFirst(), (ItemStack)var1x.getSecond()));
      }
   }

   @Override
   public void handleContainerClose(ClientboundContainerClosePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.player.clientSideCloseContainer();
   }

   @Override
   public void handleBlockEvent(ClientboundBlockEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.level.blockEvent(var1.getPos(), var1.getBlock(), var1.getB0(), var1.getB1());
   }

   @Override
   public void handleBlockDestruction(ClientboundBlockDestructionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.level.destroyBlockProgress(var1.getId(), var1.getPos(), var1.getProgress());
   }

   @Override
   public void handleGameEvent(ClientboundGameEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      ClientboundGameEventPacket.Type var3 = var1.getEvent();
      float var4 = var1.getParam();
      int var5 = Mth.floor(var4 + 0.5F);
      if (var3 == ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE) {
         var2.displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid"), false);
      } else if (var3 == ClientboundGameEventPacket.START_RAINING) {
         this.level.getLevelData().setRaining(true);
         this.level.setRainLevel(0.0F);
      } else if (var3 == ClientboundGameEventPacket.STOP_RAINING) {
         this.level.getLevelData().setRaining(false);
         this.level.setRainLevel(1.0F);
      } else if (var3 == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
         this.minecraft.gameMode.setLocalMode(GameType.byId(var5));
      } else if (var3 == ClientboundGameEventPacket.WIN_GAME) {
         if (var5 == 0) {
            this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
            this.minecraft.setScreen(new ReceivingLevelScreen(() -> false, ReceivingLevelScreen.Reason.END_PORTAL));
         } else if (var5 == 1) {
            this.minecraft.setScreen(new WinScreen(true, () -> {
               this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
               this.minecraft.setScreen(null);
            }));
         }
      } else if (var3 == ClientboundGameEventPacket.DEMO_EVENT) {
         Options var6 = this.minecraft.options;
         if (var4 == 0.0F) {
            this.minecraft.setScreen(new DemoIntroScreen());
         } else if (var4 == 101.0F) {
            this.minecraft
               .gui
               .getChat()
               .addMessage(
                  Component.translatable(
                     "demo.help.movement",
                     var6.keyUp.getTranslatedKeyMessage(),
                     var6.keyLeft.getTranslatedKeyMessage(),
                     var6.keyDown.getTranslatedKeyMessage(),
                     var6.keyRight.getTranslatedKeyMessage()
                  )
               );
         } else if (var4 == 102.0F) {
            this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.jump", var6.keyJump.getTranslatedKeyMessage()));
         } else if (var4 == 103.0F) {
            this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.inventory", var6.keyInventory.getTranslatedKeyMessage()));
         } else if (var4 == 104.0F) {
            this.minecraft.gui.getChat().addMessage(Component.translatable("demo.day.6", var6.keyScreenshot.getTranslatedKeyMessage()));
         }
      } else if (var3 == ClientboundGameEventPacket.ARROW_HIT_PLAYER) {
         this.level.playSound(var2, var2.getX(), var2.getEyeY(), var2.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18F, 0.45F);
      } else if (var3 == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE) {
         this.level.setRainLevel(var4);
      } else if (var3 == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
         this.level.setThunderLevel(var4);
      } else if (var3 == ClientboundGameEventPacket.PUFFER_FISH_STING) {
         this.level.playSound(var2, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0F, 1.0F);
      } else if (var3 == ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT) {
         this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, var2.getX(), var2.getY(), var2.getZ(), 0.0, 0.0, 0.0);
         if (var5 == 1) {
            this.level.playSound(var2, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
         }
      } else if (var3 == ClientboundGameEventPacket.IMMEDIATE_RESPAWN) {
         this.minecraft.player.setShowDeathScreen(var4 == 0.0F);
      } else if (var3 == ClientboundGameEventPacket.LIMITED_CRAFTING) {
         this.minecraft.player.setDoLimitedCrafting(var4 == 1.0F);
      } else if (var3 == ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START && this.levelLoadStatusManager != null) {
         this.levelLoadStatusManager.loadingPacketsReceived();
      }
   }

   private void startWaitingForNewLevel(LocalPlayer var1, ClientLevel var2, ReceivingLevelScreen.Reason var3) {
      this.levelLoadStatusManager = new LevelLoadStatusManager(var1, var2, this.minecraft.levelRenderer);
      this.minecraft.setScreen(new ReceivingLevelScreen(this.levelLoadStatusManager::levelReady, var3));
   }

   @Override
   public void handleMapItemData(ClientboundMapItemDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      MapRenderer var2 = this.minecraft.gameRenderer.getMapRenderer();
      MapId var3 = var1.mapId();
      MapItemSavedData var4 = this.minecraft.level.getMapData(var3);
      if (var4 == null) {
         var4 = MapItemSavedData.createForClient(var1.scale(), var1.locked(), this.minecraft.level.dimension());
         this.minecraft.level.overrideMapData(var3, var4);
      }

      var1.applyToMap(var4);
      var2.update(var3, var4);
   }

   @Override
   public void handleLevelEvent(ClientboundLevelEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (var1.isGlobalEvent()) {
         this.minecraft.level.globalLevelEvent(var1.getType(), var1.getPos(), var1.getData());
      } else {
         this.minecraft.level.levelEvent(var1.getType(), var1.getPos(), var1.getData());
      }
   }

   @Override
   public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.advancements.update(var1);
   }

   @Override
   public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ResourceLocation var2 = var1.getTab();
      if (var2 == null) {
         this.advancements.setSelectedTab(null, false);
      } else {
         AdvancementHolder var3 = this.advancements.get(var2);
         this.advancements.setSelectedTab(var3, false);
      }
   }

   @Override
   public void handleCommands(ClientboundCommandsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.commands = new CommandDispatcher(var1.getRoot(CommandBuildContext.simple(this.registryAccess, this.enabledFeatures)));
   }

   @Override
   public void handleStopSoundEvent(ClientboundStopSoundPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.getSoundManager().stop(var1.getName(), var1.getSource());
   }

   @Override
   public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.suggestionsProvider.completeCustomSuggestions(var1.id(), var1.toSuggestions());
   }

   @Override
   public void handleUpdateRecipes(ClientboundUpdateRecipesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.recipeManager.replaceRecipes(var1.getRecipes());
      ClientRecipeBook var2 = this.minecraft.player.getRecipeBook();
      var2.setupCollections(this.recipeManager.getOrderedRecipes(), this.minecraft.level.registryAccess());
      this.minecraft.populateSearchTree(SearchRegistry.RECIPE_COLLECTIONS, var2.getCollections());
   }

   @Override
   public void handleLookAt(ClientboundPlayerLookAtPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Vec3 var2 = var1.getPosition(this.level);
      if (var2 != null) {
         this.minecraft.player.lookAt(var1.getFromAnchor(), var2);
      }
   }

   @Override
   public void handleTagQueryPacket(ClientboundTagQueryPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (!this.debugQueryHandler.handleResponse(var1.getTransactionId(), var1.getTag())) {
         LOGGER.debug("Got unhandled response to tag query {}", var1.getTransactionId());
      }
   }

   @Override
   public void handleAwardStats(ClientboundAwardStatsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ObjectIterator var2 = var1.stats().object2IntEntrySet().iterator();

      while (var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         int var5 = var3.getIntValue();
         this.minecraft.player.getStats().setValue(this.minecraft.player, var4, var5);
      }

      if (this.minecraft.screen instanceof StatsScreen var6) {
         var6.onStatsUpdated();
      }
   }

   @Override
   public void handleAddOrRemoveRecipes(ClientboundRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ClientRecipeBook var2 = this.minecraft.player.getRecipeBook();
      var2.setBookSettings(var1.getBookSettings());
      ClientboundRecipePacket.State var3 = var1.getState();
      switch (var3) {
         case REMOVE:
            for (ResourceLocation var11 : var1.getRecipes()) {
               this.recipeManager.byKey(var11).ifPresent(var2::remove);
            }
            break;
         case INIT:
            for (ResourceLocation var9 : var1.getRecipes()) {
               this.recipeManager.byKey(var9).ifPresent(var2::add);
            }

            for (ResourceLocation var10 : var1.getHighlights()) {
               this.recipeManager.byKey(var10).ifPresent(var2::addHighlight);
            }
            break;
         case ADD:
            for (ResourceLocation var5 : var1.getRecipes()) {
               this.recipeManager.byKey(var5).ifPresent(var2x -> {
                  var2.add((RecipeHolder<?>)var2x);
                  var2.addHighlight((RecipeHolder<?>)var2x);
                  if (var2x.value().showNotification()) {
                     RecipeToast.addOrUpdate(this.minecraft.getToasts(), (RecipeHolder<?>)var2x);
                  }
               });
            }
      }

      var2.getCollections().forEach(var1x -> var1x.updateKnownRecipes(var2));
      if (this.minecraft.screen instanceof RecipeUpdateListener) {
         ((RecipeUpdateListener)this.minecraft.screen).recipesUpdated();
      }
   }

   @Override
   public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getEntityId());
      if (var2 instanceof LivingEntity) {
         Holder var3 = var1.getEffect();
         MobEffectInstance var4 = new MobEffectInstance(
            var3, var1.getEffectDurationTicks(), var1.getEffectAmplifier(), var1.isEffectAmbient(), var1.isEffectVisible(), var1.effectShowsIcon(), null
         );
         if (!var1.shouldBlend()) {
            var4.skipBlending();
         }

         ((LivingEntity)var2).forceAddEffect(var4, null);
      }
   }

   @Override
   public void handleUpdateTags(ClientboundUpdateTagsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      TagCollector var2 = new TagCollector();
      var1.getTags().forEach(var2::append);
      var2.updateTags(this.registryAccess, this.connection.isMemoryConnection());
   }

   @Override
   public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket var1) {
   }

   @Override
   public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket var1) {
   }

   @Override
   public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.playerId());
      if (var2 == this.minecraft.player) {
         if (this.minecraft.player.shouldShowDeathScreen()) {
            this.minecraft.setScreen(new DeathScreen(var1.message(), this.level.getLevelData().isHardcore()));
         } else {
            this.minecraft.player.respawn();
         }
      }
   }

   @Override
   public void handleChangeDifficulty(ClientboundChangeDifficultyPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.levelData.setDifficulty(var1.getDifficulty());
      this.levelData.setDifficultyLocked(var1.isLocked());
   }

   @Override
   public void handleSetCamera(ClientboundSetCameraPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         this.minecraft.setCameraEntity(var2);
      }
   }

   @Override
   public void handleInitializeBorder(ClientboundInitializeBorderPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      WorldBorder var2 = this.level.getWorldBorder();
      var2.setCenter(var1.getNewCenterX(), var1.getNewCenterZ());
      long var3 = var1.getLerpTime();
      if (var3 > 0L) {
         var2.lerpSizeBetween(var1.getOldSize(), var1.getNewSize(), var3);
      } else {
         var2.setSize(var1.getNewSize());
      }

      var2.setAbsoluteMaxSize(var1.getNewAbsoluteMaxSize());
      var2.setWarningBlocks(var1.getWarningBlocks());
      var2.setWarningTime(var1.getWarningTime());
   }

   @Override
   public void handleSetBorderCenter(ClientboundSetBorderCenterPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.getWorldBorder().setCenter(var1.getNewCenterX(), var1.getNewCenterZ());
   }

   @Override
   public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.getWorldBorder().lerpSizeBetween(var1.getOldSize(), var1.getNewSize(), var1.getLerpTime());
   }

   @Override
   public void handleSetBorderSize(ClientboundSetBorderSizePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.getWorldBorder().setSize(var1.getSize());
   }

   @Override
   public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.getWorldBorder().setWarningBlocks(var1.getWarningBlocks());
   }

   @Override
   public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.getWorldBorder().setWarningTime(var1.getWarningDelay());
   }

   @Override
   public void handleTitlesClear(ClientboundClearTitlesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.clear();
      if (var1.shouldResetTimes()) {
         this.minecraft.gui.resetTitleTimes();
      }
   }

   @Override
   public void handleServerData(ClientboundServerDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (this.serverData != null) {
         this.serverData.motd = var1.motd();
         var1.iconBytes().map(ServerData::validateIcon).ifPresent(this.serverData::setIconBytes);
         ServerList.saveSingleServer(this.serverData);
      }
   }

   @Override
   public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.suggestionsProvider.modifyCustomCompletions(var1.action(), var1.entries());
   }

   @Override
   public void setActionBarText(ClientboundSetActionBarTextPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setOverlayMessage(var1.text(), false);
   }

   @Override
   public void setTitleText(ClientboundSetTitleTextPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setTitle(var1.text());
   }

   @Override
   public void setSubtitleText(ClientboundSetSubtitleTextPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setSubtitle(var1.text());
   }

   @Override
   public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setTimes(var1.getFadeIn(), var1.getStay(), var1.getFadeOut());
   }

   @Override
   public void handleTabListCustomisation(ClientboundTabListPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.getTabList().setHeader(var1.header().getString().isEmpty() ? null : var1.header());
      this.minecraft.gui.getTabList().setFooter(var1.footer().getString().isEmpty() ? null : var1.footer());
   }

   @Override
   public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (var1.getEntity(this.level) instanceof LivingEntity var2) {
         var2.removeEffectNoUpdate(var1.effect());
      }
   }

   @Override
   public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);

      for (UUID var3 : var1.profileIds()) {
         this.minecraft.getPlayerSocialManager().removePlayer(var3);
         PlayerInfo var4 = this.playerInfoMap.remove(var3);
         if (var4 != null) {
            this.listedPlayers.remove(var4);
         }
      }
   }

   @Override
   public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);

      for (ClientboundPlayerInfoUpdatePacket.Entry var3 : var1.newEntries()) {
         PlayerInfo var4 = new PlayerInfo(Objects.requireNonNull(var3.profile()), this.enforcesSecureChat());
         if (this.playerInfoMap.putIfAbsent(var3.profileId(), var4) == null) {
            this.minecraft.getPlayerSocialManager().addPlayer(var4);
         }
      }

      for (ClientboundPlayerInfoUpdatePacket.Entry var8 : var1.entries()) {
         PlayerInfo var9 = this.playerInfoMap.get(var8.profileId());
         if (var9 == null) {
            LOGGER.warn("Ignoring player info update for unknown player {} ({})", var8.profileId(), var1.actions());
         } else {
            for (ClientboundPlayerInfoUpdatePacket.Action var6 : var1.actions()) {
               this.applyPlayerInfoUpdate(var6, var8, var9);
            }
         }
      }
   }

   private void applyPlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket.Action var1, ClientboundPlayerInfoUpdatePacket.Entry var2, PlayerInfo var3) {
      switch (var1) {
         case INITIALIZE_CHAT:
            this.initializeChatSession(var2, var3);
            break;
         case UPDATE_GAME_MODE:
            if (var3.getGameMode() != var2.gameMode() && this.minecraft.player != null && this.minecraft.player.getUUID().equals(var2.profileId())) {
               this.minecraft.player.onGameModeChanged(var2.gameMode());
            }

            var3.setGameMode(var2.gameMode());
            break;
         case UPDATE_LISTED:
            if (var2.listed()) {
               this.listedPlayers.add(var3);
            } else {
               this.listedPlayers.remove(var3);
            }
            break;
         case UPDATE_LATENCY:
            var3.setLatency(var2.latency());
            break;
         case UPDATE_DISPLAY_NAME:
            var3.setTabListDisplayName(var2.displayName());
      }
   }

   private void initializeChatSession(ClientboundPlayerInfoUpdatePacket.Entry var1, PlayerInfo var2) {
      GameProfile var3 = var2.getProfile();
      SignatureValidator var4 = this.minecraft.getProfileKeySignatureValidator();
      if (var4 == null) {
         LOGGER.warn("Ignoring chat session from {} due to missing Services public key", var3.getName());
         var2.clearChatSession(this.enforcesSecureChat());
      } else {
         RemoteChatSession.Data var5 = var1.chatSession();
         if (var5 != null) {
            try {
               RemoteChatSession var6 = var5.validate(var3, var4);
               var2.setChatSession(var6);
            } catch (ProfilePublicKey.ValidationException var7) {
               LOGGER.error("Failed to validate profile key for player: '{}'", var3.getName(), var7);
               var2.clearChatSession(this.enforcesSecureChat());
            }
         } else {
            var2.clearChatSession(this.enforcesSecureChat());
         }
      }
   }

   private boolean enforcesSecureChat() {
      return this.minecraft.canValidateProfileKeys() && this.serverEnforcesSecureChat;
   }

   @Override
   public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      var2.getAbilities().flying = var1.isFlying();
      var2.getAbilities().instabuild = var1.canInstabuild();
      var2.getAbilities().invulnerable = var1.isInvulnerable();
      var2.getAbilities().mayfly = var1.canFly();
      var2.getAbilities().setFlyingSpeed(var1.getFlyingSpeed());
      var2.getAbilities().setWalkingSpeed(var1.getWalkingSpeed());
   }

   @Override
   public void handleSoundEvent(ClientboundSoundPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft
         .level
         .playSeededSound(
            this.minecraft.player, var1.getX(), var1.getY(), var1.getZ(), var1.getSound(), var1.getSource(), var1.getVolume(), var1.getPitch(), var1.getSeed()
         );
   }

   @Override
   public void handleSoundEntityEvent(ClientboundSoundEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         this.minecraft
            .level
            .playSeededSound(this.minecraft.player, var2, var1.getSound(), var1.getSource(), var1.getVolume(), var1.getPitch(), var1.getSeed());
      }
   }

   @Override
   public void handleBossUpdate(ClientboundBossEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.getBossOverlay().update(var1);
   }

   @Override
   public void handleItemCooldown(ClientboundCooldownPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (var1.duration() == 0) {
         this.minecraft.player.getCooldowns().removeCooldown(var1.item());
      } else {
         this.minecraft.player.getCooldowns().addCooldown(var1.item(), var1.duration());
      }
   }

   @Override
   public void handleMoveVehicle(ClientboundMoveVehiclePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.minecraft.player.getRootVehicle();
      if (var2 != this.minecraft.player && var2.isControlledByLocalInstance()) {
         var2.absMoveTo(var1.getX(), var1.getY(), var1.getZ(), var1.getYRot(), var1.getXRot());
         this.connection.send(new ServerboundMoveVehiclePacket(var2));
      }
   }

   @Override
   public void handleOpenBook(ClientboundOpenBookPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ItemStack var2 = this.minecraft.player.getItemInHand(var1.getHand());
      BookViewScreen.BookAccess var3 = BookViewScreen.BookAccess.fromItem(var2);
      if (var3 != null) {
         this.minecraft.setScreen(new BookViewScreen(var3));
      }
   }

   @Override
   public void handleCustomPayload(CustomPacketPayload var1) {
      if (var1 instanceof PathfindingDebugPayload var2) {
         this.minecraft.debugRenderer.pathfindingRenderer.addPath(var2.entityId(), var2.path(), var2.maxNodeDistance());
      } else if (var1 instanceof NeighborUpdatesDebugPayload var3) {
         ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(var3.time(), var3.pos());
      } else if (var1 instanceof StructuresDebugPayload var4) {
         this.minecraft.debugRenderer.structureRenderer.addBoundingBox(var4.mainBB(), var4.pieces(), var4.dimension());
      } else if (var1 instanceof WorldGenAttemptDebugPayload var5) {
         ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer)
            .addPos(var5.pos(), var5.scale(), var5.red(), var5.green(), var5.blue(), var5.alpha());
      } else if (var1 instanceof PoiTicketCountDebugPayload var6) {
         this.minecraft.debugRenderer.brainDebugRenderer.setFreeTicketCount(var6.pos(), var6.freeTicketCount());
      } else if (var1 instanceof PoiAddedDebugPayload var7) {
         BrainDebugRenderer.PoiInfo var19 = new BrainDebugRenderer.PoiInfo(var7.pos(), var7.poiType(), var7.freeTicketCount());
         this.minecraft.debugRenderer.brainDebugRenderer.addPoi(var19);
      } else if (var1 instanceof PoiRemovedDebugPayload var8) {
         this.minecraft.debugRenderer.brainDebugRenderer.removePoi(var8.pos());
      } else if (var1 instanceof VillageSectionsDebugPayload var9) {
         VillageSectionsDebugRenderer var20 = this.minecraft.debugRenderer.villageSectionsDebugRenderer;
         var9.villageChunks().forEach(var20::setVillageSection);
         var9.notVillageChunks().forEach(var20::setNotVillageSection);
      } else if (var1 instanceof GoalDebugPayload var10) {
         this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(var10.entityId(), var10.pos(), var10.goals());
      } else if (var1 instanceof BrainDebugPayload var11) {
         this.minecraft.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump(var11.brainDump());
      } else if (var1 instanceof BeeDebugPayload var12) {
         this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo(var12.beeInfo());
      } else if (var1 instanceof HiveDebugPayload var13) {
         this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo(var13.hiveInfo(), this.level.getGameTime());
      } else if (var1 instanceof GameTestAddMarkerDebugPayload var14) {
         this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker(var14.pos(), var14.color(), var14.text(), var14.durationMs());
      } else if (var1 instanceof GameTestClearMarkersDebugPayload) {
         this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
      } else if (var1 instanceof RaidsDebugPayload var15) {
         this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(var15.raidCenters());
      } else if (var1 instanceof GameEventDebugPayload var16) {
         this.minecraft.debugRenderer.gameEventListenerRenderer.trackGameEvent(var16.gameEventType(), var16.pos());
      } else if (var1 instanceof GameEventListenerDebugPayload var17) {
         this.minecraft.debugRenderer.gameEventListenerRenderer.trackListener(var17.listenerPos(), var17.listenerRange());
      } else if (var1 instanceof BreezeDebugPayload var18) {
         this.minecraft.debugRenderer.breezeDebugRenderer.add(var18.breezeInfo());
      } else {
         this.handleUnknownCustomPayload(var1);
      }
   }

   private void handleUnknownCustomPayload(CustomPacketPayload var1) {
      LOGGER.warn("Unknown custom packet payload: {}", var1.type().id());
   }

   @Override
   public void handleAddObjective(ClientboundSetObjectivePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      String var2 = var1.getObjectiveName();
      if (var1.getMethod() == 0) {
         this.scoreboard.addObjective(var2, ObjectiveCriteria.DUMMY, var1.getDisplayName(), var1.getRenderType(), false, var1.getNumberFormat().orElse(null));
      } else {
         Objective var3 = this.scoreboard.getObjective(var2);
         if (var3 != null) {
            if (var1.getMethod() == 1) {
               this.scoreboard.removeObjective(var3);
            } else if (var1.getMethod() == 2) {
               var3.setRenderType(var1.getRenderType());
               var3.setDisplayName(var1.getDisplayName());
               var3.setNumberFormat(var1.getNumberFormat().orElse(null));
            }
         }
      }
   }

   @Override
   public void handleSetScore(ClientboundSetScorePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      String var2 = var1.objectiveName();
      ScoreHolder var3 = ScoreHolder.forNameOnly(var1.owner());
      Objective var4 = this.scoreboard.getObjective(var2);
      if (var4 != null) {
         ScoreAccess var5 = this.scoreboard.getOrCreatePlayerScore(var3, var4, true);
         var5.set(var1.score());
         var5.display(var1.display().orElse(null));
         var5.numberFormatOverride(var1.numberFormat().orElse(null));
      } else {
         LOGGER.warn("Received packet for unknown scoreboard objective: {}", var2);
      }
   }

   @Override
   public void handleResetScore(ClientboundResetScorePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      String var2 = var1.objectiveName();
      ScoreHolder var3 = ScoreHolder.forNameOnly(var1.owner());
      if (var2 == null) {
         this.scoreboard.resetAllPlayerScores(var3);
      } else {
         Objective var4 = this.scoreboard.getObjective(var2);
         if (var4 != null) {
            this.scoreboard.resetSinglePlayerScore(var3, var4);
         } else {
            LOGGER.warn("Received packet for unknown scoreboard objective: {}", var2);
         }
      }
   }

   @Override
   public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      String var2 = var1.getObjectiveName();
      Objective var3 = var2 == null ? null : this.scoreboard.getObjective(var2);
      this.scoreboard.setDisplayObjective(var1.getSlot(), var3);
   }

   @Override
   public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ClientboundSetPlayerTeamPacket.Action var3 = var1.getTeamAction();
      PlayerTeam var2;
      if (var3 == ClientboundSetPlayerTeamPacket.Action.ADD) {
         var2 = this.scoreboard.addPlayerTeam(var1.getName());
      } else {
         var2 = this.scoreboard.getPlayerTeam(var1.getName());
         if (var2 == null) {
            LOGGER.warn(
               "Received packet for unknown team {}: team action: {}, player action: {}",
               new Object[]{var1.getName(), var1.getTeamAction(), var1.getPlayerAction()}
            );
            return;
         }
      }

      Optional var4 = var1.getParameters();
      var4.ifPresent(var1x -> {
         var2.setDisplayName(var1x.getDisplayName());
         var2.setColor(var1x.getColor());
         var2.unpackOptions(var1x.getOptions());
         Team.Visibility var2x = Team.Visibility.byName(var1x.getNametagVisibility());
         if (var2x != null) {
            var2.setNameTagVisibility(var2x);
         }

         Team.CollisionRule var3x = Team.CollisionRule.byName(var1x.getCollisionRule());
         if (var3x != null) {
            var2.setCollisionRule(var3x);
         }

         var2.setPlayerPrefix(var1x.getPlayerPrefix());
         var2.setPlayerSuffix(var1x.getPlayerSuffix());
      });
      ClientboundSetPlayerTeamPacket.Action var5 = var1.getPlayerAction();
      if (var5 == ClientboundSetPlayerTeamPacket.Action.ADD) {
         for (String var7 : var1.getPlayers()) {
            this.scoreboard.addPlayerToTeam(var7, var2);
         }
      } else if (var5 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         for (String var9 : var1.getPlayers()) {
            this.scoreboard.removePlayerFromTeam(var9, var2);
         }
      }

      if (var3 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         this.scoreboard.removePlayerTeam(var2);
      }
   }

   @Override
   public void handleParticleEvent(ClientboundLevelParticlesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (var1.getCount() == 0) {
         double var2 = (double)(var1.getMaxSpeed() * var1.getXDist());
         double var4 = (double)(var1.getMaxSpeed() * var1.getYDist());
         double var6 = (double)(var1.getMaxSpeed() * var1.getZDist());

         try {
            this.level.addParticle(var1.getParticle(), var1.isOverrideLimiter(), var1.getX(), var1.getY(), var1.getZ(), var2, var4, var6);
         } catch (Throwable var17) {
            LOGGER.warn("Could not spawn particle effect {}", var1.getParticle());
         }
      } else {
         for (int var18 = 0; var18 < var1.getCount(); var18++) {
            double var3 = this.random.nextGaussian() * (double)var1.getXDist();
            double var5 = this.random.nextGaussian() * (double)var1.getYDist();
            double var7 = this.random.nextGaussian() * (double)var1.getZDist();
            double var9 = this.random.nextGaussian() * (double)var1.getMaxSpeed();
            double var11 = this.random.nextGaussian() * (double)var1.getMaxSpeed();
            double var13 = this.random.nextGaussian() * (double)var1.getMaxSpeed();

            try {
               this.level
                  .addParticle(var1.getParticle(), var1.isOverrideLimiter(), var1.getX() + var3, var1.getY() + var5, var1.getZ() + var7, var9, var11, var13);
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect {}", var1.getParticle());
               return;
            }
         }
      }
   }

   @Override
   public void handleUpdateAttributes(ClientboundUpdateAttributesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = this.level.getEntity(var1.getEntityId());
      if (var2 != null) {
         if (!(var2 instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         } else {
            AttributeMap var3 = ((LivingEntity)var2).getAttributes();

            for (ClientboundUpdateAttributesPacket.AttributeSnapshot var5 : var1.getValues()) {
               AttributeInstance var6 = var3.getInstance(var5.attribute());
               if (var6 == null) {
                  LOGGER.warn("Entity {} does not have attribute {}", var2, var5.attribute().getRegisteredName());
               } else {
                  var6.setBaseValue(var5.base());
                  var6.removeModifiers();

                  for (AttributeModifier var8 : var5.modifiers()) {
                     var6.addTransientModifier(var8);
                  }
               }
            }
         }
      }
   }

   @Override
   public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      AbstractContainerMenu var2 = this.minecraft.player.containerMenu;
      if (var2.containerId == var1.getContainerId()) {
         this.recipeManager.byKey(var1.getRecipe()).ifPresent(var2x -> {
            if (this.minecraft.screen instanceof RecipeUpdateListener) {
               RecipeBookComponent var3 = ((RecipeUpdateListener)this.minecraft.screen).getRecipeBookComponent();
               var3.setupGhostRecipe((RecipeHolder<?>)var2x, var2.slots);
            }
         });
      }
   }

   @Override
   public void handleLightUpdatePacket(ClientboundLightUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      int var2 = var1.getX();
      int var3 = var1.getZ();
      ClientboundLightUpdatePacketData var4 = var1.getLightData();
      this.level.queueLightUpdate(() -> this.applyLightData(var2, var3, var4));
   }

   private void applyLightData(int var1, int var2, ClientboundLightUpdatePacketData var3) {
      LevelLightEngine var4 = this.level.getChunkSource().getLightEngine();
      BitSet var5 = var3.getSkyYMask();
      BitSet var6 = var3.getEmptySkyYMask();
      Iterator var7 = var3.getSkyUpdates().iterator();
      this.readSectionList(var1, var2, var4, LightLayer.SKY, var5, var6, var7);
      BitSet var8 = var3.getBlockYMask();
      BitSet var9 = var3.getEmptyBlockYMask();
      Iterator var10 = var3.getBlockUpdates().iterator();
      this.readSectionList(var1, var2, var4, LightLayer.BLOCK, var8, var9, var10);
      var4.setLightEnabled(new ChunkPos(var1, var2), true);
   }

   @Override
   public void handleMerchantOffers(ClientboundMerchantOffersPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      AbstractContainerMenu var2 = this.minecraft.player.containerMenu;
      if (var1.getContainerId() == var2.containerId && var2 instanceof MerchantMenu var3) {
         var3.setOffers(var1.getOffers());
         var3.setXp(var1.getVillagerXp());
         var3.setMerchantLevel(var1.getVillagerLevel());
         var3.setShowProgressBar(var1.showProgress());
         var3.setCanRestock(var1.canRestock());
      }
   }

   @Override
   public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.serverChunkRadius = var1.getRadius();
      this.minecraft.options.setServerRenderDistance(this.serverChunkRadius);
      this.level.getChunkSource().updateViewRadius(var1.getRadius());
   }

   @Override
   public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.serverSimulationDistance = var1.simulationDistance();
      this.level.setServerSimulationDistance(this.serverSimulationDistance);
   }

   @Override
   public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.getChunkSource().updateViewCenter(var1.getX(), var1.getZ());
   }

   @Override
   public void handleBlockChangedAck(ClientboundBlockChangedAckPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.handleBlockChangedAck(var1.sequence());
   }

   @Override
   public void handleBundlePacket(ClientboundBundlePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);

      for (Packet var3 : var1.subPackets()) {
         var3.handle(this);
      }
   }

   @Override
   public void handleProjectilePowerPacket(ClientboundProjectilePowerPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (this.level.getEntity(var1.getId()) instanceof AbstractHurtingProjectile var3) {
         var3.xPower = var1.getXPower();
         var3.yPower = var1.getYPower();
         var3.zPower = var1.getZPower();
      }
   }

   @Override
   public void handleChunkBatchStart(ClientboundChunkBatchStartPacket var1) {
      this.chunkBatchSizeCalculator.onBatchStart();
   }

   @Override
   public void handleChunkBatchFinished(ClientboundChunkBatchFinishedPacket var1) {
      this.chunkBatchSizeCalculator.onBatchFinished(var1.batchSize());
      this.send(new ServerboundChunkBatchReceivedPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
   }

   @Override
   public void handleDebugSample(ClientboundDebugSamplePacket var1) {
      this.minecraft.getDebugOverlay().logRemoteSample(var1.sample(), var1.debugSampleType());
   }

   @Override
   public void handlePongResponse(ClientboundPongResponsePacket var1) {
      this.pingDebugMonitor.onPongReceived(var1);
   }

   private void readSectionList(int var1, int var2, LevelLightEngine var3, LightLayer var4, BitSet var5, BitSet var6, Iterator<byte[]> var7) {
      for (int var8 = 0; var8 < var3.getLightSectionCount(); var8++) {
         int var9 = var3.getMinLightSection() + var8;
         boolean var10 = var5.get(var8);
         boolean var11 = var6.get(var8);
         if (var10 || var11) {
            var3.queueSectionData(var4, SectionPos.of(var1, var9, var2), var10 ? new DataLayer((byte[])((byte[])var7.next()).clone()) : new DataLayer());
            this.level.setSectionDirtyWithNeighbors(var1, var9, var2);
         }
      }
   }

   public Connection getConnection() {
      return this.connection;
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isConnected() && !this.closed;
   }

   public Collection<PlayerInfo> getListedOnlinePlayers() {
      return this.listedPlayers;
   }

   public Collection<PlayerInfo> getOnlinePlayers() {
      return this.playerInfoMap.values();
   }

   public Collection<UUID> getOnlinePlayerIds() {
      return this.playerInfoMap.keySet();
   }

   @Nullable
   public PlayerInfo getPlayerInfo(UUID var1) {
      return this.playerInfoMap.get(var1);
   }

   @Nullable
   public PlayerInfo getPlayerInfo(String var1) {
      for (PlayerInfo var3 : this.playerInfoMap.values()) {
         if (var3.getProfile().getName().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public GameProfile getLocalGameProfile() {
      return this.localGameProfile;
   }

   public ClientAdvancements getAdvancements() {
      return this.advancements;
   }

   public CommandDispatcher<SharedSuggestionProvider> getCommands() {
      return this.commands;
   }

   public ClientLevel getLevel() {
      return this.level;
   }

   public DebugQueryHandler getDebugQueryHandler() {
      return this.debugQueryHandler;
   }

   public UUID getId() {
      return this.id;
   }

   public Set<ResourceKey<Level>> levels() {
      return this.levels;
   }

   public RegistryAccess.Frozen registryAccess() {
      return this.registryAccess;
   }

   public void markMessageAsProcessed(PlayerChatMessage var1, boolean var2) {
      MessageSignature var3 = var1.signature();
      if (var3 != null && this.lastSeenMessages.addPending(var3, var2) && this.lastSeenMessages.offset() > 64) {
         this.sendChatAcknowledgement();
      }
   }

   private void sendChatAcknowledgement() {
      int var1 = this.lastSeenMessages.getAndClearOffset();
      if (var1 > 0) {
         this.send(new ServerboundChatAckPacket(var1));
      }
   }

   public void sendChat(String var1) {
      Instant var2 = Instant.now();
      long var3 = Crypt.SaltSupplier.getLong();
      LastSeenMessagesTracker.Update var5 = this.lastSeenMessages.generateAndApplyUpdate();
      MessageSignature var6 = this.signedMessageEncoder.pack(new SignedMessageBody(var1, var2, var3, var5.lastSeen()));
      this.send(new ServerboundChatPacket(var1, var2, var3, var6, var5.update()));
   }

   public void sendCommand(String var1) {
      SignableCommand var2 = SignableCommand.of(this.parseCommand(var1));
      if (var2.arguments().isEmpty()) {
         this.send(new ServerboundChatCommandPacket(var1));
      } else {
         Instant var3 = Instant.now();
         long var4 = Crypt.SaltSupplier.getLong();
         LastSeenMessagesTracker.Update var6 = this.lastSeenMessages.generateAndApplyUpdate();
         ArgumentSignatures var7 = ArgumentSignatures.signCommand(var2, var5 -> {
            SignedMessageBody var6x = new SignedMessageBody(var5, var3, var4, var6.lastSeen());
            return this.signedMessageEncoder.pack(var6x);
         });
         this.send(new ServerboundChatCommandSignedPacket(var1, var3, var4, var7, var6.update()));
      }
   }

   public boolean sendUnsignedCommand(String var1) {
      if (!SignableCommand.hasSignableArguments(this.parseCommand(var1))) {
         this.send(new ServerboundChatCommandPacket(var1));
         return true;
      } else {
         return false;
      }
   }

   private ParseResults<SharedSuggestionProvider> parseCommand(String var1) {
      return this.commands.parse(var1, this.suggestionsProvider);
   }

   @Override
   public void tick() {
      if (this.connection.isEncrypted()) {
         ProfileKeyPairManager var1 = this.minecraft.getProfileKeyPairManager();
         if (var1.shouldRefreshKeyPair()) {
            var1.prepareKeyPair().thenAcceptAsync(var1x -> var1x.ifPresent(this::setKeyPair), this.minecraft);
         }
      }

      this.sendDeferredPackets();
      if (this.minecraft.getDebugOverlay().showNetworkCharts()) {
         this.pingDebugMonitor.tick();
      }

      this.debugSampleSubscriber.tick();
      this.telemetryManager.tick();
      if (this.levelLoadStatusManager != null) {
         this.levelLoadStatusManager.tick();
      }
   }

   public void setKeyPair(ProfileKeyPair var1) {
      if (this.minecraft.isLocalPlayer(this.localGameProfile.getId())) {
         if (this.chatSession == null || !this.chatSession.keyPair().equals(var1)) {
            this.chatSession = LocalChatSession.create(var1);
            this.signedMessageEncoder = this.chatSession.createMessageEncoder(this.localGameProfile.getId());
            this.send(new ServerboundChatSessionUpdatePacket(this.chatSession.asRemote().asData()));
         }
      }
   }

   @Nullable
   public ServerData getServerData() {
      return this.serverData;
   }

   public FeatureFlagSet enabledFeatures() {
      return this.enabledFeatures;
   }

   public boolean isFeatureEnabled(FeatureFlagSet var1) {
      return var1.isSubsetOf(this.enabledFeatures());
   }

   public Scoreboard scoreboard() {
      return this.scoreboard;
   }

   public PotionBrewing potionBrewing() {
      return this.potionBrewing;
   }
}
