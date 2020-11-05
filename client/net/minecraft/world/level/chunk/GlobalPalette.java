package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette<T> implements Palette<T> {
   private final IdMapper<T> registry;
   private final T defaultValue;

   public GlobalPalette(IdMapper<T> var1, T var2) {
      super();
      this.registry = var1;
      this.defaultValue = var2;
   }

   public int idFor(T var1) {
      int var2 = this.registry.getId(var1);
      return var2 == -1 ? 0 : var2;
   }

   public boolean maybeHas(Predicate<T> var1) {
      return true;
   }

   public T valueFor(int var1) {
      Object var2 = this.registry.byId(var1);
      return var2 == null ? this.defaultValue : var2;
   }

   public void read(FriendlyByteBuf var1) {
   }

   public void write(FriendlyByteBuf var1) {
   }

   public int getSerializedSize() {
      return FriendlyByteBuf.getVarIntSize(0);
   }

   public void read(ListTag var1) {
   }
}
