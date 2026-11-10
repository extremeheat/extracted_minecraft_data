package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EntityAnchorArgument implements SerializableArgument.ContextFree<Anchor> {
   private static final EntityAnchorArgument INSTANCE = new EntityAnchorArgument();
   public static final SerializableArgument.Type<EntityAnchorArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType ERROR_INVALID;

   private EntityAnchorArgument() {
      super();
   }

   public SerializableArgument.Type<EntityAnchorArgument> type() {
      return TYPE;
   }

   public static Anchor getAnchor(final CommandContext<?> context, final String name) {
      return (Anchor)context.getArgument(name, Anchor.class);
   }

   public static ArgumentType<Anchor> anchor() {
      return INSTANCE.toBrigadier();
   }

   public Anchor parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      String name = reader.readUnquotedString();
      Anchor anchor = EntityAnchorArgument.Anchor.getByName(name);
      if (anchor == null) {
         reader.setCursor(start);
         throw ERROR_INVALID.createWithContext(reader, name);
      } else {
         return anchor;
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(EntityAnchorArgument.Anchor.BY_NAME.keySet(), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<EntityAnchorArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("eyes", "feet");
      ERROR_INVALID = new DynamicCommandExceptionType((name) -> Component.translatableEscape("argument.anchor.invalid", name));
   }

   public static enum Anchor {
      FEET("feet", (p, e) -> p),
      EYES("eyes", (p, e) -> new Vec3(p.x, p.y + (double)e.getEyeHeight(), p.z));

      private static final Map<String, Anchor> BY_NAME = (Map)Util.make(new HashMap(), (map) -> {
         for(Anchor anchor : values()) {
            map.put(anchor.name, anchor);
         }

      });
      private final String name;
      private final BiFunction<Vec3, Entity, Vec3> transform;

      private Anchor(final String name, final BiFunction<Vec3, Entity, Vec3> transform) {
         this.name = name;
         this.transform = transform;
      }

      public static @Nullable Anchor getByName(final String name) {
         return (Anchor)BY_NAME.get(name);
      }

      public Vec3 apply(final Entity entity) {
         return (Vec3)this.transform.apply(entity.position(), entity);
      }

      public Vec3 apply(final CommandSourceStack source) {
         Entity entity = source.getEntity();
         return entity == null ? source.getPosition() : (Vec3)this.transform.apply(source.getPosition(), entity);
      }

      // $FF: synthetic method
      private static Anchor[] $values() {
         return new Anchor[]{FEET, EYES};
      }
   }
}
