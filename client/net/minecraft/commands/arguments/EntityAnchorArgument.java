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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument implements ArgumentType<EntityAnchorArgument.Anchor> {
   private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("argument.anchor.invalid", new Object[]{var0});
   });

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
      return SharedSuggestionProvider.suggest((Iterable)EntityAnchorArgument.Anchor.BY_NAME.keySet(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static enum Anchor {
      FEET("feet", (var0, var1) -> {
         return var0;
      }),
      EYES("eyes", (var0, var1) -> {
         return new Vec3(var0.x, var0.y + (double)var1.getEyeHeight(), var0.z);
      });

      private static final Map<String, EntityAnchorArgument.Anchor> BY_NAME = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         EntityAnchorArgument.Anchor[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EntityAnchorArgument.Anchor var4 = var1[var3];
            var0.put(var4.name, var4);
         }

      });
      private final String name;
      private final BiFunction<Vec3, Entity, Vec3> transform;

      private Anchor(String var3, BiFunction<Vec3, Entity, Vec3> var4) {
         this.name = var3;
         this.transform = var4;
      }

      @Nullable
      public static EntityAnchorArgument.Anchor getByName(String var0) {
         return (EntityAnchorArgument.Anchor)BY_NAME.get(var0);
      }

      public Vec3 apply(Entity var1) {
         return (Vec3)this.transform.apply(var1.position(), var1);
      }

      public Vec3 apply(CommandSourceStack var1) {
         Entity var2 = var1.getEntity();
         return var2 == null ? var1.getPosition() : (Vec3)this.transform.apply(var1.getPosition(), var2);
      }
   }
}
