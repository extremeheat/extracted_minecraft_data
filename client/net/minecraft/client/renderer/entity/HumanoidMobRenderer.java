package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class HumanoidMobRenderer<T extends Mob, M extends HumanoidModel<T>> extends MobRenderer<T, M> {
   private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation("textures/entity/steve.png");

   public HumanoidMobRenderer(EntityRenderDispatcher var1, M var2, float var3) {
      super(var1, var2, var3);
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new ItemInHandLayer(this));
   }

   protected ResourceLocation getTextureLocation(T var1) {
      return DEFAULT_LOCATION;
   }
}
