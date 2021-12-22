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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

public class ScoreComponent extends BaseComponent implements ContextAwareComponent {
   private static final String SCORER_PLACEHOLDER = "*";
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;

   @Nullable
   private static EntitySelector parseSelector(String var0) {
      try {
         return (new EntitySelectorParser(new StringReader(var0))).parse();
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public ScoreComponent(String var1, String var2) {
      this(var1, parseSelector(var1), var2);
   }

   private ScoreComponent(String var1, @Nullable EntitySelector var2, String var3) {
      super();
      this.name = var1;
      this.selector = var2;
      this.objective = var3;
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
         if (var4.hasPlayerScore(var1, var5)) {
            Score var6 = var4.getOrCreatePlayerScore(var1, var5);
            return Integer.toString(var6.getScore());
         }
      }

      return "";
   }

   public ScoreComponent plainCopy() {
      return new ScoreComponent(this.name, this.selector, this.objective);
   }

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 == null) {
         return new TextComponent("");
      } else {
         String var4 = this.findTargetName(var1);
         String var5 = var2 != null && var4.equals("*") ? var2.getScoreboardName() : var4;
         return new TextComponent(this.getScore(var5, var1));
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
      String var10000 = this.name;
      return "ScoreComponent{name='" + var10000 + "'objective='" + this.objective + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }

   // $FF: synthetic method
   public BaseComponent plainCopy() {
      return this.plainCopy();
   }

   // $FF: synthetic method
   public MutableComponent plainCopy() {
      return this.plainCopy();
   }
}
