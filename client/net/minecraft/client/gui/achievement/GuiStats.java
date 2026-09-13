package net.minecraft.client.gui.achievement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus$EnumState;
import net.minecraft.stats.StatFileWriter;
import org.lwjgl.opengl.GL11;

public class GuiStats extends GuiScreen implements IProgressMeter {
   private static RenderItem field_146544_g = new RenderItem();
   protected GuiScreen field_146549_a;
   protected String field_146542_f = "Select world";
   private GuiStats$StatsGeneral field_146550_h;
   private GuiStats$StatsItem field_146551_i;
   private GuiStats$StatsBlock field_146548_r;
   private GuiStats$StatsMobsList field_146547_s;
   private StatFileWriter field_146546_t;
   private GuiSlot field_146545_u;
   private boolean field_146543_v = true;

   public GuiStats(GuiScreen var1, StatFileWriter var2) {
      super();
      this.field_146549_a = var1;
      this.field_146546_t = var2;
   }

   @Override
   public void func_73866_w_() {
      this.field_146542_f = I18n.func_135052_a("gui.stats");
      this.field_146297_k.func_147114_u().func_147297_a(new C16PacketClientStatus(C16PacketClientStatus$EnumState.REQUEST_STATS));
   }

   public void func_146541_h() {
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.done")));
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 160, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.generalButton")));
      GuiButton var1;
      this.field_146292_n
         .add(var1 = new GuiButton(2, this.field_146294_l / 2 - 80, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.blocksButton")));
      GuiButton var2;
      this.field_146292_n.add(var2 = new GuiButton(3, this.field_146294_l / 2, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.itemsButton")));
      GuiButton var3;
      this.field_146292_n.add(var3 = new GuiButton(4, this.field_146294_l / 2 + 80, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.mobsButton")));
      if (this.field_146548_r.func_148127_b() == 0) {
         var1.field_146124_l = false;
      }

      if (this.field_146551_i.func_148127_b() == 0) {
         var2.field_146124_l = false;
      }

      if (this.field_146547_s.func_148127_b() == 0) {
         var3.field_146124_l = false;
      }
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 0) {
            this.field_146297_k.func_147108_a(this.field_146549_a);
         } else if (var1.field_146127_k == 1) {
            this.field_146545_u = this.field_146550_h;
         } else if (var1.field_146127_k == 3) {
            this.field_146545_u = this.field_146551_i;
         } else if (var1.field_146127_k == 2) {
            this.field_146545_u = this.field_146548_r;
         } else if (var1.field_146127_k == 4) {
            this.field_146545_u = this.field_146547_s;
         } else {
            this.field_146545_u.func_148147_a(var1);
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      if (this.field_146543_v) {
         this.func_146276_q_();
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.downloadingStats"), this.field_146294_l / 2, this.field_146295_m / 2, 16777215);
         this.func_73732_a(
            this.field_146289_q,
            field_146510_b_[(int)(Minecraft.func_71386_F() / 150L % (long)field_146510_b_.length)],
            this.field_146294_l / 2,
            this.field_146295_m / 2 + this.field_146289_q.field_78288_b * 2,
            16777215
         );
      } else {
         this.field_146545_u.func_148128_a(var1, var2, var3);
         this.func_73732_a(this.field_146289_q, this.field_146542_f, this.field_146294_l / 2, 20, 16777215);
         super.func_73863_a(var1, var2, var3);
      }
   }

   @Override
   public void func_146509_g() {
      if (this.field_146543_v) {
         this.field_146550_h = new GuiStats$StatsGeneral(this);
         this.field_146550_h.func_148134_d(1, 1);
         this.field_146551_i = new GuiStats$StatsItem(this);
         this.field_146551_i.func_148134_d(1, 1);
         this.field_146548_r = new GuiStats$StatsBlock(this);
         this.field_146548_r.func_148134_d(1, 1);
         this.field_146547_s = new GuiStats$StatsMobsList(this);
         this.field_146547_s.func_148134_d(1, 1);
         this.field_146545_u = this.field_146550_h;
         this.func_146541_h();
         this.field_146543_v = false;
      }
   }

   @Override
   public boolean func_73868_f() {
      return !this.field_146543_v;
   }

   private void func_146521_a(int var1, int var2, Item var3) {
      this.func_146531_b(var1 + 1, var2 + 1);
      GL11.glEnable(32826);
      RenderHelper.func_74520_c();
      field_146544_g.func_77015_a(this.field_146289_q, this.field_146297_k.func_110434_K(), new ItemStack(var3, 1, 0), var1 + 2, var2 + 2);
      RenderHelper.func_74518_a();
      GL11.glDisable(32826);
   }

   private void func_146531_b(int var1, int var2) {
      this.func_146527_c(var1, var2, 0, 0);
   }

   private void func_146527_c(int var1, int var2, int var3, int var4) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_110323_l);
      float var5 = 0.0078125F;
      float var6 = 0.0078125F;
      boolean var7 = true;
      boolean var8 = true;
      Tessellator var9 = Tessellator.field_78398_a;
      var9.func_78382_b();
      var9.func_78374_a(
         (double)(var1 + 0),
         (double)(var2 + 18),
         (double)this.field_73735_i,
         (double)((float)(var3 + 0) * 0.0078125F),
         (double)((float)(var4 + 18) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 18),
         (double)(var2 + 18),
         (double)this.field_73735_i,
         (double)((float)(var3 + 18) * 0.0078125F),
         (double)((float)(var4 + 18) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 18),
         (double)(var2 + 0),
         (double)this.field_73735_i,
         (double)((float)(var3 + 18) * 0.0078125F),
         (double)((float)(var4 + 0) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 0),
         (double)(var2 + 0),
         (double)this.field_73735_i,
         (double)((float)(var3 + 0) * 0.0078125F),
         (double)((float)(var4 + 0) * 0.0078125F)
      );
      var9.func_78381_a();
   }
}
