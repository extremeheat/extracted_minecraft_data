package net.minecraft.world.inventory;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
      return (var1.hasInfiniteMaterials() || var1.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
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
         if (!var1.hasInfiniteMaterials() && var3xx.is(BlockTags.ANVIL) && var1.getRandom().nextFloat() < 0.12F) {
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
      if (!var1.isEmpty() && EnchantmentHelper.canStoreEnchantments(var1)) {
         ItemStack var5 = var1.copy();
         ItemStack var6 = this.inputSlots.getItem(1);
         ItemEnchantments.Mutable var7 = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(var5));
         var3 += var1.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)) + var6.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
         this.repairItemCountCost = 0;
         if (!var6.isEmpty()) {
            boolean var8 = var6.has(DataComponents.STORED_ENCHANTMENTS);
            if (var5.isDamageableItem() && var5.getItem().isValidRepairItem(var1, var6)) {
               int var24 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               if (var24 <= 0) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               int var26;
               for(var26 = 0; var24 > 0 && var26 < var6.getCount(); ++var26) {
                  int var28 = var5.getDamageValue() - var24;
                  var5.setDamageValue(var28);
                  ++var2;
                  var24 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               }

               this.repairItemCountCost = var26;
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

               ItemEnchantments var23 = EnchantmentHelper.getEnchantmentsForCrafting(var6);
               boolean var25 = false;
               boolean var27 = false;

               for(Entry var30 : var23.entrySet()) {
                  Holder var14 = (Holder)var30.getKey();
                  Enchantment var15 = (Enchantment)var14.value();
                  int var16 = var7.getLevel(var15);
                  int var17 = var30.getIntValue();
                  var17 = var16 == var17 ? var17 + 1 : Math.max(var17, var16);
                  boolean var18 = var15.canEnchant(var1);
                  if (this.player.getAbilities().instabuild || var1.is(Items.ENCHANTED_BOOK)) {
                     var18 = true;
                  }

                  for(Holder var20 : var7.keySet()) {
                     if (!var20.equals(var14) && !var15.isCompatibleWith((Enchantment)var20.value())) {
                        var18 = false;
                        ++var2;
                     }
                  }

                  if (!var18) {
                     var27 = true;
                  } else {
                     var25 = true;
                     if (var17 > var15.getMaxLevel()) {
                        var17 = var15.getMaxLevel();
                     }

                     var7.set(var15, var17);

                     int var32 = switch(var15.getRarity()) {
                        case COMMON -> 1;
                        case UNCOMMON -> 2;
                        case RARE -> 4;
                        case VERY_RARE -> 8;
                     };
                     if (var8) {
                        var32 = Math.max(1, var32 / 2);
                     }

                     var2 += var32 * var17;
                     if (var1.getCount() > 1) {
                        var2 = 40;
                     }
                  }
               }

               if (var27 && !var25) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }
            }
         }

         if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(var1.getHoverName().getString())) {
               var4 = 1;
               var2 += var4;
               var5.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
         } else if (var1.has(DataComponents.CUSTOM_NAME)) {
            var4 = 1;
            var2 += var4;
            var5.remove(DataComponents.CUSTOM_NAME);
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
            int var22 = var5.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
            if (var22 < var6.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))) {
               var22 = var6.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
            }

            if (var4 != var2 || var4 == 0) {
               var22 = calculateIncreasedRepairCost(var22);
            }

            var5.set(DataComponents.REPAIR_COST, var22);
            EnchantmentHelper.setEnchantments(var5, var7.toImmutable());
         }

         this.resultSlots.setItem(0, var5);
         this.broadcastChanges();
      } else {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
         this.cost.set(0);
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
            if (StringUtil.isBlank(var2)) {
               var3.remove(DataComponents.CUSTOM_NAME);
            } else {
               var3.set(DataComponents.CUSTOM_NAME, Component.literal(var2));
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
      String var1 = StringUtil.filterText(var0);
      return var1.length() <= 50 ? var1 : null;
   }

   public int getCost() {
      return this.cost.get();
   }
}
