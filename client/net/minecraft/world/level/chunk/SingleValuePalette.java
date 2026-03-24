package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

public class SingleValuePalette<T> implements Palette<T> {
   private @Nullable T value;

   public SingleValuePalette(final List<T> paletteEntries) {
      super();
      if (!paletteEntries.isEmpty()) {
         Validate.isTrue(paletteEntries.size() <= 1, "Can't initialize SingleValuePalette with %d values.", (long)paletteEntries.size());
         this.value = (T)paletteEntries.getFirst();
      }

   }

   public static <A> Palette<A> create(final int bits, final List<A> paletteEntries) {
      return new SingleValuePalette<A>(paletteEntries);
   }

   public int idFor(final T value, final PaletteResize<T> resizeHandler) {
      if (this.value != null && this.value != value) {
         return resizeHandler.onResize(1, value);
      } else {
         this.value = value;
         return 0;
      }
   }

   public boolean maybeHas(final Predicate<T> predicate) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return predicate.test(this.value);
      }
   }

   public T valueFor(final int index) {
      if (this.value != null && index == 0) {
         return this.value;
      } else {
         throw new IllegalStateException("Missing Palette entry for id " + index + ".");
      }
   }

   public void read(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
      this.value = globalMap.byIdOrThrow(buffer.readVarInt());
   }

   public void write(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         buffer.writeVarInt(globalMap.getId(this.value));
      }
   }

   public int getSerializedSize(final IdMap<T> globalMap) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return VarInt.getByteSize(globalMap.getId(this.value));
      }
   }

   public int getSize() {
      return 1;
   }

   public Palette<T> copy() {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return this;
      }
   }
}
