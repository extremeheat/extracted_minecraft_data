package net.minecraft.client.renderer.feature;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;

public class FeatureRenderDispatcher implements AutoCloseable {
   private final SubmitNodeStorage submitNodeStorage;
   private final ModelManager modelManager;
   private final MultiBufferSource.BufferSource bufferSource;
   private final AtlasManager atlasManager;
   private final OutlineBufferSource outlineBufferSource;
   private final MultiBufferSource.BufferSource crumblingBufferSource;
   private final Font font;
   private final GameRenderState gameRenderState;
   private final ShadowFeatureRenderer shadowFeatureRenderer = new ShadowFeatureRenderer();
   private final FlameFeatureRenderer flameFeatureRenderer = new FlameFeatureRenderer();
   private final ModelFeatureRenderer modelFeatureRenderer = new ModelFeatureRenderer();
   private final NameTagFeatureRenderer nameTagFeatureRenderer = new NameTagFeatureRenderer();
   private final TextFeatureRenderer textFeatureRenderer = new TextFeatureRenderer();
   private final LeashFeatureRenderer leashFeatureRenderer = new LeashFeatureRenderer();
   private final ItemFeatureRenderer itemFeatureRenderer = new ItemFeatureRenderer();
   private final CustomFeatureRenderer customFeatureRenderer = new CustomFeatureRenderer();
   private final BlockFeatureRenderer blockFeatureRenderer = new BlockFeatureRenderer();
   private final ParticleFeatureRenderer particleFeatureRenderer = new ParticleFeatureRenderer();
   private final ShapeOutlineFeatureRenderer shapeOutlineFeatureRenderer = new ShapeOutlineFeatureRenderer();

   public FeatureRenderDispatcher(final SubmitNodeStorage submitNodeStorage, final ModelManager modelManager, final MultiBufferSource.BufferSource bufferSource, final AtlasManager atlasManager, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource, final Font font, final GameRenderState gameRenderState) {
      super();
      this.submitNodeStorage = submitNodeStorage;
      this.modelManager = modelManager;
      this.bufferSource = bufferSource;
      this.atlasManager = atlasManager;
      this.outlineBufferSource = outlineBufferSource;
      this.crumblingBufferSource = crumblingBufferSource;
      this.font = font;
      this.gameRenderState = gameRenderState;
   }

   public void renderSolidFeatures() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var1.next();
         this.modelFeatureRenderer.renderSolid(collection, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.flameFeatureRenderer.renderSolid(collection, this.bufferSource, this.atlasManager);
         this.leashFeatureRenderer.renderSolid(collection, this.bufferSource);
         this.itemFeatureRenderer.renderSolid(collection, this.bufferSource, this.outlineBufferSource);
         this.blockFeatureRenderer.renderSolid(collection, this.bufferSource, this.modelManager.getBlockStateModelSet(), this.outlineBufferSource, this.gameRenderState.optionsRenderState);
         this.customFeatureRenderer.renderSolid(collection, this.bufferSource, this.outlineBufferSource);
         this.particleFeatureRenderer.renderSolid(collection);
      }

   }

   public void renderTranslucentFeatures() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var1.next();
         this.shadowFeatureRenderer.renderTranslucent(collection, this.bufferSource);
         this.modelFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.outlineBufferSource, this.crumblingBufferSource);
         this.nameTagFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.font);
         this.textFeatureRenderer.renderTranslucent(collection, this.bufferSource);
         this.itemFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.outlineBufferSource);
         this.blockFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.modelManager.getBlockStateModelSet(), this.outlineBufferSource, this.crumblingBufferSource, this.gameRenderState.optionsRenderState);
         this.customFeatureRenderer.renderTranslucent(collection, this.bufferSource, this.outlineBufferSource);
         this.shapeOutlineFeatureRenderer.renderTranslucent(collection, this.bufferSource, false);
      }

   }

   public void renderTranslucentAfterTerrain() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var1.next();
         this.particleFeatureRenderer.renderTranslucent(collection);
         this.shapeOutlineFeatureRenderer.renderTranslucent(collection, this.bufferSource, true);
      }

   }

   public void clearSubmitNodes() {
      this.submitNodeStorage.clear();
   }

   public void renderAllFeatures() {
      this.renderSolidFeatures();
      this.renderTranslucentFeatures();
      this.renderTranslucentAfterTerrain();
      this.clearSubmitNodes();
      this.bufferSource.uploadAndDraw();
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
