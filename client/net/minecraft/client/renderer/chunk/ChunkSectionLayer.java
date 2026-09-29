package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Locale;
import net.minecraft.client.renderer.RenderPipelines;
import org.jspecify.annotations.Nullable;

public enum ChunkSectionLayer {
   SOLID(RenderPipelines.SOLID_TERRAIN, RenderPipelines.SOLID_TERRAIN_MULTIDRAW, RenderPipelines.SOLID_TERRAIN_IMPROVED_FOG, RenderPipelines.SOLID_TERRAIN_IMPROVED_FOG_MULTIDRAW, 4194304, false),
   CUTOUT(RenderPipelines.CUTOUT_TERRAIN, RenderPipelines.CUTOUT_TERRAIN_MULTIDRAW, RenderPipelines.CUTOUT_TERRAIN_IMPROVED_FOG, RenderPipelines.CUTOUT_TERRAIN_IMPROVED_FOG_MULTIDRAW, 4194304, false),
   TRANSLUCENT(RenderPipelines.TRANSLUCENT_TERRAIN, RenderPipelines.TRANSLUCENT_TERRAIN_MULTIDRAW, (RenderPipeline)null, (RenderPipeline)null, 786432, true);

   private final RenderPipeline pipeline;
   private final RenderPipeline multiDrawPipeline;
   private final @Nullable RenderPipeline improvedFogPipeline;
   private final @Nullable RenderPipeline improvedFogMultiDrawPipeline;
   private final int bufferSize;
   private final boolean translucent;
   private final String label;

   private ChunkSectionLayer(final @Nullable RenderPipeline pipeline, final @Nullable RenderPipeline multiDrawPipeline, final RenderPipeline improvedFogPipeline, final RenderPipeline improvedFogMultiDrawPipeline, final int bufferSize, final boolean translucent) {
      this.pipeline = pipeline;
      this.multiDrawPipeline = multiDrawPipeline;
      this.improvedFogPipeline = improvedFogPipeline;
      this.improvedFogMultiDrawPipeline = improvedFogMultiDrawPipeline;
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

   public RenderPipeline pipeline(final boolean multiDraw, final boolean cloudsInFog) {
      if (!cloudsInFog) {
         return multiDraw ? this.multiDrawPipeline : this.pipeline;
      } else {
         RenderPipeline improvedFogPipeline = multiDraw ? this.improvedFogMultiDrawPipeline : this.improvedFogPipeline;
         if (improvedFogPipeline == null) {
            throw new IllegalStateException("Chunk section layer " + this.label + " has no pipeline variant with improved fog");
         } else {
            return improvedFogPipeline;
         }
      }
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

   public @Nullable VertexFormat vertexFormat() {
      return this.pipeline.getVertexFormatBinding(0);
   }

   // $FF: synthetic method
   private static ChunkSectionLayer[] $values() {
      return new ChunkSectionLayer[]{SOLID, CUTOUT, TRANSLUCENT};
   }
}
