package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
      EntityModelSet var2 = var1.getModelSet();
      this.modelByType = Util.memoize((Function)((var1x) -> createModel(var2, var1x)));
   }

   public void render(SkullBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      float var7 = var1.getAnimation(var2);
      BlockState var8 = var1.getBlockState();
      boolean var9 = var8.getBlock() instanceof WallSkullBlock;
      Direction var10 = var9 ? (Direction)var8.getValue(WallSkullBlock.FACING) : null;
      int var11 = var9 ? RotationSegment.convertToSegment(var10.getOpposite()) : (Integer)var8.getValue(SkullBlock.ROTATION);
      float var12 = RotationSegment.convertToDegrees(var11);
      SkullBlock.Type var13 = ((AbstractSkullBlock)var8.getBlock()).getType();
      SkullModelBase var14 = (SkullModelBase)this.modelByType.apply(var13);
      RenderType var15 = getRenderType(var13, var1.getOwnerProfile());
      renderSkull(var10, var12, var7, var3, var4, var5, var14, var15);
   }

   public static void renderSkull(@Nullable Direction var0, float var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, SkullModelBase var6, RenderType var7) {
      var3.pushPose();
      if (var0 == null) {
         var3.translate(0.5F, 0.0F, 0.5F);
      } else {
         float var8 = 0.25F;
         var3.translate(0.5F - (float)var0.getStepX() * 0.25F, 0.25F, 0.5F - (float)var0.getStepZ() * 0.25F);
      }

      var3.scale(-1.0F, -1.0F, 1.0F);
      VertexConsumer var9 = var4.getBuffer(var7);
      var6.setupAnim(var2, var1, 0.0F);
      var6.renderToBuffer(var3, var9, var5, OverlayTexture.NO_OVERLAY);
      var3.popPose();
   }

   public static RenderType getRenderType(SkullBlock.Type var0, @Nullable ResolvableProfile var1) {
      return getRenderType(var0, var1, (ResourceLocation)null);
   }

   public static RenderType getRenderType(SkullBlock.Type var0, @Nullable ResolvableProfile var1, @Nullable ResourceLocation var2) {
      return var0 == SkullBlock.Types.PLAYER && var1 != null ? RenderType.entityTranslucent(var2 != null ? var2 : Minecraft.getInstance().getSkinManager().getInsecureSkin(var1.gameProfile()).texture()) : RenderType.entityCutoutNoCullZOffset(var2 != null ? var2 : (ResourceLocation)SKIN_BY_TYPE.get(var0));
   }
}
