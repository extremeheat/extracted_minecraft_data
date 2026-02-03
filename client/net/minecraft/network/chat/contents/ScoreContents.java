package net.minecraft.network.chat.contents;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.SelectorPattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.jspecify.annotations.Nullable;

public record ScoreContents(Either<SelectorPattern, String> name, String objective) implements ComponentContents {
   public static final MapCodec<ScoreContents> INNER_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.either(SelectorPattern.CODEC, Codec.STRING).fieldOf("name").forGetter(ScoreContents::name), Codec.STRING.fieldOf("objective").forGetter(ScoreContents::objective)).apply(i, ScoreContents::new));
   public static final MapCodec<ScoreContents> MAP_CODEC;

   public ScoreContents {
      super();
   }

   public MapCodec<ScoreContents> codec() {
      return MAP_CODEC;
   }

   private ScoreHolder findTargetName(final CommandSourceStack source) throws CommandSyntaxException {
      Optional<SelectorPattern> selector = this.name.left();
      if (selector.isPresent()) {
         List<? extends Entity> entities = ((SelectorPattern)selector.get()).resolved().findEntities(source);
         if (!entities.isEmpty()) {
            if (entities.size() != 1) {
               throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            } else {
               return (ScoreHolder)entities.getFirst();
            }
         } else {
            return ScoreHolder.forNameOnly(((SelectorPattern)selector.get()).pattern());
         }
      } else {
         return ScoreHolder.forNameOnly((String)this.name.right().orElseThrow());
      }
   }

   private MutableComponent getScore(final ScoreHolder name, final CommandSourceStack source) {
      MinecraftServer server = source.getServer();
      if (server != null) {
         Scoreboard scoreboard = server.getScoreboard();
         Objective objective = scoreboard.getObjective(this.objective);
         if (objective != null) {
            ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(name, objective);
            if (scoreInfo != null) {
               return scoreInfo.formatValue(objective.numberFormatOrDefault(StyledFormat.NO_STYLE));
            }
         }
      }

      return Component.empty();
   }

   public MutableComponent resolve(final @Nullable CommandSourceStack source, final @Nullable Entity entity, final int recursionDepth) throws CommandSyntaxException {
      if (source == null) {
         return Component.empty();
      } else {
         ScoreHolder scoreHolder = this.findTargetName(source);
         ScoreHolder scoreName = (ScoreHolder)(entity != null && scoreHolder.equals(ScoreHolder.WILDCARD) ? entity : scoreHolder);
         return this.getScore(scoreName, source);
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.name);
      return "score{name='" + var10000 + "', objective='" + this.objective + "'}";
   }

   static {
      MAP_CODEC = INNER_CODEC.fieldOf("score");
   }
}
