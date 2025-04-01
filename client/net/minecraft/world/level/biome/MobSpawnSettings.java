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
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;

public class MobSpawnSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1F;
   public static final WeightedList<SpawnerData> EMPTY_MOB_LIST = WeightedList.<SpawnerData>of();
   public static final MobSpawnSettings EMPTY = (new Builder()).build();
   public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      RecordCodecBuilder var10001 = Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((var0x) -> var0x.creatureGenerationProbability);
      Codec var10002 = MobCategory.CODEC;
      Codec var10003 = WeightedList.codec(MobSpawnSettings.SpawnerData.CODEC);
      Logger var10005 = LOGGER;
      Objects.requireNonNull(var10005);
      return var0.group(var10001, Codec.simpleMap(var10002, var10003.promotePartial(Util.prefix("Spawn data: ", var10005::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter((var0x) -> var0x.spawners), Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((var0x) -> var0x.mobSpawnCosts)).apply(var0, MobSpawnSettings::new);
   });
   private final float creatureGenerationProbability;
   private final Map<MobCategory, WeightedList<SpawnerData>> spawners;
   private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;

   MobSpawnSettings(float var1, Map<MobCategory, WeightedList<SpawnerData>> var2, Map<EntityType<?>, MobSpawnCost> var3) {
      super();
      this.creatureGenerationProbability = var1;
      this.spawners = ImmutableMap.copyOf(var2);
      this.mobSpawnCosts = ImmutableMap.copyOf(var3);
   }

   public WeightedList<SpawnerData> getMobs(MobCategory var1) {
      return (WeightedList)this.spawners.getOrDefault(var1, EMPTY_MOB_LIST);
   }

   @Nullable
   public MobSpawnCost getMobSpawnCost(EntityType<?> var1) {
      return (MobSpawnCost)this.mobSpawnCosts.get(var1);
   }

   public float getCreatureProbability() {
      return this.creatureGenerationProbability;
   }

   Builder asBuilder() {
      Builder var1 = (new Builder()).creatureGenerationProbability(this.creatureGenerationProbability);

      for(Map.Entry var3 : this.spawners.entrySet()) {
         for(Weighted var5 : ((WeightedList)var3.getValue()).unwrap()) {
            var1.addSpawn((MobCategory)var3.getKey(), var5.weight(), (SpawnerData)var5.value());
         }
      }

      for(Map.Entry var7 : this.mobSpawnCosts.entrySet()) {
         var1.addMobCharge((EntityType)var7.getKey(), ((MobSpawnCost)var7.getValue()).charge(), ((MobSpawnCost)var7.getValue()).energyBudget());
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MobSpawnSettings var2 = (MobSpawnSettings)var1;
         return Float.compare(this.creatureGenerationProbability, var2.creatureGenerationProbability) == 0 && Objects.equals(this.spawners, var2.spawners) && Objects.equals(this.mobSpawnCosts, var2.mobSpawnCosts);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.creatureGenerationProbability, this.spawners, this.mobSpawnCosts});
   }

   public static record SpawnerData(EntityType<?> type, int minCount, int maxCount) {
      public static final MapCodec<SpawnerData> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter((var0x) -> var0x.type), ExtraCodecs.POSITIVE_INT.fieldOf("minCount").forGetter((var0x) -> var0x.minCount), ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter((var0x) -> var0x.maxCount)).apply(var0, SpawnerData::new)).validate((var0) -> var0.minCount > var0.maxCount ? DataResult.error(() -> "minCount needs to be smaller or equal to maxCount") : DataResult.success(var0));

      public SpawnerData(EntityType<?> var1, int var2, int var3) {
         super();
         var1 = var1.getCategory() == MobCategory.MISC ? EntityType.PIG : var1;
         this.type = var1;
         this.minCount = var2;
         this.maxCount = var3;
      }

      public String toString() {
         String var10000 = String.valueOf(EntityType.getKey(this.type));
         return var10000 + "*(" + this.minCount + "-" + this.maxCount + ")";
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
      private final Map<MobCategory, WeightedList.Builder<SpawnerData>> spawners = Util.<MobCategory, WeightedList.Builder<SpawnerData>>makeEnumMap(MobCategory.class, (var0) -> WeightedList.builder());
      private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
      private float creatureGenerationProbability = 0.1F;

      public Builder() {
         super();
      }

      public Builder addSpawn(MobCategory var1, int var2, SpawnerData var3) {
         ((WeightedList.Builder)this.spawners.get(var1)).add(var3, var2);
         return this;
      }

      public Builder clearSpawns(MobCategory var1) {
         ((WeightedList.Builder)this.spawners.get(var1)).clear();
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
         return new MobSpawnSettings(this.creatureGenerationProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (var0) -> ((WeightedList.Builder)var0.getValue()).build())), ImmutableMap.copyOf(this.mobSpawnCosts));
      }
   }
}
