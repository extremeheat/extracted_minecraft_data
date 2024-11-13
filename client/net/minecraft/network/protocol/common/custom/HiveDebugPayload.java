package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record HiveDebugPayload(HiveInfo hiveInfo) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, HiveDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(HiveDebugPayload::write, HiveDebugPayload::new);
   public static final CustomPacketPayload.Type<HiveDebugPayload> TYPE = CustomPacketPayload.<HiveDebugPayload>createType("debug/hive");

   private HiveDebugPayload(FriendlyByteBuf var1) {
      this(new HiveInfo(var1));
   }

   public HiveDebugPayload(HiveInfo var1) {
      super();
      this.hiveInfo = var1;
   }

   private void write(FriendlyByteBuf var1) {
      this.hiveInfo.write(var1);
   }

   public CustomPacketPayload.Type<HiveDebugPayload> type() {
      return TYPE;
   }

   public static record HiveInfo(BlockPos pos, String hiveType, int occupantCount, int honeyLevel, boolean sedated) {
      public HiveInfo(FriendlyByteBuf var1) {
         this(var1.readBlockPos(), var1.readUtf(), var1.readInt(), var1.readInt(), var1.readBoolean());
      }

      public HiveInfo(BlockPos var1, String var2, int var3, int var4, boolean var5) {
         super();
         this.pos = var1;
         this.hiveType = var2;
         this.occupantCount = var3;
         this.honeyLevel = var4;
         this.sedated = var5;
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
