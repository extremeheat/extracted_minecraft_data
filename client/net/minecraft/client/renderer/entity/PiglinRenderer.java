package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinRenderer extends HumanoidMobRenderer<Mob, PiglinModel<Mob>> {
   private static final Map<EntityType<?>, ResourceLocation> TEXTURES;
   private static final float PIGLIN_CUSTOM_HEAD_SCALE = 1.0019531F;

   public PiglinRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ModelLayerLocation var4, boolean var5) {
      super(var1, createModel(var1.getModelSet(), var2, var5), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
      this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(var1.bakeLayer(var3)), new HumanoidModel(var1.bakeLayer(var4))));
   }

   private static PiglinModel<Mob> createModel(EntityModelSet var0, ModelLayerLocation var1, boolean var2) {
      PiglinModel var3 = new PiglinModel(var0.bakeLayer(var1));
      if (var2) {
         var3.rightEar.visible = false;
      }

      return var3;
   }

   public ResourceLocation getTextureLocation(Mob var1) {
      ResourceLocation var2 = (ResourceLocation)TEXTURES.get(var1.getType());
      if (var2 == null) {
         throw new IllegalArgumentException("I don't know what texture to use for " + var1.getType());
      } else {
         return var2;
      }
   }

   protected boolean isShaking(Mob var1) {
      return super.isShaking(var1) || var1 instanceof AbstractPiglin && ((AbstractPiglin)var1).isConverting();
   }

   // $FF: synthetic method
   protected boolean isShaking(LivingEntity var1) {
      return this.isShaking((Mob)var1);
   }

   static {
      TEXTURES = ImmutableMap.of(EntityType.PIGLIN, new ResourceLocation("textures/entity/piglin/piglin.png"), EntityType.ZOMBIFIED_PIGLIN, new ResourceLocation("textures/entity/piglin/zombified_piglin.png"), EntityType.PIGLIN_BRUTE, new ResourceLocation("textures/entity/piglin/piglin_brute.png"));
   }
}
