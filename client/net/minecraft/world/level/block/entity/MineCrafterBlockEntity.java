package net.minecraft.world.level.block.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MineCraftingMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MineTravellingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;

public class MineCrafterBlockEntity extends BaseContainerBlockEntity implements StackedContentsCompatible {
   public static final int MAX_WORLD_EFFECTS = 50;
   public static final int EXPERIENCE_DONATION_CHUNK_SIZE = 20;
   public static final int WIN_EXPERIENCE = 60;
   protected NonNullList<ItemStack> items;
   @Nullable
   private BlockPos mineTravellingBlockPos;
   private int dropRewardsInTicks;

   public MineCrafterBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.MINE_CRAFTER, var1, var2);
      this.items = NonNullList.<ItemStack>withSize(99, ItemStack.EMPTY);
      this.dropRewardsInTicks = 0;
   }

   public static int experienceRequiredForLevel(int var0) {
      if (var0 >= 15) {
         return 2000 + (var0 - 10) * 400;
      } else {
         return var0 >= 5 ? 400 + (var0 - 5) * 160 : 100 + Math.max(0, var0) * 60;
      }
   }

   public static int craftingSlotsForLevel(int var0) {
      return Math.min(50, 3 + var0);
   }

   public static int randomCraftingSlotsForLevel(int var0) {
      return 1 + Mth.floor((float)craftingSlotsForLevel(var0) / 3.0F);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items, var2);
      this.mineTravellingBlockPos = (BlockPos)var1.read("mine_travelling_block_pos", BlockPos.CODEC).filter(Level::isInSpawnableBounds).orElse((Object)null);
      this.dropRewardsInTicks = var1.getIntOr("drop_rewards_in_ticks", 0);
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.storeNullable("mine_travelling_block_pos", BlockPos.CODEC, this.mineTravellingBlockPos);
      var1.putInt("drop_rewards_in_ticks", this.dropRewardsInTicks);
      ContainerHelper.saveAllItems(var1, this.items, var2);
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, MineCrafterBlockEntity var3) {
      if (var0 instanceof ServerLevel var4) {
         boolean var5 = false;
         if (var3.dropRewardsInTicks > 0) {
            --var3.dropRewardsInTicks;
            if (var3.dropRewardsInTicks == 0) {
               var3.dispenseRewards(var4, var1);
               var3.setChanged();
            }
         }

         if (var3.mineTravellingBlockPos != null) {
            ItemStack var6 = (ItemStack)var3.items.getFirst();
            if (var6 != null) {
               boolean var7 = var6.get(DataComponents.MINE_ACTIVE) != null;
               Boolean var8 = (Boolean)var6.get(DataComponents.MINE_COMPLETED);
               if (var7 && var8 == null) {
                  BlockEntity var10 = var0.getBlockEntity(var3.mineTravellingBlockPos);
                  if (var10 instanceof MineTravellingBlockEntity) {
                     MineTravellingBlockEntity var9 = (MineTravellingBlockEntity)var10;
                     ServerLevel var11 = var4.theGame().getLevel(var9.getTargetDimension());
                     if (var11 != null && var11.isMine() && var11.isMineCompleted()) {
                        var6.set(DataComponents.MINE_COMPLETED, var11.isMineWon());
                        var6.remove(DataComponents.MINE_ACTIVE);
                        var0.destroyBlock(var3.mineTravellingBlockPos, false, (Entity)null);
                        var3.mineTravellingBlockPos = null;
                        var3.dropRewardsInTicks = 20;
                        var5 = true;
                     }
                  }
               }
            } else {
               var3.mineTravellingBlockPos = null;
               var5 = true;
            }
         }

         if (var5) {
            setChanged(var0, var1, var2);
         }

      }
   }

   public int getContainerSize() {
      return this.items.size();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   public void setItem(int var1, ItemStack var2) {
      this.items.set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
      this.setChanged();
   }

   public boolean canPlaceItem(int var1, ItemStack var2) {
      return var1 != 0;
   }

   public void fillStackedContents(StackedItemContents var1) {
      for(ItemStack var3 : this.items) {
         var1.accountStack(var3);
      }

   }

   protected Component getDefaultName() {
      return Component.translatable("container.mine_crafter");
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new MineCraftingMenu(var1, var2, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this, (var1x) -> this.makeMineTravellingBlock(this.getLevel(), this.getBlockPos(), var1x), this.getAdditionalData());
   }

   private void makeMineTravellingBlock(Level var1, BlockPos var2, ResourceKey<LevelStem> var3) {
      this.mineTravellingBlockPos = var2.above();
      MineTravellingBlock.createBlock(var1, var2.above(), var3, false);
      this.setChanged();
   }

   public static List<Integer> getAdditionalData(ServerLevel var0) {
      int var1 = var0.getMineCrafterLevel();
      int var2 = var0.getMineCrafterExp();
      return List.of(var1, var2);
   }

   public List<Integer> getAdditionalData() {
      Level var2 = this.getLevel();
      if (var2 instanceof ServerLevel var1) {
         return getAdditionalData(var1);
      } else {
         return super.getAdditionalData();
      }
   }

   public void preRemoveSideEffects(BlockPos var1, BlockState var2) {
      Level var4 = this.getLevel();
      if (var4 instanceof ServerLevel var3) {
         int var6 = randomCraftingSlotsForLevel(var3.getMineCrafterLevel());

         for(int var5 = 0; var5 < var6 + 1; ++var5) {
            this.items.set(var5, ItemStack.EMPTY);
         }
      }

      super.preRemoveSideEffects(var1, var2);
   }

   public void dispenseRewards(ServerLevel var1, BlockPos var2) {
      ItemStack var3 = this.getItem(0);
      Boolean var4 = (Boolean)var3.get(DataComponents.MINE_COMPLETED);
      if (var4 != null) {
         boolean var5 = var4;
         ItemStack var6 = var3.copyWithCount(1);
         ItemEntity var7 = new ItemEntity(var1, (double)var2.getX(), (double)var2.getY() + 2.5 + 9.999999747378752E-6, (double)var2.getZ(), var6);
         Vec3 var8 = new Vec3((var1.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0, var1.random.nextDouble() * 0.4, (var1.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0);
         var7.push(var8);
         var1.addFreshEntity(var7);
         var1.playSound((Entity)null, var2, var5 ? SoundEvents.UI_LOOM_TAKE_RESULT : SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
         var1.dropRewards(var2);
         this.getItems().forEach((var0) -> var0.setCount(0));
      }
   }

   // $FF: synthetic method
   @Nullable
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
