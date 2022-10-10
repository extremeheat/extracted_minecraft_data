package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.HotbarSnapshot;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GuiContainerCreative extends InventoryEffectRenderer {
   private static final ResourceLocation field_147061_u = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final InventoryBasic field_195378_x = new InventoryBasic(new TextComponentString("tmp"), 45);
   private static int field_147058_w;
   private float field_147067_x;
   private boolean field_147066_y;
   private GuiTextField field_147062_A;
   private List<Slot> field_147063_B;
   private Slot field_147064_C;
   private CreativeCrafting field_147059_E;
   private boolean field_195377_F;
   private boolean field_199506_G;

   public GuiContainerCreative(EntityPlayer var1) {
      super(new GuiContainerCreative.ContainerCreative(var1));
      var1.field_71070_bA = this.field_147002_h;
      this.field_146291_p = true;
      this.field_147000_g = 136;
      this.field_146999_f = 195;
   }

   public void func_73876_c() {
      if (!this.field_146297_k.field_71442_b.func_78758_h()) {
         this.field_146297_k.func_147108_a(new GuiInventory(this.field_146297_k.field_71439_g));
      }

   }

   protected void func_184098_a(@Nullable Slot var1, int var2, int var3, ClickType var4) {
      if (this.func_208018_a(var1)) {
         this.field_147062_A.func_146202_e();
         this.field_147062_A.func_146199_i(0);
      }

      boolean var5 = var4 == ClickType.QUICK_MOVE;
      var4 = var2 == -999 && var4 == ClickType.PICKUP ? ClickType.THROW : var4;
      ItemStack var7;
      InventoryPlayer var10;
      if (var1 == null && field_147058_w != ItemGroup.field_78036_m.func_78021_a() && var4 != ClickType.QUICK_CRAFT) {
         var10 = this.field_146297_k.field_71439_g.field_71071_by;
         if (!var10.func_70445_o().func_190926_b() && this.field_199506_G) {
            if (var3 == 0) {
               this.field_146297_k.field_71439_g.func_71019_a(var10.func_70445_o(), true);
               this.field_146297_k.field_71442_b.func_78752_a(var10.func_70445_o());
               var10.func_70437_b(ItemStack.field_190927_a);
            }

            if (var3 == 1) {
               var7 = var10.func_70445_o().func_77979_a(1);
               this.field_146297_k.field_71439_g.func_71019_a(var7, true);
               this.field_146297_k.field_71442_b.func_78752_a(var7);
            }
         }
      } else {
         if (var1 != null && !var1.func_82869_a(this.field_146297_k.field_71439_g)) {
            return;
         }

         if (var1 == this.field_147064_C && var5) {
            for(int var11 = 0; var11 < this.field_146297_k.field_71439_g.field_71069_bz.func_75138_a().size(); ++var11) {
               this.field_146297_k.field_71442_b.func_78761_a(ItemStack.field_190927_a, var11);
            }
         } else {
            ItemStack var6;
            if (field_147058_w == ItemGroup.field_78036_m.func_78021_a()) {
               if (var1 == this.field_147064_C) {
                  this.field_146297_k.field_71439_g.field_71071_by.func_70437_b(ItemStack.field_190927_a);
               } else if (var4 == ClickType.THROW && var1 != null && var1.func_75216_d()) {
                  var6 = var1.func_75209_a(var3 == 0 ? 1 : var1.func_75211_c().func_77976_d());
                  var7 = var1.func_75211_c();
                  this.field_146297_k.field_71439_g.func_71019_a(var6, true);
                  this.field_146297_k.field_71442_b.func_78752_a(var6);
                  this.field_146297_k.field_71442_b.func_78761_a(var7, ((GuiContainerCreative.CreativeSlot)var1).field_148332_b.field_75222_d);
               } else if (var4 == ClickType.THROW && !this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b()) {
                  this.field_146297_k.field_71439_g.func_71019_a(this.field_146297_k.field_71439_g.field_71071_by.func_70445_o(), true);
                  this.field_146297_k.field_71442_b.func_78752_a(this.field_146297_k.field_71439_g.field_71071_by.func_70445_o());
                  this.field_146297_k.field_71439_g.field_71071_by.func_70437_b(ItemStack.field_190927_a);
               } else {
                  this.field_146297_k.field_71439_g.field_71069_bz.func_184996_a(var1 == null ? var2 : ((GuiContainerCreative.CreativeSlot)var1).field_148332_b.field_75222_d, var3, var4, this.field_146297_k.field_71439_g);
                  this.field_146297_k.field_71439_g.field_71069_bz.func_75142_b();
               }
            } else {
               ItemStack var9;
               if (var4 != ClickType.QUICK_CRAFT && var1.field_75224_c == field_195378_x) {
                  var10 = this.field_146297_k.field_71439_g.field_71071_by;
                  var7 = var10.func_70445_o();
                  ItemStack var13 = var1.func_75211_c();
                  if (var4 == ClickType.SWAP) {
                     if (!var13.func_190926_b() && var3 >= 0 && var3 < 9) {
                        var9 = var13.func_77946_l();
                        var9.func_190920_e(var9.func_77976_d());
                        this.field_146297_k.field_71439_g.field_71071_by.func_70299_a(var3, var9);
                        this.field_146297_k.field_71439_g.field_71069_bz.func_75142_b();
                     }

                     return;
                  }

                  if (var4 == ClickType.CLONE) {
                     if (var10.func_70445_o().func_190926_b() && var1.func_75216_d()) {
                        var9 = var1.func_75211_c().func_77946_l();
                        var9.func_190920_e(var9.func_77976_d());
                        var10.func_70437_b(var9);
                     }

                     return;
                  }

                  if (var4 == ClickType.THROW) {
                     if (!var13.func_190926_b()) {
                        var9 = var13.func_77946_l();
                        var9.func_190920_e(var3 == 0 ? 1 : var9.func_77976_d());
                        this.field_146297_k.field_71439_g.func_71019_a(var9, true);
                        this.field_146297_k.field_71442_b.func_78752_a(var9);
                     }

                     return;
                  }

                  if (!var7.func_190926_b() && !var13.func_190926_b() && var7.func_77969_a(var13) && ItemStack.func_77970_a(var7, var13)) {
                     if (var3 == 0) {
                        if (var5) {
                           var7.func_190920_e(var7.func_77976_d());
                        } else if (var7.func_190916_E() < var7.func_77976_d()) {
                           var7.func_190917_f(1);
                        }
                     } else {
                        var7.func_190918_g(1);
                     }
                  } else if (!var13.func_190926_b() && var7.func_190926_b()) {
                     var10.func_70437_b(var13.func_77946_l());
                     var7 = var10.func_70445_o();
                     if (var5) {
                        var7.func_190920_e(var7.func_77976_d());
                     }
                  } else if (var3 == 0) {
                     var10.func_70437_b(ItemStack.field_190927_a);
                  } else {
                     var10.func_70445_o().func_190918_g(1);
                  }
               } else if (this.field_147002_h != null) {
                  var6 = var1 == null ? ItemStack.field_190927_a : this.field_147002_h.func_75139_a(var1.field_75222_d).func_75211_c();
                  this.field_147002_h.func_184996_a(var1 == null ? var2 : var1.field_75222_d, var3, var4, this.field_146297_k.field_71439_g);
                  if (Container.func_94532_c(var3) == 2) {
                     for(int var12 = 0; var12 < 9; ++var12) {
                        this.field_146297_k.field_71442_b.func_78761_a(this.field_147002_h.func_75139_a(45 + var12).func_75211_c(), 36 + var12);
                     }
                  } else if (var1 != null) {
                     var7 = this.field_147002_h.func_75139_a(var1.field_75222_d).func_75211_c();
                     this.field_146297_k.field_71442_b.func_78761_a(var7, var1.field_75222_d - this.field_147002_h.field_75151_b.size() + 9 + 36);
                     int var8 = 45 + var3;
                     if (var4 == ClickType.SWAP) {
                        this.field_146297_k.field_71442_b.func_78761_a(var6, var8 - this.field_147002_h.field_75151_b.size() + 9 + 36);
                     } else if (var4 == ClickType.THROW && !var6.func_190926_b()) {
                        var9 = var6.func_77946_l();
                        var9.func_190920_e(var3 == 0 ? 1 : var9.func_77976_d());
                        this.field_146297_k.field_71439_g.func_71019_a(var9, true);
                        this.field_146297_k.field_71442_b.func_78752_a(var9);
                     }

                     this.field_146297_k.field_71439_g.field_71069_bz.func_75142_b();
                  }
               }
            }
         }
      }

   }

   private boolean func_208018_a(@Nullable Slot var1) {
      return var1 != null && var1.field_75224_c == field_195378_x;
   }

   protected void func_175378_g() {
      int var1 = this.field_147003_i;
      super.func_175378_g();
      if (this.field_147062_A != null && this.field_147003_i != var1) {
         this.field_147062_A.field_146209_f = this.field_147003_i + 82;
      }

   }

   protected void func_73866_w_() {
      if (this.field_146297_k.field_71442_b.func_78758_h()) {
         super.func_73866_w_();
         this.field_146297_k.field_195559_v.func_197967_a(true);
         this.field_147062_A = new GuiTextField(0, this.field_146289_q, this.field_147003_i + 82, this.field_147009_r + 6, 80, this.field_146289_q.field_78288_b);
         this.field_147062_A.func_146203_f(50);
         this.field_147062_A.func_146185_a(false);
         this.field_147062_A.func_146189_e(false);
         this.field_147062_A.func_146193_g(16777215);
         this.field_195124_j.add(this.field_147062_A);
         int var1 = field_147058_w;
         field_147058_w = -1;
         this.func_147050_b(ItemGroup.field_78032_a[var1]);
         this.field_147059_E = new CreativeCrafting(this.field_146297_k);
         this.field_146297_k.field_71439_g.field_71069_bz.func_75132_a(this.field_147059_E);
      } else {
         this.field_146297_k.func_147108_a(new GuiInventory(this.field_146297_k.field_71439_g));
      }

   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      String var4 = this.field_147062_A.func_146179_b();
      this.func_146280_a(var1, var2, var3);
      this.field_147062_A.func_146180_a(var4);
      if (!this.field_147062_A.func_146179_b().isEmpty()) {
         this.func_147053_i();
      }

   }

   public void func_146281_b() {
      super.func_146281_b();
      if (this.field_146297_k.field_71439_g != null && this.field_146297_k.field_71439_g.field_71071_by != null) {
         this.field_146297_k.field_71439_g.field_71069_bz.func_82847_b(this.field_147059_E);
      }

      this.field_146297_k.field_195559_v.func_197967_a(false);
   }

   public boolean charTyped(char var1, int var2) {
      if (this.field_195377_F) {
         return false;
      } else if (field_147058_w != ItemGroup.field_78027_g.func_78021_a()) {
         return false;
      } else {
         String var3 = this.field_147062_A.func_146179_b();
         if (this.field_147062_A.charTyped(var1, var2)) {
            if (!Objects.equals(var3, this.field_147062_A.func_146179_b())) {
               this.func_147053_i();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      this.field_195377_F = false;
      if (field_147058_w != ItemGroup.field_78027_g.func_78021_a()) {
         if (this.field_146297_k.field_71474_y.field_74310_D.func_197976_a(var1, var2)) {
            this.field_195377_F = true;
            this.func_147050_b(ItemGroup.field_78027_g);
            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      } else {
         boolean var4 = !this.func_208018_a(this.field_147006_u) || this.field_147006_u != null && this.field_147006_u.func_75216_d();
         if (var4 && this.func_195363_d(var1, var2)) {
            this.field_195377_F = true;
            return true;
         } else {
            String var5 = this.field_147062_A.func_146179_b();
            if (this.field_147062_A.keyPressed(var1, var2, var3)) {
               if (!Objects.equals(var5, this.field_147062_A.func_146179_b())) {
                  this.func_147053_i();
               }

               return true;
            } else {
               return super.keyPressed(var1, var2, var3);
            }
         }
      }
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      this.field_195377_F = false;
      return super.keyReleased(var1, var2, var3);
   }

   private void func_147053_i() {
      GuiContainerCreative.ContainerCreative var1 = (GuiContainerCreative.ContainerCreative)this.field_147002_h;
      var1.field_148330_a.clear();
      if (this.field_147062_A.func_146179_b().isEmpty()) {
         Iterator var2 = IRegistry.field_212630_s.iterator();

         while(var2.hasNext()) {
            Item var3 = (Item)var2.next();
            var3.func_150895_a(ItemGroup.field_78027_g, var1.field_148330_a);
         }
      } else {
         var1.field_148330_a.addAll(this.field_146297_k.func_193987_a(SearchTreeManager.field_194011_a).func_194038_a(this.field_147062_A.func_146179_b().toLowerCase(Locale.ROOT)));
      }

      this.field_147067_x = 0.0F;
      var1.func_148329_a(0.0F);
   }

   protected void func_146979_b(int var1, int var2) {
      ItemGroup var3 = ItemGroup.field_78032_a[field_147058_w];
      if (var3.func_78019_g()) {
         GlStateManager.func_179084_k();
         this.field_146289_q.func_211126_b(I18n.func_135052_a(var3.func_78024_c()), 8.0F, 6.0F, 4210752);
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         double var6 = var1 - (double)this.field_147003_i;
         double var8 = var3 - (double)this.field_147009_r;
         ItemGroup[] var10 = ItemGroup.field_78032_a;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ItemGroup var13 = var10[var12];
            if (this.func_195375_a(var13, var6, var8)) {
               return true;
            }
         }

         if (field_147058_w != ItemGroup.field_78036_m.func_78021_a() && this.func_195376_a(var1, var3)) {
            this.field_147066_y = this.func_147055_p();
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0) {
         double var6 = var1 - (double)this.field_147003_i;
         double var8 = var3 - (double)this.field_147009_r;
         this.field_147066_y = false;
         ItemGroup[] var10 = ItemGroup.field_78032_a;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ItemGroup var13 = var10[var12];
            if (this.func_195375_a(var13, var6, var8)) {
               this.func_147050_b(var13);
               return true;
            }
         }
      }

      return super.mouseReleased(var1, var3, var5);
   }

   private boolean func_147055_p() {
      return field_147058_w != ItemGroup.field_78036_m.func_78021_a() && ItemGroup.field_78032_a[field_147058_w].func_78017_i() && ((GuiContainerCreative.ContainerCreative)this.field_147002_h).func_148328_e();
   }

   private void func_147050_b(ItemGroup var1) {
      int var2 = field_147058_w;
      field_147058_w = var1.func_78021_a();
      GuiContainerCreative.ContainerCreative var3 = (GuiContainerCreative.ContainerCreative)this.field_147002_h;
      this.field_147008_s.clear();
      var3.field_148330_a.clear();
      int var5;
      int var7;
      if (var1 == ItemGroup.field_192395_m) {
         CreativeSettings var4 = this.field_146297_k.func_199403_al();

         for(var5 = 0; var5 < 9; ++var5) {
            HotbarSnapshot var6 = var4.func_192563_a(var5);
            if (var6.isEmpty()) {
               for(var7 = 0; var7 < 9; ++var7) {
                  if (var7 == var5) {
                     ItemStack var8 = new ItemStack(Items.field_151121_aF);
                     var8.func_190925_c("CustomCreativeLock");
                     String var9 = this.field_146297_k.field_71474_y.field_151456_ac[var5].func_197978_k();
                     String var10 = this.field_146297_k.field_71474_y.field_193629_ap.func_197978_k();
                     var8.func_200302_a(new TextComponentTranslation("inventory.hotbarInfo", new Object[]{var10, var9}));
                     var3.field_148330_a.add(var8);
                  } else {
                     var3.field_148330_a.add(ItemStack.field_190927_a);
                  }
               }
            } else {
               var3.field_148330_a.addAll(var6);
            }
         }
      } else if (var1 != ItemGroup.field_78027_g) {
         var1.func_78018_a(var3.field_148330_a);
      }

      if (var1 == ItemGroup.field_78036_m) {
         Container var11 = this.field_146297_k.field_71439_g.field_71069_bz;
         if (this.field_147063_B == null) {
            this.field_147063_B = var3.field_75151_b;
         }

         var3.field_75151_b = Lists.newArrayList();

         for(var5 = 0; var5 < var11.field_75151_b.size(); ++var5) {
            GuiContainerCreative.CreativeSlot var12 = new GuiContainerCreative.CreativeSlot((Slot)var11.field_75151_b.get(var5), var5);
            var3.field_75151_b.add(var12);
            int var13;
            int var14;
            if (var5 >= 5 && var5 < 9) {
               var7 = var5 - 5;
               var13 = var7 / 2;
               var14 = var7 % 2;
               var12.field_75223_e = 54 + var13 * 54;
               var12.field_75221_f = 6 + var14 * 27;
            } else if (var5 >= 0 && var5 < 5) {
               var12.field_75223_e = -2000;
               var12.field_75221_f = -2000;
            } else if (var5 == 45) {
               var12.field_75223_e = 35;
               var12.field_75221_f = 20;
            } else if (var5 < var11.field_75151_b.size()) {
               var7 = var5 - 9;
               var13 = var7 % 9;
               var14 = var7 / 9;
               var12.field_75223_e = 9 + var13 * 18;
               if (var5 >= 36) {
                  var12.field_75221_f = 112;
               } else {
                  var12.field_75221_f = 54 + var14 * 18;
               }
            }
         }

         this.field_147064_C = new Slot(field_195378_x, 0, 173, 112);
         var3.field_75151_b.add(this.field_147064_C);
      } else if (var2 == ItemGroup.field_78036_m.func_78021_a()) {
         var3.field_75151_b = this.field_147063_B;
         this.field_147063_B = null;
      }

      if (this.field_147062_A != null) {
         if (var1 == ItemGroup.field_78027_g) {
            this.field_147062_A.func_146189_e(true);
            this.field_147062_A.func_146205_d(false);
            this.field_147062_A.func_146195_b(true);
            if (var2 != var1.func_78021_a()) {
               this.field_147062_A.func_146180_a("");
            }

            this.func_147053_i();
         } else {
            this.field_147062_A.func_146189_e(false);
            this.field_147062_A.func_146205_d(true);
            this.field_147062_A.func_146195_b(false);
            this.field_147062_A.func_146180_a("");
         }
      }

      this.field_147067_x = 0.0F;
      var3.func_148329_a(0.0F);
   }

   public boolean mouseScrolled(double var1) {
      if (!this.func_147055_p()) {
         return false;
      } else {
         int var3 = (((GuiContainerCreative.ContainerCreative)this.field_147002_h).field_148330_a.size() + 9 - 1) / 9 - 5;
         this.field_147067_x = (float)((double)this.field_147067_x - var1 / (double)var3);
         this.field_147067_x = MathHelper.func_76131_a(this.field_147067_x, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.field_147002_h).func_148329_a(this.field_147067_x);
         return true;
      }
   }

   protected boolean func_195361_a(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.field_146999_f) || var3 >= (double)(var6 + this.field_147000_g);
      this.field_199506_G = var8 && !this.func_195375_a(ItemGroup.field_78032_a[field_147058_w], var1, var3);
      return this.field_199506_G;
   }

   protected boolean func_195376_a(double var1, double var3) {
      int var5 = this.field_147003_i;
      int var6 = this.field_147009_r;
      int var7 = var5 + 175;
      int var8 = var6 + 18;
      int var9 = var7 + 14;
      int var10 = var8 + 112;
      return var1 >= (double)var7 && var3 >= (double)var8 && var1 < (double)var9 && var3 < (double)var10;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.field_147066_y) {
         int var10 = this.field_147009_r + 18;
         int var11 = var10 + 112;
         this.field_147067_x = ((float)var3 - (float)var10 - 7.5F) / ((float)(var11 - var10) - 15.0F);
         this.field_147067_x = MathHelper.func_76131_a(this.field_147067_x, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.field_147002_h).func_148329_a(this.field_147067_x);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      super.func_73863_a(var1, var2, var3);
      ItemGroup[] var4 = ItemGroup.field_78032_a;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemGroup var7 = var4[var6];
         if (this.func_147052_b(var7, var1, var2)) {
            break;
         }
      }

      if (this.field_147064_C != null && field_147058_w == ItemGroup.field_78036_m.func_78021_a() && this.func_195359_a(this.field_147064_C.field_75223_e, this.field_147064_C.field_75221_f, 16, 16, (double)var1, (double)var2)) {
         this.func_146279_a(I18n.func_135052_a("inventory.binSlot"), var1, var2);
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179140_f();
      this.func_191948_b(var1, var2);
   }

   protected void func_146285_a(ItemStack var1, int var2, int var3) {
      if (field_147058_w == ItemGroup.field_78027_g.func_78021_a()) {
         List var4 = var1.func_82840_a(this.field_146297_k.field_71439_g, this.field_146297_k.field_71474_y.field_82882_x ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
         ArrayList var5 = Lists.newArrayListWithCapacity(var4.size());
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            ITextComponent var7 = (ITextComponent)var6.next();
            var5.add(var7.func_150254_d());
         }

         ItemGroup var13 = var1.func_77973_b().func_77640_w();
         if (var13 == null && var1.func_77973_b() == Items.field_151134_bR) {
            Map var14 = EnchantmentHelper.func_82781_a(var1);
            if (var14.size() == 1) {
               Enchantment var8 = (Enchantment)var14.keySet().iterator().next();
               ItemGroup[] var9 = ItemGroup.field_78032_a;
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  ItemGroup var12 = var9[var11];
                  if (var12.func_111226_a(var8.field_77351_y)) {
                     var13 = var12;
                     break;
                  }
               }
            }
         }

         if (var13 != null) {
            var5.add(1, "" + TextFormatting.BOLD + TextFormatting.BLUE + I18n.func_135052_a(var13.func_78024_c()));
         }

         for(int var15 = 0; var15 < var5.size(); ++var15) {
            if (var15 == 0) {
               var5.set(var15, var1.func_77953_t().field_77937_e + (String)var5.get(var15));
            } else {
               var5.set(var15, TextFormatting.GRAY + (String)var5.get(var15));
            }
         }

         this.func_146283_a(var5, var2, var3);
      } else {
         super.func_146285_a(var1, var2, var3);
      }

   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.func_74520_c();
      ItemGroup var4 = ItemGroup.field_78032_a[field_147058_w];
      ItemGroup[] var5 = ItemGroup.field_78032_a;
      int var6 = var5.length;

      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         ItemGroup var8 = var5[var7];
         this.field_146297_k.func_110434_K().func_110577_a(field_147061_u);
         if (var8.func_78021_a() != field_147058_w) {
            this.func_147051_a(var8);
         }
      }

      this.field_146297_k.func_110434_K().func_110577_a(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + var4.func_78015_f()));
      this.func_73729_b(this.field_147003_i, this.field_147009_r, 0, 0, this.field_146999_f, this.field_147000_g);
      this.field_147062_A.func_195608_a(var2, var3, var1);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      int var9 = this.field_147003_i + 175;
      var6 = this.field_147009_r + 18;
      var7 = var6 + 112;
      this.field_146297_k.func_110434_K().func_110577_a(field_147061_u);
      if (var4.func_78017_i()) {
         this.func_73729_b(var9, var6 + (int)((float)(var7 - var6 - 17) * this.field_147067_x), 232 + (this.func_147055_p() ? 0 : 12), 0, 12, 15);
      }

      this.func_147051_a(var4);
      if (var4 == ItemGroup.field_78036_m) {
         GuiInventory.func_147046_a(this.field_147003_i + 88, this.field_147009_r + 45, 20, (float)(this.field_147003_i + 88 - var2), (float)(this.field_147009_r + 45 - 30 - var3), this.field_146297_k.field_71439_g);
      }

   }

   protected boolean func_195375_a(ItemGroup var1, double var2, double var4) {
      int var6 = var1.func_78020_k();
      int var7 = 28 * var6;
      byte var8 = 0;
      if (var1.func_192394_m()) {
         var7 = this.field_146999_f - 28 * (6 - var6) + 2;
      } else if (var6 > 0) {
         var7 += var6;
      }

      int var9;
      if (var1.func_78023_l()) {
         var9 = var8 - 32;
      } else {
         var9 = var8 + this.field_147000_g;
      }

      return var2 >= (double)var7 && var2 <= (double)(var7 + 28) && var4 >= (double)var9 && var4 <= (double)(var9 + 32);
   }

   protected boolean func_147052_b(ItemGroup var1, int var2, int var3) {
      int var4 = var1.func_78020_k();
      int var5 = 28 * var4;
      byte var6 = 0;
      if (var1.func_192394_m()) {
         var5 = this.field_146999_f - 28 * (6 - var4) + 2;
      } else if (var4 > 0) {
         var5 += var4;
      }

      int var7;
      if (var1.func_78023_l()) {
         var7 = var6 - 32;
      } else {
         var7 = var6 + this.field_147000_g;
      }

      if (this.func_195359_a(var5 + 3, var7 + 3, 23, 27, (double)var2, (double)var3)) {
         this.func_146279_a(I18n.func_135052_a(var1.func_78024_c()), var2, var3);
         return true;
      } else {
         return false;
      }
   }

   protected void func_147051_a(ItemGroup var1) {
      boolean var2 = var1.func_78021_a() == field_147058_w;
      boolean var3 = var1.func_78023_l();
      int var4 = var1.func_78020_k();
      int var5 = var4 * 28;
      int var6 = 0;
      int var7 = this.field_147003_i + 28 * var4;
      int var8 = this.field_147009_r;
      boolean var9 = true;
      if (var2) {
         var6 += 32;
      }

      if (var1.func_192394_m()) {
         var7 = this.field_147003_i + this.field_146999_f - 28 * (6 - var4);
      } else if (var4 > 0) {
         var7 += var4;
      }

      if (var3) {
         var8 -= 28;
      } else {
         var6 += 64;
         var8 += this.field_147000_g - 4;
      }

      GlStateManager.func_179140_f();
      this.func_73729_b(var7, var8, var5, var6, 28, 32);
      this.field_73735_i = 100.0F;
      this.field_146296_j.field_77023_b = 100.0F;
      var7 += 6;
      var8 += 8 + (var3 ? 1 : -1);
      GlStateManager.func_179145_e();
      GlStateManager.func_179091_B();
      ItemStack var10 = var1.func_151244_d();
      this.field_146296_j.func_180450_b(var10, var7, var8);
      this.field_146296_j.func_175030_a(this.field_146289_q, var10, var7, var8);
      GlStateManager.func_179140_f();
      this.field_146296_j.field_77023_b = 0.0F;
      this.field_73735_i = 0.0F;
   }

   public int func_147056_g() {
      return field_147058_w;
   }

   public static void func_192044_a(Minecraft var0, int var1, boolean var2, boolean var3) {
      EntityPlayerSP var4 = var0.field_71439_g;
      CreativeSettings var5 = var0.func_199403_al();
      HotbarSnapshot var6 = var5.func_192563_a(var1);
      int var7;
      if (var2) {
         for(var7 = 0; var7 < InventoryPlayer.func_70451_h(); ++var7) {
            ItemStack var8 = ((ItemStack)var6.get(var7)).func_77946_l();
            var4.field_71071_by.func_70299_a(var7, var8);
            var0.field_71442_b.func_78761_a(var8, 36 + var7);
         }

         var4.field_71069_bz.func_75142_b();
      } else if (var3) {
         for(var7 = 0; var7 < InventoryPlayer.func_70451_h(); ++var7) {
            var6.set(var7, var4.field_71071_by.func_70301_a(var7).func_77946_l());
         }

         String var9 = var0.field_71474_y.field_151456_ac[var1].func_197978_k();
         String var10 = var0.field_71474_y.field_193630_aq.func_197978_k();
         var0.field_71456_v.func_175188_a(new TextComponentTranslation("inventory.hotbarSaved", new Object[]{var10, var9}), false);
         var5.func_192564_b();
      }

   }

   static {
      field_147058_w = ItemGroup.field_78030_b.func_78021_a();
   }

   static class LockedSlot extends Slot {
      public LockedSlot(IInventory var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean func_82869_a(EntityPlayer var1) {
         if (super.func_82869_a(var1) && this.func_75216_d()) {
            return this.func_75211_c().func_179543_a("CustomCreativeLock") == null;
         } else {
            return !this.func_75216_d();
         }
      }
   }

   class CreativeSlot extends Slot {
      private final Slot field_148332_b;

      public CreativeSlot(Slot var2, int var3) {
         super(var2.field_75224_c, var3, 0, 0);
         this.field_148332_b = var2;
      }

      public ItemStack func_190901_a(EntityPlayer var1, ItemStack var2) {
         this.field_148332_b.func_190901_a(var1, var2);
         return var2;
      }

      public boolean func_75214_a(ItemStack var1) {
         return this.field_148332_b.func_75214_a(var1);
      }

      public ItemStack func_75211_c() {
         return this.field_148332_b.func_75211_c();
      }

      public boolean func_75216_d() {
         return this.field_148332_b.func_75216_d();
      }

      public void func_75215_d(ItemStack var1) {
         this.field_148332_b.func_75215_d(var1);
      }

      public void func_75218_e() {
         this.field_148332_b.func_75218_e();
      }

      public int func_75219_a() {
         return this.field_148332_b.func_75219_a();
      }

      public int func_178170_b(ItemStack var1) {
         return this.field_148332_b.func_178170_b(var1);
      }

      @Nullable
      public String func_178171_c() {
         return this.field_148332_b.func_178171_c();
      }

      public ItemStack func_75209_a(int var1) {
         return this.field_148332_b.func_75209_a(var1);
      }

      public boolean func_75217_a(IInventory var1, int var2) {
         return this.field_148332_b.func_75217_a(var1, var2);
      }

      public boolean func_111238_b() {
         return this.field_148332_b.func_111238_b();
      }

      public boolean func_82869_a(EntityPlayer var1) {
         return this.field_148332_b.func_82869_a(var1);
      }
   }

   public static class ContainerCreative extends Container {
      public NonNullList<ItemStack> field_148330_a = NonNullList.func_191196_a();

      public ContainerCreative(EntityPlayer var1) {
         super();
         InventoryPlayer var2 = var1.field_71071_by;

         int var3;
         for(var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 9; ++var4) {
               this.func_75146_a(new GuiContainerCreative.LockedSlot(GuiContainerCreative.field_195378_x, var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18));
            }
         }

         for(var3 = 0; var3 < 9; ++var3) {
            this.func_75146_a(new Slot(var2, var3, 9 + var3 * 18, 112));
         }

         this.func_148329_a(0.0F);
      }

      public boolean func_75145_c(EntityPlayer var1) {
         return true;
      }

      public void func_148329_a(float var1) {
         int var2 = (this.field_148330_a.size() + 9 - 1) / 9 - 5;
         int var3 = (int)((double)(var1 * (float)var2) + 0.5D);
         if (var3 < 0) {
            var3 = 0;
         }

         for(int var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 9; ++var5) {
               int var6 = var5 + (var4 + var3) * 9;
               if (var6 >= 0 && var6 < this.field_148330_a.size()) {
                  GuiContainerCreative.field_195378_x.func_70299_a(var5 + var4 * 9, (ItemStack)this.field_148330_a.get(var6));
               } else {
                  GuiContainerCreative.field_195378_x.func_70299_a(var5 + var4 * 9, ItemStack.field_190927_a);
               }
            }
         }

      }

      public boolean func_148328_e() {
         return this.field_148330_a.size() > 45;
      }

      public ItemStack func_82846_b(EntityPlayer var1, int var2) {
         if (var2 >= this.field_75151_b.size() - 9 && var2 < this.field_75151_b.size()) {
            Slot var3 = (Slot)this.field_75151_b.get(var2);
            if (var3 != null && var3.func_75216_d()) {
               var3.func_75215_d(ItemStack.field_190927_a);
            }
         }

         return ItemStack.field_190927_a;
      }

      public boolean func_94530_a(ItemStack var1, Slot var2) {
         return var2.field_75221_f > 90;
      }

      public boolean func_94531_b(Slot var1) {
         return var1.field_75224_c instanceof InventoryPlayer || var1.field_75221_f > 90 && var1.field_75223_e <= 162;
      }
   }
}
