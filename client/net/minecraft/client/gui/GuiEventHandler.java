package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public abstract class GuiEventHandler extends Gui implements IGuiEventListenerDeferred {
   @Nullable
   private IGuiEventListener field_195075_a;
   private boolean field_195076_f;

   public GuiEventHandler() {
      super();
   }

   protected abstract List<? extends IGuiEventListener> func_195074_b();

   private final boolean func_195071_s() {
      return this.field_195076_f;
   }

   protected final void func_195072_d(boolean var1) {
      this.field_195076_f = var1;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.field_195075_a;
   }

   protected void func_195073_a(@Nullable IGuiEventListener var1) {
      this.field_195075_a = var1;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      Iterator var6 = this.func_195074_b().iterator();

      IGuiEventListener var7;
      boolean var8;
      do {
         if (!var6.hasNext()) {
            return false;
         }

         var7 = (IGuiEventListener)var6.next();
         var8 = var7.mouseClicked(var1, var3, var5);
      } while(!var8);

      this.func_205725_b(var7);
      if (var5 == 0) {
         this.func_195072_d(true);
      }

      return true;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return IGuiEventListenerDeferred.super.keyPressed(var1, var2, var3);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.getFocused() != null && this.func_195071_s() && var5 == 0 ? this.getFocused().mouseDragged(var1, var3, var5, var6, var8) : false;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      this.func_195072_d(false);
      return IGuiEventListenerDeferred.super.mouseReleased(var1, var3, var5);
   }

   public void func_205725_b(@Nullable IGuiEventListener var1) {
      this.func_205728_a(var1, this.func_195074_b().indexOf(this.getFocused()));
   }

   public void func_207714_t() {
      int var1 = this.func_195074_b().indexOf(this.getFocused());
      int var2 = var1 == -1 ? 0 : (var1 + 1) % this.func_195074_b().size();
      this.func_205728_a(this.func_207713_a(var2), var1);
   }

   @Nullable
   private IGuiEventListener func_207713_a(int var1) {
      List var2 = this.func_195074_b();
      int var3 = var2.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         IGuiEventListener var5 = (IGuiEventListener)var2.get((var1 + var4) % var3);
         if (var5.func_207704_ae_()) {
            return var5;
         }
      }

      return null;
   }

   private void func_205728_a(@Nullable IGuiEventListener var1, int var2) {
      IGuiEventListener var3 = var2 == -1 ? null : (IGuiEventListener)this.func_195074_b().get(var2);
      if (var3 != var1) {
         if (var3 != null) {
            var3.func_205700_b(false);
         }

         if (var1 != null) {
            var1.func_205700_b(true);
         }

         this.func_195073_a(var1);
      }
   }
}
