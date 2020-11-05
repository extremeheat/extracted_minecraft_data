package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockRenderer extends BlockEntityRenderer<SkullBlockEntity> {
   private static final Map<SkullBlock.Type, SkullModel> MODEL_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      SkullModel var1 = new SkullModel(0, 0, 64, 32);
      HumanoidHeadModel var2 = new HumanoidHeadModel();
      DragonHeadModel var3 = new DragonHeadModel(0.0F);
      var0.put(SkullBlock.Types.SKELETON, var1);
      var0.put(SkullBlock.Types.WITHER_SKELETON, var1);
      var0.put(SkullBlock.Types.PLAYER, var2);
      var0.put(SkullBlock.Types.ZOMBIE, var2);
      var0.put(SkullBlock.Types.CREEPER, var1);
      var0.put(SkullBlock.Types.DRAGON, var3);
   });
   private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      var0.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      var0.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      var0.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      var0.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      var0.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
   });

   public SkullBlockRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(SkullBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      float var7 = var1.getMouthAnimation(var2);
      BlockState var8 = var1.getBlockState();
      boolean var9 = var8.getBlock() instanceof WallSkullBlock;
      Direction var10 = var9 ? (Direction)var8.getValue(WallSkullBlock.FACING) : null;
      float var11 = 22.5F * (float)(var9 ? (2 + var10.get2DDataValue()) * 4 : (Integer)var8.getValue(SkullBlock.ROTATION));
      renderSkull(var10, var11, ((AbstractSkullBlock)var8.getBlock()).getType(), var1.getOwnerProfile(), var7, var3, var4, var5);
   }

   public static void renderSkull(@Nullable Direction var0, float var1, SkullBlock.Type var2, @Nullable GameProfile var3, float var4, PoseStack var5, MultiBufferSource var6, int var7) {
      SkullModel var8 = (SkullModel)MODEL_BY_TYPE.get(var2);
      var5.pushPose();
      if (var0 == null) {
         var5.translate(0.5D, 0.0D, 0.5D);
      } else {
         float var9 = 0.25F;
         var5.translate((double)(0.5F - (float)var0.getStepX() * 0.25F), 0.25D, (double)(0.5F - (float)var0.getStepZ() * 0.25F));
      }

      var5.scale(-1.0F, -1.0F, 1.0F);
      VertexConsumer var10 = var6.getBuffer(getRenderType(var2, var3));
      var8.setupAnim(var4, var1, 0.0F);
      var8.renderToBuffer(var5, var10, var7, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      var5.popPose();
   }

   private static RenderType getRenderType(SkullBlock.Type var0, @Nullable GameProfile var1) {
      ResourceLocation var2 = (ResourceLocation)SKIN_BY_TYPE.get(var0);
      if (var0 == SkullBlock.Types.PLAYER && var1 != null) {
         Minecraft var3 = Minecraft.getInstance();
         Map var4 = var3.getSkinManager().getInsecureSkinInformation(var1);
         return var4.containsKey(Type.SKIN) ? RenderType.entityTranslucent(var3.getSkinManager().registerTexture((MinecraftProfileTexture)var4.get(Type.SKIN), Type.SKIN)) : RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(var1)));
      } else {
         return RenderType.entityCutoutNoCullZOffset(var2);
      }
   }
}
