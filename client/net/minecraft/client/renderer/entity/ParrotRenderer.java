package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderer extends MobRenderer<Parrot, ParrotModel> {
   public static final ResourceLocation[] PARROT_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/parrot/parrot_red_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_green.png"), new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_grey.png")};

   public ParrotRenderer(EntityRenderDispatcher var1) {
      super(var1, new ParrotModel(), 0.3F);
   }

   protected ResourceLocation getTextureLocation(Parrot var1) {
      return PARROT_LOCATIONS[var1.getVariant()];
   }

   public float getBob(Parrot var1, float var2) {
      float var3 = Mth.lerp(var2, var1.oFlap, var1.flap);
      float var4 = Mth.lerp(var2, var1.oFlapSpeed, var1.flapSpeed);
      return (Mth.sin(var3) + 1.0F) * var4;
   }

   // $FF: synthetic method
   public float getBob(LivingEntity var1, float var2) {
      return this.getBob((Parrot)var1, var2);
   }
}
