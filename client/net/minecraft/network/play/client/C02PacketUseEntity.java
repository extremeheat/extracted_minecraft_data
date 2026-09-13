package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.World;

public class C02PacketUseEntity extends Packet {
   private int field_149567_a;
   private C02PacketUseEntity$Action field_149566_b;

   public C02PacketUseEntity() {
      super();
   }

   public C02PacketUseEntity(Entity var1, C02PacketUseEntity$Action var2) {
      super();
      this.field_149567_a = var1.func_145782_y();
      this.field_149566_b = var2;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149567_a = var1.readInt();
      this.field_149566_b = C02PacketUseEntity$Action.access$000()[var1.readByte() % C02PacketUseEntity$Action.access$000().length];
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149567_a);
      var1.writeByte(C02PacketUseEntity$Action.access$100(this.field_149566_b));
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147340_a(this);
   }

   public Entity func_149564_a(World var1) {
      return var1.func_73045_a(this.field_149567_a);
   }

   public C02PacketUseEntity$Action func_149565_c() {
      return this.field_149566_b;
   }
}
