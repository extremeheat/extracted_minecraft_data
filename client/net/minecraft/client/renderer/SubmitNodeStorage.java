package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
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
import org.joml.Vector3f;

public class SubmitNodeStorage implements SubmitNodeCollector {
   private final Int2ObjectAVLTreeMap<SubmitNodeCollection> submitsPerOrder = new Int2ObjectAVLTreeMap();

   public SubmitNodeStorage() {
      super();
   }

   public SubmitNodeCollection order(int var1) {
      return (SubmitNodeCollection)this.submitsPerOrder.computeIfAbsent(var1, (var1x) -> new SubmitNodeCollection(this));
   }

   public void submitHitbox(PoseStack var1, EntityRenderState var2, HitboxesRenderState var3) {
      this.order(0).submitHitbox(var1, var2, var3);
   }

   public void submitShadow(PoseStack var1, float var2, List<EntityRenderState.ShadowPiece> var3) {
      this.order(0).submitShadow(var1, var2, var3);
   }

   public void submitNameTag(PoseStack var1, @Nullable Vec3 var2, Component var3, boolean var4, int var5, double var6, CameraRenderState var8) {
      this.order(0).submitNameTag(var1, var2, var3, var4, var5, var6, var8);
   }

   public void submitText(PoseStack var1, float var2, float var3, FormattedCharSequence var4, boolean var5, Font.DisplayMode var6, int var7, int var8, int var9, int var10) {
      this.order(0).submitText(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void submitFlame(PoseStack var1, EntityRenderState var2, Quaternionf var3) {
      this.order(0).submitFlame(var1, var2, var3);
   }

   public void submitLeash(PoseStack var1, EntityRenderState.LeashState var2) {
      this.order(0).submitLeash(var1, var2);
   }

   public <S> void submitModel(Model<? super S> var1, S var2, PoseStack var3, RenderType var4, int var5, int var6, int var7, @Nullable TextureAtlasSprite var8, int var9, @Nullable ModelFeatureRenderer.CrumblingOverlay var10) {
      this.order(0).submitModel(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void submitModelPart(ModelPart var1, PoseStack var2, RenderType var3, int var4, int var5, @Nullable TextureAtlasSprite var6, boolean var7, boolean var8, int var9, ModelFeatureRenderer.CrumblingOverlay var10) {
      this.order(0).submitModelPart(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void submitBlock(PoseStack var1, BlockState var2, int var3, int var4, int var5) {
      this.order(0).submitBlock(var1, var2, var3, var4, var5);
   }

   public void submitMovingBlock(PoseStack var1, MovingBlockRenderState var2) {
      this.order(0).submitMovingBlock(var1, var2);
   }

   public void submitBlockModel(PoseStack var1, RenderType var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8, int var9) {
      this.order(0).submitBlockModel(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void submitItem(PoseStack var1, ItemDisplayContext var2, int var3, int var4, int var5, int[] var6, List<BakedQuad> var7, RenderType var8, ItemStackRenderState.FoilType var9) {
      this.order(0).submitItem(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void submitCustomGeometry(PoseStack var1, RenderType var2, SubmitNodeCollector.CustomGeometryRenderer var3) {
      this.order(0).submitCustomGeometry(var1, var2, var3);
   }

   public void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer var1) {
      this.order(0).submitParticleGroup(var1);
   }

   public void clear() {
      this.submitsPerOrder.values().forEach(SubmitNodeCollection::clear);
   }

   public void endFrame() {
      this.submitsPerOrder.values().removeIf((var0) -> !var0.wasUsed());
      this.submitsPerOrder.values().forEach(SubmitNodeCollection::endFrame);
   }

   public Int2ObjectAVLTreeMap<SubmitNodeCollection> getSubmitsPerOrder() {
      return this.submitsPerOrder;
   }

   // $FF: synthetic method
   public OrderedSubmitNodeCollector order(final int var1) {
      return this.order(var1);
   }

   public static record ShadowSubmit(Matrix4f pose, float radius, List<EntityRenderState.ShadowPiece> pieces) {
      public ShadowSubmit(Matrix4f var1, float var2, List<EntityRenderState.ShadowPiece> var3) {
         super();
         this.pose = var1;
         this.radius = var2;
         this.pieces = var3;
      }
   }

   public static record FlameSubmit(PoseStack.Pose pose, EntityRenderState entityRenderState, Quaternionf rotation) {
      public FlameSubmit(PoseStack.Pose var1, EntityRenderState var2, Quaternionf var3) {
         super();
         this.pose = var1;
         this.entityRenderState = var2;
         this.rotation = var3;
      }
   }

   public static record NameTagSubmit(Matrix4f pose, float x, float y, Component text, int lightCoords, int color, int backgroundColor, double distanceToCameraSq) {
      public NameTagSubmit(Matrix4f var1, float var2, float var3, Component var4, int var5, int var6, int var7, double var8) {
         super();
         this.pose = var1;
         this.x = var2;
         this.y = var3;
         this.text = var4;
         this.lightCoords = var5;
         this.color = var6;
         this.backgroundColor = var7;
         this.distanceToCameraSq = var8;
      }
   }

   public static record TextSubmit(Matrix4f pose, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) {
      public TextSubmit(Matrix4f var1, float var2, float var3, FormattedCharSequence var4, boolean var5, Font.DisplayMode var6, int var7, int var8, int var9, int var10) {
         super();
         this.pose = var1;
         this.x = var2;
         this.y = var3;
         this.string = var4;
         this.dropShadow = var5;
         this.displayMode = var6;
         this.lightCoords = var7;
         this.color = var8;
         this.backgroundColor = var9;
         this.outlineColor = var10;
      }
   }

   public static record HitboxSubmit(Matrix4f pose, EntityRenderState entityRenderState, HitboxesRenderState hitboxesRenderState) {
      public HitboxSubmit(Matrix4f var1, EntityRenderState var2, HitboxesRenderState var3) {
         super();
         this.pose = var1;
         this.entityRenderState = var2;
         this.hitboxesRenderState = var3;
      }
   }

   public static record LeashSubmit(Matrix4f pose, EntityRenderState.LeashState leashState) {
      public LeashSubmit(Matrix4f var1, EntityRenderState.LeashState var2) {
         super();
         this.pose = var1;
         this.leashState = var2;
      }
   }

   public static record ModelSubmit<S>(PoseStack.Pose pose, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
      public ModelSubmit(PoseStack.Pose var1, Model<? super S> var2, S var3, int var4, int var5, int var6, @Nullable TextureAtlasSprite var7, int var8, @Nullable ModelFeatureRenderer.CrumblingOverlay var9) {
         super();
         this.pose = var1;
         this.model = var2;
         this.state = var3;
         this.lightCoords = var4;
         this.overlayCoords = var5;
         this.tintedColor = var6;
         this.sprite = var7;
         this.outlineColor = var8;
         this.crumblingOverlay = var9;
      }
   }

   public static record ModelPartSubmit(PoseStack.Pose pose, ModelPart modelPart, int lightCoords, int overlayCoords, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintedColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
      public ModelPartSubmit(PoseStack.Pose var1, ModelPart var2, int var3, int var4, @Nullable TextureAtlasSprite var5, boolean var6, boolean var7, int var8, @Nullable ModelFeatureRenderer.CrumblingOverlay var9) {
         super();
         this.pose = var1;
         this.modelPart = var2;
         this.lightCoords = var3;
         this.overlayCoords = var4;
         this.sprite = var5;
         this.sheeted = var6;
         this.hasFoil = var7;
         this.tintedColor = var8;
         this.crumblingOverlay = var9;
      }
   }

   public static record TranslucentModelSubmit<S>(ModelSubmit<S> modelSubmit, RenderType renderType, Vector3f position) {
      public TranslucentModelSubmit(ModelSubmit<S> var1, RenderType var2, Vector3f var3) {
         super();
         this.modelSubmit = var1;
         this.renderType = var2;
         this.position = var3;
      }
   }

   public static record BlockSubmit(PoseStack.Pose pose, BlockState state, int lightCoords, int overlayCoords, int outlineColor) {
      public BlockSubmit(PoseStack.Pose var1, BlockState var2, int var3, int var4, int var5) {
         super();
         this.pose = var1;
         this.state = var2;
         this.lightCoords = var3;
         this.overlayCoords = var4;
         this.outlineColor = var5;
      }
   }

   public static record MovingBlockSubmit(Matrix4f pose, MovingBlockRenderState movingBlockRenderState) {
      public MovingBlockSubmit(Matrix4f var1, MovingBlockRenderState var2) {
         super();
         this.pose = var1;
         this.movingBlockRenderState = var2;
      }
   }

   public static record BlockModelSubmit(PoseStack.Pose pose, RenderType renderType, BlockStateModel model, float r, float g, float b, int lightCoords, int overlayCoords, int outlineColor) {
      public BlockModelSubmit(PoseStack.Pose var1, RenderType var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8, int var9) {
         super();
         this.pose = var1;
         this.renderType = var2;
         this.model = var3;
         this.r = var4;
         this.g = var5;
         this.b = var6;
         this.lightCoords = var7;
         this.overlayCoords = var8;
         this.outlineColor = var9;
      }
   }

   public static record ItemSubmit(PoseStack.Pose pose, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, RenderType renderType, ItemStackRenderState.FoilType foilType) {
      public ItemSubmit(PoseStack.Pose var1, ItemDisplayContext var2, int var3, int var4, int var5, int[] var6, List<BakedQuad> var7, RenderType var8, ItemStackRenderState.FoilType var9) {
         super();
         this.pose = var1;
         this.displayContext = var2;
         this.lightCoords = var3;
         this.overlayCoords = var4;
         this.outlineColor = var5;
         this.tintLayers = var6;
         this.quads = var7;
         this.renderType = var8;
         this.foilType = var9;
      }
   }

   public static record CustomGeometrySubmit(PoseStack.Pose pose, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      public CustomGeometrySubmit(PoseStack.Pose var1, SubmitNodeCollector.CustomGeometryRenderer var2) {
         super();
         this.pose = var1;
         this.customGeometryRenderer = var2;
      }
   }
}
