package net.minecraft.client.renderer.feature;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.AtlasManager;

public class FeatureRenderDispatcher {
   private final SubmitNodeStorage submitNodeStorage;
   private final BlockRenderDispatcher blockRenderDispatcher;
   private final MultiBufferSource.BufferSource bufferSource;
   private final AtlasManager atlasManager;
   private final OutlineBufferSource outlineBufferSource;
   private final Font font;
   private final ShadowFeatureRenderer shadowFeatureRenderer = new ShadowFeatureRenderer();
   private final FlameFeatureRenderer flameFeatureRenderer = new FlameFeatureRenderer();
   private final EntityModelFeatureRenderer entityModelFeatureRenderer = new EntityModelFeatureRenderer();
   private final NameTagFeatureRenderer nameTagFeatureRenderer = new NameTagFeatureRenderer();
   private final TextFeatureRenderer textFeatureRenderer = new TextFeatureRenderer();
   private final HitboxFeatureRenderer hitboxFeatureRenderer = new HitboxFeatureRenderer();
   private final LeashFeatureRenderer leashFeatureRenderer = new LeashFeatureRenderer();
   private final ItemFeatureRenderer itemFeatureRenderer = new ItemFeatureRenderer();
   private final CustomFeatureRenderer customFeatureRenderer = new CustomFeatureRenderer();
   private final BlockFeatureRenderer blockFeatureRenderer = new BlockFeatureRenderer();

   public FeatureRenderDispatcher(SubmitNodeStorage var1, BlockRenderDispatcher var2, MultiBufferSource.BufferSource var3, AtlasManager var4, OutlineBufferSource var5, Font var6) {
      super();
      this.submitNodeStorage = var1;
      this.blockRenderDispatcher = var2;
      this.bufferSource = var3;
      this.atlasManager = var4;
      this.outlineBufferSource = var5;
      this.font = var6;
   }

   public void renderAllFeatures() {
      this.shadowFeatureRenderer.render(this.submitNodeStorage, this.bufferSource);
      this.entityModelFeatureRenderer.render(this.submitNodeStorage, this.bufferSource, this.outlineBufferSource);
      this.flameFeatureRenderer.render(this.submitNodeStorage, this.bufferSource, this.atlasManager);
      this.nameTagFeatureRenderer.render(this.submitNodeStorage, this.bufferSource, this.font);
      this.textFeatureRenderer.render(this.submitNodeStorage, this.bufferSource);
      this.hitboxFeatureRenderer.render(this.submitNodeStorage, this.bufferSource);
      this.leashFeatureRenderer.render(this.submitNodeStorage, this.bufferSource);
      this.itemFeatureRenderer.render(this.submitNodeStorage, this.bufferSource);
      this.blockFeatureRenderer.render(this.submitNodeStorage, this.bufferSource, this.blockRenderDispatcher);
      this.customFeatureRenderer.render(this.submitNodeStorage, this.bufferSource);
      this.submitNodeStorage.clear();
   }

   public SubmitNodeStorage getSubmitNodeStorage() {
      return this.submitNodeStorage;
   }
}
