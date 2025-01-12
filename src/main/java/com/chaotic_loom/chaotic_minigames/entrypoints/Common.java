package com.chaotic_loom.chaotic_minigames.entrypoints;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.api.whitelist.WhitelistAPI;
import com.chaotic_loom.under_control.networking.services.ApiClient;
import com.chaotic_loom.under_control.networking.services.ApiResponse;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;

import java.util.List;

public class Common implements ModInitializer {
    public static final ApiClient CHAOTIC_LOOM_API = new ApiClient("https://chaotic-loom.com/api/");

    @Override
    public void onInitialize() {

    }
}
