package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
   private WorldServer field_175743_a;

   public WorldServerMulti(MinecraftServer var1, ISaveHandler var2, int var3, WorldServer var4, Profiler var5) {
      super(var1, var2, new DerivedWorldInfo(var4.func_72912_H()), var3, var5);
      this.field_175743_a = var4;
      var4.func_175723_af().func_177737_a(new IBorderListener() {
         public void func_177694_a(WorldBorder var1, double var2) {
            WorldServerMulti.this.func_175723_af().func_177750_a(var2);
         }

         public void func_177692_a(WorldBorder var1, double var2, double var4, long var6) {
            WorldServerMulti.this.func_175723_af().func_177738_a(var2, var4, var6);
         }

         public void func_177693_a(WorldBorder var1, double var2, double var4) {
            WorldServerMulti.this.func_175723_af().func_177739_c(var2, var4);
         }

         public void func_177691_a(WorldBorder var1, int var2) {
            WorldServerMulti.this.func_175723_af().func_177723_b(var2);
         }

         public void func_177690_b(WorldBorder var1, int var2) {
            WorldServerMulti.this.func_175723_af().func_177747_c(var2);
         }

         public void func_177696_b(WorldBorder var1, double var2) {
            WorldServerMulti.this.func_175723_af().func_177744_c(var2);
         }

         public void func_177695_c(WorldBorder var1, double var2) {
            WorldServerMulti.this.func_175723_af().func_177724_b(var2);
         }
      });
   }

   protected void func_73042_a() {
   }

   public World func_175643_b() {
      this.field_72988_C = this.field_175743_a.func_175693_T();
      this.field_96442_D = this.field_175743_a.func_96441_U();
      String var1 = VillageCollection.func_176062_a(this.field_73011_w);
      VillageCollection var2 = (VillageCollection)this.field_72988_C.func_75742_a(VillageCollection.class, var1);
      if (var2 == null) {
         this.field_72982_D = new VillageCollection(this);
         this.field_72988_C.func_75745_a(var1, this.field_72982_D);
      } else {
         this.field_72982_D = var2;
         this.field_72982_D.func_82566_a(this);
      }

      return this;
   }
}
