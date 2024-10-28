package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;

public class ParrotOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
   private final ParrotModel model;

   public ParrotOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new ParrotModel(var2.bakeLayer(ModelLayers.PARROT));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.render(var1, var2, var3, var4, var5, var6, var9, var10, true);
      this.render(var1, var2, var3, var4, var5, var6, var9, var10, false);
   }

   private void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, boolean var9) {
      CompoundTag var10 = var9 ? var4.getShoulderEntityLeft() : var4.getShoulderEntityRight();
      EntityType.byString(var10.getString("id")).filter((var0) -> {
         return var0 == EntityType.PARROT;
      }).ifPresent((var11) -> {
         var1.pushPose();
         var1.translate(var9 ? 0.4F : -0.4F, var4.isCrouching() ? -1.3F : -1.5F, 0.0F);
         Parrot.Variant var12 = Parrot.Variant.byId(var10.getInt("Variant"));
         VertexConsumer var13 = var2.getBuffer(this.model.renderType(ParrotRenderer.getVariantTexture(var12)));
         this.model.renderOnShoulder(var1, var13, var3, OverlayTexture.NO_OVERLAY, var5, var6, var7, var8, var4.tickCount);
         var1.popPose();
      });
   }
}
