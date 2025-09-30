package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelPartFeatureRenderer;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class SubmitNodeCollection implements OrderedSubmitNodeCollector {
   private final List<SubmitNodeStorage.ShadowSubmit> shadowSubmits = new ArrayList();
   private final List<SubmitNodeStorage.FlameSubmit> flameSubmits = new ArrayList();
   private final NameTagFeatureRenderer.Storage nameTagSubmits = new NameTagFeatureRenderer.Storage();
   private final List<SubmitNodeStorage.TextSubmit> textSubmits = new ArrayList();
   private final List<SubmitNodeStorage.HitboxSubmit> hitboxSubmits = new ArrayList();
   private final List<SubmitNodeStorage.LeashSubmit> leashSubmits = new ArrayList();
   private final List<SubmitNodeStorage.BlockSubmit> blockSubmits = new ArrayList();
   private final List<SubmitNodeStorage.MovingBlockSubmit> movingBlockSubmits = new ArrayList();
   private final List<SubmitNodeStorage.BlockModelSubmit> blockModelSubmits = new ArrayList();
   private final List<SubmitNodeStorage.ItemSubmit> itemSubmits = new ArrayList();
   private final List<SubmitNodeCollector.ParticleGroupRenderer> particleGroupRenderers = new ArrayList();
   private final ModelFeatureRenderer.Storage modelSubmits = new ModelFeatureRenderer.Storage();
   private final ModelPartFeatureRenderer.Storage modelPartSubmits = new ModelPartFeatureRenderer.Storage();
   private final CustomFeatureRenderer.Storage customGeometrySubmits = new CustomFeatureRenderer.Storage();
   private final SubmitNodeStorage submitNodeStorage;
   private boolean wasUsed = false;

   public SubmitNodeCollection(SubmitNodeStorage var1) {
      super();
      this.submitNodeStorage = var1;
   }

   public void submitHitbox(PoseStack var1, EntityRenderState var2, HitboxesRenderState var3) {
      this.wasUsed = true;
      this.hitboxSubmits.add(new SubmitNodeStorage.HitboxSubmit(new Matrix4f(var1.last().pose()), var2, var3));
   }

   public void submitShadow(PoseStack var1, float var2, List<EntityRenderState.ShadowPiece> var3) {
      this.wasUsed = true;
      PoseStack.Pose var4 = var1.last();
      this.shadowSubmits.add(new SubmitNodeStorage.ShadowSubmit(new Matrix4f(var4.pose()), var2, var3));
   }

   public void submitNameTag(PoseStack var1, @Nullable Vec3 var2, int var3, Component var4, boolean var5, int var6, double var7, CameraRenderState var9) {
      this.wasUsed = true;
      this.nameTagSubmits.add(var1, var2, var3, var4, var5, var6, var7, var9);
   }

   public void submitText(PoseStack var1, float var2, float var3, FormattedCharSequence var4, boolean var5, Font.DisplayMode var6, int var7, int var8, int var9, int var10) {
      this.wasUsed = true;
      this.textSubmits.add(new SubmitNodeStorage.TextSubmit(new Matrix4f(var1.last().pose()), var2, var3, var4, var5, var6, var7, var8, var9, var10));
   }

   public void submitFlame(PoseStack var1, EntityRenderState var2, Quaternionf var3) {
      this.wasUsed = true;
      this.flameSubmits.add(new SubmitNodeStorage.FlameSubmit(var1.last().copy(), var2, var3));
   }

   public void submitLeash(PoseStack var1, EntityRenderState.LeashState var2) {
      this.wasUsed = true;
      this.leashSubmits.add(new SubmitNodeStorage.LeashSubmit(new Matrix4f(var1.last().pose()), var2));
   }

   public <S> void submitModel(Model<? super S> var1, S var2, PoseStack var3, RenderType var4, int var5, int var6, int var7, @Nullable TextureAtlasSprite var8, int var9, @Nullable ModelFeatureRenderer.CrumblingOverlay var10) {
      this.wasUsed = true;
      SubmitNodeStorage.ModelSubmit var11 = new SubmitNodeStorage.ModelSubmit(var3.last().copy(), var1, var2, var5, var6, var7, var8, var9, var10);
      this.modelSubmits.add(var4, var11);
   }

   public void submitModelPart(ModelPart var1, PoseStack var2, RenderType var3, int var4, int var5, @Nullable TextureAtlasSprite var6, boolean var7, boolean var8, int var9, @Nullable ModelFeatureRenderer.CrumblingOverlay var10, int var11) {
      this.wasUsed = true;
      this.modelPartSubmits.add(var3, new SubmitNodeStorage.ModelPartSubmit(var2.last().copy(), var1, var4, var5, var6, var7, var8, var9, var10, var11));
   }

   public void submitBlock(PoseStack var1, BlockState var2, int var3, int var4, int var5) {
      this.wasUsed = true;
      this.blockSubmits.add(new SubmitNodeStorage.BlockSubmit(var1.last().copy(), var2, var3, var4, var5));
      ((SpecialBlockModelRenderer)Minecraft.getInstance().getModelManager().specialBlockModelRenderer().get()).renderByBlock(var2.getBlock(), ItemDisplayContext.NONE, var1, this.submitNodeStorage, var3, var4, var5);
   }

   public void submitMovingBlock(PoseStack var1, MovingBlockRenderState var2) {
      this.wasUsed = true;
      this.movingBlockSubmits.add(new SubmitNodeStorage.MovingBlockSubmit(new Matrix4f(var1.last().pose()), var2));
   }

   public void submitBlockModel(PoseStack var1, RenderType var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8, int var9) {
      this.wasUsed = true;
      this.blockModelSubmits.add(new SubmitNodeStorage.BlockModelSubmit(var1.last().copy(), var2, var3, var4, var5, var6, var7, var8, var9));
   }

   public void submitItem(PoseStack var1, ItemDisplayContext var2, int var3, int var4, int var5, int[] var6, List<BakedQuad> var7, RenderType var8, ItemStackRenderState.FoilType var9) {
      this.wasUsed = true;
      this.itemSubmits.add(new SubmitNodeStorage.ItemSubmit(var1.last().copy(), var2, var3, var4, var5, var6, var7, var8, var9));
   }

   public void submitCustomGeometry(PoseStack var1, RenderType var2, SubmitNodeCollector.CustomGeometryRenderer var3) {
      this.wasUsed = true;
      this.customGeometrySubmits.add(var1, var2, var3);
   }

   public void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer var1) {
      this.wasUsed = true;
      this.particleGroupRenderers.add(var1);
   }

   public List<SubmitNodeStorage.ShadowSubmit> getShadowSubmits() {
      return this.shadowSubmits;
   }

   public List<SubmitNodeStorage.FlameSubmit> getFlameSubmits() {
      return this.flameSubmits;
   }

   public NameTagFeatureRenderer.Storage getNameTagSubmits() {
      return this.nameTagSubmits;
   }

   public List<SubmitNodeStorage.TextSubmit> getTextSubmits() {
      return this.textSubmits;
   }

   public List<SubmitNodeStorage.HitboxSubmit> getHitboxSubmits() {
      return this.hitboxSubmits;
   }

   public List<SubmitNodeStorage.LeashSubmit> getLeashSubmits() {
      return this.leashSubmits;
   }

   public List<SubmitNodeStorage.BlockSubmit> getBlockSubmits() {
      return this.blockSubmits;
   }

   public List<SubmitNodeStorage.MovingBlockSubmit> getMovingBlockSubmits() {
      return this.movingBlockSubmits;
   }

   public List<SubmitNodeStorage.BlockModelSubmit> getBlockModelSubmits() {
      return this.blockModelSubmits;
   }

   public ModelPartFeatureRenderer.Storage getModelPartSubmits() {
      return this.modelPartSubmits;
   }

   public List<SubmitNodeStorage.ItemSubmit> getItemSubmits() {
      return this.itemSubmits;
   }

   public List<SubmitNodeCollector.ParticleGroupRenderer> getParticleGroupRenderers() {
      return this.particleGroupRenderers;
   }

   public ModelFeatureRenderer.Storage getModelSubmits() {
      return this.modelSubmits;
   }

   public CustomFeatureRenderer.Storage getCustomGeometrySubmits() {
      return this.customGeometrySubmits;
   }

   public boolean wasUsed() {
      return this.wasUsed;
   }

   public void clear() {
      this.shadowSubmits.clear();
      this.flameSubmits.clear();
      this.nameTagSubmits.clear();
      this.textSubmits.clear();
      this.hitboxSubmits.clear();
      this.leashSubmits.clear();
      this.blockSubmits.clear();
      this.movingBlockSubmits.clear();
      this.blockModelSubmits.clear();
      this.itemSubmits.clear();
      this.particleGroupRenderers.clear();
      this.modelSubmits.clear();
      this.customGeometrySubmits.clear();
      this.modelPartSubmits.clear();
   }

   public void endFrame() {
      this.modelSubmits.endFrame();
      this.modelPartSubmits.endFrame();
      this.customGeometrySubmits.endFrame();
      this.wasUsed = false;
   }
}
