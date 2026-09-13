package net.minecraft.client.renderer.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor$ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

public class RenderPlayer extends RendererLivingEntity {
   private static final ResourceLocation field_110826_a = new ResourceLocation("textures/entity/steve.png");
   private ModelBiped field_77109_a = (ModelBiped)this.field_77045_g;
   private ModelBiped field_77108_b = new ModelBiped(1.0F);
   private ModelBiped field_77111_i = new ModelBiped(0.5F);

   public RenderPlayer() {
      super(new ModelBiped(0.0F), 0.5F);
   }

   protected int func_77032_a(AbstractClientPlayer var1, int var2, float var3) {
      ItemStack var4 = var1.field_71071_by.func_70440_f(3 - var2);
      if (var4 != null) {
         Item var5 = var4.func_77973_b();
         if (var5 instanceof ItemArmor) {
            ItemArmor var6 = (ItemArmor)var5;
            this.func_110776_a(RenderBiped.func_110857_a(var6, var2));
            ModelBiped var7 = var2 == 2 ? this.field_77111_i : this.field_77108_b;
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

   protected void func_82408_c(AbstractClientPlayer var1, int var2, float var3) {
      ItemStack var4 = var1.field_71071_by.func_70440_f(3 - var2);
      if (var4 != null) {
         Item var5 = var4.func_77973_b();
         if (var5 instanceof ItemArmor) {
            this.func_110776_a(RenderBiped.func_110858_a((ItemArmor)var5, var2, "overlay"));
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
         }
      }
   }

   public void func_76986_a(AbstractClientPlayer var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      ItemStack var10 = var1.field_71071_by.func_70448_g();
      this.field_77108_b.field_78120_m = this.field_77111_i.field_78120_m = this.field_77109_a.field_78120_m = var10 != null ? 1 : 0;
      if (var10 != null && var1.func_71052_bv() > 0) {
         EnumAction var11 = var10.func_77975_n();
         if (var11 == EnumAction.block) {
            this.field_77108_b.field_78120_m = this.field_77111_i.field_78120_m = this.field_77109_a.field_78120_m = 3;
         } else if (var11 == EnumAction.bow) {
            this.field_77108_b.field_78118_o = this.field_77111_i.field_78118_o = this.field_77109_a.field_78118_o = true;
         }
      }

      this.field_77108_b.field_78117_n = this.field_77111_i.field_78117_n = this.field_77109_a.field_78117_n = var1.func_70093_af();
      double var13 = var4 - (double)var1.field_70129_M;
      if (var1.func_70093_af() && !(var1 instanceof EntityPlayerSP)) {
         var13 -= 0.125;
      }

      super.func_76986_a((EntityLivingBase)var1, var2, var13, var6, var8, var9);
      this.field_77108_b.field_78118_o = this.field_77111_i.field_78118_o = this.field_77109_a.field_78118_o = false;
      this.field_77108_b.field_78117_n = this.field_77111_i.field_78117_n = this.field_77109_a.field_78117_n = false;
      this.field_77108_b.field_78120_m = this.field_77111_i.field_78120_m = this.field_77109_a.field_78120_m = 0;
   }

   protected ResourceLocation func_110775_a(AbstractClientPlayer var1) {
      return var1.func_110306_p();
   }

   protected void func_77029_c(AbstractClientPlayer var1, float var2) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      super.func_77029_c(var1, var2);
      super.func_85093_e(var1, var2);
      ItemStack var3 = var1.field_71071_by.func_70440_f(3);
      if (var3 != null) {
         GL11.glPushMatrix();
         this.field_77109_a.field_78116_c.func_78794_c(0.0625F);
         if (var3.func_77973_b() instanceof ItemBlock) {
            if (RenderBlocks.func_147739_a(Block.func_149634_a(var3.func_77973_b()).func_149645_b())) {
               float var4 = 0.625F;
               GL11.glTranslatef(0.0F, -0.25F, 0.0F);
               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
               GL11.glScalef(var4, -var4, -var4);
            }

            this.field_76990_c.field_78721_f.func_78443_a(var1, var3, 0);
         } else if (var3.func_77973_b() == Items.field_151144_bL) {
            float var20 = 1.0625F;
            GL11.glScalef(var20, -var20, -var20);
            GameProfile var5 = null;
            if (var3.func_77942_o()) {
               NBTTagCompound var6 = var3.func_77978_p();
               if (var6.func_150297_b("SkullOwner", 10)) {
                  var5 = NBTUtil.func_152459_a(var6.func_74775_l("SkullOwner"));
               } else if (var6.func_150297_b("SkullOwner", 8) && !StringUtils.func_151246_b(var6.func_74779_i("SkullOwner"))) {
                  var5 = new GameProfile(null, var6.func_74779_i("SkullOwner"));
               }
            }

            TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var3.func_77960_j(), var5);
         }

         GL11.glPopMatrix();
      }

      if (var1.func_70005_c_().equals("deadmau5") && var1.func_152123_o()) {
         this.func_110776_a(var1.func_110306_p());

         for(int var21 = 0; var21 < 2; ++var21) {
            float var23 = var1.field_70126_B
               + (var1.field_70177_z - var1.field_70126_B) * var2
               - (var1.field_70760_ar + (var1.field_70761_aq - var1.field_70760_ar) * var2);
            float var26 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var2;
            GL11.glPushMatrix();
            GL11.glRotatef(var23, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(var26, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.375F * (float)(var21 * 2 - 1), 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.375F, 0.0F);
            GL11.glRotatef(-var26, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-var23, 0.0F, 1.0F, 0.0F);
            float var7 = 1.3333334F;
            GL11.glScalef(var7, var7, var7);
            this.field_77109_a.func_78110_b(0.0625F);
            GL11.glPopMatrix();
         }
      }

      boolean var22 = var1.func_152122_n();
      if (var22 && !var1.func_82150_aj() && !var1.func_82238_cc()) {
         this.func_110776_a(var1.func_110303_q());
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, 0.0F, 0.125F);
         double var24 = var1.field_71091_bM
            + (var1.field_71094_bP - var1.field_71091_bM) * (double)var2
            - (var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var2);
         double var28 = var1.field_71096_bN
            + (var1.field_71095_bQ - var1.field_71096_bN) * (double)var2
            - (var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var2);
         double var9 = var1.field_71097_bO
            + (var1.field_71085_bR - var1.field_71097_bO) * (double)var2
            - (var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var2);
         float var11 = var1.field_70760_ar + (var1.field_70761_aq - var1.field_70760_ar) * var2;
         double var12 = (double)MathHelper.func_76126_a(var11 * 3.1415927F / 180.0F);
         double var14 = (double)(-MathHelper.func_76134_b(var11 * 3.1415927F / 180.0F));
         float var16 = (float)var28 * 10.0F;
         if (var16 < -6.0F) {
            var16 = -6.0F;
         }

         if (var16 > 32.0F) {
            var16 = 32.0F;
         }

         float var17 = (float)(var24 * var12 + var9 * var14) * 100.0F;
         float var18 = (float)(var24 * var14 - var9 * var12) * 100.0F;
         if (var17 < 0.0F) {
            var17 = 0.0F;
         }

         float var19 = var1.field_71107_bF + (var1.field_71109_bG - var1.field_71107_bF) * var2;
         var16 += MathHelper.func_76126_a((var1.field_70141_P + (var1.field_70140_Q - var1.field_70141_P) * var2) * 6.0F) * 32.0F * var19;
         if (var1.func_70093_af()) {
            var16 += 25.0F;
         }

         GL11.glRotatef(6.0F + var17 / 2.0F + var16, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(var18 / 2.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(-var18 / 2.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
         this.field_77109_a.func_78111_c(0.0625F);
         GL11.glPopMatrix();
      }

      ItemStack var25 = var1.field_71071_by.func_70448_g();
      if (var25 != null) {
         GL11.glPushMatrix();
         this.field_77109_a.field_78112_f.func_78794_c(0.0625F);
         GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
         if (var1.field_71104_cf != null) {
            var25 = new ItemStack(Items.field_151055_y);
         }

         EnumAction var27 = null;
         if (var1.func_71052_bv() > 0) {
            var27 = var25.func_77975_n();
         }

         if (var25.func_77973_b() instanceof ItemBlock && RenderBlocks.func_147739_a(Block.func_149634_a(var25.func_77973_b()).func_149645_b())) {
            float var32 = 0.5F;
            GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
            var32 *= 0.75F;
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-var32, -var32, var32);
         } else if (var25.func_77973_b() == Items.field_151031_f) {
            float var29 = 0.625F;
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(var29, -var29, var29);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else if (var25.func_77973_b().func_77662_d()) {
            float var30 = 0.625F;
            if (var25.func_77973_b().func_77629_n_()) {
               GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
               GL11.glTranslatef(0.0F, -0.125F, 0.0F);
            }

            if (var1.func_71052_bv() > 0 && var27 == EnumAction.block) {
               GL11.glTranslatef(0.05F, 0.0F, -0.1F);
               GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
               GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
               GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
            GL11.glScalef(var30, -var30, var30);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else {
            float var31 = 0.375F;
            GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
            GL11.glScalef(var31, var31, var31);
            GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
         }

         if (var25.func_77973_b().func_77623_v()) {
            for(int var35 = 0; var35 <= 1; ++var35) {
               int var36 = var25.func_77973_b().func_82790_a(var25, var35);
               float var38 = (float)(var36 >> 16 & 0xFF) / 255.0F;
               float var39 = (float)(var36 >> 8 & 0xFF) / 255.0F;
               float var40 = (float)(var36 & 0xFF) / 255.0F;
               GL11.glColor4f(var38, var39, var40, 1.0F);
               this.field_76990_c.field_78721_f.func_78443_a(var1, var25, var35);
            }
         } else {
            int var34 = var25.func_77973_b().func_82790_a(var25, 0);
            float var8 = (float)(var34 >> 16 & 0xFF) / 255.0F;
            float var37 = (float)(var34 >> 8 & 0xFF) / 255.0F;
            float var10 = (float)(var34 & 0xFF) / 255.0F;
            GL11.glColor4f(var8, var37, var10, 1.0F);
            this.field_76990_c.field_78721_f.func_78443_a(var1, var25, 0);
         }

         GL11.glPopMatrix();
      }
   }

   protected void func_77041_b(AbstractClientPlayer var1, float var2) {
      float var3 = 0.9375F;
      GL11.glScalef(var3, var3, var3);
   }

   protected void func_96449_a(AbstractClientPlayer var1, double var2, double var4, double var6, String var8, float var9, double var10) {
      if (var10 < 100.0) {
         Scoreboard var12 = var1.func_96123_co();
         ScoreObjective var13 = var12.func_96539_a(2);
         if (var13 != null) {
            Score var14 = var12.func_96529_a(var1.func_70005_c_(), var13);
            if (var1.func_70608_bn()) {
               this.func_147906_a(var1, var14.func_96652_c() + " " + var13.func_96678_d(), var2, var4 - 1.5, var6, 64);
            } else {
               this.func_147906_a(var1, var14.func_96652_c() + " " + var13.func_96678_d(), var2, var4, var6, 64);
            }

            var4 += (double)((float)this.func_76983_a().field_78288_b * 1.15F * var9);
         }
      }

      super.func_96449_a(var1, var2, var4, var6, var8, var9, var10);
   }

   public void func_82441_a(EntityPlayer var1) {
      float var2 = 1.0F;
      GL11.glColor3f(var2, var2, var2);
      this.field_77109_a.field_78095_p = 0.0F;
      this.field_77109_a.func_78087_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, var1);
      this.field_77109_a.field_78112_f.func_78785_a(0.0625F);
   }

   protected void func_77039_a(AbstractClientPlayer var1, double var2, double var4, double var6) {
      if (var1.func_70089_S() && var1.func_70608_bn()) {
         super.func_77039_a(var1, var2 + (double)var1.field_71079_bU, var4 + (double)var1.field_71082_cx, var6 + (double)var1.field_71089_bV);
      } else {
         super.func_77039_a(var1, var2, var4, var6);
      }
   }

   protected void func_77043_a(AbstractClientPlayer var1, float var2, float var3, float var4) {
      if (var1.func_70089_S() && var1.func_70608_bn()) {
         GL11.glRotatef(var1.func_71051_bG(), 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(this.func_77037_a(var1), 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         super.func_77043_a(var1, var2, var3, var4);
      }
   }
}
