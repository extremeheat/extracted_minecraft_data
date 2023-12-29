package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;

public record EntityDataSource(String d, @Nullable EntitySelector e) implements DataSource {
   private final String selectorPattern;
   @Nullable
   private final EntitySelector compiledSelector;
   public static final MapCodec<EntityDataSource> SUB_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.STRING.fieldOf("entity").forGetter(EntityDataSource::selectorPattern)).apply(var0, EntityDataSource::new)
   );
   public static final DataSource.Type<EntityDataSource> TYPE = new DataSource.Type<>(SUB_CODEC, "entity");

   public EntityDataSource(String var1) {
      this(var1, compileSelector(var1));
   }

   public EntityDataSource(String var1, @Nullable EntitySelector var2) {
      super();
      this.selectorPattern = var1;
      this.compiledSelector = var2;
   }

   @Nullable
   private static EntitySelector compileSelector(String var0) {
      try {
         EntitySelectorParser var1 = new EntitySelectorParser(new StringReader(var0));
         return var1.parse();
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   @Override
   public Stream<CompoundTag> getData(CommandSourceStack var1) throws CommandSyntaxException {
      if (this.compiledSelector != null) {
         List var2 = this.compiledSelector.findEntities(var1);
         return var2.stream().map(NbtPredicate::getEntityTagToCompare);
      } else {
         return Stream.empty();
      }
   }

   @Override
   public DataSource.Type<?> type() {
      return TYPE;
   }

   @Override
   public String toString() {
      return "entity=" + this.selectorPattern;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof EntityDataSource var2 && this.selectorPattern.equals(var2.selectorPattern)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.selectorPattern.hashCode();
   }
}
