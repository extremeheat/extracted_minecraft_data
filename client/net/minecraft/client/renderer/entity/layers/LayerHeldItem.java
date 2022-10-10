package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase> {
   protected final RenderLivingBase<?> field_177206_a;

   public LayerHeldItem(RenderLivingBase<?> var1) {
      super();
      this.field_177206_a = var1;
   }

   public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      boolean var9 = var1.func_184591_cq() == EnumHandSide.RIGHT;
      ItemStack var10 = var9 ? var1.func_184592_cb() : var1.func_184614_ca();
      ItemStack var11 = var9 ? var1.func_184614_ca() : var1.func_184592_cb();
      if (!var10.func_190926_b() || !var11.func_190926_b()) {
         GlStateManager.func_179094_E();
         if (this.field_177206_a.func_177087_b().field_78091_s) {
            float var12 = 0.5F;
            GlStateManager.func_179109_b(0.0F, 0.75F, 0.0F);
            GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         }

         this.func_188358_a(var1, var11, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
         this.func_188358_a(var1, var10, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
         GlStateManager.func_179121_F();
      }
   }

   private void func_188358_a(EntityLivingBase var1, ItemStack var2, ItemCameraTransforms.TransformType var3, EnumHandSide var4) {
      if (!var2.func_190926_b()) {
         GlStateManager.func_179094_E();
         this.func_191361_a(var4);
         if (var1.func_70093_af()) {
            GlStateManager.func_179109_b(0.0F, 0.2F, 0.0F);
         }

         GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         boolean var5 = var4 == EnumHandSide.LEFT;
         GlStateManager.func_179109_b((float)(var5 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         Minecraft.func_71410_x().func_175597_ag().func_187462_a(var1, var2, var3, var5);
         GlStateManager.func_179121_F();
      }
   }

   protected void func_191361_a(EnumHandSide var1) {
      ((ModelBiped)this.field_177206_a.func_177087_b()).func_187073_a(0.0625F, var1);
   }

   public boolean func_177142_b() {
      return false;
   }
}
