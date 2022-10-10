package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderEntityItem extends Render<EntityItem> {
   private final ItemRenderer field_177080_a;
   private final Random field_177079_e = new Random();

   public RenderEntityItem(RenderManager var1, ItemRenderer var2) {
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
         float var16 = var9.func_177552_f().func_181688_b(ItemCameraTransforms.TransformType.GROUND).field_178363_d.func_195900_b();
         GlStateManager.func_179109_b((float)var2, (float)var4 + var15 + 0.25F * var16, (float)var6);
         if (var12 || this.field_76990_c.field_78733_k != null) {
            float var17 = (((float)var1.func_174872_o() + var8) / 20.0F + var1.field_70290_d) * 57.295776F;
            GlStateManager.func_179114_b(var17, 0.0F, 1.0F, 0.0F);
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         return var13;
      }
   }

   private int func_177078_a(ItemStack var1) {
      byte var2 = 1;
      if (var1.func_190916_E() > 48) {
         var2 = 5;
      } else if (var1.func_190916_E() > 32) {
         var2 = 4;
      } else if (var1.func_190916_E() > 16) {
         var2 = 3;
      } else if (var1.func_190916_E() > 1) {
         var2 = 2;
      }

      return var2;
   }

   public void func_76986_a(EntityItem var1, double var2, double var4, double var6, float var8, float var9) {
      ItemStack var10 = var1.func_92059_d();
      int var11 = var10.func_190926_b() ? 187 : Item.func_150891_b(var10.func_77973_b()) + var10.func_77952_i();
      this.field_177079_e.setSeed((long)var11);
      boolean var12 = false;
      if (this.func_180548_c(var1)) {
         this.field_76990_c.field_78724_e.func_110581_b(this.func_110775_a(var1)).func_174936_b(false, false);
         var12 = true;
      }

      GlStateManager.func_179091_B();
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_179147_l();
      RenderHelper.func_74519_b();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179094_E();
      IBakedModel var13 = this.field_177080_a.func_184393_a(var10, var1.field_70170_p, (EntityLivingBase)null);
      int var14 = this.func_177077_a(var1, var2, var4, var6, var9, var13);
      float var15 = var13.func_177552_f().field_181699_o.field_178363_d.func_195899_a();
      float var16 = var13.func_177552_f().field_181699_o.field_178363_d.func_195900_b();
      float var17 = var13.func_177552_f().field_181699_o.field_178363_d.func_195902_c();
      boolean var18 = var13.func_177556_c();
      float var20;
      float var21;
      if (!var18) {
         float var19 = -0.0F * (float)(var14 - 1) * 0.5F * var15;
         var20 = -0.0F * (float)(var14 - 1) * 0.5F * var16;
         var21 = -0.09375F * (float)(var14 - 1) * 0.5F * var17;
         GlStateManager.func_179109_b(var19, var20, var21);
      }

      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      for(int var23 = 0; var23 < var14; ++var23) {
         if (var18) {
            GlStateManager.func_179094_E();
            if (var23 > 0) {
               var20 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var21 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var22 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.func_179109_b(var20, var21, var22);
            }

            var13.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GROUND);
            this.field_177080_a.func_180454_a(var10, var13);
            GlStateManager.func_179121_F();
         } else {
            GlStateManager.func_179094_E();
            if (var23 > 0) {
               var20 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var21 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               GlStateManager.func_179109_b(var20, var21, 0.0F);
            }

            var13.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GROUND);
            this.field_177080_a.func_180454_a(var10, var13);
            GlStateManager.func_179121_F();
            GlStateManager.func_179109_b(0.0F * var15, 0.0F * var16, 0.09375F * var17);
         }
      }

      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179121_F();
      GlStateManager.func_179101_C();
      GlStateManager.func_179084_k();
      this.func_180548_c(var1);
      if (var12) {
         this.field_76990_c.field_78724_e.func_110581_b(this.func_110775_a(var1)).func_174935_a();
      }

      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityItem var1) {
      return TextureMap.field_110575_b;
   }
}
