package net.minecraft.tileentity;

import net.minecraft.util.EnumFacing;

public class TileEntityEndPortal extends TileEntity {
   public TileEntityEndPortal(TileEntityType<?> var1) {
      super(var1);
   }

   public TileEntityEndPortal() {
      this(TileEntityType.field_200983_n);
   }

   public boolean func_184313_a(EnumFacing var1) {
      return var1 == EnumFacing.UP;
   }
}
