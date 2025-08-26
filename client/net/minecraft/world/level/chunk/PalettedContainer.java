package net.minecraft.world.level.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
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
   private volatile Data<T> data;
   private final Strategy strategy;
   private final ThreadingDetector threadingDetector = new ThreadingDetector("PalettedContainer");

   public void acquire() {
      this.threadingDetector.checkAndLock();
   }

   public void release() {
      this.threadingDetector.checkAndUnlock();
   }

   public static <T> Codec<PalettedContainer<T>> codecRW(IdMap<T> var0, Codec<T> var1, Strategy var2, T var3) {
      PalettedContainerRO.Unpacker var4 = PalettedContainer::unpack;
      return codec(var0, var1, var2, var3, var4);
   }

   public static <T> Codec<PalettedContainerRO<T>> codecRO(IdMap<T> var0, Codec<T> var1, Strategy var2, T var3) {
      PalettedContainerRO.Unpacker var4 = (var0x, var1x, var2x) -> unpack(var0x, var1x, var2x).map((var0) -> var0);
      return codec(var0, var1, var2, var3, var4);
   }

   private static <T, C extends PalettedContainerRO<T>> Codec<C> codec(IdMap<T> var0, Codec<T> var1, Strategy var2, T var3, PalettedContainerRO.Unpacker<T, C> var4) {
      return RecordCodecBuilder.create((var2x) -> var2x.group(var1.mapResult(ExtraCodecs.orElsePartial(var3)).listOf().fieldOf("palette").forGetter(PalettedContainerRO.PackedData::paletteEntries), Codec.LONG_STREAM.lenientOptionalFieldOf("data").forGetter(PalettedContainerRO.PackedData::storage)).apply(var2x, PalettedContainerRO.PackedData::new)).comapFlatMap((var3x) -> var4.read(var0, var2, var3x), (var2x) -> var2x.pack(var0, var2));
   }

   private PalettedContainer(IdMap<T> var1, Strategy var2, Configuration var3, BitStorage var4, List<T> var5) {
      super();
      this.registry = var1;
      this.strategy = var2;
      this.data = new Data<T>(var3, var4, var3.factory().create(var3.bits(), var1, this, var5));
   }

   private PalettedContainer(PalettedContainer<T> var1) {
      super();
      this.registry = var1.registry;
      this.strategy = var1.strategy;
      this.data = var1.data.copy(this);
   }

   public PalettedContainer(IdMap<T> var1, T var2, Strategy var3) {
      super();
      this.strategy = var3;
      this.registry = var1;
      this.data = this.createOrReuseData((Data)null, 0);
      this.data.palette.idFor(var2);
   }

   private Data<T> createOrReuseData(@Nullable Data<T> var1, int var2) {
      Configuration var3 = this.strategy.getConfiguration(this.registry.size(), var2);
      return var1 != null && var3.equals(var1.configuration()) ? var1 : var3.createData(this.registry, this, this.strategy.size());
   }

   public int onResize(int var1, T var2) {
      Data var3 = this.data;
      Data var4 = this.createOrReuseData(var3, var1);
      var4.copyFrom(var3.palette, var3.storage);
      this.data = var4;
      return var4.palette.idFor(var2);
   }

   public T getAndSet(int var1, int var2, int var3, T var4) {
      this.acquire();

      Object var5;
      try {
         var5 = this.getAndSet(this.strategy.getIndex(var1, var2, var3), var4);
      } finally {
         this.release();
      }

      return (T)var5;
   }

   public T getAndSetUnchecked(int var1, int var2, int var3, T var4) {
      return (T)this.getAndSet(this.strategy.getIndex(var1, var2, var3), var4);
   }

   private T getAndSet(int var1, T var2) {
      int var3 = this.data.palette.idFor(var2);
      int var4 = this.data.storage.getAndSet(var1, var3);
      return this.data.palette.valueFor(var4);
   }

   public void set(int var1, int var2, int var3, T var4) {
      this.acquire();

      try {
         this.set(this.strategy.getIndex(var1, var2, var3), var4);
      } finally {
         this.release();
      }

   }

   private void set(int var1, T var2) {
      int var3 = this.data.palette.idFor(var2);
      this.data.storage.set(var1, var3);
   }

   public T get(int var1, int var2, int var3) {
      return (T)this.get(this.strategy.getIndex(var1, var2, var3));
   }

   protected T get(int var1) {
      Data var2 = this.data;
      return var2.palette.valueFor(var2.storage.get(var1));
   }

   public void getAll(Consumer<T> var1) {
      Palette var2 = this.data.palette();
      IntArraySet var3 = new IntArraySet();
      BitStorage var10000 = this.data.storage;
      Objects.requireNonNull(var3);
      var10000.getAll(var3::add);
      var3.forEach((var2x) -> var1.accept(var2.valueFor(var2x)));
   }

   public void read(FriendlyByteBuf var1) {
      this.acquire();

      try {
         byte var2 = var1.readByte();
         Data var3 = this.createOrReuseData(this.data, var2);
         var3.palette.read(var1);
         var1.readFixedSizeLongArray(var3.storage.getRaw());
         this.data = var3;
      } finally {
         this.release();
      }

   }

   public void write(FriendlyByteBuf var1) {
      this.acquire();

      try {
         this.data.write(var1);
      } finally {
         this.release();
      }

   }

   private static <T> DataResult<PalettedContainer<T>> unpack(IdMap<T> var0, Strategy var1, PalettedContainerRO.PackedData<T> var2) {
      List var3 = var2.paletteEntries();
      int var4 = var0.size();
      int var5 = var1.size();
      int var6 = var1.calculateBitsForSerialization(var4, var3.size());
      Configuration var7 = var1.getConfiguration(var4, var6);
      Object var8;
      if (var6 == 0) {
         var8 = new ZeroBitStorage(var5);
      } else {
         Optional var9 = var2.storage();
         if (var9.isEmpty()) {
            return DataResult.error(() -> "Missing values for non-zero storage");
         }

         long[] var10 = ((LongStream)var9.get()).toArray();

         try {
            if (var7.factory() == PalettedContainer.Strategy.GLOBAL_PALETTE_FACTORY) {
               HashMapPalette var11 = new HashMapPalette(var0, var6, (var0x, var1x) -> 0, var3);
               SimpleBitStorage var12 = new SimpleBitStorage(var6, var5, var10);
               int[] var13 = new int[var5];
               var12.unpack(var13);
               swapPalette(var13, (var2x) -> var0.getId(var11.valueFor(var2x)));
               var8 = new SimpleBitStorage(var7.bits(), var5, var13);
            } else {
               var8 = new SimpleBitStorage(var7.bits(), var5, var10);
            }
         } catch (SimpleBitStorage.InitializationException var14) {
            return DataResult.error(() -> "Failed to read PalettedContainer: " + var14.getMessage());
         }
      }

      return DataResult.success(new PalettedContainer(var0, var1, var7, (BitStorage)var8, var3));
   }

   public PalettedContainerRO.PackedData<T> pack(IdMap<T> var1, Strategy var2) {
      this.acquire();

      PalettedContainerRO.PackedData var12;
      try {
         HashMapPalette var3 = new HashMapPalette(var1, this.data.storage.getBits(), this.dummyPaletteResize);
         int var4 = var2.size();
         int[] var5 = new int[var4];
         this.data.storage.unpack(var5);
         swapPalette(var5, (var2x) -> var3.idFor(this.data.palette.valueFor(var2x)));
         int var6 = var2.calculateBitsForSerialization(var1.size(), var3.getSize());
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

   private static void swapPalette(int[] var0, IntUnaryOperator var1) {
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

   public int getSerializedSize() {
      return this.data.getSerializedSize();
   }

   public boolean maybeHas(Predicate<T> var1) {
      return this.data.palette.maybeHas(var1);
   }

   public PalettedContainer<T> copy() {
      return new PalettedContainer<T>(this);
   }

   public PalettedContainer<T> recreate() {
      return new PalettedContainer<T>(this.registry, this.data.palette.valueFor(0), this.strategy);
   }

   public void count(CountConsumer<T> var1) {
      if (this.data.palette.getSize() == 1) {
         var1.accept(this.data.palette.valueFor(0), this.data.storage.getSize());
      } else {
         Int2IntOpenHashMap var2 = new Int2IntOpenHashMap();
         this.data.storage.getAll((var1x) -> var2.addTo(var1x, 1));
         var2.int2IntEntrySet().forEach((var2x) -> var1.accept(this.data.palette.valueFor(var2x.getIntKey()), var2x.getIntValue()));
      }
   }

   static record Data<T>(Configuration configuration, BitStorage storage, Palette<T> palette) {
      final BitStorage storage;
      final Palette<T> palette;

      Data(Configuration var1, BitStorage var2, Palette<T> var3) {
         super();
         this.configuration = var1;
         this.storage = var2;
         this.palette = var3;
      }

      public void copyFrom(Palette<T> var1, BitStorage var2) {
         for(int var3 = 0; var3 < var2.getSize(); ++var3) {
            Object var4 = var1.valueFor(var2.get(var3));
            this.storage.set(var3, this.palette.idFor(var4));
         }

      }

      public int getSerializedSize() {
         return 1 + this.palette.getSerializedSize() + this.storage.getRaw().length * 8;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeByte(this.storage.getBits());
         this.palette.write(var1);
         var1.writeFixedSizeLongArray(this.storage.getRaw());
      }

      public Data<T> copy(PaletteResize<T> var1) {
         return new Data<T>(this.configuration, this.storage.copy(), this.palette.copy(var1));
      }
   }

   static record Configuration(Palette.Factory factory, int bits) {
      Configuration(Palette.Factory var1, int var2) {
         super();
         this.factory = var1;
         this.bits = var2;
      }

      public <T> Data<T> createData(IdMap<T> var1, PaletteResize<T> var2, int var3) {
         Object var4 = this.bits == 0 ? new ZeroBitStorage(var3) : new SimpleBitStorage(this.bits, var3);
         Palette var5 = this.factory.create(this.bits, var1, var2, List.of());
         return new Data<T>(this, (BitStorage)var4, var5);
      }
   }

   public abstract static class Strategy {
      public static final Palette.Factory SINGLE_VALUE_PALETTE_FACTORY = SingleValuePalette::create;
      public static final Palette.Factory LINEAR_PALETTE_FACTORY = LinearPalette::create;
      public static final Palette.Factory HASHMAP_PALETTE_FACTORY = HashMapPalette::create;
      static final Palette.Factory GLOBAL_PALETTE_FACTORY = GlobalPalette::create;
      static final Configuration ZERO_BITS;
      static final Configuration ONE_BIT_LINEAR;
      static final Configuration TWO_BITS_LINEAR;
      static final Configuration THREE_BITS_LINEAR;
      static final Configuration FOUR_BITS_LINEAR;
      static final Configuration FIVE_BITS_HASHMAP;
      static final Configuration SIX_BITS_HASHMAP;
      static final Configuration SEVEN_BITS_HASHMAP;
      static final Configuration EIGHT_BITS_HASHMAP;
      public static final Strategy SECTION_STATES;
      public static final Strategy SECTION_BIOMES;
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

      protected abstract Configuration getConfiguration(int var1, int var2);

      int calculateBitsForSerialization(int var1, int var2) {
         int var3 = Mth.ceillog2(var2);
         Configuration var4 = this.getConfiguration(var1, var3);
         return var4.factory() == GLOBAL_PALETTE_FACTORY ? var3 : var4.bits();
      }

      static {
         ZERO_BITS = new Configuration(SINGLE_VALUE_PALETTE_FACTORY, 0);
         ONE_BIT_LINEAR = new Configuration(LINEAR_PALETTE_FACTORY, 1);
         TWO_BITS_LINEAR = new Configuration(LINEAR_PALETTE_FACTORY, 2);
         THREE_BITS_LINEAR = new Configuration(LINEAR_PALETTE_FACTORY, 3);
         FOUR_BITS_LINEAR = new Configuration(LINEAR_PALETTE_FACTORY, 4);
         FIVE_BITS_HASHMAP = new Configuration(HASHMAP_PALETTE_FACTORY, 5);
         SIX_BITS_HASHMAP = new Configuration(HASHMAP_PALETTE_FACTORY, 6);
         SEVEN_BITS_HASHMAP = new Configuration(HASHMAP_PALETTE_FACTORY, 7);
         EIGHT_BITS_HASHMAP = new Configuration(HASHMAP_PALETTE_FACTORY, 8);
         SECTION_STATES = new Strategy(4) {
            public Configuration getConfiguration(int var1, int var2) {
               Configuration var10000;
               switch (var2) {
                  case 0:
                     var10000 = PalettedContainer.Strategy.ZERO_BITS;
                     break;
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                     var10000 = PalettedContainer.Strategy.FOUR_BITS_LINEAR;
                     break;
                  case 5:
                     var10000 = PalettedContainer.Strategy.FIVE_BITS_HASHMAP;
                     break;
                  case 6:
                     var10000 = PalettedContainer.Strategy.SIX_BITS_HASHMAP;
                     break;
                  case 7:
                     var10000 = PalettedContainer.Strategy.SEVEN_BITS_HASHMAP;
                     break;
                  case 8:
                     var10000 = PalettedContainer.Strategy.EIGHT_BITS_HASHMAP;
                     break;
                  default:
                     var10000 = new Configuration(PalettedContainer.Strategy.GLOBAL_PALETTE_FACTORY, Mth.ceillog2(var1));
               }

               return var10000;
            }
         };
         SECTION_BIOMES = new Strategy(2) {
            public Configuration getConfiguration(int var1, int var2) {
               Configuration var10000;
               switch (var2) {
                  case 0 -> var10000 = PalettedContainer.Strategy.ZERO_BITS;
                  case 1 -> var10000 = PalettedContainer.Strategy.ONE_BIT_LINEAR;
                  case 2 -> var10000 = PalettedContainer.Strategy.TWO_BITS_LINEAR;
                  case 3 -> var10000 = PalettedContainer.Strategy.THREE_BITS_LINEAR;
                  default -> var10000 = new Configuration(PalettedContainer.Strategy.GLOBAL_PALETTE_FACTORY, Mth.ceillog2(var1));
               }

               return var10000;
            }
         };
      }
   }

   @FunctionalInterface
   public interface CountConsumer<T> {
      void accept(T var1, int var2);
   }
}
