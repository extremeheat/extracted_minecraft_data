package net.minecraft.network.play.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties extends Packet {
   private int field_149445_a;
   private final List field_149444_b = new ArrayList();

   public S20PacketEntityProperties() {
      super();
   }

   public S20PacketEntityProperties(int var1, Collection var2) {
      super();
      this.field_149445_a = var1;

      for(IAttributeInstance var4 : var2) {
         this.field_149444_b
            .add(new S20PacketEntityProperties$Snapshot(this, var4.func_111123_a().func_111108_a(), var4.func_111125_b(), var4.func_111122_c()));
      }
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149445_a = var1.readInt();
      int var2 = var1.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1.func_150789_c(64);
         double var5 = var1.readDouble();
         ArrayList var7 = new ArrayList();
         short var8 = var1.readShort();

         for(int var9 = 0; var9 < var8; ++var9) {
            UUID var10 = new UUID(var1.readLong(), var1.readLong());
            var7.add(new AttributeModifier(var10, "Unknown synced attribute modifier", var1.readDouble(), var1.readByte()));
         }

         this.field_149444_b.add(new S20PacketEntityProperties$Snapshot(this, var4, var5, var7));
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149445_a);
      var1.writeInt(this.field_149444_b.size());

      for(S20PacketEntityProperties$Snapshot var3 : this.field_149444_b) {
         var1.func_150785_a(var3.func_151409_a());
         var1.writeDouble(var3.func_151410_b());
         var1.writeShort(var3.func_151408_c().size());

         for(AttributeModifier var5 : var3.func_151408_c()) {
            var1.writeLong(var5.func_111167_a().getMostSignificantBits());
            var1.writeLong(var5.func_111167_a().getLeastSignificantBits());
            var1.writeDouble(var5.func_111164_d());
            var1.writeByte(var5.func_111169_c());
         }
      }
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147290_a(this);
   }

   public int func_149442_c() {
      return this.field_149445_a;
   }

   public List func_149441_d() {
      return this.field_149444_b;
   }
}
