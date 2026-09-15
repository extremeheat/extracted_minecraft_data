package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record TemplateFeature(WeightedList<TemplateEntry> templates, Optional<Holder<StructureProcessorList>> processors) implements Feature {
   public static final MapCodec<TemplateFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WeightedList.codec(TemplateFeature.TemplateEntry.CODEC).fieldOf("templates").forGetter(TemplateFeature::templates), StructureProcessorType.LIST_CODEC.optionalFieldOf("processors").forGetter(TemplateFeature::processors)).apply(i, TemplateFeature::new));

   public TemplateFeature(final WeightedList<TemplateEntry> templates) {
      this(templates, Optional.empty());
   }

   public TemplateFeature {
      super();
   }

   public static TemplateFeature simple(final Identifier id, final Holder<StructureProcessorList> processors) {
      return new TemplateFeature(WeightedList.of(TemplateFeature.TemplateEntry.of(id)), Optional.of(processors));
   }

   public static TemplateFeature simple(final Identifier id) {
      return new TemplateFeature(WeightedList.of(TemplateFeature.TemplateEntry.of(id)), Optional.empty());
   }

   public MapCodec<TemplateFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      TemplateEntry templateEntry = this.templates.getRandomOrThrow(random);
      Rotation rotation = (Rotation)Util.getRandom(templateEntry.rotations(), random);
      StructureTemplateManager structureTemplateManager = level.getLevel().getServer().getStructureTemplateManager();
      StructureTemplate template = structureTemplateManager.getOrCreate(templateEntry.template());
      Vec3i offsetX = this.getRotatedOffset(rotation, Direction.Axis.X, template);
      Vec3i offsetZ = this.getRotatedOffset(rotation, Direction.Axis.Z, template);
      BlockPos pos = origin.offset(offsetX).offset(offsetZ);
      StructurePlaceSettings settings = (new StructurePlaceSettings()).setRotation(rotation).setRandom(random);
      if (this.processors.isPresent()) {
         for(StructureProcessor processor : ((StructureProcessorList)((Holder)this.processors.get()).value()).list()) {
            settings.addProcessor(processor);
         }
      }

      return template.placeInWorld(level, pos, pos, settings, random, 3);
   }

   private Vec3i getRotatedOffset(final Rotation rotation, final Direction.Axis axis, final StructureTemplate template) {
      return rotation.rotate(axis.getNegative()).getUnitVec3i().multiply(template.getSize().get(axis) / 2);
   }

   public static record TemplateEntry(Identifier template, List<Rotation> rotations) {
      public static final Codec<TemplateEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(TemplateEntry::template), Rotation.CODEC.listOf().optionalFieldOf("rotations", List.of(Rotation.values())).forGetter(TemplateEntry::rotations)).apply(i, TemplateEntry::new));

      public TemplateEntry {
         super();
      }

      public static TemplateEntry of(final Identifier template) {
         return new TemplateEntry(template, List.of(Rotation.values()));
      }
   }
}
