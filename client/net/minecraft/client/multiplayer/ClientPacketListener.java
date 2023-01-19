package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.ClientTelemetryManager;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.multiplayer.ChatPreviewWarningScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPreviewPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
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
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
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
import net.minecraft.network.protocol.game.ClientboundSetDisplayChatPreviewPacket;
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
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ClientPacketListener implements ClientGamePacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component GENERIC_DISCONNECT_MESSAGE = Component.translatable("disconnect.lost");
   private final Connection connection;
   private final GameProfile localGameProfile;
   private final Screen callbackScreen;
   private final Minecraft minecraft;
   private ClientLevel level;
   private ClientLevel.ClientLevelData levelData;
   private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
   private final ClientAdvancements advancements;
   private final ClientSuggestionProvider suggestionsProvider;
   private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
   private int serverChunkRadius = 3;
   private int serverSimulationDistance = 3;
   private final RandomSource random = RandomSource.createThreadSafe();
   private CommandDispatcher<SharedSuggestionProvider> commands = new CommandDispatcher();
   private final RecipeManager recipeManager = new RecipeManager();
   private final UUID id = UUID.randomUUID();
   private Set<ResourceKey<Level>> levels;
   private RegistryAccess.Frozen registryAccess = RegistryAccess.BUILTIN.get();
   private final ClientTelemetryManager telemetryManager;

   public ClientPacketListener(Minecraft var1, Screen var2, Connection var3, GameProfile var4, ClientTelemetryManager var5) {
      super();
      this.minecraft = var1;
      this.callbackScreen = var2;
      this.connection = var3;
      this.localGameProfile = var4;
      this.advancements = new ClientAdvancements(var1);
      this.suggestionsProvider = new ClientSuggestionProvider(this, var1);
      this.telemetryManager = var5;
   }

   public ClientSuggestionProvider getSuggestionsProvider() {
      return this.suggestionsProvider;
   }

   public void cleanup() {
      this.level = null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   @Override
   public void handleLogin(ClientboundLoginPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
      this.registryAccess = var1.registryHolder();
      if (!this.connection.isMemoryConnection()) {
         this.registryAccess.registries().forEach(var0 -> var0.value().resetTags());
      }

      ArrayList var2 = Lists.newArrayList(var1.levels());
      Collections.shuffle(var2);
      this.levels = Sets.newLinkedHashSet(var2);
      ResourceKey var3 = var1.dimension();
      Holder var4 = this.registryAccess.<DimensionType>registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).getHolderOrThrow(var1.dimensionType());
      this.serverChunkRadius = var1.chunkRadius();
      this.serverSimulationDistance = var1.simulationDistance();
      boolean var5 = var1.isDebug();
      boolean var6 = var1.isFlat();
      ClientLevel.ClientLevelData var7 = new ClientLevel.ClientLevelData(Difficulty.NORMAL, var1.hardcore(), var6);
      this.levelData = var7;
      this.level = new ClientLevel(
         this,
         var7,
         var3,
         var4,
         this.serverChunkRadius,
         this.serverSimulationDistance,
         this.minecraft::getProfiler,
         this.minecraft.levelRenderer,
         var5,
         var1.seed()
      );
      this.minecraft.setLevel(this.level);
      if (this.minecraft.player == null) {
         this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
         this.minecraft.player.setYRot(-180.0F);
         if (this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
         }
      }

      this.minecraft.debugRenderer.clear();
      this.minecraft.player.resetPos();
      int var8 = var1.playerId();
      this.minecraft.player.setId(var8);
      this.level.addPlayer(var8, this.minecraft.player);
      this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
      this.minecraft.cameraEntity = this.minecraft.player;
      this.minecraft.setScreen(new ReceivingLevelScreen());
      this.minecraft.player.setReducedDebugInfo(var1.reducedDebugInfo());
      this.minecraft.player.setShowDeathScreen(var1.showDeathScreen());
      this.minecraft.player.setLastDeathLocation(var1.lastDeathLocation());
      this.minecraft.gameMode.setLocalMode(var1.gameType(), var1.previousGameType());
      this.minecraft.options.setServerRenderDistance(var1.chunkRadius());
      this.minecraft.options.broadcastOptions();
      this.connection
         .send(
            new ServerboundCustomPayloadPacket(
               ServerboundCustomPayloadPacket.BRAND, new FriendlyByteBuf(Unpooled.buffer()).writeUtf(ClientBrandRetriever.getClientModName())
            )
         );
      this.minecraft.getGame().onStartGameSession();
      this.telemetryManager.onPlayerInfoReceived(var1.gameType(), var1.hardcore());
   }

   @Override
   public void handleAddEntity(ClientboundAddEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      EntityType var2 = var1.getType();
      Entity var3 = var2.create(this.level);
      if (var3 != null) {
         var3.recreateFromPacket(var1);
         int var4 = var1.getId();
         this.level.putNonPlayerEntity(var4, var3);
         this.postAddEntitySoundInstance(var3);
      } else {
         LOGGER.warn("Skipping Entity with id {}", var2);
      }
   }

   private void postAddEntitySoundInstance(Entity var1) {
      if (var1 instanceof AbstractMinecart) {
         this.minecraft.getSoundManager().play(new MinecartSoundInstance((AbstractMinecart)var1));
      } else if (var1 instanceof Bee) {
         boolean var2 = ((Bee)var1).isAngry();
         Object var3;
         if (var2) {
            var3 = new BeeAggressiveSoundInstance((Bee)var1);
         } else {
            var3 = new BeeFlyingSoundInstance((Bee)var1);
         }

         this.minecraft.getSoundManager().queueTickingSound((TickableSoundInstance)var3);
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
      this.level.putNonPlayerEntity(var1.getId(), var8);
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
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null && var1.getUnpackedData() != null) {
         var2.getEntityData().assignValues(var1.getUnpackedData());
      }
   }

   @Override
   public void handleAddPlayer(ClientboundAddPlayerPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      PlayerInfo var2 = this.getPlayerInfo(var1.getPlayerId());
      if (var2 == null) {
         LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", var1.getPlayerId());
      } else {
         double var3 = var1.getX();
         double var5 = var1.getY();
         double var7 = var1.getZ();
         float var9 = (float)(var1.getyRot() * 360) / 256.0F;
         float var10 = (float)(var1.getxRot() * 360) / 256.0F;
         int var11 = var1.getEntityId();
         RemotePlayer var12 = new RemotePlayer(this.minecraft.level, var2.getProfile(), var2.getProfilePublicKey());
         var12.setId(var11);
         var12.syncPacketPositionCodec(var3, var5, var7);
         var12.absMoveTo(var3, var5, var7, var9, var10);
         var12.setOldPosAndRot();
         this.level.addPlayer(var11, var12);
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
            var2.lerpTo(var3, var5, var7, var9, var10, 3, true);
            var2.setOnGround(var1.isOnGround());
         }
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
               float var5 = var1.hasRotation() ? (float)(var1.getyRot() * 360) / 256.0F : var2.getYRot();
               float var6 = var1.hasRotation() ? (float)(var1.getxRot() * 360) / 256.0F : var2.getXRot();
               var2.lerpTo(var4.x(), var4.y(), var4.z(), var5, var6, 3, false);
            } else if (var1.hasRotation()) {
               float var7 = (float)(var1.getyRot() * 360) / 256.0F;
               float var8 = (float)(var1.getxRot() * 360) / 256.0F;
               var2.lerpTo(var2.getX(), var2.getY(), var2.getZ(), var7, var8, 3, false);
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
      if (var1.requestDismountVehicle()) {
         var2.removeVehicle();
      }

      Vec3 var3 = var2.getDeltaMovement();
      boolean var4 = var1.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.X);
      boolean var5 = var1.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Y);
      boolean var6 = var1.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Z);
      double var7;
      double var9;
      if (var4) {
         var7 = var3.x();
         var9 = var2.getX() + var1.getX();
         var2.xOld += var1.getX();
      } else {
         var7 = 0.0;
         var9 = var1.getX();
         var2.xOld = var9;
      }

      double var11;
      double var13;
      if (var5) {
         var11 = var3.y();
         var13 = var2.getY() + var1.getY();
         var2.yOld += var1.getY();
      } else {
         var11 = 0.0;
         var13 = var1.getY();
         var2.yOld = var13;
      }

      double var15;
      double var17;
      if (var6) {
         var15 = var3.z();
         var17 = var2.getZ() + var1.getZ();
         var2.zOld += var1.getZ();
      } else {
         var15 = 0.0;
         var17 = var1.getZ();
         var2.zOld = var17;
      }

      var2.setPosRaw(var9, var13, var17);
      var2.xo = var9;
      var2.yo = var13;
      var2.zo = var17;
      var2.setDeltaMovement(var7, var11, var15);
      float var19 = var1.getYRot();
      float var20 = var1.getXRot();
      if (var1.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT)) {
         var20 += var2.getXRot();
      }

      if (var1.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT)) {
         var19 += var2.getYRot();
      }

      var2.absMoveTo(var9, var13, var17, var19, var20);
      this.connection.send(new ServerboundAcceptTeleportationPacket(var1.getId()));
      this.connection.send(new ServerboundMovePlayerPacket.PosRot(var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot(), false));
   }

   @Override
   public void handleChatPreview(ClientboundChatPreviewPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ChatScreen var2 = this.minecraft.gui.getChat().getFocusedChat();
      if (var2 != null) {
         var2.getChatPreview().handleResponse(var1.queryId(), var1.preview());
      }
   }

   @Override
   public void handleSetDisplayChatPreview(ClientboundSetDisplayChatPreviewPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ServerData var2 = this.minecraft.getCurrentServer();
      if (var2 != null) {
         var2.setChatPreviewEnabled(var1.enabled());
      }
   }

   @Override
   public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      int var2 = 19 | (var1.shouldSuppressLightUpdates() ? 128 : 0);
      var1.runUpdates((var2x, var3) -> this.level.setServerVerifiedBlockState(var2x, var3, var2));
   }

   @Override
   public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.updateLevelChunk(var1.getX(), var1.getZ(), var1.getChunkData());
      this.queueLightUpdate(var1.getX(), var1.getZ(), var1.getLightData());
   }

   private void updateLevelChunk(int var1, int var2, ClientboundLevelChunkPacketData var3) {
      this.level.getChunkSource().replaceWithPacketData(var1, var2, var3.getReadBuffer(), var3.getHeightmaps(), var3.getBlockEntitiesTagsConsumer(var1, var2));
   }

   private void queueLightUpdate(int var1, int var2, ClientboundLightUpdatePacketData var3) {
      this.level.queueLightUpdate(() -> {
         this.applyLightData(var1, var2, var3);
         LevelChunk var4 = this.level.getChunkSource().getChunk(var1, var2, false);
         if (var4 != null) {
            this.enableChunkLight(var4, var1, var2);
         }
      });
   }

   private void enableChunkLight(LevelChunk var1, int var2, int var3) {
      LevelLightEngine var4 = this.level.getChunkSource().getLightEngine();
      LevelChunkSection[] var5 = var1.getSections();
      ChunkPos var6 = var1.getPos();
      var4.enableLightSources(var6, true);

      for(int var7 = 0; var7 < var5.length; ++var7) {
         LevelChunkSection var8 = var5[var7];
         int var9 = this.level.getSectionYFromSectionIndex(var7);
         var4.updateSectionStatus(SectionPos.of(var6, var9), var8.hasOnlyAir());
         this.level.setSectionDirtyWithNeighbors(var2, var9, var3);
      }

      this.level.setLightReady(var2, var3);
   }

   @Override
   public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      int var2 = var1.getX();
      int var3 = var1.getZ();
      ClientChunkCache var4 = this.level.getChunkSource();
      var4.drop(var2, var3);
      this.queueLightUpdate(var1);
   }

   private void queueLightUpdate(ClientboundForgetLevelChunkPacket var1) {
      this.level.queueLightUpdate(() -> {
         LevelLightEngine var2 = this.level.getLightEngine();

         for(int var3 = this.level.getMinSection(); var3 < this.level.getMaxSection(); ++var3) {
            var2.updateSectionStatus(SectionPos.of(var1.getX(), var3, var1.getZ()), true);
         }

         var2.enableLightSources(new ChunkPos(var1.getX(), var1.getZ()), false);
         this.level.setLightReady(var1.getX(), var1.getZ());
      });
   }

   @Override
   public void handleBlockUpdate(ClientboundBlockUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.level.setServerVerifiedBlockState(var1.getPos(), var1.getBlockState(), 19);
   }

   @Override
   public void handleDisconnect(ClientboundDisconnectPacket var1) {
      this.connection.disconnect(var1.getReason());
   }

   @Override
   public void onDisconnect(Component var1) {
      this.minecraft.clearLevel();
      this.telemetryManager.onDisconnect();
      if (this.callbackScreen != null) {
         if (this.callbackScreen instanceof RealmsScreen) {
            this.minecraft.setScreen(new DisconnectedRealmsScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, var1));
         } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, var1));
         }
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(new JoinMultiplayerScreen(new TitleScreen()), GENERIC_DISCONNECT_MESSAGE, var1));
      }
   }

   public void send(Packet<?> var1) {
      this.connection.send(var1);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
            var5.shrink(var1.getAmount());
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
      Registry var2 = this.registryAccess.registryOrThrow(Registry.CHAT_TYPE_REGISTRY);
      ChatType var3 = var1.resolveType(var2);
      this.minecraft.gui.handleSystemChat(var3, var1.content());
   }

   @Override
   public void handlePlayerChat(ClientboundPlayerChatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ChatSender var2 = var1.sender();
      if (var1.hasExpired(Instant.now())) {
         LOGGER.warn("Received expired chat packet from {}", var2.name().getString());
      }

      Registry var3 = this.registryAccess.registryOrThrow(Registry.CHAT_TYPE_REGISTRY);
      ChatType var4 = var1.resolveType(var3);
      PlayerChatMessage var5 = var1.getMessage();
      this.handlePlayerChat(var4, var5, var2);
   }

   private void handlePlayerChat(ChatType var1, PlayerChatMessage var2, ChatSender var3) {
      boolean var4 = this.minecraft.options.onlyShowSecureChat().get();
      PlayerInfo var5 = this.getPlayerInfo(var2.signature().sender());
      if (var5 != null && !this.hasValidSignature(var2, var5)) {
         LOGGER.warn("Received chat packet without valid signature from {}", var5.getProfile().getName());
         if (var4) {
            return;
         }
      }

      Component var6 = var4 ? var2.signedContent() : var2.serverContent();
      this.minecraft.gui.handlePlayerChat(var1, var6, var3);
   }

   private boolean hasValidSignature(PlayerChatMessage var1, PlayerInfo var2) {
      ProfilePublicKey var3 = var2.getProfilePublicKey();
      return var3 != null && var1.verify(var3);
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
         } else if (var1.getAction() == 1) {
            var2.animateHurt();
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
   public void handleSetTime(ClientboundSetTimePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.level.setGameTime(var1.getGameTime());
      this.minecraft.level.setDayTime(var1.getDayTime());
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.level.setDefaultSpawnPos(var1.getPos(), var1.getAngle());
      Screen var3 = this.minecraft.screen;
      if (var3 instanceof ReceivingLevelScreen var2) {
         var2.loadingPacketsReceived();
      }
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

         for(int var7 : var1.getPassengers()) {
            Entity var8 = this.level.getEntity(var7);
            if (var8 != null) {
               var8.startRiding(var2, true);
               if (var8 == this.minecraft.player && !var3) {
                  if (var2 instanceof Boat) {
                     this.minecraft.player.yRotO = var2.getYRot();
                     this.minecraft.player.setYRot(var2.getYRot());
                     this.minecraft.player.setYHeadRot(var2.getYRot());
                  }

                  this.minecraft
                     .gui
                     .setOverlayMessage(Component.translatable("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage()), false);
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
      for(InteractionHand var4 : InteractionHand.values()) {
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
         if (var1.getEventId() == 21) {
            this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)var2));
         } else if (var1.getEventId() == 35) {
            boolean var3 = true;
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.TOTEM_OF_UNDYING, 30);
            this.level.playLocalSound(var2.getX(), var2.getY(), var2.getZ(), SoundEvents.TOTEM_USE, var2.getSoundSource(), 1.0F, 1.0F, false);
            if (var2 == this.minecraft.player) {
               this.minecraft.gameRenderer.displayItemActivation(findTotem(this.minecraft.player));
            }
         } else {
            var2.handleEntityEvent(var1.getEventId());
         }
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
      ResourceKey var2 = var1.getDimension();
      Holder var3 = this.registryAccess.<DimensionType>registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).getHolderOrThrow(var1.getDimensionType());
      LocalPlayer var4 = this.minecraft.player;
      int var5 = var4.getId();
      if (var2 != var4.level.dimension()) {
         Scoreboard var6 = this.level.getScoreboard();
         Map var7 = this.level.getAllMapData();
         boolean var8 = var1.isDebug();
         boolean var9 = var1.isFlat();
         ClientLevel.ClientLevelData var10 = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), var9);
         this.levelData = var10;
         this.level = new ClientLevel(
            this,
            var10,
            var2,
            var3,
            this.serverChunkRadius,
            this.serverSimulationDistance,
            this.minecraft::getProfiler,
            this.minecraft.levelRenderer,
            var8,
            var1.getSeed()
         );
         this.level.setScoreboard(var6);
         this.level.addMapData(var7);
         this.minecraft.setLevel(this.level);
         this.minecraft.setScreen(new ReceivingLevelScreen());
      }

      String var11 = var4.getServerBrand();
      this.minecraft.cameraEntity = null;
      LocalPlayer var12 = this.minecraft.gameMode.createPlayer(this.level, var4.getStats(), var4.getRecipeBook(), var4.isShiftKeyDown(), var4.isSprinting());
      var12.setId(var5);
      this.minecraft.player = var12;
      if (var2 != var4.level.dimension()) {
         this.minecraft.getMusicManager().stopPlaying();
      }

      this.minecraft.cameraEntity = var12;
      var12.getEntityData().assignValues(var4.getEntityData().getAll());
      if (var1.shouldKeepAllPlayerData()) {
         var12.getAttributes().assignValues(var4.getAttributes());
      }

      var12.resetPos();
      var12.setServerBrand(var11);
      this.level.addPlayer(var5, var12);
      var12.setYRot(-180.0F);
      var12.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(var12);
      var12.setReducedDebugInfo(var4.isReducedDebugInfo());
      var12.setShowDeathScreen(var4.shouldShowDeathScreen());
      var12.setLastDeathLocation(var1.getLastDeathLocation());
      if (this.minecraft.screen instanceof DeathScreen) {
         this.minecraft.setScreen(null);
      }

      this.minecraft.gameMode.setLocalMode(var1.getPlayerGameType(), var1.getPreviousPlayerGameType());
   }

   @Override
   public void handleExplosion(ClientboundExplodePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Explosion var2 = new Explosion(this.minecraft.level, null, var1.getX(), var1.getY(), var1.getZ(), var1.getPower(), var1.getToBlow());
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
      Entity var2 = this.level.getEntity(var1.getEntityId());
      if (var2 instanceof AbstractHorse) {
         LocalPlayer var3 = this.minecraft.player;
         AbstractHorse var4 = (AbstractHorse)var2;
         SimpleContainer var5 = new SimpleContainer(var1.getSize());
         HorseInventoryMenu var6 = new HorseInventoryMenu(var1.getContainerId(), var3.getInventory(), var5, var4);
         var3.containerMenu = var6;
         this.minecraft.setScreen(new HorseInventoryScreen(var6, var3.getInventory(), var4));
      }
   }

   @Override
   public void handleOpenScreen(ClientboundOpenScreenPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      MenuScreens.create(var1.getType(), this.minecraft, var1.getContainerId(), var1.getTitle());
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
            var5 = var6.getSelectedTab() != CreativeModeTab.TAB_INVENTORY.getId();
         }

         if (var1.getContainerId() == 0 && InventoryMenu.isHotbarSlot(var4)) {
            if (!var3.isEmpty()) {
               ItemStack var7 = var2.inventoryMenu.getSlot(var4).getItem();
               if (var7.isEmpty() || var7.getCount() < var3.getCount()) {
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
      Object var3 = this.level.getBlockEntity(var2);
      if (!(var3 instanceof SignBlockEntity)) {
         BlockState var4 = this.level.getBlockState(var2);
         var3 = new SignBlockEntity(var2, var4);
         ((BlockEntity)var3).setLevel(this.level);
      }

      this.minecraft.player.openTextEdit((SignBlockEntity)var3);
   }

   @Override
   public void handleBlockEntityData(ClientboundBlockEntityDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      BlockPos var2 = var1.getPos();
      this.minecraft.level.getBlockEntity(var2, var1.getType()).ifPresent(var2x -> {
         CompoundTag var3 = var1.getTag();
         if (var3 != null) {
            var2x.load(var3);
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
      Entity var2 = this.level.getEntity(var1.getEntity());
      if (var2 != null) {
         var1.getSlots().forEach(var1x -> var2.setItemSlot((EquipmentSlot)var1x.getFirst(), (ItemStack)var1x.getSecond()));
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
            this.minecraft.setScreen(new ReceivingLevelScreen());
         } else if (var5 == 1) {
            this.minecraft
               .setScreen(
                  new WinScreen(
                     true,
                     () -> this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN))
                  )
               );
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
      }
   }

   @Override
   public void handleMapItemData(ClientboundMapItemDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      MapRenderer var2 = this.minecraft.gameRenderer.getMapRenderer();
      int var3 = var1.getMapId();
      String var4 = MapItem.makeKey(var3);
      MapItemSavedData var5 = this.minecraft.level.getMapData(var4);
      if (var5 == null) {
         var5 = MapItemSavedData.createForClient(var1.getScale(), var1.isLocked(), this.minecraft.level.dimension());
         this.minecraft.level.setMapData(var4, var5);
      }

      var1.applyToMap(var5);
      var2.update(var3, var5);
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
         Advancement var3 = this.advancements.getAdvancements().get(var2);
         this.advancements.setSelectedTab(var3, false);
      }
   }

   @Override
   public void handleCommands(ClientboundCommandsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.commands = new CommandDispatcher(var1.getRoot(new CommandBuildContext(this.registryAccess)));
   }

   @Override
   public void handleStopSoundEvent(ClientboundStopSoundPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.getSoundManager().stop(var1.getName(), var1.getSource());
   }

   @Override
   public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.suggestionsProvider.completeCustomSuggestions(var1.getId(), var1.getSuggestions());
   }

   @Override
   public void handleUpdateRecipes(ClientboundUpdateRecipesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.recipeManager.replaceRecipes(var1.getRecipes());
      ClientRecipeBook var2 = this.minecraft.player.getRecipeBook();
      var2.setupCollections(this.recipeManager.getRecipes());
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

      for(Entry var3 : var1.getStats().entrySet()) {
         Stat var4 = (Stat)var3.getKey();
         int var5 = var3.getValue();
         this.minecraft.player.getStats().setValue(this.minecraft.player, var4, var5);
      }

      if (this.minecraft.screen instanceof StatsUpdateListener) {
         ((StatsUpdateListener)this.minecraft.screen).onStatsUpdated();
      }
   }

   @Override
   public void handleAddOrRemoveRecipes(ClientboundRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ClientRecipeBook var2 = this.minecraft.player.getRecipeBook();
      var2.setBookSettings(var1.getBookSettings());
      ClientboundRecipePacket.State var3 = var1.getState();
      switch(var3) {
         case REMOVE:
            for(ResourceLocation var11 : var1.getRecipes()) {
               this.recipeManager.byKey(var11).ifPresent(var2::remove);
            }
            break;
         case INIT:
            for(ResourceLocation var9 : var1.getRecipes()) {
               this.recipeManager.byKey(var9).ifPresent(var2::add);
            }

            for(ResourceLocation var10 : var1.getHighlights()) {
               this.recipeManager.byKey(var10).ifPresent(var2::addHighlight);
            }
            break;
         case ADD:
            for(ResourceLocation var5 : var1.getRecipes()) {
               this.recipeManager.byKey(var5).ifPresent(var2x -> {
                  var2.add(var2x);
                  var2.addHighlight(var2x);
                  RecipeToast.addOrUpdate(this.minecraft.getToasts(), var2x);
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
         MobEffect var3 = var1.getEffect();
         if (var3 != null) {
            MobEffectInstance var4 = new MobEffectInstance(
               var3,
               var1.getEffectDurationTicks(),
               var1.getEffectAmplifier(),
               var1.isEffectAmbient(),
               var1.isEffectVisible(),
               var1.effectShowsIcon(),
               null,
               Optional.ofNullable(var1.getFactorData())
            );
            var4.setNoCounter(var1.isSuperLongDuration());
            ((LivingEntity)var2).forceAddEffect(var4, null);
         }
      }
   }

   @Override
   public void handleUpdateTags(ClientboundUpdateTagsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      var1.getTags().forEach(this::updateTagsForRegistry);
      if (!this.connection.isMemoryConnection()) {
         Blocks.rebuildCache();
      }

      NonNullList var2 = NonNullList.create();

      for(Item var4 : Registry.ITEM) {
         var4.fillItemCategory(CreativeModeTab.TAB_SEARCH, var2);
      }

      this.minecraft.populateSearchTree(SearchRegistry.CREATIVE_NAMES, var2);
      this.minecraft.populateSearchTree(SearchRegistry.CREATIVE_TAGS, var2);
   }

   private <T> void updateTagsForRegistry(ResourceKey<? extends Registry<? extends T>> var1, TagNetworkSerialization.NetworkPayload var2) {
      if (!var2.isEmpty()) {
         Registry var3 = (Registry)this.registryAccess.registry(var1).orElseThrow(() -> new IllegalStateException("Unknown registry " + var1));
         HashMap var5 = new HashMap();
         TagNetworkSerialization.deserializeTagsFromNetwork(var1, var3, var2, var5::put);
         var3.bindTags(var5);
      }
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
      Entity var2 = this.level.getEntity(var1.getPlayerId());
      if (var2 == this.minecraft.player) {
         if (this.minecraft.player.shouldShowDeathScreen()) {
            this.minecraft.setScreen(new DeathScreen(var1.getMessage(), this.level.getLevelData().isHardcore()));
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
      ServerData var2 = this.minecraft.getCurrentServer();
      if (var2 != null) {
         var1.getMotd().ifPresent(var1x -> var2.motd = var1x);
         var1.getIconBase64().ifPresent(var1x -> {
            try {
               var2.setIconB64(ServerData.parseFavicon(var1x));
            } catch (ParseException var3x) {
               LOGGER.error("Invalid server icon", var3x);
            }
         });
         var2.setPreviewsChat(var1.previewsChat());
         ServerList.saveSingleServer(var2);
         if (this.minecraft.options.chatPreview().get()) {
            ServerData.ChatPreview var3 = var2.getChatPreview();
            if (var3 != null && !var3.isAcknowledged()) {
               this.minecraft.execute(() -> this.minecraft.setScreen(new ChatPreviewWarningScreen(this.minecraft.screen, var2)));
            }
         }
      }
   }

   @Override
   public void setActionBarText(ClientboundSetActionBarTextPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setOverlayMessage(var1.getText(), false);
   }

   @Override
   public void setTitleText(ClientboundSetTitleTextPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setTitle(var1.getText());
   }

   @Override
   public void setSubtitleText(ClientboundSetSubtitleTextPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setSubtitle(var1.getText());
   }

   @Override
   public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.setTimes(var1.getFadeIn(), var1.getStay(), var1.getFadeOut());
   }

   @Override
   public void handleTabListCustomisation(ClientboundTabListPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.getTabList().setHeader(var1.getHeader().getString().isEmpty() ? null : var1.getHeader());
      this.minecraft.gui.getTabList().setFooter(var1.getFooter().getString().isEmpty() ? null : var1.getFooter());
   }

   @Override
   public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 instanceof LivingEntity) {
         ((LivingEntity)var2).removeEffectNoUpdate(var1.getEffect());
      }
   }

   @Override
   public void handlePlayerInfo(ClientboundPlayerInfoPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);

      for(ClientboundPlayerInfoPacket.PlayerUpdate var3 : var1.getEntries()) {
         if (var1.getAction() == ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER) {
            this.minecraft.getPlayerSocialManager().removePlayer(var3.getProfile().getId());
            this.playerInfoMap.remove(var3.getProfile().getId());
         } else {
            PlayerInfo var4 = this.playerInfoMap.get(var3.getProfile().getId());
            if (var1.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
               var4 = new PlayerInfo(var3, this.minecraft.getServiceSignatureValidator());
               this.playerInfoMap.put(var4.getProfile().getId(), var4);
               this.minecraft.getPlayerSocialManager().addPlayer(var4);
            }

            if (var4 != null) {
               switch(var1.getAction()) {
                  case ADD_PLAYER:
                     var4.setGameMode(var3.getGameMode());
                     var4.setLatency(var3.getLatency());
                     var4.setTabListDisplayName(var3.getDisplayName());
                     break;
                  case UPDATE_GAME_MODE:
                     var4.setGameMode(var3.getGameMode());
                     break;
                  case UPDATE_LATENCY:
                     var4.setLatency(var3.getLatency());
                     break;
                  case UPDATE_DISPLAY_NAME:
                     var4.setTabListDisplayName(var3.getDisplayName());
               }
            }
         }
      }
   }

   @Override
   public void handleKeepAlive(ClientboundKeepAlivePacket var1) {
      this.send(new ServerboundKeepAlivePacket(var1.getId()));
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
   public void handleCustomSoundEvent(ClientboundCustomSoundPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft
         .getSoundManager()
         .play(
            new SimpleSoundInstance(
               var1.getName(),
               var1.getSource(),
               var1.getVolume(),
               var1.getPitch(),
               RandomSource.create(var1.getSeed()),
               false,
               0,
               SoundInstance.Attenuation.LINEAR,
               var1.getX(),
               var1.getY(),
               var1.getZ(),
               false
            )
         );
   }

   @Override
   public void handleResourcePack(ClientboundResourcePackPacket var1) {
      URL var2 = parseResourcePackUrl(var1.getUrl());
      if (var2 == null) {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
      } else {
         String var3 = var1.getHash();
         boolean var4 = var1.isRequired();
         ServerData var5 = this.minecraft.getCurrentServer();
         if (var5 != null && var5.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
            this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
            this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3, true));
         } else if (var5 != null
            && var5.getResourcePackStatus() != ServerData.ServerPackStatus.PROMPT
            && (!var4 || var5.getResourcePackStatus() != ServerData.ServerPackStatus.DISABLED)) {
            this.send(ServerboundResourcePackPacket.Action.DECLINED);
            if (var4) {
               this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
            }
         } else {
            this.minecraft
               .execute(
                  () -> this.minecraft
                        .setScreen(
                           new ConfirmScreen(
                              var4xx -> {
                                 this.minecraft.setScreen(null);
                                 ServerData var5x = this.minecraft.getCurrentServer();
                                 if (var4xx) {
                                    if (var5x != null) {
                                       var5x.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                                    }
               
                                    this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                                    this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3, true));
                                 } else {
                                    this.send(ServerboundResourcePackPacket.Action.DECLINED);
                                    if (var4) {
                                       this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                                    } else if (var5x != null) {
                                       var5x.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                                    }
                                 }
               
                                 if (var5x != null) {
                                    ServerList.saveSingleServer(var5x);
                                 }
                              },
                              var4
                                 ? Component.translatable("multiplayer.requiredTexturePrompt.line1")
                                 : Component.translatable("multiplayer.texturePrompt.line1"),
                              preparePackPrompt(
                                 var4
                                    ? Component.translatable("multiplayer.requiredTexturePrompt.line2").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                                    : Component.translatable("multiplayer.texturePrompt.line2"),
                                 var1.getPrompt()
                              ),
                              var4 ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES,
                              (Component)(var4 ? Component.translatable("menu.disconnect") : CommonComponents.GUI_NO)
                           )
                        )
               );
         }
      }
   }

   private static Component preparePackPrompt(Component var0, @Nullable Component var1) {
      return (Component)(var1 == null ? var0 : Component.translatable("multiplayer.texturePrompt.serverPrompt", var0, var1));
   }

   @Nullable
   private static URL parseResourcePackUrl(String var0) {
      try {
         URL var1 = new URL(var0);
         String var2 = var1.getProtocol();
         return !"http".equals(var2) && !"https".equals(var2) ? null : var1;
      } catch (MalformedURLException var3) {
         return null;
      }
   }

   private void downloadCallback(CompletableFuture<?> var1) {
      var1.thenRun(() -> this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED)).exceptionally(var1x -> {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         return null;
      });
   }

   private void send(ServerboundResourcePackPacket.Action var1) {
      this.connection.send(new ServerboundResourcePackPacket(var1));
   }

   @Override
   public void handleBossUpdate(ClientboundBossEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.minecraft.gui.getBossOverlay().update(var1);
   }

   @Override
   public void handleItemCooldown(ClientboundCooldownPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (var1.getDuration() == 0) {
         this.minecraft.player.getCooldowns().removeCooldown(var1.getItem());
      } else {
         this.minecraft.player.getCooldowns().addCooldown(var1.getItem(), var1.getDuration());
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
      if (var2.is(Items.WRITTEN_BOOK)) {
         this.minecraft.setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(var2)));
      }
   }

   @Override
   public void handleCustomPayload(ClientboundCustomPayloadPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      ResourceLocation var2 = var1.getIdentifier();
      FriendlyByteBuf var3 = null;

      try {
         var3 = var1.getData();
         if (ClientboundCustomPayloadPacket.BRAND.equals(var2)) {
            String var4 = var3.readUtf();
            this.minecraft.player.setServerBrand(var4);
            this.telemetryManager.onServerBrandReceived(var4);
         } else if (ClientboundCustomPayloadPacket.DEBUG_PATHFINDING_PACKET.equals(var2)) {
            int var34 = var3.readInt();
            float var5 = var3.readFloat();
            Path var6 = Path.createFromStream(var3);
            this.minecraft.debugRenderer.pathfindingRenderer.addPath(var34, var6, var5);
         } else if (ClientboundCustomPayloadPacket.DEBUG_NEIGHBORSUPDATE_PACKET.equals(var2)) {
            long var35 = var3.readVarLong();
            BlockPos var60 = var3.readBlockPos();
            ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(var35, var60);
         } else if (ClientboundCustomPayloadPacket.DEBUG_STRUCTURES_PACKET.equals(var2)) {
            DimensionType var36 = this.registryAccess.<DimensionType>registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(var3.readResourceLocation());
            BoundingBox var49 = new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt());
            int var61 = var3.readInt();
            ArrayList var7 = Lists.newArrayList();
            ArrayList var8 = Lists.newArrayList();

            for(int var9 = 0; var9 < var61; ++var9) {
               var7.add(new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt()));
               var8.add(var3.readBoolean());
            }

            this.minecraft.debugRenderer.structureRenderer.addBoundingBox(var49, var7, var8, var36);
         } else if (ClientboundCustomPayloadPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals(var2)) {
            ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer)
               .addPos(var3.readBlockPos(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat());
         } else if (ClientboundCustomPayloadPacket.DEBUG_VILLAGE_SECTIONS.equals(var2)) {
            int var37 = var3.readInt();

            for(int var50 = 0; var50 < var37; ++var50) {
               this.minecraft.debugRenderer.villageSectionsDebugRenderer.setVillageSection(var3.readSectionPos());
            }

            int var51 = var3.readInt();

            for(int var62 = 0; var62 < var51; ++var62) {
               this.minecraft.debugRenderer.villageSectionsDebugRenderer.setNotVillageSection(var3.readSectionPos());
            }
         } else if (ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET.equals(var2)) {
            BlockPos var38 = var3.readBlockPos();
            String var52 = var3.readUtf();
            int var63 = var3.readInt();
            BrainDebugRenderer.PoiInfo var71 = new BrainDebugRenderer.PoiInfo(var38, var52, var63);
            this.minecraft.debugRenderer.brainDebugRenderer.addPoi(var71);
         } else if (ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET.equals(var2)) {
            BlockPos var39 = var3.readBlockPos();
            this.minecraft.debugRenderer.brainDebugRenderer.removePoi(var39);
         } else if (ClientboundCustomPayloadPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals(var2)) {
            BlockPos var40 = var3.readBlockPos();
            int var53 = var3.readInt();
            this.minecraft.debugRenderer.brainDebugRenderer.setFreeTicketCount(var40, var53);
         } else if (ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR.equals(var2)) {
            BlockPos var41 = var3.readBlockPos();
            int var54 = var3.readInt();
            int var64 = var3.readInt();
            ArrayList var72 = Lists.newArrayList();

            for(int var75 = 0; var75 < var64; ++var75) {
               int var79 = var3.readInt();
               boolean var10 = var3.readBoolean();
               String var11 = var3.readUtf(255);
               var72.add(new GoalSelectorDebugRenderer.DebugGoal(var41, var79, var11, var10));
            }

            this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(var54, var72);
         } else if (ClientboundCustomPayloadPacket.DEBUG_RAIDS.equals(var2)) {
            int var42 = var3.readInt();
            ArrayList var55 = Lists.newArrayList();

            for(int var65 = 0; var65 < var42; ++var65) {
               var55.add(var3.readBlockPos());
            }

            this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(var55);
         } else if (ClientboundCustomPayloadPacket.DEBUG_BRAIN.equals(var2)) {
            double var43 = var3.readDouble();
            double var66 = var3.readDouble();
            double var76 = var3.readDouble();
            PositionImpl var81 = new PositionImpl(var43, var66, var76);
            UUID var83 = var3.readUUID();
            int var12 = var3.readInt();
            String var13 = var3.readUtf();
            String var14 = var3.readUtf();
            int var15 = var3.readInt();
            float var16 = var3.readFloat();
            float var17 = var3.readFloat();
            String var18 = var3.readUtf();
            Path var19 = var3.readNullable(Path::createFromStream);
            boolean var20 = var3.readBoolean();
            int var21 = var3.readInt();
            BrainDebugRenderer.BrainDump var22 = new BrainDebugRenderer.BrainDump(
               var83, var12, var13, var14, var15, var16, var17, var81, var18, var19, var20, var21
            );
            int var23 = var3.readVarInt();

            for(int var24 = 0; var24 < var23; ++var24) {
               String var25 = var3.readUtf();
               var22.activities.add(var25);
            }

            int var97 = var3.readVarInt();

            for(int var98 = 0; var98 < var97; ++var98) {
               String var26 = var3.readUtf();
               var22.behaviors.add(var26);
            }

            int var99 = var3.readVarInt();

            for(int var100 = 0; var100 < var99; ++var100) {
               String var27 = var3.readUtf();
               var22.memories.add(var27);
            }

            int var101 = var3.readVarInt();

            for(int var102 = 0; var102 < var101; ++var102) {
               BlockPos var28 = var3.readBlockPos();
               var22.pois.add(var28);
            }

            int var103 = var3.readVarInt();

            for(int var104 = 0; var104 < var103; ++var104) {
               BlockPos var29 = var3.readBlockPos();
               var22.potentialPois.add(var29);
            }

            int var105 = var3.readVarInt();

            for(int var106 = 0; var106 < var105; ++var106) {
               String var30 = var3.readUtf();
               var22.gossips.add(var30);
            }

            this.minecraft.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump(var22);
         } else if (ClientboundCustomPayloadPacket.DEBUG_BEE.equals(var2)) {
            double var44 = var3.readDouble();
            double var67 = var3.readDouble();
            double var77 = var3.readDouble();
            PositionImpl var82 = new PositionImpl(var44, var67, var77);
            UUID var84 = var3.readUUID();
            int var85 = var3.readInt();
            BlockPos var86 = var3.readNullable(FriendlyByteBuf::readBlockPos);
            BlockPos var87 = var3.readNullable(FriendlyByteBuf::readBlockPos);
            int var88 = var3.readInt();
            Path var89 = var3.readNullable(Path::createFromStream);
            BeeDebugRenderer.BeeInfo var90 = new BeeDebugRenderer.BeeInfo(var84, var85, var82, var89, var86, var87, var88);
            int var91 = var3.readVarInt();

            for(int var92 = 0; var92 < var91; ++var92) {
               String var94 = var3.readUtf();
               var90.goals.add(var94);
            }

            int var93 = var3.readVarInt();

            for(int var95 = 0; var95 < var93; ++var95) {
               BlockPos var96 = var3.readBlockPos();
               var90.blacklistedHives.add(var96);
            }

            this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo(var90);
         } else if (ClientboundCustomPayloadPacket.DEBUG_HIVE.equals(var2)) {
            BlockPos var45 = var3.readBlockPos();
            String var56 = var3.readUtf();
            int var68 = var3.readInt();
            int var73 = var3.readInt();
            boolean var78 = var3.readBoolean();
            BeeDebugRenderer.HiveInfo var80 = new BeeDebugRenderer.HiveInfo(var45, var56, var68, var73, var78, this.level.getGameTime());
            this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo(var80);
         } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR.equals(var2)) {
            this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
         } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(var2)) {
            BlockPos var46 = var3.readBlockPos();
            int var57 = var3.readInt();
            String var69 = var3.readUtf();
            int var74 = var3.readInt();
            this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker(var46, var57, var69, var74);
         } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT.equals(var2)) {
            GameEvent var47 = Registry.GAME_EVENT.get(new ResourceLocation(var3.readUtf()));
            Vec3 var58 = new Vec3(var3.readDouble(), var3.readDouble(), var3.readDouble());
            this.minecraft.debugRenderer.gameEventListenerRenderer.trackGameEvent(var47, var58);
         } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT_LISTENER.equals(var2)) {
            ResourceLocation var48 = var3.readResourceLocation();
            PositionSource var59 = Registry.POSITION_SOURCE_TYPE
               .getOptional(var48)
               .orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + var48))
               .read(var3);
            int var70 = var3.readVarInt();
            this.minecraft.debugRenderer.gameEventListenerRenderer.trackListener(var59, var70);
         } else {
            LOGGER.warn("Unknown custom packed identifier: {}", var2);
         }
      } finally {
         if (var3 != null) {
            var3.release();
         }
      }
   }

   @Override
   public void handleAddObjective(ClientboundSetObjectivePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      String var3 = var1.getObjectiveName();
      if (var1.getMethod() == 0) {
         var2.addObjective(var3, ObjectiveCriteria.DUMMY, var1.getDisplayName(), var1.getRenderType());
      } else if (var2.hasObjective(var3)) {
         Objective var4 = var2.getObjective(var3);
         if (var1.getMethod() == 1) {
            var2.removeObjective(var4);
         } else if (var1.getMethod() == 2) {
            var4.setRenderType(var1.getRenderType());
            var4.setDisplayName(var1.getDisplayName());
         }
      }
   }

   @Override
   public void handleSetScore(ClientboundSetScorePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      String var3 = var1.getObjectiveName();
      switch(var1.getMethod()) {
         case CHANGE:
            Objective var4 = var2.getOrCreateObjective(var3);
            Score var5 = var2.getOrCreatePlayerScore(var1.getOwner(), var4);
            var5.setScore(var1.getScore());
            break;
         case REMOVE:
            var2.resetPlayerScore(var1.getOwner(), var2.getObjective(var3));
      }
   }

   @Override
   public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      String var3 = var1.getObjectiveName();
      Objective var4 = var3 == null ? null : var2.getOrCreateObjective(var3);
      var2.setDisplayObjective(var1.getSlot(), var4);
   }

   @Override
   public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      ClientboundSetPlayerTeamPacket.Action var4 = var1.getTeamAction();
      PlayerTeam var3;
      if (var4 == ClientboundSetPlayerTeamPacket.Action.ADD) {
         var3 = var2.addPlayerTeam(var1.getName());
      } else {
         var3 = var2.getPlayerTeam(var1.getName());
         if (var3 == null) {
            LOGGER.warn(
               "Received packet for unknown team {}: team action: {}, player action: {}",
               new Object[]{var1.getName(), var1.getTeamAction(), var1.getPlayerAction()}
            );
            return;
         }
      }

      Optional var5 = var1.getParameters();
      var5.ifPresent(var1x -> {
         var3.setDisplayName(var1x.getDisplayName());
         var3.setColor(var1x.getColor());
         var3.unpackOptions(var1x.getOptions());
         Team.Visibility var2x = Team.Visibility.byName(var1x.getNametagVisibility());
         if (var2x != null) {
            var3.setNameTagVisibility(var2x);
         }

         Team.CollisionRule var3x = Team.CollisionRule.byName(var1x.getCollisionRule());
         if (var3x != null) {
            var3.setCollisionRule(var3x);
         }

         var3.setPlayerPrefix(var1x.getPlayerPrefix());
         var3.setPlayerSuffix(var1x.getPlayerSuffix());
      });
      ClientboundSetPlayerTeamPacket.Action var6 = var1.getPlayerAction();
      if (var6 == ClientboundSetPlayerTeamPacket.Action.ADD) {
         for(String var8 : var1.getPlayers()) {
            var2.addPlayerToTeam(var8, var3);
         }
      } else if (var6 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         for(String var10 : var1.getPlayers()) {
            var2.removePlayerFromTeam(var10, var3);
         }
      }

      if (var4 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         var2.removePlayerTeam(var3);
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
         for(int var18 = 0; var18 < var1.getCount(); ++var18) {
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
   public void handlePing(ClientboundPingPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.send(new ServerboundPongPacket(var1.getId()));
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

            for(ClientboundUpdateAttributesPacket.AttributeSnapshot var5 : var1.getValues()) {
               AttributeInstance var6 = var3.getInstance(var5.getAttribute());
               if (var6 == null) {
                  LOGGER.warn("Entity {} does not have attribute {}", var2, Registry.ATTRIBUTE.getKey(var5.getAttribute()));
               } else {
                  var6.setBaseValue(var5.getBase());
                  var6.removeModifiers();

                  for(AttributeModifier var8 : var5.getModifiers()) {
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
               var3.setupGhostRecipe(var2x, var2.slots);
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
      this.readSectionList(var1, var2, var4, LightLayer.SKY, var5, var6, var7, var3.getTrustEdges());
      BitSet var8 = var3.getBlockYMask();
      BitSet var9 = var3.getEmptyBlockYMask();
      Iterator var10 = var3.getBlockUpdates().iterator();
      this.readSectionList(var1, var2, var4, LightLayer.BLOCK, var8, var9, var10, var3.getTrustEdges());
      this.level.setLightReady(var1, var2);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleMerchantOffers(ClientboundMerchantOffersPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      AbstractContainerMenu var2 = this.minecraft.player.containerMenu;
      if (var1.getContainerId() == var2.containerId && var2 instanceof MerchantMenu var3) {
         var3.setOffers(new MerchantOffers(var1.getOffers().createTag()));
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

   private void readSectionList(int var1, int var2, LevelLightEngine var3, LightLayer var4, BitSet var5, BitSet var6, Iterator<byte[]> var7, boolean var8) {
      for(int var9 = 0; var9 < var3.getLightSectionCount(); ++var9) {
         int var10 = var3.getMinLightSection() + var9;
         boolean var11 = var5.get(var9);
         boolean var12 = var6.get(var9);
         if (var11 || var12) {
            var3.queueSectionData(var4, SectionPos.of(var1, var10, var2), var11 ? new DataLayer((byte[])((byte[])var7.next()).clone()) : new DataLayer(), var8);
            this.level.setSectionDirtyWithNeighbors(var1, var10, var2);
         }
      }
   }

   @Override
   public Connection getConnection() {
      return this.connection;
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
      for(PlayerInfo var3 : this.playerInfoMap.values()) {
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

   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }
}
