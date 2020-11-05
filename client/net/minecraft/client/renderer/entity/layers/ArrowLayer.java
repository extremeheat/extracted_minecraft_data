package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;

public class ArrowLayer<T extends LivingEntity, M extends PlayerModel<T>> extends StuckInBodyLayer<T, M> {
   private final EntityRenderDispatcher dispatcher;
   private Arrow arrow;

   public ArrowLayer(LivingEntityRenderer<T, M> var1) {
      super(var1);
      this.dispatcher = var1.getDispatcher();
   }

   protected int numStuck(T var1) {
      return var1.getArrowCount();
   }

   protected void renderStuckItem(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8) {
      float var9 = Mth.sqrt(var5 * var5 + var7 * var7);
      this.arrow = new Arrow(var4.level, var4.getX(), var4.getY(), var4.getZ());
      this.arrow.yRot = (float)(Math.atan2((double)var5, (double)var7) * 57.2957763671875D);
      this.arrow.xRot = (float)(Math.atan2((double)var6, (double)var9) * 57.2957763671875D);
      this.arrow.yRotO = this.arrow.yRot;
      this.arrow.xRotO = this.arrow.xRot;
      this.dispatcher.render(this.arrow, 0.0D, 0.0D, 0.0D, 0.0F, var8, var1, var2, var3);
   }
}
