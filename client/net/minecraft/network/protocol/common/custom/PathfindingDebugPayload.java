package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;

public record PathfindingDebugPayload(int entityId, Path path, float maxNodeDistance) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, PathfindingDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(PathfindingDebugPayload::write, PathfindingDebugPayload::new);
   public static final CustomPacketPayload.Type<PathfindingDebugPayload> TYPE = CustomPacketPayload.<PathfindingDebugPayload>createType("debug/path");

   private PathfindingDebugPayload(FriendlyByteBuf var1) {
      this(var1.readInt(), Path.createFromStream(var1), var1.readFloat());
   }

   public PathfindingDebugPayload(int var1, Path var2, float var3) {
      super();
      this.entityId = var1;
      this.path = var2;
      this.maxNodeDistance = var3;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeInt(this.entityId);
      this.path.writeToStream(var1);
      var1.writeFloat(this.maxNodeDistance);
   }

   public CustomPacketPayload.Type<PathfindingDebugPayload> type() {
      return TYPE;
   }
}
