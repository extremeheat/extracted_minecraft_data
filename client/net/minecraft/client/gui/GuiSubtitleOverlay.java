package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GuiSubtitleOverlay extends Gui implements ISoundEventListener {
   private final Minecraft field_184069_a;
   private final List<GuiSubtitleOverlay.Subtitle> field_184070_f = Lists.newArrayList();
   private boolean field_184071_g;

   public GuiSubtitleOverlay(Minecraft var1) {
      super();
      this.field_184069_a = var1;
   }

   public void func_195620_a() {
      if (!this.field_184071_g && this.field_184069_a.field_71474_y.field_186717_N) {
         this.field_184069_a.func_147118_V().func_184402_a(this);
         this.field_184071_g = true;
      } else if (this.field_184071_g && !this.field_184069_a.field_71474_y.field_186717_N) {
         this.field_184069_a.func_147118_V().func_184400_b(this);
         this.field_184071_g = false;
      }

      if (this.field_184071_g && !this.field_184070_f.isEmpty()) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         Vec3d var1 = new Vec3d(this.field_184069_a.field_71439_g.field_70165_t, this.field_184069_a.field_71439_g.field_70163_u + (double)this.field_184069_a.field_71439_g.func_70047_e(), this.field_184069_a.field_71439_g.field_70161_v);
         Vec3d var2 = (new Vec3d(0.0D, 0.0D, -1.0D)).func_178789_a(-this.field_184069_a.field_71439_g.field_70125_A * 0.017453292F).func_178785_b(-this.field_184069_a.field_71439_g.field_70177_z * 0.017453292F);
         Vec3d var3 = (new Vec3d(0.0D, 1.0D, 0.0D)).func_178789_a(-this.field_184069_a.field_71439_g.field_70125_A * 0.017453292F).func_178785_b(-this.field_184069_a.field_71439_g.field_70177_z * 0.017453292F);
         Vec3d var4 = var2.func_72431_c(var3);
         int var5 = 0;
         int var6 = 0;
         Iterator var7 = this.field_184070_f.iterator();

         GuiSubtitleOverlay.Subtitle var8;
         while(var7.hasNext()) {
            var8 = (GuiSubtitleOverlay.Subtitle)var7.next();
            if (var8.func_186825_b() + 3000L <= Util.func_211177_b()) {
               var7.remove();
            } else {
               var6 = Math.max(var6, this.field_184069_a.field_71466_p.func_78256_a(var8.func_186824_a()));
            }
         }

         var6 += this.field_184069_a.field_71466_p.func_78256_a("<") + this.field_184069_a.field_71466_p.func_78256_a(" ") + this.field_184069_a.field_71466_p.func_78256_a(">") + this.field_184069_a.field_71466_p.func_78256_a(" ");

         for(var7 = this.field_184070_f.iterator(); var7.hasNext(); ++var5) {
            var8 = (GuiSubtitleOverlay.Subtitle)var7.next();
            boolean var9 = true;
            String var10 = var8.func_186824_a();
            Vec3d var11 = var8.func_186826_c().func_178788_d(var1).func_72432_b();
            double var12 = -var4.func_72430_b(var11);
            double var14 = -var2.func_72430_b(var11);
            boolean var16 = var14 > 0.5D;
            int var17 = var6 / 2;
            int var18 = this.field_184069_a.field_71466_p.field_78288_b;
            int var19 = var18 / 2;
            float var20 = 1.0F;
            int var21 = this.field_184069_a.field_71466_p.func_78256_a(var10);
            int var22 = MathHelper.func_76128_c(MathHelper.func_151238_b(255.0D, 75.0D, (double)((float)(Util.func_211177_b() - var8.func_186825_b()) / 3000.0F)));
            int var23 = var22 << 16 | var22 << 8 | var22;
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)this.field_184069_a.field_195558_d.func_198107_o() - (float)var17 * 1.0F - 2.0F, (float)(this.field_184069_a.field_195558_d.func_198087_p() - 30) - (float)(var5 * (var18 + 1)) * 1.0F, 0.0F);
            GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F);
            func_73734_a(-var17 - 1, -var19 - 1, var17 + 1, var19 + 1, -872415232);
            GlStateManager.func_179147_l();
            if (!var16) {
               if (var12 > 0.0D) {
                  this.field_184069_a.field_71466_p.func_211126_b(">", (float)(var17 - this.field_184069_a.field_71466_p.func_78256_a(">")), (float)(-var19), var23 + -16777216);
               } else if (var12 < 0.0D) {
                  this.field_184069_a.field_71466_p.func_211126_b("<", (float)(-var17), (float)(-var19), var23 + -16777216);
               }
            }

            this.field_184069_a.field_71466_p.func_211126_b(var10, (float)(-var21 / 2), (float)(-var19), var23 + -16777216);
            GlStateManager.func_179121_F();
         }

         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
      }
   }

   public void func_184067_a(ISound var1, SoundEventAccessor var2) {
      if (var2.func_188712_c() != null) {
         String var3 = var2.func_188712_c().func_150254_d();
         if (!this.field_184070_f.isEmpty()) {
            Iterator var4 = this.field_184070_f.iterator();

            while(var4.hasNext()) {
               GuiSubtitleOverlay.Subtitle var5 = (GuiSubtitleOverlay.Subtitle)var4.next();
               if (var5.func_186824_a().equals(var3)) {
                  var5.func_186823_a(new Vec3d((double)var1.func_147649_g(), (double)var1.func_147654_h(), (double)var1.func_147651_i()));
                  return;
               }
            }
         }

         this.field_184070_f.add(new GuiSubtitleOverlay.Subtitle(var3, new Vec3d((double)var1.func_147649_g(), (double)var1.func_147654_h(), (double)var1.func_147651_i())));
      }
   }

   public class Subtitle {
      private final String field_186828_b;
      private long field_186829_c;
      private Vec3d field_186830_d;

      public Subtitle(String var2, Vec3d var3) {
         super();
         this.field_186828_b = var2;
         this.field_186830_d = var3;
         this.field_186829_c = Util.func_211177_b();
      }

      public String func_186824_a() {
         return this.field_186828_b;
      }

      public long func_186825_b() {
         return this.field_186829_c;
      }

      public Vec3d func_186826_c() {
         return this.field_186830_d;
      }

      public void func_186823_a(Vec3d var1) {
         this.field_186830_d = var1;
         this.field_186829_c = Util.func_211177_b();
      }
   }
}
