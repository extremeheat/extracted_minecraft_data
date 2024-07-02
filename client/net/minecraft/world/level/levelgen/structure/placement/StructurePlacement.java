package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.datafixers.Products.P5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public abstract class StructurePlacement {
   public static final Codec<StructurePlacement> CODEC = BuiltInRegistries.STRUCTURE_PLACEMENT
      .byNameCodec()
      .dispatch(StructurePlacement::type, StructurePlacementType::codec);
   private static final int HIGHLY_ARBITRARY_RANDOM_SALT = 10387320;
   private final Vec3i locateOffset;
   private final StructurePlacement.FrequencyReductionMethod frequencyReductionMethod;
   private final float frequency;
   private final int salt;
   private final Optional<StructurePlacement.ExclusionZone> exclusionZone;

   protected static <S extends StructurePlacement> P5<Mu<S>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.ExclusionZone>> placementCodec(
      Instance<S> var0
   ) {
      return var0.group(
         Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(StructurePlacement::locateOffset),
         StructurePlacement.FrequencyReductionMethod.CODEC
            .optionalFieldOf("frequency_reduction_method", StructurePlacement.FrequencyReductionMethod.DEFAULT)
            .forGetter(StructurePlacement::frequencyReductionMethod),
         Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(StructurePlacement::frequency),
         ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(StructurePlacement::salt),
         StructurePlacement.ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::exclusionZone)
      );
   }

   protected StructurePlacement(
      Vec3i var1, StructurePlacement.FrequencyReductionMethod var2, float var3, int var4, Optional<StructurePlacement.ExclusionZone> var5
   ) {
      super();
      this.locateOffset = var1;
      this.frequencyReductionMethod = var2;
      this.frequency = var3;
      this.salt = var4;
      this.exclusionZone = var5;
   }

   protected Vec3i locateOffset() {
      return this.locateOffset;
   }

   protected StructurePlacement.FrequencyReductionMethod frequencyReductionMethod() {
      return this.frequencyReductionMethod;
   }

   protected float frequency() {
      return this.frequency;
   }

   protected int salt() {
      return this.salt;
   }

   protected Optional<StructurePlacement.ExclusionZone> exclusionZone() {
      return this.exclusionZone;
   }

   public boolean isStructureChunk(ChunkGeneratorStructureState var1, int var2, int var3) {
      return this.isPlacementChunk(var1, var2, var3)
         && this.applyAdditionalChunkRestrictions(var2, var3, var1.getLevelSeed())
         && this.applyInteractionsWithOtherStructures(var1, var2, var3);
   }

   public boolean applyAdditionalChunkRestrictions(int var1, int var2, long var3) {
      return !(this.frequency < 1.0F) || this.frequencyReductionMethod.shouldGenerate(var3, this.salt, var1, var2, this.frequency);
   }

   public boolean applyInteractionsWithOtherStructures(ChunkGeneratorStructureState var1, int var2, int var3) {
      return !this.exclusionZone.isPresent() || !this.exclusionZone.get().isPlacementForbidden(var1, var2, var3);
   }

   protected abstract boolean isPlacementChunk(ChunkGeneratorStructureState var1, int var2, int var3);

   public BlockPos getLocatePos(ChunkPos var1) {
      return new BlockPos(var1.getMinBlockX(), 0, var1.getMinBlockZ()).offset(this.locateOffset());
   }

   public abstract StructurePlacementType<?> type();

   private static boolean probabilityReducer(long var0, int var2, int var3, int var4, float var5) {
      WorldgenRandom var6 = new WorldgenRandom(new LegacyRandomSource(0L));
      var6.setLargeFeatureWithSalt(var0, var2, var3, var4);
      return var6.nextFloat() < var5;
   }

   private static boolean legacyProbabilityReducerWithDouble(long var0, int var2, int var3, int var4, float var5) {
      WorldgenRandom var6 = new WorldgenRandom(new LegacyRandomSource(0L));
      var6.setLargeFeatureSeed(var0, var3, var4);
      return var6.nextDouble() < (double)var5;
   }

   private static boolean legacyArbitrarySaltProbabilityReducer(long var0, int var2, int var3, int var4, float var5) {
      WorldgenRandom var6 = new WorldgenRandom(new LegacyRandomSource(0L));
      var6.setLargeFeatureWithSalt(var0, var3, var4, 10387320);
      return var6.nextFloat() < var5;
   }

   private static boolean legacyPillagerOutpostReducer(long var0, int var2, int var3, int var4, float var5) {
      int var6 = var3 >> 4;
      int var7 = var4 >> 4;
      WorldgenRandom var8 = new WorldgenRandom(new LegacyRandomSource(0L));
      var8.setSeed((long)(var6 ^ var7 << 4) ^ var0);
      var8.nextInt();
      return var8.nextInt((int)(1.0F / var5)) == 0;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   @FunctionalInterface
   public interface FrequencyReducer {
      boolean shouldGenerate(long var1, int var3, int var4, int var5, float var6);
   }

   public static enum FrequencyReductionMethod implements StringRepresentable {
      DEFAULT("default", StructurePlacement::probabilityReducer),
      LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyPillagerOutpostReducer),
      LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyArbitrarySaltProbabilityReducer),
      LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyProbabilityReducerWithDouble);

      public static final Codec<StructurePlacement.FrequencyReductionMethod> CODEC = StringRepresentable.fromEnum(
         StructurePlacement.FrequencyReductionMethod::values
      );
      private final String name;
      private final StructurePlacement.FrequencyReducer reducer;

      private FrequencyReductionMethod(final String nullxx, final StructurePlacement.FrequencyReducer nullxxx) {
         this.name = nullxx;
         this.reducer = nullxxx;
      }

      public boolean shouldGenerate(long var1, int var3, int var4, int var5, float var6) {
         return this.reducer.shouldGenerate(var1, var3, var4, var5, var6);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
