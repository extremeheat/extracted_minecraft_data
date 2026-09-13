package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSkeleton extends RenderBiped {
   private static final ResourceLocation field_110862_k = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation field_110861_l = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public RenderSkeleton() {
      super(new ModelSkeleton(), 0.5F);
   }

   protected void func_77041_b(EntitySkeleton var1, float var2) {
      if (var1.func_82202_m() == 1) {
         GL11.glScalef(1.2F, 1.2F, 1.2F);
      }
   }

   @Override
   protected void func_82422_c() {
      GL11.glTranslatef(0.09375F, 0.1875F, 0.0F);
   }

   protected ResourceLocation func_110775_a(EntitySkeleton var1) {
      return var1.func_82202_m() == 1 ? field_110861_l : field_110862_k;
   }
}
