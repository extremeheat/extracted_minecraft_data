package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;

public class StructureProcessorList {
   private final List<StructureProcessor> list;

   public StructureProcessorList(final List<StructureProcessor> list) {
      super();
      this.list = list;
   }

   public List<StructureProcessor> list() {
      return this.list;
   }

   public String toString() {
      return "ProcessorList[" + String.valueOf(this.list) + "]";
   }
}
