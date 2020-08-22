package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public interface Palette {
   int idFor(Object var1);

   boolean maybeHas(Object var1);

   @Nullable
   Object valueFor(int var1);

   void read(FriendlyByteBuf var1);

   void write(FriendlyByteBuf var1);

   int getSerializedSize();

   void read(ListTag var1);
}
