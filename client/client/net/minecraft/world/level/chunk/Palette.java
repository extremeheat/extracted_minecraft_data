package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public interface Palette<T> {
   int idFor(T var1);

   boolean maybeHas(Predicate<T> var1);

   T valueFor(int var1);

   void read(FriendlyByteBuf var1);

   void write(FriendlyByteBuf var1);

   int getSerializedSize();

   int getSize();

   Palette<T> copy();

   public interface Factory {
      <A> Palette<A> create(int var1, IdMap<A> var2, PaletteResize<A> var3, List<A> var4);
   }
}
