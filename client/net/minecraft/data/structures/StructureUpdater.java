package net.minecraft.data.structures;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

public class StructureUpdater implements SnbtToNbt.Filter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PREFIX = PackType.SERVER_DATA.getDirectory() + "/minecraft/structure/";

   public StructureUpdater() {
      super();
   }

   @Override
   public CompoundTag apply(String var1, CompoundTag var2) {
      return var1.startsWith(PREFIX) ? update(var1, var2) : var2;
   }

   public static CompoundTag update(String var0, CompoundTag var1) {
      StructureTemplate var2 = new StructureTemplate();
      int var3 = NbtUtils.getDataVersion(var1, 500);
      short var4 = 4053;
      if (var3 < 4053) {
         LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", new Object[]{var3, 4053, var0});
      }

      CompoundTag var5 = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), var1, var3);
      var2.load(BuiltInRegistries.BLOCK.asLookup(), var5);
      return var2.save(new CompoundTag());
   }
}
