package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerElytra implements LayerRenderer<EntityLivingBase> {
   private static final ResourceLocation field_188355_a = new ResourceLocation("textures/entity/elytra.png");
   protected final RenderLivingBase<?> field_188356_b;
   private final ModelElytra field_188357_c = new ModelElytra();

   public LayerElytra(RenderLivingBase<?> var1) {
      super();
      this.field_188356_b = var1;
   }

   public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = var1.func_184582_a(EntityEquipmentSlot.CHEST);
      if (var9.func_77973_b() == Items.field_185160_cR) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179147_l();
         GlStateManager.func_187401_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         if (var1 instanceof AbstractClientPlayer) {
            AbstractClientPlayer var10 = (AbstractClientPlayer)var1;
            if (var10.func_184833_s() && var10.func_184834_t() != null) {
               this.field_188356_b.func_110776_a(var10.func_184834_t());
            } else if (var10.func_152122_n() && var10.func_110303_q() != null && var10.func_175148_a(EnumPlayerModelParts.CAPE)) {
               this.field_188356_b.func_110776_a(var10.func_110303_q());
            } else {
               this.field_188356_b.func_110776_a(field_188355_a);
            }
         } else {
            this.field_188356_b.func_110776_a(field_188355_a);
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, 0.0F, 0.125F);
         this.field_188357_c.func_78087_a(var2, var3, var5, var6, var7, var8, var1);
         this.field_188357_c.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
         if (var9.func_77948_v()) {
            LayerArmorBase.func_188364_a(this.field_188356_b, var1, this.field_188357_c, var2, var3, var4, var5, var6, var7, var8);
         }

         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
