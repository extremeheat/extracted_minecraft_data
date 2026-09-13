package net.minecraft.tileentity;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityCommandBlock extends TileEntity {
   private final CommandBlockLogic field_145994_a = new TileEntityCommandBlock$1(this);

   public TileEntityCommandBlock() {
      super();
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      this.field_145994_a.func_145758_a(var1);
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145994_a.func_145759_b(var1);
   }

   @Override
   public Packet func_145844_m() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_145841_b(var1);
      return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 2, var1);
   }

   public CommandBlockLogic func_145993_a() {
      return this.field_145994_a;
   }
}
