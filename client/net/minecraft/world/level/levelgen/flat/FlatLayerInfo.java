package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public class FlatLayerInfo {
   public static final Codec<FlatLayerInfo> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(0, DimensionType.Y_SIZE).fieldOf("height").forGetter(FlatLayerInfo::getHeight), BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").orElse(Blocks.AIR).forGetter((var0x) -> {
         return var0x.getBlockState().getBlock();
      })).apply(var0, FlatLayerInfo::new);
   });
   private final Block block;
   private final int height;

   public FlatLayerInfo(int var1, Block var2) {
      super();
      this.height = var1;
      this.block = var2;
   }

   public int getHeight() {
      return this.height;
   }

   public BlockState getBlockState() {
      return this.block.defaultBlockState();
   }

   public String toString() {
      String var10000 = this.height != 1 ? this.height + "*" : "";
      return var10000 + String.valueOf(BuiltInRegistries.BLOCK.getKey(this.block));
   }
}
