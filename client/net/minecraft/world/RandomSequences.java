package net.minecraft.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class RandomSequences extends SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long seed;
   private final Map<ResourceLocation, RandomSequence> sequences = new Object2ObjectOpenHashMap();

   public RandomSequences(long var1) {
      super();
      this.seed = var1;
   }

   public RandomSource get(ResourceLocation var1) {
      final RandomSource var2 = this.sequences.computeIfAbsent(var1, var1x -> new RandomSequence(this.seed, var1x)).random();
      return new RandomSource() {
         @Override
         public RandomSource fork() {
            RandomSequences.this.setDirty();
            return var2.fork();
         }

         @Override
         public PositionalRandomFactory forkPositional() {
            RandomSequences.this.setDirty();
            return var2.forkPositional();
         }

         @Override
         public void setSeed(long var1) {
            RandomSequences.this.setDirty();
            var2.setSeed(var1);
         }

         @Override
         public int nextInt() {
            RandomSequences.this.setDirty();
            return var2.nextInt();
         }

         @Override
         public int nextInt(int var1) {
            RandomSequences.this.setDirty();
            return var2.nextInt(var1);
         }

         @Override
         public long nextLong() {
            RandomSequences.this.setDirty();
            return var2.nextLong();
         }

         @Override
         public boolean nextBoolean() {
            RandomSequences.this.setDirty();
            return var2.nextBoolean();
         }

         @Override
         public float nextFloat() {
            RandomSequences.this.setDirty();
            return var2.nextFloat();
         }

         @Override
         public double nextDouble() {
            RandomSequences.this.setDirty();
            return var2.nextDouble();
         }

         @Override
         public double nextGaussian() {
            RandomSequences.this.setDirty();
            return var2.nextGaussian();
         }
      };
   }

   @Override
   public CompoundTag save(CompoundTag var1) {
      this.sequences.forEach((var1x, var2) -> var1.put(var1x.toString(), (Tag)RandomSequence.CODEC.encodeStart(NbtOps.INSTANCE, var2).result().orElseThrow()));
      return var1;
   }

   public static RandomSequences load(long var0, CompoundTag var2) {
      RandomSequences var3 = new RandomSequences(var0);

      for(String var6 : var2.getAllKeys()) {
         try {
            RandomSequence var7 = (RandomSequence)((Pair)RandomSequence.CODEC.decode(NbtOps.INSTANCE, var2.get(var6)).result().get()).getFirst();
            var3.sequences.put(new ResourceLocation(var6), var7);
         } catch (Exception var8) {
            LOGGER.error("Failed to load random sequence {}", var6, var8);
         }
      }

      return var3;
   }
}
