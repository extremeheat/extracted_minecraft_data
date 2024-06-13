package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record StructuresDebugPayload(ResourceKey<Level> dimension, BoundingBox mainBB, List<StructuresDebugPayload.PieceInfo> pieces)
   implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, StructuresDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      StructuresDebugPayload::write, StructuresDebugPayload::new
   );
   public static final CustomPacketPayload.Type<StructuresDebugPayload> TYPE = CustomPacketPayload.createType("debug/structures");

   private StructuresDebugPayload(FriendlyByteBuf var1) {
      this(var1.readResourceKey(Registries.DIMENSION), readBoundingBox(var1), var1.readList(StructuresDebugPayload.PieceInfo::new));
   }

   public StructuresDebugPayload(ResourceKey<Level> dimension, BoundingBox mainBB, List<StructuresDebugPayload.PieceInfo> pieces) {
      super();
      this.dimension = dimension;
      this.mainBB = mainBB;
      this.pieces = pieces;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeResourceKey(this.dimension);
      writeBoundingBox(var1, this.mainBB);
      var1.writeCollection(this.pieces, (var1x, var2) -> var2.write(var1));
   }

   @Override
   public CustomPacketPayload.Type<StructuresDebugPayload> type() {
      return TYPE;
   }

   static BoundingBox readBoundingBox(FriendlyByteBuf var0) {
      return new BoundingBox(var0.readInt(), var0.readInt(), var0.readInt(), var0.readInt(), var0.readInt(), var0.readInt());
   }

   static void writeBoundingBox(FriendlyByteBuf var0, BoundingBox var1) {
      var0.writeInt(var1.minX());
      var0.writeInt(var1.minY());
      var0.writeInt(var1.minZ());
      var0.writeInt(var1.maxX());
      var0.writeInt(var1.maxY());
      var0.writeInt(var1.maxZ());
   }

   public static record PieceInfo(BoundingBox boundingBox, boolean isStart) {
      public PieceInfo(FriendlyByteBuf var1) {
         this(StructuresDebugPayload.readBoundingBox(var1), var1.readBoolean());
      }

      public PieceInfo(BoundingBox boundingBox, boolean isStart) {
         super();
         this.boundingBox = boundingBox;
         this.isStart = isStart;
      }

      public void write(FriendlyByteBuf var1) {
         StructuresDebugPayload.writeBoundingBox(var1, this.boundingBox);
         var1.writeBoolean(this.isStart);
      }
   }
}