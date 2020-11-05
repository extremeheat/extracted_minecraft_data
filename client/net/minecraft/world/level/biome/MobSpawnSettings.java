package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobSpawnSettings {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final MobSpawnSettings EMPTY = new MobSpawnSettings(0.1F, (Map)Stream.of(MobCategory.values()).collect(ImmutableMap.toImmutableMap((var0) -> {
      return var0;
   }, (var0) -> {
      return ImmutableList.of();
   })), ImmutableMap.of(), false);
   public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      RecordCodecBuilder var10001 = Codec.FLOAT.optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((var0x) -> {
         return var0x.creatureGenerationProbability;
      });
      Codec var10002 = MobCategory.CODEC;
      Codec var10003 = MobSpawnSettings.SpawnerData.CODEC.listOf();
      Logger var10005 = LOGGER;
      var10005.getClass();
      return var0.group(var10001, Codec.simpleMap(var10002, var10003.promotePartial(Util.prefix("Spawn data: ", var10005::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter((var0x) -> {
         return var0x.spawners;
      }), Codec.simpleMap(Registry.ENTITY_TYPE, MobSpawnSettings.MobSpawnCost.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((var0x) -> {
         return var0x.mobSpawnCosts;
      }), Codec.BOOL.fieldOf("player_spawn_friendly").orElse(false).forGetter(MobSpawnSettings::playerSpawnFriendly)).apply(var0, MobSpawnSettings::new);
   });
   private final float creatureGenerationProbability;
   private final Map<MobCategory, List<MobSpawnSettings.SpawnerData>> spawners;
   private final Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> mobSpawnCosts;
   private final boolean playerSpawnFriendly;

   private MobSpawnSettings(float var1, Map<MobCategory, List<MobSpawnSettings.SpawnerData>> var2, Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> var3, boolean var4) {
      super();
      this.creatureGenerationProbability = var1;
      this.spawners = var2;
      this.mobSpawnCosts = var3;
      this.playerSpawnFriendly = var4;
   }

   public List<MobSpawnSettings.SpawnerData> getMobs(MobCategory var1) {
      return (List)this.spawners.getOrDefault(var1, ImmutableList.of());
   }

   @Nullable
   public MobSpawnSettings.MobSpawnCost getMobSpawnCost(EntityType<?> var1) {
      return (MobSpawnSettings.MobSpawnCost)this.mobSpawnCosts.get(var1);
   }

   public float getCreatureProbability() {
      return this.creatureGenerationProbability;
   }

   public boolean playerSpawnFriendly() {
      return this.playerSpawnFriendly;
   }

   // $FF: synthetic method
   MobSpawnSettings(float var1, Map var2, Map var3, boolean var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   public static class Builder {
      private final Map<MobCategory, List<MobSpawnSettings.SpawnerData>> spawners = (Map)Stream.of(MobCategory.values()).collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0;
      }, (var0) -> {
         return Lists.newArrayList();
      }));
      private final Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
      private float creatureGenerationProbability = 0.1F;
      private boolean playerCanSpawn;

      public Builder() {
         super();
      }

      public MobSpawnSettings.Builder addSpawn(MobCategory var1, MobSpawnSettings.SpawnerData var2) {
         ((List)this.spawners.get(var1)).add(var2);
         return this;
      }

      public MobSpawnSettings.Builder addMobCharge(EntityType<?> var1, double var2, double var4) {
         this.mobSpawnCosts.put(var1, new MobSpawnSettings.MobSpawnCost(var4, var2));
         return this;
      }

      public MobSpawnSettings.Builder creatureGenerationProbability(float var1) {
         this.creatureGenerationProbability = var1;
         return this;
      }

      public MobSpawnSettings.Builder setPlayerCanSpawn() {
         this.playerCanSpawn = true;
         return this;
      }

      public MobSpawnSettings build() {
         return new MobSpawnSettings(this.creatureGenerationProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (var0) -> {
            return ImmutableList.copyOf((Collection)var0.getValue());
         })), ImmutableMap.copyOf(this.mobSpawnCosts), this.playerCanSpawn);
      }
   }

   public static class MobSpawnCost {
      public static final Codec<MobSpawnSettings.MobSpawnCost> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((var0x) -> {
            return var0x.energyBudget;
         }), Codec.DOUBLE.fieldOf("charge").forGetter((var0x) -> {
            return var0x.charge;
         })).apply(var0, MobSpawnSettings.MobSpawnCost::new);
      });
      private final double energyBudget;
      private final double charge;

      private MobSpawnCost(double var1, double var3) {
         super();
         this.energyBudget = var1;
         this.charge = var3;
      }

      public double getEnergyBudget() {
         return this.energyBudget;
      }

      public double getCharge() {
         return this.charge;
      }

      // $FF: synthetic method
      MobSpawnCost(double var1, double var3, Object var5) {
         this(var1, var3);
      }
   }

   public static class SpawnerData extends WeighedRandom.WeighedRandomItem {
      public static final Codec<MobSpawnSettings.SpawnerData> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Registry.ENTITY_TYPE.fieldOf("type").forGetter((var0x) -> {
            return var0x.type;
         }), Codec.INT.fieldOf("weight").forGetter((var0x) -> {
            return var0x.weight;
         }), Codec.INT.fieldOf("minCount").forGetter((var0x) -> {
            return var0x.minCount;
         }), Codec.INT.fieldOf("maxCount").forGetter((var0x) -> {
            return var0x.maxCount;
         })).apply(var0, MobSpawnSettings.SpawnerData::new);
      });
      public final EntityType<?> type;
      public final int minCount;
      public final int maxCount;

      public SpawnerData(EntityType<?> var1, int var2, int var3, int var4) {
         super(var2);
         this.type = var1.getCategory() == MobCategory.MISC ? EntityType.PIG : var1;
         this.minCount = var3;
         this.maxCount = var4;
      }

      public String toString() {
         return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.weight;
      }
   }
}
