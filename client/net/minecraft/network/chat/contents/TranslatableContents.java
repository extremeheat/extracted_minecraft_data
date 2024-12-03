package net.minecraft.network.chat.contents;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;

public class TranslatableContents implements ComponentContents {
   public static final Object[] NO_ARGS = new Object[0];
   private static final Codec<Object> PRIMITIVE_ARG_CODEC;
   private static final Codec<Object> ARG_CODEC;
   public static final MapCodec<TranslatableContents> CODEC;
   public static final ComponentContents.Type<TranslatableContents> TYPE;
   private static final FormattedText TEXT_PERCENT;
   private static final FormattedText TEXT_NULL;
   private final String key;
   @Nullable
   private final String fallback;
   private final Object[] args;
   @Nullable
   private Language decomposedWith;
   private List<FormattedText> decomposedParts = ImmutableList.of();
   private static final Pattern FORMAT_PATTERN;

   private static DataResult<Object> filterAllowedArguments(@Nullable Object var0) {
      return !isAllowedPrimitiveArgument(var0) ? DataResult.error(() -> "This value needs to be parsed as component") : DataResult.success(var0);
   }

   public static boolean isAllowedPrimitiveArgument(@Nullable Object var0) {
      return var0 instanceof Number || var0 instanceof Boolean || var0 instanceof String;
   }

   private static Optional<List<Object>> adjustArgs(Object[] var0) {
      return var0.length == 0 ? Optional.empty() : Optional.of(Arrays.asList(var0));
   }

   private static Object[] adjustArgs(Optional<List<Object>> var0) {
      return var0.map((var0x) -> var0x.isEmpty() ? NO_ARGS : var0x.toArray()).orElse(NO_ARGS);
   }

   private static TranslatableContents create(String var0, Optional<String> var1, Optional<List<Object>> var2) {
      return new TranslatableContents(var0, (String)var1.orElse((Object)null), adjustArgs(var2));
   }

   public TranslatableContents(String var1, @Nullable String var2, Object[] var3) {
      super();
      this.key = var1;
      this.fallback = var2;
      this.args = var3;
   }

   public ComponentContents.Type<?> type() {
      return TYPE;
   }

   private void decompose() {
      Language var1 = Language.getInstance();
      if (var1 != this.decomposedWith) {
         this.decomposedWith = var1;
         String var2 = this.fallback != null ? var1.getOrDefault(this.key, this.fallback) : var1.getOrDefault(this.key);

         try {
            ImmutableList.Builder var3 = ImmutableList.builder();
            Objects.requireNonNull(var3);
            this.decomposeTemplate(var2, var3::add);
            this.decomposedParts = var3.build();
         } catch (TranslatableFormatException var4) {
            this.decomposedParts = ImmutableList.of(FormattedText.of(var2));
         }

      }
   }

   private void decomposeTemplate(String var1, Consumer<FormattedText> var2) {
      Matcher var3 = FORMAT_PATTERN.matcher(var1);

      try {
         int var4 = 0;

         int var5;
         int var7;
         for(var5 = 0; var3.find(var5); var5 = var7) {
            int var6 = var3.start();
            var7 = var3.end();
            if (var6 > var5) {
               String var8 = var1.substring(var5, var6);
               if (var8.indexOf(37) != -1) {
                  throw new IllegalArgumentException();
               }

               var2.accept(FormattedText.of(var8));
            }

            String var14 = var3.group(2);
            String var9 = var1.substring(var6, var7);
            if ("%".equals(var14) && "%%".equals(var9)) {
               var2.accept(TEXT_PERCENT);
            } else {
               if (!"s".equals(var14)) {
                  throw new TranslatableFormatException(this, "Unsupported format: '" + var9 + "'");
               }

               String var10 = var3.group(1);
               int var11 = var10 != null ? Integer.parseInt(var10) - 1 : var4++;
               var2.accept(this.getArgument(var11));
            }
         }

         if (var5 < var1.length()) {
            String var13 = var1.substring(var5);
            if (var13.indexOf(37) != -1) {
               throw new IllegalArgumentException();
            }

            var2.accept(FormattedText.of(var13));
         }

      } catch (IllegalArgumentException var12) {
         throw new TranslatableFormatException(this, var12);
      }
   }

   private FormattedText getArgument(int var1) {
      if (var1 >= 0 && var1 < this.args.length) {
         Object var2 = this.args[var1];
         if (var2 instanceof Component) {
            Component var3 = (Component)var2;
            return var3;
         } else {
            return var2 == null ? TEXT_NULL : FormattedText.of(var2.toString());
         }
      } else {
         throw new TranslatableFormatException(this, var1);
      }
   }

   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      this.decompose();

      for(FormattedText var4 : this.decomposedParts) {
         Optional var5 = var4.visit(var1, var2);
         if (var5.isPresent()) {
            return var5;
         }
      }

      return Optional.empty();
   }

   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      this.decompose();

      for(FormattedText var3 : this.decomposedParts) {
         Optional var4 = var3.visit(var1);
         if (var4.isPresent()) {
            return var4;
         }
      }

      return Optional.empty();
   }

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      Object[] var4 = new Object[this.args.length];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         Object var6 = this.args[var5];
         if (var6 instanceof Component var7) {
            var4[var5] = ComponentUtils.updateForEntity(var1, var7, var2, var3);
         } else {
            var4[var5] = var6;
         }
      }

      return MutableComponent.create(new TranslatableContents(this.key, this.fallback, var4));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof TranslatableContents) {
            TranslatableContents var2 = (TranslatableContents)var1;
            if (Objects.equals(this.key, var2.key) && Objects.equals(this.fallback, var2.fallback) && Arrays.equals(this.args, var2.args)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      int var1 = Objects.hashCode(this.key);
      var1 = 31 * var1 + Objects.hashCode(this.fallback);
      var1 = 31 * var1 + Arrays.hashCode(this.args);
      return var1;
   }

   public String toString() {
      String var10000 = this.key;
      return "translation{key='" + var10000 + "'" + (this.fallback != null ? ", fallback='" + this.fallback + "'" : "") + ", args=" + Arrays.toString(this.args) + "}";
   }

   public String getKey() {
      return this.key;
   }

   @Nullable
   public String getFallback() {
      return this.fallback;
   }

   public Object[] getArgs() {
      return this.args;
   }

   static {
      PRIMITIVE_ARG_CODEC = ExtraCodecs.JAVA.validate(TranslatableContents::filterAllowedArguments);
      ARG_CODEC = Codec.either(PRIMITIVE_ARG_CODEC, ComponentSerialization.CODEC).xmap((var0) -> var0.map((var0x) -> var0x, (var0x) -> Objects.requireNonNullElse(var0x.tryCollapseToString(), var0x)), (var0) -> {
         Either var10000;
         if (var0 instanceof Component var1) {
            var10000 = Either.right(var1);
         } else {
            var10000 = Either.left(var0);
         }

         return var10000;
      });
      CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("translate").forGetter((var0x) -> var0x.key), Codec.STRING.lenientOptionalFieldOf("fallback").forGetter((var0x) -> Optional.ofNullable(var0x.fallback)), ARG_CODEC.listOf().optionalFieldOf("with").forGetter((var0x) -> adjustArgs(var0x.args))).apply(var0, TranslatableContents::create));
      TYPE = new ComponentContents.Type<TranslatableContents>(CODEC, "translatable");
      TEXT_PERCENT = FormattedText.of("%");
      TEXT_NULL = FormattedText.of("null");
      FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
   }
}
