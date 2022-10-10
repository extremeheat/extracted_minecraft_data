package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.border.WorldBorder;

public class SPacketWorldBorder implements Packet<INetHandlerPlayClient> {
   private SPacketWorldBorder.Action field_179795_a;
   private int field_179793_b;
   private double field_179794_c;
   private double field_179791_d;
   private double field_179792_e;
   private double field_179789_f;
   private long field_179790_g;
   private int field_179796_h;
   private int field_179797_i;

   public SPacketWorldBorder() {
      super();
   }

   public SPacketWorldBorder(WorldBorder var1, SPacketWorldBorder.Action var2) {
      super();
      this.field_179795_a = var2;
      this.field_179794_c = var1.func_177731_f();
      this.field_179791_d = var1.func_177721_g();
      this.field_179789_f = var1.func_177741_h();
      this.field_179792_e = var1.func_177751_j();
      this.field_179790_g = var1.func_177732_i();
      this.field_179793_b = var1.func_177722_l();
      this.field_179797_i = var1.func_177748_q();
      this.field_179796_h = var1.func_177740_p();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179795_a = (SPacketWorldBorder.Action)var1.func_179257_a(SPacketWorldBorder.Action.class);
      switch(this.field_179795_a) {
      case SET_SIZE:
         this.field_179792_e = var1.readDouble();
         break;
      case LERP_SIZE:
         this.field_179789_f = var1.readDouble();
         this.field_179792_e = var1.readDouble();
         this.field_179790_g = var1.func_179260_f();
         break;
      case SET_CENTER:
         this.field_179794_c = var1.readDouble();
         this.field_179791_d = var1.readDouble();
         break;
      case SET_WARNING_BLOCKS:
         this.field_179797_i = var1.func_150792_a();
         break;
      case SET_WARNING_TIME:
         this.field_179796_h = var1.func_150792_a();
         break;
      case INITIALIZE:
         this.field_179794_c = var1.readDouble();
         this.field_179791_d = var1.readDouble();
         this.field_179789_f = var1.readDouble();
         this.field_179792_e = var1.readDouble();
         this.field_179790_g = var1.func_179260_f();
         this.field_179793_b = var1.func_150792_a();
         this.field_179797_i = var1.func_150792_a();
         this.field_179796_h = var1.func_150792_a();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_179795_a);
      switch(this.field_179795_a) {
      case SET_SIZE:
         var1.writeDouble(this.field_179792_e);
         break;
      case LERP_SIZE:
         var1.writeDouble(this.field_179789_f);
         var1.writeDouble(this.field_179792_e);
         var1.func_179254_b(this.field_179790_g);
         break;
      case SET_CENTER:
         var1.writeDouble(this.field_179794_c);
         var1.writeDouble(this.field_179791_d);
         break;
      case SET_WARNING_BLOCKS:
         var1.func_150787_b(this.field_179797_i);
         break;
      case SET_WARNING_TIME:
         var1.func_150787_b(this.field_179796_h);
         break;
      case INITIALIZE:
         var1.writeDouble(this.field_179794_c);
         var1.writeDouble(this.field_179791_d);
         var1.writeDouble(this.field_179789_f);
         var1.writeDouble(this.field_179792_e);
         var1.func_179254_b(this.field_179790_g);
         var1.func_150787_b(this.field_179793_b);
         var1.func_150787_b(this.field_179797_i);
         var1.func_150787_b(this.field_179796_h);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175093_a(this);
   }

   public void func_179788_a(WorldBorder var1) {
      switch(this.field_179795_a) {
      case SET_SIZE:
         var1.func_177750_a(this.field_179792_e);
         break;
      case LERP_SIZE:
         var1.func_177738_a(this.field_179789_f, this.field_179792_e, this.field_179790_g);
         break;
      case SET_CENTER:
         var1.func_177739_c(this.field_179794_c, this.field_179791_d);
         break;
      case SET_WARNING_BLOCKS:
         var1.func_177747_c(this.field_179797_i);
         break;
      case SET_WARNING_TIME:
         var1.func_177723_b(this.field_179796_h);
         break;
      case INITIALIZE:
         var1.func_177739_c(this.field_179794_c, this.field_179791_d);
         if (this.field_179790_g > 0L) {
            var1.func_177738_a(this.field_179789_f, this.field_179792_e, this.field_179790_g);
         } else {
            var1.func_177750_a(this.field_179792_e);
         }

         var1.func_177725_a(this.field_179793_b);
         var1.func_177747_c(this.field_179797_i);
         var1.func_177723_b(this.field_179796_h);
      }

   }

   public static enum Action {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS;

      private Action() {
      }
   }
}
