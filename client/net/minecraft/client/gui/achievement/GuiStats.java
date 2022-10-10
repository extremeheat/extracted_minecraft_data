package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GuiStats extends GuiScreen implements IProgressMeter {
   protected GuiScreen field_146549_a;
   protected String field_146542_f = "Select world";
   private GuiStats.StatsGeneral field_146550_h;
   private GuiStats.StatsItem field_146551_i;
   private GuiStats.StatsMobsList field_146547_s;
   private final StatisticsManager field_146546_t;
   private GuiSlot field_146545_u;
   private boolean field_146543_v = true;

   public GuiStats(GuiScreen var1, StatisticsManager var2) {
      super();
      this.field_146549_a = var1;
      this.field_146546_t = var2;
   }

   public IGuiEventListener getFocused() {
      return this.field_146545_u;
   }

   protected void func_73866_w_() {
      this.field_146542_f = I18n.func_135052_a("gui.stats");
      this.field_146543_v = true;
      this.field_146297_k.func_147114_u().func_147297_a(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
   }

   public void func_193028_a() {
      this.field_146550_h = new GuiStats.StatsGeneral(this.field_146297_k);
      this.field_146551_i = new GuiStats.StatsItem(this.field_146297_k);
      this.field_146547_s = new GuiStats.StatsMobsList(this.field_146297_k);
   }

   public void func_193029_f() {
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m - 28, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiStats.this.field_146297_k.func_147108_a(GuiStats.this.field_146549_a);
         }
      });
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 120, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.generalButton")) {
         public void func_194829_a(double var1, double var3) {
            GuiStats.this.field_146545_u = GuiStats.this.field_146550_h;
         }
      });
      GuiButton var1 = this.func_189646_b(new GuiButton(3, this.field_146294_l / 2 - 40, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.itemsButton")) {
         public void func_194829_a(double var1, double var3) {
            GuiStats.this.field_146545_u = GuiStats.this.field_146551_i;
         }
      });
      GuiButton var2 = this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 + 40, this.field_146295_m - 52, 80, 20, I18n.func_135052_a("stat.mobsButton")) {
         public void func_194829_a(double var1, double var3) {
            GuiStats.this.field_146545_u = GuiStats.this.field_146547_s;
         }
      });
      if (this.field_146551_i.func_148127_b() == 0) {
         var1.field_146124_l = false;
      }

      if (this.field_146547_s.func_148127_b() == 0) {
         var2.field_146124_l = false;
      }

      this.field_195124_j.add(() -> {
         return this.field_146545_u;
      });
   }

   public void func_73863_a(int var1, int var2, float var3) {
      if (this.field_146543_v) {
         this.func_146276_q_();
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.downloadingStats"), this.field_146294_l / 2, this.field_146295_m / 2, 16777215);
         this.func_73732_a(this.field_146289_q, field_146510_b_[(int)(Util.func_211177_b() / 150L % (long)field_146510_b_.length)], this.field_146294_l / 2, this.field_146295_m / 2 + this.field_146289_q.field_78288_b * 2, 16777215);
      } else {
         this.field_146545_u.func_148128_a(var1, var2, var3);
         this.func_73732_a(this.field_146289_q, this.field_146542_f, this.field_146294_l / 2, 20, 16777215);
         super.func_73863_a(var1, var2, var3);
      }

   }

   public void func_193026_g() {
      if (this.field_146543_v) {
         this.func_193028_a();
         this.func_193029_f();
         this.field_146545_u = this.field_146550_h;
         this.field_146543_v = false;
      }

   }

   public boolean func_73868_f() {
      return !this.field_146543_v;
   }

   private int func_195224_b(int var1) {
      return 115 + 40 * var1;
   }

   private void func_146521_a(int var1, int var2, Item var3) {
      this.func_146531_b(var1 + 1, var2 + 1);
      GlStateManager.func_179091_B();
      RenderHelper.func_74520_c();
      this.field_146296_j.func_175042_a(var3.func_190903_i(), var1 + 2, var2 + 2);
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
      BufferBuilder var10 = var9.func_178180_c();
      var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var10.func_181662_b((double)(var1 + 0), (double)(var2 + 18), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
      var10.func_181662_b((double)(var1 + 18), (double)(var2 + 18), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
      var10.func_181662_b((double)(var1 + 18), (double)(var2 + 0), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
      var10.func_181662_b((double)(var1 + 0), (double)(var2 + 0), (double)this.field_73735_i).func_187315_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
      var9.func_78381_a();
   }

   class StatsMobsList extends GuiSlot {
      private final List<EntityType<?>> field_148222_l = Lists.newArrayList();

      public StatsMobsList(Minecraft var2) {
         super(var2, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, GuiStats.this.field_146289_q.field_78288_b * 4);
         this.func_193651_b(false);
         Iterator var3 = IRegistry.field_212629_r.iterator();

         while(true) {
            EntityType var4;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (EntityType)var3.next();
            } while(GuiStats.this.field_146546_t.func_77444_a(StatList.field_199090_h.func_199076_b(var4)) <= 0 && GuiStats.this.field_146546_t.func_77444_a(StatList.field_199091_i.func_199076_b(var4)) <= 0);

            this.field_148222_l.add(var4);
         }
      }

      protected int func_148127_b() {
         return this.field_148222_l.size();
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

      protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
         EntityType var8 = (EntityType)this.field_148222_l.get(var1);
         String var9 = I18n.func_135052_a(Util.func_200697_a("entity", EntityType.func_200718_a(var8)));
         int var10 = GuiStats.this.field_146546_t.func_77444_a(StatList.field_199090_h.func_199076_b(var8));
         int var11 = GuiStats.this.field_146546_t.func_77444_a(StatList.field_199091_i.func_199076_b(var8));
         this.func_73731_b(GuiStats.this.field_146289_q, var9, var2 + 2 - 10, var3 + 1, 16777215);
         this.func_73731_b(GuiStats.this.field_146289_q, this.func_199707_a(var9, var10), var2 + 2, var3 + 1 + GuiStats.this.field_146289_q.field_78288_b, var10 == 0 ? 6316128 : 9474192);
         this.func_73731_b(GuiStats.this.field_146289_q, this.func_199706_b(var9, var11), var2 + 2, var3 + 1 + GuiStats.this.field_146289_q.field_78288_b * 2, var11 == 0 ? 6316128 : 9474192);
      }

      private String func_199707_a(String var1, int var2) {
         String var3 = StatList.field_199090_h.func_199078_c();
         return var2 == 0 ? I18n.func_135052_a(var3 + ".none", var1) : I18n.func_135052_a(var3, var2, var1);
      }

      private String func_199706_b(String var1, int var2) {
         String var3 = StatList.field_199091_i.func_199078_c();
         return var2 == 0 ? I18n.func_135052_a(var3 + ".none", var1) : I18n.func_135052_a(var3, var1, var2);
      }
   }

   class StatsItem extends GuiSlot {
      protected final List<StatType<Block>> field_195113_v = Lists.newArrayList();
      protected final List<StatType<Item>> field_195114_w;
      private final int[] field_195112_D = new int[]{3, 4, 1, 2, 5, 6};
      protected int field_195115_x = -1;
      protected final List<Item> field_195116_y;
      protected final java.util.Comparator<Item> field_195117_z = new GuiStats.StatsItem.Comparator();
      @Nullable
      protected StatType<?> field_195110_A;
      protected int field_195111_B;

      public StatsItem(Minecraft var2) {
         super(var2, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, 20);
         this.field_195113_v.add(StatList.field_188065_ae);
         this.field_195114_w = Lists.newArrayList(new StatType[]{StatList.field_199088_e, StatList.field_188066_af, StatList.field_75929_E, StatList.field_199089_f, StatList.field_188068_aj});
         this.func_193651_b(false);
         this.func_148133_a(true, 20);
         Set var3 = Sets.newIdentityHashSet();
         Iterator var4 = IRegistry.field_212630_s.iterator();

         boolean var6;
         Iterator var7;
         StatType var8;
         while(var4.hasNext()) {
            Item var5 = (Item)var4.next();
            var6 = false;
            var7 = this.field_195114_w.iterator();

            while(var7.hasNext()) {
               var8 = (StatType)var7.next();
               if (var8.func_199079_a(var5) && GuiStats.this.field_146546_t.func_77444_a(var8.func_199076_b(var5)) > 0) {
                  var6 = true;
               }
            }

            if (var6) {
               var3.add(var5);
            }
         }

         var4 = IRegistry.field_212618_g.iterator();

         while(var4.hasNext()) {
            Block var9 = (Block)var4.next();
            var6 = false;
            var7 = this.field_195113_v.iterator();

            while(var7.hasNext()) {
               var8 = (StatType)var7.next();
               if (var8.func_199079_a(var9) && GuiStats.this.field_146546_t.func_77444_a(var8.func_199076_b(var9)) > 0) {
                  var6 = true;
               }
            }

            if (var6) {
               var3.add(var9.func_199767_j());
            }
         }

         var3.remove(Items.field_190931_a);
         this.field_195116_y = Lists.newArrayList(var3);
      }

      protected void func_148129_a(int var1, int var2, Tessellator var3) {
         if (!this.field_148161_k.field_71417_B.func_198030_b()) {
            this.field_195115_x = -1;
         }

         int var4;
         for(var4 = 0; var4 < this.field_195112_D.length; ++var4) {
            GuiStats.this.func_146527_c(var1 + GuiStats.this.func_195224_b(var4) - 18, var2 + 1, 0, this.field_195115_x == var4 ? 0 : 18);
         }

         int var5;
         if (this.field_195110_A != null) {
            var4 = GuiStats.this.func_195224_b(this.func_195105_b(this.field_195110_A)) - 36;
            var5 = this.field_195111_B == 1 ? 2 : 1;
            GuiStats.this.func_146527_c(var1 + var4, var2 + 1, 18 * var5, 0);
         }

         for(var4 = 0; var4 < this.field_195112_D.length; ++var4) {
            var5 = this.field_195115_x != var4 ? 0 : 1;
            GuiStats.this.func_146527_c(var1 + GuiStats.this.func_195224_b(var4) - 18 + var5, var2 + 1 + var5, 18 * this.field_195112_D[var4], 18);
         }

      }

      protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
         Item var8 = this.func_195106_c(var1);
         GuiStats.this.func_146521_a(var2 + 40, var3, var8);

         int var9;
         for(var9 = 0; var9 < this.field_195113_v.size(); ++var9) {
            Stat var10;
            if (var8 instanceof ItemBlock) {
               var10 = ((StatType)this.field_195113_v.get(var9)).func_199076_b(((ItemBlock)var8).func_179223_d());
            } else {
               var10 = null;
            }

            this.func_195103_a(var10, var2 + GuiStats.this.func_195224_b(var9), var3, var1 % 2 == 0);
         }

         for(var9 = 0; var9 < this.field_195114_w.size(); ++var9) {
            this.func_195103_a(((StatType)this.field_195114_w.get(var9)).func_199076_b(var8), var2 + GuiStats.this.func_195224_b(var9 + this.field_195113_v.size()), var3, var1 % 2 == 0);
         }

      }

      protected boolean func_148131_a(int var1) {
         return false;
      }

      public int func_148139_c() {
         return 375;
      }

      protected int func_148137_d() {
         return this.field_148155_a / 2 + 140;
      }

      protected void func_148123_a() {
         GuiStats.this.func_146276_q_();
      }

      protected void func_148132_a(int var1, int var2) {
         this.field_195115_x = -1;

         for(int var3 = 0; var3 < this.field_195112_D.length; ++var3) {
            int var4 = var1 - GuiStats.this.func_195224_b(var3);
            if (var4 >= -36 && var4 <= 0) {
               this.field_195115_x = var3;
               break;
            }
         }

         if (this.field_195115_x >= 0) {
            this.func_195107_a(this.func_195108_d(this.field_195115_x));
            this.field_148161_k.func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
         }

      }

      private StatType<?> func_195108_d(int var1) {
         return var1 < this.field_195113_v.size() ? (StatType)this.field_195113_v.get(var1) : (StatType)this.field_195114_w.get(var1 - this.field_195113_v.size());
      }

      private int func_195105_b(StatType<?> var1) {
         int var2 = this.field_195113_v.indexOf(var1);
         if (var2 >= 0) {
            return var2;
         } else {
            int var3 = this.field_195114_w.indexOf(var1);
            return var3 >= 0 ? var3 + this.field_195113_v.size() : -1;
         }
      }

      protected final int func_148127_b() {
         return this.field_195116_y.size();
      }

      protected final Item func_195106_c(int var1) {
         return (Item)this.field_195116_y.get(var1);
      }

      protected void func_195103_a(@Nullable Stat<?> var1, int var2, int var3, boolean var4) {
         String var5 = var1 == null ? "-" : var1.func_75968_a(GuiStats.this.field_146546_t.func_77444_a(var1));
         this.func_73731_b(GuiStats.this.field_146289_q, var5, var2 - GuiStats.this.field_146289_q.func_78256_a(var5), var3 + 5, var4 ? 16777215 : 9474192);
      }

      protected void func_148142_b(int var1, int var2) {
         if (var2 >= this.field_148153_b && var2 <= this.field_148154_c) {
            int var3 = this.func_195083_a((double)var1, (double)var2);
            int var4 = (this.field_148155_a - this.func_148139_c()) / 2;
            if (var3 >= 0) {
               if (var1 < var4 + 40 || var1 > var4 + 40 + 20) {
                  return;
               }

               Item var9 = this.func_195106_c(var3);
               this.func_200207_a(this.func_200208_a(var9), var1, var2);
            } else {
               TextComponentTranslation var5 = null;
               int var6 = var1 - var4;

               for(int var7 = 0; var7 < this.field_195112_D.length; ++var7) {
                  int var8 = GuiStats.this.func_195224_b(var7);
                  if (var6 >= var8 - 18 && var6 <= var8) {
                     var5 = new TextComponentTranslation(this.func_195108_d(var7).func_199078_c(), new Object[0]);
                     break;
                  }
               }

               this.func_200207_a(var5, var1, var2);
            }

         }
      }

      protected void func_200207_a(@Nullable ITextComponent var1, int var2, int var3) {
         if (var1 != null) {
            String var4 = var1.func_150254_d();
            int var5 = var2 + 12;
            int var6 = var3 - 12;
            int var7 = GuiStats.this.field_146289_q.func_78256_a(var4);
            this.func_73733_a(var5 - 3, var6 - 3, var5 + var7 + 3, var6 + 8 + 3, -1073741824, -1073741824);
            GuiStats.this.field_146289_q.func_175063_a(var4, (float)var5, (float)var6, -1);
         }
      }

      protected ITextComponent func_200208_a(Item var1) {
         return var1.func_200296_o();
      }

      protected void func_195107_a(StatType<?> var1) {
         if (var1 != this.field_195110_A) {
            this.field_195110_A = var1;
            this.field_195111_B = -1;
         } else if (this.field_195111_B == -1) {
            this.field_195111_B = 1;
         } else {
            this.field_195110_A = null;
            this.field_195111_B = 0;
         }

         this.field_195116_y.sort(this.field_195117_z);
      }

      class Comparator implements java.util.Comparator<Item> {
         private Comparator() {
            super();
         }

         public int compare(Item var1, Item var2) {
            int var3;
            int var4;
            if (StatsItem.this.field_195110_A == null) {
               var3 = 0;
               var4 = 0;
            } else {
               StatType var5;
               if (StatsItem.this.field_195113_v.contains(StatsItem.this.field_195110_A)) {
                  var5 = StatsItem.this.field_195110_A;
                  var3 = var1 instanceof ItemBlock ? GuiStats.this.field_146546_t.func_199060_a(var5, ((ItemBlock)var1).func_179223_d()) : -1;
                  var4 = var2 instanceof ItemBlock ? GuiStats.this.field_146546_t.func_199060_a(var5, ((ItemBlock)var2).func_179223_d()) : -1;
               } else {
                  var5 = StatsItem.this.field_195110_A;
                  var3 = GuiStats.this.field_146546_t.func_199060_a(var5, var1);
                  var4 = GuiStats.this.field_146546_t.func_199060_a(var5, var2);
               }
            }

            return var3 == var4 ? StatsItem.this.field_195111_B * Integer.compare(Item.func_150891_b(var1), Item.func_150891_b(var2)) : StatsItem.this.field_195111_B * Integer.compare(var3, var4);
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((Item)var1, (Item)var2);
         }

         // $FF: synthetic method
         Comparator(Object var2) {
            this();
         }
      }
   }

   class StatsGeneral extends GuiSlot {
      private Iterator<Stat<ResourceLocation>> field_195102_w;

      public StatsGeneral(Minecraft var2) {
         super(var2, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, 10);
         this.func_193651_b(false);
      }

      protected int func_148127_b() {
         return StatList.field_199092_j.func_199081_b();
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

      protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
         if (var1 == 0) {
            this.field_195102_w = StatList.field_199092_j.iterator();
         }

         Stat var8 = (Stat)this.field_195102_w.next();
         ITextComponent var9 = (new TextComponentTranslation("stat." + ((ResourceLocation)var8.func_197920_b()).toString().replace(':', '.'), new Object[0])).func_211708_a(TextFormatting.GRAY);
         this.func_73731_b(GuiStats.this.field_146289_q, var9.getString(), var2 + 2, var3 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
         String var10 = var8.func_75968_a(GuiStats.this.field_146546_t.func_77444_a(var8));
         this.func_73731_b(GuiStats.this.field_146289_q, var10, var2 + 2 + 213 - GuiStats.this.field_146289_q.func_78256_a(var10), var3 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
      }
   }
}
