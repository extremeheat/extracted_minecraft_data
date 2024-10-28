package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import org.apache.commons.lang3.mutable.MutableObject;

public class CopyCustomDataFunction extends LootItemConditionalFunction {
   public static final MapCodec<CopyCustomDataFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(NbtProviders.CODEC.fieldOf("source").forGetter((var0x) -> {
         return var0x.source;
      }), CopyCustomDataFunction.CopyOperation.CODEC.listOf().fieldOf("ops").forGetter((var0x) -> {
         return var0x.operations;
      }))).apply(var0, CopyCustomDataFunction::new);
   });
   private final NbtProvider source;
   private final List<CopyOperation> operations;

   CopyCustomDataFunction(List<LootItemCondition> var1, NbtProvider var2, List<CopyOperation> var3) {
      super(var1);
      this.source = var2;
      this.operations = List.copyOf(var3);
   }

   public LootItemFunctionType<CopyCustomDataFunction> getType() {
      return LootItemFunctions.COPY_CUSTOM_DATA;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.source.getReferencedContextParams();
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Tag var3 = this.source.get(var2);
      if (var3 == null) {
         return var1;
      } else {
         MutableObject var4 = new MutableObject();
         Supplier var5 = () -> {
            if (var4.getValue() == null) {
               var4.setValue(((CustomData)var1.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag());
            }

            return (Tag)var4.getValue();
         };
         this.operations.forEach((var2x) -> {
            var2x.apply(var5, var3);
         });
         CompoundTag var6 = (CompoundTag)var4.getValue();
         if (var6 != null) {
            CustomData.set(DataComponents.CUSTOM_DATA, var1, var6);
         }

         return var1;
      }
   }

   /** @deprecated */
   @Deprecated
   public static Builder copyData(NbtProvider var0) {
      return new Builder(var0);
   }

   public static Builder copyData(LootContext.EntityTarget var0) {
      return new Builder(ContextNbtProvider.forContextEntity(var0));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final NbtProvider source;
      private final List<CopyOperation> ops = Lists.newArrayList();

      Builder(NbtProvider var1) {
         super();
         this.source = var1;
      }

      public Builder copy(String var1, String var2, MergeStrategy var3) {
         try {
            this.ops.add(new CopyOperation(NbtPathArgument.NbtPath.of(var1), NbtPathArgument.NbtPath.of(var2), var3));
            return this;
         } catch (CommandSyntaxException var5) {
            throw new IllegalArgumentException(var5);
         }
      }

      public Builder copy(String var1, String var2) {
         return this.copy(var1, var2, CopyCustomDataFunction.MergeStrategy.REPLACE);
      }

      protected Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyCustomDataFunction(this.getConditions(), this.source, this.ops);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   private static record CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, MergeStrategy op) {
      public static final Codec<CopyOperation> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(NbtPathArgument.NbtPath.CODEC.fieldOf("source").forGetter(CopyOperation::sourcePath), NbtPathArgument.NbtPath.CODEC.fieldOf("target").forGetter(CopyOperation::targetPath), CopyCustomDataFunction.MergeStrategy.CODEC.fieldOf("op").forGetter(CopyOperation::op)).apply(var0, CopyOperation::new);
      });

      CopyOperation(NbtPathArgument.NbtPath var1, NbtPathArgument.NbtPath var2, MergeStrategy var3) {
         super();
         this.sourcePath = var1;
         this.targetPath = var2;
         this.op = var3;
      }

      public void apply(Supplier<Tag> var1, Tag var2) {
         try {
            List var3 = this.sourcePath.get(var2);
            if (!var3.isEmpty()) {
               this.op.merge((Tag)var1.get(), this.targetPath, var3);
            }
         } catch (CommandSyntaxException var4) {
         }

      }

      public NbtPathArgument.NbtPath sourcePath() {
         return this.sourcePath;
      }

      public NbtPathArgument.NbtPath targetPath() {
         return this.targetPath;
      }

      public MergeStrategy op() {
         return this.op;
      }
   }

   public static enum MergeStrategy implements StringRepresentable {
      REPLACE("replace") {
         public void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException {
            var2.set(var1, (Tag)Iterables.getLast(var3));
         }
      },
      APPEND("append") {
         public void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException {
            List var4 = var2.getOrCreate(var1, ListTag::new);
            var4.forEach((var1x) -> {
               if (var1x instanceof ListTag) {
                  var3.forEach((var1) -> {
                     ((ListTag)var1x).add(var1.copy());
                  });
               }

            });
         }
      },
      MERGE("merge") {
         public void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException {
            List var4 = var2.getOrCreate(var1, CompoundTag::new);
            var4.forEach((var1x) -> {
               if (var1x instanceof CompoundTag) {
                  var3.forEach((var1) -> {
                     if (var1 instanceof CompoundTag) {
                        ((CompoundTag)var1x).merge((CompoundTag)var1);
                     }

                  });
               }

            });
         }
      };

      public static final Codec<MergeStrategy> CODEC = StringRepresentable.fromEnum(MergeStrategy::values);
      private final String name;

      public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

      MergeStrategy(final String var3) {
         this.name = var3;
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
