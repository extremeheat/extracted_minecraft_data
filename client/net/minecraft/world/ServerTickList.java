package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.chunk.Chunk;

public class ServerTickList<T> implements ITickList<T> {
   protected final Predicate<T> field_205371_a;
   protected final Function<T, ResourceLocation> field_205372_b;
   protected final Function<ResourceLocation, T> field_205373_c;
   protected final Set<NextTickListEntry<T>> field_205374_d = Sets.newHashSet();
   protected final TreeSet<NextTickListEntry<T>> field_205375_e = new TreeSet();
   private final WorldServer field_205376_f;
   private final List<NextTickListEntry<T>> field_205377_g = Lists.newArrayList();
   private final Consumer<NextTickListEntry<T>> field_205378_h;

   public ServerTickList(WorldServer var1, Predicate<T> var2, Function<T, ResourceLocation> var3, Function<ResourceLocation, T> var4, Consumer<NextTickListEntry<T>> var5) {
      super();
      this.field_205371_a = var2;
      this.field_205372_b = var3;
      this.field_205373_c = var4;
      this.field_205376_f = var1;
      this.field_205378_h = var5;
   }

   public void func_205365_a() {
      int var1 = this.field_205375_e.size();
      if (var1 != this.field_205374_d.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if (var1 > 65536) {
            var1 = 65536;
         }

         this.field_205376_f.field_72984_F.func_76320_a("cleaning");

         NextTickListEntry var3;
         for(int var2 = 0; var2 < var1; ++var2) {
            var3 = (NextTickListEntry)this.field_205375_e.first();
            if (var3.field_77180_e > this.field_205376_f.func_82737_E()) {
               break;
            }

            this.field_205375_e.remove(var3);
            this.field_205374_d.remove(var3);
            this.field_205377_g.add(var3);
         }

         this.field_205376_f.field_72984_F.func_76319_b();
         this.field_205376_f.field_72984_F.func_76320_a("ticking");
         Iterator var9 = this.field_205377_g.iterator();

         while(var9.hasNext()) {
            var3 = (NextTickListEntry)var9.next();
            var9.remove();
            boolean var4 = false;
            if (this.field_205376_f.func_175707_a(var3.field_180282_a.func_177982_a(0, 0, 0), var3.field_180282_a.func_177982_a(0, 0, 0))) {
               try {
                  this.field_205378_h.accept(var3);
               } catch (Throwable var8) {
                  CrashReport var6 = CrashReport.func_85055_a(var8, "Exception while ticking");
                  CrashReportCategory var7 = var6.func_85058_a("Block being ticked");
                  CrashReportCategory.func_175750_a(var7, var3.field_180282_a, (IBlockState)null);
                  throw new ReportedException(var6);
               }
            } else {
               this.func_205360_a(var3.field_180282_a, var3.func_151351_a(), 0);
            }
         }

         this.field_205376_f.field_72984_F.func_76319_b();
         this.field_205377_g.clear();
      }
   }

   public boolean func_205361_b(BlockPos var1, T var2) {
      return this.field_205377_g.contains(new NextTickListEntry(var1, var2));
   }

   public List<NextTickListEntry<T>> func_205364_a(Chunk var1, boolean var2) {
      ChunkPos var3 = var1.func_76632_l();
      int var4 = (var3.field_77276_a << 4) - 2;
      int var5 = var4 + 16 + 2;
      int var6 = (var3.field_77275_b << 4) - 2;
      int var7 = var6 + 16 + 2;
      return this.func_205366_a(new MutableBoundingBox(var4, 0, var6, var5, 256, var7), var2);
   }

   public List<NextTickListEntry<T>> func_205366_a(MutableBoundingBox var1, boolean var2) {
      ArrayList var3 = null;

      for(int var4 = 0; var4 < 2; ++var4) {
         Iterator var5;
         if (var4 == 0) {
            var5 = this.field_205375_e.iterator();
         } else {
            var5 = this.field_205377_g.iterator();
         }

         while(var5.hasNext()) {
            NextTickListEntry var6 = (NextTickListEntry)var5.next();
            BlockPos var7 = var6.field_180282_a;
            if (var7.func_177958_n() >= var1.field_78897_a && var7.func_177958_n() < var1.field_78893_d && var7.func_177952_p() >= var1.field_78896_c && var7.func_177952_p() < var1.field_78892_f) {
               if (var2) {
                  if (var4 == 0) {
                     this.field_205374_d.remove(var6);
                  }

                  var5.remove();
               }

               if (var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var6);
            }
         }
      }

      return (List)(var3 == null ? Collections.emptyList() : var3);
   }

   public void func_205368_a(MutableBoundingBox var1, BlockPos var2) {
      List var3 = this.func_205366_a(var1, false);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         NextTickListEntry var5 = (NextTickListEntry)var4.next();
         if (var1.func_175898_b(var5.field_180282_a)) {
            BlockPos var6 = var5.field_180282_a.func_177971_a(var2);
            this.func_205367_b(var6, var5.func_151351_a(), (int)(var5.field_77180_e - this.field_205376_f.func_72912_H().func_82573_f()), var5.field_82754_f);
         }
      }

   }

   public NBTTagList func_205363_a(Chunk var1) {
      List var2 = this.func_205364_a(var1, false);
      long var3 = this.field_205376_f.func_82737_E();
      NBTTagList var5 = new NBTTagList();
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         NextTickListEntry var7 = (NextTickListEntry)var6.next();
         NBTTagCompound var8 = new NBTTagCompound();
         var8.func_74778_a("i", ((ResourceLocation)this.field_205372_b.apply(var7.func_151351_a())).toString());
         var8.func_74768_a("x", var7.field_180282_a.func_177958_n());
         var8.func_74768_a("y", var7.field_180282_a.func_177956_o());
         var8.func_74768_a("z", var7.field_180282_a.func_177952_p());
         var8.func_74768_a("t", (int)(var7.field_77180_e - var3));
         var8.func_74768_a("p", var7.field_82754_f.func_205398_a());
         var5.add((INBTBase)var8);
      }

      return var5;
   }

   public void func_205369_a(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         Object var4 = this.field_205373_c.apply(new ResourceLocation(var3.func_74779_i("i")));
         if (var4 != null) {
            this.func_205367_b(new BlockPos(var3.func_74762_e("x"), var3.func_74762_e("y"), var3.func_74762_e("z")), var4, var3.func_74762_e("t"), TickPriority.func_205397_a(var3.func_74762_e("p")));
         }
      }

   }

   public boolean func_205359_a(BlockPos var1, T var2) {
      return this.field_205374_d.contains(new NextTickListEntry(var1, var2));
   }

   public void func_205362_a(BlockPos var1, T var2, int var3, TickPriority var4) {
      if (!this.field_205371_a.test(var2)) {
         if (this.field_205376_f.func_175667_e(var1)) {
            this.func_205370_c(var1, var2, var3, var4);
         }

      }
   }

   protected void func_205367_b(BlockPos var1, T var2, int var3, TickPriority var4) {
      if (!this.field_205371_a.test(var2)) {
         this.func_205370_c(var1, var2, var3, var4);
      }

   }

   private void func_205370_c(BlockPos var1, T var2, int var3, TickPriority var4) {
      NextTickListEntry var5 = new NextTickListEntry(var1, var2, (long)var3 + this.field_205376_f.func_82737_E(), var4);
      if (!this.field_205374_d.contains(var5)) {
         this.field_205374_d.add(var5);
         this.field_205375_e.add(var5);
      }

   }
}
