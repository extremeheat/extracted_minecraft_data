package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S35PacketUpdateTileEntity implements Packet<INetHandlerPlayClient> {
   private BlockPos field_179824_a;
   private int field_148859_d;
   private NBTTagCompound field_148860_e;

   public S35PacketUpdateTileEntity() {
      super();
   }

   public S35PacketUpdateTileEntity(BlockPos var1, int var2, NBTTagCompound var3) {
      super();
      this.field_179824_a = var1;
      this.field_148859_d = var2;
      this.field_148860_e = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179824_a = var1.func_179259_c();
      this.field_148859_d = var1.readUnsignedByte();
      this.field_148860_e = var1.func_150793_b();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179824_a);
      var1.writeByte((byte)this.field_148859_d);
      var1.func_150786_a(this.field_148860_e);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147273_a(this);
   }

   public BlockPos func_179823_a() {
      return this.field_179824_a;
   }

   public int func_148853_f() {
      return this.field_148859_d;
   }

   public NBTTagCompound func_148857_g() {
      return this.field_148860_e;
   }
}
