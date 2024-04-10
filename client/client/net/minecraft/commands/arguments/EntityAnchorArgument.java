package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument implements ArgumentType<EntityAnchorArgument.Anchor> {
   private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("argument.anchor.invalid", var0)
   );

   public EntityAnchorArgument() {
      super();
   }

   public static EntityAnchorArgument.Anchor getAnchor(CommandContext<CommandSourceStack> var0, String var1) {
      return (EntityAnchorArgument.Anchor)var0.getArgument(var1, EntityAnchorArgument.Anchor.class);
   }

   public static EntityAnchorArgument anchor() {
      return new EntityAnchorArgument();
   }

   public EntityAnchorArgument.Anchor parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      String var3 = var1.readUnquotedString();
      EntityAnchorArgument.Anchor var4 = EntityAnchorArgument.Anchor.getByName(var3);
      if (var4 == null) {
         var1.setCursor(var2);
         throw ERROR_INVALID.createWithContext(var1, var3);
      } else {
         return var4;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(EntityAnchorArgument.Anchor.BY_NAME.keySet(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static enum Anchor {
      FEET("feet", (var0, var1) -> var0),
      EYES("eyes", (var0, var1) -> new Vec3(var0.x, var0.y + (double)var1.getEyeHeight(), var0.z));

      static final Map<String, EntityAnchorArgument.Anchor> BY_NAME = Util.make(Maps.newHashMap(), var0 -> {
         for (EntityAnchorArgument.Anchor var4 : values()) {
            var0.put(var4.name, var4);
         }
      });
      private final String name;
      private final BiFunction<Vec3, Entity, Vec3> transform;

      private Anchor(final String param3, final BiFunction<Vec3, Entity, Vec3> param4) {
         this.name = nullxx;
         this.transform = nullxxx;
      }

      @Nullable
      public static EntityAnchorArgument.Anchor getByName(String var0) {
         return BY_NAME.get(var0);
      }

      public Vec3 apply(Entity var1) {
         return this.transform.apply(var1.position(), var1);
      }

      public Vec3 apply(CommandSourceStack var1) {
         Entity var2 = var1.getEntity();
         return var2 == null ? var1.getPosition() : this.transform.apply(var1.getPosition(), var2);
      }
   }
}
