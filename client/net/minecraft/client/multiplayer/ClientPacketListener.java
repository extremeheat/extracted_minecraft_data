package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.components.toasts.RecipeToast;
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
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerAckPacket;
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
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
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
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
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
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
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
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
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
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.Mth;
import net.minecraft.util.thread.BlockableEventLoop;
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
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.CreativeModeTab;
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
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPacketListener implements ClientGamePacketListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Component GENERIC_DISCONNECT_MESSAGE = new TranslatableComponent("disconnect.lost");
   private final Connection connection;
   private final GameProfile localGameProfile;
   private final Screen callbackScreen;
   private final Minecraft minecraft;
   private ClientLevel level;
   private ClientLevel.ClientLevelData levelData;
   private boolean started;
   private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
   private final ClientAdvancements advancements;
   private final ClientSuggestionProvider suggestionsProvider;
   private TagContainer tags;
   private final DebugQueryHandler debugQueryHandler;
   private int serverChunkRadius;
   private final Random random;
   private CommandDispatcher<SharedSuggestionProvider> commands;
   private final RecipeManager recipeManager;
   private final UUID id;
   private Set<ResourceKey<Level>> levels;
   private RegistryAccess registryAccess;

   public ClientPacketListener(Minecraft var1, Screen var2, Connection var3, GameProfile var4) {
      super();
      this.tags = TagContainer.EMPTY;
      this.debugQueryHandler = new DebugQueryHandler(this);
      this.serverChunkRadius = 3;
      this.random = new Random();
      this.commands = new CommandDispatcher();
      this.recipeManager = new RecipeManager();
      this.id = UUID.randomUUID();
      this.registryAccess = RegistryAccess.builtin();
      this.minecraft = var1;
      this.callbackScreen = var2;
      this.connection = var3;
      this.localGameProfile = var4;
      this.advancements = new ClientAdvancements(var1);
      this.suggestionsProvider = new ClientSuggestionProvider(this, var1);
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

   public void handleLogin(ClientboundLoginPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
      if (!this.connection.isMemoryConnection()) {
         StaticTags.resetAllToEmpty();
      }

      ArrayList var2 = Lists.newArrayList(var1.levels());
      Collections.shuffle(var2);
      this.levels = Sets.newLinkedHashSet(var2);
      this.registryAccess = var1.registryAccess();
      ResourceKey var3 = var1.getDimension();
      DimensionType var4 = var1.getDimensionType();
      this.serverChunkRadius = var1.getChunkRadius();
      boolean var5 = var1.isDebug();
      boolean var6 = var1.isFlat();
      ClientLevel.ClientLevelData var7 = new ClientLevel.ClientLevelData(Difficulty.NORMAL, var1.isHardcore(), var6);
      this.levelData = var7;
      this.level = new ClientLevel(this, var7, var3, var4, this.serverChunkRadius, this.minecraft::getProfiler, this.minecraft.levelRenderer, var5, var1.getSeed());
      this.minecraft.setLevel(this.level);
      if (this.minecraft.player == null) {
         this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
         this.minecraft.player.yRot = -180.0F;
         if (this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
         }
      }

      this.minecraft.debugRenderer.clear();
      this.minecraft.player.resetPos();
      int var8 = var1.getPlayerId();
      this.minecraft.player.setId(var8);
      this.level.addPlayer(var8, this.minecraft.player);
      this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
      this.minecraft.cameraEntity = this.minecraft.player;
      this.minecraft.setScreen(new ReceivingLevelScreen());
      this.minecraft.player.setReducedDebugInfo(var1.isReducedDebugInfo());
      this.minecraft.player.setShowDeathScreen(var1.shouldShowDeathScreen());
      this.minecraft.gameMode.setLocalMode(var1.getGameType());
      this.minecraft.gameMode.setPreviousLocalMode(var1.getPreviousGameType());
      this.minecraft.options.broadcastOptions();
      this.connection.send(new ServerboundCustomPayloadPacket(ServerboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(ClientBrandRetriever.getClientModName())));
      this.minecraft.getGame().onStartGameSession();
   }

   public void handleAddEntity(ClientboundAddEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      EntityType var2 = var1.getType();
      Entity var3 = var2.create(this.level);
      if (var3 != null) {
         var3.recreateFromPacket(var1);
         int var4 = var1.getId();
         this.level.putNonPlayerEntity(var4, var3);
         if (var3 instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new MinecartSoundInstance((AbstractMinecart)var3));
         }
      }

   }

   public void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      ExperienceOrb var8 = new ExperienceOrb(this.level, var2, var4, var6, var1.getValue());
      var8.setPacketCoordinates(var2, var4, var6);
      var8.yRot = 0.0F;
      var8.xRot = 0.0F;
      var8.setId(var1.getId());
      this.level.putNonPlayerEntity(var1.getId(), var8);
   }

   public void handleAddPainting(ClientboundAddPaintingPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Painting var2 = new Painting(this.level, var1.getPos(), var1.getDirection(), var1.getMotive());
      var2.setId(var1.getId());
      var2.setUUID(var1.getUUID());
      this.level.putNonPlayerEntity(var1.getId(), var2);
   }

   public void handleSetEntityMotion(ClientboundSetEntityMotionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         var2.lerpMotion((double)var1.getXa() / 8000.0D, (double)var1.getYa() / 8000.0D, (double)var1.getZa() / 8000.0D);
      }
   }

   public void handleSetEntityData(ClientboundSetEntityDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null && var1.getUnpackedData() != null) {
         var2.getEntityData().assignValues(var1.getUnpackedData());
      }

   }

   public void handleAddPlayer(ClientboundAddPlayerPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      float var8 = (float)(var1.getyRot() * 360) / 256.0F;
      float var9 = (float)(var1.getxRot() * 360) / 256.0F;
      int var10 = var1.getEntityId();
      RemotePlayer var11 = new RemotePlayer(this.minecraft.level, this.getPlayerInfo(var1.getPlayerId()).getProfile());
      var11.setId(var10);
      var11.setPosAndOldPos(var2, var4, var6);
      var11.setPacketCoordinates(var2, var4, var6);
      var11.absMoveTo(var2, var4, var6, var8, var9);
      this.level.addPlayer(var10, var11);
   }

   public void handleTeleportEntity(ClientboundTeleportEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         double var3 = var1.getX();
         double var5 = var1.getY();
         double var7 = var1.getZ();
         var2.setPacketCoordinates(var3, var5, var7);
         if (!var2.isControlledByLocalInstance()) {
            float var9 = (float)(var1.getyRot() * 360) / 256.0F;
            float var10 = (float)(var1.getxRot() * 360) / 256.0F;
            var2.lerpTo(var3, var5, var7, var9, var10, 3, true);
            var2.setOnGround(var1.isOnGround());
         }

      }
   }

   public void handleSetCarriedItem(ClientboundSetCarriedItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (Inventory.isHotbarSlot(var1.getSlot())) {
         this.minecraft.player.getInventory().selected = var1.getSlot();
      }

   }

   public void handleMoveEntity(ClientboundMoveEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         if (!var2.isControlledByLocalInstance()) {
            float var4;
            if (var1.hasPosition()) {
               Vec3 var3 = var1.updateEntityPosition(var2.getPacketCoordinates());
               var2.setPacketCoordinates(var3);
               var4 = var1.hasRotation() ? (float)(var1.getyRot() * 360) / 256.0F : var2.yRot;
               float var5 = var1.hasRotation() ? (float)(var1.getxRot() * 360) / 256.0F : var2.xRot;
               var2.lerpTo(var3.x(), var3.y(), var3.z(), var4, var5, 3, false);
            } else if (var1.hasRotation()) {
               float var6 = (float)(var1.getyRot() * 360) / 256.0F;
               var4 = (float)(var1.getxRot() * 360) / 256.0F;
               var2.lerpTo(var2.getX(), var2.getY(), var2.getZ(), var6, var4, 3, false);
            }

            var2.setOnGround(var1.isOnGround());
         }

      }
   }

   public void handleRotateMob(ClientboundRotateHeadPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         float var3 = (float)(var1.getYHeadRot() * 360) / 256.0F;
         var2.lerpHeadTo(var3, 3);
      }
   }

   public void handleRemoveEntity(ClientboundRemoveEntitiesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);

      for(int var2 = 0; var2 < var1.getEntityIds().length; ++var2) {
         int var3 = var1.getEntityIds()[var2];
         this.level.removeEntity(var3, Entity.RemovalReason.DISCARDED);
      }

   }

   public void handleMovePlayer(ClientboundPlayerPositionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
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
         var7 = 0.0D;
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
         var11 = 0.0D;
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
         var15 = 0.0D;
         var17 = var1.getZ();
         var2.zOld = var17;
      }

      if (var2.tickCount > 0 && var2.getVehicle() != null) {
         var2.removeVehicle();
      }

      var2.setPosRaw(var9, var13, var17);
      var2.xo = var9;
      var2.yo = var13;
      var2.zo = var17;
      var2.setDeltaMovement(var7, var11, var15);
      float var19 = var1.getYRot();
      float var20 = var1.getXRot();
      if (var1.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT)) {
         var20 += var2.xRot;
      }

      if (var1.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT)) {
         var19 += var2.yRot;
      }

      var2.absMoveTo(var9, var13, var17, var19, var20);
      this.connection.send(new ServerboundAcceptTeleportationPacket(var1.getId()));
      this.connection.send(new ServerboundMovePlayerPacket.PosRot(var2.getX(), var2.getY(), var2.getZ(), var2.yRot, var2.xRot, false));
      if (!this.started) {
         this.started = true;
         this.minecraft.setScreen((Screen)null);
      }

   }

   public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      int var2 = 19 | (var1.shouldSuppressLightUpdates() ? 128 : 0);
      var1.runUpdates((var2x, var3) -> {
         this.level.setBlock(var2x, var3, var2);
      });
   }

   public void handleLevelChunk(ClientboundLevelChunkPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      int var2 = var1.getX();
      int var3 = var1.getZ();
      ChunkBiomeContainer var4 = var1.getBiomes() == null ? null : new ChunkBiomeContainer(this.registryAccess.registryOrThrow(Registry.BIOME_REGISTRY), var1.getBiomes());
      LevelChunk var5 = this.level.getChunkSource().replaceWithPacketData(var2, var3, var4, var1.getReadBuffer(), var1.getHeightmaps(), var1.getAvailableSections());

      for(int var6 = this.level.getMinSection(); var6 < this.level.getMaxSection(); ++var6) {
         this.level.setSectionDirtyWithNeighbors(var2, var6, var3);
      }

      if (var5 != null) {
         Iterator var10 = var1.getBlockEntitiesTags().iterator();

         while(var10.hasNext()) {
            CompoundTag var7 = (CompoundTag)var10.next();
            BlockPos var8 = new BlockPos(var7.getInt("x"), var7.getInt("y"), var7.getInt("z"));
            BlockEntity var9 = var5.getBlockEntity(var8, LevelChunk.EntityCreationType.IMMEDIATE);
            if (var9 != null) {
               var9.load(var7);
            }
         }
      }

   }

   public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      int var2 = var1.getX();
      int var3 = var1.getZ();
      ClientChunkCache var4 = this.level.getChunkSource();
      var4.drop(var2, var3);
      LevelLightEngine var5 = var4.getLightEngine();

      for(int var6 = this.level.getMinSection(); var6 < this.level.getMaxSection(); ++var6) {
         this.level.setSectionDirtyWithNeighbors(var2, var6, var3);
         var5.updateSectionStatus(SectionPos.of(var2, var6, var3), true);
      }

      var5.enableLightSources(new ChunkPos(var2, var3), false);
   }

   public void handleBlockUpdate(ClientboundBlockUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.level.setKnownState(var1.getPos(), var1.getBlockState());
   }

   public void handleDisconnect(ClientboundDisconnectPacket var1) {
      this.connection.disconnect(var1.getReason());
   }

   public void onDisconnect(Component var1) {
      this.minecraft.clearLevel();
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

   public void handleTakeItemEntity(ClientboundTakeItemEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getItemId());
      Object var3 = (LivingEntity)this.level.getEntity(var1.getPlayerId());
      if (var3 == null) {
         var3 = this.minecraft.player;
      }

      if (var2 != null) {
         if (var2 instanceof ExperienceOrb) {
            this.level.playLocalSound(var2.getX(), var2.getY(), var2.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.level.playLocalSound(var2.getX(), var2.getY(), var2.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F, false);
         }

         this.minecraft.particleEngine.add(new ItemPickupParticle(this.minecraft.getEntityRenderDispatcher(), this.minecraft.renderBuffers(), this.level, var2, (Entity)var3));
         if (var2 instanceof ItemEntity) {
            ItemEntity var4 = (ItemEntity)var2;
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

   public void handleChat(ClientboundChatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gui.handleChat(var1.getType(), var1.getMessage(), var1.getSender());
   }

   public void handleAnimate(ClientboundAnimatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         LivingEntity var3;
         if (var1.getAction() == 0) {
            var3 = (LivingEntity)var2;
            var3.swing(InteractionHand.MAIN_HAND);
         } else if (var1.getAction() == 3) {
            var3 = (LivingEntity)var2;
            var3.swing(InteractionHand.OFF_HAND);
         } else if (var1.getAction() == 1) {
            var2.animateHurt();
         } else if (var1.getAction() == 2) {
            Player var4 = (Player)var2;
            var4.stopSleepInBed(false, false);
         } else if (var1.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.CRIT);
         } else if (var1.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.ENCHANTED_HIT);
         }

      }
   }

   public void handleAddMob(ClientboundAddMobPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      float var8 = (float)(var1.getyRot() * 360) / 256.0F;
      float var9 = (float)(var1.getxRot() * 360) / 256.0F;
      LivingEntity var10 = (LivingEntity)EntityType.create(var1.getType(), this.minecraft.level);
      if (var10 != null) {
         var10.setPacketCoordinates(var2, var4, var6);
         var10.yBodyRot = (float)(var1.getyHeadRot() * 360) / 256.0F;
         var10.yHeadRot = (float)(var1.getyHeadRot() * 360) / 256.0F;
         if (var10 instanceof EnderDragon) {
            EnderDragonPart[] var11 = ((EnderDragon)var10).getSubEntities();

            for(int var12 = 0; var12 < var11.length; ++var12) {
               var11[var12].setId(var12 + var1.getId());
            }
         }

         var10.setId(var1.getId());
         var10.setUUID(var1.getUUID());
         var10.absMoveTo(var2, var4, var6, var8, var9);
         var10.setDeltaMovement((double)((float)var1.getXd() / 8000.0F), (double)((float)var1.getYd() / 8000.0F), (double)((float)var1.getZd() / 8000.0F));
         this.level.putNonPlayerEntity(var1.getId(), var10);
         if (var10 instanceof Bee) {
            boolean var13 = ((Bee)var10).isAngry();
            Object var14;
            if (var13) {
               var14 = new BeeAggressiveSoundInstance((Bee)var10);
            } else {
               var14 = new BeeFlyingSoundInstance((Bee)var10);
            }

            this.minecraft.getSoundManager().queueTickingSound((TickableSoundInstance)var14);
         }
      } else {
         LOGGER.warn("Skipping Entity with id {}", var1.getType());
      }

   }

   public void handleSetTime(ClientboundSetTimePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.setGameTime(var1.getGameTime());
      this.minecraft.level.setDayTime(var1.getDayTime());
   }

   public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.setDefaultSpawnPos(var1.getPos(), var1.getAngle());
   }

   public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getVehicle());
      if (var2 == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean var3 = var2.hasIndirectPassenger(this.minecraft.player);
         var2.ejectPassengers();
         int[] var4 = var1.getPassengers();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var4[var6];
            Entity var8 = this.level.getEntity(var7);
            if (var8 != null) {
               var8.startRiding(var2, true);
               if (var8 == this.minecraft.player && !var3) {
                  this.minecraft.gui.setOverlayMessage(new TranslatableComponent("mount.onboard", new Object[]{this.minecraft.options.keyShift.getTranslatedKeyMessage()}), false);
               }
            }
         }

      }
   }

   public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getSourceId());
      if (var2 instanceof Mob) {
         ((Mob)var2).setDelayedLeashHolderId(var1.getDestId());
      }

   }

   private static ItemStack findTotem(Player var0) {
      InteractionHand[] var1 = InteractionHand.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         InteractionHand var4 = var1[var3];
         ItemStack var5 = var0.getItemInHand(var4);
         if (var5.is(Items.TOTEM_OF_UNDYING)) {
            return var5;
         }
      }

      return new ItemStack(Items.TOTEM_OF_UNDYING);
   }

   public void handleEntityEvent(ClientboundEntityEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
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

   public void handleSetHealth(ClientboundSetHealthPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.hurtTo(var1.getHealth());
      this.minecraft.player.getFoodData().setFoodLevel(var1.getFood());
      this.minecraft.player.getFoodData().setSaturation(var1.getSaturation());
   }

   public void handleSetExperience(ClientboundSetExperiencePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.setExperienceValues(var1.getExperienceProgress(), var1.getTotalExperience(), var1.getExperienceLevel());
   }

   public void handleRespawn(ClientboundRespawnPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      ResourceKey var2 = var1.getDimension();
      DimensionType var3 = var1.getDimensionType();
      LocalPlayer var4 = this.minecraft.player;
      int var5 = var4.getId();
      this.started = false;
      if (var2 != var4.level.dimension()) {
         Scoreboard var6 = this.level.getScoreboard();
         boolean var7 = var1.isDebug();
         boolean var8 = var1.isFlat();
         ClientLevel.ClientLevelData var9 = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), var8);
         this.levelData = var9;
         this.level = new ClientLevel(this, var9, var2, var3, this.serverChunkRadius, this.minecraft::getProfiler, this.minecraft.levelRenderer, var7, var1.getSeed());
         this.level.setScoreboard(var6);
         this.minecraft.setLevel(this.level);
         this.minecraft.setScreen(new ReceivingLevelScreen());
      }

      String var10 = var4.getServerBrand();
      this.minecraft.cameraEntity = null;
      LocalPlayer var11 = this.minecraft.gameMode.createPlayer(this.level, var4.getStats(), var4.getRecipeBook(), var4.isShiftKeyDown(), var4.isSprinting());
      var11.setId(var5);
      this.minecraft.player = var11;
      if (var2 != var4.level.dimension()) {
         this.minecraft.getMusicManager().stopPlaying();
      }

      this.minecraft.cameraEntity = var11;
      var11.getEntityData().assignValues(var4.getEntityData().getAll());
      if (var1.shouldKeepAllPlayerData()) {
         var11.getAttributes().assignValues(var4.getAttributes());
      }

      var11.resetPos();
      var11.setServerBrand(var10);
      this.level.addPlayer(var5, var11);
      var11.yRot = -180.0F;
      var11.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(var11);
      var11.setReducedDebugInfo(var4.isReducedDebugInfo());
      var11.setShowDeathScreen(var4.shouldShowDeathScreen());
      if (this.minecraft.screen instanceof DeathScreen) {
         this.minecraft.setScreen((Screen)null);
      }

      this.minecraft.gameMode.setLocalMode(var1.getPlayerGameType());
      this.minecraft.gameMode.setPreviousLocalMode(var1.getPreviousPlayerGameType());
   }

   public void handleExplosion(ClientboundExplodePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Explosion var2 = new Explosion(this.minecraft.level, (Entity)null, var1.getX(), var1.getY(), var1.getZ(), var1.getPower(), var1.getToBlow());
      var2.finalizeExplosion(true);
      this.minecraft.player.setDeltaMovement(this.minecraft.player.getDeltaMovement().add((double)var1.getKnockbackX(), (double)var1.getKnockbackY(), (double)var1.getKnockbackZ()));
   }

   public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
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

   public void handleOpenScreen(ClientboundOpenScreenPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      MenuScreens.create(var1.getType(), this.minecraft, var1.getContainerId(), var1.getTitle());
   }

   public void handleContainerSetSlot(ClientboundContainerSetSlotPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      ItemStack var3 = var1.getItem();
      int var4 = var1.getSlot();
      this.minecraft.getTutorial().onGetItem(var3);
      if (var1.getContainerId() == -1) {
         if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
            var2.getInventory().setCarried(var3);
         }
      } else if (var1.getContainerId() == -2) {
         var2.getInventory().setItem(var4, var3);
      } else {
         boolean var5 = false;
         if (this.minecraft.screen instanceof CreativeModeInventoryScreen) {
            CreativeModeInventoryScreen var6 = (CreativeModeInventoryScreen)this.minecraft.screen;
            var5 = var6.getSelectedTab() != CreativeModeTab.TAB_INVENTORY.getId();
         }

         if (var1.getContainerId() == 0 && var1.getSlot() >= 36 && var4 < 45) {
            if (!var3.isEmpty()) {
               ItemStack var7 = var2.inventoryMenu.getSlot(var4).getItem();
               if (var7.isEmpty() || var7.getCount() < var3.getCount()) {
                  var3.setPopTime(5);
               }
            }

            var2.inventoryMenu.setItem(var4, var3);
         } else if (var1.getContainerId() == var2.containerMenu.containerId && (var1.getContainerId() != 0 || !var5)) {
            var2.containerMenu.setItem(var4, var3);
         }
      }

   }

   public void handleContainerAck(ClientboundContainerAckPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Object var2 = null;
      LocalPlayer var3 = this.minecraft.player;
      if (var1.getContainerId() == 0) {
         var2 = var3.inventoryMenu;
      } else if (var1.getContainerId() == var3.containerMenu.containerId) {
         var2 = var3.containerMenu;
      }

      if (var2 != null && !var1.isAccepted()) {
         this.send((Packet)(new ServerboundContainerAckPacket(var1.getContainerId(), var1.getUid(), true)));
      }

   }

   public void handleContainerContent(ClientboundContainerSetContentPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      if (var1.getContainerId() == 0) {
         var2.inventoryMenu.setAll(var1.getItems());
      } else if (var1.getContainerId() == var2.containerMenu.containerId) {
         var2.containerMenu.setAll(var1.getItems());
      }

   }

   public void handleOpenSignEditor(ClientboundOpenSignEditorPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      BlockPos var2 = var1.getPos();
      Object var3 = this.level.getBlockEntity(var2);
      if (!(var3 instanceof SignBlockEntity)) {
         BlockState var4 = this.level.getBlockState(var2);
         var3 = new SignBlockEntity(var2, var4);
         ((BlockEntity)var3).setLevel(this.level);
      }

      this.minecraft.player.openTextEdit((SignBlockEntity)var3);
   }

   public void handleBlockEntityData(ClientboundBlockEntityDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      BlockPos var2 = var1.getPos();
      BlockEntity var3 = this.minecraft.level.getBlockEntity(var2);
      int var4 = var1.getType();
      boolean var5 = var4 == 2 && var3 instanceof CommandBlockEntity;
      if (var4 == 1 && var3 instanceof SpawnerBlockEntity || var5 || var4 == 3 && var3 instanceof BeaconBlockEntity || var4 == 4 && var3 instanceof SkullBlockEntity || var4 == 6 && var3 instanceof BannerBlockEntity || var4 == 7 && var3 instanceof StructureBlockEntity || var4 == 8 && var3 instanceof TheEndGatewayBlockEntity || var4 == 9 && var3 instanceof SignBlockEntity || var4 == 11 && var3 instanceof BedBlockEntity || var4 == 5 && var3 instanceof ConduitBlockEntity || var4 == 12 && var3 instanceof JigsawBlockEntity || var4 == 13 && var3 instanceof CampfireBlockEntity || var4 == 14 && var3 instanceof BeehiveBlockEntity) {
         var3.load(var1.getTag());
      }

      if (var5 && this.minecraft.screen instanceof CommandBlockEditScreen) {
         ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
      }

   }

   public void handleContainerSetData(ClientboundContainerSetDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      if (var2.containerMenu != null && var2.containerMenu.containerId == var1.getContainerId()) {
         var2.containerMenu.setData(var1.getId(), var1.getValue());
      }

   }

   public void handleSetEquipment(ClientboundSetEquipmentPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getEntity());
      if (var2 != null) {
         var1.getSlots().forEach((var1x) -> {
            var2.setItemSlot((EquipmentSlot)var1x.getFirst(), (ItemStack)var1x.getSecond());
         });
      }

   }

   public void handleContainerClose(ClientboundContainerClosePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.clientSideCloseContainer();
   }

   public void handleBlockEvent(ClientboundBlockEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.blockEvent(var1.getPos(), var1.getBlock(), var1.getB0(), var1.getB1());
   }

   public void handleBlockDestruction(ClientboundBlockDestructionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.destroyBlockProgress(var1.getId(), var1.getPos(), var1.getProgress());
   }

   public void handleGameEvent(ClientboundGameEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      ClientboundGameEventPacket.Type var3 = var1.getEvent();
      float var4 = var1.getParam();
      int var5 = Mth.floor(var4 + 0.5F);
      if (var3 == ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE) {
         var2.displayClientMessage(new TranslatableComponent("block.minecraft.spawn.not_valid"), false);
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
            this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)));
            this.minecraft.setScreen(new ReceivingLevelScreen());
         } else if (var5 == 1) {
            this.minecraft.setScreen(new WinScreen(true, () -> {
               this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)));
            }));
         }
      } else if (var3 == ClientboundGameEventPacket.DEMO_EVENT) {
         Options var6 = this.minecraft.options;
         if (var4 == 0.0F) {
            this.minecraft.setScreen(new DemoIntroScreen());
         } else if (var4 == 101.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.movement", new Object[]{var6.keyUp.getTranslatedKeyMessage(), var6.keyLeft.getTranslatedKeyMessage(), var6.keyDown.getTranslatedKeyMessage(), var6.keyRight.getTranslatedKeyMessage()}));
         } else if (var4 == 102.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.jump", new Object[]{var6.keyJump.getTranslatedKeyMessage()}));
         } else if (var4 == 103.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.inventory", new Object[]{var6.keyInventory.getTranslatedKeyMessage()}));
         } else if (var4 == 104.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.day.6", new Object[]{var6.keyScreenshot.getTranslatedKeyMessage()}));
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
         this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, var2.getX(), var2.getY(), var2.getZ(), 0.0D, 0.0D, 0.0D);
         if (var5 == 1) {
            this.level.playSound(var2, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
         }
      } else if (var3 == ClientboundGameEventPacket.IMMEDIATE_RESPAWN) {
         this.minecraft.player.setShowDeathScreen(var4 == 0.0F);
      }

   }

   public void handleMapItemData(ClientboundMapItemDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      MapRenderer var2 = this.minecraft.gameRenderer.getMapRenderer();
      String var3 = MapItem.makeKey(var1.getMapId());
      MapItemSavedData var4 = this.minecraft.level.getMapData(var3);
      if (var4 == null) {
         var4 = new MapItemSavedData(var3);
         if (var2.getMapInstanceIfExists(var3) != null) {
            MapItemSavedData var5 = var2.getData(var2.getMapInstanceIfExists(var3));
            if (var5 != null) {
               var4 = var5;
            }
         }

         this.minecraft.level.setMapData(var4);
      }

      var1.applyToMap(var4);
      var2.update(var4);
   }

   public void handleLevelEvent(ClientboundLevelEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (var1.isGlobalEvent()) {
         this.minecraft.level.globalLevelEvent(var1.getType(), var1.getPos(), var1.getData());
      } else {
         this.minecraft.level.levelEvent(var1.getType(), var1.getPos(), var1.getData());
      }

   }

   public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.advancements.update(var1);
   }

   public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      ResourceLocation var2 = var1.getTab();
      if (var2 == null) {
         this.advancements.setSelectedTab((Advancement)null, false);
      } else {
         Advancement var3 = this.advancements.getAdvancements().get(var2);
         this.advancements.setSelectedTab(var3, false);
      }

   }

   public void handleCommands(ClientboundCommandsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.commands = new CommandDispatcher(var1.getRoot());
   }

   public void handleStopSoundEvent(ClientboundStopSoundPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.getSoundManager().stop(var1.getName(), var1.getSource());
   }

   public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.suggestionsProvider.completeCustomSuggestions(var1.getId(), var1.getSuggestions());
   }

   public void handleUpdateRecipes(ClientboundUpdateRecipesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.recipeManager.replaceRecipes(var1.getRecipes());
      MutableSearchTree var2 = this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS);
      var2.clear();
      ClientRecipeBook var3 = this.minecraft.player.getRecipeBook();
      var3.setupCollections(this.recipeManager.getRecipes());
      var3.getCollections().forEach(var2::add);
      var2.refresh();
   }

   public void handleLookAt(ClientboundPlayerLookAtPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Vec3 var2 = var1.getPosition(this.level);
      if (var2 != null) {
         this.minecraft.player.lookAt(var1.getFromAnchor(), var2);
      }

   }

   public void handleTagQueryPacket(ClientboundTagQueryPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (!this.debugQueryHandler.handleResponse(var1.getTransactionId(), var1.getTag())) {
         LOGGER.debug("Got unhandled response to tag query {}", var1.getTransactionId());
      }

   }

   public void handleAwardStats(ClientboundAwardStatsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Iterator var2 = var1.getStats().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         int var5 = (Integer)var3.getValue();
         this.minecraft.player.getStats().setValue(this.minecraft.player, var4, var5);
      }

      if (this.minecraft.screen instanceof StatsUpdateListener) {
         ((StatsUpdateListener)this.minecraft.screen).onStatsUpdated();
      }

   }

   public void handleAddOrRemoveRecipes(ClientboundRecipePacket var1) {
      ClientRecipeBook var2;
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      var2 = this.minecraft.player.getRecipeBook();
      var2.setBookSettings(var1.getBookSettings());
      ClientboundRecipePacket.State var3 = var1.getState();
      Iterator var4;
      ResourceLocation var5;
      label45:
      switch(var3) {
      case REMOVE:
         var4 = var1.getRecipes().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break label45;
            }

            var5 = (ResourceLocation)var4.next();
            this.recipeManager.byKey(var5).ifPresent(var2::remove);
         }
      case INIT:
         var4 = var1.getRecipes().iterator();

         while(var4.hasNext()) {
            var5 = (ResourceLocation)var4.next();
            this.recipeManager.byKey(var5).ifPresent(var2::add);
         }

         var4 = var1.getHighlights().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break label45;
            }

            var5 = (ResourceLocation)var4.next();
            this.recipeManager.byKey(var5).ifPresent(var2::addHighlight);
         }
      case ADD:
         var4 = var1.getRecipes().iterator();

         while(var4.hasNext()) {
            var5 = (ResourceLocation)var4.next();
            this.recipeManager.byKey(var5).ifPresent((var2x) -> {
               var2.add(var2x);
               var2.addHighlight(var2x);
               RecipeToast.addOrUpdate(this.minecraft.getToasts(), var2x);
            });
         }
      }

      var2.getCollections().forEach((var1x) -> {
         var1x.updateKnownRecipes(var2);
      });
      if (this.minecraft.screen instanceof RecipeUpdateListener) {
         ((RecipeUpdateListener)this.minecraft.screen).recipesUpdated();
      }

   }

   public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getEntityId());
      if (var2 instanceof LivingEntity) {
         MobEffect var3 = MobEffect.byId(var1.getEffectId());
         if (var3 != null) {
            MobEffectInstance var4 = new MobEffectInstance(var3, var1.getEffectDurationTicks(), var1.getEffectAmplifier(), var1.isEffectAmbient(), var1.isEffectVisible(), var1.effectShowsIcon());
            var4.setNoCounter(var1.isSuperLongDuration());
            ((LivingEntity)var2).forceAddEffect(var4);
         }
      }
   }

   public void handleUpdateTags(ClientboundUpdateTagsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      TagContainer var2 = var1.getTags();
      Multimap var3 = StaticTags.getAllMissingTags(var2);
      if (!var3.isEmpty()) {
         LOGGER.warn("Incomplete server tags, disconnecting. Missing: {}", var3);
         this.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.missing_tags"));
      } else {
         this.tags = var2;
         if (!this.connection.isMemoryConnection()) {
            var2.bindToGlobal();
         }

         this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS).refresh();
      }
   }

   public void handlePlayerCombat(ClientboundPlayerCombatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (var1.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED) {
         Entity var2 = this.level.getEntity(var1.playerId);
         if (var2 == this.minecraft.player) {
            if (this.minecraft.player.shouldShowDeathScreen()) {
               this.minecraft.setScreen(new DeathScreen(var1.message, this.level.getLevelData().isHardcore()));
            } else {
               this.minecraft.player.respawn();
            }
         }
      }

   }

   public void handleChangeDifficulty(ClientboundChangeDifficultyPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.levelData.setDifficulty(var1.getDifficulty());
      this.levelData.setDifficultyLocked(var1.isLocked());
   }

   public void handleSetCamera(ClientboundSetCameraPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         this.minecraft.setCameraEntity(var2);
      }

   }

   public void handleSetBorder(ClientboundSetBorderPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      var1.applyChanges(this.level.getWorldBorder());
   }

   public void handleSetTitles(ClientboundSetTitlesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      ClientboundSetTitlesPacket.Type var2 = var1.getType();
      Component var3 = null;
      Component var4 = null;
      Component var5 = var1.getText() != null ? var1.getText() : TextComponent.EMPTY;
      switch(var2) {
      case TITLE:
         var3 = var5;
         break;
      case SUBTITLE:
         var4 = var5;
         break;
      case ACTIONBAR:
         this.minecraft.gui.setOverlayMessage(var5, false);
         return;
      case RESET:
         this.minecraft.gui.setTitles((Component)null, (Component)null, -1, -1, -1);
         this.minecraft.gui.resetTitleTimes();
         return;
      }

      this.minecraft.gui.setTitles(var3, var4, var1.getFadeInTime(), var1.getStayTime(), var1.getFadeOutTime());
   }

   public void handleTabListCustomisation(ClientboundTabListPacket var1) {
      this.minecraft.gui.getTabList().setHeader(var1.getHeader().getString().isEmpty() ? null : var1.getHeader());
      this.minecraft.gui.getTabList().setFooter(var1.getFooter().getString().isEmpty() ? null : var1.getFooter());
   }

   public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 instanceof LivingEntity) {
         ((LivingEntity)var2).removeEffectNoUpdate(var1.getEffect());
      }

   }

   public void handlePlayerInfo(ClientboundPlayerInfoPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Iterator var2 = var1.getEntries().iterator();

      while(var2.hasNext()) {
         ClientboundPlayerInfoPacket.PlayerUpdate var3 = (ClientboundPlayerInfoPacket.PlayerUpdate)var2.next();
         if (var1.getAction() == ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER) {
            this.minecraft.getPlayerSocialManager().removePlayer(var3.getProfile().getId());
            this.playerInfoMap.remove(var3.getProfile().getId());
         } else {
            PlayerInfo var4 = (PlayerInfo)this.playerInfoMap.get(var3.getProfile().getId());
            if (var1.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
               var4 = new PlayerInfo(var3);
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

   public void handleKeepAlive(ClientboundKeepAlivePacket var1) {
      this.send((Packet)(new ServerboundKeepAlivePacket(var1.getId())));
   }

   public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      var2.getAbilities().flying = var1.isFlying();
      var2.getAbilities().instabuild = var1.canInstabuild();
      var2.getAbilities().invulnerable = var1.isInvulnerable();
      var2.getAbilities().mayfly = var1.canFly();
      var2.getAbilities().setFlyingSpeed(var1.getFlyingSpeed());
      var2.getAbilities().setWalkingSpeed(var1.getWalkingSpeed());
   }

   public void handleSoundEvent(ClientboundSoundPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.playSound(this.minecraft.player, var1.getX(), var1.getY(), var1.getZ(), var1.getSound(), var1.getSource(), var1.getVolume(), var1.getPitch());
   }

   public void handleSoundEntityEvent(ClientboundSoundEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getId());
      if (var2 != null) {
         this.minecraft.level.playSound(this.minecraft.player, var2, var1.getSound(), var1.getSource(), var1.getVolume(), var1.getPitch());
      }
   }

   public void handleCustomSoundEvent(ClientboundCustomSoundPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.getSoundManager().play(new SimpleSoundInstance(var1.getName(), var1.getSource(), var1.getVolume(), var1.getPitch(), false, 0, SoundInstance.Attenuation.LINEAR, var1.getX(), var1.getY(), var1.getZ(), false));
   }

   public void handleResourcePack(ClientboundResourcePackPacket var1) {
      String var2 = var1.getUrl();
      String var3 = var1.getHash();
      boolean var4 = var1.isRequired();
      if (this.validateResourcePackUrl(var2)) {
         if (var2.startsWith("level://")) {
            try {
               String var10 = URLDecoder.decode(var2.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File var6 = new File(this.minecraft.gameDirectory, "saves");
               File var7 = new File(var6, var10);
               if (var7.isFile()) {
                  this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                  CompletableFuture var8 = this.minecraft.getClientPackSource().setServerPack(var7, PackSource.WORLD);
                  this.downloadCallback(var8);
                  return;
               }
            } catch (UnsupportedEncodingException var9) {
            }

            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         } else {
            ServerData var5 = this.minecraft.getCurrentServer();
            if (var5 != null && var5.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
               this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
               this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3));
            } else if (var5 != null && var5.getResourcePackStatus() != ServerData.ServerPackStatus.PROMPT) {
               this.send(ServerboundResourcePackPacket.Action.DECLINED);
               if (var4) {
                  this.connection.disconnect(new TranslatableComponent("multiplayer.requiredTexturePrompt.disconnect"));
               }
            } else {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(new ConfirmScreen((var4x) -> {
                     this.minecraft.setScreen((Screen)null);
                     ServerData var5 = this.minecraft.getCurrentServer();
                     if (var4x) {
                        if (var5 != null) {
                           var5.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                        }

                        this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                        this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3));
                     } else {
                        this.send(ServerboundResourcePackPacket.Action.DECLINED);
                        if (var4) {
                           this.connection.disconnect(new TranslatableComponent("multiplayer.requiredTexturePrompt.disconnect"));
                        } else if (var5 != null) {
                           var5.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                        }
                     }

                     if (var5 != null) {
                        ServerList.saveSingleServer(var5);
                     }

                  }, var4 ? new TranslatableComponent("multiplayer.requiredTexturePrompt.line1") : new TranslatableComponent("multiplayer.texturePrompt.line1"), (Component)(var4 ? (new TranslatableComponent("multiplayer.requiredTexturePrompt.line2")).withStyle(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.BOLD}) : new TranslatableComponent("multiplayer.texturePrompt.line2")), (Component)(var4 ? new TranslatableComponent("gui.proceed") : CommonComponents.GUI_YES), (Component)(var4 ? new TranslatableComponent("menu.disconnect") : CommonComponents.GUI_NO)));
               });
            }

         }
      }
   }

   private boolean validateResourcePackUrl(String var1) {
      try {
         URI var2 = new URI(var1);
         String var3 = var2.getScheme();
         boolean var4 = "level".equals(var3);
         if (!"http".equals(var3) && !"https".equals(var3) && !var4) {
            throw new URISyntaxException(var1, "Wrong protocol");
         } else if (!var4 || !var1.contains("..") && var1.endsWith("/resources.zip")) {
            return true;
         } else {
            throw new URISyntaxException(var1, "Invalid levelstorage resourcepack path");
         }
      } catch (URISyntaxException var5) {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         return false;
      }
   }

   private void downloadCallback(CompletableFuture<?> var1) {
      var1.thenRun(() -> {
         this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED);
      }).exceptionally((var1x) -> {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         return null;
      });
   }

   private void send(ServerboundResourcePackPacket.Action var1) {
      this.connection.send(new ServerboundResourcePackPacket(var1));
   }

   public void handleBossUpdate(ClientboundBossEventPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gui.getBossOverlay().update(var1);
   }

   public void handleItemCooldown(ClientboundCooldownPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (var1.getDuration() == 0) {
         this.minecraft.player.getCooldowns().removeCooldown(var1.getItem());
      } else {
         this.minecraft.player.getCooldowns().addCooldown(var1.getItem(), var1.getDuration());
      }

   }

   public void handleMoveVehicle(ClientboundMoveVehiclePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.minecraft.player.getRootVehicle();
      if (var2 != this.minecraft.player && var2.isControlledByLocalInstance()) {
         var2.absMoveTo(var1.getX(), var1.getY(), var1.getZ(), var1.getYRot(), var1.getXRot());
         this.connection.send(new ServerboundMoveVehiclePacket(var2));
      }

   }

   public void handleOpenBook(ClientboundOpenBookPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      ItemStack var2 = this.minecraft.player.getItemInHand(var1.getHand());
      if (var2.is(Items.WRITTEN_BOOK)) {
         this.minecraft.setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(var2)));
      }

   }

   public void handleCustomPayload(ClientboundCustomPayloadPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      ResourceLocation var2 = var1.getIdentifier();
      FriendlyByteBuf var3 = null;

      try {
         var3 = var1.getData();
         if (ClientboundCustomPayloadPacket.BRAND.equals(var2)) {
            this.minecraft.player.setServerBrand(var3.readUtf(32767));
         } else {
            int var4;
            if (ClientboundCustomPayloadPacket.DEBUG_PATHFINDING_PACKET.equals(var2)) {
               var4 = var3.readInt();
               float var5 = var3.readFloat();
               Path var6 = Path.createFromStream(var3);
               this.minecraft.debugRenderer.pathfindingRenderer.addPath(var4, var6, var5);
            } else if (ClientboundCustomPayloadPacket.DEBUG_NEIGHBORSUPDATE_PACKET.equals(var2)) {
               long var34 = var3.readVarLong();
               BlockPos var39 = var3.readBlockPos();
               ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(var34, var39);
            } else {
               ArrayList var7;
               int var8;
               BlockPos var35;
               int var37;
               if (ClientboundCustomPayloadPacket.DEBUG_CAVES_PACKET.equals(var2)) {
                  var35 = var3.readBlockPos();
                  var37 = var3.readInt();
                  ArrayList var40 = Lists.newArrayList();
                  var7 = Lists.newArrayList();

                  for(var8 = 0; var8 < var37; ++var8) {
                     var40.add(var3.readBlockPos());
                     var7.add(var3.readFloat());
                  }

                  this.minecraft.debugRenderer.caveRenderer.addTunnel(var35, var40, var7);
               } else {
                  int var9;
                  int var41;
                  if (ClientboundCustomPayloadPacket.DEBUG_STRUCTURES_PACKET.equals(var2)) {
                     DimensionType var36 = (DimensionType)this.registryAccess.dimensionTypes().get(var3.readResourceLocation());
                     BoundingBox var38 = new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt());
                     var41 = var3.readInt();
                     var7 = Lists.newArrayList();
                     ArrayList var47 = Lists.newArrayList();

                     for(var9 = 0; var9 < var41; ++var9) {
                        var7.add(new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt()));
                        var47.add(var3.readBoolean());
                     }

                     this.minecraft.debugRenderer.structureRenderer.addBoundingBox(var38, var7, var47, var36);
                  } else if (ClientboundCustomPayloadPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals(var2)) {
                     ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos(var3.readBlockPos(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat());
                  } else if (ClientboundCustomPayloadPacket.DEBUG_VILLAGE_SECTIONS.equals(var2)) {
                     var4 = var3.readInt();

                     for(var37 = 0; var37 < var4; ++var37) {
                        this.minecraft.debugRenderer.villageSectionsDebugRenderer.setVillageSection(var3.readSectionPos());
                     }

                     var37 = var3.readInt();

                     for(var41 = 0; var41 < var37; ++var41) {
                        this.minecraft.debugRenderer.villageSectionsDebugRenderer.setNotVillageSection(var3.readSectionPos());
                     }
                  } else {
                     String var42;
                     if (ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET.equals(var2)) {
                        var35 = var3.readBlockPos();
                        var42 = var3.readUtf();
                        var41 = var3.readInt();
                        BrainDebugRenderer.PoiInfo var44 = new BrainDebugRenderer.PoiInfo(var35, var42, var41);
                        this.minecraft.debugRenderer.brainDebugRenderer.addPoi(var44);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET.equals(var2)) {
                        var35 = var3.readBlockPos();
                        this.minecraft.debugRenderer.brainDebugRenderer.removePoi(var35);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals(var2)) {
                        var35 = var3.readBlockPos();
                        var37 = var3.readInt();
                        this.minecraft.debugRenderer.brainDebugRenderer.setFreeTicketCount(var35, var37);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR.equals(var2)) {
                        var35 = var3.readBlockPos();
                        var37 = var3.readInt();
                        var41 = var3.readInt();
                        var7 = Lists.newArrayList();

                        for(var8 = 0; var8 < var41; ++var8) {
                           var9 = var3.readInt();
                           boolean var10 = var3.readBoolean();
                           String var11 = var3.readUtf(255);
                           var7.add(new GoalSelectorDebugRenderer.DebugGoal(var35, var9, var11, var10));
                        }

                        this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(var37, var7);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_RAIDS.equals(var2)) {
                        var4 = var3.readInt();
                        ArrayList var45 = Lists.newArrayList();

                        for(var41 = 0; var41 < var4; ++var41) {
                           var45.add(var3.readBlockPos());
                        }

                        this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(var45);
                     } else {
                        int var12;
                        int var23;
                        double var43;
                        double var48;
                        double var49;
                        PositionImpl var51;
                        UUID var54;
                        if (ClientboundCustomPayloadPacket.DEBUG_BRAIN.equals(var2)) {
                           var43 = var3.readDouble();
                           var48 = var3.readDouble();
                           var49 = var3.readDouble();
                           var51 = new PositionImpl(var43, var48, var49);
                           var54 = var3.readUUID();
                           var12 = var3.readInt();
                           String var13 = var3.readUtf();
                           String var14 = var3.readUtf();
                           int var15 = var3.readInt();
                           float var16 = var3.readFloat();
                           float var17 = var3.readFloat();
                           String var18 = var3.readUtf();
                           boolean var19 = var3.readBoolean();
                           Path var20;
                           if (var19) {
                              var20 = Path.createFromStream(var3);
                           } else {
                              var20 = null;
                           }

                           boolean var21 = var3.readBoolean();
                           BrainDebugRenderer.BrainDump var22 = new BrainDebugRenderer.BrainDump(var54, var12, var13, var14, var15, var16, var17, var51, var18, var20, var21);
                           var23 = var3.readInt();

                           int var24;
                           for(var24 = 0; var24 < var23; ++var24) {
                              String var25 = var3.readUtf();
                              var22.activities.add(var25);
                           }

                           var24 = var3.readInt();

                           int var67;
                           for(var67 = 0; var67 < var24; ++var67) {
                              String var26 = var3.readUtf();
                              var22.behaviors.add(var26);
                           }

                           var67 = var3.readInt();

                           int var68;
                           for(var68 = 0; var68 < var67; ++var68) {
                              String var27 = var3.readUtf();
                              var22.memories.add(var27);
                           }

                           var68 = var3.readInt();

                           int var69;
                           for(var69 = 0; var69 < var68; ++var69) {
                              BlockPos var28 = var3.readBlockPos();
                              var22.pois.add(var28);
                           }

                           var69 = var3.readInt();

                           int var70;
                           for(var70 = 0; var70 < var69; ++var70) {
                              BlockPos var29 = var3.readBlockPos();
                              var22.potentialPois.add(var29);
                           }

                           var70 = var3.readInt();

                           for(int var71 = 0; var71 < var70; ++var71) {
                              String var30 = var3.readUtf();
                              var22.gossips.add(var30);
                           }

                           this.minecraft.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump(var22);
                        } else if (ClientboundCustomPayloadPacket.DEBUG_BEE.equals(var2)) {
                           var43 = var3.readDouble();
                           var48 = var3.readDouble();
                           var49 = var3.readDouble();
                           var51 = new PositionImpl(var43, var48, var49);
                           var54 = var3.readUUID();
                           var12 = var3.readInt();
                           boolean var55 = var3.readBoolean();
                           BlockPos var56 = null;
                           if (var55) {
                              var56 = var3.readBlockPos();
                           }

                           boolean var57 = var3.readBoolean();
                           BlockPos var58 = null;
                           if (var57) {
                              var58 = var3.readBlockPos();
                           }

                           int var59 = var3.readInt();
                           boolean var60 = var3.readBoolean();
                           Path var61 = null;
                           if (var60) {
                              var61 = Path.createFromStream(var3);
                           }

                           BeeDebugRenderer.BeeInfo var62 = new BeeDebugRenderer.BeeInfo(var54, var12, var51, var61, var56, var58, var59);
                           int var63 = var3.readInt();

                           int var64;
                           for(var64 = 0; var64 < var63; ++var64) {
                              String var65 = var3.readUtf();
                              var62.goals.add(var65);
                           }

                           var64 = var3.readInt();

                           for(var23 = 0; var23 < var64; ++var23) {
                              BlockPos var66 = var3.readBlockPos();
                              var62.blacklistedHives.add(var66);
                           }

                           this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo(var62);
                        } else {
                           int var46;
                           if (ClientboundCustomPayloadPacket.DEBUG_HIVE.equals(var2)) {
                              var35 = var3.readBlockPos();
                              var42 = var3.readUtf();
                              var41 = var3.readInt();
                              var46 = var3.readInt();
                              boolean var52 = var3.readBoolean();
                              BeeDebugRenderer.HiveInfo var50 = new BeeDebugRenderer.HiveInfo(var35, var42, var41, var46, var52, this.level.getGameTime());
                              this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo(var50);
                           } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR.equals(var2)) {
                              this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
                           } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(var2)) {
                              var35 = var3.readBlockPos();
                              var37 = var3.readInt();
                              String var53 = var3.readUtf();
                              var46 = var3.readInt();
                              this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker(var35, var37, var53, var46);
                           } else {
                              LOGGER.warn("Unknown custom packed identifier: {}", var2);
                           }
                        }
                     }
                  }
               }
            }
         }
      } finally {
         if (var3 != null) {
            var3.release();
         }

      }

   }

   public void handleAddObjective(ClientboundSetObjectivePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
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

   public void handleSetScore(ClientboundSetScorePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
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

   public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      String var3 = var1.getObjectiveName();
      Objective var4 = var3 == null ? null : var2.getOrCreateObjective(var3);
      var2.setDisplayObjective(var1.getSlot(), var4);
   }

   public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      PlayerTeam var3;
      if (var1.getMethod() == 0) {
         var3 = var2.addPlayerTeam(var1.getName());
      } else {
         var3 = var2.getPlayerTeam(var1.getName());
      }

      if (var1.getMethod() == 0 || var1.getMethod() == 2) {
         var3.setDisplayName(var1.getDisplayName());
         var3.setColor(var1.getColor());
         var3.unpackOptions(var1.getOptions());
         Team.Visibility var4 = Team.Visibility.byName(var1.getNametagVisibility());
         if (var4 != null) {
            var3.setNameTagVisibility(var4);
         }

         Team.CollisionRule var5 = Team.CollisionRule.byName(var1.getCollisionRule());
         if (var5 != null) {
            var3.setCollisionRule(var5);
         }

         var3.setPlayerPrefix(var1.getPlayerPrefix());
         var3.setPlayerSuffix(var1.getPlayerSuffix());
      }

      Iterator var6;
      String var7;
      if (var1.getMethod() == 0 || var1.getMethod() == 3) {
         var6 = var1.getPlayers().iterator();

         while(var6.hasNext()) {
            var7 = (String)var6.next();
            var2.addPlayerToTeam(var7, var3);
         }
      }

      if (var1.getMethod() == 4) {
         var6 = var1.getPlayers().iterator();

         while(var6.hasNext()) {
            var7 = (String)var6.next();
            var2.removePlayerFromTeam(var7, var3);
         }
      }

      if (var1.getMethod() == 1) {
         var2.removePlayerTeam(var3);
      }

   }

   public void handleParticleEvent(ClientboundLevelParticlesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
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
               this.level.addParticle(var1.getParticle(), var1.isOverrideLimiter(), var1.getX() + var3, var1.getY() + var5, var1.getZ() + var7, var9, var11, var13);
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect {}", var1.getParticle());
               return;
            }
         }
      }

   }

   public void handleUpdateAttributes(ClientboundUpdateAttributesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getEntityId());
      if (var2 != null) {
         if (!(var2 instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         } else {
            AttributeMap var3 = ((LivingEntity)var2).getAttributes();
            Iterator var4 = var1.getValues().iterator();

            while(true) {
               while(var4.hasNext()) {
                  ClientboundUpdateAttributesPacket.AttributeSnapshot var5 = (ClientboundUpdateAttributesPacket.AttributeSnapshot)var4.next();
                  AttributeInstance var6 = var3.getInstance(var5.getAttribute());
                  if (var6 == null) {
                     LOGGER.warn("Entity {} does not have attribute {}", var2, Registry.ATTRIBUTE.getKey(var5.getAttribute()));
                  } else {
                     var6.setBaseValue(var5.getBase());
                     var6.removeModifiers();
                     Iterator var7 = var5.getModifiers().iterator();

                     while(var7.hasNext()) {
                        AttributeModifier var8 = (AttributeModifier)var7.next();
                        var6.addTransientModifier(var8);
                     }
                  }
               }

               return;
            }
         }
      }
   }

   public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      AbstractContainerMenu var2 = this.minecraft.player.containerMenu;
      if (var2.containerId == var1.getContainerId() && var2.isSynched(this.minecraft.player)) {
         this.recipeManager.byKey(var1.getRecipe()).ifPresent((var2x) -> {
            if (this.minecraft.screen instanceof RecipeUpdateListener) {
               RecipeBookComponent var3 = ((RecipeUpdateListener)this.minecraft.screen).getRecipeBookComponent();
               var3.setupGhostRecipe(var2x, var2.slots);
            }

         });
      }
   }

   public void handleLightUpdatePacked(ClientboundLightUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      int var2 = var1.getX();
      int var3 = var1.getZ();
      LevelLightEngine var4 = this.level.getChunkSource().getLightEngine();
      long var5 = var1.getSkyYMask();
      long var7 = var1.getEmptySkyYMask();
      Iterator var9 = var1.getSkyUpdates().iterator();
      this.readSectionList(var2, var3, var4, LightLayer.SKY, var5, var7, var9, var1.getTrustEdges());
      long var10 = var1.getBlockYMask();
      long var12 = var1.getEmptyBlockYMask();
      Iterator var14 = var1.getBlockUpdates().iterator();
      this.readSectionList(var2, var3, var4, LightLayer.BLOCK, var10, var12, var14, var1.getTrustEdges());
   }

   public void handleMerchantOffers(ClientboundMerchantOffersPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      AbstractContainerMenu var2 = this.minecraft.player.containerMenu;
      if (var1.getContainerId() == var2.containerId && var2 instanceof MerchantMenu) {
         ((MerchantMenu)var2).setOffers(new MerchantOffers(var1.getOffers().createTag()));
         ((MerchantMenu)var2).setXp(var1.getVillagerXp());
         ((MerchantMenu)var2).setMerchantLevel(var1.getVillagerLevel());
         ((MerchantMenu)var2).setShowProgressBar(var1.showProgress());
         ((MerchantMenu)var2).setCanRestock(var1.canRestock());
      }

   }

   public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.serverChunkRadius = var1.getRadius();
      this.level.getChunkSource().updateViewRadius(var1.getRadius());
   }

   public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.level.getChunkSource().updateViewCenter(var1.getX(), var1.getZ());
   }

   public void handleBlockBreakAck(ClientboundBlockBreakAckPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gameMode.handleBlockBreakAck(this.level, var1.getPos(), var1.getState(), var1.action(), var1.allGood());
   }

   private void readSectionList(int var1, int var2, LevelLightEngine var3, LightLayer var4, long var5, long var7, Iterator<byte[]> var9, boolean var10) {
      for(int var11 = 0; var11 < var3.getLightSectionCount(); ++var11) {
         int var12 = var3.getMinLightSection() + var11;
         boolean var13 = (var5 & 1L << var11) != 0L;
         boolean var14 = (var7 & 1L << var11) != 0L;
         if (var13 || var14) {
            var3.queueSectionData(var4, SectionPos.of(var1, var12, var2), var13 ? new DataLayer((byte[])((byte[])var9.next()).clone()) : new DataLayer(), var10);
            this.level.setSectionDirtyWithNeighbors(var1, var12, var2);
         }
      }

   }

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
      return (PlayerInfo)this.playerInfoMap.get(var1);
   }

   @Nullable
   public PlayerInfo getPlayerInfo(String var1) {
      Iterator var2 = this.playerInfoMap.values().iterator();

      PlayerInfo var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (PlayerInfo)var2.next();
      } while(!var3.getProfile().getName().equals(var1));

      return var3;
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

   public TagContainer getTags() {
      return this.tags;
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
