package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.ResourceLocation;

public class RenderEndermite extends RenderLiving<EntityEndermite> {
   private static final ResourceLocation field_177108_a = new ResourceLocation("textures/entity/endermite.png");

   public RenderEndermite(RenderManager var1) {
      super(var1, new ModelEnderMite(), 0.3F);
   }

   protected float func_77037_a(EntityEndermite var1) {
      return 180.0F;
   }

   protected ResourceLocation func_110775_a(EntityEndermite var1) {
      return field_177108_a;
   }

   // $FF: synthetic method
   protected float func_77037_a(EntityLivingBase var1) {
      return this.func_77037_a((EntityEndermite)var1);
   }

   // $FF: synthetic method
   protected ResourceLocation func_110775_a(Entity var1) {
      return this.func_110775_a((EntityEndermite)var1);
   }
}
