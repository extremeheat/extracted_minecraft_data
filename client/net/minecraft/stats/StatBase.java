package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent$Action;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class StatBase {
   public final String field_75975_e;
   private final IChatComponent field_75978_a;
   public boolean field_75972_f;
   private final IStatType field_75976_b;
   private final IScoreObjectiveCriteria field_150957_c;
   private Class field_150956_d;
   private static NumberFormat field_75977_c = NumberFormat.getIntegerInstance(Locale.US);
   public static IStatType field_75980_h = new StatBase$1();
   private static DecimalFormat field_75974_d = new DecimalFormat("########0.00");
   public static IStatType field_75981_i = new StatBase$2();
   public static IStatType field_75979_j = new StatBase$3();
   public static IStatType field_111202_k = new StatBase$4();

   public StatBase(String var1, IChatComponent var2, IStatType var3) {
      super();
      this.field_75975_e = var1;
      this.field_75978_a = var2;
      this.field_75976_b = var3;
      this.field_150957_c = new ObjectiveStat(this);
      IScoreObjectiveCriteria.field_96643_a.put(this.field_150957_c.func_96636_a(), this.field_150957_c);
   }

   public StatBase(String var1, IChatComponent var2) {
      this(var1, var2, field_75980_h);
   }

   public StatBase func_75966_h() {
      this.field_75972_f = true;
      return this;
   }

   public StatBase func_75971_g() {
      if (StatList.field_75942_a.containsKey(this.field_75975_e)) {
         throw new RuntimeException(
            "Duplicate stat id: \""
               + ((StatBase)StatList.field_75942_a.get(this.field_75975_e)).field_75978_a
               + "\" and \""
               + this.field_75978_a
               + "\" at id "
               + this.field_75975_e
         );
      } else {
         StatList.field_75940_b.add(this);
         StatList.field_75942_a.put(this.field_75975_e, this);
         return this;
      }
   }

   public boolean func_75967_d() {
      return false;
   }

   public String func_75968_a(int var1) {
      return this.field_75976_b.func_75843_a(var1);
   }

   public IChatComponent func_150951_e() {
      IChatComponent var1 = this.field_75978_a.func_150259_f();
      var1.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
      var1.func_150256_b().func_150209_a(new HoverEvent(HoverEvent$Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.field_75975_e)));
      return var1;
   }

   public IChatComponent func_150955_j() {
      IChatComponent var1 = this.func_150951_e();
      IChatComponent var2 = new ChatComponentText("[").func_150257_a(var1).func_150258_a("]");
      var2.func_150255_a(var1.func_150256_b());
      return var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         StatBase var2 = (StatBase)var1;
         return this.field_75975_e.equals(var2.field_75975_e);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.field_75975_e.hashCode();
   }

   @Override
   public String toString() {
      return "Stat{id="
         + this.field_75975_e
         + ", nameId="
         + this.field_75978_a
         + ", awardLocallyOnly="
         + this.field_75972_f
         + ", formatter="
         + this.field_75976_b
         + ", objectiveCriteria="
         + this.field_150957_c
         + '}';
   }

   public IScoreObjectiveCriteria func_150952_k() {
      return this.field_150957_c;
   }

   public Class func_150954_l() {
      return this.field_150956_d;
   }

   public StatBase func_150953_b(Class var1) {
      this.field_150956_d = var1;
      return this;
   }
}
