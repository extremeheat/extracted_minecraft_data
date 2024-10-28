package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record StructureSpawnOverride(BoundingBoxType boundingBox, WeightedRandomList<MobSpawnSettings.SpawnerData> spawns) {
   public static final Codec<StructureSpawnOverride> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(StructureSpawnOverride.BoundingBoxType.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox), WeightedRandomList.codec(MobSpawnSettings.SpawnerData.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)).apply(var0, StructureSpawnOverride::new);
   });

   public StructureSpawnOverride(BoundingBoxType boundingBox, WeightedRandomList<MobSpawnSettings.SpawnerData> spawns) {
      super();
      this.boundingBox = boundingBox;
      this.spawns = spawns;
   }

   public BoundingBoxType boundingBox() {
      return this.boundingBox;
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> spawns() {
      return this.spawns;
   }

   public static enum BoundingBoxType implements StringRepresentable {
      PIECE("piece"),
      STRUCTURE("full");

      public static final Codec<BoundingBoxType> CODEC = StringRepresentable.fromEnum(BoundingBoxType::values);
      private final String id;

      private BoundingBoxType(final String var3) {
         this.id = var3;
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static BoundingBoxType[] $values() {
         return new BoundingBoxType[]{PIECE, STRUCTURE};
      }
   }
}
