package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer field_96555_a;
   private final Set<ScoreObjective> field_96553_b = Sets.newHashSet();
   private Runnable[] field_186685_c = new Runnable[0];

   public ServerScoreboard(MinecraftServer var1) {
      super();
      this.field_96555_a = var1;
   }

   public void func_96536_a(Score var1) {
      super.func_96536_a(var1);
      if (this.field_96553_b.contains(var1.func_96645_d())) {
         this.field_96555_a.func_184103_al().func_148540_a(new SPacketUpdateScore(ServerScoreboard.Action.CHANGE, var1.func_96645_d().func_96679_b(), var1.func_96653_e(), var1.func_96652_c()));
      }

      this.func_96551_b();
   }

   public void func_96516_a(String var1) {
      super.func_96516_a(var1);
      this.field_96555_a.func_184103_al().func_148540_a(new SPacketUpdateScore(ServerScoreboard.Action.REMOVE, (String)null, var1, 0));
      this.func_96551_b();
   }

   public void func_178820_a(String var1, ScoreObjective var2) {
      super.func_178820_a(var1, var2);
      if (this.field_96553_b.contains(var2)) {
         this.field_96555_a.func_184103_al().func_148540_a(new SPacketUpdateScore(ServerScoreboard.Action.REMOVE, var2.func_96679_b(), var1, 0));
      }

      this.func_96551_b();
   }

   public void func_96530_a(int var1, @Nullable ScoreObjective var2) {
      ScoreObjective var3 = this.func_96539_a(var1);
      super.func_96530_a(var1, var2);
      if (var3 != var2 && var3 != null) {
         if (this.func_96552_h(var3) > 0) {
            this.field_96555_a.func_184103_al().func_148540_a(new SPacketDisplayObjective(var1, var2));
         } else {
            this.func_96546_g(var3);
         }
      }

      if (var2 != null) {
         if (this.field_96553_b.contains(var2)) {
            this.field_96555_a.func_184103_al().func_148540_a(new SPacketDisplayObjective(var1, var2));
         } else {
            this.func_96549_e(var2);
         }
      }

      this.func_96551_b();
   }

   public boolean func_197901_a(String var1, ScorePlayerTeam var2) {
      if (super.func_197901_a(var1, var2)) {
         this.field_96555_a.func_184103_al().func_148540_a(new SPacketTeams(var2, Arrays.asList(var1), 3));
         this.func_96551_b();
         return true;
      } else {
         return false;
      }
   }

   public void func_96512_b(String var1, ScorePlayerTeam var2) {
      super.func_96512_b(var1, var2);
      this.field_96555_a.func_184103_al().func_148540_a(new SPacketTeams(var2, Arrays.asList(var1), 4));
      this.func_96551_b();
   }

   public void func_96522_a(ScoreObjective var1) {
      super.func_96522_a(var1);
      this.func_96551_b();
   }

   public void func_199869_b(ScoreObjective var1) {
      super.func_199869_b(var1);
      if (this.field_96553_b.contains(var1)) {
         this.field_96555_a.func_184103_al().func_148540_a(new SPacketScoreboardObjective(var1, 2));
      }

      this.func_96551_b();
   }

   public void func_96533_c(ScoreObjective var1) {
      super.func_96533_c(var1);
      if (this.field_96553_b.contains(var1)) {
         this.func_96546_g(var1);
      }

      this.func_96551_b();
   }

   public void func_96523_a(ScorePlayerTeam var1) {
      super.func_96523_a(var1);
      this.field_96555_a.func_184103_al().func_148540_a(new SPacketTeams(var1, 0));
      this.func_96551_b();
   }

   public void func_96538_b(ScorePlayerTeam var1) {
      super.func_96538_b(var1);
      this.field_96555_a.func_184103_al().func_148540_a(new SPacketTeams(var1, 2));
      this.func_96551_b();
   }

   public void func_96513_c(ScorePlayerTeam var1) {
      super.func_96513_c(var1);
      this.field_96555_a.func_184103_al().func_148540_a(new SPacketTeams(var1, 1));
      this.func_96551_b();
   }

   public void func_186684_a(Runnable var1) {
      this.field_186685_c = (Runnable[])Arrays.copyOf(this.field_186685_c, this.field_186685_c.length + 1);
      this.field_186685_c[this.field_186685_c.length - 1] = var1;
   }

   protected void func_96551_b() {
      Runnable[] var1 = this.field_186685_c;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Runnable var4 = var1[var3];
         var4.run();
      }

   }

   public List<Packet<?>> func_96550_d(ScoreObjective var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new SPacketScoreboardObjective(var1, 0));

      for(int var3 = 0; var3 < 19; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            var2.add(new SPacketDisplayObjective(var3, var1));
         }
      }

      Iterator var5 = this.func_96534_i(var1).iterator();

      while(var5.hasNext()) {
         Score var4 = (Score)var5.next();
         var2.add(new SPacketUpdateScore(ServerScoreboard.Action.CHANGE, var4.func_96645_d().func_96679_b(), var4.func_96653_e(), var4.func_96652_c()));
      }

      return var2;
   }

   public void func_96549_e(ScoreObjective var1) {
      List var2 = this.func_96550_d(var1);
      Iterator var3 = this.field_96555_a.func_184103_al().func_181057_v().iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            Packet var6 = (Packet)var5.next();
            var4.field_71135_a.func_147359_a(var6);
         }
      }

      this.field_96553_b.add(var1);
   }

   public List<Packet<?>> func_96548_f(ScoreObjective var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new SPacketScoreboardObjective(var1, 1));

      for(int var3 = 0; var3 < 19; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            var2.add(new SPacketDisplayObjective(var3, var1));
         }
      }

      return var2;
   }

   public void func_96546_g(ScoreObjective var1) {
      List var2 = this.func_96548_f(var1);
      Iterator var3 = this.field_96555_a.func_184103_al().func_181057_v().iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            Packet var6 = (Packet)var5.next();
            var4.field_71135_a.func_147359_a(var6);
         }
      }

      this.field_96553_b.remove(var1);
   }

   public int func_96552_h(ScoreObjective var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < 19; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            ++var2;
         }
      }

      return var2;
   }

   public static enum Action {
      CHANGE,
      REMOVE;

      private Action() {
      }
   }
}
