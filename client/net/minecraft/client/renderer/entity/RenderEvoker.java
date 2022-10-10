package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelIllager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpellcasterIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderEvoker extends RenderLiving<EntityMob> {
   private static final ResourceLocation field_191338_a = new ResourceLocation("textures/entity/illager/evoker.png");

   public RenderEvoker(RenderManager var1) {
      super(var1, new ModelIllager(0.0F, 0.0F, 64, 64), 0.5F);
      this.func_177094_a(new LayerHeldItem(this) {
         public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
            if (((EntitySpellcasterIllager)var1).func_193082_dl()) {
               super.func_177141_a(var1, var2, var3, var4, var5, var6, var7, var8);
            }

         }

         protected void func_191361_a(EnumHandSide var1) {
            ((ModelIllager)this.field_177206_a.func_177087_b()).func_191216_a(var1).func_78794_c(0.0625F);
         }
      });
   }

   protected ResourceLocation func_110775_a(EntityMob var1) {
      return field_191338_a;
   }

   protected void func_77041_b(EntityMob var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.func_179152_a(0.9375F, 0.9375F, 0.9375F);
   }
}
