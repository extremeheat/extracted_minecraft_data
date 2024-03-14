package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record GlobalPos(ResourceKey<Level> d, BlockPos e) {
   private final ResourceKey<Level> dimension;
   private final BlockPos pos;
   public static final MapCodec<GlobalPos> MAP_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos))
            .apply(var0, GlobalPos::of)
   );
   public static final Codec<GlobalPos> CODEC = MAP_CODEC.codec();
   public static final StreamCodec<ByteBuf, GlobalPos> STREAM_CODEC = StreamCodec.composite(
      ResourceKey.streamCodec(Registries.DIMENSION), GlobalPos::dimension, BlockPos.STREAM_CODEC, GlobalPos::pos, GlobalPos::of
   );

   public GlobalPos(ResourceKey<Level> var1, BlockPos var2) {
      super();
      this.dimension = var1;
      this.pos = var2;
   }

   public static GlobalPos of(ResourceKey<Level> var0, BlockPos var1) {
      return new GlobalPos(var0, var1);
   }

   public String toString() {
      return this.dimension + " " + this.pos;
   }
}
