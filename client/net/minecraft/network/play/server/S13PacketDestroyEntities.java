package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities implements Packet<INetHandlerPlayClient> {
   private int[] field_149100_a;

   public S13PacketDestroyEntities() {
      super();
   }

   public S13PacketDestroyEntities(int... var1) {
      super();
      this.field_149100_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149100_a = new int[var1.func_150792_a()];

      for(int var2 = 0; var2 < this.field_149100_a.length; ++var2) {
         this.field_149100_a[var2] = var1.func_150792_a();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149100_a.length);

      for(int var2 = 0; var2 < this.field_149100_a.length; ++var2) {
         var1.func_150787_b(this.field_149100_a[var2]);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147238_a(this);
   }

   public int[] func_149098_c() {
      return this.field_149100_a;
   }
}
