package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class BlockMobSpawner extends BlockContainer {
   protected BlockMobSpawner() {
      super(Material.field_151576_e);
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityMobSpawner();
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      super.func_149690_a(var1, var2, var3, var4, var5, var6, var7);
      int var8 = 15 + var1.field_73012_v.nextInt(15) + var1.field_73012_v.nextInt(15);
      this.func_149657_c(var1, var2, var3, var4, var8);
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150899_d(0);
   }
}
