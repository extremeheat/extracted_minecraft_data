package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.BlockLayer;

public class ChunkBufferBuilderPack {
   private final BufferBuilder[] builders = new BufferBuilder[BlockLayer.values().length];

   public ChunkBufferBuilderPack() {
      super();
      this.builders[BlockLayer.SOLID.ordinal()] = new BufferBuilder(2097152);
      this.builders[BlockLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
      this.builders[BlockLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072);
      this.builders[BlockLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
   }

   public BufferBuilder builder(BlockLayer var1) {
      return this.builders[var1.ordinal()];
   }

   public BufferBuilder builder(int var1) {
      return this.builders[var1];
   }
}
