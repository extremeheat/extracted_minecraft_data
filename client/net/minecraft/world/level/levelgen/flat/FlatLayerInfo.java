package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlatLayerInfo {
   public static final Codec<FlatLayerInfo> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(0, 256).fieldOf("height").forGetter(FlatLayerInfo::getHeight), Registry.BLOCK.fieldOf("block").orElse(Blocks.AIR).forGetter((var0x) -> {
         return var0x.getBlockState().getBlock();
      })).apply(var0, FlatLayerInfo::new);
   });
   private final BlockState blockState;
   private final int height;
   private int start;

   public FlatLayerInfo(int var1, Block var2) {
      super();
      this.height = var1;
      this.blockState = var2.defaultBlockState();
   }

   public int getHeight() {
      return this.height;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public int getStart() {
      return this.start;
   }

   public void setStart(int var1) {
      this.start = var1;
   }

   public String toString() {
      return (this.height != 1 ? this.height + "*" : "") + Registry.BLOCK.getKey(this.blockState.getBlock());
   }
}
