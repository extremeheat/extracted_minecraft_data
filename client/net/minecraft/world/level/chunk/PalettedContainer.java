package net.minecraft.world.level.chunk;

import com.google.common.annotations.VisibleForTesting;
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
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.BitStorage;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.util.ZeroBitStorage;
import org.jspecify.annotations.Nullable;

public class PalettedContainer<T> implements PaletteResize<T>, PalettedContainerRO<T> {
   private static final int MIN_PALETTE_BITS = 0;
   private volatile Data<T> data;
   private final Strategy<T> strategy;
   private final ThreadingDetector threadingDetector = new ThreadingDetector("PalettedContainer");

   public void acquire() {
      this.threadingDetector.checkAndLock();
   }

   public void release() {
      this.threadingDetector.checkAndUnlock();
   }

   public static <T> Codec<PalettedContainer<T>> codecRW(Codec<T> var0, Strategy<T> var1, T var2) {
      PalettedContainerRO.Unpacker var3 = PalettedContainer::unpack;
      return codec(var0, var1, var2, var3);
   }

   public static <T> Codec<PalettedContainerRO<T>> codecRO(Codec<T> var0, Strategy<T> var1, T var2) {
      PalettedContainerRO.Unpacker var3 = (var0x, var1x) -> unpack(var0x, var1x).map((var0) -> var0);
      return codec(var0, var1, var2, var3);
   }

   private static <T, C extends PalettedContainerRO<T>> Codec<C> codec(Codec<T> var0, Strategy<T> var1, T var2, PalettedContainerRO.Unpacker<T, C> var3) {
      return RecordCodecBuilder.create((var2x) -> var2x.group(var0.mapResult(ExtraCodecs.orElsePartial(var2)).listOf().fieldOf("palette").forGetter(PalettedContainerRO.PackedData::paletteEntries), Codec.LONG_STREAM.lenientOptionalFieldOf("data").forGetter(PalettedContainerRO.PackedData::storage)).apply(var2x, PalettedContainerRO.PackedData::new)).comapFlatMap((var2x) -> var3.read(var1, var2x), (var1x) -> var1x.pack(var1));
   }

   private PalettedContainer(Strategy<T> var1, Configuration var2, BitStorage var3, Palette<T> var4) {
      super();
      this.strategy = var1;
      this.data = new Data<T>(var2, var3, var4);
   }

   private PalettedContainer(PalettedContainer<T> var1) {
      super();
      this.strategy = var1.strategy;
      this.data = var1.data.copy();
   }

   public PalettedContainer(T var1, Strategy<T> var2) {
      super();
      this.strategy = var2;
      this.data = this.createOrReuseData((Data)null, 0);
      this.data.palette.idFor(var1, this);
   }

   private Data<T> createOrReuseData(@Nullable Data<T> var1, int var2) {
      Configuration var3 = this.strategy.getConfigurationForBitCount(var2);
      if (var1 != null && var3.equals(var1.configuration())) {
         return var1;
      } else {
         Object var4 = var3.bitsInMemory() == 0 ? new ZeroBitStorage(this.strategy.entryCount()) : new SimpleBitStorage(var3.bitsInMemory(), this.strategy.entryCount());
         Palette var5 = var3.createPalette(this.strategy, List.of());
         return new Data<T>(var3, (BitStorage)var4, var5);
      }
   }

   public int onResize(int var1, T var2) {
      Data var3 = this.data;
      Data var4 = this.createOrReuseData(var3, var1);
      var4.copyFrom(var3.palette, var3.storage);
      this.data = var4;
      return var4.palette.idFor(var2, PaletteResize.noResizeExpected());
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
      int var3 = this.data.palette.idFor(var2, this);
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
      int var3 = this.data.palette.idFor(var2, this);
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
         var3.palette.read(var1, this.strategy.globalMap());
         var1.readFixedSizeLongArray(var3.storage.getRaw());
         this.data = var3;
      } finally {
         this.release();
      }

   }

   public void write(FriendlyByteBuf var1) {
      this.acquire();

      try {
         this.data.write(var1, this.strategy.globalMap());
      } finally {
         this.release();
      }

   }

   @VisibleForTesting
   public static <T> DataResult<PalettedContainer<T>> unpack(Strategy<T> var0, PalettedContainerRO.PackedData<T> var1) {
      List var2 = var1.paletteEntries();
      int var3 = var0.entryCount();
      Configuration var4 = var0.getConfigurationForPaletteSize(var2.size());
      int var5 = var4.bitsInStorage();
      if (var1.bitsPerEntry() != -1 && var5 != var1.bitsPerEntry()) {
         return DataResult.error(() -> "Invalid bit count, calculated " + var5 + ", but container declared " + var1.bitsPerEntry());
      } else {
         Object var6;
         Palette var7;
         if (var4.bitsInMemory() == 0) {
            var7 = var4.createPalette(var0, var2);
            var6 = new ZeroBitStorage(var3);
         } else {
            Optional var8 = var1.storage();
            if (var8.isEmpty()) {
               return DataResult.error(() -> "Missing values for non-zero storage");
            }

            long[] var9 = ((LongStream)var8.get()).toArray();

            try {
               if (!var4.alwaysRepack() && var4.bitsInMemory() == var5) {
                  var7 = var4.createPalette(var0, var2);
                  var6 = new SimpleBitStorage(var4.bitsInMemory(), var3, var9);
               } else {
                  HashMapPalette var10 = new HashMapPalette(var5, var2);
                  SimpleBitStorage var11 = new SimpleBitStorage(var5, var3, var9);
                  Palette var12 = var4.createPalette(var0, var2);
                  int[] var13 = reencodeContents(var11, var10, var12);
                  var7 = var12;
                  var6 = new SimpleBitStorage(var4.bitsInMemory(), var3, var13);
               }
            } catch (SimpleBitStorage.InitializationException var14) {
               return DataResult.error(() -> "Failed to read PalettedContainer: " + var14.getMessage());
            }
         }

         return DataResult.success(new PalettedContainer(var0, var4, (BitStorage)var6, var7));
      }
   }

   public PalettedContainerRO.PackedData<T> pack(Strategy<T> var1) {
      this.acquire();

      PalettedContainerRO.PackedData var14;
      try {
         BitStorage var2 = this.data.storage;
         Palette var3 = this.data.palette;
         HashMapPalette var4 = new HashMapPalette(var2.getBits());
         int var5 = var1.entryCount();
         int[] var6 = reencodeContents(var2, var3, var4);
         Configuration var7 = var1.getConfigurationForPaletteSize(var4.getSize());
         int var9 = var7.bitsInStorage();
         Optional var8;
         if (var9 != 0) {
            SimpleBitStorage var10 = new SimpleBitStorage(var9, var5, var6);
            var8 = Optional.of(Arrays.stream(var10.getRaw()));
         } else {
            var8 = Optional.empty();
         }

         var14 = new PalettedContainerRO.PackedData(var4.getEntries(), var8, var9);
      } finally {
         this.release();
      }

      return var14;
   }

   private static <T> int[] reencodeContents(BitStorage var0, Palette<T> var1, Palette<T> var2) {
      int[] var3 = new int[var0.getSize()];
      var0.unpack(var3);
      PaletteResize var4 = PaletteResize.noResizeExpected();
      int var5 = -1;
      int var6 = -1;

      for(int var7 = 0; var7 < var3.length; ++var7) {
         int var8 = var3[var7];
         if (var8 != var5) {
            var5 = var8;
            var6 = var2.idFor(var1.valueFor(var8), var4);
         }

         var3[var7] = var6;
      }

      return var3;
   }

   public int getSerializedSize() {
      return this.data.getSerializedSize(this.strategy.globalMap());
   }

   public int bitsPerEntry() {
      return this.data.storage().getBits();
   }

   public boolean maybeHas(Predicate<T> var1) {
      return this.data.palette.maybeHas(var1);
   }

   public PalettedContainer<T> copy() {
      return new PalettedContainer<T>(this);
   }

   public PalettedContainer<T> recreate() {
      return new PalettedContainer<T>(this.data.palette.valueFor(0), this.strategy);
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
         PaletteResize var3 = PaletteResize.noResizeExpected();

         for(int var4 = 0; var4 < var2.getSize(); ++var4) {
            Object var5 = var1.valueFor(var2.get(var4));
            this.storage.set(var4, this.palette.idFor(var5, var3));
         }

      }

      public int getSerializedSize(IdMap<T> var1) {
         return 1 + this.palette.getSerializedSize(var1) + this.storage.getRaw().length * 8;
      }

      public void write(FriendlyByteBuf var1, IdMap<T> var2) {
         var1.writeByte(this.storage.getBits());
         this.palette.write(var1, var2);
         var1.writeFixedSizeLongArray(this.storage.getRaw());
      }

      public Data<T> copy() {
         return new Data<T>(this.configuration, this.storage.copy(), this.palette.copy());
      }
   }

   @FunctionalInterface
   public interface CountConsumer<T> {
      void accept(T var1, int var2);
   }
}
