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
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

public class TranslatableContents implements ComponentContents {
   public static final Object[] NO_ARGS = new Object[0];
   private static final Codec<Object> PRIMITIVE_ARG_CODEC;
   private static final Codec<Object> ARG_CODEC;
   public static final MapCodec<TranslatableContents> MAP_CODEC;
   private static final FormattedText TEXT_PERCENT;
   private static final FormattedText TEXT_NULL;
   private final String key;
   private final @Nullable String fallback;
   private final Object[] args;
   private @Nullable Language decomposedWith;
   private List<FormattedText> decomposedParts = ImmutableList.of();
   private static final Pattern FORMAT_PATTERN;

   private static DataResult<Object> filterAllowedArguments(final @Nullable Object result) {
      return !isAllowedPrimitiveArgument(result) ? DataResult.error(() -> "This value needs to be parsed as component") : DataResult.success(result);
   }

   public static boolean isAllowedPrimitiveArgument(final @Nullable Object object) {
      return object instanceof Number || object instanceof Boolean || object instanceof String;
   }

   private static Optional<List<Object>> adjustArgs(final Object[] args) {
      return args.length == 0 ? Optional.empty() : Optional.of(Arrays.asList(args));
   }

   private static Object[] adjustArgs(final Optional<List<Object>> args) {
      return args.map((a) -> a.isEmpty() ? NO_ARGS : a.toArray()).orElse(NO_ARGS);
   }

   private static TranslatableContents create(final String key, final Optional<String> fallback, final Optional<List<Object>> args) {
      return new TranslatableContents(key, (String)fallback.orElse((Object)null), adjustArgs(args));
   }

   public TranslatableContents(final String key, final @Nullable String fallback, final Object[] args) {
      super();
      this.key = key;
      this.fallback = fallback;
      this.args = args;
   }

   public MapCodec<TranslatableContents> codec() {
      return MAP_CODEC;
   }

   private void decompose() {
      Language currentLanguage = Language.getInstance();
      if (currentLanguage != this.decomposedWith) {
         this.decomposedWith = currentLanguage;
         String format = this.fallback != null ? currentLanguage.getOrDefault(this.key, this.fallback) : currentLanguage.getOrDefault(this.key);

         try {
            ImmutableList.Builder<FormattedText> parts = ImmutableList.builder();
            Objects.requireNonNull(parts);
            this.decomposeTemplate(format, parts::add);
            this.decomposedParts = parts.build();
         } catch (TranslatableFormatException var4) {
            this.decomposedParts = ImmutableList.of(FormattedText.of(format));
         }

      }
   }

   private void decomposeTemplate(final String template, final Consumer<FormattedText> decomposedParts) {
      Matcher matcher = FORMAT_PATTERN.matcher(template);

      try {
         int replacementIndex = 0;

         int current;
         int end;
         for(current = 0; matcher.find(current); current = end) {
            int start = matcher.start();
            end = matcher.end();
            if (start > current) {
               String prefix = template.substring(current, start);
               if (prefix.indexOf(37) != -1) {
                  throw new IllegalArgumentException();
               }

               decomposedParts.accept(FormattedText.of(prefix));
            }

            String formatType = matcher.group(2);
            String formatString = template.substring(start, end);
            if ("%".equals(formatType) && "%%".equals(formatString)) {
               decomposedParts.accept(TEXT_PERCENT);
            } else {
               if (!"s".equals(formatType)) {
                  throw new TranslatableFormatException(this, "Unsupported format: '" + formatString + "'");
               }

               String possiblePositionIndex = matcher.group(1);
               int index = possiblePositionIndex != null ? Integer.parseInt(possiblePositionIndex) - 1 : replacementIndex++;
               decomposedParts.accept(this.getArgument(index));
            }
         }

         if (current < template.length()) {
            String tail = template.substring(current);
            if (tail.indexOf(37) != -1) {
               throw new IllegalArgumentException();
            }

            decomposedParts.accept(FormattedText.of(tail));
         }

      } catch (IllegalArgumentException e) {
         throw new TranslatableFormatException(this, e);
      }
   }

   private FormattedText getArgument(final int index) {
      if (index >= 0 && index < this.args.length) {
         Object arg = this.args[index];
         if (arg instanceof Component) {
            Component componentArg = (Component)arg;
            return componentArg;
         } else {
            return arg == null ? TEXT_NULL : FormattedText.of(arg.toString());
         }
      } else {
         throw new TranslatableFormatException(this, index);
      }
   }

   public <T> Optional<T> visit(final FormattedText.StyledContentConsumer<T> output, final Style currentStyle) {
      this.decompose();

      for(FormattedText part : this.decomposedParts) {
         Optional<T> result = part.<T>visit(output, currentStyle);
         if (result.isPresent()) {
            return result;
         }
      }

      return Optional.empty();
   }

   public <T> Optional<T> visit(final FormattedText.ContentConsumer<T> output) {
      this.decompose();

      for(FormattedText part : this.decomposedParts) {
         Optional<T> result = part.<T>visit(output);
         if (result.isPresent()) {
            return result;
         }
      }

      return Optional.empty();
   }

   public MutableComponent resolve(final ResolutionContext context, final int recursionDepth) throws CommandSyntaxException {
      Object[] argsCopy = new Object[this.args.length];

      for(int i = 0; i < argsCopy.length; ++i) {
         Object param = this.args[i];
         if (param instanceof Component component) {
            argsCopy[i] = ComponentUtils.resolve(context, component, recursionDepth);
         } else {
            argsCopy[i] = param;
         }
      }

      return MutableComponent.create(new TranslatableContents(this.key, this.fallback, argsCopy));
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof TranslatableContents) {
            TranslatableContents that = (TranslatableContents)o;
            if (Objects.equals(this.key, that.key) && Objects.equals(this.fallback, that.fallback) && Arrays.equals(this.args, that.args)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      int result = Objects.hashCode(this.key);
      result = 31 * result + Objects.hashCode(this.fallback);
      result = 31 * result + Arrays.hashCode(this.args);
      return result;
   }

   public String toString() {
      String var10000 = this.key;
      return "translation{key='" + var10000 + "'" + (this.fallback != null ? ", fallback='" + this.fallback + "'" : "") + ", args=" + Arrays.toString(this.args) + "}";
   }

   public String getKey() {
      return this.key;
   }

   public @Nullable String getFallback() {
      return this.fallback;
   }

   public Object[] getArgs() {
      return this.args;
   }

   static {
      PRIMITIVE_ARG_CODEC = ExtraCodecs.JAVA.validate(TranslatableContents::filterAllowedArguments);
      ARG_CODEC = Codec.either(PRIMITIVE_ARG_CODEC, ComponentSerialization.CODEC).xmap((e) -> e.map((o) -> o, (component) -> Objects.requireNonNullElse(component.tryCollapseToString(), component)), (o) -> {
         Either var10000;
         if (o instanceof Component c) {
            var10000 = Either.right(c);
         } else {
            var10000 = Either.left(o);
         }

         return var10000;
      });
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("translate").forGetter((o) -> o.key), Codec.STRING.lenientOptionalFieldOf("fallback").forGetter((o) -> Optional.ofNullable(o.fallback)), ARG_CODEC.listOf().optionalFieldOf("with").forGetter((o) -> adjustArgs(o.args))).apply(i, TranslatableContents::create));
      TEXT_PERCENT = FormattedText.of("%");
      TEXT_NULL = FormattedText.of("null");
      FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
   }
}
