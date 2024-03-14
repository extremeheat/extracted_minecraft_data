package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WorldGenAttemptDebugPayload(BlockPos c, float d, float e, float f, float g, float h) implements CustomPacketPayload {
   private final BlockPos pos;
   private final float scale;
   private final float red;
   private final float green;
   private final float blue;
   private final float alpha;
   public static final StreamCodec<FriendlyByteBuf, WorldGenAttemptDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      WorldGenAttemptDebugPayload::write, WorldGenAttemptDebugPayload::new
   );
   public static final CustomPacketPayload.Type<WorldGenAttemptDebugPayload> TYPE = CustomPacketPayload.createType("debug/worldgen_attempt");

   private WorldGenAttemptDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readFloat(), var1.readFloat(), var1.readFloat(), var1.readFloat(), var1.readFloat());
   }

   public WorldGenAttemptDebugPayload(BlockPos var1, float var2, float var3, float var4, float var5, float var6) {
      super();
      this.pos = var1;
      this.scale = var2;
      this.red = var3;
      this.green = var4;
      this.blue = var5;
      this.alpha = var6;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeFloat(this.scale);
      var1.writeFloat(this.red);
      var1.writeFloat(this.green);
      var1.writeFloat(this.blue);
      var1.writeFloat(this.alpha);
   }

   @Override
   public CustomPacketPayload.Type<WorldGenAttemptDebugPayload> type() {
      return TYPE;
   }
}
