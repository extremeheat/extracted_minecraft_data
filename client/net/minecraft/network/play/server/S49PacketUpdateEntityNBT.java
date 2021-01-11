package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S49PacketUpdateEntityNBT implements Packet<INetHandlerPlayClient> {
   private int field_179766_a;
   private NBTTagCompound field_179765_b;

   public S49PacketUpdateEntityNBT() {
      super();
   }

   public S49PacketUpdateEntityNBT(int var1, NBTTagCompound var2) {
      super();
      this.field_179766_a = var1;
      this.field_179765_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179766_a = var1.func_150792_a();
      this.field_179765_b = var1.func_150793_b();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_179766_a);
      var1.func_150786_a(this.field_179765_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175097_a(this);
   }

   public NBTTagCompound func_179763_a() {
      return this.field_179765_b;
   }

   public Entity func_179764_a(World var1) {
      return var1.func_73045_a(this.field_179766_a);
   }
}
