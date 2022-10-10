package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

public interface IBlockStatePalette<T> {
   int func_186041_a(T var1);

   @Nullable
   T func_186039_a(int var1);

   void func_186038_a(PacketBuffer var1);

   void func_186037_b(PacketBuffer var1);

   int func_186040_a();

   void func_196968_a(NBTTagList var1);
}
