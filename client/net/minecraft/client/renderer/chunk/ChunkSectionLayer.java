package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public enum ChunkSectionLayer {
   SOLID(RenderPipelines.SOLID, 4194304, false),
   CUTOUT(RenderPipelines.CUTOUT, 4194304, false),
   TRANSLUCENT(RenderPipelines.TRANSLUCENT, 786432, true),
   TRIPWIRE(RenderPipelines.TRIPWIRE, 1536, true);

   private final RenderPipeline pipeline;
   private final int bufferSize;
   private final boolean sortOnUpload;
   private final String label;

   private ChunkSectionLayer(final RenderPipeline var3, final int var4, final boolean var5) {
      this.pipeline = var3;
      this.bufferSize = var4;
      this.sortOnUpload = var5;
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

   public AbstractTexture texture() {
      TextureManager var1 = Minecraft.getInstance().getTextureManager();
      return var1.getTexture(TextureAtlas.LOCATION_BLOCKS);
   }

   // $FF: synthetic method
   private static ChunkSectionLayer[] $values() {
      return new ChunkSectionLayer[]{SOLID, CUTOUT, TRANSLUCENT, TRIPWIRE};
   }
}
