package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

public class ScoreComponent extends BaseComponent implements ContextAwareComponent {
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;
   private String value = "";

   public ScoreComponent(String var1, String var2) {
      this.name = var1;
      this.objective = var2;
      EntitySelector var3 = null;

      try {
         EntitySelectorParser var4 = new EntitySelectorParser(new StringReader(var1));
         var3 = var4.parse();
      } catch (CommandSyntaxException var5) {
      }

      this.selector = var3;
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public String getContents() {
      return this.value;
   }

   private void resolve(CommandSourceStack var1) {
      MinecraftServer var2 = var1.getServer();
      if (var2 != null && var2.isInitialized() && StringUtil.isNullOrEmpty(this.value)) {
         ServerScoreboard var3 = var2.getScoreboard();
         Objective var4 = var3.getObjective(this.objective);
         if (var3.hasPlayerScore(this.name, var4)) {
            Score var5 = var3.getOrCreatePlayerScore(this.name, var4);
            this.setValue(String.format("%d", var5.getScore()));
         } else {
            this.value = "";
         }
      }

   }

   public ScoreComponent copy() {
      ScoreComponent var1 = new ScoreComponent(this.name, this.objective);
      var1.setValue(this.value);
      return var1;
   }

   public Component resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 == null) {
         return this.copy();
      } else {
         String var4;
         if (this.selector != null) {
            List var5 = this.selector.findEntities(var1);
            if (var5.isEmpty()) {
               var4 = this.name;
            } else {
               if (var5.size() != 1) {
                  throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
               }

               var4 = ((Entity)var5.get(0)).getScoreboardName();
            }
         } else {
            var4 = this.name;
         }

         String var7 = var2 != null && var4.equals("*") ? var2.getScoreboardName() : var4;
         ScoreComponent var6 = new ScoreComponent(var7, this.objective);
         var6.setValue(this.value);
         var6.resolve(var1);
         return var6;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ScoreComponent)) {
         return false;
      } else {
         ScoreComponent var2 = (ScoreComponent)var1;
         return this.name.equals(var2.name) && this.objective.equals(var2.objective) && super.equals(var1);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
