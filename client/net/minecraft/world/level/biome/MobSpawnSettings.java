package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class MobSpawnSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final float DEFAULT_CREATURE_WORLD_GEN_SPAWN_PROBABILITY = 0.1F;
   public static final WeightedList<SpawnerData> EMPTY_MOB_LIST = WeightedList.<SpawnerData>of();
   public static final MobSpawnSettings EMPTY = (new Builder()).build();
   public static final MobSpawnSettings NO_SPAWNS = (MobSpawnSettings)Util.make(() -> {
      Builder builder = new Builder();

      for(MobCategory category : MobCategory.values()) {
         builder.noSpawns(category);
      }

      return builder.build();
   });
   public static final Codec<MobSpawnSettings> CODEC = RecordCodecBuilder.create((i) -> {
      Codec var10001 = MobCategory.CODEC;
      Codec var10002 = WeightedList.codec(MobSpawnSettings.SpawnerData.CODEC);
      Logger var10004 = LOGGER;
      Objects.requireNonNull(var10004);
      return i.group(Codec.simpleMap(var10001, var10002.promotePartial(Util.prefix("Spawn data: ", var10004::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawns_by_category").forGetter((b) -> b.spawnsByCategory), Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((b) -> b.mobSpawnCosts)).apply(i, MobSpawnSettings::new);
   });
   private final Map<MobCategory, WeightedList<SpawnerData>> spawnsByCategory;
   private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;

   private MobSpawnSettings(final Map<MobCategory, WeightedList<SpawnerData>> spawnsByCategory, final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts) {
      super();
      this.spawnsByCategory = ImmutableMap.copyOf(spawnsByCategory);
      this.mobSpawnCosts = ImmutableMap.copyOf(mobSpawnCosts);
   }

   public WeightedList<SpawnerData> getMobsToSpawn(final MobCategory category) {
      return (WeightedList)this.spawnsByCategory.getOrDefault(category, EMPTY_MOB_LIST);
   }

   public @Nullable WeightedList<SpawnerData> getMobsInCategory(final MobCategory category) {
      return (WeightedList)this.spawnsByCategory.get(category);
   }

   public Set<MobCategory> definedCategories() {
      return this.spawnsByCategory.keySet();
   }

   public @Nullable MobSpawnCost getMobSpawnCost(final EntityType<?> type) {
      return (MobSpawnCost)this.mobSpawnCosts.get(type);
   }

   public Map<EntityType<?>, MobSpawnCost> allSpawnCosts() {
      return this.mobSpawnCosts;
   }

   public static record SpawnerData(EntityType<?> type, IntProvider count) {
      public static final MapCodec<SpawnerData> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(SpawnerData::type), IntProviders.CODEC.fieldOf("count").forGetter(SpawnerData::count)).apply(i, SpawnerData::new));

      public SpawnerData(EntityType<?> type, IntProvider count) {
         super();
         type = type.getCategory() == MobCategory.MISC ? EntityTypes.PIG : type;
         this.type = type;
         this.count = count;
      }

      public String toString() {
         String var10000 = String.valueOf(EntityType.getKey(this.type));
         return var10000 + "*(" + this.count.minInclusive() + "-" + this.count.maxInclusive() + ") (" + String.valueOf(BuiltInRegistries.INT_PROVIDER_TYPE.getKey(this.count.codec())) + ")";
      }
   }

   public static record MobSpawnCost(double energyBudget, double charge) {
      public static final Codec<MobSpawnCost> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((e) -> e.energyBudget), Codec.DOUBLE.fieldOf("charge").forGetter((e) -> e.charge)).apply(i, MobSpawnCost::new));

      public MobSpawnCost {
         super();
      }
   }

   public static class Builder {
      private final Map<MobCategory, WeightedList.Builder<SpawnerData>> spawnsByCategory = new EnumMap(MobCategory.class);
      private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();

      public Builder() {
         super();
      }

      public Builder addSpawn(final EntityType<?> type, final int weight, final int minCount, final int maxCount) {
         IntProvider count;
         if (minCount == maxCount) {
            count = new ConstantInt(minCount);
         } else {
            count = new UniformInt(minCount, maxCount);
         }

         this.addSpawn(type, type.getCategory(), weight, count);
         return this;
      }

      public Builder addSpawn(final EntityType<?> type, final int weight, final IntProvider count) {
         this.addSpawn(type, type.getCategory(), weight, count);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder addSpawn(final EntityType<?> type, final MobCategory category, final int weight, final IntProvider count) {
         this.forCategory(category).add(new SpawnerData(type, count), weight);
         return this;
      }

      public Builder addAllSpawns(final MobCategory category, final WeightedList<SpawnerData> spawns) {
         this.forCategory(category).addAll(spawns);
         return this;
      }

      public Builder noSpawns(final MobCategory category) {
         this.spawnsByCategory.put(category, WeightedList.builder());
         return this;
      }

      public Builder dontOverride(final MobCategory category) {
         this.spawnsByCategory.remove(category);
         return this;
      }

      public Builder addMobSpawnCost(final EntityType<?> type, final double charge, final double energyBudget) {
         this.mobSpawnCosts.put(type, new MobSpawnCost(energyBudget, charge));
         return this;
      }

      public Builder addAllCosts(final Map<EntityType<?>, MobSpawnCost> costs) {
         this.mobSpawnCosts.putAll(costs);
         return this;
      }

      public MobSpawnSettings build() {
         return new MobSpawnSettings((Map)this.spawnsByCategory.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (e) -> ((WeightedList.Builder)e.getValue()).build())), ImmutableMap.copyOf(this.mobSpawnCosts));
      }

      private WeightedList.Builder<SpawnerData> forCategory(final MobCategory category) {
         return (WeightedList.Builder)this.spawnsByCategory.computeIfAbsent(category, (var0) -> WeightedList.builder());
      }
   }
}
