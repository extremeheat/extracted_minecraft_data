package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

public class ScoreContents implements ComponentContents {
   private static final String SCORER_PLACEHOLDER = "*";
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;

   @Nullable
   private static EntitySelector parseSelector(String var0) {
      try {
         return new EntitySelectorParser(new StringReader(var0)).parse();
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public ScoreContents(String var1, String var2) {
      super();
      this.name = var1;
      this.selector = parseSelector(var1);
      this.objective = var2;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public EntitySelector getSelector() {
      return this.selector;
   }

   public String getObjective() {
      return this.objective;
   }

   private String findTargetName(CommandSourceStack var1) throws CommandSyntaxException {
      if (this.selector != null) {
         List var2 = this.selector.findEntities(var1);
         if (!var2.isEmpty()) {
            if (var2.size() != 1) {
               throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            }

            return ((Entity)var2.get(0)).getScoreboardName();
         }
      }

      return this.name;
   }

   private String getScore(String var1, CommandSourceStack var2) {
      MinecraftServer var3 = var2.getServer();
      if (var3 != null) {
         ServerScoreboard var4 = var3.getScoreboard();
         Objective var5 = var4.getObjective(this.objective);
         if (var5 != null && var4.hasPlayerScore(var1, var5)) {
            Score var6 = var4.getOrCreatePlayerScore(var1, var5);
            return Integer.toString(var6.getScore());
         }
      }

      return "";
   }

   @Override
   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 == null) {
         return Component.empty();
      } else {
         String var4 = this.findTargetName(var1);
         String var5 = var2 != null && var4.equals("*") ? var2.getScoreboardName() : var4;
         return Component.literal(this.getScore(var5, var1));
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof ScoreContents var2 && this.name.equals(var2.name) && this.objective.equals(var2.objective)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.name.hashCode();
      return 31 * var1 + this.objective.hashCode();
   }

   @Override
   public String toString() {
      return "score{name='" + this.name + "', objective='" + this.objective + "'}";
   }
}
