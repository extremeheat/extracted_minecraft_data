package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Locale;
import net.minecraft.client.renderer.RenderPipelines;

public enum ChunkSectionLayer {
   SOLID(RenderPipelines.SOLID_TERRAIN, 4194304, false),
   CUTOUT(RenderPipelines.CUTOUT_TERRAIN, 4194304, false),
   TRANSLUCENT(RenderPipelines.TRANSLUCENT_TERRAIN, 786432, true);

   private final RenderPipeline pipeline;
   private final int bufferSize;
   private final boolean sortOnUpload;
   private final String label;

   private ChunkSectionLayer(final RenderPipeline pipeline, final int bufferSize, final boolean sortOnUpload) {
      this.pipeline = pipeline;
      this.bufferSize = bufferSize;
      this.sortOnUpload = sortOnUpload;
      this.label = this.toString().toLowerCase(Locale.ROOT);
   }

   public RenderPipeline pipeline() {
      return this.pipeline;
   }

   public int bufferSize() {
      return this.bufferSize;
   }

   public String label() {
      return this.label;
   }

   public boolean sortOnUpload() {
      return this.sortOnUpload;
   }

   // $FF: synthetic method
   private static ChunkSectionLayer[] $values() {
      return new ChunkSectionLayer[]{SOLID, CUTOUT, TRANSLUCENT};
   }
}
