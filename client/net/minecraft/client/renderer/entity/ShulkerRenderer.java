package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderer extends MobRenderer<Shulker, ShulkerRenderState, ShulkerModel> {
   private static final ResourceLocation DEFAULT_TEXTURE_LOCATION = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION
      .texture()
      .withPath(var0 -> "textures/" + var0 + ".png");
   private static final ResourceLocation[] TEXTURE_LOCATION = Sheets.SHULKER_TEXTURE_LOCATION
      .stream()
      .map(var0 -> var0.texture().withPath(var0x -> "textures/" + var0x + ".png"))
      .toArray(ResourceLocation[]::new);

   public ShulkerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ShulkerModel(var1.bakeLayer(ModelLayers.SHULKER)), 0.0F);
   }

   public Vec3 getRenderOffset(ShulkerRenderState var1) {
      return var1.renderOffset;
   }

   public boolean shouldRender(Shulker var1, Frustum var2, double var3, double var5, double var7) {
      if (super.shouldRender(var1, var2, var3, var5, var7)) {
         return true;
      } else {
         Vec3 var9 = var1.getRenderPosition(0.0F);
         if (var9 == null) {
            return false;
         } else {
            EntityType var10 = var1.getType();
            float var11 = var10.getHeight() / 2.0F;
            float var12 = var10.getWidth() / 2.0F;
            Vec3 var13 = Vec3.atBottomCenterOf(var1.blockPosition());
            return var2.isVisible(
               new AABB(var9.x, var9.y + (double)var11, var9.z, var13.x, var13.y + (double)var11, var13.z).inflate((double)var12, (double)var11, (double)var12)
            );
         }
      }
   }

   public ResourceLocation getTextureLocation(ShulkerRenderState var1) {
      return getTextureLocation(var1.color);
   }

   public ShulkerRenderState createRenderState() {
      return new ShulkerRenderState();
   }

   public void extractRenderState(Shulker var1, ShulkerRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.renderOffset = Objects.requireNonNullElse(var1.getRenderPosition(var3), Vec3.ZERO);
      var2.color = var1.getColor();
      var2.peekAmount = var1.getClientPeekAmount(var3);
      var2.yHeadRot = var1.yHeadRot;
      var2.yBodyRot = var1.yBodyRot;
      var2.attachFace = var1.getAttachFace();
   }

   public static ResourceLocation getTextureLocation(@Nullable DyeColor var0) {
      return var0 == null ? DEFAULT_TEXTURE_LOCATION : TEXTURE_LOCATION[var0.getId()];
   }

   protected void setupRotations(ShulkerRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3 + 180.0F, var4);
      var2.rotateAround(var1.attachFace.getOpposite().getRotation(), 0.0F, 0.5F, 0.0F);
   }
}
