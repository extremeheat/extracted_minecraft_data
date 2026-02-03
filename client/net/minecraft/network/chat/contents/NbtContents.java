package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.data.DataSource;
import net.minecraft.network.chat.contents.data.DataSources;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class NbtContents implements ComponentContents {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<NbtContents> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("nbt").forGetter(NbtContents::getNbtPath), Codec.BOOL.lenientOptionalFieldOf("interpret", false).forGetter(NbtContents::isInterpreting), ComponentSerialization.CODEC.lenientOptionalFieldOf("separator").forGetter(NbtContents::getSeparator), DataSources.CODEC.forGetter(NbtContents::getDataSource)).apply(i, NbtContents::new));
   private final boolean interpreting;
   private final Optional<Component> separator;
   private final String nbtPathPattern;
   private final DataSource dataSource;
   protected final NbtPathArgument.@Nullable NbtPath compiledNbtPath;

   public NbtContents(final String nbtPath, final boolean interpreting, final Optional<Component> separator, final DataSource dataSource) {
      this(nbtPath, compileNbtPath(nbtPath), interpreting, separator, dataSource);
   }

   private NbtContents(final String nbtPathPattern, final NbtPathArgument.@Nullable NbtPath compiledNbtPath, final boolean interpreting, final Optional<Component> separator, final DataSource dataSource) {
      super();
      this.nbtPathPattern = nbtPathPattern;
      this.compiledNbtPath = compiledNbtPath;
      this.interpreting = interpreting;
      this.separator = separator;
      this.dataSource = dataSource;
   }

   private static NbtPathArgument.@Nullable NbtPath compileNbtPath(final String path) {
      try {
         return (new NbtPathArgument()).parse(new StringReader(path));
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

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof NbtContents) {
            NbtContents that = (NbtContents)o;
            if (this.dataSource.equals(that.dataSource) && this.separator.equals(that.separator) && this.interpreting == that.interpreting && this.nbtPathPattern.equals(that.nbtPathPattern)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      int result = this.interpreting ? 1 : 0;
      result = 31 * result + this.separator.hashCode();
      result = 31 * result + this.nbtPathPattern.hashCode();
      result = 31 * result + this.dataSource.hashCode();
      return result;
   }

   public String toString() {
      String var10000 = String.valueOf(this.dataSource);
      return "nbt{" + var10000 + ", interpreting=" + this.interpreting + ", separator=" + String.valueOf(this.separator) + "}";
   }

   public MutableComponent resolve(final @Nullable CommandSourceStack source, final @Nullable Entity entity, final int recursionDepth) throws CommandSyntaxException {
      if (source != null && this.compiledNbtPath != null) {
         Stream<Tag> elements = this.dataSource.getData(source).flatMap((t) -> {
            try {
               return this.compiledNbtPath.get(t).stream();
            } catch (CommandSyntaxException var3) {
               return Stream.empty();
            }
         });
         Component resolvedSeparator = (Component)DataFixUtils.orElse(ComponentUtils.updateForEntity(source, this.separator, entity, recursionDepth), ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR);
         if (this.interpreting) {
            RegistryOps<Tag> registryOps = source.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            return (MutableComponent)elements.flatMap((tag) -> {
               try {
                  Component component = (Component)ComponentSerialization.CODEC.parse(registryOps, tag).getOrThrow();
                  return Stream.of(ComponentUtils.updateForEntity(source, component, entity, recursionDepth));
               } catch (Exception e) {
                  LOGGER.warn("Failed to parse component: {}", tag, e);
                  return Stream.of();
               }
            }).reduce((left, right) -> left.append(resolvedSeparator).append((Component)right)).orElseGet(Component::empty);
         } else {
            return (MutableComponent)elements.map((tag) -> {
               TextComponentTagVisitor visitor = new TextComponentTagVisitor("");
               return (MutableComponent)visitor.visit(tag);
            }).reduce((left, right) -> left.append(resolvedSeparator).append((Component)right)).orElseGet(Component::empty);
         }
      } else {
         return Component.empty();
      }
   }

   public MapCodec<NbtContents> codec() {
      return MAP_CODEC;
   }
}
