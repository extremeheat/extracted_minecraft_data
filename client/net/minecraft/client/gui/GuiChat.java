package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent$Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent$Action;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tv.twitch.chat.ChatUserInfo;

public class GuiChat extends GuiScreen implements GuiYesNoCallback {
   private static final Set field_152175_f = Sets.newHashSet(new String[]{"http", "https"});
   private static final Logger field_146408_f = LogManager.getLogger();
   private String field_146410_g = "";
   private int field_146416_h = -1;
   private boolean field_146417_i;
   private boolean field_146414_r;
   private int field_146413_s;
   private List field_146412_t = new ArrayList();
   private URI field_146411_u;
   protected GuiTextField field_146415_a;
   private String field_146409_v = "";

   public GuiChat() {
      super();
   }

   public GuiChat(String var1) {
      super();
      this.field_146409_v = var1;
   }

   @Override
   public void func_73866_w_() {
      Keyboard.enableRepeatEvents(true);
      this.field_146416_h = this.field_146297_k.field_71456_v.func_146158_b().func_146238_c().size();
      this.field_146415_a = new GuiTextField(this.field_146289_q, 4, this.field_146295_m - 12, this.field_146294_l - 4, 12);
      this.field_146415_a.func_146203_f(100);
      this.field_146415_a.func_146185_a(false);
      this.field_146415_a.func_146195_b(true);
      this.field_146415_a.func_146180_a(this.field_146409_v);
      this.field_146415_a.func_146205_d(false);
   }

   @Override
   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
      this.field_146297_k.field_71456_v.func_146158_b().func_146240_d();
   }

   @Override
   public void func_73876_c() {
      this.field_146415_a.func_146178_a();
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
      this.field_146414_r = false;
      if (var2 == 15) {
         this.func_146404_p_();
      } else {
         this.field_146417_i = false;
      }

      if (var2 == 1) {
         this.field_146297_k.func_147108_a(null);
      } else if (var2 == 28 || var2 == 156) {
         String var3 = this.field_146415_a.func_146179_b().trim();
         if (var3.length() > 0) {
            this.func_146403_a(var3);
         }

         this.field_146297_k.func_147108_a(null);
      } else if (var2 == 200) {
         this.func_146402_a(-1);
      } else if (var2 == 208) {
         this.func_146402_a(1);
      } else if (var2 == 201) {
         this.field_146297_k.field_71456_v.func_146158_b().func_146229_b(this.field_146297_k.field_71456_v.func_146158_b().func_146232_i() - 1);
      } else if (var2 == 209) {
         this.field_146297_k.field_71456_v.func_146158_b().func_146229_b(-this.field_146297_k.field_71456_v.func_146158_b().func_146232_i() + 1);
      } else {
         this.field_146415_a.func_146201_a(var1, var2);
      }
   }

   public void func_146403_a(String var1) {
      this.field_146297_k.field_71456_v.func_146158_b().func_146239_a(var1);
      this.field_146297_k.field_71439_g.func_71165_d(var1);
   }

   @Override
   public void func_146274_d() {
      super.func_146274_d();
      int var1 = Mouse.getEventDWheel();
      if (var1 != 0) {
         if (var1 > 1) {
            var1 = 1;
         }

         if (var1 < -1) {
            var1 = -1;
         }

         if (!func_146272_n()) {
            var1 *= 7;
         }

         this.field_146297_k.field_71456_v.func_146158_b().func_146229_b(var1);
      }
   }

   @Override
   protected void func_73864_a(int var1, int var2, int var3) {
      if (var3 == 0 && this.field_146297_k.field_71474_y.field_74359_p) {
         IChatComponent var4 = this.field_146297_k.field_71456_v.func_146158_b().func_146236_a(Mouse.getX(), Mouse.getY());
         if (var4 != null) {
            ClickEvent var5 = var4.func_150256_b().func_150235_h();
            if (var5 != null) {
               if (func_146272_n()) {
                  this.field_146415_a.func_146191_b(var4.func_150261_e());
               } else if (var5.func_150669_a() == ClickEvent$Action.OPEN_URL) {
                  try {
                     URI var6 = new URI(var5.func_150668_b());
                     if (!field_152175_f.contains(var6.getScheme().toLowerCase())) {
                        throw new URISyntaxException(var5.func_150668_b(), "Unsupported protocol: " + var6.getScheme().toLowerCase());
                     }

                     if (this.field_146297_k.field_71474_y.field_74358_q) {
                        this.field_146411_u = var6;
                        this.field_146297_k.func_147108_a(new GuiConfirmOpenLink(this, var5.func_150668_b(), 0, false));
                     } else {
                        this.func_146407_a(var6);
                     }
                  } catch (URISyntaxException var7) {
                     field_146408_f.error("Can't open url for " + var5, var7);
                  }
               } else if (var5.func_150669_a() == ClickEvent$Action.OPEN_FILE) {
                  URI var8 = new File(var5.func_150668_b()).toURI();
                  this.func_146407_a(var8);
               } else if (var5.func_150669_a() == ClickEvent$Action.SUGGEST_COMMAND) {
                  this.field_146415_a.func_146180_a(var5.func_150668_b());
               } else if (var5.func_150669_a() == ClickEvent$Action.RUN_COMMAND) {
                  this.func_146403_a(var5.func_150668_b());
               } else if (var5.func_150669_a() == ClickEvent$Action.TWITCH_USER_INFO) {
                  ChatUserInfo var9 = this.field_146297_k.func_152346_Z().func_152926_a(var5.func_150668_b());
                  if (var9 != null) {
                     this.field_146297_k.func_147108_a(new GuiTwitchUserMode(this.field_146297_k.func_152346_Z(), var9));
                  } else {
                     field_146408_f.error("Tried to handle twitch user but couldn't find them!");
                  }
               } else {
                  field_146408_f.error("Don't know how to handle " + var5);
               }

               return;
            }
         }
      }

      this.field_146415_a.func_146192_a(var1, var2, var3);
      super.func_73864_a(var1, var2, var3);
   }

   @Override
   public void func_73878_a(boolean var1, int var2) {
      if (var2 == 0) {
         if (var1) {
            this.func_146407_a(this.field_146411_u);
         }

         this.field_146411_u = null;
         this.field_146297_k.func_147108_a(this);
      }
   }

   private void func_146407_a(URI var1) {
      try {
         Class var2 = Class.forName("java.awt.Desktop");
         Object var3 = var2.getMethod("getDesktop").invoke(null);
         var2.getMethod("browse", URI.class).invoke(var3, var1);
      } catch (Throwable var4) {
         field_146408_f.error("Couldn't open link", var4);
      }
   }

   public void func_146404_p_() {
      if (this.field_146417_i) {
         this.field_146415_a
            .func_146175_b(this.field_146415_a.func_146197_a(-1, this.field_146415_a.func_146198_h(), false) - this.field_146415_a.func_146198_h());
         if (this.field_146413_s >= this.field_146412_t.size()) {
            this.field_146413_s = 0;
         }
      } else {
         int var1 = this.field_146415_a.func_146197_a(-1, this.field_146415_a.func_146198_h(), false);
         this.field_146412_t.clear();
         this.field_146413_s = 0;
         String var2 = this.field_146415_a.func_146179_b().substring(var1).toLowerCase();
         String var3 = this.field_146415_a.func_146179_b().substring(0, this.field_146415_a.func_146198_h());
         this.func_146405_a(var3, var2);
         if (this.field_146412_t.isEmpty()) {
            return;
         }

         this.field_146417_i = true;
         this.field_146415_a.func_146175_b(var1 - this.field_146415_a.func_146198_h());
      }

      if (this.field_146412_t.size() > 1) {
         StringBuilder var4 = new StringBuilder();

         for(String var6 : this.field_146412_t) {
            if (var4.length() > 0) {
               var4.append(", ");
            }

            var4.append(var6);
         }

         this.field_146297_k.field_71456_v.func_146158_b().func_146234_a(new ChatComponentText(var4.toString()), 1);
      }

      this.field_146415_a.func_146191_b((String)this.field_146412_t.get(this.field_146413_s++));
   }

   private void func_146405_a(String var1, String var2) {
      if (var1.length() >= 1) {
         this.field_146297_k.field_71439_g.field_71174_a.func_147297_a(new C14PacketTabComplete(var1));
         this.field_146414_r = true;
      }
   }

   public void func_146402_a(int var1) {
      int var2 = this.field_146416_h + var1;
      int var3 = this.field_146297_k.field_71456_v.func_146158_b().func_146238_c().size();
      if (var2 < 0) {
         var2 = 0;
      }

      if (var2 > var3) {
         var2 = var3;
      }

      if (var2 != this.field_146416_h) {
         if (var2 == var3) {
            this.field_146416_h = var3;
            this.field_146415_a.func_146180_a(this.field_146410_g);
         } else {
            if (this.field_146416_h == var3) {
               this.field_146410_g = this.field_146415_a.func_146179_b();
            }

            this.field_146415_a.func_146180_a((String)this.field_146297_k.field_71456_v.func_146158_b().func_146238_c().get(var2));
            this.field_146416_h = var2;
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      func_73734_a(2, this.field_146295_m - 14, this.field_146294_l - 2, this.field_146295_m - 2, -2147483648);
      this.field_146415_a.func_146194_f();
      IChatComponent var4 = this.field_146297_k.field_71456_v.func_146158_b().func_146236_a(Mouse.getX(), Mouse.getY());
      if (var4 != null && var4.func_150256_b().func_150210_i() != null) {
         HoverEvent var5 = var4.func_150256_b().func_150210_i();
         if (var5.func_150701_a() == HoverEvent$Action.SHOW_ITEM) {
            ItemStack var6 = null;

            try {
               NBTBase var7 = JsonToNBT.func_150315_a(var5.func_150702_b().func_150260_c());
               if (var7 != null && var7 instanceof NBTTagCompound) {
                  var6 = ItemStack.func_77949_a((NBTTagCompound)var7);
               }
            } catch (NBTException var11) {
            }

            if (var6 != null) {
               this.func_146285_a(var6, var1, var2);
            } else {
               this.func_146279_a(EnumChatFormatting.RED + "Invalid Item!", var1, var2);
            }
         } else if (var5.func_150701_a() == HoverEvent$Action.SHOW_TEXT) {
            this.func_146283_a(Splitter.on("\n").splitToList(var5.func_150702_b().func_150254_d()), var1, var2);
         } else if (var5.func_150701_a() == HoverEvent$Action.SHOW_ACHIEVEMENT) {
            StatBase var12 = StatList.func_151177_a(var5.func_150702_b().func_150260_c());
            if (var12 != null) {
               IChatComponent var13 = var12.func_150951_e();
               ChatComponentTranslation var8 = new ChatComponentTranslation("stats.tooltip.type." + (var12.func_75967_d() ? "achievement" : "statistic"));
               var8.func_150256_b().func_150217_b(true);
               String var9 = var12 instanceof Achievement ? ((Achievement)var12).func_75989_e() : null;
               ArrayList var10 = Lists.newArrayList(new String[]{var13.func_150254_d(), var8.func_150254_d()});
               if (var9 != null) {
                  var10.addAll(this.field_146289_q.func_78271_c(var9, 150));
               }

               this.func_146283_a(var10, var1, var2);
            } else {
               this.func_146279_a(EnumChatFormatting.RED + "Invalid statistic/achievement!", var1, var2);
            }
         }

         GL11.glDisable(2896);
      }

      super.func_73863_a(var1, var2, var3);
   }

   public void func_146406_a(String[] var1) {
      if (this.field_146414_r) {
         this.field_146417_i = false;
         this.field_146412_t.clear();

         for(String var5 : var1) {
            if (var5.length() > 0) {
               this.field_146412_t.add(var5);
            }
         }

         String var6 = this.field_146415_a.func_146179_b().substring(this.field_146415_a.func_146197_a(-1, this.field_146415_a.func_146198_h(), false));
         String var7 = StringUtils.getCommonPrefix(var1);
         if (var7.length() > 0 && !var6.equalsIgnoreCase(var7)) {
            this.field_146415_a
               .func_146175_b(this.field_146415_a.func_146197_a(-1, this.field_146415_a.func_146198_h(), false) - this.field_146415_a.func_146198_h());
            this.field_146415_a.func_146191_b(var7);
         } else if (this.field_146412_t.size() > 0) {
            this.field_146417_i = true;
            this.func_146404_p_();
         }
      }
   }

   @Override
   public boolean func_73868_f() {
      return false;
   }
}
