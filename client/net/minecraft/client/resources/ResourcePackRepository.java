package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.HttpUtil;

public class ResourcePackRepository {
   protected static final FileFilter field_110622_a = new ResourcePackRepository$1();
   private final File field_110618_d;
   public final IResourcePack field_110620_b;
   private final File field_148534_e;
   public final IMetadataSerializer field_110621_c;
   private IResourcePack field_148532_f;
   private boolean field_148533_g;
   private List field_110619_e = Lists.newArrayList();
   private List field_110617_f = Lists.newArrayList();

   public ResourcePackRepository(File var1, File var2, IResourcePack var3, IMetadataSerializer var4, GameSettings var5) {
      super();
      this.field_110618_d = var1;
      this.field_148534_e = var2;
      this.field_110620_b = var3;
      this.field_110621_c = var4;
      this.func_110616_f();
      this.func_110611_a();

      for(String var7 : var5.field_151453_l) {
         for(ResourcePackRepository$Entry var9 : this.field_110619_e) {
            if (var9.func_110515_d().equals(var7)) {
               this.field_110617_f.add(var9);
               break;
            }
         }
      }
   }

   private void func_110616_f() {
      if (!this.field_110618_d.isDirectory()) {
         this.field_110618_d.delete();
         this.field_110618_d.mkdirs();
      }
   }

   private List func_110614_g() {
      return this.field_110618_d.isDirectory() ? Arrays.asList(this.field_110618_d.listFiles(field_110622_a)) : Collections.emptyList();
   }

   public void func_110611_a() {
      ArrayList var1 = Lists.newArrayList();

      for(File var3 : this.func_110614_g()) {
         ResourcePackRepository$Entry var4 = new ResourcePackRepository$Entry(this, var3, null);
         if (!this.field_110619_e.contains(var4)) {
            try {
               var4.func_110516_a();
               var1.add(var4);
            } catch (Exception var6) {
               var1.remove(var4);
            }
         } else {
            int var5 = this.field_110619_e.indexOf(var4);
            if (var5 > -1 && var5 < this.field_110619_e.size()) {
               var1.add(this.field_110619_e.get(var5));
            }
         }
      }

      this.field_110619_e.removeAll(var1);

      for(ResourcePackRepository$Entry var8 : this.field_110619_e) {
         var8.func_110517_b();
      }

      this.field_110619_e = var1;
   }

   public List func_110609_b() {
      return ImmutableList.copyOf(this.field_110619_e);
   }

   public List func_110613_c() {
      return ImmutableList.copyOf(this.field_110617_f);
   }

   public void func_148527_a(List var1) {
      this.field_110617_f.clear();
      this.field_110617_f.addAll(var1);
   }

   public File func_110612_e() {
      return this.field_110618_d;
   }

   public void func_148526_a(String var1) {
      String var2 = var1.substring(var1.lastIndexOf("/") + 1);
      if (var2.contains("?")) {
         var2 = var2.substring(0, var2.indexOf("?"));
      }

      if (var2.endsWith(".zip")) {
         File var3 = new File(this.field_148534_e, var2.replaceAll("\\W", ""));
         this.func_148529_f();
         this.func_148528_a(var1, var3);
      }
   }

   private void func_148528_a(String var1, File var2) {
      HashMap var3 = Maps.newHashMap();
      GuiScreenWorking var4 = new GuiScreenWorking();
      var3.put("X-Minecraft-Username", Minecraft.func_71410_x().func_110432_I().func_111285_a());
      var3.put("X-Minecraft-UUID", Minecraft.func_71410_x().func_110432_I().func_148255_b());
      var3.put("X-Minecraft-Version", "1.7.10");
      this.field_148533_g = true;
      Minecraft.func_71410_x().func_147108_a(var4);
      HttpUtil.func_151223_a(var2, var1, new ResourcePackRepository$2(this), var3, 52428800, var4, Minecraft.func_71410_x().func_110437_J());
   }

   public IResourcePack func_148530_e() {
      return this.field_148532_f;
   }

   public void func_148529_f() {
      this.field_148532_f = null;
      this.field_148533_g = false;
   }
}
