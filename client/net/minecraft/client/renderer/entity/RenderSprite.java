package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderSprite<T extends Entity> extends Render<T> {
   protected final Item field_177084_a;
   private final ItemRenderer field_177083_e;

   public RenderSprite(RenderManager var1, Item var2, ItemRenderer var3) {
      super(var1);
      this.field_177084_a = var2;
      this.field_177083_e = var3;
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      GlStateManager.func_179091_B();
      GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b((float)(this.field_76990_c.field_78733_k.field_74320_O == 2 ? -1 : 1) * this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
      this.func_110776_a(TextureMap.field_110575_b);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      this.field_177083_e.func_181564_a(this.func_177082_d(var1), ItemCameraTransforms.TransformType.GROUND);
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   public ItemStack func_177082_d(T var1) {
      return new ItemStack(this.field_177084_a);
   }

   protected ResourceLocation func_110775_a(Entity var1) {
      return TextureMap.field_110575_b;
   }
}
