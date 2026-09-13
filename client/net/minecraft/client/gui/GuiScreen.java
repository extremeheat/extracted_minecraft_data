package net.minecraft.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends Gui {
   protected static RenderItem field_146296_j = new RenderItem();
   protected Minecraft field_146297_k;
   public int field_146294_l;
   public int field_146295_m;
   protected List field_146292_n = new ArrayList();
   protected List field_146293_o = new ArrayList();
   public boolean field_146291_p;
   protected FontRenderer field_146289_q;
   private GuiButton field_146290_a;
   private int field_146287_f;
   private long field_146288_g;
   private int field_146298_h;

   public GuiScreen() {
      super();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      for(int var4 = 0; var4 < this.field_146292_n.size(); ++var4) {
         ((GuiButton)this.field_146292_n.get(var4)).func_146112_a(this.field_146297_k, var1, var2);
      }

      for(int var5 = 0; var5 < this.field_146293_o.size(); ++var5) {
         ((GuiLabel)this.field_146293_o.get(var5)).func_146159_a(this.field_146297_k, var1, var2);
      }
   }

   protected void func_73869_a(char var1, int var2) {
      if (var2 == 1) {
         this.field_146297_k.func_147108_a(null);
         this.field_146297_k.func_71381_h();
      }
   }

   public static String func_146277_j() {
      try {
         Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)var0.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception var1) {
      }

      return "";
   }

   public static void func_146275_d(String var0) {
      try {
         StringSelection var1 = new StringSelection(var0);
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, null);
      } catch (Exception var2) {
      }
   }

   protected void func_146285_a(ItemStack var1, int var2, int var3) {
      List var4 = var1.func_82840_a(this.field_146297_k.field_71439_g, this.field_146297_k.field_71474_y.field_82882_x);

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         if (var5 == 0) {
            var4.set(var5, var1.func_77953_t().field_77937_e + (String)var4.get(var5));
         } else {
            var4.set(var5, EnumChatFormatting.GRAY + (String)var4.get(var5));
         }
      }

      this.func_146283_a(var4, var2, var3);
   }

   protected void func_146279_a(String var1, int var2, int var3) {
      this.func_146283_a(Arrays.asList(var1), var2, var3);
   }

   protected void func_146283_a(List var1, int var2, int var3) {
      if (!var1.isEmpty()) {
         GL11.glDisable(32826);
         RenderHelper.func_74518_a();
         GL11.glDisable(2896);
         GL11.glDisable(2929);
         int var4 = 0;

         for(String var6 : var1) {
            int var7 = this.field_146289_q.func_78256_a(var6);
            if (var7 > var4) {
               var4 = var7;
            }
         }

         int var14 = var2 + 12;
         int var15 = var3 - 12;
         int var8 = 8;
         if (var1.size() > 1) {
            var8 += 2 + (var1.size() - 1) * 10;
         }

         if (var14 + var4 > this.field_146294_l) {
            var14 -= 28 + var4;
         }

         if (var15 + var8 + 6 > this.field_146295_m) {
            var15 = this.field_146295_m - var8 - 6;
         }

         this.field_73735_i = 300.0F;
         field_146296_j.field_77023_b = 300.0F;
         int var9 = -267386864;
         this.func_73733_a(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
         this.func_73733_a(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
         this.func_73733_a(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
         this.func_73733_a(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
         this.func_73733_a(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
         int var10 = 1347420415;
         int var11 = (var10 & 16711422) >> 1 | var10 & 0xFF000000;
         this.func_73733_a(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
         this.func_73733_a(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
         this.func_73733_a(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
         this.func_73733_a(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

         for(int var12 = 0; var12 < var1.size(); ++var12) {
            String var13 = (String)var1.get(var12);
            this.field_146289_q.func_78261_a(var13, var14, var15, -1);
            if (var12 == 0) {
               var15 += 2;
            }

            var15 += 10;
         }

         this.field_73735_i = 0.0F;
         field_146296_j.field_77023_b = 0.0F;
         GL11.glEnable(2896);
         GL11.glEnable(2929);
         RenderHelper.func_74519_b();
         GL11.glEnable(32826);
      }
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      if (var3 == 0) {
         for(int var4 = 0; var4 < this.field_146292_n.size(); ++var4) {
            GuiButton var5 = (GuiButton)this.field_146292_n.get(var4);
            if (var5.func_146116_c(this.field_146297_k, var1, var2)) {
               this.field_146290_a = var5;
               var5.func_146113_a(this.field_146297_k.func_147118_V());
               this.func_146284_a(var5);
            }
         }
      }
   }

   protected void func_146286_b(int var1, int var2, int var3) {
      if (this.field_146290_a != null && var3 == 0) {
         this.field_146290_a.func_146118_a(var1, var2);
         this.field_146290_a = null;
      }
   }

   protected void func_146273_a(int var1, int var2, int var3, long var4) {
   }

   protected void func_146284_a(GuiButton var1) {
   }

   public void func_146280_a(Minecraft var1, int var2, int var3) {
      this.field_146297_k = var1;
      this.field_146289_q = var1.field_71466_p;
      this.field_146294_l = var2;
      this.field_146295_m = var3;
      this.field_146292_n.clear();
      this.func_73866_w_();
   }

   public void func_73866_w_() {
   }

   public void func_146269_k() {
      if (Mouse.isCreated()) {
         while(Mouse.next()) {
            this.func_146274_d();
         }
      }

      if (Keyboard.isCreated()) {
         while(Keyboard.next()) {
            this.func_146282_l();
         }
      }
   }

   public void func_146274_d() {
      int var1 = Mouse.getEventX() * this.field_146294_l / this.field_146297_k.field_71443_c;
      int var2 = this.field_146295_m - Mouse.getEventY() * this.field_146295_m / this.field_146297_k.field_71440_d - 1;
      int var3 = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
         if (this.field_146297_k.field_71474_y.field_85185_A && this.field_146298_h++ > 0) {
            return;
         }

         this.field_146287_f = var3;
         this.field_146288_g = Minecraft.func_71386_F();
         this.func_73864_a(var1, var2, this.field_146287_f);
      } else if (var3 != -1) {
         if (this.field_146297_k.field_71474_y.field_85185_A && --this.field_146298_h > 0) {
            return;
         }

         this.field_146287_f = -1;
         this.func_146286_b(var1, var2, var3);
      } else if (this.field_146287_f != -1 && this.field_146288_g > 0L) {
         long var4 = Minecraft.func_71386_F() - this.field_146288_g;
         this.func_146273_a(var1, var2, this.field_146287_f, var4);
      }
   }

   public void func_146282_l() {
      if (Keyboard.getEventKeyState()) {
         this.func_73869_a(Keyboard.getEventCharacter(), Keyboard.getEventKey());
      }

      this.field_146297_k.func_152348_aa();
   }

   public void func_73876_c() {
   }

   public void func_146281_b() {
   }

   public void func_146276_q_() {
      this.func_146270_b(0);
   }

   public void func_146270_b(int var1) {
      if (this.field_146297_k.field_71441_e != null) {
         this.func_73733_a(0, 0, this.field_146294_l, this.field_146295_m, -1072689136, -804253680);
      } else {
         this.func_146278_c(var1);
      }
   }

   public void func_146278_c(int var1) {
      GL11.glDisable(2896);
      GL11.glDisable(2912);
      Tessellator var2 = Tessellator.field_78398_a;
      this.field_146297_k.func_110434_K().func_110577_a(field_110325_k);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var3 = 32.0F;
      var2.func_78382_b();
      var2.func_78378_d(4210752);
      var2.func_78374_a(0.0, (double)this.field_146295_m, 0.0, 0.0, (double)((float)this.field_146295_m / var3 + (float)var1));
      var2.func_78374_a(
         (double)this.field_146294_l,
         (double)this.field_146295_m,
         0.0,
         (double)((float)this.field_146294_l / var3),
         (double)((float)this.field_146295_m / var3 + (float)var1)
      );
      var2.func_78374_a((double)this.field_146294_l, 0.0, 0.0, (double)((float)this.field_146294_l / var3), (double)var1);
      var2.func_78374_a(0.0, 0.0, 0.0, 0.0, (double)var1);
      var2.func_78381_a();
   }

   public boolean func_73868_f() {
      return true;
   }

   public void func_73878_a(boolean var1, int var2) {
   }

   public static boolean func_146271_m() {
      if (Minecraft.field_142025_a) {
         return Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220);
      } else {
         return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
      }
   }

   public static boolean func_146272_n() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }
}
