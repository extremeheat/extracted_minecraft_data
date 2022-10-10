package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketEntityAction implements Packet<INetHandlerPlayServer> {
   private int field_149517_a;
   private CPacketEntityAction.Action field_149515_b;
   private int field_149516_c;

   public CPacketEntityAction() {
      super();
   }

   public CPacketEntityAction(Entity var1, CPacketEntityAction.Action var2) {
      this(var1, var2, 0);
   }

   public CPacketEntityAction(Entity var1, CPacketEntityAction.Action var2, int var3) {
      super();
      this.field_149517_a = var1.func_145782_y();
      this.field_149515_b = var2;
      this.field_149516_c = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149517_a = var1.func_150792_a();
      this.field_149515_b = (CPacketEntityAction.Action)var1.func_179257_a(CPacketEntityAction.Action.class);
      this.field_149516_c = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149517_a);
      var1.func_179249_a(this.field_149515_b);
      var1.func_150787_b(this.field_149516_c);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147357_a(this);
   }

   public CPacketEntityAction.Action func_180764_b() {
      return this.field_149515_b;
   }

   public int func_149512_e() {
      return this.field_149516_c;
   }

   public static enum Action {
      START_SNEAKING,
      STOP_SNEAKING,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;

      private Action() {
      }
   }
}
