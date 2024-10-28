package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class NbtContents implements ComponentContents {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<NbtContents> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.STRING.fieldOf("nbt").forGetter(NbtContents::getNbtPath), Codec.BOOL.lenientOptionalFieldOf("interpret", false).forGetter(NbtContents::isInterpreting), ComponentSerialization.CODEC.lenientOptionalFieldOf("separator").forGetter(NbtContents::getSeparator), DataSource.CODEC.forGetter(NbtContents::getDataSource)).apply(var0, NbtContents::new);
   });
   public static final ComponentContents.Type<NbtContents> TYPE;
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
         return (new NbtPathArgument()).parse(new StringReader(var0));
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof NbtContents) {
            NbtContents var2 = (NbtContents)var1;
            if (this.dataSource.equals(var2.dataSource) && this.separator.equals(var2.separator) && this.interpreting == var2.interpreting && this.nbtPathPattern.equals(var2.nbtPathPattern)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      int var1 = this.interpreting ? 1 : 0;
      var1 = 31 * var1 + this.separator.hashCode();
      var1 = 31 * var1 + this.nbtPathPattern.hashCode();
      var1 = 31 * var1 + this.dataSource.hashCode();
      return var1;
   }

   public String toString() {
      String var10000 = String.valueOf(this.dataSource);
      return "nbt{" + var10000 + ", interpreting=" + this.interpreting + ", separator=" + String.valueOf(this.separator) + "}";
   }

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 != null && this.compiledNbtPath != null) {
         Stream var4 = this.dataSource.getData(var1).flatMap((var1x) -> {
            try {
               return this.compiledNbtPath.get(var1x).stream();
            } catch (CommandSyntaxException var3) {
               return Stream.empty();
            }
         }).map(Tag::getAsString);
         if (this.interpreting) {
            Component var5 = (Component)DataFixUtils.orElse(ComponentUtils.updateForEntity(var1, this.separator, var2, var3), ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR);
            return (MutableComponent)var4.flatMap((var3x) -> {
               try {
                  MutableComponent var4 = Component.Serializer.fromJson((String)var3x, var1.registryAccess());
                  return Stream.of(ComponentUtils.updateForEntity(var1, (Component)var4, var2, var3));
               } catch (Exception var5) {
                  LOGGER.warn("Failed to parse component: {}", var3x, var5);
                  return Stream.of();
               }
            }).reduce((var1x, var2x) -> {
               return var1x.append(var5).append((Component)var2x);
            }).orElseGet(Component::empty);
         } else {
            return (MutableComponent)ComponentUtils.updateForEntity(var1, this.separator, var2, var3).map((var1x) -> {
               return (MutableComponent)var4.map(Component::literal).reduce((var1, var2) -> {
                  return var1.append((Component)var1x).append((Component)var2);
               }).orElseGet(Component::empty);
            }).orElseGet(() -> {
               return Component.literal((String)var4.collect(Collectors.joining(", ")));
            });
         }
      } else {
         return Component.empty();
      }
   }

   public ComponentContents.Type<?> type() {
      return TYPE;
   }

   static {
      TYPE = new ComponentContents.Type(CODEC, "nbt");
   }
}
