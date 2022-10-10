package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RenderShulker extends RenderLiving<EntityShulker> {
   public static final ResourceLocation field_204402_a = new ResourceLocation("textures/entity/shulker/shulker.png");
   public static final ResourceLocation[] field_188342_a = new ResourceLocation[]{new ResourceLocation("textures/entity/shulker/shulker_white.png"), new ResourceLocation("textures/entity/shulker/shulker_orange.png"), new ResourceLocation("textures/entity/shulker/shulker_magenta.png"), new ResourceLocation("textures/entity/shulker/shulker_light_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_yellow.png"), new ResourceLocation("textures/entity/shulker/shulker_lime.png"), new ResourceLocation("textures/entity/shulker/shulker_pink.png"), new ResourceLocation("textures/entity/shulker/shulker_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_light_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_cyan.png"), new ResourceLocation("textures/entity/shulker/shulker_purple.png"), new ResourceLocation("textures/entity/shulker/shulker_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_brown.png"), new ResourceLocation("textures/entity/shulker/shulker_green.png"), new ResourceLocation("textures/entity/shulker/shulker_red.png"), new ResourceLocation("textures/entity/shulker/shulker_black.png")};

   public RenderShulker(RenderManager var1) {
      super(var1, new ModelShulker(), 0.0F);
      this.func_177094_a(new RenderShulker.HeadLayer());
   }

   public ModelShulker func_177087_b() {
      return (ModelShulker)super.func_177087_b();
   }

   public void func_76986_a(EntityShulker var1, double var2, double var4, double var6, float var8, float var9) {
      int var10 = var1.func_184693_dc();
      if (var10 > 0 && var1.func_184697_de()) {
         BlockPos var11 = var1.func_184699_da();
         BlockPos var12 = var1.func_184692_dd();
         double var13 = (double)((float)var10 - var9) / 6.0D;
         var13 *= var13;
         double var15 = (double)(var11.func_177958_n() - var12.func_177958_n()) * var13;
         double var17 = (double)(var11.func_177956_o() - var12.func_177956_o()) * var13;
         double var19 = (double)(var11.func_177952_p() - var12.func_177952_p()) * var13;
         super.func_76986_a((EntityLiving)var1, var2 - var15, var4 - var17, var6 - var19, var8, var9);
      } else {
         super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
      }

   }

   public boolean func_177071_a(EntityShulker var1, ICamera var2, double var3, double var5, double var7) {
      if (super.func_177071_a((EntityLiving)var1, var2, var3, var5, var7)) {
         return true;
      } else {
         if (var1.func_184693_dc() > 0 && var1.func_184697_de()) {
            BlockPos var9 = var1.func_184692_dd();
            BlockPos var10 = var1.func_184699_da();
            Vec3d var11 = new Vec3d((double)var10.func_177958_n(), (double)var10.func_177956_o(), (double)var10.func_177952_p());
            Vec3d var12 = new Vec3d((double)var9.func_177958_n(), (double)var9.func_177956_o(), (double)var9.func_177952_p());
            if (var2.func_78546_a(new AxisAlignedBB(var12.field_72450_a, var12.field_72448_b, var12.field_72449_c, var11.field_72450_a, var11.field_72448_b, var11.field_72449_c))) {
               return true;
            }
         }

         return false;
      }
   }

   protected ResourceLocation func_110775_a(EntityShulker var1) {
      return var1.func_190769_dn() == null ? field_204402_a : field_188342_a[var1.func_190769_dn().func_196059_a()];
   }

   protected void func_77043_a(EntityShulker var1, float var2, float var3, float var4) {
      super.func_77043_a(var1, var2, var3, var4);
      switch(var1.func_184696_cZ()) {
      case DOWN:
      default:
         break;
      case EAST:
         GlStateManager.func_179109_b(0.5F, 0.5F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case WEST:
         GlStateManager.func_179109_b(-0.5F, 0.5F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case NORTH:
         GlStateManager.func_179109_b(0.0F, 0.5F, -0.5F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case SOUTH:
         GlStateManager.func_179109_b(0.0F, 0.5F, 0.5F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case UP:
         GlStateManager.func_179109_b(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
      }

   }

   protected void func_77041_b(EntityShulker var1, float var2) {
      float var3 = 0.999F;
      GlStateManager.func_179152_a(0.999F, 0.999F, 0.999F);
   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }

   class HeadLayer implements LayerRenderer<EntityShulker> {
      private HeadLayer() {
         super();
      }

      public void func_177141_a(EntityShulker var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         GlStateManager.func_179094_E();
         switch(var1.func_184696_cZ()) {
         case DOWN:
         default:
            break;
         case EAST:
            GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(1.0F, -1.0F, 0.0F);
            GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case WEST:
            GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(-1.0F, -1.0F, 0.0F);
            GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case NORTH:
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, -1.0F, -1.0F);
            break;
         case SOUTH:
            GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, -1.0F, 1.0F);
            break;
         case UP:
            GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, -2.0F, 0.0F);
         }

         ModelRenderer var9 = RenderShulker.this.func_177087_b().func_205067_c();
         var9.field_78796_g = var6 * 0.017453292F;
         var9.field_78795_f = var7 * 0.017453292F;
         EnumDyeColor var10 = var1.func_190769_dn();
         if (var10 == null) {
            RenderShulker.this.func_110776_a(RenderShulker.field_204402_a);
         } else {
            RenderShulker.this.func_110776_a(RenderShulker.field_188342_a[var10.func_196059_a()]);
         }

         var9.func_78785_a(var8);
         GlStateManager.func_179121_F();
      }

      public boolean func_177142_b() {
         return false;
      }

      // $FF: synthetic method
      HeadLayer(Object var2) {
         this();
      }
   }
}
