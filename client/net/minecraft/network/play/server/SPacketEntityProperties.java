package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityProperties implements Packet<INetHandlerPlayClient> {
   private int field_149445_a;
   private final List<SPacketEntityProperties.Snapshot> field_149444_b = Lists.newArrayList();

   public SPacketEntityProperties() {
      super();
   }

   public SPacketEntityProperties(int var1, Collection<IAttributeInstance> var2) {
      super();
      this.field_149445_a = var1;
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         IAttributeInstance var4 = (IAttributeInstance)var3.next();
         this.field_149444_b.add(new SPacketEntityProperties.Snapshot(var4.func_111123_a().func_111108_a(), var4.func_111125_b(), var4.func_111122_c()));
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149445_a = var1.func_150792_a();
      int var2 = var1.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1.func_150789_c(64);
         double var5 = var1.readDouble();
         ArrayList var7 = Lists.newArrayList();
         int var8 = var1.func_150792_a();

         for(int var9 = 0; var9 < var8; ++var9) {
            UUID var10 = var1.func_179253_g();
            var7.add(new AttributeModifier(var10, "Unknown synced attribute modifier", var1.readDouble(), var1.readByte()));
         }

         this.field_149444_b.add(new SPacketEntityProperties.Snapshot(var4, var5, var7));
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149445_a);
      var1.writeInt(this.field_149444_b.size());
      Iterator var2 = this.field_149444_b.iterator();

      while(var2.hasNext()) {
         SPacketEntityProperties.Snapshot var3 = (SPacketEntityProperties.Snapshot)var2.next();
         var1.func_180714_a(var3.func_151409_a());
         var1.writeDouble(var3.func_151410_b());
         var1.func_150787_b(var3.func_151408_c().size());
         Iterator var4 = var3.func_151408_c().iterator();

         while(var4.hasNext()) {
            AttributeModifier var5 = (AttributeModifier)var4.next();
            var1.func_179252_a(var5.func_111167_a());
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

   public List<SPacketEntityProperties.Snapshot> func_149441_d() {
      return this.field_149444_b;
   }

   public class Snapshot {
      private final String field_151412_b;
      private final double field_151413_c;
      private final Collection<AttributeModifier> field_151411_d;

      public Snapshot(String var2, double var3, Collection<AttributeModifier> var5) {
         super();
         this.field_151412_b = var2;
         this.field_151413_c = var3;
         this.field_151411_d = var5;
      }

      public String func_151409_a() {
         return this.field_151412_b;
      }

      public double func_151410_b() {
         return this.field_151413_c;
      }

      public Collection<AttributeModifier> func_151408_c() {
         return this.field_151411_d;
      }
   }
}
