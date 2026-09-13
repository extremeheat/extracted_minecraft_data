package net.minecraft.client.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
   private GuiScreenBook$NextPageButton field_146470_A;
   private GuiScreenBook$NextPageButton field_146471_B;
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

   @Override
   public void func_73876_c() {
      super.func_73876_c();
      ++this.field_146479_t;
   }

   @Override
   public void func_73866_w_() {
      this.field_146292_n.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.field_146475_i) {
         this.field_146292_n
            .add(this.field_146465_D = new GuiButton(3, this.field_146294_l / 2 - 100, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("book.signButton")));
         this.field_146292_n
            .add(this.field_146472_C = new GuiButton(0, this.field_146294_l / 2 + 2, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("gui.done")));
         this.field_146292_n
            .add(
               this.field_146467_E = new GuiButton(
                  5, this.field_146294_l / 2 - 100, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("book.finalizeButton")
               )
            );
         this.field_146292_n
            .add(this.field_146469_F = new GuiButton(4, this.field_146294_l / 2 + 2, 4 + this.field_146477_v, 98, 20, I18n.func_135052_a("gui.cancel")));
      } else {
         this.field_146292_n
            .add(this.field_146472_C = new GuiButton(0, this.field_146294_l / 2 - 100, 4 + this.field_146477_v, 200, 20, I18n.func_135052_a("gui.done")));
      }

      int var1 = (this.field_146294_l - this.field_146478_u) / 2;
      byte var2 = 2;
      this.field_146292_n.add(this.field_146470_A = new GuiScreenBook$NextPageButton(1, var1 + 120, var2 + 154, true));
      this.field_146292_n.add(this.field_146471_B = new GuiScreenBook$NextPageButton(2, var1 + 38, var2 + 154, false));
      this.func_146464_h();
   }

   @Override
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
            while(this.field_146483_y.func_74745_c() > 1) {
               String var2 = this.field_146483_y.func_150307_f(this.field_146483_y.func_74745_c() - 1);
               if (var2.length() != 0) {
                  break;
               }

               this.field_146483_y.func_74744_a(this.field_146483_y.func_74745_c() - 1);
            }

            if (this.field_146474_h.func_77942_o()) {
               NBTTagCompound var10 = this.field_146474_h.func_77978_p();
               var10.func_74782_a("pages", this.field_146483_y);
            } else {
               this.field_146474_h.func_77983_a("pages", this.field_146483_y);
            }

            String var11 = "MC|BEdit";
            if (var1) {
               var11 = "MC|BSign";
               this.field_146474_h.func_77983_a("author", new NBTTagString(this.field_146468_g.func_70005_c_()));
               this.field_146474_h.func_77983_a("title", new NBTTagString(this.field_146482_z.trim()));
               this.field_146474_h.func_150996_a(Items.field_151164_bB);
            }

            ByteBuf var3 = Unpooled.buffer();

            try {
               new PacketBuffer(var3).func_150788_a(this.field_146474_h);
               this.field_146297_k.func_147114_u().func_147297_a(new C17PacketCustomPayload(var11, var3));
            } catch (Exception var8) {
               field_146473_a.error("Couldn't send book info", var8);
            } finally {
               var3.release();
            }
         }
      }
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 0) {
            this.field_146297_k.func_147108_a(null);
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
            this.field_146297_k.func_147108_a(null);
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

   @Override
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
      switch(var1) {
         case '\u0016':
            this.func_146459_b(GuiScreen.func_146277_j());
            return;
         default:
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
               this.field_146297_k.func_147108_a(null);
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
      return this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.func_74745_c()
         ? this.field_146483_y.func_150307_f(this.field_146484_x)
         : "";
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
      if (var4 <= 118 && var3.length() < 256) {
         this.func_146457_a(var3);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146466_f);
      int var4 = (this.field_146294_l - this.field_146478_u) / 2;
      byte var5 = 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146478_u, this.field_146477_v);
      if (this.field_146480_s) {
         String var6 = this.field_146482_z;
         if (this.field_146475_i) {
            if (this.field_146479_t / 6 % 2 == 0) {
               var6 = var6 + "" + EnumChatFormatting.BLACK + "_";
            } else {
               var6 = var6 + "" + EnumChatFormatting.GRAY + "_";
            }
         }

         String var7 = I18n.func_135052_a("book.editTitle");
         int var8 = this.field_146289_q.func_78256_a(var7);
         this.field_146289_q.func_78276_b(var7, var4 + 36 + (116 - var8) / 2, var5 + 16 + 16, 0);
         int var9 = this.field_146289_q.func_78256_a(var6);
         this.field_146289_q.func_78276_b(var6, var4 + 36 + (116 - var9) / 2, var5 + 48, 0);
         String var10 = I18n.func_135052_a("book.byAuthor", this.field_146468_g.func_70005_c_());
         int var11 = this.field_146289_q.func_78256_a(var10);
         this.field_146289_q.func_78276_b(EnumChatFormatting.DARK_GRAY + var10, var4 + 36 + (116 - var11) / 2, var5 + 48 + 10, 0);
         String var12 = I18n.func_135052_a("book.finalizeWarning");
         this.field_146289_q.func_78279_b(var12, var4 + 36, var5 + 80, 116, 0);
      } else {
         String var13 = I18n.func_135052_a("book.pageIndicator", this.field_146484_x + 1, this.field_146476_w);
         String var14 = "";
         if (this.field_146483_y != null && this.field_146484_x >= 0 && this.field_146484_x < this.field_146483_y.func_74745_c()) {
            var14 = this.field_146483_y.func_150307_f(this.field_146484_x);
         }

         if (this.field_146475_i) {
            if (this.field_146289_q.func_78260_a()) {
               var14 = var14 + "_";
            } else if (this.field_146479_t / 6 % 2 == 0) {
               var14 = var14 + "" + EnumChatFormatting.BLACK + "_";
            } else {
               var14 = var14 + "" + EnumChatFormatting.GRAY + "_";
            }
         }

         int var15 = this.field_146289_q.func_78256_a(var13);
         this.field_146289_q.func_78276_b(var13, var4 - var15 + this.field_146478_u - 44, var5 + 16, 0);
         this.field_146289_q.func_78279_b(var14, var4 + 36, var5 + 16 + 16, 116, 0);
      }

      super.func_73863_a(var1, var2, var3);
   }
}
