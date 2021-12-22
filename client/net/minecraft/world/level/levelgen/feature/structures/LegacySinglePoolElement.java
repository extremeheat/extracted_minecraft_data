package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class LegacySinglePoolElement extends SinglePoolElement {
   public static final Codec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(templateCodec(), processorsCodec(), projectionCodec()).apply(var0, LegacySinglePoolElement::new);
   });

   protected LegacySinglePoolElement(Either<ResourceLocation, StructureTemplate> var1, Supplier<StructureProcessorList> var2, StructureTemplatePool.Projection var3) {
      super(var1, var2, var3);
   }

   protected StructurePlaceSettings getSettings(Rotation var1, BoundingBox var2, boolean var3) {
      StructurePlaceSettings var4 = super.getSettings(var1, var2, var3);
      var4.popProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      var4.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      return var4;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.LEGACY;
   }

   public String toString() {
      return "LegacySingle[" + this.template + "]";
   }
}
