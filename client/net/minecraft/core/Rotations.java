package net.minecraft.core;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.network.codec.StreamCodec;

public record Rotations(float x, float y, float z) {
   final float x;
   final float y;
   final float z;
   public static final Codec<Rotations> CODEC;
   public static final StreamCodec<ByteBuf, Rotations> STREAM_CODEC;

   public Rotations(float var1, float var2, float var3) {
      super();
      var1 = !Float.isInfinite(var1) && !Float.isNaN(var1) ? var1 % 360.0F : 0.0F;
      var2 = !Float.isInfinite(var2) && !Float.isNaN(var2) ? var2 % 360.0F : 0.0F;
      var3 = !Float.isInfinite(var3) && !Float.isNaN(var3) ? var3 % 360.0F : 0.0F;
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   static {
      CODEC = Codec.FLOAT.listOf().comapFlatMap((var0) -> Util.fixedSize((List)var0, 3).map((var0x) -> new Rotations((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2))), (var0) -> List.of(var0.x(), var0.y(), var0.z()));
      STREAM_CODEC = new StreamCodec<ByteBuf, Rotations>() {
         public Rotations decode(ByteBuf var1) {
            return new Rotations(var1.readFloat(), var1.readFloat(), var1.readFloat());
         }

         public void encode(ByteBuf var1, Rotations var2) {
            var1.writeFloat(var2.x);
            var1.writeFloat(var2.y);
            var1.writeFloat(var2.z);
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (Rotations)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
   }
}
