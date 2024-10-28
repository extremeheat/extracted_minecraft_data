package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record GlobalPos(ResourceKey<Level> dimension, BlockPos pos) {
   public static final MapCodec<GlobalPos> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos)).apply(var0, GlobalPos::of);
   });
   public static final Codec<GlobalPos> CODEC;
   public static final StreamCodec<ByteBuf, GlobalPos> STREAM_CODEC;

   public GlobalPos(ResourceKey<Level> var1, BlockPos var2) {
      super();
      this.dimension = var1;
      this.pos = var2;
   }

   public static GlobalPos of(ResourceKey<Level> var0, BlockPos var1) {
      return new GlobalPos(var0, var1);
   }

   public String toString() {
      String var10000 = String.valueOf(this.dimension);
      return var10000 + " " + String.valueOf(this.pos);
   }

   public boolean isCloseEnough(ResourceKey<Level> var1, BlockPos var2, int var3) {
      return this.dimension.equals(var1) && this.pos.distChessboard(var2) <= var3;
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   public BlockPos pos() {
      return this.pos;
   }

   static {
      CODEC = MAP_CODEC.codec();
      STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(Registries.DIMENSION), GlobalPos::dimension, BlockPos.STREAM_CODEC, GlobalPos::pos, GlobalPos::of);
   }
}
