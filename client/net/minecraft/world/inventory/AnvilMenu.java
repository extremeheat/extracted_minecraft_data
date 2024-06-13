package net.minecraft.world.inventory;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
         BlockState var3x = var1x.getBlockState(var2x);
         if (!var1.hasInfiniteMaterials() && var3x.is(BlockTags.ANVIL) && var1.getRandom().nextFloat() < 0.12F) {
            BlockState var4 = AnvilBlock.damage(var3x);
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
      long var3 = 0L;
      byte var5 = 0;
      if (!var1.isEmpty() && EnchantmentHelper.canStoreEnchantments(var1)) {
         ItemStack var6 = var1.copy();
         ItemStack var7 = this.inputSlots.getItem(1);
         ItemEnchantments.Mutable var8 = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(var6));
         var3 += (long)var1.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue()
            + (long)var7.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue();
         this.repairItemCountCost = 0;
         if (!var7.isEmpty()) {
            boolean var9 = var7.has(DataComponents.STORED_ENCHANTMENTS);
            if (var6.isDamageableItem() && var6.getItem().isValidRepairItem(var1, var7)) {
               int var25 = Math.min(var6.getDamageValue(), var6.getMaxDamage() / 4);
               if (var25 <= 0) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               int var28;
               for (var28 = 0; var25 > 0 && var28 < var7.getCount(); var28++) {
                  int var30 = var6.getDamageValue() - var25;
                  var6.setDamageValue(var30);
                  var2++;
                  var25 = Math.min(var6.getDamageValue(), var6.getMaxDamage() / 4);
               }

               this.repairItemCountCost = var28;
            } else {
               if (!var9 && (!var6.is(var7.getItem()) || !var6.isDamageableItem())) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               if (var6.isDamageableItem() && !var9) {
                  int var10 = var1.getMaxDamage() - var1.getDamageValue();
                  int var11 = var7.getMaxDamage() - var7.getDamageValue();
                  int var12 = var11 + var6.getMaxDamage() * 12 / 100;
                  int var13 = var10 + var12;
                  int var14 = var6.getMaxDamage() - var13;
                  if (var14 < 0) {
                     var14 = 0;
                  }

                  if (var14 < var6.getDamageValue()) {
                     var6.setDamageValue(var14);
                     var2 += 2;
                  }
               }

               ItemEnchantments var24 = EnchantmentHelper.getEnchantmentsForCrafting(var7);
               boolean var27 = false;
               boolean var29 = false;

               for (Entry var32 : var24.entrySet()) {
                  Holder var15 = (Holder)var32.getKey();
                  int var16 = var8.getLevel(var15);
                  int var17 = var32.getIntValue();
                  var17 = var16 == var17 ? var17 + 1 : Math.max(var17, var16);
                  Enchantment var18 = (Enchantment)var15.value();
                  boolean var19 = var18.canEnchant(var1);
                  if (this.player.getAbilities().instabuild || var1.is(Items.ENCHANTED_BOOK)) {
                     var19 = true;
                  }

                  for (Holder var21 : var8.keySet()) {
                     if (!var21.equals(var15) && !Enchantment.areCompatible(var15, var21)) {
                        var19 = false;
                        var2++;
                     }
                  }

                  if (!var19) {
                     var29 = true;
                  } else {
                     var27 = true;
                     if (var17 > var18.getMaxLevel()) {
                        var17 = var18.getMaxLevel();
                     }

                     var8.set(var15, var17);
                     int var34 = var18.getAnvilCost();
                     if (var9) {
                        var34 = Math.max(1, var34 / 2);
                     }

                     var2 += var34 * var17;
                     if (var1.getCount() > 1) {
                        var2 = 40;
                     }
                  }
               }

               if (var29 && !var27) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }
            }
         }

         if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(var1.getHoverName().getString())) {
               var5 = 1;
               var2 += var5;
               var6.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
         } else if (var1.has(DataComponents.CUSTOM_NAME)) {
            var5 = 1;
            var2 += var5;
            var6.remove(DataComponents.CUSTOM_NAME);
         }

         int var23 = (int)Mth.clamp(var3 + (long)var2, 0L, 2147483647L);
         this.cost.set(var23);
         if (var2 <= 0) {
            var6 = ItemStack.EMPTY;
         }

         if (var5 == var2 && var5 > 0 && this.cost.get() >= 40) {
            this.cost.set(39);
         }

         if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
            var6 = ItemStack.EMPTY;
         }

         if (!var6.isEmpty()) {
            int var26 = var6.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
            if (var26 < var7.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))) {
               var26 = var7.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
            }

            if (var5 != var2 || var5 == 0) {
               var26 = calculateIncreasedRepairCost(var26);
            }

            var6.set(DataComponents.REPAIR_COST, var26);
            EnchantmentHelper.setEnchantments(var6, var8.toImmutable());
         }

         this.resultSlots.setItem(0, var6);
         this.broadcastChanges();
      } else {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
         this.cost.set(0);
      }
   }

   public static int calculateIncreasedRepairCost(int var0) {
      return (int)Math.min((long)var0 * 2L + 1L, 2147483647L);
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
