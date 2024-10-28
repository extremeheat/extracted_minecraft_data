package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class EntityLootSubProvider implements LootTableSubProvider {
   private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES;
   protected final HolderLookup.Provider registries;
   private final FeatureFlagSet allowed;
   private final FeatureFlagSet required;
   private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map;

   protected final AnyOfCondition.Builder shouldSmeltLoot() {
      HolderLookup.RegistryLookup var1 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return AnyOfCondition.anyOf(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(var1.getOrThrow(EnchantmentTags.SMELTS_LOOT), MinMaxBounds.Ints.ANY))))))));
   }

   protected EntityLootSubProvider(FeatureFlagSet var1, HolderLookup.Provider var2) {
      this(var1, var1, var2);
   }

   protected EntityLootSubProvider(FeatureFlagSet var1, FeatureFlagSet var2, HolderLookup.Provider var3) {
      super();
      this.map = Maps.newHashMap();
      this.allowed = var1;
      this.required = var2;
      this.registries = var3;
   }

   protected static LootTable.Builder createSheepTable(ItemLike var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var0))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(EntityType.SHEEP.getDefaultLootTable())));
   }

   public abstract void generate();

   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> var1) {
      this.generate();
      HashSet var2 = new HashSet();
      BuiltInRegistries.ENTITY_TYPE.holders().forEach((var3) -> {
         EntityType var4 = (EntityType)var3.value();
         if (var4.isEnabled(this.allowed)) {
            Map var5;
            if (canHaveLootTable(var4)) {
               var5 = (Map)this.map.remove(var4);
               ResourceKey var6 = var4.getDefaultLootTable();
               if (var6 != BuiltInLootTables.EMPTY && var4.isEnabled(this.required) && (var5 == null || !var5.containsKey(var6))) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", var6, var3.key().location()));
               }

               if (var5 != null) {
                  var5.forEach((var3x, var4x) -> {
                     if (!var2.add(var3x)) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", var3x, var3.key().location()));
                     } else {
                        var1.accept(var3x, var4x);
                     }
                  });
               }
            } else {
               var5 = (Map)this.map.remove(var4);
               if (var5 != null) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", var5.keySet().stream().map((var0) -> {
                     return var0.location().toString();
                  }).collect(Collectors.joining(",")), var3.key().location()));
               }
            }

         }
      });
      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + String.valueOf(this.map.keySet()));
      }
   }

   private static boolean canHaveLootTable(EntityType<?> var0) {
      return SPECIAL_LOOT_TABLE_TYPES.contains(var0) || var0.getCategory() != MobCategory.MISC;
   }

   protected LootItemCondition.Builder killedByFrog() {
      return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
   }

   protected LootItemCondition.Builder killedByFrogVariant(ResourceKey<FrogVariant> var1) {
      return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG).subPredicate(EntitySubPredicates.frogVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(var1)))));
   }

   protected void add(EntityType<?> var1, LootTable.Builder var2) {
      this.add(var1, var1.getDefaultLootTable(), var2);
   }

   protected void add(EntityType<?> var1, ResourceKey<LootTable> var2, LootTable.Builder var3) {
      ((Map)this.map.computeIfAbsent(var1, (var0) -> {
         return new HashMap();
      })).put(var2, var3);
   }

   static {
      SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
   }
}
