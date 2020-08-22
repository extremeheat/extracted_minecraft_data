package net.minecraft.network.syncher;

import net.minecraft.network.FriendlyByteBuf;

public interface EntityDataSerializer {
   void write(FriendlyByteBuf var1, Object var2);

   Object read(FriendlyByteBuf var1);

   default EntityDataAccessor createAccessor(int var1) {
      return new EntityDataAccessor(var1, this);
   }

   Object copy(Object var1);
}
