package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class MobSpawnSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1F;
   public static final WeightedList<SpawnerData> EMPTY_MOB_LIST = WeightedList.<SpawnerData>of();
   public static final MobSpawnSettings EMPTY = (new Builder()).build();
   public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec((i) -> {
      RecordCodecBuilder var10001 = Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((b) -> b.creatureGenerationProbability);
      Codec var10002 = MobCategory.CODEC;
      Codec var10003 = WeightedList.codec(MobSpawnSettings.SpawnerData.CODEC);
      Logger var10005 = LOGGER;
      Objects.requireNonNull(var10005);
      return i.group(var10001, Codec.simpleMap(var10002, var10003.promotePartial(Util.prefix("Spawn data: ", var10005::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter((b) -> b.spawners), Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((b) -> b.mobSpawnCosts)).apply(i, MobSpawnSettings::new);
   });
   private final float creatureGenerationProbability;
   private final Map<MobCategory, WeightedList<SpawnerData>> spawners;
   private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;

   private MobSpawnSettings(final float creatureGenerationProbability, final Map<MobCategory, WeightedList<SpawnerData>> spawners, final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts) {
      super();
      this.creatureGenerationProbability = creatureGenerationProbability;
      this.spawners = ImmutableMap.copyOf(spawners);
      this.mobSpawnCosts = ImmutableMap.copyOf(mobSpawnCosts);
   }

   public WeightedList<SpawnerData> getMobs(final MobCategory category) {
      return (WeightedList)this.spawners.getOrDefault(category, EMPTY_MOB_LIST);
   }

   public @Nullable MobSpawnCost getMobSpawnCost(final EntityType<?> type) {
      return (MobSpawnCost)this.mobSpawnCosts.get(type);
   }

   public float getCreatureProbability() {
      return this.creatureGenerationProbability;
   }

   public static record SpawnerData(EntityType<?> type, int minCount, int maxCount) {
      public static final MapCodec<SpawnerData> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter((d) -> d.type), ExtraCodecs.POSITIVE_INT.fieldOf("minCount").forGetter((e) -> e.minCount), ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter((e) -> e.maxCount)).apply(i, SpawnerData::new)).validate((spawnerData) -> spawnerData.minCount > spawnerData.maxCount ? DataResult.error(() -> "minCount needs to be smaller or equal to maxCount") : DataResult.success(spawnerData));

      public SpawnerData(EntityType<?> type, int minCount, int maxCount) {
         super();
         type = type.getCategory() == MobCategory.MISC ? EntityTypes.PIG : type;
         this.type = type;
         this.minCount = minCount;
         this.maxCount = maxCount;
      }

      public String toString() {
         String var10000 = String.valueOf(EntityType.getKey(this.type));
         return var10000 + "*(" + this.minCount + "-" + this.maxCount + ")";
      }
   }

   public static record MobSpawnCost(double energyBudget, double charge) {
      public static final Codec<MobSpawnCost> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((e) -> e.energyBudget), Codec.DOUBLE.fieldOf("charge").forGetter((e) -> e.charge)).apply(i, MobSpawnCost::new));

      public MobSpawnCost {
         super();
      }
   }

   public static class Builder {
      private final Map<MobCategory, WeightedList.Builder<SpawnerData>> spawners = Util.<MobCategory, WeightedList.Builder<SpawnerData>>makeEnumMap(MobCategory.class, (c) -> WeightedList.builder());
      private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
      private float creatureGenerationProbability = 0.1F;

      public Builder() {
         super();
      }

      public Builder addSpawn(final MobCategory category, final int weight, final SpawnerData spawnerData) {
         ((WeightedList.Builder)this.spawners.get(category)).add(spawnerData, weight);
         return this;
      }

      public Builder addMobCharge(final EntityType<?> type, final double charge, final double energyBudget) {
         this.mobSpawnCosts.put(type, new MobSpawnCost(energyBudget, charge));
         return this;
      }

      public Builder creatureGenerationProbability(final float creatureGenerationProbability) {
         this.creatureGenerationProbability = creatureGenerationProbability;
         return this;
      }

      public MobSpawnSettings build() {
         return new MobSpawnSettings(this.creatureGenerationProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (e) -> ((WeightedList.Builder)e.getValue()).build())), ImmutableMap.copyOf(this.mobSpawnCosts));
      }
   }
}
