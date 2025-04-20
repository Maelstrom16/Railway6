/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin;

import com.mojang.datafixers.util.Pair;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = MountedStorageManager.class, remap = false)
public abstract class MixinMountedStorageManager implements IFuelInventory {
	@Unique private Map<BlockPos, MountedFluidStorage> fuelsBuilder;
	@Unique private Map<BlockPos, SyncedMountedStorage> syncedFuelsBuilder;

    @Unique private CombinedTankWrapper railways$fluidFuelInventory;
    @Unique private Map<BlockPos, MountedFluidStorage> railways$fluidFuelStorage = new HashMap<>();
    

    // @Inject(method = "entityTick", at = @At("TAIL"))
    // private void entityTick(AbstractContraptionEntity entity, CallbackInfo ci) {
    //     railways$fluidFuelStorage.forEach((pos, mfs) -> mfs.tick(entity, pos, entity.level.isClientSide));
    // }

    @SuppressWarnings({"ConstantConditions"})
    @Inject(method = "addBlock", at = @At("TAIL"))
    private void addBlock(Level level, BlockState state, BlockPos globalPos, BlockPos localPos, @Nullable BlockEntity be, CallbackInfo ci) {
        MountedFluidStorageType<?> fluidType = MountedFluidStorageType.REGISTRY.get(state.getBlock());
		if (fluidType != null) {
			MountedFluidStorage storage = fluidType.mount(level, state, globalPos, be);
			if (storage != null) {
				this.addStorage(storage, localPos);
			}
		}
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci) {
        NBTHelper.iterateCompoundList(nbt.getList("fuels", Tag.TAG_COMPOUND), tag -> {
            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
            CompoundTag data = tag.getCompound("storage");
            MountedFluidStorage.CODEC.decode(NbtOps.INSTANCE, data)
                .result()
                .map(Pair::getFirst)
                .ifPresent(storage -> this.addStorage(storage, pos));
        });
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void write(CompoundTag nbt, boolean clientPacket, CallbackInfo ci) {
        ListTag fuels = new ListTag();
		railways$fluidFuelStorage.forEach((pos, storage) -> {
				if (!clientPacket || storage instanceof SyncedMountedStorage) {
					MountedFluidStorage.CODEC.encodeStart(NbtOps.INSTANCE, storage).result().ifPresent(encoded -> {
						CompoundTag tag = new CompoundTag();
						tag.put("pos", NbtUtils.writeBlockPos(pos));
						tag.put("data", encoded);
						fuels.add(tag);
					});
				}
			}
		);
		if (!fuels.isEmpty()) {
			nbt.put("fuels", fuels);
		}
    }

    // @Inject(method = "removeStorage", at = @At("TAIL"))
    // public void removeStorage(CallbackInfo ci) {
    //     railways$fluidFuelStorage.values()
    //             .forEach(MountedFluidStorage::removeStorage);
    // }

    @Inject(method = "addStorage", at = @At("TAIL"))
    private void addStorage(MountedFluidStorage storage, BlockPos pos) {
        this.fuelsBuilder.put(pos, storage);
		if (storage instanceof SyncedMountedStorage synced)
			this.syncedFuelsBuilder.put(pos, synced);
    }

    @Override
    public void railways$setFuelFluids(CombinedTankWrapper combinedTankWrapper) {
        railways$fluidFuelInventory = combinedTankWrapper;
    }

    @Override
    public CombinedTankWrapper railways$getFuelFluids() {
        return railways$fluidFuelInventory;
    }

    @Override
    public void railways$setFluidFuelStorage(Map<BlockPos, MountedFluidStorage> storageMap) {
        railways$fluidFuelStorage = storageMap;
    }

    @Override
    public Map<BlockPos, MountedFluidStorage> railways$getFluidFuelStorage() {
        return railways$fluidFuelStorage;
    }
}
