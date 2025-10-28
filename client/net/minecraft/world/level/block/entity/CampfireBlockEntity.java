package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class CampfireBlockEntity extends BlockEntity implements Clearable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int BURN_COOL_SPEED = 2;
   private static final int NUM_SLOTS = 4;
   private final NonNullList<ItemStack> items;
   private final int[] cookingProgress;
   private final int[] cookingTime;

   public CampfireBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.CAMPFIRE, var1, var2);
      this.items = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
      this.cookingProgress = new int[4];
      this.cookingTime = new int[4];
   }

   public static void cookTick(ServerLevel var0, BlockPos var1, BlockState var2, CampfireBlockEntity var3, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> var4) {
      boolean var5 = false;

      for(int var6 = 0; var6 < var3.items.size(); ++var6) {
         ItemStack var7 = var3.items.get(var6);
         if (!var7.isEmpty()) {
            var5 = true;
            int var10002 = var3.cookingProgress[var6]++;
            if (var3.cookingProgress[var6] >= var3.cookingTime[var6]) {
               SingleRecipeInput var8 = new SingleRecipeInput(var7);
               ItemStack var9 = (ItemStack)var4.getRecipeFor(var8, var0).map((var2x) -> ((CampfireCookingRecipe)var2x.value()).assemble(var8, var0.registryAccess())).orElse(var7);
               if (var9.isItemEnabled(var0.enabledFeatures())) {
                  Containers.dropItemStack(var0, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var9);
                  var3.items.set(var6, ItemStack.EMPTY);
                  var0.sendBlockUpdated(var1, var2, var2, 3);
                  var0.gameEvent(GameEvent.BLOCK_CHANGE, var1, GameEvent.Context.of(var2));
               }
            }
         }
      }

      if (var5) {
         setChanged(var0, var1, var2);
      }

   }

   public static void cooldownTick(Level var0, BlockPos var1, BlockState var2, CampfireBlockEntity var3) {
      boolean var4 = false;

      for(int var5 = 0; var5 < var3.items.size(); ++var5) {
         if (var3.cookingProgress[var5] > 0) {
            var4 = true;
            var3.cookingProgress[var5] = Mth.clamp(var3.cookingProgress[var5] - 2, 0, var3.cookingTime[var5]);
         }
      }

      if (var4) {
         setChanged(var0, var1, var2);
      }

   }

   public static void particleTick(Level var0, BlockPos var1, BlockState var2, CampfireBlockEntity var3) {
      RandomSource var4 = var0.random;
      if (var4.nextFloat() < 0.11F) {
         for(int var5 = 0; var5 < var4.nextInt(2) + 2; ++var5) {
            CampfireBlock.makeParticles(var0, var1, (Boolean)var2.getValue(CampfireBlock.SIGNAL_FIRE), false);
         }
      }

      int var16 = ((Direction)var2.getValue(CampfireBlock.FACING)).get2DDataValue();

      for(int var6 = 0; var6 < var3.items.size(); ++var6) {
         if (!((ItemStack)var3.items.get(var6)).isEmpty() && var4.nextFloat() < 0.2F) {
            Direction var7 = Direction.from2DDataValue(Math.floorMod(var6 + var16, 4));
            float var8 = 0.3125F;
            double var9 = (double)var1.getX() + 0.5 - (double)((float)var7.getStepX() * 0.3125F) + (double)((float)var7.getClockWise().getStepX() * 0.3125F);
            double var11 = (double)var1.getY() + 0.5;
            double var13 = (double)var1.getZ() + 0.5 - (double)((float)var7.getStepZ() * 0.3125F) + (double)((float)var7.getClockWise().getStepZ() * 0.3125F);

            for(int var15 = 0; var15 < 4; ++var15) {
               var0.addParticle(ParticleTypes.SMOKE, var9, var11, var13, 0.0, 5.0E-4, 0.0);
            }
         }
      }

   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.items.clear();
      ContainerHelper.loadAllItems(var1, this.items);
      var1.getIntArray("CookingTimes").ifPresentOrElse((var1x) -> System.arraycopy(var1x, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, var1x.length)), () -> Arrays.fill(this.cookingProgress, 0));
      var1.getIntArray("CookingTotalTimes").ifPresentOrElse((var1x) -> System.arraycopy(var1x, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, var1x.length)), () -> Arrays.fill(this.cookingTime, 0));
   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      ContainerHelper.saveAllItems(var1, this.items, true);
      var1.putIntArray("CookingTimes", this.cookingProgress);
      var1.putIntArray("CookingTotalTimes", this.cookingTime);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1);
         ContainerHelper.saveAllItems(var3, this.items, true);
         return var3.buildResult();
      }
   }

   public boolean placeFood(ServerLevel var1, @Nullable LivingEntity var2, ItemStack var3) {
      for(int var4 = 0; var4 < this.items.size(); ++var4) {
         ItemStack var5 = this.items.get(var4);
         if (var5.isEmpty()) {
            Optional var6 = var1.recipeAccess().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(var3), var1);
            if (var6.isEmpty()) {
               return false;
            }

            this.cookingTime[var4] = ((CampfireCookingRecipe)((RecipeHolder)var6.get()).value()).cookingTime();
            this.cookingProgress[var4] = 0;
            this.items.set(var4, var3.consumeAndReturn(1, var2));
            var1.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(var2, this.getBlockState()));
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

   public void preRemoveSideEffects(BlockPos var1, BlockState var2) {
      if (this.level != null) {
         Containers.dropContents(this.level, var1, this.getItems());
      }

   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      super.applyImplicitComponents(var1);
      ((ItemContainerContents)var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(this.getItems());
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
   }

   public void removeComponentsFromTag(ValueOutput var1) {
      var1.discard("Items");
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
