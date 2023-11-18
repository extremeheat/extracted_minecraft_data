package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record StructuresDebugPayload(ResourceKey<Level> b, BoundingBox c, List<StructuresDebugPayload.PieceInfo> d) implements CustomPacketPayload {
   private final ResourceKey<Level> dimension;
   private final BoundingBox mainBB;
   private final List<StructuresDebugPayload.PieceInfo> pieces;
   public static final ResourceLocation ID = new ResourceLocation("debug/structures");

   public StructuresDebugPayload(FriendlyByteBuf var1) {
      this(var1.readResourceKey(Registries.DIMENSION), readBoundingBox(var1), var1.readList(StructuresDebugPayload.PieceInfo::new));
   }

   public StructuresDebugPayload(ResourceKey<Level> var1, BoundingBox var2, List<StructuresDebugPayload.PieceInfo> var3) {
      super();
      this.dimension = var1;
      this.mainBB = var2;
      this.pieces = var3;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeResourceKey(this.dimension);
      writeBoundingBox(var1, this.mainBB);
      var1.writeCollection(this.pieces, (var1x, var2) -> var2.write(var1));
   }

   @Override
   public ResourceLocation id() {
      return ID;
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

   public static record PieceInfo(BoundingBox a, boolean b) {
      private final BoundingBox boundingBox;
      private final boolean isStart;

      public PieceInfo(FriendlyByteBuf var1) {
         this(StructuresDebugPayload.readBoundingBox(var1), var1.readBoolean());
      }

      public PieceInfo(BoundingBox var1, boolean var2) {
         super();
         this.boundingBox = var1;
         this.isStart = var2;
      }

      public void write(FriendlyByteBuf var1) {
         StructuresDebugPayload.writeBoundingBox(var1, this.boundingBox);
         var1.writeBoolean(this.isStart);
      }
   }
}
