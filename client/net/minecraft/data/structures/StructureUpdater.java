package net.minecraft.data.structures;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

public class StructureUpdater implements SnbtToNbt.Filter {
   private static final Logger LOGGER = LogUtils.getLogger();

   public StructureUpdater() {
      super();
   }

   public CompoundTag apply(String var1, CompoundTag var2) {
      return var1.startsWith("data/minecraft/structures/") ? update(var1, var2) : var2;
   }

   public static CompoundTag update(String var0, CompoundTag var1) {
      return updateStructure(var0, patchVersion(var1));
   }

   private static CompoundTag patchVersion(CompoundTag var0) {
      if (!var0.contains("DataVersion", 99)) {
         var0.putInt("DataVersion", 500);
      }

      return var0;
   }

   private static CompoundTag updateStructure(String var0, CompoundTag var1) {
      StructureTemplate var2 = new StructureTemplate();
      int var3 = var1.getInt("DataVersion");
      boolean var4 = true;
      if (var3 < 3075) {
         LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", new Object[]{var3, 3075, var0});
      }

      CompoundTag var5 = NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, var1, var3);
      var2.load(var5);
      return var2.save(new CompoundTag());
   }
}
