package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
import net.minecraft.util.Util;
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
import net.minecraft.world.item.ItemInstance;
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
import org.jspecify.annotations.Nullable;

public class EnchantmentHelper {
   public EnchantmentHelper() {
      super();
   }

   public static int getItemEnchantmentLevel(final Holder<Enchantment> enchantment, final ItemInstance piece) {
      ItemEnchantments enchantments = (ItemEnchantments)piece.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      return enchantments.getLevel(enchantment);
   }

   public static ItemEnchantments updateEnchantments(final ItemStack itemStack, final Consumer<ItemEnchantments.Mutable> consumer) {
      DataComponentType<ItemEnchantments> componentType = getComponentType(itemStack);
      ItemEnchantments oldEnchantments = (ItemEnchantments)itemStack.get(componentType);
      if (oldEnchantments == null) {
         return ItemEnchantments.EMPTY;
      } else {
         ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(oldEnchantments);
         consumer.accept(mutableEnchantments);
         ItemEnchantments newEnchantments = mutableEnchantments.toImmutable();
         itemStack.set(componentType, newEnchantments);
         return newEnchantments;
      }
   }

   public static boolean canStoreEnchantments(final ItemStack itemStack) {
      return itemStack.has(getComponentType(itemStack));
   }

   public static void setEnchantments(final ItemStack itemStack, final ItemEnchantments enchantments) {
      itemStack.set(getComponentType(itemStack), enchantments);
   }

   public static ItemEnchantments getEnchantmentsForCrafting(final ItemStack itemStack) {
      return (ItemEnchantments)itemStack.getOrDefault(getComponentType(itemStack), ItemEnchantments.EMPTY);
   }

   private static DataComponentType<ItemEnchantments> getComponentType(final ItemStack itemStack) {
      return itemStack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
   }

   public static boolean hasAnyEnchantments(final ItemStack itemStack) {
      return !((ItemEnchantments)itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)).isEmpty() || !((ItemEnchantments)itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)).isEmpty();
   }

   public static int processDurabilityChange(final ServerLevel serverLevel, final ItemStack itemStack, final int amount) {
      MutableFloat modifiedAmount = new MutableFloat((float)amount);
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyDurabilityChange(serverLevel, level, itemStack, modifiedAmount));
      return modifiedAmount.intValue();
   }

   public static int processAmmoUse(final ServerLevel serverLevel, final ItemStack weapon, final ItemStack ammo, final int amount) {
      MutableFloat modifiedAmount = new MutableFloat((float)amount);
      runIterationOnItem(weapon, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyAmmoCount(serverLevel, level, ammo, modifiedAmount));
      return modifiedAmount.intValue();
   }

   public static int processBlockExperience(final ServerLevel serverLevel, final ItemStack itemStack, final int amount) {
      MutableFloat modifiedAmount = new MutableFloat((float)amount);
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyBlockExperience(serverLevel, level, itemStack, modifiedAmount));
      return modifiedAmount.intValue();
   }

   public static int processMobExperience(final ServerLevel serverLevel, final @Nullable Entity killer, final Entity killed, final int amount) {
      if (killer instanceof LivingEntity livingKiller) {
         MutableFloat modifiedAmount = new MutableFloat((float)amount);
         runIterationOnEquipment(livingKiller, (enchantment, level, item) -> ((Enchantment)enchantment.value()).modifyMobExperience(serverLevel, level, item.itemStack(), killed, modifiedAmount));
         return modifiedAmount.intValue();
      } else {
         return amount;
      }
   }

   public static ItemStack createBook(final EnchantmentInstance enchant) {
      ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
      itemStack.enchant(enchant.enchantment(), enchant.level());
      return itemStack;
   }

   private static void runIterationOnItem(final ItemStack piece, final EnchantmentVisitor method) {
      ItemEnchantments enchantments = (ItemEnchantments)piece.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

      for(Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
         method.accept((Holder)entry.getKey(), entry.getIntValue());
      }

   }

   private static void runIterationOnItem(final ItemStack piece, final EquipmentSlot slot, final LivingEntity owner, final EnchantmentInSlotVisitor method) {
      if (!piece.isEmpty()) {
         ItemEnchantments itemEnchantments = (ItemEnchantments)piece.get(DataComponents.ENCHANTMENTS);
         if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
            EnchantedItemInUse itemInUse = new EnchantedItemInUse(piece, slot, owner);

            for(Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
               Holder<Enchantment> enchantment = (Holder)entry.getKey();
               if (((Enchantment)enchantment.value()).matchingSlot(slot)) {
                  method.accept(enchantment, entry.getIntValue(), itemInUse);
               }
            }

         }
      }
   }

   private static void runIterationOnEquipment(final LivingEntity owner, final EnchantmentInSlotVisitor method) {
      for(EquipmentSlot slot : EquipmentSlot.VALUES) {
         runIterationOnItem(owner.getItemBySlot(slot), slot, owner, method);
      }

   }

   public static boolean isImmuneToDamage(final ServerLevel serverLevel, final LivingEntity victim, final DamageSource source) {
      MutableBoolean result = new MutableBoolean();
      runIterationOnEquipment(victim, (enchantment, level, item) -> result.setValue(result.isTrue() || ((Enchantment)enchantment.value()).isImmuneToDamage(serverLevel, level, victim, source)));
      return result.isTrue();
   }

   public static float getDamageProtection(final ServerLevel serverLevel, final LivingEntity victim, final DamageSource source) {
      MutableFloat result = new MutableFloat(0.0F);
      runIterationOnEquipment(victim, (enchantment, level, item) -> ((Enchantment)enchantment.value()).modifyDamageProtection(serverLevel, level, item.itemStack(), victim, source, result));
      return result.floatValue();
   }

   public static float modifyDamage(final ServerLevel serverLevel, final ItemStack itemStack, final Entity victim, final DamageSource damageSource, final float damage) {
      MutableFloat result = new MutableFloat(damage);
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyDamage(serverLevel, level, itemStack, victim, damageSource, result));
      return result.floatValue();
   }

   public static float modifyFallBasedDamage(final ServerLevel serverLevel, final ItemStack itemStack, final Entity victim, final DamageSource damageSource, final float damage) {
      MutableFloat result = new MutableFloat(damage);
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyFallBasedDamage(serverLevel, level, itemStack, victim, damageSource, result));
      return result.floatValue();
   }

   public static float modifyArmorEffectiveness(final ServerLevel serverLevel, final ItemStack itemStack, final Entity victim, final DamageSource damageSource, final float armorFraction) {
      MutableFloat result = new MutableFloat(armorFraction);
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyArmorEffectivness(serverLevel, level, itemStack, victim, damageSource, result));
      return result.floatValue();
   }

   public static float modifyKnockback(final ServerLevel serverLevel, final ItemStack itemStack, final Entity victim, final DamageSource damageSource, final float knockback) {
      MutableFloat result = new MutableFloat(knockback);
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyKnockback(serverLevel, level, itemStack, victim, damageSource, result));
      return result.floatValue();
   }

   public static void doPostAttackEffects(final ServerLevel serverLevel, final Entity victim, final DamageSource damageSource) {
      Entity var4 = damageSource.getEntity();
      if (var4 instanceof LivingEntity attacker) {
         doPostAttackEffectsWithItemSource(serverLevel, victim, damageSource, attacker.getWeaponItem());
      } else {
         doPostAttackEffectsWithItemSource(serverLevel, victim, damageSource, (ItemStack)null);
      }

   }

   public static void doPostPiercingAttackEffects(final ServerLevel serverLevel, final LivingEntity user) {
      runIterationOnItem(user.getWeaponItem(), EquipmentSlot.MAINHAND, user, (enchantment, level, item) -> ((Enchantment)enchantment.value()).doPostPiercingAttack(serverLevel, level, item, user));
   }

   public static void doPostAttackEffectsWithItemSource(final ServerLevel serverLevel, final Entity victim, final DamageSource damageSource, final @Nullable ItemStack source) {
      doPostAttackEffectsWithItemSourceOnBreak(serverLevel, victim, damageSource, source, (Consumer)null);
   }

   public static void doPostAttackEffectsWithItemSourceOnBreak(final ServerLevel serverLevel, final Entity victim, final DamageSource damageSource, final @Nullable ItemStack source, final @Nullable Consumer<ItemStack> attackerlessOnBreak) {
      if (victim instanceof LivingEntity livingVictim) {
         runIterationOnEquipment(livingVictim, (enchantment, level, item) -> ((Enchantment)enchantment.value()).doPostAttack(serverLevel, level, item, EnchantmentTarget.VICTIM, victim, damageSource));
      }

      if (source != null) {
         Entity var6 = damageSource.getEntity();
         if (var6 instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity)var6;
            runIterationOnItem(source, EquipmentSlot.MAINHAND, attacker, (enchantment, level, item) -> ((Enchantment)enchantment.value()).doPostAttack(serverLevel, level, item, EnchantmentTarget.ATTACKER, victim, damageSource));
         } else if (attackerlessOnBreak != null) {
            EnchantedItemInUse item = new EnchantedItemInUse(source, (EquipmentSlot)null, (LivingEntity)null, attackerlessOnBreak);
            runIterationOnItem(source, (enchantment, level) -> ((Enchantment)enchantment.value()).doPostAttack(serverLevel, level, item, EnchantmentTarget.ATTACKER, victim, damageSource));
         }
      }

   }

   public static void runLocationChangedEffects(final ServerLevel serverLevel, final LivingEntity entity) {
      runIterationOnEquipment(entity, (enchantment, level, item) -> ((Enchantment)enchantment.value()).runLocationChangedEffects(serverLevel, level, item, entity));
   }

   public static void runLocationChangedEffects(final ServerLevel serverLevel, final ItemStack stack, final LivingEntity entity, final EquipmentSlot slot) {
      runIterationOnItem(stack, slot, entity, (enchantment, level, item) -> ((Enchantment)enchantment.value()).runLocationChangedEffects(serverLevel, level, item, entity));
   }

   public static void stopLocationBasedEffects(final LivingEntity entity) {
      runIterationOnEquipment(entity, (enchantment, level, item) -> ((Enchantment)enchantment.value()).stopLocationBasedEffects(level, item, entity));
   }

   public static void stopLocationBasedEffects(final ItemStack stack, final LivingEntity entity, final EquipmentSlot slot) {
      runIterationOnItem(stack, slot, entity, (enchantment, level, item) -> ((Enchantment)enchantment.value()).stopLocationBasedEffects(level, item, entity));
   }

   public static void tickEffects(final ServerLevel serverLevel, final LivingEntity entity) {
      runIterationOnEquipment(entity, (enchantment, level, item) -> ((Enchantment)enchantment.value()).tick(serverLevel, level, item, entity));
   }

   public static int getEnchantmentLevel(final Holder<Enchantment> enchantment, final LivingEntity entity) {
      Iterable<ItemStack> allowedSlots = ((Enchantment)enchantment.value()).getSlotItems(entity).values();
      int bestLevel = 0;

      for(ItemStack piece : allowedSlots) {
         int newLevel = getItemEnchantmentLevel(enchantment, piece);
         if (newLevel > bestLevel) {
            bestLevel = newLevel;
         }
      }

      return bestLevel;
   }

   public static int processProjectileCount(final ServerLevel serverLevel, final ItemStack weapon, final Entity shooter, final int count) {
      MutableFloat modifiedCount = new MutableFloat((float)count);
      runIterationOnItem(weapon, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyProjectileCount(serverLevel, level, weapon, shooter, modifiedCount));
      return Math.max(0, modifiedCount.intValue());
   }

   public static float processProjectileSpread(final ServerLevel serverLevel, final ItemStack weapon, final Entity shooter, final float angle) {
      MutableFloat modifiedAngle = new MutableFloat(angle);
      runIterationOnItem(weapon, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyProjectileSpread(serverLevel, level, weapon, shooter, modifiedAngle));
      return Math.max(0.0F, modifiedAngle.floatValue());
   }

   public static int getPiercingCount(final ServerLevel serverLevel, final ItemStack weapon, final ItemStack ammo) {
      MutableFloat modifiedAmount = new MutableFloat(0.0F);
      runIterationOnItem(weapon, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyPiercingCount(serverLevel, level, ammo, modifiedAmount));
      return Math.max(0, modifiedAmount.intValue());
   }

   public static void onProjectileSpawned(final ServerLevel serverLevel, final ItemStack weapon, final Projectile projectileEntity, final Consumer<ItemStack> onBreak) {
      Entity var6 = projectileEntity.getOwner();
      LivingEntity var10000;
      if (var6 instanceof LivingEntity le) {
         var10000 = le;
      } else {
         var10000 = null;
      }

      LivingEntity owner = var10000;
      EnchantedItemInUse item = new EnchantedItemInUse(weapon, (EquipmentSlot)null, owner, onBreak);
      runIterationOnItem(weapon, (enchantment, level) -> ((Enchantment)enchantment.value()).onProjectileSpawned(serverLevel, level, item, projectileEntity));
   }

   public static void onHitBlock(final ServerLevel serverLevel, final ItemStack weapon, final @Nullable LivingEntity owner, final Entity entity, final @Nullable EquipmentSlot slot, final Vec3 hitLocation, final BlockState hitBlock, final Consumer<ItemStack> onBreak) {
      EnchantedItemInUse item = new EnchantedItemInUse(weapon, slot, owner, onBreak);
      runIterationOnItem(weapon, (enchantment, level) -> ((Enchantment)enchantment.value()).onHitBlock(serverLevel, level, item, entity, hitLocation, hitBlock));
   }

   public static int modifyDurabilityToRepairFromXp(final ServerLevel serverLevel, final ItemStack item, final int durability) {
      MutableFloat modifiedDurability = new MutableFloat((float)durability);
      runIterationOnItem(item, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyDurabilityToRepairFromXp(serverLevel, level, item, modifiedDurability));
      return Math.max(0, modifiedDurability.intValue());
   }

   public static float processEquipmentDropChance(final ServerLevel serverLevel, final LivingEntity entity, final DamageSource killingBlow, final float chance) {
      MutableFloat modifiedChance = new MutableFloat(chance);
      RandomSource random = entity.getRandom();
      runIterationOnEquipment(entity, (enchantment, level, item) -> {
         LootContext context = Enchantment.damageContext(serverLevel, level, entity, killingBlow);
         ((Enchantment)enchantment.value()).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach((filteredEffect) -> {
            if (filteredEffect.enchanted() == EnchantmentTarget.VICTIM && filteredEffect.affected() == EnchantmentTarget.VICTIM && filteredEffect.matches(context)) {
               modifiedChance.setValue(((EnchantmentValueEffect)filteredEffect.effect()).process(level, random, modifiedChance.floatValue()));
            }

         });
      });
      Entity attacker = killingBlow.getEntity();
      if (attacker instanceof LivingEntity livingAttacker) {
         runIterationOnEquipment(livingAttacker, (enchantment, level, item) -> {
            LootContext context = Enchantment.damageContext(serverLevel, level, entity, killingBlow);
            ((Enchantment)enchantment.value()).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach((filteredEffect) -> {
               if (filteredEffect.enchanted() == EnchantmentTarget.ATTACKER && filteredEffect.affected() == EnchantmentTarget.VICTIM && filteredEffect.matches(context)) {
                  modifiedChance.setValue(((EnchantmentValueEffect)filteredEffect.effect()).process(level, random, modifiedChance.floatValue()));
               }

            });
         });
      }

      return modifiedChance.floatValue();
   }

   public static void forEachModifier(final ItemStack itemStack, final EquipmentSlotGroup slot, final BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach((effect) -> {
            if (((Enchantment)enchantment.value()).definition().slots().contains(slot)) {
               consumer.accept(effect.attribute(), effect.getModifier(level, slot));
            }

         }));
   }

   public static void forEachModifier(final ItemStack itemStack, final EquipmentSlot slot, final BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
      runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach((effect) -> {
            if (((Enchantment)enchantment.value()).matchingSlot(slot)) {
               consumer.accept(effect.attribute(), effect.getModifier(level, slot));
            }

         }));
   }

   public static int getFishingLuckBonus(final ServerLevel serverLevel, final ItemStack rod, final Entity fisher) {
      MutableFloat modifiedSpeed = new MutableFloat(0.0F);
      runIterationOnItem(rod, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyFishingLuckBonus(serverLevel, level, rod, fisher, modifiedSpeed));
      return Math.max(0, modifiedSpeed.intValue());
   }

   public static float getFishingTimeReduction(final ServerLevel serverLevel, final ItemStack rod, final Entity fisher) {
      MutableFloat modifiedSpeed = new MutableFloat(0.0F);
      runIterationOnItem(rod, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyFishingTimeReduction(serverLevel, level, rod, fisher, modifiedSpeed));
      return Math.max(0.0F, modifiedSpeed.floatValue());
   }

   public static int getTridentReturnToOwnerAcceleration(final ServerLevel serverLevel, final ItemStack weapon, final Entity trident) {
      MutableFloat modifiedAcceleration = new MutableFloat(0.0F);
      runIterationOnItem(weapon, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyTridentReturnToOwnerAcceleration(serverLevel, level, weapon, trident, modifiedAcceleration));
      return Math.max(0, modifiedAcceleration.intValue());
   }

   public static float modifyCrossbowChargingTime(final ItemStack crossbow, final LivingEntity holder, final float time) {
      MutableFloat modifiedTime = new MutableFloat(time);
      runIterationOnItem(crossbow, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyCrossbowChargeTime(holder.getRandom(), level, modifiedTime));
      return Math.max(0.0F, modifiedTime.floatValue());
   }

   public static float getTridentSpinAttackStrength(final ItemStack trident, final LivingEntity holder) {
      MutableFloat strength = new MutableFloat(0.0F);
      runIterationOnItem(trident, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyTridentSpinAttackStrength(holder.getRandom(), level, strength));
      return strength.floatValue();
   }

   public static boolean hasTag(final ItemStack item, final TagKey<Enchantment> tag) {
      ItemEnchantments enchantments = (ItemEnchantments)item.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

      for(Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
         Holder<Enchantment> enchantment = (Holder)entry.getKey();
         if (enchantment.is(tag)) {
            return true;
         }
      }

      return false;
   }

   public static boolean has(final ItemStack item, final DataComponentType<?> effectType) {
      MutableBoolean found = new MutableBoolean(false);
      runIterationOnItem(item, (enchantment, level) -> {
         if (((Enchantment)enchantment.value()).effects().has(effectType)) {
            found.setTrue();
         }

      });
      return found.booleanValue();
   }

   public static <T> Optional<T> pickHighestLevel(final ItemStack itemStack, final DataComponentType<List<T>> componentType) {
      Pair<List<T>, Integer> picked = getHighestLevel(itemStack, componentType);
      if (picked != null) {
         List<T> list = (List)picked.getFirst();
         int enchantmentLevel = (Integer)picked.getSecond();
         return Optional.of(list.get(Math.min(enchantmentLevel, list.size()) - 1));
      } else {
         return Optional.empty();
      }
   }

   public static <T> Pair<T, Integer> getHighestLevel(final ItemStack item, final DataComponentType<T> effectType) {
      MutableObject<Pair<T, Integer>> found = new MutableObject();
      runIterationOnItem(item, (enchantment, level) -> {
         if (found.get() == null || (Integer)((Pair)found.get()).getSecond() < level) {
            T effect = (T)((Enchantment)enchantment.value()).effects().get(effectType);
            if (effect != null) {
               found.setValue(Pair.of(effect, level));
            }
         }

      });
      return (Pair)found.get();
   }

   public static Optional<EnchantedItemInUse> getRandomItemWith(final DataComponentType<?> componentType, final LivingEntity source, final Predicate<ItemStack> predicate) {
      List<EnchantedItemInUse> items = new ArrayList();

      for(EquipmentSlot slot : EquipmentSlot.VALUES) {
         ItemStack item = source.getItemBySlot(slot);
         if (predicate.test(item)) {
            ItemEnchantments enchantments = (ItemEnchantments)item.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

            for(Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
               Holder<Enchantment> enchantment = (Holder)entry.getKey();
               if (((Enchantment)enchantment.value()).effects().has(componentType) && ((Enchantment)enchantment.value()).matchingSlot(slot)) {
                  items.add(new EnchantedItemInUse(item, slot, source));
               }
            }
         }
      }

      return Util.<EnchantedItemInUse>getRandomSafe(items, source.getRandom());
   }

   public static int getEnchantmentCost(final RandomSource random, final int slot, int bookcases, final ItemStack itemStack) {
      Enchantable enchantable = (Enchantable)itemStack.get(DataComponents.ENCHANTABLE);
      if (enchantable == null) {
         return 0;
      } else {
         if (bookcases > 15) {
            bookcases = 15;
         }

         int selected = random.nextInt(8) + 1 + (bookcases >> 1) + random.nextInt(bookcases + 1);
         if (slot == 0) {
            return Math.max(selected / 3, 1);
         } else {
            return slot == 1 ? selected * 2 / 3 + 1 : Math.max(selected, bookcases * 2);
         }
      }
   }

   public static ItemStack enchantItem(final RandomSource random, final ItemStack itemStack, final int enchantmentCost, final RegistryAccess registryAccess, final Optional<? extends HolderSet<Enchantment>> set) {
      return enchantItem(random, itemStack, enchantmentCost, (Stream)set.map(HolderSet::stream).orElseGet(() -> registryAccess.lookupOrThrow(Registries.ENCHANTMENT).listElements().map((h) -> h)));
   }

   public static ItemStack enchantItem(final RandomSource random, ItemStack itemStack, final int enchantmentCost, final Stream<Holder<Enchantment>> source) {
      List<EnchantmentInstance> enchants = selectEnchantment(random, itemStack, enchantmentCost, source);
      if (itemStack.is(Items.BOOK)) {
         itemStack = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentInstance enchant : enchants) {
         itemStack.enchant(enchant.enchantment(), enchant.level());
      }

      return itemStack;
   }

   public static List<EnchantmentInstance> selectEnchantment(final RandomSource random, final ItemStack itemStack, int enchantmentCost, final Stream<Holder<Enchantment>> source) {
      List<EnchantmentInstance> results = Lists.newArrayList();
      Enchantable enchantable = (Enchantable)itemStack.get(DataComponents.ENCHANTABLE);
      if (enchantable == null) {
         return results;
      } else {
         enchantmentCost += 1 + random.nextInt(enchantable.value() / 4 + 1) + random.nextInt(enchantable.value() / 4 + 1);
         float randomSpan = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
         enchantmentCost = Mth.clamp(Math.round((float)enchantmentCost + (float)enchantmentCost * randomSpan), 1, 2147483647);
         List<EnchantmentInstance> enchantments = getAvailableEnchantmentResults(enchantmentCost, itemStack, source);
         if (!enchantments.isEmpty()) {
            Optional var10000 = WeightedRandom.getRandomItem(random, enchantments, EnchantmentInstance::weight);
            Objects.requireNonNull(results);
            var10000.ifPresent(results::add);

            while(random.nextInt(50) <= enchantmentCost) {
               if (!results.isEmpty()) {
                  filterCompatibleEnchantments(enchantments, (EnchantmentInstance)results.getLast());
               }

               if (enchantments.isEmpty()) {
                  break;
               }

               var10000 = WeightedRandom.getRandomItem(random, enchantments, EnchantmentInstance::weight);
               Objects.requireNonNull(results);
               var10000.ifPresent(results::add);
               enchantmentCost /= 2;
            }
         }

         return results;
      }
   }

   public static void filterCompatibleEnchantments(final List<EnchantmentInstance> enchants, final EnchantmentInstance target) {
      enchants.removeIf((e) -> !Enchantment.areCompatible(target.enchantment(), e.enchantment()));
   }

   public static boolean isEnchantmentCompatible(final Collection<Holder<Enchantment>> enchants, final Holder<Enchantment> target) {
      for(Holder<Enchantment> existing : enchants) {
         if (!Enchantment.areCompatible(existing, target)) {
            return false;
         }
      }

      return true;
   }

   public static List<EnchantmentInstance> getAvailableEnchantmentResults(final int value, final ItemStack itemStack, final Stream<Holder<Enchantment>> source) {
      List<EnchantmentInstance> results = Lists.newArrayList();
      boolean isBook = itemStack.is(Items.BOOK);
      source.filter((enchantment) -> ((Enchantment)enchantment.value()).isPrimaryItem(itemStack) || isBook).forEach((holder) -> {
         Enchantment enchantment = (Enchantment)holder.value();

         for(int level = enchantment.getMaxLevel(); level >= enchantment.getMinLevel(); --level) {
            if (value >= enchantment.getMinCost(level) && value <= enchantment.getMaxCost(level)) {
               results.add(new EnchantmentInstance(holder, level));
               break;
            }
         }

      });
      return results;
   }

   public static void enchantItemFromProvider(final ItemStack itemStack, final RegistryAccess registryAccess, final ResourceKey<EnchantmentProvider> providerKey, final DifficultyInstance difficulty, final RandomSource random) {
      EnchantmentProvider provider = (EnchantmentProvider)registryAccess.lookupOrThrow(Registries.ENCHANTMENT_PROVIDER).getValue(providerKey);
      if (provider != null) {
         updateEnchantments(itemStack, (enchantments) -> provider.enchant(itemStack, enchantments, random, difficulty));
      }

   }

   @FunctionalInterface
   private interface EnchantmentInSlotVisitor {
      void accept(Holder<Enchantment> enchantment, int level, EnchantedItemInUse item);
   }

   @FunctionalInterface
   private interface EnchantmentVisitor {
      void accept(Holder<Enchantment> enchantment, int level);
   }
}
