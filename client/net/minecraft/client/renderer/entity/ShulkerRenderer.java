package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.layers.ShulkerHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderer extends MobRenderer<Shulker, ShulkerModel<Shulker>> {
   private static final ResourceLocation DEFAULT_TEXTURE_LOCATION;
   private static final ResourceLocation[] TEXTURE_LOCATION;

   public ShulkerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ShulkerModel(var1.bakeLayer(ModelLayers.SHULKER)), 0.0F);
      this.addLayer(new ShulkerHeadLayer(this));
   }

   public Vec3 getRenderOffset(Shulker var1, float var2) {
      return ((Vec3)var1.getRenderPosition(var2).orElse(super.getRenderOffset(var1, var2))).scale((double)var1.getScale());
   }

   public boolean shouldRender(Shulker var1, Frustum var2, double var3, double var5, double var7) {
      return super.shouldRender(var1, var2, var3, var5, var7) ? true : var1.getRenderPosition(0.0F).filter((var2x) -> {
         EntityType var3 = var1.getType();
         float var4 = var3.getHeight() / 2.0F;
         float var5 = var3.getWidth() / 2.0F;
         Vec3 var6 = Vec3.atBottomCenterOf(var1.blockPosition());
         return var2.isVisible((new AABB(var2x.x, var2x.y + (double)var4, var2x.z, var6.x, var6.y + (double)var4, var6.z)).inflate((double)var5, (double)var4, (double)var5));
      }).isPresent();
   }

   public ResourceLocation getTextureLocation(Shulker var1) {
      return getTextureLocation(var1.getColor());
   }

   public static ResourceLocation getTextureLocation(@Nullable DyeColor var0) {
      return var0 == null ? DEFAULT_TEXTURE_LOCATION : TEXTURE_LOCATION[var0.getId()];
   }

   protected void setupRotations(Shulker var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4 + 180.0F, var5, var6);
      var2.rotateAround(var1.getAttachFace().getOpposite().getRotation(), 0.0F, 0.5F, 0.0F);
   }

   static {
      DEFAULT_TEXTURE_LOCATION = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().withPath((var0) -> {
         return "textures/" + var0 + ".png";
      });
      TEXTURE_LOCATION = (ResourceLocation[])Sheets.SHULKER_TEXTURE_LOCATION.stream().map((var0) -> {
         return var0.texture().withPath((var0x) -> {
            return "textures/" + var0x + ".png";
         });
      }).toArray((var0) -> {
         return new ResourceLocation[var0];
      });
   }
}
