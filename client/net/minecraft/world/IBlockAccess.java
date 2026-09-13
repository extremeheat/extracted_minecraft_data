package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;

public interface IBlockAccess {
   Block func_147439_a(int var1, int var2, int var3);

   TileEntity func_147438_o(int var1, int var2, int var3);

   int func_72802_i(int var1, int var2, int var3, int var4);

   int func_72805_g(int var1, int var2, int var3);

   boolean func_147437_c(int var1, int var2, int var3);

   BiomeGenBase func_72807_a(int var1, int var2);

   int func_72800_K();

   boolean func_72806_N();

   int func_72879_k(int var1, int var2, int var3, int var4);
}
