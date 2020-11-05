package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public interface Palette<T> {
   int idFor(T var1);

   boolean maybeHas(Predicate<T> var1);

   @Nullable
   T valueFor(int var1);

   void read(FriendlyByteBuf var1);

   void write(FriendlyByteBuf var1);

   int getSerializedSize();

   void read(ListTag var1);
}
