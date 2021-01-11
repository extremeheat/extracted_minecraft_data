package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1CPacketEntityMetadata implements Packet<INetHandlerPlayClient> {
   private int field_149379_a;
   private List<DataWatcher.WatchableObject> field_149378_b;

   public S1CPacketEntityMetadata() {
      super();
   }

   public S1CPacketEntityMetadata(int var1, DataWatcher var2, boolean var3) {
      super();
      this.field_149379_a = var1;
      if (var3) {
         this.field_149378_b = var2.func_75685_c();
      } else {
         this.field_149378_b = var2.func_75688_b();
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149379_a = var1.func_150792_a();
      this.field_149378_b = DataWatcher.func_151508_b(var1);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149379_a);
      DataWatcher.func_151507_a(this.field_149378_b, var1);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147284_a(this);
   }

   public List<DataWatcher.WatchableObject> func_149376_c() {
      return this.field_149378_b;
   }

   public int func_149375_d() {
      return this.field_149379_a;
   }
}
