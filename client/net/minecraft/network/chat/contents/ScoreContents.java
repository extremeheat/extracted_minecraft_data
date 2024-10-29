package net.minecraft.network.chat.contents;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.SelectorPattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

public record ScoreContents(Either<SelectorPattern, String> name, String objective) implements ComponentContents {
   public static final MapCodec<ScoreContents> INNER_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.either(SelectorPattern.CODEC, Codec.STRING).fieldOf("name").forGetter(ScoreContents::name), Codec.STRING.fieldOf("objective").forGetter(ScoreContents::objective)).apply(var0, ScoreContents::new);
   });
   public static final MapCodec<ScoreContents> CODEC;
   public static final ComponentContents.Type<ScoreContents> TYPE;

   public ScoreContents(Either<SelectorPattern, String> var1, String var2) {
      super();
      this.name = var1;
      this.objective = var2;
   }

   public ComponentContents.Type<?> type() {
      return TYPE;
   }

   private ScoreHolder findTargetName(CommandSourceStack var1) throws CommandSyntaxException {
      Optional var2 = this.name.left();
      if (var2.isPresent()) {
         List var3 = ((SelectorPattern)var2.get()).resolved().findEntities(var1);
         if (!var3.isEmpty()) {
            if (var3.size() != 1) {
               throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            } else {
               return (ScoreHolder)var3.getFirst();
            }
         } else {
            return ScoreHolder.forNameOnly(((SelectorPattern)var2.get()).pattern());
         }
      } else {
         return ScoreHolder.forNameOnly((String)this.name.right().orElseThrow());
      }
   }

   private MutableComponent getScore(ScoreHolder var1, CommandSourceStack var2) {
      MinecraftServer var3 = var2.getServer();
      if (var3 != null) {
         ServerScoreboard var4 = var3.getScoreboard();
         Objective var5 = ((Scoreboard)var4).getObjective(this.objective);
         if (var5 != null) {
            ReadOnlyScoreInfo var6 = ((Scoreboard)var4).getPlayerScoreInfo(var1, var5);
            if (var6 != null) {
               return var6.formatValue(var5.numberFormatOrDefault(StyledFormat.NO_STYLE));
            }
         }
      }

      return Component.empty();
   }

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 == null) {
         return Component.empty();
      } else {
         ScoreHolder var4 = this.findTargetName(var1);
         Object var5 = var2 != null && var4.equals(ScoreHolder.WILDCARD) ? var2 : var4;
         return this.getScore((ScoreHolder)var5, var1);
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.name);
      return "score{name='" + var10000 + "', objective='" + this.objective + "'}";
   }

   public Either<SelectorPattern, String> name() {
      return this.name;
   }

   public String objective() {
      return this.objective;
   }

   static {
      CODEC = INNER_CODEC.fieldOf("score");
      TYPE = new ComponentContents.Type(CODEC, "score");
   }
}
