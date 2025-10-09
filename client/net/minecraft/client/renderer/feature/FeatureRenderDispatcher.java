package net.minecraft.client.renderer.feature;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.AtlasManager;

public class FeatureRenderDispatcher implements AutoCloseable {
   private final SubmitNodeStorage submitNodeStorage;
   private final BlockRenderDispatcher blockRenderDispatcher;
   private final MultiBufferSource.BufferSource bufferSource;
   private final AtlasManager atlasManager;
   private final OutlineBufferSource outlineBufferSource;
   private final MultiBufferSource.BufferSource crumblingBufferSource;
   private final Font font;
   private final ShadowFeatureRenderer shadowFeatureRenderer = new ShadowFeatureRenderer();
   private final FlameFeatureRenderer flameFeatureRenderer = new FlameFeatureRenderer();
   private final ModelFeatureRenderer modelFeatureRenderer = new ModelFeatureRenderer();
   private final ModelPartFeatureRenderer modelPartFeatureRenderer = new ModelPartFeatureRenderer();
   private final NameTagFeatureRenderer nameTagFeatureRenderer = new NameTagFeatureRenderer();
   private final TextFeatureRenderer textFeatureRenderer = new TextFeatureRenderer();
   private final LeashFeatureRenderer leashFeatureRenderer = new LeashFeatureRenderer();
   private final ItemFeatureRenderer itemFeatureRenderer = new ItemFeatureRenderer();
   private final CustomFeatureRenderer customFeatureRenderer = new CustomFeatureRenderer();
   private final BlockFeatureRenderer blockFeatureRenderer = new BlockFeatureRenderer();
   private final ParticleFeatureRenderer particleFeatureRenderer = new ParticleFeatureRenderer();

   public FeatureRenderDispatcher(SubmitNodeStorage var1, BlockRenderDispatcher var2, MultiBufferSource.BufferSource var3, AtlasManager var4, OutlineBufferSource var5, MultiBufferSource.BufferSource var6, Font var7) {
      super();
      this.submitNodeStorage = var1;
      this.blockRenderDispatcher = var2;
      this.bufferSource = var3;
      this.atlasManager = var4;
      this.outlineBufferSource = var5;
      this.crumblingBufferSource = var6;
      this.font = var7;
   }

   public void renderAllFeatures() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection var2 = (SubmitNodeCollection)var1.next();
         this.shadowFeatureRenderer.render(var2, this.bufferSource);
         this.modelFeatureRenderer.render(var2, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.modelPartFeatureRenderer.render(var2, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.flameFeatureRenderer.render(var2, this.bufferSource, this.atlasManager);
         this.nameTagFeatureRenderer.render(var2, this.bufferSource, this.font);
         this.textFeatureRenderer.render(var2, this.bufferSource);
         this.leashFeatureRenderer.render(var2, this.bufferSource);
         this.itemFeatureRenderer.render(var2, this.bufferSource, this.outlineBufferSource);
         this.blockFeatureRenderer.render(var2, this.bufferSource, this.blockRenderDispatcher, this.outlineBufferSource);
         this.customFeatureRenderer.render(var2, this.bufferSource);
         this.particleFeatureRenderer.render(var2);
      }

      this.submitNodeStorage.clear();
   }

   public void endFrame() {
      this.particleFeatureRenderer.endFrame();
   }

   public SubmitNodeStorage getSubmitNodeStorage() {
      return this.submitNodeStorage;
   }

   public void close() {
      this.particleFeatureRenderer.close();
   }
}
