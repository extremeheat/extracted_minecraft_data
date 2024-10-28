package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class RandomSpreadStructurePlacement extends StructurePlacement {
   public static final MapCodec<RandomSpreadStructurePlacement> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return placementCodec(var0).and(var0.group(Codec.intRange(0, 4096).fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::spacing), Codec.intRange(0, 4096).fieldOf("separation").forGetter(RandomSpreadStructurePlacement::separation), RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType))).apply(var0, RandomSpreadStructurePlacement::new);
   }).validate(RandomSpreadStructurePlacement::validate);
   private final int spacing;
   private final int separation;
   private final RandomSpreadType spreadType;

   private static DataResult<RandomSpreadStructurePlacement> validate(RandomSpreadStructurePlacement var0) {
      return var0.spacing <= var0.separation ? DataResult.error(() -> {
         return "Spacing has to be larger than separation";
      }) : DataResult.success(var0);
   }

   public RandomSpreadStructurePlacement(Vec3i var1, StructurePlacement.FrequencyReductionMethod var2, float var3, int var4, Optional<StructurePlacement.ExclusionZone> var5, int var6, int var7, RandomSpreadType var8) {
      super(var1, var2, var3, var4, var5);
      this.spacing = var6;
      this.separation = var7;
      this.spreadType = var8;
   }

   public RandomSpreadStructurePlacement(int var1, int var2, RandomSpreadType var3, int var4) {
      this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, var4, Optional.empty(), var1, var2, var3);
   }

   public int spacing() {
      return this.spacing;
   }

   public int separation() {
      return this.separation;
   }

   public RandomSpreadType spreadType() {
      return this.spreadType;
   }

   public ChunkPos getPotentialStructureChunk(long var1, int var3, int var4) {
      int var5 = Math.floorDiv(var3, this.spacing);
      int var6 = Math.floorDiv(var4, this.spacing);
      WorldgenRandom var7 = new WorldgenRandom(new LegacyRandomSource(0L));
      var7.setLargeFeatureWithSalt(var1, var5, var6, this.salt());
      int var8 = this.spacing - this.separation;
      int var9 = this.spreadType.evaluate(var7, var8);
      int var10 = this.spreadType.evaluate(var7, var8);
      return new ChunkPos(var5 * this.spacing + var9, var6 * this.spacing + var10);
   }

   protected boolean isPlacementChunk(ChunkGeneratorStructureState var1, int var2, int var3) {
      ChunkPos var4 = this.getPotentialStructureChunk(var1.getLevelSeed(), var2, var3);
      return var4.x == var2 && var4.z == var3;
   }

   public StructurePlacementType<?> type() {
      return StructurePlacementType.RANDOM_SPREAD;
   }
}
