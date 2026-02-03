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

   public FeatureRenderDispatcher(final SubmitNodeStorage submitNodeStorage, final BlockRenderDispatcher blockRenderDispatcher, final MultiBufferSource.BufferSource bufferSource, final AtlasManager atlasManager, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource, final Font font) {
      super();
      this.submitNodeStorage = submitNodeStorage;
      this.blockRenderDispatcher = blockRenderDispatcher;
      this.bufferSource = bufferSource;
      this.atlasManager = atlasManager;
      this.outlineBufferSource = outlineBufferSource;
      this.crumblingBufferSource = crumblingBufferSource;
      this.font = font;
   }

   public void renderSolidFeatures() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var1.next();
         this.modelFeatureRenderer.renderSolid(collection, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.modelPartFeatureRenderer.renderSolid(collection, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.flameFeatureRenderer.renderSolid(collection, this.bufferSource, this.atlasManager);
         this.leashFeatureRenderer.renderSolid(collection, this.bufferSource);
         this.itemFeatureRenderer.renderSolid(collection, this.bufferSource, this.outlineBufferSource);
         this.blockFeatureRenderer.renderSolid(collection, this.bufferSource, this.blockRenderDispatcher, this.outlineBufferSource);
         this.customFeatureRenderer.renderSolid(collection, this.bufferSource);
         this.particleFeatureRenderer.renderSolid(collection);
      }

   }

   public void renderTranslucentFeatures() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var1.next();
         this.shadowFeatureRenderer.renderTranslucent(collection, this.bufferSource);
         this.modelFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.modelPartFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.nameTagFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.font);
         this.textFeatureRenderer.renderTranslucent(collection, this.bufferSource);
         this.itemFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.outlineBufferSource);
         this.blockFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.blockRenderDispatcher, this.outlineBufferSource);
         this.customFeatureRenderer.renderTranslucent(collection, this.bufferSource);
      }

   }

   public void renderTranslucentParticles() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var1.next();
         this.particleFeatureRenderer.renderTranslucent(collection);
      }

   }

   public void clearSubmitNodes() {
      this.submitNodeStorage.clear();
   }

   public void renderAllFeatures() {
      this.renderSolidFeatures();
      this.renderTranslucentFeatures();
      this.renderTranslucentParticles();
      this.clearSubmitNodes();
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
