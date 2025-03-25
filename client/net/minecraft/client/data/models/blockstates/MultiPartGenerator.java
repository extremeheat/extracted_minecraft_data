package net.minecraft.client.data.models.blockstates;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.world.level.block.Block;

public class MultiPartGenerator implements BlockModelDefinitionGenerator {
   private final Block block;
   private final List<Entry> parts = new ArrayList();

   private MultiPartGenerator(Block var1) {
      super();
      this.block = var1;
   }

   public Block block() {
      return this.block;
   }

   public static MultiPartGenerator multiPart(Block var0) {
      return new MultiPartGenerator(var0);
   }

   public MultiPartGenerator with(MultiVariant var1) {
      this.parts.add(new Entry(Optional.empty(), var1));
      return this;
   }

   private void validateCondition(Condition var1) {
      var1.instantiate(this.block.getStateDefinition());
   }

   public MultiPartGenerator with(Condition var1, MultiVariant var2) {
      this.validateCondition(var1);
      this.parts.add(new Entry(Optional.of(var1), var2));
      return this;
   }

   public MultiPartGenerator with(ConditionBuilder var1, MultiVariant var2) {
      return this.with(var1.build(), var2);
   }

   public BlockModelDefinition create() {
      return new BlockModelDefinition(Optional.empty(), Optional.of(new BlockModelDefinition.MultiPartDefinition(this.parts.stream().map(Entry::toUnbaked).toList())));
   }

   static record Entry(Optional<Condition> condition, MultiVariant variants) {
      Entry(Optional<Condition> var1, MultiVariant var2) {
         super();
         this.condition = var1;
         this.variants = var2;
      }

      public Selector toUnbaked() {
         return new Selector(this.condition, this.variants.toUnbaked());
      }
   }
}
