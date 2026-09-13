package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scoreboard {
   private final Map field_96545_a = new HashMap();
   private final Map field_96543_b = new HashMap();
   private final Map field_96544_c = new HashMap();
   private final ScoreObjective[] field_96541_d = new ScoreObjective[3];
   private final Map field_96542_e = new HashMap();
   private final Map field_96540_f = new HashMap();

   public Scoreboard() {
      super();
   }

   public ScoreObjective func_96518_b(String var1) {
      return (ScoreObjective)this.field_96545_a.get(var1);
   }

   public ScoreObjective func_96535_a(String var1, IScoreObjectiveCriteria var2) {
      ScoreObjective var3 = this.func_96518_b(var1);
      if (var3 != null) {
         throw new IllegalArgumentException("An objective with the name '" + var1 + "' already exists!");
      } else {
         var3 = new ScoreObjective(this, var1, var2);
         Object var4 = (List)this.field_96543_b.get(var2);
         if (var4 == null) {
            var4 = new ArrayList();
            this.field_96543_b.put(var2, var4);
         }

         var4.add(var3);
         this.field_96545_a.put(var1, var3);
         this.func_96522_a(var3);
         return var3;
      }
   }

   public Collection func_96520_a(IScoreObjectiveCriteria var1) {
      Collection var2 = (Collection)this.field_96543_b.get(var1);
      return var2 == null ? new ArrayList() : new ArrayList(var2);
   }

   public Score func_96529_a(String var1, ScoreObjective var2) {
      Object var3 = (Map)this.field_96544_c.get(var1);
      if (var3 == null) {
         var3 = new HashMap();
         this.field_96544_c.put(var1, var3);
      }

      Score var4 = (Score)var3.get(var2);
      if (var4 == null) {
         var4 = new Score(this, var2, var1);
         var3.put(var2, var4);
      }

      return var4;
   }

   public Collection func_96534_i(ScoreObjective var1) {
      ArrayList var2 = new ArrayList();

      for(Map var4 : this.field_96544_c.values()) {
         Score var5 = (Score)var4.get(var1);
         if (var5 != null) {
            var2.add(var5);
         }
      }

      Collections.sort(var2, Score.field_96658_a);
      return var2;
   }

   public Collection func_96514_c() {
      return this.field_96545_a.values();
   }

   public Collection func_96526_d() {
      return this.field_96544_c.keySet();
   }

   public void func_96515_c(String var1) {
      Map var2 = (Map)this.field_96544_c.remove(var1);
      if (var2 != null) {
         this.func_96516_a(var1);
      }
   }

   public Collection func_96528_e() {
      Collection var1 = this.field_96544_c.values();
      ArrayList var2 = new ArrayList();

      for(Map var4 : var1) {
         var2.addAll(var4.values());
      }

      return var2;
   }

   public Map func_96510_d(String var1) {
      Object var2 = (Map)this.field_96544_c.get(var1);
      if (var2 == null) {
         var2 = new HashMap();
      }

      return (Map)var2;
   }

   public void func_96519_k(ScoreObjective var1) {
      this.field_96545_a.remove(var1.func_96679_b());

      for(int var2 = 0; var2 < 3; ++var2) {
         if (this.func_96539_a(var2) == var1) {
            this.func_96530_a(var2, null);
         }
      }

      List var5 = (List)this.field_96543_b.get(var1.func_96680_c());
      if (var5 != null) {
         var5.remove(var1);
      }

      for(Map var4 : this.field_96544_c.values()) {
         var4.remove(var1);
      }

      this.func_96533_c(var1);
   }

   public void func_96530_a(int var1, ScoreObjective var2) {
      this.field_96541_d[var1] = var2;
   }

   public ScoreObjective func_96539_a(int var1) {
      return this.field_96541_d[var1];
   }

   public ScorePlayerTeam func_96508_e(String var1) {
      return (ScorePlayerTeam)this.field_96542_e.get(var1);
   }

   public ScorePlayerTeam func_96527_f(String var1) {
      ScorePlayerTeam var2 = this.func_96508_e(var1);
      if (var2 != null) {
         throw new IllegalArgumentException("A team with the name '" + var1 + "' already exists!");
      } else {
         var2 = new ScorePlayerTeam(this, var1);
         this.field_96542_e.put(var1, var2);
         this.func_96523_a(var2);
         return var2;
      }
   }

   public void func_96511_d(ScorePlayerTeam var1) {
      this.field_96542_e.remove(var1.func_96661_b());

      for(String var3 : var1.func_96670_d()) {
         this.field_96540_f.remove(var3);
      }

      this.func_96513_c(var1);
   }

   public boolean func_151392_a(String var1, String var2) {
      if (!this.field_96542_e.containsKey(var2)) {
         return false;
      } else {
         ScorePlayerTeam var3 = this.func_96508_e(var2);
         if (this.func_96509_i(var1) != null) {
            this.func_96524_g(var1);
         }

         this.field_96540_f.put(var1, var3);
         var3.func_96670_d().add(var1);
         return true;
      }
   }

   public boolean func_96524_g(String var1) {
      ScorePlayerTeam var2 = this.func_96509_i(var1);
      if (var2 != null) {
         this.func_96512_b(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public void func_96512_b(String var1, ScorePlayerTeam var2) {
      if (this.func_96509_i(var1) != var2) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + var2.func_96661_b() + "'.");
      } else {
         this.field_96540_f.remove(var1);
         var2.func_96670_d().remove(var1);
      }
   }

   public Collection func_96531_f() {
      return this.field_96542_e.keySet();
   }

   public Collection func_96525_g() {
      return this.field_96542_e.values();
   }

   public ScorePlayerTeam func_96509_i(String var1) {
      return (ScorePlayerTeam)this.field_96540_f.get(var1);
   }

   public void func_96522_a(ScoreObjective var1) {
   }

   public void func_96532_b(ScoreObjective var1) {
   }

   public void func_96533_c(ScoreObjective var1) {
   }

   public void func_96536_a(Score var1) {
   }

   public void func_96516_a(String var1) {
   }

   public void func_96523_a(ScorePlayerTeam var1) {
   }

   public void func_96538_b(ScorePlayerTeam var1) {
   }

   public void func_96513_c(ScorePlayerTeam var1) {
   }

   public static String func_96517_b(int var0) {
      switch(var0) {
         case 0:
            return "list";
         case 1:
            return "sidebar";
         case 2:
            return "belowName";
         default:
            return null;
      }
   }

   public static int func_96537_j(String var0) {
      if (var0.equalsIgnoreCase("list")) {
         return 0;
      } else if (var0.equalsIgnoreCase("sidebar")) {
         return 1;
      } else {
         return var0.equalsIgnoreCase("belowName") ? 2 : -1;
      }
   }
}
