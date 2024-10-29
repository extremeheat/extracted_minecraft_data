package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;

public class EnchantmentHelper {
   public EnchantmentHelper() {
      super();
   }

   public static int getItemEnchantmentLevel(Holder<Enchantment> var0, ItemStack var1) {
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

   public static int processDurabilityChange(ServerLevel var0, ItemStack var1, int var2) {
      MutableFloat var3 = new MutableFloat((float)var2);
      runIterationOnItem(var1, (var3x, var4) -> {
         ((Enchantment)var3x.value()).modifyDurabilityChange(var0, var4, var1, var3);
      });
      return var3.intValue();
   }

   public static int processAmmoUse(ServerLevel var0, ItemStack var1, ItemStack var2, int var3) {
      MutableFloat var4 = new MutableFloat((float)var3);
      runIterationOnItem(var1, (var3x, var4x) -> {
         ((Enchantment)var3x.value()).modifyAmmoCount(var0, var4x, var2, var4);
      });
      return var4.intValue();
   }

   public static int processBlockExperience(ServerLevel var0, ItemStack var1, int var2) {
      MutableFloat var3 = new MutableFloat((float)var2);
      runIterationOnItem(var1, (var3x, var4) -> {
         ((Enchantment)var3x.value()).modifyBlockExperience(var0, var4, var1, var3);
      });
      return var3.intValue();
   }

   public static int processMobExperience(ServerLevel var0, @Nullable Entity var1, Entity var2, int var3) {
      if (var1 instanceof LivingEntity var4) {
         MutableFloat var5 = new MutableFloat((float)var3);
         runIterationOnEquipment(var4, (var3x, var4x, var5x) -> {
            ((Enchantment)var3x.value()).modifyMobExperience(var0, var4x, var5x.itemStack(), var2, var5);
         });
         return var5.intValue();
      } else {
         return var3;
      }
   }

   public static ItemStack createBook(EnchantmentInstance var0) {
      ItemStack var1 = new ItemStack(Items.ENCHANTED_BOOK);
      var1.enchant(var0.enchantment, var0.level);
      return var1;
   }

   private static void runIterationOnItem(ItemStack var0, EnchantmentVisitor var1) {
      ItemEnchantments var2 = (ItemEnchantments)var0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry var4 = (Object2IntMap.Entry)var3.next();
         var1.accept((Holder)var4.getKey(), var4.getIntValue());
      }

   }

   private static void runIterationOnItem(ItemStack var0, EquipmentSlot var1, LivingEntity var2, EnchantmentInSlotVisitor var3) {
      if (!var0.isEmpty()) {
         ItemEnchantments var4 = (ItemEnchantments)var0.get(DataComponents.ENCHANTMENTS);
         if (var4 != null && !var4.isEmpty()) {
            EnchantedItemInUse var5 = new EnchantedItemInUse(var0, var1, var2);
            Iterator var6 = var4.entrySet().iterator();

            while(var6.hasNext()) {
               Object2IntMap.Entry var7 = (Object2IntMap.Entry)var6.next();
               Holder var8 = (Holder)var7.getKey();
               if (((Enchantment)var8.value()).matchingSlot(var1)) {
                  var3.accept(var8, var7.getIntValue(), var5);
               }
            }

         }
      }
   }

   private static void runIterationOnEquipment(LivingEntity var0, EnchantmentInSlotVisitor var1) {
      Iterator var2 = EquipmentSlot.VALUES.iterator();

      while(var2.hasNext()) {
         EquipmentSlot var3 = (EquipmentSlot)var2.next();
         runIterationOnItem(var0.getItemBySlot(var3), var3, var0, var1);
      }

   }

   public static boolean isImmuneToDamage(ServerLevel var0, LivingEntity var1, DamageSource var2) {
      MutableBoolean var3 = new MutableBoolean();
      runIterationOnEquipment(var1, (var4, var5, var6) -> {
         var3.setValue(var3.isTrue() || ((Enchantment)var4.value()).isImmuneToDamage(var0, var5, var1, var2));
      });
      return var3.isTrue();
   }

   public static float getDamageProtection(ServerLevel var0, LivingEntity var1, DamageSource var2) {
      MutableFloat var3 = new MutableFloat(0.0F);
      runIterationOnEquipment(var1, (var4, var5, var6) -> {
         ((Enchantment)var4.value()).modifyDamageProtection(var0, var5, var6.itemStack(), var1, var2, var3);
      });
      return var3.floatValue();
   }

   public static float modifyDamage(ServerLevel var0, ItemStack var1, Entity var2, DamageSource var3, float var4) {
      MutableFloat var5 = new MutableFloat(var4);
      runIterationOnItem(var1, (var5x, var6) -> {
         ((Enchantment)var5x.value()).modifyDamage(var0, var6, var1, var2, var3, var5);
      });
      return var5.floatValue();
   }

   public static float modifyFallBasedDamage(ServerLevel var0, ItemStack var1, Entity var2, DamageSource var3, float var4) {
      MutableFloat var5 = new MutableFloat(var4);
      runIterationOnItem(var1, (var5x, var6) -> {
         ((Enchantment)var5x.value()).modifyFallBasedDamage(var0, var6, var1, var2, var3, var5);
      });
      return var5.floatValue();
   }

   public static float modifyArmorEffectiveness(ServerLevel var0, ItemStack var1, Entity var2, DamageSource var3, float var4) {
      MutableFloat var5 = new MutableFloat(var4);
      runIterationOnItem(var1, (var5x, var6) -> {
         ((Enchantment)var5x.value()).modifyArmorEffectivness(var0, var6, var1, var2, var3, var5);
      });
      return var5.floatValue();
   }

   public static float modifyKnockback(ServerLevel var0, ItemStack var1, Entity var2, DamageSource var3, float var4) {
      MutableFloat var5 = new MutableFloat(var4);
      runIterationOnItem(var1, (var5x, var6) -> {
         ((Enchantment)var5x.value()).modifyKnockback(var0, var6, var1, var2, var3, var5);
      });
      return var5.floatValue();
   }

   public static void doPostAttackEffects(ServerLevel var0, Entity var1, DamageSource var2) {
      Entity var4 = var2.getEntity();
      if (var4 instanceof LivingEntity var3) {
         doPostAttackEffectsWithItemSource(var0, var1, var2, var3.getWeaponItem());
      } else {
         doPostAttackEffectsWithItemSource(var0, var1, var2, (ItemStack)null);
      }

   }

   public static void doPostAttackEffectsWithItemSource(ServerLevel var0, Entity var1, DamageSource var2, @Nullable ItemStack var3) {
      doPostAttackEffectsWithItemSourceOnBreak(var0, var1, var2, var3, (Consumer)null);
   }

   public static void doPostAttackEffectsWithItemSourceOnBreak(ServerLevel var0, Entity var1, DamageSource var2, @Nullable ItemStack var3, @Nullable Consumer<Item> var4) {
      if (var1 instanceof LivingEntity var5) {
         runIterationOnEquipment(var5, (var3x, var4x, var5x) -> {
            ((Enchantment)var3x.value()).doPostAttack(var0, var4x, var5x, EnchantmentTarget.VICTIM, var1, var2);
         });
      }

      if (var3 != null) {
         Entity var6 = var2.getEntity();
         if (var6 instanceof LivingEntity) {
            var5 = (LivingEntity)var6;
            runIterationOnItem(var3, EquipmentSlot.MAINHAND, var5, (var3x, var4x, var5x) -> {
               ((Enchantment)var3x.value()).doPostAttack(var0, var4x, var5x, EnchantmentTarget.ATTACKER, var1, var2);
            });
         } else if (var4 != null) {
            EnchantedItemInUse var7 = new EnchantedItemInUse(var3, (EquipmentSlot)null, (LivingEntity)null, var4);
            runIterationOnItem(var3, (var4x, var5x) -> {
               ((Enchantment)var4x.value()).doPostAttack(var0, var5x, var7, EnchantmentTarget.ATTACKER, var1, var2);
            });
         }
      }

   }

   public static void runLocationChangedEffects(ServerLevel var0, LivingEntity var1) {
      runIterationOnEquipment(var1, (var2, var3, var4) -> {
         ((Enchantment)var2.value()).runLocationChangedEffects(var0, var3, var4, var1);
      });
   }

   public static void runLocationChangedEffects(ServerLevel var0, ItemStack var1, LivingEntity var2, EquipmentSlot var3) {
      runIterationOnItem(var1, var3, var2, (var2x, var3x, var4) -> {
         ((Enchantment)var2x.value()).runLocationChangedEffects(var0, var3x, var4, var2);
      });
   }

   public static void stopLocationBasedEffects(LivingEntity var0) {
      runIterationOnEquipment(var0, (var1, var2, var3) -> {
         ((Enchantment)var1.value()).stopLocationBasedEffects(var2, var3, var0);
      });
   }

   public static void stopLocationBasedEffects(ItemStack var0, LivingEntity var1, EquipmentSlot var2) {
      runIterationOnItem(var0, var2, var1, (var1x, var2x, var3) -> {
         ((Enchantment)var1x.value()).stopLocationBasedEffects(var2x, var3, var1);
      });
   }

   public static void tickEffects(ServerLevel var0, LivingEntity var1) {
      runIterationOnEquipment(var1, (var2, var3, var4) -> {
         ((Enchantment)var2.value()).tick(var0, var3, var4, var1);
      });
   }

   public static int getEnchantmentLevel(Holder<Enchantment> var0, LivingEntity var1) {
      Collection var2 = ((Enchantment)var0.value()).getSlotItems(var1).values();
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

   public static int processProjectileCount(ServerLevel var0, ItemStack var1, Entity var2, int var3) {
      MutableFloat var4 = new MutableFloat((float)var3);
      runIterationOnItem(var1, (var4x, var5) -> {
         ((Enchantment)var4x.value()).modifyProjectileCount(var0, var5, var1, var2, var4);
      });
      return Math.max(0, var4.intValue());
   }

   public static float processProjectileSpread(ServerLevel var0, ItemStack var1, Entity var2, float var3) {
      MutableFloat var4 = new MutableFloat(var3);
      runIterationOnItem(var1, (var4x, var5) -> {
         ((Enchantment)var4x.value()).modifyProjectileSpread(var0, var5, var1, var2, var4);
      });
      return Math.max(0.0F, var4.floatValue());
   }

   public static int getPiercingCount(ServerLevel var0, ItemStack var1, ItemStack var2) {
      MutableFloat var3 = new MutableFloat(0.0F);
      runIterationOnItem(var1, (var3x, var4) -> {
         ((Enchantment)var3x.value()).modifyPiercingCount(var0, var4, var2, var3);
      });
      return Math.max(0, var3.intValue());
   }

   public static void onProjectileSpawned(ServerLevel var0, ItemStack var1, Projectile var2, Consumer<Item> var3) {
      Entity var6 = var2.getOwner();
      LivingEntity var10000;
      if (var6 instanceof LivingEntity var5) {
         var10000 = var5;
      } else {
         var10000 = null;
      }

      LivingEntity var4 = var10000;
      EnchantedItemInUse var7 = new EnchantedItemInUse(var1, (EquipmentSlot)null, var4, var3);
      runIterationOnItem(var1, (var3x, var4x) -> {
         ((Enchantment)var3x.value()).onProjectileSpawned(var0, var4x, var7, var2);
      });
   }

   public static void onHitBlock(ServerLevel var0, ItemStack var1, @Nullable LivingEntity var2, Entity var3, @Nullable EquipmentSlot var4, Vec3 var5, BlockState var6, Consumer<Item> var7) {
      EnchantedItemInUse var8 = new EnchantedItemInUse(var1, var4, var2, var7);
      runIterationOnItem(var1, (var5x, var6x) -> {
         ((Enchantment)var5x.value()).onHitBlock(var0, var6x, var8, var3, var5, var6);
      });
   }

   public static int modifyDurabilityToRepairFromXp(ServerLevel var0, ItemStack var1, int var2) {
      MutableFloat var3 = new MutableFloat((float)var2);
      runIterationOnItem(var1, (var3x, var4) -> {
         ((Enchantment)var3x.value()).modifyDurabilityToRepairFromXp(var0, var4, var1, var3);
      });
      return Math.max(0, var3.intValue());
   }

   public static float processEquipmentDropChance(ServerLevel var0, LivingEntity var1, DamageSource var2, float var3) {
      MutableFloat var4 = new MutableFloat(var3);
      RandomSource var5 = var1.getRandom();
      runIterationOnEquipment(var1, (var5x, var6x, var7x) -> {
         LootContext var8 = Enchantment.damageContext(var0, var6x, var1, var2);
         ((Enchantment)var5x.value()).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach((var4x) -> {
            if (var4x.enchanted() == EnchantmentTarget.VICTIM && var4x.affected() == EnchantmentTarget.VICTIM && var4x.matches(var8)) {
               var4.setValue(((EnchantmentValueEffect)var4x.effect()).process(var6x, var5, var4.floatValue()));
            }

         });
      });
      Entity var6 = var2.getEntity();
      if (var6 instanceof LivingEntity var7) {
         runIterationOnEquipment(var7, (var5x, var6x, var7x) -> {
            LootContext var8 = Enchantment.damageContext(var0, var6x, var1, var2);
            ((Enchantment)var5x.value()).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach((var4x) -> {
               if (var4x.enchanted() == EnchantmentTarget.ATTACKER && var4x.affected() == EnchantmentTarget.VICTIM && var4x.matches(var8)) {
                  var4.setValue(((EnchantmentValueEffect)var4x.effect()).process(var6x, var5, var4.floatValue()));
               }

            });
         });
      }

      return var4.floatValue();
   }

   public static void forEachModifier(ItemStack var0, EquipmentSlotGroup var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      runIterationOnItem(var0, (var2x, var3) -> {
         ((Enchantment)var2x.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach((var4) -> {
            if (((Enchantment)var2x.value()).definition().slots().contains(var1)) {
               var2.accept(var4.attribute(), var4.getModifier(var3, var1));
            }

         });
      });
   }

   public static void forEachModifier(ItemStack var0, EquipmentSlot var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      runIterationOnItem(var0, (var2x, var3) -> {
         ((Enchantment)var2x.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach((var4) -> {
            if (((Enchantment)var2x.value()).matchingSlot(var1)) {
               var2.accept(var4.attribute(), var4.getModifier(var3, var1));
            }

         });
      });
   }

   public static int getFishingLuckBonus(ServerLevel var0, ItemStack var1, Entity var2) {
      MutableFloat var3 = new MutableFloat(0.0F);
      runIterationOnItem(var1, (var4, var5) -> {
         ((Enchantment)var4.value()).modifyFishingLuckBonus(var0, var5, var1, var2, var3);
      });
      return Math.max(0, var3.intValue());
   }

   public static float getFishingTimeReduction(ServerLevel var0, ItemStack var1, Entity var2) {
      MutableFloat var3 = new MutableFloat(0.0F);
      runIterationOnItem(var1, (var4, var5) -> {
         ((Enchantment)var4.value()).modifyFishingTimeReduction(var0, var5, var1, var2, var3);
      });
      return Math.max(0.0F, var3.floatValue());
   }

   public static int getTridentReturnToOwnerAcceleration(ServerLevel var0, ItemStack var1, Entity var2) {
      MutableFloat var3 = new MutableFloat(0.0F);
      runIterationOnItem(var1, (var4, var5) -> {
         ((Enchantment)var4.value()).modifyTridentReturnToOwnerAcceleration(var0, var5, var1, var2, var3);
      });
      return Math.max(0, var3.intValue());
   }

   public static float modifyCrossbowChargingTime(ItemStack var0, LivingEntity var1, float var2) {
      MutableFloat var3 = new MutableFloat(var2);
      runIterationOnItem(var0, (var2x, var3x) -> {
         ((Enchantment)var2x.value()).modifyCrossbowChargeTime(var1.getRandom(), var3x, var3);
      });
      return Math.max(0.0F, var3.floatValue());
   }

   public static float getTridentSpinAttackStrength(ItemStack var0, LivingEntity var1) {
      MutableFloat var2 = new MutableFloat(0.0F);
      runIterationOnItem(var0, (var2x, var3) -> {
         ((Enchantment)var2x.value()).modifyTridentSpinAttackStrength(var1.getRandom(), var3, var2);
      });
      return var2.floatValue();
   }

   public static boolean hasTag(ItemStack var0, TagKey<Enchantment> var1) {
      ItemEnchantments var2 = (ItemEnchantments)var0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      Iterator var3 = var2.entrySet().iterator();

      Holder var5;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         Object2IntMap.Entry var4 = (Object2IntMap.Entry)var3.next();
         var5 = (Holder)var4.getKey();
      } while(!var5.is(var1));

      return true;
   }

   public static boolean has(ItemStack var0, DataComponentType<?> var1) {
      MutableBoolean var2 = new MutableBoolean(false);
      runIterationOnItem(var0, (var2x, var3) -> {
         if (((Enchantment)var2x.value()).effects().has(var1)) {
            var2.setTrue();
         }

      });
      return var2.booleanValue();
   }

   public static <T> Optional<T> pickHighestLevel(ItemStack var0, DataComponentType<List<T>> var1) {
      Pair var2 = getHighestLevel(var0, var1);
      if (var2 != null) {
         List var3 = (List)var2.getFirst();
         int var4 = (Integer)var2.getSecond();
         return Optional.of(var3.get(Math.min(var4, var3.size()) - 1));
      } else {
         return Optional.empty();
      }
   }

   @Nullable
   public static <T> Pair<T, Integer> getHighestLevel(ItemStack var0, DataComponentType<T> var1) {
      MutableObject var2 = new MutableObject();
      runIterationOnItem(var0, (var2x, var3) -> {
         if (var2.getValue() == null || (Integer)((Pair)var2.getValue()).getSecond() < var3) {
            Object var4 = ((Enchantment)var2x.value()).effects().get(var1);
            if (var4 != null) {
               var2.setValue(Pair.of(var4, var3));
            }
         }

      });
      return (Pair)var2.getValue();
   }

   public static Optional<EnchantedItemInUse> getRandomItemWith(DataComponentType<?> var0, LivingEntity var1, Predicate<ItemStack> var2) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = EquipmentSlot.VALUES.iterator();

      while(true) {
         EquipmentSlot var5;
         ItemStack var6;
         do {
            if (!var4.hasNext()) {
               return Util.getRandomSafe(var3, var1.getRandom());
            }

            var5 = (EquipmentSlot)var4.next();
            var6 = var1.getItemBySlot(var5);
         } while(!var2.test(var6));

         ItemEnchantments var7 = (ItemEnchantments)var6.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
         Iterator var8 = var7.entrySet().iterator();

         while(var8.hasNext()) {
            Object2IntMap.Entry var9 = (Object2IntMap.Entry)var8.next();
            Holder var10 = (Holder)var9.getKey();
            if (((Enchantment)var10.value()).effects().has(var0) && ((Enchantment)var10.value()).matchingSlot(var5)) {
               var3.add(new EnchantedItemInUse(var6, var5, var1));
            }
         }
      }
   }

   public static int getEnchantmentCost(RandomSource var0, int var1, int var2, ItemStack var3) {
      Enchantable var4 = (Enchantable)var3.get(DataComponents.ENCHANTABLE);
      if (var4 == null) {
         return 0;
      } else {
         if (var2 > 15) {
            var2 = 15;
         }

         int var5 = var0.nextInt(8) + 1 + (var2 >> 1) + var0.nextInt(var2 + 1);
         if (var1 == 0) {
            return Math.max(var5 / 3, 1);
         } else {
            return var1 == 1 ? var5 * 2 / 3 + 1 : Math.max(var5, var2 * 2);
         }
      }
   }

   public static ItemStack enchantItem(RandomSource var0, ItemStack var1, int var2, RegistryAccess var3, Optional<? extends HolderSet<Enchantment>> var4) {
      return enchantItem(var0, var1, var2, (Stream)var4.map(HolderSet::stream).orElseGet(() -> {
         return var3.lookupOrThrow(Registries.ENCHANTMENT).listElements().map((var0) -> {
            return var0;
         });
      }));
   }

   public static ItemStack enchantItem(RandomSource var0, ItemStack var1, int var2, Stream<Holder<Enchantment>> var3) {
      List var4 = selectEnchantment(var0, var1, var2, var3);
      if (var1.is(Items.BOOK)) {
         var1 = new ItemStack(Items.ENCHANTED_BOOK);
      }

      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         EnchantmentInstance var6 = (EnchantmentInstance)var5.next();
         var1.enchant(var6.enchantment, var6.level);
      }

      return var1;
   }

   public static List<EnchantmentInstance> selectEnchantment(RandomSource var0, ItemStack var1, int var2, Stream<Holder<Enchantment>> var3) {
      ArrayList var4 = Lists.newArrayList();
      Enchantable var5 = (Enchantable)var1.get(DataComponents.ENCHANTABLE);
      if (var5 == null) {
         return var4;
      } else {
         var2 += 1 + var0.nextInt(var5.value() / 4 + 1) + var0.nextInt(var5.value() / 4 + 1);
         float var6 = (var0.nextFloat() + var0.nextFloat() - 1.0F) * 0.15F;
         var2 = Mth.clamp(Math.round((float)var2 + (float)var2 * var6), 1, 2147483647);
         List var7 = getAvailableEnchantmentResults(var2, var1, var3);
         if (!var7.isEmpty()) {
            Optional var10000 = WeightedRandom.getRandomItem(var0, var7);
            Objects.requireNonNull(var4);
            var10000.ifPresent(var4::add);

            while(var0.nextInt(50) <= var2) {
               if (!var4.isEmpty()) {
                  filterCompatibleEnchantments(var7, (EnchantmentInstance)Util.lastOf(var4));
               }

               if (var7.isEmpty()) {
                  break;
               }

               var10000 = WeightedRandom.getRandomItem(var0, var7);
               Objects.requireNonNull(var4);
               var10000.ifPresent(var4::add);
               var2 /= 2;
            }
         }

         return var4;
      }
   }

   public static void filterCompatibleEnchantments(List<EnchantmentInstance> var0, EnchantmentInstance var1) {
      var0.removeIf((var1x) -> {
         return !Enchantment.areCompatible(var1.enchantment, var1x.enchantment);
      });
   }

   public static boolean isEnchantmentCompatible(Collection<Holder<Enchantment>> var0, Holder<Enchantment> var1) {
      Iterator var2 = var0.iterator();

      Holder var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Holder)var2.next();
      } while(Enchantment.areCompatible(var3, var1));

      return false;
   }

   public static List<EnchantmentInstance> getAvailableEnchantmentResults(int var0, ItemStack var1, Stream<Holder<Enchantment>> var2) {
      ArrayList var3 = Lists.newArrayList();
      boolean var4 = var1.is(Items.BOOK);
      var2.filter((var2x) -> {
         return ((Enchantment)var2x.value()).isPrimaryItem(var1) || var4;
      }).forEach((var2x) -> {
         Enchantment var3x = (Enchantment)var2x.value();

         for(int var4 = var3x.getMaxLevel(); var4 >= var3x.getMinLevel(); --var4) {
            if (var0 >= var3x.getMinCost(var4) && var0 <= var3x.getMaxCost(var4)) {
               var3.add(new EnchantmentInstance(var2x, var4));
               break;
            }
         }

      });
      return var3;
   }

   public static void enchantItemFromProvider(ItemStack var0, RegistryAccess var1, ResourceKey<EnchantmentProvider> var2, DifficultyInstance var3, RandomSource var4) {
      EnchantmentProvider var5 = (EnchantmentProvider)var1.lookupOrThrow(Registries.ENCHANTMENT_PROVIDER).getValue(var2);
      if (var5 != null) {
         updateEnchantments(var0, (var4x) -> {
            var5.enchant(var0, var4x, var4, var3);
         });
      }

   }

   @FunctionalInterface
   interface EnchantmentVisitor {
      void accept(Holder<Enchantment> var1, int var2);
   }

   @FunctionalInterface
   private interface EnchantmentInSlotVisitor {
      void accept(Holder<Enchantment> var1, int var2, EnchantedItemInUse var3);
   }
}
