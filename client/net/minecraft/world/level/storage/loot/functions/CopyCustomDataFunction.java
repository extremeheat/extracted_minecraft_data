package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import org.apache.commons.lang3.mutable.MutableObject;

public class CopyCustomDataFunction extends LootItemConditionalFunction {
   public static final MapCodec<CopyCustomDataFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(NbtProviders.CODEC.fieldOf("source").forGetter((f) -> f.source), CopyCustomDataFunction.CopyOperation.CODEC.listOf().fieldOf("ops").forGetter((f) -> f.operations))).apply(i, CopyCustomDataFunction::new));
   private final NbtProvider source;
   private final List<CopyOperation> operations;

   private CopyCustomDataFunction(final Optional<Holder<LootItemCondition>> condition, final NbtProvider source, final List<CopyOperation> operations) {
      super(condition);
      this.source = source;
      this.operations = List.copyOf(operations);
   }

   public MapCodec<CopyCustomDataFunction> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validate(context, "source", this.source);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      Tag sourceTag = this.source.get(context);
      if (sourceTag == null) {
         return itemStack;
      } else {
         MutableObject<CompoundTag> result = new MutableObject();
         Supplier<Tag> lazyTargetCopy = () -> {
            if (result.get() == null) {
               result.setValue(((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag());
            }

            return (Tag)result.get();
         };
         this.operations.forEach((op) -> op.apply(lazyTargetCopy, sourceTag));
         CompoundTag resultTag = (CompoundTag)result.get();
         if (resultTag != null) {
            CustomData.set(DataComponents.CUSTOM_DATA, itemStack, resultTag);
         }

         return itemStack;
      }
   }

   /** @deprecated */
   @Deprecated
   public static Builder copyData(final NbtProvider source) {
      return new Builder(source);
   }

   public static Builder copyData(final LootContext.EntityTarget source) {
      return new Builder(ContextNbtProvider.forContextEntity(source));
   }

   private static record CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, MergeStrategy op) {
      public static final Codec<CopyOperation> CODEC = RecordCodecBuilder.create((i) -> i.group(NbtPathArgument.NbtPath.CODEC.fieldOf("source").forGetter(CopyOperation::sourcePath), NbtPathArgument.NbtPath.CODEC.fieldOf("target").forGetter(CopyOperation::targetPath), CopyCustomDataFunction.MergeStrategy.CODEC.fieldOf("op").forGetter(CopyOperation::op)).apply(i, CopyOperation::new));

      private CopyOperation {
         super();
      }

      public void apply(final Supplier<Tag> target, final Tag source) {
         try {
            List<Tag> sourceTags = this.sourcePath.get(source);
            if (!sourceTags.isEmpty()) {
               this.op.merge((Tag)target.get(), this.targetPath, sourceTags);
            }
         } catch (CommandSyntaxException var4) {
         }

      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final NbtProvider source;
      private final List<CopyOperation> ops = Lists.newArrayList();

      private Builder(final NbtProvider source) {
         super();
         this.source = source;
      }

      public Builder copy(final String sourcePath, final String targetPath, final MergeStrategy mergeStrategy) {
         try {
            this.ops.add(new CopyOperation(NbtPathArgument.NbtPath.of(sourcePath), NbtPathArgument.NbtPath.of(targetPath), mergeStrategy));
            return this;
         } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException(e);
         }
      }

      public Builder copy(final String sourcePath, final String targetPath) {
         return this.copy(sourcePath, targetPath, CopyCustomDataFunction.MergeStrategy.REPLACE);
      }

      protected Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyCustomDataFunction(this.getCondition(), this.source, this.ops);
      }
   }

   public static enum MergeStrategy implements StringRepresentable {
      REPLACE("replace") {
         public void merge(final Tag target, final NbtPathArgument.NbtPath path, final List<Tag> sources) throws CommandSyntaxException {
            path.set(target, (Tag)Iterables.getLast(sources));
         }
      },
      APPEND("append") {
         public void merge(final Tag target, final NbtPathArgument.NbtPath path, final List<Tag> sources) throws CommandSyntaxException {
            List<Tag> targets = path.getOrCreate(target, ListTag::new);
            targets.forEach((tag) -> {
               if (tag instanceof ListTag listTag) {
                  sources.forEach((source) -> listTag.add(source.copy()));
               }

            });
         }
      },
      MERGE("merge") {
         public void merge(final Tag target, final NbtPathArgument.NbtPath path, final List<Tag> sources) throws CommandSyntaxException {
            List<Tag> targets = path.getOrCreate(target, CompoundTag::new);
            targets.forEach((tag) -> {
               if (tag instanceof CompoundTag compoundTag) {
                  sources.forEach((source) -> {
                     if (source instanceof CompoundTag sourceTag) {
                        compoundTag.merge(sourceTag);
                     }

                  });
               }

            });
         }
      };

      public static final Codec<MergeStrategy> CODEC = StringRepresentable.<MergeStrategy>fromEnum(MergeStrategy::values);
      private final String name;

      public abstract void merge(final Tag target, final NbtPathArgument.NbtPath path, List<Tag> sources) throws CommandSyntaxException;

      private MergeStrategy(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static MergeStrategy[] $values() {
         return new MergeStrategy[]{REPLACE, APPEND, MERGE};
      }
   }
}
