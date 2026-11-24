package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemShulkerBoxColorFix extends DataFix {
   public static final String[] NAMES_BY_COLOR = new String[]{"minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

   public ItemShulkerBoxColorFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> itemStackType = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> idF = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> tagF = itemStackType.findField("tag");
      OpticFinder<?> blockEntityF = tagF.type().findField("BlockEntityTag");
      OpticFinder<?> shulkerBoxF = DSL.namedChoice("minecraft:shulker_box", this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:shulker_box"));
      BitSet dependencies = new BitSet();
      dependencies.set(((RecursivePoint.RecursivePointType)this.getInputSchema().getTypeRaw(References.BLOCK_ENTITY)).index());
      Type<?> rawItemStackType = this.getInputSchema().getTypeRaw(References.ITEM_STACK);
      return this.fixTypeEverywhereTyped("ItemShulkerBoxColorFix", rawItemStackType, (rawInput) -> {
         Typed<?> input = rawInput.out();
         Optional<Pair<String, String>> idOpt = input.getOptional(idF);
         if (idOpt.isPresent() && Objects.equals(((Pair)idOpt.get()).getSecond(), "minecraft:shulker_box")) {
            Optional<? extends Typed<?>> tagOpt = input.getOptionalTyped(tagF);
            if (tagOpt.isPresent()) {
               Typed<?> tag = (Typed)tagOpt.get();
               Optional<? extends Typed<?>> blockEntityOpt = tag.getOptionalTyped(blockEntityF);
               if (blockEntityOpt.isPresent()) {
                  Typed<?> blockEntity = (Typed)blockEntityOpt.get();
                  Optional<? extends Typed<?>> shulkerBoxOpt = blockEntity.getOptionalTyped(shulkerBoxF);
                  if (shulkerBoxOpt.isPresent()) {
                     Typed<?> shulkerBox = (Typed)shulkerBoxOpt.get();
                     Dynamic<?> shulkerBoxRest = (Dynamic)shulkerBox.get(DSL.remainderFinder());
                     OptionalDynamic<?> maybeColor = shulkerBoxRest.get("Color");
                     if (maybeColor.result().isPresent()) {
                        int color = maybeColor.asInt(0);
                        Typed<?> newShulkerBox = shulkerBox.set(DSL.remainderFinder(), shulkerBoxRest.remove("Color"));
                        Typed<?> output = input.set(tagF, tag.set(blockEntityF, blockEntity.set(shulkerBoxF, newShulkerBox))).set(idF, Pair.of(References.ITEM_NAME.typeName(), NAMES_BY_COLOR[color % 16]));
                        return fold(rawItemStackType, output);
                     }
                  }
               }
            }
         }

         return rawInput;
      }, dependencies);
   }

   private static <A> Typed<A> fold(final Type<A> type, final Typed<?> unfolded) {
      return new Typed(type, unfolded.getOps(), unfolded.getValue());
   }
}
