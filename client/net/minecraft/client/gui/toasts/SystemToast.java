package net.minecraft.client.gui.toasts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

public class SystemToast implements IToast {
   private final SystemToast.Type field_193659_c;
   private String field_193660_d;
   private String field_193661_e;
   private long field_193662_f;
   private boolean field_193663_g;

   public SystemToast(SystemToast.Type var1, ITextComponent var2, @Nullable ITextComponent var3) {
      super();
      this.field_193659_c = var1;
      this.field_193660_d = var2.getString();
      this.field_193661_e = var3 == null ? null : var3.getString();
   }

   public IToast.Visibility func_193653_a(GuiToast var1, long var2) {
      if (this.field_193663_g) {
         this.field_193662_f = var2;
         this.field_193663_g = false;
      }

      var1.func_192989_b().func_110434_K().func_110577_a(field_193654_a);
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      var1.func_73729_b(0, 0, 0, 64, 160, 32);
      if (this.field_193661_e == null) {
         var1.func_192989_b().field_71466_p.func_211126_b(this.field_193660_d, 18.0F, 12.0F, -256);
      } else {
         var1.func_192989_b().field_71466_p.func_211126_b(this.field_193660_d, 18.0F, 7.0F, -256);
         var1.func_192989_b().field_71466_p.func_211126_b(this.field_193661_e, 18.0F, 18.0F, -1);
      }

      return var2 - this.field_193662_f < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
   }

   public void func_193656_a(ITextComponent var1, @Nullable ITextComponent var2) {
      this.field_193660_d = var1.getString();
      this.field_193661_e = var2 == null ? null : var2.getString();
      this.field_193663_g = true;
   }

   public SystemToast.Type func_193652_b() {
      return this.field_193659_c;
   }

   public static void func_193657_a(GuiToast var0, SystemToast.Type var1, ITextComponent var2, @Nullable ITextComponent var3) {
      SystemToast var4 = (SystemToast)var0.func_192990_a(SystemToast.class, var1);
      if (var4 == null) {
         var0.func_192988_a(new SystemToast(var1, var2, var3));
      } else {
         var4.func_193656_a(var2, var3);
      }

   }

   // $FF: synthetic method
   public Object func_193652_b() {
      return this.func_193652_b();
   }

   public static enum Type {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP;

      private Type() {
      }
   }
}
