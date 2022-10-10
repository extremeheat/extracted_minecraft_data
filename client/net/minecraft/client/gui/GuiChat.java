package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;

public class GuiChat extends GuiScreen {
   private static final Pattern field_208608_i = Pattern.compile("(\\s+)");
   private String field_146410_g = "";
   private int field_146416_h = -1;
   protected GuiTextField field_146415_a;
   private String field_146409_v = "";
   protected final List<String> field_195136_f = Lists.newArrayList();
   protected int field_195138_g;
   protected int field_195140_h;
   private ParseResults<ISuggestionProvider> field_195135_u;
   private CompletableFuture<Suggestions> field_195137_v;
   private GuiChat.SuggestionsList field_195139_w;
   private boolean field_211139_z;
   private boolean field_212338_z;

   public GuiChat() {
      super();
   }

   public GuiChat(String var1) {
      super();
      this.field_146409_v = var1;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.field_146415_a;
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      this.field_146416_h = this.field_146297_k.field_71456_v.func_146158_b().func_146238_c().size();
      this.field_146415_a = new GuiTextField(0, this.field_146289_q, 4, this.field_146295_m - 12, this.field_146294_l - 4, 12);
      this.field_146415_a.func_146203_f(256);
      this.field_146415_a.func_146185_a(false);
      this.field_146415_a.func_146195_b(true);
      this.field_146415_a.func_146180_a(this.field_146409_v);
      this.field_146415_a.func_146205_d(false);
      this.field_146415_a.func_195607_a(this::func_195130_a);
      this.field_146415_a.func_195609_a(this::func_195128_a);
      this.field_195124_j.add(this.field_146415_a);
      this.func_195129_h();
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      String var4 = this.field_146415_a.func_146179_b();
      this.func_146280_a(var1, var2, var3);
      this.func_208604_b(var4);
      this.func_195129_h();
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
      this.field_146297_k.field_71456_v.func_146158_b().func_146240_d();
   }

   public void func_73876_c() {
      this.field_146415_a.func_146178_a();
   }

   private void func_195128_a(int var1, String var2) {
      String var3 = this.field_146415_a.func_146179_b();
      this.field_211139_z = !var3.equals(this.field_146409_v);
      this.func_195129_h();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.field_195139_w != null && this.field_195139_w.func_198503_b(var1, var2, var3)) {
         return true;
      } else if (var1 == 256) {
         this.field_146297_k.func_147108_a((GuiScreen)null);
         return true;
      } else if (var1 != 257 && var1 != 335) {
         if (var1 == 265) {
            this.func_146402_a(-1);
            return true;
         } else if (var1 == 264) {
            this.func_146402_a(1);
            return true;
         } else if (var1 == 266) {
            this.field_146297_k.field_71456_v.func_146158_b().func_194813_a((double)(this.field_146297_k.field_71456_v.func_146158_b().func_146232_i() - 1));
            return true;
         } else if (var1 == 267) {
            this.field_146297_k.field_71456_v.func_146158_b().func_194813_a((double)(-this.field_146297_k.field_71456_v.func_146158_b().func_146232_i() + 1));
            return true;
         } else {
            if (var1 == 258) {
               this.field_211139_z = true;
               this.func_195131_X_();
            }

            return this.field_146415_a.keyPressed(var1, var2, var3);
         }
      } else {
         String var4 = this.field_146415_a.func_146179_b().trim();
         if (!var4.isEmpty()) {
            this.func_175275_f(var4);
         }

         this.field_146297_k.func_147108_a((GuiScreen)null);
         return true;
      }
   }

   public void func_195131_X_() {
      if (this.field_195137_v != null && this.field_195137_v.isDone()) {
         int var1 = 0;
         Suggestions var2 = (Suggestions)this.field_195137_v.join();
         if (!var2.getList().isEmpty()) {
            Suggestion var4;
            for(Iterator var3 = var2.getList().iterator(); var3.hasNext(); var1 = Math.max(var1, this.field_146289_q.func_78256_a(var4.getText()))) {
               var4 = (Suggestion)var3.next();
            }

            int var5 = MathHelper.func_76125_a(this.field_146415_a.func_195611_j(var2.getRange().getStart()), 0, this.field_146294_l - var1);
            this.field_195139_w = new GuiChat.SuggestionsList(var5, this.field_146295_m - 12, var1, var2);
         }
      }

   }

   private static int func_208603_a(String var0) {
      if (Strings.isNullOrEmpty(var0)) {
         return 0;
      } else {
         int var1 = 0;

         for(Matcher var2 = field_208608_i.matcher(var0); var2.find(); var1 = var2.end()) {
         }

         return var1;
      }
   }

   private void func_195129_h() {
      this.field_195135_u = null;
      if (!this.field_212338_z) {
         this.field_146415_a.func_195612_c((String)null);
         this.field_195139_w = null;
      }

      this.field_195136_f.clear();
      String var1 = this.field_146415_a.func_146179_b();
      StringReader var2 = new StringReader(var1);
      if (var2.canRead() && var2.peek() == '/') {
         var2.skip();
         CommandDispatcher var3 = this.field_146297_k.field_71439_g.field_71174_a.func_195515_i();
         this.field_195135_u = var3.parse(var2, this.field_146297_k.field_71439_g.field_71174_a.func_195513_b());
         if (this.field_195139_w == null || !this.field_212338_z) {
            StringReader var6 = new StringReader(var1.substring(0, Math.min(var1.length(), this.field_146415_a.func_146198_h())));
            if (var6.canRead() && var6.peek() == '/') {
               var6.skip();
               ParseResults var7 = var3.parse(var6, this.field_146297_k.field_71439_g.field_71174_a.func_195513_b());
               this.field_195137_v = var3.getCompletionSuggestions(var7);
               this.field_195137_v.thenRun(() -> {
                  if (this.field_195137_v.isDone()) {
                     this.func_195133_i();
                  }
               });
            }
         }
      } else {
         int var4 = func_208603_a(var1);
         Collection var5 = this.field_146297_k.field_71439_g.field_71174_a.func_195513_b().func_197011_j();
         this.field_195137_v = ISuggestionProvider.func_197005_b(var5, new SuggestionsBuilder(var1, var4));
      }

   }

   private void func_195133_i() {
      if (((Suggestions)this.field_195137_v.join()).isEmpty() && !this.field_195135_u.getExceptions().isEmpty() && this.field_146415_a.func_146198_h() == this.field_146415_a.func_146179_b().length()) {
         int var1 = 0;
         Iterator var2 = this.field_195135_u.getExceptions().entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            CommandSyntaxException var4 = (CommandSyntaxException)var3.getValue();
            if (var4.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
               ++var1;
            } else {
               this.field_195136_f.add(var4.getMessage());
            }
         }

         if (var1 > 0) {
            this.field_195136_f.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
         }
      }

      this.field_195138_g = 0;
      this.field_195140_h = this.field_146294_l;
      if (this.field_195136_f.isEmpty()) {
         this.func_195132_a(TextFormatting.GRAY);
      }

      this.field_195139_w = null;
      if (this.field_211139_z && this.field_146297_k.field_71474_y.field_198018_T) {
         this.func_195131_X_();
      }

   }

   private String func_195130_a(String var1, int var2) {
      return this.field_195135_u != null ? func_212336_a(this.field_195135_u, var1, var2) : var1;
   }

   public static String func_212336_a(ParseResults<ISuggestionProvider> var0, String var1, int var2) {
      TextFormatting[] var3 = new TextFormatting[]{TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD};
      String var4 = TextFormatting.GRAY.toString();
      StringBuilder var5 = new StringBuilder(var4);
      int var6 = 0;
      int var7 = -1;
      CommandContextBuilder var8 = var0.getContext().getLastChild();
      Iterator var9 = var8.getArguments().values().iterator();

      while(var9.hasNext()) {
         ParsedArgument var10 = (ParsedArgument)var9.next();
         ++var7;
         if (var7 >= var3.length) {
            var7 = 0;
         }

         int var11 = Math.max(var10.getRange().getStart() - var2, 0);
         if (var11 >= var1.length()) {
            break;
         }

         int var12 = Math.min(var10.getRange().getEnd() - var2, var1.length());
         if (var12 > 0) {
            var5.append(var1, var6, var11);
            var5.append(var3[var7]);
            var5.append(var1, var11, var12);
            var5.append(var4);
            var6 = var12;
         }
      }

      if (var0.getReader().canRead()) {
         int var13 = Math.max(var0.getReader().getCursor() - var2, 0);
         if (var13 < var1.length()) {
            int var14 = Math.min(var13 + var0.getReader().getRemainingLength(), var1.length());
            var5.append(var1, var6, var13);
            var5.append(TextFormatting.RED);
            var5.append(var1, var13, var14);
            var6 = var14;
         }
      }

      var5.append(var1, var6, var1.length());
      return var5.toString();
   }

   public boolean mouseScrolled(double var1) {
      if (var1 > 1.0D) {
         var1 = 1.0D;
      }

      if (var1 < -1.0D) {
         var1 = -1.0D;
      }

      if (this.field_195139_w != null && this.field_195139_w.func_198498_a(var1)) {
         return true;
      } else {
         if (!func_146272_n()) {
            var1 *= 7.0D;
         }

         this.field_146297_k.field_71456_v.func_146158_b().func_194813_a(var1);
         return true;
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.field_195139_w != null && this.field_195139_w.func_198499_a((int)var1, (int)var3, var5)) {
         return true;
      } else {
         if (var5 == 0) {
            ITextComponent var6 = this.field_146297_k.field_71456_v.func_146158_b().func_194817_a(var1, var3);
            if (var6 != null && this.func_175276_a(var6)) {
               return true;
            }
         }

         return this.field_146415_a.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   protected void func_175274_a(String var1, boolean var2) {
      if (var2) {
         this.field_146415_a.func_146180_a(var1);
      } else {
         this.field_146415_a.func_146191_b(var1);
      }

   }

   public void func_146402_a(int var1) {
      int var2 = this.field_146416_h + var1;
      int var3 = this.field_146297_k.field_71456_v.func_146158_b().func_146238_c().size();
      var2 = MathHelper.func_76125_a(var2, 0, var3);
      if (var2 != this.field_146416_h) {
         if (var2 == var3) {
            this.field_146416_h = var3;
            this.field_146415_a.func_146180_a(this.field_146410_g);
         } else {
            if (this.field_146416_h == var3) {
               this.field_146410_g = this.field_146415_a.func_146179_b();
            }

            this.field_146415_a.func_146180_a((String)this.field_146297_k.field_71456_v.func_146158_b().func_146238_c().get(var2));
            this.field_195139_w = null;
            this.field_146416_h = var2;
            this.field_211139_z = false;
         }
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      func_73734_a(2, this.field_146295_m - 14, this.field_146294_l - 2, this.field_146295_m - 2, -2147483648);
      this.field_146415_a.func_195608_a(var1, var2, var3);
      if (this.field_195139_w != null) {
         this.field_195139_w.func_198500_a(var1, var2);
      } else {
         int var4 = 0;

         for(Iterator var5 = this.field_195136_f.iterator(); var5.hasNext(); ++var4) {
            String var6 = (String)var5.next();
            func_73734_a(this.field_195138_g - 1, this.field_146295_m - 14 - 13 - 12 * var4, this.field_195138_g + this.field_195140_h + 1, this.field_146295_m - 2 - 13 - 12 * var4, -16777216);
            this.field_146289_q.func_175063_a(var6, (float)this.field_195138_g, (float)(this.field_146295_m - 14 - 13 + 2 - 12 * var4), -1);
         }
      }

      ITextComponent var7 = this.field_146297_k.field_71456_v.func_146158_b().func_194817_a((double)var1, (double)var2);
      if (var7 != null && var7.func_150256_b().func_150210_i() != null) {
         this.func_175272_a(var7, var1, var2);
      }

      super.func_73863_a(var1, var2, var3);
   }

   public boolean func_73868_f() {
      return false;
   }

   private void func_195132_a(TextFormatting var1) {
      CommandContextBuilder var2 = this.field_195135_u.getContext();
      CommandContextBuilder var3 = var2.getLastChild();
      if (!var3.getNodes().isEmpty()) {
         CommandNode var4;
         int var5;
         Entry var6;
         if (this.field_195135_u.getReader().canRead()) {
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
            this.field_195136_f.addAll(var7);
            this.field_195138_g = MathHelper.func_76125_a(this.field_146415_a.func_195611_j(var5) + this.field_146289_q.func_78256_a(" "), 0, this.field_146294_l - var8);
            this.field_195140_h = var8;
         }

      }
   }

   @Nullable
   private static String func_208602_b(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   private void func_208604_b(String var1) {
      this.field_146415_a.func_146180_a(var1);
   }

   class SuggestionsList {
      private final Rectangle2d field_198505_b;
      private final Suggestions field_198506_c;
      private final String field_212466_d;
      private int field_198507_d;
      private int field_198508_e;
      private Vec2f field_198509_f;
      private boolean field_199880_h;

      private SuggestionsList(int var2, int var3, int var4, Suggestions var5) {
         super();
         this.field_198509_f = Vec2f.field_189974_a;
         this.field_198505_b = new Rectangle2d(var2 - 1, var3 - 3 - Math.min(var5.getList().size(), 10) * 12, var4 + 1, Math.min(var5.getList().size(), 10) * 12);
         this.field_198506_c = var5;
         this.field_212466_d = GuiChat.this.field_146415_a.func_146179_b();
         this.func_199675_a(0);
      }

      public void func_198500_a(int var1, int var2) {
         int var3 = Math.min(this.field_198506_c.getList().size(), 10);
         int var4 = -5592406;
         boolean var5 = this.field_198507_d > 0;
         boolean var6 = this.field_198506_c.getList().size() > this.field_198507_d + var3;
         boolean var7 = var5 || var6;
         boolean var8 = this.field_198509_f.field_189982_i != (float)var1 || this.field_198509_f.field_189983_j != (float)var2;
         if (var8) {
            this.field_198509_f = new Vec2f((float)var1, (float)var2);
         }

         if (var7) {
            Gui.func_73734_a(this.field_198505_b.func_199318_a(), this.field_198505_b.func_199319_b() - 1, this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c(), this.field_198505_b.func_199319_b(), -805306368);
            Gui.func_73734_a(this.field_198505_b.func_199318_a(), this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d(), this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c(), this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d() + 1, -805306368);
            int var9;
            if (var5) {
               for(var9 = 0; var9 < this.field_198505_b.func_199316_c(); ++var9) {
                  if (var9 % 2 == 0) {
                     Gui.func_73734_a(this.field_198505_b.func_199318_a() + var9, this.field_198505_b.func_199319_b() - 1, this.field_198505_b.func_199318_a() + var9 + 1, this.field_198505_b.func_199319_b(), -1);
                  }
               }
            }

            if (var6) {
               for(var9 = 0; var9 < this.field_198505_b.func_199316_c(); ++var9) {
                  if (var9 % 2 == 0) {
                     Gui.func_73734_a(this.field_198505_b.func_199318_a() + var9, this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d(), this.field_198505_b.func_199318_a() + var9 + 1, this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d() + 1, -1);
                  }
               }
            }
         }

         boolean var12 = false;

         for(int var10 = 0; var10 < var3; ++var10) {
            Suggestion var11 = (Suggestion)this.field_198506_c.getList().get(var10 + this.field_198507_d);
            Gui.func_73734_a(this.field_198505_b.func_199318_a(), this.field_198505_b.func_199319_b() + 12 * var10, this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c(), this.field_198505_b.func_199319_b() + 12 * var10 + 12, -805306368);
            if (var1 > this.field_198505_b.func_199318_a() && var1 < this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c() && var2 > this.field_198505_b.func_199319_b() + 12 * var10 && var2 < this.field_198505_b.func_199319_b() + 12 * var10 + 12) {
               if (var8) {
                  this.func_199675_a(var10 + this.field_198507_d);
               }

               var12 = true;
            }

            GuiChat.this.field_146289_q.func_175063_a(var11.getText(), (float)(this.field_198505_b.func_199318_a() + 1), (float)(this.field_198505_b.func_199319_b() + 2 + 12 * var10), var10 + this.field_198507_d == this.field_198508_e ? -256 : -5592406);
         }

         if (var12) {
            Message var13 = ((Suggestion)this.field_198506_c.getList().get(this.field_198508_e)).getTooltip();
            if (var13 != null) {
               GuiChat.this.func_146279_a(TextComponentUtils.func_202465_a(var13).func_150254_d(), var1, var2);
            }
         }

      }

      public boolean func_198499_a(int var1, int var2, int var3) {
         if (!this.field_198505_b.func_199315_b(var1, var2)) {
            return false;
         } else {
            int var4 = (var2 - this.field_198505_b.func_199319_b()) / 12 + this.field_198507_d;
            if (var4 >= 0 && var4 < this.field_198506_c.getList().size()) {
               this.func_199675_a(var4);
               this.func_198501_a();
            }

            return true;
         }
      }

      public boolean func_198498_a(double var1) {
         int var3 = (int)(GuiChat.this.field_146297_k.field_71417_B.func_198024_e() * (double)GuiChat.this.field_146297_k.field_195558_d.func_198107_o() / (double)GuiChat.this.field_146297_k.field_195558_d.func_198105_m());
         int var4 = (int)(GuiChat.this.field_146297_k.field_71417_B.func_198026_f() * (double)GuiChat.this.field_146297_k.field_195558_d.func_198087_p() / (double)GuiChat.this.field_146297_k.field_195558_d.func_198083_n());
         if (this.field_198505_b.func_199315_b(var3, var4)) {
            this.field_198507_d = MathHelper.func_76125_a((int)((double)this.field_198507_d - var1), 0, Math.max(this.field_198506_c.getList().size() - 10, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean func_198503_b(int var1, int var2, int var3) {
         if (var1 == 265) {
            this.func_199879_a(-1);
            this.field_199880_h = false;
            return true;
         } else if (var1 == 264) {
            this.func_199879_a(1);
            this.field_199880_h = false;
            return true;
         } else if (var1 == 258) {
            if (this.field_199880_h) {
               this.func_199879_a(GuiScreen.func_146272_n() ? -1 : 1);
            }

            this.func_198501_a();
            return true;
         } else if (var1 == 256) {
            this.func_198502_b();
            return true;
         } else {
            return false;
         }
      }

      public void func_199879_a(int var1) {
         this.func_199675_a(this.field_198508_e + var1);
         int var2 = this.field_198507_d;
         int var3 = this.field_198507_d + 10 - 1;
         if (this.field_198508_e < var2) {
            this.field_198507_d = MathHelper.func_76125_a(this.field_198508_e, 0, Math.max(this.field_198506_c.getList().size() - 10, 0));
         } else if (this.field_198508_e > var3) {
            this.field_198507_d = MathHelper.func_76125_a(this.field_198508_e + 1 - 10, 0, Math.max(this.field_198506_c.getList().size() - 10, 0));
         }

      }

      public void func_199675_a(int var1) {
         this.field_198508_e = var1;
         if (this.field_198508_e < 0) {
            this.field_198508_e += this.field_198506_c.getList().size();
         }

         if (this.field_198508_e >= this.field_198506_c.getList().size()) {
            this.field_198508_e -= this.field_198506_c.getList().size();
         }

         Suggestion var2 = (Suggestion)this.field_198506_c.getList().get(this.field_198508_e);
         GuiChat.this.field_146415_a.func_195612_c(GuiChat.func_208602_b(GuiChat.this.field_146415_a.func_146179_b(), var2.apply(this.field_212466_d)));
      }

      public void func_198501_a() {
         Suggestion var1 = (Suggestion)this.field_198506_c.getList().get(this.field_198508_e);
         GuiChat.this.field_212338_z = true;
         GuiChat.this.func_208604_b(var1.apply(this.field_212466_d));
         int var2 = var1.getRange().getStart() + var1.getText().length();
         GuiChat.this.field_146415_a.func_212422_f(var2);
         GuiChat.this.field_146415_a.func_146199_i(var2);
         this.func_199675_a(this.field_198508_e);
         GuiChat.this.field_212338_z = false;
         this.field_199880_h = true;
      }

      public void func_198502_b() {
         GuiChat.this.field_195139_w = null;
      }

      // $FF: synthetic method
      SuggestionsList(int var2, int var3, int var4, Suggestions var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }
}
