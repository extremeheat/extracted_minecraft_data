package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public enum ChunkSectionLayer {
   SOLID(RenderPipelines.SOLID, 4194304, true, false),
   CUTOUT_MIPPED(RenderPipelines.CUTOUT_MIPPED, 4194304, true, false),
   CUTOUT(RenderPipelines.CUTOUT, 786432, false, false),
   TRANSLUCENT(RenderPipelines.TRANSLUCENT, 786432, true, true),
   TRIPWIRE(RenderPipelines.TRIPWIRE, 1536, true, true);

   private final RenderPipeline pipeline;
   private final int bufferSize;
   private final boolean useMipmaps;
   private final boolean sortOnUpload;
   private final String label;

   private ChunkSectionLayer(final RenderPipeline var3, final int var4, final boolean var5, final boolean var6) {
      this.pipeline = var3;
      this.bufferSize = var4;
      this.useMipmaps = var5;
      this.sortOnUpload = var6;
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

   public GpuTextureView textureView() {
      TextureManager var1 = Minecraft.getInstance().getTextureManager();
      AbstractTexture var2 = var1.getTexture(TextureAtlas.LOCATION_BLOCKS);
      var2.setUseMipmaps(this.useMipmaps);
      return var2.getTextureView();
   }

   // $FF: synthetic method
   private static ChunkSectionLayer[] $values() {
      return new ChunkSectionLayer[]{SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT, TRIPWIRE};
   }
}
