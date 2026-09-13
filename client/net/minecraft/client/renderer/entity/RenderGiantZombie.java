package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGiantZombie extends RenderLiving {
   private static final ResourceLocation field_110871_a = new ResourceLocation("textures/entity/zombie/zombie.png");
   private float field_77073_a;

   public RenderGiantZombie(ModelBase var1, float var2, float var3) {
      super(var1, var2 * var3);
      this.field_77073_a = var3;
   }

   protected void func_77041_b(EntityGiantZombie var1, float var2) {
      GL11.glScalef(this.field_77073_a, this.field_77073_a, this.field_77073_a);
   }

   protected ResourceLocation func_110775_a(EntityGiantZombie var1) {
      return field_110871_a;
   }
}
