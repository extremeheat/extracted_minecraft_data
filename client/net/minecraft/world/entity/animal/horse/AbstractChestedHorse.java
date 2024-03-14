package net.minecraft.world.entity.animal.horse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public abstract class AbstractChestedHorse extends AbstractHorse {
   private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData.defineId(AbstractChestedHorse.class, EntityDataSerializers.BOOLEAN);
   public static final int INV_CHEST_COUNT = 15;
   private final EntityDimensions babyDimensions;

   protected AbstractChestedHorse(EntityType<? extends AbstractChestedHorse> var1, Level var2) {
      super(var1, var2);
      this.canGallop = false;
      this.babyDimensions = var1.getDimensions()
         .withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, var1.getHeight() - 0.15625F, 0.0F))
         .scale(0.5F);
   }

   @Override
   protected void randomizeAttributes(RandomSource var1) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)generateMaxHealth(var1::nextInt));
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_CHEST, false);
   }

   public static AttributeSupplier.Builder createBaseChestedHorseAttributes() {
      return createBaseHorseAttributes().add(Attributes.MOVEMENT_SPEED, 0.17499999701976776).add(Attributes.JUMP_STRENGTH, 0.5);
   }

   public boolean hasChest() {
      return this.entityData.get(DATA_ID_CHEST);
   }

   public void setChest(boolean var1) {
      this.entityData.set(DATA_ID_CHEST, var1);
   }

   @Override
   protected int getInventorySize() {
      return this.hasChest() ? 16 : super.getInventorySize();
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? this.babyDimensions : super.getDefaultDimensions(var1);
   }

   @Override
   protected void dropEquipment() {
      super.dropEquipment();
      if (this.hasChest()) {
         if (!this.level().isClientSide) {
            this.spawnAtLocation(Blocks.CHEST);
         }

         this.setChest(false);
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("ChestedHorse", this.hasChest());
      if (this.hasChest()) {
         ListTag var2 = new ListTag();

         for(int var3 = 1; var3 < this.inventory.getContainerSize(); ++var3) {
            ItemStack var4 = this.inventory.getItem(var3);
            if (!var4.isEmpty()) {
               CompoundTag var5 = new CompoundTag();
               var5.putByte("Slot", (byte)(var3 - 1));
               var2.add(var4.save(this.registryAccess(), var5));
            }
         }

         var1.put("Items", var2);
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setChest(var1.getBoolean("ChestedHorse"));
      this.createInventory();
      if (this.hasChest()) {
         ListTag var2 = var1.getList("Items", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            CompoundTag var4 = var2.getCompound(var3);
            int var5 = var4.getByte("Slot") & 255;
            if (var5 < this.inventory.getContainerSize() - 1) {
               this.inventory.setItem(var5 + 1, ItemStack.parse(this.registryAccess(), var4).orElse(ItemStack.EMPTY));
            }
         }
      }

      this.syncSaddleToClients();
   }

   @Override
   public SlotAccess getSlot(int var1) {
      return var1 == 499 ? new SlotAccess() {
         @Override
         public ItemStack get() {
            return AbstractChestedHorse.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
         }

         @Override
         public boolean set(ItemStack var1) {
            if (var1.isEmpty()) {
               if (AbstractChestedHorse.this.hasChest()) {
                  AbstractChestedHorse.this.setChest(false);
                  AbstractChestedHorse.this.createInventory();
               }

               return true;
            } else if (var1.is(Items.CHEST)) {
               if (!AbstractChestedHorse.this.hasChest()) {
                  AbstractChestedHorse.this.setChest(true);
                  AbstractChestedHorse.this.createInventory();
               }

               return true;
            } else {
               return false;
            }
         }
      } : super.getSlot(var1);
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      boolean var3 = !this.isBaby() && this.isTamed() && var1.isSecondaryUseActive();
      if (!this.isVehicle() && !var3) {
         ItemStack var4 = var1.getItemInHand(var2);
         if (!var4.isEmpty()) {
            if (this.isFood(var4)) {
               return this.fedFood(var1, var4);
            }

            if (!this.isTamed()) {
               this.makeMad();
               return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (!this.hasChest() && var4.is(Items.CHEST)) {
               this.equipChest(var1, var4);
               return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
         }

         return super.mobInteract(var1, var2);
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   private void equipChest(Player var1, ItemStack var2) {
      this.setChest(true);
      this.playChestEquipsSound();
      var2.consume(1, var1);
      this.createInventory();
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return 5;
   }
}
