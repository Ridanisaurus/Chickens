package net.creeperhost.chickens.init;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.ChickensExpectPlatform;
import net.creeperhost.chickens.api.ChickensRegistry;
import net.creeperhost.chickens.api.ChickensRegistryItem;
import net.creeperhost.chickens.entity.EntityChickensChicken;
import net.creeperhost.chickens.item.ItemAnalyzer;
import net.creeperhost.chickens.item.ItemChickenCatcher;
import net.creeperhost.chickens.item.ItemColoredEgg;
import net.creeperhost.chickens.item.ItemFluidEgg;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Chickens.MOD_ID, Registry.ITEM_REGISTRY);

    public static final CreativeModeTab CREATIVE_MODE_TAB = CreativeTabRegistry.create(new ResourceLocation(Chickens.MOD_ID, "creative_tab"), () -> new ItemStack(ModBlocks.ROOST.get()));

    public static final RegistrySupplier<Item> COLOURED_EGG = ITEMS.register("colored_egg", () -> new ItemColoredEgg(new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> ANALYZER = ITEMS.register("analyzer", () -> new ItemAnalyzer(new Item.Properties().tab(CREATIVE_MODE_TAB).stacksTo(1)));
    public static final RegistrySupplier<Item> CHICKEN_ITEM = ITEMS.register("chicken_item", () -> ChickensExpectPlatform.createNewChickenItem(new Item.Properties().tab(CREATIVE_MODE_TAB).stacksTo(16)));
    public static final RegistrySupplier<Item> CATCHER_ITEM = ITEMS.register("catcher", () -> new ItemChickenCatcher(new Item.Properties().tab(CREATIVE_MODE_TAB)));

    public static final RegistrySupplier<Item> FLUID_EGG = ITEMS.register("fluid_egg", () -> new ItemFluidEgg());

    //ItemBlocks
    public static final RegistrySupplier<Item> HEN_HOUSE_ITEM = ITEMS.register("henhouse", () -> new BlockItem(ModBlocks.HEN_HOUSE.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> HEN_HOUSE_ACACIA_ITEM = ITEMS.register("henhouse_acacia", () -> new BlockItem(ModBlocks.HEN_HOUSE_ACACIA.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> HEN_HOUSE_BIRCH_ITEM = ITEMS.register("henhouse_birch", () -> new BlockItem(ModBlocks.HEN_HOUSE_BIRCH.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> HEN_HOUSE_DARK_OAK_ITEM = ITEMS.register("henhouse_dark_oak", () -> new BlockItem(ModBlocks.HEN_HOUSE_DARK_OAK.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> HEN_HOUSE_JUNGLE_ITEM = ITEMS.register("henhouse_jungle", () -> new BlockItem(ModBlocks.HEN_HOUSE_JUNGLE.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> HEN_HOUSE_SPRUCE_ITEM = ITEMS.register("henhouse_spruce", () -> new BlockItem(ModBlocks.HEN_HOUSE_SPRUCE.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));

    public static final RegistrySupplier<Item> BREEDER = ITEMS.register("breeder", () -> new BlockItem(ModBlocks.BREEDER.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> ROOST = ITEMS.register("roost", () -> new BlockItem(ModBlocks.ROOST.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));

}
