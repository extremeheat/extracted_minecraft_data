package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelIllager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RenderIllusionIllager extends RenderLiving<EntityMob> {
   private static final ResourceLocation field_193121_a = new ResourceLocation("textures/entity/illager/illusioner.png");

   public RenderIllusionIllager(RenderManager var1) {
      super(var1, new ModelIllager(0.0F, 0.0F, 64, 64), 0.5F);
      this.func_177094_a(new LayerHeldItem(this) {
         public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
            if (((EntityIllusionIllager)var1).func_193082_dl() || ((EntityIllusionIllager)var1).func_193096_dj()) {
               super.func_177141_a(var1, var2, var3, var4, var5, var6, var7, var8);
            }

         }

         protected void func_191361_a(EnumHandSide var1) {
            ((ModelIllager)this.field_177206_a.func_177087_b()).func_191216_a(var1).func_78794_c(0.0625F);
         }
      });
      ((ModelIllager)this.func_177087_b()).func_205062_a().field_78806_j = true;
   }

   protected ResourceLocation func_110775_a(EntityMob var1) {
      return field_193121_a;
   }

   protected void func_77041_b(EntityMob var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.func_179152_a(0.9375F, 0.9375F, 0.9375F);
   }

   public void func_76986_a(EntityMob var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.func_82150_aj()) {
         Vec3d[] var10 = ((EntityIllusionIllager)var1).func_193098_a(var9);
         float var11 = this.func_77044_a(var1, var9);

         for(int var12 = 0; var12 < var10.length; ++var12) {
            super.func_76986_a((EntityLiving)var1, var2 + var10[var12].field_72450_a + (double)MathHelper.func_76134_b((float)var12 + var11 * 0.5F) * 0.025D, var4 + var10[var12].field_72448_b + (double)MathHelper.func_76134_b((float)var12 + var11 * 0.75F) * 0.0125D, var6 + var10[var12].field_72449_c + (double)MathHelper.func_76134_b((float)var12 + var11 * 0.7F) * 0.025D, var8, var9);
         }
      } else {
         super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
      }

   }

   protected boolean func_193115_c(EntityMob var1) {
      return true;
   }

   // $FF: synthetic method
   protected boolean func_193115_c(EntityLivingBase var1) {
      return this.func_193115_c((EntityMob)var1);
   }
}
