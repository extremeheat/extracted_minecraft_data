package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ItemFrame extends HangingEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<ItemStack> DATA_ITEM;
   private static final EntityDataAccessor<Integer> DATA_ROTATION;
   public static final int NUM_ROTATIONS = 8;
   private float dropChance;
   private boolean fixed;

   public ItemFrame(EntityType<? extends ItemFrame> var1, Level var2) {
      super(var1, var2);
      this.dropChance = 1.0F;
   }

   public ItemFrame(Level var1, BlockPos var2, Direction var3) {
      this(EntityType.ITEM_FRAME, var1, var2, var3);
   }

   public ItemFrame(EntityType<? extends ItemFrame> var1, Level var2, BlockPos var3, Direction var4) {
      super(var1, var2, var3);
      this.dropChance = 1.0F;
      this.setDirection(var4);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM, ItemStack.EMPTY);
      var1.define(DATA_ROTATION, 0);
   }

   protected void setDirection(Direction var1) {
      Validate.notNull(var1);
      this.direction = var1;
      if (var1.getAxis().isHorizontal()) {
         this.setXRot(0.0F);
         this.setYRot((float)(this.direction.get2DDataValue() * 90));
      } else {
         this.setXRot((float)(-90 * var1.getAxisDirection().getStep()));
         this.setYRot(0.0F);
      }

      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double var1 = 0.46875;
         double var3 = (double)this.pos.getX() + 0.5 - (double)this.direction.getStepX() * 0.46875;
         double var5 = (double)this.pos.getY() + 0.5 - (double)this.direction.getStepY() * 0.46875;
         double var7 = (double)this.pos.getZ() + 0.5 - (double)this.direction.getStepZ() * 0.46875;
         this.setPosRaw(var3, var5, var7);
         double var9 = (double)this.getWidth();
         double var11 = (double)this.getHeight();
         double var13 = (double)this.getWidth();
         Direction.Axis var15 = this.direction.getAxis();
         switch (var15) {
            case X -> var9 = 1.0;
            case Y -> var11 = 1.0;
            case Z -> var13 = 1.0;
         }

         var9 /= 32.0;
         var11 /= 32.0;
         var13 /= 32.0;
         this.setBoundingBox(new AABB(var3 - var9, var5 - var11, var7 - var13, var3 + var9, var5 + var11, var7 + var13));
      }
   }

   public boolean survives() {
      if (this.fixed) {
         return true;
      } else if (!this.level().noCollision(this)) {
         return false;
      } else {
         BlockState var1 = this.level().getBlockState(this.pos.relative(this.direction.getOpposite()));
         return var1.isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(var1) ? this.level().getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty() : false;
      }
   }

   public void move(MoverType var1, Vec3 var2) {
      if (!this.fixed) {
         super.move(var1, var2);
      }

   }

   public void push(double var1, double var3, double var5) {
      if (!this.fixed) {
         super.push(var1, var3, var5);
      }

   }

   public void kill() {
      this.removeFramedMap(this.getItem());
      super.kill();
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.fixed) {
         return !var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !var1.isCreativePlayer() ? false : super.hurt(var1, var2);
      } else if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (!var1.is(DamageTypeTags.IS_EXPLOSION) && !this.getItem().isEmpty()) {
         if (!this.level().isClientSide) {
            this.dropItem(var1.getEntity(), false);
            this.gameEvent(GameEvent.BLOCK_CHANGE, var1.getEntity());
            this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.hurt(var1, var2);
      }
   }

   public SoundEvent getRemoveItemSound() {
      return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
   }

   public int getWidth() {
      return 12;
   }

   public int getHeight() {
      return 12;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = 16.0;
      var3 *= 64.0 * getViewScale();
      return var1 < var3 * var3;
   }

   public void dropItem(@Nullable Entity var1) {
      this.playSound(this.getBreakSound(), 1.0F, 1.0F);
      this.dropItem(var1, true);
      this.gameEvent(GameEvent.BLOCK_CHANGE, var1);
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

   private void dropItem(@Nullable Entity var1, boolean var2) {
      if (!this.fixed) {
         ItemStack var3 = this.getItem();
         this.setItem(ItemStack.EMPTY);
         if (!this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (var1 == null) {
               this.removeFramedMap(var3);
            }

         } else {
            if (var1 instanceof Player) {
               Player var4 = (Player)var1;
               if (var4.hasInfiniteMaterials()) {
                  this.removeFramedMap(var3);
                  return;
               }
            }

            if (var2) {
               this.spawnAtLocation(this.getFrameItemStack());
            }

            if (!var3.isEmpty()) {
               var3 = var3.copy();
               this.removeFramedMap(var3);
               if (this.random.nextFloat() < this.dropChance) {
                  this.spawnAtLocation(var3);
               }
            }

         }
      }
   }

   private void removeFramedMap(ItemStack var1) {
      MapId var2 = this.getFramedMapId();
      if (var2 != null) {
         MapItemSavedData var3 = MapItem.getSavedData(var2, this.level());
         if (var3 != null) {
            var3.removedFromFrame(this.pos, this.getId());
            var3.setDirty(true);
         }
      }

      var1.setEntityRepresentation((Entity)null);
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM);
   }

   @Nullable
   public MapId getFramedMapId() {
      return (MapId)this.getItem().get(DataComponents.MAP_ID);
   }

   public boolean hasFramedMap() {
      return this.getItem().has(DataComponents.MAP_ID);
   }

   public void setItem(ItemStack var1) {
      this.setItem(var1, true);
   }

   public void setItem(ItemStack var1, boolean var2) {
      if (!var1.isEmpty()) {
         var1 = var1.copyWithCount(1);
      }

      this.onItemChanged(var1);
      this.getEntityData().set(DATA_ITEM, var1);
      if (!var1.isEmpty()) {
         this.playSound(this.getAddItemSound(), 1.0F, 1.0F);
      }

      if (var2 && this.pos != null) {
         this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.ITEM_FRAME_ADD_ITEM;
   }

   public SlotAccess getSlot(int var1) {
      return var1 == 0 ? SlotAccess.of(this::getItem, this::setItem) : super.getSlot(var1);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (var1.equals(DATA_ITEM)) {
         this.onItemChanged(this.getItem());
      }

   }

   private void onItemChanged(ItemStack var1) {
      if (!var1.isEmpty() && var1.getFrame() != this) {
         var1.setEntityRepresentation(this);
      }

      this.recalculateBoundingBox();
   }

   public int getRotation() {
      return (Integer)this.getEntityData().get(DATA_ROTATION);
   }

   public void setRotation(int var1) {
      this.setRotation(var1, true);
   }

   private void setRotation(int var1, boolean var2) {
      this.getEntityData().set(DATA_ROTATION, var1 % 8);
      if (var2 && this.pos != null) {
         this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (!this.getItem().isEmpty()) {
         var1.put("Item", this.getItem().save(this.registryAccess()));
         var1.putByte("ItemRotation", (byte)this.getRotation());
         var1.putFloat("ItemDropChance", this.dropChance);
      }

      var1.putByte("Facing", (byte)this.direction.get3DDataValue());
      var1.putBoolean("Invisible", this.isInvisible());
      var1.putBoolean("Fixed", this.fixed);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      ItemStack var2;
      if (var1.contains("Item", 10)) {
         CompoundTag var3 = var1.getCompound("Item");
         var2 = (ItemStack)ItemStack.parse(this.registryAccess(), var3).orElse(ItemStack.EMPTY);
      } else {
         var2 = ItemStack.EMPTY;
      }

      ItemStack var4 = this.getItem();
      if (!var4.isEmpty() && !ItemStack.matches(var2, var4)) {
         this.removeFramedMap(var4);
      }

      this.setItem(var2, false);
      if (!var2.isEmpty()) {
         this.setRotation(var1.getByte("ItemRotation"), false);
         if (var1.contains("ItemDropChance", 99)) {
            this.dropChance = var1.getFloat("ItemDropChance");
         }
      }

      this.setDirection(Direction.from3DDataValue(var1.getByte("Facing")));
      this.setInvisible(var1.getBoolean("Invisible"));
      this.fixed = var1.getBoolean("Fixed");
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      boolean var4 = !this.getItem().isEmpty();
      boolean var5 = !var3.isEmpty();
      if (this.fixed) {
         return InteractionResult.PASS;
      } else if (!this.level().isClientSide) {
         if (!var4) {
            if (var5 && !this.isRemoved()) {
               if (var3.is(Items.FILLED_MAP)) {
                  MapItemSavedData var6 = MapItem.getSavedData(var3, this.level());
                  if (var6 != null && var6.isTrackedCountOverLimit(256)) {
                     return InteractionResult.FAIL;
                  }
               }

               this.setItem(var3);
               this.gameEvent(GameEvent.BLOCK_CHANGE, var1);
               var3.consume(1, var1);
            }
         } else {
            this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
            this.gameEvent(GameEvent.BLOCK_CHANGE, var1);
         }

         return InteractionResult.CONSUME;
      } else {
         return !var4 && !var5 ? InteractionResult.PASS : InteractionResult.SUCCESS;
      }
   }

   public SoundEvent getRotateItemSound() {
      return SoundEvents.ITEM_FRAME_ROTATE_ITEM;
   }

   public int getAnalogOutput() {
      return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.setDirection(Direction.from3DDataValue(var1.getData()));
   }

   public ItemStack getPickResult() {
      ItemStack var1 = this.getItem();
      return var1.isEmpty() ? this.getFrameItemStack() : var1.copy();
   }

   protected ItemStack getFrameItemStack() {
      return new ItemStack(Items.ITEM_FRAME);
   }

   public float getVisualRotationYInDegrees() {
      Direction var1 = this.getDirection();
      int var2 = var1.getAxis().isVertical() ? 90 * var1.getAxisDirection().getStep() : 0;
      return (float)Mth.wrapDegrees(180 + var1.get2DDataValue() * 90 + this.getRotation() * 45 + var2);
   }

   static {
      DATA_ITEM = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
      DATA_ROTATION = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
   }
}
