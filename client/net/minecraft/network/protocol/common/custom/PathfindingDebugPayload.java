package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.pathfinder.Path;

public record PathfindingDebugPayload(int b, Path c, float d) implements CustomPacketPayload {
   private final int entityId;
   private final Path path;
   private final float maxNodeDistance;
   public static final ResourceLocation ID = new ResourceLocation("debug/path");

   public PathfindingDebugPayload(FriendlyByteBuf var1) {
      this(var1.readInt(), Path.createFromStream(var1), var1.readFloat());
   }

   public PathfindingDebugPayload(int var1, Path var2, float var3) {
      super();
      this.entityId = var1;
      this.path = var2;
      this.maxNodeDistance = var3;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.entityId);
      this.path.writeToStream(var1);
      var1.writeFloat(this.maxNodeDistance);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
