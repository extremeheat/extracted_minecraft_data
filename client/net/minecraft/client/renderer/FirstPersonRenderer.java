package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;

public class FirstPersonRenderer {
   private static final ResourceLocation field_110931_c = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation field_110929_d = new ResourceLocation("textures/misc/underwater.png");
   private final Minecraft field_78455_a;
   private ItemStack field_187467_d;
   private ItemStack field_187468_e;
   private float field_187469_f;
   private float field_187470_g;
   private float field_187471_h;
   private float field_187472_i;
   private final RenderManager field_178111_g;
   private final ItemRenderer field_178112_h;

   public FirstPersonRenderer(Minecraft var1) {
      super();
      this.field_187467_d = ItemStack.field_190927_a;
      this.field_187468_e = ItemStack.field_190927_a;
      this.field_78455_a = var1;
      this.field_178111_g = var1.func_175598_ae();
      this.field_178112_h = var1.func_175599_af();
   }

   public void func_178099_a(EntityLivingBase var1, ItemStack var2, ItemCameraTransforms.TransformType var3) {
      this.func_187462_a(var1, var2, var3, false);
   }

   public void func_187462_a(EntityLivingBase var1, ItemStack var2, ItemCameraTransforms.TransformType var3, boolean var4) {
      if (!var2.func_190926_b()) {
         Item var5 = var2.func_77973_b();
         Block var6 = Block.func_149634_a(var5);
         GlStateManager.func_179094_E();
         boolean var7 = this.field_178112_h.func_175050_a(var2) && var6.func_180664_k() == BlockRenderLayer.TRANSLUCENT;
         if (var7) {
            GlStateManager.func_179132_a(false);
         }

         this.field_178112_h.func_184392_a(var2, var1, var3, var4);
         if (var7) {
            GlStateManager.func_179132_a(true);
         }

         GlStateManager.func_179121_F();
      }
   }

   private void func_178101_a(float var1, float var2) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179114_b(var1, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(var2, 0.0F, 1.0F, 0.0F);
      RenderHelper.func_74519_b();
      GlStateManager.func_179121_F();
   }

   private void func_187464_b() {
      EntityPlayerSP var1 = this.field_78455_a.field_71439_g;
      int var2 = this.field_78455_a.field_71441_e.func_175626_b(new BlockPos(var1.field_70165_t, var1.field_70163_u + (double)var1.func_70047_e(), var1.field_70161_v), 0);
      float var3 = (float)(var2 & '\uffff');
      float var4 = (float)(var2 >> 16);
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, var3, var4);
   }

   private void func_187458_c(float var1) {
      EntityPlayerSP var2 = this.field_78455_a.field_71439_g;
      float var3 = var2.field_71164_i + (var2.field_71155_g - var2.field_71164_i) * var1;
      float var4 = var2.field_71163_h + (var2.field_71154_f - var2.field_71163_h) * var1;
      GlStateManager.func_179114_b((var2.field_70125_A - var3) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b((var2.field_70177_z - var4) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float func_178100_c(float var1) {
      float var2 = 1.0F - var1 / 45.0F + 0.1F;
      var2 = MathHelper.func_76131_a(var2, 0.0F, 1.0F);
      var2 = -MathHelper.func_76134_b(var2 * 3.1415927F) * 0.5F + 0.5F;
      return var2;
   }

   private void func_187466_c() {
      if (!this.field_78455_a.field_71439_g.func_82150_aj()) {
         GlStateManager.func_179129_p();
         GlStateManager.func_179094_E();
         GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
         this.func_187455_a(EnumHandSide.RIGHT);
         this.func_187455_a(EnumHandSide.LEFT);
         GlStateManager.func_179121_F();
         GlStateManager.func_179089_o();
      }
   }

   private void func_187455_a(EnumHandSide var1) {
      this.field_78455_a.func_110434_K().func_110577_a(this.field_78455_a.field_71439_g.func_110306_p());
      Render var2 = this.field_178111_g.func_78713_a(this.field_78455_a.field_71439_g);
      RenderPlayer var3 = (RenderPlayer)var2;
      GlStateManager.func_179094_E();
      float var4 = var1 == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.func_179114_b(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(var4 * -41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179109_b(var4 * 0.3F, -1.1F, 0.45F);
      if (var1 == EnumHandSide.RIGHT) {
         var3.func_177138_b(this.field_78455_a.field_71439_g);
      } else {
         var3.func_177139_c(this.field_78455_a.field_71439_g);
      }

      GlStateManager.func_179121_F();
   }

   private void func_187465_a(float var1, EnumHandSide var2, float var3, ItemStack var4) {
      float var5 = var2 == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.func_179109_b(var5 * 0.125F, -0.125F, 0.0F);
      if (!this.field_78455_a.field_71439_g.func_82150_aj()) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179114_b(var5 * 10.0F, 0.0F, 0.0F, 1.0F);
         this.func_187456_a(var1, var3, var2);
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(var5 * 0.51F, -0.08F + var1 * -1.2F, -0.75F);
      float var6 = MathHelper.func_76129_c(var3);
      float var7 = MathHelper.func_76126_a(var6 * 3.1415927F);
      float var8 = -0.5F * var7;
      float var9 = 0.4F * MathHelper.func_76126_a(var6 * 6.2831855F);
      float var10 = -0.3F * MathHelper.func_76126_a(var3 * 3.1415927F);
      GlStateManager.func_179109_b(var5 * var8, var9 - 0.3F * var7, var10);
      GlStateManager.func_179114_b(var7 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(var5 * var7 * -30.0F, 0.0F, 1.0F, 0.0F);
      this.func_187461_a(var4);
      GlStateManager.func_179121_F();
   }

   private void func_187463_a(float var1, float var2, float var3) {
      float var4 = MathHelper.func_76129_c(var3);
      float var5 = -0.2F * MathHelper.func_76126_a(var3 * 3.1415927F);
      float var6 = -0.4F * MathHelper.func_76126_a(var4 * 3.1415927F);
      GlStateManager.func_179109_b(0.0F, -var5 / 2.0F, var6);
      float var7 = this.func_178100_c(var1);
      GlStateManager.func_179109_b(0.0F, 0.04F + var2 * -1.2F + var7 * -0.5F, -0.72F);
      GlStateManager.func_179114_b(var7 * -85.0F, 1.0F, 0.0F, 0.0F);
      this.func_187466_c();
      float var8 = MathHelper.func_76126_a(var4 * 3.1415927F);
      GlStateManager.func_179114_b(var8 * 20.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      this.func_187461_a(this.field_187467_d);
   }

   private void func_187461_a(ItemStack var1) {
      GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179152_a(0.38F, 0.38F, 0.38F);
      GlStateManager.func_179140_f();
      this.field_78455_a.func_110434_K().func_110577_a(field_110931_c);
      Tessellator var2 = Tessellator.func_178181_a();
      BufferBuilder var3 = var2.func_178180_c();
      GlStateManager.func_179109_b(-0.5F, -0.5F, 0.0F);
      GlStateManager.func_179152_a(0.0078125F, 0.0078125F, 0.0078125F);
      var3.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var3.func_181662_b(-7.0D, 135.0D, 0.0D).func_187315_a(0.0D, 1.0D).func_181675_d();
      var3.func_181662_b(135.0D, 135.0D, 0.0D).func_187315_a(1.0D, 1.0D).func_181675_d();
      var3.func_181662_b(135.0D, -7.0D, 0.0D).func_187315_a(1.0D, 0.0D).func_181675_d();
      var3.func_181662_b(-7.0D, -7.0D, 0.0D).func_187315_a(0.0D, 0.0D).func_181675_d();
      var2.func_78381_a();
      MapData var4 = ItemMap.func_195950_a(var1, this.field_78455_a.field_71441_e);
      if (var4 != null) {
         this.field_78455_a.field_71460_t.func_147701_i().func_148250_a(var4, false);
      }

      GlStateManager.func_179145_e();
   }

   private void func_187456_a(float var1, float var2, EnumHandSide var3) {
      boolean var4 = var3 != EnumHandSide.LEFT;
      float var5 = var4 ? 1.0F : -1.0F;
      float var6 = MathHelper.func_76129_c(var2);
      float var7 = -0.3F * MathHelper.func_76126_a(var6 * 3.1415927F);
      float var8 = 0.4F * MathHelper.func_76126_a(var6 * 6.2831855F);
      float var9 = -0.4F * MathHelper.func_76126_a(var2 * 3.1415927F);
      GlStateManager.func_179109_b(var5 * (var7 + 0.64000005F), var8 + -0.6F + var1 * -0.6F, var9 + -0.71999997F);
      GlStateManager.func_179114_b(var5 * 45.0F, 0.0F, 1.0F, 0.0F);
      float var10 = MathHelper.func_76126_a(var2 * var2 * 3.1415927F);
      float var11 = MathHelper.func_76126_a(var6 * 3.1415927F);
      GlStateManager.func_179114_b(var5 * var11 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var5 * var10 * -20.0F, 0.0F, 0.0F, 1.0F);
      EntityPlayerSP var12 = this.field_78455_a.field_71439_g;
      this.field_78455_a.func_110434_K().func_110577_a(var12.func_110306_p());
      GlStateManager.func_179109_b(var5 * -1.0F, 3.6F, 3.5F);
      GlStateManager.func_179114_b(var5 * 120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(var5 * -135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(var5 * 5.6F, 0.0F, 0.0F);
      RenderPlayer var13 = (RenderPlayer)this.field_178111_g.func_78713_a(var12);
      GlStateManager.func_179129_p();
      if (var4) {
         var13.func_177138_b(var12);
      } else {
         var13.func_177139_c(var12);
      }

      GlStateManager.func_179089_o();
   }

   private void func_187454_a(float var1, EnumHandSide var2, ItemStack var3) {
      float var4 = (float)this.field_78455_a.field_71439_g.func_184605_cv() - var1 + 1.0F;
      float var5 = var4 / (float)var3.func_77988_m();
      float var6;
      if (var5 < 0.8F) {
         var6 = MathHelper.func_76135_e(MathHelper.func_76134_b(var4 / 4.0F * 3.1415927F) * 0.1F);
         GlStateManager.func_179109_b(0.0F, var6, 0.0F);
      }

      var6 = 1.0F - (float)Math.pow((double)var5, 27.0D);
      int var7 = var2 == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.func_179109_b(var6 * 0.6F * (float)var7, var6 * -0.5F, var6 * 0.0F);
      GlStateManager.func_179114_b((float)var7 * var6 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b((float)var7 * var6 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void func_187453_a(EnumHandSide var1, float var2) {
      int var3 = var1 == EnumHandSide.RIGHT ? 1 : -1;
      float var4 = MathHelper.func_76126_a(var2 * var2 * 3.1415927F);
      GlStateManager.func_179114_b((float)var3 * (45.0F + var4 * -20.0F), 0.0F, 1.0F, 0.0F);
      float var5 = MathHelper.func_76126_a(MathHelper.func_76129_c(var2) * 3.1415927F);
      GlStateManager.func_179114_b((float)var3 * var5 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(var5 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b((float)var3 * -45.0F, 0.0F, 1.0F, 0.0F);
   }

   private void func_187459_b(EnumHandSide var1, float var2) {
      int var3 = var1 == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.func_179109_b((float)var3 * 0.56F, -0.52F + var2 * -0.6F, -0.72F);
   }

   public void func_78440_a(float var1) {
      EntityPlayerSP var2 = this.field_78455_a.field_71439_g;
      float var3 = var2.func_70678_g(var1);
      EnumHand var4 = (EnumHand)MoreObjects.firstNonNull(var2.field_184622_au, EnumHand.MAIN_HAND);
      float var5 = var2.field_70127_C + (var2.field_70125_A - var2.field_70127_C) * var1;
      float var6 = var2.field_70126_B + (var2.field_70177_z - var2.field_70126_B) * var1;
      boolean var7 = true;
      boolean var8 = true;
      if (var2.func_184587_cr()) {
         ItemStack var9 = var2.func_184607_cu();
         if (var9.func_77973_b() == Items.field_151031_f) {
            var7 = var2.func_184600_cs() == EnumHand.MAIN_HAND;
            var8 = !var7;
         }
      }

      this.func_178101_a(var5, var6);
      this.func_187464_b();
      this.func_187458_c(var1);
      GlStateManager.func_179091_B();
      float var10;
      float var11;
      if (var7) {
         var11 = var4 == EnumHand.MAIN_HAND ? var3 : 0.0F;
         var10 = 1.0F - (this.field_187470_g + (this.field_187469_f - this.field_187470_g) * var1);
         this.func_187457_a(var2, var1, var5, EnumHand.MAIN_HAND, var11, this.field_187467_d, var10);
      }

      if (var8) {
         var11 = var4 == EnumHand.OFF_HAND ? var3 : 0.0F;
         var10 = 1.0F - (this.field_187472_i + (this.field_187471_h - this.field_187472_i) * var1);
         this.func_187457_a(var2, var1, var5, EnumHand.OFF_HAND, var11, this.field_187468_e, var10);
      }

      GlStateManager.func_179101_C();
      RenderHelper.func_74518_a();
   }

   public void func_187457_a(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7) {
      boolean var8 = var4 == EnumHand.MAIN_HAND;
      EnumHandSide var9 = var8 ? var1.func_184591_cq() : var1.func_184591_cq().func_188468_a();
      GlStateManager.func_179094_E();
      if (var6.func_190926_b()) {
         if (var8 && !var1.func_82150_aj()) {
            this.func_187456_a(var7, var5, var9);
         }
      } else if (var6.func_77973_b() == Items.field_151098_aY) {
         if (var8 && this.field_187468_e.func_190926_b()) {
            this.func_187463_a(var3, var7, var5);
         } else {
            this.func_187465_a(var7, var9, var5, var6);
         }
      } else {
         boolean var10 = var9 == EnumHandSide.RIGHT;
         int var11;
         float var12;
         float var13;
         if (var1.func_184587_cr() && var1.func_184605_cv() > 0 && var1.func_184600_cs() == var4) {
            var11 = var10 ? 1 : -1;
            float var15;
            float var16;
            float var18;
            switch(var6.func_77975_n()) {
            case NONE:
               this.func_187459_b(var9, var7);
               break;
            case EAT:
            case DRINK:
               this.func_187454_a(var2, var9, var6);
               this.func_187459_b(var9, var7);
               break;
            case BLOCK:
               this.func_187459_b(var9, var7);
               break;
            case BOW:
               this.func_187459_b(var9, var7);
               GlStateManager.func_179109_b((float)var11 * -0.2785682F, 0.18344387F, 0.15731531F);
               GlStateManager.func_179114_b(-13.935F, 1.0F, 0.0F, 0.0F);
               GlStateManager.func_179114_b((float)var11 * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b((float)var11 * -9.785F, 0.0F, 0.0F, 1.0F);
               var12 = (float)var6.func_77988_m() - ((float)this.field_78455_a.field_71439_g.func_184605_cv() - var2 + 1.0F);
               var13 = var12 / 20.0F;
               var13 = (var13 * var13 + var13 * 2.0F) / 3.0F;
               if (var13 > 1.0F) {
                  var13 = 1.0F;
               }

               if (var13 > 0.1F) {
                  var18 = MathHelper.func_76126_a((var12 - 0.1F) * 1.3F);
                  var15 = var13 - 0.1F;
                  var16 = var18 * var15;
                  GlStateManager.func_179109_b(var16 * 0.0F, var16 * 0.004F, var16 * 0.0F);
               }

               GlStateManager.func_179109_b(var13 * 0.0F, var13 * 0.0F, var13 * 0.04F);
               GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F + var13 * 0.2F);
               GlStateManager.func_179114_b((float)var11 * 45.0F, 0.0F, -1.0F, 0.0F);
               break;
            case SPEAR:
               this.func_187459_b(var9, var7);
               GlStateManager.func_179109_b((float)var11 * -0.5F, 0.7F, 0.1F);
               GlStateManager.func_179114_b(-55.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.func_179114_b((float)var11 * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b((float)var11 * -9.785F, 0.0F, 0.0F, 1.0F);
               var12 = (float)var6.func_77988_m() - ((float)this.field_78455_a.field_71439_g.func_184605_cv() - var2 + 1.0F);
               var13 = var12 / 10.0F;
               if (var13 > 1.0F) {
                  var13 = 1.0F;
               }

               if (var13 > 0.1F) {
                  var18 = MathHelper.func_76126_a((var12 - 0.1F) * 1.3F);
                  var15 = var13 - 0.1F;
                  var16 = var18 * var15;
                  GlStateManager.func_179109_b(var16 * 0.0F, var16 * 0.004F, var16 * 0.0F);
               }

               GlStateManager.func_179109_b(0.0F, 0.0F, var13 * 0.2F);
               GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F + var13 * 0.2F);
               GlStateManager.func_179114_b((float)var11 * 45.0F, 0.0F, -1.0F, 0.0F);
            }
         } else if (var1.func_204805_cN()) {
            this.func_187459_b(var9, var7);
            var11 = var10 ? 1 : -1;
            GlStateManager.func_179109_b((float)var11 * -0.4F, 0.8F, 0.3F);
            GlStateManager.func_179114_b((float)var11 * 65.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b((float)var11 * -85.0F, 0.0F, 0.0F, 1.0F);
         } else {
            float var17 = -0.4F * MathHelper.func_76126_a(MathHelper.func_76129_c(var5) * 3.1415927F);
            var12 = 0.2F * MathHelper.func_76126_a(MathHelper.func_76129_c(var5) * 6.2831855F);
            var13 = -0.2F * MathHelper.func_76126_a(var5 * 3.1415927F);
            int var14 = var10 ? 1 : -1;
            GlStateManager.func_179109_b((float)var14 * var17, var12, var13);
            this.func_187459_b(var9, var7);
            this.func_187453_a(var9, var5);
         }

         this.func_187462_a(var1, var6, var10 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !var10);
      }

      GlStateManager.func_179121_F();
   }

   public void func_78447_b(float var1) {
      GlStateManager.func_179118_c();
      if (this.field_78455_a.field_71439_g.func_70094_T()) {
         IBlockState var2 = this.field_78455_a.field_71441_e.func_180495_p(new BlockPos(this.field_78455_a.field_71439_g));
         EntityPlayerSP var3 = this.field_78455_a.field_71439_g;

         for(int var4 = 0; var4 < 8; ++var4) {
            double var5 = var3.field_70165_t + (double)(((float)((var4 >> 0) % 2) - 0.5F) * var3.field_70130_N * 0.8F);
            double var7 = var3.field_70163_u + (double)(((float)((var4 >> 1) % 2) - 0.5F) * 0.1F);
            double var9 = var3.field_70161_v + (double)(((float)((var4 >> 2) % 2) - 0.5F) * var3.field_70130_N * 0.8F);
            BlockPos var11 = new BlockPos(var5, var7 + (double)var3.func_70047_e(), var9);
            IBlockState var12 = this.field_78455_a.field_71441_e.func_180495_p(var11);
            if (var12.func_191058_s()) {
               var2 = var12;
            }
         }

         if (var2.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
            this.func_178108_a(this.field_78455_a.func_175602_ab().func_175023_a().func_178122_a(var2));
         }
      }

      if (!this.field_78455_a.field_71439_g.func_175149_v()) {
         if (this.field_78455_a.field_71439_g.func_208600_a(FluidTags.field_206959_a)) {
            this.func_78448_c(var1);
         }

         if (this.field_78455_a.field_71439_g.func_70027_ad()) {
            this.func_78442_d();
         }
      }

      GlStateManager.func_179141_d();
   }

   private void func_178108_a(TextureAtlasSprite var1) {
      this.field_78455_a.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      Tessellator var2 = Tessellator.func_178181_a();
      BufferBuilder var3 = var2.func_178180_c();
      float var4 = 0.1F;
      GlStateManager.func_179131_c(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.func_179094_E();
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = var1.func_94209_e();
      float var11 = var1.func_94212_f();
      float var12 = var1.func_94206_g();
      float var13 = var1.func_94210_h();
      var3.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var3.func_181662_b(-1.0D, -1.0D, -0.5D).func_187315_a((double)var11, (double)var13).func_181675_d();
      var3.func_181662_b(1.0D, -1.0D, -0.5D).func_187315_a((double)var10, (double)var13).func_181675_d();
      var3.func_181662_b(1.0D, 1.0D, -0.5D).func_187315_a((double)var10, (double)var12).func_181675_d();
      var3.func_181662_b(-1.0D, 1.0D, -0.5D).func_187315_a((double)var11, (double)var12).func_181675_d();
      var2.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_78448_c(float var1) {
      this.field_78455_a.func_110434_K().func_110577_a(field_110929_d);
      Tessellator var2 = Tessellator.func_178181_a();
      BufferBuilder var3 = var2.func_178180_c();
      float var4 = this.field_78455_a.field_71439_g.func_70013_c();
      GlStateManager.func_179131_c(var4, var4, var4, 0.1F);
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179094_E();
      float var5 = 4.0F;
      float var6 = -1.0F;
      float var7 = 1.0F;
      float var8 = -1.0F;
      float var9 = 1.0F;
      float var10 = -0.5F;
      float var11 = -this.field_78455_a.field_71439_g.field_70177_z / 64.0F;
      float var12 = this.field_78455_a.field_71439_g.field_70125_A / 64.0F;
      var3.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var3.func_181662_b(-1.0D, -1.0D, -0.5D).func_187315_a((double)(4.0F + var11), (double)(4.0F + var12)).func_181675_d();
      var3.func_181662_b(1.0D, -1.0D, -0.5D).func_187315_a((double)(0.0F + var11), (double)(4.0F + var12)).func_181675_d();
      var3.func_181662_b(1.0D, 1.0D, -0.5D).func_187315_a((double)(0.0F + var11), (double)(0.0F + var12)).func_181675_d();
      var3.func_181662_b(-1.0D, 1.0D, -0.5D).func_187315_a((double)(4.0F + var11), (double)(0.0F + var12)).func_181675_d();
      var2.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
   }

   private void func_78442_d() {
      Tessellator var1 = Tessellator.func_178181_a();
      BufferBuilder var2 = var1.func_178180_c();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.func_179143_c(519);
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      float var3 = 1.0F;

      for(int var4 = 0; var4 < 2; ++var4) {
         GlStateManager.func_179094_E();
         TextureAtlasSprite var5 = this.field_78455_a.func_147117_R().func_195424_a(ModelBakery.field_207764_b);
         this.field_78455_a.func_110434_K().func_110577_a(TextureMap.field_110575_b);
         float var6 = var5.func_94209_e();
         float var7 = var5.func_94212_f();
         float var8 = var5.func_94206_g();
         float var9 = var5.func_94210_h();
         float var10 = -0.5F;
         float var11 = 0.5F;
         float var12 = -0.5F;
         float var13 = 0.5F;
         float var14 = -0.5F;
         GlStateManager.func_179109_b((float)(-(var4 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GlStateManager.func_179114_b((float)(var4 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         var2.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var2.func_181662_b(-0.5D, -0.5D, -0.5D).func_187315_a((double)var7, (double)var9).func_181675_d();
         var2.func_181662_b(0.5D, -0.5D, -0.5D).func_187315_a((double)var6, (double)var9).func_181675_d();
         var2.func_181662_b(0.5D, 0.5D, -0.5D).func_187315_a((double)var6, (double)var8).func_181675_d();
         var2.func_181662_b(-0.5D, 0.5D, -0.5D).func_187315_a((double)var7, (double)var8).func_181675_d();
         var1.func_78381_a();
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179143_c(515);
   }

   public void func_78441_a() {
      this.field_187470_g = this.field_187469_f;
      this.field_187472_i = this.field_187471_h;
      EntityPlayerSP var1 = this.field_78455_a.field_71439_g;
      ItemStack var2 = var1.func_184614_ca();
      ItemStack var3 = var1.func_184592_cb();
      if (var1.func_184838_M()) {
         this.field_187469_f = MathHelper.func_76131_a(this.field_187469_f - 0.4F, 0.0F, 1.0F);
         this.field_187471_h = MathHelper.func_76131_a(this.field_187471_h - 0.4F, 0.0F, 1.0F);
      } else {
         float var4 = var1.func_184825_o(1.0F);
         this.field_187469_f += MathHelper.func_76131_a((Objects.equals(this.field_187467_d, var2) ? var4 * var4 * var4 : 0.0F) - this.field_187469_f, -0.4F, 0.4F);
         this.field_187471_h += MathHelper.func_76131_a((float)(Objects.equals(this.field_187468_e, var3) ? 1 : 0) - this.field_187471_h, -0.4F, 0.4F);
      }

      if (this.field_187469_f < 0.1F) {
         this.field_187467_d = var2;
      }

      if (this.field_187471_h < 0.1F) {
         this.field_187468_e = var3;
      }

   }

   public void func_187460_a(EnumHand var1) {
      if (var1 == EnumHand.MAIN_HAND) {
         this.field_187469_f = 0.0F;
      } else {
         this.field_187471_h = 0.0F;
      }

   }
}
