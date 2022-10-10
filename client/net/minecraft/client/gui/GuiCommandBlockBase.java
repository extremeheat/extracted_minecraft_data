package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiCommandBlockBase extends GuiScreen {
   protected GuiTextField field_195237_a;
   protected GuiTextField field_195239_f;
   protected GuiButton field_195240_g;
   protected GuiButton field_195241_h;
   protected GuiButton field_195242_i;
   protected boolean field_195238_s;
   protected final List<String> field_209111_t = Lists.newArrayList();
   protected int field_209112_u;
   protected int field_209113_v;
   protected ParseResults<ISuggestionProvider> field_209114_w;
   protected CompletableFuture<Suggestions> field_209115_x;
   protected GuiCommandBlockBase.SuggestionsList field_209116_y;
   private boolean field_212342_z;

   public GuiCommandBlockBase() {
      super();
   }

   public void func_73876_c() {
      this.field_195237_a.func_146178_a();
   }

   abstract CommandBlockBaseLogic func_195231_h();

   abstract int func_195236_i();

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      this.field_195240_g = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 4 - 150, this.field_146295_m / 4 + 120 + 12, 150, 20, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiCommandBlockBase.this.func_195234_k();
         }
      });
      this.field_195241_h = this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 + 4, this.field_146295_m / 4 + 120 + 12, 150, 20, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiCommandBlockBase.this.func_195232_m();
         }
      });
      this.field_195242_i = this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 + 150 - 20, this.func_195236_i(), 20, 20, "O") {
         public void func_194829_a(double var1, double var3) {
            CommandBlockBaseLogic var5 = GuiCommandBlockBase.this.func_195231_h();
            var5.func_175573_a(!var5.func_175571_m());
            GuiCommandBlockBase.this.func_195233_j();
         }
      });
      this.field_195237_a = new GuiTextField(2, this.field_146289_q, this.field_146294_l / 2 - 150, 50, 300, 20) {
         public void func_146195_b(boolean var1) {
            super.func_146195_b(var1);
            if (var1) {
               GuiCommandBlockBase.this.field_195239_f.func_146195_b(false);
            }

         }
      };
      this.field_195237_a.func_146203_f(32500);
      this.field_195237_a.func_195607_a(this::func_209104_a);
      this.field_195237_a.func_195609_a(this::func_209103_a);
      this.field_195124_j.add(this.field_195237_a);
      this.field_195239_f = new GuiTextField(3, this.field_146289_q, this.field_146294_l / 2 - 150, this.func_195236_i(), 276, 20) {
         public void func_146195_b(boolean var1) {
            super.func_146195_b(var1);
            if (var1) {
               GuiCommandBlockBase.this.field_195237_a.func_146195_b(false);
            }

         }
      };
      this.field_195239_f.func_146203_f(32500);
      this.field_195239_f.func_146184_c(false);
      this.field_195239_f.func_146180_a("-");
      this.field_195124_j.add(this.field_195239_f);
      this.field_195237_a.func_146195_b(true);
      this.func_195073_a(this.field_195237_a);
      this.func_209106_o();
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      String var4 = this.field_195237_a.func_146179_b();
      this.func_146280_a(var1, var2, var3);
      this.func_209102_a(var4);
      this.func_209106_o();
   }

   protected void func_195233_j() {
      if (this.func_195231_h().func_175571_m()) {
         this.field_195242_i.field_146126_j = "O";
         this.field_195239_f.func_146180_a(this.func_195231_h().func_145749_h().getString());
      } else {
         this.field_195242_i.field_146126_j = "X";
         this.field_195239_f.func_146180_a("-");
      }

   }

   protected void func_195234_k() {
      CommandBlockBaseLogic var1 = this.func_195231_h();
      this.func_195235_a(var1);
      if (!var1.func_175571_m()) {
         var1.func_145750_b((ITextComponent)null);
      }

      this.field_146297_k.func_147108_a((GuiScreen)null);
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
   }

   protected abstract void func_195235_a(CommandBlockBaseLogic var1);

   protected void func_195232_m() {
      this.func_195231_h().func_175573_a(this.field_195238_s);
      this.field_146297_k.func_147108_a((GuiScreen)null);
   }

   public void func_195122_V_() {
      this.func_195232_m();
   }

   private void func_209103_a(int var1, String var2) {
      this.func_209106_o();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 != 257 && var1 != 335) {
         if (this.field_209116_y != null && this.field_209116_y.func_209133_b(var1, var2, var3)) {
            return true;
         } else {
            if (var1 == 258) {
               this.func_209109_s();
            }

            return super.keyPressed(var1, var2, var3);
         }
      } else {
         this.func_195234_k();
         return true;
      }
   }

   public boolean mouseScrolled(double var1) {
      return this.field_209116_y != null && this.field_209116_y.func_209232_a(MathHelper.func_151237_a(var1, -1.0D, 1.0D)) ? true : super.mouseScrolled(var1);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.field_209116_y != null && this.field_209116_y.func_209233_a((int)var1, (int)var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   protected void func_209106_o() {
      this.field_209114_w = null;
      if (!this.field_212342_z) {
         this.field_195237_a.func_195612_c((String)null);
         this.field_209116_y = null;
      }

      this.field_209111_t.clear();
      CommandDispatcher var1 = this.field_146297_k.field_71439_g.field_71174_a.func_195515_i();
      String var2 = this.field_195237_a.func_146179_b();
      StringReader var3 = new StringReader(var2);
      if (var3.canRead() && var3.peek() == '/') {
         var3.skip();
      }

      this.field_209114_w = var1.parse(var3, this.field_146297_k.field_71439_g.field_71174_a.func_195513_b());
      if (this.field_209116_y == null || !this.field_212342_z) {
         StringReader var4 = new StringReader(var2.substring(0, Math.min(var2.length(), this.field_195237_a.func_146198_h())));
         if (var4.canRead() && var4.peek() == '/') {
            var4.skip();
         }

         ParseResults var5 = var1.parse(var4, this.field_146297_k.field_71439_g.field_71174_a.func_195513_b());
         this.field_209115_x = var1.getCompletionSuggestions(var5);
         this.field_209115_x.thenRun(() -> {
            if (this.field_209115_x.isDone()) {
               this.func_209107_u();
            }
         });
      }

   }

   private void func_209107_u() {
      if (((Suggestions)this.field_209115_x.join()).isEmpty() && !this.field_209114_w.getExceptions().isEmpty() && this.field_195237_a.func_146198_h() == this.field_195237_a.func_146179_b().length()) {
         int var1 = 0;
         Iterator var2 = this.field_209114_w.getExceptions().entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            CommandSyntaxException var4 = (CommandSyntaxException)var3.getValue();
            if (var4.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
               ++var1;
            } else {
               this.field_209111_t.add(var4.getMessage());
            }
         }

         if (var1 > 0) {
            this.field_209111_t.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
         }
      }

      this.field_209112_u = 0;
      this.field_209113_v = this.field_146294_l;
      if (this.field_209111_t.isEmpty()) {
         this.func_209108_a(TextFormatting.GRAY);
      }

      this.field_209116_y = null;
      if (this.field_146297_k.field_71474_y.field_198018_T) {
         this.func_209109_s();
      }

   }

   private String func_209104_a(String var1, int var2) {
      return this.field_209114_w != null ? GuiChat.func_212336_a(this.field_209114_w, var1, var2) : var1;
   }

   private void func_209108_a(TextFormatting var1) {
      CommandContextBuilder var2 = this.field_209114_w.getContext();
      CommandContextBuilder var3 = var2.getLastChild();
      if (!var3.getNodes().isEmpty()) {
         CommandNode var4;
         int var5;
         Entry var6;
         if (this.field_209114_w.getReader().canRead()) {
            var6 = (Entry)Iterables.getLast(var3.getNodes().entrySet());
            var4 = (CommandNode)var6.getKey();
            var5 = ((StringRange)var6.getValue()).getEnd() + 1;
         } else if (var3.getNodes().size() > 1) {
            var6 = (Entry)Iterables.get(var3.getNodes().entrySet(), var3.getNodes().size() - 2);
            var4 = (CommandNode)var6.getKey();
            var5 = ((StringRange)var6.getValue()).getEnd() + 1;
         } else {
            if (var2 == var3 || var3.getNodes().isEmpty()) {
               return;
            }

            var6 = (Entry)Iterables.getLast(var3.getNodes().entrySet());
            var4 = (CommandNode)var6.getKey();
            var5 = ((StringRange)var6.getValue()).getEnd() + 1;
         }

         Map var11 = this.field_146297_k.field_71439_g.field_71174_a.func_195515_i().getSmartUsage(var4, this.field_146297_k.field_71439_g.field_71174_a.func_195513_b());
         ArrayList var7 = Lists.newArrayList();
         int var8 = 0;
         Iterator var9 = var11.entrySet().iterator();

         while(var9.hasNext()) {
            Entry var10 = (Entry)var9.next();
            if (!(var10.getKey() instanceof LiteralCommandNode)) {
               var7.add(var1 + (String)var10.getValue());
               var8 = Math.max(var8, this.field_146289_q.func_78256_a((String)var10.getValue()));
            }
         }

         if (!var7.isEmpty()) {
            this.field_209111_t.addAll(var7);
            this.field_209112_u = MathHelper.func_76125_a(this.field_195237_a.func_195611_j(var5) + this.field_146289_q.func_78256_a(" "), 0, this.field_195237_a.func_195611_j(0) + this.field_146289_q.func_78256_a(" ") + this.field_195237_a.func_146200_o() - var8);
            this.field_209113_v = var8;
         }

      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("advMode.setCommand"), this.field_146294_l / 2, 20, 16777215);
      this.func_73731_b(this.field_146289_q, I18n.func_135052_a("advMode.command"), this.field_146294_l / 2 - 150, 40, 10526880);
      this.field_195237_a.func_195608_a(var1, var2, var3);
      byte var4 = 75;
      int var7;
      if (!this.field_195239_f.func_146179_b().isEmpty()) {
         var7 = var4 + (5 * this.field_146289_q.field_78288_b + 1 + this.func_195236_i() - 135);
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("advMode.previousOutput"), this.field_146294_l / 2 - 150, var7 + 4, 10526880);
         this.field_195239_f.func_195608_a(var1, var2, var3);
      }

      super.func_73863_a(var1, var2, var3);
      if (this.field_209116_y != null) {
         this.field_209116_y.func_209129_a(var1, var2);
      } else {
         var7 = 0;

         for(Iterator var5 = this.field_209111_t.iterator(); var5.hasNext(); ++var7) {
            String var6 = (String)var5.next();
            func_73734_a(this.field_209112_u - 1, 72 + 12 * var7, this.field_209112_u + this.field_209113_v + 1, 84 + 12 * var7, -2147483648);
            this.field_146289_q.func_175063_a(var6, (float)this.field_209112_u, (float)(74 + 12 * var7), -1);
         }
      }

   }

   public void func_209109_s() {
      if (this.field_209115_x != null && this.field_209115_x.isDone()) {
         Suggestions var1 = (Suggestions)this.field_209115_x.join();
         if (!var1.isEmpty()) {
            int var2 = 0;

            Suggestion var4;
            for(Iterator var3 = var1.getList().iterator(); var3.hasNext(); var2 = Math.max(var2, this.field_146289_q.func_78256_a(var4.getText()))) {
               var4 = (Suggestion)var3.next();
            }

            int var5 = MathHelper.func_76125_a(this.field_195237_a.func_195611_j(var1.getRange().getStart()) + this.field_146289_q.func_78256_a(" "), 0, this.field_195237_a.func_195611_j(0) + this.field_146289_q.func_78256_a(" ") + this.field_195237_a.func_146200_o() - var2);
            this.field_209116_y = new GuiCommandBlockBase.SuggestionsList(var5, 72, var2, var1);
         }
      }

   }

   protected void func_209102_a(String var1) {
      this.field_195237_a.func_146180_a(var1);
   }

   @Nullable
   private static String func_212339_b(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   class SuggestionsList {
      private final Rectangle2d field_209135_b;
      private final Suggestions field_209136_c;
      private final String field_212467_d;
      private int field_209138_e;
      private int field_209139_f;
      private Vec2f field_209140_g;
      private boolean field_209141_h;

      private SuggestionsList(int var2, int var3, int var4, Suggestions var5) {
         super();
         this.field_209140_g = Vec2f.field_189974_a;
         this.field_209135_b = new Rectangle2d(var2 - 1, var3, var4 + 1, Math.min(var5.getList().size(), 7) * 12);
         this.field_209136_c = var5;
         this.field_212467_d = GuiCommandBlockBase.this.field_195237_a.func_146179_b();
         this.func_209130_b(0);
      }

      public void func_209129_a(int var1, int var2) {
         int var3 = Math.min(this.field_209136_c.getList().size(), 7);
         int var4 = -2147483648;
         int var5 = -5592406;
         boolean var6 = this.field_209138_e > 0;
         boolean var7 = this.field_209136_c.getList().size() > this.field_209138_e + var3;
         boolean var8 = var6 || var7;
         boolean var9 = this.field_209140_g.field_189982_i != (float)var1 || this.field_209140_g.field_189983_j != (float)var2;
         if (var9) {
            this.field_209140_g = new Vec2f((float)var1, (float)var2);
         }

         if (var8) {
            Gui.func_73734_a(this.field_209135_b.func_199318_a(), this.field_209135_b.func_199319_b() - 1, this.field_209135_b.func_199318_a() + this.field_209135_b.func_199316_c(), this.field_209135_b.func_199319_b(), -2147483648);
            Gui.func_73734_a(this.field_209135_b.func_199318_a(), this.field_209135_b.func_199319_b() + this.field_209135_b.func_199317_d(), this.field_209135_b.func_199318_a() + this.field_209135_b.func_199316_c(), this.field_209135_b.func_199319_b() + this.field_209135_b.func_199317_d() + 1, -2147483648);
            int var10;
            if (var6) {
               for(var10 = 0; var10 < this.field_209135_b.func_199316_c(); ++var10) {
                  if (var10 % 2 == 0) {
                     Gui.func_73734_a(this.field_209135_b.func_199318_a() + var10, this.field_209135_b.func_199319_b() - 1, this.field_209135_b.func_199318_a() + var10 + 1, this.field_209135_b.func_199319_b(), -1);
                  }
               }
            }

            if (var7) {
               for(var10 = 0; var10 < this.field_209135_b.func_199316_c(); ++var10) {
                  if (var10 % 2 == 0) {
                     Gui.func_73734_a(this.field_209135_b.func_199318_a() + var10, this.field_209135_b.func_199319_b() + this.field_209135_b.func_199317_d(), this.field_209135_b.func_199318_a() + var10 + 1, this.field_209135_b.func_199319_b() + this.field_209135_b.func_199317_d() + 1, -1);
                  }
               }
            }
         }

         boolean var13 = false;

         for(int var11 = 0; var11 < var3; ++var11) {
            Suggestion var12 = (Suggestion)this.field_209136_c.getList().get(var11 + this.field_209138_e);
            Gui.func_73734_a(this.field_209135_b.func_199318_a(), this.field_209135_b.func_199319_b() + 12 * var11, this.field_209135_b.func_199318_a() + this.field_209135_b.func_199316_c(), this.field_209135_b.func_199319_b() + 12 * var11 + 12, -2147483648);
            if (var1 > this.field_209135_b.func_199318_a() && var1 < this.field_209135_b.func_199318_a() + this.field_209135_b.func_199316_c() && var2 > this.field_209135_b.func_199319_b() + 12 * var11 && var2 < this.field_209135_b.func_199319_b() + 12 * var11 + 12) {
               if (var9) {
                  this.func_209130_b(var11 + this.field_209138_e);
               }

               var13 = true;
            }

            GuiCommandBlockBase.this.field_146289_q.func_175063_a(var12.getText(), (float)(this.field_209135_b.func_199318_a() + 1), (float)(this.field_209135_b.func_199319_b() + 2 + 12 * var11), var11 + this.field_209138_e == this.field_209139_f ? -256 : -5592406);
         }

         if (var13) {
            Message var14 = ((Suggestion)this.field_209136_c.getList().get(this.field_209139_f)).getTooltip();
            if (var14 != null) {
               GuiCommandBlockBase.this.func_146279_a(TextComponentUtils.func_202465_a(var14).func_150254_d(), var1, var2);
            }
         }

      }

      public boolean func_209233_a(int var1, int var2, int var3) {
         if (!this.field_209135_b.func_199315_b(var1, var2)) {
            return false;
         } else {
            int var4 = (var2 - this.field_209135_b.func_199319_b()) / 12 + this.field_209138_e;
            if (var4 >= 0 && var4 < this.field_209136_c.getList().size()) {
               this.func_209130_b(var4);
               this.func_209131_a();
            }

            return true;
         }
      }

      public boolean func_209232_a(double var1) {
         int var3 = (int)(GuiCommandBlockBase.this.field_146297_k.field_71417_B.func_198024_e() * (double)GuiCommandBlockBase.this.field_146297_k.field_195558_d.func_198107_o() / (double)GuiCommandBlockBase.this.field_146297_k.field_195558_d.func_198105_m());
         int var4 = (int)(GuiCommandBlockBase.this.field_146297_k.field_71417_B.func_198026_f() * (double)GuiCommandBlockBase.this.field_146297_k.field_195558_d.func_198087_p() / (double)GuiCommandBlockBase.this.field_146297_k.field_195558_d.func_198083_n());
         if (this.field_209135_b.func_199315_b(var3, var4)) {
            this.field_209138_e = MathHelper.func_76125_a((int)((double)this.field_209138_e - var1), 0, Math.max(this.field_209136_c.getList().size() - 7, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean func_209133_b(int var1, int var2, int var3) {
         if (var1 == 265) {
            this.func_209128_a(-1);
            this.field_209141_h = false;
            return true;
         } else if (var1 == 264) {
            this.func_209128_a(1);
            this.field_209141_h = false;
            return true;
         } else if (var1 == 258) {
            if (this.field_209141_h) {
               this.func_209128_a(GuiScreen.func_146272_n() ? -1 : 1);
            }

            this.func_209131_a();
            return true;
         } else if (var1 == 256) {
            this.func_209132_b();
            return true;
         } else {
            return false;
         }
      }

      public void func_209128_a(int var1) {
         this.func_209130_b(this.field_209139_f + var1);
         int var2 = this.field_209138_e;
         int var3 = this.field_209138_e + 7 - 1;
         if (this.field_209139_f < var2) {
            this.field_209138_e = MathHelper.func_76125_a(this.field_209139_f, 0, Math.max(this.field_209136_c.getList().size() - 7, 0));
         } else if (this.field_209139_f > var3) {
            this.field_209138_e = MathHelper.func_76125_a(this.field_209139_f - 7, 0, Math.max(this.field_209136_c.getList().size() - 7, 0));
         }

      }

      public void func_209130_b(int var1) {
         this.field_209139_f = var1;
         if (this.field_209139_f < 0) {
            this.field_209139_f += this.field_209136_c.getList().size();
         }

         if (this.field_209139_f >= this.field_209136_c.getList().size()) {
            this.field_209139_f -= this.field_209136_c.getList().size();
         }

         Suggestion var2 = (Suggestion)this.field_209136_c.getList().get(this.field_209139_f);
         GuiCommandBlockBase.this.field_195237_a.func_195612_c(GuiCommandBlockBase.func_212339_b(GuiCommandBlockBase.this.field_195237_a.func_146179_b(), var2.apply(this.field_212467_d)));
      }

      public void func_209131_a() {
         Suggestion var1 = (Suggestion)this.field_209136_c.getList().get(this.field_209139_f);
         GuiCommandBlockBase.this.field_212342_z = true;
         GuiCommandBlockBase.this.func_209102_a(var1.apply(this.field_212467_d));
         int var2 = var1.getRange().getStart() + var1.getText().length();
         GuiCommandBlockBase.this.field_195237_a.func_212422_f(var2);
         GuiCommandBlockBase.this.field_195237_a.func_146199_i(var2);
         this.func_209130_b(this.field_209139_f);
         GuiCommandBlockBase.this.field_212342_z = false;
         this.field_209141_h = true;
      }

      public void func_209132_b() {
         GuiCommandBlockBase.this.field_209116_y = null;
      }

      // $FF: synthetic method
      SuggestionsList(int var2, int var3, int var4, Suggestions var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }
}
