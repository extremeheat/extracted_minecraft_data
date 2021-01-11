package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback {
   private static final Logger field_175287_a = LogManager.getLogger();
   private static final Set<String> field_175284_f = Sets.newHashSet(new String[]{"http", "https"});
   private static final Splitter field_175285_g = Splitter.on('\n');
   protected Minecraft field_146297_k;
   protected RenderItem field_146296_j;
   public int field_146294_l;
   public int field_146295_m;
   protected List<GuiButton> field_146292_n = Lists.newArrayList();
   protected List<GuiLabel> field_146293_o = Lists.newArrayList();
   public boolean field_146291_p;
   protected FontRenderer field_146289_q;
   private GuiButton field_146290_a;
   private int field_146287_f;
   private long field_146288_g;
   private int field_146298_h;
   private URI field_175286_t;

   public GuiScreen() {
      super();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      int var4;
      for(var4 = 0; var4 < this.field_146292_n.size(); ++var4) {
         ((GuiButton)this.field_146292_n.get(var4)).func_146112_a(this.field_146297_k, var1, var2);
      }

      for(var4 = 0; var4 < this.field_146293_o.size(); ++var4) {
         ((GuiLabel)this.field_146293_o.get(var4)).func_146159_a(this.field_146297_k, var1, var2);
      }

   }

   protected void func_73869_a(char var1, int var2) {
      if (var2 == 1) {
         this.field_146297_k.func_147108_a((GuiScreen)null);
         if (this.field_146297_k.field_71462_r == null) {
            this.field_146297_k.func_71381_h();
         }
      }

   }

   public static String func_146277_j() {
      try {
         Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);
         if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)var0.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception var1) {
      }

      return "";
   }

   public static void func_146275_d(String var0) {
      if (!StringUtils.isEmpty(var0)) {
         try {
            StringSelection var1 = new StringSelection(var0);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, (ClipboardOwner)null);
         } catch (Exception var2) {
         }

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

   protected void func_146283_a(List<String> var1, int var2, int var3) {
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
         this.func_73733_a(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
         this.func_73733_a(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
         this.func_73733_a(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
         this.func_73733_a(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
         this.func_73733_a(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
         int var10 = 1347420415;
         int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
         this.func_73733_a(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
         this.func_73733_a(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
         this.func_73733_a(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
         this.func_73733_a(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

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

   protected void func_175272_a(IChatComponent var1, int var2, int var3) {
      if (var1 != null && var1.func_150256_b().func_150210_i() != null) {
         HoverEvent var4 = var1.func_150256_b().func_150210_i();
         if (var4.func_150701_a() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack var5 = null;

            try {
               NBTTagCompound var6 = JsonToNBT.func_180713_a(var4.func_150702_b().func_150260_c());
               if (var6 instanceof NBTTagCompound) {
                  var5 = ItemStack.func_77949_a((NBTTagCompound)var6);
               }
            } catch (NBTException var11) {
            }

            if (var5 != null) {
               this.func_146285_a(var5, var2, var3);
            } else {
               this.func_146279_a(EnumChatFormatting.RED + "Invalid Item!", var2, var3);
            }
         } else {
            String var8;
            if (var4.func_150701_a() == HoverEvent.Action.SHOW_ENTITY) {
               if (this.field_146297_k.field_71474_y.field_82882_x) {
                  try {
                     NBTTagCompound var12 = JsonToNBT.func_180713_a(var4.func_150702_b().func_150260_c());
                     if (var12 instanceof NBTTagCompound) {
                        ArrayList var14 = Lists.newArrayList();
                        NBTTagCompound var7 = (NBTTagCompound)var12;
                        var14.add(var7.func_74779_i("name"));
                        if (var7.func_150297_b("type", 8)) {
                           var8 = var7.func_74779_i("type");
                           var14.add("Type: " + var8 + " (" + EntityList.func_180122_a(var8) + ")");
                        }

                        var14.add(var7.func_74779_i("id"));
                        this.func_146283_a(var14, var2, var3);
                     } else {
                        this.func_146279_a(EnumChatFormatting.RED + "Invalid Entity!", var2, var3);
                     }
                  } catch (NBTException var10) {
                     this.func_146279_a(EnumChatFormatting.RED + "Invalid Entity!", var2, var3);
                  }
               }
            } else if (var4.func_150701_a() == HoverEvent.Action.SHOW_TEXT) {
               this.func_146283_a(field_175285_g.splitToList(var4.func_150702_b().func_150254_d()), var2, var3);
            } else if (var4.func_150701_a() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
               StatBase var13 = StatList.func_151177_a(var4.func_150702_b().func_150260_c());
               if (var13 != null) {
                  IChatComponent var15 = var13.func_150951_e();
                  ChatComponentTranslation var16 = new ChatComponentTranslation("stats.tooltip.type." + (var13.func_75967_d() ? "achievement" : "statistic"), new Object[0]);
                  var16.func_150256_b().func_150217_b(true);
                  var8 = var13 instanceof Achievement ? ((Achievement)var13).func_75989_e() : null;
                  ArrayList var9 = Lists.newArrayList(new String[]{var15.func_150254_d(), var16.func_150254_d()});
                  if (var8 != null) {
                     var9.addAll(this.field_146289_q.func_78271_c(var8, 150));
                  }

                  this.func_146283_a(var9, var2, var3);
               } else {
                  this.func_146279_a(EnumChatFormatting.RED + "Invalid statistic/achievement!", var2, var3);
               }
            }
         }

         GlStateManager.func_179140_f();
      }
   }

   protected void func_175274_a(String var1, boolean var2) {
   }

   protected boolean func_175276_a(IChatComponent var1) {
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

                  if (!field_175284_f.contains(var4.toLowerCase())) {
                     throw new URISyntaxException(var2.func_150668_b(), "Unsupported protocol: " + var4.toLowerCase());
                  }

                  if (this.field_146297_k.field_71474_y.field_74358_q) {
                     this.field_175286_t = var3;
                     this.field_146297_k.func_147108_a(new GuiConfirmOpenLink(this, var2.func_150668_b(), 31102009, false));
                  } else {
                     this.func_175282_a(var3);
                  }
               } catch (URISyntaxException var5) {
                  field_175287_a.error("Can't open url for " + var2, var5);
               }
            } else if (var2.func_150669_a() == ClickEvent.Action.OPEN_FILE) {
               var3 = (new File(var2.func_150668_b())).toURI();
               this.func_175282_a(var3);
            } else if (var2.func_150669_a() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.func_175274_a(var2.func_150668_b(), true);
            } else if (var2.func_150669_a() == ClickEvent.Action.RUN_COMMAND) {
               this.func_175281_b(var2.func_150668_b(), false);
            } else if (var2.func_150669_a() == ClickEvent.Action.TWITCH_USER_INFO) {
               ChatUserInfo var6 = this.field_146297_k.func_152346_Z().func_152926_a(var2.func_150668_b());
               if (var6 != null) {
                  this.field_146297_k.func_147108_a(new GuiTwitchUserMode(this.field_146297_k.func_152346_Z(), var6));
               } else {
                  field_175287_a.error("Tried to handle twitch user but couldn't find them!");
               }
            } else {
               field_175287_a.error("Don't know how to handle " + var2);
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
      this.field_146296_j = var1.func_175599_af();
      this.field_146289_q = var1.field_71466_p;
      this.field_146294_l = var2;
      this.field_146295_m = var3;
      this.field_146292_n.clear();
      this.func_73866_w_();
   }

   public void func_183500_a(int var1, int var2) {
      this.field_146294_l = var1;
      this.field_146295_m = var2;
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
      GlStateManager.func_179140_f();
      GlStateManager.func_179106_n();
      Tessellator var2 = Tessellator.func_178181_a();
      WorldRenderer var3 = var2.func_178180_c();
      this.field_146297_k.func_110434_K().func_110577_a(field_110325_k);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float var4 = 32.0F;
      var3.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var3.func_181662_b(0.0D, (double)this.field_146295_m, 0.0D).func_181673_a(0.0D, (double)((float)this.field_146295_m / 32.0F + (float)var1)).func_181669_b(64, 64, 64, 255).func_181675_d();
      var3.func_181662_b((double)this.field_146294_l, (double)this.field_146295_m, 0.0D).func_181673_a((double)((float)this.field_146294_l / 32.0F), (double)((float)this.field_146295_m / 32.0F + (float)var1)).func_181669_b(64, 64, 64, 255).func_181675_d();
      var3.func_181662_b((double)this.field_146294_l, 0.0D, 0.0D).func_181673_a((double)((float)this.field_146294_l / 32.0F), (double)var1).func_181669_b(64, 64, 64, 255).func_181675_d();
      var3.func_181662_b(0.0D, 0.0D, 0.0D).func_181673_a(0.0D, (double)var1).func_181669_b(64, 64, 64, 255).func_181675_d();
      var2.func_78381_a();
   }

   public boolean func_73868_f() {
      return true;
   }

   public void func_73878_a(boolean var1, int var2) {
      if (var2 == 31102009) {
         if (var1) {
            this.func_175282_a(this.field_175286_t);
         }

         this.field_175286_t = null;
         this.field_146297_k.func_147108_a(this);
      }

   }

   private void func_175282_a(URI var1) {
      try {
         Class var2 = Class.forName("java.awt.Desktop");
         Object var3 = var2.getMethod("getDesktop").invoke((Object)null);
         var2.getMethod("browse", URI.class).invoke(var3, var1);
      } catch (Throwable var4) {
         field_175287_a.error("Couldn't open link", var4);
      }

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

   public static boolean func_175283_s() {
      return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
   }

   public static boolean func_175277_d(int var0) {
      return var0 == 45 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public static boolean func_175279_e(int var0) {
      return var0 == 47 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public static boolean func_175280_f(int var0) {
      return var0 == 46 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public static boolean func_175278_g(int var0) {
      return var0 == 30 && func_146271_m() && !func_146272_n() && !func_175283_s();
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      this.func_146280_a(var1, var2, var3);
   }
}
