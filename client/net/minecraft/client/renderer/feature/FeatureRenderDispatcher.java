package net.minecraft.client.renderer.feature;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Minecraft;
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
   private final MovingBlockFeatureRenderer movingBlockFeatureRenderer = new MovingBlockFeatureRenderer();
   private final BlockModelFeatureRenderer blockModelFeatureRenderer = new BlockModelFeatureRenderer();
   private final QuadParticleFeatureRenderer particleFeatureRenderer = new QuadParticleFeatureRenderer();
   private final ShapeOutlineFeatureRenderer shapeOutlineFeatureRenderer = new ShapeOutlineFeatureRenderer();
   private final GizmoFeatureRenderer gizmoFeatureRenderer = new GizmoFeatureRenderer();

   public FeatureRenderDispatcher(final SubmitNodeStorage submitNodeStorage, final ModelManager modelManager, final MultiBufferSource.BufferSource bufferSource, final AtlasManager atlasManager, final OutlineBufferSource outlineBufferSource, final Font font, final GameRenderState gameRenderState) {
      super();
      this.submitNodeStorage = submitNodeStorage;
      this.modelManager = modelManager;
      this.bufferSource = bufferSource;
      this.atlasManager = atlasManager;
      this.outlineBufferSource = outlineBufferSource;
      this.font = font;
      this.gameRenderState = gameRenderState;
   }

   private FeatureFrameContext createFrameContext() {
      Minecraft minecraft = Minecraft.getInstance();
      return new FeatureFrameContext(this.gameRenderState.optionsRenderState, this.font, this.modelManager.getBlockStateModelSet(), minecraft.getBlockColors(), minecraft.getTextureManager(), this.atlasManager, minecraft.gameRenderer.lightmap(), this.bufferSource, this.outlineBufferSource);
   }

   public void renderSolidFeatures() {
      FeatureFrameContext context = this.createFrameContext();
      ObjectIterator var2 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var2.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var2.next();
         this.modelFeatureRenderer.renderSolid(collection, context);
         this.flameFeatureRenderer.renderSolid(collection, context);
         this.leashFeatureRenderer.renderSolid(collection, context);
         this.itemFeatureRenderer.renderSolid(collection, context);
         this.movingBlockFeatureRenderer.renderSolid(collection, context);
         this.blockModelFeatureRenderer.renderSolid(collection, context);
         this.customFeatureRenderer.renderSolid(collection, context);
         this.particleFeatureRenderer.renderSolid(collection, context);
      }

   }

   public void renderTranslucentFeatures() {
      FeatureFrameContext context = this.createFrameContext();
      ObjectIterator var2 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var2.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var2.next();
         this.shadowFeatureRenderer.renderTranslucent(collection, context);
         this.modelFeatureRenderer.renderTranslucent(collection, context);
         this.nameTagFeatureRenderer.renderTranslucent(collection, context);
         this.textFeatureRenderer.renderTranslucent(collection, context);
         this.itemFeatureRenderer.renderTranslucent(collection, context);
         this.movingBlockFeatureRenderer.renderTranslucent(collection, context);
         this.blockModelFeatureRenderer.renderTranslucent(collection, context);
         this.customFeatureRenderer.renderTranslucent(collection, context);
         this.shapeOutlineFeatureRenderer.renderTranslucent(collection, context, false);
         this.gizmoFeatureRenderer.render(collection, context, false);
      }

   }

   public void renderTranslucentAfterTerrain() {
      FeatureFrameContext context = this.createFrameContext();
      ObjectIterator var2 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var2.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var2.next();
         this.particleFeatureRenderer.renderTranslucent(collection, context);
         this.shapeOutlineFeatureRenderer.renderTranslucent(collection, context, true);
      }

   }

   public void renderAlwaysOnTop() {
      FeatureFrameContext context = this.createFrameContext();
      ObjectIterator var2 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var2.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var2.next();
         this.gizmoFeatureRenderer.render(collection, context, true);
      }

   }

   public void clearSubmitNodes() {
      this.submitNodeStorage.clear();
   }

   public void renderAllFeatures() {
      this.renderSolidFeatures();
      this.renderTranslucentFeatures();
      this.renderTranslucentAfterTerrain();
      this.renderAlwaysOnTop();
      this.clearSubmitNodes();
      this.bufferSource.uploadAndDraw();
   }

   public void endFrame() {
      this.particleFeatureRenderer.endFrame();
   }

   public SubmitNodeStorage getSubmitNodeStorage() {
      return this.submitNodeStorage;
   }

   public boolean hasAnyAlwaysOnTop() {
      ObjectIterator var1 = this.submitNodeStorage.getSubmitsPerOrder().values().iterator();

      while(var1.hasNext()) {
         SubmitNodeCollection collection = (SubmitNodeCollection)var1.next();

         for(GizmoFeatureRenderer.Submit gizmos : collection.getGizmoSubmits()) {
            if (gizmos.onTop()) {
               return true;
            }
         }
      }

      return false;
   }

   public void close() {
      this.particleFeatureRenderer.close();
   }
}
