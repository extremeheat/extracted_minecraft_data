package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;

public class SPacketAdvancementInfo implements Packet<INetHandlerPlayClient> {
   private boolean field_192605_a;
   private Map<ResourceLocation, Advancement.Builder> field_192606_b;
   private Set<ResourceLocation> field_192607_c;
   private Map<ResourceLocation, AdvancementProgress> field_192608_d;

   public SPacketAdvancementInfo() {
      super();
   }

   public SPacketAdvancementInfo(boolean var1, Collection<Advancement> var2, Set<ResourceLocation> var3, Map<ResourceLocation, AdvancementProgress> var4) {
      super();
      this.field_192605_a = var1;
      this.field_192606_b = Maps.newHashMap();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Advancement var6 = (Advancement)var5.next();
         this.field_192606_b.put(var6.func_192067_g(), var6.func_192075_a());
      }

      this.field_192607_c = var3;
      this.field_192608_d = Maps.newHashMap(var4);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_191981_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_192605_a = var1.readBoolean();
      this.field_192606_b = Maps.newHashMap();
      this.field_192607_c = Sets.newLinkedHashSet();
      this.field_192608_d = Maps.newHashMap();
      int var2 = var1.func_150792_a();

      int var3;
      ResourceLocation var4;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1.func_192575_l();
         Advancement.Builder var5 = Advancement.Builder.func_192060_b(var1);
         this.field_192606_b.put(var4, var5);
      }

      var2 = var1.func_150792_a();

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1.func_192575_l();
         this.field_192607_c.add(var4);
      }

      var2 = var1.func_150792_a();

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1.func_192575_l();
         this.field_192608_d.put(var4, AdvancementProgress.func_192100_b(var1));
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeBoolean(this.field_192605_a);
      var1.func_150787_b(this.field_192606_b.size());
      Iterator var2 = this.field_192606_b.entrySet().iterator();

      Entry var3;
      while(var2.hasNext()) {
         var3 = (Entry)var2.next();
         ResourceLocation var4 = (ResourceLocation)var3.getKey();
         Advancement.Builder var5 = (Advancement.Builder)var3.getValue();
         var1.func_192572_a(var4);
         var5.func_192057_a(var1);
      }

      var1.func_150787_b(this.field_192607_c.size());
      var2 = this.field_192607_c.iterator();

      while(var2.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var2.next();
         var1.func_192572_a(var6);
      }

      var1.func_150787_b(this.field_192608_d.size());
      var2 = this.field_192608_d.entrySet().iterator();

      while(var2.hasNext()) {
         var3 = (Entry)var2.next();
         var1.func_192572_a((ResourceLocation)var3.getKey());
         ((AdvancementProgress)var3.getValue()).func_192104_a(var1);
      }

   }

   public Map<ResourceLocation, Advancement.Builder> func_192603_a() {
      return this.field_192606_b;
   }

   public Set<ResourceLocation> func_192600_b() {
      return this.field_192607_c;
   }

   public Map<ResourceLocation, AdvancementProgress> func_192604_c() {
      return this.field_192608_d;
   }

   public boolean func_192602_d() {
      return this.field_192605_a;
   }
}
