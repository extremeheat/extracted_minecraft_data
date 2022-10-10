package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class BlockStateParser {
   public static final SimpleCommandExceptionType field_199831_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.block.tag.disallowed", new Object[0]));
   public static final DynamicCommandExceptionType field_197259_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.block.id.invalid", new Object[]{var0});
   });
   public static final Dynamic2CommandExceptionType field_197260_b = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.block.property.unknown", new Object[]{var0, var1});
   });
   public static final Dynamic2CommandExceptionType field_197261_c = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.block.property.duplicate", new Object[]{var1, var0});
   });
   public static final Dynamic3CommandExceptionType field_197262_d = new Dynamic3CommandExceptionType((var0, var1, var2) -> {
      return new TextComponentTranslation("argument.block.property.invalid", new Object[]{var0, var2, var1});
   });
   public static final Dynamic2CommandExceptionType field_197263_e = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.block.property.novalue", new Object[]{var0, var1});
   });
   public static final SimpleCommandExceptionType field_197264_f = new SimpleCommandExceptionType(new TextComponentTranslation("argument.block.property.unclosed", new Object[0]));
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_197265_g = SuggestionsBuilder::buildFuture;
   private final StringReader field_197266_h;
   private final boolean field_199832_j;
   private final Map<IProperty<?>, Comparable<?>> field_197267_i = Maps.newHashMap();
   private final Map<String, String> field_200141_l = Maps.newHashMap();
   private ResourceLocation field_197268_j = new ResourceLocation("");
   private StateContainer<Block, IBlockState> field_197269_k;
   private IBlockState field_197270_l;
   @Nullable
   private NBTTagCompound field_197271_m;
   private ResourceLocation field_199833_p = new ResourceLocation("");
   private int field_201954_r;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_197272_n;

   public BlockStateParser(StringReader var1, boolean var2) {
      super();
      this.field_197272_n = field_197265_g;
      this.field_197266_h = var1;
      this.field_199832_j = var2;
   }

   public Map<IProperty<?>, Comparable<?>> func_197254_a() {
      return this.field_197267_i;
   }

   @Nullable
   public IBlockState func_197249_b() {
      return this.field_197270_l;
   }

   @Nullable
   public NBTTagCompound func_197241_c() {
      return this.field_197271_m;
   }

   @Nullable
   public ResourceLocation func_199829_d() {
      return this.field_199833_p;
   }

   public BlockStateParser func_197243_a(boolean var1) throws CommandSyntaxException {
      this.field_197272_n = this::func_197250_h;
      if (this.field_197266_h.canRead() && this.field_197266_h.peek() == '#') {
         this.func_199827_f();
         this.field_197272_n = this::func_212599_i;
         if (this.field_197266_h.canRead() && this.field_197266_h.peek() == '[') {
            this.func_200137_h();
            this.field_197272_n = this::func_197244_d;
         }
      } else {
         this.func_197258_d();
         this.field_197272_n = this::func_197255_g;
         if (this.field_197266_h.canRead() && this.field_197266_h.peek() == '[') {
            this.func_197257_e();
            this.field_197272_n = this::func_197244_d;
         }
      }

      if (var1 && this.field_197266_h.canRead() && this.field_197266_h.peek() == '{') {
         this.field_197272_n = field_197265_g;
         this.func_197240_f();
      }

      return this;
   }

   private CompletableFuture<Suggestions> func_197252_b(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf(']'));
      }

      return this.func_197256_c(var1);
   }

   private CompletableFuture<Suggestions> func_200136_c(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf(']'));
      }

      return this.func_200134_e(var1);
   }

   private CompletableFuture<Suggestions> func_197256_c(SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = this.field_197270_l.func_206869_a().iterator();

      while(var3.hasNext()) {
         IProperty var4 = (IProperty)var3.next();
         if (!this.field_197267_i.containsKey(var4) && var4.func_177701_a().startsWith(var2)) {
            var1.suggest(var4.func_177701_a() + '=');
         }
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_200134_e(SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      if (this.field_199833_p != null && !this.field_199833_p.func_110623_a().isEmpty()) {
         Tag var3 = BlockTags.func_199896_a().func_199910_a(this.field_199833_p);
         if (var3 != null) {
            Iterator var4 = var3.func_199885_a().iterator();

            while(var4.hasNext()) {
               Block var5 = (Block)var4.next();
               Iterator var6 = var5.func_176194_O().func_177623_d().iterator();

               while(var6.hasNext()) {
                  IProperty var7 = (IProperty)var6.next();
                  if (!this.field_200141_l.containsKey(var7.func_177701_a()) && var7.func_177701_a().startsWith(var2)) {
                     var1.suggest(var7.func_177701_a() + '=');
                  }
               }
            }
         }
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_197244_d(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty() && this.func_212598_k()) {
         var1.suggest(String.valueOf('{'));
      }

      return var1.buildFuture();
   }

   private boolean func_212598_k() {
      if (this.field_197270_l != null) {
         return this.field_197270_l.func_177230_c().func_149716_u();
      } else {
         if (this.field_199833_p != null) {
            Tag var1 = BlockTags.func_199896_a().func_199910_a(this.field_199833_p);
            if (var1 != null) {
               Iterator var2 = var1.func_199885_a().iterator();

               while(var2.hasNext()) {
                  Block var3 = (Block)var2.next();
                  if (var3.func_149716_u()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private CompletableFuture<Suggestions> func_197246_e(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf('='));
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_197248_f(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf(']'));
      }

      if (var1.getRemaining().isEmpty() && this.field_197267_i.size() < this.field_197270_l.func_206869_a().size()) {
         var1.suggest(String.valueOf(','));
      }

      return var1.buildFuture();
   }

   private static <T extends Comparable<T>> SuggestionsBuilder func_201037_a(SuggestionsBuilder var0, IProperty<T> var1) {
      Iterator var2 = var1.func_177700_c().iterator();

      while(var2.hasNext()) {
         Comparable var3 = (Comparable)var2.next();
         if (var3 instanceof Integer) {
            var0.suggest((Integer)var3);
         } else {
            var0.suggest(var1.func_177702_a(var3));
         }
      }

      return var0;
   }

   private CompletableFuture<Suggestions> func_200140_a(SuggestionsBuilder var1, String var2) {
      boolean var3 = false;
      if (this.field_199833_p != null && !this.field_199833_p.func_110623_a().isEmpty()) {
         Tag var4 = BlockTags.func_199896_a().func_199910_a(this.field_199833_p);
         if (var4 != null) {
            Iterator var5 = var4.func_199885_a().iterator();

            label40:
            while(true) {
               while(true) {
                  Block var6;
                  do {
                     if (!var5.hasNext()) {
                        break label40;
                     }

                     var6 = (Block)var5.next();
                     IProperty var7 = var6.func_176194_O().func_185920_a(var2);
                     if (var7 != null) {
                        func_201037_a(var1, var7);
                     }
                  } while(var3);

                  Iterator var8 = var6.func_176194_O().func_177623_d().iterator();

                  while(var8.hasNext()) {
                     IProperty var9 = (IProperty)var8.next();
                     if (!this.field_200141_l.containsKey(var9.func_177701_a())) {
                        var3 = true;
                        break;
                     }
                  }
               }
            }
         }
      }

      if (var3) {
         var1.suggest(String.valueOf(','));
      }

      var1.suggest(String.valueOf(']'));
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_212599_i(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         Tag var2 = BlockTags.func_199896_a().func_199910_a(this.field_199833_p);
         if (var2 != null) {
            boolean var3 = false;
            boolean var4 = false;
            Iterator var5 = var2.func_199885_a().iterator();

            while(var5.hasNext()) {
               Block var6 = (Block)var5.next();
               var3 |= !var6.func_176194_O().func_177623_d().isEmpty();
               var4 |= var6.func_149716_u();
               if (var3 && var4) {
                  break;
               }
            }

            if (var3) {
               var1.suggest(String.valueOf('['));
            }

            if (var4) {
               var1.suggest(String.valueOf('{'));
            }
         }
      }

      return this.func_201953_j(var1);
   }

   private CompletableFuture<Suggestions> func_197255_g(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         if (!this.field_197270_l.func_177230_c().func_176194_O().func_177623_d().isEmpty()) {
            var1.suggest(String.valueOf('['));
         }

         if (this.field_197270_l.func_177230_c().func_149716_u()) {
            var1.suggest(String.valueOf('{'));
         }
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201953_j(SuggestionsBuilder var1) {
      return ISuggestionProvider.func_197014_a(BlockTags.func_199896_a().func_199908_a(), var1.createOffset(this.field_201954_r).add(var1));
   }

   private CompletableFuture<Suggestions> func_197250_h(SuggestionsBuilder var1) {
      if (this.field_199832_j) {
         ISuggestionProvider.func_197006_a(BlockTags.func_199896_a().func_199908_a(), var1, String.valueOf('#'));
      }

      ISuggestionProvider.func_197014_a(IRegistry.field_212618_g.func_148742_b(), var1);
      return var1.buildFuture();
   }

   public void func_197258_d() throws CommandSyntaxException {
      int var1 = this.field_197266_h.getCursor();
      this.field_197268_j = ResourceLocation.func_195826_a(this.field_197266_h);
      if (IRegistry.field_212618_g.func_212607_c(this.field_197268_j)) {
         Block var2 = (Block)IRegistry.field_212618_g.func_82594_a(this.field_197268_j);
         this.field_197269_k = var2.func_176194_O();
         this.field_197270_l = var2.func_176223_P();
      } else {
         this.field_197266_h.setCursor(var1);
         throw field_197259_a.createWithContext(this.field_197266_h, this.field_197268_j.toString());
      }
   }

   public void func_199827_f() throws CommandSyntaxException {
      if (!this.field_199832_j) {
         throw field_199831_a.create();
      } else {
         this.field_197272_n = this::func_201953_j;
         this.field_197266_h.expect('#');
         this.field_201954_r = this.field_197266_h.getCursor();
         this.field_199833_p = ResourceLocation.func_195826_a(this.field_197266_h);
      }
   }

   public void func_197257_e() throws CommandSyntaxException {
      this.field_197266_h.skip();
      this.field_197272_n = this::func_197252_b;
      this.field_197266_h.skipWhitespace();

      while(this.field_197266_h.canRead() && this.field_197266_h.peek() != ']') {
         this.field_197266_h.skipWhitespace();
         int var1 = this.field_197266_h.getCursor();
         String var2 = this.field_197266_h.readString();
         IProperty var3 = this.field_197269_k.func_185920_a(var2);
         if (var3 == null) {
            this.field_197266_h.setCursor(var1);
            throw field_197260_b.createWithContext(this.field_197266_h, this.field_197268_j.toString(), var2);
         }

         if (this.field_197267_i.containsKey(var3)) {
            this.field_197266_h.setCursor(var1);
            throw field_197261_c.createWithContext(this.field_197266_h, this.field_197268_j.toString(), var2);
         }

         this.field_197266_h.skipWhitespace();
         this.field_197272_n = this::func_197246_e;
         if (this.field_197266_h.canRead() && this.field_197266_h.peek() == '=') {
            this.field_197266_h.skip();
            this.field_197266_h.skipWhitespace();
            this.field_197272_n = (var1x) -> {
               return func_201037_a(var1x, var3).buildFuture();
            };
            int var4 = this.field_197266_h.getCursor();
            this.func_197253_a(var3, this.field_197266_h.readString(), var4);
            this.field_197272_n = this::func_197248_f;
            this.field_197266_h.skipWhitespace();
            if (!this.field_197266_h.canRead()) {
               continue;
            }

            if (this.field_197266_h.peek() == ',') {
               this.field_197266_h.skip();
               this.field_197272_n = this::func_197256_c;
               continue;
            }

            if (this.field_197266_h.peek() != ']') {
               throw field_197264_f.createWithContext(this.field_197266_h);
            }
            break;
         }

         throw field_197263_e.createWithContext(this.field_197266_h, this.field_197268_j.toString(), var2);
      }

      if (this.field_197266_h.canRead()) {
         this.field_197266_h.skip();
      } else {
         throw field_197264_f.createWithContext(this.field_197266_h);
      }
   }

   public void func_200137_h() throws CommandSyntaxException {
      this.field_197266_h.skip();
      this.field_197272_n = this::func_200136_c;
      int var1 = -1;
      this.field_197266_h.skipWhitespace();

      while(true) {
         if (this.field_197266_h.canRead() && this.field_197266_h.peek() != ']') {
            this.field_197266_h.skipWhitespace();
            int var2 = this.field_197266_h.getCursor();
            String var3 = this.field_197266_h.readString();
            if (this.field_200141_l.containsKey(var3)) {
               this.field_197266_h.setCursor(var2);
               throw field_197261_c.createWithContext(this.field_197266_h, this.field_197268_j.toString(), var3);
            }

            this.field_197266_h.skipWhitespace();
            if (!this.field_197266_h.canRead() || this.field_197266_h.peek() != '=') {
               this.field_197266_h.setCursor(var2);
               throw field_197263_e.createWithContext(this.field_197266_h, this.field_197268_j.toString(), var3);
            }

            this.field_197266_h.skip();
            this.field_197266_h.skipWhitespace();
            this.field_197272_n = (var2x) -> {
               return this.func_200140_a(var2x, var3);
            };
            var1 = this.field_197266_h.getCursor();
            String var4 = this.field_197266_h.readString();
            this.field_200141_l.put(var3, var4);
            this.field_197266_h.skipWhitespace();
            if (!this.field_197266_h.canRead()) {
               continue;
            }

            var1 = -1;
            if (this.field_197266_h.peek() == ',') {
               this.field_197266_h.skip();
               this.field_197272_n = this::func_200134_e;
               continue;
            }

            if (this.field_197266_h.peek() != ']') {
               throw field_197264_f.createWithContext(this.field_197266_h);
            }
         }

         if (this.field_197266_h.canRead()) {
            this.field_197266_h.skip();
            return;
         }

         if (var1 >= 0) {
            this.field_197266_h.setCursor(var1);
         }

         throw field_197264_f.createWithContext(this.field_197266_h);
      }
   }

   public void func_197240_f() throws CommandSyntaxException {
      this.field_197271_m = (new JsonToNBT(this.field_197266_h)).func_193593_f();
   }

   private <T extends Comparable<T>> void func_197253_a(IProperty<T> var1, String var2, int var3) throws CommandSyntaxException {
      Optional var4 = var1.func_185929_b(var2);
      if (var4.isPresent()) {
         this.field_197270_l = (IBlockState)this.field_197270_l.func_206870_a(var1, (Comparable)var4.get());
         this.field_197267_i.put(var1, var4.get());
      } else {
         this.field_197266_h.setCursor(var3);
         throw field_197262_d.createWithContext(this.field_197266_h, this.field_197268_j.toString(), var1.func_177701_a(), var2);
      }
   }

   public static String func_197247_a(IBlockState var0, @Nullable NBTTagCompound var1) {
      StringBuilder var2 = new StringBuilder(IRegistry.field_212618_g.func_177774_c(var0.func_177230_c()).toString());
      if (!var0.func_206869_a().isEmpty()) {
         var2.append('[');
         boolean var3 = false;

         for(UnmodifiableIterator var4 = var0.func_206871_b().entrySet().iterator(); var4.hasNext(); var3 = true) {
            Entry var5 = (Entry)var4.next();
            if (var3) {
               var2.append(',');
            }

            func_211375_a(var2, (IProperty)var5.getKey(), (Comparable)var5.getValue());
         }

         var2.append(']');
      }

      if (var1 != null) {
         var2.append(var1);
      }

      return var2.toString();
   }

   private static <T extends Comparable<T>> void func_211375_a(StringBuilder var0, IProperty<T> var1, Comparable<?> var2) {
      var0.append(var1.func_177701_a());
      var0.append('=');
      var0.append(var1.func_177702_a(var2));
   }

   public CompletableFuture<Suggestions> func_197245_a(SuggestionsBuilder var1) {
      return (CompletableFuture)this.field_197272_n.apply(var1.createOffset(this.field_197266_h.getCursor()));
   }

   public Map<String, String> func_200139_j() {
      return this.field_200141_l;
   }
}
