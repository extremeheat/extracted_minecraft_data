package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderSkeleton extends RenderBiped<EntitySkeleton> {
   private static final ResourceLocation field_110862_k = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation field_110861_l = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public RenderSkeleton(RenderManager var1) {
      super(var1, new ModelSkeleton(), 0.5F);
      this.func_177094_a(new LayerHeldItem(this));
      this.func_177094_a(new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelSkeleton(0.5F, true);
            this.field_177186_d = new ModelSkeleton(1.0F, true);
         }
      });
   }

   protected void func_77041_b(EntitySkeleton var1, float var2) {
      if (var1.func_82202_m() == 1) {
         GlStateManager.func_179152_a(1.2F, 1.2F, 1.2F);
      }

   }

   public void func_82422_c() {
      GlStateManager.func_179109_b(0.09375F, 0.1875F, 0.0F);
   }

   protected ResourceLocation func_110775_a(EntitySkeleton var1) {
      return var1.func_82202_m() == 1 ? field_110861_l : field_110862_k;
   }
}
