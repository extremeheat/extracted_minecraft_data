package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItem extends Render<EntityItem> {
   private final RenderItem field_177080_a;
   private Random field_177079_e = new Random();

   public RenderEntityItem(RenderManager var1, RenderItem var2) {
      super(var1);
      this.field_177080_a = var2;
      this.field_76989_e = 0.15F;
      this.field_76987_f = 0.75F;
   }

   private int func_177077_a(EntityItem var1, double var2, double var4, double var6, float var8, IBakedModel var9) {
      ItemStack var10 = var1.func_92059_d();
      Item var11 = var10.func_77973_b();
      if (var11 == null) {
         return 0;
      } else {
         boolean var12 = var9.func_177556_c();
         int var13 = this.func_177078_a(var10);
         float var14 = 0.25F;
         float var15 = MathHelper.func_76126_a(((float)var1.func_174872_o() + var8) / 10.0F + var1.field_70290_d) * 0.1F + 0.1F;
         float var16 = var9.func_177552_f().func_181688_b(ItemCameraTransforms.TransformType.GROUND).field_178363_d.y;
         GlStateManager.func_179109_b((float)var2, (float)var4 + var15 + 0.25F * var16, (float)var6);
         float var17;
         if (var12 || this.field_76990_c.field_78733_k != null) {
            var17 = (((float)var1.func_174872_o() + var8) / 20.0F + var1.field_70290_d) * 57.295776F;
            GlStateManager.func_179114_b(var17, 0.0F, 1.0F, 0.0F);
         }

         if (!var12) {
            var17 = -0.0F * (float)(var13 - 1) * 0.5F;
            float var18 = -0.0F * (float)(var13 - 1) * 0.5F;
            float var19 = -0.046875F * (float)(var13 - 1) * 0.5F;
            GlStateManager.func_179109_b(var17, var18, var19);
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         return var13;
      }
   }

   private int func_177078_a(ItemStack var1) {
      byte var2 = 1;
      if (var1.field_77994_a > 48) {
         var2 = 5;
      } else if (var1.field_77994_a > 32) {
         var2 = 4;
      } else if (var1.field_77994_a > 16) {
         var2 = 3;
      } else if (var1.field_77994_a > 1) {
         var2 = 2;
      }

      return var2;
   }

   public void func_76986_a(EntityItem var1, double var2, double var4, double var6, float var8, float var9) {
      ItemStack var10 = var1.func_92059_d();
      this.field_177079_e.setSeed(187L);
      boolean var11 = false;
      if (this.func_180548_c(var1)) {
         this.field_76990_c.field_78724_e.func_110581_b(this.func_110775_a(var1)).func_174936_b(false, false);
         var11 = true;
      }

      GlStateManager.func_179091_B();
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179094_E();
      IBakedModel var12 = this.field_177080_a.func_175037_a().func_178089_a(var10);
      int var13 = this.func_177077_a(var1, var2, var4, var6, var9, var12);

      for(int var14 = 0; var14 < var13; ++var14) {
         float var15;
         float var16;
         float var17;
         if (var12.func_177556_c()) {
            GlStateManager.func_179094_E();
            if (var14 > 0) {
               var15 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var16 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var17 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.func_179109_b(var15, var16, var17);
            }

            GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
            var12.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GROUND);
            this.field_177080_a.func_180454_a(var10, var12);
            GlStateManager.func_179121_F();
         } else {
            GlStateManager.func_179094_E();
            var12.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GROUND);
            this.field_177080_a.func_180454_a(var10, var12);
            GlStateManager.func_179121_F();
            var15 = var12.func_177552_f().field_181699_o.field_178363_d.x;
            var16 = var12.func_177552_f().field_181699_o.field_178363_d.y;
            var17 = var12.func_177552_f().field_181699_o.field_178363_d.z;
            GlStateManager.func_179109_b(0.0F * var15, 0.0F * var16, 0.046875F * var17);
         }
      }

      GlStateManager.func_179121_F();
      GlStateManager.func_179101_C();
      GlStateManager.func_179084_k();
      this.func_180548_c(var1);
      if (var11) {
         this.field_76990_c.field_78724_e.func_110581_b(this.func_110775_a(var1)).func_174935_a();
      }

      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityItem var1) {
      return TextureMap.field_110575_b;
   }
}
