package net.minecraft.network.protocol.common.custom;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;

public record BreezeDebugPayload(BreezeDebugPayload.BreezeInfo c) implements CustomPacketPayload {
   private final BreezeDebugPayload.BreezeInfo breezeInfo;
   public static final StreamCodec<FriendlyByteBuf, BreezeDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      BreezeDebugPayload::write, BreezeDebugPayload::new
   );
   public static final CustomPacketPayload.Type<BreezeDebugPayload> TYPE = CustomPacketPayload.createType("debug/breeze");

   private BreezeDebugPayload(FriendlyByteBuf var1) {
      this(new BreezeDebugPayload.BreezeInfo(var1));
   }

   public BreezeDebugPayload(BreezeDebugPayload.BreezeInfo var1) {
      super();
      this.breezeInfo = var1;
   }

   private void write(FriendlyByteBuf var1) {
      this.breezeInfo.write(var1);
   }

   @Override
   public CustomPacketPayload.Type<BreezeDebugPayload> type() {
      return TYPE;
   }

   public static record BreezeInfo(UUID a, int b, Integer c, BlockPos d) {
      private final UUID uuid;
      private final int id;
      private final Integer attackTarget;
      private final BlockPos jumpTarget;

      public BreezeInfo(FriendlyByteBuf var1) {
         this(var1.readUUID(), var1.readInt(), var1.readNullable(FriendlyByteBuf::readInt), var1.readNullable(BlockPos.STREAM_CODEC));
      }

      public BreezeInfo(UUID var1, int var2, Integer var3, BlockPos var4) {
         super();
         this.uuid = var1;
         this.id = var2;
         this.attackTarget = var3;
         this.jumpTarget = var4;
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
