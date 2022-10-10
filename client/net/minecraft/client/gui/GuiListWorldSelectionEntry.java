package net.minecraft.client.gui;

import com.google.common.hash.Hashing;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class GuiListWorldSelectionEntry extends GuiListExtended.IGuiListEntry<GuiListWorldSelectionEntry> implements AutoCloseable {
   private static final Logger field_186780_a = LogManager.getLogger();
   private static final DateFormat field_186781_b = new SimpleDateFormat();
   private static final ResourceLocation field_186782_c = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation field_186783_d = new ResourceLocation("textures/gui/world_selection.png");
   private final Minecraft field_186784_e;
   private final GuiWorldSelection field_186785_f;
   private final WorldSummary field_186786_g;
   private final ResourceLocation field_186787_h;
   private final GuiListWorldSelection field_186788_i;
   private File field_186789_j;
   @Nullable
   private final DynamicTexture field_186790_k;
   private long field_186791_l;

   public GuiListWorldSelectionEntry(GuiListWorldSelection var1, WorldSummary var2, ISaveFormat var3) {
      super();
      this.field_186788_i = var1;
      this.field_186785_f = var1.func_186796_g();
      this.field_186786_g = var2;
      this.field_186784_e = Minecraft.func_71410_x();
      this.field_186787_h = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(var2.func_75786_a()) + "/icon");
      this.field_186789_j = var3.func_186352_b(var2.func_75786_a(), "icon.png");
      if (!this.field_186789_j.isFile()) {
         this.field_186789_j = null;
      }

      this.field_186790_k = this.func_195033_j();
   }

   public void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6) {
      int var7 = this.func_195001_c();
      int var8 = this.func_195002_d();
      String var9 = this.field_186786_g.func_75788_b();
      String var10 = this.field_186786_g.func_75786_a() + " (" + field_186781_b.format(new Date(this.field_186786_g.func_75784_e())) + ")";
      String var11 = "";
      if (StringUtils.isEmpty(var9)) {
         var9 = I18n.func_135052_a("selectWorld.world") + " " + (this.func_195003_b() + 1);
      }

      if (this.field_186786_g.func_75785_d()) {
         var11 = I18n.func_135052_a("selectWorld.conversion") + " " + var11;
      } else {
         var11 = I18n.func_135052_a("gameMode." + this.field_186786_g.func_75790_f().func_77149_b());
         if (this.field_186786_g.func_75789_g()) {
            var11 = TextFormatting.DARK_RED + I18n.func_135052_a("gameMode.hardcore") + TextFormatting.RESET;
         }

         if (this.field_186786_g.func_75783_h()) {
            var11 = var11 + ", " + I18n.func_135052_a("selectWorld.cheats");
         }

         String var12 = this.field_186786_g.func_200538_i().func_150254_d();
         if (this.field_186786_g.func_186355_l()) {
            if (this.field_186786_g.func_186356_m()) {
               var11 = var11 + ", " + I18n.func_135052_a("selectWorld.version") + " " + TextFormatting.RED + var12 + TextFormatting.RESET;
            } else {
               var11 = var11 + ", " + I18n.func_135052_a("selectWorld.version") + " " + TextFormatting.ITALIC + var12 + TextFormatting.RESET;
            }
         } else {
            var11 = var11 + ", " + I18n.func_135052_a("selectWorld.version") + " " + var12;
         }
      }

      this.field_186784_e.field_71466_p.func_211126_b(var9, (float)(var8 + 32 + 3), (float)(var7 + 1), 16777215);
      this.field_186784_e.field_71466_p.func_211126_b(var10, (float)(var8 + 32 + 3), (float)(var7 + this.field_186784_e.field_71466_p.field_78288_b + 3), 8421504);
      this.field_186784_e.field_71466_p.func_211126_b(var11, (float)(var8 + 32 + 3), (float)(var7 + this.field_186784_e.field_71466_p.field_78288_b + this.field_186784_e.field_71466_p.field_78288_b + 3), 8421504);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_186784_e.func_110434_K().func_110577_a(this.field_186790_k != null ? this.field_186787_h : field_186782_c);
      GlStateManager.func_179147_l();
      Gui.func_146110_a(var8, var7, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.func_179084_k();
      if (this.field_186784_e.field_71474_y.field_85185_A || var5) {
         this.field_186784_e.func_110434_K().func_110577_a(field_186783_d);
         Gui.func_73734_a(var8, var7, var8 + 32, var7 + 32, -1601138544);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         int var15 = var3 - var8;
         int var13 = var15 < 32 ? 32 : 0;
         if (this.field_186786_g.func_186355_l()) {
            Gui.func_146110_a(var8, var7, 32.0F, (float)var13, 32, 32, 256.0F, 256.0F);
            if (this.field_186786_g.func_202842_n()) {
               Gui.func_146110_a(var8, var7, 96.0F, (float)var13, 32, 32, 256.0F, 256.0F);
               if (var15 < 32) {
                  ITextComponent var14 = (new TextComponentTranslation("selectWorld.tooltip.unsupported", new Object[]{this.field_186786_g.func_200538_i()})).func_211708_a(TextFormatting.RED);
                  this.field_186785_f.func_184861_a(this.field_186784_e.field_71466_p.func_78280_d(var14.func_150254_d(), 175));
               }
            } else if (this.field_186786_g.func_186356_m()) {
               Gui.func_146110_a(var8, var7, 96.0F, (float)var13, 32, 32, 256.0F, 256.0F);
               if (var15 < 32) {
                  this.field_186785_f.func_184861_a(TextFormatting.RED + I18n.func_135052_a("selectWorld.tooltip.fromNewerVersion1") + "\n" + TextFormatting.RED + I18n.func_135052_a("selectWorld.tooltip.fromNewerVersion2"));
               }
            }
         } else {
            Gui.func_146110_a(var8, var7, 0.0F, (float)var13, 32, 32, 256.0F, 256.0F);
         }
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.field_186788_i.func_186792_d(this.func_195003_b());
      if (var1 - (double)this.func_195002_d() <= 32.0D) {
         this.func_186774_a();
         return true;
      } else if (Util.func_211177_b() - this.field_186791_l < 250L) {
         this.func_186774_a();
         return true;
      } else {
         this.field_186791_l = Util.func_211177_b();
         return false;
      }
   }

   public void func_186774_a() {
      if (!this.field_186786_g.func_197731_n() && !this.field_186786_g.func_202842_n()) {
         if (this.field_186786_g.func_186356_m()) {
            this.field_186784_e.func_147108_a(new GuiYesNo((var1x, var2x) -> {
               if (var1x) {
                  try {
                     this.func_186777_e();
                  } catch (Exception var4) {
                     field_186780_a.error("Failure to open 'future world'", var4);
                     this.field_186784_e.func_147108_a(new GuiScreenAlert(() -> {
                        this.field_186784_e.func_147108_a(this.field_186785_f);
                     }, new TextComponentTranslation("selectWorld.futureworld.error.title", new Object[0]), new TextComponentTranslation("selectWorld.futureworld.error.text", new Object[0])));
                  }
               } else {
                  this.field_186784_e.func_147108_a(this.field_186785_f);
               }

            }, I18n.func_135052_a("selectWorld.versionQuestion"), I18n.func_135052_a("selectWorld.versionWarning", this.field_186786_g.func_200538_i().func_150254_d()), I18n.func_135052_a("selectWorld.versionJoinButton"), I18n.func_135052_a("gui.cancel"), 0));
         } else {
            this.func_186777_e();
         }
      } else {
         String var1 = I18n.func_135052_a("selectWorld.backupQuestion");
         String var2 = I18n.func_135052_a("selectWorld.backupWarning", this.field_186786_g.func_200538_i().func_150254_d(), "1.13.2");
         if (this.field_186786_g.func_202842_n()) {
            var1 = I18n.func_135052_a("selectWorld.backupQuestion.customized");
            var2 = I18n.func_135052_a("selectWorld.backupWarning.customized");
         }

         this.field_186784_e.func_147108_a(new GuiConfirmBackup(this.field_186785_f, (var1x) -> {
            if (var1x) {
               String var2 = this.field_186786_g.func_75786_a();
               GuiWorldEdit.func_200212_a(this.field_186784_e.func_71359_d(), var2);
            }

            this.func_186777_e();
         }, var1, var2));
      }

   }

   public void func_186776_b() {
      this.field_186784_e.func_147108_a(new GuiYesNo((var1, var2) -> {
         if (var1) {
            this.field_186784_e.func_147108_a(new GuiScreenWorking());
            ISaveFormat var3 = this.field_186784_e.func_71359_d();
            var3.func_75800_d();
            var3.func_75802_e(this.field_186786_g.func_75786_a());
            this.field_186788_i.func_212330_a(() -> {
               return this.field_186785_f.field_212352_g.func_146179_b();
            }, true);
         }

         this.field_186784_e.func_147108_a(this.field_186785_f);
      }, I18n.func_135052_a("selectWorld.deleteQuestion"), I18n.func_135052_a("selectWorld.deleteWarning", this.field_186786_g.func_75788_b()), I18n.func_135052_a("selectWorld.deleteButton"), I18n.func_135052_a("gui.cancel"), 0));
   }

   public void func_186778_c() {
      this.field_186784_e.func_147108_a(new GuiWorldEdit((var1, var2) -> {
         if (var1) {
            this.field_186788_i.func_212330_a(() -> {
               return this.field_186785_f.field_212352_g.func_146179_b();
            }, true);
         }

         this.field_186784_e.func_147108_a(this.field_186785_f);
      }, this.field_186786_g.func_75786_a()));
   }

   public void func_186779_d() {
      try {
         this.field_186784_e.func_147108_a(new GuiScreenWorking());
         GuiCreateWorld var1 = new GuiCreateWorld(this.field_186785_f);
         ISaveHandler var2 = this.field_186784_e.func_71359_d().func_197715_a(this.field_186786_g.func_75786_a(), (MinecraftServer)null);
         WorldInfo var3 = var2.func_75757_d();
         var2.func_75759_a();
         if (var3 != null) {
            var1.func_146318_a(var3);
            if (this.field_186786_g.func_202842_n()) {
               this.field_186784_e.func_147108_a(new GuiYesNo((var2x, var3x) -> {
                  if (var2x) {
                     this.field_186784_e.func_147108_a(var1);
                  } else {
                     this.field_186784_e.func_147108_a(this.field_186785_f);
                  }

               }, I18n.func_135052_a("selectWorld.recreate.customized.title"), I18n.func_135052_a("selectWorld.recreate.customized.text"), I18n.func_135052_a("gui.proceed"), I18n.func_135052_a("gui.cancel"), 0));
            } else {
               this.field_186784_e.func_147108_a(var1);
            }
         }
      } catch (Exception var4) {
         field_186780_a.error("Unable to recreate world", var4);
         this.field_186784_e.func_147108_a(new GuiScreenAlert(() -> {
            this.field_186784_e.func_147108_a(this.field_186785_f);
         }, new TextComponentTranslation("selectWorld.recreate.error.title", new Object[0]), new TextComponentTranslation("selectWorld.recreate.error.text", new Object[0])));
      }

   }

   private void func_186777_e() {
      this.field_186784_e.func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
      if (this.field_186784_e.func_71359_d().func_90033_f(this.field_186786_g.func_75786_a())) {
         this.field_186784_e.func_71371_a(this.field_186786_g.func_75786_a(), this.field_186786_g.func_75788_b(), (WorldSettings)null);
      }

   }

   @Nullable
   private DynamicTexture func_195033_j() {
      boolean var1 = this.field_186789_j != null && this.field_186789_j.isFile();
      if (var1) {
         try {
            FileInputStream var2 = new FileInputStream(this.field_186789_j);
            Throwable var3 = null;

            DynamicTexture var6;
            try {
               NativeImage var4 = NativeImage.func_195713_a(var2);
               Validate.validState(var4.func_195702_a() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(var4.func_195714_b() == 64, "Must be 64 pixels high", new Object[0]);
               DynamicTexture var5 = new DynamicTexture(var4);
               this.field_186784_e.func_110434_K().func_110579_a(this.field_186787_h, var5);
               var6 = var5;
            } catch (Throwable var16) {
               var3 = var16;
               throw var16;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return var6;
         } catch (Throwable var18) {
            field_186780_a.error("Invalid icon for world {}", this.field_186786_g.func_75786_a(), var18);
            this.field_186789_j = null;
            return null;
         }
      } else {
         this.field_186784_e.func_110434_K().func_147645_c(this.field_186787_h);
         return null;
      }
   }

   public void close() {
      if (this.field_186790_k != null) {
         this.field_186790_k.close();
      }

   }

   public void func_195000_a(float var1) {
   }
}
