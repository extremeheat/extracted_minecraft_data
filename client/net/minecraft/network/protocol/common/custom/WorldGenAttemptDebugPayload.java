package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record WorldGenAttemptDebugPayload(BlockPos b, float c, float d, float e, float f, float g) implements CustomPacketPayload {
   private final BlockPos pos;
   private final float scale;
   private final float red;
   private final float green;
   private final float blue;
   private final float alpha;
   public static final ResourceLocation ID = new ResourceLocation("debug/worldgen_attempt");

   public WorldGenAttemptDebugPayload(FriendlyByteBuf var1) {
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

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeFloat(this.scale);
      var1.writeFloat(this.red);
      var1.writeFloat(this.green);
      var1.writeFloat(this.blue);
      var1.writeFloat(this.alpha);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
