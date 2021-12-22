package net.minecraft.world.inventory;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
      this.addSlot(new Slot(this.repairSlots, 0, 49, 19) {
         public boolean mayPlace(ItemStack var1) {
            return var1.isDamageableItem() || var1.method_87(Items.ENCHANTED_BOOK) || var1.isEnchanted();
         }
      });
      this.addSlot(new Slot(this.repairSlots, 1, 49, 40) {
         public boolean mayPlace(ItemStack var1) {
            return var1.isDamageableItem() || var1.method_87(Items.ENCHANTED_BOOK) || var1.isEnchanted();
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
            byte var2 = 0;
            int var4 = var2 + this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(0));
            var4 += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(1));
            if (var4 > 0) {
               int var3x = (int)Math.ceil((double)var4 / 2.0D);
               return var3x + var1.random.nextInt(var3x);
            } else {
               return 0;
            }
         }

         private int getExperienceFromItem(ItemStack var1) {
            int var2 = 0;
            Map var3x = EnchantmentHelper.getEnchantments(var1);
            Iterator var4 = var3x.entrySet().iterator();

            while(var4.hasNext()) {
               Entry var5 = (Entry)var4.next();
               Enchantment var6 = (Enchantment)var5.getKey();
               Integer var7 = (Integer)var5.getValue();
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
      ItemStack var1 = this.repairSlots.getItem(0);
      ItemStack var2 = this.repairSlots.getItem(1);
      boolean var3 = !var1.isEmpty() || !var2.isEmpty();
      boolean var4 = !var1.isEmpty() && !var2.isEmpty();
      if (!var3) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         boolean var5 = !var1.isEmpty() && !var1.method_87(Items.ENCHANTED_BOOK) && !var1.isEnchanted() || !var2.isEmpty() && !var2.method_87(Items.ENCHANTED_BOOK) && !var2.isEnchanted();
         if (var1.getCount() > 1 || var2.getCount() > 1 || !var4 && var5) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
            return;
         }

         byte var7 = 1;
         int var6;
         ItemStack var8;
         if (var4) {
            if (!var1.method_87(var2.getItem())) {
               this.resultSlots.setItem(0, ItemStack.EMPTY);
               this.broadcastChanges();
               return;
            }

            Item var9 = var1.getItem();
            int var10 = var9.getMaxDamage() - var1.getDamageValue();
            int var11 = var9.getMaxDamage() - var2.getDamageValue();
            int var12 = var10 + var11 + var9.getMaxDamage() * 5 / 100;
            var6 = Math.max(var9.getMaxDamage() - var12, 0);
            var8 = this.mergeEnchants(var1, var2);
            if (!var8.isDamageableItem()) {
               if (!ItemStack.matches(var1, var2)) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.broadcastChanges();
                  return;
               }

               var7 = 2;
            }
         } else {
            boolean var13 = !var1.isEmpty();
            var6 = var13 ? var1.getDamageValue() : var2.getDamageValue();
            var8 = var13 ? var1 : var2;
         }

         this.resultSlots.setItem(0, this.removeNonCurses(var8, var6, var7));
      }

      this.broadcastChanges();
   }

   private ItemStack mergeEnchants(ItemStack var1, ItemStack var2) {
      ItemStack var3 = var1.copy();
      Map var4 = EnchantmentHelper.getEnchantments(var2);
      Iterator var5 = var4.entrySet().iterator();

      while(true) {
         Entry var6;
         Enchantment var7;
         do {
            if (!var5.hasNext()) {
               return var3;
            }

            var6 = (Entry)var5.next();
            var7 = (Enchantment)var6.getKey();
         } while(var7.isCurse() && EnchantmentHelper.getItemEnchantmentLevel(var7, var3) != 0);

         var3.enchant(var7, (Integer)var6.getValue());
      }
   }

   private ItemStack removeNonCurses(ItemStack var1, int var2, int var3) {
      ItemStack var4 = var1.copy();
      var4.removeTagKey("Enchantments");
      var4.removeTagKey("StoredEnchantments");
      if (var2 > 0) {
         var4.setDamageValue(var2);
      } else {
         var4.removeTagKey("Damage");
      }

      var4.setCount(var3);
      Map var5 = (Map)EnchantmentHelper.getEnchantments(var1).entrySet().stream().filter((var0) -> {
         return ((Enchantment)var0.getKey()).isCurse();
      }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      EnchantmentHelper.setEnchantments(var5, var4);
      var4.setRepairCost(0);
      if (var4.method_87(Items.ENCHANTED_BOOK) && var5.size() == 0) {
         var4 = new ItemStack(Items.BOOK);
         if (var1.hasCustomHoverName()) {
            var4.setHoverName(var1.getHoverName());
         }
      }

      for(int var6 = 0; var6 < var5.size(); ++var6) {
         var4.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(var4.getBaseRepairCost()));
      }

      return var4;
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
            var4.set(ItemStack.EMPTY);
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
