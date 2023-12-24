package org.polyfrost.blockoverlay.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Checkbox
import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import org.polyfrost.blockoverlay.PolyBlockOverlay

object ModConfig : Config(Mod("BlockOverlay", ModType.UTIL_QOL), "${PolyBlockOverlay.MODID}.json") {

    @Slider(name = "Line Width", min = 0.1F, max = 5.0F)
    var lineWidth = 1.0F

    @Dropdown(
        name = "Mode",
        options = ["Hidden", "Vanilla", "Side", "Full"],
    )
    var mode = 1

    @Switch(name = "Hide plants")
    var hidePlants = false

    @Switch(name = "Barriers")
    var barriers = true

    @Switch(name = "Ignore depth", description = "Ignore depth when rendering the block overlay")
    var ignoreDepth = false

    @Switch(name = "Persistent")
    var persistent = false

    @Switch(name = "Gradient")
    var gradient = false

    @Checkbox(name = "Draw Overlay", size = 2)
    var overlayColor = false

    @Color(name = "First Color Overlay")
    var colorFO: OneColor = OneColor(255, 255, 255, 0)

    @Color(name = "Second Color Overlay")
    var colorSO: OneColor = OneColor(255, 255, 255, 0)

    @Checkbox(name = "Draw Outline", size = 2)
    var outlineColor = true

    @Color(name = "First Color Outline")
    var colorFU: OneColor = OneColor(0, 0, 0, 255)

    @Color(name = "Second Color Outline")
    var colorSU: OneColor = OneColor(0, 0, 0, 255)

    init {
        initialize()
        addDependency("colorSO", "gradient")
        addDependency("colorSU", "gradient")
    }
}