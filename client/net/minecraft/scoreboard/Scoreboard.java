package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class Scoreboard {
   private final Map<String, ScoreObjective> field_96545_a = Maps.newHashMap();
   private final Map<ScoreCriteria, List<ScoreObjective>> field_96543_b = Maps.newHashMap();
   private final Map<String, Map<ScoreObjective, Score>> field_96544_c = Maps.newHashMap();
   private final ScoreObjective[] field_96541_d = new ScoreObjective[19];
   private final Map<String, ScorePlayerTeam> field_96542_e = Maps.newHashMap();
   private final Map<String, ScorePlayerTeam> field_96540_f = Maps.newHashMap();
   private static String[] field_178823_g;

   public Scoreboard() {
      super();
   }

   public boolean func_197900_b(String var1) {
      return this.field_96545_a.containsKey(var1);
   }

   public ScoreObjective func_197899_c(String var1) {
      return (ScoreObjective)this.field_96545_a.get(var1);
   }

   @Nullable
   public ScoreObjective func_96518_b(@Nullable String var1) {
      return (ScoreObjective)this.field_96545_a.get(var1);
   }

   public ScoreObjective func_199868_a(String var1, ScoreCriteria var2, ITextComponent var3, ScoreCriteria.RenderType var4) {
      if (var1.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + var1 + "' is too long!");
      } else if (this.field_96545_a.containsKey(var1)) {
         throw new IllegalArgumentException("An objective with the name '" + var1 + "' already exists!");
      } else {
         ScoreObjective var5 = new ScoreObjective(this, var1, var2, var3, var4);
         ((List)this.field_96543_b.computeIfAbsent(var2, (var0) -> {
            return Lists.newArrayList();
         })).add(var5);
         this.field_96545_a.put(var1, var5);
         this.func_96522_a(var5);
         return var5;
      }
   }

   public final void func_197893_a(ScoreCriteria var1, String var2, Consumer<Score> var3) {
      ((List)this.field_96543_b.getOrDefault(var1, Collections.emptyList())).forEach((var3x) -> {
         var3.accept(this.func_96529_a(var2, var3x));
      });
   }

   public boolean func_178819_b(String var1, ScoreObjective var2) {
      Map var3 = (Map)this.field_96544_c.get(var1);
      if (var3 == null) {
         return false;
      } else {
         Score var4 = (Score)var3.get(var2);
         return var4 != null;
      }
   }

   public Score func_96529_a(String var1, ScoreObjective var2) {
      if (var1.length() > 40) {
         throw new IllegalArgumentException("The player name '" + var1 + "' is too long!");
      } else {
         Map var3 = (Map)this.field_96544_c.computeIfAbsent(var1, (var0) -> {
            return Maps.newHashMap();
         });
         return (Score)var3.computeIfAbsent(var2, (var2x) -> {
            Score var3 = new Score(this, var2x, var1);
            var3.func_96647_c(0);
            return var3;
         });
      }
   }

   public Collection<Score> func_96534_i(ScoreObjective var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_96544_c.values().iterator();

      while(var3.hasNext()) {
         Map var4 = (Map)var3.next();
         Score var5 = (Score)var4.get(var1);
         if (var5 != null) {
            var2.add(var5);
         }
      }

      Collections.sort(var2, Score.field_96658_a);
      return var2;
   }

   public Collection<ScoreObjective> func_96514_c() {
      return this.field_96545_a.values();
   }

   public Collection<String> func_197897_d() {
      return this.field_96545_a.keySet();
   }

   public Collection<String> func_96526_d() {
      return Lists.newArrayList(this.field_96544_c.keySet());
   }

   public void func_178822_d(String var1, @Nullable ScoreObjective var2) {
      Map var3;
      if (var2 == null) {
         var3 = (Map)this.field_96544_c.remove(var1);
         if (var3 != null) {
            this.func_96516_a(var1);
         }
      } else {
         var3 = (Map)this.field_96544_c.get(var1);
         if (var3 != null) {
            Score var4 = (Score)var3.remove(var2);
            if (var3.size() < 1) {
               Map var5 = (Map)this.field_96544_c.remove(var1);
               if (var5 != null) {
                  this.func_96516_a(var1);
               }
            } else if (var4 != null) {
               this.func_178820_a(var1, var2);
            }
         }
      }

   }

   public Map<ScoreObjective, Score> func_96510_d(String var1) {
      Object var2 = (Map)this.field_96544_c.get(var1);
      if (var2 == null) {
         var2 = Maps.newHashMap();
      }

      return (Map)var2;
   }

   public void func_96519_k(ScoreObjective var1) {
      this.field_96545_a.remove(var1.func_96679_b());

      for(int var2 = 0; var2 < 19; ++var2) {
         if (this.func_96539_a(var2) == var1) {
            this.func_96530_a(var2, (ScoreObjective)null);
         }
      }

      List var5 = (List)this.field_96543_b.get(var1.func_96680_c());
      if (var5 != null) {
         var5.remove(var1);
      }

      Iterator var3 = this.field_96544_c.values().iterator();

      while(var3.hasNext()) {
         Map var4 = (Map)var3.next();
         var4.remove(var1);
      }

      this.func_96533_c(var1);
   }

   public void func_96530_a(int var1, @Nullable ScoreObjective var2) {
      this.field_96541_d[var1] = var2;
   }

   @Nullable
   public ScoreObjective func_96539_a(int var1) {
      return this.field_96541_d[var1];
   }

   public ScorePlayerTeam func_96508_e(String var1) {
      return (ScorePlayerTeam)this.field_96542_e.get(var1);
   }

   public ScorePlayerTeam func_96527_f(String var1) {
      if (var1.length() > 16) {
         throw new IllegalArgumentException("The team name '" + var1 + "' is too long!");
      } else {
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
   }

   public void func_96511_d(ScorePlayerTeam var1) {
      this.field_96542_e.remove(var1.func_96661_b());
      Iterator var2 = var1.func_96670_d().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.field_96540_f.remove(var3);
      }

      this.func_96513_c(var1);
   }

   public boolean func_197901_a(String var1, ScorePlayerTeam var2) {
      if (var1.length() > 40) {
         throw new IllegalArgumentException("The player name '" + var1 + "' is too long!");
      } else {
         if (this.func_96509_i(var1) != null) {
            this.func_96524_g(var1);
         }

         this.field_96540_f.put(var1, var2);
         return var2.func_96670_d().add(var1);
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

   public Collection<String> func_96531_f() {
      return this.field_96542_e.keySet();
   }

   public Collection<ScorePlayerTeam> func_96525_g() {
      return this.field_96542_e.values();
   }

   @Nullable
   public ScorePlayerTeam func_96509_i(String var1) {
      return (ScorePlayerTeam)this.field_96540_f.get(var1);
   }

   public void func_96522_a(ScoreObjective var1) {
   }

   public void func_199869_b(ScoreObjective var1) {
   }

   public void func_96533_c(ScoreObjective var1) {
   }

   public void func_96536_a(Score var1) {
   }

   public void func_96516_a(String var1) {
   }

   public void func_178820_a(String var1, ScoreObjective var2) {
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
         if (var0 >= 3 && var0 <= 18) {
            TextFormatting var1 = TextFormatting.func_175744_a(var0 - 3);
            if (var1 != null && var1 != TextFormatting.RESET) {
               return "sidebar.team." + var1.func_96297_d();
            }
         }

         return null;
      }
   }

   public static int func_96537_j(String var0) {
      if ("list".equalsIgnoreCase(var0)) {
         return 0;
      } else if ("sidebar".equalsIgnoreCase(var0)) {
         return 1;
      } else if ("belowName".equalsIgnoreCase(var0)) {
         return 2;
      } else {
         if (var0.startsWith("sidebar.team.")) {
            String var1 = var0.substring("sidebar.team.".length());
            TextFormatting var2 = TextFormatting.func_96300_b(var1);
            if (var2 != null && var2.func_175746_b() >= 0) {
               return var2.func_175746_b() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] func_178821_h() {
      if (field_178823_g == null) {
         field_178823_g = new String[19];

         for(int var0 = 0; var0 < 19; ++var0) {
            field_178823_g[var0] = func_96517_b(var0);
         }
      }

      return field_178823_g;
   }

   public void func_181140_a(Entity var1) {
      if (var1 != null && !(var1 instanceof EntityPlayer) && !var1.func_70089_S()) {
         String var2 = var1.func_189512_bd();
         this.func_178822_d(var2, (ScoreObjective)null);
         this.func_96524_g(var2);
      }
   }

   protected NBTTagList func_197902_i() {
      NBTTagList var1 = new NBTTagList();
      this.field_96544_c.values().stream().map(Map::values).forEach((var1x) -> {
         var1x.stream().filter((var0) -> {
            return var0.func_96645_d() != null;
         }).forEach((var1xx) -> {
            NBTTagCompound var2 = new NBTTagCompound();
            var2.func_74778_a("Name", var1xx.func_96653_e());
            var2.func_74778_a("Objective", var1xx.func_96645_d().func_96679_b());
            var2.func_74768_a("Score", var1xx.func_96652_c());
            var2.func_74757_a("Locked", var1xx.func_178816_g());
            var1.add((INBTBase)var2);
         });
      });
      return var1;
   }

   protected void func_197905_a(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         ScoreObjective var4 = this.func_197899_c(var3.func_74779_i("Objective"));
         String var5 = var3.func_74779_i("Name");
         if (var5.length() > 40) {
            var5 = var5.substring(0, 40);
         }

         Score var6 = this.func_96529_a(var5, var4);
         var6.func_96647_c(var3.func_74762_e("Score"));
         if (var3.func_74764_b("Locked")) {
            var6.func_178815_a(var3.func_74767_n("Locked"));
         }
      }

   }
}
