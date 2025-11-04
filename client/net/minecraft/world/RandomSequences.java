package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class RandomSequences extends SavedData {
   public static final Codec<RandomSequences> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.INT.fieldOf("salt").forGetter(RandomSequences::salt), Codec.BOOL.optionalFieldOf("include_world_seed", true).forGetter(RandomSequences::includeWorldSeed), Codec.BOOL.optionalFieldOf("include_sequence_id", true).forGetter(RandomSequences::includeSequenceId), Codec.unboundedMap(Identifier.CODEC, RandomSequence.CODEC).fieldOf("sequences").forGetter((var0x) -> var0x.sequences)).apply(var0, RandomSequences::new));
   public static final SavedDataType<RandomSequences> TYPE;
   private int salt;
   private boolean includeWorldSeed = true;
   private boolean includeSequenceId = true;
   private final Map<Identifier, RandomSequence> sequences = new Object2ObjectOpenHashMap();

   public RandomSequences() {
      super();
   }

   private RandomSequences(int var1, boolean var2, boolean var3, Map<Identifier, RandomSequence> var4) {
      super();
      this.salt = var1;
      this.includeWorldSeed = var2;
      this.includeSequenceId = var3;
      this.sequences.putAll(var4);
   }

   public RandomSource get(Identifier var1, long var2) {
      RandomSource var4 = ((RandomSequence)this.sequences.computeIfAbsent(var1, (var3) -> this.createSequence(var3, var2))).random();
      return new DirtyMarkingRandomSource(var4);
   }

   private RandomSequence createSequence(Identifier var1, long var2) {
      return this.createSequence(var1, var2, this.salt, this.includeWorldSeed, this.includeSequenceId);
   }

   private RandomSequence createSequence(Identifier var1, long var2, int var4, boolean var5, boolean var6) {
      long var7 = (var5 ? var2 : 0L) ^ (long)var4;
      return new RandomSequence(var7, var6 ? Optional.of(var1) : Optional.empty());
   }

   public void forAllSequences(BiConsumer<Identifier, RandomSequence> var1) {
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

   public void reset(Identifier var1, long var2) {
      this.sequences.put(var1, this.createSequence(var1, var2));
   }

   public void reset(Identifier var1, long var2, int var4, boolean var5, boolean var6) {
      this.sequences.put(var1, this.createSequence(var1, var2, var4, var5, var6));
   }

   private int salt() {
      return this.salt;
   }

   private boolean includeWorldSeed() {
      return this.includeWorldSeed;
   }

   private boolean includeSequenceId() {
      return this.includeSequenceId;
   }

   static {
      TYPE = new SavedDataType<RandomSequences>("random_sequences", RandomSequences::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);
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
