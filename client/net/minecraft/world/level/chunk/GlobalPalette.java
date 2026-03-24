package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette<T> implements Palette<T> {
   private final IdMap<T> registry;

   public GlobalPalette(final IdMap<T> registry) {
      super();
      this.registry = registry;
   }

   public int idFor(final T value, final PaletteResize<T> resizeHandler) {
      int id = this.registry.getId(value);
      return id == -1 ? 0 : id;
   }

   public boolean maybeHas(final Predicate<T> predicate) {
      return true;
   }

   public T valueFor(final int index) {
      T value = this.registry.byId(index);
      if (value == null) {
         throw new MissingPaletteEntryException(index);
      } else {
         return value;
      }
   }

   public void read(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
   }

   public void write(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
   }

   public int getSerializedSize(final IdMap<T> globalMap) {
      return 0;
   }

   public int getSize() {
      return this.registry.size();
   }

   public Palette<T> copy() {
      return this;
   }
}
