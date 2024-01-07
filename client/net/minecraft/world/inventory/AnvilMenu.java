package net.minecraft.world.inventory;

import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class AnvilMenu extends ItemCombinerMenu {
   public static final int INPUT_SLOT = 0;
   public static final int ADDITIONAL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean DEBUG_COST = false;
   public static final int MAX_NAME_LENGTH = 50;
   private int repairItemCountCost;
   @Nullable
   private String itemName;
   private final DataSlot cost = DataSlot.standalone();
   private static final int COST_FAIL = 0;
   private static final int COST_BASE = 1;
   private static final int COST_ADDED_BASE = 1;
   private static final int COST_REPAIR_MATERIAL = 1;
   private static final int COST_REPAIR_SACRIFICE = 2;
   private static final int COST_INCOMPATIBLE_PENALTY = 1;
   private static final int COST_RENAME = 1;
   private static final int INPUT_SLOT_X_PLACEMENT = 27;
   private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
   private static final int RESULT_SLOT_X_PLACEMENT = 134;
   private static final int SLOT_Y_PLACEMENT = 47;

   public AnvilMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public AnvilMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.ANVIL, var1, var2, var3);
      this.addDataSlot(this.cost);
   }

   @Override
   protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
      return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, var0 -> true).withSlot(1, 76, 47, var0 -> true).withResultSlot(2, 134, 47).build();
   }

   @Override
   protected boolean isValidBlock(BlockState var1) {
      return var1.is(BlockTags.ANVIL);
   }

   @Override
   protected boolean mayPickup(Player var1, boolean var2) {
      return (var1.getAbilities().instabuild || var1.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
   }

   @Override
   protected void onTake(Player var1, ItemStack var2) {
      if (!var1.getAbilities().instabuild) {
         var1.giveExperienceLevels(-this.cost.get());
      }

      this.inputSlots.setItem(0, ItemStack.EMPTY);
      if (this.repairItemCountCost > 0) {
         ItemStack var3 = this.inputSlots.getItem(1);
         if (!var3.isEmpty() && var3.getCount() > this.repairItemCountCost) {
            var3.shrink(this.repairItemCountCost);
            this.inputSlots.setItem(1, var3);
         } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
         }
      } else {
         this.inputSlots.setItem(1, ItemStack.EMPTY);
      }

      this.cost.set(0);
      this.access.execute((var1x, var2x) -> {
         BlockState var3xx = var1x.getBlockState(var2x);
         if (!var1.getAbilities().instabuild && var3xx.is(BlockTags.ANVIL) && var1.getRandom().nextFloat() < 0.12F) {
            BlockState var4 = AnvilBlock.damage(var3xx);
            if (var4 == null) {
               var1x.removeBlock(var2x, false);
               var1x.levelEvent(1029, var2x, 0);
            } else {
               var1x.setBlock(var2x, var4, 2);
               var1x.levelEvent(1030, var2x, 0);
            }
         } else {
            var1x.levelEvent(1030, var2x, 0);
         }
      });
   }

   @Override
   public void createResult() {
      ItemStack var1 = this.inputSlots.getItem(0);
      this.cost.set(1);
      int var2 = 0;
      int var3 = 0;
      byte var4 = 0;
      if (var1.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
         this.cost.set(0);
      } else {
         ItemStack var5 = var1.copy();
         ItemStack var6 = this.inputSlots.getItem(1);
         Map var7 = EnchantmentHelper.getEnchantments(var5);
         var3 += var1.getBaseRepairCost() + (var6.isEmpty() ? 0 : var6.getBaseRepairCost());
         this.repairItemCountCost = 0;
         if (!var6.isEmpty()) {
            boolean var8 = var6.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(var6).isEmpty();
            if (var5.isDamageableItem() && var5.getItem().isValidRepairItem(var1, var6)) {
               int var22 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               if (var22 <= 0) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               int var24;
               for(var24 = 0; var22 > 0 && var24 < var6.getCount(); ++var24) {
                  int var26 = var5.getDamageValue() - var22;
                  var5.setDamageValue(var26);
                  ++var2;
                  var22 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               }

               this.repairItemCountCost = var24;
            } else {
               if (!var8 && (!var5.is(var6.getItem()) || !var5.isDamageableItem())) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               if (var5.isDamageableItem() && !var8) {
                  int var9 = var1.getMaxDamage() - var1.getDamageValue();
                  int var10 = var6.getMaxDamage() - var6.getDamageValue();
                  int var11 = var10 + var5.getMaxDamage() * 12 / 100;
                  int var12 = var9 + var11;
                  int var13 = var5.getMaxDamage() - var12;
                  if (var13 < 0) {
                     var13 = 0;
                  }

                  if (var13 < var5.getDamageValue()) {
                     var5.setDamageValue(var13);
                     var2 += 2;
                  }
               }

               Map var21 = EnchantmentHelper.getEnchantments(var6);
               boolean var23 = false;
               boolean var25 = false;

               for(Enchantment var28 : var21.keySet()) {
                  if (var28 != null) {
                     int var14 = var7.getOrDefault(var28, 0);
                     int var15 = var21.get(var28);
                     var15 = var14 == var15 ? var15 + 1 : Math.max(var15, var14);
                     boolean var16 = var28.canEnchant(var1);
                     if (this.player.getAbilities().instabuild || var1.is(Items.ENCHANTED_BOOK)) {
                        var16 = true;
                     }

                     for(Enchantment var18 : var7.keySet()) {
                        if (var18 != var28 && !var28.isCompatibleWith(var18)) {
                           var16 = false;
                           ++var2;
                        }
                     }

                     if (!var16) {
                        var25 = true;
                     } else {
                        var23 = true;
                        if (var15 > var28.getMaxLevel()) {
                           var15 = var28.getMaxLevel();
                        }

                        var7.put(var28, var15);
                        int var30 = 0;
                        switch(var28.getRarity()) {
                           case COMMON:
                              var30 = 1;
                              break;
                           case UNCOMMON:
                              var30 = 2;
                              break;
                           case RARE:
                              var30 = 4;
                              break;
                           case VERY_RARE:
                              var30 = 8;
                        }

                        if (var8) {
                           var30 = Math.max(1, var30 / 2);
                        }

                        var2 += var30 * var15;
                        if (var1.getCount() > 1) {
                           var2 = 40;
                        }
                     }
                  }
               }

               if (var25 && !var23) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }
            }
         }

         if (this.itemName != null && !Util.isBlank(this.itemName)) {
            if (!this.itemName.equals(var1.getHoverName().getString())) {
               var4 = 1;
               var2 += var4;
               var5.setHoverName(Component.literal(this.itemName));
            }
         } else if (var1.hasCustomHoverName()) {
            var4 = 1;
            var2 += var4;
            var5.resetHoverName();
         }

         this.cost.set(var3 + var2);
         if (var2 <= 0) {
            var5 = ItemStack.EMPTY;
         }

         if (var4 == var2 && var4 > 0 && this.cost.get() >= 40) {
            this.cost.set(39);
         }

         if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
            var5 = ItemStack.EMPTY;
         }

         if (!var5.isEmpty()) {
            int var20 = var5.getBaseRepairCost();
            if (!var6.isEmpty() && var20 < var6.getBaseRepairCost()) {
               var20 = var6.getBaseRepairCost();
            }

            if (var4 != var2 || var4 == 0) {
               var20 = calculateIncreasedRepairCost(var20);
            }

            var5.setRepairCost(var20);
            EnchantmentHelper.setEnchantments(var7, var5);
         }

         this.resultSlots.setItem(0, var5);
         this.broadcastChanges();
      }
   }

   public static int calculateIncreasedRepairCost(int var0) {
      return var0 * 2 + 1;
   }

   public boolean setItemName(String var1) {
      String var2 = validateName(var1);
      if (var2 != null && !var2.equals(this.itemName)) {
         this.itemName = var2;
         if (this.getSlot(2).hasItem()) {
            ItemStack var3 = this.getSlot(2).getItem();
            if (Util.isBlank(var2)) {
               var3.resetHoverName();
            } else {
               var3.setHoverName(Component.literal(var2));
            }
         }

         this.createResult();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private static String validateName(String var0) {
      String var1 = SharedConstants.filterText(var0);
      return var1.length() <= 50 ? var1 : null;
   }

   public int getCost() {
      return this.cost.get();
   }
}
