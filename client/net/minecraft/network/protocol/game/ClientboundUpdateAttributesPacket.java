package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAttributesPacket> STREAM_CODEC;
   private final int entityId;
   private final List<AttributeSnapshot> attributes;

   public ClientboundUpdateAttributesPacket(int var1, Collection<AttributeInstance> var2) {
      super();
      this.entityId = var1;
      this.attributes = Lists.newArrayList();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         AttributeInstance var4 = (AttributeInstance)var3.next();
         this.attributes.add(new AttributeSnapshot(var4.getAttribute(), var4.getBaseValue(), var4.getModifiers()));
      }

   }

   private ClientboundUpdateAttributesPacket(int var1, List<AttributeSnapshot> var2) {
      super();
      this.entityId = var1;
      this.attributes = var2;
   }

   public PacketType<ClientboundUpdateAttributesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_ATTRIBUTES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List<AttributeSnapshot> getValues() {
      return this.attributes;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundUpdateAttributesPacket::getEntityId, ClientboundUpdateAttributesPacket.AttributeSnapshot.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundUpdateAttributesPacket::getValues, ClientboundUpdateAttributesPacket::new);
   }

   public static record AttributeSnapshot(Holder<Attribute> attribute, double base, Collection<AttributeModifier> modifiers) {
      public static final StreamCodec<ByteBuf, AttributeModifier> MODIFIER_STREAM_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, AttributeSnapshot> STREAM_CODEC;

      public AttributeSnapshot(Holder<Attribute> attribute, double base, Collection<AttributeModifier> modifiers) {
         super();
         this.attribute = attribute;
         this.base = base;
         this.modifiers = modifiers;
      }

      public Holder<Attribute> attribute() {
         return this.attribute;
      }

      public double base() {
         return this.base;
      }

      public Collection<AttributeModifier> modifiers() {
         return this.modifiers;
      }

      static {
         MODIFIER_STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, AttributeModifier::id, ByteBufCodecs.DOUBLE, AttributeModifier::amount, AttributeModifier.Operation.STREAM_CODEC, AttributeModifier::operation, (var0, var1, var2) -> {
            return new AttributeModifier(var0, "Unknown synced attribute modifier", var1, var2);
         });
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE), AttributeSnapshot::attribute, ByteBufCodecs.DOUBLE, AttributeSnapshot::base, MODIFIER_STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), AttributeSnapshot::modifiers, AttributeSnapshot::new);
      }
   }
}
