package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.MenuType;

public class ClientboundUpdateScreenPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateScreenPacket> STREAM_CODEC;
   private final MenuType<?> type;
   private final List<Integer> additionalData;

   public ClientboundUpdateScreenPacket(MenuType<?> var1, List<Integer> var2) {
      super();
      this.type = var1;
      this.additionalData = var2;
   }

   public PacketType<ClientboundUpdateScreenPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_SCREEN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateScreen(this);
   }

   public MenuType<?> getType() {
      return this.type;
   }

   public List<Integer> getAdditionalData() {
      return this.additionalData;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.registry(Registries.MENU), ClientboundUpdateScreenPacket::getType, ByteBufCodecs.INT.apply(ByteBufCodecs.list()), ClientboundUpdateScreenPacket::getAdditionalData, ClientboundUpdateScreenPacket::new);
   }
}
