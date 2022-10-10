package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.CPacketEditBook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenBook extends GuiScreen {
   private static final Logger field_146473_a = LogManager.getLogger();
   private static final ResourceLocation field_146466_f = new ResourceLocation("textures/gui/book.png");
   private final EntityPlayer field_146468_g;
   private final ItemStack field_146474_h;
   private final boolean field_146475_i;
   private boolean field_146481_r;
   private boolean field_146480_s;
   private int field_146479_t;
   private final int field_146478_u = 192;
   private final int field_146477_v = 192;
   private int field_146476_w = 1;
   private int field_146484_x;
   private NBTTagList field_146483_y;
   private String field_146482_z = "";
   private List<ITextComponent> field_175386_A;
   private int field_175387_B = -1;
   private GuiScreenBook.NextPageButton field_146470_A;
   private GuiScreenBook.NextPageButton field_146471_B;
   private GuiButton field_146472_C;
   private GuiButton field_146465_D;
   private GuiButton field_146467_E;
   private GuiButton field_146469_F;
   private final EnumHand field_212343_J;

   public GuiScreenBook(EntityPlayer var1, ItemStack var2, boolean var3, EnumHand var4) {
      super();
      this.field_146468_g = var1;
      this.field_146474_h = var2;
      this.field_146475_i = var3;
      this.field_212343_J = var4;
      if (var2.func_77942_o()) {
         NBTTagCompound var5 = var2.func_77978_p();
         this.field_146483_y = var5.func_150295_c("pages", 8).func_74737_b();
         this.field_146476_w = this.field_146483_y.size();
         if (this.field_146476_w < 1) {
            this.field_146483_y.add((INBTBase)(new NBTTagString("")));
            this.field_146476_w = 1;
         }
      }

      if (this.field_146483_y == null && var3) {
         this.field_146483_y = new NBTTagList();
         this.field_146483_y.add((INBTBase)(new NBTTagString("")));
         this.field_146476_w = 1;
      }

   }

   public void func_73876_c() {
      super.func_73876_c();
      ++this.field_146479_t;
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      if (this.field_146475_i) {
         this.field_146465_D = this.func_189646_b(new GuiButton(3, this.field_146294_l / 2 - 100, 196, 98, 20, I18n.func_135052_a("book.signButton")) {
            public void func_194829_a(double var1, double var3) {
               GuiScreenBook.this.field_146480_s = true;
               GuiScreenBook.this.func_146464_h();
            }
         });
         this.field_146472_C = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 + 2, 196, 98, 20, I18n.func_135052_a("gui.done")) {
            public void func_194829_a(double var1, double var3) {
               GuiScreenBook.this.field_146297_k.func_147108_a((GuiScreen)null);
               GuiScreenBook.this.func_146462_a(false);
            }
         });
         this.field_146467_E = this.func_189646_b(new GuiButton(5, this.field_146294_l / 2 - 100, 196, 98, 20, I18n.func_135052_a("book.finalizeButton")) {
            public void func_194829_a(double var1, double var3) {
               if (GuiScreenBook.this.field_146480_s) {
                  GuiScreenBook.this.func_146462_a(true);
                  GuiScreenBook.this.field_146297_k.func_147108_a((GuiScreen)null);
               }

            }
         });
         this.field_146469_F = this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 + 2, 196, 98, 20, I18n.func_135052_a("gui.cancel")) {
            public void func_194829_a(double var1, double var3) {
               if (GuiScreenBook.this.field_146480_s) {
                  GuiScreenBook.this.field_146480_s = false;
               }

               GuiScreenBook.this.func_146464_h();
            }
         });
      } else {
         this.field_146472_C = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, 196, 200, 20, I18n.func_135052_a("gui.done")) {
            public void func_194829_a(double var1, double var3) {
               GuiScreenBook.this.field_146297_k.func_147108_a((GuiScreen)null);
               GuiScreenBook.this.func_146462_a(false);
            }
         });
      }

      int var1 = (this.field_146294_l - 192) / 2;
      boolean var2 = true;
      this.field_146470_A = (GuiScreenBook.NextPageButton)this.func_189646_b(new GuiScreenBook.NextPageButton(1, var1 + 120, 156, true) {
         public void func_194829_a(double var1, double var3) {
            if (GuiScreenBook.this.field_146484_x < GuiScreenBook.this.field_146476_w - 1) {
               GuiScreenBook.this.field_146484_x++;
            } else if (GuiScreenBook.this.field_146475_i) {
               GuiScreenBook.this.func_146461_i();
               if (GuiScreenBook.this.field_146484_x < GuiScreenBook.this.field_146476_w - 1) {
                  GuiScreenBook.this.field_146484_x++;
               }
            }

            GuiScreenBook.this.func_146464_h();
         }
      });
      this.field_146471_B = (GuiScreenBook.NextPageButton)this.func_189646_b(new GuiScreenBook.NextPageButton(2, var1 + 38, 156, false) {
         public void func_194829_a(double var1, double var3) {
            if (GuiScreenBook.this.field_146484_x > 0) {
               GuiScreenBook.this.field_146484_x--;
            }

            GuiScreenBook.this.func_146464_h();
         }
      });
      this.func_146464_h();
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
   }

   private void func_146464_h() {
      this.field_146470_A.field_146125_m = !this.field_146480_s && (this.field_146484_x < this.field_146476_w - 1 || this.field_146475_i);
      this.field_146471_B.field_146125_m = !this.field_146480_s && this.field_146484_x > 0;
      this.field_146472_C.field_146125_m = !this.field_146475_i || !this.field_146480_s;
      if (this.field_146475_i) {
         this.field_146465_D.field_146125_m = !this.field_146480_s;
         this.field_146469_F.field_146125_m = this.field_146480_s;
         this.field_146467_E.field_146125_m = this.field_146480_s;
         this.field_146467_E.field_146124_l = !this.field_146482_z.trim().isEmpty();
      }

   }

   private void func_146462_a(boolean var1) {
      if (this.field_146475_i && this.field_146481_r) {
         if (this.field_146483_y != null) {
            while(this.field_146483_y.size() > 1) {
               String var2 = this.field_146483_y.func_150307_f(this.field_146483_y.size() - 1);
               if (!var2.isEmpty()) {
                  break;
               }

               this.field_146483_y.remove(this.field_146483_y.size() - 1);
            }

            this.field_146474_h.func_77983_a("pages", this.field_146483_y);
            if (var1) {
               this.field_146474_h.func_77983_a("author", new NBTTagString(this.field_146468_g.func_146103_bH().getName()));
               this.field_146474_h.func_77983_a("title", new NBTTagString(this.field_146482_z.trim()));
            }

            this.field_146297_k.func_147114_u().func_147297_a(new CPacketEditBook(this.field_146474_h, var1, this.field_212343_J));
         }

      }
   }

   private void func_146461_i() {
      if (this.field_146483_y != null && this.field_146483_y.size() < 50) {
         this.field_146483_y.add((INBTBase)(new NBTTagString("")));
         ++this.field_146476_w;
         this.field_146481_r = true;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (this.field_146475_i) {
         return this.field_146480_s ? this.func_195267_b(var1, var2, var3) : this.func_195259_a(var1, var2, var3);
      } else {
         return false;
      }
   }

   public boolean charTyped(char var1, int var2) {
      if (super.charTyped(var1, var2)) {
         return true;
      } else if (this.field_146475_i) {
         if (this.field_146480_s) {
            if (this.field_146482_z.length() < 16 && SharedConstants.func_71566_a(var1)) {
               this.field_146482_z = this.field_146482_z + Character.toString(var1);
               this.func_146464_h();
               this.field_146481_r = true;
               return true;
            } else {
               return false;
            }
         } else if (SharedConstants.func_71566_a(var1)) {
            this.func_146459_b(Character.toString(var1));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean func_195259_a(int var1, int var2, int var3) {
      if (GuiScreen.func_175279_e(var1)) {
         this.func_146459_b(this.field_146297_k.field_195559_v.func_197965_a());
         return true;
      } else {
         switch(var1) {
         case 257:
         case 335:
            this.func_146459_b("\n");
            return true;
         case 259:
            String var4 = this.func_146456_p();
            if (!var4.isEmpty()) {
               this.func_146457_a(var4.substring(0, var4.length() - 1));
            }

            return true;
         default:
            return false;
         }
      }
   }

   private boolean func_195267_b(int var1, int var2, int var3) {
      switch(var1) {
      case 257:
      case 335:
         if (!this.field_146482_z.isEmpty()) {
            this.func_146462_a(true);
            this.field_146297_k.func_147108_a((GuiScreen)null);
         }

         return true;
      case 259:
         if (!this.field_146482_z.isEmpty()) {
            this.field_146482_z = this.field_146482_z.substring(0, this.field_146482_z.length() - 1);
            this.func_146464_h();
         }

         return true;
      default:
         return false;
      }
   }

   private String func_146456_p() {
      return this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.size() ? this.field_146483_y.func_150307_f(this.field_146484_x) : "";
   }

   private void func_146457_a(String var1) {
      if (this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.size()) {
         this.field_146483_y.set(this.field_146484_x, (INBTBase)(new NBTTagString(var1)));
         this.field_146481_r = true;
      }

   }

   private void func_146459_b(String var1) {
      String var2 = this.func_146456_p();
      String var3 = var2 + var1;
      int var4 = this.field_146289_q.func_78267_b(var3 + "" + TextFormatting.BLACK + "_", 118);
      if (var4 <= 128 && var3.length() < 256) {
         this.func_146457_a(var3);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146466_f);
      int var4 = (this.field_146294_l - 192) / 2;
      boolean var5 = true;
      this.func_73729_b(var4, 2, 0, 0, 192, 192);
      String var6;
      String var7;
      int var8;
      int var9;
      if (this.field_146480_s) {
         var6 = this.field_146482_z;
         if (this.field_146475_i) {
            if (this.field_146479_t / 6 % 2 == 0) {
               var6 = var6 + "" + TextFormatting.BLACK + "_";
            } else {
               var6 = var6 + "" + TextFormatting.GRAY + "_";
            }
         }

         var7 = I18n.func_135052_a("book.editTitle");
         var8 = this.field_146289_q.func_78256_a(var7);
         this.field_146289_q.func_211126_b(var7, (float)(var4 + 36 + (116 - var8) / 2), 34.0F, 0);
         var9 = this.field_146289_q.func_78256_a(var6);
         this.field_146289_q.func_211126_b(var6, (float)(var4 + 36 + (116 - var9) / 2), 50.0F, 0);
         String var10 = I18n.func_135052_a("book.byAuthor", this.field_146468_g.func_200200_C_().getString());
         int var11 = this.field_146289_q.func_78256_a(var10);
         this.field_146289_q.func_211126_b(TextFormatting.DARK_GRAY + var10, (float)(var4 + 36 + (116 - var11) / 2), 60.0F, 0);
         String var12 = I18n.func_135052_a("book.finalizeWarning");
         this.field_146289_q.func_78279_b(var12, var4 + 36, 82, 116, 0);
      } else {
         var6 = I18n.func_135052_a("book.pageIndicator", this.field_146484_x + 1, this.field_146476_w);
         var7 = "";
         if (this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.size()) {
            var7 = this.field_146483_y.func_150307_f(this.field_146484_x);
         }

         if (this.field_146475_i) {
            if (this.field_146289_q.func_78260_a()) {
               var7 = var7 + "_";
            } else if (this.field_146479_t / 6 % 2 == 0) {
               var7 = var7 + "" + TextFormatting.BLACK + "_";
            } else {
               var7 = var7 + "" + TextFormatting.GRAY + "_";
            }
         } else if (this.field_175387_B != this.field_146484_x) {
            if (ItemWrittenBook.func_77828_a(this.field_146474_h.func_77978_p())) {
               try {
                  ITextComponent var14 = ITextComponent.Serializer.func_150699_a(var7);
                  this.field_175386_A = var14 != null ? GuiUtilRenderComponents.func_178908_a(var14, 116, this.field_146289_q, true, true) : null;
               } catch (JsonParseException var13) {
                  this.field_175386_A = null;
               }
            } else {
               this.field_175386_A = Lists.newArrayList((new TextComponentTranslation("book.invalid.tag", new Object[0])).func_211708_a(TextFormatting.DARK_RED));
            }

            this.field_175387_B = this.field_146484_x;
         }

         var8 = this.field_146289_q.func_78256_a(var6);
         this.field_146289_q.func_211126_b(var6, (float)(var4 - var8 + 192 - 44), 18.0F, 0);
         if (this.field_175386_A == null) {
            this.field_146289_q.func_78279_b(var7, var4 + 36, 34, 116, 0);
         } else {
            var9 = Math.min(128 / this.field_146289_q.field_78288_b, this.field_175386_A.size());

            for(int var15 = 0; var15 < var9; ++var15) {
               ITextComponent var17 = (ITextComponent)this.field_175386_A.get(var15);
               this.field_146289_q.func_211126_b(var17.func_150254_d(), (float)(var4 + 36), (float)(34 + var15 * this.field_146289_q.field_78288_b), 0);
            }

            ITextComponent var16 = this.func_195260_a((double)var1, (double)var2);
            if (var16 != null) {
               this.func_175272_a(var16, var1, var2);
            }
         }
      }

      super.func_73863_a(var1, var2, var3);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         ITextComponent var6 = this.func_195260_a(var1, var3);
         if (var6 != null && this.func_175276_a(var6)) {
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean func_175276_a(ITextComponent var1) {
      ClickEvent var2 = var1.func_150256_b().func_150235_h();
      if (var2 == null) {
         return false;
      } else if (var2.func_150669_a() == ClickEvent.Action.CHANGE_PAGE) {
         String var6 = var2.func_150668_b();

         try {
            int var4 = Integer.parseInt(var6) - 1;
            if (var4 >= 0 && var4 < this.field_146476_w && var4 != this.field_146484_x) {
               this.field_146484_x = var4;
               this.func_146464_h();
               return true;
            }
         } catch (Throwable var5) {
         }

         return false;
      } else {
         boolean var3 = super.func_175276_a(var1);
         if (var3 && var2.func_150669_a() == ClickEvent.Action.RUN_COMMAND) {
            this.field_146297_k.func_147108_a((GuiScreen)null);
         }

         return var3;
      }
   }

   @Nullable
   public ITextComponent func_195260_a(double var1, double var3) {
      if (this.field_175386_A == null) {
         return null;
      } else {
         int var5 = MathHelper.func_76128_c(var1 - (double)((this.field_146294_l - 192) / 2) - 36.0D);
         int var6 = MathHelper.func_76128_c(var3 - 2.0D - 16.0D - 16.0D);
         if (var5 >= 0 && var6 >= 0) {
            int var7 = Math.min(128 / this.field_146289_q.field_78288_b, this.field_175386_A.size());
            if (var5 <= 116 && var6 < this.field_146297_k.field_71466_p.field_78288_b * var7 + var7) {
               int var8 = var6 / this.field_146297_k.field_71466_p.field_78288_b;
               if (var8 >= 0 && var8 < this.field_175386_A.size()) {
                  ITextComponent var9 = (ITextComponent)this.field_175386_A.get(var8);
                  int var10 = 0;
                  Iterator var11 = var9.iterator();

                  while(var11.hasNext()) {
                     ITextComponent var12 = (ITextComponent)var11.next();
                     if (var12 instanceof TextComponentString) {
                        var10 += this.field_146297_k.field_71466_p.func_78256_a(var12.func_150254_d());
                        if (var10 > var5) {
                           return var12;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   abstract static class NextPageButton extends GuiButton {
      private final boolean field_146151_o;

      public NextPageButton(int var1, int var2, int var3, boolean var4) {
         super(var1, var2, var3, 23, 13, "");
         this.field_146151_o = var4;
      }

      public void func_194828_a(int var1, int var2, float var3) {
         if (this.field_146125_m) {
            boolean var4 = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.func_71410_x().func_110434_K().func_110577_a(GuiScreenBook.field_146466_f);
            int var5 = 0;
            int var6 = 192;
            if (var4) {
               var5 += 23;
            }

            if (!this.field_146151_o) {
               var6 += 13;
            }

            this.func_73729_b(this.field_146128_h, this.field_146129_i, var5, var6, 23, 13);
         }
      }
   }
}
