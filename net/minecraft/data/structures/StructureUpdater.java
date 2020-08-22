package net.minecraft.data.structures;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureUpdater implements SnbtToNbt.Filter {
   public CompoundTag apply(String var1, CompoundTag var2) {
      return var1.startsWith("data/minecraft/structures/") ? updateStructure(patchVersion(var2)) : var2;
   }

   private static CompoundTag patchVersion(CompoundTag var0) {
      if (!var0.contains("DataVersion", 99)) {
         var0.putInt("DataVersion", 500);
      }

      return var0;
   }

   private static CompoundTag updateStructure(CompoundTag var0) {
      StructureTemplate var1 = new StructureTemplate();
      var1.load(NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, var0, var0.getInt("DataVersion")));
      return var1.save(new CompoundTag());
   }
}
