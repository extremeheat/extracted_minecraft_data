package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.model.ModelArmorStandArmor;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;

public class ArmorStandRenderer extends RendererLivingEntity<EntityArmorStand> {
   public static final ResourceLocation field_177103_a = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(RenderManager var1) {
      super(var1, new ModelArmorStand(), 0.0F);
      LayerBipedArmor var2 = new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelArmorStandArmor(0.5F);
            this.field_177186_d = new ModelArmorStandArmor(1.0F);
         }
      };
      this.func_177094_a(var2);
      this.func_177094_a(new LayerHeldItem(this));
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
   }

   protected boolean func_177070_b(EntityArmorStand var1) {
      return var1.func_174833_aM();
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
