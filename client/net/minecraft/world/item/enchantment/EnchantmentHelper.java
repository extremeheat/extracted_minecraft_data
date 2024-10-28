package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   private static final float SWIFT_SNEAK_EXTRA_FACTOR = 0.15F;

   public EnchantmentHelper() {
      super();
   }

   public static int getItemEnchantmentLevel(Enchantment var0, ItemStack var1) {
      ItemEnchantments var2 = (ItemEnchantments)var1.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      return var2.getLevel(var0);
   }

   public static ItemEnchantments updateEnchantments(ItemStack var0, Consumer<ItemEnchantments.Mutable> var1) {
      DataComponentType var2 = getComponentType(var0);
      ItemEnchantments var3 = (ItemEnchantments)var0.get(var2);
      if (var3 == null) {
         return ItemEnchantments.EMPTY;
      } else {
         ItemEnchantments.Mutable var4 = new ItemEnchantments.Mutable(var3);
         var1.accept(var4);
         ItemEnchantments var5 = var4.toImmutable();
         var0.set(var2, var5);
         return var5;
      }
   }

   public static boolean canStoreEnchantments(ItemStack var0) {
      return var0.has(getComponentType(var0));
   }

   public static void setEnchantments(ItemStack var0, ItemEnchantments var1) {
      var0.set(getComponentType(var0), var1);
   }

   public static ItemEnchantments getEnchantmentsForCrafting(ItemStack var0) {
      return (ItemEnchantments)var0.getOrDefault(getComponentType(var0), ItemEnchantments.EMPTY);
   }

   private static DataComponentType<ItemEnchantments> getComponentType(ItemStack var0) {
      return var0.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
   }

   public static boolean hasAnyEnchantments(ItemStack var0) {
      return !((ItemEnchantments)var0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)).isEmpty() || !((ItemEnchantments)var0.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)).isEmpty();
   }

   public static float getSweepingDamageRatio(int var0) {
      return 1.0F - 1.0F / (float)(var0 + 1);
   }

   private static void runIterationOnItem(EnchantmentVisitor var0, ItemStack var1) {
      ItemEnchantments var2 = (ItemEnchantments)var1.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry var4 = (Object2IntMap.Entry)var3.next();
         var0.accept((Enchantment)((Holder)var4.getKey()).value(), var4.getIntValue());
      }

   }

   private static void runIterationOnInventory(EnchantmentVisitor var0, Iterable<ItemStack> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         runIterationOnItem(var0, var3);
      }

   }

   public static int getDamageProtection(Iterable<ItemStack> var0, DamageSource var1) {
      MutableInt var2 = new MutableInt();
      runIterationOnInventory((var2x, var3) -> {
         var2.add(var2x.getDamageProtection(var3, var1));
      }, var0);
      return var2.intValue();
   }

   public static float getDamageBonus(ItemStack var0, @Nullable EntityType<?> var1) {
      MutableFloat var2 = new MutableFloat();
      runIterationOnItem((var2x, var3) -> {
         var2.add(var2x.getDamageBonus(var3, var1));
      }, var0);
      return var2.floatValue();
   }

   public static float getSweepingDamageRatio(LivingEntity var0) {
      int var1 = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, var0);
      return var1 > 0 ? getSweepingDamageRatio(var1) : 0.0F;
   }

   public static float calculateArmorBreach(@Nullable Entity var0, float var1) {
      if (var0 instanceof LivingEntity var2) {
         int var3 = getEnchantmentLevel(Enchantments.BREACH, var2);
         if (var3 > 0) {
            return BreachEnchantment.calculateArmorBreach((float)var3, var1);
         }
      }

      return var1;
   }

   public static void doPostHurtEffects(LivingEntity var0, Entity var1) {
      EnchantmentVisitor var2 = (var2x, var3) -> {
         var2x.doPostHurt(var0, var1, var3);
      };
      if (var0 != null) {
         runIterationOnInventory(var2, var0.getAllSlots());
      }

      if (var1 instanceof Player) {
         runIterationOnItem(var2, var0.getMainHandItem());
      }

   }

   public static void doPostDamageEffects(LivingEntity var0, Entity var1) {
      EnchantmentVisitor var2 = (var2x, var3) -> {
         var2x.doPostAttack(var0, var1, var3);
      };
      if (var0 != null) {
         runIterationOnInventory(var2, var0.getAllSlots());
      }

      if (var0 instanceof Player) {
         runIterationOnItem(var2, var0.getMainHandItem());
      }

   }

   public static void doPostItemStackHurtEffects(LivingEntity var0, Entity var1, ItemEnchantments var2) {
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry var4 = (Object2IntMap.Entry)var3.next();
         ((Enchantment)((Holder)var4.getKey()).value()).doPostItemStackHurt(var0, var1, var4.getIntValue());
      }

   }

   public static int getEnchantmentLevel(Enchantment var0, LivingEntity var1) {
      Collection var2 = var0.getSlotItems(var1).values();
      if (var2 == null) {
         return 0;
      } else {
         int var3 = 0;
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            int var6 = getItemEnchantmentLevel(var0, var5);
            if (var6 > var3) {
               var3 = var6;
            }
         }

         return var3;
      }
   }

   public static float getSneakingSpeedBonus(LivingEntity var0) {
      return (float)getEnchantmentLevel(Enchantments.SWIFT_SNEAK, var0) * 0.15F;
   }

   public static int getKnockbackBonus(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.KNOCKBACK, var0);
   }

   public static int getFireAspect(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.FIRE_ASPECT, var0);
   }

   public static int getRespiration(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.RESPIRATION, var0);
   }

   public static int getDepthStrider(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, var0);
   }

   public static int getBlockEfficiency(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.EFFICIENCY, var0);
   }

   public static int getFishingLuckBonus(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, var0);
   }

   public static int getFishingSpeedBonus(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.LURE, var0);
   }

   public static int getMobLooting(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.LOOTING, var0);
   }

   public static boolean hasAquaAffinity(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, var0) > 0;
   }

   public static boolean hasFrostWalker(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.FROST_WALKER, var0) > 0;
   }

   public static boolean hasSoulSpeed(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.SOUL_SPEED, var0) > 0;
   }

   public static boolean hasBindingCurse(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, var0) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, var0) > 0;
   }

   public static boolean hasSilkTouch(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.SILK_TOUCH, var0) > 0;
   }

   public static int getLoyalty(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.LOYALTY, var0);
   }

   public static int getRiptide(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.RIPTIDE, var0);
   }

   public static boolean hasChanneling(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.CHANNELING, var0) > 0;
   }

   @Nullable
   public static Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment var0, LivingEntity var1) {
      return getRandomItemWith(var0, var1, (var0x) -> {
         return true;
      });
   }

   @Nullable
   public static Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment var0, LivingEntity var1, Predicate<ItemStack> var2) {
      Map var3 = var0.getSlotItems(var1);
      if (var3.isEmpty()) {
         return null;
      } else {
         ArrayList var4 = Lists.newArrayList();
         Iterator var5 = var3.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            ItemStack var7 = (ItemStack)var6.getValue();
            if (!var7.isEmpty() && getItemEnchantmentLevel(var0, var7) > 0 && var2.test(var7)) {
               var4.add(var6);
            }
         }

         return var4.isEmpty() ? null : (Map.Entry)var4.get(var1.getRandom().nextInt(var4.size()));
      }
   }

   public static int getEnchantmentCost(RandomSource var0, int var1, int var2, ItemStack var3) {
      Item var4 = var3.getItem();
      int var5 = var4.getEnchantmentValue();
      if (var5 <= 0) {
         return 0;
      } else {
         if (var2 > 15) {
            var2 = 15;
         }

         int var6 = var0.nextInt(8) + 1 + (var2 >> 1) + var0.nextInt(var2 + 1);
         if (var1 == 0) {
            return Math.max(var6 / 3, 1);
         } else {
            return var1 == 1 ? var6 * 2 / 3 + 1 : Math.max(var6, var2 * 2);
         }
      }
   }

   public static ItemStack enchantItem(FeatureFlagSet var0, RandomSource var1, ItemStack var2, int var3, boolean var4) {
      List var5 = selectEnchantment(var0, var1, var2, var3, var4);
      if (var2.is(Items.BOOK)) {
         var2 = new ItemStack(Items.ENCHANTED_BOOK);
      }

      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         EnchantmentInstance var7 = (EnchantmentInstance)var6.next();
         var2.enchant(var7.enchantment, var7.level);
      }

      return var2;
   }

   public static List<EnchantmentInstance> selectEnchantment(FeatureFlagSet var0, RandomSource var1, ItemStack var2, int var3, boolean var4) {
      ArrayList var5 = Lists.newArrayList();
      Item var6 = var2.getItem();
      int var7 = var6.getEnchantmentValue();
      if (var7 <= 0) {
         return var5;
      } else {
         var3 += 1 + var1.nextInt(var7 / 4 + 1) + var1.nextInt(var7 / 4 + 1);
         float var8 = (var1.nextFloat() + var1.nextFloat() - 1.0F) * 0.15F;
         var3 = Mth.clamp(Math.round((float)var3 + (float)var3 * var8), 1, 2147483647);
         List var9 = getAvailableEnchantmentResults(var0, var3, var2, var4);
         if (!var9.isEmpty()) {
            Optional var10000 = WeightedRandom.getRandomItem(var1, var9);
            Objects.requireNonNull(var5);
            var10000.ifPresent(var5::add);

            while(var1.nextInt(50) <= var3) {
               if (!var5.isEmpty()) {
                  filterCompatibleEnchantments(var9, (EnchantmentInstance)Util.lastOf(var5));
               }

               if (var9.isEmpty()) {
                  break;
               }

               var10000 = WeightedRandom.getRandomItem(var1, var9);
               Objects.requireNonNull(var5);
               var10000.ifPresent(var5::add);
               var3 /= 2;
            }
         }

         return var5;
      }
   }

   public static void filterCompatibleEnchantments(List<EnchantmentInstance> var0, EnchantmentInstance var1) {
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         if (!var1.enchantment.isCompatibleWith(((EnchantmentInstance)var2.next()).enchantment)) {
            var2.remove();
         }
      }

   }

   public static boolean isEnchantmentCompatible(Collection<Holder<Enchantment>> var0, Enchantment var1) {
      Iterator var2 = var0.iterator();

      Holder var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Holder)var2.next();
      } while(((Enchantment)var3.value()).isCompatibleWith(var1));

      return false;
   }

   public static List<EnchantmentInstance> getAvailableEnchantmentResults(FeatureFlagSet var0, int var1, ItemStack var2, boolean var3) {
      ArrayList var4 = Lists.newArrayList();
      boolean var5 = var2.is(Items.BOOK);
      Iterator var6 = BuiltInRegistries.ENCHANTMENT.iterator();

      while(true) {
         while(true) {
            Enchantment var7;
            do {
               do {
                  do {
                     do {
                        if (!var6.hasNext()) {
                           return var4;
                        }

                        var7 = (Enchantment)var6.next();
                     } while(!var7.isEnabled(var0));
                  } while(var7.isTreasureOnly() && !var3);
               } while(!var7.isDiscoverable());
            } while(!var5 && (!var7.canEnchant(var2) || !var7.isPrimaryItem(var2)));

            for(int var8 = var7.getMaxLevel(); var8 > var7.getMinLevel() - 1; --var8) {
               if (var1 >= var7.getMinCost(var8) && var1 <= var7.getMaxCost(var8)) {
                  var4.add(new EnchantmentInstance(var7, var8));
                  break;
               }
            }
         }
      }
   }

   @FunctionalInterface
   interface EnchantmentVisitor {
      void accept(Enchantment var1, int var2);
   }
}
