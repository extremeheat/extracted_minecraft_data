package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook extends GuiScreen {
   private static final Logger field_146473_a = LogManager.getLogger();
   private static final ResourceLocation field_146466_f = new ResourceLocation("textures/gui/book.png");
   private final EntityPlayer field_146468_g;
   private final ItemStack field_146474_h;
   private final boolean field_146475_i;
   private boolean field_146481_r;
   private boolean field_146480_s;
   private int field_146479_t;
   private int field_146478_u = 192;
   private int field_146477_v = 192;
   private int field_146476_w = 1;
   private int field_146484_x;
   private NBTTagList field_146483_y;
   private String field_146482_z = "";
   private List<IChatComponent> field_175386_A;
   private int field_175387_B = -1;
   private GuiScreenBook.NextPageButton field_146470_A;
   private GuiScreenBook.NextPageButton field_146471_B;
   private GuiButton field_146472_C;
   private GuiButton field_146465_D;
   private GuiButton field_146467_E;
   private GuiButton field_146469_F;

   public GuiScreenBook(EntityPlayer var1, ItemStack var2, boolean var3) {
      super();
      this.field_146468_g = var1;
      this.field_146474_h = var2;
      this.field_146475_i = var3;
      if (var2.func_77942_o()) {
         NBTTagCompound var4 = var2.func_77978_p();
         this.field_146483_y = var4.func_150295_c("pages", 8);
         if (this.field_146483_y != null) {
            this.field_146483_y = (NBTTagList)this.field_146483_y.func_74737_b();
            this.field_146476_w = this.field_146483_y.func_74745_c();
            if (this.field_146476_w < 1) {
               this.field_146476_w = 1;
            }
         }
      }

      if (this.field_146483_y == null && var3) {
         this.field_146483_y = new NBTTagList();
         this.field_146483_y.func_74742_a(new NBTTagString(""));
         this.field_146476_w = 1;
      }

   }

   public void func_73876_c() {
      super.func_73876_c();
      ++this.field_146479_t;
   }

   public void func_73866_w_() {
      this.field_146292_n.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.field_146475_i) {
         this.field_146292_n.add(this.field_146465_D = new GuiButton(3, this.field_146294_l / 2 - 100, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("book.signButton")));
         this.field_146292_n.add(this.field_146472_C = new GuiButton(0, this.field_146294_l / 2 + 2, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("gui.done")));
         this.field_146292_n.add(this.field_146467_E = new GuiButton(5, this.field_146294_l / 2 - 100, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("book.finalizeButton")));
         this.field_146292_n.add(this.field_146469_F = new GuiButton(4, this.field_146294_l / 2 + 2, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("gui.cancel")));
      } else {
         this.field_146292_n.add(this.field_146472_C = new GuiButton(0, this.field_146294_l / 2 - 100, 4 + this.field_146477_v, 200, 20, I18n.func_135052_a("gui.done")));
      }

      int var1 = (this.field_146294_l - this.field_146478_u) / 2;
      byte var2 = 2;
      this.field_146292_n.add(this.field_146470_A = new GuiScreenBook.NextPageButton(1, var1 + 120, var2 + 154, true));
      this.field_146292_n.add(this.field_146471_B = new GuiScreenBook.NextPageButton(2, var1 + 38, var2 + 154, false));
      this.func_146464_h();
   }

   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
   }

   private void func_146464_h() {
      this.field_146470_A.field_146125_m = !this.field_146480_s && (this.field_146484_x < this.field_146476_w - 1 || this.field_146475_i);
      this.field_146471_B.field_146125_m = !this.field_146480_s && this.field_146484_x > 0;
      this.field_146472_C.field_146125_m = !this.field_146475_i || !this.field_146480_s;
      if (this.field_146475_i) {
         this.field_146465_D.field_146125_m = !this.field_146480_s;
         this.field_146469_F.field_146125_m = this.field_146480_s;
         this.field_146467_E.field_146125_m = this.field_146480_s;
         this.field_146467_E.field_146124_l = this.field_146482_z.trim().length() > 0;
      }

   }

   private void func_146462_a(boolean var1) {
      if (this.field_146475_i && this.field_146481_r) {
         if (this.field_146483_y != null) {
            String var2;
            while(this.field_146483_y.func_74745_c() > 1) {
               var2 = this.field_146483_y.func_150307_f(this.field_146483_y.func_74745_c() - 1);
               if (var2.length() != 0) {
                  break;
               }

               this.field_146483_y.func_74744_a(this.field_146483_y.func_74745_c() - 1);
            }

            if (this.field_146474_h.func_77942_o()) {
               NBTTagCompound var6 = this.field_146474_h.func_77978_p();
               var6.func_74782_a("pages", this.field_146483_y);
            } else {
               this.field_146474_h.func_77983_a("pages", this.field_146483_y);
            }

            var2 = "MC|BEdit";
            if (var1) {
               var2 = "MC|BSign";
               this.field_146474_h.func_77983_a("author", new NBTTagString(this.field_146468_g.func_70005_c_()));
               this.field_146474_h.func_77983_a("title", new NBTTagString(this.field_146482_z.trim()));

               for(int var3 = 0; var3 < this.field_146483_y.func_74745_c(); ++var3) {
                  String var4 = this.field_146483_y.func_150307_f(var3);
                  ChatComponentText var5 = new ChatComponentText(var4);
                  var4 = IChatComponent.Serializer.func_150696_a(var5);
                  this.field_146483_y.func_150304_a(var3, new NBTTagString(var4));
               }

               this.field_146474_h.func_150996_a(Items.field_151164_bB);
            }

            PacketBuffer var7 = new PacketBuffer(Unpooled.buffer());
            var7.func_150788_a(this.field_146474_h);
            this.field_146297_k.func_147114_u().func_147297_a(new C17PacketCustomPayload(var2, var7));
         }

      }
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 0) {
            this.field_146297_k.func_147108_a((GuiScreen)null);
            this.func_146462_a(false);
         } else if (var1.field_146127_k == 3 && this.field_146475_i) {
            this.field_146480_s = true;
         } else if (var1.field_146127_k == 1) {
            if (this.field_146484_x < this.field_146476_w - 1) {
               ++this.field_146484_x;
            } else if (this.field_146475_i) {
               this.func_146461_i();
               if (this.field_146484_x < this.field_146476_w - 1) {
                  ++this.field_146484_x;
               }
            }
         } else if (var1.field_146127_k == 2) {
            if (this.field_146484_x > 0) {
               --this.field_146484_x;
            }
         } else if (var1.field_146127_k == 5 && this.field_146480_s) {
            this.func_146462_a(true);
            this.field_146297_k.func_147108_a((GuiScreen)null);
         } else if (var1.field_146127_k == 4 && this.field_146480_s) {
            this.field_146480_s = false;
         }

         this.func_146464_h();
      }
   }

   private void func_146461_i() {
      if (this.field_146483_y != null && this.field_146483_y.func_74745_c() < 50) {
         this.field_146483_y.func_74742_a(new NBTTagString(""));
         ++this.field_146476_w;
         this.field_146481_r = true;
      }
   }

   protected void func_73869_a(char var1, int var2) {
      super.func_73869_a(var1, var2);
      if (this.field_146475_i) {
         if (this.field_146480_s) {
            this.func_146460_c(var1, var2);
         } else {
            this.func_146463_b(var1, var2);
         }

      }
   }

   private void func_146463_b(char var1, int var2) {
      if (GuiScreen.func_175279_e(var2)) {
         this.func_146459_b(GuiScreen.func_146277_j());
      } else {
         switch(var2) {
         case 14:
            String var3 = this.func_146456_p();
            if (var3.length() > 0) {
               this.func_146457_a(var3.substring(0, var3.length() - 1));
            }

            return;
         case 28:
         case 156:
            this.func_146459_b("\n");
            return;
         default:
            if (ChatAllowedCharacters.func_71566_a(var1)) {
               this.func_146459_b(Character.toString(var1));
            }
         }
      }
   }

   private void func_146460_c(char var1, int var2) {
      switch(var2) {
      case 14:
         if (!this.field_146482_z.isEmpty()) {
            this.field_146482_z = this.field_146482_z.substring(0, this.field_146482_z.length() - 1);
            this.func_146464_h();
         }

         return;
      case 28:
      case 156:
         if (!this.field_146482_z.isEmpty()) {
            this.func_146462_a(true);
            this.field_146297_k.func_147108_a((GuiScreen)null);
         }

         return;
      default:
         if (this.field_146482_z.length() < 16 && ChatAllowedCharacters.func_71566_a(var1)) {
            this.field_146482_z = this.field_146482_z + Character.toString(var1);
            this.func_146464_h();
            this.field_146481_r = true;
         }

      }
   }

   private String func_146456_p() {
      return this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.func_74745_c() ? this.field_146483_y.func_150307_f(this.field_146484_x) : "";
   }

   private void func_146457_a(String var1) {
      if (this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.func_74745_c()) {
         this.field_146483_y.func_150304_a(this.field_146484_x, new NBTTagString(var1));
         this.field_146481_r = true;
      }

   }

   private void func_146459_b(String var1) {
      String var2 = this.func_146456_p();
      String var3 = var2 + var1;
      int var4 = this.field_146289_q.func_78267_b(var3 + "" + EnumChatFormatting.BLACK + "_", 118);
      if (var4 <= 128 && var3.length() < 256) {
         this.func_146457_a(var3);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146466_f);
      int var4 = (this.field_146294_l - this.field_146478_u) / 2;
      byte var5 = 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146478_u, this.field_146477_v);
      String var6;
      String var7;
      int var8;
      int var9;
      if (this.field_146480_s) {
         var6 = this.field_146482_z;
         if (this.field_146475_i) {
            if (this.field_146479_t / 6 % 2 == 0) {
               var6 = var6 + "" + EnumChatFormatting.BLACK + "_";
            } else {
               var6 = var6 + "" + EnumChatFormatting.GRAY + "_";
            }
         }

         var7 = I18n.func_135052_a("book.editTitle");
         var8 = this.field_146289_q.func_78256_a(var7);
         this.field_146289_q.func_78276_b(var7, var4 + 36 + (116 - var8) / 2, var5 + 16 + 16, 0);
         var9 = this.field_146289_q.func_78256_a(var6);
         this.field_146289_q.func_78276_b(var6, var4 + 36 + (116 - var9) / 2, var5 + 48, 0);
         String var10 = I18n.func_135052_a("book.byAuthor", this.field_146468_g.func_70005_c_());
         int var11 = this.field_146289_q.func_78256_a(var10);
         this.field_146289_q.func_78276_b(EnumChatFormatting.DARK_GRAY + var10, var4 + 36 + (116 - var11) / 2, var5 + 48 + 10, 0);
         String var12 = I18n.func_135052_a("book.finalizeWarning");
         this.field_146289_q.func_78279_b(var12, var4 + 36, var5 + 80, 116, 0);
      } else {
         var6 = I18n.func_135052_a("book.pageIndicator", this.field_146484_x + 1, this.field_146476_w);
         var7 = "";
         if (this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.func_74745_c()) {
            var7 = this.field_146483_y.func_150307_f(this.field_146484_x);
         }

         if (this.field_146475_i) {
            if (this.field_146289_q.func_78260_a()) {
               var7 = var7 + "_";
            } else if (this.field_146479_t / 6 % 2 == 0) {
               var7 = var7 + "" + EnumChatFormatting.BLACK + "_";
            } else {
               var7 = var7 + "" + EnumChatFormatting.GRAY + "_";
            }
         } else if (this.field_175387_B != this.field_146484_x) {
            if (ItemEditableBook.func_77828_a(this.field_146474_h.func_77978_p())) {
               try {
                  IChatComponent var14 = IChatComponent.Serializer.func_150699_a(var7);
                  this.field_175386_A = var14 != null ? GuiUtilRenderComponents.func_178908_a(var14, 116, this.field_146289_q, true, true) : null;
               } catch (JsonParseException var13) {
                  this.field_175386_A = null;
               }
            } else {
               ChatComponentText var15 = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
               this.field_175386_A = Lists.newArrayList(var15);
            }

            this.field_175387_B = this.field_146484_x;
         }

         var8 = this.field_146289_q.func_78256_a(var6);
         this.field_146289_q.func_78276_b(var6, var4 - var8 + this.field_146478_u - 44, var5 + 16, 0);
         if (this.field_175386_A == null) {
            this.field_146289_q.func_78279_b(var7, var4 + 36, var5 + 16 + 16, 116, 0);
         } else {
            var9 = Math.min(128 / this.field_146289_q.field_78288_b, this.field_175386_A.size());

            for(int var16 = 0; var16 < var9; ++var16) {
               IChatComponent var18 = (IChatComponent)this.field_175386_A.get(var16);
               this.field_146289_q.func_78276_b(var18.func_150260_c(), var4 + 36, var5 + 16 + 16 + var16 * this.field_146289_q.field_78288_b, 0);
            }

            IChatComponent var17 = this.func_175385_b(var1, var2);
            if (var17 != null) {
               this.func_175272_a(var17, var1, var2);
            }
         }
      }

      super.func_73863_a(var1, var2, var3);
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      if (var3 == 0) {
         IChatComponent var4 = this.func_175385_b(var1, var2);
         if (this.func_175276_a(var4)) {
            return;
         }
      }

      super.func_73864_a(var1, var2, var3);
   }

   protected boolean func_175276_a(IChatComponent var1) {
      ClickEvent var2 = var1 == null ? null : var1.func_150256_b().func_150235_h();
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

   public IChatComponent func_175385_b(int var1, int var2) {
      if (this.field_175386_A == null) {
         return null;
      } else {
         int var3 = var1 - (this.field_146294_l - this.field_146478_u) / 2 - 36;
         int var4 = var2 - 2 - 16 - 16;
         if (var3 >= 0 && var4 >= 0) {
            int var5 = Math.min(128 / this.field_146289_q.field_78288_b, this.field_175386_A.size());
            if (var3 <= 116 && var4 < this.field_146297_k.field_71466_p.field_78288_b * var5 + var5) {
               int var6 = var4 / this.field_146297_k.field_71466_p.field_78288_b;
               if (var6 >= 0 && var6 < this.field_175386_A.size()) {
                  IChatComponent var7 = (IChatComponent)this.field_175386_A.get(var6);
                  int var8 = 0;
                  Iterator var9 = var7.iterator();

                  while(var9.hasNext()) {
                     IChatComponent var10 = (IChatComponent)var9.next();
                     if (var10 instanceof ChatComponentText) {
                        var8 += this.field_146297_k.field_71466_p.func_78256_a(((ChatComponentText)var10).func_150265_g());
                        if (var8 > var3) {
                           return var10;
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

   static class NextPageButton extends GuiButton {
      private final boolean field_146151_o;

      public NextPageButton(int var1, int var2, int var3, boolean var4) {
         super(var1, var2, var3, 23, 13, "");
         this.field_146151_o = var4;
      }

      public void func_146112_a(Minecraft var1, int var2, int var3) {
         if (this.field_146125_m) {
            boolean var4 = var2 >= this.field_146128_h && var3 >= this.field_146129_i && var2 < this.field_146128_h + this.field_146120_f && var3 < this.field_146129_i + this.field_146121_g;
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            var1.func_110434_K().func_110577_a(GuiScreenBook.field_146466_f);
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
