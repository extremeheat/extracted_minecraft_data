package net.minecraft.command.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityArgument implements ArgumentType<EntitySelector> {
   private static final Collection<String> field_201310_f = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
   public static final SimpleCommandExceptionType field_197098_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.toomany", new Object[0]));
   public static final SimpleCommandExceptionType field_197099_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.player.toomany", new Object[0]));
   public static final SimpleCommandExceptionType field_197100_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.player.entities", new Object[0]));
   public static final SimpleCommandExceptionType field_197101_d = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.notfound.entity", new Object[0]));
   public static final SimpleCommandExceptionType field_197102_e = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.notfound.player", new Object[0]));
   public static final SimpleCommandExceptionType field_210323_f = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.selector.not_allowed", new Object[0]));
   private final boolean field_197103_f;
   private final boolean field_197104_g;

   protected EntityArgument(boolean var1, boolean var2) {
      super();
      this.field_197103_f = var1;
      this.field_197104_g = var2;
   }

   public static EntityArgument func_197086_a() {
      return new EntityArgument(true, false);
   }

   public static Entity func_197088_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).func_197340_a((CommandSource)var0.getSource());
   }

   public static EntityArgument func_197093_b() {
      return new EntityArgument(false, false);
   }

   public static Collection<? extends Entity> func_197097_b(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      Collection var2 = func_197087_c(var0, var1);
      if (var2.isEmpty()) {
         throw field_197101_d.create();
      } else {
         return var2;
      }
   }

   public static Collection<? extends Entity> func_197087_c(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).func_197341_b((CommandSource)var0.getSource());
   }

   public static Collection<EntityPlayerMP> func_201309_d(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).func_197342_d((CommandSource)var0.getSource());
   }

   public static EntityArgument func_197096_c() {
      return new EntityArgument(true, true);
   }

   public static EntityPlayerMP func_197089_d(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).func_197347_c((CommandSource)var0.getSource());
   }

   public static EntityArgument func_197094_d() {
      return new EntityArgument(false, true);
   }

   public static Collection<EntityPlayerMP> func_197090_e(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      List var2 = ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).func_197342_d((CommandSource)var0.getSource());
      if (var2.isEmpty()) {
         throw field_197102_e.create();
      } else {
         return var2;
      }
   }

   public EntitySelector parse(StringReader var1) throws CommandSyntaxException {
      boolean var2 = false;
      EntitySelectorParser var3 = new EntitySelectorParser(var1);
      EntitySelector var4 = var3.func_201345_m();
      if (var4.func_197346_a() > 1 && this.field_197103_f) {
         if (this.field_197104_g) {
            var1.setCursor(0);
            throw field_197099_b.createWithContext(var1);
         } else {
            var1.setCursor(0);
            throw field_197098_a.createWithContext(var1);
         }
      } else if (var4.func_197351_b() && this.field_197104_g && !var4.func_197352_c()) {
         var1.setCursor(0);
         throw field_197100_c.createWithContext(var1);
      } else {
         return var4;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (var1.getSource() instanceof ISuggestionProvider) {
         StringReader var3 = new StringReader(var2.getInput());
         var3.setCursor(var2.getStart());
         ISuggestionProvider var4 = (ISuggestionProvider)var1.getSource();
         EntitySelectorParser var5 = new EntitySelectorParser(var3, var4.func_197034_c(2));

         try {
            var5.func_201345_m();
         } catch (CommandSyntaxException var7) {
         }

         return var5.func_201993_a(var2, (var2x) -> {
            Collection var3 = var4.func_197011_j();
            Object var4x = this.field_197104_g ? var3 : Iterables.concat(var3, var4.func_211270_p());
            ISuggestionProvider.func_197005_b((Iterable)var4x, var2x);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return field_201310_f;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Serializer implements IArgumentSerializer<EntityArgument> {
      public Serializer() {
         super();
      }

      public void func_197072_a(EntityArgument var1, PacketBuffer var2) {
         byte var3 = 0;
         if (var1.field_197103_f) {
            var3 = (byte)(var3 | 1);
         }

         if (var1.field_197104_g) {
            var3 = (byte)(var3 | 2);
         }

         var2.writeByte(var3);
      }

      public EntityArgument func_197071_b(PacketBuffer var1) {
         byte var2 = var1.readByte();
         return new EntityArgument((var2 & 1) != 0, (var2 & 2) != 0);
      }

      public void func_212244_a(EntityArgument var1, JsonObject var2) {
         var2.addProperty("amount", var1.field_197103_f ? "single" : "multiple");
         var2.addProperty("type", var1.field_197104_g ? "players" : "entities");
      }

      // $FF: synthetic method
      public ArgumentType func_197071_b(PacketBuffer var1) {
         return this.func_197071_b(var1);
      }
   }
}
