package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;

public class SkullBlockRenderer implements BlockEntityRenderer<SkullBlockEntity> {
   private final Function<SkullBlock.Type, SkullModelBase> modelByType;
   private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put(SkullBlock.Types.SKELETON, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png"));
      var0.put(SkullBlock.Types.WITHER_SKELETON, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png"));
      var0.put(SkullBlock.Types.ZOMBIE, ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png"));
      var0.put(SkullBlock.Types.CREEPER, ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png"));
      var0.put(SkullBlock.Types.DRAGON, ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png"));
      var0.put(SkullBlock.Types.PIGLIN, ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin.png"));
      var0.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultTexture());
   });
   private final PlayerSkinRenderCache playerSkinRenderCache;

   @Nullable
   public static SkullModelBase createModel(EntityModelSet var0, SkullBlock.Type var1) {
      if (var1 instanceof SkullBlock.Types) {
         SkullBlock.Types var2 = (SkullBlock.Types)var1;
         Object var10000;
         switch (var2) {
            case SKELETON -> var10000 = new SkullModel(var0.bakeLayer(ModelLayers.SKELETON_SKULL));
            case WITHER_SKELETON -> var10000 = new SkullModel(var0.bakeLayer(ModelLayers.WITHER_SKELETON_SKULL));
            case PLAYER -> var10000 = new SkullModel(var0.bakeLayer(ModelLayers.PLAYER_HEAD));
            case ZOMBIE -> var10000 = new SkullModel(var0.bakeLayer(ModelLayers.ZOMBIE_HEAD));
            case CREEPER -> var10000 = new SkullModel(var0.bakeLayer(ModelLayers.CREEPER_HEAD));
            case DRAGON -> var10000 = new DragonHeadModel(var0.bakeLayer(ModelLayers.DRAGON_SKULL));
            case PIGLIN -> var10000 = new PiglinHeadModel(var0.bakeLayer(ModelLayers.PIGLIN_HEAD));
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return (SkullModelBase)var10000;
      } else {
         return null;
      }
   }

   public SkullBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      EntityModelSet var2 = var1.entityModelSet();
      this.playerSkinRenderCache = var1.playerSkinRenderCache();
      this.modelByType = Util.memoize((Function)((var1x) -> createModel(var2, var1x)));
   }

   public void submit(SkullBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      float var9 = var1.getAnimation(var2);
      BlockState var10 = var1.getBlockState();
      boolean var11 = var10.getBlock() instanceof WallSkullBlock;
      Direction var12 = var11 ? (Direction)var10.getValue(WallSkullBlock.FACING) : null;
      int var13 = var11 ? RotationSegment.convertToSegment(var12.getOpposite()) : (Integer)var10.getValue(SkullBlock.ROTATION);
      float var14 = RotationSegment.convertToDegrees(var13);
      SkullBlock.Type var15 = ((AbstractSkullBlock)var10.getBlock()).getType();
      SkullModelBase var16 = (SkullModelBase)this.modelByType.apply(var15);
      RenderType var17 = this.resolveSkullRenderType(var15, var1);
      submitSkull(var12, var14, var9, var3, var8, var4, var16, var17, 0, var7);
   }

   public static void submitSkull(@Nullable Direction var0, float var1, float var2, PoseStack var3, SubmitNodeCollector var4, int var5, SkullModelBase var6, RenderType var7, int var8, @Nullable ModelFeatureRenderer.CrumblingOverlay var9) {
      var3.pushPose();
      if (var0 == null) {
         var3.translate(0.5F, 0.0F, 0.5F);
      } else {
         float var10 = 0.25F;
         var3.translate(0.5F - (float)var0.getStepX() * 0.25F, 0.25F, 0.5F - (float)var0.getStepZ() * 0.25F);
      }

      var3.scale(-1.0F, -1.0F, 1.0F);
      SkullModelBase.State var11 = new SkullModelBase.State();
      var11.animationPos = var2;
      var11.yRot = var1;
      var4.submitModel(var6, var11, var3, var7, var5, OverlayTexture.NO_OVERLAY, var8, var9);
      var3.popPose();
   }

   private RenderType resolveSkullRenderType(SkullBlock.Type var1, SkullBlockEntity var2) {
      if (var1 == SkullBlock.Types.PLAYER) {
         ResolvableProfile var3 = var2.getOwnerProfile();
         if (var3 != null) {
            return this.playerSkinRenderCache.getOrDefault(var3).renderType();
         }
      }

      return getSkullRenderType(var1, (ResourceLocation)null);
   }

   public static RenderType getSkullRenderType(SkullBlock.Type var0, @Nullable ResourceLocation var1) {
      return RenderType.entityCutoutNoCullZOffset(var1 != null ? var1 : (ResourceLocation)SKIN_BY_TYPE.get(var0));
   }

   public static RenderType getPlayerSkinRenderType(ResourceLocation var0) {
      return RenderType.entityTranslucent(var0);
   }
}
