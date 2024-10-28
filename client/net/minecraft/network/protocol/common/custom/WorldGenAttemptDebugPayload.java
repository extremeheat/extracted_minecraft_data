package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WorldGenAttemptDebugPayload(BlockPos pos, float scale, float red, float green, float blue, float alpha) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, WorldGenAttemptDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(WorldGenAttemptDebugPayload::write, WorldGenAttemptDebugPayload::new);
   public static final CustomPacketPayload.Type<WorldGenAttemptDebugPayload> TYPE = CustomPacketPayload.createType("debug/worldgen_attempt");

   private WorldGenAttemptDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readFloat(), var1.readFloat(), var1.readFloat(), var1.readFloat(), var1.readFloat());
   }

   public WorldGenAttemptDebugPayload(BlockPos pos, float scale, float red, float green, float blue, float alpha) {
      super();
      this.pos = pos;
      this.scale = scale;
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeFloat(this.scale);
      var1.writeFloat(this.red);
      var1.writeFloat(this.green);
      var1.writeFloat(this.blue);
      var1.writeFloat(this.alpha);
   }

   public CustomPacketPayload.Type<WorldGenAttemptDebugPayload> type() {
      return TYPE;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public float scale() {
      return this.scale;
   }

   public float red() {
      return this.red;
   }

   public float green() {
      return this.green;
   }

   public float blue() {
      return this.blue;
   }

   public float alpha() {
      return this.alpha;
   }
}
