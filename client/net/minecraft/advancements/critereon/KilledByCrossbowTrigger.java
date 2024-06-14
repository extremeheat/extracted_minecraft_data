package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class KilledByCrossbowTrigger extends SimpleCriterionTrigger<KilledByCrossbowTrigger.TriggerInstance> {
   public KilledByCrossbowTrigger() {
      super();
   }

   @Override
   public Codec<KilledByCrossbowTrigger.TriggerInstance> codec() {
      return KilledByCrossbowTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Collection<Entity> var2) {
      ArrayList var3 = Lists.newArrayList();
      HashSet var4 = Sets.newHashSet();

      for (Entity var6 : var2) {
         var4.add(var6.getType());
         var3.add(EntityPredicate.createContext(var1, var6));
      }

      this.trigger(var1, var2x -> var2x.matches(var3, var4.size()));
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
