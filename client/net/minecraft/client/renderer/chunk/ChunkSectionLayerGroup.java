package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.Locale;
import net.minecraft.client.Minecraft;

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

   public RenderTarget outputTarget() {
      Minecraft minecraft = Minecraft.getInstance();
      RenderTarget var10000;
      switch (this.ordinal()) {
         case 1 -> var10000 = minecraft.levelRenderer.getTranslucentTarget();
         default -> var10000 = minecraft.getMainRenderTarget();
      }

      RenderTarget renderTarget = var10000;
      return renderTarget != null ? renderTarget : minecraft.getMainRenderTarget();
   }

   // $FF: synthetic method
   private static ChunkSectionLayerGroup[] $values() {
      return new ChunkSectionLayerGroup[]{OPAQUE, TRANSLUCENT};
   }
}
