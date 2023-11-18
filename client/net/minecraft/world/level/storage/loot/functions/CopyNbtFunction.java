package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;

public class CopyNbtFunction extends LootItemConditionalFunction {
   public static final Codec<CopyNbtFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  NbtProviders.CODEC.fieldOf("source").forGetter(var0x -> var0x.source),
                  CopyNbtFunction.CopyOperation.CODEC.listOf().fieldOf("ops").forGetter(var0x -> var0x.operations)
               )
            )
            .apply(var0, CopyNbtFunction::new)
   );
   private final NbtProvider source;
   private final List<CopyNbtFunction.CopyOperation> operations;

   CopyNbtFunction(List<LootItemCondition> var1, NbtProvider var2, List<CopyNbtFunction.CopyOperation> var3) {
      super(var1);
      this.source = var2;
      this.operations = List.copyOf(var3);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_NBT;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.source.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      Tag var3 = this.source.get(var2);
      if (var3 != null) {
         this.operations.forEach(var2x -> var2x.apply(var1::getOrCreateTag, var3));
      }

      return var1;
   }

   public static CopyNbtFunction.Builder copyData(NbtProvider var0) {
      return new CopyNbtFunction.Builder(var0);
   }

   public static CopyNbtFunction.Builder copyData(LootContext.EntityTarget var0) {
      return new CopyNbtFunction.Builder(ContextNbtProvider.forContextEntity(var0));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyNbtFunction.Builder> {
      private final NbtProvider source;
      private final List<CopyNbtFunction.CopyOperation> ops = Lists.newArrayList();

      Builder(NbtProvider var1) {
         super();
         this.source = var1;
      }

      public CopyNbtFunction.Builder copy(String var1, String var2, CopyNbtFunction.MergeStrategy var3) {
         try {
            this.ops.add(new CopyNbtFunction.CopyOperation(CopyNbtFunction.Path.of(var1), CopyNbtFunction.Path.of(var2), var3));
            return this;
         } catch (CommandSyntaxException var5) {
            throw new IllegalArgumentException(var5);
         }
      }

      public CopyNbtFunction.Builder copy(String var1, String var2) {
         return this.copy(var1, var2, CopyNbtFunction.MergeStrategy.REPLACE);
      }

      protected CopyNbtFunction.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new CopyNbtFunction(this.getConditions(), this.source, this.ops);
      }
   }

   static record CopyOperation(CopyNbtFunction.Path b, CopyNbtFunction.Path c, CopyNbtFunction.MergeStrategy d) {
      private final CopyNbtFunction.Path sourcePath;
      private final CopyNbtFunction.Path targetPath;
      private final CopyNbtFunction.MergeStrategy op;
      public static final Codec<CopyNbtFunction.CopyOperation> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  CopyNbtFunction.Path.CODEC.fieldOf("source").forGetter(CopyNbtFunction.CopyOperation::sourcePath),
                  CopyNbtFunction.Path.CODEC.fieldOf("target").forGetter(CopyNbtFunction.CopyOperation::targetPath),
                  CopyNbtFunction.MergeStrategy.CODEC.fieldOf("op").forGetter(CopyNbtFunction.CopyOperation::op)
               )
               .apply(var0, CopyNbtFunction.CopyOperation::new)
      );

      CopyOperation(CopyNbtFunction.Path var1, CopyNbtFunction.Path var2, CopyNbtFunction.MergeStrategy var3) {
         super();
         this.sourcePath = var1;
         this.targetPath = var2;
         this.op = var3;
      }

      public void apply(Supplier<Tag> var1, Tag var2) {
         try {
            List var3 = this.sourcePath.path().get(var2);
            if (!var3.isEmpty()) {
               this.op.merge((Tag)var1.get(), this.targetPath.path(), var3);
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

      public static final Codec<CopyNbtFunction.MergeStrategy> CODEC = StringRepresentable.fromEnum(CopyNbtFunction.MergeStrategy::values);
      private final String name;

      public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

      MergeStrategy(String var3) {
         this.name = var3;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }

   static record Path(String b, NbtPathArgument.NbtPath c) {
      private final String string;
      private final NbtPathArgument.NbtPath path;
      public static final Codec<CopyNbtFunction.Path> CODEC = Codec.STRING.comapFlatMap(var0 -> {
         try {
            return DataResult.success(of(var0));
         } catch (CommandSyntaxException var2) {
            return DataResult.error(() -> "Failed to parse path " + var0 + ": " + var2.getMessage());
         }
      }, CopyNbtFunction.Path::string);

      private Path(String var1, NbtPathArgument.NbtPath var2) {
         super();
         this.string = var1;
         this.path = var2;
      }

      public static CopyNbtFunction.Path of(String var0) throws CommandSyntaxException {
         NbtPathArgument.NbtPath var1 = new NbtPathArgument().parse(new StringReader(var0));
         return new CopyNbtFunction.Path(var0, var1);
      }
   }
}
