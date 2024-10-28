package net.minecraft.world.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Iterator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class GrindstoneMenu extends AbstractContainerMenu {
   public static final int MAX_NAME_LENGTH = 35;
   public static final int INPUT_SLOT = 0;
   public static final int ADDITIONAL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   private final Container resultSlots;
   final Container repairSlots;
   private final ContainerLevelAccess access;

   public GrindstoneMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public GrindstoneMenu(int var1, Inventory var2, final ContainerLevelAccess var3) {
      super(MenuType.GRINDSTONE, var1);
      this.resultSlots = new ResultContainer();
      this.repairSlots = new SimpleContainer(2) {
         public void setChanged() {
            super.setChanged();
            GrindstoneMenu.this.slotsChanged(this);
         }
      };
      this.access = var3;
      this.addSlot(new Slot(this, this.repairSlots, 0, 49, 19) {
         public boolean mayPlace(ItemStack var1) {
            return var1.isDamageableItem() || EnchantmentHelper.hasAnyEnchantments(var1);
         }
      });
      this.addSlot(new Slot(this, this.repairSlots, 1, 49, 40) {
         public boolean mayPlace(ItemStack var1) {
            return var1.isDamageableItem() || EnchantmentHelper.hasAnyEnchantments(var1);
         }
      });
      this.addSlot(new Slot(this.resultSlots, 2, 129, 34) {
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         public void onTake(Player var1, ItemStack var2) {
            var3.execute((var1x, var2x) -> {
               if (var1x instanceof ServerLevel) {
                  ExperienceOrb.award((ServerLevel)var1x, Vec3.atCenterOf(var2x), this.getExperienceAmount(var1x));
               }

               var1x.levelEvent(1042, var2x, 0);
            });
            GrindstoneMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
            GrindstoneMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
         }

         private int getExperienceAmount(Level var1) {
            int var2 = 0;
            var2 += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(0));
            var2 += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(1));
            if (var2 > 0) {
               int var3x = (int)Math.ceil((double)var2 / 2.0);
               return var3x + var1.random.nextInt(var3x);
            } else {
               return 0;
            }
         }

         private int getExperienceFromItem(ItemStack var1) {
            int var2 = 0;
            ItemEnchantments var3x = EnchantmentHelper.getEnchantmentsForCrafting(var1);
            Iterator var4 = var3x.entrySet().iterator();

            while(var4.hasNext()) {
               Object2IntMap.Entry var5 = (Object2IntMap.Entry)var4.next();
               Enchantment var6 = (Enchantment)((Holder)var5.getKey()).value();
               int var7 = var5.getIntValue();
               if (!var6.isCurse()) {
                  var2 += var6.getMinCost(var7);
               }
            }

            return var2;
         }
      });

      int var4;
      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var2, var4, 8 + var4 * 18, 142));
      }

   }

   public void slotsChanged(Container var1) {
      super.slotsChanged(var1);
      if (var1 == this.repairSlots) {
         this.createResult();
      }

   }

   private void createResult() {
      this.resultSlots.setItem(0, this.computeResult(this.repairSlots.getItem(0), this.repairSlots.getItem(1)));
      this.broadcastChanges();
   }

   private ItemStack computeResult(ItemStack var1, ItemStack var2) {
      boolean var3 = !var1.isEmpty() || !var2.isEmpty();
      if (!var3) {
         return ItemStack.EMPTY;
      } else if (var1.getCount() <= 1 && var2.getCount() <= 1) {
         boolean var4 = !var1.isEmpty() && !var2.isEmpty();
         if (!var4) {
            ItemStack var5 = !var1.isEmpty() ? var1 : var2;
            return !EnchantmentHelper.hasAnyEnchantments(var5) ? ItemStack.EMPTY : this.removeNonCursesFrom(var5.copy());
         } else {
            return this.mergeItems(var1, var2);
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private ItemStack mergeItems(ItemStack var1, ItemStack var2) {
      if (!var1.is(var2.getItem())) {
         return ItemStack.EMPTY;
      } else {
         int var3 = Math.max(var1.getMaxDamage(), var2.getMaxDamage());
         int var4 = var1.getMaxDamage() - var1.getDamageValue();
         int var5 = var2.getMaxDamage() - var2.getDamageValue();
         int var6 = var4 + var5 + var3 * 5 / 100;
         byte var7 = 1;
         if (!var1.isDamageableItem()) {
            if (var1.getMaxStackSize() < 2 || !ItemStack.matches(var1, var2)) {
               return ItemStack.EMPTY;
            }

            var7 = 2;
         }

         ItemStack var8 = var1.copyWithCount(var7);
         if (var8.isDamageableItem()) {
            var8.set(DataComponents.MAX_DAMAGE, var3);
            var8.setDamageValue(Math.max(var3 - var6, 0));
         }

         this.mergeEnchantsFrom(var8, var2);
         return this.removeNonCursesFrom(var8);
      }
   }

   private void mergeEnchantsFrom(ItemStack var1, ItemStack var2) {
      EnchantmentHelper.updateEnchantments(var1, (var1x) -> {
         ItemEnchantments var2x = EnchantmentHelper.getEnchantmentsForCrafting(var2);
         Iterator var3 = var2x.entrySet().iterator();

         while(true) {
            Object2IntMap.Entry var4;
            Enchantment var5;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (Object2IntMap.Entry)var3.next();
               var5 = (Enchantment)((Holder)var4.getKey()).value();
            } while(var5.isCurse() && var1x.getLevel(var5) != 0);

            var1x.upgrade(var5, var4.getIntValue());
         }
      });
   }

   private ItemStack removeNonCursesFrom(ItemStack var1) {
      ItemEnchantments var2 = EnchantmentHelper.updateEnchantments(var1, (var0) -> {
         var0.removeIf((var0x) -> {
            return !((Enchantment)var0x.value()).isCurse();
         });
      });
      if (var1.is(Items.ENCHANTED_BOOK) && var2.isEmpty()) {
         var1 = var1.transmuteCopy(Items.BOOK, var1.getCount());
      }

      int var3 = 0;

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         var3 = AnvilMenu.calculateIncreasedRepairCost(var3);
      }

      var1.set(DataComponents.REPAIR_COST, var3);
      return var1;
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> {
         this.clearContainer(var1, this.repairSlots);
      });
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.GRINDSTONE);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         ItemStack var6 = this.repairSlots.getItem(0);
         ItemStack var7 = this.repairSlots.getItem(1);
         if (var2 == 2) {
            if (!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 != 0 && var2 != 1) {
            if (!var6.isEmpty() && !var7.isEmpty()) {
               if (var2 >= 3 && var2 < 30) {
                  if (!this.moveItemStackTo(var5, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(var5, 0, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 3, 39, false)) {
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
      }

      return var3;
   }
}
