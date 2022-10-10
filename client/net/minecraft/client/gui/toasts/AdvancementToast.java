package net.minecraft.client.gui.toasts;

import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class AdvancementToast implements IToast {
   private final Advancement field_193679_c;
   private boolean field_194168_d;

   public AdvancementToast(Advancement var1) {
      super();
      this.field_193679_c = var1;
   }

   public IToast.Visibility func_193653_a(GuiToast var1, long var2) {
      var1.func_192989_b().func_110434_K().func_110577_a(field_193654_a);
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      DisplayInfo var4 = this.field_193679_c.func_192068_c();
      var1.func_73729_b(0, 0, 0, 0, 160, 32);
      if (var4 != null) {
         List var5 = var1.func_192989_b().field_71466_p.func_78271_c(var4.func_192297_a().func_150254_d(), 125);
         int var6 = var4.func_192291_d() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (var5.size() == 1) {
            var1.func_192989_b().field_71466_p.func_211126_b(I18n.func_135052_a("advancements.toast." + var4.func_192291_d().func_192307_a()), 30.0F, 7.0F, var6 | -16777216);
            var1.func_192989_b().field_71466_p.func_211126_b(var4.func_192297_a().func_150254_d(), 30.0F, 18.0F, -1);
         } else {
            boolean var7 = true;
            float var8 = 300.0F;
            int var9;
            if (var2 < 1500L) {
               var9 = MathHelper.func_76141_d(MathHelper.func_76131_a((float)(1500L - var2) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               var1.func_192989_b().field_71466_p.func_211126_b(I18n.func_135052_a("advancements.toast." + var4.func_192291_d().func_192307_a()), 30.0F, 11.0F, var6 | var9);
            } else {
               var9 = MathHelper.func_76141_d(MathHelper.func_76131_a((float)(var2 - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int var10 = 16 - var5.size() * var1.func_192989_b().field_71466_p.field_78288_b / 2;

               for(Iterator var11 = var5.iterator(); var11.hasNext(); var10 += var1.func_192989_b().field_71466_p.field_78288_b) {
                  String var12 = (String)var11.next();
                  var1.func_192989_b().field_71466_p.func_211126_b(var12, 30.0F, (float)var10, 16777215 | var9);
               }
            }
         }

         if (!this.field_194168_d && var2 > 0L) {
            this.field_194168_d = true;
            if (var4.func_192291_d() == FrameType.CHALLENGE) {
               var1.func_192989_b().func_147118_V().func_147682_a(SimpleSound.func_194007_a(SoundEvents.field_194228_if, 1.0F, 1.0F));
            }
         }

         RenderHelper.func_74520_c();
         var1.func_192989_b().func_175599_af().func_184391_a((EntityLivingBase)null, var4.func_192298_b(), 8, 8);
         return var2 >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      } else {
         return IToast.Visibility.HIDE;
      }
   }
}
