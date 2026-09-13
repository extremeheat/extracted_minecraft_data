package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor$ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

public class RenderBiped extends RenderLiving {
   protected ModelBiped field_77071_a;
   protected float field_77070_b;
   protected ModelBiped field_82423_g;
   protected ModelBiped field_82425_h;
   private static final Map field_110859_k = Maps.newHashMap();
   private static final String[] field_82424_k = new String[]{"leather", "chainmail", "iron", "diamond", "gold"};

   public RenderBiped(ModelBiped var1, float var2) {
      this(var1, var2, 1.0F);
   }

   public RenderBiped(ModelBiped var1, float var2, float var3) {
      super(var1, var2);
      this.field_77071_a = var1;
      this.field_77070_b = var3;
      this.func_82421_b();
   }

   protected void func_82421_b() {
      this.field_82423_g = new ModelBiped(1.0F);
      this.field_82425_h = new ModelBiped(0.5F);
   }

   public static ResourceLocation func_110857_a(ItemArmor var0, int var1) {
      return func_110858_a(var0, var1, null);
   }

   public static ResourceLocation func_110858_a(ItemArmor var0, int var1, String var2) {
      String var3 = String.format(
         "textures/models/armor/%s_layer_%d%s.png", field_82424_k[var0.field_77880_c], var1 == 2 ? 2 : 1, var2 == null ? "" : String.format("_%s", var2)
      );
      ResourceLocation var4 = (ResourceLocation)field_110859_k.get(var3);
      if (var4 == null) {
         var4 = new ResourceLocation(var3);
         field_110859_k.put(var3, var4);
      }

      return var4;
   }

   protected int func_77032_a(EntityLiving var1, int var2, float var3) {
      ItemStack var4 = var1.func_130225_q(3 - var2);
      if (var4 != null) {
         Item var5 = var4.func_77973_b();
         if (var5 instanceof ItemArmor) {
            ItemArmor var6 = (ItemArmor)var5;
            this.func_110776_a(func_110857_a(var6, var2));
            ModelBiped var7 = var2 == 2 ? this.field_82425_h : this.field_82423_g;
            var7.field_78116_c.field_78806_j = var2 == 0;
            var7.field_78114_d.field_78806_j = var2 == 0;
            var7.field_78115_e.field_78806_j = var2 == 1 || var2 == 2;
            var7.field_78112_f.field_78806_j = var2 == 1;
            var7.field_78113_g.field_78806_j = var2 == 1;
            var7.field_78123_h.field_78806_j = var2 == 2 || var2 == 3;
            var7.field_78124_i.field_78806_j = var2 == 2 || var2 == 3;
            this.func_77042_a(var7);
            var7.field_78095_p = this.field_77045_g.field_78095_p;
            var7.field_78093_q = this.field_77045_g.field_78093_q;
            var7.field_78091_s = this.field_77045_g.field_78091_s;
            if (var6.func_82812_d() == ItemArmor$ArmorMaterial.CLOTH) {
               int var8 = var6.func_82814_b(var4);
               float var9 = (float)(var8 >> 16 & 0xFF) / 255.0F;
               float var10 = (float)(var8 >> 8 & 0xFF) / 255.0F;
               float var11 = (float)(var8 & 0xFF) / 255.0F;
               GL11.glColor3f(var9, var10, var11);
               if (var4.func_77948_v()) {
                  return 31;
               }

               return 16;
            }

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            if (var4.func_77948_v()) {
               return 15;
            }

            return 1;
         }
      }

      return -1;
   }

   protected void func_82408_c(EntityLiving var1, int var2, float var3) {
      ItemStack var4 = var1.func_130225_q(3 - var2);
      if (var4 != null) {
         Item var5 = var4.func_77973_b();
         if (var5 instanceof ItemArmor) {
            this.func_110776_a(func_110858_a((ItemArmor)var5, var2, "overlay"));
            float var6 = 1.0F;
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void func_76986_a(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      ItemStack var10 = var1.func_70694_bm();
      this.func_82420_a(var1, var10);
      double var11 = var4 - (double)var1.field_70129_M;
      if (var1.func_70093_af()) {
         var11 -= 0.125;
      }

      super.func_76986_a(var1, var2, var11, var6, var8, var9);
      this.field_82423_g.field_78118_o = this.field_82425_h.field_78118_o = this.field_77071_a.field_78118_o = false;
      this.field_82423_g.field_78117_n = this.field_82425_h.field_78117_n = this.field_77071_a.field_78117_n = false;
      this.field_82423_g.field_78120_m = this.field_82425_h.field_78120_m = this.field_77071_a.field_78120_m = 0;
   }

   protected ResourceLocation func_110775_a(EntityLiving var1) {
      return null;
   }

   protected void func_82420_a(EntityLiving var1, ItemStack var2) {
      this.field_82423_g.field_78120_m = this.field_82425_h.field_78120_m = this.field_77071_a.field_78120_m = var2 != null ? 1 : 0;
      this.field_82423_g.field_78117_n = this.field_82425_h.field_78117_n = this.field_77071_a.field_78117_n = var1.func_70093_af();
   }

   protected void func_77029_c(EntityLiving var1, float var2) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      super.func_77029_c(var1, var2);
      ItemStack var3 = var1.func_70694_bm();
      ItemStack var4 = var1.func_130225_q(3);
      if (var4 != null) {
         GL11.glPushMatrix();
         this.field_77071_a.field_78116_c.func_78794_c(0.0625F);
         Item var5 = var4.func_77973_b();
         if (var5 instanceof ItemBlock) {
            if (RenderBlocks.func_147739_a(Block.func_149634_a(var5).func_149645_b())) {
               float var6 = 0.625F;
               GL11.glTranslatef(0.0F, -0.25F, 0.0F);
               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
               GL11.glScalef(var6, -var6, -var6);
            }

            this.field_76990_c.field_78721_f.func_78443_a(var1, var4, 0);
         } else if (var5 == Items.field_151144_bL) {
            float var12 = 1.0625F;
            GL11.glScalef(var12, -var12, -var12);
            GameProfile var7 = null;
            if (var4.func_77942_o()) {
               NBTTagCompound var8 = var4.func_77978_p();
               if (var8.func_150297_b("SkullOwner", 10)) {
                  var7 = NBTUtil.func_152459_a(var8.func_74775_l("SkullOwner"));
               } else if (var8.func_150297_b("SkullOwner", 8) && !StringUtils.func_151246_b(var8.func_74779_i("SkullOwner"))) {
                  var7 = new GameProfile(null, var8.func_74779_i("SkullOwner"));
               }
            }

            TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var4.func_77960_j(), var7);
         }

         GL11.glPopMatrix();
      }

      if (var3 != null && var3.func_77973_b() != null) {
         Item var11 = var3.func_77973_b();
         GL11.glPushMatrix();
         if (this.field_77045_g.field_78091_s) {
            float var13 = 0.5F;
            GL11.glTranslatef(0.0F, 0.625F, 0.0F);
            GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
            GL11.glScalef(var13, var13, var13);
         }

         this.field_77071_a.field_78112_f.func_78794_c(0.0625F);
         GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
         if (var11 instanceof ItemBlock && RenderBlocks.func_147739_a(Block.func_149634_a(var11).func_149645_b())) {
            float var17 = 0.5F;
            GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
            var17 *= 0.75F;
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-var17, -var17, var17);
         } else if (var11 == Items.field_151031_f) {
            float var14 = 0.625F;
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(var14, -var14, var14);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else if (var11.func_77662_d()) {
            float var15 = 0.625F;
            if (var11.func_77629_n_()) {
               GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
               GL11.glTranslatef(0.0F, -0.125F, 0.0F);
            }

            this.func_82422_c();
            GL11.glScalef(var15, -var15, var15);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else {
            float var16 = 0.375F;
            GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
            GL11.glScalef(var16, var16, var16);
            GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
         }

         if (var3.func_77973_b().func_77623_v()) {
            for(int var20 = 0; var20 <= 1; ++var20) {
               int var22 = var3.func_77973_b().func_82790_a(var3, var20);
               float var24 = (float)(var22 >> 16 & 0xFF) / 255.0F;
               float var25 = (float)(var22 >> 8 & 0xFF) / 255.0F;
               float var10 = (float)(var22 & 0xFF) / 255.0F;
               GL11.glColor4f(var24, var25, var10, 1.0F);
               this.field_76990_c.field_78721_f.func_78443_a(var1, var3, var20);
            }
         } else {
            int var19 = var3.func_77973_b().func_82790_a(var3, 0);
            float var21 = (float)(var19 >> 16 & 0xFF) / 255.0F;
            float var23 = (float)(var19 >> 8 & 0xFF) / 255.0F;
            float var9 = (float)(var19 & 0xFF) / 255.0F;
            GL11.glColor4f(var21, var23, var9, 1.0F);
            this.field_76990_c.field_78721_f.func_78443_a(var1, var3, 0);
         }

         GL11.glPopMatrix();
      }
   }

   protected void func_82422_c() {
      GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
   }
}
