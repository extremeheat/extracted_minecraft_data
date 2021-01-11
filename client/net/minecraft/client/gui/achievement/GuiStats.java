package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiStats extends GuiScreen implements IProgressMeter {
   protected GuiScreen field_146549_a;
   protected String field_146542_f = "Select world";
   private GuiStats.StatsGeneral field_146550_h;
   private GuiStats.StatsItem field_146551_i;
   private GuiStats.StatsBlock field_146548_r;
   private GuiStats.StatsMobsList field_146547_s;
   private StatFileWriter field_146546_t;
   private GuiSlot field_146545_u;
   private boolean field_146543_v = true;

   public GuiStats(GuiScreen var1, StatFileWriter var2) {
      super();
      this.field_146549_a = var1;
      this.field_146546_t = var2;
   }

   public void func_73866_w_() {
      this.field_146542_f = I18n.func_135052_a("gui.stats");
      this.field_146543_v = true;
      this.field_146297_k.func_147114_u().func_147297_a(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
   }

   public void func_146274_d() {
      super.func_146274_d();
      if (this.field_146545_u != null) {
         this.field_146545_u.func_178039_p();
      }

   }

   public void func_175366_f() {
      this.field_146550_h = new GuiStats.StatsGeneral(this.field_146297_k);
      this.field_146550_h.func_148134_d(1, 1);
      this.field_146551_i = new GuiStats.StatsItem(this.field_146297_k);
      this.field_146551_i.func_148134_d(1, 1);
      this.field_146548_r = new GuiStats.StatsBlock(this.field_146297_k);
      this.field_146548_r.func_148134_d(1, 1);
      this.field_146547_s = new GuiStats.StatsMobsList(this.field_146297_k);
      this.field_146547_s.func_148134_d(1, 1);
   }

   public void func_146541_h() {
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.done")));
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 160, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.generalButton")));
      GuiButton var1;
      this.field_146292_n.add(var1 = new GuiButton(2, this.field_146294_l / 2 - 80, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.blocksButton")));
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

   public void func_73863_a(int var1, int var2, float var3) {
      if (this.field_146543_v) {
         this.func_146276_q_();
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.downloadingStats"), this.field_146294_l / 2, this.field_146295_m / 2, 16777215);
         this.func_73732_a(this.field_146289_q, field_146510_b_[(int)(Minecraft.func_71386_F() / 150L % (long)field_146510_b_.length)], this.field_146294_l / 2, this.field_146295_m / 2 + this.field_146289_q.field_78288_b * 2, 16777215);
      } else {
         this.field_146545_u.func_148128_a(var1, var2, var3);
         this.func_73732_a(this.field_146289_q, this.field_146542_f, this.field_146294_l / 2, 20, 16777215);
         super.func_73863_a(var1, var2, var3);
      }

   }

   public void func_146509_g() {
      if (this.field_146543_v) {
         this.func_175366_f();
         this.func_146541_h();
         this.field_146545_u = this.field_146550_h;
         this.field_146543_v = false;
      }

   }

   public boolean func_73868_f() {
      return !this.field_146543_v;
   }

   private void func_146521_a(int var1, int var2, Item var3) {
      this.func_146531_b(var1 + 1, var2 + 1);
      GlStateManager.func_179091_B();
      RenderHelper.func_74520_c();
      this.field_146296_j.func_175042_a(new ItemStack(var3, 1, 0), var1 + 2, var2 + 2);
      RenderHelper.func_74518_a();
      GlStateManager.func_179101_C();
   }

   private void func_146531_b(int var1, int var2) {
      this.func_146527_c(var1, var2, 0, 0);
   }

   private void func_146527_c(int var1, int var2, int var3, int var4) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_110323_l);
      float var5 = 0.0078125F;
      float var6 = 0.0078125F;
      boolean var7 = true;
      boolean var8 = true;
      Tessellator var9 = Tessellator.func_178181_a();
      WorldRenderer var10 = var9.func_178180_c();
      var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var10.func_181662_b((double)(var1 + 0), (double)(var2 + 18), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
      var10.func_181662_b((double)(var1 + 18), (double)(var2 + 18), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
      var10.func_181662_b((double)(var1 + 18), (double)(var2 + 0), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
      var10.func_181662_b((double)(var1 + 0), (double)(var2 + 0), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
      var9.func_78381_a();
   }

   class StatsMobsList extends GuiSlot {
      private final List<EntityList.EntityEggInfo> field_148222_l = Lists.newArrayList();

      public StatsMobsList(Minecraft var2) {
         super(var2, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, GuiStats.this.field_146289_q.field_78288_b * 4);
         this.func_148130_a(false);
         Iterator var3 = EntityList.field_75627_a.values().iterator();

         while(true) {
            EntityList.EntityEggInfo var4;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (EntityList.EntityEggInfo)var3.next();
            } while(GuiStats.this.field_146546_t.func_77444_a(var4.field_151512_d) <= 0 && GuiStats.this.field_146546_t.func_77444_a(var4.field_151513_e) <= 0);

            this.field_148222_l.add(var4);
         }
      }

      protected int func_148127_b() {
         return this.field_148222_l.size();
      }

      protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      }

      protected boolean func_148131_a(int var1) {
         return false;
      }

      protected int func_148138_e() {
         return this.func_148127_b() * GuiStats.this.field_146289_q.field_78288_b * 4;
      }

      protected void func_148123_a() {
         GuiStats.this.func_146276_q_();
      }

      protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
         EntityList.EntityEggInfo var7 = (EntityList.EntityEggInfo)this.field_148222_l.get(var1);
         String var8 = I18n.func_135052_a("entity." + EntityList.func_75617_a(var7.field_75613_a) + ".name");
         int var9 = GuiStats.this.field_146546_t.func_77444_a(var7.field_151512_d);
         int var10 = GuiStats.this.field_146546_t.func_77444_a(var7.field_151513_e);
         String var11 = I18n.func_135052_a("stat.entityKills", var9, var8);
         String var12 = I18n.func_135052_a("stat.entityKilledBy", var8, var10);
         if (var9 == 0) {
            var11 = I18n.func_135052_a("stat.entityKills.none", var8);
         }

         if (var10 == 0) {
            var12 = I18n.func_135052_a("stat.entityKilledBy.none", var8);
         }

         GuiStats.this.func_73731_b(GuiStats.this.field_146289_q, var8, var2 + 2 - 10, var3 + 1, 16777215);
         GuiStats.this.func_73731_b(GuiStats.this.field_146289_q, var11, var2 + 2, var3 + 1 + GuiStats.this.field_146289_q.field_78288_b, var9 == 0 ? 6316128 : 9474192);
         GuiStats.this.func_73731_b(GuiStats.this.field_146289_q, var12, var2 + 2, var3 + 1 + GuiStats.this.field_146289_q.field_78288_b * 2, var10 == 0 ? 6316128 : 9474192);
      }
   }

   class StatsBlock extends GuiStats.Stats {
      public StatsBlock(Minecraft var2) {
         super(var2);
         this.field_148219_m = Lists.newArrayList();
         Iterator var3 = StatList.field_75939_e.iterator();

         while(var3.hasNext()) {
            StatCrafting var4 = (StatCrafting)var3.next();
            boolean var5 = false;
            int var6 = Item.func_150891_b(var4.func_150959_a());
            if (GuiStats.this.field_146546_t.func_77444_a(var4) > 0) {
               var5 = true;
            } else if (StatList.field_75929_E[var6] != null && GuiStats.this.field_146546_t.func_77444_a(StatList.field_75929_E[var6]) > 0) {
               var5 = true;
            } else if (StatList.field_75928_D[var6] != null && GuiStats.this.field_146546_t.func_77444_a(StatList.field_75928_D[var6]) > 0) {
               var5 = true;
            }

            if (var5) {
               this.field_148219_m.add(var4);
            }
         }

         this.field_148216_n = new Comparator<StatCrafting>() {
            public int compare(StatCrafting var1, StatCrafting var2) {
               int var3 = Item.func_150891_b(var1.func_150959_a());
               int var4 = Item.func_150891_b(var2.func_150959_a());
               StatBase var5 = null;
               StatBase var6 = null;
               if (StatsBlock.this.field_148217_o == 2) {
                  var5 = StatList.field_75934_C[var3];
                  var6 = StatList.field_75934_C[var4];
               } else if (StatsBlock.this.field_148217_o == 0) {
                  var5 = StatList.field_75928_D[var3];
                  var6 = StatList.field_75928_D[var4];
               } else if (StatsBlock.this.field_148217_o == 1) {
                  var5 = StatList.field_75929_E[var3];
                  var6 = StatList.field_75929_E[var4];
               }

               if (var5 != null || var6 != null) {
                  if (var5 == null) {
                     return 1;
                  }

                  if (var6 == null) {
                     return -1;
                  }

                  int var7 = GuiStats.this.field_146546_t.func_77444_a(var5);
                  int var8 = GuiStats.this.field_146546_t.func_77444_a(var6);
                  if (var7 != var8) {
                     return (var7 - var8) * StatsBlock.this.field_148215_p;
                  }
               }

               return var3 - var4;
            }

            // $FF: synthetic method
            public int compare(Object var1, Object var2) {
               return this.compare((StatCrafting)var1, (StatCrafting)var2);
            }
         };
      }

      protected void func_148129_a(int var1, int var2, Tessellator var3) {
         super.func_148129_a(var1, var2, var3);
         if (this.field_148218_l == 0) {
            GuiStats.this.func_146527_c(var1 + 115 - 18 + 1, var2 + 1 + 1, 18, 18);
         } else {
            GuiStats.this.func_146527_c(var1 + 115 - 18, var2 + 1, 18, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.func_146527_c(var1 + 165 - 18 + 1, var2 + 1 + 1, 36, 18);
         } else {
            GuiStats.this.func_146527_c(var1 + 165 - 18, var2 + 1, 36, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.func_146527_c(var1 + 215 - 18 + 1, var2 + 1 + 1, 54, 18);
         } else {
            GuiStats.this.func_146527_c(var1 + 215 - 18, var2 + 1, 54, 18);
         }

      }

      protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
         StatCrafting var7 = this.func_148211_c(var1);
         Item var8 = var7.func_150959_a();
         GuiStats.this.func_146521_a(var2 + 40, var3, var8);
         int var9 = Item.func_150891_b(var8);
         this.func_148209_a(StatList.field_75928_D[var9], var2 + 115, var3, var1 % 2 == 0);
         this.func_148209_a(StatList.field_75929_E[var9], var2 + 165, var3, var1 % 2 == 0);
         this.func_148209_a(var7, var2 + 215, var3, var1 % 2 == 0);
      }

      protected String func_148210_b(int var1) {
         if (var1 == 0) {
            return "stat.crafted";
         } else {
            return var1 == 1 ? "stat.used" : "stat.mined";
         }
      }
   }

   class StatsItem extends GuiStats.Stats {
      public StatsItem(Minecraft var2) {
         super(var2);
         this.field_148219_m = Lists.newArrayList();
         Iterator var3 = StatList.field_75938_d.iterator();

         while(var3.hasNext()) {
            StatCrafting var4 = (StatCrafting)var3.next();
            boolean var5 = false;
            int var6 = Item.func_150891_b(var4.func_150959_a());
            if (GuiStats.this.field_146546_t.func_77444_a(var4) > 0) {
               var5 = true;
            } else if (StatList.field_75930_F[var6] != null && GuiStats.this.field_146546_t.func_77444_a(StatList.field_75930_F[var6]) > 0) {
               var5 = true;
            } else if (StatList.field_75928_D[var6] != null && GuiStats.this.field_146546_t.func_77444_a(StatList.field_75928_D[var6]) > 0) {
               var5 = true;
            }

            if (var5) {
               this.field_148219_m.add(var4);
            }
         }

         this.field_148216_n = new Comparator<StatCrafting>() {
            public int compare(StatCrafting var1, StatCrafting var2) {
               int var3 = Item.func_150891_b(var1.func_150959_a());
               int var4 = Item.func_150891_b(var2.func_150959_a());
               StatBase var5 = null;
               StatBase var6 = null;
               if (StatsItem.this.field_148217_o == 0) {
                  var5 = StatList.field_75930_F[var3];
                  var6 = StatList.field_75930_F[var4];
               } else if (StatsItem.this.field_148217_o == 1) {
                  var5 = StatList.field_75928_D[var3];
                  var6 = StatList.field_75928_D[var4];
               } else if (StatsItem.this.field_148217_o == 2) {
                  var5 = StatList.field_75929_E[var3];
                  var6 = StatList.field_75929_E[var4];
               }

               if (var5 != null || var6 != null) {
                  if (var5 == null) {
                     return 1;
                  }

                  if (var6 == null) {
                     return -1;
                  }

                  int var7 = GuiStats.this.field_146546_t.func_77444_a(var5);
                  int var8 = GuiStats.this.field_146546_t.func_77444_a(var6);
                  if (var7 != var8) {
                     return (var7 - var8) * StatsItem.this.field_148215_p;
                  }
               }

               return var3 - var4;
            }

            // $FF: synthetic method
            public int compare(Object var1, Object var2) {
               return this.compare((StatCrafting)var1, (StatCrafting)var2);
            }
         };
      }

      protected void func_148129_a(int var1, int var2, Tessellator var3) {
         super.func_148129_a(var1, var2, var3);
         if (this.field_148218_l == 0) {
            GuiStats.this.func_146527_c(var1 + 115 - 18 + 1, var2 + 1 + 1, 72, 18);
         } else {
            GuiStats.this.func_146527_c(var1 + 115 - 18, var2 + 1, 72, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.func_146527_c(var1 + 165 - 18 + 1, var2 + 1 + 1, 18, 18);
         } else {
            GuiStats.this.func_146527_c(var1 + 165 - 18, var2 + 1, 18, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.func_146527_c(var1 + 215 - 18 + 1, var2 + 1 + 1, 36, 18);
         } else {
            GuiStats.this.func_146527_c(var1 + 215 - 18, var2 + 1, 36, 18);
         }

      }

      protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
         StatCrafting var7 = this.func_148211_c(var1);
         Item var8 = var7.func_150959_a();
         GuiStats.this.func_146521_a(var2 + 40, var3, var8);
         int var9 = Item.func_150891_b(var8);
         this.func_148209_a(StatList.field_75930_F[var9], var2 + 115, var3, var1 % 2 == 0);
         this.func_148209_a(StatList.field_75928_D[var9], var2 + 165, var3, var1 % 2 == 0);
         this.func_148209_a(var7, var2 + 215, var3, var1 % 2 == 0);
      }

      protected String func_148210_b(int var1) {
         if (var1 == 1) {
            return "stat.crafted";
         } else {
            return var1 == 2 ? "stat.used" : "stat.depleted";
         }
      }
   }

   abstract class Stats extends GuiSlot {
      protected int field_148218_l = -1;
      protected List<StatCrafting> field_148219_m;
      protected Comparator<StatCrafting> field_148216_n;
      protected int field_148217_o = -1;
      protected int field_148215_p;

      protected Stats(Minecraft var2) {
         super(var2, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, 20);
         this.func_148130_a(false);
         this.func_148133_a(true, 20);
      }

      protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      }

      protected boolean func_148131_a(int var1) {
         return false;
      }

      protected void func_148123_a() {
         GuiStats.this.func_146276_q_();
      }

      protected void func_148129_a(int var1, int var2, Tessellator var3) {
         if (!Mouse.isButtonDown(0)) {
            this.field_148218_l = -1;
         }

         if (this.field_148218_l == 0) {
            GuiStats.this.func_146527_c(var1 + 115 - 18, var2 + 1, 0, 0);
         } else {
            GuiStats.this.func_146527_c(var1 + 115 - 18, var2 + 1, 0, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.func_146527_c(var1 + 165 - 18, var2 + 1, 0, 0);
         } else {
            GuiStats.this.func_146527_c(var1 + 165 - 18, var2 + 1, 0, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.func_146527_c(var1 + 215 - 18, var2 + 1, 0, 0);
         } else {
            GuiStats.this.func_146527_c(var1 + 215 - 18, var2 + 1, 0, 18);
         }

         if (this.field_148217_o != -1) {
            short var4 = 79;
            byte var5 = 18;
            if (this.field_148217_o == 1) {
               var4 = 129;
            } else if (this.field_148217_o == 2) {
               var4 = 179;
            }

            if (this.field_148215_p == 1) {
               var5 = 36;
            }

            GuiStats.this.func_146527_c(var1 + var4, var2 + 1, var5, 0);
         }

      }

      protected void func_148132_a(int var1, int var2) {
         this.field_148218_l = -1;
         if (var1 >= 79 && var1 < 115) {
            this.field_148218_l = 0;
         } else if (var1 >= 129 && var1 < 165) {
            this.field_148218_l = 1;
         } else if (var1 >= 179 && var1 < 215) {
            this.field_148218_l = 2;
         }

         if (this.field_148218_l >= 0) {
            this.func_148212_h(this.field_148218_l);
            this.field_148161_k.func_147118_V().func_147682_a(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
         }

      }

      protected final int func_148127_b() {
         return this.field_148219_m.size();
      }

      protected final StatCrafting func_148211_c(int var1) {
         return (StatCrafting)this.field_148219_m.get(var1);
      }

      protected abstract String func_148210_b(int var1);

      protected void func_148209_a(StatBase var1, int var2, int var3, boolean var4) {
         String var5;
         if (var1 != null) {
            var5 = var1.func_75968_a(GuiStats.this.field_146546_t.func_77444_a(var1));
            GuiStats.this.func_73731_b(GuiStats.this.field_146289_q, var5, var2 - GuiStats.this.field_146289_q.func_78256_a(var5), var3 + 5, var4 ? 16777215 : 9474192);
         } else {
            var5 = "-";
            GuiStats.this.func_73731_b(GuiStats.this.field_146289_q, var5, var2 - GuiStats.this.field_146289_q.func_78256_a(var5), var3 + 5, var4 ? 16777215 : 9474192);
         }

      }

      protected void func_148142_b(int var1, int var2) {
         if (var2 >= this.field_148153_b && var2 <= this.field_148154_c) {
            int var3 = this.func_148124_c(var1, var2);
            int var4 = this.field_148155_a / 2 - 92 - 16;
            if (var3 >= 0) {
               if (var1 < var4 + 40 || var1 > var4 + 40 + 20) {
                  return;
               }

               StatCrafting var9 = this.func_148211_c(var3);
               this.func_148213_a(var9, var1, var2);
            } else {
               String var5 = "";
               if (var1 >= var4 + 115 - 18 && var1 <= var4 + 115) {
                  var5 = this.func_148210_b(0);
               } else if (var1 >= var4 + 165 - 18 && var1 <= var4 + 165) {
                  var5 = this.func_148210_b(1);
               } else {
                  if (var1 < var4 + 215 - 18 || var1 > var4 + 215) {
                     return;
                  }

                  var5 = this.func_148210_b(2);
               }

               var5 = ("" + I18n.func_135052_a(var5)).trim();
               if (var5.length() > 0) {
                  int var6 = var1 + 12;
                  int var7 = var2 - 12;
                  int var8 = GuiStats.this.field_146289_q.func_78256_a(var5);
                  GuiStats.this.func_73733_a(var6 - 3, var7 - 3, var6 + var8 + 3, var7 + 8 + 3, -1073741824, -1073741824);
                  GuiStats.this.field_146289_q.func_175063_a(var5, (float)var6, (float)var7, -1);
               }
            }

         }
      }

      protected void func_148213_a(StatCrafting var1, int var2, int var3) {
         if (var1 != null) {
            Item var4 = var1.func_150959_a();
            ItemStack var5 = new ItemStack(var4);
            String var6 = var5.func_77977_a();
            String var7 = ("" + I18n.func_135052_a(var6 + ".name")).trim();
            if (var7.length() > 0) {
               int var8 = var2 + 12;
               int var9 = var3 - 12;
               int var10 = GuiStats.this.field_146289_q.func_78256_a(var7);
               GuiStats.this.func_73733_a(var8 - 3, var9 - 3, var8 + var10 + 3, var9 + 8 + 3, -1073741824, -1073741824);
               GuiStats.this.field_146289_q.func_175063_a(var7, (float)var8, (float)var9, -1);
            }

         }
      }

      protected void func_148212_h(int var1) {
         if (var1 != this.field_148217_o) {
            this.field_148217_o = var1;
            this.field_148215_p = -1;
         } else if (this.field_148215_p == -1) {
            this.field_148215_p = 1;
         } else {
            this.field_148217_o = -1;
            this.field_148215_p = 0;
         }

         Collections.sort(this.field_148219_m, this.field_148216_n);
      }
   }

   class StatsGeneral extends GuiSlot {
      public StatsGeneral(Minecraft var2) {
         super(var2, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, 10);
         this.func_148130_a(false);
      }

      protected int func_148127_b() {
         return StatList.field_75941_c.size();
      }

      protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      }

      protected boolean func_148131_a(int var1) {
         return false;
      }

      protected int func_148138_e() {
         return this.func_148127_b() * 10;
      }

      protected void func_148123_a() {
         GuiStats.this.func_146276_q_();
      }

      protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
         StatBase var7 = (StatBase)StatList.field_75941_c.get(var1);
         GuiStats.this.func_73731_b(GuiStats.this.field_146289_q, var7.func_150951_e().func_150260_c(), var2 + 2, var3 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
         String var8 = var7.func_75968_a(GuiStats.this.field_146546_t.func_77444_a(var7));
         GuiStats.this.func_73731_b(GuiStats.this.field_146289_q, var8, var2 + 2 + 213 - GuiStats.this.field_146289_q.func_78256_a(var8), var3 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
      }
   }
}
