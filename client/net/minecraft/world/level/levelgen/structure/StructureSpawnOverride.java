package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType b, WeightedRandomList<MobSpawnSettings.SpawnerData> c) {
   private final StructureSpawnOverride.BoundingBoxType boundingBox;
   private final WeightedRandomList<MobSpawnSettings.SpawnerData> spawns;
   public static final Codec<StructureSpawnOverride> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               StructureSpawnOverride.BoundingBoxType.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox),
               WeightedRandomList.codec(MobSpawnSettings.SpawnerData.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)
            )
            .apply(var0, StructureSpawnOverride::new)
   );

   public StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType var1, WeightedRandomList<MobSpawnSettings.SpawnerData> var2) {
      super();
      this.boundingBox = var1;
      this.spawns = var2;
   }

   public static enum BoundingBoxType implements StringRepresentable {
      PIECE("piece"),
      STRUCTURE("full");

      public static final Codec<StructureSpawnOverride.BoundingBoxType> CODEC = StringRepresentable.fromEnum(StructureSpawnOverride.BoundingBoxType::values);
      private final String id;

      private BoundingBoxType(String var3) {
         this.id = var3;
      }

      @Override
      public String getSerializedName() {
         return this.id;
      }
   }
}
