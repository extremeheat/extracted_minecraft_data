package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette<T> implements Palette<T> {
   private final IdMap<T> registry;

   public GlobalPalette(IdMap<T> var1) {
      super();
      this.registry = var1;
   }

   public static <A> Palette<A> create(int var0, IdMap<A> var1, PaletteResize<A> var2, List<A> var3) {
      return new GlobalPalette<>(var1);
   }

   @Override
   public int idFor(T var1) {
      int var2 = this.registry.getId((T)var1);
      return var2 == -1 ? 0 : var2;
   }

   @Override
   public boolean maybeHas(Predicate<T> var1) {
      return true;
   }

   @Override
   public T valueFor(int var1) {
      Object var2 = this.registry.byId(var1);
      if (var2 == null) {
         throw new MissingPaletteEntryException(var1);
      } else {
         return (T)var2;
      }
   }

   @Override
   public void read(FriendlyByteBuf var1) {
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }

   @Override
   public int getSerializedSize() {
      return 0;
   }

   @Override
   public int getSize() {
      return this.registry.size();
   }

   @Override
   public Palette<T> copy() {
      return this;
   }
}
