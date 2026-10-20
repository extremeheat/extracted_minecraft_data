package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.pipeline.RenderPipelines;
import net.minecraft.client.renderer.pipeline.variant.TerrainRenderPipelineVariant;
import org.jspecify.annotations.Nullable;

public enum ChunkSectionLayer {
   SOLID(TerrainRenderPipelineVariant.SOLID, 4194304, false),
   CUTOUT(TerrainRenderPipelineVariant.CUTOUT, 4194304, false),
   TRANSLUCENT(TerrainRenderPipelineVariant.TRANSLUCENT, 786432, true);

   private final TerrainRenderPipelineVariant variant;
   private @Nullable VertexFormat vertexFormat;
   private final int bufferSize;
   private final boolean translucent;
   private final String label;

   private ChunkSectionLayer(final TerrainRenderPipelineVariant variant, final int bufferSize, final boolean translucent) {
      this.variant = variant;
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

   public RenderPipeline pipeline(final boolean multiDraw, final boolean improvedFog, final boolean wireframe, final @Nullable OitStage oitStage) {
      return RenderPipelines.terrain(new RenderPipelines.TerrainParameters(this.variant, improvedFog, multiDraw, wireframe, oitStage));
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
      if (this.vertexFormat == null) {
         this.vertexFormat = (VertexFormat)Objects.requireNonNull(RenderPipelines.terrain(new RenderPipelines.TerrainParameters(this.variant, false, false, false, (OitStage)null)).getVertexFormatBinding(0));
      }

      return this.vertexFormat;
   }

   // $FF: synthetic method
   private static ChunkSectionLayer[] $values() {
      return new ChunkSectionLayer[]{SOLID, CUTOUT, TRANSLUCENT};
   }
}
