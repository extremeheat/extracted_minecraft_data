package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SaveHandlerMP implements ISaveHandler {
   public SaveHandlerMP() {
      super();
   }

   public WorldInfo func_75757_d() {
      return null;
   }

   public void func_75762_c() throws SessionLockException {
   }

   public IChunkLoader func_75763_a(Dimension var1) {
      return null;
   }

   public void func_75755_a(WorldInfo var1, NBTTagCompound var2) {
   }

   public void func_75761_a(WorldInfo var1) {
   }

   public IPlayerFileData func_75756_e() {
      return null;
   }

   public void func_75759_a() {
   }

   @Nullable
   public File func_212423_a(DimensionType var1, String var2) {
      return null;
   }

   public File func_75765_b() {
      return null;
   }

   public TemplateManager func_186340_h() {
      return null;
   }

   public DataFixer func_197718_i() {
      return null;
   }
}
