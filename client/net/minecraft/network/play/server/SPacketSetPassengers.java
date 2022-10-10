package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSetPassengers implements Packet<INetHandlerPlayClient> {
   private int field_186973_a;
   private int[] field_186974_b;

   public SPacketSetPassengers() {
      super();
   }

   public SPacketSetPassengers(Entity var1) {
      super();
      this.field_186973_a = var1.func_145782_y();
      List var2 = var1.func_184188_bt();
      this.field_186974_b = new int[var2.size()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.field_186974_b[var3] = ((Entity)var2.get(var3)).func_145782_y();
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_186973_a = var1.func_150792_a();
      this.field_186974_b = var1.func_186863_b();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_186973_a);
      var1.func_186875_a(this.field_186974_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_184328_a(this);
   }

   public int[] func_186971_a() {
      return this.field_186974_b;
   }

   public int func_186972_b() {
      return this.field_186973_a;
   }
}
