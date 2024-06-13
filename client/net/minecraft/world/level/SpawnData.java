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

public record SpawnData(CompoundTag entityToSpawn, Optional<SpawnData.CustomSpawnRules> customSpawnRules, Optional<ResourceLocation> equipmentLootTable) {
   public static final String ENTITY_TAG = "entity";
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               CompoundTag.CODEC.fieldOf("entity").forGetter(var0x -> var0x.entityToSpawn),
               SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(var0x -> var0x.customSpawnRules),
               ResourceLocation.CODEC.optionalFieldOf("equipment_loot_table").forGetter(var0x -> var0x.equipmentLootTable)
            )
            .apply(var0, SpawnData::new)
   );
   public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);

   public SpawnData() {
      this(new CompoundTag(), Optional.empty(), Optional.empty());
   }

   public SpawnData(CompoundTag entityToSpawn, Optional<SpawnData.CustomSpawnRules> customSpawnRules, Optional<ResourceLocation> equipmentLootTable) {
      super();
      if (entityToSpawn.contains("id")) {
         ResourceLocation var4 = ResourceLocation.tryParse(entityToSpawn.getString("id"));
         if (var4 != null) {
            entityToSpawn.putString("id", var4.toString());
         } else {
            entityToSpawn.remove("id");
         }
      }

      this.entityToSpawn = entityToSpawn;
      this.customSpawnRules = customSpawnRules;
      this.equipmentLootTable = equipmentLootTable;
   }

   public CompoundTag getEntityToSpawn() {
      return this.entityToSpawn;
   }

   public Optional<SpawnData.CustomSpawnRules> getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public Optional<ResourceLocation> getEquipmentLootTable() {
      return this.equipmentLootTable;
   }

   public static record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
      private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<>(0, 15);
      public static final Codec<SpawnData.CustomSpawnRules> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  lightLimit("block_light_limit").forGetter(var0x -> var0x.blockLightLimit),
                  lightLimit("sky_light_limit").forGetter(var0x -> var0x.skyLightLimit)
               )
               .apply(var0, SpawnData.CustomSpawnRules::new)
      );

      public CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
         super();
         this.blockLightLimit = blockLightLimit;
         this.skyLightLimit = skyLightLimit;
      }

      private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> var0) {
         return !LIGHT_RANGE.contains(var0) ? DataResult.error(() -> "Light values must be withing range " + LIGHT_RANGE) : DataResult.success(var0);
      }

      private static MapCodec<InclusiveRange<Integer>> lightLimit(String var0) {
         return InclusiveRange.INT.lenientOptionalFieldOf(var0, LIGHT_RANGE).validate(SpawnData.CustomSpawnRules::checkLightBoundaries);
      }

      public boolean isValidPosition(BlockPos var1, ServerLevel var2) {
         return this.blockLightLimit.isValueInRange(var2.getBrightness(LightLayer.BLOCK, var1))
            && this.skyLightLimit.isValueInRange(var2.getBrightness(LightLayer.SKY, var1));
      }
   }
}
