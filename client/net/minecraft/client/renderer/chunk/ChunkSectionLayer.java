package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Locale;
import net.minecraft.client.renderer.RenderPipelines;

public enum ChunkSectionLayer {
   SOLID(RenderPipelines.SOLID_TERRAIN, 4194304, false),
   CUTOUT(RenderPipelines.CUTOUT_TERRAIN, 4194304, false),
   TRANSLUCENT(RenderPipelines.TRANSLUCENT_TERRAIN, 786432, true);

   private final RenderPipeline pipeline;
   private final int bufferSize;
   private final boolean translucent;
   private final String label;

   private ChunkSectionLayer(final RenderPipeline pipeline, final int bufferSize, final boolean translucent) {
      this.pipeline = pipeline;
      this.bufferSize = bufferSize;
      this.translucent = translucent;
      this.label = this.toString().toLowerCase(Locale.ROOT);
   }

   public static ChunkSectionLayer byTransparency(final Transparency transparency) {
      if (transparency.hasTranslucent()) {
         return TRANSLUCENT;
      } else {
         return transparency.hasTransparent() ? CUTOUT : SOLID;
      }
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

   public boolean translucent() {
      return this.translucent;
   }

   public VertexFormat vertexFormat() {
      return this.pipeline.getVertexFormat();
   }

   // $FF: synthetic method
   private static ChunkSectionLayer[] $values() {
      return new ChunkSectionLayer[]{SOLID, CUTOUT, TRANSLUCENT};
   }
}
