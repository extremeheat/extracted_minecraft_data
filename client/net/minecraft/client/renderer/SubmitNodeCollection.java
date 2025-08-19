package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class SubmitNodeCollection implements OrderedSubmitNodeCollector {
   private final List<SubmitNodeStorage.ShadowSubmit> shadowSubmits = new ArrayList();
   private final List<SubmitNodeStorage.FlameSubmit> flameSubmits = new ArrayList();
   private final List<SubmitNodeStorage.NameTagSubmit> nameTagSubmitsSeethrough = new ArrayList();
   private final List<SubmitNodeStorage.NameTagSubmit> nameTagSubmitsNormal = new ArrayList();
   private final List<SubmitNodeStorage.TextSubmit> textSubmits = new ArrayList();
   private final List<SubmitNodeStorage.HitboxSubmit> hitboxSubmits = new ArrayList();
   private final List<SubmitNodeStorage.LeashSubmit> leashSubmits = new ArrayList();
   private final List<SubmitNodeStorage.BlockSubmit> blockSubmits = new ArrayList();
   private final List<SubmitNodeStorage.MovingBlockSubmit> movingBlockSubmits = new ArrayList();
   private final List<SubmitNodeStorage.BlockModelSubmit> blockModelSubmits = new ArrayList();
   private final List<SubmitNodeStorage.ItemSubmit> itemSubmits = new ArrayList();
   private final Map<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>> opaqueModelSubmits = new HashMap();
   private final List<SubmitNodeStorage.TranslucentModelSubmit<?>> translucentModelSubmits = new ArrayList();
   private final Set<RenderType> usedModelSubmitBuckets = new ObjectOpenHashSet();
   private final Map<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> modelPartSubmits = new HashMap();
   private final Set<RenderType> modelPartSubmitsUsage = new ObjectOpenHashSet();
   private final Map<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> customGeometrySubmits = new HashMap();
   private final Set<RenderType> customGeometrySubmitsUsage = new ObjectOpenHashSet();
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

   public void submitNameTag(PoseStack var1, @Nullable Vec3 var2, Component var3, boolean var4, int var5, double var6) {
      this.wasUsed = true;
      if (var2 != null) {
         int var8 = "deadmau5".equals(var3.getString()) ? -10 : 0;
         Minecraft var9 = Minecraft.getInstance();
         var1.pushPose();
         var1.translate(var2.x, var2.y + 0.5, var2.z);
         var1.mulPose((Quaternionfc)var9.getEntityRenderDispatcher().cameraOrientation());
         var1.scale(0.025F, -0.025F, 0.025F);
         Matrix4f var10 = new Matrix4f(var1.last().pose());
         float var11 = (float)(-var9.font.width((FormattedText)var3)) / 2.0F;
         int var12 = (int)(var9.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
         if (var4) {
            this.nameTagSubmitsNormal.add(new SubmitNodeStorage.NameTagSubmit(var10, var11, (float)var8, var3, LightTexture.lightCoordsWithEmission(var5, 2), -1, 0, var6));
            this.nameTagSubmitsSeethrough.add(new SubmitNodeStorage.NameTagSubmit(var10, var11, (float)var8, var3, var5, -2130706433, var12, var6));
         } else {
            this.nameTagSubmitsNormal.add(new SubmitNodeStorage.NameTagSubmit(var10, var11, (float)var8, var3, var5, -2130706433, var12, var6));
         }

         var1.popPose();
      }
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
      if (var4.pipeline().getBlendFunction().isEmpty()) {
         ((List)this.opaqueModelSubmits.computeIfAbsent(var4, (var0) -> new ArrayList())).add(var11);
      } else {
         Vector3f var12 = var3.last().pose().transformPosition(new Vector3f());
         this.translucentModelSubmits.add(new SubmitNodeStorage.TranslucentModelSubmit(var11, var4, var12));
      }

   }

   public void submitModelPart(ModelPart var1, PoseStack var2, RenderType var3, int var4, int var5, @Nullable TextureAtlasSprite var6, boolean var7, boolean var8, int var9) {
      this.wasUsed = true;
      ((List)this.modelPartSubmits.computeIfAbsent(var3, (var0) -> new ArrayList())).add(new SubmitNodeStorage.ModelPartSubmit(var2.last().copy(), var1, var4, var5, var6, var7, var8, var9));
   }

   public void submitBlock(PoseStack var1, BlockState var2, int var3, int var4, int var5) {
      this.wasUsed = true;
      this.blockSubmits.add(new SubmitNodeStorage.BlockSubmit(var1.last().copy(), var2, var3, var4, var5));
      ((SpecialBlockModelRenderer)Minecraft.getInstance().getModelManager().specialBlockModelRenderer().get()).renderByBlock(var2.getBlock(), ItemDisplayContext.NONE, var1, this.submitNodeStorage, var3, var4);
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
      List var4 = (List)this.customGeometrySubmits.computeIfAbsent(var2, (var0) -> new ArrayList());
      var4.add(new SubmitNodeStorage.CustomGeometrySubmit(var1.last().copy(), var3));
   }

   public List<SubmitNodeStorage.ShadowSubmit> getShadowSubmits() {
      return this.shadowSubmits;
   }

   public List<SubmitNodeStorage.FlameSubmit> getFlameSubmits() {
      return this.flameSubmits;
   }

   public List<SubmitNodeStorage.NameTagSubmit> getNameTagSubmitsSeethrough() {
      return this.nameTagSubmitsSeethrough;
   }

   public List<SubmitNodeStorage.NameTagSubmit> getNameTagSubmitsNormal() {
      return this.nameTagSubmitsNormal;
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

   public Map<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> getModelPartSubmits() {
      return this.modelPartSubmits;
   }

   public List<SubmitNodeStorage.ItemSubmit> getItemSubmits() {
      return this.itemSubmits;
   }

   public Map<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>> getOpaqueModelSubmits() {
      return this.opaqueModelSubmits;
   }

   public List<SubmitNodeStorage.TranslucentModelSubmit<?>> getTranslucentModelSubmits() {
      return this.translucentModelSubmits;
   }

   public Map<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> getCustomGeometrySubmits() {
      return this.customGeometrySubmits;
   }

   public boolean wasUsed() {
      return this.wasUsed;
   }

   public void clear() {
      this.shadowSubmits.clear();
      this.flameSubmits.clear();
      this.nameTagSubmitsNormal.clear();
      this.nameTagSubmitsSeethrough.clear();
      this.textSubmits.clear();
      this.hitboxSubmits.clear();
      this.leashSubmits.clear();
      this.blockSubmits.clear();
      this.movingBlockSubmits.clear();
      this.blockModelSubmits.clear();
      this.itemSubmits.clear();
      this.translucentModelSubmits.clear();

      for(Map.Entry var2 : this.opaqueModelSubmits.entrySet()) {
         List var3 = (List)var2.getValue();
         if (!var3.isEmpty()) {
            this.usedModelSubmitBuckets.add((RenderType)var2.getKey());
            var3.clear();
         }
      }

      for(Map.Entry var6 : this.customGeometrySubmits.entrySet()) {
         if (!((List)var6.getValue()).isEmpty()) {
            this.customGeometrySubmitsUsage.add((RenderType)var6.getKey());
            ((List)var6.getValue()).clear();
         }
      }

      for(Map.Entry var7 : this.modelPartSubmits.entrySet()) {
         if (!((List)var7.getValue()).isEmpty()) {
            this.modelPartSubmitsUsage.add((RenderType)var7.getKey());
            ((List)var7.getValue()).clear();
         }
      }

   }

   public void endFrame() {
      this.opaqueModelSubmits.keySet().removeIf((var1) -> !this.usedModelSubmitBuckets.contains(var1));
      this.usedModelSubmitBuckets.clear();
      this.modelPartSubmits.keySet().removeIf((var1) -> !this.modelPartSubmitsUsage.contains(var1));
      this.modelPartSubmitsUsage.clear();
      this.customGeometrySubmits.keySet().removeIf((var1) -> !this.customGeometrySubmitsUsage.contains(var1));
      this.customGeometrySubmitsUsage.clear();
      this.wasUsed = false;
   }
}
