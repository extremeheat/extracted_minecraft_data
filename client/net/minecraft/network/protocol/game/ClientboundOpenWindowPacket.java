package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenWindowPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenWindowPacket> STREAM_CODEC;
   private final int containerId;
   private final MenuType<?> type;
   private final Component title;
   private final List<Integer> additionalData;

   public ClientboundOpenWindowPacket(int var1, MenuType<?> var2, Component var3, List<Integer> var4) {
      super();
      this.containerId = var1;
      this.type = var2;
      this.title = var3;
      this.additionalData = var4;
   }

   public PacketType<ClientboundOpenWindowPacket> type() {
      return GamePacketTypes.CLIENTBOUND_OPEN_WINDOW;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleWindowScreen(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public MenuType<?> getType() {
      return this.type;
   }

   public Component getTitle() {
      return this.title;
   }

   public List<Integer> getAdditionalData() {
      return this.additionalData;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ClientboundOpenWindowPacket::getContainerId, ByteBufCodecs.registry(Registries.MENU), ClientboundOpenWindowPacket::getType, ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundOpenWindowPacket::getTitle, ByteBufCodecs.INT.apply(ByteBufCodecs.list()), ClientboundOpenWindowPacket::getAdditionalData, ClientboundOpenWindowPacket::new);
   }
}
