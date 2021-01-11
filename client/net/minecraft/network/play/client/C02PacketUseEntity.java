package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class C02PacketUseEntity implements Packet<INetHandlerPlayServer> {
   private int field_149567_a;
   private C02PacketUseEntity.Action field_149566_b;
   private Vec3 field_179713_c;

   public C02PacketUseEntity() {
      super();
   }

   public C02PacketUseEntity(Entity var1, C02PacketUseEntity.Action var2) {
      super();
      this.field_149567_a = var1.func_145782_y();
      this.field_149566_b = var2;
   }

   public C02PacketUseEntity(Entity var1, Vec3 var2) {
      this(var1, C02PacketUseEntity.Action.INTERACT_AT);
      this.field_179713_c = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149567_a = var1.func_150792_a();
      this.field_149566_b = (C02PacketUseEntity.Action)var1.func_179257_a(C02PacketUseEntity.Action.class);
      if (this.field_149566_b == C02PacketUseEntity.Action.INTERACT_AT) {
         this.field_179713_c = new Vec3((double)var1.readFloat(), (double)var1.readFloat(), (double)var1.readFloat());
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149567_a);
      var1.func_179249_a(this.field_149566_b);
      if (this.field_149566_b == C02PacketUseEntity.Action.INTERACT_AT) {
         var1.writeFloat((float)this.field_179713_c.field_72450_a);
         var1.writeFloat((float)this.field_179713_c.field_72448_b);
         var1.writeFloat((float)this.field_179713_c.field_72449_c);
      }

   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147340_a(this);
   }

   public Entity func_149564_a(World var1) {
      return var1.func_73045_a(this.field_149567_a);
   }

   public C02PacketUseEntity.Action func_149565_c() {
      return this.field_149566_b;
   }

   public Vec3 func_179712_b() {
      return this.field_179713_c;
   }

   public static enum Action {
      INTERACT,
      ATTACK,
      INTERACT_AT;

      private Action() {
      }
   }
}
