package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.util.ResourceLocation;

public class GuiScreenAdvancements extends GuiScreen implements ClientAdvancementManager.IListener {
   private static final ResourceLocation field_191943_f = new ResourceLocation("textures/gui/advancements/window.png");
   private static final ResourceLocation field_191945_g = new ResourceLocation("textures/gui/advancements/tabs.png");
   private final ClientAdvancementManager field_191946_h;
   private final Map<Advancement, GuiAdvancementTab> field_191947_i = Maps.newLinkedHashMap();
   private GuiAdvancementTab field_191940_s;
   private boolean field_191944_v;

   public GuiScreenAdvancements(ClientAdvancementManager var1) {
      super();
      this.field_191946_h = var1;
   }

   protected void func_73866_w_() {
      this.field_191947_i.clear();
      this.field_191940_s = null;
      this.field_191946_h.func_192798_a(this);
      if (this.field_191940_s == null && !this.field_191947_i.isEmpty()) {
         this.field_191946_h.func_194230_a(((GuiAdvancementTab)this.field_191947_i.values().iterator().next()).func_193935_c(), true);
      } else {
         this.field_191946_h.func_194230_a(this.field_191940_s == null ? null : this.field_191940_s.func_193935_c(), true);
      }

   }

   public void func_146281_b() {
      this.field_191946_h.func_192798_a((ClientAdvancementManager.IListener)null);
      NetHandlerPlayClient var1 = this.field_146297_k.func_147114_u();
      if (var1 != null) {
         var1.func_147297_a(CPacketSeenAdvancements.func_194164_a());
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         int var6 = (this.field_146294_l - 252) / 2;
         int var7 = (this.field_146295_m - 140) / 2;
         Iterator var8 = this.field_191947_i.values().iterator();

         while(var8.hasNext()) {
            GuiAdvancementTab var9 = (GuiAdvancementTab)var8.next();
            if (var9.func_195627_a(var6, var7, var1, var3)) {
               this.field_191946_h.func_194230_a(var9.func_193935_c(), true);
               break;
            }
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.field_146297_k.field_71474_y.field_194146_ao.func_197976_a(var1, var2)) {
         this.field_146297_k.func_147108_a((GuiScreen)null);
         this.field_146297_k.field_71417_B.func_198034_i();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      int var4 = (this.field_146294_l - 252) / 2;
      int var5 = (this.field_146295_m - 140) / 2;
      this.func_146276_q_();
      this.func_191936_c(var1, var2, var4, var5);
      this.func_191934_b(var4, var5);
      this.func_191937_d(var1, var2, var4, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (var5 != 0) {
         this.field_191944_v = false;
         return false;
      } else {
         if (!this.field_191944_v) {
            this.field_191944_v = true;
         } else if (this.field_191940_s != null) {
            this.field_191940_s.func_195626_a(var6, var8);
         }

         return true;
      }
   }

   private void func_191936_c(int var1, int var2, int var3, int var4) {
      GuiAdvancementTab var5 = this.field_191940_s;
      if (var5 == null) {
         func_73734_a(var3 + 9, var4 + 18, var3 + 9 + 234, var4 + 18 + 113, -16777216);
         String var6 = I18n.func_135052_a("advancements.empty");
         int var7 = this.field_146289_q.func_78256_a(var6);
         this.field_146289_q.func_211126_b(var6, (float)(var3 + 9 + 117 - var7 / 2), (float)(var4 + 18 + 56 - this.field_146289_q.field_78288_b / 2), -1);
         this.field_146289_q.func_211126_b(":(", (float)(var3 + 9 + 117 - this.field_146289_q.func_78256_a(":(") / 2), (float)(var4 + 18 + 113 - this.field_146289_q.field_78288_b), -1);
      } else {
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)(var3 + 9), (float)(var4 + 18), -400.0F);
         GlStateManager.func_179126_j();
         var5.func_191799_a();
         GlStateManager.func_179121_F();
         GlStateManager.func_179143_c(515);
         GlStateManager.func_179097_i();
      }
   }

   public void func_191934_b(int var1, int var2) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179147_l();
      RenderHelper.func_74518_a();
      this.field_146297_k.func_110434_K().func_110577_a(field_191943_f);
      this.func_73729_b(var1, var2, 0, 0, 252, 140);
      if (this.field_191947_i.size() > 1) {
         this.field_146297_k.func_110434_K().func_110577_a(field_191945_g);
         Iterator var3 = this.field_191947_i.values().iterator();

         GuiAdvancementTab var4;
         while(var3.hasNext()) {
            var4 = (GuiAdvancementTab)var3.next();
            var4.func_191798_a(var1, var2, var4 == this.field_191940_s);
         }

         GlStateManager.func_179091_B();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderHelper.func_74520_c();
         var3 = this.field_191947_i.values().iterator();

         while(var3.hasNext()) {
            var4 = (GuiAdvancementTab)var3.next();
            var4.func_191796_a(var1, var2, this.field_146296_j);
         }

         GlStateManager.func_179084_k();
      }

      this.field_146289_q.func_211126_b(I18n.func_135052_a("gui.advancements"), (float)(var1 + 8), (float)(var2 + 6), 4210752);
   }

   private void func_191937_d(int var1, int var2, int var3, int var4) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.field_191940_s != null) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179126_j();
         GlStateManager.func_179109_b((float)(var3 + 9), (float)(var4 + 18), 400.0F);
         this.field_191940_s.func_192991_a(var1 - var3 - 9, var2 - var4 - 18, var3, var4);
         GlStateManager.func_179097_i();
         GlStateManager.func_179121_F();
      }

      if (this.field_191947_i.size() > 1) {
         Iterator var5 = this.field_191947_i.values().iterator();

         while(var5.hasNext()) {
            GuiAdvancementTab var6 = (GuiAdvancementTab)var5.next();
            if (var6.func_195627_a(var3, var4, (double)var1, (double)var2)) {
               this.func_146279_a(var6.func_191795_d(), var1, var2);
            }
         }
      }

   }

   public void func_191931_a(Advancement var1) {
      GuiAdvancementTab var2 = GuiAdvancementTab.func_193936_a(this.field_146297_k, this, this.field_191947_i.size(), var1);
      if (var2 != null) {
         this.field_191947_i.put(var1, var2);
      }
   }

   public void func_191928_b(Advancement var1) {
   }

   public void func_191932_c(Advancement var1) {
      GuiAdvancementTab var2 = this.func_191935_f(var1);
      if (var2 != null) {
         var2.func_191800_a(var1);
      }

   }

   public void func_191929_d(Advancement var1) {
   }

   public void func_191933_a(Advancement var1, AdvancementProgress var2) {
      GuiAdvancement var3 = this.func_191938_e(var1);
      if (var3 != null) {
         var3.func_191824_a(var2);
      }

   }

   public void func_193982_e(@Nullable Advancement var1) {
      this.field_191940_s = (GuiAdvancementTab)this.field_191947_i.get(var1);
   }

   public void func_191930_a() {
      this.field_191947_i.clear();
      this.field_191940_s = null;
   }

   @Nullable
   public GuiAdvancement func_191938_e(Advancement var1) {
      GuiAdvancementTab var2 = this.func_191935_f(var1);
      return var2 == null ? null : var2.func_191794_b(var1);
   }

   @Nullable
   private GuiAdvancementTab func_191935_f(Advancement var1) {
      while(var1.func_192070_b() != null) {
         var1 = var1.func_192070_b();
      }

      return (GuiAdvancementTab)this.field_191947_i.get(var1);
   }
}
