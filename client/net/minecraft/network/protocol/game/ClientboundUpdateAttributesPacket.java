package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet<ClientGamePacketListener> {
   private final int entityId;
   private final List<ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes;

   public ClientboundUpdateAttributesPacket(int var1, Collection<AttributeInstance> var2) {
      super();
      this.entityId = var1;
      this.attributes = Lists.newArrayList();

      for(AttributeInstance var4 : var2) {
         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(var4.getAttribute(), var4.getBaseValue(), var4.getModifiers()));
      }
   }

   public ClientboundUpdateAttributesPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.attributes = var1.readList(
         var0 -> {
            ResourceLocation var1x = var0.readResourceLocation();
            Attribute var2 = Registry.ATTRIBUTE.get(var1x);
            double var3 = var0.readDouble();
            List var5 = var0.readList(
               var0x -> new AttributeModifier(
                     var0x.readUUID(), "Unknown synced attribute modifier", var0x.readDouble(), AttributeModifier.Operation.fromValue(var0x.readByte())
                  )
            );
            return new ClientboundUpdateAttributesPacket.AttributeSnapshot(var2, var3, var5);
         }
      );
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeCollection(this.attributes, (var0, var1x) -> {
         var0.writeResourceLocation(Registry.ATTRIBUTE.getKey(var1x.getAttribute()));
         var0.writeDouble(var1x.getBase());
         var0.writeCollection(var1x.getModifiers(), (var0x, var1xx) -> {
            var0x.writeUUID(var1xx.getId());
            var0x.writeDouble(var1xx.getAmount());
            var0x.writeByte(var1xx.getOperation().toValue());
         });
      });
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List<ClientboundUpdateAttributesPacket.AttributeSnapshot> getValues() {
      return this.attributes;
   }

   public static class AttributeSnapshot {
      private final Attribute attribute;
      private final double base;
      private final Collection<AttributeModifier> modifiers;

      public AttributeSnapshot(Attribute var1, double var2, Collection<AttributeModifier> var4) {
         super();
         this.attribute = var1;
         this.base = var2;
         this.modifiers = var4;
      }

      public Attribute getAttribute() {
         return this.attribute;
      }

      public double getBase() {
         return this.base;
      }

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }
   }
}
