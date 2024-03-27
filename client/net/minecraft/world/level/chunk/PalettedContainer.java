package net.minecraft.world.level.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.Int2IntMap.Entry;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.util.BitStorage;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.util.ZeroBitStorage;

public class PalettedContainer<T> implements PaletteResize<T>, PalettedContainerRO<T> {
   private static final int MIN_PALETTE_BITS = 0;
   private final PaletteResize<T> dummyPaletteResize = (var0, var1x) -> 0;
   private final IdMap<T> registry;
   private volatile PalettedContainer.Data<T> data;
   private final PalettedContainer.Strategy strategy;
   private final ThreadingDetector threadingDetector = new ThreadingDetector("PalettedContainer");

   public void acquire() {
      this.threadingDetector.checkAndLock();
   }

   public void release() {
      this.threadingDetector.checkAndUnlock();
   }

   public static <T> Codec<PalettedContainer<T>> codecRW(IdMap<T> var0, Codec<T> var1, PalettedContainer.Strategy var2, T var3) {
      PalettedContainerRO.Unpacker var4 = PalettedContainer::unpack;
      return codec(var0, var1, var2, var3, var4);
   }

   public static <T> Codec<PalettedContainerRO<T>> codecRO(IdMap<T> var0, Codec<T> var1, PalettedContainer.Strategy var2, T var3) {
      PalettedContainerRO.Unpacker var4 = (var0x, var1x, var2x) -> unpack(var0x, var1x, var2x).map(var0xx -> var0xx);
      return codec(var0, var1, var2, var3, var4);
   }

   private static <T, C extends PalettedContainerRO<T>> Codec<C> codec(
      IdMap<T> var0, Codec<T> var1, PalettedContainer.Strategy var2, T var3, PalettedContainerRO.Unpacker<T, C> var4
   ) {
      return RecordCodecBuilder.create(
            var2x -> var2x.group(
                     var1.mapResult(ExtraCodecs.orElsePartial(var3)).listOf().fieldOf("palette").forGetter(PalettedContainerRO.PackedData::paletteEntries),
                     Codec.LONG_STREAM.lenientOptionalFieldOf("data").forGetter(PalettedContainerRO.PackedData::storage)
                  )
                  .apply(var2x, PalettedContainerRO.PackedData::new)
         )
         .comapFlatMap(var3x -> var4.read(var0, var2, var3x), var2x -> var2x.pack(var0, var2));
   }

   public PalettedContainer(IdMap<T> var1, PalettedContainer.Strategy var2, PalettedContainer.Configuration<T> var3, BitStorage var4, List<T> var5) {
      super();
      this.registry = var1;
      this.strategy = var2;
      this.data = new PalettedContainer.Data<>(var3, var4, var3.factory().create(var3.bits(), var1, this, var5));
   }

   private PalettedContainer(IdMap<T> var1, PalettedContainer.Strategy var2, PalettedContainer.Data<T> var3) {
      super();
      this.registry = var1;
      this.strategy = var2;
      this.data = var3;
   }

   public PalettedContainer(IdMap<T> var1, T var2, PalettedContainer.Strategy var3) {
      super();
      this.strategy = var3;
      this.registry = var1;
      this.data = this.createOrReuseData(null, 0);
      this.data.palette.idFor((T)var2);
   }

   private PalettedContainer.Data<T> createOrReuseData(@Nullable PalettedContainer.Data<T> var1, int var2) {
      PalettedContainer.Configuration var3 = this.strategy.getConfiguration(this.registry, var2);
      return var1 != null && var3.equals(var1.configuration()) ? var1 : var3.createData(this.registry, this, this.strategy.size());
   }

   @Override
   public int onResize(int var1, T var2) {
      PalettedContainer.Data var3 = this.data;
      PalettedContainer.Data var4 = this.createOrReuseData(var3, var1);
      var4.copyFrom(var3.palette, var3.storage);
      this.data = var4;
      return var4.palette.idFor((T)var2);
   }

   public T getAndSet(int var1, int var2, int var3, T var4) {
      this.acquire();

      Object var5;
      try {
         var5 = this.getAndSet(this.strategy.getIndex(var1, var2, var3), (T)var4);
      } finally {
         this.release();
      }

      return (T)var5;
   }

   public T getAndSetUnchecked(int var1, int var2, int var3, T var4) {
      return this.getAndSet(this.strategy.getIndex(var1, var2, var3), (T)var4);
   }

   private T getAndSet(int var1, T var2) {
      int var3 = this.data.palette.idFor((T)var2);
      int var4 = this.data.storage.getAndSet(var1, var3);
      return this.data.palette.valueFor(var4);
   }

   public void set(int var1, int var2, int var3, T var4) {
      this.acquire();

      try {
         this.set(this.strategy.getIndex(var1, var2, var3), (T)var4);
      } finally {
         this.release();
      }
   }

   private void set(int var1, T var2) {
      int var3 = this.data.palette.idFor((T)var2);
      this.data.storage.set(var1, var3);
   }

   @Override
   public T get(int var1, int var2, int var3) {
      return this.get(this.strategy.getIndex(var1, var2, var3));
   }

   protected T get(int var1) {
      PalettedContainer.Data var2 = this.data;
      return var2.palette.valueFor(var2.storage.get(var1));
   }

   @Override
   public void getAll(Consumer<T> var1) {
      Palette var2 = this.data.palette();
      IntArraySet var3 = new IntArraySet();
      this.data.storage.getAll(var3::add);
      var3.forEach(var2x -> var1.accept(var2.valueFor(var2x)));
   }

   public void read(FriendlyByteBuf var1) {
      this.acquire();

      try {
         byte var2 = var1.readByte();
         PalettedContainer.Data var3 = this.createOrReuseData(this.data, var2);
         var3.palette.read(var1);
         var1.readLongArray(var3.storage.getRaw());
         this.data = var3;
      } finally {
         this.release();
      }
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      this.acquire();

      try {
         this.data.write(var1);
      } finally {
         this.release();
      }
   }

   private static <T> DataResult<PalettedContainer<T>> unpack(IdMap<T> var0, PalettedContainer.Strategy var1, PalettedContainerRO.PackedData<T> var2) {
      List var3 = var2.paletteEntries();
      int var4 = var1.size();
      int var5 = var1.calculateBitsForSerialization(var0, var3.size());
      PalettedContainer.Configuration var6 = var1.getConfiguration(var0, var5);
      Object var7;
      if (var5 == 0) {
         var7 = new ZeroBitStorage(var4);
      } else {
         Optional var8 = var2.storage();
         if (var8.isEmpty()) {
            return DataResult.error(() -> "Missing values for non-zero storage");
         }

         long[] var9 = ((LongStream)var8.get()).toArray();

         try {
            if (var6.factory() == PalettedContainer.Strategy.GLOBAL_PALETTE_FACTORY) {
               HashMapPalette var10 = new HashMapPalette<>(var0, var5, (var0x, var1x) -> 0, var3);
               SimpleBitStorage var11 = new SimpleBitStorage(var5, var4, var9);
               int[] var12 = new int[var4];
               var11.unpack(var12);
               swapPalette(var12, var2x -> var0.getId(var10.valueFor(var2x)));
               var7 = new SimpleBitStorage(var6.bits(), var4, var12);
            } else {
               var7 = new SimpleBitStorage(var6.bits(), var4, var9);
            }
         } catch (SimpleBitStorage.InitializationException var13) {
            return DataResult.error(() -> "Failed to read PalettedContainer: " + var13.getMessage());
         }
      }

      return DataResult.success(new PalettedContainer(var0, var1, var6, (BitStorage)var7, var3));
   }

   @Override
   public PalettedContainerRO.PackedData<T> pack(IdMap<T> var1, PalettedContainer.Strategy var2) {
      this.acquire();

      PalettedContainerRO.PackedData var12;
      try {
         HashMapPalette var3 = new HashMapPalette<>(var1, this.data.storage.getBits(), this.dummyPaletteResize);
         int var4 = var2.size();
         int[] var5 = new int[var4];
         this.data.storage.unpack(var5);
         swapPalette(var5, var2x -> var3.idFor(this.data.palette.valueFor(var2x)));
         int var6 = var2.calculateBitsForSerialization(var1, var3.getSize());
         Optional var7;
         if (var6 != 0) {
            SimpleBitStorage var8 = new SimpleBitStorage(var6, var4, var5);
            var7 = Optional.of(Arrays.stream(var8.getRaw()));
         } else {
            var7 = Optional.empty();
         }

         var12 = new PalettedContainerRO.PackedData(var3.getEntries(), var7);
      } finally {
         this.release();
      }

      return var12;
   }

   private static <T> void swapPalette(int[] var0, IntUnaryOperator var1) {
      int var2 = -1;
      int var3 = -1;

      for(int var4 = 0; var4 < var0.length; ++var4) {
         int var5 = var0[var4];
         if (var5 != var2) {
            var2 = var5;
            var3 = var1.applyAsInt(var5);
         }

         var0[var4] = var3;
      }
   }

   @Override
   public int getSerializedSize() {
      return this.data.getSerializedSize();
   }

   @Override
   public boolean maybeHas(Predicate<T> var1) {
      return this.data.palette.maybeHas(var1);
   }

   public PalettedContainer<T> copy() {
      return new PalettedContainer<>(this.registry, this.strategy, this.data.copy());
   }

   @Override
   public PalettedContainer<T> recreate() {
      return new PalettedContainer<>(this.registry, this.data.palette.valueFor(0), this.strategy);
   }

   @Override
   public void count(PalettedContainer.CountConsumer<T> var1) {
      if (this.data.palette.getSize() == 1) {
         var1.accept((int)this.data.palette.valueFor(0), this.data.storage.getSize());
      } else {
         Int2IntOpenHashMap var2 = new Int2IntOpenHashMap();
         this.data.storage.getAll(var1x -> var2.addTo(var1x, 1));
         var2.int2IntEntrySet().forEach(var2x -> var1.accept((int)this.data.palette.valueFor(var2x.getIntKey()), var2x.getIntValue()));
      }
   }

   static record Configuration<T>(Palette.Factory a, int b) {
      private final Palette.Factory factory;
      private final int bits;

      Configuration(Palette.Factory var1, int var2) {
         super();
         this.factory = var1;
         this.bits = var2;
      }

      public PalettedContainer.Data<T> createData(IdMap<T> var1, PaletteResize<T> var2, int var3) {
         Object var4 = this.bits == 0 ? new ZeroBitStorage(var3) : new SimpleBitStorage(this.bits, var3);
         Palette var5 = this.factory.create(this.bits, var1, var2, List.of());
         return new PalettedContainer.Data<>(this, (BitStorage)var4, var5);
      }
   }

   @FunctionalInterface
   public interface CountConsumer<T> {
      void accept(T var1, int var2);
   }

   static record Data<T>(PalettedContainer.Configuration<T> a, BitStorage b, Palette<T> c) {
      private final PalettedContainer.Configuration<T> configuration;
      final BitStorage storage;
      final Palette<T> palette;

      Data(PalettedContainer.Configuration<T> var1, BitStorage var2, Palette<T> var3) {
         super();
         this.configuration = var1;
         this.storage = var2;
         this.palette = var3;
      }

      public void copyFrom(Palette<T> var1, BitStorage var2) {
         for(int var3 = 0; var3 < var2.getSize(); ++var3) {
            Object var4 = var1.valueFor(var2.get(var3));
            this.storage.set(var3, this.palette.idFor((T)var4));
         }
      }

      public int getSerializedSize() {
         return 1 + this.palette.getSerializedSize() + VarInt.getByteSize(this.storage.getRaw().length) + this.storage.getRaw().length * 8;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeByte(this.storage.getBits());
         this.palette.write(var1);
         var1.writeLongArray(this.storage.getRaw());
      }

      public PalettedContainer.Data<T> copy() {
         return new PalettedContainer.Data<>(this.configuration, this.storage.copy(), this.palette.copy());
      }
   }

   public abstract static class Strategy {
      public static final Palette.Factory SINGLE_VALUE_PALETTE_FACTORY = SingleValuePalette::create;
      public static final Palette.Factory LINEAR_PALETTE_FACTORY = LinearPalette::create;
      public static final Palette.Factory HASHMAP_PALETTE_FACTORY = HashMapPalette::create;
      static final Palette.Factory GLOBAL_PALETTE_FACTORY = GlobalPalette::create;
      public static final PalettedContainer.Strategy SECTION_STATES = new PalettedContainer.Strategy(4) {
         @Override
         public <A> PalettedContainer.Configuration<A> getConfiguration(IdMap<A> var1, int var2) {
            return switch(var2) {
               case 0 -> new PalettedContainer.Configuration(SINGLE_VALUE_PALETTE_FACTORY, var2);
               case 1, 2, 3, 4 -> new PalettedContainer.Configuration(LINEAR_PALETTE_FACTORY, 4);
               case 5, 6, 7, 8 -> new PalettedContainer.Configuration(HASHMAP_PALETTE_FACTORY, var2);
               default -> new PalettedContainer.Configuration(PalettedContainer.Strategy.GLOBAL_PALETTE_FACTORY, Mth.ceillog2(var1.size()));
            };
         }
      };
      public static final PalettedContainer.Strategy SECTION_BIOMES = new PalettedContainer.Strategy(2) {
         @Override
         public <A> PalettedContainer.Configuration<A> getConfiguration(IdMap<A> var1, int var2) {
            return switch(var2) {
               case 0 -> new PalettedContainer.Configuration(SINGLE_VALUE_PALETTE_FACTORY, var2);
               case 1, 2, 3 -> new PalettedContainer.Configuration(LINEAR_PALETTE_FACTORY, var2);
               default -> new PalettedContainer.Configuration(PalettedContainer.Strategy.GLOBAL_PALETTE_FACTORY, Mth.ceillog2(var1.size()));
            };
         }
      };
      private final int sizeBits;

      Strategy(int var1) {
         super();
         this.sizeBits = var1;
      }

      public int size() {
         return 1 << this.sizeBits * 3;
      }

      public int getIndex(int var1, int var2, int var3) {
         return (var2 << this.sizeBits | var3) << this.sizeBits | var1;
      }

      public abstract <A> PalettedContainer.Configuration<A> getConfiguration(IdMap<A> var1, int var2);

      <A> int calculateBitsForSerialization(IdMap<A> var1, int var2) {
         int var3 = Mth.ceillog2(var2);
         PalettedContainer.Configuration var4 = this.getConfiguration(var1, var3);
         return var4.factory() == GLOBAL_PALETTE_FACTORY ? var3 : var4.bits();
      }
   }
}
