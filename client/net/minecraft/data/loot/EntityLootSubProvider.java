package net.minecraft.data.loot;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityEquipmentPredicate;
import net.minecraft.advancements.predicates.entity.EntityFlagsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.SheepPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

public abstract class EntityLootSubProvider implements LootTableSubProvider {
   protected final LootTableSubProvider.Context output;
   protected final HolderGetter<Enchantment> enchantments;
   protected final HolderGetter<EntityType<?>> entityTypes;
   protected final HolderGetter<FrogVariant> frogVariants;
   private final FeatureFlagSet allowed;
   private final FeatureFlagSet required;
   private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map;

   protected EntityLootSubProvider(final FeatureFlagSet enabledFeatures, final LootTableSubProvider.Context output) {
      this(enabledFeatures, enabledFeatures, output);
   }

   protected EntityLootSubProvider(final FeatureFlagSet allowed, final FeatureFlagSet required, final LootTableSubProvider.Context output) {
      super();
      this.map = Maps.newHashMap();
      this.allowed = allowed;
      this.required = required;
      this.output = output;
      this.enchantments = output.lookup(Registries.ENCHANTMENT);
      this.entityTypes = output.lookup(Registries.ENTITY_TYPE);
      this.frogVariants = output.lookup(Registries.FROG_VARIANT);
   }

   protected final AnyOfCondition.Builder shouldSmeltLoot() {
      return AnyOfCondition.anyOf(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(this.enchantments.getOrThrow(EnchantmentTags.SMELTS_LOOT), MinMaxBounds.Ints.ANY)))).build())))));
   }

   public static LootPool.Builder createSheepDispatchPool(final ColorCollection<ResourceKey<LootTable>> tableNames) {
      AlternativesEntry.Builder variants = AlternativesEntry.alternatives();

      for(DyeColor color : DyeColor.VALUES) {
         variants = variants.otherwise(NestedLootTable.lootTableReference(tableNames.pick(color)).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().components(DataComponentExactPredicate.expect(DataComponents.SHEEP_COLOR, color)).sheep(SheepPredicate.hasWool()))));
      }

      return LootPool.lootPool().add(variants);
   }

   public abstract void generate();

   public void run() {
      this.generate();
      Set<ResourceKey<LootTable>> seen = new HashSet();
      this.output.listContextElements(Registries.ENTITY_TYPE).forEach((holder) -> {
         EntityType<?> type = (EntityType)holder.value();
         if (type.isEnabled(this.allowed)) {
            Optional<ResourceKey<LootTable>> defaultLootTable = type.getDefaultLootTable();
            if (defaultLootTable.isPresent()) {
               Map<ResourceKey<LootTable>, LootTable.Builder> builders = (Map)this.map.remove(type);
               if (type.isEnabled(this.required) && (builders == null || !builders.containsKey(defaultLootTable.get()))) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", defaultLootTable.get(), holder.key().identifier()));
               }

               if (builders != null) {
                  builders.forEach((id, builder) -> {
                     if (!seen.add(id)) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", id, holder.key().identifier()));
                     } else {
                        this.output.accept(id, builder);
                     }
                  });
               }
            } else {
               Map<ResourceKey<LootTable>, LootTable.Builder> builders = (Map)this.map.remove(type);
               if (builders != null) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", builders.keySet().stream().map((r) -> r.identifier().toString()).collect(Collectors.joining(",")), holder.key().identifier()));
               }
            }

         }
      });
      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + String.valueOf(this.map.keySet()));
      }
   }

   protected LootItemCondition.Builder killedByFrog() {
      return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.FROG)));
   }

   protected LootItemCondition.Builder killedByFrogVariant(final ResourceKey<FrogVariant> variant) {
      return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.FROG).components(DataComponentExactPredicate.expect(DataComponents.FROG_VARIANT, this.frogVariants.getOrThrow(variant)))));
   }

   protected void add(final EntityType<?> type, final LootTable.Builder builder) {
      this.add(type, (ResourceKey)type.getDefaultLootTable().orElseThrow(() -> new IllegalStateException("Entity " + String.valueOf(type) + " has no loot table")), builder);
   }

   protected void add(final EntityType<?> type, final ResourceKey<LootTable> lootTable, final LootTable.Builder builder) {
      ((Map)this.map.computeIfAbsent(type, (k) -> new HashMap())).put(lootTable, builder);
   }
}
