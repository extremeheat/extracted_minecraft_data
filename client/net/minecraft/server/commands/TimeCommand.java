package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.FloatArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.IntegerArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.timeline.Timeline;

public class TimeCommand {
   private static final DynamicCommandExceptionType ERROR_NO_DEFAULT_CLOCK = new DynamicCommandExceptionType((dimension) -> Component.translatableEscape("commands.time.no_default_clock", dimension));
   private static final Dynamic2CommandExceptionType ERROR_NO_TIME_MARKER_FOUND = new Dynamic2CommandExceptionType((clock, timeMarker) -> Component.translatableEscape("commands.time.no_time_marker_found", timeMarker, clock));
   private static final Dynamic2CommandExceptionType ERROR_WRONG_TIMELINE_FOR_CLOCK = new Dynamic2CommandExceptionType((clock, timeline) -> Component.translatableEscape("commands.time.wrong_timeline_for_clock", timeline, clock));
   private static final Dynamic2CommandExceptionType ERROR_ALREADY_AT_TIME_MARKER = new Dynamic2CommandExceptionType((clock, marker) -> Component.translatableEscape("commands.time.set.already_at_time_marker", clock, marker));
   private static final Dynamic2CommandExceptionType ERROR_ALREADY_AT_TIME = new Dynamic2CommandExceptionType((clock, time) -> Component.translatableEscape("commands.time.set.already_at_time", clock, time));
   private static final DynamicCommandExceptionType ERROR_ALREADY_PAUSED = new DynamicCommandExceptionType((clock) -> Component.translatableEscape("commands.time.pause.already_paused", clock));
   private static final DynamicCommandExceptionType ERROR_ALREADY_RUNNING = new DynamicCommandExceptionType((clock) -> Component.translatableEscape("commands.time.pause.already_running", clock));
   private static final Dynamic2CommandExceptionType ERROR_ALREADY_SAME_RATE = new Dynamic2CommandExceptionType((clock, rate) -> Component.translatableEscape("commands.time.rate.already_same", clock, rate));
   private static final int MAX_CLOCK_RATE = 1000;

   public TimeCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> baseCommand = (LiteralArgumentBuilder)Commands.literal("time").requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_GAMEMASTERS));
      dispatcher.register((LiteralArgumentBuilder)addClockNodes(baseCommand, TimeCommand.ClockGetter.FOR_LEVEL_DEFAULT));
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)baseCommand.then(Commands.literal("query").then(Commands.literal("gametime").executes((c) -> queryGameTime((CommandSourceStack)c.getSource()))))).then(Commands.literal("of").then(addClockNodes(Commands.argument("clock", ResourceArgument.resource(Registries.WORLD_CLOCK)), TimeCommand.ClockGetter.fromContext("clock")))));
   }

   private static <A extends ArgumentBuilder<CommandSourceStack, A>> A addClockNodes(final A node, final ClockGetter clockGetter) {
      return (A)node.then(((LiteralArgumentBuilder)Commands.literal("set").then(Commands.argument("time", TimeArgument.time()).executes((c) -> setTotalTicks((CommandSourceStack)c.getSource(), clockGetter.getClock(c), IntegerArgument.getInteger(c, "time"))))).then(Commands.argument("timemarker", IdentifierArgument.id()).suggests(suggestTimeMarker(clockGetter)).executes((c) -> setTimeToTimeMarker((CommandSourceStack)c.getSource(), clockGetter.getClock(c), ResourceKey.create(ClockTimeMarkers.ROOT_ID, IdentifierArgument.getId(c, "timemarker")))))).then(Commands.literal("add").then(Commands.argument("time", TimeArgument.time(-2147483648)).executes((c) -> addTime((CommandSourceStack)c.getSource(), clockGetter.getClock(c), IntegerArgument.getInteger(c, "time"))))).then(Commands.literal("pause").executes((c) -> setPaused((CommandSourceStack)c.getSource(), clockGetter.getClock(c), true))).then(Commands.literal("resume").executes((c) -> setPaused((CommandSourceStack)c.getSource(), clockGetter.getClock(c), false))).then(Commands.literal("rate").then(Commands.argument("rate", FloatArgument.floatArg(1.0E-5F, 1000.0F)).executes((c) -> setRate((CommandSourceStack)c.getSource(), clockGetter.getClock(c), FloatArgument.getFloat(c, "rate"))))).then(((LiteralArgumentBuilder)Commands.literal("query").then(Commands.literal("time").executes((c) -> queryTime((CommandSourceStack)c.getSource(), clockGetter.getClock(c))))).then(((RequiredArgumentBuilder)Commands.argument("timeline", ResourceArgument.resource(Registries.TIMELINE)).suggests(suggestTimelines(clockGetter)).executes((c) -> queryTimelineTicks((CommandSourceStack)c.getSource(), clockGetter.getClock(c), ResourceArgument.getTimeline(c, "timeline")))).then(Commands.literal("repetition").executes((c) -> queryTimelineRepetitions((CommandSourceStack)c.getSource(), clockGetter.getClock(c), ResourceArgument.getTimeline(c, "timeline"))))));
   }

   private static SuggestionProvider<CommandSourceStack> suggestTimeMarker(final ClockGetter clockGetter) {
      return SuggestionProviders.serverOnly((server, context, builder) -> {
         Holder<WorldClock> clock = clockGetter.getClockForSuggestions(context);
         return SuggestionContext.suggestResource(server.clockManager().commandTimeMarkersForClock(clock).map(ResourceKey::identifier), builder);
      });
   }

   private static SuggestionProvider<CommandSourceStack> suggestTimelines(final ClockGetter clockGetter) {
      return SuggestionProviders.serverOnly((server, context, builder) -> {
         Holder<WorldClock> clock = clockGetter.getClockForSuggestions(context);
         Stream<ResourceKey<Timeline>> timelines = server.registryAccess().lookupOrThrow(Registries.TIMELINE).listElements().filter((timeline) -> ((Timeline)timeline.value()).clock().equals(clock)).map(Holder.Reference::key);
         return SuggestionContext.suggestResource(timelines.map(ResourceKey::identifier), builder);
      });
   }

   private static int queryGameTime(final CommandSourceStack source) {
      long gameTime = source.getLevel().getGameTime();
      source.sendSuccess(() -> Component.translatable("commands.time.query.gametime", gameTime), false);
      return wrapTime(gameTime);
   }

   private static int queryTime(final CommandSourceStack source, final Holder<WorldClock> clock) {
      ServerClockManager clockManager = source.getServer().clockManager();
      long totalTicks = clockManager.getInstance(clock).totalTicks();
      source.sendSuccess(() -> Component.translatable("commands.time.query.absolute", clock.getRegisteredName(), totalTicks), false);
      return wrapTime(totalTicks);
   }

   private static int queryTimelineTicks(final CommandSourceStack source, final Holder<WorldClock> clock, final Holder<Timeline> timeline) throws CommandSyntaxException {
      if (!clock.equals((timeline.value()).clock())) {
         throw ERROR_WRONG_TIMELINE_FOR_CLOCK.create(clock.getRegisteredName(), timeline.getRegisteredName());
      } else {
         ServerClockManager clockManager = source.getServer().clockManager();
         long currentTicks = ((Timeline)timeline.value()).getCurrentTicks(clockManager);
         source.sendSuccess(() -> Component.translatable("commands.time.query.timeline", timeline.getRegisteredName(), currentTicks), false);
         return wrapTime(currentTicks);
      }
   }

   private static int queryTimelineRepetitions(final CommandSourceStack source, final Holder<WorldClock> clock, final Holder<Timeline> timeline) throws CommandSyntaxException {
      if (!clock.equals((timeline.value()).clock())) {
         throw ERROR_WRONG_TIMELINE_FOR_CLOCK.create(clock.getRegisteredName(), timeline.getRegisteredName());
      } else {
         ServerClockManager clockManager = source.getServer().clockManager();
         long repetitions = (long)((Timeline)timeline.value()).getPeriodCount(clockManager);
         source.sendSuccess(() -> Component.translatable("commands.time.query.timeline.repetitions", timeline.getRegisteredName(), repetitions), false);
         return wrapTime(repetitions);
      }
   }

   private static int setTotalTicks(final CommandSourceStack source, final Holder<WorldClock> clock, final int totalTicks) throws CommandSyntaxException {
      ServerClockManager clockManager = source.getServer().clockManager();
      if (clockManager.getInstance(clock).totalTicks() == (long)totalTicks) {
         throw ERROR_ALREADY_AT_TIME.create(clock.getRegisteredName(), totalTicks);
      } else {
         clockManager.setTotalTicks(clock, (long)totalTicks);
         source.sendSuccess(() -> Component.translatable("commands.time.set.absolute", clock.getRegisteredName(), totalTicks), true);
         return totalTicks;
      }
   }

   private static int addTime(final CommandSourceStack source, final Holder<WorldClock> clock, final int time) {
      ServerClockManager clockManager = source.getServer().clockManager();
      clockManager.addTicks(clock, time);
      long totalTicks = clockManager.getInstance(clock).totalTicks();
      source.sendSuccess(() -> Component.translatable("commands.time.set.absolute", clock.getRegisteredName(), totalTicks), true);
      return wrapTime(totalTicks);
   }

   private static int setTimeToTimeMarker(final CommandSourceStack source, final Holder<WorldClock> clock, final ResourceKey<ClockTimeMarker> timeMarkerId) throws CommandSyntaxException {
      ServerClockManager clockManager = source.getServer().clockManager();
      ServerClockManager.MoveResult moveResult = clockManager.moveToTimeMarker(clock, timeMarkerId);
      String clockName = clock.getRegisteredName();
      String timeMarkerName = timeMarkerId.identifier().toString();
      switch (moveResult) {
         case NO_TIME_MARKER_FOUND:
            throw ERROR_NO_TIME_MARKER_FOUND.create(clockName, timeMarkerName);
         case NOT_MOVED:
            throw ERROR_ALREADY_AT_TIME_MARKER.create(clockName, timeMarkerName);
         case MOVED:
            source.sendSuccess(() -> Component.translatable("commands.time.set.time_marker", clockName, timeMarkerName), true);
         default:
            return wrapTime(clockManager.getInstance(clock).totalTicks());
      }
   }

   private static int setPaused(final CommandSourceStack source, final Holder<WorldClock> clock, final boolean paused) throws CommandSyntaxException {
      ServerClockManager clockManager = source.getServer().clockManager();
      String clockName = clock.getRegisteredName();
      if (clockManager.getInstance(clock).isPaused() == paused) {
         if (paused) {
            throw ERROR_ALREADY_PAUSED.create(clockName);
         } else {
            throw ERROR_ALREADY_RUNNING.create(clockName);
         }
      } else {
         clockManager.setPaused(clock, paused);
         source.sendSuccess(() -> Component.translatable(paused ? "commands.time.pause" : "commands.time.resume", clockName), true);
         return 1;
      }
   }

   private static int setRate(final CommandSourceStack source, final Holder<WorldClock> clock, final float rate) throws CommandSyntaxException {
      ServerClockManager clockManager = source.getServer().clockManager();
      if (clockManager.getInstance(clock).rate() == rate) {
         throw ERROR_ALREADY_SAME_RATE.create(clock.getRegisteredName(), rate);
      } else {
         clockManager.setRate(clock, rate);
         source.sendSuccess(() -> Component.translatable("commands.time.rate", clock.getRegisteredName(), rate), true);
         return 1;
      }
   }

   private static int wrapTime(final long ticks) {
      return Math.toIntExact(ticks % 2147483647L);
   }

   private interface ClockGetter {
      ClockGetter FOR_LEVEL_DEFAULT = new ClockGetter() {
         private static Holder<WorldClock> getDefaultClock(final Optional<? extends Level> serverLevel) throws CommandSyntaxException {
            Optional<Holder<DimensionType>> dimension = serverLevel.map(Level::dimensionTypeRegistration);
            return (Holder)dimension.flatMap((dim) -> ((DimensionType)dim.value()).defaultClock()).orElseThrow(() -> TimeCommand.ERROR_NO_DEFAULT_CLOCK.create(dimension.map(Holder::getRegisteredName).orElse("<null>")));
         }

         public Holder<WorldClock> getClockForSuggestions(final CommandContext<? extends SuggestionContext> context) throws CommandSyntaxException {
            return getDefaultClock(Optional.ofNullable(((SuggestionContext)context.getSource()).level()));
         }

         public Holder<WorldClock> getClock(final CommandContext<? extends CommandSourceStack> context) throws CommandSyntaxException {
            return getDefaultClock(Optional.of(((CommandSourceStack)context.getSource()).getLevel()));
         }
      };

      static ClockGetter fromContext(final String name) {
         return new ClockGetter() {
            private Holder.Reference<WorldClock> getClockFromContext(final CommandContext<?> context) throws CommandSyntaxException {
               return ResourceArgument.getClock(context, name);
            }

            public Holder<WorldClock> getClockForSuggestions(final CommandContext<? extends SuggestionContext> context) throws CommandSyntaxException {
               return this.getClockFromContext(context);
            }

            public Holder<WorldClock> getClock(final CommandContext<? extends CommandSourceStack> context) throws CommandSyntaxException {
               return this.getClockFromContext(context);
            }
         };
      }

      Holder<WorldClock> getClockForSuggestions(CommandContext<? extends SuggestionContext> context) throws CommandSyntaxException;

      Holder<WorldClock> getClock(CommandContext<? extends CommandSourceStack> context) throws CommandSyntaxException;
   }
}
