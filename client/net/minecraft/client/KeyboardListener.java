package net.minecraft.client;

import java.nio.ByteBuffer;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

public class KeyboardListener {
   private final Minecraft field_197972_a;
   private boolean field_197973_b;
   private long field_197974_c = -1L;
   private long field_204871_d = -1L;
   private long field_204872_e = -1L;
   private boolean field_197975_d;
   private final ByteBuffer field_211563_g = ByteBuffer.allocateDirect(1024);

   public KeyboardListener(Minecraft var1) {
      super();
      this.field_197972_a = var1;
   }

   private void func_197964_a(String var1, Object... var2) {
      this.field_197972_a.field_71456_v.func_146158_b().func_146227_a((new TextComponentString("")).func_150257_a((new TextComponentTranslation("debug.prefix", new Object[0])).func_211709_a(new TextFormatting[]{TextFormatting.YELLOW, TextFormatting.BOLD})).func_150258_a(" ").func_150257_a(new TextComponentTranslation(var1, var2)));
   }

   private void func_204869_b(String var1, Object... var2) {
      this.field_197972_a.field_71456_v.func_146158_b().func_146227_a((new TextComponentString("")).func_150257_a((new TextComponentTranslation("debug.prefix", new Object[0])).func_211709_a(new TextFormatting[]{TextFormatting.RED, TextFormatting.BOLD})).func_150258_a(" ").func_150257_a(new TextComponentTranslation(var1, var2)));
   }

   private boolean func_197962_c(int var1) {
      if (this.field_197974_c > 0L && this.field_197974_c < Util.func_211177_b() - 100L) {
         return true;
      } else {
         switch(var1) {
         case 65:
            this.field_197972_a.field_71438_f.func_72712_a();
            this.func_197964_a("debug.reload_chunks.message");
            return true;
         case 66:
            boolean var2 = !this.field_197972_a.func_175598_ae().func_178634_b();
            this.field_197972_a.func_175598_ae().func_178629_b(var2);
            this.func_197964_a(var2 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            return true;
         case 67:
            if (this.field_197972_a.field_71439_g.func_175140_cp()) {
               return false;
            }

            this.func_197964_a("debug.copy_location.message");
            this.func_197960_a(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.func_212678_a(this.field_197972_a.field_71439_g.field_70170_p.field_73011_w.func_186058_p()), this.field_197972_a.field_71439_g.field_70165_t, this.field_197972_a.field_71439_g.field_70163_u, this.field_197972_a.field_71439_g.field_70161_v, this.field_197972_a.field_71439_g.field_70177_z, this.field_197972_a.field_71439_g.field_70125_A));
            return true;
         case 68:
            if (this.field_197972_a.field_71456_v != null) {
               this.field_197972_a.field_71456_v.func_146158_b().func_146231_a(false);
            }

            return true;
         case 69:
         case 74:
         case 75:
         case 76:
         case 77:
         case 79:
         case 82:
         case 83:
         default:
            return false;
         case 70:
            this.field_197972_a.field_71474_y.func_74306_a(GameSettings.Options.RENDER_DISTANCE, GuiScreen.func_146272_n() ? -1 : 1);
            this.func_197964_a("debug.cycle_renderdistance.message", this.field_197972_a.field_71474_y.field_151451_c);
            return true;
         case 71:
            boolean var3 = this.field_197972_a.field_184132_p.func_190075_b();
            this.func_197964_a(var3 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return true;
         case 72:
            this.field_197972_a.field_71474_y.field_82882_x = !this.field_197972_a.field_71474_y.field_82882_x;
            this.func_197964_a(this.field_197972_a.field_71474_y.field_82882_x ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            this.field_197972_a.field_71474_y.func_74303_b();
            return true;
         case 73:
            if (!this.field_197972_a.field_71439_g.func_175140_cp()) {
               this.func_211556_a(this.field_197972_a.field_71439_g.func_211513_k(2), !GuiScreen.func_146272_n());
            }

            return true;
         case 78:
            if (!this.field_197972_a.field_71439_g.func_211513_k(2)) {
               this.func_197964_a("debug.creative_spectator.error");
            } else if (this.field_197972_a.field_71439_g.func_184812_l_()) {
               this.field_197972_a.field_71439_g.func_71165_d("/gamemode spectator");
            } else if (this.field_197972_a.field_71439_g.func_175149_v()) {
               this.field_197972_a.field_71439_g.func_71165_d("/gamemode creative");
            }

            return true;
         case 80:
            this.field_197972_a.field_71474_y.field_82881_y = !this.field_197972_a.field_71474_y.field_82881_y;
            this.field_197972_a.field_71474_y.func_74303_b();
            this.func_197964_a(this.field_197972_a.field_71474_y.field_82881_y ? "debug.pause_focus.on" : "debug.pause_focus.off");
            return true;
         case 81:
            this.func_197964_a("debug.help.message");
            GuiNewChat var4 = this.field_197972_a.field_71456_v.func_146158_b();
            var4.func_146227_a(new TextComponentTranslation("debug.reload_chunks.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.show_hitboxes.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.copy_location.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.clear_chat.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.cycle_renderdistance.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.chunk_boundaries.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.advanced_tooltips.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.inspect.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.creative_spectator.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.pause_focus.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.help.help", new Object[0]));
            var4.func_146227_a(new TextComponentTranslation("debug.reload_resourcepacks.help", new Object[0]));
            return true;
         case 84:
            this.func_197964_a("debug.reload_resourcepacks.message");
            this.field_197972_a.func_110436_a();
            return true;
         }
      }
   }

   private void func_211556_a(boolean var1, boolean var2) {
      if (this.field_197972_a.field_71476_x != null) {
         NBTTagCompound var6;
         switch(this.field_197972_a.field_71476_x.field_72313_a) {
         case BLOCK:
            BlockPos var7 = this.field_197972_a.field_71476_x.func_178782_a();
            IBlockState var8 = this.field_197972_a.field_71439_g.field_70170_p.func_180495_p(var7);
            if (var1) {
               if (var2) {
                  this.field_197972_a.field_71439_g.field_71174_a.func_211523_k().func_211547_a(var7, (var3x) -> {
                     this.func_211558_a(var8, var7, var3x);
                     this.func_197964_a("debug.inspect.server.block");
                  });
               } else {
                  TileEntity var9 = this.field_197972_a.field_71439_g.field_70170_p.func_175625_s(var7);
                  var6 = var9 != null ? var9.func_189515_b(new NBTTagCompound()) : null;
                  this.func_211558_a(var8, var7, var6);
                  this.func_197964_a("debug.inspect.client.block");
               }
            } else {
               this.func_211558_a(var8, var7, (NBTTagCompound)null);
               this.func_197964_a("debug.inspect.client.block");
            }
            break;
         case ENTITY:
            Entity var3 = this.field_197972_a.field_71476_x.field_72308_g;
            if (var3 == null) {
               return;
            }

            ResourceLocation var4 = IRegistry.field_212629_r.func_177774_c(var3.func_200600_R());
            Vec3d var5 = new Vec3d(var3.field_70165_t, var3.field_70163_u, var3.field_70161_v);
            if (var1) {
               if (var2) {
                  this.field_197972_a.field_71439_g.field_71174_a.func_211523_k().func_211549_a(var3.func_145782_y(), (var3x) -> {
                     this.func_211557_a(var4, var5, var3x);
                     this.func_197964_a("debug.inspect.server.entity");
                  });
               } else {
                  var6 = var3.func_189511_e(new NBTTagCompound());
                  this.func_211557_a(var4, var5, var6);
                  this.func_197964_a("debug.inspect.client.entity");
               }
            } else {
               this.func_211557_a(var4, var5, (NBTTagCompound)null);
               this.func_197964_a("debug.inspect.client.entity");
            }
         }

      }
   }

   private void func_211558_a(IBlockState var1, BlockPos var2, @Nullable NBTTagCompound var3) {
      if (var3 != null) {
         var3.func_82580_o("x");
         var3.func_82580_o("y");
         var3.func_82580_o("z");
         var3.func_82580_o("id");
      }

      String var4 = BlockStateParser.func_197247_a(var1, var3);
      String var5 = String.format(Locale.ROOT, "/setblock %d %d %d %s", var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p(), var4);
      this.func_197960_a(var5);
   }

   private void func_211557_a(ResourceLocation var1, Vec3d var2, @Nullable NBTTagCompound var3) {
      String var4;
      if (var3 != null) {
         var3.func_82580_o("UUIDMost");
         var3.func_82580_o("UUIDLeast");
         var3.func_82580_o("Pos");
         var3.func_82580_o("Dimension");
         String var5 = var3.func_197637_c().getString();
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", var1.toString(), var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, var5);
      } else {
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", var1.toString(), var2.field_72450_a, var2.field_72448_b, var2.field_72449_c);
      }

      this.func_197960_a(var4);
   }

   public void func_197961_a(long var1, int var3, int var4, int var5, int var6) {
      if (var1 == this.field_197972_a.field_195558_d.func_198092_i()) {
         if (this.field_197974_c > 0L) {
            if (!InputMappings.func_197956_a(67) || !InputMappings.func_197956_a(292)) {
               this.field_197974_c = -1L;
            }
         } else if (InputMappings.func_197956_a(67) && InputMappings.func_197956_a(292)) {
            this.field_197975_d = true;
            this.field_197974_c = Util.func_211177_b();
            this.field_204871_d = Util.func_211177_b();
            this.field_204872_e = 0L;
         }

         GuiScreen var7 = this.field_197972_a.field_71462_r;
         if (var5 == 1 && (!(this.field_197972_a.field_71462_r instanceof GuiControls) || ((GuiControls)var7).field_152177_g <= Util.func_211177_b() - 20L)) {
            if (this.field_197972_a.field_71474_y.field_152395_am.func_197976_a(var3, var4)) {
               this.field_197972_a.field_195558_d.func_198077_g();
               return;
            }

            if (this.field_197972_a.field_71474_y.field_151447_Z.func_197976_a(var3, var4)) {
               if (GuiScreen.func_146271_m()) {
               }

               ScreenShotHelper.func_148260_a(this.field_197972_a.field_71412_D, this.field_197972_a.field_195558_d.func_198109_k(), this.field_197972_a.field_195558_d.func_198091_l(), this.field_197972_a.func_147110_a(), (var1x) -> {
                  this.field_197972_a.func_152344_a(() -> {
                     this.field_197972_a.field_71456_v.func_146158_b().func_146227_a(var1x);
                  });
               });
               return;
            }
         }

         if (var7 != null) {
            boolean[] var8 = new boolean[]{false};
            GuiScreen.func_195121_a(() -> {
               if (var5 != 1 && (var5 != 2 || !this.field_197973_b)) {
                  if (var5 == 0) {
                     var8[0] = var7.keyReleased(var3, var4, var6);
                  }
               } else {
                  var8[0] = var7.keyPressed(var3, var4, var6);
               }

            }, "keyPressed event handler", var7.getClass().getCanonicalName());
            if (var8[0]) {
               return;
            }
         }

         if (this.field_197972_a.field_71462_r == null || this.field_197972_a.field_71462_r.field_146291_p) {
            InputMappings.Input var11 = InputMappings.func_197954_a(var3, var4);
            if (var5 == 0) {
               KeyBinding.func_197980_a(var11, false);
               if (var3 == 292) {
                  if (this.field_197975_d) {
                     this.field_197975_d = false;
                  } else {
                     this.field_197972_a.field_71474_y.field_74330_P = !this.field_197972_a.field_71474_y.field_74330_P;
                     this.field_197972_a.field_71474_y.field_74329_Q = this.field_197972_a.field_71474_y.field_74330_P && GuiScreen.func_146272_n();
                     this.field_197972_a.field_71474_y.field_181657_aC = this.field_197972_a.field_71474_y.field_74330_P && GuiScreen.func_175283_s();
                  }
               }
            } else {
               if (var3 == 66 && GuiScreen.func_146271_m()) {
                  this.field_197972_a.field_71474_y.func_74306_a(GameSettings.Options.NARRATOR, 1);
                  if (var7 instanceof ScreenChatOptions) {
                     ((ScreenChatOptions)var7).func_193024_a();
                  }
               }

               if (var3 == 293 && this.field_197972_a.field_71460_t != null) {
                  this.field_197972_a.field_71460_t.func_175071_c();
               }

               boolean var9 = false;
               if (this.field_197972_a.field_71462_r == null) {
                  if (var3 == 256) {
                     this.field_197972_a.func_71385_j();
                  }

                  var9 = InputMappings.func_197956_a(292) && this.func_197962_c(var3);
                  this.field_197975_d |= var9;
                  if (var3 == 290) {
                     this.field_197972_a.field_71474_y.field_74319_N = !this.field_197972_a.field_71474_y.field_74319_N;
                  }
               }

               if (var9) {
                  KeyBinding.func_197980_a(var11, false);
               } else {
                  KeyBinding.func_197980_a(var11, true);
                  KeyBinding.func_197981_a(var11);
               }

               if (this.field_197972_a.field_71474_y.field_74329_Q) {
                  if (var3 == 48) {
                     this.field_197972_a.func_71383_b(0);
                  }

                  for(int var10 = 0; var10 < 9; ++var10) {
                     if (var3 == 49 + var10) {
                        this.field_197972_a.func_71383_b(var10 + 1);
                     }
                  }
               }
            }
         }

      }
   }

   private void func_197963_a(long var1, int var3, int var4) {
      if (var1 == this.field_197972_a.field_195558_d.func_198092_i()) {
         GuiScreen var5 = this.field_197972_a.field_71462_r;
         if (var5 != null) {
            if (Character.charCount(var3) == 1) {
               GuiScreen.func_195121_a(() -> {
                  var5.charTyped((char)var3, var4);
               }, "charTyped event handler", var5.getClass().getCanonicalName());
            } else {
               char[] var6 = Character.toChars(var3);
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  char var9 = var6[var8];
                  GuiScreen.func_195121_a(() -> {
                     var5.charTyped(var9, var4);
                  }, "charTyped event handler", var5.getClass().getCanonicalName());
               }
            }

         }
      }
   }

   public void func_197967_a(boolean var1) {
      this.field_197973_b = var1;
   }

   public void func_197968_a(long var1) {
      GLFW.glfwSetKeyCallback(var1, this::func_197961_a);
      GLFW.glfwSetCharModsCallback(var1, this::func_197963_a);
   }

   public String func_197965_a() {
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback((var1x, var2x) -> {
         if (var1x != 65545) {
            this.field_197972_a.field_195558_d.func_198084_a(var1x, var2x);
         }

      });
      String var2 = GLFW.glfwGetClipboardString(this.field_197972_a.field_195558_d.func_198092_i());
      GLFW.glfwSetErrorCallback(var1).free();
      return var2 == null ? "" : var2;
   }

   private void func_211559_a(ByteBuffer var1, String var2) {
      MemoryUtil.memUTF8(var2, true, var1);
      GLFW.glfwSetClipboardString(this.field_197972_a.field_195558_d.func_198092_i(), var1);
   }

   public void func_197960_a(String var1) {
      int var2 = MemoryUtil.memLengthUTF8(var1, true);
      if (var2 < this.field_211563_g.capacity()) {
         this.func_211559_a(this.field_211563_g, var1);
         this.field_211563_g.clear();
      } else {
         ByteBuffer var3 = ByteBuffer.allocateDirect(var2);
         this.func_211559_a(var3, var1);
      }

   }

   public void func_204870_b() {
      if (this.field_197974_c > 0L) {
         long var1 = Util.func_211177_b();
         long var3 = 10000L - (var1 - this.field_197974_c);
         long var5 = var1 - this.field_204871_d;
         if (var3 < 0L) {
            if (GuiScreen.func_146271_m()) {
               MemoryUtil.memSet(0L, 0, 1L);
            }

            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
         }

         if (var5 >= 1000L) {
            if (this.field_204872_e == 0L) {
               this.func_197964_a("debug.crash.message");
            } else {
               this.func_204869_b("debug.crash.warning", MathHelper.func_76123_f((float)var3 / 1000.0F));
            }

            this.field_204871_d = var1;
            ++this.field_204872_e;
         }
      }

   }
}
