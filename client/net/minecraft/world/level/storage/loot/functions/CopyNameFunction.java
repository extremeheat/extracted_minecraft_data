package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   private static final ExtraCodecs.LateBoundIdMapper<String, Source> SOURCES = new ExtraCodecs.LateBoundIdMapper<String, Source>();
   public static final MapCodec<CopyNameFunction> CODEC;
   private final Source source;

   private CopyNameFunction(List<LootItemCondition> var1, Source var2) {
      super(var1);
      this.source = var2;
   }

   public LootItemFunctionType<CopyNameFunction> getType() {
      return LootItemFunctions.COPY_NAME;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.param);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Object var3 = var2.getOptionalParameter(this.source.param);
      if (var3 instanceof Nameable var4) {
         var1.set(DataComponents.CUSTOM_NAME, var4.getCustomName());
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(Source var0) {
      return simpleBuilder((var1) -> new CopyNameFunction(var1, var0));
   }

   static {
      for(LootContext.EntityTarget var3 : LootContext.EntityTarget.values()) {
         SOURCES.put(var3.getSerializedName(), new Source(var3.getParam()));
      }

      for(LootContext.BlockEntityTarget var7 : LootContext.BlockEntityTarget.values()) {
         SOURCES.put(var7.getSerializedName(), new Source(var7.getParam()));
      }

      CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(SOURCES.codec(Codec.STRING).fieldOf("source").forGetter((var0x) -> var0x.source)).apply(var0, CopyNameFunction::new));
   }

   public static record Source(ContextKey<?> param) {
      final ContextKey<?> param;

      public Source(ContextKey<?> var1) {
         super();
         this.param = var1;
      }
   }
}
