package net.minecraft.network.protocol.game;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenScreenPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenScreenPacket> STREAM_CODEC;
   private final int containerId;
   private final MenuType<?> type;
   private final Component title;

   public ClientboundOpenScreenPacket(int var1, MenuType<?> var2, Component var3) {
      super();
      this.containerId = var1;
      this.type = var2;
      this.title = var3;
   }

   public PacketType<ClientboundOpenScreenPacket> type() {
      return GamePacketTypes.CLIENTBOUND_OPEN_SCREEN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleOpenScreen(this);
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

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundOpenScreenPacket::getContainerId, ByteBufCodecs.registry(Registries.MENU), ClientboundOpenScreenPacket::getType, ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundOpenScreenPacket::getTitle, ClientboundOpenScreenPacket::new);
   }
}
