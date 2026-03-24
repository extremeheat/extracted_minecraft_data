package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public interface ListOperation {
   MapCodec<ListOperation> UNLIMITED_CODEC = codec(2147483647);

   static MapCodec<ListOperation> codec(final int maxSize) {
      return ListOperation.Type.CODEC.dispatchMap("mode", ListOperation::mode, (e) -> e.mapCodec).validate((op) -> {
         if (op instanceof ReplaceSection section) {
            if (section.size().isPresent()) {
               int size = (Integer)section.size().get();
               if (size > maxSize) {
                  return DataResult.error(() -> "Size value too large: " + size + ", max size is " + maxSize);
               }
            }
         }

         return DataResult.success(op);
      });
   }

   Type mode();

   default <T> List<T> apply(final List<T> original, final List<T> replacement) {
      return this.<T>apply(original, replacement, 2147483647);
   }

   <T> List<T> apply(List<T> original, List<T> replacement, int maxSize);

   public static enum Type implements StringRepresentable {
      REPLACE_ALL("replace_all", ListOperation.ReplaceAll.MAP_CODEC),
      REPLACE_SECTION("replace_section", ListOperation.ReplaceSection.MAP_CODEC),
      INSERT("insert", ListOperation.Insert.MAP_CODEC),
      APPEND("append", ListOperation.Append.MAP_CODEC);

      public static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final String id;
      private final MapCodec<? extends ListOperation> mapCodec;

      private Type(final String id, final MapCodec<? extends ListOperation> mapCodec) {
         this.id = id;
         this.mapCodec = mapCodec;
      }

      public MapCodec<? extends ListOperation> mapCodec() {
         return this.mapCodec;
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{REPLACE_ALL, REPLACE_SECTION, INSERT, APPEND};
      }
   }

   public static class ReplaceAll implements ListOperation {
      public static final ReplaceAll INSTANCE = new ReplaceAll();
      public static final MapCodec<ReplaceAll> MAP_CODEC = MapCodec.unit(() -> INSTANCE);

      private ReplaceAll() {
         super();
      }

      public Type mode() {
         return ListOperation.Type.REPLACE_ALL;
      }

      public <T> List<T> apply(final List<T> original, final List<T> replacement, final int maxSize) {
         return replacement;
      }
   }

   public static record ReplaceSection(int offset, Optional<Integer> size) implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec<ReplaceSection> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(ReplaceSection::offset), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("size").forGetter(ReplaceSection::size)).apply(i, ReplaceSection::new));

      public ReplaceSection(final int offset) {
         this(offset, Optional.empty());
      }

      public ReplaceSection {
         super();
      }

      public Type mode() {
         return ListOperation.Type.REPLACE_SECTION;
      }

      public <T> List<T> apply(final List<T> original, final List<T> replacement, final int maxSize) {
         int originalSize = original.size();
         if (this.offset > originalSize) {
            LOGGER.error("Cannot replace when offset is out of bounds");
            return original;
         } else {
            ImmutableList.Builder<T> newList = ImmutableList.builder();
            newList.addAll(original.subList(0, this.offset));
            newList.addAll(replacement);
            int resumeIndex = this.offset + (Integer)this.size.orElse(replacement.size());
            if (resumeIndex < originalSize) {
               newList.addAll(original.subList(resumeIndex, originalSize));
            }

            List<T> result = newList.build();
            if (result.size() > maxSize) {
               LOGGER.error("Contents overflow in section replacement");
               return original;
            } else {
               return result;
            }
         }
      }
   }

   public static record Insert(int offset) implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec<Insert> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(Insert::offset)).apply(i, Insert::new));

      public Insert {
         super();
      }

      public Type mode() {
         return ListOperation.Type.INSERT;
      }

      public <T> List<T> apply(final List<T> original, final List<T> replacement, final int maxSize) {
         int originalSize = original.size();
         if (this.offset > originalSize) {
            LOGGER.error("Cannot insert when offset is out of bounds");
            return original;
         } else if (originalSize + replacement.size() > maxSize) {
            LOGGER.error("Contents overflow in section insertion");
            return original;
         } else {
            ImmutableList.Builder<T> newList = ImmutableList.builder();
            newList.addAll(original.subList(0, this.offset));
            newList.addAll(replacement);
            newList.addAll(original.subList(this.offset, originalSize));
            return newList.build();
         }
      }
   }

   public static class Append implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final Append INSTANCE = new Append();
      public static final MapCodec<Append> MAP_CODEC = MapCodec.unit(() -> INSTANCE);

      private Append() {
         super();
      }

      public Type mode() {
         return ListOperation.Type.APPEND;
      }

      public <T> List<T> apply(final List<T> original, final List<T> replacement, final int maxSize) {
         if (original.size() + replacement.size() > maxSize) {
            LOGGER.error("Contents overflow in section append");
            return original;
         } else {
            return Stream.concat(original.stream(), replacement.stream()).toList();
         }
      }
   }

   public static record StandAlone<T>(List<T> value, ListOperation operation) {
      public StandAlone {
         super();
      }

      public static <T> Codec<StandAlone<T>> codec(final Codec<T> valueCodec, final int maxSize) {
         return RecordCodecBuilder.create((i) -> i.group(valueCodec.sizeLimitedListOf(maxSize).fieldOf("values").forGetter((f) -> f.value), ListOperation.codec(maxSize).forGetter((f) -> f.operation)).apply(i, StandAlone::new));
      }

      public List<T> apply(final List<T> input) {
         return this.operation.<T>apply(input, this.value);
      }
   }
}
