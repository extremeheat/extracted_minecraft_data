package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketNBTQueryResponse implements Packet<INetHandlerPlayClient> {
   private int field_211714_a;
   @Nullable
   private NBTTagCompound field_211715_b;

   public SPacketNBTQueryResponse() {
      super();
   }

   public SPacketNBTQueryResponse(int var1, @Nullable NBTTagCompound var2) {
      super();
      this.field_211714_a = var1;
      this.field_211715_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_211714_a = var1.func_150792_a();
      this.field_211715_b = var1.func_150793_b();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_211714_a);
      var1.func_150786_a(this.field_211715_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_211522_a(this);
   }

   public int func_211713_b() {
      return this.field_211714_a;
   }

   @Nullable
   public NBTTagCompound func_211712_c() {
      return this.field_211715_b;
   }

   public boolean func_211402_a() {
      return true;
   }
}
