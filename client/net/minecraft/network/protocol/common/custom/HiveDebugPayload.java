package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record HiveDebugPayload(HiveDebugPayload.HiveInfo hiveInfo) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, HiveDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(HiveDebugPayload::write, HiveDebugPayload::new);
   public static final CustomPacketPayload.Type<HiveDebugPayload> TYPE = CustomPacketPayload.createType("debug/hive");

   private HiveDebugPayload(FriendlyByteBuf var1) {
      this(new HiveDebugPayload.HiveInfo(var1));
   }

   public HiveDebugPayload(HiveDebugPayload.HiveInfo hiveInfo) {
      super();
      this.hiveInfo = hiveInfo;
   }

   private void write(FriendlyByteBuf var1) {
      this.hiveInfo.write(var1);
   }

   @Override
   public CustomPacketPayload.Type<HiveDebugPayload> type() {
      return TYPE;
   }

   public static record HiveInfo(BlockPos pos, String hiveType, int occupantCount, int honeyLevel, boolean sedated) {
      public HiveInfo(FriendlyByteBuf var1) {
         this(var1.readBlockPos(), var1.readUtf(), var1.readInt(), var1.readInt(), var1.readBoolean());
      }

      public HiveInfo(BlockPos pos, String hiveType, int occupantCount, int honeyLevel, boolean sedated) {
         super();
         this.pos = pos;
         this.hiveType = hiveType;
         this.occupantCount = occupantCount;
         this.honeyLevel = honeyLevel;
         this.sedated = sedated;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeBlockPos(this.pos);
         var1.writeUtf(this.hiveType);
         var1.writeInt(this.occupantCount);
         var1.writeInt(this.honeyLevel);
         var1.writeBoolean(this.sedated);
      }
   }
}
