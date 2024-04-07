package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public class ServerboundUseItemPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundUseItemPacket> STREAM_CODEC = Packet.codec(
      ServerboundUseItemPacket::write, ServerboundUseItemPacket::new
   );
   private final InteractionHand hand;
   private final int sequence;

   public ServerboundUseItemPacket(InteractionHand var1, int var2) {
      super();
      this.hand = var1;
      this.sequence = var2;
   }

   private ServerboundUseItemPacket(FriendlyByteBuf var1) {
      super();
      this.hand = var1.readEnum(InteractionHand.class);
      this.sequence = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.hand);
      var1.writeVarInt(this.sequence);
   }

   @Override
   public PacketType<ServerboundUseItemPacket> type() {
      return GamePacketTypes.SERVERBOUND_USE_ITEM;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleUseItem(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public int getSequence() {
      return this.sequence;
   }
}
