package net.minecraft.world.entity.player;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;

public class StackedItemContents {
   private final StackedContents<Holder<Item>> raw = new StackedContents<Holder<Item>>();

   public StackedItemContents() {
      super();
   }

   public void accountSimpleStack(ItemStack var1) {
      if (Inventory.isUsableForCrafting(var1)) {
         this.accountStack(var1);
      }

   }

   public void accountStack(ItemStack var1) {
      this.accountStack(var1, var1.getMaxStackSize());
   }

   public void accountStack(ItemStack var1, int var2) {
      if (!var1.isEmpty()) {
         int var3 = Math.min(var2, var1.getCount());
         this.raw.account(var1.getItemHolder(), var3);
      }

   }

   public boolean canCraft(Recipe<?> var1, @Nullable StackedContents.Output<Holder<Item>> var2) {
      return this.canCraft(var1, 1, var2);
   }

   public boolean canCraft(Recipe<?> var1, int var2, @Nullable StackedContents.Output<Holder<Item>> var3) {
      PlacementInfo var4 = var1.placementInfo();
      return var4.isImpossibleToPlace() ? false : this.canCraft(var4.ingredients(), var2, var3);
   }

   public boolean canCraft(List<? extends StackedContents.IngredientInfo<Holder<Item>>> var1, @Nullable StackedContents.Output<Holder<Item>> var2) {
      return this.canCraft(var1, 1, var2);
   }

   private boolean canCraft(List<? extends StackedContents.IngredientInfo<Holder<Item>>> var1, int var2, @Nullable StackedContents.Output<Holder<Item>> var3) {
      return this.raw.tryPick(var1, var2, var3);
   }

   public int getBiggestCraftableStack(Recipe<?> var1, @Nullable StackedContents.Output<Holder<Item>> var2) {
      return this.getBiggestCraftableStack(var1, 2147483647, var2);
   }

   public int getBiggestCraftableStack(Recipe<?> var1, int var2, @Nullable StackedContents.Output<Holder<Item>> var3) {
      return this.raw.tryPickAll(var1.placementInfo().ingredients(), var2, var3);
   }

   public void clear() {
      this.raw.clear();
   }
}