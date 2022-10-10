package net.minecraft.client.gui;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.FlatLayerInfo;

public class GuiCreateFlatWorld extends GuiScreen {
   private final GuiCreateWorld field_146385_f;
   private FlatGenSettings field_146387_g = FlatGenSettings.func_82649_e();
   private String field_146393_h;
   private String field_146394_i;
   private String field_146391_r;
   private GuiCreateFlatWorld.Details field_146390_s;
   private GuiButton field_146389_t;
   private GuiButton field_146388_u;
   private GuiButton field_146386_v;

   public GuiCreateFlatWorld(GuiCreateWorld var1, NBTTagCompound var2) {
      super();
      this.field_146385_f = var1;
      this.func_210503_a(var2);
   }

   public String func_210501_h() {
      return this.field_146387_g.toString();
   }

   public NBTTagCompound func_210504_i() {
      return (NBTTagCompound)this.field_146387_g.func_210834_a(NBTDynamicOps.field_210820_a).getValue();
   }

   public void func_210502_a(String var1) {
      this.field_146387_g = FlatGenSettings.func_82651_a(var1);
   }

   public void func_210503_a(NBTTagCompound var1) {
      this.field_146387_g = FlatGenSettings.func_210835_a(new Dynamic(NBTDynamicOps.field_210820_a, var1));
   }

   protected void func_73866_w_() {
      this.field_146393_h = I18n.func_135052_a("createWorld.customize.flat.title");
      this.field_146394_i = I18n.func_135052_a("createWorld.customize.flat.tile");
      this.field_146391_r = I18n.func_135052_a("createWorld.customize.flat.height");
      this.field_146390_s = new GuiCreateFlatWorld.Details();
      this.field_195124_j.add(this.field_146390_s);
      this.field_146389_t = this.func_189646_b(new GuiButton(2, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("createWorld.customize.flat.addLayer") + " (NYI)") {
         public void func_194829_a(double var1, double var3) {
            GuiCreateFlatWorld.this.field_146387_g.func_82645_d();
            GuiCreateFlatWorld.this.func_146375_g();
         }
      });
      this.field_146388_u = this.func_189646_b(new GuiButton(3, this.field_146294_l / 2 - 50, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("createWorld.customize.flat.editLayer") + " (NYI)") {
         public void func_194829_a(double var1, double var3) {
            GuiCreateFlatWorld.this.field_146387_g.func_82645_d();
            GuiCreateFlatWorld.this.func_146375_g();
         }
      });
      this.field_146386_v = this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 - 155, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("createWorld.customize.flat.removeLayer")) {
         public void func_194829_a(double var1, double var3) {
            if (GuiCreateFlatWorld.this.func_146382_i()) {
               List var5 = GuiCreateFlatWorld.this.field_146387_g.func_82650_c();
               int var6 = var5.size() - GuiCreateFlatWorld.this.field_146390_s.field_148228_k - 1;
               var5.remove(var6);
               GuiCreateFlatWorld.this.field_146390_s.field_148228_k = Math.min(GuiCreateFlatWorld.this.field_146390_s.field_148228_k, var5.size() - 1);
               GuiCreateFlatWorld.this.field_146387_g.func_82645_d();
               GuiCreateFlatWorld.this.func_146375_g();
            }
         }
      });
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateFlatWorld.this.field_146385_f.field_146334_a = GuiCreateFlatWorld.this.func_210504_i();
            GuiCreateFlatWorld.this.field_146297_k.func_147108_a(GuiCreateFlatWorld.this.field_146385_f);
            GuiCreateFlatWorld.this.field_146387_g.func_82645_d();
            GuiCreateFlatWorld.this.func_146375_g();
         }
      });
      this.func_189646_b(new GuiButton(5, this.field_146294_l / 2 + 5, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("createWorld.customize.presets")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateFlatWorld.this.field_146297_k.func_147108_a(new GuiFlatPresets(GuiCreateFlatWorld.this));
            GuiCreateFlatWorld.this.field_146387_g.func_82645_d();
            GuiCreateFlatWorld.this.func_146375_g();
         }
      });
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateFlatWorld.this.field_146297_k.func_147108_a(GuiCreateFlatWorld.this.field_146385_f);
            GuiCreateFlatWorld.this.field_146387_g.func_82645_d();
            GuiCreateFlatWorld.this.func_146375_g();
         }
      });
      this.field_146389_t.field_146125_m = false;
      this.field_146388_u.field_146125_m = false;
      this.field_146387_g.func_82645_d();
      this.func_146375_g();
   }

   public void func_146375_g() {
      boolean var1 = this.func_146382_i();
      this.field_146386_v.field_146124_l = var1;
      this.field_146388_u.field_146124_l = var1;
      this.field_146388_u.field_146124_l = false;
      this.field_146389_t.field_146124_l = false;
   }

   private boolean func_146382_i() {
      return this.field_146390_s.field_148228_k > -1 && this.field_146390_s.field_148228_k < this.field_146387_g.func_82650_c().size();
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.field_146390_s;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146390_s.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146393_h, this.field_146294_l / 2, 8, 16777215);
      int var4 = this.field_146294_l / 2 - 92 - 16;
      this.func_73731_b(this.field_146289_q, this.field_146394_i, var4, 32, 16777215);
      this.func_73731_b(this.field_146289_q, this.field_146391_r, var4 + 2 + 213 - this.field_146289_q.func_78256_a(this.field_146391_r), 32, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   class Details extends GuiSlot {
      public int field_148228_k = -1;

      public Details() {
         super(GuiCreateFlatWorld.this.field_146297_k, GuiCreateFlatWorld.this.field_146294_l, GuiCreateFlatWorld.this.field_146295_m, 43, GuiCreateFlatWorld.this.field_146295_m - 60, 24);
      }

      private void func_148225_a(int var1, int var2, ItemStack var3) {
         this.func_148226_e(var1 + 1, var2 + 1);
         GlStateManager.func_179091_B();
         if (!var3.func_190926_b()) {
            RenderHelper.func_74520_c();
            GuiCreateFlatWorld.this.field_146296_j.func_175042_a(var3, var1 + 2, var2 + 2);
            RenderHelper.func_74518_a();
         }

         GlStateManager.func_179101_C();
      }

      private void func_148226_e(int var1, int var2) {
         this.func_148224_c(var1, var2, 0, 0);
      }

      private void func_148224_c(int var1, int var2, int var3, int var4) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_148161_k.func_110434_K().func_110577_a(field_110323_l);
         float var5 = 0.0078125F;
         float var6 = 0.0078125F;
         boolean var7 = true;
         boolean var8 = true;
         Tessellator var9 = Tessellator.func_178181_a();
         BufferBuilder var10 = var9.func_178180_c();
         var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var10.func_181662_b((double)(var1 + 0), (double)(var2 + 18), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 18), (double)(var2 + 18), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 18), (double)(var2 + 0), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 0), (double)(var2 + 0), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
         var9.func_78381_a();
      }

      protected int func_148127_b() {
         return GuiCreateFlatWorld.this.field_146387_g.func_82650_c().size();
      }

      protected boolean func_195078_a(int var1, int var2, double var3, double var5) {
         this.field_148228_k = var1;
         GuiCreateFlatWorld.this.func_146375_g();
         return true;
      }

      protected boolean func_148131_a(int var1) {
         return var1 == this.field_148228_k;
      }

      protected void func_148123_a() {
      }

      protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
         FlatLayerInfo var8 = (FlatLayerInfo)GuiCreateFlatWorld.this.field_146387_g.func_82650_c().get(GuiCreateFlatWorld.this.field_146387_g.func_82650_c().size() - var1 - 1);
         IBlockState var9 = var8.func_175900_c();
         Block var10 = var9.func_177230_c();
         Item var11 = var10.func_199767_j();
         if (var11 == Items.field_190931_a) {
            if (var10 == Blocks.field_150355_j) {
               var11 = Items.field_151131_as;
            } else if (var10 == Blocks.field_150353_l) {
               var11 = Items.field_151129_at;
            }
         }

         ItemStack var12 = new ItemStack(var11);
         String var13 = var11.func_200295_i(var12).func_150254_d();
         this.func_148225_a(var2, var3, var12);
         GuiCreateFlatWorld.this.field_146289_q.func_211126_b(var13, (float)(var2 + 18 + 5), (float)(var3 + 3), 16777215);
         String var14;
         if (var1 == 0) {
            var14 = I18n.func_135052_a("createWorld.customize.flat.layer.top", var8.func_82657_a());
         } else if (var1 == GuiCreateFlatWorld.this.field_146387_g.func_82650_c().size() - 1) {
            var14 = I18n.func_135052_a("createWorld.customize.flat.layer.bottom", var8.func_82657_a());
         } else {
            var14 = I18n.func_135052_a("createWorld.customize.flat.layer", var8.func_82657_a());
         }

         GuiCreateFlatWorld.this.field_146289_q.func_211126_b(var14, (float)(var2 + 2 + 213 - GuiCreateFlatWorld.this.field_146289_q.func_78256_a(var14)), (float)(var3 + 3), 16777215);
      }

      protected int func_148137_d() {
         return this.field_148155_a - 70;
      }
   }
}
