package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class LayerSnowmanHead implements LayerRenderer<EntitySnowman> {
   private final RenderSnowMan field_177152_a;

   public LayerSnowmanHead(RenderSnowMan var1) {
      super();
      this.field_177152_a = var1;
   }

   public void func_177141_a(EntitySnowman var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.func_82150_aj() && var1.func_184748_o()) {
         GlStateManager.func_179094_E();
         this.field_177152_a.func_177087_b().func_205070_a().func_78794_c(0.0625F);
         float var9 = 0.625F;
         GlStateManager.func_179109_b(0.0F, -0.34375F, 0.0F);
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179152_a(0.625F, -0.625F, -0.625F);
         Minecraft.func_71410_x().func_175597_ag().func_178099_a(var1, new ItemStack(Blocks.field_196625_cS), ItemCameraTransforms.TransformType.HEAD);
         GlStateManager.func_179121_F();
      }
   }

   public boolean func_177142_b() {
      return true;
   }
}
