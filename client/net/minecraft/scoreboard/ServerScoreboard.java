package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer field_96555_a;
   private final Set field_96553_b = new HashSet();
   private ScoreboardSaveData field_96554_c;

   public ServerScoreboard(MinecraftServer var1) {
      super();
      this.field_96555_a = var1;
   }

   @Override
   public void func_96536_a(Score var1) {
      super.func_96536_a(var1);
      if (this.field_96553_b.contains(var1.func_96645_d())) {
         this.field_96555_a.func_71203_ab().func_148540_a(new S3CPacketUpdateScore(var1, 0));
      }

      this.func_96551_b();
   }

   @Override
   public void func_96516_a(String var1) {
      super.func_96516_a(var1);
      this.field_96555_a.func_71203_ab().func_148540_a(new S3CPacketUpdateScore(var1));
      this.func_96551_b();
   }

   @Override
   public void func_96530_a(int var1, ScoreObjective var2) {
      ScoreObjective var3 = this.func_96539_a(var1);
      super.func_96530_a(var1, var2);
      if (var3 != var2 && var3 != null) {
         if (this.func_96552_h(var3) > 0) {
            this.field_96555_a.func_71203_ab().func_148540_a(new S3DPacketDisplayScoreboard(var1, var2));
         } else {
            this.func_96546_g(var3);
         }
      }

      if (var2 != null) {
         if (this.field_96553_b.contains(var2)) {
            this.field_96555_a.func_71203_ab().func_148540_a(new S3DPacketDisplayScoreboard(var1, var2));
         } else {
            this.func_96549_e(var2);
         }
      }

      this.func_96551_b();
   }

   @Override
   public boolean func_151392_a(String var1, String var2) {
      if (super.func_151392_a(var1, var2)) {
         ScorePlayerTeam var3 = this.func_96508_e(var2);
         this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(var3, Arrays.asList(var1), 3));
         this.func_96551_b();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_96512_b(String var1, ScorePlayerTeam var2) {
      super.func_96512_b(var1, var2);
      this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(var2, Arrays.asList(var1), 4));
      this.func_96551_b();
   }

   @Override
   public void func_96522_a(ScoreObjective var1) {
      super.func_96522_a(var1);
      this.func_96551_b();
   }

   @Override
   public void func_96532_b(ScoreObjective var1) {
      super.func_96532_b(var1);
      if (this.field_96553_b.contains(var1)) {
         this.field_96555_a.func_71203_ab().func_148540_a(new S3BPacketScoreboardObjective(var1, 2));
      }

      this.func_96551_b();
   }

   @Override
   public void func_96533_c(ScoreObjective var1) {
      super.func_96533_c(var1);
      if (this.field_96553_b.contains(var1)) {
         this.func_96546_g(var1);
      }

      this.func_96551_b();
   }

   @Override
   public void func_96523_a(ScorePlayerTeam var1) {
      super.func_96523_a(var1);
      this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(var1, 0));
      this.func_96551_b();
   }

   @Override
   public void func_96538_b(ScorePlayerTeam var1) {
      super.func_96538_b(var1);
      this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(var1, 2));
      this.func_96551_b();
   }

   @Override
   public void func_96513_c(ScorePlayerTeam var1) {
      super.func_96513_c(var1);
      this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(var1, 1));
      this.func_96551_b();
   }

   public void func_96547_a(ScoreboardSaveData var1) {
      this.field_96554_c = var1;
   }

   protected void func_96551_b() {
      if (this.field_96554_c != null) {
         this.field_96554_c.func_76185_a();
      }
   }

   public List func_96550_d(ScoreObjective var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new S3BPacketScoreboardObjective(var1, 0));

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            var2.add(new S3DPacketDisplayScoreboard(var3, var1));
         }
      }

      for(Score var4 : this.func_96534_i(var1)) {
         var2.add(new S3CPacketUpdateScore(var4, 0));
      }

      return var2;
   }

   public void func_96549_e(ScoreObjective var1) {
      List var2 = this.func_96550_d(var1);

      for(EntityPlayerMP var4 : this.field_96555_a.func_71203_ab().field_72404_b) {
         for(Packet var6 : var2) {
            var4.field_71135_a.func_147359_a(var6);
         }
      }

      this.field_96553_b.add(var1);
   }

   public List func_96548_f(ScoreObjective var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new S3BPacketScoreboardObjective(var1, 1));

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            var2.add(new S3DPacketDisplayScoreboard(var3, var1));
         }
      }

      return var2;
   }

   public void func_96546_g(ScoreObjective var1) {
      List var2 = this.func_96548_f(var1);

      for(EntityPlayerMP var4 : this.field_96555_a.func_71203_ab().field_72404_b) {
         for(Packet var6 : var2) {
            var4.field_71135_a.func_147359_a(var6);
         }
      }

      this.field_96553_b.remove(var1);
   }

   public int func_96552_h(ScoreObjective var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            ++var2;
         }
      }

      return var2;
   }
}
