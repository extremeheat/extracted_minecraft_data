package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class EntityLootSubProvider implements LootTableSubProvider {
   protected static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity()
      .flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build());
   private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(
      EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER
   );
   private final FeatureFlagSet enabledFeatures;
   private final Map<EntityType<?>, Map<ResourceLocation, LootTable.Builder>> map = Maps.newHashMap();

   protected EntityLootSubProvider(FeatureFlagSet var1) {
      super();
      this.enabledFeatures = var1;
   }

   protected static LootTable.Builder createSheepTable(ItemLike var0) {
      return LootTable.lootTable()
         .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var0)))
         .withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootTableReference.lootTableReference(EntityType.SHEEP.getDefaultLootTable()))
         );
   }

   public abstract void generate();

   @Override
   public void generate(BiConsumer<ResourceLocation, LootTable.Builder> var1) {
      this.generate();
      HashSet var2 = Sets.newHashSet();
      BuiltInRegistries.ENTITY_TYPE
         .holders()
         .forEach(
            var3 -> {
               EntityType var4 = var3.value();
               if (var4.isEnabled(this.enabledFeatures)) {
                  if (canHaveLootTable(var4)) {
                     Map var5 = this.map.remove(var4);
                     ResourceLocation var6 = var4.getDefaultLootTable();
                     if (!var6.equals(BuiltInLootTables.EMPTY) && (var5 == null || !var5.containsKey(var6))) {
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
                     Map var7 = this.map.remove(var4);
                     if (var7 != null) {
                        throw new IllegalStateException(
                           String.format(
                              Locale.ROOT,
                              "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                              var7.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")),
                              var3.key().location()
                           )
                        );
                     }
                  }
               }
            }
         );
      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
      }
   }

   private static boolean canHaveLootTable(EntityType<?> var0) {
      return SPECIAL_LOOT_TABLE_TYPES.contains(var0) || var0.getCategory() != MobCategory.MISC;
   }

   protected LootItemCondition.Builder killedByFrog() {
      return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
   }

   protected LootItemCondition.Builder killedByFrogVariant(FrogVariant var1) {
      return DamageSourceCondition.hasDamageSource(
         DamageSourcePredicate.Builder.damageType()
            .source(EntityPredicate.Builder.entity().of(EntityType.FROG).subPredicate(EntitySubPredicate.variant(var1)))
      );
   }

   protected void add(EntityType<?> var1, LootTable.Builder var2) {
      this.add(var1, var1.getDefaultLootTable(), var2);
   }

   protected void add(EntityType<?> var1, ResourceLocation var2, LootTable.Builder var3) {
      this.map.computeIfAbsent(var1, var0 -> new HashMap()).put(var2, var3);
   }
}
