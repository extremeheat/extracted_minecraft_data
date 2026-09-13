package net.minecraft.scoreboard;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private static final Logger field_151481_a = LogManager.getLogger();
   private Scoreboard field_96507_a;
   private NBTTagCompound field_96506_b;

   public ScoreboardSaveData() {
      this("scoreboard");
   }

   public ScoreboardSaveData(String var1) {
      super(var1);
   }

   public void func_96499_a(Scoreboard var1) {
      this.field_96507_a = var1;
      if (this.field_96506_b != null) {
         this.func_76184_a(this.field_96506_b);
      }
   }

   @Override
   public void func_76184_a(NBTTagCompound var1) {
      if (this.field_96507_a == null) {
         this.field_96506_b = var1;
      } else {
         this.func_96501_b(var1.func_150295_c("Objectives", 10));
         this.func_96500_c(var1.func_150295_c("PlayerScores", 10));
         if (var1.func_150297_b("DisplaySlots", 10)) {
            this.func_96504_c(var1.func_74775_l("DisplaySlots"));
         }

         if (var1.func_150297_b("Teams", 9)) {
            this.func_96498_a(var1.func_150295_c("Teams", 10));
         }
      }
   }

   protected void func_96498_a(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.func_74745_c(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         ScorePlayerTeam var4 = this.field_96507_a.func_96527_f(var3.func_74779_i("Name"));
         var4.func_96664_a(var3.func_74779_i("DisplayName"));
         var4.func_96666_b(var3.func_74779_i("Prefix"));
         var4.func_96662_c(var3.func_74779_i("Suffix"));
         if (var3.func_150297_b("AllowFriendlyFire", 99)) {
            var4.func_96660_a(var3.func_74767_n("AllowFriendlyFire"));
         }

         if (var3.func_150297_b("SeeFriendlyInvisibles", 99)) {
            var4.func_98300_b(var3.func_74767_n("SeeFriendlyInvisibles"));
         }

         this.func_96502_a(var4, var3.func_150295_c("Players", 8));
      }
   }

   protected void func_96502_a(ScorePlayerTeam var1, NBTTagList var2) {
      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         this.field_96507_a.func_151392_a(var2.func_150307_f(var3), var1.func_96661_b());
      }
   }

   protected void func_96504_c(NBTTagCompound var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         if (var1.func_150297_b("slot_" + var2, 8)) {
            String var3 = var1.func_74779_i("slot_" + var2);
            ScoreObjective var4 = this.field_96507_a.func_96518_b(var3);
            this.field_96507_a.func_96530_a(var2, var4);
         }
      }
   }

   protected void func_96501_b(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.func_74745_c(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         IScoreObjectiveCriteria var4 = (IScoreObjectiveCriteria)IScoreObjectiveCriteria.field_96643_a.get(var3.func_74779_i("CriteriaName"));
         ScoreObjective var5 = this.field_96507_a.func_96535_a(var3.func_74779_i("Name"), var4);
         var5.func_96681_a(var3.func_74779_i("DisplayName"));
      }
   }

   protected void func_96500_c(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.func_74745_c(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         ScoreObjective var4 = this.field_96507_a.func_96518_b(var3.func_74779_i("Objective"));
         Score var5 = this.field_96507_a.func_96529_a(var3.func_74779_i("Name"), var4);
         var5.func_96647_c(var3.func_74762_e("Score"));
      }
   }

   @Override
   public void func_76187_b(NBTTagCompound var1) {
      if (this.field_96507_a == null) {
         field_151481_a.warn("Tried to save scoreboard without having a scoreboard...");
      } else {
         var1.func_74782_a("Objectives", this.func_96505_b());
         var1.func_74782_a("PlayerScores", this.func_96503_e());
         var1.func_74782_a("Teams", this.func_96496_a());
         this.func_96497_d(var1);
      }
   }

   protected NBTTagList func_96496_a() {
      NBTTagList var1 = new NBTTagList();

      for(ScorePlayerTeam var4 : this.field_96507_a.func_96525_g()) {
         NBTTagCompound var5 = new NBTTagCompound();
         var5.func_74778_a("Name", var4.func_96661_b());
         var5.func_74778_a("DisplayName", var4.func_96669_c());
         var5.func_74778_a("Prefix", var4.func_96668_e());
         var5.func_74778_a("Suffix", var4.func_96663_f());
         var5.func_74757_a("AllowFriendlyFire", var4.func_96665_g());
         var5.func_74757_a("SeeFriendlyInvisibles", var4.func_98297_h());
         NBTTagList var6 = new NBTTagList();

         for(String var8 : var4.func_96670_d()) {
            var6.func_74742_a(new NBTTagString(var8));
         }

         var5.func_74782_a("Players", var6);
         var1.func_74742_a(var5);
      }

      return var1;
   }

   protected void func_96497_d(NBTTagCompound var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      boolean var3 = false;

      for(int var4 = 0; var4 < 3; ++var4) {
         ScoreObjective var5 = this.field_96507_a.func_96539_a(var4);
         if (var5 != null) {
            var2.func_74778_a("slot_" + var4, var5.func_96679_b());
            var3 = true;
         }
      }

      if (var3) {
         var1.func_74782_a("DisplaySlots", var2);
      }
   }

   protected NBTTagList func_96505_b() {
      NBTTagList var1 = new NBTTagList();

      for(ScoreObjective var4 : this.field_96507_a.func_96514_c()) {
         NBTTagCompound var5 = new NBTTagCompound();
         var5.func_74778_a("Name", var4.func_96679_b());
         var5.func_74778_a("CriteriaName", var4.func_96680_c().func_96636_a());
         var5.func_74778_a("DisplayName", var4.func_96678_d());
         var1.func_74742_a(var5);
      }

      return var1;
   }

   protected NBTTagList func_96503_e() {
      NBTTagList var1 = new NBTTagList();

      for(Score var4 : this.field_96507_a.func_96528_e()) {
         NBTTagCompound var5 = new NBTTagCompound();
         var5.func_74778_a("Name", var4.func_96653_e());
         var5.func_74778_a("Objective", var4.func_96645_d().func_96679_b());
         var5.func_74768_a("Score", var4.func_96652_c());
         var1.func_74742_a(var5);
      }

      return var1;
   }
}
