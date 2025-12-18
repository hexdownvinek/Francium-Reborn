package net.fabricmc.fabric.dimension.api.v1;

import net.fabricmc.api.ModInitializer;

//yourchannel
/*
░█▀▀█ ░█▀▀▀█ ░█▀▀█ ░█▀▀▀█ ░█▀▄▀█ ▀▀█▀▀ ░█▀▀█
░█─── ░█──░█ ░█─▄▄ ░█──░█ ░█░█░█ ─░█── ░█▄▄▀
░█▄▄█ ░█▄▄▄█ ░█▄▄█ ░█▄▄▄█ ░█──░█ ─░█── ░█─░█
 */

@SuppressWarnings("all")
public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        new Client();
    }

}
