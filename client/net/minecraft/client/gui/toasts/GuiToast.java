package net.minecraft.client.gui.toasts;

import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class GuiToast extends Gui {
   private final Minecraft field_191790_f;
   private final GuiToast.ToastInstance<?>[] field_191791_g = new GuiToast.ToastInstance[5];
   private final Deque<IToast> field_191792_h = Queues.newArrayDeque();

   public GuiToast(Minecraft var1) {
      super();
      this.field_191790_f = var1;
   }

   public void func_195625_a() {
      if (!this.field_191790_f.field_71474_y.field_74319_N) {
         RenderHelper.func_74518_a();

         for(int var1 = 0; var1 < this.field_191791_g.length; ++var1) {
            GuiToast.ToastInstance var2 = this.field_191791_g[var1];
            if (var2 != null && var2.func_193684_a(this.field_191790_f.field_195558_d.func_198107_o(), var1)) {
               this.field_191791_g[var1] = null;
            }

            if (this.field_191791_g[var1] == null && !this.field_191792_h.isEmpty()) {
               this.field_191791_g[var1] = new GuiToast.ToastInstance((IToast)this.field_191792_h.removeFirst());
            }
         }

      }
   }

   @Nullable
   public <T extends IToast> T func_192990_a(Class<? extends T> var1, Object var2) {
      GuiToast.ToastInstance[] var3 = this.field_191791_g;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         GuiToast.ToastInstance var6 = var3[var5];
         if (var6 != null && var1.isAssignableFrom(var6.func_193685_a().getClass()) && var6.func_193685_a().func_193652_b().equals(var2)) {
            return var6.func_193685_a();
         }
      }

      Iterator var7 = this.field_191792_h.iterator();

      IToast var8;
      do {
         if (!var7.hasNext()) {
            return null;
         }

         var8 = (IToast)var7.next();
      } while(!var1.isAssignableFrom(var8.getClass()) || !var8.func_193652_b().equals(var2));

      return var8;
   }

   public void func_191788_b() {
      Arrays.fill(this.field_191791_g, (Object)null);
      this.field_191792_h.clear();
   }

   public void func_192988_a(IToast var1) {
      this.field_191792_h.add(var1);
   }

   public Minecraft func_192989_b() {
      return this.field_191790_f;
   }

   class ToastInstance<T extends IToast> {
      private final T field_193688_b;
      private long field_193689_c;
      private long field_193690_d;
      private IToast.Visibility field_193691_e;

      private ToastInstance(T var2) {
         super();
         this.field_193689_c = -1L;
         this.field_193690_d = -1L;
         this.field_193691_e = IToast.Visibility.SHOW;
         this.field_193688_b = var2;
      }

      public T func_193685_a() {
         return this.field_193688_b;
      }

      private float func_193686_a(long var1) {
         float var3 = MathHelper.func_76131_a((float)(var1 - this.field_193689_c) / 600.0F, 0.0F, 1.0F);
         var3 *= var3;
         return this.field_193691_e == IToast.Visibility.HIDE ? 1.0F - var3 : var3;
      }

      public boolean func_193684_a(int var1, int var2) {
         long var3 = Util.func_211177_b();
         if (this.field_193689_c == -1L) {
            this.field_193689_c = var3;
            this.field_193691_e.func_194169_a(GuiToast.this.field_191790_f.func_147118_V());
         }

         if (this.field_193691_e == IToast.Visibility.SHOW && var3 - this.field_193689_c <= 600L) {
            this.field_193690_d = var3;
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)var1 - 160.0F * this.func_193686_a(var3), (float)(var2 * 32), (float)(500 + var2));
         IToast.Visibility var5 = this.field_193688_b.func_193653_a(GuiToast.this, var3 - this.field_193690_d);
         GlStateManager.func_179121_F();
         if (var5 != this.field_193691_e) {
            this.field_193689_c = var3 - (long)((int)((1.0F - this.func_193686_a(var3)) * 600.0F));
            this.field_193691_e = var5;
            this.field_193691_e.func_194169_a(GuiToast.this.field_191790_f.func_147118_V());
         }

         return this.field_193691_e == IToast.Visibility.HIDE && var3 - this.field_193689_c > 600L;
      }

      // $FF: synthetic method
      ToastInstance(IToast var2, Object var3) {
         this(var2);
      }
   }
}
