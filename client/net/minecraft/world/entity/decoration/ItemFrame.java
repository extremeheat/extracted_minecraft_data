package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ItemFrame extends HangingEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
   public static final int NUM_ROTATIONS = 8;
   private float dropChance = 1.0F;
   private boolean fixed;

   public ItemFrame(EntityType<? extends ItemFrame> var1, Level var2) {
      super(var1, var2);
   }

   public ItemFrame(Level var1, BlockPos var2, Direction var3) {
      this(EntityType.ITEM_FRAME, var1, var2, var3);
   }

   public ItemFrame(EntityType<? extends ItemFrame> var1, Level var2, BlockPos var3, Direction var4) {
      super(var1, var2, var3);
      this.setDirection(var4);
   }

   @Override
   protected float getEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.0F;
   }

   @Override
   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
      this.getEntityData().define(DATA_ROTATION, 0);
   }

   @Override
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

   @Override
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
         switch(var15) {
            case X:
               var9 = 1.0;
               break;
            case Y:
               var11 = 1.0;
               break;
            case Z:
               var13 = 1.0;
         }

         var9 /= 32.0;
         var11 /= 32.0;
         var13 /= 32.0;
         this.setBoundingBox(new AABB(var3 - var9, var5 - var11, var7 - var13, var3 + var9, var5 + var11, var7 + var13));
      }
   }

   @Override
   public boolean survives() {
      if (this.fixed) {
         return true;
      } else if (!this.level.noCollision(this)) {
         return false;
      } else {
         BlockState var1 = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
         return var1.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(var1)
            ? this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty()
            : false;
      }
   }

   @Override
   public void move(MoverType var1, Vec3 var2) {
      if (!this.fixed) {
         super.move(var1, var2);
      }
   }

   @Override
   public void push(double var1, double var3, double var5) {
      if (!this.fixed) {
         super.push(var1, var3, var5);
      }
   }

   @Override
   public float getPickRadius() {
      return 0.0F;
   }

   @Override
   public void kill() {
      this.removeFramedMap(this.getItem());
      super.kill();
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.fixed) {
         return var1 != DamageSource.OUT_OF_WORLD && !var1.isCreativePlayer() ? false : super.hurt(var1, var2);
      } else if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (!var1.isExplosion() && !this.getItem().isEmpty()) {
         if (!this.level.isClientSide) {
            this.dropItem(var1.getEntity(), false);
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

   @Override
   public int getWidth() {
      return 12;
   }

   @Override
   public int getHeight() {
      return 12;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = 16.0;
      var3 *= 64.0 * getViewScale();
      return var1 < var3 * var3;
   }

   @Override
   public void dropItem(@Nullable Entity var1) {
      this.playSound(this.getBreakSound(), 1.0F, 1.0F);
      this.dropItem(var1, true);
   }

   public SoundEvent getBreakSound() {
      return SoundEvents.ITEM_FRAME_BREAK;
   }

   @Override
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
         if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (var1 == null) {
               this.removeFramedMap(var3);
            }
         } else {
            if (var1 instanceof Player var4 && var4.getAbilities().instabuild) {
               this.removeFramedMap(var3);
               return;
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
      this.getFramedMapId().ifPresent(var1x -> {
         MapItemSavedData var2 = MapItem.getSavedData(var1x, this.level);
         if (var2 != null) {
            var2.removedFromFrame(this.pos, this.getId());
            var2.setDirty(true);
         }
      });
      var1.setEntityRepresentation(null);
   }

   public ItemStack getItem() {
      return this.getEntityData().get(DATA_ITEM);
   }

   public OptionalInt getFramedMapId() {
      ItemStack var1 = this.getItem();
      if (var1.is(Items.FILLED_MAP)) {
         Integer var2 = MapItem.getMapId(var1);
         if (var2 != null) {
            return OptionalInt.of(var2);
         }
      }

      return OptionalInt.empty();
   }

   public boolean hasFramedMap() {
      return this.getFramedMapId().isPresent();
   }

   public void setItem(ItemStack var1) {
      this.setItem(var1, true);
   }

   public void setItem(ItemStack var1, boolean var2) {
      if (!var1.isEmpty()) {
         var1 = var1.copy();
         var1.setCount(1);
      }

      this.onItemChanged(var1);
      this.getEntityData().set(DATA_ITEM, var1);
      if (!var1.isEmpty()) {
         this.playSound(this.getAddItemSound(), 1.0F, 1.0F);
      }

      if (var2 && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }
   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.ITEM_FRAME_ADD_ITEM;
   }

   @Override
   public SlotAccess getSlot(int var1) {
      return var1 == 0 ? new SlotAccess() {
         @Override
         public ItemStack get() {
            return ItemFrame.this.getItem();
         }

         @Override
         public boolean set(ItemStack var1) {
            ItemFrame.this.setItem(var1);
            return true;
         }
      } : super.getSlot(var1);
   }

   @Override
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
      return this.getEntityData().get(DATA_ROTATION);
   }

   public void setRotation(int var1) {
      this.setRotation(var1, true);
   }

   private void setRotation(int var1, boolean var2) {
      this.getEntityData().set(DATA_ROTATION, var1 % 8);
      if (var2 && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (!this.getItem().isEmpty()) {
         var1.put("Item", this.getItem().save(new CompoundTag()));
         var1.putByte("ItemRotation", (byte)this.getRotation());
         var1.putFloat("ItemDropChance", this.dropChance);
      }

      var1.putByte("Facing", (byte)this.direction.get3DDataValue());
      var1.putBoolean("Invisible", this.isInvisible());
      var1.putBoolean("Fixed", this.fixed);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      CompoundTag var2 = var1.getCompound("Item");
      if (var2 != null && !var2.isEmpty()) {
         ItemStack var3 = ItemStack.of(var2);
         if (var3.isEmpty()) {
            LOGGER.warn("Unable to load item from: {}", var2);
         }

         ItemStack var4 = this.getItem();
         if (!var4.isEmpty() && !ItemStack.matches(var3, var4)) {
            this.removeFramedMap(var4);
         }

         this.setItem(var3, false);
         this.setRotation(var1.getByte("ItemRotation"), false);
         if (var1.contains("ItemDropChance", 99)) {
            this.dropChance = var1.getFloat("ItemDropChance");
         }
      }

      this.setDirection(Direction.from3DDataValue(var1.getByte("Facing")));
      this.setInvisible(var1.getBoolean("Invisible"));
      this.fixed = var1.getBoolean("Fixed");
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      boolean var4 = !this.getItem().isEmpty();
      boolean var5 = !var3.isEmpty();
      if (this.fixed) {
         return InteractionResult.PASS;
      } else if (!this.level.isClientSide) {
         if (!var4) {
            if (var5 && !this.isRemoved()) {
               if (var3.is(Items.FILLED_MAP)) {
                  MapItemSavedData var6 = MapItem.getSavedData(var3, this.level);
                  if (var6 != null && var6.isTrackedCountOverLimit(256)) {
                     return InteractionResult.FAIL;
                  }
               }

               this.setItem(var3);
               if (!var1.getAbilities().instabuild) {
                  var3.shrink(1);
               }
            }
         } else {
            this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
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

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.setDirection(Direction.from3DDataValue(var1.getData()));
   }

   @Override
   public ItemStack getPickResult() {
      ItemStack var1 = this.getItem();
      return var1.isEmpty() ? this.getFrameItemStack() : var1.copy();
   }

   protected ItemStack getFrameItemStack() {
      return new ItemStack(Items.ITEM_FRAME);
   }

   @Override
   public float getVisualRotationYInDegrees() {
      Direction var1 = this.getDirection();
      int var2 = var1.getAxis().isVertical() ? 90 * var1.getAxisDirection().getStep() : 0;
      return (float)Mth.wrapDegrees(180 + var1.get2DDataValue() * 90 + this.getRotation() * 45 + var2);
   }
}
