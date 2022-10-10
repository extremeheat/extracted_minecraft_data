package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelArmorStand;
import net.minecraft.client.renderer.entity.model.ModelArmorStandArmor;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderArmorStand extends RenderLivingBase<EntityArmorStand> {
   public static final ResourceLocation field_177103_a = new ResourceLocation("textures/entity/armorstand/wood.png");

   public RenderArmorStand(RenderManager var1) {
      super(var1, new ModelArmorStand(), 0.0F);
      LayerBipedArmor var2 = new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelArmorStandArmor(0.5F);
            this.field_177186_d = new ModelArmorStandArmor(1.0F);
         }
      };
      this.func_177094_a(var2);
      this.func_177094_a(new LayerHeldItem(this));
      this.func_177094_a(new LayerElytra(this));
      this.func_177094_a(new LayerCustomHead(this.func_177087_b().field_78116_c));
   }

   protected ResourceLocation func_110775_a(EntityArmorStand var1) {
      return field_177103_a;
   }

   public ModelArmorStand func_177087_b() {
      return (ModelArmorStand)super.func_177087_b();
   }

   protected void func_77043_a(EntityArmorStand var1, float var2, float var3, float var4) {
      GlStateManager.func_179114_b(180.0F - var3, 0.0F, 1.0F, 0.0F);
      float var5 = (float)(var1.field_70170_p.func_82737_E() - var1.field_175437_i) + var4;
      if (var5 < 5.0F) {
         GlStateManager.func_179114_b(MathHelper.func_76126_a(var5 / 1.5F * 3.1415927F) * 3.0F, 0.0F, 1.0F, 0.0F);
      }

   }

   protected boolean func_177070_b(EntityArmorStand var1) {
      return var1.func_174833_aM();
   }

   public void func_76986_a(EntityArmorStand var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.func_181026_s()) {
         this.field_188323_j = true;
      }

      super.func_76986_a((EntityLivingBase)var1, var2, var4, var6, var8, var9);
      if (var1.func_181026_s()) {
         this.field_188323_j = false;
      }

   }

   // $FF: synthetic method
   protected boolean func_177070_b(EntityLivingBase var1) {
      return this.func_177070_b((EntityArmorStand)var1);
   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }
}
