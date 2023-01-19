package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
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
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class SkullBlockRenderer implements BlockEntityRenderer<SkullBlockEntity> {
   private final Map<SkullBlock.Type, SkullModelBase> modelByType;
   private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE = Util.make(Maps.newHashMap(), var0 -> {
      var0.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      var0.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      var0.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      var0.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      var0.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      var0.put(SkullBlock.Types.PIGLIN, new ResourceLocation("textures/entity/piglin/piglin.png"));
      var0.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
   });

   public static Map<SkullBlock.Type, SkullModelBase> createSkullRenderers(EntityModelSet var0) {
      Builder var1 = ImmutableMap.builder();
      var1.put(SkullBlock.Types.SKELETON, new SkullModel(var0.bakeLayer(ModelLayers.SKELETON_SKULL)));
      var1.put(SkullBlock.Types.WITHER_SKELETON, new SkullModel(var0.bakeLayer(ModelLayers.WITHER_SKELETON_SKULL)));
      var1.put(SkullBlock.Types.PLAYER, new SkullModel(var0.bakeLayer(ModelLayers.PLAYER_HEAD)));
      var1.put(SkullBlock.Types.ZOMBIE, new SkullModel(var0.bakeLayer(ModelLayers.ZOMBIE_HEAD)));
      var1.put(SkullBlock.Types.CREEPER, new SkullModel(var0.bakeLayer(ModelLayers.CREEPER_HEAD)));
      var1.put(SkullBlock.Types.DRAGON, new DragonHeadModel(var0.bakeLayer(ModelLayers.DRAGON_SKULL)));
      var1.put(SkullBlock.Types.PIGLIN, new PiglinHeadModel(var0.bakeLayer(ModelLayers.PIGLIN_HEAD)));
      return var1.build();
   }

   public SkullBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.modelByType = createSkullRenderers(var1.getModelSet());
   }

   public void render(SkullBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      float var7 = var1.getAnimation(var2);
      BlockState var8 = var1.getBlockState();
      boolean var9 = var8.getBlock() instanceof WallSkullBlock;
      Direction var10 = var9 ? var8.getValue(WallSkullBlock.FACING) : null;
      int var11 = var9 ? RotationSegment.convertToSegment(var10.getOpposite()) : var8.getValue(SkullBlock.ROTATION);
      float var12 = RotationSegment.convertToDegrees(var11);
      SkullBlock.Type var13 = ((AbstractSkullBlock)var8.getBlock()).getType();
      SkullModelBase var14 = this.modelByType.get(var13);
      RenderType var15 = getRenderType(var13, var1.getOwnerProfile());
      renderSkull(var10, var12, var7, var3, var4, var5, var14, var15);
   }

   public static void renderSkull(
      @Nullable Direction var0, float var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, SkullModelBase var6, RenderType var7
   ) {
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
      var6.renderToBuffer(var3, var9, var5, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      var3.popPose();
   }

   public static RenderType getRenderType(SkullBlock.Type var0, @Nullable GameProfile var1) {
      ResourceLocation var2 = SKIN_BY_TYPE.get(var0);
      if (var0 == SkullBlock.Types.PLAYER && var1 != null) {
         Minecraft var3 = Minecraft.getInstance();
         Map var4 = var3.getSkinManager().getInsecureSkinInformation(var1);
         return var4.containsKey(Type.SKIN)
            ? RenderType.entityTranslucent(var3.getSkinManager().registerTexture((MinecraftProfileTexture)var4.get(Type.SKIN), Type.SKIN))
            : RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(var1)));
      } else {
         return RenderType.entityCutoutNoCullZOffset(var2);
      }
   }
}
