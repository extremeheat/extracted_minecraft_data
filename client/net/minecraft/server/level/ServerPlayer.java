package net.minecraft.server.level;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ServerItemCooldowns;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ServerPlayer extends Player {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_XZ = 32;
   private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_Y = 10;
   private static final int FLY_STAT_RECORDING_SPEED = 25;
   public static final double INTERACTION_DISTANCE_VERIFICATION_BUFFER = 1.0;
   private static final AttributeModifier CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER = new AttributeModifier(
      UUID.fromString("736565d2-e1a7-403d-a3f8-1aeb3e302542"), "Creative block interaction range modifier", 0.5, AttributeModifier.Operation.ADD_VALUE
   );
   private static final AttributeModifier CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER = new AttributeModifier(
      UUID.fromString("98491ef6-97b1-4584-ae82-71a8cc85cf73"), "Creative entity interaction range modifier", 2.0, AttributeModifier.Operation.ADD_VALUE
   );
   public ServerGamePacketListenerImpl connection;
   public final MinecraftServer server;
   public final ServerPlayerGameMode gameMode;
   private final PlayerAdvancements advancements;
   private final ServerStatsCounter stats;
   private float lastRecordedHealthAndAbsorption = 1.0E-45F;
   private int lastRecordedFoodLevel = -2147483648;
   private int lastRecordedAirLevel = -2147483648;
   private int lastRecordedArmor = -2147483648;
   private int lastRecordedLevel = -2147483648;
   private int lastRecordedExperience = -2147483648;
   private float lastSentHealth = -1.0E8F;
   private int lastSentFood = -99999999;
   private boolean lastFoodSaturationZero = true;
   private int lastSentExp = -99999999;
   private int spawnInvulnerableTime = 60;
   private ChatVisiblity chatVisibility = ChatVisiblity.FULL;
   private boolean canChatColor = true;
   private long lastActionTime = Util.getMillis();
   @Nullable
   private Entity camera;
   private boolean isChangingDimension;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook = new ServerRecipeBook();
   @Nullable
   private Vec3 levitationStartPos;
   private int levitationStartTime;
   private boolean disconnected;
   private int requestedViewDistance = 2;
   private String language = "en_us";
   @Nullable
   private Vec3 startingToFallPosition;
   @Nullable
   private Vec3 enteredNetherPosition;
   @Nullable
   private Vec3 enteredLavaOnVehiclePosition;
   private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
   private ChunkTrackingView chunkTrackingView = ChunkTrackingView.EMPTY;
   private ResourceKey<Level> respawnDimension = Level.OVERWORLD;
   @Nullable
   private BlockPos respawnPosition;
   private boolean respawnForced;
   private float respawnAngle;
   private final TextFilter textFilter;
   private boolean textFilteringEnabled;
   private boolean allowsListing;
   private boolean spawnExtraParticlesOnFall;
   private WardenSpawnTracker wardenSpawnTracker = new WardenSpawnTracker(0, 0, 0);
   @Nullable
   private BlockPos raidOmenPosition;
   private final ContainerSynchronizer containerSynchronizer = new ContainerSynchronizer() {
      @Override
      public void sendInitialData(AbstractContainerMenu var1, NonNullList<ItemStack> var2, ItemStack var3, int[] var4) {
         ServerPlayer.this.connection.send(new ClientboundContainerSetContentPacket(var1.containerId, var1.incrementStateId(), var2, var3));

         for (int var5 = 0; var5 < var4.length; var5++) {
            this.broadcastDataValue(var1, var5, var4[var5]);
         }
      }

      @Override
      public void sendSlotChange(AbstractContainerMenu var1, int var2, ItemStack var3) {
         ServerPlayer.this.connection.send(new ClientboundContainerSetSlotPacket(var1.containerId, var1.incrementStateId(), var2, var3));
      }

      @Override
      public void sendCarriedChange(AbstractContainerMenu var1, ItemStack var2) {
         ServerPlayer.this.connection.send(new ClientboundContainerSetSlotPacket(-1, var1.incrementStateId(), -1, var2));
      }

      @Override
      public void sendDataChange(AbstractContainerMenu var1, int var2, int var3) {
         this.broadcastDataValue(var1, var2, var3);
      }

      private void broadcastDataValue(AbstractContainerMenu var1, int var2, int var3) {
         ServerPlayer.this.connection.send(new ClientboundContainerSetDataPacket(var1.containerId, var2, var3));
      }
   };
   private final ContainerListener containerListener = new ContainerListener() {
      @Override
      public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
         Slot var4 = var1.getSlot(var2);
         if (!(var4 instanceof ResultSlot)) {
            if (var4.container == ServerPlayer.this.getInventory()) {
               CriteriaTriggers.INVENTORY_CHANGED.trigger(ServerPlayer.this, ServerPlayer.this.getInventory(), var3);
            }
         }
      }

      @Override
      public void dataChanged(AbstractContainerMenu var1, int var2, int var3) {
      }
   };
   @Nullable
   private RemoteChatSession chatSession;
   @Nullable
   public final Object object;
   private int containerCounter;
   public boolean wonGame;

   public ServerPlayer(MinecraftServer var1, ServerLevel var2, GameProfile var3, ClientInformation var4) {
      super(var2, var2.getSharedSpawnPos(), var2.getSharedSpawnAngle(), var3);
      this.textFilter = var1.createTextFilterForPlayer(this);
      this.gameMode = var1.createGameModeForPlayer(this);
      this.server = var1;
      this.stats = var1.getPlayerList().getPlayerStats(this);
      this.advancements = var1.getPlayerList().getPlayerAdvancements(this);
      this.fudgeSpawnLocation(var2);
      this.updateOptions(var4);
      this.object = null;
   }

   private void fudgeSpawnLocation(ServerLevel var1) {
      BlockPos var2 = var1.getSharedSpawnPos();
      if (var1.dimensionType().hasSkyLight() && var1.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
         int var3 = Math.max(0, this.server.getSpawnRadius(var1));
         int var4 = Mth.floor(var1.getWorldBorder().getDistanceToBorder((double)var2.getX(), (double)var2.getZ()));
         if (var4 < var3) {
            var3 = var4;
         }

         if (var4 <= 1) {
            var3 = 1;
         }

         long var5 = (long)(var3 * 2 + 1);
         long var7 = var5 * var5;
         int var9 = var7 > 2147483647L ? 2147483647 : (int)var7;
         int var10 = this.getCoprime(var9);
         int var11 = RandomSource.create().nextInt(var9);

         for (int var12 = 0; var12 < var9; var12++) {
            int var13 = (var11 + var10 * var12) % var9;
            int var14 = var13 % (var3 * 2 + 1);
            int var15 = var13 / (var3 * 2 + 1);
            BlockPos var16 = PlayerRespawnLogic.getOverworldRespawnPos(var1, var2.getX() + var14 - var3, var2.getZ() + var15 - var3);
            if (var16 != null) {
               this.moveTo(var16, 0.0F, 0.0F);
               if (var1.noCollision(this)) {
                  break;
               }
            }
         }
      } else {
         this.moveTo(var2, 0.0F, 0.0F);

         while (!var1.noCollision(this) && this.getY() < (double)(var1.getMaxBuildHeight() - 1)) {
            this.setPos(this.getX(), this.getY() + 1.0, this.getZ());
         }
      }
   }

   private int getCoprime(int var1) {
      return var1 <= 16 ? var1 - 1 : 17;
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("warden_spawn_tracker", 10)) {
         WardenSpawnTracker.CODEC
            .parse(new Dynamic(NbtOps.INSTANCE, var1.get("warden_spawn_tracker")))
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> this.wardenSpawnTracker = var1x);
      }

      if (var1.contains("enteredNetherPosition", 10)) {
         CompoundTag var2 = var1.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3(var2.getDouble("x"), var2.getDouble("y"), var2.getDouble("z"));
      }

      this.seenCredits = var1.getBoolean("seenCredits");
      if (var1.contains("recipeBook", 10)) {
         this.recipeBook.fromNbt(var1.getCompound("recipeBook"), this.server.getRecipeManager());
      }

      if (this.isSleeping()) {
         this.stopSleeping();
      }

      if (var1.contains("SpawnX", 99) && var1.contains("SpawnY", 99) && var1.contains("SpawnZ", 99)) {
         this.respawnPosition = new BlockPos(var1.getInt("SpawnX"), var1.getInt("SpawnY"), var1.getInt("SpawnZ"));
         this.respawnForced = var1.getBoolean("SpawnForced");
         this.respawnAngle = var1.getFloat("SpawnAngle");
         if (var1.contains("SpawnDimension")) {
            this.respawnDimension = Level.RESOURCE_KEY_CODEC
               .parse(NbtOps.INSTANCE, var1.get("SpawnDimension"))
               .resultOrPartial(LOGGER::error)
               .orElse(Level.OVERWORLD);
         }
      }

      this.spawnExtraParticlesOnFall = var1.getBoolean("spawn_extra_particles_on_fall");
      Tag var3 = var1.get("raid_omen_position");
      if (var3 != null) {
         BlockPos.CODEC.parse(NbtOps.INSTANCE, var3).resultOrPartial(LOGGER::error).ifPresent(var1x -> this.raidOmenPosition = var1x);
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      WardenSpawnTracker.CODEC
         .encodeStart(NbtOps.INSTANCE, this.wardenSpawnTracker)
         .resultOrPartial(LOGGER::error)
         .ifPresent(var1x -> var1.put("warden_spawn_tracker", var1x));
      this.storeGameTypes(var1);
      var1.putBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         CompoundTag var2 = new CompoundTag();
         var2.putDouble("x", this.enteredNetherPosition.x);
         var2.putDouble("y", this.enteredNetherPosition.y);
         var2.putDouble("z", this.enteredNetherPosition.z);
         var1.put("enteredNetherPosition", var2);
      }

      Entity var6 = this.getRootVehicle();
      Entity var3 = this.getVehicle();
      if (var3 != null && var6 != this && var6.hasExactlyOnePlayerPassenger()) {
         CompoundTag var4 = new CompoundTag();
         CompoundTag var5 = new CompoundTag();
         var6.save(var5);
         var4.putUUID("Attach", var3.getUUID());
         var4.put("Entity", var5);
         var1.put("RootVehicle", var4);
      }

      var1.put("recipeBook", this.recipeBook.toNbt());
      var1.putString("Dimension", this.level().dimension().location().toString());
      if (this.respawnPosition != null) {
         var1.putInt("SpawnX", this.respawnPosition.getX());
         var1.putInt("SpawnY", this.respawnPosition.getY());
         var1.putInt("SpawnZ", this.respawnPosition.getZ());
         var1.putBoolean("SpawnForced", this.respawnForced);
         var1.putFloat("SpawnAngle", this.respawnAngle);
         ResourceLocation.CODEC
            .encodeStart(NbtOps.INSTANCE, this.respawnDimension.location())
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> var1.put("SpawnDimension", var1x));
      }

      var1.putBoolean("spawn_extra_particles_on_fall", this.spawnExtraParticlesOnFall);
      if (this.raidOmenPosition != null) {
         BlockPos.CODEC
            .encodeStart(NbtOps.INSTANCE, this.raidOmenPosition)
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> var1.put("raid_omen_position", var1x));
      }
   }

   public void setExperiencePoints(int var1) {
      float var2 = (float)this.getXpNeededForNextLevel();
      float var3 = (var2 - 1.0F) / var2;
      this.experienceProgress = Mth.clamp((float)var1 / var2, 0.0F, var3);
      this.lastSentExp = -1;
   }

   public void setExperienceLevels(int var1) {
      this.experienceLevel = var1;
      this.lastSentExp = -1;
   }

   @Override
   public void giveExperienceLevels(int var1) {
      super.giveExperienceLevels(var1);
      this.lastSentExp = -1;
   }

   @Override
   public void onEnchantmentPerformed(ItemStack var1, int var2) {
      super.onEnchantmentPerformed(var1, var2);
      this.lastSentExp = -1;
   }

   private void initMenu(AbstractContainerMenu var1) {
      var1.addSlotListener(this.containerListener);
      var1.setSynchronizer(this.containerSynchronizer);
   }

   public void initInventoryMenu() {
      this.initMenu(this.inventoryMenu);
   }

   @Override
   public void onEnterCombat() {
      super.onEnterCombat();
      this.connection.send(ClientboundPlayerCombatEnterPacket.INSTANCE);
   }

   @Override
   public void onLeaveCombat() {
      super.onLeaveCombat();
      this.connection.send(new ClientboundPlayerCombatEndPacket(this.getCombatTracker()));
   }

   @Override
   protected void onInsideBlock(BlockState var1) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, var1);
   }

   @Override
   protected ItemCooldowns createItemCooldowns() {
      return new ServerItemCooldowns(this);
   }

   @Override
   public void tick() {
      this.gameMode.tick();
      this.wardenSpawnTracker.tick();
      this.spawnInvulnerableTime--;
      if (this.invulnerableTime > 0) {
         this.invulnerableTime--;
      }

      this.containerMenu.broadcastChanges();
      if (!this.level().isClientSide && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      Entity var1 = this.getCamera();
      if (var1 != this) {
         if (var1.isAlive()) {
            this.absMoveTo(var1.getX(), var1.getY(), var1.getZ(), var1.getYRot(), var1.getXRot());
            this.serverLevel().getChunkSource().move(this);
            if (this.wantsToStopRiding()) {
               this.setCamera(this);
            }
         } else {
            this.setCamera(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
      }

      this.trackStartFallingPosition();
      this.trackEnteredOrExitedLavaOnVehicle();
      this.updatePlayerAttributes();
      this.advancements.flushDirty(this);
   }

   private void updatePlayerAttributes() {
      AttributeInstance var1 = this.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
      if (var1 != null) {
         if (this.isCreative()) {
            var1.addOrUpdateTransientModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER);
         } else {
            var1.removeModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER);
         }
      }

      AttributeInstance var2 = this.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
      if (var2 != null) {
         if (this.isCreative()) {
            var2.addOrUpdateTransientModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER);
         } else {
            var2.removeModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER);
         }
      }
   }

   public void doTick() {
      try {
         if (!this.isSpectator() || !this.touchingUnloadedChunk()) {
            super.tick();
         }

         for (int var1 = 0; var1 < this.getInventory().getContainerSize(); var1++) {
            ItemStack var5 = this.getInventory().getItem(var1);
            if (var5.getItem().isComplex()) {
               Packet var6 = ((ComplexItem)var5.getItem()).getUpdatePacket(var5, this.level(), this);
               if (var6 != null) {
                  this.connection.send(var6);
               }
            }
         }

         if (this.getHealth() != this.lastSentHealth
            || this.lastSentFood != this.foodData.getFoodLevel()
            || this.foodData.getSaturationLevel() == 0.0F != this.lastFoodSaturationZero) {
            this.connection.send(new ClientboundSetHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
            this.lastSentHealth = this.getHealth();
            this.lastSentFood = this.foodData.getFoodLevel();
            this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
            this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
            this.updateScoreForCriteria(ObjectiveCriteria.HEALTH, Mth.ceil(this.lastRecordedHealthAndAbsorption));
         }

         if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
            this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
            this.updateScoreForCriteria(ObjectiveCriteria.FOOD, Mth.ceil((float)this.lastRecordedFoodLevel));
         }

         if (this.getAirSupply() != this.lastRecordedAirLevel) {
            this.lastRecordedAirLevel = this.getAirSupply();
            this.updateScoreForCriteria(ObjectiveCriteria.AIR, Mth.ceil((float)this.lastRecordedAirLevel));
         }

         if (this.getArmorValue() != this.lastRecordedArmor) {
            this.lastRecordedArmor = this.getArmorValue();
            this.updateScoreForCriteria(ObjectiveCriteria.ARMOR, Mth.ceil((float)this.lastRecordedArmor));
         }

         if (this.totalExperience != this.lastRecordedExperience) {
            this.lastRecordedExperience = this.totalExperience;
            this.updateScoreForCriteria(ObjectiveCriteria.EXPERIENCE, Mth.ceil((float)this.lastRecordedExperience));
         }

         if (this.experienceLevel != this.lastRecordedLevel) {
            this.lastRecordedLevel = this.experienceLevel;
            this.updateScoreForCriteria(ObjectiveCriteria.LEVEL, Mth.ceil((float)this.lastRecordedLevel));
         }

         if (this.totalExperience != this.lastSentExp) {
            this.lastSentExp = this.totalExperience;
            this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
         }

         if (this.tickCount % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }
      } catch (Throwable var4) {
         CrashReport var2 = CrashReport.forThrowable(var4, "Ticking player");
         CrashReportCategory var3 = var2.addCategory("Player being ticked");
         this.fillCrashReportCategory(var3);
         throw new ReportedException(var2);
      }
   }

   @Override
   public void resetFallDistance() {
      if (this.getHealth() > 0.0F && this.startingToFallPosition != null) {
         CriteriaTriggers.FALL_FROM_HEIGHT.trigger(this, this.startingToFallPosition);
      }

      this.startingToFallPosition = null;
      super.resetFallDistance();
   }

   public void trackStartFallingPosition() {
      if (this.fallDistance > 0.0F && this.startingToFallPosition == null) {
         this.startingToFallPosition = this.position();
         if (this.currentImpulseImpactPos != null) {
            CriteriaTriggers.FALL_AFTER_EXPLOSION.trigger(this, this.currentImpulseImpactPos, this.currentExplosionCause);
         }
      }
   }

   public void trackEnteredOrExitedLavaOnVehicle() {
      if (this.getVehicle() != null && this.getVehicle().isInLava()) {
         if (this.enteredLavaOnVehiclePosition == null) {
            this.enteredLavaOnVehiclePosition = this.position();
         } else {
            CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.trigger(this, this.enteredLavaOnVehiclePosition);
         }
      }

      if (this.enteredLavaOnVehiclePosition != null && (this.getVehicle() == null || !this.getVehicle().isInLava())) {
         this.enteredLavaOnVehiclePosition = null;
      }
   }

   private void updateScoreForCriteria(ObjectiveCriteria var1, int var2) {
      this.getScoreboard().forAllObjectives(var1, this, var1x -> var1x.set(var2));
   }

   @Override
   public void die(DamageSource var1) {
      this.gameEvent(GameEvent.ENTITY_DIE);
      boolean var2 = this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
      if (var2) {
         Component var3 = this.getCombatTracker().getDeathMessage();
         this.connection
            .send(
               new ClientboundPlayerCombatKillPacket(this.getId(), var3),
               PacketSendListener.exceptionallySend(
                  () -> {
                     short var2x = 256;
                     String var3x = var3.getString(256);
                     MutableComponent var4x = Component.translatable("death.attack.message_too_long", Component.literal(var3x).withStyle(ChatFormatting.YELLOW));
                     MutableComponent var5x = Component.translatable("death.attack.even_more_magic", this.getDisplayName())
                        .withStyle(var1xx -> var1xx.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var4x)));
                     return new ClientboundPlayerCombatKillPacket(this.getId(), var5x);
                  }
               )
            );
         PlayerTeam var4 = this.getTeam();
         if (var4 == null || var4.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
            this.server.getPlayerList().broadcastSystemMessage(var3, false);
         } else if (var4.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
            this.server.getPlayerList().broadcastSystemToTeam(this, var3);
         } else if (var4.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
            this.server.getPlayerList().broadcastSystemToAllExceptTeam(this, var3);
         }
      } else {
         this.connection.send(new ClientboundPlayerCombatKillPacket(this.getId(), CommonComponents.EMPTY));
      }

      this.removeEntitiesOnShoulder();
      if (this.level().getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         this.tellNeutralMobsThatIDied();
      }

      if (!this.isSpectator()) {
         this.dropAllDeathLoot(var1);
      }

      this.getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this, ScoreAccess::increment);
      LivingEntity var5 = this.getKillCredit();
      if (var5 != null) {
         this.awardStat(Stats.ENTITY_KILLED_BY.get(var5.getType()));
         var5.awardKillScore(this, this.deathScore, var1);
         this.createWitherRose(var5);
      }

      this.level().broadcastEntityEvent(this, (byte)3);
      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setTicksFrozen(0);
      this.setSharedFlagOnFire(false);
      this.getCombatTracker().recheckStatus();
      this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level().dimension(), this.blockPosition())));
   }

   private void tellNeutralMobsThatIDied() {
      AABB var1 = new AABB(this.blockPosition()).inflate(32.0, 10.0, 32.0);
      this.level()
         .getEntitiesOfClass(Mob.class, var1, EntitySelector.NO_SPECTATORS)
         .stream()
         .filter(var0 -> var0 instanceof NeutralMob)
         .forEach(var1x -> ((NeutralMob)var1x).playerDied(this));
   }

   @Override
   public void awardKillScore(Entity var1, int var2, DamageSource var3) {
      if (var1 != this) {
         super.awardKillScore(var1, var2, var3);
         this.increaseScore(var2);
         this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_ALL, this, ScoreAccess::increment);
         if (var1 instanceof Player) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, this, ScoreAccess::increment);
         } else {
            this.awardStat(Stats.MOB_KILLS);
         }

         this.handleTeamKill(this, var1, ObjectiveCriteria.TEAM_KILL);
         this.handleTeamKill(var1, this, ObjectiveCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, var1, var3);
      }
   }

   private void handleTeamKill(ScoreHolder var1, ScoreHolder var2, ObjectiveCriteria[] var3) {
      PlayerTeam var4 = this.getScoreboard().getPlayersTeam(var2.getScoreboardName());
      if (var4 != null) {
         int var5 = var4.getColor().getId();
         if (var5 >= 0 && var5 < var3.length) {
            this.getScoreboard().forAllObjectives(var3[var5], var1, ScoreAccess::increment);
         }
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         boolean var3 = this.server.isDedicatedServer() && this.isPvpAllowed() && var1.is(DamageTypeTags.IS_FALL);
         if (!var3 && this.spawnInvulnerableTime > 0 && !var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
         } else {
            Entity var4 = var1.getEntity();
            if (var4 instanceof Player var5 && !this.canHarmPlayer(var5)) {
               return false;
            }

            if (var4 instanceof AbstractArrow var8 && var8.getOwner() instanceof Player var7 && !this.canHarmPlayer(var7)) {
               return false;
            }

            return super.hurt(var1, var2);
         }
      }
   }

   @Override
   public boolean canHarmPlayer(Player var1) {
      return !this.isPvpAllowed() ? false : super.canHarmPlayer(var1);
   }

   private boolean isPvpAllowed() {
      return this.server.isPvpAllowed();
   }

   @Nullable
   @Override
   protected PortalInfo findDimensionEntryPoint(ServerLevel var1) {
      PortalInfo var2 = super.findDimensionEntryPoint(var1);
      if (var2 != null && this.level().dimension() == Level.OVERWORLD && var1.dimension() == Level.END) {
         Vec3 var3 = var2.pos.add(0.0, -1.0, 0.0);
         return new PortalInfo(var3, Vec3.ZERO, 90.0F, 0.0F);
      } else {
         return var2;
      }
   }

   @Nullable
   @Override
   public Entity changeDimension(ServerLevel var1) {
      this.isChangingDimension = true;
      ServerLevel var2 = this.serverLevel();
      ResourceKey var3 = var2.dimension();
      if (var3 == Level.END && var1.dimension() == Level.OVERWORLD) {
         this.unRide();
         this.serverLevel().removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
         if (!this.wonGame) {
            this.wonGame = true;
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, this.seenCredits ? 0.0F : 1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         LevelData var4 = var1.getLevelData();
         this.connection.send(new ClientboundRespawnPacket(this.createCommonSpawnInfo(var1), (byte)3));
         this.connection.send(new ClientboundChangeDifficultyPacket(var4.getDifficulty(), var4.isDifficultyLocked()));
         PlayerList var5 = this.server.getPlayerList();
         var5.sendPlayerPermissionLevel(this);
         var2.removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
         this.unsetRemoved();
         PortalInfo var6 = this.findDimensionEntryPoint(var1);
         if (var6 != null) {
            var2.getProfiler().push("moving");
            if (var3 == Level.OVERWORLD && var1.dimension() == Level.NETHER) {
               this.enteredNetherPosition = this.position();
            } else if (var1.dimension() == Level.END) {
               this.createEndPlatform(var1, BlockPos.containing(var6.pos));
            }

            var2.getProfiler().pop();
            var2.getProfiler().push("placing");
            this.setServerLevel(var1);
            this.connection.teleport(var6.pos.x, var6.pos.y, var6.pos.z, var6.yRot, var6.xRot);
            this.connection.resetPosition();
            var1.addDuringPortalTeleport(this);
            var2.getProfiler().pop();
            this.triggerDimensionChangeTriggers(var2);
            this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
            var5.sendLevelInfo(this, var1);
            var5.sendAllPlayerInfo(this);

            for (MobEffectInstance var8 : this.getActiveEffects()) {
               this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), var8, false));
            }

            this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
            this.lastSentExp = -1;
            this.lastSentHealth = -1.0F;
            this.lastSentFood = -1;
         }

         return this;
      }
   }

   private void createEndPlatform(ServerLevel var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      for (int var4 = -2; var4 <= 2; var4++) {
         for (int var5 = -2; var5 <= 2; var5++) {
            for (int var6 = -1; var6 < 3; var6++) {
               BlockState var7 = var6 == -1 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
               var1.setBlockAndUpdate(var3.set(var2).move(var5, var6, var4), var7);
            }
         }
      }
   }

   @Override
   protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel var1, BlockPos var2, boolean var3, WorldBorder var4) {
      Optional var5 = super.getExitPortal(var1, var2, var3, var4);
      if (var5.isPresent()) {
         return var5;
      } else {
         Direction.Axis var6 = this.level().getBlockState(this.portalEntrancePos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
         Optional var7 = var1.getPortalForcer().createPortal(var2, var6);
         if (var7.isEmpty()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
         }

         return var7;
      }
   }

   private void triggerDimensionChangeTriggers(ServerLevel var1) {
      ResourceKey var2 = var1.dimension();
      ResourceKey var3 = this.level().dimension();
      CriteriaTriggers.CHANGED_DIMENSION.trigger(this, var2, var3);
      if (var2 == Level.NETHER && var3 == Level.OVERWORLD && this.enteredNetherPosition != null) {
         CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
      }

      if (var3 != Level.NETHER) {
         this.enteredNetherPosition = null;
      }
   }

   @Override
   public boolean broadcastToPlayer(ServerPlayer var1) {
      if (var1.isSpectator()) {
         return this.getCamera() == this;
      } else {
         return this.isSpectator() ? false : super.broadcastToPlayer(var1);
      }
   }

   @Override
   public void take(Entity var1, int var2) {
      super.take(var1, var2);
      this.containerMenu.broadcastChanges();
   }

   @Override
   public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos var1) {
      Direction var2 = this.level().getBlockState(var1).getValue(HorizontalDirectionalBlock.FACING);
      if (this.isSleeping() || !this.isAlive()) {
         return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
      } else if (!this.level().dimensionType().natural()) {
         return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
      } else if (!this.bedInRange(var1, var2)) {
         return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
      } else if (this.bedBlocked(var1, var2)) {
         return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
      } else {
         this.setRespawnPosition(this.level().dimension(), var1, this.getYRot(), false, true);
         if (this.level().isDay()) {
            return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
         } else {
            if (!this.isCreative()) {
               double var3 = 8.0;
               double var5 = 5.0;
               Vec3 var7 = Vec3.atBottomCenterOf(var1);
               List var8 = this.level()
                  .getEntitiesOfClass(
                     Monster.class,
                     new AABB(var7.x() - 8.0, var7.y() - 5.0, var7.z() - 8.0, var7.x() + 8.0, var7.y() + 5.0, var7.z() + 8.0),
                     var1x -> var1x.isPreventingPlayerRest(this)
                  );
               if (!var8.isEmpty()) {
                  return Either.left(Player.BedSleepingProblem.NOT_SAFE);
               }
            }

            Either var9 = super.startSleepInBed(var1).ifRight(var1x -> {
               this.awardStat(Stats.SLEEP_IN_BED);
               CriteriaTriggers.SLEPT_IN_BED.trigger(this);
            });
            if (!this.serverLevel().canSleepThroughNights()) {
               this.displayClientMessage(Component.translatable("sleep.not_possible"), true);
            }

            ((ServerLevel)this.level()).updateSleepingPlayerList();
            return var9;
         }
      }
   }

   @Override
   public void startSleeping(BlockPos var1) {
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      super.startSleeping(var1);
   }

   private boolean bedInRange(BlockPos var1, Direction var2) {
      return this.isReachableBedBlock(var1) || this.isReachableBedBlock(var1.relative(var2.getOpposite()));
   }

   private boolean isReachableBedBlock(BlockPos var1) {
      Vec3 var2 = Vec3.atBottomCenterOf(var1);
      return Math.abs(this.getX() - var2.x()) <= 3.0 && Math.abs(this.getY() - var2.y()) <= 2.0 && Math.abs(this.getZ() - var2.z()) <= 3.0;
   }

   private boolean bedBlocked(BlockPos var1, Direction var2) {
      BlockPos var3 = var1.above();
      return !this.freeAt(var3) || !this.freeAt(var3.relative(var2.getOpposite()));
   }

   @Override
   public void stopSleepInBed(boolean var1, boolean var2) {
      if (this.isSleeping()) {
         this.serverLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, 2));
      }

      super.stopSleepInBed(var1, var2);
      if (this.connection != null) {
         this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
      }
   }

   @Override
   public void dismountTo(double var1, double var3, double var5) {
      this.removeVehicle();
      this.setPos(var1, var3, var5);
   }

   @Override
   public boolean isInvulnerableTo(DamageSource var1) {
      return super.isInvulnerableTo(var1) || this.isChangingDimension();
   }

   @Override
   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   @Override
   protected void onChangedBlock(BlockPos var1) {
      if (!this.isSpectator()) {
         super.onChangedBlock(var1);
      }
   }

   public void doCheckFallDamage(double var1, double var3, double var5, boolean var7) {
      if (!this.touchingUnloadedChunk()) {
         this.checkSupportingBlock(var7, new Vec3(var1, var3, var5));
         BlockPos var8 = this.getOnPosLegacy();
         BlockState var9 = this.level().getBlockState(var8);
         if (this.spawnExtraParticlesOnFall && var7 && this.fallDistance > 0.0F) {
            Vec3 var10 = var8.getCenter().add(0.0, 0.5, 0.0);
            int var11 = (int)(50.0F * this.fallDistance);
            this.serverLevel()
               .sendParticles(
                  new BlockParticleOption(ParticleTypes.BLOCK, var9),
                  var10.x,
                  var10.y,
                  var10.z,
                  var11,
                  0.30000001192092896,
                  0.30000001192092896,
                  0.30000001192092896,
                  0.15000000596046448
               );
            this.spawnExtraParticlesOnFall = false;
         }

         super.checkFallDamage(var3, var7, var9, var8);
      }
   }

   @Override
   public void onExplosionHit(@Nullable Entity var1) {
      super.onExplosionHit(var1);
      this.currentImpulseImpactPos = this.position();
      this.currentExplosionCause = var1;
      this.ignoreFallDamageFromCurrentImpulse = var1 != null && var1.getType() == EntityType.WIND_CHARGE;
   }

   @Override
   protected void pushEntities() {
      if (this.level().tickRateManager().runsNormally()) {
         super.pushEntities();
      }
   }

   @Override
   public void openTextEdit(SignBlockEntity var1, boolean var2) {
      this.connection.send(new ClientboundBlockUpdatePacket(this.level(), var1.getBlockPos()));
      this.connection.send(new ClientboundOpenSignEditorPacket(var1.getBlockPos(), var2));
   }

   private void nextContainerCounter() {
      this.containerCounter = this.containerCounter % 100 + 1;
   }

   @Override
   public OptionalInt openMenu(@Nullable MenuProvider var1) {
      if (var1 == null) {
         return OptionalInt.empty();
      } else {
         if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
         }

         this.nextContainerCounter();
         AbstractContainerMenu var2 = var1.createMenu(this.containerCounter, this.getInventory(), this);
         if (var2 == null) {
            if (this.isSpectator()) {
               this.displayClientMessage(Component.translatable("container.spectatorCantOpen").withStyle(ChatFormatting.RED), true);
            }

            return OptionalInt.empty();
         } else {
            this.connection.send(new ClientboundOpenScreenPacket(var2.containerId, var2.getType(), var1.getDisplayName()));
            this.initMenu(var2);
            this.containerMenu = var2;
            return OptionalInt.of(this.containerCounter);
         }
      }
   }

   @Override
   public void sendMerchantOffers(int var1, MerchantOffers var2, int var3, int var4, boolean var5, boolean var6) {
      this.connection.send(new ClientboundMerchantOffersPacket(var1, var2, var3, var4, var5, var6));
   }

   @Override
   public void openHorseInventory(AbstractHorse var1, Container var2) {
      if (this.containerMenu != this.inventoryMenu) {
         this.closeContainer();
      }

      this.nextContainerCounter();
      this.connection.send(new ClientboundHorseScreenOpenPacket(this.containerCounter, var2.getContainerSize(), var1.getId()));
      this.containerMenu = new HorseInventoryMenu(this.containerCounter, this.getInventory(), var2, var1);
      this.initMenu(this.containerMenu);
   }

   @Override
   public void openItemGui(ItemStack var1, InteractionHand var2) {
      if (var1.is(Items.WRITTEN_BOOK)) {
         if (WrittenBookItem.resolveBookComponents(var1, this.createCommandSourceStack(), this)) {
            this.containerMenu.broadcastChanges();
         }

         this.connection.send(new ClientboundOpenBookPacket(var2));
      }
   }

   @Override
   public void openCommandBlock(CommandBlockEntity var1) {
      this.connection.send(ClientboundBlockEntityDataPacket.create(var1, BlockEntity::saveCustomOnly));
   }

   @Override
   public void closeContainer() {
      this.connection.send(new ClientboundContainerClosePacket(this.containerMenu.containerId));
      this.doCloseContainer();
   }

   @Override
   public void doCloseContainer() {
      this.containerMenu.removed(this);
      this.inventoryMenu.transferState(this.containerMenu);
      this.containerMenu = this.inventoryMenu;
   }

   public void setPlayerInput(float var1, float var2, boolean var3, boolean var4) {
      if (this.isPassenger()) {
         if (var1 >= -1.0F && var1 <= 1.0F) {
            this.xxa = var1;
         }

         if (var2 >= -1.0F && var2 <= 1.0F) {
            this.zza = var2;
         }

         this.jumping = var3;
         this.setShiftKeyDown(var4);
      }
   }

   @Override
   public void travel(Vec3 var1) {
      double var2 = this.getX();
      double var4 = this.getY();
      double var6 = this.getZ();
      super.travel(var1);
      this.checkMovementStatistics(this.getX() - var2, this.getY() - var4, this.getZ() - var6);
   }

   @Override
   public void rideTick() {
      double var1 = this.getX();
      double var3 = this.getY();
      double var5 = this.getZ();
      super.rideTick();
      this.checkRidingStatistics(this.getX() - var1, this.getY() - var3, this.getZ() - var5);
   }

   public void checkMovementStatistics(double var1, double var3, double var5) {
      if (!this.isPassenger() && !didNotMove(var1, var3, var5)) {
         if (this.isSwimming()) {
            int var7 = Math.round((float)Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.awardStat(Stats.SWIM_ONE_CM, var7);
               this.causeFoodExhaustion(0.01F * (float)var7 * 0.01F);
            }
         } else if (this.isEyeInFluid(FluidTags.WATER)) {
            int var8 = Math.round((float)Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var8 > 0) {
               this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, var8);
               this.causeFoodExhaustion(0.01F * (float)var8 * 0.01F);
            }
         } else if (this.isInWater()) {
            int var9 = Math.round((float)Math.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if (var9 > 0) {
               this.awardStat(Stats.WALK_ON_WATER_ONE_CM, var9);
               this.causeFoodExhaustion(0.01F * (float)var9 * 0.01F);
            }
         } else if (this.onClimbable()) {
            if (var3 > 0.0) {
               this.awardStat(Stats.CLIMB_ONE_CM, (int)Math.round(var3 * 100.0));
            }
         } else if (this.onGround()) {
            int var10 = Math.round((float)Math.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if (var10 > 0) {
               if (this.isSprinting()) {
                  this.awardStat(Stats.SPRINT_ONE_CM, var10);
                  this.causeFoodExhaustion(0.1F * (float)var10 * 0.01F);
               } else if (this.isCrouching()) {
                  this.awardStat(Stats.CROUCH_ONE_CM, var10);
                  this.causeFoodExhaustion(0.0F * (float)var10 * 0.01F);
               } else {
                  this.awardStat(Stats.WALK_ONE_CM, var10);
                  this.causeFoodExhaustion(0.0F * (float)var10 * 0.01F);
               }
            }
         } else if (this.isFallFlying()) {
            int var11 = Math.round((float)Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            this.awardStat(Stats.AVIATE_ONE_CM, var11);
         } else {
            int var12 = Math.round((float)Math.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if (var12 > 25) {
               this.awardStat(Stats.FLY_ONE_CM, var12);
            }
         }
      }
   }

   private void checkRidingStatistics(double var1, double var3, double var5) {
      if (this.isPassenger() && !didNotMove(var1, var3, var5)) {
         int var7 = Math.round((float)Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
         Entity var8 = this.getVehicle();
         if (var8 instanceof AbstractMinecart) {
            this.awardStat(Stats.MINECART_ONE_CM, var7);
         } else if (var8 instanceof Boat) {
            this.awardStat(Stats.BOAT_ONE_CM, var7);
         } else if (var8 instanceof Pig) {
            this.awardStat(Stats.PIG_ONE_CM, var7);
         } else if (var8 instanceof AbstractHorse) {
            this.awardStat(Stats.HORSE_ONE_CM, var7);
         } else if (var8 instanceof Strider) {
            this.awardStat(Stats.STRIDER_ONE_CM, var7);
         }
      }
   }

   private static boolean didNotMove(double var0, double var2, double var4) {
      return var0 == 0.0 && var2 == 0.0 && var4 == 0.0;
   }

   @Override
   public void awardStat(Stat<?> var1, int var2) {
      this.stats.increment(this, var1, var2);
      this.getScoreboard().forAllObjectives(var1, this, var1x -> var1x.add(var2));
   }

   @Override
   public void resetStat(Stat<?> var1) {
      this.stats.setValue(this, var1, 0);
      this.getScoreboard().forAllObjectives(var1, this, ScoreAccess::reset);
   }

   @Override
   public int awardRecipes(Collection<RecipeHolder<?>> var1) {
      return this.recipeBook.addRecipes(var1, this);
   }

   @Override
   public void triggerRecipeCrafted(RecipeHolder<?> var1, List<ItemStack> var2) {
      CriteriaTriggers.RECIPE_CRAFTED.trigger(this, var1.id(), var2);
   }

   @Override
   public void awardRecipesByKey(List<ResourceLocation> var1) {
      List var2 = var1.stream().flatMap(var1x -> this.server.getRecipeManager().byKey(var1x).stream()).collect(Collectors.toList());
      this.awardRecipes(var2);
   }

   @Override
   public int resetRecipes(Collection<RecipeHolder<?>> var1) {
      return this.recipeBook.removeRecipes(var1, this);
   }

   @Override
   public void giveExperiencePoints(int var1) {
      super.giveExperiencePoints(var1);
      this.lastSentExp = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.ejectPassengers();
      if (this.isSleeping()) {
         this.stopSleepInBed(true, false);
      }
   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   public void resetSentInfo() {
      this.lastSentHealth = -1.0E8F;
   }

   @Override
   public void displayClientMessage(Component var1, boolean var2) {
      this.sendSystemMessage(var1, var2);
   }

   @Override
   protected void completeUsingItem() {
      if (!this.useItem.isEmpty() && this.isUsingItem()) {
         this.connection.send(new ClientboundEntityEventPacket(this, (byte)9));
         super.completeUsingItem();
      }
   }

   @Override
   public void lookAt(EntityAnchorArgument.Anchor var1, Vec3 var2) {
      super.lookAt(var1, var2);
      this.connection.send(new ClientboundPlayerLookAtPacket(var1, var2.x, var2.y, var2.z));
   }

   public void lookAt(EntityAnchorArgument.Anchor var1, Entity var2, EntityAnchorArgument.Anchor var3) {
      Vec3 var4 = var3.apply(var2);
      super.lookAt(var1, var4);
      this.connection.send(new ClientboundPlayerLookAtPacket(var1, var2, var3));
   }

   public void restoreFrom(ServerPlayer var1, boolean var2) {
      this.wardenSpawnTracker = var1.wardenSpawnTracker;
      this.chatSession = var1.chatSession;
      this.gameMode.setGameModeForPlayer(var1.gameMode.getGameModeForPlayer(), var1.gameMode.getPreviousGameModeForPlayer());
      this.onUpdateAbilities();
      if (var2) {
         this.getInventory().replaceWith(var1.getInventory());
         this.setHealth(var1.getHealth());
         this.foodData = var1.foodData;
         this.experienceLevel = var1.experienceLevel;
         this.totalExperience = var1.totalExperience;
         this.experienceProgress = var1.experienceProgress;
         this.setScore(var1.getScore());
         this.portalEntrancePos = var1.portalEntrancePos;
      } else if (this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || var1.isSpectator()) {
         this.getInventory().replaceWith(var1.getInventory());
         this.experienceLevel = var1.experienceLevel;
         this.totalExperience = var1.totalExperience;
         this.experienceProgress = var1.experienceProgress;
         this.setScore(var1.getScore());
      }

      this.enchantmentSeed = var1.enchantmentSeed;
      this.enderChestInventory = var1.enderChestInventory;
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, var1.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
      this.lastSentExp = -1;
      this.lastSentHealth = -1.0F;
      this.lastSentFood = -1;
      this.recipeBook.copyOverData(var1.recipeBook);
      this.seenCredits = var1.seenCredits;
      this.enteredNetherPosition = var1.enteredNetherPosition;
      this.chunkTrackingView = var1.chunkTrackingView;
      this.setShoulderEntityLeft(var1.getShoulderEntityLeft());
      this.setShoulderEntityRight(var1.getShoulderEntityRight());
      this.setLastDeathLocation(var1.getLastDeathLocation());
   }

   @Override
   protected void onEffectAdded(MobEffectInstance var1, @Nullable Entity var2) {
      super.onEffectAdded(var1, var2);
      this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), var1, true));
      if (var1.is(MobEffects.LEVITATION)) {
         this.levitationStartTime = this.tickCount;
         this.levitationStartPos = this.position();
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this, var2);
   }

   @Override
   protected void onEffectUpdated(MobEffectInstance var1, boolean var2, @Nullable Entity var3) {
      super.onEffectUpdated(var1, var2, var3);
      this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), var1, false));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this, var3);
   }

   @Override
   protected void onEffectRemoved(MobEffectInstance var1) {
      super.onEffectRemoved(var1);
      this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), var1.getEffect()));
      if (var1.is(MobEffects.LEVITATION)) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this, null);
   }

   @Override
   public void teleportTo(double var1, double var3, double var5) {
      this.connection.teleport(var1, var3, var5, this.getYRot(), this.getXRot(), RelativeMovement.ROTATION);
   }

   @Override
   public void teleportRelative(double var1, double var3, double var5) {
      this.connection.teleport(this.getX() + var1, this.getY() + var3, this.getZ() + var5, this.getYRot(), this.getXRot(), RelativeMovement.ALL);
   }

   @Override
   public boolean teleportTo(ServerLevel var1, double var2, double var4, double var6, Set<RelativeMovement> var8, float var9, float var10) {
      ChunkPos var11 = new ChunkPos(BlockPos.containing(var2, var4, var6));
      var1.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, var11, 1, this.getId());
      this.stopRiding();
      if (this.isSleeping()) {
         this.stopSleepInBed(true, true);
      }

      if (var1 == this.level()) {
         this.connection.teleport(var2, var4, var6, var9, var10, var8);
      } else {
         this.teleportTo(var1, var2, var4, var6, var9, var10);
      }

      this.setYHeadRot(var9);
      return true;
   }

   @Override
   public void moveTo(double var1, double var3, double var5) {
      super.moveTo(var1, var3, var5);
      this.connection.resetPosition();
   }

   @Override
   public void crit(Entity var1) {
      this.serverLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(var1, 4));
   }

   @Override
   public void magicCrit(Entity var1) {
      this.serverLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(var1, 5));
   }

   @Override
   public void onUpdateAbilities() {
      if (this.connection != null) {
         this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
         this.updateInvisibilityStatus();
      }
   }

   public ServerLevel serverLevel() {
      return (ServerLevel)this.level();
   }

   public boolean setGameMode(GameType var1) {
      if (!this.gameMode.changeGameModeForPlayer(var1)) {
         return false;
      } else {
         this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, (float)var1.getId()));
         if (var1 == GameType.SPECTATOR) {
            this.removeEntitiesOnShoulder();
            this.stopRiding();
         } else {
            this.setCamera(this);
         }

         this.onUpdateAbilities();
         this.updateEffectVisibility();
         return true;
      }
   }

   @Override
   public boolean isSpectator() {
      return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
   }

   @Override
   public boolean isCreative() {
      return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
   }

   @Override
   public void sendSystemMessage(Component var1) {
      this.sendSystemMessage(var1, false);
   }

   public void sendSystemMessage(Component var1, boolean var2) {
      if (this.acceptsSystemMessages(var2)) {
         this.connection.send(new ClientboundSystemChatPacket(var1, var2), PacketSendListener.exceptionallySend(() -> {
            if (this.acceptsSystemMessages(false)) {
               short var2x = 256;
               String var3 = var1.getString(256);
               MutableComponent var4 = Component.literal(var3).withStyle(ChatFormatting.YELLOW);
               return new ClientboundSystemChatPacket(Component.translatable("multiplayer.message_not_delivered", var4).withStyle(ChatFormatting.RED), false);
            } else {
               return null;
            }
         }));
      }
   }

   public void sendChatMessage(OutgoingChatMessage var1, boolean var2, ChatType.Bound var3) {
      if (this.acceptsChatMessages()) {
         var1.sendToPlayer(this, var2, var3);
      }
   }

   public String getIpAddress() {
      return this.connection.getRemoteAddress() instanceof InetSocketAddress var2 ? InetAddresses.toAddrString(var2.getAddress()) : "<unknown>";
   }

   public void updateOptions(ClientInformation var1) {
      this.language = var1.language();
      this.requestedViewDistance = var1.viewDistance();
      this.chatVisibility = var1.chatVisibility();
      this.canChatColor = var1.chatColors();
      this.textFilteringEnabled = var1.textFilteringEnabled();
      this.allowsListing = var1.allowsListing();
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)var1.modelCustomisation());
      this.getEntityData().set(DATA_PLAYER_MAIN_HAND, (byte)var1.mainHand().getId());
   }

   public ClientInformation clientInformation() {
      byte var1 = this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION);
      HumanoidArm var2 = HumanoidArm.BY_ID.apply(this.getEntityData().get(DATA_PLAYER_MAIN_HAND));
      return new ClientInformation(
         this.language, this.requestedViewDistance, this.chatVisibility, this.canChatColor, var1, var2, this.textFilteringEnabled, this.allowsListing
      );
   }

   public boolean canChatInColor() {
      return this.canChatColor;
   }

   public ChatVisiblity getChatVisibility() {
      return this.chatVisibility;
   }

   private boolean acceptsSystemMessages(boolean var1) {
      return this.chatVisibility == ChatVisiblity.HIDDEN ? var1 : true;
   }

   private boolean acceptsChatMessages() {
      return this.chatVisibility == ChatVisiblity.FULL;
   }

   public int requestedViewDistance() {
      return this.requestedViewDistance;
   }

   public void sendServerStatus(ServerStatus var1) {
      this.connection.send(new ClientboundServerDataPacket(var1.description(), var1.favicon().map(ServerStatus.Favicon::iconBytes)));
   }

   @Override
   protected int getPermissionLevel() {
      return this.server.getProfilePermissions(this.getGameProfile());
   }

   public void resetLastActionTime() {
      this.lastActionTime = Util.getMillis();
   }

   public ServerStatsCounter getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   @Override
   protected void updateInvisibilityStatus() {
      if (this.isSpectator()) {
         this.removeEffectParticles();
         this.setInvisible(true);
      } else {
         super.updateInvisibilityStatus();
      }
   }

   public Entity getCamera() {
      return (Entity)(this.camera == null ? this : this.camera);
   }

   public void setCamera(@Nullable Entity var1) {
      Entity var2 = this.getCamera();
      this.camera = (Entity)(var1 == null ? this : var1);
      if (var2 != this.camera) {
         if (this.camera.level() instanceof ServerLevel var3) {
            this.teleportTo(var3, this.camera.getX(), this.camera.getY(), this.camera.getZ(), Set.of(), this.getYRot(), this.getXRot());
         }

         if (var1 != null) {
            this.serverLevel().getChunkSource().move(this);
         }

         this.connection.send(new ClientboundSetCameraPacket(this.camera));
         this.connection.resetPosition();
      }
   }

   @Override
   protected void processPortalCooldown() {
      if (!this.isChangingDimension) {
         super.processPortalCooldown();
      }
   }

   @Override
   public void attack(Entity var1) {
      if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
         this.setCamera(var1);
      } else {
         super.attack(var1);
      }
   }

   public long getLastActionTime() {
      return this.lastActionTime;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return null;
   }

   @Override
   public void swing(InteractionHand var1) {
      super.swing(var1);
      this.resetAttackStrengthTicker();
   }

   public boolean isChangingDimension() {
      return this.isChangingDimension;
   }

   public void hasChangedDimension() {
      this.isChangingDimension = false;
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   public void teleportTo(ServerLevel var1, double var2, double var4, double var6, float var8, float var9) {
      this.setCamera(this);
      this.stopRiding();
      if (var1 == this.level()) {
         this.connection.teleport(var2, var4, var6, var8, var9);
      } else {
         ServerLevel var10 = this.serverLevel();
         LevelData var11 = var1.getLevelData();
         this.connection.send(new ClientboundRespawnPacket(this.createCommonSpawnInfo(var1), (byte)3));
         this.connection.send(new ClientboundChangeDifficultyPacket(var11.getDifficulty(), var11.isDifficultyLocked()));
         this.server.getPlayerList().sendPlayerPermissionLevel(this);
         var10.removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
         this.unsetRemoved();
         this.moveTo(var2, var4, var6, var8, var9);
         this.setServerLevel(var1);
         var1.addDuringCommandTeleport(this);
         this.triggerDimensionChangeTriggers(var10);
         this.connection.teleport(var2, var4, var6, var8, var9);
         this.server.getPlayerList().sendLevelInfo(this, var1);
         this.server.getPlayerList().sendAllPlayerInfo(this);
      }
   }

   @Nullable
   public BlockPos getRespawnPosition() {
      return this.respawnPosition;
   }

   public float getRespawnAngle() {
      return this.respawnAngle;
   }

   public ResourceKey<Level> getRespawnDimension() {
      return this.respawnDimension;
   }

   public boolean isRespawnForced() {
      return this.respawnForced;
   }

   public void setRespawnPosition(ResourceKey<Level> var1, @Nullable BlockPos var2, float var3, boolean var4, boolean var5) {
      if (var2 != null) {
         boolean var6 = var2.equals(this.respawnPosition) && var1.equals(this.respawnDimension);
         if (var5 && !var6) {
            this.sendSystemMessage(Component.translatable("block.minecraft.set_spawn"));
         }

         this.respawnPosition = var2;
         this.respawnDimension = var1;
         this.respawnAngle = var3;
         this.respawnForced = var4;
      } else {
         this.respawnPosition = null;
         this.respawnDimension = Level.OVERWORLD;
         this.respawnAngle = 0.0F;
         this.respawnForced = false;
      }
   }

   public SectionPos getLastSectionPos() {
      return this.lastSectionPos;
   }

   public void setLastSectionPos(SectionPos var1) {
      this.lastSectionPos = var1;
   }

   public ChunkTrackingView getChunkTrackingView() {
      return this.chunkTrackingView;
   }

   public void setChunkTrackingView(ChunkTrackingView var1) {
      this.chunkTrackingView = var1;
   }

   @Override
   public void playNotifySound(SoundEvent var1, SoundSource var2, float var3, float var4) {
      this.connection
         .send(
            new ClientboundSoundPacket(
               BuiltInRegistries.SOUND_EVENT.wrapAsHolder(var1), var2, this.getX(), this.getY(), this.getZ(), var3, var4, this.random.nextLong()
            )
         );
   }

   @Override
   public ItemEntity drop(ItemStack var1, boolean var2, boolean var3) {
      ItemEntity var4 = super.drop(var1, var2, var3);
      if (var4 == null) {
         return null;
      } else {
         this.level().addFreshEntity(var4);
         ItemStack var5 = var4.getItem();
         if (var3) {
            if (!var5.isEmpty()) {
               this.awardStat(Stats.ITEM_DROPPED.get(var5.getItem()), var1.getCount());
            }

            this.awardStat(Stats.DROP);
         }

         return var4;
      }
   }

   public TextFilter getTextFilter() {
      return this.textFilter;
   }

   public void setServerLevel(ServerLevel var1) {
      this.setLevel(var1);
      this.gameMode.setLevel(var1);
   }

   @Nullable
   private static GameType readPlayerMode(@Nullable CompoundTag var0, String var1) {
      return var0 != null && var0.contains(var1, 99) ? GameType.byId(var0.getInt(var1)) : null;
   }

   private GameType calculateGameModeForNewPlayer(@Nullable GameType var1) {
      GameType var2 = this.server.getForcedGameType();
      if (var2 != null) {
         return var2;
      } else {
         return var1 != null ? var1 : this.server.getDefaultGameType();
      }
   }

   public void loadGameTypes(@Nullable CompoundTag var1) {
      this.gameMode
         .setGameModeForPlayer(this.calculateGameModeForNewPlayer(readPlayerMode(var1, "playerGameType")), readPlayerMode(var1, "previousPlayerGameType"));
   }

   private void storeGameTypes(CompoundTag var1) {
      var1.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
      GameType var2 = this.gameMode.getPreviousGameModeForPlayer();
      if (var2 != null) {
         var1.putInt("previousPlayerGameType", var2.getId());
      }
   }

   @Override
   public boolean isTextFilteringEnabled() {
      return this.textFilteringEnabled;
   }

   public boolean shouldFilterMessageTo(ServerPlayer var1) {
      return var1 == this ? false : this.textFilteringEnabled || var1.textFilteringEnabled;
   }

   @Override
   public boolean mayInteract(Level var1, BlockPos var2) {
      return super.mayInteract(var1, var2) && var1.mayInteract(this, var2);
   }

   @Override
   protected void updateUsingItem(ItemStack var1) {
      CriteriaTriggers.USING_ITEM.trigger(this, var1);
      super.updateUsingItem(var1);
   }

   public boolean drop(boolean var1) {
      Inventory var2 = this.getInventory();
      ItemStack var3 = var2.removeFromSelected(var1);
      this.containerMenu.findSlot(var2, var2.selected).ifPresent(var2x -> this.containerMenu.setRemoteSlot(var2x, var2.getSelected()));
      return this.drop(var3, false, true) != null;
   }

   public boolean allowsListing() {
      return this.allowsListing;
   }

   @Override
   public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
      return Optional.of(this.wardenSpawnTracker);
   }

   public void setSpawnExtraParticlesOnFall(boolean var1) {
      this.spawnExtraParticlesOnFall = var1;
   }

   @Override
   public void onItemPickup(ItemEntity var1) {
      super.onItemPickup(var1);
      Entity var2 = var1.getOwner();
      if (var2 != null) {
         CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.trigger(this, var1.getItem(), var2);
      }
   }

   public void setChatSession(RemoteChatSession var1) {
      this.chatSession = var1;
   }

   @Nullable
   public RemoteChatSession getChatSession() {
      return this.chatSession != null && this.chatSession.hasExpired() ? null : this.chatSession;
   }

   @Override
   public void indicateDamage(double var1, double var3) {
      this.hurtDir = (float)(Mth.atan2(var3, var1) * 57.2957763671875 - (double)this.getYRot());
      this.connection.send(new ClientboundHurtAnimationPacket(this));
   }

   @Override
   public boolean startRiding(Entity var1, boolean var2) {
      if (!super.startRiding(var1, var2)) {
         return false;
      } else {
         var1.positionRider(this);
         this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
         if (var1 instanceof LivingEntity var3) {
            for (MobEffectInstance var5 : var3.getActiveEffects()) {
               this.connection.send(new ClientboundUpdateMobEffectPacket(var1.getId(), var5, false));
            }
         }

         return true;
      }
   }

   @Override
   public void stopRiding() {
      Entity var1 = this.getVehicle();
      super.stopRiding();
      if (var1 instanceof LivingEntity var2) {
         for (MobEffectInstance var4 : var2.getActiveEffects()) {
            this.connection.send(new ClientboundRemoveMobEffectPacket(var1.getId(), var4.getEffect()));
         }
      }
   }

   public CommonPlayerSpawnInfo createCommonSpawnInfo(ServerLevel var1) {
      return new CommonPlayerSpawnInfo(
         var1.dimensionTypeRegistration(),
         var1.dimension(),
         BiomeManager.obfuscateSeed(var1.getSeed()),
         this.gameMode.getGameModeForPlayer(),
         this.gameMode.getPreviousGameModeForPlayer(),
         var1.isDebug(),
         var1.isFlat(),
         this.getLastDeathLocation(),
         this.getPortalCooldown()
      );
   }

   public void setRaidOmenPosition(BlockPos var1) {
      this.raidOmenPosition = var1;
   }

   public void clearRaidOmenPosition() {
      this.raidOmenPosition = null;
   }

   @Nullable
   public BlockPos getRaidOmenPosition() {
      return this.raidOmenPosition;
   }
}
