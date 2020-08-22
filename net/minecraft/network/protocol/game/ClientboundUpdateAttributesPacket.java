package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet {
   private int entityId;
   private final List attributes = Lists.newArrayList();

   public ClientboundUpdateAttributesPacket() {
   }

   public ClientboundUpdateAttributesPacket(int var1, Collection var2) {
      this.entityId = var1;
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         AttributeInstance var4 = (AttributeInstance)var3.next();
         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(var4.getAttribute().getName(), var4.getBaseValue(), var4.getModifiers()));
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readVarInt();
      int var2 = var1.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1.readUtf(64);
         double var5 = var1.readDouble();
         ArrayList var7 = Lists.newArrayList();
         int var8 = var1.readVarInt();

         for(int var9 = 0; var9 < var8; ++var9) {
            UUID var10 = var1.readUUID();
            var7.add(new AttributeModifier(var10, "Unknown synced attribute modifier", var1.readDouble(), AttributeModifier.Operation.fromValue(var1.readByte())));
         }

         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(var4, var5, var7));
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entityId);
      var1.writeInt(this.attributes.size());
      Iterator var2 = this.attributes.iterator();

      while(var2.hasNext()) {
         ClientboundUpdateAttributesPacket.AttributeSnapshot var3 = (ClientboundUpdateAttributesPacket.AttributeSnapshot)var2.next();
         var1.writeUtf(var3.getName());
         var1.writeDouble(var3.getBase());
         var1.writeVarInt(var3.getModifiers().size());
         Iterator var4 = var3.getModifiers().iterator();

         while(var4.hasNext()) {
            AttributeModifier var5 = (AttributeModifier)var4.next();
            var1.writeUUID(var5.getId());
            var1.writeDouble(var5.getAmount());
            var1.writeByte(var5.getOperation().toValue());
         }
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List getValues() {
      return this.attributes;
   }

   public class AttributeSnapshot {
      private final String name;
      private final double base;
      private final Collection modifiers;

      public AttributeSnapshot(String var2, double var3, Collection var5) {
         this.name = var2;
         this.base = var3;
         this.modifiers = var5;
      }

      public String getName() {
         return this.name;
      }

      public double getBase() {
         return this.base;
      }

      public Collection getModifiers() {
         return this.modifiers;
      }
   }
}
