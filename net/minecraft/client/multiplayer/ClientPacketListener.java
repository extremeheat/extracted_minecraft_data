package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
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
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.VillageDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
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
import net.minecraft.network.protocol.game.ClientboundChunkBlocksUpdatePacket;
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
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
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
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Mth;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
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
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
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
   private final Connection connection;
   private final GameProfile localGameProfile;
   private final Screen callbackScreen;
   private Minecraft minecraft;
   private ClientLevel level;
   private boolean started;
   private final Map playerInfoMap = Maps.newHashMap();
   private final ClientAdvancements advancements;
   private final ClientSuggestionProvider suggestionsProvider;
   private TagManager tags = new TagManager();
   private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
   private int serverChunkRadius = 3;
   private final Random random = new Random();
   private CommandDispatcher commands = new CommandDispatcher();
   private final RecipeManager recipeManager = new RecipeManager();
   private final UUID id = UUID.randomUUID();

   public ClientPacketListener(Minecraft var1, Screen var2, Connection var3, GameProfile var4) {
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
      this.serverChunkRadius = var1.getChunkRadius();
      this.level = new ClientLevel(this, new LevelSettings(var1.getSeed(), var1.getGameType(), false, var1.isHardcore(), var1.getLevelType()), var1.getDimension(), this.serverChunkRadius, this.minecraft.getProfiler(), this.minecraft.levelRenderer);
      this.minecraft.setLevel(this.level);
      if (this.minecraft.player == null) {
         this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook(this.level.getRecipeManager()));
         this.minecraft.player.yRot = -180.0F;
         if (this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
         }
      }

      this.minecraft.debugRenderer.clear();
      this.minecraft.player.resetPos();
      int var2 = var1.getPlayerId();
      this.level.addPlayer(var2, this.minecraft.player);
      this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
      this.minecraft.cameraEntity = this.minecraft.player;
      this.minecraft.player.dimension = var1.getDimension();
      this.minecraft.setScreen(new ReceivingLevelScreen());
      this.minecraft.player.setId(var2);
      this.minecraft.player.setReducedDebugInfo(var1.isReducedDebugInfo());
      this.minecraft.player.setShowDeathScreen(var1.shouldShowDeathScreen());
      this.minecraft.gameMode.setLocalMode(var1.getGameType());
      this.minecraft.options.broadcastOptions();
      this.connection.send(new ServerboundCustomPayloadPacket(ServerboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(ClientBrandRetriever.getClientModName())));
      this.minecraft.getGame().onStartGameSession();
   }

   public void handleAddEntity(ClientboundAddEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      EntityType var9 = var1.getType();
      Object var8;
      if (var9 == EntityType.CHEST_MINECART) {
         var8 = new MinecartChest(this.level, var2, var4, var6);
      } else if (var9 == EntityType.FURNACE_MINECART) {
         var8 = new MinecartFurnace(this.level, var2, var4, var6);
      } else if (var9 == EntityType.TNT_MINECART) {
         var8 = new MinecartTNT(this.level, var2, var4, var6);
      } else if (var9 == EntityType.SPAWNER_MINECART) {
         var8 = new MinecartSpawner(this.level, var2, var4, var6);
      } else if (var9 == EntityType.HOPPER_MINECART) {
         var8 = new MinecartHopper(this.level, var2, var4, var6);
      } else if (var9 == EntityType.COMMAND_BLOCK_MINECART) {
         var8 = new MinecartCommandBlock(this.level, var2, var4, var6);
      } else if (var9 == EntityType.MINECART) {
         var8 = new Minecart(this.level, var2, var4, var6);
      } else {
         Entity var10;
         if (var9 == EntityType.FISHING_BOBBER) {
            var10 = this.level.getEntity(var1.getData());
            if (var10 instanceof Player) {
               var8 = new FishingHook(this.level, (Player)var10, var2, var4, var6);
            } else {
               var8 = null;
            }
         } else if (var9 == EntityType.ARROW) {
            var8 = new Arrow(this.level, var2, var4, var6);
            var10 = this.level.getEntity(var1.getData());
            if (var10 != null) {
               ((AbstractArrow)var8).setOwner(var10);
            }
         } else if (var9 == EntityType.SPECTRAL_ARROW) {
            var8 = new SpectralArrow(this.level, var2, var4, var6);
            var10 = this.level.getEntity(var1.getData());
            if (var10 != null) {
               ((AbstractArrow)var8).setOwner(var10);
            }
         } else if (var9 == EntityType.TRIDENT) {
            var8 = new ThrownTrident(this.level, var2, var4, var6);
            var10 = this.level.getEntity(var1.getData());
            if (var10 != null) {
               ((AbstractArrow)var8).setOwner(var10);
            }
         } else if (var9 == EntityType.SNOWBALL) {
            var8 = new Snowball(this.level, var2, var4, var6);
         } else if (var9 == EntityType.LLAMA_SPIT) {
            var8 = new LlamaSpit(this.level, var2, var4, var6, var1.getXa(), var1.getYa(), var1.getZa());
         } else if (var9 == EntityType.ITEM_FRAME) {
            var8 = new ItemFrame(this.level, new BlockPos(var2, var4, var6), Direction.from3DDataValue(var1.getData()));
         } else if (var9 == EntityType.LEASH_KNOT) {
            var8 = new LeashFenceKnotEntity(this.level, new BlockPos(var2, var4, var6));
         } else if (var9 == EntityType.ENDER_PEARL) {
            var8 = new ThrownEnderpearl(this.level, var2, var4, var6);
         } else if (var9 == EntityType.EYE_OF_ENDER) {
            var8 = new EyeOfEnder(this.level, var2, var4, var6);
         } else if (var9 == EntityType.FIREWORK_ROCKET) {
            var8 = new FireworkRocketEntity(this.level, var2, var4, var6, ItemStack.EMPTY);
         } else if (var9 == EntityType.FIREBALL) {
            var8 = new LargeFireball(this.level, var2, var4, var6, var1.getXa(), var1.getYa(), var1.getZa());
         } else if (var9 == EntityType.DRAGON_FIREBALL) {
            var8 = new DragonFireball(this.level, var2, var4, var6, var1.getXa(), var1.getYa(), var1.getZa());
         } else if (var9 == EntityType.SMALL_FIREBALL) {
            var8 = new SmallFireball(this.level, var2, var4, var6, var1.getXa(), var1.getYa(), var1.getZa());
         } else if (var9 == EntityType.WITHER_SKULL) {
            var8 = new WitherSkull(this.level, var2, var4, var6, var1.getXa(), var1.getYa(), var1.getZa());
         } else if (var9 == EntityType.SHULKER_BULLET) {
            var8 = new ShulkerBullet(this.level, var2, var4, var6, var1.getXa(), var1.getYa(), var1.getZa());
         } else if (var9 == EntityType.EGG) {
            var8 = new ThrownEgg(this.level, var2, var4, var6);
         } else if (var9 == EntityType.EVOKER_FANGS) {
            var8 = new EvokerFangs(this.level, var2, var4, var6, 0.0F, 0, (LivingEntity)null);
         } else if (var9 == EntityType.POTION) {
            var8 = new ThrownPotion(this.level, var2, var4, var6);
         } else if (var9 == EntityType.EXPERIENCE_BOTTLE) {
            var8 = new ThrownExperienceBottle(this.level, var2, var4, var6);
         } else if (var9 == EntityType.BOAT) {
            var8 = new Boat(this.level, var2, var4, var6);
         } else if (var9 == EntityType.TNT) {
            var8 = new PrimedTnt(this.level, var2, var4, var6, (LivingEntity)null);
         } else if (var9 == EntityType.ARMOR_STAND) {
            var8 = new ArmorStand(this.level, var2, var4, var6);
         } else if (var9 == EntityType.END_CRYSTAL) {
            var8 = new EndCrystal(this.level, var2, var4, var6);
         } else if (var9 == EntityType.ITEM) {
            var8 = new ItemEntity(this.level, var2, var4, var6);
         } else if (var9 == EntityType.FALLING_BLOCK) {
            var8 = new FallingBlockEntity(this.level, var2, var4, var6, Block.stateById(var1.getData()));
         } else if (var9 == EntityType.AREA_EFFECT_CLOUD) {
            var8 = new AreaEffectCloud(this.level, var2, var4, var6);
         } else {
            var8 = null;
         }
      }

      if (var8 != null) {
         int var11 = var1.getId();
         ((Entity)var8).setPacketCoordinates(var2, var4, var6);
         ((Entity)var8).xRot = (float)(var1.getxRot() * 360) / 256.0F;
         ((Entity)var8).yRot = (float)(var1.getyRot() * 360) / 256.0F;
         ((Entity)var8).setId(var11);
         ((Entity)var8).setUUID(var1.getUUID());
         this.level.putNonPlayerEntity(var11, (Entity)var8);
         if (var8 instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new MinecartSoundInstance((AbstractMinecart)var8));
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

   public void handleAddGlobalEntity(ClientboundAddGlobalEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      if (var1.getType() == 1) {
         LightningBolt var8 = new LightningBolt(this.level, var2, var4, var6, false);
         var8.setPacketCoordinates(var2, var4, var6);
         var8.yRot = 0.0F;
         var8.xRot = 0.0F;
         var8.setId(var1.getId());
         this.level.addLightning(var8);
      }

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
            if (Math.abs(var2.getX() - var3) < 0.03125D && Math.abs(var2.getY() - var5) < 0.015625D && Math.abs(var2.getZ() - var7) < 0.03125D) {
               var2.lerpTo(var2.getX(), var2.getY(), var2.getZ(), var9, var10, 3, true);
            } else {
               var2.lerpTo(var3, var5, var7, var9, var10, 3, true);
            }

            var2.onGround = var1.isOnGround();
         }

      }
   }

   public void handleSetCarriedItem(ClientboundSetCarriedItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (Inventory.isHotbarSlot(var1.getSlot())) {
         this.minecraft.player.inventory.selected = var1.getSlot();
      }

   }

   public void handleMoveEntity(ClientboundMoveEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = var1.getEntity(this.level);
      if (var2 != null) {
         if (!var2.isControlledByLocalInstance()) {
            float var4;
            if (var1.hasPosition()) {
               var2.xp += (long)var1.getXa();
               var2.yp += (long)var1.getYa();
               var2.zp += (long)var1.getZa();
               Vec3 var3 = ClientboundMoveEntityPacket.packetToEntity(var2.xp, var2.yp, var2.zp);
               var4 = var1.hasRotation() ? (float)(var1.getyRot() * 360) / 256.0F : var2.yRot;
               float var5 = var1.hasRotation() ? (float)(var1.getxRot() * 360) / 256.0F : var2.xRot;
               var2.lerpTo(var3.x, var3.y, var3.z, var4, var5, 3, false);
            } else if (var1.hasRotation()) {
               float var6 = (float)(var1.getyRot() * 360) / 256.0F;
               var4 = (float)(var1.getxRot() * 360) / 256.0F;
               var2.lerpTo(var2.getX(), var2.getY(), var2.getZ(), var6, var4, 3, false);
            }

            var2.onGround = var1.isOnGround();
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
         this.level.removeEntity(var3);
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

   public void handleChunkBlocksUpdate(ClientboundChunkBlocksUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      ClientboundChunkBlocksUpdatePacket.BlockUpdate[] var2 = var1.getUpdates();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ClientboundChunkBlocksUpdatePacket.BlockUpdate var5 = var2[var4];
         this.level.setKnownState(var5.getPos(), var5.getBlock());
      }

   }

   public void handleLevelChunk(ClientboundLevelChunkPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      int var2 = var1.getX();
      int var3 = var1.getZ();
      LevelChunk var4 = this.level.getChunkSource().replaceWithPacketData(var2, var3, var1.getBiomes(), var1.getReadBuffer(), var1.getHeightmaps(), var1.getAvailableSections());
      if (var4 != null && var1.isFullChunk()) {
         this.level.reAddEntitiesToChunk(var4);
      }

      for(int var5 = 0; var5 < 16; ++var5) {
         this.level.setSectionDirtyWithNeighbors(var2, var5, var3);
      }

      Iterator var9 = var1.getBlockEntitiesTags().iterator();

      while(var9.hasNext()) {
         CompoundTag var6 = (CompoundTag)var9.next();
         BlockPos var7 = new BlockPos(var6.getInt("x"), var6.getInt("y"), var6.getInt("z"));
         BlockEntity var8 = this.level.getBlockEntity(var7);
         if (var8 != null) {
            var8.load(var6);
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

      for(int var6 = 0; var6 < 16; ++var6) {
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
         if (this.callbackScreen instanceof RealmsScreenProxy) {
            this.minecraft.setScreen((new DisconnectedRealmsScreen(((RealmsScreenProxy)this.callbackScreen).getScreen(), "disconnect.lost", var1)).getProxy());
         } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.callbackScreen, "disconnect.lost", var1));
         }
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(new JoinMultiplayerScreen(new TitleScreen()), "disconnect.lost", var1));
      }

   }

   public void send(Packet var1) {
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

         if (var2 instanceof ItemEntity) {
            ((ItemEntity)var2).getItem().setCount(var1.getAmount());
         }

         this.minecraft.particleEngine.add(new ItemPickupParticle(this.minecraft.getEntityRenderDispatcher(), this.minecraft.renderBuffers(), this.level, var2, (Entity)var3));
         this.level.removeEntity(var1.getItemId());
      }

   }

   public void handleChat(ClientboundChatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gui.handleChat(var1.getType(), var1.getMessage());
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

            this.minecraft.getSoundManager().play((SoundInstance)var14);
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

   public void handleSetSpawn(ClientboundSetSpawnPositionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.setRespawnPosition(var1.getPos(), true, false);
      this.minecraft.level.getLevelData().setSpawn(var1.getPos());
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
                  this.minecraft.gui.setOverlayMessage(I18n.get("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage()), false);
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
         if (var5.getItem() == Items.TOTEM_OF_UNDYING) {
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
      DimensionType var2 = var1.getDimension();
      LocalPlayer var3 = this.minecraft.player;
      int var4 = var3.getId();
      if (var2 != var3.dimension) {
         this.started = false;
         Scoreboard var5 = this.level.getScoreboard();
         this.level = new ClientLevel(this, new LevelSettings(var1.getSeed(), var1.getPlayerGameType(), false, this.minecraft.level.getLevelData().isHardcore(), var1.getLevelType()), var1.getDimension(), this.serverChunkRadius, this.minecraft.getProfiler(), this.minecraft.levelRenderer);
         this.level.setScoreboard(var5);
         this.minecraft.setLevel(this.level);
         this.minecraft.setScreen(new ReceivingLevelScreen());
      }

      this.level.validateSpawn();
      this.level.removeAllPendingEntityRemovals();
      String var7 = var3.getServerBrand();
      this.minecraft.cameraEntity = null;
      LocalPlayer var6 = this.minecraft.gameMode.createPlayer(this.level, var3.getStats(), var3.getRecipeBook());
      var6.setId(var4);
      var6.dimension = var2;
      this.minecraft.player = var6;
      this.minecraft.cameraEntity = var6;
      var6.getEntityData().assignValues(var3.getEntityData().getAll());
      var6.getAttributes().assignValues(var3.getAttributes());
      var6.resetPos();
      var6.setServerBrand(var7);
      this.level.addPlayer(var4, var6);
      var6.yRot = -180.0F;
      var6.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(var6);
      var6.setReducedDebugInfo(var3.isReducedDebugInfo());
      var6.setShowDeathScreen(var3.shouldShowDeathScreen());
      if (this.minecraft.screen instanceof DeathScreen) {
         this.minecraft.setScreen((Screen)null);
      }

      this.minecraft.gameMode.setLocalMode(var1.getPlayerGameType());
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
         HorseInventoryMenu var6 = new HorseInventoryMenu(var1.getContainerId(), var3.inventory, var5, var4);
         var3.containerMenu = var6;
         this.minecraft.setScreen(new HorseInventoryScreen(var6, var3.inventory, var4));
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
            var2.inventory.setCarried(var3);
         }
      } else if (var1.getContainerId() == -2) {
         var2.inventory.setItem(var4, var3);
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
      Object var2 = this.level.getBlockEntity(var1.getPos());
      if (!(var2 instanceof SignBlockEntity)) {
         var2 = new SignBlockEntity();
         ((BlockEntity)var2).setLevelAndPosition(this.level, var1.getPos());
      }

      this.minecraft.player.openTextEdit((SignBlockEntity)var2);
   }

   public void handleBlockEntityData(ClientboundBlockEntityDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (this.minecraft.level.hasChunkAt(var1.getPos())) {
         BlockEntity var2 = this.minecraft.level.getBlockEntity(var1.getPos());
         int var3 = var1.getType();
         boolean var4 = var3 == 2 && var2 instanceof CommandBlockEntity;
         if (var3 == 1 && var2 instanceof SpawnerBlockEntity || var4 || var3 == 3 && var2 instanceof BeaconBlockEntity || var3 == 4 && var2 instanceof SkullBlockEntity || var3 == 6 && var2 instanceof BannerBlockEntity || var3 == 7 && var2 instanceof StructureBlockEntity || var3 == 8 && var2 instanceof TheEndGatewayBlockEntity || var3 == 9 && var2 instanceof SignBlockEntity || var3 == 11 && var2 instanceof BedBlockEntity || var3 == 5 && var2 instanceof ConduitBlockEntity || var3 == 12 && var2 instanceof JigsawBlockEntity || var3 == 13 && var2 instanceof CampfireBlockEntity || var3 == 14 && var2 instanceof BeehiveBlockEntity) {
            var2.load(var1.getTag());
         }

         if (var4 && this.minecraft.screen instanceof CommandBlockEditScreen) {
            ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
         }
      }

   }

   public void handleContainerSetData(ClientboundContainerSetDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      LocalPlayer var2 = this.minecraft.player;
      if (var2.containerMenu != null && var2.containerMenu.containerId == var1.getContainerId()) {
         var2.containerMenu.setData(var1.getId(), var1.getValue());
      }

   }

   public void handleSetEquippedItem(ClientboundSetEquippedItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(var1.getEntity());
      if (var2 != null) {
         var2.setItemSlot(var1.getSlot(), var1.getItem());
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
      int var3 = var1.getEvent();
      float var4 = var1.getParam();
      int var5 = Mth.floor(var4 + 0.5F);
      if (var3 >= 0 && var3 < ClientboundGameEventPacket.EVENT_LANGUAGE_ID.length && ClientboundGameEventPacket.EVENT_LANGUAGE_ID[var3] != null) {
         var2.displayClientMessage(new TranslatableComponent(ClientboundGameEventPacket.EVENT_LANGUAGE_ID[var3], new Object[0]), false);
      }

      if (var3 == 1) {
         this.level.getLevelData().setRaining(true);
         this.level.setRainLevel(0.0F);
      } else if (var3 == 2) {
         this.level.getLevelData().setRaining(false);
         this.level.setRainLevel(1.0F);
      } else if (var3 == 3) {
         this.minecraft.gameMode.setLocalMode(GameType.byId(var5));
      } else if (var3 == 4) {
         if (var5 == 0) {
            this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)));
            this.minecraft.setScreen(new ReceivingLevelScreen());
         } else if (var5 == 1) {
            this.minecraft.setScreen(new WinScreen(true, () -> {
               this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)));
            }));
         }
      } else if (var3 == 5) {
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
      } else if (var3 == 6) {
         this.level.playSound(var2, var2.getX(), var2.getEyeY(), var2.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18F, 0.45F);
      } else if (var3 == 7) {
         this.level.setRainLevel(var4);
      } else if (var3 == 8) {
         this.level.setThunderLevel(var4);
      } else if (var3 == 9) {
         this.level.playSound(var2, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0F, 1.0F);
      } else if (var3 == 10) {
         this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, var2.getX(), var2.getY(), var2.getZ(), 0.0D, 0.0D, 0.0D);
         this.level.playSound(var2, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
      } else if (var3 == 11) {
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
      var3.setupCollections();
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
      var2.setGuiOpen(var1.isGuiOpen());
      var2.setFilteringCraftable(var1.isFilteringCraftable());
      var2.setFurnaceGuiOpen(var1.isFurnaceGuiOpen());
      var2.setFurnaceFilteringCraftable(var1.isFurnaceFilteringCraftable());
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
            ((LivingEntity)var2).addEffect(var4);
         }
      }
   }

   public void handleUpdateTags(ClientboundUpdateTagsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.tags = var1.getTags();
      if (!this.connection.isMemoryConnection()) {
         BlockTags.reset(this.tags.getBlocks());
         ItemTags.reset(this.tags.getItems());
         FluidTags.reset(this.tags.getFluids());
         EntityTypeTags.reset(this.tags.getEntityTypes());
      }

      this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS).refresh();
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
      this.minecraft.level.getLevelData().setDifficulty(var1.getDifficulty());
      this.minecraft.level.getLevelData().setDifficultyLocked(var1.isLocked());
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
      String var3 = null;
      String var4 = null;
      String var5 = var1.getText() != null ? var1.getText().getColoredString() : "";
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
         this.minecraft.gui.setTitles("", "", -1, -1, -1);
         this.minecraft.gui.resetTitleTimes();
         return;
      }

      this.minecraft.gui.setTitles(var3, var4, var1.getFadeInTime(), var1.getStayTime(), var1.getFadeOutTime());
   }

   public void handleTabListCustomisation(ClientboundTabListPacket var1) {
      this.minecraft.gui.getTabList().setHeader(var1.getHeader().getColoredString().isEmpty() ? null : var1.getHeader());
      this.minecraft.gui.getTabList().setFooter(var1.getFooter().getColoredString().isEmpty() ? null : var1.getFooter());
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
            this.playerInfoMap.remove(var3.getProfile().getId());
         } else {
            PlayerInfo var4 = (PlayerInfo)this.playerInfoMap.get(var3.getProfile().getId());
            if (var1.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
               var4 = new PlayerInfo(var3);
               this.playerInfoMap.put(var4.getProfile().getId(), var4);
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
      var2.abilities.flying = var1.isFlying();
      var2.abilities.instabuild = var1.canInstabuild();
      var2.abilities.invulnerable = var1.isInvulnerable();
      var2.abilities.mayfly = var1.canFly();
      var2.abilities.setFlyingSpeed(var1.getFlyingSpeed());
      var2.abilities.setWalkingSpeed(var1.getWalkingSpeed());
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
      this.minecraft.getSoundManager().play(new SimpleSoundInstance(var1.getName(), var1.getSource(), var1.getVolume(), var1.getPitch(), false, 0, SoundInstance.Attenuation.LINEAR, (float)var1.getX(), (float)var1.getY(), (float)var1.getZ(), false));
   }

   public void handleResourcePack(ClientboundResourcePackPacket var1) {
      String var2 = var1.getUrl();
      String var3 = var1.getHash();
      if (this.validateResourcePackUrl(var2)) {
         if (var2.startsWith("level://")) {
            try {
               String var9 = URLDecoder.decode(var2.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File var5 = new File(this.minecraft.gameDirectory, "saves");
               File var6 = new File(var5, var9);
               if (var6.isFile()) {
                  this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                  CompletableFuture var7 = this.minecraft.getClientPackSource().setServerPack(var6);
                  this.downloadCallback(var7);
                  return;
               }
            } catch (UnsupportedEncodingException var8) {
            }

            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         } else {
            ServerData var4 = this.minecraft.getCurrentServer();
            if (var4 != null && var4.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
               this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
               this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3));
            } else if (var4 != null && var4.getResourcePackStatus() != ServerData.ServerPackStatus.PROMPT) {
               this.send(ServerboundResourcePackPacket.Action.DECLINED);
            } else {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(new ConfirmScreen((var3x) -> {
                     this.minecraft = Minecraft.getInstance();
                     ServerData var4 = this.minecraft.getCurrentServer();
                     if (var3x) {
                        if (var4 != null) {
                           var4.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                        }

                        this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                        this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3));
                     } else {
                        if (var4 != null) {
                           var4.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                        }

                        this.send(ServerboundResourcePackPacket.Action.DECLINED);
                     }

                     ServerList.saveSingleServer(var4);
                     this.minecraft.setScreen((Screen)null);
                  }, new TranslatableComponent("multiplayer.texturePrompt.line1", new Object[0]), new TranslatableComponent("multiplayer.texturePrompt.line2", new Object[0])));
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

   private void downloadCallback(CompletableFuture var1) {
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
      if (var2.getItem() == Items.WRITTEN_BOOK) {
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
               long var31 = var3.readVarLong();
               BlockPos var36 = var3.readBlockPos();
               ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(var31, var36);
            } else {
               ArrayList var7;
               int var8;
               BlockPos var32;
               int var34;
               if (ClientboundCustomPayloadPacket.DEBUG_CAVES_PACKET.equals(var2)) {
                  var32 = var3.readBlockPos();
                  var34 = var3.readInt();
                  ArrayList var37 = Lists.newArrayList();
                  var7 = Lists.newArrayList();

                  for(var8 = 0; var8 < var34; ++var8) {
                     var37.add(var3.readBlockPos());
                     var7.add(var3.readFloat());
                  }

                  this.minecraft.debugRenderer.caveRenderer.addTunnel(var32, var37, var7);
               } else {
                  int var9;
                  int var38;
                  if (ClientboundCustomPayloadPacket.DEBUG_STRUCTURES_PACKET.equals(var2)) {
                     DimensionType var33 = DimensionType.getById(var3.readInt());
                     BoundingBox var35 = new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt());
                     var38 = var3.readInt();
                     var7 = Lists.newArrayList();
                     ArrayList var44 = Lists.newArrayList();

                     for(var9 = 0; var9 < var38; ++var9) {
                        var7.add(new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt()));
                        var44.add(var3.readBoolean());
                     }

                     this.minecraft.debugRenderer.structureRenderer.addBoundingBox(var35, var7, var44, var33);
                  } else if (ClientboundCustomPayloadPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals(var2)) {
                     ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos(var3.readBlockPos(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat());
                  } else if (ClientboundCustomPayloadPacket.DEBUG_VILLAGE_SECTIONS.equals(var2)) {
                     var4 = var3.readInt();

                     for(var34 = 0; var34 < var4; ++var34) {
                        this.minecraft.debugRenderer.villageDebugRenderer.setVillageSection(var3.readSectionPos());
                     }

                     var34 = var3.readInt();

                     for(var38 = 0; var38 < var34; ++var38) {
                        this.minecraft.debugRenderer.villageDebugRenderer.setNotVillageSection(var3.readSectionPos());
                     }
                  } else {
                     String var39;
                     if (ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET.equals(var2)) {
                        var32 = var3.readBlockPos();
                        var39 = var3.readUtf();
                        var38 = var3.readInt();
                        VillageDebugRenderer.PoiInfo var41 = new VillageDebugRenderer.PoiInfo(var32, var39, var38);
                        this.minecraft.debugRenderer.villageDebugRenderer.addPoi(var41);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET.equals(var2)) {
                        var32 = var3.readBlockPos();
                        this.minecraft.debugRenderer.villageDebugRenderer.removePoi(var32);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals(var2)) {
                        var32 = var3.readBlockPos();
                        var34 = var3.readInt();
                        this.minecraft.debugRenderer.villageDebugRenderer.setFreeTicketCount(var32, var34);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR.equals(var2)) {
                        var32 = var3.readBlockPos();
                        var34 = var3.readInt();
                        var38 = var3.readInt();
                        var7 = Lists.newArrayList();

                        for(var8 = 0; var8 < var38; ++var8) {
                           var9 = var3.readInt();
                           boolean var10 = var3.readBoolean();
                           String var11 = var3.readUtf(255);
                           var7.add(new GoalSelectorDebugRenderer.DebugGoal(var32, var9, var11, var10));
                        }

                        this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(var34, var7);
                     } else if (ClientboundCustomPayloadPacket.DEBUG_RAIDS.equals(var2)) {
                        var4 = var3.readInt();
                        ArrayList var42 = Lists.newArrayList();

                        for(var38 = 0; var38 < var4; ++var38) {
                           var42.add(var3.readBlockPos());
                        }

                        this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(var42);
                     } else {
                        int var12;
                        int var21;
                        int var22;
                        String var23;
                        double var40;
                        double var45;
                        double var46;
                        PositionImpl var48;
                        UUID var51;
                        int var60;
                        if (ClientboundCustomPayloadPacket.DEBUG_BRAIN.equals(var2)) {
                           var40 = var3.readDouble();
                           var45 = var3.readDouble();
                           var46 = var3.readDouble();
                           var48 = new PositionImpl(var40, var45, var46);
                           var51 = var3.readUUID();
                           var12 = var3.readInt();
                           String var13 = var3.readUtf();
                           String var14 = var3.readUtf();
                           int var15 = var3.readInt();
                           String var16 = var3.readUtf();
                           boolean var17 = var3.readBoolean();
                           Path var18;
                           if (var17) {
                              var18 = Path.createFromStream(var3);
                           } else {
                              var18 = null;
                           }

                           boolean var19 = var3.readBoolean();
                           VillageDebugRenderer.BrainDump var20 = new VillageDebugRenderer.BrainDump(var51, var12, var13, var14, var15, var48, var16, var18, var19);
                           var21 = var3.readInt();

                           for(var22 = 0; var22 < var21; ++var22) {
                              var23 = var3.readUtf();
                              var20.activities.add(var23);
                           }

                           var22 = var3.readInt();

                           for(var60 = 0; var60 < var22; ++var60) {
                              String var24 = var3.readUtf();
                              var20.behaviors.add(var24);
                           }

                           var60 = var3.readInt();

                           int var61;
                           for(var61 = 0; var61 < var60; ++var61) {
                              String var25 = var3.readUtf();
                              var20.memories.add(var25);
                           }

                           var61 = var3.readInt();

                           int var62;
                           for(var62 = 0; var62 < var61; ++var62) {
                              BlockPos var26 = var3.readBlockPos();
                              var20.pois.add(var26);
                           }

                           var62 = var3.readInt();

                           for(int var64 = 0; var64 < var62; ++var64) {
                              String var27 = var3.readUtf();
                              var20.gossips.add(var27);
                           }

                           this.minecraft.debugRenderer.villageDebugRenderer.addOrUpdateBrainDump(var20);
                        } else if (ClientboundCustomPayloadPacket.DEBUG_BEE.equals(var2)) {
                           var40 = var3.readDouble();
                           var45 = var3.readDouble();
                           var46 = var3.readDouble();
                           var48 = new PositionImpl(var40, var45, var46);
                           var51 = var3.readUUID();
                           var12 = var3.readInt();
                           boolean var52 = var3.readBoolean();
                           BlockPos var53 = null;
                           if (var52) {
                              var53 = var3.readBlockPos();
                           }

                           boolean var54 = var3.readBoolean();
                           BlockPos var55 = null;
                           if (var54) {
                              var55 = var3.readBlockPos();
                           }

                           int var56 = var3.readInt();
                           boolean var57 = var3.readBoolean();
                           Path var58 = null;
                           if (var57) {
                              var58 = Path.createFromStream(var3);
                           }

                           BeeDebugRenderer.BeeInfo var59 = new BeeDebugRenderer.BeeInfo(var51, var12, var48, var58, var53, var55, var56);
                           var21 = var3.readInt();

                           for(var22 = 0; var22 < var21; ++var22) {
                              var23 = var3.readUtf();
                              var59.goals.add(var23);
                           }

                           var22 = var3.readInt();

                           for(var60 = 0; var60 < var22; ++var60) {
                              BlockPos var63 = var3.readBlockPos();
                              var59.blacklistedHives.add(var63);
                           }

                           this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo(var59);
                        } else {
                           int var43;
                           if (ClientboundCustomPayloadPacket.DEBUG_HIVE.equals(var2)) {
                              var32 = var3.readBlockPos();
                              var39 = var3.readUtf();
                              var38 = var3.readInt();
                              var43 = var3.readInt();
                              boolean var49 = var3.readBoolean();
                              BeeDebugRenderer.HiveInfo var47 = new BeeDebugRenderer.HiveInfo(var32, var39, var38, var43, var49, this.level.getGameTime());
                              this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo(var47);
                           } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR.equals(var2)) {
                              this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
                           } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(var2)) {
                              var32 = var3.readBlockPos();
                              var34 = var3.readInt();
                              String var50 = var3.readUtf();
                              var43 = var3.readInt();
                              this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker(var32, var34, var50, var43);
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
            BaseAttributeMap var3 = ((LivingEntity)var2).getAttributes();
            Iterator var4 = var1.getValues().iterator();

            while(var4.hasNext()) {
               ClientboundUpdateAttributesPacket.AttributeSnapshot var5 = (ClientboundUpdateAttributesPacket.AttributeSnapshot)var4.next();
               AttributeInstance var6 = var3.getInstance(var5.getName());
               if (var6 == null) {
                  var6 = var3.registerAttribute(new RangedAttribute((Attribute)null, var5.getName(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
               }

               var6.setBaseValue(var5.getBase());
               var6.removeModifiers();
               Iterator var7 = var5.getModifiers().iterator();

               while(var7.hasNext()) {
                  AttributeModifier var8 = (AttributeModifier)var7.next();
                  var6.addModifier(var8);
               }
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
      int var5 = var1.getSkyYMask();
      int var6 = var1.getEmptySkyYMask();
      Iterator var7 = var1.getSkyUpdates().iterator();
      this.readSectionList(var2, var3, var4, LightLayer.SKY, var5, var6, var7);
      int var8 = var1.getBlockYMask();
      int var9 = var1.getEmptyBlockYMask();
      Iterator var10 = var1.getBlockUpdates().iterator();
      this.readSectionList(var2, var3, var4, LightLayer.BLOCK, var8, var9, var10);
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

   private void readSectionList(int var1, int var2, LevelLightEngine var3, LightLayer var4, int var5, int var6, Iterator var7) {
      for(int var8 = 0; var8 < 18; ++var8) {
         int var9 = -1 + var8;
         boolean var10 = (var5 & 1 << var8) != 0;
         boolean var11 = (var6 & 1 << var8) != 0;
         if (var10 || var11) {
            var3.queueSectionData(var4, SectionPos.of(var1, var9, var2), var10 ? new DataLayer((byte[])((byte[])var7.next()).clone()) : new DataLayer());
            this.level.setSectionDirtyWithNeighbors(var1, var9, var2);
         }
      }

   }

   public Connection getConnection() {
      return this.connection;
   }

   public Collection getOnlinePlayers() {
      return this.playerInfoMap.values();
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

   public CommandDispatcher getCommands() {
      return this.commands;
   }

   public ClientLevel getLevel() {
      return this.level;
   }

   public TagManager getTags() {
      return this.tags;
   }

   public DebugQueryHandler getDebugQueryHandler() {
      return this.debugQueryHandler;
   }

   public UUID getId() {
      return this.id;
   }
}
