package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketDestroyEntities implements Packet<INetHandlerPlayClient> {
   private int[] field_149100_a;

   public SPacketDestroyEntities() {
      super();
   }

   public SPacketDestroyEntities(int... var1) {
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
      int[] var2 = this.field_149100_a;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1.func_150787_b(var5);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147238_a(this);
   }

   public int[] func_149098_c() {
      return this.field_149100_a;
   }
}
