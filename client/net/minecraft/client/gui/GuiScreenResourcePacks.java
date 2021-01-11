package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

public class GuiScreenResourcePacks extends GuiScreen {
   private static final Logger field_146968_a = LogManager.getLogger();
   private final GuiScreen field_146965_f;
   private List<ResourcePackListEntry> field_146966_g;
   private List<ResourcePackListEntry> field_146969_h;
   private GuiResourcePackAvailable field_146970_i;
   private GuiResourcePackSelected field_146967_r;
   private boolean field_175289_s = false;

   public GuiScreenResourcePacks(GuiScreen var1) {
      super();
      this.field_146965_f = var1;
   }

   public void func_73866_w_() {
      this.field_146292_n.add(new GuiOptionButton(2, this.field_146294_l / 2 - 154, this.field_146295_m - 48, I18n.func_135052_a("resourcePack.openFolder")));
      this.field_146292_n.add(new GuiOptionButton(1, this.field_146294_l / 2 + 4, this.field_146295_m - 48, I18n.func_135052_a("gui.done")));
      if (!this.field_175289_s) {
         this.field_146966_g = Lists.newArrayList();
         this.field_146969_h = Lists.newArrayList();
         ResourcePackRepository var1 = this.field_146297_k.func_110438_M();
         var1.func_110611_a();
         ArrayList var2 = Lists.newArrayList(var1.func_110609_b());
         var2.removeAll(var1.func_110613_c());
         Iterator var3 = var2.iterator();

         ResourcePackRepository.Entry var4;
         while(var3.hasNext()) {
            var4 = (ResourcePackRepository.Entry)var3.next();
            this.field_146966_g.add(new ResourcePackListEntryFound(this, var4));
         }

         var3 = Lists.reverse(var1.func_110613_c()).iterator();

         while(var3.hasNext()) {
            var4 = (ResourcePackRepository.Entry)var3.next();
            this.field_146969_h.add(new ResourcePackListEntryFound(this, var4));
         }

         this.field_146969_h.add(new ResourcePackListEntryDefault(this));
      }

      this.field_146970_i = new GuiResourcePackAvailable(this.field_146297_k, 200, this.field_146295_m, this.field_146966_g);
      this.field_146970_i.func_148140_g(this.field_146294_l / 2 - 4 - 200);
      this.field_146970_i.func_148134_d(7, 8);
      this.field_146967_r = new GuiResourcePackSelected(this.field_146297_k, 200, this.field_146295_m, this.field_146969_h);
      this.field_146967_r.func_148140_g(this.field_146294_l / 2 + 4);
      this.field_146967_r.func_148134_d(7, 8);
   }

   public void func_146274_d() {
      super.func_146274_d();
      this.field_146967_r.func_178039_p();
      this.field_146970_i.func_178039_p();
   }

   public boolean func_146961_a(ResourcePackListEntry var1) {
      return this.field_146969_h.contains(var1);
   }

   public List<ResourcePackListEntry> func_146962_b(ResourcePackListEntry var1) {
      return this.func_146961_a(var1) ? this.field_146969_h : this.field_146966_g;
   }

   public List<ResourcePackListEntry> func_146964_g() {
      return this.field_146966_g;
   }

   public List<ResourcePackListEntry> func_146963_h() {
      return this.field_146969_h;
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 2) {
            File var2 = this.field_146297_k.func_110438_M().func_110612_e();
            String var3 = var2.getAbsolutePath();
            if (Util.func_110647_a() == Util.EnumOS.OSX) {
               try {
                  field_146968_a.info(var3);
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", var3});
                  return;
               } catch (IOException var9) {
                  field_146968_a.error("Couldn't open file", var9);
               }
            } else if (Util.func_110647_a() == Util.EnumOS.WINDOWS) {
               String var4 = String.format("cmd.exe /C start \"Open file\" \"%s\"", var3);

               try {
                  Runtime.getRuntime().exec(var4);
                  return;
               } catch (IOException var8) {
                  field_146968_a.error("Couldn't open file", var8);
               }
            }

            boolean var12 = false;

            try {
               Class var5 = Class.forName("java.awt.Desktop");
               Object var6 = var5.getMethod("getDesktop").invoke((Object)null);
               var5.getMethod("browse", URI.class).invoke(var6, var2.toURI());
            } catch (Throwable var7) {
               field_146968_a.error("Couldn't open link", var7);
               var12 = true;
            }

            if (var12) {
               field_146968_a.info("Opening via system class!");
               Sys.openURL("file://" + var3);
            }
         } else if (var1.field_146127_k == 1) {
            if (this.field_175289_s) {
               ArrayList var10 = Lists.newArrayList();
               Iterator var11 = this.field_146969_h.iterator();

               while(var11.hasNext()) {
                  ResourcePackListEntry var13 = (ResourcePackListEntry)var11.next();
                  if (var13 instanceof ResourcePackListEntryFound) {
                     var10.add(((ResourcePackListEntryFound)var13).func_148318_i());
                  }
               }

               Collections.reverse(var10);
               this.field_146297_k.func_110438_M().func_148527_a(var10);
               this.field_146297_k.field_71474_y.field_151453_l.clear();
               this.field_146297_k.field_71474_y.field_183018_l.clear();
               var11 = var10.iterator();

               while(var11.hasNext()) {
                  ResourcePackRepository.Entry var14 = (ResourcePackRepository.Entry)var11.next();
                  this.field_146297_k.field_71474_y.field_151453_l.add(var14.func_110515_d());
                  if (var14.func_183027_f() != 1) {
                     this.field_146297_k.field_71474_y.field_183018_l.add(var14.func_110515_d());
                  }
               }

               this.field_146297_k.field_71474_y.func_74303_b();
               this.field_146297_k.func_110436_a();
            }

            this.field_146297_k.func_147108_a(this.field_146965_f);
         }

      }
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      this.field_146970_i.func_148179_a(var1, var2, var3);
      this.field_146967_r.func_148179_a(var1, var2, var3);
   }

   protected void func_146286_b(int var1, int var2, int var3) {
      super.func_146286_b(var1, var2, var3);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146278_c(0);
      this.field_146970_i.func_148128_a(var1, var2, var3);
      this.field_146967_r.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("resourcePack.title"), this.field_146294_l / 2, 16, 16777215);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("resourcePack.folderInfo"), this.field_146294_l / 2 - 77, this.field_146295_m - 26, 8421504);
      super.func_73863_a(var1, var2, var3);
   }

   public void func_175288_g() {
      this.field_175289_s = true;
   }
}
