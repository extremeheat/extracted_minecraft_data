package net.minecraft.client.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiMerchant extends GuiContainer {
   private static final Logger field_147039_u = LogManager.getLogger();
   private static final ResourceLocation field_147038_v = new ResourceLocation("textures/gui/container/villager.png");
   private IMerchant field_147037_w;
   private GuiMerchant$MerchantButton field_147043_x;
   private GuiMerchant$MerchantButton field_147042_y;
   private int field_147041_z;
   private String field_147040_A;

   public GuiMerchant(InventoryPlayer var1, IMerchant var2, World var3, String var4) {
      super(new ContainerMerchant(var1, var2, var3));
      this.field_147037_w = var2;
      this.field_147040_A = var4 != null && var4.length() >= 1 ? var4 : I18n.func_135052_a("entity.Villager.name");
   }

   @Override
   public void func_73866_w_() {
      super.func_73866_w_();
      int var1 = (this.field_146294_l - this.field_146999_f) / 2;
      int var2 = (this.field_146295_m - this.field_147000_g) / 2;
      this.field_146292_n.add(this.field_147043_x = new GuiMerchant$MerchantButton(1, var1 + 120 + 27, var2 + 24 - 1, true));
      this.field_146292_n.add(this.field_147042_y = new GuiMerchant$MerchantButton(2, var1 + 36 - 19, var2 + 24 - 1, false));
      this.field_147043_x.field_146124_l = false;
      this.field_147042_y.field_146124_l = false;
   }

   @Override
   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_78276_b(this.field_147040_A, this.field_146999_f / 2 - this.field_146289_q.func_78256_a(this.field_147040_A) / 2, 6, 4210752);
      this.field_146289_q.func_78276_b(I18n.func_135052_a("container.inventory"), 8, this.field_147000_g - 96 + 2, 4210752);
   }

   @Override
   public void func_73876_c() {
      super.func_73876_c();
      MerchantRecipeList var1 = this.field_147037_w.func_70934_b(this.field_146297_k.field_71439_g);
      if (var1 != null) {
         this.field_147043_x.field_146124_l = this.field_147041_z < var1.size() - 1;
         this.field_147042_y.field_146124_l = this.field_147041_z > 0;
      }
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      boolean var2 = false;
      if (var1 == this.field_147043_x) {
         ++this.field_147041_z;
         var2 = true;
      } else if (var1 == this.field_147042_y) {
         --this.field_147041_z;
         var2 = true;
      }

      if (var2) {
         ((ContainerMerchant)this.field_147002_h).func_75175_c(this.field_147041_z);
         ByteBuf var3 = Unpooled.buffer();

         try {
            var3.writeInt(this.field_147041_z);
            this.field_146297_k.func_147114_u().func_147297_a(new C17PacketCustomPayload("MC|TrSel", var3));
         } catch (Exception var8) {
            field_147039_u.error("Couldn't send trade info", var8);
         } finally {
            var3.release();
         }
      }
   }

   @Override
   protected void func_146976_a(float var1, int var2, int var3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147038_v);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      MerchantRecipeList var6 = this.field_147037_w.func_70934_b(this.field_146297_k.field_71439_g);
      if (var6 != null && !var6.isEmpty()) {
         int var7 = this.field_147041_z;
         MerchantRecipe var8 = (MerchantRecipe)var6.get(var7);
         if (var8.func_82784_g()) {
            this.field_146297_k.func_110434_K().func_110577_a(field_147038_v);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            this.func_73729_b(this.field_147003_i + 83, this.field_147009_r + 21, 212, 0, 28, 21);
            this.func_73729_b(this.field_147003_i + 83, this.field_147009_r + 51, 212, 0, 28, 21);
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      super.func_73863_a(var1, var2, var3);
      MerchantRecipeList var4 = this.field_147037_w.func_70934_b(this.field_146297_k.field_71439_g);
      if (var4 != null && !var4.isEmpty()) {
         int var5 = (this.field_146294_l - this.field_146999_f) / 2;
         int var6 = (this.field_146295_m - this.field_147000_g) / 2;
         int var7 = this.field_147041_z;
         MerchantRecipe var8 = (MerchantRecipe)var4.get(var7);
         GL11.glPushMatrix();
         ItemStack var9 = var8.func_77394_a();
         ItemStack var10 = var8.func_77396_b();
         ItemStack var11 = var8.func_77397_d();
         RenderHelper.func_74520_c();
         GL11.glDisable(2896);
         GL11.glEnable(32826);
         GL11.glEnable(2903);
         GL11.glEnable(2896);
         field_146296_j.field_77023_b = 100.0F;
         field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), var9, var5 + 36, var6 + 24);
         field_146296_j.func_77021_b(this.field_146289_q, this.field_146297_k.func_110434_K(), var9, var5 + 36, var6 + 24);
         if (var10 != null) {
            field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), var10, var5 + 62, var6 + 24);
            field_146296_j.func_77021_b(this.field_146289_q, this.field_146297_k.func_110434_K(), var10, var5 + 62, var6 + 24);
         }

         field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), var11, var5 + 120, var6 + 24);
         field_146296_j.func_77021_b(this.field_146289_q, this.field_146297_k.func_110434_K(), var11, var5 + 120, var6 + 24);
         field_146296_j.field_77023_b = 0.0F;
         GL11.glDisable(2896);
         if (this.func_146978_c(36, 24, 16, 16, var1, var2)) {
            this.func_146285_a(var9, var1, var2);
         } else if (var10 != null && this.func_146978_c(62, 24, 16, 16, var1, var2)) {
            this.func_146285_a(var10, var1, var2);
         } else if (this.func_146978_c(120, 24, 16, 16, var1, var2)) {
            this.func_146285_a(var11, var1, var2);
         }

         GL11.glPopMatrix();
         GL11.glEnable(2896);
         GL11.glEnable(2929);
         RenderHelper.func_74519_b();
      }
   }

   public IMerchant func_147035_g() {
      return this.field_147037_w;
   }
}
