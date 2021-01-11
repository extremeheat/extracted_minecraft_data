package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.CombatTracker;

public class S42PacketCombatEvent implements Packet<INetHandlerPlayClient> {
   public S42PacketCombatEvent.Event field_179776_a;
   public int field_179774_b;
   public int field_179775_c;
   public int field_179772_d;
   public String field_179773_e;

   public S42PacketCombatEvent() {
      super();
   }

   public S42PacketCombatEvent(CombatTracker var1, S42PacketCombatEvent.Event var2) {
      super();
      this.field_179776_a = var2;
      EntityLivingBase var3 = var1.func_94550_c();
      switch(var2) {
      case END_COMBAT:
         this.field_179772_d = var1.func_180134_f();
         this.field_179775_c = var3 == null ? -1 : var3.func_145782_y();
         break;
      case ENTITY_DIED:
         this.field_179774_b = var1.func_180135_h().func_145782_y();
         this.field_179775_c = var3 == null ? -1 : var3.func_145782_y();
         this.field_179773_e = var1.func_151521_b().func_150260_c();
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179776_a = (S42PacketCombatEvent.Event)var1.func_179257_a(S42PacketCombatEvent.Event.class);
      if (this.field_179776_a == S42PacketCombatEvent.Event.END_COMBAT) {
         this.field_179772_d = var1.func_150792_a();
         this.field_179775_c = var1.readInt();
      } else if (this.field_179776_a == S42PacketCombatEvent.Event.ENTITY_DIED) {
         this.field_179774_b = var1.func_150792_a();
         this.field_179775_c = var1.readInt();
         this.field_179773_e = var1.func_150789_c(32767);
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_179776_a);
      if (this.field_179776_a == S42PacketCombatEvent.Event.END_COMBAT) {
         var1.func_150787_b(this.field_179772_d);
         var1.writeInt(this.field_179775_c);
      } else if (this.field_179776_a == S42PacketCombatEvent.Event.ENTITY_DIED) {
         var1.func_150787_b(this.field_179774_b);
         var1.writeInt(this.field_179775_c);
         var1.func_180714_a(this.field_179773_e);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175098_a(this);
   }

   public static enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;

      private Event() {
      }
   }
}
