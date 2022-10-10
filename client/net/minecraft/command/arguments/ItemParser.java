package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.IProperty;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class ItemParser {
   public static final SimpleCommandExceptionType field_199838_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.item.tag.disallowed", new Object[0]));
   public static final DynamicCommandExceptionType field_197333_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.item.id.invalid", new Object[]{var0});
   });
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_197334_b = SuggestionsBuilder::buildFuture;
   private final StringReader field_197335_c;
   private final boolean field_199839_e;
   private final Map<IProperty<?>, Comparable<?>> field_197336_d = Maps.newHashMap();
   private Item field_197337_e;
   @Nullable
   private NBTTagCompound field_197338_f;
   private ResourceLocation field_199840_f = new ResourceLocation("");
   private int field_201956_j;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_197339_g;

   public ItemParser(StringReader var1, boolean var2) {
      super();
      this.field_197339_g = field_197334_b;
      this.field_197335_c = var1;
      this.field_199839_e = var2;
   }

   public Item func_197326_b() {
      return this.field_197337_e;
   }

   @Nullable
   public NBTTagCompound func_197325_c() {
      return this.field_197338_f;
   }

   public ResourceLocation func_199835_d() {
      return this.field_199840_f;
   }

   public void func_197332_d() throws CommandSyntaxException {
      int var1 = this.field_197335_c.getCursor();
      ResourceLocation var2 = ResourceLocation.func_195826_a(this.field_197335_c);
      if (IRegistry.field_212630_s.func_212607_c(var2)) {
         this.field_197337_e = (Item)IRegistry.field_212630_s.func_212608_b(var2);
      } else {
         this.field_197335_c.setCursor(var1);
         throw field_197333_a.createWithContext(this.field_197335_c, var2.toString());
      }
   }

   public void func_199834_f() throws CommandSyntaxException {
      if (!this.field_199839_e) {
         throw field_199838_a.create();
      } else {
         this.field_197339_g = this::func_201955_c;
         this.field_197335_c.expect('#');
         this.field_201956_j = this.field_197335_c.getCursor();
         this.field_199840_f = ResourceLocation.func_195826_a(this.field_197335_c);
      }
   }

   public void func_197330_e() throws CommandSyntaxException {
      this.field_197338_f = (new JsonToNBT(this.field_197335_c)).func_193593_f();
   }

   public ItemParser func_197327_f() throws CommandSyntaxException {
      this.field_197339_g = this::func_197331_c;
      if (this.field_197335_c.canRead() && this.field_197335_c.peek() == '#') {
         this.func_199834_f();
      } else {
         this.func_197332_d();
         this.field_197339_g = this::func_197328_b;
      }

      if (this.field_197335_c.canRead() && this.field_197335_c.peek() == '{') {
         this.field_197339_g = field_197334_b;
         this.func_197330_e();
      }

      return this;
   }

   private CompletableFuture<Suggestions> func_197328_b(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf('{'));
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201955_c(SuggestionsBuilder var1) {
      return ISuggestionProvider.func_197014_a(ItemTags.func_199903_a().func_199908_a(), var1.createOffset(this.field_201956_j));
   }

   private CompletableFuture<Suggestions> func_197331_c(SuggestionsBuilder var1) {
      if (this.field_199839_e) {
         ISuggestionProvider.func_197006_a(ItemTags.func_199903_a().func_199908_a(), var1, String.valueOf('#'));
      }

      return ISuggestionProvider.func_197014_a(IRegistry.field_212630_s.func_148742_b(), var1);
   }

   public CompletableFuture<Suggestions> func_197329_a(SuggestionsBuilder var1) {
      return (CompletableFuture)this.field_197339_g.apply(var1.createOffset(this.field_197335_c.getCursor()));
   }
}
