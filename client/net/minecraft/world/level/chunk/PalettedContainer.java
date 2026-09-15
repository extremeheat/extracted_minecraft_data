package net.minecraft.world.level.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
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

   public static <T> Codec<PalettedContainer<T>> codecRW(final Codec<T> elementCodec, final Strategy<T> strategy, final T defaultValue) {
      PalettedContainerRO.Unpacker<T, PalettedContainer<T>> unpacker = PalettedContainer::unpack;
      return codec(elementCodec, strategy, defaultValue, unpacker);
   }

   public static <T> Codec<PalettedContainerRO<T>> codecRO(final Codec<T> elementCodec, final Strategy<T> strategy, final T defaultValue) {
      PalettedContainerRO.Unpacker<T, PalettedContainerRO<T>> unpacker = (s, data) -> unpack(s, data).map((e) -> e);
      return codec(elementCodec, strategy, defaultValue, unpacker);
   }

   private static <T, C extends PalettedContainerRO<T>> Codec<C> codec(final Codec<T> elementCodec, final Strategy<T> strategy, final T defaultValue, final PalettedContainerRO.Unpacker<T, C> unpacker) {
      return RecordCodecBuilder.create((i) -> i.group(elementCodec.mapResult(ExtraCodecs.orElsePartial(defaultValue)).listOf().fieldOf("palette").forGetter(PalettedContainerRO.PackedData::paletteEntries), Codec.LONG_STREAM.lenientOptionalFieldOf("data").forGetter(PalettedContainerRO.PackedData::storage)).apply(i, PalettedContainerRO.PackedData::new)).comapFlatMap((discData) -> unpacker.read(strategy, discData), (palettedContainer) -> palettedContainer.pack(strategy));
   }

   private PalettedContainer(final Strategy<T> strategy, final Configuration dataConfiguration, final BitStorage storage, final Palette<T> palette) {
      super();
      this.strategy = strategy;
      this.data = new Data<T>(dataConfiguration, storage, palette);
   }

   private PalettedContainer(final PalettedContainer<T> source) {
      super();
      this.strategy = source.strategy;
      this.data = source.data.copy();
   }

   public PalettedContainer(final T initialValue, final Strategy<T> strategy) {
      super();
      this.strategy = strategy;
      this.data = this.createOrReuseData((Data)null, 0);
      this.data.palette.idFor(initialValue, this);
   }

   private Data<T> createOrReuseData(final @Nullable Data<T> oldData, final int targetBits) {
      Configuration dataConfiguration = this.strategy.getConfigurationForBitCount(targetBits);
      if (oldData != null && dataConfiguration.equals(oldData.configuration())) {
         return oldData;
      } else {
         BitStorage storage = (BitStorage)(dataConfiguration.bitsInMemory() == 0 ? new ZeroBitStorage(this.strategy.entryCount()) : new SimpleBitStorage(dataConfiguration.bitsInMemory(), this.strategy.entryCount()));
         Palette<T> palette = dataConfiguration.<T>createPalette(this.strategy, List.of());
         return new Data<T>(dataConfiguration, storage, palette);
      }
   }

   public int onResize(final int bits, final T lastAddedValue) {
      Data<T> oldData = this.data;
      Data<T> newData = this.createOrReuseData(oldData, bits);
      newData.copyFrom(oldData.palette, oldData.storage);
      this.data = newData;
      return newData.palette.idFor(lastAddedValue, PaletteResize.noResizeExpected());
   }

   public T getAndSet(final int x, final int y, final int z, final T value) {
      this.acquire();

      Object var5;
      try {
         var5 = this.getAndSet(this.strategy.getIndex(x, y, z), value);
      } finally {
         this.release();
      }

      return (T)var5;
   }

   public T getAndSetUnchecked(final int x, final int y, final int z, final T value) {
      return (T)this.getAndSet(this.strategy.getIndex(x, y, z), value);
   }

   private T getAndSet(final int index, final T value) {
      int id = this.data.palette.idFor(value, this);
      int oldId = this.data.storage.getAndSet(index, id);
      return this.data.palette.valueFor(oldId);
   }

   public void set(final int x, final int y, final int z, final T value) {
      this.acquire();

      try {
         this.set(this.strategy.getIndex(x, y, z), value);
      } finally {
         this.release();
      }

   }

   private void set(final int index, final T value) {
      int id = this.data.palette.idFor(value, this);
      this.data.storage.set(index, id);
   }

   public T get(final int x, final int y, final int z) {
      return (T)this.get(this.strategy.getIndex(x, y, z));
   }

   protected T get(final int index) {
      Data<T> data = this.data;
      return data.palette.valueFor(data.storage.get(index));
   }

   public void getAll(final Consumer<T> consumer) {
      Palette<T> palette = this.data.palette();
      IntSet allExistingEntries = new IntArraySet();
      BitStorage var10000 = this.data.storage;
      Objects.requireNonNull(allExistingEntries);
      var10000.getAll(allExistingEntries::add);
      allExistingEntries.forEach((state) -> consumer.accept(palette.valueFor(state)));
   }

   public void read(final FriendlyByteBuf buffer) {
      this.acquire();

      try {
         int newBits = buffer.readByte();
         Data<T> newData = this.createOrReuseData(this.data, newBits);
         newData.palette.read(buffer, this.strategy.globalMap());
         buffer.readFixedSizeLongArray(newData.storage.getRaw());
         this.data = newData;
      } finally {
         this.release();
      }

   }

   public void write(final FriendlyByteBuf buffer) {
      this.acquire();

      try {
         this.data.write(buffer, this.strategy.globalMap());
      } finally {
         this.release();
      }

   }

   @VisibleForTesting
   public static <T> DataResult<PalettedContainer<T>> unpack(final Strategy<T> strategy, final PalettedContainerRO.PackedData<T> discData) {
      List<T> paletteEntries = discData.paletteEntries();
      int entryCount = strategy.entryCount();
      Configuration storedConfiguration = strategy.getConfigurationForPaletteSize(paletteEntries.size());
      int bitsOnDisc = storedConfiguration.bitsInStorage();
      if (discData.bitsPerEntry() != -1 && bitsOnDisc != discData.bitsPerEntry()) {
         return DataResult.error(() -> "Invalid bit count, calculated " + bitsOnDisc + ", but container declared " + discData.bitsPerEntry());
      } else {
         BitStorage storage;
         Palette<T> palette;
         if (storedConfiguration.bitsInMemory() == 0) {
            palette = storedConfiguration.<T>createPalette(strategy, paletteEntries);
            storage = new ZeroBitStorage(entryCount);
         } else {
            Optional<LongStream> dataOpt = discData.storage();
            if (dataOpt.isEmpty()) {
               return DataResult.error(() -> "Missing values for non-zero storage");
            }

            long[] data = ((LongStream)dataOpt.get()).toArray();

            try {
               if (!storedConfiguration.alwaysRepack() && storedConfiguration.bitsInMemory() == bitsOnDisc) {
                  palette = storedConfiguration.<T>createPalette(strategy, paletteEntries);
                  storage = new SimpleBitStorage(storedConfiguration.bitsInMemory(), entryCount, data);
               } else {
                  Palette<T> oldPalette = new HashMapPalette<T>(bitsOnDisc, paletteEntries);
                  SimpleBitStorage oldStorage = new SimpleBitStorage(bitsOnDisc, entryCount, data);
                  Palette<T> newPalette = storedConfiguration.<T>createPalette(strategy, paletteEntries);
                  int[] newContents = reencodeContents(oldStorage, oldPalette, newPalette);
                  palette = newPalette;
                  storage = new SimpleBitStorage(storedConfiguration.bitsInMemory(), entryCount, newContents);
               }
            } catch (SimpleBitStorage.InitializationException exception) {
               return DataResult.error(() -> "Failed to read PalettedContainer: " + exception.getMessage());
            }
         }

         return DataResult.success(new PalettedContainer(strategy, storedConfiguration, storage, palette));
      }
   }

   public PalettedContainerRO.PackedData<T> pack(final Strategy<T> strategy) {
      this.acquire();

      PalettedContainerRO.PackedData var14;
      try {
         BitStorage currentStorage = this.data.storage;
         Palette<T> currentPalette = this.data.palette;
         HashMapPalette<T> newPalette = new HashMapPalette<T>(currentStorage.getBits());
         int entryCount = strategy.entryCount();
         int[] newContents = reencodeContents(currentStorage, currentPalette, newPalette);
         Configuration storedConfiguration = strategy.getConfigurationForPaletteSize(newPalette.getSize());
         int bitsOnDisc = storedConfiguration.bitsInStorage();
         Optional<LongStream> values;
         if (bitsOnDisc != 0) {
            SimpleBitStorage storage = new SimpleBitStorage(bitsOnDisc, entryCount, newContents);
            values = Optional.of(Arrays.stream(storage.getRaw()));
         } else {
            values = Optional.empty();
         }

         var14 = new PalettedContainerRO.PackedData(newPalette.getEntries(), values, bitsOnDisc);
      } finally {
         this.release();
      }

      return var14;
   }

   private static <T> int[] reencodeContents(final BitStorage storage, final Palette<T> oldPalette, final Palette<T> newPalette) {
      int[] buffer = new int[storage.getSize()];
      storage.unpack(buffer);
      PaletteResize<T> dummyResizer = PaletteResize.<T>noResizeExpected();
      int lastReadId = -1;
      int lastWrittenId = -1;

      for(int index = 0; index < buffer.length; ++index) {
         int id = buffer[index];
         if (id != lastReadId) {
            lastReadId = id;
            lastWrittenId = newPalette.idFor(oldPalette.valueFor(id), dummyResizer);
         }

         buffer[index] = lastWrittenId;
      }

      return buffer;
   }

   public int getSerializedSize() {
      return this.data.getSerializedSize(this.strategy.globalMap());
   }

   public int bitsPerEntry() {
      return this.data.storage().getBits();
   }

   public boolean maybeHas(final Predicate<T> predicate) {
      return this.data.palette.maybeHas(predicate);
   }

   public void forEachInPalette(final Consumer<T> consumer) {
      for(int i = 0; i < this.data.palette.getSize(); ++i) {
         consumer.accept(this.data.palette.valueFor(i));
      }

   }

   public PalettedContainer<T> copy() {
      return new PalettedContainer<T>(this);
   }

   public PalettedContainer<T> recreate() {
      return new PalettedContainer<T>(this.data.palette.valueFor(0), this.strategy);
   }

   public void count(final CountConsumer<T> output) {
      if (this.data.palette.getSize() == 1) {
         output.accept(this.data.palette.valueFor(0), this.data.storage.getSize());
      } else {
         Int2IntOpenHashMap counts = new Int2IntOpenHashMap();
         this.data.storage.getAll((state) -> counts.addTo(state, 1));
         counts.int2IntEntrySet().forEach((entry) -> output.accept(this.data.palette.valueFor(entry.getIntKey()), entry.getIntValue()));
      }
   }

   private static record Data<T>(Configuration configuration, BitStorage storage, Palette<T> palette) {
      private Data {
         super();
      }

      public void copyFrom(final Palette<T> oldPalette, final BitStorage oldStorage) {
         PaletteResize<T> dummyResizer = PaletteResize.<T>noResizeExpected();
         if (oldPalette.getSize() == 1) {
            T value = oldPalette.valueFor(0);
            this.storage.fill(this.palette.idFor(value, dummyResizer));
         } else {
            int lastOldId = -1;
            int newId = -1;

            for(int i = 0; i < oldStorage.getSize(); ++i) {
               int oldId = oldStorage.get(i);
               if (oldId != lastOldId) {
                  T value = oldPalette.valueFor(oldId);
                  newId = this.palette.idFor(value, dummyResizer);
                  lastOldId = oldId;
               }

               this.storage.set(i, newId);
            }

         }
      }

      public int getSerializedSize(final IdMap<T> globalMap) {
         return 1 + this.palette.getSerializedSize(globalMap) + this.storage.getRaw().length * 8;
      }

      public void write(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
         buffer.writeByte(this.storage.getBits());
         this.palette.write(buffer, globalMap);
         buffer.writeFixedSizeLongArray(this.storage.getRaw());
      }

      public Data<T> copy() {
         return new Data<T>(this.configuration, this.storage.copy(), this.palette.copy());
      }
   }

   @FunctionalInterface
   public interface CountConsumer<T> {
      void accept(final T entry, final int count);
   }
}
