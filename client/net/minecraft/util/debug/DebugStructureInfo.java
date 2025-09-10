package net.minecraft.util.debug;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record DebugStructureInfo(BoundingBox boundingBox, List<Piece> pieces) {
   public static final StreamCodec<ByteBuf, DebugStructureInfo> STREAM_CODEC;

   public DebugStructureInfo(BoundingBox var1, List<Piece> var2) {
      super();
      this.boundingBox = var1;
      this.pieces = var2;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BoundingBox.STREAM_CODEC, DebugStructureInfo::boundingBox, DebugStructureInfo.Piece.STREAM_CODEC.apply(ByteBufCodecs.list()), DebugStructureInfo::pieces, DebugStructureInfo::new);
   }

   public static record Piece(BoundingBox boundingBox, boolean isStart) {
      public static final StreamCodec<ByteBuf, Piece> STREAM_CODEC;

      public Piece(BoundingBox var1, boolean var2) {
         super();
         this.boundingBox = var1;
         this.isStart = var2;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(BoundingBox.STREAM_CODEC, Piece::boundingBox, ByteBufCodecs.BOOL, Piece::isStart, Piece::new);
      }
   }
}
