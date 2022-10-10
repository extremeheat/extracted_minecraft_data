package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.ServerTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.gen.feature.structure.StructureIO;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {
   private static final Logger field_151505_a = LogManager.getLogger();
   private final Map<ChunkPos, NBTTagCompound> field_75828_a = Maps.newHashMap();
   private final File field_75825_d;
   private final DataFixer field_193416_e;
   private LegacyStructureDataUtil field_208031_e;
   private boolean field_183014_e;

   public AnvilChunkLoader(File var1, DataFixer var2) {
      super();
      this.field_75825_d = var1;
      this.field_193416_e = var2;
   }

   @Nullable
   private NBTTagCompound func_208030_a(IWorld var1, int var2, int var3) throws IOException {
      return this.func_212146_a(var1.func_201675_m().func_186058_p(), var1.func_175693_T(), var2, var3);
   }

   @Nullable
   private NBTTagCompound func_212146_a(DimensionType var1, @Nullable WorldSavedDataStorage var2, int var3, int var4) throws IOException {
      NBTTagCompound var5 = (NBTTagCompound)this.field_75828_a.get(new ChunkPos(var3, var4));
      if (var5 != null) {
         return var5;
      } else {
         DataInputStream var7 = RegionFileCache.func_76549_c(this.field_75825_d, var3, var4);
         if (var7 == null) {
            return null;
         } else {
            NBTTagCompound var6 = CompressedStreamTools.func_74794_a(var7);
            var7.close();
            int var8 = var6.func_150297_b("DataVersion", 99) ? var6.func_74762_e("DataVersion") : -1;
            if (var8 < 1493) {
               var6 = NBTUtil.func_210821_a(this.field_193416_e, DataFixTypes.CHUNK, var6, var8, 1493);
               if (var6.func_74775_l("Level").func_74767_n("hasLegacyStructureData")) {
                  this.func_212429_a(var1, var2);
                  var6 = this.field_208031_e.func_212181_a(var6);
               }
            }

            var6 = NBTUtil.func_210822_a(this.field_193416_e, DataFixTypes.CHUNK, var6, Math.max(1493, var8));
            if (var8 < 1631) {
               var6.func_74768_a("DataVersion", 1631);
               this.func_75824_a(new ChunkPos(var3, var4), var6);
            }

            return var6;
         }
      }
   }

   public void func_212429_a(DimensionType var1, @Nullable WorldSavedDataStorage var2) {
      if (this.field_208031_e == null) {
         this.field_208031_e = LegacyStructureDataUtil.func_212183_a(var1, var2);
      }

   }

   @Nullable
   public Chunk func_199813_a(IWorld var1, int var2, int var3, Consumer<Chunk> var4) throws IOException {
      NBTTagCompound var5 = this.func_208030_a(var1, var2, var3);
      if (var5 == null) {
         return null;
      } else {
         Chunk var6 = this.func_75822_a(var1, var2, var3, var5);
         if (var6 != null) {
            var4.accept(var6);
            this.func_199814_a(var5.func_74775_l("Level"), var6);
         }

         return var6;
      }
   }

   @Nullable
   public ChunkPrimer func_202152_b(IWorld var1, int var2, int var3, Consumer<IChunk> var4) throws IOException {
      NBTTagCompound var5;
      try {
         var5 = this.func_208030_a(var1, var2, var3);
      } catch (ReportedException var7) {
         if (var7.getCause() instanceof IOException) {
            throw (IOException)var7.getCause();
         }

         throw var7;
      }

      if (var5 == null) {
         return null;
      } else {
         ChunkPrimer var6 = this.func_202165_b(var1, var2, var3, var5);
         if (var6 != null) {
            var4.accept(var6);
         }

         return var6;
      }
   }

   @Nullable
   protected Chunk func_75822_a(IWorld var1, int var2, int var3, NBTTagCompound var4) {
      if (var4.func_150297_b("Level", 10) && var4.func_74775_l("Level").func_150297_b("Status", 8)) {
         ChunkStatus.Type var5 = this.func_202161_a(var4);
         if (var5 != ChunkStatus.Type.LEVELCHUNK) {
            return null;
         } else {
            NBTTagCompound var6 = var4.func_74775_l("Level");
            if (!var6.func_150297_b("Sections", 9)) {
               field_151505_a.error("Chunk file at {},{} is missing block data, skipping", var2, var3);
               return null;
            } else {
               Chunk var7 = this.func_75823_a(var1, var6);
               if (!var7.func_76600_a(var2, var3)) {
                  field_151505_a.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", var2, var3, var2, var3, var7.field_76635_g, var7.field_76647_h);
                  var6.func_74768_a("xPos", var2);
                  var6.func_74768_a("zPos", var3);
                  var7 = this.func_75823_a(var1, var6);
               }

               return var7;
            }
         }
      } else {
         field_151505_a.error("Chunk file at {},{} is missing level data, skipping", var2, var3);
         return null;
      }
   }

   @Nullable
   protected ChunkPrimer func_202165_b(IWorld var1, int var2, int var3, NBTTagCompound var4) {
      if (var4.func_150297_b("Level", 10) && var4.func_74775_l("Level").func_150297_b("Status", 8)) {
         ChunkStatus.Type var5 = this.func_202161_a(var4);
         if (var5 == ChunkStatus.Type.LEVELCHUNK) {
            return new ChunkPrimerWrapper(this.func_75822_a(var1, var2, var3, var4));
         } else {
            NBTTagCompound var6 = var4.func_74775_l("Level");
            return this.func_202155_b(var1, var6);
         }
      } else {
         field_151505_a.error("Chunk file at {},{} is missing level data, skipping", var2, var3);
         return null;
      }
   }

   public void func_75816_a(World var1, IChunk var2) throws IOException, SessionLockException {
      var1.func_72906_B();

      try {
         NBTTagCompound var3 = new NBTTagCompound();
         NBTTagCompound var4 = new NBTTagCompound();
         var3.func_74768_a("DataVersion", 1631);
         ChunkPos var5 = var2.func_76632_l();
         var3.func_74782_a("Level", var4);
         if (var2.func_201589_g().func_202129_d() == ChunkStatus.Type.LEVELCHUNK) {
            this.func_75820_a((Chunk)var2, var1, var4);
         } else {
            NBTTagCompound var6 = this.func_208030_a(var1, var5.field_77276_a, var5.field_77275_b);
            if (var6 != null && this.func_202161_a(var6) == ChunkStatus.Type.LEVELCHUNK) {
               return;
            }

            this.func_202156_a((ChunkPrimer)var2, var1, var4);
         }

         this.func_75824_a(var5, var3);
      } catch (Exception var7) {
         field_151505_a.error("Failed to save chunk", var7);
      }

   }

   protected void func_75824_a(ChunkPos var1, NBTTagCompound var2) {
      this.field_75828_a.put(var1, var2);
      ThreadedFileIOBase.func_178779_a().func_75735_a(this);
   }

   public boolean func_75814_c() {
      Iterator var1 = this.field_75828_a.entrySet().iterator();
      if (!var1.hasNext()) {
         if (this.field_183014_e) {
            field_151505_a.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.field_75825_d.getName());
         }

         return false;
      } else {
         Entry var2 = (Entry)var1.next();
         var1.remove();
         ChunkPos var3 = (ChunkPos)var2.getKey();
         NBTTagCompound var4 = (NBTTagCompound)var2.getValue();
         if (var4 == null) {
            return true;
         } else {
            try {
               DataOutputStream var5 = RegionFileCache.func_76552_d(this.field_75825_d, var3.field_77276_a, var3.field_77275_b);
               CompressedStreamTools.func_74800_a(var4, var5);
               var5.close();
               if (this.field_208031_e != null) {
                  this.field_208031_e.func_208216_a(var3.func_201841_a());
               }
            } catch (Exception var6) {
               field_151505_a.error("Failed to save chunk", var6);
            }

            return true;
         }
      }
   }

   private ChunkStatus.Type func_202161_a(@Nullable NBTTagCompound var1) {
      if (var1 != null) {
         ChunkStatus var2 = ChunkStatus.func_202127_a(var1.func_74775_l("Level").func_74779_i("Status"));
         if (var2 != null) {
            return var2.func_202129_d();
         }
      }

      return ChunkStatus.Type.PROTOCHUNK;
   }

   public void func_75818_b() {
      try {
         this.field_183014_e = true;

         while(true) {
            if (this.func_75814_c()) {
               continue;
            }
         }
      } finally {
         this.field_183014_e = false;
      }

   }

   private void func_202156_a(ChunkPrimer var1, World var2, NBTTagCompound var3) {
      int var4 = var1.func_76632_l().field_77276_a;
      int var5 = var1.func_76632_l().field_77275_b;
      var3.func_74768_a("xPos", var4);
      var3.func_74768_a("zPos", var5);
      var3.func_74772_a("LastUpdate", var2.func_82737_E());
      var3.func_74772_a("InhabitedTime", var1.func_209216_m());
      var3.func_74778_a("Status", var1.func_201589_g().func_202125_b());
      UpgradeData var6 = var1.func_201631_p();
      if (!var6.func_196988_a()) {
         var3.func_74782_a("UpgradeData", var6.func_196992_b());
      }

      ChunkSection[] var7 = var1.func_76587_i();
      NBTTagList var8 = this.func_202159_a(var2, var7);
      var3.func_74782_a("Sections", var8);
      Biome[] var9 = var1.func_201590_e();
      int[] var10 = var9 != null ? new int[var9.length] : new int[0];
      if (var9 != null) {
         for(int var11 = 0; var11 < var9.length; ++var11) {
            var10[var11] = IRegistry.field_212624_m.func_148757_b(var9[var11]);
         }
      }

      var3.func_74783_a("Biomes", var10);
      NBTTagList var19 = new NBTTagList();
      Iterator var12 = var1.func_201652_l().iterator();

      NBTTagCompound var13;
      while(var12.hasNext()) {
         var13 = (NBTTagCompound)var12.next();
         var19.add((INBTBase)var13);
      }

      var3.func_74782_a("Entities", var19);
      NBTTagList var20 = new NBTTagList();
      Iterator var21 = var1.func_201638_j().iterator();

      while(var21.hasNext()) {
         BlockPos var14 = (BlockPos)var21.next();
         TileEntity var15 = var1.func_175625_s(var14);
         if (var15 != null) {
            NBTTagCompound var16 = new NBTTagCompound();
            var15.func_189515_b(var16);
            var20.add((INBTBase)var16);
         } else {
            var20.add((INBTBase)var1.func_201579_g(var14));
         }
      }

      var3.func_74782_a("TileEntities", var20);
      var3.func_74782_a("Lights", func_202163_a(var1.func_201647_i()));
      var3.func_74782_a("PostProcessing", func_202163_a(var1.func_201645_n()));
      var3.func_74782_a("ToBeTicked", var1.func_205218_i_().func_205379_a());
      var3.func_74782_a("LiquidsToBeTicked", var1.func_212247_j().func_205379_a());
      var13 = new NBTTagCompound();
      Iterator var22 = var1.func_201634_m().iterator();

      while(var22.hasNext()) {
         Heightmap.Type var24 = (Heightmap.Type)var22.next();
         var13.func_74782_a(var24.func_203500_b(), new NBTTagLongArray(var1.func_201642_a(var24).func_202269_a()));
      }

      var3.func_74782_a("Heightmaps", var13);
      NBTTagCompound var23 = new NBTTagCompound();
      GenerationStage.Carving[] var25 = GenerationStage.Carving.values();
      int var26 = var25.length;

      for(int var17 = 0; var17 < var26; ++var17) {
         GenerationStage.Carving var18 = var25[var17];
         var23.func_74773_a(var18.toString(), var1.func_205749_a(var18).toByteArray());
      }

      var3.func_74782_a("CarvingMasks", var23);
      var3.func_74782_a("Structures", this.func_202160_a(var4, var5, var1.func_201609_c(), var1.func_201604_d()));
   }

   private void func_75820_a(Chunk var1, World var2, NBTTagCompound var3) {
      var3.func_74768_a("xPos", var1.field_76635_g);
      var3.func_74768_a("zPos", var1.field_76647_h);
      var3.func_74772_a("LastUpdate", var2.func_82737_E());
      var3.func_74772_a("InhabitedTime", var1.func_177416_w());
      var3.func_74778_a("Status", var1.func_201589_g().func_202125_b());
      UpgradeData var4 = var1.func_196966_y();
      if (!var4.func_196988_a()) {
         var3.func_74782_a("UpgradeData", var4.func_196992_b());
      }

      ChunkSection[] var5 = var1.func_76587_i();
      NBTTagList var6 = this.func_202159_a(var2, var5);
      var3.func_74782_a("Sections", var6);
      Biome[] var7 = var1.func_201590_e();
      int[] var8 = new int[var7.length];

      for(int var9 = 0; var9 < var7.length; ++var9) {
         var8[var9] = IRegistry.field_212624_m.func_148757_b(var7[var9]);
      }

      var3.func_74783_a("Biomes", var8);
      var1.func_177409_g(false);
      NBTTagList var15 = new NBTTagList();

      Iterator var11;
      for(int var10 = 0; var10 < var1.func_177429_s().length; ++var10) {
         var11 = var1.func_177429_s()[var10].iterator();

         while(var11.hasNext()) {
            Entity var12 = (Entity)var11.next();
            NBTTagCompound var13 = new NBTTagCompound();
            if (var12.func_70039_c(var13)) {
               var1.func_177409_g(true);
               var15.add((INBTBase)var13);
            }
         }
      }

      var3.func_74782_a("Entities", var15);
      NBTTagList var16 = new NBTTagList();
      var11 = var1.func_203066_o().iterator();

      while(var11.hasNext()) {
         BlockPos var18 = (BlockPos)var11.next();
         TileEntity var20 = var1.func_175625_s(var18);
         NBTTagCompound var14;
         if (var20 != null) {
            var14 = new NBTTagCompound();
            var20.func_189515_b(var14);
            var14.func_74757_a("keepPacked", false);
            var16.add((INBTBase)var14);
         } else {
            var14 = var1.func_201579_g(var18);
            if (var14 != null) {
               var14.func_74757_a("keepPacked", true);
               var16.add((INBTBase)var14);
            }
         }
      }

      var3.func_74782_a("TileEntities", var16);
      if (var2.func_205220_G_() instanceof ServerTickList) {
         var3.func_74782_a("TileTicks", ((ServerTickList)var2.func_205220_G_()).func_205363_a(var1));
      }

      if (var2.func_205219_F_() instanceof ServerTickList) {
         var3.func_74782_a("LiquidTicks", ((ServerTickList)var2.func_205219_F_()).func_205363_a(var1));
      }

      var3.func_74782_a("PostProcessing", func_202163_a(var1.func_201614_D()));
      if (var1.func_205218_i_() instanceof ChunkPrimerTickList) {
         var3.func_74782_a("ToBeTicked", ((ChunkPrimerTickList)var1.func_205218_i_()).func_205379_a());
      }

      if (var1.func_212247_j() instanceof ChunkPrimerTickList) {
         var3.func_74782_a("LiquidsToBeTicked", ((ChunkPrimerTickList)var1.func_212247_j()).func_205379_a());
      }

      NBTTagCompound var17 = new NBTTagCompound();
      Iterator var19 = var1.func_201615_v().iterator();

      while(var19.hasNext()) {
         Heightmap.Type var21 = (Heightmap.Type)var19.next();
         if (var21.func_207512_c() == Heightmap.Usage.LIVE_WORLD) {
            var17.func_74782_a(var21.func_203500_b(), new NBTTagLongArray(var1.func_201608_a(var21).func_202269_a()));
         }
      }

      var3.func_74782_a("Heightmaps", var17);
      var3.func_74782_a("Structures", this.func_202160_a(var1.field_76635_g, var1.field_76647_h, var1.func_201609_c(), var1.func_201604_d()));
   }

   private Chunk func_75823_a(IWorld var1, NBTTagCompound var2) {
      int var3 = var2.func_74762_e("xPos");
      int var4 = var2.func_74762_e("zPos");
      Biome[] var5 = new Biome[256];
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      if (var2.func_150297_b("Biomes", 11)) {
         int[] var7 = var2.func_74759_k("Biomes");

         for(int var8 = 0; var8 < var7.length; ++var8) {
            var5[var8] = (Biome)IRegistry.field_212624_m.func_148754_a(var7[var8]);
            if (var5[var8] == null) {
               var5[var8] = var1.func_72863_F().func_201711_g().func_202090_b().func_180300_a(var6.func_181079_c((var8 & 15) + (var3 << 4), 0, (var8 >> 4 & 15) + (var4 << 4)), Biomes.field_76772_c);
            }
         }
      } else {
         for(int var20 = 0; var20 < var5.length; ++var20) {
            var5[var20] = var1.func_72863_F().func_201711_g().func_202090_b().func_180300_a(var6.func_181079_c((var20 & 15) + (var3 << 4), 0, (var20 >> 4 & 15) + (var4 << 4)), Biomes.field_76772_c);
         }
      }

      UpgradeData var21 = var2.func_150297_b("UpgradeData", 10) ? new UpgradeData(var2.func_74775_l("UpgradeData")) : UpgradeData.field_196994_a;
      ChunkPrimerTickList var22 = new ChunkPrimerTickList((var0) -> {
         return var0.func_176223_P().func_196958_f();
      }, IRegistry.field_212618_g::func_177774_c, IRegistry.field_212618_g::func_82594_a, new ChunkPos(var3, var4));
      ChunkPrimerTickList var9 = new ChunkPrimerTickList((var0) -> {
         return var0 == Fluids.field_204541_a;
      }, IRegistry.field_212619_h::func_177774_c, IRegistry.field_212619_h::func_82594_a, new ChunkPos(var3, var4));
      long var10 = var2.func_74763_f("InhabitedTime");
      Chunk var12 = new Chunk(var1.func_201672_e(), var3, var4, var5, var21, var22, var9, var10);
      var12.func_201613_c(var2.func_74779_i("Status"));
      NBTTagList var13 = var2.func_150295_c("Sections", 10);
      var12.func_76602_a(this.func_202158_a(var1, var13));
      NBTTagCompound var14 = var2.func_74775_l("Heightmaps");
      Heightmap.Type[] var15 = Heightmap.Type.values();
      int var16 = var15.length;

      int var17;
      for(var17 = 0; var17 < var16; ++var17) {
         Heightmap.Type var18 = var15[var17];
         if (var18.func_207512_c() == Heightmap.Usage.LIVE_WORLD) {
            String var19 = var18.func_203500_b();
            if (var14.func_150297_b(var19, 12)) {
               var12.func_201607_a(var18, var14.func_197645_o(var19));
            } else {
               var12.func_201608_a(var18).func_202266_a();
            }
         }
      }

      NBTTagCompound var23 = var2.func_74775_l("Structures");
      var12.func_201612_a(this.func_202162_c(var1, var23));
      var12.func_201606_b(this.func_202167_b(var23));
      NBTTagList var24 = var2.func_150295_c("PostProcessing", 9);

      for(var17 = 0; var17 < var24.size(); ++var17) {
         NBTTagList var25 = var24.func_202169_e(var17);

         for(int var26 = 0; var26 < var25.size(); ++var26) {
            var12.func_201610_a(var25.func_202170_f(var26), var17);
         }
      }

      var22.func_205380_a(var2.func_150295_c("ToBeTicked", 9));
      var9.func_205380_a(var2.func_150295_c("LiquidsToBeTicked", 9));
      if (var2.func_74767_n("shouldSave")) {
         var12.func_177427_f(true);
      }

      return var12;
   }

   private void func_199814_a(NBTTagCompound var1, Chunk var2) {
      NBTTagList var3 = var1.func_150295_c("Entities", 10);
      World var4 = var2.func_177412_p();

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         NBTTagCompound var6 = var3.func_150305_b(var5);
         func_186050_a(var6, var4, var2);
         var2.func_177409_g(true);
      }

      NBTTagList var10 = var1.func_150295_c("TileEntities", 10);

      for(int var11 = 0; var11 < var10.size(); ++var11) {
         NBTTagCompound var7 = var10.func_150305_b(var11);
         boolean var8 = var7.func_74767_n("keepPacked");
         if (var8) {
            var2.func_201591_a(var7);
         } else {
            TileEntity var9 = TileEntity.func_203403_c(var7);
            if (var9 != null) {
               var2.func_150813_a(var9);
            }
         }
      }

      if (var1.func_150297_b("TileTicks", 9) && var4.func_205220_G_() instanceof ServerTickList) {
         ((ServerTickList)var4.func_205220_G_()).func_205369_a(var1.func_150295_c("TileTicks", 10));
      }

      if (var1.func_150297_b("LiquidTicks", 9) && var4.func_205219_F_() instanceof ServerTickList) {
         ((ServerTickList)var4.func_205219_F_()).func_205369_a(var1.func_150295_c("LiquidTicks", 10));
      }

   }

   private ChunkPrimer func_202155_b(IWorld var1, NBTTagCompound var2) {
      int var3 = var2.func_74762_e("xPos");
      int var4 = var2.func_74762_e("zPos");
      Biome[] var5 = new Biome[256];
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      if (var2.func_150297_b("Biomes", 11)) {
         int[] var7 = var2.func_74759_k("Biomes");

         for(int var8 = 0; var8 < var7.length; ++var8) {
            var5[var8] = (Biome)IRegistry.field_212624_m.func_148754_a(var7[var8]);
            if (var5[var8] == null) {
               var5[var8] = var1.func_72863_F().func_201711_g().func_202090_b().func_180300_a(var6.func_181079_c((var8 & 15) + (var3 << 4), 0, (var8 >> 4 & 15) + (var4 << 4)), Biomes.field_76772_c);
            }
         }
      } else {
         for(int var20 = 0; var20 < var5.length; ++var20) {
            var5[var20] = var1.func_72863_F().func_201711_g().func_202090_b().func_180300_a(var6.func_181079_c((var20 & 15) + (var3 << 4), 0, (var20 >> 4 & 15) + (var4 << 4)), Biomes.field_76772_c);
         }
      }

      UpgradeData var21 = var2.func_150297_b("UpgradeData", 10) ? new UpgradeData(var2.func_74775_l("UpgradeData")) : UpgradeData.field_196994_a;
      ChunkPrimer var22 = new ChunkPrimer(var3, var4, var21);
      var22.func_201577_a(var5);
      var22.func_209215_b(var2.func_74763_f("InhabitedTime"));
      var22.func_201650_c(var2.func_74779_i("Status"));
      NBTTagList var9 = var2.func_150295_c("Sections", 10);
      var22.func_201630_a(this.func_202158_a(var1, var9));
      NBTTagList var10 = var2.func_150295_c("Entities", 10);

      for(int var11 = 0; var11 < var10.size(); ++var11) {
         var22.func_201626_b(var10.func_150305_b(var11));
      }

      NBTTagList var23 = var2.func_150295_c("TileEntities", 10);

      for(int var12 = 0; var12 < var23.size(); ++var12) {
         NBTTagCompound var13 = var23.func_150305_b(var12);
         var22.func_201591_a(var13);
      }

      NBTTagList var24 = var2.func_150295_c("Lights", 9);

      for(int var25 = 0; var25 < var24.size(); ++var25) {
         NBTTagList var14 = var24.func_202169_e(var25);

         for(int var15 = 0; var15 < var14.size(); ++var15) {
            var22.func_201646_a(var14.func_202170_f(var15), var25);
         }
      }

      NBTTagList var26 = var2.func_150295_c("PostProcessing", 9);

      for(int var27 = 0; var27 < var26.size(); ++var27) {
         NBTTagList var29 = var26.func_202169_e(var27);

         for(int var16 = 0; var16 < var29.size(); ++var16) {
            var22.func_201636_b(var29.func_202170_f(var16), var27);
         }
      }

      var22.func_205218_i_().func_205380_a(var2.func_150295_c("ToBeTicked", 9));
      var22.func_212247_j().func_205380_a(var2.func_150295_c("LiquidsToBeTicked", 9));
      NBTTagCompound var28 = var2.func_74775_l("Heightmaps");
      Iterator var30 = var28.func_150296_c().iterator();

      while(var30.hasNext()) {
         String var32 = (String)var30.next();
         var22.func_201643_a(Heightmap.Type.func_203501_a(var32), var28.func_197645_o(var32));
      }

      NBTTagCompound var31 = var2.func_74775_l("Structures");
      var22.func_201648_a(this.func_202162_c(var1, var31));
      var22.func_201641_b(this.func_202167_b(var31));
      NBTTagCompound var33 = var2.func_74775_l("CarvingMasks");
      Iterator var17 = var33.func_150296_c().iterator();

      while(var17.hasNext()) {
         String var18 = (String)var17.next();
         GenerationStage.Carving var19 = GenerationStage.Carving.valueOf(var18);
         var22.func_205767_a(var19, BitSet.valueOf(var33.func_74770_j(var18)));
      }

      return var22;
   }

   private NBTTagList func_202159_a(World var1, ChunkSection[] var2) {
      NBTTagList var3 = new NBTTagList();
      boolean var4 = var1.field_73011_w.func_191066_m();
      ChunkSection[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         ChunkSection var8 = var5[var7];
         if (var8 != Chunk.field_186036_a) {
            NBTTagCompound var9 = new NBTTagCompound();
            var9.func_74774_a("Y", (byte)(var8.func_76662_d() >> 4 & 255));
            var8.func_186049_g().func_196963_b(var9, "Palette", "BlockStates");
            var9.func_74773_a("BlockLight", var8.func_76661_k().func_177481_a());
            if (var4) {
               var9.func_74773_a("SkyLight", var8.func_76671_l().func_177481_a());
            } else {
               var9.func_74773_a("SkyLight", new byte[var8.func_76661_k().func_177481_a().length]);
            }

            var3.add((INBTBase)var9);
         }
      }

      return var3;
   }

   private ChunkSection[] func_202158_a(IWorldReaderBase var1, NBTTagList var2) {
      boolean var3 = true;
      ChunkSection[] var4 = new ChunkSection[16];
      boolean var5 = var1.func_201675_m().func_191066_m();

      for(int var6 = 0; var6 < var2.size(); ++var6) {
         NBTTagCompound var7 = var2.func_150305_b(var6);
         byte var8 = var7.func_74771_c("Y");
         ChunkSection var9 = new ChunkSection(var8 << 4, var5);
         var9.func_186049_g().func_196964_a(var7, "Palette", "BlockStates");
         var9.func_76659_c(new NibbleArray(var7.func_74770_j("BlockLight")));
         if (var5) {
            var9.func_76666_d(new NibbleArray(var7.func_74770_j("SkyLight")));
         }

         var9.func_76672_e();
         var4[var8] = var9;
      }

      return var4;
   }

   private NBTTagCompound func_202160_a(int var1, int var2, Map<String, StructureStart> var3, Map<String, LongSet> var4) {
      NBTTagCompound var5 = new NBTTagCompound();
      NBTTagCompound var6 = new NBTTagCompound();
      Iterator var7 = var3.entrySet().iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         var6.func_74782_a((String)var8.getKey(), ((StructureStart)var8.getValue()).func_143021_a(var1, var2));
      }

      var5.func_74782_a("Starts", var6);
      NBTTagCompound var10 = new NBTTagCompound();
      Iterator var11 = var4.entrySet().iterator();

      while(var11.hasNext()) {
         Entry var9 = (Entry)var11.next();
         var10.func_74782_a((String)var9.getKey(), new NBTTagLongArray((LongSet)var9.getValue()));
      }

      var5.func_74782_a("References", var10);
      return var5;
   }

   private Map<String, StructureStart> func_202162_c(IWorld var1, NBTTagCompound var2) {
      HashMap var3 = Maps.newHashMap();
      NBTTagCompound var4 = var2.func_74775_l("Starts");
      Iterator var5 = var4.func_150296_c().iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         var3.put(var6, StructureIO.func_202602_a(var4.func_74775_l(var6), var1));
      }

      return var3;
   }

   private Map<String, LongSet> func_202167_b(NBTTagCompound var1) {
      HashMap var2 = Maps.newHashMap();
      NBTTagCompound var3 = var1.func_74775_l("References");
      Iterator var4 = var3.func_150296_c().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var2.put(var5, new LongOpenHashSet(var3.func_197645_o(var5)));
      }

      return var2;
   }

   public static NBTTagList func_202163_a(ShortList[] var0) {
      NBTTagList var1 = new NBTTagList();
      ShortList[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ShortList var5 = var2[var4];
         NBTTagList var6 = new NBTTagList();
         if (var5 != null) {
            ShortListIterator var7 = var5.iterator();

            while(var7.hasNext()) {
               Short var8 = (Short)var7.next();
               var6.add((INBTBase)(new NBTTagShort(var8)));
            }
         }

         var1.add((INBTBase)var6);
      }

      return var1;
   }

   @Nullable
   private static Entity func_206240_a(NBTTagCompound var0, World var1, Function<Entity, Entity> var2) {
      Entity var3 = func_186053_a(var0, var1);
      if (var3 == null) {
         return null;
      } else {
         var3 = (Entity)var2.apply(var3);
         if (var3 != null && var0.func_150297_b("Passengers", 9)) {
            NBTTagList var4 = var0.func_150295_c("Passengers", 10);

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               Entity var6 = func_206240_a(var4.func_150305_b(var5), var1, var2);
               if (var6 != null) {
                  var6.func_184205_a(var3, true);
               }
            }
         }

         return var3;
      }
   }

   @Nullable
   public static Entity func_186050_a(NBTTagCompound var0, World var1, Chunk var2) {
      return func_206240_a(var0, var1, (var1x) -> {
         var2.func_76612_a(var1x);
         return var1x;
      });
   }

   @Nullable
   public static Entity func_186054_a(NBTTagCompound var0, World var1, double var2, double var4, double var6, boolean var8) {
      return func_206240_a(var0, var1, (var8x) -> {
         var8x.func_70012_b(var2, var4, var6, var8x.field_70177_z, var8x.field_70125_A);
         return var8 && !var1.func_72838_d(var8x) ? null : var8x;
      });
   }

   @Nullable
   public static Entity func_186051_a(NBTTagCompound var0, World var1, boolean var2) {
      return func_206240_a(var0, var1, (var2x) -> {
         return var2 && !var1.func_72838_d(var2x) ? null : var2x;
      });
   }

   @Nullable
   protected static Entity func_186053_a(NBTTagCompound var0, World var1) {
      try {
         return EntityType.func_200716_a(var0, var1);
      } catch (RuntimeException var3) {
         field_151505_a.warn("Exception loading entity: ", var3);
         return null;
      }
   }

   public static void func_186052_a(Entity var0, IWorld var1) {
      if (var1.func_72838_d(var0) && var0.func_184207_aI()) {
         Iterator var2 = var0.func_184188_bt().iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            func_186052_a(var3, var1);
         }
      }

   }

   public boolean func_212147_a(ChunkPos var1, DimensionType var2, WorldSavedDataStorage var3) {
      boolean var4 = false;

      try {
         this.func_212146_a(var2, var3, var1.field_77276_a, var1.field_77275_b);

         while(this.func_75814_c()) {
            var4 = true;
         }
      } catch (IOException var6) {
      }

      return var4;
   }
}
