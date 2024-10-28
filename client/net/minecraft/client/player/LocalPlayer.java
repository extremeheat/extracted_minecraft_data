package net.minecraft.client.player;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class LocalPlayer extends AbstractClientPlayer {
   public static final Logger LOGGER = LogUtils.getLogger();
   private static final int POSITION_REMINDER_INTERVAL = 20;
   private static final int WATER_VISION_MAX_TIME = 600;
   private static final int WATER_VISION_QUICK_TIME = 100;
   private static final float WATER_VISION_QUICK_PERCENT = 0.6F;
   private static final double SUFFOCATING_COLLISION_CHECK_SCALE = 0.35;
   private static final double MINOR_COLLISION_ANGLE_THRESHOLD_RADIAN = 0.13962633907794952;
   public final ClientPacketListener connection;
   private final StatsCounter stats;
   private final ClientRecipeBook recipeBook;
   private final List<AmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
   private int permissionLevel = 0;
   private double xLast;
   private double yLast1;
   private double zLast;
   private float yRotLast;
   private float xRotLast;
   private boolean lastOnGround;
   private boolean crouching;
   private boolean wasShiftKeyDown;
   private boolean wasSprinting;
   private int positionReminder;
   private boolean flashOnSetHealth;
   public Input input;
   protected final Minecraft minecraft;
   protected int sprintTriggerTime;
   public float yBob;
   public float xBob;
   public float yBobO;
   public float xBobO;
   private int jumpRidingTicks;
   private float jumpRidingScale;
   public float spinningEffectIntensity;
   public float oSpinningEffectIntensity;
   private boolean startedUsingItem;
   @Nullable
   private InteractionHand usingItemHand;
   private boolean handsBusy;
   private boolean autoJumpEnabled = true;
   private int autoJumpTime;
   private boolean wasFallFlying;
   private int waterVisionTime;
   private boolean showDeathScreen = true;
   private boolean doLimitedCrafting = false;

   public LocalPlayer(Minecraft var1, ClientLevel var2, ClientPacketListener var3, StatsCounter var4, ClientRecipeBook var5, boolean var6, boolean var7) {
      super(var2, var3.getLocalGameProfile());
      this.minecraft = var1;
      this.connection = var3;
      this.stats = var4;
      this.recipeBook = var5;
      this.wasShiftKeyDown = var6;
      this.wasSprinting = var7;
      this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, var1.getSoundManager()));
      this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
      this.ambientSoundHandlers.add(new BiomeAmbientSoundsHandler(this, var1.getSoundManager(), var2.getBiomeManager()));
   }

   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }

   public void heal(float var1) {
   }

   public boolean startRiding(Entity var1, boolean var2) {
      if (!super.startRiding(var1, var2)) {
         return false;
      } else {
         if (var1 instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)var1, true));
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)var1, false));
         }

         return true;
      }
   }

   public void removeVehicle() {
      super.removeVehicle();
      this.handsBusy = false;
   }

   public float getViewXRot(float var1) {
      return this.getXRot();
   }

   public float getViewYRot(float var1) {
      return this.isPassenger() ? super.getViewYRot(var1) : this.getYRot();
   }

   public void tick() {
      if (this.level().hasChunkAt(this.getBlockX(), this.getBlockZ())) {
         super.tick();
         if (this.isPassenger()) {
            this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround()));
            this.connection.send(new ServerboundPlayerInputPacket(this.xxa, this.zza, this.input.jumping, this.input.shiftKeyDown));
            Entity var1 = this.getRootVehicle();
            if (var1 != this && var1.isControlledByLocalInstance()) {
               this.connection.send(new ServerboundMoveVehiclePacket(var1));
               this.sendIsSprintingIfNeeded();
            }
         } else {
            this.sendPosition();
         }

         Iterator var3 = this.ambientSoundHandlers.iterator();

         while(var3.hasNext()) {
            AmbientSoundHandler var2 = (AmbientSoundHandler)var3.next();
            var2.tick();
         }

      }
   }

   public float getCurrentMood() {
      Iterator var1 = this.ambientSoundHandlers.iterator();

      AmbientSoundHandler var2;
      do {
         if (!var1.hasNext()) {
            return 0.0F;
         }

         var2 = (AmbientSoundHandler)var1.next();
      } while(!(var2 instanceof BiomeAmbientSoundsHandler));

      return ((BiomeAmbientSoundsHandler)var2).getMoodiness();
   }

   private void sendPosition() {
      this.sendIsSprintingIfNeeded();
      boolean var1 = this.isShiftKeyDown();
      if (var1 != this.wasShiftKeyDown) {
         ServerboundPlayerCommandPacket.Action var2 = var1 ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
         this.connection.send(new ServerboundPlayerCommandPacket(this, var2));
         this.wasShiftKeyDown = var1;
      }

      if (this.isControlledCamera()) {
         double var15 = this.getX() - this.xLast;
         double var4 = this.getY() - this.yLast1;
         double var6 = this.getZ() - this.zLast;
         double var8 = (double)(this.getYRot() - this.yRotLast);
         double var10 = (double)(this.getXRot() - this.xRotLast);
         ++this.positionReminder;
         boolean var12 = Mth.lengthSquared(var15, var4, var6) > Mth.square(2.0E-4) || this.positionReminder >= 20;
         boolean var13 = var8 != 0.0 || var10 != 0.0;
         if (this.isPassenger()) {
            Vec3 var14 = this.getDeltaMovement();
            this.connection.send(new ServerboundMovePlayerPacket.PosRot(var14.x, -999.0, var14.z, this.getYRot(), this.getXRot(), this.onGround()));
            var12 = false;
         } else if (var12 && var13) {
            this.connection.send(new ServerboundMovePlayerPacket.PosRot(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot(), this.onGround()));
         } else if (var12) {
            this.connection.send(new ServerboundMovePlayerPacket.Pos(this.getX(), this.getY(), this.getZ(), this.onGround()));
         } else if (var13) {
            this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround()));
         } else if (this.lastOnGround != this.onGround()) {
            this.connection.send(new ServerboundMovePlayerPacket.StatusOnly(this.onGround()));
         }

         if (var12) {
            this.xLast = this.getX();
            this.yLast1 = this.getY();
            this.zLast = this.getZ();
            this.positionReminder = 0;
         }

         if (var13) {
            this.yRotLast = this.getYRot();
            this.xRotLast = this.getXRot();
         }

         this.lastOnGround = this.onGround();
         this.autoJumpEnabled = (Boolean)this.minecraft.options.autoJump().get();
      }

   }

   private void sendIsSprintingIfNeeded() {
      boolean var1 = this.isSprinting();
      if (var1 != this.wasSprinting) {
         ServerboundPlayerCommandPacket.Action var2 = var1 ? ServerboundPlayerCommandPacket.Action.START_SPRINTING : ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
         this.connection.send(new ServerboundPlayerCommandPacket(this, var2));
         this.wasSprinting = var1;
      }

   }

   public boolean drop(boolean var1) {
      ServerboundPlayerActionPacket.Action var2 = var1 ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM;
      ItemStack var3 = this.getInventory().removeFromSelected(var1);
      this.connection.send(new ServerboundPlayerActionPacket(var2, BlockPos.ZERO, Direction.DOWN));
      return !var3.isEmpty();
   }

   public void swing(InteractionHand var1) {
      super.swing(var1);
      this.connection.send(new ServerboundSwingPacket(var1));
   }

   public void respawn() {
      this.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
      KeyMapping.resetToggleKeys();
   }

   protected void actuallyHurt(DamageSource var1, float var2) {
      if (!this.isInvulnerableTo(var1)) {
         this.setHealth(this.getHealth() - var2);
      }
   }

   public void closeContainer() {
      this.connection.send(new ServerboundContainerClosePacket(this.containerMenu.containerId));
      this.clientSideCloseContainer();
   }

   public void clientSideCloseContainer() {
      super.closeContainer();
      this.minecraft.setScreen((Screen)null);
   }

   public void hurtTo(float var1) {
      if (this.flashOnSetHealth) {
         float var2 = this.getHealth() - var1;
         if (var2 <= 0.0F) {
            this.setHealth(var1);
            if (var2 < 0.0F) {
               this.invulnerableTime = 10;
            }
         } else {
            this.lastHurt = var2;
            this.invulnerableTime = 20;
            this.setHealth(var1);
            this.hurtDuration = 10;
            this.hurtTime = this.hurtDuration;
         }
      } else {
         this.setHealth(var1);
         this.flashOnSetHealth = true;
      }

   }

   public void onUpdateAbilities() {
      this.connection.send(new ServerboundPlayerAbilitiesPacket(this.getAbilities()));
   }

   public boolean isLocalPlayer() {
      return true;
   }

   public boolean isSuppressingSlidingDownLadder() {
      return !this.getAbilities().flying && super.isSuppressingSlidingDownLadder();
   }

   public boolean canSpawnSprintParticle() {
      return !this.getAbilities().flying && super.canSpawnSprintParticle();
   }

   protected void sendRidingJump() {
      this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(this.getJumpRidingScale() * 100.0F)));
   }

   public void sendOpenInventory() {
      this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
   }

   public StatsCounter getStats() {
      return this.stats;
   }

   public ClientRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void removeRecipeHighlight(RecipeHolder<?> var1) {
      if (this.recipeBook.willHighlight(var1)) {
         this.recipeBook.removeHighlight(var1);
         this.connection.send(new ServerboundRecipeBookSeenRecipePacket(var1));
      }

   }

   protected int getPermissionLevel() {
      return this.permissionLevel;
   }

   public void setPermissionLevel(int var1) {
      this.permissionLevel = var1;
   }

   public void displayClientMessage(Component var1, boolean var2) {
      this.minecraft.getChatListener().handleSystemMessage(var1, var2);
   }

   private void moveTowardsClosestSpace(double var1, double var3) {
      BlockPos var5 = BlockPos.containing(var1, this.getY(), var3);
      if (this.suffocatesAt(var5)) {
         double var6 = var1 - (double)var5.getX();
         double var8 = var3 - (double)var5.getZ();
         Direction var10 = null;
         double var11 = 1.7976931348623157E308;
         Direction[] var13 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
         Direction[] var14 = var13;
         int var15 = var13.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            Direction var17 = var14[var16];
            double var18 = var17.getAxis().choose(var6, 0.0, var8);
            double var20 = var17.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - var18 : var18;
            if (var20 < var11 && !this.suffocatesAt(var5.relative(var17))) {
               var11 = var20;
               var10 = var17;
            }
         }

         if (var10 != null) {
            Vec3 var22 = this.getDeltaMovement();
            if (var10.getAxis() == Direction.Axis.X) {
               this.setDeltaMovement(0.1 * (double)var10.getStepX(), var22.y, var22.z);
            } else {
               this.setDeltaMovement(var22.x, var22.y, 0.1 * (double)var10.getStepZ());
            }
         }

      }
   }

   private boolean suffocatesAt(BlockPos var1) {
      AABB var2 = this.getBoundingBox();
      AABB var3 = (new AABB((double)var1.getX(), var2.minY, (double)var1.getZ(), (double)var1.getX() + 1.0, var2.maxY, (double)var1.getZ() + 1.0)).deflate(1.0E-7);
      return this.level().collidesWithSuffocatingBlock(this, var3);
   }

   public void setExperienceValues(float var1, int var2, int var3) {
      this.experienceProgress = var1;
      this.totalExperience = var2;
      this.experienceLevel = var3;
   }

   public void sendSystemMessage(Component var1) {
      this.minecraft.gui.getChat().addMessage(var1);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 >= 24 && var1 <= 28) {
         this.setPermissionLevel(var1 - 24);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public void setShowDeathScreen(boolean var1) {
      this.showDeathScreen = var1;
   }

   public boolean shouldShowDeathScreen() {
      return this.showDeathScreen;
   }

   public void setDoLimitedCrafting(boolean var1) {
      this.doLimitedCrafting = var1;
   }

   public boolean getDoLimitedCrafting() {
      return this.doLimitedCrafting;
   }

   public void playSound(SoundEvent var1, float var2, float var3) {
      this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), var1, this.getSoundSource(), var2, var3, false);
   }

   public void playNotifySound(SoundEvent var1, SoundSource var2, float var3, float var4) {
      this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), var1, var2, var3, var4, false);
   }

   public boolean isEffectiveAi() {
      return true;
   }

   public void startUsingItem(InteractionHand var1) {
      ItemStack var2 = this.getItemInHand(var1);
      if (!var2.isEmpty() && !this.isUsingItem()) {
         super.startUsingItem(var1);
         this.startedUsingItem = true;
         this.usingItemHand = var1;
      }
   }

   public boolean isUsingItem() {
      return this.startedUsingItem;
   }

   public void stopUsingItem() {
      super.stopUsingItem();
      this.startedUsingItem = false;
   }

   public InteractionHand getUsedItemHand() {
      return (InteractionHand)Objects.requireNonNullElse(this.usingItemHand, InteractionHand.MAIN_HAND);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_LIVING_ENTITY_FLAGS.equals(var1)) {
         boolean var2 = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
         InteractionHand var3 = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         if (var2 && !this.startedUsingItem) {
            this.startUsingItem(var3);
         } else if (!var2 && this.startedUsingItem) {
            this.stopUsingItem();
         }
      }

      if (DATA_SHARED_FLAGS_ID.equals(var1) && this.isFallFlying() && !this.wasFallFlying) {
         this.minecraft.getSoundManager().play(new ElytraOnPlayerSoundInstance(this));
      }

   }

   @Nullable
   public PlayerRideableJumping jumpableVehicle() {
      Entity var2 = this.getControlledVehicle();
      PlayerRideableJumping var10000;
      if (var2 instanceof PlayerRideableJumping var1) {
         if (var1.canJump()) {
            var10000 = var1;
            return var10000;
         }
      }

      var10000 = null;
      return var10000;
   }

   public float getJumpRidingScale() {
      return this.jumpRidingScale;
   }

   public boolean isTextFilteringEnabled() {
      return this.minecraft.isTextFilteringEnabled();
   }

   public void openTextEdit(SignBlockEntity var1, boolean var2) {
      if (var1 instanceof HangingSignBlockEntity var3) {
         this.minecraft.setScreen(new HangingSignEditScreen(var3, var2, this.minecraft.isTextFilteringEnabled()));
      } else {
         this.minecraft.setScreen(new SignEditScreen(var1, var2, this.minecraft.isTextFilteringEnabled()));
      }

   }

   public void openMinecartCommandBlock(BaseCommandBlock var1) {
      this.minecraft.setScreen(new MinecartCommandBlockEditScreen(var1));
   }

   public void openCommandBlock(CommandBlockEntity var1) {
      this.minecraft.setScreen(new CommandBlockEditScreen(var1));
   }

   public void openStructureBlock(StructureBlockEntity var1) {
      this.minecraft.setScreen(new StructureBlockEditScreen(var1));
   }

   public void openJigsawBlock(JigsawBlockEntity var1) {
      this.minecraft.setScreen(new JigsawBlockEditScreen(var1));
   }

   public void openItemGui(ItemStack var1, InteractionHand var2) {
      if (var1.is(Items.WRITABLE_BOOK)) {
         this.minecraft.setScreen(new BookEditScreen(this, var1, var2));
      }

   }

   public void crit(Entity var1) {
      this.minecraft.particleEngine.createTrackingEmitter(var1, ParticleTypes.CRIT);
   }

   public void magicCrit(Entity var1) {
      this.minecraft.particleEngine.createTrackingEmitter(var1, ParticleTypes.ENCHANTED_HIT);
   }

   public boolean isShiftKeyDown() {
      return this.input != null && this.input.shiftKeyDown;
   }

   public boolean isCrouching() {
      return this.crouching;
   }

   public boolean isMovingSlowly() {
      return this.isCrouching() || this.isVisuallyCrawling();
   }

   public void serverAiStep() {
      super.serverAiStep();
      if (this.isControlledCamera()) {
         this.xxa = this.input.leftImpulse;
         this.zza = this.input.forwardImpulse;
         this.jumping = this.input.jumping;
         this.yBobO = this.yBob;
         this.xBobO = this.xBob;
         this.xBob += (this.getXRot() - this.xBob) * 0.5F;
         this.yBob += (this.getYRot() - this.yBob) * 0.5F;
      }

   }

   protected boolean isControlledCamera() {
      return this.minecraft.getCameraEntity() == this;
   }

   public void resetPos() {
      this.setPose(Pose.STANDING);
      if (this.level() != null) {
         for(double var1 = this.getY(); var1 > (double)this.level().getMinBuildHeight() && var1 < (double)this.level().getMaxBuildHeight(); ++var1) {
            this.setPos(this.getX(), var1, this.getZ());
            if (this.level().noCollision(this)) {
               break;
            }
         }

         this.setDeltaMovement(Vec3.ZERO);
         this.setXRot(0.0F);
      }

      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   public void aiStep() {
      if (this.sprintTriggerTime > 0) {
         --this.sprintTriggerTime;
      }

      if (!(this.minecraft.screen instanceof ReceivingLevelScreen)) {
         this.handleNetherPortalClient();
      }

      boolean var1 = this.input.jumping;
      boolean var2 = this.input.shiftKeyDown;
      boolean var3 = this.hasEnoughImpulseToStartSprinting();
      Abilities var4 = this.getAbilities();
      this.crouching = !var4.flying && !this.isSwimming() && !this.isPassenger() && this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING) && (this.isShiftKeyDown() || !this.isSleeping() && !this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING));
      float var5 = (float)this.getAttributeValue(Attributes.SNEAKING_SPEED);
      this.input.tick(this.isMovingSlowly(), var5);
      this.minecraft.getTutorial().onInput(this.input);
      if (this.isUsingItem() && !this.isPassenger()) {
         Input var10000 = this.input;
         var10000.leftImpulse *= 0.2F;
         var10000 = this.input;
         var10000.forwardImpulse *= 0.2F;
         this.sprintTriggerTime = 0;
      }

      boolean var6 = false;
      if (this.autoJumpTime > 0) {
         --this.autoJumpTime;
         var6 = true;
         this.input.jumping = true;
      }

      if (!this.noPhysics) {
         this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
         this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
         this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
         this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
      }

      if (var2) {
         this.sprintTriggerTime = 0;
      }

      boolean var7 = this.canStartSprinting();
      boolean var8 = this.isPassenger() ? this.getVehicle().onGround() : this.onGround();
      boolean var9 = !var2 && !var3;
      if ((var8 || this.isUnderWater()) && var9 && var7) {
         if (this.sprintTriggerTime <= 0 && !this.minecraft.options.keySprint.isDown()) {
            this.sprintTriggerTime = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if ((!this.isInWater() || this.isUnderWater()) && var7 && this.minecraft.options.keySprint.isDown()) {
         this.setSprinting(true);
      }

      boolean var10;
      if (this.isSprinting()) {
         var10 = !this.input.hasForwardImpulse() || !this.hasEnoughFoodToStartSprinting();
         boolean var11 = var10 || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater();
         if (this.isSwimming()) {
            if (!this.onGround() && !this.input.shiftKeyDown && var10 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (var11) {
            this.setSprinting(false);
         }
      }

      var10 = false;
      if (var4.mayfly) {
         if (this.minecraft.gameMode.isAlwaysFlying()) {
            if (!var4.flying) {
               var4.flying = true;
               var10 = true;
               this.onUpdateAbilities();
            }
         } else if (!var1 && this.input.jumping && !var6) {
            if (this.jumpTriggerTime == 0) {
               this.jumpTriggerTime = 7;
            } else if (!this.isSwimming()) {
               var4.flying = !var4.flying;
               if (var4.flying && this.onGround()) {
                  this.jumpFromGround();
               }

               var10 = true;
               this.onUpdateAbilities();
               this.jumpTriggerTime = 0;
            }
         }
      }

      if (this.input.jumping && !var10 && !var1 && !var4.flying && !this.isPassenger() && !this.onClimbable()) {
         ItemStack var12 = this.getItemBySlot(EquipmentSlot.CHEST);
         if (var12.is(Items.ELYTRA) && ElytraItem.isFlyEnabled(var12) && this.tryToStartFallFlying()) {
            this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
         }
      }

      this.wasFallFlying = this.isFallFlying();
      if (this.isInWater() && this.input.shiftKeyDown && this.isAffectedByFluids()) {
         this.goDownInWater();
      }

      int var13;
      if (this.isEyeInFluid(FluidTags.WATER)) {
         var13 = this.isSpectator() ? 10 : 1;
         this.waterVisionTime = Mth.clamp(this.waterVisionTime + var13, 0, 600);
      } else if (this.waterVisionTime > 0) {
         this.isEyeInFluid(FluidTags.WATER);
         this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
      }

      if (var4.flying && this.isControlledCamera()) {
         var13 = 0;
         if (this.input.shiftKeyDown) {
            --var13;
         }

         if (this.input.jumping) {
            ++var13;
         }

         if (var13 != 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, (double)((float)var13 * var4.getFlyingSpeed() * 3.0F), 0.0));
         }
      }

      PlayerRideableJumping var14 = this.jumpableVehicle();
      if (var14 != null && var14.getJumpCooldown() == 0) {
         if (this.jumpRidingTicks < 0) {
            ++this.jumpRidingTicks;
            if (this.jumpRidingTicks == 0) {
               this.jumpRidingScale = 0.0F;
            }
         }

         if (var1 && !this.input.jumping) {
            this.jumpRidingTicks = -10;
            var14.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0F));
            this.sendRidingJump();
         } else if (!var1 && this.input.jumping) {
            this.jumpRidingTicks = 0;
            this.jumpRidingScale = 0.0F;
         } else if (var1) {
            ++this.jumpRidingTicks;
            if (this.jumpRidingTicks < 10) {
               this.jumpRidingScale = (float)this.jumpRidingTicks * 0.1F;
            } else {
               this.jumpRidingScale = 0.8F + 2.0F / (float)(this.jumpRidingTicks - 9) * 0.1F;
            }
         }
      } else {
         this.jumpRidingScale = 0.0F;
      }

      super.aiStep();
      if (this.onGround() && var4.flying && !this.minecraft.gameMode.isAlwaysFlying()) {
         var4.flying = false;
         this.onUpdateAbilities();
      }

   }

   protected void tickDeath() {
      ++this.deathTime;
      if (this.deathTime == 20) {
         this.remove(Entity.RemovalReason.KILLED);
      }

   }

   private void handleNetherPortalClient() {
      this.oSpinningEffectIntensity = this.spinningEffectIntensity;
      float var1 = 0.0F;
      if (this.isInsidePortal) {
         if (this.minecraft.screen != null && !this.minecraft.screen.isPauseScreen() && !(this.minecraft.screen instanceof DeathScreen)) {
            if (this.minecraft.screen instanceof AbstractContainerScreen) {
               this.closeContainer();
            }

            this.minecraft.setScreen((Screen)null);
         }

         if (this.spinningEffectIntensity == 0.0F) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F, 0.25F));
         }

         var1 = 0.0125F;
         this.isInsidePortal = false;
      } else if (this.hasEffect(MobEffects.CONFUSION) && !this.getEffect(MobEffects.CONFUSION).endsWithin(60)) {
         var1 = 0.006666667F;
      } else if (this.spinningEffectIntensity > 0.0F) {
         var1 = -0.05F;
      }

      this.spinningEffectIntensity = Mth.clamp(this.spinningEffectIntensity + var1, 0.0F, 1.0F);
      this.processPortalCooldown();
   }

   public void rideTick() {
      super.rideTick();
      this.handsBusy = false;
      Entity var2 = this.getControlledVehicle();
      if (var2 instanceof Boat var1) {
         var1.setInput(this.input.left, this.input.right, this.input.up, this.input.down);
         this.handsBusy |= this.input.left || this.input.right || this.input.up || this.input.down;
      }

   }

   public boolean isHandsBusy() {
      return this.handsBusy;
   }

   @Nullable
   public MobEffectInstance removeEffectNoUpdate(Holder<MobEffect> var1) {
      if (var1.is(MobEffects.CONFUSION)) {
         this.oSpinningEffectIntensity = 0.0F;
         this.spinningEffectIntensity = 0.0F;
      }

      return super.removeEffectNoUpdate(var1);
   }

   public void move(MoverType var1, Vec3 var2) {
      double var3 = this.getX();
      double var5 = this.getZ();
      super.move(var1, var2);
      this.updateAutoJump((float)(this.getX() - var3), (float)(this.getZ() - var5));
   }

   public boolean isAutoJumpEnabled() {
      return this.autoJumpEnabled;
   }

   protected void updateAutoJump(float var1, float var2) {
      if (this.canAutoJump()) {
         Vec3 var3 = this.position();
         Vec3 var4 = var3.add((double)var1, 0.0, (double)var2);
         Vec3 var5 = new Vec3((double)var1, 0.0, (double)var2);
         float var6 = this.getSpeed();
         float var7 = (float)var5.lengthSqr();
         float var11;
         if (var7 <= 0.001F) {
            Vec2 var8 = this.input.getMoveVector();
            float var9 = var6 * var8.x;
            float var10 = var6 * var8.y;
            var11 = Mth.sin(this.getYRot() * 0.017453292F);
            float var12 = Mth.cos(this.getYRot() * 0.017453292F);
            var5 = new Vec3((double)(var9 * var12 - var10 * var11), var5.y, (double)(var10 * var12 + var9 * var11));
            var7 = (float)var5.lengthSqr();
            if (var7 <= 0.001F) {
               return;
            }
         }

         float var41 = Mth.invSqrt(var7);
         Vec3 var42 = var5.scale((double)var41);
         Vec3 var43 = this.getForward();
         var11 = (float)(var43.x * var42.x + var43.z * var42.z);
         if (!(var11 < -0.15F)) {
            CollisionContext var44 = CollisionContext.of(this);
            BlockPos var13 = BlockPos.containing(this.getX(), this.getBoundingBox().maxY, this.getZ());
            BlockState var14 = this.level().getBlockState(var13);
            if (var14.getCollisionShape(this.level(), var13, var44).isEmpty()) {
               var13 = var13.above();
               BlockState var15 = this.level().getBlockState(var13);
               if (var15.getCollisionShape(this.level(), var13, var44).isEmpty()) {
                  float var16 = 7.0F;
                  float var17 = 1.2F;
                  if (this.hasEffect(MobEffects.JUMP)) {
                     var17 += (float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.75F;
                  }

                  float var18 = Math.max(var6 * 7.0F, 1.0F / var41);
                  Vec3 var20 = var4.add(var42.scale((double)var18));
                  float var21 = this.getBbWidth();
                  float var22 = this.getBbHeight();
                  AABB var23 = (new AABB(var3, var20.add(0.0, (double)var22, 0.0))).inflate((double)var21, 0.0, (double)var21);
                  Vec3 var19 = var3.add(0.0, 0.5099999904632568, 0.0);
                  var20 = var20.add(0.0, 0.5099999904632568, 0.0);
                  Vec3 var24 = var42.cross(new Vec3(0.0, 1.0, 0.0));
                  Vec3 var25 = var24.scale((double)(var21 * 0.5F));
                  Vec3 var26 = var19.subtract(var25);
                  Vec3 var27 = var20.subtract(var25);
                  Vec3 var28 = var19.add(var25);
                  Vec3 var29 = var20.add(var25);
                  Iterable var30 = this.level().getCollisions(this, var23);
                  Iterator var31 = StreamSupport.stream(var30.spliterator(), false).flatMap((var0) -> {
                     return var0.toAabbs().stream();
                  }).iterator();
                  float var33 = 1.4E-45F;

                  label73:
                  while(var31.hasNext()) {
                     AABB var35 = (AABB)var31.next();
                     if (var35.intersects(var26, var27) || var35.intersects(var28, var29)) {
                        var33 = (float)var35.maxY;
                        Vec3 var32 = var35.getCenter();
                        BlockPos var36 = BlockPos.containing(var32);
                        int var37 = 1;

                        while(true) {
                           if (!((float)var37 < var17)) {
                              break label73;
                           }

                           BlockPos var38 = var36.above(var37);
                           BlockState var39 = this.level().getBlockState(var38);
                           VoxelShape var34;
                           if (!(var34 = var39.getCollisionShape(this.level(), var38, var44)).isEmpty()) {
                              var33 = (float)var34.max(Direction.Axis.Y) + (float)var38.getY();
                              if ((double)var33 - this.getY() > (double)var17) {
                                 return;
                              }
                           }

                           if (var37 > 1) {
                              var13 = var13.above();
                              BlockState var40 = this.level().getBlockState(var13);
                              if (!var40.getCollisionShape(this.level(), var13, var44).isEmpty()) {
                                 return;
                              }
                           }

                           ++var37;
                        }
                     }
                  }

                  if (var33 != 1.4E-45F) {
                     float var45 = (float)((double)var33 - this.getY());
                     if (!(var45 <= 0.5F) && !(var45 > var17)) {
                        this.autoJumpTime = 1;
                     }
                  }
               }
            }
         }
      }
   }

   protected boolean isHorizontalCollisionMinor(Vec3 var1) {
      float var2 = this.getYRot() * 0.017453292F;
      double var3 = (double)Mth.sin(var2);
      double var5 = (double)Mth.cos(var2);
      double var7 = (double)this.xxa * var5 - (double)this.zza * var3;
      double var9 = (double)this.zza * var5 + (double)this.xxa * var3;
      double var11 = Mth.square(var7) + Mth.square(var9);
      double var13 = Mth.square(var1.x) + Mth.square(var1.z);
      if (!(var11 < 9.999999747378752E-6) && !(var13 < 9.999999747378752E-6)) {
         double var15 = var7 * var1.x + var9 * var1.z;
         double var17 = Math.acos(var15 / Math.sqrt(var11 * var13));
         return var17 < 0.13962633907794952;
      } else {
         return false;
      }
   }

   private boolean canAutoJump() {
      return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround() && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && (double)this.getBlockJumpFactor() >= 1.0;
   }

   private boolean isMoving() {
      Vec2 var1 = this.input.getMoveVector();
      return var1.x != 0.0F || var1.y != 0.0F;
   }

   private boolean canStartSprinting() {
      return !this.isSprinting() && this.hasEnoughImpulseToStartSprinting() && this.hasEnoughFoodToStartSprinting() && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS) && (!this.isPassenger() || this.vehicleCanSprint(this.getVehicle())) && !this.isFallFlying();
   }

   private boolean vehicleCanSprint(Entity var1) {
      return var1.canSprint() && var1.isControlledByLocalInstance();
   }

   private boolean hasEnoughImpulseToStartSprinting() {
      double var1 = 0.8;
      return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.8;
   }

   private boolean hasEnoughFoodToStartSprinting() {
      return this.isPassenger() || (float)this.getFoodData().getFoodLevel() > 6.0F || this.getAbilities().mayfly;
   }

   public float getWaterVision() {
      if (!this.isEyeInFluid(FluidTags.WATER)) {
         return 0.0F;
      } else {
         float var1 = 600.0F;
         float var2 = 100.0F;
         if ((float)this.waterVisionTime >= 600.0F) {
            return 1.0F;
         } else {
            float var3 = Mth.clamp((float)this.waterVisionTime / 100.0F, 0.0F, 1.0F);
            float var4 = (float)this.waterVisionTime < 100.0F ? 0.0F : Mth.clamp(((float)this.waterVisionTime - 100.0F) / 500.0F, 0.0F, 1.0F);
            return var3 * 0.6F + var4 * 0.39999998F;
         }
      }
   }

   public void onGameModeChanged(GameType var1) {
      if (var1 == GameType.SPECTATOR) {
         this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, 0.0));
      }

   }

   public boolean isUnderWater() {
      return this.wasUnderwater;
   }

   protected boolean updateIsUnderwater() {
      boolean var1 = this.wasUnderwater;
      boolean var2 = super.updateIsUnderwater();
      if (this.isSpectator()) {
         return this.wasUnderwater;
      } else {
         if (!var1 && var2) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.AMBIENT, 1.0F, 1.0F, false);
            this.minecraft.getSoundManager().play(new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(this));
         }

         if (var1 && !var2) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.wasUnderwater;
      }
   }

   public Vec3 getRopeHoldPosition(float var1) {
      if (this.minecraft.options.getCameraType().isFirstPerson()) {
         float var2 = Mth.lerp(var1 * 0.5F, this.getYRot(), this.yRotO) * 0.017453292F;
         float var3 = Mth.lerp(var1 * 0.5F, this.getXRot(), this.xRotO) * 0.017453292F;
         double var4 = this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0;
         Vec3 var6 = new Vec3(0.39 * var4, -0.6, 0.3);
         return var6.xRot(-var3).yRot(-var2).add(this.getEyePosition(var1));
      } else {
         return super.getRopeHoldPosition(var1);
      }
   }

   public void updateTutorialInventoryAction(ItemStack var1, ItemStack var2, ClickAction var3) {
      this.minecraft.getTutorial().onInventoryAction(var1, var2, var3);
   }

   public float getVisualRotationYInDegrees() {
      return this.getYRot();
   }
}
