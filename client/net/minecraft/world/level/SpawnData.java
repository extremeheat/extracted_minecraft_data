package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;

public record SpawnData(CompoundTag d, Optional<SpawnData.CustomSpawnRules> e) {
   private final CompoundTag entityToSpawn;
   private final Optional<SpawnData.CustomSpawnRules> customSpawnRules;
   public static final String ENTITY_TAG = "entity";
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               CompoundTag.CODEC.fieldOf("entity").forGetter(var0x -> var0x.entityToSpawn),
               SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(var0x -> var0x.customSpawnRules)
            )
            .apply(var0, SpawnData::new)
   );
   public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);

   public SpawnData() {
      this(new CompoundTag(), Optional.empty());
   }

   public SpawnData(CompoundTag var1, Optional<SpawnData.CustomSpawnRules> var2) {
      super();
      if (var1.contains("id")) {
         ResourceLocation var3 = ResourceLocation.tryParse(var1.getString("id"));
         if (var3 != null) {
            var1.putString("id", var3.toString());
         } else {
            var1.remove("id");
         }
      }

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
                  lightLimit("block_light_limit").forGetter(var0x -> var0x.blockLightLimit),
                  lightLimit("sky_light_limit").forGetter(var0x -> var0x.skyLightLimit)
               )
               .apply(var0, SpawnData.CustomSpawnRules::new)
      );

      public CustomSpawnRules(InclusiveRange<Integer> var1, InclusiveRange<Integer> var2) {
         super();
         this.blockLightLimit = var1;
         this.skyLightLimit = var2;
      }

      private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> var0) {
         return !LIGHT_RANGE.contains(var0) ? DataResult.error(() -> "Light values must be withing range " + LIGHT_RANGE) : DataResult.success(var0);
      }

      private static MapCodec<InclusiveRange<Integer>> lightLimit(String var0) {
         return ExtraCodecs.validate(InclusiveRange.INT.optionalFieldOf(var0, LIGHT_RANGE), SpawnData.CustomSpawnRules::checkLightBoundaries);
      }
   }
}
