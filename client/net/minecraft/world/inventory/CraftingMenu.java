package net.minecraft.world.inventory;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CraftingMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
   public static final int RESULT_SLOT = 0;
   private static final int CRAFT_SLOT_START = 1;
   private static final int CRAFT_SLOT_END = 10;
   private static final int INV_SLOT_START = 10;
   private static final int INV_SLOT_END = 37;
   private static final int USE_ROW_SLOT_START = 37;
   private static final int USE_ROW_SLOT_END = 46;
   private final CraftingContainer craftSlots;
   private final ResultContainer resultSlots;
   private final ContainerLevelAccess access;
   private final Player player;
   private boolean placingRecipe;

   public CraftingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public CraftingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.CRAFTING, var1);
      this.craftSlots = new TransientCraftingContainer(this, 3, 3);
      this.resultSlots = new ResultContainer();
      this.access = var3;
      this.player = var2.player;
      this.addSlot(new ResultSlot(var2.player, this.craftSlots, this.resultSlots, 0, 124, 35));

      int var4;
      int var5;
      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 3; ++var5) {
            this.addSlot(new Slot(this.craftSlots, var5 + var4 * 3, 30 + var5 * 18, 17 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var2, var4, 8 + var4 * 18, 142));
      }

   }

   protected static void slotChangedCraftingGrid(AbstractContainerMenu var0, Level var1, Player var2, CraftingContainer var3, ResultContainer var4, @Nullable RecipeHolder<CraftingRecipe> var5) {
      if (!var1.isClientSide) {
         CraftingInput var6 = var3.asCraftInput();
         ServerPlayer var7 = (ServerPlayer)var2;
         ItemStack var8 = ItemStack.EMPTY;
         Optional var9 = var1.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, var6, var1, (RecipeHolder)var5);
         if (var9.isPresent()) {
            RecipeHolder var10 = (RecipeHolder)var9.get();
            CraftingRecipe var11 = (CraftingRecipe)var10.value();
            if (var4.setRecipeUsed(var1, var7, var10)) {
               ItemStack var12 = var11.assemble(var6, var1.registryAccess());
               if (var12.isItemEnabled(var1.enabledFeatures())) {
                  var8 = var12;
               }
            }
         }

         var4.setItem(0, var8);
         var0.setRemoteSlot(0, var8);
         var7.connection.send(new ClientboundContainerSetSlotPacket(var0.containerId, var0.incrementStateId(), 0, var8));
      }
   }

   public void slotsChanged(Container var1) {
      if (!this.placingRecipe) {
         this.access.execute((var1x, var2) -> {
            slotChangedCraftingGrid(this, var1x, this.player, this.craftSlots, this.resultSlots, (RecipeHolder)null);
         });
      }

   }

   public void beginPlacingRecipe() {
      this.placingRecipe = true;
   }

   public void finishPlacingRecipe(RecipeHolder<CraftingRecipe> var1) {
      this.placingRecipe = false;
      this.access.execute((var2, var3) -> {
         slotChangedCraftingGrid(this, var2, this.player, this.craftSlots, this.resultSlots, var1);
      });
   }

   public void fillCraftSlotsStackedContents(StackedContents var1) {
      this.craftSlots.fillStackedContents(var1);
   }

   public void clearCraftingContent() {
      this.craftSlots.clearContent();
      this.resultSlots.clearContent();
   }

   public boolean recipeMatches(RecipeHolder<CraftingRecipe> var1) {
      return ((CraftingRecipe)var1.value()).matches(this.craftSlots.asCraftInput(), this.player.level());
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> {
         this.clearContainer(var1, this.craftSlots);
      });
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.CRAFTING_TABLE);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 0) {
            this.access.execute((var2x, var3x) -> {
               var5.getItem().onCraftedBy(var5, var2x, var1);
            });
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

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }

   public int getResultSlotIndex() {
      return 0;
   }

   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   public int getSize() {
      return 10;
   }

   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   public boolean shouldMoveToInventory(int var1) {
      return var1 != this.getResultSlotIndex();
   }
}
