package net.minecraft.network.chat.contents.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public record EntityDataSource(String selectorPattern, @Nullable EntitySelector compiledSelector) implements DataSource {
   public static final MapCodec<EntityDataSource> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("entity").forGetter(EntityDataSource::selectorPattern)).apply(i, EntityDataSource::new));

   public EntityDataSource(final String selector) {
      this(selector, compileSelector(selector));
   }

   public EntityDataSource {
      super();
   }

   private static @Nullable EntitySelector compileSelector(final String selector) {
      try {
         EntitySelectorParser parser = new EntitySelectorParser(new StringReader(selector), true);
         return parser.parse();
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public Stream<CompoundTag> getData(final CommandSourceStack sender) throws CommandSyntaxException {
      if (this.compiledSelector != null) {
         List<? extends Entity> entities = this.compiledSelector.findEntities(sender);
         return entities.stream().map(NbtPredicate::getEntityTagToCompare);
      } else {
         return Stream.empty();
      }
   }

   public MapCodec<EntityDataSource> codec() {
      return MAP_CODEC;
   }

   public String toString() {
      return "entity=" + this.selectorPattern;
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof EntityDataSource) {
            EntityDataSource that = (EntityDataSource)o;
            if (this.selectorPattern.equals(that.selectorPattern)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.selectorPattern.hashCode();
   }
}
