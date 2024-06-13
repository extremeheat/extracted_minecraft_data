package net.minecraft.network.protocol.common.custom;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;

public record BreezeDebugPayload(BreezeDebugPayload.BreezeInfo breezeInfo) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, BreezeDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      BreezeDebugPayload::write, BreezeDebugPayload::new
   );
   public static final CustomPacketPayload.Type<BreezeDebugPayload> TYPE = CustomPacketPayload.createType("debug/breeze");

   private BreezeDebugPayload(FriendlyByteBuf var1) {
      this(new BreezeDebugPayload.BreezeInfo(var1));
   }

   public BreezeDebugPayload(BreezeDebugPayload.BreezeInfo breezeInfo) {
      super();
      this.breezeInfo = breezeInfo;
   }

   private void write(FriendlyByteBuf var1) {
      this.breezeInfo.write(var1);
   }

   @Override
   public CustomPacketPayload.Type<BreezeDebugPayload> type() {
      return TYPE;
   }

   public static record BreezeInfo(UUID uuid, int id, Integer attackTarget, BlockPos jumpTarget) {
      public BreezeInfo(FriendlyByteBuf var1) {
         this(var1.readUUID(), var1.readInt(), var1.readNullable(FriendlyByteBuf::readInt), var1.readNullable(BlockPos.STREAM_CODEC));
      }

      public BreezeInfo(UUID uuid, int id, Integer attackTarget, BlockPos jumpTarget) {
         super();
         this.uuid = uuid;
         this.id = id;
         this.attackTarget = attackTarget;
         this.jumpTarget = jumpTarget;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUUID(this.uuid);
         var1.writeInt(this.id);
         var1.writeNullable(this.attackTarget, FriendlyByteBuf::writeInt);
         var1.writeNullable(this.jumpTarget, BlockPos.STREAM_CODEC);
      }

      public String generateName() {
         return DebugEntityNameGenerator.getEntityName(this.uuid);
      }

      public String toString() {
         return this.generateName();
      }
   }
}
