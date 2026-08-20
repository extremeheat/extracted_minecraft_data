package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BrewingFuel;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class BrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
   private static final int INGREDIENT_SLOT = 3;
   private static final int FUEL_SLOT = 4;
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
   public static final int DATA_BREW_TIME = 0;
   public static final int DATA_FUEL_USES = 1;
   public static final int DATA_TOTAL_BREW_TIME = 2;
   public static final int DATA_TOTAL_FUEL_USES = 3;
   public static final int NUM_DATA_VALUES = 4;
   private static final int DEFAULT_BREW_TIME = 0;
   public static final int BREWING_TIME_SECONDS = 20;
   private static final int DEFAULT_FUEL = 0;
   private static final float DEFAULT_SPEED_MULTIPLIER = 1.0F;
   private static final int DEFAULT_FUEL_USES = 20;
   private static final Component DEFAULT_NAME = Component.translatable("container.brewing");
   private NonNullList<ItemStack> items;
   private int brewTime;
   private int totalBrewTime;
   private boolean[] lastPotionCount;
   private Item ingredient;
   private int fuel;
   private int totalFuel;
   private float speedMultiplier;
   protected final ContainerData dataAccess;
   private final RecipeManager.CachedCheck<BrewingInput, BrewingRecipe> quickCheck;

   public BrewingStandBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityTypes.BREWING_STAND, worldPosition, blockState);
      this.items = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
      this.speedMultiplier = 1.0F;
      this.dataAccess = new ContainerData() {
         {
            Objects.requireNonNull(BrewingStandBlockEntity.this);
         }

         public int get(final int dataId) {
            int var10000;
            switch (dataId) {
               case 0 -> var10000 = BrewingStandBlockEntity.this.brewTime;
               case 1 -> var10000 = BrewingStandBlockEntity.this.fuel;
               case 2 -> var10000 = BrewingStandBlockEntity.this.totalBrewTime;
               case 3 -> var10000 = BrewingStandBlockEntity.this.totalFuel;
               default -> var10000 = 0;
            }

            return var10000;
         }

         public void set(final int dataId, final int value) {
            switch (dataId) {
               case 0 -> BrewingStandBlockEntity.this.brewTime = value;
               case 1 -> BrewingStandBlockEntity.this.fuel = value;
               case 2 -> BrewingStandBlockEntity.this.totalBrewTime = value;
               case 3 -> BrewingStandBlockEntity.this.totalFuel = value;
            }

         }

         public int getCount() {
            return 4;
         }
      };
      this.quickCheck = RecipeManager.<BrewingInput, BrewingRecipe>createCheck(RecipeType.BREWING);
   }

   protected Component getDefaultName() {
      return DEFAULT_NAME;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(final NonNullList<ItemStack> items) {
      this.items = items;
   }

   protected int getUses(final ServerLevel level, final BrewingFuel brewingFuel) {
      return brewingFuel.uses().getInt(this.getLootContext(level), 0);
   }

   protected float getSpeedMultiplier(final ServerLevel level, final BrewingFuel brewingFuel) {
      return brewingFuel.speedMultiplier().getFloat(this.getLootContext(level), 1.0F);
   }

   public static void serverTick(final ServerLevel level, final BlockPos pos, final BlockState selfState, final BrewingStandBlockEntity entity) {
      ItemStack fuel = entity.items.get(4);
      BrewingFuel brewingFuel = (BrewingFuel)fuel.get(DataComponents.BREWING_FUEL);
      if (entity.fuel <= 0 && brewingFuel != null) {
         entity.fuel = entity.getUses(level, brewingFuel);
         entity.totalFuel = entity.fuel;
         entity.speedMultiplier = entity.getSpeedMultiplier(level, brewingFuel);
         fuel.shrink(1);
         setChanged(level, pos, selfState);
      }

      boolean brewable = isBrewable(level, entity);
      boolean isBrewing = entity.brewTime > 0;
      ItemStack ingredient = entity.items.get(3);
      if (isBrewing) {
         --entity.brewTime;
         boolean isDoneBrewing = entity.brewTime == 0;
         if (isDoneBrewing && brewable) {
            doBrew(level, pos, entity);
         } else if (!brewable || !ingredient.is(entity.ingredient)) {
            entity.brewTime = 0;
         }

         setChanged(level, pos, selfState);
      } else if (brewable && entity.fuel > 0) {
         float speedMutliplier = entity.speedMultiplier > 0.0F ? entity.speedMultiplier : 1.0F;
         --entity.fuel;
         entity.brewTime = (int)Math.ceil((double)(400.0F / speedMutliplier));
         entity.totalBrewTime = entity.brewTime;
         entity.ingredient = ingredient.getItem();
         setChanged(level, pos, selfState);
      }

      boolean[] newCount = entity.getPotionBits();
      if (!Arrays.equals(newCount, entity.lastPotionCount)) {
         entity.lastPotionCount = newCount;
         BlockState state = selfState;
         if (!(selfState.getBlock() instanceof BrewingStandBlock)) {
            return;
         }

         for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
            state = (BlockState)state.setValue(BrewingStandBlock.HAS_BOTTLE[i], newCount[i]);
         }

         level.setBlock(pos, state, 2);
      }

   }

   private boolean[] getPotionBits() {
      boolean[] result = new boolean[3];

      for(int potion = 0; potion < 3; ++potion) {
         if (!((ItemStack)this.items.get(potion)).isEmpty()) {
            result[potion] = true;
         }
      }

      return result;
   }

   private static boolean isBrewable(final ServerLevel serverLevel, final BrewingStandBlockEntity entity) {
      NonNullList<ItemStack> items = entity.getItems();
      ItemStack ingredient = items.get(3);
      if (ingredient.isEmpty()) {
         return false;
      } else {
         RecipeManager recipeManager = serverLevel.recipeAccess();
         if (!recipeManager.propertySet(RecipePropertySet.BREWING_REAGENTS).test(ingredient)) {
            return false;
         } else {
            for(int dest = 0; dest < 3; ++dest) {
               ItemStack itemStack = items.get(dest);
               if (!itemStack.isEmpty()) {
                  Optional<RecipeHolder<BrewingRecipe>> recipe = entity.quickCheck.getRecipeFor(new BrewingInput(itemStack, ingredient), serverLevel);
                  if (recipe.isPresent()) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   private static void doBrew(final ServerLevel level, final BlockPos pos, final BrewingStandBlockEntity entity) {
      NonNullList<ItemStack> items = entity.getItems();
      ItemStack ingredient = items.get(3);

      for(int dest = 0; dest < 3; ++dest) {
         ItemStack container = items.get(dest);
         BrewingInput input = new BrewingInput(container, ingredient);
         Optional<RecipeHolder<BrewingRecipe>> recipe = entity.quickCheck.getRecipeFor(input, level);
         items.set(dest, recipe.isPresent() ? ((BrewingRecipe)((RecipeHolder)recipe.get()).value()).assemble(input) : container);
      }

      ItemStackTemplate remainder = ingredient.getItem().getCraftingRemainder();
      ingredient.shrink(1);
      if (remainder != null) {
         if (ingredient.isEmpty()) {
            ingredient = remainder.create();
         } else {
            Containers.dropItemStack(level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), remainder.create());
         }
      }

      items.set(3, ingredient);
      level.levelEvent(1035, pos, 0);
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(input, this.items);
      this.brewTime = input.getIntOr("BrewTime", 0);
      this.totalBrewTime = input.getIntOr("total_brew_time", 400);
      if (this.brewTime > 0) {
         this.ingredient = ((ItemStack)this.items.get(3)).getItem();
      }

      this.fuel = input.getIntOr("Fuel", 0);
      this.totalFuel = input.getIntOr("total_fuel", 20);
      this.speedMultiplier = input.getFloatOr("speed_multiplier", 1.0F);
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      output.putInt("BrewTime", this.brewTime);
      output.putInt("total_brew_time", this.totalBrewTime);
      ContainerHelper.saveAllItems(output, this.items);
      output.putInt("Fuel", this.fuel);
      output.putInt("total_fuel", this.totalFuel);
      output.putFloat("speed_multiplier", this.speedMultiplier);
   }

   public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
      if (slot == 4) {
         return itemStack.has(DataComponents.BREWING_FUEL);
      } else if (this.level == null) {
         return false;
      } else {
         RecipeAccess recipeAccess = this.level.recipeAccess();
         if (slot == 3) {
            return recipeAccess.propertySet(RecipePropertySet.BREWING_REAGENTS).test(itemStack);
         } else {
            return PotionIngredient.isPotionInput(itemStack, recipeAccess) && this.getItem(slot).isEmpty();
         }
      }
   }

   public int[] getSlotsForFace(final Direction direction) {
      if (direction == Direction.UP) {
         return SLOTS_FOR_UP;
      } else {
         return direction == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDES;
      }
   }

   public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
      return this.canPlaceItem(slot, itemStack);
   }

   public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
      return slot == 3 ? itemStack.is(Items.GLASS_BOTTLE) : true;
   }

   protected AbstractContainerMenu createMenu(final int containerId, final Inventory inventory) {
      return new BrewingStandMenu(containerId, inventory, this, this.dataAccess);
   }
}
