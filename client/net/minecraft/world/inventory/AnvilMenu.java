package net.minecraft.world.inventory;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilMenu extends ItemCombinerMenu {
   private static final Logger LOGGER = LogManager.getLogger();
   private int repairItemCountCost;
   private String itemName;
   private final DataSlot cost;

   public AnvilMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public AnvilMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.ANVIL, var1, var2, var3);
      this.cost = DataSlot.standalone();
      this.addDataSlot(this.cost);
   }

   protected boolean isValidBlock(BlockState var1) {
      return var1.is(BlockTags.ANVIL);
   }

   protected boolean mayPickup(Player var1, boolean var2) {
      return (var1.abilities.instabuild || var1.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
   }

   protected ItemStack onTake(Player var1, ItemStack var2) {
      if (!var1.abilities.instabuild) {
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
         BlockState var3 = var1x.getBlockState(var2x);
         if (!var1.abilities.instabuild && var3.is(BlockTags.ANVIL) && var1.getRandom().nextFloat() < 0.12F) {
            BlockState var4 = AnvilBlock.damage(var3);
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
      return var2;
   }

   public void createResult() {
      ItemStack var1 = this.inputSlots.getItem(0);
      this.cost.set(1);
      int var2 = 0;
      byte var3 = 0;
      byte var4 = 0;
      if (var1.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
         this.cost.set(0);
      } else {
         ItemStack var5 = var1.copy();
         ItemStack var6 = this.inputSlots.getItem(1);
         Map var7 = EnchantmentHelper.getEnchantments(var5);
         int var19 = var3 + var1.getBaseRepairCost() + (var6.isEmpty() ? 0 : var6.getBaseRepairCost());
         this.repairItemCountCost = 0;
         if (!var6.isEmpty()) {
            boolean var8 = var6.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(var6).isEmpty();
            int var9;
            int var10;
            int var11;
            if (var5.isDamageableItem() && var5.getItem().isValidRepairItem(var1, var6)) {
               var9 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               if (var9 <= 0) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               for(var10 = 0; var9 > 0 && var10 < var6.getCount(); ++var10) {
                  var11 = var5.getDamageValue() - var9;
                  var5.setDamageValue(var11);
                  ++var2;
                  var9 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               }

               this.repairItemCountCost = var10;
            } else {
               if (!var8 && (var5.getItem() != var6.getItem() || !var5.isDamageableItem())) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               if (var5.isDamageableItem() && !var8) {
                  var9 = var1.getMaxDamage() - var1.getDamageValue();
                  var10 = var6.getMaxDamage() - var6.getDamageValue();
                  var11 = var10 + var5.getMaxDamage() * 12 / 100;
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
               boolean var22 = false;
               boolean var23 = false;
               Iterator var24 = var21.keySet().iterator();

               label155:
               while(true) {
                  Enchantment var25;
                  do {
                     if (!var24.hasNext()) {
                        if (var23 && !var22) {
                           this.resultSlots.setItem(0, ItemStack.EMPTY);
                           this.cost.set(0);
                           return;
                        }
                        break label155;
                     }

                     var25 = (Enchantment)var24.next();
                  } while(var25 == null);

                  int var14 = (Integer)var7.getOrDefault(var25, 0);
                  int var15 = (Integer)var21.get(var25);
                  var15 = var14 == var15 ? var15 + 1 : Math.max(var15, var14);
                  boolean var16 = var25.canEnchant(var1);
                  if (this.player.abilities.instabuild || var1.getItem() == Items.ENCHANTED_BOOK) {
                     var16 = true;
                  }

                  Iterator var17 = var7.keySet().iterator();

                  while(var17.hasNext()) {
                     Enchantment var18 = (Enchantment)var17.next();
                     if (var18 != var25 && !var25.isCompatibleWith(var18)) {
                        var16 = false;
                        ++var2;
                     }
                  }

                  if (!var16) {
                     var23 = true;
                  } else {
                     var22 = true;
                     if (var15 > var25.getMaxLevel()) {
                        var15 = var25.getMaxLevel();
                     }

                     var7.put(var25, var15);
                     int var26 = 0;
                     switch(var25.getRarity()) {
                     case COMMON:
                        var26 = 1;
                        break;
                     case UNCOMMON:
                        var26 = 2;
                        break;
                     case RARE:
                        var26 = 4;
                        break;
                     case VERY_RARE:
                        var26 = 8;
                     }

                     if (var8) {
                        var26 = Math.max(1, var26 / 2);
                     }

                     var2 += var26 * var15;
                     if (var1.getCount() > 1) {
                        var2 = 40;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.itemName)) {
            if (var1.hasCustomHoverName()) {
               var4 = 1;
               var2 += var4;
               var5.resetHoverName();
            }
         } else if (!this.itemName.equals(var1.getHoverName().getString())) {
            var4 = 1;
            var2 += var4;
            var5.setHoverName(new TextComponent(this.itemName));
         }

         this.cost.set(var19 + var2);
         if (var2 <= 0) {
            var5 = ItemStack.EMPTY;
         }

         if (var4 == var2 && var4 > 0 && this.cost.get() >= 40) {
            this.cost.set(39);
         }

         if (this.cost.get() >= 40 && !this.player.abilities.instabuild) {
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

   public void setItemName(String var1) {
      this.itemName = var1;
      if (this.getSlot(2).hasItem()) {
         ItemStack var2 = this.getSlot(2).getItem();
         if (StringUtils.isBlank(var1)) {
            var2.resetHoverName();
         } else {
            var2.setHoverName(new TextComponent(this.itemName));
         }
      }

      this.createResult();
   }

   public int getCost() {
      return this.cost.get();
   }
}
