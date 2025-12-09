package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Quaternionfc;

public class CustomHeadLayer<S extends LivingEntityRenderState, M extends EntityModel<S> & HeadedModel> extends RenderLayer<S, M> {
   private static final float ITEM_SCALE = 0.625F;
   private static final float SKULL_SCALE = 1.1875F;
   private final Transforms transforms;
   private final Function<SkullBlock.Type, SkullModelBase> skullModels;
   private final PlayerSkinRenderCache playerSkinRenderCache;

   public CustomHeadLayer(RenderLayerParent<S, M> var1, EntityModelSet var2, PlayerSkinRenderCache var3) {
      this(var1, var2, var3, CustomHeadLayer.Transforms.DEFAULT);
   }

   public CustomHeadLayer(RenderLayerParent<S, M> var1, EntityModelSet var2, PlayerSkinRenderCache var3, Transforms var4) {
      super(var1);
      this.transforms = var4;
      this.skullModels = Util.memoize((Function)((var1x) -> SkullBlockRenderer.createModel(var2, var1x)));
      this.playerSkinRenderCache = var3;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      if (!var4.headItem.isEmpty() || var4.wornHeadType != null) {
         var1.pushPose();
         var1.scale(this.transforms.horizontalScale(), 1.0F, this.transforms.horizontalScale());
         EntityModel var7 = this.getParentModel();
         var7.root().translateAndRotate(var1);
         ((HeadedModel)var7).translateToHead(var1);
         if (var4.wornHeadType != null) {
            var1.translate(0.0F, this.transforms.skullYOffset(), 0.0F);
            var1.scale(1.1875F, -1.1875F, -1.1875F);
            var1.translate(-0.5, 0.0, -0.5);
            SkullBlock.Type var8 = var4.wornHeadType;
            SkullModelBase var9 = (SkullModelBase)this.skullModels.apply(var8);
            RenderType var10 = this.resolveSkullRenderType(var4, var8);
            SkullBlockRenderer.submitSkull((Direction)null, 180.0F, var4.wornHeadAnimationPos, var1, var2, var3, var9, var10, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
         } else {
            translateToHead(var1, this.transforms);
            var4.headItem.submit(var1, var2, var3, OverlayTexture.NO_OVERLAY, var4.outlineColor);
         }

         var1.popPose();
      }
   }

   private RenderType resolveSkullRenderType(LivingEntityRenderState var1, SkullBlock.Type var2) {
      if (var2 == SkullBlock.Types.PLAYER) {
         ResolvableProfile var3 = var1.wornHeadProfile;
         if (var3 != null) {
            return this.playerSkinRenderCache.getOrDefault(var3).renderType();
         }
      }

      return SkullBlockRenderer.getSkullRenderType(var2, (Identifier)null);
   }

   public static void translateToHead(PoseStack var0, Transforms var1) {
      var0.translate(0.0F, -0.25F + var1.yOffset(), 0.0F);
      var0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      var0.scale(0.625F, -0.625F, -0.625F);
   }

   public static record Transforms(float yOffset, float skullYOffset, float horizontalScale) {
      public static final Transforms DEFAULT = new Transforms(0.0F, 0.0F, 1.0F);

      public Transforms(float var1, float var2, float var3) {
         super();
         this.yOffset = var1;
         this.skullYOffset = var2;
         this.horizontalScale = var3;
      }
   }
}
