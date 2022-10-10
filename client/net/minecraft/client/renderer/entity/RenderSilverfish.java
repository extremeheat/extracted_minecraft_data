package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving<EntitySilverfish> {
   private static final ResourceLocation field_110882_a = new ResourceLocation("textures/entity/silverfish.png");

   public RenderSilverfish(RenderManager var1) {
      super(var1, new ModelSilverfish(), 0.3F);
   }

   protected float func_77037_a(EntitySilverfish var1) {
      return 180.0F;
   }

   protected ResourceLocation func_110775_a(EntitySilverfish var1) {
      return field_110882_a;
   }

   // $FF: synthetic method
   protected float func_77037_a(EntityLivingBase var1) {
      return this.func_77037_a((EntitySilverfish)var1);
   }

   // $FF: synthetic method
   protected ResourceLocation func_110775_a(Entity var1) {
      return this.func_110775_a((EntitySilverfish)var1);
   }
}
