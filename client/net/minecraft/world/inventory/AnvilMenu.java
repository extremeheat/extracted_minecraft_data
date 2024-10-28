package net.minecraft.world.inventory;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Iterator;
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
   private final DataSlot cost;
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
      this.cost = DataSlot.standalone();
      this.addDataSlot(this.cost);
   }

   protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
      return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, (var0) -> {
         return true;
      }).withSlot(1, 76, 47, (var0) -> {
         return true;
      }).withResultSlot(2, 134, 47).build();
   }

   protected boolean isValidBlock(BlockState var1) {
      return var1.is(BlockTags.ANVIL);
   }

   protected boolean mayPickup(Player var1, boolean var2) {
      return (var1.hasInfiniteMaterials() || var1.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
   }

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
         BlockState var3 = var1x.getBlockState(var2x);
         if (!var1.hasInfiniteMaterials() && var3.is(BlockTags.ANVIL) && var1.getRandom().nextFloat() < 0.12F) {
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
   }

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
         var3 += (long)(Integer)var1.getOrDefault(DataComponents.REPAIR_COST, 0) + (long)(Integer)var7.getOrDefault(DataComponents.REPAIR_COST, 0);
         this.repairItemCountCost = 0;
         int var10;
         if (!var7.isEmpty()) {
            boolean var9 = var7.has(DataComponents.STORED_ENCHANTMENTS);
            int var11;
            int var12;
            if (var6.isDamageableItem() && var6.getItem().isValidRepairItem(var1, var7)) {
               var10 = Math.min(var6.getDamageValue(), var6.getMaxDamage() / 4);
               if (var10 <= 0) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               for(var11 = 0; var10 > 0 && var11 < var7.getCount(); ++var11) {
                  var12 = var6.getDamageValue() - var10;
                  var6.setDamageValue(var12);
                  ++var2;
                  var10 = Math.min(var6.getDamageValue(), var6.getMaxDamage() / 4);
               }

               this.repairItemCountCost = var11;
            } else {
               if (!var9 && (!var6.is(var7.getItem()) || !var6.isDamageableItem())) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               if (var6.isDamageableItem() && !var9) {
                  var10 = var1.getMaxDamage() - var1.getDamageValue();
                  var11 = var7.getMaxDamage() - var7.getDamageValue();
                  var12 = var11 + var6.getMaxDamage() * 12 / 100;
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

               ItemEnchantments var23 = EnchantmentHelper.getEnchantmentsForCrafting(var7);
               boolean var24 = false;
               boolean var25 = false;
               Iterator var26 = var23.entrySet().iterator();

               while(var26.hasNext()) {
                  Object2IntMap.Entry var27 = (Object2IntMap.Entry)var26.next();
                  Holder var15 = (Holder)var27.getKey();
                  int var16 = var8.getLevel(var15);
                  int var17 = var27.getIntValue();
                  var17 = var16 == var17 ? var17 + 1 : Math.max(var17, var16);
                  Enchantment var18 = (Enchantment)var15.value();
                  boolean var19 = var18.canEnchant(var1);
                  if (this.player.getAbilities().instabuild || var1.is(Items.ENCHANTED_BOOK)) {
                     var19 = true;
                  }

                  Iterator var20 = var8.keySet().iterator();

                  while(var20.hasNext()) {
                     Holder var21 = (Holder)var20.next();
                     if (!var21.equals(var15) && !Enchantment.areCompatible(var15, var21)) {
                        var19 = false;
                        ++var2;
                     }
                  }

                  if (!var19) {
                     var25 = true;
                  } else {
                     var24 = true;
                     if (var17 > var18.getMaxLevel()) {
                        var17 = var18.getMaxLevel();
                     }

                     var8.set(var15, var17);
                     int var28 = var18.getAnvilCost();
                     if (var9) {
                        var28 = Math.max(1, var28 / 2);
                     }

                     var2 += var28 * var17;
                     if (var1.getCount() > 1) {
                        var2 = 40;
                     }
                  }
               }

               if (var25 && !var24) {
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

         int var22 = (int)Mth.clamp(var3 + (long)var2, 0L, 2147483647L);
         this.cost.set(var22);
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
            var10 = (Integer)var6.getOrDefault(DataComponents.REPAIR_COST, 0);
            if (var10 < (Integer)var7.getOrDefault(DataComponents.REPAIR_COST, 0)) {
               var10 = (Integer)var7.getOrDefault(DataComponents.REPAIR_COST, 0);
            }

            if (var5 != var2 || var5 == 0) {
               var10 = calculateIncreasedRepairCost(var10);
            }

            var6.set(DataComponents.REPAIR_COST, var10);
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
