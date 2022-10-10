package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.client.renderer.entity.model.ModelSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class LayerSheepWool implements LayerRenderer<EntitySheep> {
   private static final ResourceLocation field_177165_a = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final RenderSheep field_177163_b;
   private final ModelSheepWool field_177164_c = new ModelSheepWool();

   public LayerSheepWool(RenderSheep var1) {
      super();
      this.field_177163_b = var1;
   }

   public void func_177141_a(EntitySheep var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.func_70892_o() && !var1.func_82150_aj()) {
         this.field_177163_b.func_110776_a(field_177165_a);
         if (var1.func_145818_k_() && "jeb_".equals(var1.func_200200_C_().func_150261_e())) {
            boolean var17 = true;
            int var10 = var1.field_70173_aa / 25 + var1.func_145782_y();
            int var11 = EnumDyeColor.values().length;
            int var12 = var10 % var11;
            int var13 = (var10 + 1) % var11;
            float var14 = ((float)(var1.field_70173_aa % 25) + var4) / 25.0F;
            float[] var15 = EntitySheep.func_175513_a(EnumDyeColor.func_196056_a(var12));
            float[] var16 = EntitySheep.func_175513_a(EnumDyeColor.func_196056_a(var13));
            GlStateManager.func_179124_c(var15[0] * (1.0F - var14) + var16[0] * var14, var15[1] * (1.0F - var14) + var16[1] * var14, var15[2] * (1.0F - var14) + var16[2] * var14);
         } else {
            float[] var9 = EntitySheep.func_175513_a(var1.func_175509_cj());
            GlStateManager.func_179124_c(var9[0], var9[1], var9[2]);
         }

         this.field_177164_c.func_178686_a(this.field_177163_b.func_177087_b());
         this.field_177164_c.func_78086_a(var1, var2, var3, var4);
         this.field_177164_c.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean func_177142_b() {
      return true;
   }
}
