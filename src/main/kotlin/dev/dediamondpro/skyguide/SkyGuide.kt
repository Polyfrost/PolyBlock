package dev.dediamondpro.skyguide

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.handlers.AssetHandler
import dev.dediamondpro.skyguide.handlers.KeyBindHandler
import dev.dediamondpro.skyguide.handlers.WaypointHandler
import dev.dediamondpro.skyguide.hud.MiniMap
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.SBInfo
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import java.io.File

@Mod(
    modid = SkyGuide.ID,
    name = SkyGuide.NAME,
    version = SkyGuide.VER,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object SkyGuide {
    const val NAME = "@NAME@"
    const val VER = "@VER@"
    const val ID = "@ID@"

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        File("./config/$ID").mkdirs()
        Config.preload()
        if (Config.downloadAtLaunch) AssetHandler.initialize()
        KeyBindHandler.init()
        MinecraftForge.EVENT_BUS.register(SBInfo())
        MinecraftForge.EVENT_BUS.register(GuiUtils())
        MinecraftForge.EVENT_BUS.register(WaypointHandler())
        MinecraftForge.EVENT_BUS.register(MiniMap())
        ClientCommandHandler.instance.registerCommand(dev.dediamondpro.skyguide.command.SkyGuideCommand())
    }
}