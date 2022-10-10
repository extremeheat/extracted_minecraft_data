package net.minecraft.tileentity;

import net.minecraft.block.BlockBed;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityBed extends TileEntity {
   private EnumDyeColor field_193053_a;

   public TileEntityBed() {
      super(TileEntityType.field_200994_y);
   }

   public TileEntityBed(EnumDyeColor var1) {
      this();
      this.func_193052_a(var1);
   }

   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 11, this.func_189517_E_());
   }

   public EnumDyeColor func_193048_a() {
      if (this.field_193053_a == null) {
         this.field_193053_a = ((BlockBed)this.func_195044_w().func_177230_c()).func_196350_d();
      }

      return this.field_193053_a;
   }

   public void func_193052_a(EnumDyeColor var1) {
      this.field_193053_a = var1;
   }
}
