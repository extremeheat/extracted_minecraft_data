package net.minecraft.world.inventory;

import java.util.Optional;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CraftingMenu extends RecipeBookMenu<CraftingContainer> {
   public static final int RESULT_SLOT = 0;
   private static final int CRAFT_SLOT_START = 1;
   private static final int CRAFT_SLOT_END = 10;
   private static final int INV_SLOT_START = 10;
   private static final int INV_SLOT_END = 37;
   private static final int USE_ROW_SLOT_START = 37;
   private static final int USE_ROW_SLOT_END = 46;
   private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
   private final ResultContainer resultSlots = new ResultContainer();
   private final ContainerLevelAccess access;
   private final Player player;

   public CraftingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public CraftingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.CRAFTING, var1);
      this.access = var3;
      this.player = var2.player;
      this.addSlot(new ResultSlot(var2.player, this.craftSlots, this.resultSlots, 0, 124, 35));

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 3; ++var5) {
            this.addSlot(new Slot(this.craftSlots, var5 + var4 * 3, 30 + var5 * 18, 17 + var4 * 18));
         }
      }

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(var2, var8 + var6 * 9 + 9, 8 + var8 * 18, 84 + var6 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(var2, var7, 8 + var7 * 18, 142));
      }
   }

   protected static void slotChangedCraftingGrid(AbstractContainerMenu var0, Level var1, Player var2, CraftingContainer var3, ResultContainer var4) {
      if (!var1.isClientSide) {
         ServerPlayer var5 = (ServerPlayer)var2;
         ItemStack var6 = ItemStack.EMPTY;
         Optional var7 = var1.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, var3, var1);
         if (var7.isPresent()) {
            CraftingRecipe var8 = (CraftingRecipe)var7.get();
            if (var4.setRecipeUsed(var1, var5, var8)) {
               ItemStack var9 = var8.assemble(var3, var1.registryAccess());
               if (var9.isItemEnabled(var1.enabledFeatures())) {
                  var6 = var9;
               }
            }
         }

         var4.setItem(0, var6);
         var0.setRemoteSlot(0, var6);
         var5.connection.send(new ClientboundContainerSetSlotPacket(var0.containerId, var0.incrementStateId(), 0, var6));
      }
   }

   @Override
   public void slotsChanged(Container var1) {
      this.access.execute((var1x, var2) -> slotChangedCraftingGrid(this, var1x, this.player, this.craftSlots, this.resultSlots));
   }

   @Override
   public void fillCraftSlotsStackedContents(StackedContents var1) {
      this.craftSlots.fillStackedContents(var1);
   }

   @Override
   public void clearCraftingContent() {
      this.craftSlots.clearContent();
      this.resultSlots.clearContent();
   }

   @Override
   public boolean recipeMatches(Recipe<? super CraftingContainer> var1) {
      return var1.matches(this.craftSlots, this.player.level());
   }

   @Override
   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> this.clearContainer(var1, this.craftSlots));
   }

   @Override
   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.CRAFTING_TABLE);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 0) {
            this.access.execute((var2x, var3x) -> var5.getItem().onCraftedBy(var5, var2x, var1));
            if (!this.moveItemStackTo(var5, 10, 46, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 >= 10 && var2 < 46) {
            if (!this.moveItemStackTo(var5, 1, 10, false)) {
               if (var2 < 37) {
                  if (!this.moveItemStackTo(var5, 37, 46, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.moveItemStackTo(var5, 10, 37, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(var5, 10, 46, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
         if (var2 == 0) {
            var1.drop(var5, false);
         }
      }

      return var3;
   }

   @Override
   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }

   @Override
   public int getResultSlotIndex() {
      return 0;
   }

   @Override
   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   @Override
   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   @Override
   public int getSize() {
      return 10;
   }

   @Override
   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   @Override
   public boolean shouldMoveToInventory(int var1) {
      return var1 != this.getResultSlotIndex();
   }
}
