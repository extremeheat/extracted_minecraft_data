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
   public static final MapCodec<CopyCustomDataFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  NbtProviders.CODEC.fieldOf("source").forGetter(var0x -> var0x.source),
                  CopyCustomDataFunction.CopyOperation.CODEC.listOf().fieldOf("ops").forGetter(var0x -> var0x.operations)
               )
            )
            .apply(var0, CopyCustomDataFunction::new)
   );
   private final NbtProvider source;
   private final List<CopyCustomDataFunction.CopyOperation> operations;

   CopyCustomDataFunction(List<LootItemCondition> var1, NbtProvider var2, List<CopyCustomDataFunction.CopyOperation> var3) {
      super(var1);
      this.source = var2;
      this.operations = List.copyOf(var3);
   }

   @Override
   public LootItemFunctionType<CopyCustomDataFunction> getType() {
      return LootItemFunctions.COPY_CUSTOM_DATA;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.source.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      Tag var3 = this.source.get(var2);
      if (var3 == null) {
         return var1;
      } else {
         MutableObject var4 = new MutableObject();
         Supplier var5 = () -> {
            if (var4.getValue() == null) {
               var4.setValue(var1.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
            }

            return (Tag)var4.getValue();
         };
         this.operations.forEach(var2x -> var2x.apply(var5, var3));
         CompoundTag var6 = (CompoundTag)var4.getValue();
         if (var6 != null) {
            CustomData.set(DataComponents.CUSTOM_DATA, var1, var6);
         }

         return var1;
      }
   }

   @Deprecated
   public static CopyCustomDataFunction.Builder copyData(NbtProvider var0) {
      return new CopyCustomDataFunction.Builder(var0);
   }

   public static CopyCustomDataFunction.Builder copyData(LootContext.EntityTarget var0) {
      return new CopyCustomDataFunction.Builder(ContextNbtProvider.forContextEntity(var0));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyCustomDataFunction.Builder> {
      private final NbtProvider source;
      private final List<CopyCustomDataFunction.CopyOperation> ops = Lists.newArrayList();

      Builder(NbtProvider var1) {
         super();
         this.source = var1;
      }

      public CopyCustomDataFunction.Builder copy(String var1, String var2, CopyCustomDataFunction.MergeStrategy var3) {
         try {
            this.ops.add(new CopyCustomDataFunction.CopyOperation(NbtPathArgument.NbtPath.of(var1), NbtPathArgument.NbtPath.of(var2), var3));
            return this;
         } catch (CommandSyntaxException var5) {
            throw new IllegalArgumentException(var5);
         }
      }

      public CopyCustomDataFunction.Builder copy(String var1, String var2) {
         return this.copy(var1, var2, CopyCustomDataFunction.MergeStrategy.REPLACE);
      }

      protected CopyCustomDataFunction.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new CopyCustomDataFunction(this.getConditions(), this.source, this.ops);
      }
   }

   static record CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, CopyCustomDataFunction.MergeStrategy op) {
      public static final Codec<CopyCustomDataFunction.CopyOperation> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  NbtPathArgument.NbtPath.CODEC.fieldOf("source").forGetter(CopyCustomDataFunction.CopyOperation::sourcePath),
                  NbtPathArgument.NbtPath.CODEC.fieldOf("target").forGetter(CopyCustomDataFunction.CopyOperation::targetPath),
                  CopyCustomDataFunction.MergeStrategy.CODEC.fieldOf("op").forGetter(CopyCustomDataFunction.CopyOperation::op)
               )
               .apply(var0, CopyCustomDataFunction.CopyOperation::new)
      );

      CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, CopyCustomDataFunction.MergeStrategy op) {
         super();
         this.sourcePath = sourcePath;
         this.targetPath = targetPath;
         this.op = op;
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
   }

   public static enum MergeStrategy implements StringRepresentable {
      REPLACE("replace") {
         @Override
         public void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException {
            var2.set(var1, (Tag)Iterables.getLast(var3));
         }
      },
      APPEND("append") {
         @Override
         public void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException {
            List var4 = var2.getOrCreate(var1, ListTag::new);
            var4.forEach(var1x -> {
               if (var1x instanceof ListTag) {
                  var3.forEach(var1xx -> ((ListTag)var1x).add(var1xx.copy()));
               }
            });
         }
      },
      MERGE("merge") {
         @Override
         public void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException {
            List var4 = var2.getOrCreate(var1, CompoundTag::new);
            var4.forEach(var1x -> {
               if (var1x instanceof CompoundTag) {
                  var3.forEach(var1xx -> {
                     if (var1xx instanceof CompoundTag) {
                        ((CompoundTag)var1x).merge((CompoundTag)var1xx);
                     }
                  });
               }
            });
         }
      };

      public static final Codec<CopyCustomDataFunction.MergeStrategy> CODEC = StringRepresentable.fromEnum(CopyCustomDataFunction.MergeStrategy::values);
      private final String name;

      public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

      MergeStrategy(final String nullxx) {
         this.name = nullxx;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
