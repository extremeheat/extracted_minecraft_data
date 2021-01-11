package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItemWitch implements LayerRenderer<EntityWitch> {
   private final RenderWitch field_177144_a;

   public LayerHeldItemWitch(RenderWitch var1) {
      super();
      this.field_177144_a = var1;
   }

   public void func_177141_a(EntityWitch var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = var1.func_70694_bm();
      if (var9 != null) {
         GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
         GlStateManager.func_179094_E();
         if (this.field_177144_a.func_177087_b().field_78091_s) {
            GlStateManager.func_179109_b(0.0F, 0.625F, 0.0F);
            GlStateManager.func_179114_b(-20.0F, -1.0F, 0.0F, 0.0F);
            float var10 = 0.5F;
            GlStateManager.func_179152_a(var10, var10, var10);
         }

         ((ModelWitch)this.field_177144_a.func_177087_b()).field_82898_f.func_78794_c(0.0625F);
         GlStateManager.func_179109_b(-0.0625F, 0.53125F, 0.21875F);
         Item var13 = var9.func_77973_b();
         Minecraft var11 = Minecraft.func_71410_x();
         float var12;
         if (var13 instanceof ItemBlock && var11.func_175602_ab().func_175021_a(Block.func_149634_a(var13), var9.func_77960_j())) {
            GlStateManager.func_179109_b(0.0F, 0.0625F, -0.25F);
            GlStateManager.func_179114_b(30.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(-5.0F, 0.0F, 1.0F, 0.0F);
            var12 = 0.375F;
            GlStateManager.func_179152_a(var12, -var12, var12);
         } else if (var13 == Items.field_151031_f) {
            GlStateManager.func_179109_b(0.0F, 0.125F, -0.125F);
            GlStateManager.func_179114_b(-45.0F, 0.0F, 1.0F, 0.0F);
            var12 = 0.625F;
            GlStateManager.func_179152_a(var12, -var12, var12);
            GlStateManager.func_179114_b(-100.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(-20.0F, 0.0F, 1.0F, 0.0F);
         } else if (var13.func_77662_d()) {
            if (var13.func_77629_n_()) {
               GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.func_179109_b(0.0F, -0.0625F, 0.0F);
            }

            this.field_177144_a.func_82422_c();
            GlStateManager.func_179109_b(0.0625F, -0.125F, 0.0F);
            var12 = 0.625F;
            GlStateManager.func_179152_a(var12, -var12, var12);
            GlStateManager.func_179114_b(0.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(0.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.func_179109_b(0.1875F, 0.1875F, 0.0F);
            var12 = 0.875F;
            GlStateManager.func_179152_a(var12, var12, var12);
            GlStateManager.func_179114_b(-20.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(-60.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(-30.0F, 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.func_179114_b(-15.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(40.0F, 0.0F, 0.0F, 1.0F);
         var11.func_175597_ag().func_178099_a(var1, var9, ItemCameraTransforms.TransformType.THIRD_PERSON);
         GlStateManager.func_179121_F();
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
