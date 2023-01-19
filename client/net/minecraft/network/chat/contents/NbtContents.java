package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class NbtContents implements ComponentContents {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final boolean interpreting;
   private final Optional<Component> separator;
   private final String nbtPathPattern;
   private final DataSource dataSource;
   @Nullable
   protected final NbtPathArgument.NbtPath compiledNbtPath;

   public NbtContents(String var1, boolean var2, Optional<Component> var3, DataSource var4) {
      this(var1, compileNbtPath(var1), var2, var3, var4);
   }

   private NbtContents(String var1, @Nullable NbtPathArgument.NbtPath var2, boolean var3, Optional<Component> var4, DataSource var5) {
      super();
      this.nbtPathPattern = var1;
      this.compiledNbtPath = var2;
      this.interpreting = var3;
      this.separator = var4;
      this.dataSource = var5;
   }

   @Nullable
   private static NbtPathArgument.NbtPath compileNbtPath(String var0) {
      try {
         return new NbtPathArgument().parse(new StringReader(var0));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public String getNbtPath() {
      return this.nbtPathPattern;
   }

   public boolean isInterpreting() {
      return this.interpreting;
   }

   public Optional<Component> getSeparator() {
      return this.separator;
   }

   public DataSource getDataSource() {
      return this.dataSource;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof NbtContents var2
            && this.dataSource.equals(var2.dataSource)
            && this.separator.equals(var2.separator)
            && this.interpreting == var2.interpreting
            && this.nbtPathPattern.equals(var2.nbtPathPattern)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.interpreting ? 1 : 0;
      var1 = 31 * var1 + this.separator.hashCode();
      var1 = 31 * var1 + this.nbtPathPattern.hashCode();
      return 31 * var1 + this.dataSource.hashCode();
   }

   @Override
   public String toString() {
      return "nbt{" + this.dataSource + ", interpreting=" + this.interpreting + ", separator=" + this.separator + "}";
   }

   @Override
   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 != null && this.compiledNbtPath != null) {
         Stream var4 = this.dataSource.getData(var1).flatMap(var1x -> {
            try {
               return this.compiledNbtPath.get(var1x).stream();
            } catch (CommandSyntaxException var3x) {
               return Stream.empty();
            }
         }).map(Tag::getAsString);
         if (this.interpreting) {
            Component var5 = (Component)DataFixUtils.orElse(
               ComponentUtils.updateForEntity(var1, this.separator, var2, var3), ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR
            );
            return var4.flatMap(var3x -> {
               try {
                  MutableComponent var4x = Component.Serializer.fromJson(var3x);
                  return Stream.of(ComponentUtils.updateForEntity(var1, var4x, var2, var3));
               } catch (Exception var5x) {
                  LOGGER.warn("Failed to parse component: {}", var3x, var5x);
                  return Stream.of();
               }
            }).reduce((var1x, var2x) -> var1x.append(var5).append(var2x)).orElseGet(Component::empty);
         } else {
            return ComponentUtils.updateForEntity(var1, this.separator, var2, var3)
               .map(var1x -> var4.map(Component::literal).reduce((var1xx, var2x) -> var1xx.append(var1x).append(var2x)).orElseGet(Component::empty))
               .orElseGet(() -> Component.literal(var4.collect(Collectors.joining(", "))));
         }
      } else {
         return Component.empty();
      }
   }
}
