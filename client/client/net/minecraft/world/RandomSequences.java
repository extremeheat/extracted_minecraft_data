package net.minecraft.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class RandomSequences extends SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long worldSeed;
   private int salt;
   private boolean includeWorldSeed = true;
   private boolean includeSequenceId = true;
   private final Map<ResourceLocation, RandomSequence> sequences = new Object2ObjectOpenHashMap();

   public static SavedData.Factory<RandomSequences> factory(long var0) {
      return new SavedData.Factory<>(() -> new RandomSequences(var0), (var2, var3) -> load(var0, var2), DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);
   }

   public RandomSequences(long var1) {
      super();
      this.worldSeed = var1;
   }

   public RandomSource get(ResourceLocation var1) {
      RandomSource var2 = this.sequences.computeIfAbsent(var1, this::createSequence).random();
      return new RandomSequences.DirtyMarkingRandomSource(var2);
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

   @Override
   public CompoundTag save(CompoundTag var1, HolderLookup.Provider var2) {
      var1.putInt("salt", this.salt);
      var1.putBoolean("include_world_seed", this.includeWorldSeed);
      var1.putBoolean("include_sequence_id", this.includeSequenceId);
      CompoundTag var3 = new CompoundTag();
      this.sequences
         .forEach((var1x, var2x) -> var3.put(var1x.toString(), (Tag)RandomSequence.CODEC.encodeStart(NbtOps.INSTANCE, var2x).result().orElseThrow()));
      var1.put("sequences", var3);
      return var1;
   }

   private static boolean getBooleanWithDefault(CompoundTag var0, String var1, boolean var2) {
      return var0.contains(var1, 1) ? var0.getBoolean(var1) : var2;
   }

   public static RandomSequences load(long var0, CompoundTag var2) {
      RandomSequences var3 = new RandomSequences(var0);
      var3.setSeedDefaults(
         var2.getInt("salt"), getBooleanWithDefault(var2, "include_world_seed", true), getBooleanWithDefault(var2, "include_sequence_id", true)
      );
      CompoundTag var4 = var2.getCompound("sequences");

      for (String var7 : var4.getAllKeys()) {
         try {
            RandomSequence var8 = (RandomSequence)((Pair)RandomSequence.CODEC.decode(NbtOps.INSTANCE, var4.get(var7)).result().get()).getFirst();
            var3.sequences.put(new ResourceLocation(var7), var8);
         } catch (Exception var9) {
            LOGGER.error("Failed to load random sequence {}", var7, var9);
         }
      }

      return var3;
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

   class DirtyMarkingRandomSource implements RandomSource {
      private final RandomSource random;

      DirtyMarkingRandomSource(RandomSource var2) {
         super();
         this.random = var2;
      }

      @Override
      public RandomSource fork() {
         RandomSequences.this.setDirty();
         return this.random.fork();
      }

      @Override
      public PositionalRandomFactory forkPositional() {
         RandomSequences.this.setDirty();
         return this.random.forkPositional();
      }

      @Override
      public void setSeed(long var1) {
         RandomSequences.this.setDirty();
         this.random.setSeed(var1);
      }

      @Override
      public int nextInt() {
         RandomSequences.this.setDirty();
         return this.random.nextInt();
      }

      @Override
      public int nextInt(int var1) {
         RandomSequences.this.setDirty();
         return this.random.nextInt(var1);
      }

      @Override
      public long nextLong() {
         RandomSequences.this.setDirty();
         return this.random.nextLong();
      }

      @Override
      public boolean nextBoolean() {
         RandomSequences.this.setDirty();
         return this.random.nextBoolean();
      }

      @Override
      public float nextFloat() {
         RandomSequences.this.setDirty();
         return this.random.nextFloat();
      }

      @Override
      public double nextDouble() {
         RandomSequences.this.setDirty();
         return this.random.nextDouble();
      }

      @Override
      public double nextGaussian() {
         RandomSequences.this.setDirty();
         return this.random.nextGaussian();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof RandomSequences.DirtyMarkingRandomSource var2 ? this.random.equals(var2.random) : false;
         }
      }
   }
}
