package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinRenderer extends HumanoidMobRenderer<Mob, PiglinModel<Mob>> {
   private static final Map<EntityType<?>, ResourceLocation> resourceLocations;

   public PiglinRenderer(EntityRenderDispatcher var1, boolean var2) {
      super(var1, createModel(var2), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
      this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(0.5F), new HumanoidModel(1.02F)));
   }

   private static PiglinModel<Mob> createModel(boolean var0) {
      PiglinModel var1 = new PiglinModel(0.0F, 64, 64);
      if (var0) {
         var1.earLeft.visible = false;
      }

      return var1;
   }

   public ResourceLocation getTextureLocation(Mob var1) {
      ResourceLocation var2 = (ResourceLocation)resourceLocations.get(var1.getType());
      if (var2 == null) {
         throw new IllegalArgumentException("I don't know what texture to use for " + var1.getType());
      } else {
         return var2;
      }
   }

   protected boolean isShaking(Mob var1) {
      return var1 instanceof AbstractPiglin && ((AbstractPiglin)var1).isConverting();
   }

   // $FF: synthetic method
   protected boolean isShaking(LivingEntity var1) {
      return this.isShaking((Mob)var1);
   }

   static {
      resourceLocations = ImmutableMap.of(EntityType.PIGLIN, new ResourceLocation("textures/entity/piglin/piglin.png"), EntityType.ZOMBIFIED_PIGLIN, new ResourceLocation("textures/entity/piglin/zombified_piglin.png"), EntityType.PIGLIN_BRUTE, new ResourceLocation("textures/entity/piglin/piglin_brute.png"));
   }
}
