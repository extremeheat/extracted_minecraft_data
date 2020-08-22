package net.minecraft.world.entity.animal.horse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public abstract class AbstractChestedHorse extends AbstractHorse {
   private static final EntityDataAccessor DATA_ID_CHEST;

   protected AbstractChestedHorse(EntityType var1, Level var2) {
      super(var1, var2);
      this.canGallop = false;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_CHEST, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17499999701976776D);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(0.5D);
   }

   public boolean hasChest() {
      return (Boolean)this.entityData.get(DATA_ID_CHEST);
   }

   public void setChest(boolean var1) {
      this.entityData.set(DATA_ID_CHEST, var1);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 17 : super.getInventorySize();
   }

   public double getRideHeight() {
      return super.getRideHeight() - 0.25D;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.DONKEY_ANGRY;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.hasChest()) {
         if (!this.level.isClientSide) {
            this.spawnAtLocation(Blocks.CHEST);
         }

         this.setChest(false);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("ChestedHorse", this.hasChest());
      if (this.hasChest()) {
         ListTag var2 = new ListTag();

         for(int var3 = 2; var3 < this.inventory.getContainerSize(); ++var3) {
            ItemStack var4 = this.inventory.getItem(var3);
            if (!var4.isEmpty()) {
               CompoundTag var5 = new CompoundTag();
               var5.putByte("Slot", (byte)var3);
               var4.save(var5);
               var2.add(var5);
            }
         }

         var1.put("Items", var2);
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setChest(var1.getBoolean("ChestedHorse"));
      if (this.hasChest()) {
         ListTag var2 = var1.getList("Items", 10);
         this.createInventory();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            CompoundTag var4 = var2.getCompound(var3);
            int var5 = var4.getByte("Slot") & 255;
            if (var5 >= 2 && var5 < this.inventory.getContainerSize()) {
               this.inventory.setItem(var5, ItemStack.of(var4));
            }
         }
      }

      this.updateEquipment();
   }

   public boolean setSlot(int var1, ItemStack var2) {
      if (var1 == 499) {
         if (this.hasChest() && var2.isEmpty()) {
            this.setChest(false);
            this.createInventory();
            return true;
         }

         if (!this.hasChest() && var2.getItem() == Blocks.CHEST.asItem()) {
            this.setChest(true);
            this.createInventory();
            return true;
         }
      }

      return super.setSlot(var1, var2);
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.getItem() instanceof SpawnEggItem) {
         return super.mobInteract(var1, var2);
      } else {
         if (!this.isBaby()) {
            if (this.isTamed() && var1.isSecondaryUseActive()) {
               this.openInventory(var1);
               return true;
            }

            if (this.isVehicle()) {
               return super.mobInteract(var1, var2);
            }
         }

         if (!var3.isEmpty()) {
            boolean var4 = this.handleEating(var1, var3);
            if (!var4) {
               if (!this.isTamed() || var3.getItem() == Items.NAME_TAG) {
                  if (var3.interactEnemy(var1, this, var2)) {
                     return true;
                  } else {
                     this.makeMad();
                     return true;
                  }
               }

               if (!this.hasChest() && var3.getItem() == Blocks.CHEST.asItem()) {
                  this.setChest(true);
                  this.playChestEquipsSound();
                  var4 = true;
                  this.createInventory();
               }

               if (!this.isBaby() && !this.isSaddled() && var3.getItem() == Items.SADDLE) {
                  this.openInventory(var1);
                  return true;
               }
            }

            if (var4) {
               if (!var1.abilities.instabuild) {
                  var3.shrink(1);
               }

               return true;
            }
         }

         if (this.isBaby()) {
            return super.mobInteract(var1, var2);
         } else {
            this.doPlayerRide(var1);
            return true;
         }
      }
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return 5;
   }

   static {
      DATA_ID_CHEST = SynchedEntityData.defineId(AbstractChestedHorse.class, EntityDataSerializers.BOOLEAN);
   }
}
