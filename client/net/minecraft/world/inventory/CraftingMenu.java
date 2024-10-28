package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

public class CraftingMenu extends AbstractCraftingMenu {
   private static final int CRAFTING_GRID_WIDTH = 3;
   private static final int CRAFTING_GRID_HEIGHT = 3;
   public static final int RESULT_SLOT = 0;
   private static final int CRAFT_SLOT_START = 1;
   private static final int CRAFT_SLOT_COUNT = 9;
   private static final int CRAFT_SLOT_END = 10;
   private static final int INV_SLOT_START = 10;
   private static final int INV_SLOT_END = 37;
   private static final int USE_ROW_SLOT_START = 37;
   private static final int USE_ROW_SLOT_END = 46;
   private final ContainerLevelAccess access;
   private final Player player;
   private boolean placingRecipe;

   public CraftingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public CraftingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.CRAFTING, var1, 3, 3);
      this.access = var3;
      this.player = var2.player;
      this.addResultSlot(this.player, 124, 35);
      this.addCraftingGridSlots(30, 17);
      this.addStandardInventorySlots(var2, 8, 84);
   }

   protected static void slotChangedCraftingGrid(AbstractContainerMenu var0, ServerLevel var1, Player var2, CraftingContainer var3, ResultContainer var4, @Nullable RecipeHolder<CraftingRecipe> var5) {
      CraftingInput var6 = var3.asCraftInput();
      ServerPlayer var7 = (ServerPlayer)var2;
      ItemStack var8 = ItemStack.EMPTY;
      Optional var9 = var1.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, var6, var1, (RecipeHolder)var5);
      if (var9.isPresent()) {
         RecipeHolder var10 = (RecipeHolder)var9.get();
         CraftingRecipe var11 = (CraftingRecipe)var10.value();
         if (var4.setRecipeUsed(var7, var10)) {
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

   public void slotsChanged(Container var1) {
      if (!this.placingRecipe) {
         this.access.execute((var1x, var2) -> {
            if (var1x instanceof ServerLevel var3) {
               slotChangedCraftingGrid(this, var3, this.player, this.craftSlots, this.resultSlots, (RecipeHolder)null);
            }

         });
      }

   }

   public void beginPlacingRecipe() {
      this.placingRecipe = true;
   }

   public void finishPlacingRecipe(ServerLevel var1, RecipeHolder<CraftingRecipe> var2) {
      this.placingRecipe = false;
      slotChangedCraftingGrid(this, var1, this.player, this.craftSlots, this.resultSlots, var2);
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

   public Slot getResultSlot() {
      return (Slot)this.slots.get(0);
   }

   public List<Slot> getInputGridSlots() {
      return this.slots.subList(1, 10);
   }

   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   protected Player owner() {
      return this.player;
   }
}
