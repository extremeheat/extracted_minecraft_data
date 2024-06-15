package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
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
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAttributesPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      ClientboundUpdateAttributesPacket::getEntityId,
      ClientboundUpdateAttributesPacket.AttributeSnapshot.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ClientboundUpdateAttributesPacket::getValues,
      ClientboundUpdateAttributesPacket::new
   );
   private final int entityId;
   private final List<ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes;

   public ClientboundUpdateAttributesPacket(int var1, Collection<AttributeInstance> var2) {
      super();
      this.entityId = var1;
      this.attributes = Lists.newArrayList();

      for (AttributeInstance var4 : var2) {
         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(var4.getAttribute(), var4.getBaseValue(), var4.getModifiers()));
      }
   }

   private ClientboundUpdateAttributesPacket(int var1, List<ClientboundUpdateAttributesPacket.AttributeSnapshot> var2) {
      super();
      this.entityId = var1;
      this.attributes = var2;
   }

   @Override
   public PacketType<ClientboundUpdateAttributesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_ATTRIBUTES;
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

   public static record AttributeSnapshot(Holder<Attribute> attribute, double base, Collection<AttributeModifier> modifiers) {
      public static final StreamCodec<ByteBuf, AttributeModifier> MODIFIER_STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC,
         AttributeModifier::id,
         ByteBufCodecs.DOUBLE,
         AttributeModifier::amount,
         AttributeModifier.Operation.STREAM_CODEC,
         AttributeModifier::operation,
         (var0, var1, var2) -> new AttributeModifier(var0, "Unknown synced attribute modifier", var1, var2)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAttributesPacket.AttributeSnapshot> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE),
         ClientboundUpdateAttributesPacket.AttributeSnapshot::attribute,
         ByteBufCodecs.DOUBLE,
         ClientboundUpdateAttributesPacket.AttributeSnapshot::base,
         MODIFIER_STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)),
         ClientboundUpdateAttributesPacket.AttributeSnapshot::modifiers,
         ClientboundUpdateAttributesPacket.AttributeSnapshot::new
      );

      public AttributeSnapshot(Holder<Attribute> attribute, double base, Collection<AttributeModifier> modifiers) {
         super();
         this.attribute = attribute;
         this.base = base;
         this.modifiers = modifiers;
      }
   }
}
