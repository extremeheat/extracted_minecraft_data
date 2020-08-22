package net.minecraft.world.level.storage.loot.functions;

public interface FunctionUserBuilder {
   Object apply(LootItemFunction.Builder var1);

   Object unwrap();
}
