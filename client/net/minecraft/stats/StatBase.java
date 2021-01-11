package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;

public class StatBase {
   public final String field_75975_e;
   private final IChatComponent field_75978_a;
   public boolean field_75972_f;
   private final IStatType field_75976_b;
   private final IScoreObjectiveCriteria field_150957_c;
   private Class<? extends IJsonSerializable> field_150956_d;
   private static NumberFormat field_75977_c;
   public static IStatType field_75980_h;
   private static DecimalFormat field_75974_d;
   public static IStatType field_75981_i;
   public static IStatType field_75979_j;
   public static IStatType field_111202_k;

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
         throw new RuntimeException("Duplicate stat id: \"" + ((StatBase)StatList.field_75942_a.get(this.field_75975_e)).field_75978_a + "\" and \"" + this.field_75978_a + "\" at id " + this.field_75975_e);
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
      var1.func_150256_b().func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.field_75975_e)));
      return var1;
   }

   public IChatComponent func_150955_j() {
      IChatComponent var1 = this.func_150951_e();
      IChatComponent var2 = (new ChatComponentText("[")).func_150257_a(var1).func_150258_a("]");
      var2.func_150255_a(var1.func_150256_b());
      return var2;
   }

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

   public int hashCode() {
      return this.field_75975_e.hashCode();
   }

   public String toString() {
      return "Stat{id=" + this.field_75975_e + ", nameId=" + this.field_75978_a + ", awardLocallyOnly=" + this.field_75972_f + ", formatter=" + this.field_75976_b + ", objectiveCriteria=" + this.field_150957_c + '}';
   }

   public IScoreObjectiveCriteria func_150952_k() {
      return this.field_150957_c;
   }

   public Class<? extends IJsonSerializable> func_150954_l() {
      return this.field_150956_d;
   }

   public StatBase func_150953_b(Class<? extends IJsonSerializable> var1) {
      this.field_150956_d = var1;
      return this;
   }

   static {
      field_75977_c = NumberFormat.getIntegerInstance(Locale.US);
      field_75980_h = new IStatType() {
         public String func_75843_a(int var1) {
            return StatBase.field_75977_c.format((long)var1);
         }
      };
      field_75974_d = new DecimalFormat("########0.00");
      field_75981_i = new IStatType() {
         public String func_75843_a(int var1) {
            double var2 = (double)var1 / 20.0D;
            double var4 = var2 / 60.0D;
            double var6 = var4 / 60.0D;
            double var8 = var6 / 24.0D;
            double var10 = var8 / 365.0D;
            if (var10 > 0.5D) {
               return StatBase.field_75974_d.format(var10) + " y";
            } else if (var8 > 0.5D) {
               return StatBase.field_75974_d.format(var8) + " d";
            } else if (var6 > 0.5D) {
               return StatBase.field_75974_d.format(var6) + " h";
            } else {
               return var4 > 0.5D ? StatBase.field_75974_d.format(var4) + " m" : var2 + " s";
            }
         }
      };
      field_75979_j = new IStatType() {
         public String func_75843_a(int var1) {
            double var2 = (double)var1 / 100.0D;
            double var4 = var2 / 1000.0D;
            if (var4 > 0.5D) {
               return StatBase.field_75974_d.format(var4) + " km";
            } else {
               return var2 > 0.5D ? StatBase.field_75974_d.format(var2) + " m" : var1 + " cm";
            }
         }
      };
      field_111202_k = new IStatType() {
         public String func_75843_a(int var1) {
            return StatBase.field_75974_d.format((double)var1 * 0.1D);
         }
      };
   }
}
