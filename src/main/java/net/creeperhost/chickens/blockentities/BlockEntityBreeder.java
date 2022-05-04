package net.creeperhost.chickens.blockentities;

import net.creeperhost.chickens.block.BlockBreeder;
import net.creeperhost.chickens.capability.SmartInventory;
import net.creeperhost.chickens.containers.ContainerBreeder;
import net.creeperhost.chickens.data.ChickenStats;
import net.creeperhost.chickens.entity.EntityChickensChicken;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.chickens.init.ModItems;
import net.creeperhost.chickens.item.ItemChicken;
import net.creeperhost.chickens.item.ItemSpawnEgg;
import net.creeperhost.chickens.registry.ChickensRegistry;
import net.creeperhost.chickens.registry.ChickensRegistryItem;
import net.creeperhost.chickens.util.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockEntityBreeder extends BaseContainerBlockEntity
{
    public SmartInventory inventory = new SmartInventory(6)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            if(slot == 0 && stack.is(ModItems.CHICKEN_ITEM.get())) return true;
            if(slot == 1 && stack.is(ModItems.CHICKEN_ITEM.get())) return true;
            if(slot == 2 && stack.is(Items.WHEAT_SEEDS)) return true;
            return super.isItemValid(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
        {
            return super.insertItem(slot, stack, simulate);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if(slot <= 2) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    };

    public final ContainerData containerData = new SimpleContainerData(1)
    {
        @Override
        public int get(int index)
        {
            if (index == 0)
            {
                return progress;
            }
            throw new IllegalArgumentException("Invalid index: " + index);
        }

        @Override
        public void set(int index, int value)
        {
            throw new IllegalStateException("Cannot set values through IIntArray");
        }

        @Override
        public int getCount()
        {
            return 1;
        }
    };

    public int progress = 0;

    public BlockEntityBreeder(BlockPos blockPos, BlockState blockState)
    {
        super(ModBlocks.BREEDER_TILE.get(), blockPos, blockState);
    }

    public void tick()
    {
        boolean canWork = (!inventory.getStackInSlot(0).isEmpty() && !inventory.getStackInSlot(1).isEmpty() && !inventory.getStackInSlot(2).isEmpty());
        if(level != null && !level.isClientSide && canWork)
        {
            if (progress <= 1000)
            {
                progress++;
            } else
            {
                ChickensRegistryItem chickensRegistryItem1 = ChickensRegistry.getByRegistryName(ItemSpawnEgg.getTypeFromStack(inventory.getStackInSlot(0)));
                ChickensRegistryItem chickensRegistryItem2 = ChickensRegistry.getByRegistryName(ItemSpawnEgg.getTypeFromStack(inventory.getStackInSlot(1)));

                ChickensRegistryItem baby = ChickensRegistry.getRandomChild(chickensRegistryItem1, chickensRegistryItem2);
                if(baby == null)
                {
                    progress = 0;
                    return;
                }
                ItemStack chickenStack = new ItemStack(ModItems.CHICKEN_ITEM.get());
                ItemChicken.applyEntityIdToItemStack(chickenStack, baby.getRegistryName());
                ChickenStats babyStats = increaseStats(chickenStack, inventory.getStackInSlot(0), inventory.getStackInSlot(1), level.random);
                babyStats.write(chickenStack);
                chickenStack.setCount(1);
                ItemStack inserted = InventoryHelper.insertItemStacked(inventory, chickenStack, false);
                if(inserted.isEmpty())
                {
                    inventory.getStackInSlot(2).shrink(1);
                    progress = 0;
                }
            }
        }
        else
        {
            progress = 0;
        }
    }

    private static ChickenStats increaseStats(ItemStack baby, ItemStack parent1, ItemStack parent2, Random rand)
    {
        ChickenStats babyStats = new ChickenStats(baby);
        ChickenStats parent1Stats = new ChickenStats(parent1);
        ChickenStats parent2Stats = new ChickenStats(parent2);

        babyStats.setGrowth(calculateNewStat(parent1Stats.getStrength(), parent2Stats.getStrength(), parent1Stats.getGrowth(), parent2Stats.getGrowth(), rand));
        babyStats.setGain(calculateNewStat(parent1Stats.getStrength(), parent2Stats.getStrength(), parent1Stats.getGain(), parent2Stats.getGain(), rand));
        babyStats.setStrength(calculateNewStat(parent1Stats.getStrength(), parent2Stats.getStrength(), parent1Stats.getStrength(), parent2Stats.getStrength(), rand));

        return babyStats;
    }

    private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random rand)
    {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * thisStrength + stat2 * mateStrength) / (thisStrength + mateStrength) + mutation;
        if (newStatValue <= 1) return 1;
        if (newStatValue >= 10) return 10;
        return newStatValue;
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        updateState();
    }

    public void updateState()
    {
        if(level != null)
        {
            BlockState state = level.getBlockState(worldPosition);
            state.setValue(BlockBreeder.HAS_SEEDS, true);
            boolean hasSeeds = !inventory.getStackInSlot(2).isEmpty();
            boolean isBreeding = (!inventory.getStackInSlot(0).isEmpty() && !inventory.getStackInSlot(1).isEmpty());
            level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()).setValue(BlockBreeder.HAS_SEEDS, hasSeeds).setValue(BlockBreeder.IS_BREEDING, isBreeding), 4);
        }
    }

    @Override
    public Component getDefaultName()
    {
        return new TextComponent("chickens.container.breeder");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory)
    {
        return new ContainerBreeder(id, inventory, this, containerData);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return LazyOptional.of(() -> inventory).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public int getContainerSize()
    {
        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount)
    {
        return inventory.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        return inventory.extractItem(slot, 64, false);
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
        inventory.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        compound.merge(inventory.serializeNBT());
        compound.putInt("progress", progress);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        inventory.deserializeNBT(compound);
        progress = compound.getInt("progress");
    }

    @Override
    public void clearContent() {}
}
