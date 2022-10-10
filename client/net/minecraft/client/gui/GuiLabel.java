package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLabel extends Gui implements IGuiEventListener {
   protected int field_146167_a;
   protected int field_146161_f;
   public int field_146162_g;
   public int field_146174_h;
   private final List<String> field_146173_k;
   private boolean field_146170_l;
   public boolean field_146172_j;
   private boolean field_146171_m;
   private final int field_146168_n;
   private int field_146169_o;
   private int field_146166_p;
   private int field_146165_q;
   private final FontRenderer field_146164_r;
   private int field_146163_s;

   public void func_194997_a(int var1, int var2, float var3) {
      if (this.field_146172_j) {
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.func_194996_b(var1, var2, var3);
         int var4 = this.field_146174_h + this.field_146161_f / 2 + this.field_146163_s / 2;
         int var5 = var4 - this.field_146173_k.size() * 10 / 2;

         for(int var6 = 0; var6 < this.field_146173_k.size(); ++var6) {
            if (this.field_146170_l) {
               this.func_73732_a(this.field_146164_r, (String)this.field_146173_k.get(var6), this.field_146162_g + this.field_146167_a / 2, var5 + var6 * 10, this.field_146168_n);
            } else {
               this.func_73731_b(this.field_146164_r, (String)this.field_146173_k.get(var6), this.field_146162_g, var5 + var6 * 10, this.field_146168_n);
            }
         }

      }
   }

   protected void func_194996_b(int var1, int var2, float var3) {
      if (this.field_146171_m) {
         int var4 = this.field_146167_a + this.field_146163_s * 2;
         int var5 = this.field_146161_f + this.field_146163_s * 2;
         int var6 = this.field_146162_g - this.field_146163_s;
         int var7 = this.field_146174_h - this.field_146163_s;
         func_73734_a(var6, var7, var6 + var4, var7 + var5, this.field_146169_o);
         this.func_73730_a(var6, var6 + var4, var7, this.field_146166_p);
         this.func_73730_a(var6, var6 + var4, var7 + var5, this.field_146165_q);
         this.func_73728_b(var6, var7, var7 + var5, this.field_146166_p);
         this.func_73728_b(var6 + var4, var7, var7 + var5, this.field_146165_q);
      }

   }
}
