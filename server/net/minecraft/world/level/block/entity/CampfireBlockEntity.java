package net.minecraft.world.level.block.entity;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CampfireBlockEntity extends BlockEntity implements Clearable, TickableBlockEntity {
   private final NonNullList<ItemStack> items;
   private final int[] cookingProgress;
   private final int[] cookingTime;

   public CampfireBlockEntity() {
      super(BlockEntityType.CAMPFIRE);
      this.items = NonNullList.withSize(4, ItemStack.EMPTY);
      this.cookingProgress = new int[4];
      this.cookingTime = new int[4];
   }

   public void tick() {
      boolean var1 = (Boolean)this.getBlockState().getValue(CampfireBlock.LIT);
      boolean var2 = this.level.isClientSide;
      if (var2) {
         if (var1) {
            this.makeParticles();
         }

      } else {
         if (var1) {
            this.cook();
         } else {
            for(int var3 = 0; var3 < this.items.size(); ++var3) {
               if (this.cookingProgress[var3] > 0) {
                  this.cookingProgress[var3] = Mth.clamp(this.cookingProgress[var3] - 2, 0, this.cookingTime[var3]);
               }
            }
         }

      }
   }

   private void cook() {
      for(int var1 = 0; var1 < this.items.size(); ++var1) {
         ItemStack var2 = (ItemStack)this.items.get(var1);
         if (!var2.isEmpty()) {
            int var10002 = this.cookingProgress[var1]++;
            if (this.cookingProgress[var1] >= this.cookingTime[var1]) {
               SimpleContainer var3 = new SimpleContainer(new ItemStack[]{var2});
               ItemStack var4 = (ItemStack)this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, var3, this.level).map((var1x) -> {
                  return var1x.assemble(var3);
               }).orElse(var2);
               BlockPos var5 = this.getBlockPos();
               Containers.dropItemStack(this.level, (double)var5.getX(), (double)var5.getY(), (double)var5.getZ(), var4);
               this.items.set(var1, ItemStack.EMPTY);
               this.markUpdated();
            }
         }
      }

   }

   private void makeParticles() {
      Level var1 = this.getLevel();
      if (var1 != null) {
         BlockPos var2 = this.getBlockPos();
         Random var3 = var1.random;
         int var4;
         if (var3.nextFloat() < 0.11F) {
            for(var4 = 0; var4 < var3.nextInt(2) + 2; ++var4) {
               CampfireBlock.makeParticles(var1, var2, (Boolean)this.getBlockState().getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
         }

         var4 = ((Direction)this.getBlockState().getValue(CampfireBlock.FACING)).get2DDataValue();

         for(int var5 = 0; var5 < this.items.size(); ++var5) {
            if (!((ItemStack)this.items.get(var5)).isEmpty() && var3.nextFloat() < 0.2F) {
               Direction var6 = Direction.from2DDataValue(Math.floorMod(var5 + var4, 4));
               float var7 = 0.3125F;
               double var8 = (double)var2.getX() + 0.5D - (double)((float)var6.getStepX() * 0.3125F) + (double)((float)var6.getClockWise().getStepX() * 0.3125F);
               double var10 = (double)var2.getY() + 0.5D;
               double var12 = (double)var2.getZ() + 0.5D - (double)((float)var6.getStepZ() * 0.3125F) + (double)((float)var6.getClockWise().getStepZ() * 0.3125F);

               for(int var14 = 0; var14 < 4; ++var14) {
                  var1.addParticle(ParticleTypes.SMOKE, var8, var10, var12, 0.0D, 5.0E-4D, 0.0D);
               }
            }
         }

      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public void load(BlockState var1, CompoundTag var2) {
      super.load(var1, var2);
      this.items.clear();
      ContainerHelper.loadAllItems(var2, this.items);
      int[] var3;
      if (var2.contains("CookingTimes", 11)) {
         var3 = var2.getIntArray("CookingTimes");
         System.arraycopy(var3, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, var3.length));
      }

      if (var2.contains("CookingTotalTimes", 11)) {
         var3 = var2.getIntArray("CookingTotalTimes");
         System.arraycopy(var3, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, var3.length));
      }

   }

   public CompoundTag save(CompoundTag var1) {
      this.saveMetadataAndItems(var1);
      var1.putIntArray("CookingTimes", this.cookingProgress);
      var1.putIntArray("CookingTotalTimes", this.cookingTime);
      return var1;
   }

   private CompoundTag saveMetadataAndItems(CompoundTag var1) {
      super.save(var1);
      ContainerHelper.saveAllItems(var1, this.items, true);
      return var1;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 13, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.saveMetadataAndItems(new CompoundTag());
   }

   public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack var1) {
      return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(new ItemStack[]{var1}), this.level);
   }

   public boolean placeFood(ItemStack var1, int var2) {
      for(int var3 = 0; var3 < this.items.size(); ++var3) {
         ItemStack var4 = (ItemStack)this.items.get(var3);
         if (var4.isEmpty()) {
            this.cookingTime[var3] = var2;
            this.cookingProgress[var3] = 0;
            this.items.set(var3, var1.split(1));
            this.markUpdated();
            return true;
         }
      }

      return false;
   }

   private void markUpdated() {
      this.setChanged();
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public void clearContent() {
      this.items.clear();
   }

   public void dowse() {
      if (this.level != null) {
         if (!this.level.isClientSide) {
            Containers.dropContents(this.level, this.getBlockPos(), this.getItems());
         }

         this.markUpdated();
      }

   }
}
