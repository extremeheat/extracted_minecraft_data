package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAttributesPacket> STREAM_CODEC;
   private final int entityId;
   private final List<AttributeSnapshot> attributes;

   public ClientboundUpdateAttributesPacket(final int entityId, final Collection<AttributeInstance> values) {
      super();
      this.entityId = entityId;
      this.attributes = Lists.newArrayList();

      for(AttributeInstance value : values) {
         this.attributes.add(new AttributeSnapshot(value.getAttribute(), value.getBaseValue(), value.getModifiers()));
      }

   }

   private ClientboundUpdateAttributesPacket(final int entityId, final List<AttributeSnapshot> attributes) {
      super();
      this.entityId = entityId;
      this.attributes = attributes;
   }

   public PacketType<ClientboundUpdateAttributesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_ATTRIBUTES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleUpdateAttributes(this);
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

      public AttributeSnapshot {
         super();
      }

      static {
         MODIFIER_STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, AttributeModifier::id, ByteBufCodecs.DOUBLE, AttributeModifier::amount, AttributeModifier.Operation.STREAM_CODEC, AttributeModifier::operation, AttributeModifier::new);
         STREAM_CODEC = StreamCodec.composite(Attribute.STREAM_CODEC, AttributeSnapshot::attribute, ByteBufCodecs.DOUBLE, AttributeSnapshot::base, MODIFIER_STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), AttributeSnapshot::modifiers, AttributeSnapshot::new);
      }
   }
}
