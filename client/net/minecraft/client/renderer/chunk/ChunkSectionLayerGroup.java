package net.minecraft.client.renderer.chunk;

import java.util.Locale;

public enum ChunkSectionLayerGroup {
   OPAQUE(new ChunkSectionLayer[]{ChunkSectionLayer.SOLID, ChunkSectionLayer.CUTOUT}),
   TRANSLUCENT(new ChunkSectionLayer[]{ChunkSectionLayer.TRANSLUCENT});

   private final String label;
   private final ChunkSectionLayer[] layers;

   private ChunkSectionLayerGroup(final ChunkSectionLayer... layers) {
      this.layers = layers;
      this.label = this.toString().toLowerCase(Locale.ROOT);
   }

   public String label() {
      return this.label;
   }

   public ChunkSectionLayer[] layers() {
      return this.layers;
   }

   // $FF: synthetic method
   private static ChunkSectionLayerGroup[] $values() {
      return new ChunkSectionLayerGroup[]{OPAQUE, TRANSLUCENT};
   }
}
