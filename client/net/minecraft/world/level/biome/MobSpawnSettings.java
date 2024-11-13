package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;

public class MobSpawnSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1F;
   public static final WeightedRandomList<SpawnerData> EMPTY_MOB_LIST = WeightedRandomList.<SpawnerData>create();
   public static final MobSpawnSettings EMPTY = (new Builder()).build();
   public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      RecordCodecBuilder var10001 = Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((var0x) -> var0x.creatureGenerationProbability);
      Codec var10002 = MobCategory.CODEC;
      Codec var10003 = WeightedRandomList.codec(MobSpawnSettings.SpawnerData.CODEC);
      Logger var10005 = LOGGER;
      Objects.requireNonNull(var10005);
      return var0.group(var10001, Codec.simpleMap(var10002, var10003.promotePartial(Util.prefix("Spawn data: ", var10005::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter((var0x) -> var0x.spawners), Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((var0x) -> var0x.mobSpawnCosts)).apply(var0, MobSpawnSettings::new);
   });
   private final float creatureGenerationProbability;
   private final Map<MobCategory, WeightedRandomList<SpawnerData>> spawners;
   private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;

   MobSpawnSettings(float var1, Map<MobCategory, WeightedRandomList<SpawnerData>> var2, Map<EntityType<?>, MobSpawnCost> var3) {
      super();
      this.creatureGenerationProbability = var1;
      this.spawners = ImmutableMap.copyOf(var2);
      this.mobSpawnCosts = ImmutableMap.copyOf(var3);
   }

   public WeightedRandomList<SpawnerData> getMobs(MobCategory var1) {
      return (WeightedRandomList)this.spawners.getOrDefault(var1, EMPTY_MOB_LIST);
   }

   @Nullable
   public MobSpawnCost getMobSpawnCost(EntityType<?> var1) {
      return (MobSpawnCost)this.mobSpawnCosts.get(var1);
   }

   public float getCreatureProbability() {
      return this.creatureGenerationProbability;
   }

   public static class SpawnerData extends WeightedEntry.IntrusiveBase {
      public static final Codec<SpawnerData> CODEC = RecordCodecBuilder.create((var0) -> var0.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter((var0x) -> var0x.type), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.IntrusiveBase::getWeight), ExtraCodecs.POSITIVE_INT.fieldOf("minCount").forGetter((var0x) -> var0x.minCount), ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter((var0x) -> var0x.maxCount)).apply(var0, SpawnerData::new)).validate((var0) -> var0.minCount > var0.maxCount ? DataResult.error(() -> "minCount needs to be smaller or equal to maxCount") : DataResult.success(var0));
      public final EntityType<?> type;
      public final int minCount;
      public final int maxCount;

      public SpawnerData(EntityType<?> var1, int var2, int var3, int var4) {
         this(var1, Weight.of(var2), var3, var4);
      }

      public SpawnerData(EntityType<?> var1, Weight var2, int var3, int var4) {
         super(var2);
         this.type = var1.getCategory() == MobCategory.MISC ? EntityType.PIG : var1;
         this.minCount = var3;
         this.maxCount = var4;
      }

      public String toString() {
         String var10000 = String.valueOf(EntityType.getKey(this.type));
         return var10000 + "*(" + this.minCount + "-" + this.maxCount + "):" + String.valueOf(this.getWeight());
      }
   }

   public static record MobSpawnCost(double energyBudget, double charge) {
      public static final Codec<MobSpawnCost> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((var0x) -> var0x.energyBudget), Codec.DOUBLE.fieldOf("charge").forGetter((var0x) -> var0x.charge)).apply(var0, MobSpawnCost::new));

      public MobSpawnCost(double var1, double var3) {
         super();
         this.energyBudget = var1;
         this.charge = var3;
      }
   }

   public static class Builder {
      private final Map<MobCategory, List<SpawnerData>> spawners = (Map)Stream.of(MobCategory.values()).collect(ImmutableMap.toImmutableMap((var0) -> var0, (var0) -> Lists.newArrayList()));
      private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
      private float creatureGenerationProbability = 0.1F;

      public Builder() {
         super();
      }

      public Builder addSpawn(MobCategory var1, SpawnerData var2) {
         ((List)this.spawners.get(var1)).add(var2);
         return this;
      }

      public Builder addMobCharge(EntityType<?> var1, double var2, double var4) {
         this.mobSpawnCosts.put(var1, new MobSpawnCost(var4, var2));
         return this;
      }

      public Builder creatureGenerationProbability(float var1) {
         this.creatureGenerationProbability = var1;
         return this;
      }

      public MobSpawnSettings build() {
         return new MobSpawnSettings(this.creatureGenerationProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (var0) -> WeightedRandomList.create((List)var0.getValue()))), ImmutableMap.copyOf(this.mobSpawnCosts));
      }
   }
}
