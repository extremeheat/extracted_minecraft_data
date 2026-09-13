package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities extends Packet {
   private int[] field_149100_a;

   public S13PacketDestroyEntities() {
      super();
   }

   public S13PacketDestroyEntities(int... var1) {
      super();
      this.field_149100_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149100_a = new int[var1.readByte()];

      for(int var2 = 0; var2 < this.field_149100_a.length; ++var2) {
         this.field_149100_a[var2] = var1.readInt();
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeByte(this.field_149100_a.length);

      for(int var2 = 0; var2 < this.field_149100_a.length; ++var2) {
         var1.writeInt(this.field_149100_a[var2]);
      }
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147238_a(this);
   }

   @Override
   public String func_148835_b() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < this.field_149100_a.length; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(this.field_149100_a[var2]);
      }

      return String.format("entities=%d[%s]", this.field_149100_a.length, var1);
   }

   public int[] func_149098_c() {
      return this.field_149100_a;
   }
}
