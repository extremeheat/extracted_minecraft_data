package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
   protected static final ResourceLocation field_177188_b = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   protected T field_177189_c;
   protected T field_177186_d;
   private final RendererLivingEntity<?> field_177190_a;
   private float field_177187_e = 1.0F;
   private float field_177184_f = 1.0F;
   private float field_177185_g = 1.0F;
   private float field_177192_h = 1.0F;
   private boolean field_177193_i;
   private static final Map<String, ResourceLocation> field_177191_j = Maps.newHashMap();

   public LayerArmorBase(RendererLivingEntity<?> var1) {
      super();
      this.field_177190_a = var1;
      this.func_177177_a();
   }

   public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.func_177182_a(var1, var2, var3, var4, var5, var6, var7, var8, 4);
      this.func_177182_a(var1, var2, var3, var4, var5, var6, var7, var8, 3);
      this.func_177182_a(var1, var2, var3, var4, var5, var6, var7, var8, 2);
      this.func_177182_a(var1, var2, var3, var4, var5, var6, var7, var8, 1);
   }

   public boolean func_177142_b() {
      return false;
   }

   private void func_177182_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      ItemStack var10 = this.func_177176_a(var1, var9);
      if (var10 != null && var10.func_77973_b() instanceof ItemArmor) {
         ItemArmor var11 = (ItemArmor)var10.func_77973_b();
         ModelBase var12 = this.func_177175_a(var9);
         var12.func_178686_a(this.field_177190_a.func_177087_b());
         var12.func_78086_a(var1, var2, var3, var4);
         this.func_177179_a(var12, var9);
         boolean var13 = this.func_177180_b(var9);
         this.field_177190_a.func_110776_a(this.func_177181_a(var11, var13));
         switch(var11.func_82812_d()) {
         case LEATHER:
            int var14 = var11.func_82814_b(var10);
            float var15 = (float)(var14 >> 16 & 255) / 255.0F;
            float var16 = (float)(var14 >> 8 & 255) / 255.0F;
            float var17 = (float)(var14 & 255) / 255.0F;
            GlStateManager.func_179131_c(this.field_177184_f * var15, this.field_177185_g * var16, this.field_177192_h * var17, this.field_177187_e);
            var12.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
            this.field_177190_a.func_110776_a(this.func_177178_a(var11, var13, "overlay"));
         case CHAIN:
         case IRON:
         case GOLD:
         case DIAMOND:
            GlStateManager.func_179131_c(this.field_177184_f, this.field_177185_g, this.field_177192_h, this.field_177187_e);
            var12.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
         default:
            if (!this.field_177193_i && var10.func_77948_v()) {
               this.func_177183_a(var1, var12, var2, var3, var4, var5, var6, var7, var8);
            }

         }
      }
   }

   public ItemStack func_177176_a(EntityLivingBase var1, int var2) {
      return var1.func_82169_q(var2 - 1);
   }

   public T func_177175_a(int var1) {
      return this.func_177180_b(var1) ? this.field_177189_c : this.field_177186_d;
   }

   private boolean func_177180_b(int var1) {
      return var1 == 2;
   }

   private void func_177183_a(EntityLivingBase var1, T var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = (float)var1.field_70173_aa + var5;
      this.field_177190_a.func_110776_a(field_177188_b);
      GlStateManager.func_179147_l();
      GlStateManager.func_179143_c(514);
      GlStateManager.func_179132_a(false);
      float var11 = 0.5F;
      GlStateManager.func_179131_c(var11, var11, var11, 1.0F);

      for(int var12 = 0; var12 < 2; ++var12) {
         GlStateManager.func_179140_f();
         GlStateManager.func_179112_b(768, 1);
         float var13 = 0.76F;
         GlStateManager.func_179131_c(0.5F * var13, 0.25F * var13, 0.8F * var13, 1.0F);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         float var14 = 0.33333334F;
         GlStateManager.func_179152_a(var14, var14, var14);
         GlStateManager.func_179114_b(30.0F - (float)var12 * 60.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179109_b(0.0F, var10 * (0.001F + (float)var12 * 0.003F) * 20.0F, 0.0F);
         GlStateManager.func_179128_n(5888);
         var2.func_78088_a(var1, var3, var4, var6, var7, var8, var9);
      }

      GlStateManager.func_179128_n(5890);
      GlStateManager.func_179096_D();
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179145_e();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179084_k();
   }

   private ResourceLocation func_177181_a(ItemArmor var1, boolean var2) {
      return this.func_177178_a(var1, var2, (String)null);
   }

   private ResourceLocation func_177178_a(ItemArmor var1, boolean var2, String var3) {
      String var4 = String.format("textures/models/armor/%s_layer_%d%s.png", var1.func_82812_d().func_179242_c(), var2 ? 2 : 1, var3 == null ? "" : String.format("_%s", var3));
      ResourceLocation var5 = (ResourceLocation)field_177191_j.get(var4);
      if (var5 == null) {
         var5 = new ResourceLocation(var4);
         field_177191_j.put(var4, var5);
      }

      return var5;
   }

   protected abstract void func_177177_a();

   protected abstract void func_177179_a(T var1, int var2);
}
