package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
   protected static final ResourceLocation field_177188_b = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   protected T field_177189_c;
   protected T field_177186_d;
   private final RenderLivingBase<?> field_177190_a;
   private float field_177187_e = 1.0F;
   private float field_177184_f = 1.0F;
   private float field_177185_g = 1.0F;
   private float field_177192_h = 1.0F;
   private boolean field_177193_i;
   private static final Map<String, ResourceLocation> field_177191_j = Maps.newHashMap();

   public LayerArmorBase(RenderLivingBase<?> var1) {
      super();
      this.field_177190_a = var1;
      this.func_177177_a();
   }

   public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.func_188361_a(var1, var2, var3, var4, var5, var6, var7, var8, EntityEquipmentSlot.CHEST);
      this.func_188361_a(var1, var2, var3, var4, var5, var6, var7, var8, EntityEquipmentSlot.LEGS);
      this.func_188361_a(var1, var2, var3, var4, var5, var6, var7, var8, EntityEquipmentSlot.FEET);
      this.func_188361_a(var1, var2, var3, var4, var5, var6, var7, var8, EntityEquipmentSlot.HEAD);
   }

   public boolean func_177142_b() {
      return false;
   }

   private void func_188361_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, EntityEquipmentSlot var9) {
      ItemStack var10 = var1.func_184582_a(var9);
      if (var10.func_77973_b() instanceof ItemArmor) {
         ItemArmor var11 = (ItemArmor)var10.func_77973_b();
         if (var11.func_185083_B_() == var9) {
            ModelBase var12 = this.func_188360_a(var9);
            var12.func_178686_a(this.field_177190_a.func_177087_b());
            var12.func_78086_a(var1, var2, var3, var4);
            this.func_188359_a(var12, var9);
            boolean var13 = this.func_188363_b(var9);
            this.field_177190_a.func_110776_a(this.func_177181_a(var11, var13));
            if (var11 instanceof ItemArmorDyeable) {
               int var14 = ((ItemArmorDyeable)var11).func_200886_f(var10);
               float var15 = (float)(var14 >> 16 & 255) / 255.0F;
               float var16 = (float)(var14 >> 8 & 255) / 255.0F;
               float var17 = (float)(var14 & 255) / 255.0F;
               GlStateManager.func_179131_c(this.field_177184_f * var15, this.field_177185_g * var16, this.field_177192_h * var17, this.field_177187_e);
               var12.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
               this.field_177190_a.func_110776_a(this.func_177178_a(var11, var13, "overlay"));
            }

            GlStateManager.func_179131_c(this.field_177184_f, this.field_177185_g, this.field_177192_h, this.field_177187_e);
            var12.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
            if (!this.field_177193_i && var10.func_77948_v()) {
               func_188364_a(this.field_177190_a, var1, var12, var2, var3, var4, var5, var6, var7, var8);
            }

         }
      }
   }

   public T func_188360_a(EntityEquipmentSlot var1) {
      return this.func_188363_b(var1) ? this.field_177189_c : this.field_177186_d;
   }

   private boolean func_188363_b(EntityEquipmentSlot var1) {
      return var1 == EntityEquipmentSlot.LEGS;
   }

   public static void func_188364_a(RenderLivingBase<?> var0, EntityLivingBase var1, ModelBase var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = (float)var1.field_70173_aa + var5;
      var0.func_110776_a(field_177188_b);
      Minecraft.func_71410_x().field_71460_t.func_191514_d(true);
      GlStateManager.func_179147_l();
      GlStateManager.func_179143_c(514);
      GlStateManager.func_179132_a(false);
      float var11 = 0.5F;
      GlStateManager.func_179131_c(0.5F, 0.5F, 0.5F, 1.0F);

      for(int var12 = 0; var12 < 2; ++var12) {
         GlStateManager.func_179140_f();
         GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
         float var13 = 0.76F;
         GlStateManager.func_179131_c(0.38F, 0.19F, 0.608F, 1.0F);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         float var14 = 0.33333334F;
         GlStateManager.func_179152_a(0.33333334F, 0.33333334F, 0.33333334F);
         GlStateManager.func_179114_b(30.0F - (float)var12 * 60.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179109_b(0.0F, var10 * (0.001F + (float)var12 * 0.003F) * 20.0F, 0.0F);
         GlStateManager.func_179128_n(5888);
         var2.func_78088_a(var1, var3, var4, var6, var7, var8, var9);
         GlStateManager.func_187401_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      }

      GlStateManager.func_179128_n(5890);
      GlStateManager.func_179096_D();
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179145_e();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179084_k();
      Minecraft.func_71410_x().field_71460_t.func_191514_d(false);
   }

   private ResourceLocation func_177181_a(ItemArmor var1, boolean var2) {
      return this.func_177178_a(var1, var2, (String)null);
   }

   private ResourceLocation func_177178_a(ItemArmor var1, boolean var2, @Nullable String var3) {
      String var4 = "textures/models/armor/" + var1.func_200880_d().func_200897_d() + "_layer_" + (var2 ? 2 : 1) + (var3 == null ? "" : "_" + var3) + ".png";
      return (ResourceLocation)field_177191_j.computeIfAbsent(var4, ResourceLocation::new);
   }

   protected abstract void func_177177_a();

   protected abstract void func_188359_a(T var1, EntityEquipmentSlot var2);
}
