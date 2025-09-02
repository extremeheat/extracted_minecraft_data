package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette<T> implements Palette<T> {
   private final IdMap<T> registry;

   public GlobalPalette(IdMap<T> var1) {
      super();
      this.registry = var1;
   }

   public int idFor(T var1, PaletteResize<T> var2) {
      int var3 = this.registry.getId(var1);
      return var3 == -1 ? 0 : var3;
   }

   public boolean maybeHas(Predicate<T> var1) {
      return true;
   }

   public T valueFor(int var1) {
      Object var2 = this.registry.byId(var1);
      if (var2 == null) {
         throw new MissingPaletteEntryException(var1);
      } else {
         return (T)var2;
      }
   }

   public void read(FriendlyByteBuf var1, IdMap<T> var2) {
   }

   public void write(FriendlyByteBuf var1, IdMap<T> var2) {
   }

   public int getSerializedSize(IdMap<T> var1) {
      return 0;
   }

   public int getSize() {
      return this.registry.size();
   }

   public Palette<T> copy() {
      return this;
   }
}
