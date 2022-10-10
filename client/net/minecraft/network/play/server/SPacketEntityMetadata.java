package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityMetadata implements Packet<INetHandlerPlayClient> {
   private int field_149379_a;
   private List<EntityDataManager.DataEntry<?>> field_149378_b;

   public SPacketEntityMetadata() {
      super();
   }

   public SPacketEntityMetadata(int var1, EntityDataManager var2, boolean var3) {
      super();
      this.field_149379_a = var1;
      if (var3) {
         this.field_149378_b = var2.func_187231_c();
         var2.func_187230_e();
      } else {
         this.field_149378_b = var2.func_187221_b();
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149379_a = var1.func_150792_a();
      this.field_149378_b = EntityDataManager.func_187215_b(var1);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149379_a);
      EntityDataManager.func_187229_a(this.field_149378_b, var1);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147284_a(this);
   }

   public List<EntityDataManager.DataEntry<?>> func_149376_c() {
      return this.field_149378_b;
   }

   public int func_149375_d() {
      return this.field_149379_a;
   }
}
