package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class RandomSequences extends SavedData {
   public static final SavedDataType<RandomSequences> TYPE;
   private final long worldSeed;
   private int salt;
   private boolean includeWorldSeed = true;
   private boolean includeSequenceId = true;
   private final Map<ResourceLocation, RandomSequence> sequences = new Object2ObjectOpenHashMap();

   public RandomSequences(long var1) {
      super();
      this.worldSeed = var1;
   }

   private RandomSequences(long var1, int var3, boolean var4, boolean var5, Map<ResourceLocation, RandomSequence> var6) {
      super();
      this.worldSeed = var1;
      this.salt = var3;
      this.includeWorldSeed = var4;
      this.includeSequenceId = var5;
      this.sequences.putAll(var6);
   }

   public static Codec<RandomSequences> codec(long var0) {
      return RecordCodecBuilder.create((var2) -> var2.group(RecordCodecBuilder.point(var0), Codec.INT.fieldOf("salt").forGetter((var0x) -> var0x.salt), Codec.BOOL.optionalFieldOf("include_world_seed", true).forGetter((var0x) -> var0x.includeWorldSeed), Codec.BOOL.optionalFieldOf("include_sequence_id", true).forGetter((var0x) -> var0x.includeSequenceId), Codec.unboundedMap(ResourceLocation.CODEC, RandomSequence.CODEC).fieldOf("sequences").forGetter((var0x) -> var0x.sequences)).apply(var2, RandomSequences::new));
   }

   public RandomSource get(ResourceLocation var1) {
      RandomSource var2 = ((RandomSequence)this.sequences.computeIfAbsent(var1, this::createSequence)).random();
      return new DirtyMarkingRandomSource(var2);
   }

   private RandomSequence createSequence(ResourceLocation var1) {
      return this.createSequence(var1, this.salt, this.includeWorldSeed, this.includeSequenceId);
   }

   private RandomSequence createSequence(ResourceLocation var1, int var2, boolean var3, boolean var4) {
      long var5 = (var3 ? this.worldSeed : 0L) ^ (long)var2;
      return new RandomSequence(var5, var4 ? Optional.of(var1) : Optional.empty());
   }

   public void forAllSequences(BiConsumer<ResourceLocation, RandomSequence> var1) {
      this.sequences.forEach(var1);
   }

   public void setSeedDefaults(int var1, boolean var2, boolean var3) {
      this.salt = var1;
      this.includeWorldSeed = var2;
      this.includeSequenceId = var3;
   }

   public int clear() {
      int var1 = this.sequences.size();
      this.sequences.clear();
      return var1;
   }

   public void reset(ResourceLocation var1) {
      this.sequences.put(var1, this.createSequence(var1));
   }

   public void reset(ResourceLocation var1, int var2, boolean var3, boolean var4) {
      this.sequences.put(var1, this.createSequence(var1, var2, var3, var4));
   }

   static {
      TYPE = new SavedDataType<RandomSequences>("random_sequences", (var0) -> new RandomSequences(var0.worldSeed()), (var0) -> codec(var0.worldSeed()), DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);
   }

   class DirtyMarkingRandomSource implements RandomSource {
      private final RandomSource random;

      DirtyMarkingRandomSource(final RandomSource var2) {
         super();
         this.random = var2;
      }

      public RandomSource fork() {
         RandomSequences.this.setDirty();
         return this.random.fork();
      }

      public PositionalRandomFactory forkPositional() {
         RandomSequences.this.setDirty();
         return this.random.forkPositional();
      }

      public void setSeed(long var1) {
         RandomSequences.this.setDirty();
         this.random.setSeed(var1);
      }

      public int nextInt() {
         RandomSequences.this.setDirty();
         return this.random.nextInt();
      }

      public int nextInt(int var1) {
         RandomSequences.this.setDirty();
         return this.random.nextInt(var1);
      }

      public long nextLong() {
         RandomSequences.this.setDirty();
         return this.random.nextLong();
      }

      public boolean nextBoolean() {
         RandomSequences.this.setDirty();
         return this.random.nextBoolean();
      }

      public float nextFloat() {
         RandomSequences.this.setDirty();
         return this.random.nextFloat();
      }

      public double nextDouble() {
         RandomSequences.this.setDirty();
         return this.random.nextDouble();
      }

      public double nextGaussian() {
         RandomSequences.this.setDirty();
         return this.random.nextGaussian();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 instanceof DirtyMarkingRandomSource) {
            DirtyMarkingRandomSource var2 = (DirtyMarkingRandomSource)var1;
            return this.random.equals(var2.random);
         } else {
            return false;
         }
      }
   }
}
