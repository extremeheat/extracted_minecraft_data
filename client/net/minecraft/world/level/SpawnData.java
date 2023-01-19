package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;

public record SpawnData(CompoundTag d, Optional<SpawnData.CustomSpawnRules> e) {
   private final CompoundTag entityToSpawn;
   private final Optional<SpawnData.CustomSpawnRules> customSpawnRules;
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               CompoundTag.CODEC.fieldOf("entity").forGetter(var0x -> var0x.entityToSpawn),
               SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(var0x -> var0x.customSpawnRules)
            )
            .apply(var0, SpawnData::new)
   );
   public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);
   public static final String DEFAULT_TYPE = "minecraft:pig";

   public SpawnData() {
      this(Util.make(new CompoundTag(), var0 -> var0.putString("id", "minecraft:pig")), Optional.empty());
   }

   public SpawnData(CompoundTag var1, Optional<SpawnData.CustomSpawnRules> var2) {
      super();
      ResourceLocation var3 = ResourceLocation.tryParse(var1.getString("id"));
      var1.putString("id", var3 != null ? var3.toString() : "minecraft:pig");
      this.entityToSpawn = var1;
      this.customSpawnRules = var2;
   }

   public CompoundTag getEntityToSpawn() {
      return this.entityToSpawn;
   }

   public Optional<SpawnData.CustomSpawnRules> getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public static record CustomSpawnRules(InclusiveRange<Integer> b, InclusiveRange<Integer> c) {
      private final InclusiveRange<Integer> blockLightLimit;
      private final InclusiveRange<Integer> skyLightLimit;
      private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<>(0, 15);
      public static final Codec<SpawnData.CustomSpawnRules> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  InclusiveRange.INT
                     .optionalFieldOf("block_light_limit", LIGHT_RANGE)
                     .flatXmap(SpawnData.CustomSpawnRules::checkLightBoundaries, SpawnData.CustomSpawnRules::checkLightBoundaries)
                     .forGetter(var0x -> var0x.blockLightLimit),
                  InclusiveRange.INT
                     .optionalFieldOf("sky_light_limit", LIGHT_RANGE)
                     .flatXmap(SpawnData.CustomSpawnRules::checkLightBoundaries, SpawnData.CustomSpawnRules::checkLightBoundaries)
                     .forGetter(var0x -> var0x.skyLightLimit)
               )
               .apply(var0, SpawnData.CustomSpawnRules::new)
      );

      public CustomSpawnRules(InclusiveRange<Integer> var1, InclusiveRange<Integer> var2) {
         super();
         this.blockLightLimit = var1;
         this.skyLightLimit = var2;
      }

      private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> var0) {
         return !LIGHT_RANGE.contains(var0) ? DataResult.error("Light values must be withing range " + LIGHT_RANGE) : DataResult.success(var0);
      }
   }
}
