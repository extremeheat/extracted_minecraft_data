package net.minecraft.client.gui;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiListWorldSelection extends GuiListExtended<GuiListWorldSelectionEntry> {
   private static final Logger field_186797_u = LogManager.getLogger();
   private final GuiWorldSelection field_186798_v;
   private int field_186800_x = -1;
   @Nullable
   private List<WorldSummary> field_212331_y = null;

   public GuiListWorldSelection(GuiWorldSelection var1, Minecraft var2, int var3, int var4, int var5, int var6, int var7, Supplier<String> var8, @Nullable GuiListWorldSelection var9) {
      super(var2, var3, var4, var5, var6, var7);
      this.field_186798_v = var1;
      if (var9 != null) {
         this.field_212331_y = var9.field_212331_y;
      }

      this.func_212330_a(var8, false);
   }

   public void func_212330_a(Supplier<String> var1, boolean var2) {
      this.func_195086_c();
      ISaveFormat var3 = this.field_148161_k.func_71359_d();
      if (this.field_212331_y == null || var2) {
         try {
            this.field_212331_y = var3.func_75799_b();
         } catch (AnvilConverterException var7) {
            field_186797_u.error("Couldn't load level list", var7);
            this.field_148161_k.func_147108_a(new GuiErrorScreen(I18n.func_135052_a("selectWorld.unable_to_load"), var7.getMessage()));
            return;
         }

         Collections.sort(this.field_212331_y);
      }

      String var4 = ((String)var1.get()).toLowerCase(Locale.ROOT);
      Iterator var5 = this.field_212331_y.iterator();

      while(true) {
         WorldSummary var6;
         do {
            if (!var5.hasNext()) {
               return;
            }

            var6 = (WorldSummary)var5.next();
         } while(!var6.func_75788_b().toLowerCase(Locale.ROOT).contains(var4) && !var6.func_75786_a().toLowerCase(Locale.ROOT).contains(var4));

         this.func_195085_a(new GuiListWorldSelectionEntry(this, var6, this.field_148161_k.func_71359_d()));
      }
   }

   protected int func_148137_d() {
      return super.func_148137_d() + 20;
   }

   public int func_148139_c() {
      return super.func_148139_c() + 50;
   }

   public void func_186792_d(int var1) {
      this.field_186800_x = var1;
      this.field_186798_v.func_184863_a(this.func_186794_f());
   }

   protected boolean func_148131_a(int var1) {
      return var1 == this.field_186800_x;
   }

   @Nullable
   public GuiListWorldSelectionEntry func_186794_f() {
      return this.field_186800_x >= 0 && this.field_186800_x < this.func_148127_b() ? (GuiListWorldSelectionEntry)this.func_195074_b().get(this.field_186800_x) : null;
   }

   public GuiWorldSelection func_186796_g() {
      return this.field_186798_v;
   }
}
