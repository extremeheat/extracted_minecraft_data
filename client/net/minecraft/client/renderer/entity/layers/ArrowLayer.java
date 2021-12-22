package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;

public class ArrowLayer<T extends LivingEntity, M extends PlayerModel<T>> extends StuckInBodyLayer<T, M> {
   private final EntityRenderDispatcher dispatcher;

   public ArrowLayer(EntityRendererProvider.Context var1, LivingEntityRenderer<T, M> var2) {
      super(var2);
      this.dispatcher = var1.getEntityRenderDispatcher();
   }

   protected int numStuck(T var1) {
      return var1.getArrowCount();
   }

   protected void renderStuckItem(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8) {
      float var9 = Mth.sqrt(var5 * var5 + var7 * var7);
      Arrow var10 = new Arrow(var4.level, var4.getX(), var4.getY(), var4.getZ());
      var10.setYRot((float)(Math.atan2((double)var5, (double)var7) * 57.2957763671875D));
      var10.setXRot((float)(Math.atan2((double)var6, (double)var9) * 57.2957763671875D));
      var10.yRotO = var10.getYRot();
      var10.xRotO = var10.getXRot();
      this.dispatcher.render(var10, 0.0D, 0.0D, 0.0D, 0.0F, var8, var1, var2, var3);
   }
}
