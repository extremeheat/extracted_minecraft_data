package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BlockEntityCustomNameToComponentFix extends DataFix {
   private static final Set<String> NAMEABLE_BLOCK_ENTITIES = Set.of("minecraft:beacon", "minecraft:banner", "minecraft:brewing_stand", "minecraft:chest", "minecraft:trapped_chest", "minecraft:dispenser", "minecraft:dropper", "minecraft:enchanting_table", "minecraft:furnace", "minecraft:hopper", "minecraft:shulker_box");

   public BlockEntityCustomNameToComponentFix(Schema var1) {
      super(var1, true);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      Type var2 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      Type var3 = this.getOutputSchema().getType(References.BLOCK_ENTITY);
      Type var4 = ExtraDataFixUtils.patchSubType(var2, var2, var3);
      return this.fixTypeEverywhereTyped("BlockEntityCustomNameToComponentFix", var2, var3, (var3x) -> {
         Optional var4x = var3x.getOptional(var1);
         return var4x.isPresent() && !NAMEABLE_BLOCK_ENTITIES.contains(var4x.get()) ? ExtraDataFixUtils.cast(var3, var3x) : Util.writeAndReadTypedOrThrow(ExtraDataFixUtils.cast(var4, var3x), var3, BlockEntityCustomNameToComponentFix::fixTagCustomName);
      });
   }

   public static <T> Dynamic<T> fixTagCustomName(Dynamic<T> var0) {
      String var1 = var0.get("CustomName").asString("");
      return var1.isEmpty() ? var0.remove("CustomName") : var0.set("CustomName", LegacyComponentDataFixUtils.createPlainTextComponent(var0.getOps(), var1));
   }
}
