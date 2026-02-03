package net.minecraft.world.entity.decoration;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ItemFrame extends HangingEntity {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM;
   private static final EntityDataAccessor<Integer> DATA_ROTATION;
   public static final int NUM_ROTATIONS = 8;
   private static final float DEPTH = 0.0625F;
   private static final float WIDTH = 0.75F;
   private static final float HEIGHT = 0.75F;
   private static final byte DEFAULT_ROTATION = 0;
   private static final float DEFAULT_DROP_CHANCE = 1.0F;
   private static final boolean DEFAULT_INVISIBLE = false;
   private static final boolean DEFAULT_FIXED = false;
   private float dropChance;
   private boolean fixed;

   public ItemFrame(final EntityType<? extends ItemFrame> type, final Level level) {
      super(type, level);
      this.dropChance = 1.0F;
      this.fixed = false;
      this.setInvisible(false);
   }

   public ItemFrame(final Level level, final BlockPos pos, final Direction direction) {
      this(EntityType.ITEM_FRAME, level, pos, direction);
   }

   public ItemFrame(final EntityType<? extends ItemFrame> type, final Level level, final BlockPos pos, final Direction direction) {
      super(type, level, pos);
      this.dropChance = 1.0F;
      this.fixed = false;
      this.setDirection(direction);
      this.setInvisible(false);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_ITEM, ItemStack.EMPTY);
      entityData.define(DATA_ROTATION, 0);
   }

   protected void setDirection(final Direction direction) {
      Objects.requireNonNull(direction);
      super.setDirectionRaw(direction);
      if (direction.getAxis().isHorizontal()) {
         this.setXRot(0.0F);
         this.setYRot((float)(direction.get2DDataValue() * 90));
      } else {
         this.setXRot((float)(-90 * direction.getAxisDirection().getStep()));
         this.setYRot(0.0F);
      }

      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
      this.recalculateBoundingBox();
   }

   protected final void recalculateBoundingBox() {
      super.recalculateBoundingBox();
      this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
   }

   protected AABB calculateBoundingBox(final BlockPos blockPos, final Direction direction) {
      return this.createBoundingBox(blockPos, direction, this.hasFramedMap());
   }

   protected AABB getPopBox() {
      return this.createBoundingBox(this.pos, this.getDirection(), false);
   }

   private AABB createBoundingBox(final BlockPos blockPos, final Direction direction, final boolean hasFramedMap) {
      float shiftToBlockWall = 0.46875F;
      Vec3 position = Vec3.atCenterOf(blockPos).relative(direction, -0.46875);
      float width = hasFramedMap ? 1.0F : 0.75F;
      float height = hasFramedMap ? 1.0F : 0.75F;
      Direction.Axis axis = direction.getAxis();
      double xSize = axis == Direction.Axis.X ? 0.0625 : (double)width;
      double ySize = axis == Direction.Axis.Y ? 0.0625 : (double)height;
      double zSize = axis == Direction.Axis.Z ? 0.0625 : (double)width;
      return AABB.ofSize(position, xSize, ySize, zSize);
   }

   public boolean survives() {
      if (this.fixed) {
         return true;
      } else if (this.hasLevelCollision(this.getPopBox())) {
         return false;
      } else {
         BlockState state = this.level().getBlockState(this.pos.relative(this.getDirection().getOpposite()));
         return state.isSolid() || this.getDirection().getAxis().isHorizontal() && DiodeBlock.isDiode(state) ? this.canCoexist(true) : false;
      }
   }

   public void move(final MoverType moverType, final Vec3 delta) {
      if (!this.fixed) {
         super.move(moverType, delta);
      }

   }

   public void push(final double xa, final double ya, final double za) {
      if (!this.fixed) {
         super.push(xa, ya, za);
      }

   }

   public void kill(final ServerLevel level) {
      this.removeFramedMap(this.getItem());
      super.kill(level);
   }

   private boolean shouldDamageDropItem(final DamageSource source) {
      return !source.is(DamageTypeTags.IS_EXPLOSION) && !this.getItem().isEmpty();
   }

   private static boolean canHurtWhenFixed(final DamageSource source) {
      return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.isCreativePlayer();
   }

   public boolean hurtClient(final DamageSource source) {
      if (this.fixed && !canHurtWhenFixed(source)) {
         return false;
      } else {
         return !this.isInvulnerableToBase(source);
      }
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (!this.fixed) {
         if (this.isInvulnerableToBase(source)) {
            return false;
         } else if (this.shouldDamageDropItem(source)) {
            this.dropItem(level, source.getEntity(), false);
            this.gameEvent(GameEvent.BLOCK_CHANGE, source.getEntity());
            this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
            return true;
         } else {
            return super.hurtServer(level, source, damage);
         }
      } else {
         return canHurtWhenFixed(source) && super.hurtServer(level, source, damage);
      }
   }

   public SoundEvent getRemoveItemSound() {
      return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
   }

   public boolean shouldRenderAtSqrDistance(final double distance) {
      double size = 16.0;
      size *= 64.0 * getViewScale();
      return distance < size * size;
   }

   public void dropItem(final ServerLevel level, final @Nullable Entity causedBy) {
      this.playSound(this.getBreakSound(), 1.0F, 1.0F);
      this.dropItem(level, causedBy, true);
      this.gameEvent(GameEvent.BLOCK_CHANGE, causedBy);
   }

   public SoundEvent getBreakSound() {
      return SoundEvents.ITEM_FRAME_BREAK;
   }

   public void playPlacementSound() {
      this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
   }

   public SoundEvent getPlaceSound() {
      return SoundEvents.ITEM_FRAME_PLACE;
   }

   private void dropItem(final ServerLevel level, final @Nullable Entity causedBy, final boolean withFrame) {
      if (!this.fixed) {
         ItemStack itemStack = this.getItem();
         this.setItem(ItemStack.EMPTY);
         if (!(Boolean)level.getGameRules().get(GameRules.ENTITY_DROPS)) {
            if (causedBy == null) {
               this.removeFramedMap(itemStack);
            }

         } else {
            if (causedBy instanceof Player) {
               Player player = (Player)causedBy;
               if (player.hasInfiniteMaterials()) {
                  this.removeFramedMap(itemStack);
                  return;
               }
            }

            if (withFrame) {
               this.spawnAtLocation(level, this.getFrameItemStack());
            }

            if (!itemStack.isEmpty()) {
               itemStack = itemStack.copy();
               this.removeFramedMap(itemStack);
               if (this.random.nextFloat() < this.dropChance) {
                  this.spawnAtLocation(level, itemStack);
               }
            }

         }
      }
   }

   private void removeFramedMap(final ItemStack itemStack) {
      MapId mapId = this.getFramedMapId(itemStack);
      if (mapId != null) {
         MapItemSavedData mapItemSavedData = MapItem.getSavedData(mapId, this.level());
         if (mapItemSavedData != null) {
            mapItemSavedData.removedFromFrame(this.pos, this.getId());
         }
      }

   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM);
   }

   public @Nullable MapId getFramedMapId(final ItemStack itemStack) {
      return (MapId)itemStack.get(DataComponents.MAP_ID);
   }

   public boolean hasFramedMap() {
      return this.getItem().has(DataComponents.MAP_ID);
   }

   public void setItem(final ItemStack itemStack) {
      this.setItem(itemStack, true);
   }

   public void setItem(ItemStack itemStack, final boolean updateNeighbours) {
      if (!itemStack.isEmpty()) {
         itemStack = itemStack.copyWithCount(1);
      }

      this.onItemChanged(itemStack);
      this.getEntityData().set(DATA_ITEM, itemStack);
      if (!itemStack.isEmpty()) {
         this.playSound(this.getAddItemSound(), 1.0F, 1.0F);
      }

      if (updateNeighbours && this.pos != null) {
         this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.ITEM_FRAME_ADD_ITEM;
   }

   public @Nullable SlotAccess getSlot(final int slot) {
      return slot == 0 ? SlotAccess.of(this::getItem, this::setItem) : super.getSlot(slot);
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      super.onSyncedDataUpdated(accessor);
      if (accessor.equals(DATA_ITEM)) {
         this.onItemChanged(this.getItem());
      }

   }

   private void onItemChanged(final ItemStack item) {
      this.recalculateBoundingBox();
   }

   public int getRotation() {
      return (Integer)this.getEntityData().get(DATA_ROTATION);
   }

   public void setRotation(final int rotation) {
      this.setRotation(rotation, true);
   }

   private void setRotation(final int rotation, final boolean updateNeighbours) {
      this.getEntityData().set(DATA_ROTATION, rotation % 8);
      if (updateNeighbours && this.pos != null) {
         this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      ItemStack currentItem = this.getItem();
      if (!currentItem.isEmpty()) {
         output.store("Item", ItemStack.CODEC, currentItem);
      }

      output.putByte("ItemRotation", (byte)this.getRotation());
      output.putFloat("ItemDropChance", this.dropChance);
      output.store("Facing", Direction.LEGACY_ID_CODEC, this.getDirection());
      output.putBoolean("Invisible", this.isInvisible());
      output.putBoolean("Fixed", this.fixed);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      ItemStack itemStack = (ItemStack)input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
      ItemStack currentItem = this.getItem();
      if (!currentItem.isEmpty() && !ItemStack.matches(itemStack, currentItem)) {
         this.removeFramedMap(currentItem);
      }

      this.setItem(itemStack, false);
      this.setRotation(input.getByteOr("ItemRotation", (byte)0), false);
      this.dropChance = input.getFloatOr("ItemDropChance", 1.0F);
      this.setDirection((Direction)input.read("Facing", Direction.LEGACY_ID_CODEC).orElse(Direction.DOWN));
      this.setInvisible(input.getBooleanOr("Invisible", false));
      this.fixed = input.getBooleanOr("Fixed", false);
   }

   public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
      ItemStack itemStack = player.getItemInHand(hand);
      boolean frameHasItem = !this.getItem().isEmpty();
      boolean hasHeldItem = !itemStack.isEmpty();
      if (this.fixed) {
         return InteractionResult.PASS;
      } else if (!player.level().isClientSide()) {
         if (!frameHasItem) {
            if (hasHeldItem && !this.isRemoved()) {
               MapItemSavedData data = MapItem.getSavedData(itemStack, this.level());
               if (data != null && data.isTrackedCountOverLimit(256)) {
                  return InteractionResult.FAIL;
               } else {
                  this.setItem(itemStack);
                  this.gameEvent(GameEvent.BLOCK_CHANGE, player);
                  itemStack.consume(1, player);
                  return InteractionResult.SUCCESS;
               }
            } else {
               return InteractionResult.PASS;
            }
         } else {
            this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
            this.gameEvent(GameEvent.BLOCK_CHANGE, player);
            return InteractionResult.SUCCESS;
         }
      } else {
         return (InteractionResult)(!frameHasItem && !hasHeldItem ? InteractionResult.PASS : InteractionResult.SUCCESS);
      }
   }

   public SoundEvent getRotateItemSound() {
      return SoundEvents.ITEM_FRAME_ROTATE_ITEM;
   }

   public int getAnalogOutput() {
      return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(final ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, this.getDirection().get3DDataValue(), this.getPos());
   }

   public void recreateFromPacket(final ClientboundAddEntityPacket packet) {
      super.recreateFromPacket(packet);
      this.setDirection(Direction.from3DDataValue(packet.getData()));
   }

   public ItemStack getPickResult() {
      ItemStack framedStack = this.getItem();
      return framedStack.isEmpty() ? this.getFrameItemStack() : framedStack.copy();
   }

   protected ItemStack getFrameItemStack() {
      return new ItemStack(Items.ITEM_FRAME);
   }

   public float getVisualRotationYInDegrees() {
      Direction frameDirection = this.getDirection();
      int rotationCorrection = frameDirection.getAxis().isVertical() ? 90 * frameDirection.getAxisDirection().getStep() : 0;
      return (float)Mth.wrapDegrees(180 + frameDirection.get2DDataValue() * 90 + this.getRotation() * 45 + rotationCorrection);
   }

   static {
      DATA_ITEM = SynchedEntityData.<ItemStack>defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
      DATA_ROTATION = SynchedEntityData.<Integer>defineId(ItemFrame.class, EntityDataSerializers.INT);
   }
}
