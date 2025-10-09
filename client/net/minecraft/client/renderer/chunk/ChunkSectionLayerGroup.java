package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.Locale;
import net.minecraft.client.Minecraft;

public enum ChunkSectionLayerGroup {
   OPAQUE(new ChunkSectionLayer[]{ChunkSectionLayer.SOLID, ChunkSectionLayer.CUTOUT}),
   TRANSLUCENT(new ChunkSectionLayer[]{ChunkSectionLayer.TRANSLUCENT}),
   TRIPWIRE(new ChunkSectionLayer[]{ChunkSectionLayer.TRIPWIRE});

   private final String label;
   private final ChunkSectionLayer[] layers;

   private ChunkSectionLayerGroup(final ChunkSectionLayer... var3) {
      this.layers = var3;
      this.label = this.toString().toLowerCase(Locale.ROOT);
   }

   public String label() {
      return this.label;
   }

   public ChunkSectionLayer[] layers() {
      return this.layers;
   }

   public RenderTarget outputTarget() {
      Minecraft var1 = Minecraft.getInstance();
      RenderTarget var10000;
      switch (this.ordinal()) {
         case 1 -> var10000 = var1.levelRenderer.getTranslucentTarget();
         case 2 -> var10000 = var1.levelRenderer.getWeatherTarget();
         default -> var10000 = var1.getMainRenderTarget();
      }

      RenderTarget var2 = var10000;
      return var2 != null ? var2 : var1.getMainRenderTarget();
   }

   // $FF: synthetic method
   private static ChunkSectionLayerGroup[] $values() {
      return new ChunkSectionLayerGroup[]{OPAQUE, TRANSLUCENT, TRIPWIRE};
   }
}
