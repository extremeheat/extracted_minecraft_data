package net.minecraft.client.player;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookUpdatePacket;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LocalPlayer extends AbstractClientPlayer {
   public final ClientPacketListener connection;
   private final StatsCounter stats;
   private final ClientRecipeBook recipeBook;
   private final List ambientSoundHandlers = Lists.newArrayList();
   private int permissionLevel = 0;
   private double xLast;
   private double yLast1;
   private double zLast;
   private float yRotLast;
   private float xRotLast;
   private boolean lastOnGround;
   private boolean wasShiftKeyDown;
   private boolean wasSprinting;
   private int positionReminder;
   private boolean flashOnSetHealth;
   private String serverBrand;
   public Input input;
   protected final Minecraft minecraft;
   protected int sprintTriggerTime;
   public int sprintTime;
   public float yBob;
   public float xBob;
   public float yBobO;
   public float xBobO;
   private int jumpRidingTicks;
   private float jumpRidingScale;
   public float portalTime;
   public float oPortalTime;
   private boolean startedUsingItem;
   private InteractionHand usingItemHand;
   private boolean handsBusy;
   private boolean autoJumpEnabled = true;
   private int autoJumpTime;
   private boolean wasFallFlying;
   private int waterVisionTime;
   private boolean showDeathScreen = true;

   public LocalPlayer(Minecraft var1, ClientLevel var2, ClientPacketListener var3, StatsCounter var4, ClientRecipeBook var5) {
      super(var2, var3.getLocalGameProfile());
      this.connection = var3;
      this.stats = var4;
      this.recipeBook = var5;
      this.minecraft = var1;
      this.dimension = DimensionType.OVERWORLD;
      this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, var1.getSoundManager()));
      this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
   }

   public boolean isGlowing() {
      return super.isGlowing() || this.minecraft.player.isSpectator() && this.minecraft.options.keySpectatorOutlines.isDown();
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
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)var1));
         }

         if (var1 instanceof Boat) {
            this.yRotO = var1.yRot;
            this.yRot = var1.yRot;
            this.setYHeadRot(var1.yRot);
         }

         return true;
      }
   }

   public void stopRiding() {
      super.stopRiding();
      this.handsBusy = false;
   }

   public float getViewXRot(float var1) {
      return this.xRot;
   }

   public float getViewYRot(float var1) {
      return this.isPassenger() ? super.getViewYRot(var1) : this.yRot;
   }

   public void tick() {
      if (this.level.hasChunkAt(new BlockPos(this.getX(), 0.0D, this.getZ()))) {
         super.tick();
         if (this.isPassenger()) {
            this.connection.send((Packet)(new ServerboundMovePlayerPacket.Rot(this.yRot, this.xRot, this.onGround)));
            this.connection.send((Packet)(new ServerboundPlayerInputPacket(this.xxa, this.zza, this.input.jumping, this.input.shiftKeyDown)));
            Entity var1 = this.getRootVehicle();
            if (var1 != this && var1.isControlledByLocalInstance()) {
               this.connection.send((Packet)(new ServerboundMoveVehiclePacket(var1)));
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

   private void sendPosition() {
      boolean var1 = this.isSprinting();
      if (var1 != this.wasSprinting) {
         ServerboundPlayerCommandPacket.Action var2 = var1 ? ServerboundPlayerCommandPacket.Action.START_SPRINTING : ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
         this.connection.send((Packet)(new ServerboundPlayerCommandPacket(this, var2)));
         this.wasSprinting = var1;
      }

      boolean var16 = this.isShiftKeyDown();
      if (var16 != this.wasShiftKeyDown) {
         ServerboundPlayerCommandPacket.Action var3 = var16 ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
         this.connection.send((Packet)(new ServerboundPlayerCommandPacket(this, var3)));
         this.wasShiftKeyDown = var16;
      }

      if (this.isControlledCamera()) {
         double var17 = this.getX() - this.xLast;
         double var5 = this.getY() - this.yLast1;
         double var7 = this.getZ() - this.zLast;
         double var9 = (double)(this.yRot - this.yRotLast);
         double var11 = (double)(this.xRot - this.xRotLast);
         ++this.positionReminder;
         boolean var13 = var17 * var17 + var5 * var5 + var7 * var7 > 9.0E-4D || this.positionReminder >= 20;
         boolean var14 = var9 != 0.0D || var11 != 0.0D;
         if (this.isPassenger()) {
            Vec3 var15 = this.getDeltaMovement();
            this.connection.send((Packet)(new ServerboundMovePlayerPacket.PosRot(var15.x, -999.0D, var15.z, this.yRot, this.xRot, this.onGround)));
            var13 = false;
         } else if (var13 && var14) {
            this.connection.send((Packet)(new ServerboundMovePlayerPacket.PosRot(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot, this.onGround)));
         } else if (var13) {
            this.connection.send((Packet)(new ServerboundMovePlayerPacket.Pos(this.getX(), this.getY(), this.getZ(), this.onGround)));
         } else if (var14) {
            this.connection.send((Packet)(new ServerboundMovePlayerPacket.Rot(this.yRot, this.xRot, this.onGround)));
         } else if (this.lastOnGround != this.onGround) {
            this.connection.send((Packet)(new ServerboundMovePlayerPacket(this.onGround)));
         }

         if (var13) {
            this.xLast = this.getX();
            this.yLast1 = this.getY();
            this.zLast = this.getZ();
            this.positionReminder = 0;
         }

         if (var14) {
            this.yRotLast = this.yRot;
            this.xRotLast = this.xRot;
         }

         this.lastOnGround = this.onGround;
         this.autoJumpEnabled = this.minecraft.options.autoJump;
      }

   }

   public boolean drop(boolean var1) {
      ServerboundPlayerActionPacket.Action var2 = var1 ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM;
      this.connection.send((Packet)(new ServerboundPlayerActionPacket(var2, BlockPos.ZERO, Direction.DOWN)));
      return this.inventory.removeItem(this.inventory.selected, var1 && !this.inventory.getSelected().isEmpty() ? this.inventory.getSelected().getCount() : 1) != ItemStack.EMPTY;
   }

   public void chat(String var1) {
      this.connection.send((Packet)(new ServerboundChatPacket(var1)));
   }

   public void swing(InteractionHand var1) {
      super.swing(var1);
      this.connection.send((Packet)(new ServerboundSwingPacket(var1)));
   }

   public void respawn() {
      this.connection.send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)));
   }

   protected void actuallyHurt(DamageSource var1, float var2) {
      if (!this.isInvulnerableTo(var1)) {
         this.setHealth(this.getHealth() - var2);
      }
   }

   public void closeContainer() {
      this.connection.send((Packet)(new ServerboundContainerClosePacket(this.containerMenu.containerId)));
      this.clientSideCloseContainer();
   }

   public void clientSideCloseContainer() {
      this.inventory.setCarried(ItemStack.EMPTY);
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
            this.setHealth(this.getHealth());
            this.invulnerableTime = 20;
            this.actuallyHurt(DamageSource.GENERIC, var2);
            this.hurtDuration = 10;
            this.hurtTime = this.hurtDuration;
         }
      } else {
         this.setHealth(var1);
         this.flashOnSetHealth = true;
      }

   }

   public void onUpdateAbilities() {
      this.connection.send((Packet)(new ServerboundPlayerAbilitiesPacket(this.abilities)));
   }

   public boolean isLocalPlayer() {
      return true;
   }

   protected void sendRidingJump() {
      this.connection.send((Packet)(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(this.getJumpRidingScale() * 100.0F))));
   }

   public void sendOpenInventory() {
      this.connection.send((Packet)(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY)));
   }

   public void setServerBrand(String var1) {
      this.serverBrand = var1;
   }

   public String getServerBrand() {
      return this.serverBrand;
   }

   public StatsCounter getStats() {
      return this.stats;
   }

   public ClientRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void removeRecipeHighlight(Recipe var1) {
      if (this.recipeBook.willHighlight(var1)) {
         this.recipeBook.removeHighlight(var1);
         this.connection.send((Packet)(new ServerboundRecipeBookUpdatePacket(var1)));
      }

   }

   protected int getPermissionLevel() {
      return this.permissionLevel;
   }

   public void setPermissionLevel(int var1) {
      this.permissionLevel = var1;
   }

   public void displayClientMessage(Component var1, boolean var2) {
      if (var2) {
         this.minecraft.gui.setOverlayMessage(var1, false);
      } else {
         this.minecraft.gui.getChat().addMessage(var1);
      }

   }

   protected void checkInBlock(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      if (this.blocked(var7)) {
         double var8 = var1 - (double)var7.getX();
         double var10 = var5 - (double)var7.getZ();
         Direction var12 = null;
         double var13 = 9999.0D;
         if (!this.blocked(var7.west()) && var8 < var13) {
            var13 = var8;
            var12 = Direction.WEST;
         }

         if (!this.blocked(var7.east()) && 1.0D - var8 < var13) {
            var13 = 1.0D - var8;
            var12 = Direction.EAST;
         }

         if (!this.blocked(var7.north()) && var10 < var13) {
            var13 = var10;
            var12 = Direction.NORTH;
         }

         if (!this.blocked(var7.south()) && 1.0D - var10 < var13) {
            var13 = 1.0D - var10;
            var12 = Direction.SOUTH;
         }

         if (var12 != null) {
            Vec3 var15 = this.getDeltaMovement();
            switch(var12) {
            case WEST:
               this.setDeltaMovement(-0.1D, var15.y, var15.z);
               break;
            case EAST:
               this.setDeltaMovement(0.1D, var15.y, var15.z);
               break;
            case NORTH:
               this.setDeltaMovement(var15.x, var15.y, -0.1D);
               break;
            case SOUTH:
               this.setDeltaMovement(var15.x, var15.y, 0.1D);
            }
         }
      }

   }

   private boolean blocked(BlockPos var1) {
      AABB var2 = this.getBoundingBox();
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(var1);

      for(int var4 = Mth.floor(var2.minY); var4 < Mth.ceil(var2.maxY); ++var4) {
         var3.setY(var4);
         if (!this.freeAt(var3)) {
            return true;
         }
      }

      return false;
   }

   public void setSprinting(boolean var1) {
      super.setSprinting(var1);
      this.sprintTime = 0;
   }

   public void setExperienceValues(float var1, int var2, int var3) {
      this.experienceProgress = var1;
      this.totalExperience = var2;
      this.experienceLevel = var3;
   }

   public void sendMessage(Component var1) {
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

   public void playSound(SoundEvent var1, float var2, float var3) {
      this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), var1, this.getSoundSource(), var2, var3, false);
   }

   public void playNotifySound(SoundEvent var1, SoundSource var2, float var3, float var4) {
      this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), var1, var2, var3, var4, false);
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
      return this.usingItemHand;
   }

   public void onSyncedDataUpdated(EntityDataAccessor var1) {
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

   public boolean isRidingJumpable() {
      Entity var1 = this.getVehicle();
      return this.isPassenger() && var1 instanceof PlayerRideableJumping && ((PlayerRideableJumping)var1).canJump();
   }

   public float getJumpRidingScale() {
      return this.jumpRidingScale;
   }

   public void openTextEdit(SignBlockEntity var1) {
      this.minecraft.setScreen(new SignEditScreen(var1));
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
      Item var3 = var1.getItem();
      if (var3 == Items.WRITABLE_BOOK) {
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
      if (!this.abilities.flying && !this.isSwimming() && this.canEnterPose(Pose.CROUCHING)) {
         return this.isShiftKeyDown() || !this.isSleeping() && !this.canEnterPose(Pose.STANDING);
      } else {
         return false;
      }
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
         this.xBob = (float)((double)this.xBob + (double)(this.xRot - this.xBob) * 0.5D);
         this.yBob = (float)((double)this.yBob + (double)(this.yRot - this.yBob) * 0.5D);
      }

   }

   protected boolean isControlledCamera() {
      return this.minecraft.getCameraEntity() == this;
   }

   public void aiStep() {
      ++this.sprintTime;
      if (this.sprintTriggerTime > 0) {
         --this.sprintTriggerTime;
      }

      this.handleNetherPortalClient();
      boolean var1 = this.input.jumping;
      boolean var2 = this.input.shiftKeyDown;
      boolean var3 = this.hasEnoughImpulseToStartSprinting();
      this.input.tick(this.isMovingSlowly());
      this.minecraft.getTutorial().onInput(this.input);
      if (this.isUsingItem() && !this.isPassenger()) {
         Input var10000 = this.input;
         var10000.leftImpulse *= 0.2F;
         var10000 = this.input;
         var10000.forwardImpulse *= 0.2F;
         this.sprintTriggerTime = 0;
      }

      boolean var4 = false;
      if (this.autoJumpTime > 0) {
         --this.autoJumpTime;
         var4 = true;
         this.input.jumping = true;
      }

      if (!this.noPhysics) {
         this.checkInBlock(this.getX() - (double)this.getBbWidth() * 0.35D, this.getY() + 0.5D, this.getZ() + (double)this.getBbWidth() * 0.35D);
         this.checkInBlock(this.getX() - (double)this.getBbWidth() * 0.35D, this.getY() + 0.5D, this.getZ() - (double)this.getBbWidth() * 0.35D);
         this.checkInBlock(this.getX() + (double)this.getBbWidth() * 0.35D, this.getY() + 0.5D, this.getZ() - (double)this.getBbWidth() * 0.35D);
         this.checkInBlock(this.getX() + (double)this.getBbWidth() * 0.35D, this.getY() + 0.5D, this.getZ() + (double)this.getBbWidth() * 0.35D);
      }

      boolean var5 = (float)this.getFoodData().getFoodLevel() > 6.0F || this.abilities.mayfly;
      if ((this.onGround || this.isUnderWater()) && !var2 && !var3 && this.hasEnoughImpulseToStartSprinting() && !this.isSprinting() && var5 && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS)) {
         if (this.sprintTriggerTime <= 0 && !this.minecraft.options.keySprint.isDown()) {
            this.sprintTriggerTime = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting() && (!this.isInWater() || this.isUnderWater()) && this.hasEnoughImpulseToStartSprinting() && var5 && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS) && this.minecraft.options.keySprint.isDown()) {
         this.setSprinting(true);
      }

      boolean var6;
      if (this.isSprinting()) {
         var6 = !this.input.hasForwardImpulse() || !var5;
         boolean var7 = var6 || this.horizontalCollision || this.isInWater() && !this.isUnderWater();
         if (this.isSwimming()) {
            if (!this.onGround && !this.input.shiftKeyDown && var6 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (var7) {
            this.setSprinting(false);
         }
      }

      var6 = false;
      if (this.abilities.mayfly) {
         if (this.minecraft.gameMode.isAlwaysFlying()) {
            if (!this.abilities.flying) {
               this.abilities.flying = true;
               var6 = true;
               this.onUpdateAbilities();
            }
         } else if (!var1 && this.input.jumping && !var4) {
            if (this.jumpTriggerTime == 0) {
               this.jumpTriggerTime = 7;
            } else if (!this.isSwimming()) {
               this.abilities.flying = !this.abilities.flying;
               var6 = true;
               this.onUpdateAbilities();
               this.jumpTriggerTime = 0;
            }
         }
      }

      if (this.input.jumping && !var6 && !var1 && !this.abilities.flying && !this.isPassenger()) {
         ItemStack var8 = this.getItemBySlot(EquipmentSlot.CHEST);
         if (var8.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(var8) && this.tryToStartFallFlying()) {
            this.connection.send((Packet)(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING)));
         }
      }

      this.wasFallFlying = this.isFallFlying();
      if (this.isInWater() && this.input.shiftKeyDown) {
         this.goDownInWater();
      }

      int var9;
      if (this.isUnderLiquid(FluidTags.WATER)) {
         var9 = this.isSpectator() ? 10 : 1;
         this.waterVisionTime = Mth.clamp(this.waterVisionTime + var9, 0, 600);
      } else if (this.waterVisionTime > 0) {
         this.isUnderLiquid(FluidTags.WATER);
         this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
      }

      if (this.abilities.flying && this.isControlledCamera()) {
         var9 = 0;
         if (this.input.shiftKeyDown) {
            --var9;
         }

         if (this.input.jumping) {
            ++var9;
         }

         if (var9 != 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)((float)var9 * this.abilities.getFlyingSpeed() * 3.0F), 0.0D));
         }
      }

      if (this.isRidingJumpable()) {
         PlayerRideableJumping var10 = (PlayerRideableJumping)this.getVehicle();
         if (this.jumpRidingTicks < 0) {
            ++this.jumpRidingTicks;
            if (this.jumpRidingTicks == 0) {
               this.jumpRidingScale = 0.0F;
            }
         }

         if (var1 && !this.input.jumping) {
            this.jumpRidingTicks = -10;
            var10.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0F));
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
      if (this.onGround && this.abilities.flying && !this.minecraft.gameMode.isAlwaysFlying()) {
         this.abilities.flying = false;
         this.onUpdateAbilities();
      }

   }

   private void handleNetherPortalClient() {
      this.oPortalTime = this.portalTime;
      if (this.isInsidePortal) {
         if (this.minecraft.screen != null && !this.minecraft.screen.isPauseScreen()) {
            if (this.minecraft.screen instanceof AbstractContainerScreen) {
               this.closeContainer();
            }

            this.minecraft.setScreen((Screen)null);
         }

         if (this.portalTime == 0.0F) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F));
         }

         this.portalTime += 0.0125F;
         if (this.portalTime >= 1.0F) {
            this.portalTime = 1.0F;
         }

         this.isInsidePortal = false;
      } else if (this.hasEffect(MobEffects.CONFUSION) && this.getEffect(MobEffects.CONFUSION).getDuration() > 60) {
         this.portalTime += 0.006666667F;
         if (this.portalTime > 1.0F) {
            this.portalTime = 1.0F;
         }
      } else {
         if (this.portalTime > 0.0F) {
            this.portalTime -= 0.05F;
         }

         if (this.portalTime < 0.0F) {
            this.portalTime = 0.0F;
         }
      }

      this.processDimensionDelay();
   }

   public void rideTick() {
      super.rideTick();
      this.handsBusy = false;
      if (this.getVehicle() instanceof Boat) {
         Boat var1 = (Boat)this.getVehicle();
         var1.setInput(this.input.left, this.input.right, this.input.up, this.input.down);
         this.handsBusy |= this.input.left || this.input.right || this.input.up || this.input.down;
      }

   }

   public boolean isHandsBusy() {
      return this.handsBusy;
   }

   @Nullable
   public MobEffectInstance removeEffectNoUpdate(@Nullable MobEffect var1) {
      if (var1 == MobEffects.CONFUSION) {
         this.oPortalTime = 0.0F;
         this.portalTime = 0.0F;
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
         Vec3 var4 = var3.add((double)var1, 0.0D, (double)var2);
         Vec3 var5 = new Vec3((double)var1, 0.0D, (double)var2);
         float var6 = this.getSpeed();
         float var7 = (float)var5.lengthSqr();
         float var11;
         if (var7 <= 0.001F) {
            Vec2 var8 = this.input.getMoveVector();
            float var9 = var6 * var8.x;
            float var10 = var6 * var8.y;
            var11 = Mth.sin(this.yRot * 0.017453292F);
            float var12 = Mth.cos(this.yRot * 0.017453292F);
            var5 = new Vec3((double)(var9 * var12 - var10 * var11), var5.y, (double)(var10 * var12 + var9 * var11));
            var7 = (float)var5.lengthSqr();
            if (var7 <= 0.001F) {
               return;
            }
         }

         float var40 = Mth.fastInvSqrt(var7);
         Vec3 var41 = var5.scale((double)var40);
         Vec3 var42 = this.getForward();
         var11 = (float)(var42.x * var41.x + var42.z * var41.z);
         if (var11 >= -0.15F) {
            CollisionContext var43 = CollisionContext.of(this);
            BlockPos var13 = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
            BlockState var14 = this.level.getBlockState(var13);
            if (var14.getCollisionShape(this.level, var13, var43).isEmpty()) {
               var13 = var13.above();
               BlockState var15 = this.level.getBlockState(var13);
               if (var15.getCollisionShape(this.level, var13, var43).isEmpty()) {
                  float var16 = 7.0F;
                  float var17 = 1.2F;
                  if (this.hasEffect(MobEffects.JUMP)) {
                     var17 += (float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.75F;
                  }

                  float var18 = Math.max(var6 * 7.0F, 1.0F / var40);
                  Vec3 var20 = var4.add(var41.scale((double)var18));
                  float var21 = this.getBbWidth();
                  float var22 = this.getBbHeight();
                  AABB var23 = (new AABB(var3, var20.add(0.0D, (double)var22, 0.0D))).inflate((double)var21, 0.0D, (double)var21);
                  Vec3 var19 = var3.add(0.0D, 0.5099999904632568D, 0.0D);
                  var20 = var20.add(0.0D, 0.5099999904632568D, 0.0D);
                  Vec3 var24 = var41.cross(new Vec3(0.0D, 1.0D, 0.0D));
                  Vec3 var25 = var24.scale((double)(var21 * 0.5F));
                  Vec3 var26 = var19.subtract(var25);
                  Vec3 var27 = var20.subtract(var25);
                  Vec3 var28 = var19.add(var25);
                  Vec3 var29 = var20.add(var25);
                  Iterator var30 = this.level.getCollisions(this, var23, Collections.emptySet()).flatMap((var0) -> {
                     return var0.toAabbs().stream();
                  }).iterator();
                  float var32 = Float.MIN_VALUE;

                  label73:
                  while(var30.hasNext()) {
                     AABB var34 = (AABB)var30.next();
                     if (var34.intersects(var26, var27) || var34.intersects(var28, var29)) {
                        var32 = (float)var34.maxY;
                        Vec3 var31 = var34.getCenter();
                        BlockPos var35 = new BlockPos(var31);
                        int var36 = 1;

                        while(true) {
                           if ((float)var36 >= var17) {
                              break label73;
                           }

                           BlockPos var37 = var35.above(var36);
                           BlockState var38 = this.level.getBlockState(var37);
                           VoxelShape var33;
                           if (!(var33 = var38.getCollisionShape(this.level, var37, var43)).isEmpty()) {
                              var32 = (float)var33.max(Direction.Axis.Y) + (float)var37.getY();
                              if ((double)var32 - this.getY() > (double)var17) {
                                 return;
                              }
                           }

                           if (var36 > 1) {
                              var13 = var13.above();
                              BlockState var39 = this.level.getBlockState(var13);
                              if (!var39.getCollisionShape(this.level, var13, var43).isEmpty()) {
                                 return;
                              }
                           }

                           ++var36;
                        }
                     }
                  }

                  if (var32 != Float.MIN_VALUE) {
                     float var44 = (float)((double)var32 - this.getY());
                     if (var44 > 0.5F && var44 <= var17) {
                        this.autoJumpTime = 1;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean canAutoJump() {
      return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && (double)this.getBlockJumpFactor() >= 1.0D;
   }

   private boolean isMoving() {
      Vec2 var1 = this.input.getMoveVector();
      return var1.x != 0.0F || var1.y != 0.0F;
   }

   private boolean hasEnoughImpulseToStartSprinting() {
      double var1 = 0.8D;
      return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.8D;
   }

   public float getWaterVision() {
      if (!this.isUnderLiquid(FluidTags.WATER)) {
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
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.AMBIENT, 1.0F, 1.0F, false);
            this.minecraft.getSoundManager().play(new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(this));
         }

         if (var1 && !var2) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.wasUnderwater;
      }
   }
}
