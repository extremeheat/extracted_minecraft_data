package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.IResult> {
   private static final Collection<String> field_201340_a = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
   private static final DynamicCommandExceptionType field_199849_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.item.tag.unknown", new Object[]{var0});
   });

   public ItemPredicateArgument() {
      super();
   }

   public static ItemPredicateArgument func_199846_a() {
      return new ItemPredicateArgument();
   }

   public ItemPredicateArgument.IResult parse(StringReader var1) throws CommandSyntaxException {
      ItemParser var2 = (new ItemParser(var1, true)).func_197327_f();
      if (var2.func_197326_b() != null) {
         ItemPredicateArgument.ItemPredicate var4 = new ItemPredicateArgument.ItemPredicate(var2.func_197326_b(), var2.func_197325_c());
         return (var1x) -> {
            return var4;
         };
      } else {
         ResourceLocation var3 = var2.func_199835_d();
         return (var2x) -> {
            Tag var3x = ((CommandSource)var2x.getSource()).func_197028_i().func_199731_aO().func_199715_b().func_199910_a(var3);
            if (var3x == null) {
               throw field_199849_a.create(var3.toString());
            } else {
               return new ItemPredicateArgument.TagPredicate(var3x, var2.func_197325_c());
            }
         };
      }
   }

   public static Predicate<ItemStack> func_199847_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((ItemPredicateArgument.IResult)var0.getArgument(var1, ItemPredicateArgument.IResult.class)).create(var0);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getInput());
      var3.setCursor(var2.getStart());
      ItemParser var4 = new ItemParser(var3, true);

      try {
         var4.func_197327_f();
      } catch (CommandSyntaxException var6) {
      }

      return var4.func_197329_a(var2);
   }

   public Collection<String> getExamples() {
      return field_201340_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static class TagPredicate implements Predicate<ItemStack> {
      private final Tag<Item> field_199843_a;
      @Nullable
      private final NBTTagCompound field_199844_b;

      public TagPredicate(Tag<Item> var1, @Nullable NBTTagCompound var2) {
         super();
         this.field_199843_a = var1;
         this.field_199844_b = var2;
      }

      public boolean test(ItemStack var1) {
         return this.field_199843_a.func_199685_a_(var1.func_77973_b()) && NBTUtil.func_181123_a(this.field_199844_b, var1.func_77978_p(), true);
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((ItemStack)var1);
      }
   }

   static class ItemPredicate implements Predicate<ItemStack> {
      private final Item field_199841_a;
      @Nullable
      private final NBTTagCompound field_199842_b;

      public ItemPredicate(Item var1, @Nullable NBTTagCompound var2) {
         super();
         this.field_199841_a = var1;
         this.field_199842_b = var2;
      }

      public boolean test(ItemStack var1) {
         return var1.func_77973_b() == this.field_199841_a && NBTUtil.func_181123_a(this.field_199842_b, var1.func_77978_p(), true);
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((ItemStack)var1);
      }
   }

   public interface IResult {
      Predicate<ItemStack> create(CommandContext<CommandSource> var1) throws CommandSyntaxException;
   }
}
