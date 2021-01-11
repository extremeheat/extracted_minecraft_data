package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S43PacketCamera implements Packet<INetHandlerPlayClient> {
   public int field_179781_a;

   public S43PacketCamera() {
      super();
   }

   public S43PacketCamera(Entity var1) {
      super();
      this.field_179781_a = var1.func_145782_y();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179781_a = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_179781_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175094_a(this);
   }

   public Entity func_179780_a(World var1) {
      return var1.func_73045_a(this.field_179781_a);
   }
}
