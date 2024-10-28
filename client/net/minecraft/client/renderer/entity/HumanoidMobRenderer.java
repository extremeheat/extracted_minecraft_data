package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.Mob;

public abstract class HumanoidMobRenderer<T extends Mob, M extends HumanoidModel<T>> extends MobRenderer<T, M> {
   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      this(var1, var2, var3, 1.0F, 1.0F, 1.0F);
   }

   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, float var3, float var4, float var5, float var6) {
      super(var1, var2, var3);
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var4, var5, var6, var1.getItemInHandRenderer()));
      this.addLayer(new ElytraLayer(this, var1.getModelSet()));
      this.addLayer(new ItemInHandLayer(this, var1.getItemInHandRenderer()));
   }
}
