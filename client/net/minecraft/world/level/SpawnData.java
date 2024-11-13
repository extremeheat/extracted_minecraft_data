package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EquipmentTable;

public record SpawnData(CompoundTag entityToSpawn, Optional<CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
   public static final String ENTITY_TAG = "entity";
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create((var0) -> var0.group(CompoundTag.CODEC.fieldOf("entity").forGetter((var0x) -> var0x.entityToSpawn), SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter((var0x) -> var0x.customSpawnRules), EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter((var0x) -> var0x.equipment)).apply(var0, SpawnData::new));
   public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC;

   public SpawnData() {
      this(new CompoundTag(), Optional.empty(), Optional.empty());
   }

   public SpawnData(CompoundTag var1, Optional<CustomSpawnRules> var2, Optional<EquipmentTable> var3) {
      super();
      if (var1.contains("id")) {
         ResourceLocation var4 = ResourceLocation.tryParse(var1.getString("id"));
         if (var4 != null) {
            var1.putString("id", var4.toString());
         } else {
            var1.remove("id");
         }
      }

      this.entityToSpawn = var1;
      this.customSpawnRules = var2;
      this.equipment = var3;
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
      LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);
   }

   public static record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
      private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<Integer>(0, 15);
      public static final Codec<CustomSpawnRules> CODEC = RecordCodecBuilder.create((var0) -> var0.group(lightLimit("block_light_limit").forGetter((var0x) -> var0x.blockLightLimit), lightLimit("sky_light_limit").forGetter((var0x) -> var0x.skyLightLimit)).apply(var0, CustomSpawnRules::new));

      public CustomSpawnRules(InclusiveRange<Integer> var1, InclusiveRange<Integer> var2) {
         super();
         this.blockLightLimit = var1;
         this.skyLightLimit = var2;
      }

      private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> var0) {
         return !LIGHT_RANGE.contains(var0) ? DataResult.error(() -> "Light values must be withing range " + String.valueOf(LIGHT_RANGE)) : DataResult.success(var0);
      }

      private static MapCodec<InclusiveRange<Integer>> lightLimit(String var0) {
         return InclusiveRange.INT.lenientOptionalFieldOf(var0, LIGHT_RANGE).validate(CustomSpawnRules::checkLightBoundaries);
      }

      public boolean isValidPosition(BlockPos var1, ServerLevel var2) {
         return this.blockLightLimit.isValueInRange(var2.getBrightness(LightLayer.BLOCK, var1)) && this.skyLightLimit.isValueInRange(var2.getBrightness(LightLayer.SKY, var1));
      }
   }
}
