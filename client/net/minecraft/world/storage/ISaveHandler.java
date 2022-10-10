package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;

public interface ISaveHandler {
   @Nullable
   WorldInfo func_75757_d();

   void func_75762_c() throws SessionLockException;

   IChunkLoader func_75763_a(Dimension var1);

   void func_75755_a(WorldInfo var1, NBTTagCompound var2);

   void func_75761_a(WorldInfo var1);

   IPlayerFileData func_75756_e();

   void func_75759_a();

   File func_75765_b();

   @Nullable
   File func_212423_a(DimensionType var1, String var2);

   TemplateManager func_186340_h();

   DataFixer func_197718_i();
}
