package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S19PacketEntityHeadLook implements Packet<INetHandlerPlayClient> {
   private int field_149384_a;
   private byte field_149383_b;

   public S19PacketEntityHeadLook() {
      super();
   }

   public S19PacketEntityHeadLook(Entity var1, byte var2) {
      super();
      this.field_149384_a = var1.func_145782_y();
      this.field_149383_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149384_a = var1.func_150792_a();
      this.field_149383_b = var1.readByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149384_a);
      var1.writeByte(this.field_149383_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147267_a(this);
   }

   public Entity func_149381_a(World var1) {
      return var1.func_73045_a(this.field_149384_a);
   }

   public byte func_149380_c() {
      return this.field_149383_b;
   }
}
