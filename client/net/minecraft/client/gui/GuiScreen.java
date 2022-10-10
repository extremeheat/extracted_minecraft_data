package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class GuiScreen extends GuiEventHandler implements GuiYesNoCallback {
   private static final Logger field_175287_a = LogManager.getLogger();
   private static final Set<String> field_175284_f = Sets.newHashSet(new String[]{"http", "https"});
   protected final List<IGuiEventListener> field_195124_j = Lists.newArrayList();
   protected Minecraft field_146297_k;
   protected ItemRenderer field_146296_j;
   public int field_146294_l;
   public int field_146295_m;
   protected final List<GuiButton> field_146292_n = Lists.newArrayList();
   protected final List<GuiLabel> field_146293_o = Lists.newArrayList();
   public boolean field_146291_p;
   protected FontRenderer field_146289_q;
   private URI field_175286_t;

   public GuiScreen() {
      super();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      int var4;
      for(var4 = 0; var4 < this.field_146292_n.size(); ++var4) {
         ((GuiButton)this.field_146292_n.get(var4)).func_194828_a(var1, var2, var3);
      }

      for(var4 = 0; var4 < this.field_146293_o.size(); ++var4) {
         ((GuiLabel)this.field_146293_o.get(var4)).func_194997_a(var1, var2, var3);
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256 && this.func_195120_Y_()) {
         this.func_195122_V_();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public boolean func_195120_Y_() {
      return true;
   }

   public void func_195122_V_() {
      this.field_146297_k.func_147108_a((GuiScreen)null);
   }

   protected <T extends GuiButton> T func_189646_b(T var1) {
      this.field_146292_n.add(var1);
      this.field_195124_j.add(var1);
      return var1;
   }

   protected void func_146285_a(ItemStack var1, int var2, int var3) {
      this.func_146283_a(this.func_191927_a(var1), var2, var3);
   }

   public List<String> func_191927_a(ItemStack var1) {
      List var2 = var1.func_82840_a(this.field_146297_k.field_71439_g, this.field_146297_k.field_71474_y.field_82882_x ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         ITextComponent var5 = (ITextComponent)var4.next();
         var3.add(var5.func_150254_d());
      }

      return var3;
   }

   public void func_146279_a(String var1, int var2, int var3) {
      this.func_146283_a(Arrays.asList(var1), var2, var3);
   }

   public void func_146283_a(List<String> var1, int var2, int var3) {
      if (!var1.isEmpty()) {
         GlStateManager.func_179101_C();
         RenderHelper.func_74518_a();
         GlStateManager.func_179140_f();
         GlStateManager.func_179097_i();
         int var4 = 0;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
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
         this.field_146296_j.field_77023_b = 300.0F;
         int var9 = -267386864;
         this.func_73733_a(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, -267386864, -267386864);
         this.func_73733_a(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, -267386864, -267386864);
         this.func_73733_a(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, -267386864, -267386864);
         this.func_73733_a(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, -267386864, -267386864);
         this.func_73733_a(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, -267386864, -267386864);
         int var10 = 1347420415;
         int var11 = 1344798847;
         this.func_73733_a(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, 1347420415, 1344798847);
         this.func_73733_a(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, 1347420415, 1344798847);
         this.func_73733_a(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, 1347420415, 1347420415);
         this.func_73733_a(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, 1344798847, 1344798847);

         for(int var12 = 0; var12 < var1.size(); ++var12) {
            String var13 = (String)var1.get(var12);
            this.field_146289_q.func_175063_a(var13, (float)var14, (float)var15, -1);
            if (var12 == 0) {
               var15 += 2;
            }

            var15 += 10;
         }

         this.field_73735_i = 0.0F;
         this.field_146296_j.field_77023_b = 0.0F;
         GlStateManager.func_179145_e();
         GlStateManager.func_179126_j();
         RenderHelper.func_74519_b();
         GlStateManager.func_179091_B();
      }
   }

   protected void func_175272_a(ITextComponent var1, int var2, int var3) {
      if (var1 != null && var1.func_150256_b().func_150210_i() != null) {
         HoverEvent var4 = var1.func_150256_b().func_150210_i();
         if (var4.func_150701_a() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack var5 = ItemStack.field_190927_a;

            try {
               NBTTagCompound var6 = JsonToNBT.func_180713_a(var4.func_150702_b().getString());
               if (var6 instanceof NBTTagCompound) {
                  var5 = ItemStack.func_199557_a((NBTTagCompound)var6);
               }
            } catch (CommandSyntaxException var10) {
            }

            if (var5.func_190926_b()) {
               this.func_146279_a(TextFormatting.RED + "Invalid Item!", var2, var3);
            } else {
               this.func_146285_a(var5, var2, var3);
            }
         } else if (var4.func_150701_a() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.field_146297_k.field_71474_y.field_82882_x) {
               try {
                  NBTTagCompound var11 = JsonToNBT.func_180713_a(var4.func_150702_b().getString());
                  ArrayList var12 = Lists.newArrayList();
                  ITextComponent var7 = ITextComponent.Serializer.func_150699_a(var11.func_74779_i("name"));
                  if (var7 != null) {
                     var12.add(var7.func_150254_d());
                  }

                  if (var11.func_150297_b("type", 8)) {
                     String var8 = var11.func_74779_i("type");
                     var12.add("Type: " + var8);
                  }

                  var12.add(var11.func_74779_i("id"));
                  this.func_146283_a(var12, var2, var3);
               } catch (CommandSyntaxException | JsonSyntaxException var9) {
                  this.func_146279_a(TextFormatting.RED + "Invalid Entity!", var2, var3);
               }
            }
         } else if (var4.func_150701_a() == HoverEvent.Action.SHOW_TEXT) {
            this.func_146283_a(this.field_146297_k.field_71466_p.func_78271_c(var4.func_150702_b().func_150254_d(), Math.max(this.field_146294_l / 2, 200)), var2, var3);
         }

         GlStateManager.func_179140_f();
      }
   }

   protected void func_175274_a(String var1, boolean var2) {
   }

   public boolean func_175276_a(ITextComponent var1) {
      if (var1 == null) {
         return false;
      } else {
         ClickEvent var2 = var1.func_150256_b().func_150235_h();
         if (func_146272_n()) {
            if (var1.func_150256_b().func_179986_j() != null) {
               this.func_175274_a(var1.func_150256_b().func_179986_j(), false);
            }
         } else if (var2 != null) {
            URI var3;
            if (var2.func_150669_a() == ClickEvent.Action.OPEN_URL) {
               if (!this.field_146297_k.field_71474_y.field_74359_p) {
                  return false;
               }

               try {
                  var3 = new URI(var2.func_150668_b());
                  String var4 = var3.getScheme();
                  if (var4 == null) {
                     throw new URISyntaxException(var2.func_150668_b(), "Missing protocol");
                  }

                  if (!field_175284_f.contains(var4.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(var2.func_150668_b(), "Unsupported protocol: " + var4.toLowerCase(Locale.ROOT));
                  }

                  if (this.field_146297_k.field_71474_y.field_74358_q) {
                     this.field_175286_t = var3;
                     this.field_146297_k.func_147108_a(new GuiConfirmOpenLink(this, var2.func_150668_b(), 31102009, false));
                  } else {
                     this.func_175282_a(var3);
                  }
               } catch (URISyntaxException var5) {
                  field_175287_a.error("Can't open url for {}", var2, var5);
               }
            } else if (var2.func_150669_a() == ClickEvent.Action.OPEN_FILE) {
               var3 = (new File(var2.func_150668_b())).toURI();
               this.func_175282_a(var3);
            } else if (var2.func_150669_a() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.func_175274_a(var2.func_150668_b(), true);
            } else if (var2.func_150669_a() == ClickEvent.Action.RUN_COMMAND) {
               this.func_175281_b(var2.func_150668_b(), false);
            } else {
               field_175287_a.error("Don't know how to handle {}", var2);
            }

            return true;
         }

         return false;
      }
   }

   public void func_175275_f(String var1) {
      this.func_175281_b(var1, true);
   }

   public void func_175281_b(String var1, boolean var2) {
      if (var2) {
         this.field_146297_k.field_71456_v.func_146158_b().func_146239_a(var1);
      }

      this.field_146297_k.field_71439_g.func_71165_d(var1);
   }

   public void func_146280_a(Minecraft var1, int var2, int var3) {
      this.field_146297_k = var1;
      this.field_146296_j = var1.func_175599_af();
      this.field_146289_q = var1.field_71466_p;
      this.field_146294_l = var2;
      this.field_146295_m = var3;
      this.field_146292_n.clear();
      this.field_195124_j.clear();
      this.func_73866_w_();
   }

   public List<? extends IGuiEventListener> func_195074_b() {
      return this.field_195124_j;
   }

   protected void func_73866_w_() {
      this.field_195124_j.addAll(this.field_146293_o);
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
      GlStateManager.func_179140_f();
      GlStateManager.func_179106_n();
      Tessellator var2 = Tessellator.func_178181_a();
      BufferBuilder var3 = var2.func_178180_c();
      this.field_146297_k.func_110434_K().func_110577_a(field_110325_k);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float var4 = 32.0F;
      var3.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var3.func_181662_b(0.0D, (double)this.field_146295_m, 0.0D).func_187315_a(0.0D, (double)((float)this.field_146295_m / 32.0F + (float)var1)).func_181669_b(64, 64, 64, 255).func_181675_d();
      var3.func_181662_b((double)this.field_146294_l, (double)this.field_146295_m, 0.0D).func_187315_a((double)((float)this.field_146294_l / 32.0F), (double)((float)this.field_146295_m / 32.0F + (float)var1)).func_181669_b(64, 64, 64, 255).func_181675_d();
      var3.func_181662_b((double)this.field_146294_l, 0.0D, 0.0D).func_187315_a((double)((float)this.field_146294_l / 32.0F), (double)var1).func_181669_b(64, 64, 64, 255).func_181675_d();
      var3.func_181662_b(0.0D, 0.0D, 0.0D).func_187315_a(0.0D, (double)var1).func_181669_b(64, 64, 64, 255).func_181675_d();
      var2.func_78381_a();
   }

   public boolean func_73868_f() {
      return true;
   }

   public void confirmResult(boolean var1, int var2) {
      if (var2 == 31102009) {
         if (var1) {
            this.func_175282_a(this.field_175286_t);
         }

         this.field_175286_t = null;
         this.field_146297_k.func_147108_a(this);
      }

   }

   private void func_175282_a(URI var1) {
      Util.func_110647_a().func_195642_a(var1);
   }

   public static boolean func_146271_m() {
      if (Minecraft.field_142025_a) {
         return InputMappings.func_197956_a(343) || InputMappings.func_197956_a(347);
      } else {
         return InputMappings.func_197956_a(341) || InputMappings.func_197956_a(345);
      }
   }

   public static boolean func_146272_n() {
      return InputMappings.func_197956_a(340) || InputMappings.func_197956_a(344);
   }

   public static boolean func_175283_s() {
      return InputMappings.func_197956_a(342) || InputMappings.func_197956_a(346);
   }

   public static boolean func_175277_d(int var0) {
      return var0 == 88 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public static boolean func_175279_e(int var0) {
      return var0 == 86 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public static boolean func_175280_f(int var0) {
      return var0 == 67 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public static boolean func_175278_g(int var0) {
      return var0 == 65 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      this.func_146280_a(var1, var2, var3);
   }

   public static void func_195121_a(Runnable var0, String var1, String var2) {
      try {
         var0.run();
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.func_85055_a(var6, var1);
         CrashReportCategory var5 = var4.func_85058_a("Affected screen");
         var5.func_189529_a("Screen name", () -> {
            return var2;
         });
         throw new ReportedException(var4);
      }
   }
}
