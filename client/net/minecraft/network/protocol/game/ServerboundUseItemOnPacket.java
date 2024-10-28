package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public class ServerboundUseItemOnPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundUseItemOnPacket> STREAM_CODEC = Packet.codec(ServerboundUseItemOnPacket::write, ServerboundUseItemOnPacket::new);
   private final BlockHitResult blockHit;
   private final InteractionHand hand;
   private final int sequence;

   public ServerboundUseItemOnPacket(InteractionHand var1, BlockHitResult var2, int var3) {
      super();
      this.hand = var1;
      this.blockHit = var2;
      this.sequence = var3;
   }

   private ServerboundUseItemOnPacket(FriendlyByteBuf var1) {
      super();
      this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
      this.blockHit = var1.readBlockHitResult();
      this.sequence = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.hand);
      var1.writeBlockHitResult(this.blockHit);
      var1.writeVarInt(this.sequence);
   }

   public PacketType<ServerboundUseItemOnPacket> type() {
      return GamePacketTypes.SERVERBOUND_USE_ITEM_ON;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleUseItemOn(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public BlockHitResult getHitResult() {
      return this.blockHit;
   }

   public int getSequence() {
      return this.sequence;
   }
}
