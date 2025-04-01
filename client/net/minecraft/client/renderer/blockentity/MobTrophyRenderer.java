package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.MobTrophyInfo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MobTrophyBlock;
import net.minecraft.world.level.block.entity.MobTrophyBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class MobTrophyRenderer implements BlockEntityRenderer<MobTrophyBlockEntity> {
   public static final float EXTRA_SCALE_BECAUSE_MOBS_LIE_ABOUT_SIZE = 0.5F;
   public static final float BOX_WIDTH = 0.875F;
   public static final float BOX_HEIGHT = 0.6875F;
   private final EntityRenderDispatcher entityRenderer;

   public MobTrophyRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.getEntityRenderer();
   }

   public void render(MobTrophyBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      MobTrophyInfo var8 = var1.getEntityType();
      if (var8 != null) {
         render(var3, var4, var5, var8, this.entityRenderer, (Direction)var1.getBlockState().getValue(MobTrophyBlock.FACING), var1.getLevel());
      }
   }

   public static void render(PoseStack var0, MultiBufferSource var1, int var2, MobTrophyInfo var3, EntityRenderDispatcher var4, Direction var5, @Nullable Level var6) {
      EntityType var7 = (EntityType)var3.type().value();
      EntityRenderer var8 = var4.getRenderer(var7);
      if (var8 != null) {
         var0.pushPose();
         EntityDimensions var9 = var7.getDimensions();
         float var10 = 0.875F / var9.height();
         float var11 = 0.6875F / var9.width();
         float var12 = Math.max(0.1F, Math.min(var10, var11) * 0.5F);
         if (!Float.isFinite(var12)) {
            var12 = 1.0F;
         }

         float var13 = 0.0625F;
         float var14 = 0.0F;
         float var15 = -var5.toYRot();
         var0.translate(0.5F, 0.0625F, 0.5F);
         var0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var15));
         var0.scale(var12, var12, var12);
         renderEntity(var8, var0, var1, var2, var3.shiny(), var6);
         var0.popPose();
      }
   }

   private static <T extends Entity, S extends EntityRenderState> void renderEntity(EntityRenderer<T, S> var0, PoseStack var1, MultiBufferSource var2, int var3, boolean var4, @Nullable Level var5) {
      MultiBufferSource var6;
      if (var4) {
         var6 = (var1x) -> var1x.format().contains(VertexFormatElement.UV0) ? ItemRenderer.getFoilBuffer(var2, var1x, false, ItemStackRenderState.FoilType.STANDARD) : var2.getBuffer(var1x);
      } else {
         var6 = var2;
      }

      EntityRenderState var7 = var0.createSpecialRenderStateBecauseImLazyMojangDevAndItsTimeToHack(var5);
      var0.render(var7, var1, var6, var3);
   }
}
