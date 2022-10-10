package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
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

   public void func_76184_a(NBTTagCompound var1) {
      if (this.field_96507_a == null) {
         this.field_96506_b = var1;
      } else {
         this.func_96501_b(var1.func_150295_c("Objectives", 10));
         this.field_96507_a.func_197905_a(var1.func_150295_c("PlayerScores", 10));
         if (var1.func_150297_b("DisplaySlots", 10)) {
            this.func_96504_c(var1.func_74775_l("DisplaySlots"));
         }

         if (var1.func_150297_b("Teams", 9)) {
            this.func_96498_a(var1.func_150295_c("Teams", 10));
         }

      }
   }

   protected void func_96498_a(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         String var4 = var3.func_74779_i("Name");
         if (var4.length() > 16) {
            var4 = var4.substring(0, 16);
         }

         ScorePlayerTeam var5 = this.field_96507_a.func_96527_f(var4);
         ITextComponent var6 = ITextComponent.Serializer.func_150699_a(var3.func_74779_i("DisplayName"));
         if (var6 != null) {
            var5.func_96664_a(var6);
         }

         if (var3.func_150297_b("TeamColor", 8)) {
            var5.func_178774_a(TextFormatting.func_96300_b(var3.func_74779_i("TeamColor")));
         }

         if (var3.func_150297_b("AllowFriendlyFire", 99)) {
            var5.func_96660_a(var3.func_74767_n("AllowFriendlyFire"));
         }

         if (var3.func_150297_b("SeeFriendlyInvisibles", 99)) {
            var5.func_98300_b(var3.func_74767_n("SeeFriendlyInvisibles"));
         }

         ITextComponent var7;
         if (var3.func_150297_b("MemberNamePrefix", 8)) {
            var7 = ITextComponent.Serializer.func_150699_a(var3.func_74779_i("MemberNamePrefix"));
            if (var7 != null) {
               var5.func_207408_a(var7);
            }
         }

         if (var3.func_150297_b("MemberNameSuffix", 8)) {
            var7 = ITextComponent.Serializer.func_150699_a(var3.func_74779_i("MemberNameSuffix"));
            if (var7 != null) {
               var5.func_207409_b(var7);
            }
         }

         Team.EnumVisible var8;
         if (var3.func_150297_b("NameTagVisibility", 8)) {
            var8 = Team.EnumVisible.func_178824_a(var3.func_74779_i("NameTagVisibility"));
            if (var8 != null) {
               var5.func_178772_a(var8);
            }
         }

         if (var3.func_150297_b("DeathMessageVisibility", 8)) {
            var8 = Team.EnumVisible.func_178824_a(var3.func_74779_i("DeathMessageVisibility"));
            if (var8 != null) {
               var5.func_178773_b(var8);
            }
         }

         if (var3.func_150297_b("CollisionRule", 8)) {
            Team.CollisionRule var9 = Team.CollisionRule.func_186686_a(var3.func_74779_i("CollisionRule"));
            if (var9 != null) {
               var5.func_186682_a(var9);
            }
         }

         this.func_96502_a(var5, var3.func_150295_c("Players", 8));
      }

   }

   protected void func_96502_a(ScorePlayerTeam var1, NBTTagList var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.field_96507_a.func_197901_a(var2.func_150307_f(var3), var1);
      }

   }

   protected void func_96504_c(NBTTagCompound var1) {
      for(int var2 = 0; var2 < 19; ++var2) {
         if (var1.func_150297_b("slot_" + var2, 8)) {
            String var3 = var1.func_74779_i("slot_" + var2);
            ScoreObjective var4 = this.field_96507_a.func_96518_b(var3);
            this.field_96507_a.func_96530_a(var2, var4);
         }
      }

   }

   protected void func_96501_b(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         ScoreCriteria var4 = ScoreCriteria.func_197911_a(var3.func_74779_i("CriteriaName"));
         if (var4 != null) {
            String var5 = var3.func_74779_i("Name");
            if (var5.length() > 16) {
               var5 = var5.substring(0, 16);
            }

            ITextComponent var6 = ITextComponent.Serializer.func_150699_a(var3.func_74779_i("DisplayName"));
            ScoreCriteria.RenderType var7 = ScoreCriteria.RenderType.func_211839_a(var3.func_74779_i("RenderType"));
            this.field_96507_a.func_199868_a(var5, var4, var6, var7);
         }
      }

   }

   public NBTTagCompound func_189551_b(NBTTagCompound var1) {
      if (this.field_96507_a == null) {
         field_151481_a.warn("Tried to save scoreboard without having a scoreboard...");
         return var1;
      } else {
         var1.func_74782_a("Objectives", this.func_96505_b());
         var1.func_74782_a("PlayerScores", this.field_96507_a.func_197902_i());
         var1.func_74782_a("Teams", this.func_96496_a());
         this.func_96497_d(var1);
         return var1;
      }
   }

   protected NBTTagList func_96496_a() {
      NBTTagList var1 = new NBTTagList();
      Collection var2 = this.field_96507_a.func_96525_g();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ScorePlayerTeam var4 = (ScorePlayerTeam)var3.next();
         NBTTagCompound var5 = new NBTTagCompound();
         var5.func_74778_a("Name", var4.func_96661_b());
         var5.func_74778_a("DisplayName", ITextComponent.Serializer.func_150696_a(var4.func_96669_c()));
         if (var4.func_178775_l().func_175746_b() >= 0) {
            var5.func_74778_a("TeamColor", var4.func_178775_l().func_96297_d());
         }

         var5.func_74757_a("AllowFriendlyFire", var4.func_96665_g());
         var5.func_74757_a("SeeFriendlyInvisibles", var4.func_98297_h());
         var5.func_74778_a("MemberNamePrefix", ITextComponent.Serializer.func_150696_a(var4.func_207406_e()));
         var5.func_74778_a("MemberNameSuffix", ITextComponent.Serializer.func_150696_a(var4.func_207407_f()));
         var5.func_74778_a("NameTagVisibility", var4.func_178770_i().field_178830_e);
         var5.func_74778_a("DeathMessageVisibility", var4.func_178771_j().field_178830_e);
         var5.func_74778_a("CollisionRule", var4.func_186681_k().field_186693_e);
         NBTTagList var6 = new NBTTagList();
         Iterator var7 = var4.func_96670_d().iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            var6.add((INBTBase)(new NBTTagString(var8)));
         }

         var5.func_74782_a("Players", var6);
         var1.add((INBTBase)var5);
      }

      return var1;
   }

   protected void func_96497_d(NBTTagCompound var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      boolean var3 = false;

      for(int var4 = 0; var4 < 19; ++var4) {
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
      Collection var2 = this.field_96507_a.func_96514_c();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ScoreObjective var4 = (ScoreObjective)var3.next();
         if (var4.func_96680_c() != null) {
            NBTTagCompound var5 = new NBTTagCompound();
            var5.func_74778_a("Name", var4.func_96679_b());
            var5.func_74778_a("CriteriaName", var4.func_96680_c().func_96636_a());
            var5.func_74778_a("DisplayName", ITextComponent.Serializer.func_150696_a(var4.func_96678_d()));
            var5.func_74778_a("RenderType", var4.func_199865_f().func_211838_a());
            var1.add((INBTBase)var5);
         }
      }

      return var1;
   }
}
