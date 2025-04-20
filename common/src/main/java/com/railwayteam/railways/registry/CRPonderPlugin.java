package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CRPonderPlugin implements PonderPlugin{
    @Override
    public String getModId() {
        return Railways.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CRPonderIndex.register(helper);
    }

    @Override 
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CRExtraDisplayTags.register(helper);
    }
}
