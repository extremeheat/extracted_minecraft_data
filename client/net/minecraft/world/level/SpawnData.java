package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EquipmentTable;

public record SpawnData(CompoundTag entityToSpawn, Optional<CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
   public static final String ENTITY_TAG = "entity";
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create((i) -> i.group(CompoundTag.CODEC.fieldOf("entity").forGetter((s) -> s.entityToSpawn), SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter((o) -> o.customSpawnRules), EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter((o) -> o.equipment)).apply(i, SpawnData::new));
   public static final Codec<WeightedList<SpawnData>> LIST_CODEC;

   public SpawnData() {
      this(new CompoundTag(), Optional.empty(), Optional.empty());
   }

   public SpawnData {
      super();
      Optional<Identifier> id = entityToSpawn.read("id", Identifier.CODEC);
      if (id.isPresent()) {
         entityToSpawn.store("id", Identifier.CODEC, (Identifier)id.get());
      } else {
         entityToSpawn.remove("id");
      }

   }

   public CompoundTag getEntityToSpawn() {
      return this.entityToSpawn;
   }

   public Optional<CustomSpawnRules> getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public Optional<EquipmentTable> getEquipment() {
      return this.equipment;
   }

   static {
      LIST_CODEC = WeightedList.codec(CODEC);
   }

   public static record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
      private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<Integer>(0, 15);
      public static final Codec<CustomSpawnRules> CODEC = RecordCodecBuilder.create((i) -> i.group(lightLimit("block_light_limit").forGetter((o) -> o.blockLightLimit), lightLimit("sky_light_limit").forGetter((o) -> o.skyLightLimit)).apply(i, CustomSpawnRules::new));

      public CustomSpawnRules {
         super();
      }

      private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(final InclusiveRange<Integer> range) {
         return !LIGHT_RANGE.contains(range) ? DataResult.error(() -> "Light values must be withing range " + String.valueOf(LIGHT_RANGE)) : DataResult.success(range);
      }

      private static MapCodec<InclusiveRange<Integer>> lightLimit(final String name) {
         return InclusiveRange.INT.lenientOptionalFieldOf(name, LIGHT_RANGE).validate(CustomSpawnRules::checkLightBoundaries);
      }

      public boolean isValidPosition(final BlockPos blockSpawnPos, final ServerLevel level) {
         return this.blockLightLimit.isValueInRange(level.getBrightness(LightLayer.BLOCK, blockSpawnPos)) && this.skyLightLimit.isValueInRange(level.getEffectiveSkyBrightness(blockSpawnPos));
      }
   }
}
