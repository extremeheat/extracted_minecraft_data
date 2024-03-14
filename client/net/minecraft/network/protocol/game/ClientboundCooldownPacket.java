package net.minecraft.network.protocol.game;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.Item;

public record ClientboundCooldownPacket(Item b, int c) implements Packet<ClientGamePacketListener> {
   private final Item item;
   private final int duration;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCooldownPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.registry(Registries.ITEM),
      ClientboundCooldownPacket::item,
      ByteBufCodecs.VAR_INT,
      ClientboundCooldownPacket::duration,
      ClientboundCooldownPacket::new
   );

   public ClientboundCooldownPacket(Item var1, int var2) {
      super();
      this.item = var1;
      this.duration = var2;
   }

   @Override
   public PacketType<ClientboundCooldownPacket> type() {
      return GamePacketTypes.CLIENTBOUND_COOLDOWN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleItemCooldown(this);
   }
}
